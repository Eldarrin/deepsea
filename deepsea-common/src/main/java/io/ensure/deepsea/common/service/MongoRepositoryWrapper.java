package io.ensure.deepsea.common.service;

import java.util.List;
import java.util.Optional;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.mongo.MongoClient;

public class MongoRepositoryWrapper {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	protected final MongoClient client;

	public MongoRepositoryWrapper(Vertx vertx, JsonObject config) {
		this.client = MongoClient.createShared(vertx, config);
	}
	
	protected void upsertSingle(JsonObject document, String collection, Handler<AsyncResult<String>> resultHandler) {
		client.save(collection, document, resultHandler);
	}
	
	protected Future<List<JsonObject>> selectDocuments(String collection, JsonObject query) {
		Future<List<JsonObject>> future = Future.future();
		//client.find(collection, query, future.completer());
		client.find(collection, query, res -> {
			if (res.succeeded()) {
				List<JsonObject> clients = res.result();
				for (JsonObject j : clients) {
					log.info(j.encodePrettily());
				}
				future.complete(res.result());
			} else {
				future.fail(res.cause());
			}
		});
		
		
		return future;
	}
	
	protected Future<Optional<JsonObject>> retrieveDocument(String collection, String id) {
		Future<Optional<JsonObject>> future = Future.future();
		client.findOne(collection, new JsonObject().put("_id", id), null, res -> {
			if (res.succeeded()) {
				if (res.result() != null) {
					future.complete(Optional.of(res.result()));
				} else {
					future.complete(Optional.empty());
				}
			} else {
				future.fail(res.cause());
			}
		});
		return future;
	}
}
