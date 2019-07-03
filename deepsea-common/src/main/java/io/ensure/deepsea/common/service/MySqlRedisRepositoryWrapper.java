package io.ensure.deepsea.common.service;

import java.util.Optional;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.client.RedisOptions;

public class MySqlRedisRepositoryWrapper extends MySqlRepositoryWrapper {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	//TODO: bad usage of protected, needs fixing
	protected String typeName;
	private final DeepseaRedis dRedis;

	public MySqlRedisRepositoryWrapper(Vertx vertx, JsonObject config, RedisOptions options) {
		super(vertx, config);
		dRedis = new DeepseaRedis(vertx, options);
	}
	
	protected Future<Optional<JsonObject>> executeWithPublish(JsonArray params, String sql, JsonObject jsonObject) {
		Future<Optional<JsonObject>> future = Future.future();
		this.executeReturnKey(params, sql).setHandler(res -> {
			if (res.succeeded()) {
				if (res.result().isPresent()) {
					String keyName = typeName + "Id";
					jsonObject.put(keyName, typeName + "-" + res.result().get().toString());
					dRedis.publish(typeName, jsonObject).setHandler(publish ->
							future.complete(Optional.of(jsonObject)));
				}
			} else {
				future.fail(res.cause());
			}
		});
		return future;
	}

	protected Future<Optional<JsonObject>> executeWithCache(JsonArray params, String sql, JsonObject jsonObject) {
		Future<Optional<JsonObject>> future = Future.future();
		this.executeReturnKey(params, sql).setHandler(exec -> {
			if (exec.succeeded()) {
				if (exec.result().isPresent()) {
					String keyName = typeName + "Id";
					jsonObject.put(keyName, typeName + "-" + exec.result().get());
					dRedis.setCache(typeName + "Id", jsonObject).setHandler(future);
				}
			} else {
				future.fail(exec.cause());
				log.error("SQL Failing Accessing Data");
			}
		});
		return future;
	}
	
	protected <K> Future<Optional<JsonObject>> retrieveOneWithCache(K param, String sql, String key) {
		Future<Optional<JsonObject>> future = Future.future();
		dRedis.getCache(key).setHandler(get -> {
			if (get.succeeded()) {
				if (get.result().isPresent()) {
					future.complete(get.result());
				} else {
					retrieveAndAdd(param, sql).setHandler(future);
				}
			} else {
				future.fail(get.cause());
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
					dRedis.setCache(typeName + "Id", json).setHandler(future);
				} else {
					future.complete(Optional.empty());
				}
			} else {
				future.fail(res.cause());
			}
		});
		return future;
	}
	
	protected <K> Future<Void> removeWithCache(K id, String redisKey, String sql, Handler<AsyncResult<Void>> resultHandler) {
		Future<Void> future = Future.future();
		dRedis.delCache(redisKey).setHandler(del -> {
			if (del.succeeded()) {
				this.removeOne(id, sql, resultHandler);
			} else {
				future.fail(del.cause());
			}
		});
		return future;
	}
	
}
