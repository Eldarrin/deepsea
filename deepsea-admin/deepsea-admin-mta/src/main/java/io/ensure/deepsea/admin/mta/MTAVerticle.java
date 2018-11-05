package io.ensure.deepsea.admin.mta;

import static io.ensure.deepsea.admin.mta.MTAService.SERVICE_ADDRESS;
import static io.ensure.deepsea.admin.mta.MTAService.SERVICE_NAME;

import io.ensure.deepsea.admin.mta.api.RestMTAAPIVerticle;
import io.ensure.deepsea.admin.mta.impl.MongoMTAServiceImpl;
import io.ensure.deepsea.common.BaseMicroserviceVerticle;
import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import io.vertx.serviceproxy.ServiceBinder;

public class MTAVerticle extends BaseMicroserviceVerticle {
	
	private static final String MTA_CHANNEL = "mta";
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	private MTAService mtaService;

	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();
		
		
		
		ConfigRetriever retriever = ConfigRetriever
				.create(vertx, new ConfigRetrieverHelper()
						.getOptions("deepsea", "deepsea-admin-mta"));
        retriever.getConfig(res -> {
        	if (res.succeeded()) {
        		RedisOptions redisConfig = new RedisOptions()
        				.setHost(res.result().getString("redis.host"))
        				.setPort(res.result().getInteger("redis.port"))
        				.setAuth(res.result().getString("redis.auth"));
        				
        		startEBCluster(redisConfig).setHandler(arRedis -> {
        			if (arRedis.succeeded()) {
        				redis.publish(MTA_CHANNEL, new JsonObject().put("started", "true").encodePrettily(), ar -> {
                			if (ar.succeeded()) {
                				log.info("published to Redis");
                				// create the service instance
                        		JsonObject myMongoConfig = new JsonObject()
                        				.put("host", res.result().getString("mongo.host"))
                        				.put("port", res.result().getInteger("mongo.port"))
                        				.put("username", res.result().getString("mongo.username"))
                        				.put("password", res.result().getString("mongo.password"))
                        				.put("db_name", res.result().getString("mongo.database"));

                        		mtaService = new MongoMTAServiceImpl(vertx, myMongoConfig);
                        		// Register the handler
                        		new ServiceBinder(vertx)
                        				.setAddress(SERVICE_ADDRESS)
                        				.register(MTAService.class, mtaService);

                        		initMTADatabase(mtaService);

                        		// publish the service and REST endpoint in the discovery infrastructure
                        		publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, MTAService.class)
                        				.compose(servicePublished -> deployRestVerticle(redis)).setHandler(future.completer());
                        		
                			} else {
                				log.error("Cannot publish to Redis");
                			}
                		});
        				setupReplayConsumer();
        			}
        		});
        				
        		
        		
        	} else {
        		log.error("Unable to find config map for deepsea-admin-enrolment MySQL");
        	}
        
        });
		
	}
	
	private void setupReplayConsumer() {
		vertx.eventBus().<JsonObject>consumer("mta.replay", msg -> 
			mtaService.replayMTAs(msg.body().getInteger("lastId"), res -> {
				if (res.succeeded()) {
					for (MidTermAdjustment mta : res.result()) {
						redis.publish(MTA_CHANNEL, mta.toString(), ar -> {
		        			if (!ar.succeeded()) {
		        				log.error("Cannot publish to Redis, mtaId: " + mta.getMtaId());
		        			}
		        		});
					}
				}
			})
		);
	}
	
	private Future<Void> initMTADatabase(MTAService service) {
		Future<Void> initFuture = Future.future();
		service.initializePersistence(initFuture.completer());
		return initFuture.map(v -> null);
	}

	private Future<Void> deployRestVerticle(RedisClient redis) {
		Future<String> future = Future.future();
		vertx.deployVerticle(new RestMTAAPIVerticle(mtaService, redis),
				new DeploymentOptions().setConfig(config()), future.completer());
		return future.map(r -> null);
	}

}