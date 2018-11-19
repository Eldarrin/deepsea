package io.ensure.deepsea.common.service;

import java.util.Optional;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

public class MySqlRedisRepositoryWrapper extends MySqlRepositoryWrapper {
	
	private RedisClient redis;

	public MySqlRedisRepositoryWrapper(Vertx vertx, JsonObject config, RedisOptions options) {
		super(vertx, config);
		this.redis = RedisClient.create(vertx, options);
	}

	protected Future<Optional<JsonObject>> execute(JsonArray params, String sql, JsonObject jsonObject, String typeName) {
		Future<Optional<JsonObject>> future = Future.future();
		this.executeReturnKey(params, sql).setHandler(res -> {
			if (res.succeeded()) {
				String keyName = typeName + "-id";
				jsonObject.put(keyName, typeName + "-" + res.result().get());
				redis.set(keyName, jsonObject.toString(), redRes -> {
					future.complete(Optional.of(jsonObject));
				});
			} else {
				future.fail(res.cause());
			}
		});
		return future;
	}
	
	protected <K> Future<Optional<JsonObject>> retrieveOne(K param, String sql, String typeName, String key) {
		Future<Optional<JsonObject>> future = Future.future();
		redis.get(key, res -> {
			if (res.succeeded()) {
				if (res.result() != null) {
					future.complete(Optional.of(new JsonObject(res.result())));
				} else {
					// retrieve from mysql as not in cache
					this.retrieveOne(param, sql).setHandler(resSQL -> {
						if (resSQL.succeeded()) {
							if (resSQL.result().isPresent()) {
								// add retrieved sql to cache
								redis.set(key, resSQL.result().get().toString(), resRedis -> {
									// TODO: should this fail the procedure
								});
								future.complete(Optional.of(resSQL.result().get()));
							} else {
								future.complete(Optional.empty());
							}
						} else {
							future.fail(resSQL.cause());
						}
					});
				}
			} else {
				future.fail(res.cause());
			}
		});
		return future;
	
	}
	
}
