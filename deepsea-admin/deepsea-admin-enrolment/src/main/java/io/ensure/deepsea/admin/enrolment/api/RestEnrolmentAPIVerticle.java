package io.ensure.deepsea.admin.enrolment.api;

import io.ensure.deepsea.admin.enrolment.EnrolmentService;
import io.ensure.deepsea.admin.enrolment.models.Enrolment;
import io.ensure.deepsea.common.RestAPIVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class RestEnrolmentAPIVerticle extends RestAPIVerticle {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	private static final String ENROLMENT = "enrolment";

	public static final String SERVICE_NAME = "enrolment-rest-api";

	private static final String API_ADD = "/add";
	
	private final EnrolmentService enrolmentService;
	
	public RestEnrolmentAPIVerticle(EnrolmentService enrolmentService) {
		super();
		this.enrolmentService = enrolmentService;
	}
	
	@Override
	public void start(Future<Void> future) {
		super.start();
		final Router router = Router.router(vertx);
		this.enableCorsSupport(router);
		addHealthHandler(router, future);
		router.route().handler(BodyHandler.create());
		router.post(API_ADD).handler(this::apiAdd);
		router.route("/*").handler(StaticHandler.create());
		startRestService(router, future, SERVICE_NAME, ENROLMENT, "deepsea", "deepsea-admin-enrolment");
	}
	
	private void apiAdd(RoutingContext rc) {
		try {
			Enrolment enrolment = new Enrolment(new JsonObject(rc.getBodyAsString()));
			enrolmentService.addEnrolment(enrolment, res -> {
				if (res.succeeded()) {
    				rc.response().setStatusCode(201).putHeader(CONTENT_TYPE, APPLICATION_JSON).end(res.result().toString());
				} else {
					log.error(res.cause());
    				rc.response().setStatusCode(400).putHeader(CONTENT_TYPE, APPLICATION_JSON).end();
				}
			});
		} catch (DecodeException e) {
			badRequest(rc, e);
		}
	}

}
