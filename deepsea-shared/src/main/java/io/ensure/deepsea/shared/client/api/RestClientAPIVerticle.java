package io.ensure.deepsea.shared.client.api;

import io.ensure.deepsea.common.RestAPIVerticle;
import io.ensure.deepsea.shared.client.Client;
import io.ensure.deepsea.shared.client.ClientService;
import io.vertx.core.Future;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RestClientAPIVerticle extends RestAPIVerticle {
	
private Logger log = LoggerFactory.getLogger(getClass());
	
	public static final String SERVICE_NAME = "client-rest-api";
	
	private static final String API_ADD = "/client/add";
	private static final String API_RETRIEVE = "/client/";
	
	private final ClientService service;
	
	public RestClientAPIVerticle(ClientService service) {
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
		router.get(API_RETRIEVE).handler(this::apiRetrieve);

		// get HTTP host and port from configuration, or use default value
		String host = config().getString("client.http.address", "0.0.0.0");
		int port = config().getInteger("client.http.port", 8080);
		
		log.info("Starting Deepsea Client on host:port " + host + ":" + port);

		// create HTTP server and publish REST service
		createHttpServer(router, host, port)
				.compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, "deepsea-shared.deepsea.svc", port, "client"))
				.setHandler(future.completer());
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
