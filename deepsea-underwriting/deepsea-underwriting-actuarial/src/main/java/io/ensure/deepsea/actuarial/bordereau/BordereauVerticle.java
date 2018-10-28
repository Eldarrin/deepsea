package io.ensure.deepsea.actuarial.bordereau;

import static io.ensure.deepsea.actuarial.bordereau.BordereauService.SERVICE_ADDRESS;
import static io.ensure.deepsea.actuarial.bordereau.BordereauService.SERVICE_NAME;
import static io.ensure.deepsea.shared.client.ClientService.SERVICE_ADDRESS;
import static io.ensure.deepsea.shared.client.ClientService.SERVICE_NAME;

import io.ensure.deepsea.actuarial.bordereau.api.RestBordereauAPIVerticle;
import io.ensure.deepsea.actuarial.bordereau.impl.MySqlBordereauServiceImpl;
import io.ensure.deepsea.common.BaseMicroserviceVerticle;
import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
import io.ensure.deepsea.shared.client.ClientService;
import io.ensure.deepsea.shared.client.impl.MySqlClientServiceImpl;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.serviceproxy.ServiceBinder;

public class BordereauVerticle extends BaseMicroserviceVerticle {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	private BordereauService bordereauService;

	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();
		
		ConfigRetriever retriever = ConfigRetriever
				.create(vertx, new ConfigRetrieverHelper()
						.getOptions("deepsea", "deepsea-underwriting-actuarial"));
        retriever.getConfig(res -> {
        	if (res.succeeded()) {
        		// create the service instance
        		JsonObject mySqlConfig = new JsonObject()
        				.put("host", res.result().getString("mysql.host"))
        				.put("port", res.result().getInteger("mysql.port"))
        				.put("username", res.result().getString("mysql.username"))
        				.put("password", res.result().getString("mysql.password"))
        				.put("database", res.result().getString("mysql.database"));

        		bordereauService = new MySqlBordereauServiceImpl(vertx, mySqlConfig);
        		// Register the handler
        		new ServiceBinder(vertx)
        				.setAddress(SERVICE_ADDRESS)
        				.register(BordereauService.class, bordereauService);

        		initBordereauDatabase(bordereauService);

        		// publish the service and REST endpoint in the discovery infrastructure
        		publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, BordereauService.class)
        				.compose(servicePublished -> deployRestVerticle()).setHandler(future.completer());
        	} else {
        		log.error("Unable to find config map for deepsea-underwriting-actuarial MySQL");
        	}
        
        });

	}

	private Future<Void> initBordereauDatabase(BordereauService service) {
		Future<Void> initFuture = Future.future();
		service.initializePersistence(initFuture.completer());
		return initFuture.map(v -> null);
	}

	private Future<Void> deployRestVerticle() {
		Future<String> future = Future.future();
		vertx.deployVerticle(new RestBordereauAPIVerticle(bordereauService),
				new DeploymentOptions().setConfig(config()), future.completer());
		return future.map(r -> null);
	}
}
