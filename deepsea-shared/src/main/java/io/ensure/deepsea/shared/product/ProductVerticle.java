package io.ensure.deepsea.shared.product;

import static io.ensure.deepsea.shared.product.ProductService.SERVICE_ADDRESS;
import static io.ensure.deepsea.shared.product.ProductService.SERVICE_NAME;

import io.ensure.deepsea.common.BaseMicroserviceVerticle;
import io.ensure.deepsea.shared.product.api.RestProductAPIVerticle;
import io.ensure.deepsea.shared.product.impl.MySqlProductServiceImpl;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;

public class ProductVerticle extends BaseMicroserviceVerticle {
	
	private ProductService productService;
	
    private static final String HOCON = "hocon";
    private static final String CONFIGMAP = "configmap";
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
                            .put("name", "deepsea-product"));
            configRetrieverOptions
                .addStore(kubeConfig);        // Values here will override identical keys from above
        }

		
		// create the service instance
		JsonObject mySqlConfig = new JsonObject()
				.put("host", "mysql")
				.put("port", 3306)
				.put("username", "userWYY")
				.put("password", "5FneIc4JkLWdxGYA")
				.put("database", "sampledb");
		
		productService = new MySqlProductServiceImpl(vertx, mySqlConfig);
		// Register the handler
		new ServiceBinder(vertx)
			.setAddress(SERVICE_ADDRESS)
			.register(ProductService.class, productService);
		
		initProductDatabase(productService);
		
		// register the service proxy on event bus
	    //ProxyHelper.registerService(ProductService.class, vertx, productService, SERVICE_ADDRESS);
	    
		// publish the service and REST endpoint in the discovery infrastructure
		publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, ProductService.class)
				.compose(servicePublished -> deployRestVerticle()).setHandler(future.completer());
		
		
	}
	
	
	private Future<Void> initProductDatabase(ProductService service) {
		Future<Void> initFuture = Future.future();
		service.initializePersistence(initFuture.completer());
		return initFuture.map(v -> {
			return null;
		});
	}

	private Future<Void> deployRestVerticle() {
		Future<String> future = Future.future();
		vertx.deployVerticle(new RestProductAPIVerticle(productService),
				new DeploymentOptions().setConfig(config()), future.completer());
		return future.map(r -> null);
	}

}
