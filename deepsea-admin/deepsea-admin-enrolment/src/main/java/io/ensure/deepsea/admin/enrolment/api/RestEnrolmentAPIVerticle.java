package io.ensure.deepsea.admin.enrolment.api;

import io.ensure.deepsea.admin.enrolment.EnrolmentService;
import io.ensure.deepsea.admin.enrolment.models.Enrolment;
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

public class RestEnrolmentAPIVerticle extends RestAPIVerticle {

	private Logger log = LoggerFactory.getLogger(getClass());

	public static final String SERVICE_NAME = "enrolment-rest-api";

	private static final String API_ADD = "/add";
	
	private DeliveryOptions options = new DeliveryOptions().addHeader("source", "enrolment");
	
	private final EnrolmentService service;
	
	public RestEnrolmentAPIVerticle(EnrolmentService service) {
		this.service = service;
	}
	
	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();
		final Router router = Router.router(vertx);
		// body handler
		router.route().handler(BodyHandler.create());
		// API route handler
		router.get(HEALTH).handler(rc -> {
			if (future.succeeded()) {
				rc.response().end("Ready");
			} else {
				rc.response().setStatusCode(503).end();
			}
		});
		router.post(API_ADD).handler(this::apiAdd);

		// get HTTP host and port from configuration, or use default value
		String host = config().getString("enrolment.http.address", "0.0.0.0");
		int port = config().getInteger("enrolment.http.port", 8080);
		
		log.info("Starting Deepsea Enrolment on host:port " + host + ":" + port);

		// create HTTP server and publish REST service
		createHttpServer(router, host, port)
				.compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, "deepsea-admin-enrolment.deepsea.svc", port, "enrolment"))
				.setHandler(future.completer());
	}
	
	private void apiAdd(RoutingContext rc) {
		try {
			Enrolment enrolment = new Enrolment(new JsonObject(rc.getBodyAsString()));
			service.addEnrolment(enrolment, res -> {
				if (res.succeeded()) {
					enrolment.setEnrolmentId(res.result());
					String result = new JsonObject().put("message", "enrolment_added")
							.put("enrolmentId", enrolment.getEnrolmentId()).encodePrettily();
					vertx.eventBus().publish("enrolment", enrolment.toJson(), options);
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
