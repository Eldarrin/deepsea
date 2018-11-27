package io.ensure.deepsea.admin.enrolment;

import static io.ensure.deepsea.admin.enrolment.EnrolmentService.SERVICE_ADDRESS;
import static io.ensure.deepsea.admin.enrolment.EnrolmentService.SERVICE_NAME;

import java.util.ArrayList;
import java.util.List;

import io.ensure.deepsea.admin.enrolment.api.RestEnrolmentAPIVerticle;
import io.ensure.deepsea.admin.enrolment.impl.MySqlEnrolmentServiceImpl;
import io.ensure.deepsea.common.BaseMicroserviceVerticle;
import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
import io.ensure.deepsea.common.helper.RedisHelper;
import io.ensure.deepsea.common.pubsub.RedisPubSub;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.serviceproxy.ServiceBinder;

public class EnrolmentVerticle extends BaseMicroserviceVerticle {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private static final String ENROLMENT_CHANNEL = "enrolment";

	private EnrolmentService enrolmentService;
	
	private RedisPubSub redisPubSub;

	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();
		
		ConfigRetriever retriever = ConfigRetriever
				.create(vertx, new ConfigRetrieverHelper()
						.getOptions("deepsea", "deepsea-admin-enrolment"));
        retriever.getConfig(res -> {
        	if (res.succeeded()) {
        		// create the service instance
        		JsonObject mySqlConfig = new JsonObject()
        				.put("host", res.result().getString("mysql.host"))
        				.put("port", res.result().getInteger("mysql.port"))
        				.put("username", res.result().getString("mysql.username"))
        				.put("password", res.result().getString("mysql.password"))
        				.put("database", res.result().getString("mysql.database"));
        		
        		RedisHelper.getRedisOptions(vertx).setHandler(redisRes -> {
        			enrolmentService = new MySqlEnrolmentServiceImpl(vertx, mySqlConfig, redisRes.result());
            		// Register the handler
            		new ServiceBinder(vertx)
            				.setAddress(SERVICE_ADDRESS)
            				.register(EnrolmentService.class, enrolmentService);

            		initEnrolmentDatabase(enrolmentService);

            		// publish the service and REST endpoint in the discovery infrastructure
            		publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, EnrolmentService.class)
            				.compose(servicePublished -> deployRestVerticle()).setHandler(future.completer());
            		redisPubSub = new RedisPubSub(vertx);
            		redisPubSub.startRedisPubSub(ENROLMENT_CHANNEL).setHandler(ar -> {
            			if (ar.succeeded()) {
            				setupReplayConsumer();
            			}
            		});
        		});

        		
        	} else {
        		log.error("Unable to find config map for deepsea-admin-enrolment MySQL");
        	}
        
        });
		
	}
	
	private void setupReplayConsumer() {
		vertx.eventBus().<JsonObject>consumer(ENROLMENT_CHANNEL + ".replay", msg -> {
			log.info(msg.body());
			enrolmentService.replayEnrolments(msg.body().getString("dateCreated"), msgs -> {
				if (msgs.succeeded()) {
					List<JsonObject> msgJ = new ArrayList<>();
					msgs.result().stream().forEach(msga -> {
						msgJ.add(msga.toJson());
					});
					redisPubSub.replayMessages(ENROLMENT_CHANNEL, msgJ);
				}
			});
		});
		/*
		redisPubSub.listenForReplay(ENROLMENT_CHANNEL).setHandler(res -> {
			if (res.succeeded()) {
				log.info(res.result());
				enrolmentService.replayEnrolments(res.result().getString("dateCreated"), msgs -> {
					if (msgs.succeeded()) {
						List<JsonObject> msgJ = new ArrayList<>();
						msgs.result().stream().forEach(msg -> {
							msgJ.add(msg.toJson());
						});
						redisPubSub.replayMessages(ENROLMENT_CHANNEL, msgJ);
					}
				});
			}
		});*/
	}
		
	private Future<Void> initEnrolmentDatabase(EnrolmentService service) {
		Future<Void> initFuture = Future.future();
		service.initializePersistence(initFuture.completer());
		return initFuture.map(v -> null);
	}

	private Future<Void> deployRestVerticle() {
		Future<String> future = Future.future();
		vertx.deployVerticle(new RestEnrolmentAPIVerticle(enrolmentService),
				new DeploymentOptions().setConfig(config()), future.completer());
		return future.map(r -> null);
	}

}
