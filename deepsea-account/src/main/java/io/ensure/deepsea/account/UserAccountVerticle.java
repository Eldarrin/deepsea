package io.ensure.deepsea.account;

import static io.ensure.deepsea.account.AccountService.SERVICE_ADDRESS;
import static io.ensure.deepsea.account.AccountService.SERVICE_NAME;

import io.ensure.deepsea.account.api.RestUserAccountAPIVerticle;
import io.ensure.deepsea.account.impl.MySqlAccountServiceImpl;
import io.ensure.deepsea.common.BaseMicroserviceVerticle;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;

/**
 * A verticle publishing the user service.
 *
 * @author Eric Zhao
 */
public class UserAccountVerticle extends BaseMicroserviceVerticle {

	private AccountService accountService;

	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();

		// create the service instance
		JsonObject mySqlConfig = new JsonObject()
				.put("host", "mysql")
				.put("port", 3306)
				.put("username", "user3T2")
				.put("password", "LlahjdSHYxQeAaFr")
				.put("database", "accountdb");
		
		accountService = new MySqlAccountServiceImpl(vertx, mySqlConfig);
		// Register the handler
		new ServiceBinder(vertx)
			.setAddress(SERVICE_ADDRESS)
			.register(AccountService.class, accountService);

		// register the service proxy on event bus
		//ProxyHelper.registerService(AccountService.class, vertx, accountService, SERVICE_ADDRESS);
		// publish the service and REST endpoint in the discovery infrastructure
		publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, AccountService.class)
				.compose(servicePublished -> deployRestVerticle()).setHandler(future.completer());
				
	}

	private Future<Void> deployRestVerticle() {
		Future<String> future = Future.future();
		vertx.deployVerticle(new RestUserAccountAPIVerticle(accountService),
				new DeploymentOptions().setConfig(config()), future.completer());
		return future.map(r -> null);
	}
	
}
