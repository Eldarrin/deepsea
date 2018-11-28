package io.ensure.deepsea.admin.mta.api;

import io.ensure.deepsea.admin.mta.MTAService;
import io.ensure.deepsea.admin.mta.MidTermAdjustment;
import io.ensure.deepsea.common.RestAPIVerticle;
import io.vertx.core.Future;
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

	private final MTAService mtaService;
	
	public RestMTAAPIVerticle(MTAService mtaService) {
		super();
		this.mtaService = mtaService;
	}
	
	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();
		final Router router = Router.router(vertx);
		addHealthHandler(router, future);
		router.route().handler(BodyHandler.create());
		router.post(API_ADD).handler(this::apiAdd);
		
		startRestService(router, future, SERVICE_NAME, MTA, "deepsea", "deepsea-admin-mta");
	
	}
	
	private void apiAdd(RoutingContext rc) {
		try {
			MidTermAdjustment mta = new MidTermAdjustment(new JsonObject(rc.getBodyAsString()));

			mtaService.addMTA(mta, res -> {
				if (res.succeeded()) {
					rc.response().setStatusCode(201).putHeader(CONTENT_TYPE, APPLICATION_JSON).end(res.result().toString());
				} else {
					log.error(res.cause());
					log.error("failed to write to db");
    				rc.response().setStatusCode(400).putHeader(CONTENT_TYPE, APPLICATION_JSON).end();
				}
			});
		} catch (DecodeException e) {
			badRequest(rc, e);
		}
	}

}
