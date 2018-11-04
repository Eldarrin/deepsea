package io.ensure.deepsea.admin.mta.api;

import io.ensure.deepsea.admin.mta.MTAService;
import io.ensure.deepsea.admin.mta.MidTermAdjustment;
import io.ensure.deepsea.common.RestAPIVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RestMTAAPIVerticle extends RestAPIVerticle {
	
	private static final String MTA = "mta";
	
	private Logger log = LoggerFactory.getLogger(getClass());

	public static final String SERVICE_NAME = "mta-rest-api";

	private static final String API_ADD = "/add";
	
	private DeliveryOptions options = new DeliveryOptions().addHeader("source", MTA);
	
	private final MTAService service;
	
	public RestMTAAPIVerticle(MTAService service) {
		this.service = service;
	}
	
	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();
		final Router router = Router.router(vertx);
		// body handler
		router.route().handler(BodyHandler.create());
		// API route handler
		addHealthHandler(router, future);
		router.post(API_ADD).handler(this::apiAdd);

		// get HTTP host and port from configuration, or use default value
		String host = config().getString("mta.http.address", "0.0.0.0");
		int port = config().getInteger("mta.http.port", 8080);
		
		log.info("Starting Deepsea MTA on host:port " + host + ":" + port);

		// create HTTP server and publish REST service
		createHttpServer(router, host, port)
				.compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, "deepsea-admin-mta.deepsea.svc", port, MTA))
				.setHandler(future.completer());
	}
	
	private void apiAdd(RoutingContext rc) {
		try {
			MidTermAdjustment mta = new MidTermAdjustment(new JsonObject(rc.getBodyAsString()));
			service.addMTA(mta, res -> {
				if (res.succeeded()) {
					mta.setMtaId(res.result());
					String result = new JsonObject().put("message", "mta_added")
							.put("mtaId", mta.getMtaId()).encodePrettily();
					vertx.eventBus().publish(MTA, mta.toJson(), options);
					rc.response().setStatusCode(201).putHeader("content-type", "application/json").end(result);
				} else {
					rc.response().setStatusCode(400).putHeader("content-type", "application/json").end();
				}
			});
		} catch (DecodeException e) {
			badRequest(rc, e);
		}
	}

}
