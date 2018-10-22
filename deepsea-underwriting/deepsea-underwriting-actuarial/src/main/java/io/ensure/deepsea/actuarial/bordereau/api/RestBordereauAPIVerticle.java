package io.ensure.deepsea.actuarial.bordereau.api;

import io.ensure.deepsea.actuarial.bordereau.BordereauLine;
import io.ensure.deepsea.actuarial.bordereau.BordereauService;
import io.ensure.deepsea.common.RestAPIVerticle;

import io.vertx.core.Future;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RestBordereauAPIVerticle extends RestAPIVerticle {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	public static final String SERVICE_NAME = "bordereau-rest-api";
	
	private static final String API_ADD = "/add";
	private static final String API_RETRIEVE = "/:bordereauLineId";
	private static final String API_RETRIEVE_BY_CLIENT = "/:clientId";
	private static final String API_RETRIEVE_BY_CLIENT_BY_PAGE = "/:clientId";
	
	
	private final BordereauService service;
	
	public RestBordereauAPIVerticle(BordereauService service) {
		this.service = service;
	}
	
	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();
		final Router router = Router.router(vertx);
		// body handler
		router.route().handler(BodyHandler.create());
		// API route handler
		router.post(API_ADD).handler(this::apiAdd);
		router.get(API_RETRIEVE_BY_CLIENT).handler(this::apiRetrieveByClient);
		router.get(API_RETRIEVE_BY_CLIENT_BY_PAGE).handler(this::apiRetrieveByClientByPage);
		router.get(API_RETRIEVE).handler(this::apiRetrieve);

		// get HTTP host and port from configuration, or use default value
		String host = config().getString("bordereau.http.address", "0.0.0.0");
		int port = config().getInteger("bordereau.http.port", 8080);
		
		log.info("Starting Deepsea Bordereau on host:port " + host + ":" + port);

		// create HTTP server and publish REST service
		createHttpServer(router, host, port)
				.compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, "deepsea-underwriting-actuarial.deepsea.svc", port, "bordereau"))
				.setHandler(future.completer());
	}
	
	private void apiAdd(RoutingContext rc) {
		try {
			BordereauLine bordereauLine = new BordereauLine(new JsonObject(rc.getBodyAsString()));
			service.addBordereauLine(bordereauLine, resultHandler(rc, r -> {
				String result = new JsonObject().put("message", "bordereau_line_added")
						.put("productId", bordereauLine.getBordereauLineId()).encodePrettily();
				rc.response().setStatusCode(201).putHeader("content-type", "application/json").end(result);
			}));
		} catch (DecodeException e) {
			badRequest(rc, e);
		}
	}
	
	private void apiRetrieve(RoutingContext rc) {
		String bordereauLineId = rc.request().getParam("bordereauLineId");
		service.retrieveBordereauLine(bordereauLineId, resultHandlerNonEmpty(rc));
	}
	
	private void apiRetrieveByClient(RoutingContext rc) {
		String clientId = rc.request().getParam("clientId");
		service.retrieveBordereauByClient(clientId, resultHandlerNonEmpty(rc));
	}
	
	private void apiRetrieveByClientByPage(RoutingContext rc) {
		try {
			String clientId = rc.request().getParam("clientId");
			String p = rc.request().getParam("p");
			int page = p == null ? 1 : Integer.parseInt(p);
			service.retrieveBordereauByClientByPage(clientId, page, resultHandler(rc, Json::encodePrettily));
		} catch (Exception ex) {
			badRequest(rc, ex);
		}
	}

}
