package io.ensure.deepsea.common.service;

import java.util.Optional;

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
	
	protected Future<Optional<JsonObject>> upsertWithPublish(JsonObject jsonObject, 
			String collection) {
		Future<Optional<JsonObject>> future = Future.future();
		String keyName = typeName + "Id";
		if (jsonObject.containsKey(keyName)) {
			String id = jsonObject.getString(keyName);
			jsonObject.remove(keyName);
			jsonObject.put("_id", id);
		}
		this.upsertSingle(jsonObject, collection, res -> {
			if (res.succeeded()) {
				jsonObject.remove("_id");
				jsonObject.put(keyName, typeName + "-" + res.result());
				redis.publish(typeName, jsonObject.toString(), ar -> {
	    			if (ar.succeeded()) {
	    				future.complete(Optional.of(jsonObject));
	    			} else {
	    				future.fail(ar.cause());
	    			}
	    		});
			} else {
				future.fail(res.cause());
			}
		});
		return future;
	}

}
