package io.ensure.deepsea.shared.client.impl;

import java.util.List;
import java.util.stream.Collectors;

import io.ensure.deepsea.common.service.MySqlRepositoryWrapper;
import io.ensure.deepsea.shared.client.Client;
import io.ensure.deepsea.shared.client.ClientService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class MySqlClientServiceImpl extends MySqlRepositoryWrapper implements ClientService {

	// SQL Statements
	private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS `client` (\n"
			+ "  `clientId` VARCHAR(60) NOT NULL, \n" + "  `clientName` VARCHAR(100) NOT NULL, \n"
			+ "  PRIMARY KEY (`clientId`))";

	private static final String INSERT_STATEMENT = "INSERT INTO client (`clientId`, `clientName`) \n"
			+ "  VALUES(?, ?)";

	private static final String FETCH_STATEMENT = "SELECT * FROM client";

	public MySqlClientServiceImpl(Vertx vertx, JsonObject config) {
		super(vertx, config);
	}
	
	@Override
	public ClientService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
		client.getConnection(connHandler(resultHandler, connection -> {
			connection.execute(CREATE_STATEMENT, r -> {
				resultHandler.handle(r);
				connection.close();
			});
		}));
		return this;
	}

	@Override
	public ClientService addClient(Client client, Handler<AsyncResult<Void>> resultHandler) {
		JsonArray params = new JsonArray().add(client.getClientId())
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
	public ClientService removeClient(Client client, Handler<AsyncResult<Client>> resultHandler) {
		// TODO Auto-generated method stub
		return null;
	}

}
