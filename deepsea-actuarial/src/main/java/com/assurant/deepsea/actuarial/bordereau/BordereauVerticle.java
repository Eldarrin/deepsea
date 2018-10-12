package io.ensure.deepsea.actuarial.bordereau;

import static io.ensure.deepsea.actuarial.bordereau.BordereauService.SERVICE_ADDRESS;
import static io.ensure.deepsea.actuarial.bordereau.BordereauService.SERVICE_NAME;

import io.ensure.deepsea.actuarial.bordereau.api.RestBordereauAPIVerticle;
import io.ensure.deepsea.actuarial.bordereau.impl.MySqlBordereauServiceImpl;
import io.ensure.deepsea.common.BaseMicroserviceVerticle;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;

public class BordereauVerticle extends BaseMicroserviceVerticle {

	private BordereauService bordereauService;

	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();

		// create the service instance
		JsonObject mySqlConfig = new JsonObject().put("host", "mysql").put("port", 3306).put("username", "user3T2")
				.put("password", "LlahjdSHYxQeAaFr").put("database", "accountdb");

		bordereauService = new MySqlBordereauServiceImpl(vertx, mySqlConfig);
		// Register the handler
		new ServiceBinder(vertx).setAddress(SERVICE_ADDRESS).register(BordereauService.class, bordereauService);

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
