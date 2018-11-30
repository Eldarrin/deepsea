package io.ensure.deepsea.common.service;

import java.util.Optional;

import io.ensure.deepsea.common.helper.RedisHelper;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

public class MongoRedisRepositoryWrapper extends MongoRepositoryWrapper {

	protected String typeName;
	private RedisClient redis;

	public MongoRedisRepositoryWrapper(Vertx vertx, JsonObject config, RedisOptions rOptions) {
		super(vertx, config);
		this.redis = RedisClient.create(vertx, rOptions);
	}
	
	protected Future<Optional<JsonObject>> upsertWithCache(JsonObject jsonObject, String collection) {
		Future<Optional<JsonObject>> future = Future.future();
		String keyName = typeName + "Id";
		if (jsonObject.containsKey(keyName)) {
			String id = jsonObject.getString(keyName);
			jsonObject.remove(keyName);
			if (id.startsWith(keyName)) {
				jsonObject.put("_id", id.substring(typeName.length()));
			} else {
				jsonObject.put("_id", id);
			}
		}
		this.upsertSingle(jsonObject, collection, res -> {
			if (res.succeeded()) {
				jsonObject.remove("_id");
				jsonObject.put(keyName, typeName + "-" + res.result());
				RedisHelper.setCache(redis, keyName, jsonObject).setHandler(future.completer());
			} else {
				future.fail(res.cause());
			}
		});
		return future;

	}


	protected Future<Optional<JsonObject>> upsertWithPublish(JsonObject jsonObject, String collection) {
		Future<Optional<JsonObject>> future = Future.future();
		String keyName = typeName + "Id";
		if (jsonObject.containsKey(keyName)) {
			String id = jsonObject.getString(keyName);
			jsonObject.remove(keyName);
			if (id.startsWith(keyName)) {
				jsonObject.put("_id", id.substring(typeName.length()));
			} else {
				jsonObject.put("_id", id);
			}
		}
		this.upsertSingle(jsonObject, collection, res -> {
			if (res.succeeded()) {
				jsonObject.remove("_id");
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

	private Future<Optional<JsonObject>> retrieveAndAdd(String collection, String id) {
		Future<Optional<JsonObject>> future = Future.future();
		this.retrieveDocument(collection, id).setHandler(res -> {
			if (res.succeeded()) {
				if (res.result().isPresent()) {
					JsonObject json = res.result().get();
					String key = json.getString("_id");
					key = typeName + "-" + key;
					json.remove("_id");
					json.put(typeName + "Id", key);
					RedisHelper.setCache(redis, typeName + "Id", json).setHandler(future.completer());
				} else {
					future.complete(Optional.empty());
				}
			} else {
				future.fail(res.cause());
			}
		});
		return future;
	}
	
	private JsonObject keyFix(JsonObject jsonObject) {
		String keyName = typeName + "Id";
		if (jsonObject.containsKey(keyName)) {
			String key = jsonObject.getString(keyName);
			key = key.substring(typeName.length());
			jsonObject.remove(keyName);
			jsonObject.put("_id", key);
		}
		if (jsonObject.containsKey("_id")) {
			String key = jsonObject.getString("_id");
			jsonObject.remove("_id");
			jsonObject.put(keyName, typeName + "-" + key);
		
		}
		return jsonObject;
	}
	
}
