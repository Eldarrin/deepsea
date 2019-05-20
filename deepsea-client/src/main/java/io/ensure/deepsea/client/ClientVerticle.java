package io.ensure.deepsea.client;

import io.ensure.deepsea.client.api.RestClientAPIVerticle;
import io.ensure.deepsea.client.impl.MongoClientServiceImpl;
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

public class ClientVerticle extends BaseMicroserviceVerticle {
	
	/**
	 * The name of the event bus service.
	 */
	private static final String SERVICE_NAME = "client-service";

	/**
	 * The address on which the service is published.
	 */
	private static final String SERVICE_ADDRESS = "service.client";

	
	private final Logger log = LoggerFactory.getLogger(getClass());

	private ClientService clientService;
	private DeepseaRedis dRedis;

	@Override
	public void start(Future<Void> future) {
		super.start();
		ConfigRetriever retriever = ConfigRetriever
				.create(vertx, new ConfigRetrieverHelper()
						.getOptions("deepsea", "deepsea-client"));
        retriever.getConfig(res -> {
        	if (res.succeeded()) {
        		// create the service instance
        		JsonObject myMongoConfig = new JsonObject()
        				.put("host", res.result().getString("database.host"))
						.put("port", res.result().getInteger("database.port"))
						.put("username", System.getenv("DB_USERNAME"))
						.put("password", System.getenv("DB_PASSWORD"))
						.put("db_name", System.getenv("DB_NAME"));
        		
        		DeepseaRedis.getRedisOptions(vertx, "deepsea-client").setHandler(redisRes -> {

        		clientService = new MongoClientServiceImpl(vertx, myMongoConfig, redisRes.result());
        		// Register the handler
        		new ServiceBinder(vertx)
        				.setAddress(SERVICE_ADDRESS)
        				.register(ClientService.class, clientService);

        		initClientDatabase(clientService);

        		// publish the service and REST endpoint in the discovery infrastructure
        		publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, ClientService.class)
        				.compose(servicePublished -> deployRestVerticle()).setHandler(future);
					dRedis = new DeepseaRedis(vertx, redisRes.result());
					dRedis.startRedisPubSub(vertx, "client", "deepsea-client").setHandler(ar -> {
						if (ar.succeeded()) {
							dRedis.publish("client", new JsonObject().put("started", "true"));
						}
					});

        		});
        		
        	} else {
        		log.error("Unable to find config map for deepsea-client MySQL");
        	}
        
        });
		
	}

	private Future<Void> initClientDatabase(ClientService service) {
		Future<Void> initFuture = Future.future();
		service.initializePersistence(initFuture);
		return initFuture.map(v -> null);
	}

	private Future<Void> deployRestVerticle() {
		Future<String> future = Future.future();
		vertx.deployVerticle(new RestClientAPIVerticle(clientService),
				new DeploymentOptions().setConfig(config()), future);
		return future.map(r -> null);
	}

}
