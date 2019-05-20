package io.ensure.deepsea.client.impl;

import java.util.List;
import java.util.stream.Collectors;

import io.ensure.deepsea.client.Client;
import io.ensure.deepsea.client.ClientService;
import io.ensure.deepsea.common.service.MongoRedisRepositoryWrapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.RedisOptions;

public class MongoClientServiceImpl extends MongoRedisRepositoryWrapper implements ClientService {

	private static final String CLIENT_TYPE = "client";

	public MongoClientServiceImpl(Vertx vertx, JsonObject config, RedisOptions rOptions) {
		super(vertx, config, rOptions, CLIENT_TYPE);
	}

	@Override
	public ClientService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
		// Not required
		return null;
	}

	@Override
	public ClientService addClient(Client client, Handler<AsyncResult<Client>> resultHandler) {
		this.upsertWithCache(client.toJson(), CLIENT_TYPE)
			.map(option -> option.map(Client::new).orElse(null))
			.setHandler(resultHandler);
		return this;
	}

	@Override
	public ClientService retrieveClient(String id, Handler<AsyncResult<Client>> resultHandler) {
		this.retrieveDocumentWithCache(CLIENT_TYPE, id)
			.map(option -> option.map(Client::new).orElse(null))
			.setHandler(resultHandler);
		return this;
	}

	@Override
	public ClientService retrieveClients(Handler<AsyncResult<List<Client>>> resultHandler) {
		this.selectDocuments(CLIENT_TYPE, new JsonObject())
			.map(rawList -> rawList.stream().map(Client::new).collect(Collectors.toList()))
			.setHandler(resultHandler);
		
		
		return this;
	}

	@Override
	public ClientService removeClient(Client client, Handler<AsyncResult<Void>> resultHandler) {
		this.removeWithCache(CLIENT_TYPE, client.getClientId()).setHandler(resultHandler);
		return this;
	}

}

