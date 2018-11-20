package io.ensure.deepsea.client.impl;

import java.util.List;
import java.util.stream.Collectors;

import io.ensure.deepsea.client.Client;
import io.ensure.deepsea.client.ClientService;
import io.ensure.deepsea.common.service.MySqlRepositoryWrapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class MySqlClientServiceImpl extends MySqlRepositoryWrapper implements ClientService {

	// SQL Statements
	private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS `client` (\n"
			+ "  `clientId` INT NOT NULL AUTO_INCREMENT, \n" + "  `clientName` VARCHAR(100) NOT NULL, \n"
			+ "  PRIMARY KEY (`clientId`))";

	private static final String INSERT_STATEMENT = "INSERT INTO client (`clientName`) \n"
			+ "  VALUES(?)";

	private static final String FETCH_STATEMENT = "SELECT * FROM client";
	
	private static final String REMOVE_STATEMENT = "DELETE FROM client WHERE clientId = ?";

	public MySqlClientServiceImpl(Vertx vertx, JsonObject config) {
		super(vertx, config);
	}
	
	@Override
	public ClientService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
		client.getConnection(connHandler(resultHandler, connection -> 
			connection.execute(CREATE_STATEMENT, r -> {
				resultHandler.handle(r);
				connection.close();
			})
		));
		return this;
	}

	@Override
	public ClientService addClient(Client client, Handler<AsyncResult<Void>> resultHandler) {
		JsonArray params = new JsonArray()
				.add(client.getClientName());
		executeNoResult(params, INSERT_STATEMENT, resultHandler);
		return this;
	}

	@Override
	public ClientService retrieveClients(Handler<AsyncResult<List<Client>>> resultHandler) {
		this.retrieveAll(FETCH_STATEMENT)
		.map(rawList -> rawList.stream().map(Client::new).collect(Collectors.toList()))
		.setHandler(resultHandler);
		return this;
	}

	@Override
	public ClientService removeClient(Client client, Handler<AsyncResult<Void>> resultHandler) {
		this.removeOne(client.getClientId(), REMOVE_STATEMENT, resultHandler);
		return this;
	}

}
