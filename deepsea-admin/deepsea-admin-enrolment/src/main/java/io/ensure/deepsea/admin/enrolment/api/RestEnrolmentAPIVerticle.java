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

public class RestEnrolmentAPIVerticle extends RestAPIVerticle {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	private static final String ENROLMENT = "enrolment";

	public static final String SERVICE_NAME = "enrolment-rest-api";

	private static final String API_ADD = "/add";
	
	private final EnrolmentService enrolmentService;
	
	public RestEnrolmentAPIVerticle(EnrolmentService enrolmentService) {
		super();
		this.enrolmentService = enrolmentService;
	}
	
	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();
		final Router router = Router.router(vertx);
		addHealthHandler(router, future);
		router.route().handler(BodyHandler.create());
		router.post(API_ADD).handler(this::apiAdd);
		startRestService(router, future, SERVICE_NAME, ENROLMENT, "deepsea", "deepsea-admin-enrolment");
	}
	
	private void apiAdd(RoutingContext rc) {
		try {
			log.info("In API Add");
			log.info(rc.getBodyAsString());
			Enrolment enrolment = new Enrolment(new JsonObject(rc.getBodyAsString()));
			log.info("In API JSON Coerce");
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
