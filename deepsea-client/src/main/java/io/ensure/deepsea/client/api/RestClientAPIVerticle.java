package io.ensure.deepsea.client.api;

import io.ensure.deepsea.client.Client;
import io.ensure.deepsea.client.ClientService;
import io.ensure.deepsea.common.RestAPIVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RestClientAPIVerticle extends RestAPIVerticle {
	
	public static final String SERVICE_NAME = "client-rest-api";
	
	private static final String API_ADD = "/add";
	private static final String API_RETRIEVE = "/";
	
	private final ClientService service;
	
	public RestClientAPIVerticle(ClientService service) {
		super();
		this.service = service;
	}
	
	@Override
	public void start(Future<Void> future) {
		super.start();
		final Router router = Router.router(vertx);
		// body handler
		router.route().handler(BodyHandler.create());
		// API route handler
		addHealthHandler(router, future);
		router.post(API_ADD).handler(this::apiAdd);
		router.get(API_RETRIEVE).handler(this::apiRetrieve);
		
		startRestService(router, future, SERVICE_NAME, "client", "deepsea", "deepsea-client");
	}
	
	private void apiAdd(RoutingContext rc) {
		try {
			Client client = new Client(new JsonObject(rc.getBodyAsString()));
			service.addClient(client, resultHandler(rc, r -> {
				String result = new JsonObject().put("message", "client_added")
						.put("clientId", client.getClientId()).encodePrettily();
				rc.response().setStatusCode(201).putHeader("content-type", "application/json").end(result);
			}));
		} catch (DecodeException e) {
			badRequest(rc, e);
		}
	}
	
	private void apiRetrieve(RoutingContext rc) {
		service.retrieveClients(resultHandlerNonEmpty(rc));
	}
	

}
