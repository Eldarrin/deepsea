package io.ensure.deepsea.common;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.ClusteredSessionStore;
import io.vertx.ext.web.sstore.LocalSessionStore;

/**
 * An abstract base verticle that provides several helper methods for REST API.
 *
 * @author Eric Zhao
 */
public abstract class RestAPIVerticle extends BaseMicroserviceVerticle {
	
	private static final String ERROR = "error";
	protected static final String APPLICATION_JSON = "application/json";
	protected static final String CONTENT_TYPE = "content-type";
	private static final String MESSAGE = "message";
	protected static final String HEALTH = "/health";

	private final Logger log = LoggerFactory.getLogger(getClass());
	
  /**
   * Create http server for the REST service.
   *
   * @param router router instance
   * @param host   http host
   * @param port   http port
   * @return async result of the procedure
   */
  protected Future<Void> createHttpServer(Router router, String host, int port) {
    Future<HttpServer> httpServerFuture = Future.future();
    vertx.createHttpServer()
      .requestHandler(router)
      .listen(port, host, httpServerFuture);
    return httpServerFuture.map(r -> null);
  }

  /**
   * Enable CORS support.
   *
   * @param router router instance
   */
  protected void enableCorsSupport(Router router) {
    Set<String> allowHeaders = new HashSet<>();
    allowHeaders.add("x-requested-with");
    allowHeaders.add("Access-Control-Allow-Origin");
    allowHeaders.add("origin");
    allowHeaders.add("Content-Type");
    allowHeaders.add("accept");
    Set<HttpMethod> allowMethods = new HashSet<>();
    allowMethods.add(HttpMethod.GET);
    allowMethods.add(HttpMethod.PUT);
    allowMethods.add(HttpMethod.OPTIONS);
    allowMethods.add(HttpMethod.POST);
    allowMethods.add(HttpMethod.DELETE);
    allowMethods.add(HttpMethod.PATCH);

    router.route().handler(CorsHandler.create("*")
      .allowedHeaders(allowHeaders)
      .allowedMethods(allowMethods));
  }
  
  protected void startRestService(Router router, Future<Void> future, String serviceName, String serviceType,
		  String namespace, String configMapName) {
	  addBodyHealthHandler(router, future);
	  ConfigRetriever retriever = ConfigRetriever
				.create(vertx, new ConfigRetrieverHelper()
						.getOptions(namespace, configMapName));
      retriever.getConfig(res -> {
			if (res.succeeded()) {
				String host = res.result().getString(serviceType + ".http.address", "0.0.0.0");
				int port = res.result().getInteger(serviceType + ".http.port", 8080);
				String service = res.result().getString(serviceType + ".service", configMapName + "." + namespace + ".svc");

				log.info("Starting Deepsea " + serviceType + " on host:port:service " + host + ":" + port + ":" + service);

				// create HTTP server and publish REST service
				createHttpServer(router, host, port)
						.compose(serverCreated -> publishHttpEndpoint(serviceName, service, port, serviceType))
						.setHandler(future);
			} else {
	      		log.error("Cannot start " + serviceType + " REST API, no ConfigMap");
	      	}
      });
  }
  
  protected void addBodyHealthHandler(Router router, Future<Void> future) {
	  
	  addHealthHandler(router, future);
  }
  
  protected void addHealthHandler(Router router, Future<Void> future) {
	  router.get(HEALTH).handler(rc -> {
			if (future.succeeded()) {
				rc.response().end("Ready");
			} else {
				rc.response().setStatusCode(503).end();
			}
		});
  }
  

  /**
   * Enable local session storage in requests.
   *
   * @param router router instance
   */
  protected void enableLocalSession(Router router) {
    router.route().handler(CookieHandler.create());
    router.route().handler(SessionHandler.create(
      LocalSessionStore.create(vertx, "shopping.user.session")));
  }

  /**
   * Enable clustered session storage in requests.
   *
   * @param router router instance
   */
  protected void enableClusteredSession(Router router) {
    router.route().handler(CookieHandler.create());
    router.route().handler(SessionHandler.create(
      ClusteredSessionStore.create(vertx, "shopping.user.session")));
  }

  // Auth helper method

  /**
   * Validate if a user exists in the request scope.
   */
  protected void requireLogin(RoutingContext context, BiConsumer<RoutingContext, JsonObject> biHandler) {
    Optional<JsonObject> principal = Optional.ofNullable(context.request().getHeader("user-principal"))
      .map(JsonObject::new);
    if (principal.isPresent()) {
      biHandler.accept(context, principal.get());
    } else {
      context.response()
        .setStatusCode(401)
        .end(new JsonObject().put(MESSAGE, "need_auth").encode());
    }
  }

  // helper result handler within a request context

