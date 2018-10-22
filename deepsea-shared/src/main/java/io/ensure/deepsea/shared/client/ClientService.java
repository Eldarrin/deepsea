package io.ensure.deepsea.shared.client;

import java.util.List;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@VertxGen
@ProxyGen
public interface ClientService {

	/**
	 * The name of the event bus service.
	 */
	String SERVICE_NAME = "client-service";

	/**
	 * The address on which the service is published.
	 */
	String SERVICE_ADDRESS = "service.client";

	/**
	 * Initialize the persistence.
	 *
	 * @param resultHandler the result handler will be called as soon as the
	 *                      initialization has been accomplished. The async result
	 *                      indicates whether the operation was successful or not.
	 */
	@Fluent
	ClientService initializePersistence(Handler<AsyncResult<Void>> resultHandler);

	@Fluent
	ClientService addClient(Client client, Handler<AsyncResult<Void>> resultHandler);

	@Fluent
	ClientService retrieveClients(Handler<AsyncResult<List<Client>>> resultHandler);
	
	@Fluent
	ClientService removeClient(Client client, Handler<AsyncResult<Client>> resultHandler);

}
