package io.ensure.deepsea.shared.client;

import static io.ensure.deepsea.shared.client.ClientService.SERVICE_ADDRESS;
import static io.ensure.deepsea.shared.client.ClientService.SERVICE_NAME;

import io.ensure.deepsea.common.BaseMicroserviceVerticle;
import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
import io.ensure.deepsea.shared.client.api.RestClientAPIVerticle;
import io.ensure.deepsea.shared.client.impl.MySqlClientServiceImpl;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.serviceproxy.ServiceBinder;

public class ClientVerticle extends BaseMicroserviceVerticle {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	private ClientService clientService;

	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();

		ConfigRetriever retriever = ConfigRetriever
				.create(vertx, new ConfigRetrieverHelper()
						.getOptions("deepsea", "deepsea-shared"));
        retriever.getConfig(res -> {
        	if (res.succeeded()) {
        		// create the service instance
        		JsonObject mySqlConfig = new JsonObject()
        				.put("host", res.result().getString("mysql.host"))
        				.put("port", res.result().getInteger("mysql.port"))
        				.put("username", res.result().getString("mysql.username"))
        				.put("password", res.result().getString("mysql.password"))
        				.put("database", res.result().getString("mysql.database"));

        		clientService = new MySqlClientServiceImpl(vertx, mySqlConfig);
        		// Register the handler
        		new ServiceBinder(vertx)
        				.setAddress(SERVICE_ADDRESS)
        				.register(ClientService.class, clientService);

        		initClientDatabase(clientService);

        		// publish the service and REST endpoint in the discovery infrastructure
        		publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, ClientService.class)
        				.compose(servicePublished -> deployRestVerticle()).setHandler(future.completer());
        	} else {
        		log.error("Unable to find config map for deepsea-shared MySQL");
        	}
        
        });
		
	}

	private Future<Void> initClientDatabase(ClientService service) {
		Future<Void> initFuture = Future.future();
		service.initializePersistence(initFuture.completer());
		return initFuture.map(v -> {
			return null;
		});
	}

	private Future<Void> deployRestVerticle() {
		Future<String> future = Future.future();
		vertx.deployVerticle(new RestClientAPIVerticle(clientService),
				new DeploymentOptions().setConfig(config()), future.completer());
		return future.map(r -> null);
	}

}
