package io.ensure.deepsea.common.service;

import java.util.Optional;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.RedisOptions;

public class MongoRedisRepositoryWrapper extends MongoRepositoryWrapper {

	private final String typeName;
	private final DeepseaRedis dRedis;
	private final String keyName;

	protected MongoRedisRepositoryWrapper(Vertx vertx, JsonObject config, RedisOptions options, String typeName) {
		super(vertx, config);
		this.dRedis = new DeepseaRedis(vertx, options);
		this.typeName = typeName;
		this.keyName = this.typeName + "Id";
	}
	
	protected Future<Optional<JsonObject>> upsertWithCache(JsonObject jsonObject, String collection) {
		Future<Optional<JsonObject>> future = Future.future();
		this.upsertSingle(keyFix(jsonObject), collection, res -> {
			if (res.succeeded()) {
				dRedis.setCache(keyName, keyFix(jsonObject)).setHandler(future);
			} else {
				future.fail(res.cause());
			}
		});
		return future;

	}


	protected Future<Optional<JsonObject>> upsertWithPublish(JsonObject jsonObject, String collection) {
		Future<Optional<JsonObject>> future = Future.future();
		this.upsertSingle(keyFix(jsonObject), collection, res -> {
			if (res.succeeded()) {
				jsonObject.put(keyName, typeName + "-" + res.result());
				dRedis.publish(typeName, jsonObject)
						.setHandler(publish ->
								future.complete(Optional.of(jsonObject)));
			} else {
				future.fail(res.cause());
			}
		});
		return future;

	}

	protected Future<Optional<JsonObject>> retrieveDocumentWithCache(String collection, String key) {
		Future<Optional<JsonObject>> future = Future.future();
		dRedis.getCache(key).setHandler(get -> {
			if (get.succeeded()) {
				if (get.result().isPresent()) {
					future.complete(get.result());
				} else {
					retrieveAndAdd(collection, key).setHandler(future);
				}
			} else {
				future.fail(get.cause());
			}
		});
		return future;
	}
	
	protected Future<Optional<JsonObject>> retrieveDocumentWithCache(String collection, JsonObject query) {
		Future<Optional<JsonObject>> future = Future.future();
		if (query.containsKey(keyName)) {
			dRedis.getCache(query.getString(keyName)).setHandler(get -> {
				if (get.succeeded()) {
					if (get.result().isPresent()) {
						future.complete(get.result());
					} else {
						retrieveAndAdd(collection, query).setHandler(future);
					}
				} else {
					future.fail(get.cause());
				}
			});
		} else {
			retrieveAndAdd(collection, query).setHandler(future);
		}
		return future;
	}

	private Future<Optional<JsonObject>> retrieveAndAdd(String collection, String id) {
		Future<Optional<JsonObject>> future = Future.future();
		this.retrieveDocument(collection, id).setHandler(res -> {
			if (res.succeeded()) {
				if (res.result().isPresent()) {
					dRedis.setCache(keyName, keyFix(res.result().get())).setHandler(future);
				} else {
					future.complete(Optional.empty());
				}
			} else {
				future.fail(res.cause());
			}
		});
		return future;
	}
	
	private Future<Optional<JsonObject>> retrieveAndAdd(String collection, JsonObject query) {
		Future<Optional<JsonObject>> future = Future.future();
		this.selectDocuments(collection, query).setHandler(res -> {
			if (res.succeeded()) {
				if (!res.result().isEmpty()) {
					dRedis.setCache(keyName, keyFix(res.result().get(0))).setHandler(future);
				} else {
					future.complete(Optional.empty());
				}
			} else {
				future.fail(res.cause());
			}
		});
		return future;
	}
	
	protected Future<Void> removeWithCache(String collection, String id) {
		Future<Void> future = Future.future();
		dRedis.delCache(id).setHandler(del -> {
			if (del.succeeded()) {
				this.removeDocument(collection, id.substring(collection.length())).setHandler(res -> {
					if (res.succeeded()) {
						future.complete();
					} else {
						future.fail(res.cause());
					}
				});
			} else {
				future.fail(del.cause());
			}
		});
		return future;
	}
	
	private JsonObject keyFix(JsonObject jsonObject) {
		if (jsonObject.containsKey(keyName)) {
			String key = jsonObject.getString(keyName);
			key = key.substring(typeName.length() + 1);
			jsonObject.remove(keyName);
			jsonObject.put("_id", key);
		} else if (jsonObject.containsKey("_id")) {
			String key = jsonObject.getString("_id");
			jsonObject.remove("_id");
			jsonObject.put(keyName, typeName + "-" + key);
		
		}
		return jsonObject;
	}
	
}
