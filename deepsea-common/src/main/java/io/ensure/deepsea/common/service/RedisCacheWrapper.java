package io.ensure.deepsea.common.service;

import java.util.Optional;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

public class RedisCacheWrapper {
	
	protected final RedisClient client;

	public RedisCacheWrapper(Vertx vertx, RedisOptions config) {
		this.client = RedisClient.create(vertx, config);
	}
	
	public void setCache(String key, JsonObject jsonObject, Handler<AsyncResult<Void>> asyncResult) {
		client.set(key, jsonObject.toString(), asyncResult);
	}
	
	public Future<Optional<String>> retrieveOneFromCache(String key) {
		Future<Optional<String>> future = Future.future();
		client.get(key, res -> {
			if (res.succeeded()) {
				if (res.result() == null) {
					future.complete(Optional.empty());
				} else {
					future.complete(Optional.of(res.result()));
				}
			} else {
				future.fail(res.cause());
			}
		});
		return future;
	}

}
