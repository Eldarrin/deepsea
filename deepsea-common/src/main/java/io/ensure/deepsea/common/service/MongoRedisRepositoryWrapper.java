package io.ensure.deepsea.common.service;

import java.util.Optional;

import io.ensure.deepsea.common.helper.RedisHelper;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

public class MongoRedisRepositoryWrapper extends MongoRepositoryWrapper {

	protected String typeName;
	private RedisClient redis;
	private String keyName;
	
	private Logger log = LoggerFactory.getLogger(getClass());

	public MongoRedisRepositoryWrapper(Vertx vertx, JsonObject config, RedisOptions rOptions, String typeName) {
		super(vertx, config);
		this.redis = RedisClient.create(vertx, rOptions);
		this.typeName = typeName;
		this.keyName = this.typeName + "Id";
	}
	
	protected Future<Optional<JsonObject>> upsertWithCache(JsonObject jsonObject, String collection) {
		Future<Optional<JsonObject>> future = Future.future();
		this.upsertSingle(keyFix(jsonObject), collection, res -> {
			if (res.succeeded()) {
				RedisHelper.setCache(redis, keyName, keyFix(jsonObject)).setHandler(future.completer());
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
				RedisHelper.publishRedis(redis, typeName, jsonObject)
						.setHandler(future.completer());
			} else {
				future.fail(res.cause());
			}
		});
		return future;

	}

	protected Future<Optional<JsonObject>> retrieveDocumentWithCache(String collection, String key) {
		Future<Optional<JsonObject>> future = Future.future();
		redis.get(key, res -> {
			if (res.succeeded()) {
				if (res.result() != null) {
					future.complete(Optional.of(new JsonObject(res.result())));
				} else {
					retrieveAndAdd(collection, key).setHandler(future);
				}
			} else {
				future.fail(res.cause());
			}
		});
		return future;
	}
	
	protected Future<Optional<JsonObject>> retrieveDocumentWithCache(String collection, JsonObject query) {
		Future<Optional<JsonObject>> future = Future.future();
		if (query.containsKey(keyName)) {
			redis.get(query.getString(keyName), res -> {
				if (res.succeeded()) {
					if (res.result() != null) {
						future.complete(Optional.of(new JsonObject(res.result())));
					} else {
						retrieveAndAdd(collection, query).setHandler(future);
					}
				} else {
					future.fail(res.cause());
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
					RedisHelper.setCache(redis, keyName, keyFix(res.result().get())).setHandler(future.completer());
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
				if (res.result().size() > 0) {
					RedisHelper.setCache(redis, keyName, keyFix(res.result().get(0))).setHandler(future.completer());
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
		redis.del(id, ar -> {
			if (ar.succeeded()) {
				this.removeDocument(collection, id.substring(collection.length())).setHandler(res -> {
					if (res.succeeded()) {
						future.complete();
					} else {
						future.fail(res.cause());
					}
				});
			} else {
				future.fail(ar.cause());
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
