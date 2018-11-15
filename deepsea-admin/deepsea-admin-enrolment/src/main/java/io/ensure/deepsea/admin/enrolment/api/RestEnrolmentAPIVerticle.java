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
import io.vertx.redis.RedisClient;

public class RestEnrolmentAPIVerticle extends RestAPIVerticle {

	private static final String ENROLMENT = "enrolment";

	private Logger log = LoggerFactory.getLogger(getClass());

	public static final String SERVICE_NAME = "enrolment-rest-api";

	private static final String API_ADD = "/add";
	
	private final EnrolmentService enrolmentService;
	
	public RestEnrolmentAPIVerticle(EnrolmentService enrolmentService, RedisClient redis) {
		super(redis);
		this.enrolmentService = enrolmentService;
	}
	
	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();
		final Router router = Router.router(vertx);
		router.post(API_ADD).handler(this::apiAdd);
		startRestService(router, future, SERVICE_NAME, ENROLMENT, "deepsea", "deepsea-admin-enrolment");
	}
	
	private void apiAdd(RoutingContext rc) {
		try {
			Enrolment enrolment = new Enrolment(new JsonObject(rc.getBodyAsString()));

			enrolmentService.addEnrolment(enrolment, res -> {
				if (res.succeeded()) {
					enrolment.setEnrolmentId(res.result());
					redis.publish(ENROLMENT, enrolment.toString(), ar -> {
		    			if (ar.succeeded()) {
		    				String result = new JsonObject().put("message", "enrolment_added")
									.put("enrolmentId", enrolment.getEnrolmentId()).encodePrettily();
		    				rc.response().setStatusCode(201).putHeader(CONTENT_TYPE, APPLICATION_JSON).end(result);
		    			} else {
		    				log.error("failed to publish");
		    				rc.response().setStatusCode(400).putHeader(CONTENT_TYPE, APPLICATION_JSON).end();
		    			}
		    		});
				} else {
					log.error("failed to write to db");
    				rc.response().setStatusCode(400).putHeader(CONTENT_TYPE, APPLICATION_JSON).end();
				}
			});
		} catch (DecodeException e) {
			badRequest(rc, e);
		}
	}

}