  /**
   * This method generates handler for async methods in REST APIs.
   */
  protected <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context, Handler<T> handler) {
    return res -> {
      if (res.succeeded()) {
        handler.handle(res.result());
      } else {
        internalError(context, res.cause());
        log.error(res.cause());
      }
    };
  }

  /**
   * This method generates handler for async methods in REST APIs.
   * Use the result directly and invoke `toString` as the response. The content type is JSON.
   */
  protected <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context) {
    return ar -> {
      if (ar.succeeded()) {
        T res = ar.result();
        context.response()
          .putHeader(CONTENT_TYPE, APPLICATION_JSON)
          .end(res == null ? "{}" : res.toString());
      } else {
        internalError(context, ar.cause());
        log.error(ar.cause());
      }
    };
  }

  /**
   * This method generates handler for async methods in REST APIs.
   * Use the result directly and use given {@code converter} to convert result to string
   * as the response. The content type is JSON.
   *
   * @param context   routing context instance
   * @param converter a converter that converts result to a string
   * @param <T>       result type
   * @return generated handler
   */
  protected <T> Handler<AsyncResult<T>> resultHandler(RoutingContext context, Function<T, String> converter) {
    return ar -> {
      if (ar.succeeded()) {
        T res = ar.result();
        if (res == null) {
          serviceUnavailable(context, "invalid_result");
        } else {
          context.response()
            .putHeader(CONTENT_TYPE, APPLICATION_JSON)
            .end(converter.apply(res));
        }
      } else {
        internalError(context, ar.cause());
        log.error(ar.cause());
      }
    };
  }

  /**
   * This method generates handler for async methods in REST APIs.
   * The result requires non-empty. If empty, return <em>404 Not Found</em> status.
   * The content type is JSON.
   *
   * @param context routing context instance
   * @param <T>     result type
   * @return generated handler
   */
  protected <T> Handler<AsyncResult<T>> resultHandlerNonEmpty(RoutingContext context) {
    return ar -> {
      if (ar.succeeded()) {
        T res = ar.result();
        if (res == null) {
          notFound(context);
        } else {
          context.response()
            .putHeader(CONTENT_TYPE, APPLICATION_JSON)
            .end(res.toString());
        }
      } else {
        internalError(context, ar.cause());
        log.error(ar.cause());
      }
    };
  }

  /**
   * This method generates handler for async methods in REST APIs.
   * The content type is originally raw text.
   *
   * @param context routing context instance
   * @param <T>     result type
   * @return generated handler
   */
  protected <T> Handler<AsyncResult<T>> rawResultHandler(RoutingContext context) {
    return ar -> {
      if (ar.succeeded()) {
        T res = ar.result();
        context.response()
          .end(res == null ? "" : res.toString());
      } else {
        internalError(context, ar.cause());
        log.error(ar.cause());
      }
    };
  }

  protected Handler<AsyncResult<Void>> resultVoidHandler(RoutingContext context, JsonObject result) {
    return resultVoidHandler(context, result, 200);
  }

  /**
   * This method generates handler for async methods in REST APIs.
   * The result is not needed. Only the state of the async result is required.
   *
   * @param context routing context instance
   * @param result  result content
   * @param status  status code
   * @return generated handler
   */
  protected Handler<AsyncResult<Void>> resultVoidHandler(RoutingContext context, JsonObject result, int status) {
    return ar -> {
      if (ar.succeeded()) {
        context.response()
          .setStatusCode(status == 0 ? 200 : status)
          .putHeader(CONTENT_TYPE, APPLICATION_JSON)
          .end(result.encodePrettily());
      } else {
        internalError(context, ar.cause());
        log.error(ar.cause());
      }
    };
  }

  protected Handler<AsyncResult<Void>> resultVoidHandler(RoutingContext context, int status) {
    return ar -> {
      if (ar.succeeded()) {
        context.response()
          .setStatusCode(status == 0 ? 200 : status)
          .putHeader(CONTENT_TYPE, APPLICATION_JSON)
          .end();
      } else {
        internalError(context, ar.cause());
        log.error(ar.cause());
      }
    };
  }

  /**
   * This method generates handler for async methods in REST DELETE APIs.
   * Return format in JSON (successful status = 204):
   * <code>
   * {"message": "delete_success"}
   * </code>
   *
   * @param context routing context instance
   * @return generated handler
   */
  protected Handler<AsyncResult<Void>> deleteResultHandler(RoutingContext context) {
    return res -> {
      if (res.succeeded()) {
        context.response().setStatusCode(204)
          .putHeader(CONTENT_TYPE, APPLICATION_JSON)
          .end(new JsonObject().put(MESSAGE, "delete_success").encodePrettily());
      } else {
        internalError(context, res.cause());
        log.error(res.cause());
      }
    };
  }

  // helper method dealing with failure

  protected void badRequest(RoutingContext context, Throwable ex) {
    context.response().setStatusCode(400)
      .putHeader(CONTENT_TYPE, APPLICATION_JSON)
      .end(new JsonObject().put(ERROR, ex.getMessage()).encodePrettily());
  }

  protected void notFound(RoutingContext context) {
    context.response().setStatusCode(404)
      .putHeader(CONTENT_TYPE, APPLICATION_JSON)
      .end(new JsonObject().put(MESSAGE, "not_found").encodePrettily());
  }

  protected void internalError(RoutingContext context, Throwable ex) {
    context.response().setStatusCode(500)
      .putHeader(CONTENT_TYPE, APPLICATION_JSON)
      .end(new JsonObject().put(ERROR, ex.getMessage()).encodePrettily());
  }

  protected void notImplemented(RoutingContext context) {
    context.response().setStatusCode(501)
      .putHeader(CONTENT_TYPE, APPLICATION_JSON)
      .end(new JsonObject().put(MESSAGE, "not_implemented").encodePrettily());
  }

  protected void badGateway(Throwable ex, RoutingContext context) {
    log.error(ex);
    context.response()
      .setStatusCode(502)
      .putHeader(CONTENT_TYPE, APPLICATION_JSON)
      .end(new JsonObject().put(ERROR, "bad_gateway")
        //.put("message", ex.getMessage())
        .encodePrettily());
  }

  protected void serviceUnavailable(RoutingContext context) {
    context.fail(503);
  }

  protected void serviceUnavailable(RoutingContext context, Throwable ex) {
    context.response().setStatusCode(503)
      .putHeader(CONTENT_TYPE, APPLICATION_JSON)
      .end(new JsonObject().put(ERROR, ex.getMessage()).encodePrettily());
  }

  protected void serviceUnavailable(RoutingContext context, String cause) {
    context.response().setStatusCode(503)
      .putHeader(CONTENT_TYPE, APPLICATION_JSON)
      .end(new JsonObject().put(ERROR, cause).encodePrettily());
  }
}
