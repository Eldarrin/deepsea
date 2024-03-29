package io.ensure.deepsea.ui.menu;

import io.ensure.deepsea.common.BaseMicroserviceVerticle;
import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
import io.ensure.deepsea.common.service.DeepseaRedis;
import io.ensure.deepsea.ui.menu.api.RestMenuAPIVerticle;
import io.ensure.deepsea.ui.menu.impl.MongoMenuServiceImpl;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.client.RedisOptions;
import io.vertx.serviceproxy.ServiceBinder;

public class MenuVerticle extends BaseMicroserviceVerticle {

	/**
	 * The name of the event bus service.
	 */
	private static final String SERVICE_NAME = "menu-service";

	/**
	 * The address on which the service is published.
	 */
	private static final String SERVICE_ADDRESS = "service.menu";

	private static final String REDIS_JSON_VALUE = "value";
	private static final String REDIS_CHANNEL = "io.vertx.redis.";
	private static final String MENU_CHANNEL = "menu";
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	private MenuService menuService;

	private DeepseaRedis dRedis;
	
	@Override
	public void start(Future<Void> future) {
		super.start();
		ConfigRetriever retriever = ConfigRetriever
				.create(vertx, new ConfigRetrieverHelper()
						.getOptions("deepsea", "deepsea-ui-menu"));
        retriever.getConfig(res -> {
        	if (res.succeeded()) {
        		// create the service instance
        		JsonObject myMongoConfig = new JsonObject()
        				.put("host", res.result().getString("database.host"))
						.put("port", res.result().getInteger("database.port"))
						.put("username", System.getenv("DB_USERNAME"))
						.put("password", System.getenv("DB_PASSWORD"))
						.put("db_name", System.getenv("DB_NAME"));
        		
        		DeepseaRedis.getRedisOptions(vertx, "deepsea-ui-menu").setHandler(redisRes -> {

        		menuService = new MongoMenuServiceImpl(vertx, myMongoConfig, redisRes.result());
        		// Register the handler
        		new ServiceBinder(vertx)
        				.setAddress(SERVICE_ADDRESS)
        				.register(MenuService.class, menuService);

        		initMenuDatabase(menuService);

        		// publish the service and REST endpoint in the discovery infrastructure
        		publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, MenuService.class)
        				.compose(servicePublished -> deployRestVerticle()).setHandler(future);
					dRedis = new DeepseaRedis(vertx, redisRes.result());
					dRedis.startRedisPubSub(vertx, "menu", "deepsea-ui-menu").setHandler(ar -> {
						if (ar.succeeded()) {
							dRedis.publish("menu", new JsonObject().put("started", "true"));
						}
					});
        		setupConsumer(redisRes.result());
        		});
        		
        	} else {
        		log.error("Unable to find config map for deepsea-client MySQL");
        	}
        
        });
		
	}
	
	private void setupConsumer(RedisOptions redisOptions) {
		vertx.eventBus().<JsonObject>consumer(REDIS_CHANNEL + MENU_CHANNEL, received -> {
			String message = received.body().getJsonObject(REDIS_JSON_VALUE).getString("message");
			log.trace(message);
			addMenuItem(new JsonObject(message));
		});

		dRedis = new DeepseaRedis(vertx, redisOptions);

		dRedis.subscribe(MENU_CHANNEL);
		
	}
	
	private void addMenuItem(JsonObject menuJson) {
		MenuItem menuItem = new MenuItem(menuJson);
		menuService.addMenu(menuItem, ar -> {
			if (!ar.succeeded()) {
				log.error(ar.cause());
			}
		});
	}

	private Future<Void> initMenuDatabase(MenuService service) {
		Future<Void> initFuture = Future.future();
		service.initializePersistence(initFuture);
		return initFuture.map(v -> null);
	}

	private Future<Void> deployRestVerticle() {
		Future<String> future = Future.future();
		vertx.deployVerticle(new RestMenuAPIVerticle(menuService),
				new DeploymentOptions().setConfig(config()), future);
		return future.map(r -> null);
	}
}
