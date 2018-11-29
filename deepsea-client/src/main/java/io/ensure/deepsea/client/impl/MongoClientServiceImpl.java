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
import io.vertx.redis.RedisOptions;

public class MongoClientServiceImpl extends MongoRedisRepositoryWrapper implements ClientService {

	public MongoClientServiceImpl(Vertx vertx, JsonObject config, RedisOptions rOptions) {
		super(vertx, config, rOptions);
		this.typeName = "client";
	}

	@Override
	public ClientService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ClientService addClient(Client client, Handler<AsyncResult<Client>> resultHandler) {
		this.upsertWithCache(client.toJson(), "client")
			.map(option -> option.map(Client::new).orElse(null))
			.setHandler(resultHandler);
		return this;
	}

	@Override
	public ClientService retrieveClient(String id, Handler<AsyncResult<Client>> resultHandler) {
		this.retrieveDocumentWithCache("client", id)
			.map(option -> option.map(Client::new).orElse(null))
			.setHandler(resultHandler);
		return this;
	}

	@Override
	public ClientService retrieveClients(Handler<AsyncResult<List<Client>>> resultHandler) {
		this.selectDocuments("client", new JsonObject())
			.map(rawList -> rawList.stream().map(Client::new).collect(Collectors.toList()))
			.setHandler(resultHandler);
		
		
		return this;
	}

	@Override
	public ClientService removeClient(Client client, Handler<AsyncResult<Void>> resultHandler) {
		// TODO Auto-generated method stub
		return null;
	}

}

