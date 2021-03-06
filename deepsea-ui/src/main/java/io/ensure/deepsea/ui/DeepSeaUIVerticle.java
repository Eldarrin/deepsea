package io.ensure.deepsea.ui;

import java.util.List;
import java.util.Optional;

import io.ensure.deepsea.common.RestAPIVerticle;
import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Future;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.auth.oauth2.AccessToken;
import io.vertx.ext.auth.oauth2.OAuth2Auth;
import io.vertx.ext.auth.oauth2.OAuth2FlowType;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.OAuth2AuthHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.UserSessionHandler;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import io.vertx.ext.auth.oauth2.providers.KeycloakAuth;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.servicediscovery.types.HttpEndpoint;

public class DeepSeaUIVerticle extends RestAPIVerticle {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public void start(Future<Void> future) {
		super.start();

		ConfigRetriever retriever = ConfigRetriever.create(vertx,
				new ConfigRetrieverHelper().getOptions("deepsea", "deepsea-ui"));
		retriever.getConfig(res -> {
			if (res.succeeded()) {
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

				JsonObject keyCloakJson = new JsonObject();
				
				keyCloakJson.put("realm", res.result().getString("keycloak.realm"));
				keyCloakJson.put("auth-server-url", res.result().getString("keycloak.auth-server-url"));
				keyCloakJson.put("ssl-required", res.result().getString("keycloak.ssl-required"));
				keyCloakJson.put("resource", res.result().getString("keycloak.resource"));
				keyCloakJson.put("use-resource-role-mappings", res.result().getBoolean("keycloak.use-resource-role-mappings"));
				keyCloakJson.put("confidential-port", res.result().getInteger("keycloak.confidential-port"));
				keyCloakJson.put("credentials", 
						new JsonObject().put("secret", res.result().getString("keycloak.credentials.secret")));
				
				log.info(keyCloakJson.encodePrettily());


				OAuth2Auth authProvider = KeycloakAuth.create(vertx, OAuth2FlowType.AUTH_CODE, keyCloakJson);
				
				router.route().handler(UserSessionHandler.create(authProvider));

				router.route("/protected").handler(OAuth2AuthHandler.create(authProvider)
						.setupCallback(router.route("/callback"))
				);

				// static content
				router.route("/*").handler(StaticHandler.create());

				router.route("/protected").handler(rc -> {
					AccessToken token = (AccessToken) rc.user();

					
					token.isAuthorized("deepsea", ar -> {
						if (ar.result()) {
							log.info("has view-profile");
						} else {
							log.info("not auth view profile");
						}
					});
					
					log.info(rc.user().principal().encodePrettily());
					
					
					
					token.userInfo(ar -> {
						if (ar.failed()) {
							// request didn't succeed because the token was revoked so we
							// invalidate the token stored in the session and render the
							// index page so that the user can start the OAuth flow again
							rc.session().destroy();
							rc.fail(ar.cause());
							log.error(ar.cause());
						} else {
							// the request succeeded, so we use the API to fetch the user's emails
							final JsonObject userInfo = ar.result();
							rc.session().put("UserInfo", userInfo);
							rc.response().end("Welcome to the protected resource, " + userInfo.encodePrettily());
						}
					});

				});

				// get HTTP host and port from configuration, or use default value
				String host = res.result().getString("deepsea.ui.http.address", "0.0.0.0");
				int port = res.result().getInteger("deepsea.ui.http.port", 8080);

				// create HTTP server
				vertx.createHttpServer().requestHandler(router).listen(port, host, ar -> {
					if (ar.succeeded()) {
						future.complete();
						log.info(String.format("Deep Sea UI service is running at %d", port));
					} else {
						future.fail(ar.cause());
					}
				});
			} else {
				log.error("Unable to find config map for deepsea-ui MySQL");
			}

		});
		listAllEndpoints();

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
	 * Get all REST endpoints from the service discovery infrastructure.
	 *
	 * @return async result
	 */
	private Future<List<Record>> getAllEndpoints() {
		Future<List<Record>> future = Future.future();
		discovery.getRecords(record -> record.getType().equals(HttpEndpoint.TYPE), future);
		return future;
	}

	private void listAllEndpoints() {
		discovery.getRecords(record -> record.getType().equals(HttpEndpoint.TYPE), ar -> {
			if (ar.succeeded()) {
				for (Record rec : ar.result()) {
					log.info(rec.getName() + "|" + rec.getLocation() + "|" + rec.getStatus());
				}
			}
		});
		discovery.getRecords(record -> record.getType().equals(EventBusService.TYPE), ar -> {
			if (ar.succeeded()) {
				for (Record rec : ar.result()) {
					log.info(rec.getName() + "|" + rec.getLocation() + "|" + rec.getStatus());
				}
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
