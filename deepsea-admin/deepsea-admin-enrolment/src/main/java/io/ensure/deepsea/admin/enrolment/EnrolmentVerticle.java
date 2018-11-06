package io.ensure.deepsea.admin.enrolment;

import static io.ensure.deepsea.admin.enrolment.EnrolmentService.SERVICE_ADDRESS;
import static io.ensure.deepsea.admin.enrolment.EnrolmentService.SERVICE_NAME;

import io.ensure.deepsea.admin.enrolment.api.RestEnrolmentAPIVerticle;
import io.ensure.deepsea.admin.enrolment.impl.MySqlEnrolmentServiceImpl;
import io.ensure.deepsea.admin.enrolment.models.Enrolment;
import io.ensure.deepsea.common.BaseMicroserviceVerticle;
import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisOptions;
import io.vertx.serviceproxy.ServiceBinder;

public class EnrolmentVerticle extends BaseMicroserviceVerticle {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private static final String ENROLMENT_CHANNEL = "enrolment";

	private EnrolmentService enrolmentService;

	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();
		startEBCluster();
		
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

        		enrolmentService = new MySqlEnrolmentServiceImpl(vertx, mySqlConfig);
        		// Register the handler
        		new ServiceBinder(vertx)
        				.setAddress(SERVICE_ADDRESS)
        				.register(EnrolmentService.class, enrolmentService);

        		initEnrolmentDatabase(enrolmentService);

        		// publish the service and REST endpoint in the discovery infrastructure
        		publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, EnrolmentService.class)
        				.compose(servicePublished -> deployRestVerticle()).setHandler(future.completer());
        		
        		setupReplayConsumer();
        	} else {
        		log.error("Unable to find config map for deepsea-admin-enrolment MySQL");
        	}
        
        });
        
        ConfigRetriever redisRetriever = ConfigRetriever.create(vertx,
				new ConfigRetrieverHelper().getOptions("deepsea", "deepsea-redis"));

		redisRetriever.getConfig(res -> {
			if (res.succeeded()) {
				RedisOptions redisConfig = new RedisOptions()
						.setHost(res.result().getString("redis.host"))
						.setPort(res.result().getInteger("redis.port"))
						.setAuth(res.result().getString("redis.auth"));
				
				startEBCluster(redisConfig).setHandler(redisResult -> {
					if (redisResult.succeeded()) {
						redis.publish(ENROLMENT_CHANNEL, new JsonObject().put("started", "true").encodePrettily(), ar -> {
							log.info("Publish successful");
							setupReplayConsumer();
						});
					} else {
						log.error("Failed to connect to Redis");
					}
				});

				
				// requestMissed();
			}
		});
		
	}
	
	private void setupReplayConsumer() {
		vertx.eventBus().<JsonObject>consumer("enrolment.replay", msg -> 
		enrolmentService.replayEnrolments(msg.body().getInteger("lastId"), res -> {
				if (res.succeeded()) {
					for (Enrolment enrolment : res.result()) {
						redis.publish(ENROLMENT_CHANNEL, enrolment.toString(), ar -> {
		        			if (!ar.succeeded()) {
		        				log.error("Cannot publish to Redis, enrolmentId: " + enrolment.getEnrolmentId());
		        			}
		        		});
					}
				}
			})
		);
	}
		
	private Future<Void> initEnrolmentDatabase(EnrolmentService service) {
		Future<Void> initFuture = Future.future();
		service.initializePersistence(initFuture.completer());
		return initFuture.map(v -> null);
	}

	private Future<Void> deployRestVerticle() {
		Future<String> future = Future.future();
		vertx.deployVerticle(new RestEnrolmentAPIVerticle(enrolmentService, redis),
				new DeploymentOptions().setConfig(config()), future.completer());
		return future.map(r -> null);
	}

}
