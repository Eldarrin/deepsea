package io.ensure.deepsea.admin.mta;

import io.ensure.deepsea.admin.mta.api.RestMTAAPIVerticle;
import io.ensure.deepsea.admin.mta.impl.MongoMTAServiceImpl;
import io.ensure.deepsea.common.BaseMicroserviceVerticle;
import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
import io.ensure.deepsea.common.service.DeepseaRedis;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.serviceproxy.ServiceBinder;

public class MTAVerticle extends BaseMicroserviceVerticle {
	
	
	private static final String SERVICE_NAME = "mta-eb-service";
	private static final String SERVICE_ADDRESS = "service.mta";
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	private MTAService mtaService;

	@Override
	public void start(Future<Void> future) {
		super.start();

		ConfigRetriever retriever = ConfigRetriever
				.create(vertx, new ConfigRetrieverHelper()
						.getOptions("deepsea", "deepsea-admin-mta"));
        retriever.getConfig(res -> {
        	if (res.succeeded()) {
				// create the service instance
        		JsonObject myMongoConfig = new JsonObject()
        				.put("host", res.result().getString("database.host"))
						.put("port", res.result().getInteger("database.port"))
						.put("username", System.getenv("DB_USERNAME"))
						.put("password", System.getenv("DB_PASSWORD"))
						.put("db_name", System.getenv("DB_NAME"));
        		
        		DeepseaRedis.getRedisOptions(vertx, "deepsea-admin-mta").setHandler(resRedis -> {
        			if (resRedis.succeeded()) {
        				mtaService = new MongoMTAServiceImpl(vertx, myMongoConfig, resRedis.result());
                		// Register the handler
                		new ServiceBinder(vertx)
                				.setAddress(SERVICE_ADDRESS)
                				.register(MTAService.class, mtaService);

                		initMTADatabase(mtaService);

                		// publish the service and REST endpoint in the discovery infrastructure
                		publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, MTAService.class)
                				.compose(servicePublished -> deployRestVerticle()).setHandler(future);
        			} else {
        				log.error("Cannot find Redis Config");
        			}
        		});

        		
        		
			} else {
				log.error("Unable to find config map for deepsea-admin-mta Mongo");
			}
        });
		
	}
	
	private Future<Void> initMTADatabase(MTAService service) {
		Future<Void> initFuture = Future.future();
		service.initializePersistence(initFuture);
		return initFuture.map(v -> null);
	}

	private Future<Void> deployRestVerticle() {
		Future<String> future = Future.future();
		vertx.deployVerticle(new RestMTAAPIVerticle(mtaService),
				new DeploymentOptions().setConfig(config()), future);
		return future.map(r -> null);
	}

}