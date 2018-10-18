package io.ensure.deepsea.actuarial.bordereau;

import static io.ensure.deepsea.actuarial.bordereau.BordereauService.SERVICE_ADDRESS;
import static io.ensure.deepsea.actuarial.bordereau.BordereauService.SERVICE_NAME;

import io.ensure.deepsea.actuarial.bordereau.api.RestBordereauAPIVerticle;
import io.ensure.deepsea.actuarial.bordereau.impl.MySqlBordereauServiceImpl;
import io.ensure.deepsea.common.BaseMicroserviceVerticle;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;

public class BordereauVerticle extends BaseMicroserviceVerticle {

	private BordereauService bordereauService;
	
    private static final String HOCON = "hocon";
    private static final String CONFIGMAP = "configmap";
    private static final String NAMESPACE = "deepsea";
    private static final String SECRET = "secret";
    private static final String OPTIONAL = "optional";

	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();
		
		ConfigRetrieverOptions configRetrieverOptions = new ConfigRetrieverOptions();
        if (System.getenv().containsKey("OPENSHIFT_BUILD_NAMESPACE")) {
            ConfigStoreOptions kubeConfig = new ConfigStoreOptions()
                    .setType(CONFIGMAP)
                    .setFormat(HOCON)
                    .setConfig(new JsonObject()
                            .put(OPTIONAL, true)
                            .put("name", "deepsea-underwriting-actuarial"));
            configRetrieverOptions
                .addStore(kubeConfig);        // Values here will override identical keys from above
        }

		// create the service instance
		JsonObject mySqlConfig = new JsonObject()
				.put("host", "mysql")
				.put("port", 3306)
				.put("username", "userT2K")
				.put("password", "18iPeN4wGXA1FpHH")
				.put("database", "sampledb");

		bordereauService = new MySqlBordereauServiceImpl(vertx, mySqlConfig);
		// Register the handler
		new ServiceBinder(vertx)
				.setAddress(SERVICE_ADDRESS)
				.register(BordereauService.class, bordereauService);

		initBordereauDatabase(bordereauService);

		// register the service proxy on event bus
		// ProxyHelper.registerService(AccountService.class, vertx, accountService,
		// SERVICE_ADDRESS);
		// publish the service and REST endpoint in the discovery infrastructure
		publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, BordereauService.class)
				.compose(servicePublished -> deployRestVerticle()).setHandler(future.completer());

	}

	private Future<Void> initBordereauDatabase(BordereauService service) {
		Future<Void> initFuture = Future.future();
		service.initializePersistence(initFuture.completer());
		return initFuture.map(v -> {
			return null;
		});
	}

	private Future<Void> deployRestVerticle() {
		Future<String> future = Future.future();
		vertx.deployVerticle(new RestBordereauAPIVerticle(bordereauService),
				new DeploymentOptions().setConfig(config()), future.completer());
		return future.map(r -> null);
	}
}
