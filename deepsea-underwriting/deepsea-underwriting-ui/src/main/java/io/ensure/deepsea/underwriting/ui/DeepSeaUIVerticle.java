package io.ensure.deepsea.underwriting.ui;

import java.util.List;
import java.util.Optional;

import io.ensure.deepsea.common.RestAPIVerticle;

import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;

public class DeepSeaUIVerticle extends RestAPIVerticle {
	
	private static final String HOCON = "hocon";
    private static final String CONFIGMAP = "configmap";
    private static final String NAMESPACE = "deepsea";
    private static final String SECRET = "secret";
    private static final String OPTIONAL = "optional";

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();
		
		ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions();
        if (System.getenv().containsKey("OPENSHIFT_BUILD_NAMESPACE")) {
            ConfigStoreOptions kubeConfig = new ConfigStoreOptions()
                    .setType(CONFIGMAP)
                    .setFormat(HOCON)
                    .setConfig(new JsonObject()
                            .put(OPTIONAL, true)
                            .put("name", "deepsea-underwriting-ui"));
            configRetrieverOptions
                .addStore(kubeConfig);        // Values here will override identical keys from above
        }
        
		Router router = Router.router(vertx);

		// event bus bridge
		SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
		// version handler
		router.get("/api/v").handler(this::apiVersion);

		// api dispatcher
		router.route("/api/*").handler(this::dispatchRequests);

		router.route("/eventbus/*").handler(sockJSHandler);

		// static content
		router.route("/*").handler(StaticHandler.create());

		// get HTTP host and port from configuration, or use default value
		String host = config().getString("deepsea.ui.http.address", "0.0.0.0");
		int port = config().getInteger("deepsea.ui.http.port", 8080);

		// create HTTP server
		vertx.createHttpServer().requestHandler(router::accept).listen(port, host, ar -> {
			if (ar.succeeded()) {
				future.complete();
				logger.info(String.format("Deep Sea UI service is running at %d", port));
			} else {
				future.fail(ar.cause());
			}
		});
		

	}

	private void dispatchRequests(RoutingContext context) {
		int initialOffset = 5; // length of `/api/`
		// run with circuit breaker in order to deal with failure
		circuitBreaker.execute(future -> {
			getAllEndpoints().setHandler(ar -> {
				logger.info("in get endpoints");
				if (ar.succeeded()) {
					logger.info("in get endpoints - succeeded");
					List<Record> recordList = ar.result();
					logger.info(recordList.size());
					for (Record record : recordList) {
						logger.info(record.getName());
					}
					// get relative path and retrieve prefix to dispatch client
					String path = context.request().uri();
					logger.info(path);

					if (path.length() <= initialOffset) {
						notFound(context);
						future.complete();
						return;
					}
					String prefix = (path.substring(initialOffset).split("/"))[0];
					// generate new relative path
					String newPath = path.substring(initialOffset + prefix.length());
					// get one relevant HTTP client, may not exist
					Optional<Record> client = recordList.stream()
							.filter(record -> record.getMetadata().getString("api.name") != null)
							.filter(record -> record.getMetadata().getString("api.name").equals(prefix)).findAny(); // simple
																													// load
																													// balance

					if (client.isPresent()) {
						logger.info(client.get().getLocation());
						logger.info(newPath);
						doDispatch(context, newPath, discovery.getReference(client.get()).get(), future);
					} else {
						logger.info("not found");
						notFound(context);
						future.complete();
					}
				} else {
					future.fail(ar.cause());
				}
			});
		}).setHandler(ar -> {
			if (ar.failed()) {
				badGateway(ar.cause(), context);
			}
		});
	}

	/**
	 * Get all REST endpoints from the service discovery infrastructure.
	 *
	 * @return async result
	 */
	private Future<List<Record>> getAllEndpoints() {
		Future<List<Record>> future = Future.future();
		discovery.getRecords(record -> record.getType().equals(HttpEndpoint.TYPE), future.completer());
		return future;
	}
	
	/**
	 * Dispatch the request to the downstream REST layers.
	 *
	 * @param context routing context instance
	 * @param path    relative path
	 * @param client  relevant HTTP client
	 */
	private void doDispatch(RoutingContext context, String path, HttpClient client, Future<Object> cbFuture) {
		HttpClientRequest toReq = client.request(context.request().method(), path, response -> {
			logger.info(response.request().absoluteURI());
			response.bodyHandler(body -> {
				if (response.statusCode() >= 500) { // api endpoint server error, circuit breaker should fail
					cbFuture.fail(response.statusCode() + ": " + body.toString());
				} else {
					HttpServerResponse toRsp = context.response().setStatusCode(response.statusCode());
					response.headers().forEach(header -> {
						toRsp.putHeader(header.getKey(), header.getValue());
					});
					// send response
					toRsp.end(body);
					cbFuture.complete();
				}
				ServiceDiscovery.releaseServiceObject(discovery, client);
			});
		});
		// set headers
		context.request().headers().forEach(header -> {
			toReq.putHeader(header.getKey(), header.getValue());
		});
		if (context.user() != null) {
			toReq.putHeader("user-principal", context.user().principal().encode());
		}
		// send request
		if (context.getBody() == null) {
			toReq.end();
		} else {
			toReq.end(context.getBody());
		}
	}

	private void apiVersion(RoutingContext context) {
		context.response().end(new JsonObject().put("version", "v1").encodePrettily());
	}

}
