package io.ensure.deepsea.common.service;

import java.util.Optional;

import io.ensure.deepsea.common.helper.RedisHelper;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

public class MySqlRedisRepositoryWrapper extends MySqlRepositoryWrapper {
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	protected String typeName;
	private RedisClient redis;

	public MySqlRedisRepositoryWrapper(Vertx vertx, JsonObject config, RedisOptions options) {
		super(vertx, config);
		this.redis = RedisClient.create(vertx, options);
	}
	
	protected Future<Optional<JsonObject>> executeWithPublish(JsonArray params, String sql, JsonObject jsonObject) {
		Future<Optional<JsonObject>> future = Future.future();
		this.executeReturnKey(params, sql).setHandler(res -> {
			if (res.succeeded()) {
				String keyName = typeName + "Id";
				jsonObject.put(keyName, typeName + "-" + res.result().get().toString());
				RedisHelper.publishRedis(redis, typeName, jsonObject)
				.setHandler(future.completer());
			} else {
				future.fail(res.cause());
			}
		});
		return future;
	}

	protected Future<Optional<JsonObject>> executeWithCache(JsonArray params, String sql, JsonObject jsonObject) {
		Future<Optional<JsonObject>> future = Future.future();
		this.executeReturnKey(params, sql).setHandler(res -> {
			if (res.succeeded()) {
				String keyName = typeName + "Id";
				jsonObject.put(keyName, typeName + "-" + res.result().get());
				redis.set(typeName + "-" + res.result().get(), jsonObject.toString(), redRes -> {
					future.complete(Optional.of(jsonObject));
				});
			} else {
				future.fail(res.cause());
				log.error("Redis Failing Setting Cache");
			}
		});
		return future;
	}
	
	protected <K> Future<Optional<JsonObject>> retrieveOneWithCache(K param, String sql, String key) {
		Future<Optional<JsonObject>> future = Future.future();
		redis.get(key, res -> {
			if (res.succeeded()) {
				if (res.result() != null) {
					future.complete(Optional.of(new JsonObject(res.result())));
				} else {
					retrieveAndAdd(param, sql).setHandler(future);
				}
			} else {
				future.fail(res.cause());
			}
		});
		return future;
	
	}
	
	private <K> Future<Optional<JsonObject>> retrieveAndAdd(K param, String sql) {
		Future<Optional<JsonObject>> future = Future.future();
		this.retrieveOne(param, sql).setHandler(res -> {
			if (res.succeeded()) {
				if (res.result().isPresent()) {
					JsonObject json = res.result().get();
					String key = json.getInteger(typeName + "Id").toString();
					key = typeName + "-" + key;
					json.remove(typeName + "Id");
					json.put(typeName + "Id", key);
					redis.set(key, 
							json.toString(), resRedis -> {
								if (resRedis.succeeded()) {
									future.complete(Optional.of(json));
								} else {
									future.fail(resRedis.cause());
								}
					});
					
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
