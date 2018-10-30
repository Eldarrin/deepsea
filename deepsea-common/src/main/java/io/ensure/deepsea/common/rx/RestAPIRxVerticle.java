package io.ensure.deepsea.common.rx;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.handler.CookieHandler;
import io.vertx.rxjava.ext.web.handler.CorsHandler;
import io.vertx.rxjava.ext.web.handler.SessionHandler;
import io.vertx.rxjava.ext.web.sstore.ClusteredSessionStore;
import io.vertx.rxjava.ext.web.sstore.LocalSessionStore;
import rx.Single;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * An abstract base rx-fied verticle that provides
 * several helper methods for developing RESTful services.
 *
 * @author Eric Zhao
 */
public abstract class RestAPIRxVerticle extends BaseMicroserviceRxVerticle {
	
	private static final String ERROR = "error";
	private static final String APPLICATION_JSON = "application/json";
	private static final String CONTENT_TYPE = "content-type";
	private static final String MESSAGE = "message";
	private Logger log = LoggerFactory.getLogger(getClass());

  protected Single<Void> createHttpServer(Router router, String host, int port) {
    return vertx.createHttpServer()
      .requestHandler(router::accept)
      .rxListen(port, host)
      .map(r -> null);
  }

  protected void enableCorsSupport(Router router) {
    Set<String> allowHeaders = new HashSet<>();
    allowHeaders.add("x-requested-with");
    allowHeaders.add("Access-Control-Allow-Origin");
    allowHeaders.add("origin");
    allowHeaders.add("Content-Type");
    allowHeaders.add("accept");
    router.route().handler(CorsHandler.create("*")
      .allowedHeaders(allowHeaders)
      .allowedMethod(HttpMethod.GET)
      .allowedMethod(HttpMethod.POST)
      .allowedMethod(HttpMethod.PUT)
      .allowedMethod(HttpMethod.DELETE)
      .allowedMethod(HttpMethod.PATCH)
      .allowedMethod(HttpMethod.OPTIONS)
    );
  }

  protected void enableLocalSession(Router router, String name) {
    Objects.requireNonNull(name);
    router.route().handler(CookieHandler.create());
    router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx, name)));
  }

  protected void enableClusteredSession(Router router, String name) {
    Objects.requireNonNull(name);
    router.route().handler(CookieHandler.create());
    router.route().handler(SessionHandler.create(ClusteredSessionStore.create(vertx, name)));
  }

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
      .end(new JsonObject().put(ERROR, "bad_gateway").encodePrettily());
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
