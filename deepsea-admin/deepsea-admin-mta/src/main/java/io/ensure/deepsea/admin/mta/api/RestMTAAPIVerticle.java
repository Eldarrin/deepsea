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
import io.vertx.redis.RedisClient;

public class RestMTAAPIVerticle extends RestAPIVerticle {
	
	private static final String MTA = "mta";
	
	private Logger log = LoggerFactory.getLogger(getClass());

	public static final String SERVICE_NAME = "mta-rest-api";

	private static final String API_ADD = "/add";

	private final MTAService mtaService;
	
	public RestMTAAPIVerticle(MTAService mtaService, RedisClient redis) {
		super(redis);
		this.mtaService = mtaService;
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

			mtaService.addMTA(mta, res -> {
				if (res.succeeded()) {
					mta.setMtaId(res.result());
					redis.publish(MTA, mta.toString(), ar -> {
						if (ar.succeeded()) {
		    				String result = new JsonObject().put("message", "mta_added")
									.put("mtaId", mta.getMtaId()).encodePrettily();
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
