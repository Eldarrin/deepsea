package io.ensure.deepsea.ux;

import java.util.List;
import java.util.Optional;

import io.ensure.deepsea.common.RestAPIVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.HttpEndpoint;

public class DeepSeaUXVerticle extends RestAPIVerticle {

	private Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();

		Router router = Router.router(vertx);

		addHealthHandler(router, future);

		router.route().handler(CookieHandler.create());

		// Create a clustered session store using defaults
		SessionStore store = ClusteredSessionStore.create(vertx);

		SessionHandler sessionHandler = SessionHandler.create(store);

		// Make sure all requests are routed through the session handler too
		router.route().handler(sessionHandler);

		// event bus bridge
		SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
		// version handler
		router.get("/api/v").handler(this::apiVersion);

		// api dispatcher
		router.route("/api/*").handler(this::dispatchRequests);

		router.route("/eventbus/*").handler(sockJSHandler);

		// router.route().handler(UserSessionHandler.create(authProvider));

		// static content
		router.route("/*").handler(StaticHandler.create());

		vertx.createHttpServer().requestHandler(router::accept).listen(8080, "0.0.0.0", ar -> {
			if (ar.succeeded()) {
				future.complete();
				log.info(String.format("Deep Sea UI service is running at %d", 8080));
			} else {
				future.fail(ar.cause());
			}
		});
	}

	private Future<List<Record>> getAllEndpoints() {
		Future<List<Record>> future = Future.future();
		discovery.getRecords(record -> record.getType().equals(HttpEndpoint.TYPE), future.completer());
		return future;
	}

	private void dispatchRequests(RoutingContext context) {
		int initialOffset = 5; // length of `/api/`
		// run with circuit breaker in order to deal with failure
		circuitBreaker.execute(future -> getAllEndpoints().setHandler(ar -> {
			if (ar.succeeded()) {
				List<Record> recordList = ar.result();
				// get relative path and retrieve prefix to dispatch client
				String path = context.request().uri();

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

				if (client.isPresent()) {
					doDispatch(context, newPath, discovery.getReference(client.get()).get(), future);
				} else {
					notFound(context);
					future.complete();
				}
			} else {
				future.fail(ar.cause());
			}
		})).setHandler(ar -> {
			if (ar.failed()) {
				badGateway(ar.cause(), context);
			}
		});
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
			log.info(response.request().absoluteURI());
			response.bodyHandler(body -> {
				if (response.statusCode() >= 500) { // api endpoint server error, circuit breaker should fail
					cbFuture.fail(response.statusCode() + ": " + body.toString());
				} else {
					HttpServerResponse toRsp = context.response().setStatusCode(response.statusCode());
					response.headers().forEach(header -> toRsp.putHeader(header.getKey(), header.getValue()));
					// send response
					toRsp.end(body);
					cbFuture.complete();
				}
				ServiceDiscovery.releaseServiceObject(discovery, client);
			});
		});
		// set headers
		context.request().headers().forEach(header -> toReq.putHeader(header.getKey(), header.getValue()));
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
