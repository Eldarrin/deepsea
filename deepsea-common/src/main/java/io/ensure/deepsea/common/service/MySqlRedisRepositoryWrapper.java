package io.ensure.deepsea.common.service;

import java.util.Optional;

import io.vertx.core.AsyncResult;
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
	
	// TODO: put typeName as contructer constant for better reusability
	private String typeName;
	private RedisClient redis;

	public MySqlRedisRepositoryWrapper(Vertx vertx, JsonObject config, RedisOptions options, String typeName) {
		super(vertx, config);
		this.redis = RedisClient.create(vertx, options);
		this.typeName = typeName;
	}

	protected Future<Optional<JsonObject>> executeWithCache(JsonArray params, String sql, JsonObject jsonObject) {
		Future<Optional<JsonObject>> future = Future.future();
		this.executeReturnKey(params, sql).setHandler(res -> {
			if (res.succeeded()) {
				String keyName = typeName + "Id";
				jsonObject.put(keyName, typeName + "-" + res.result().get());
				redis.set(typeName + "-" + res.result().get(), jsonObject.toString(), redRes -> {
					future.complete(Optional.of(jsonObject));
					log.info("Redis Setting Cache");
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
					log.info("Redis fetched cache");
				} else {
					log.error("Redis empty getting Cache");
					// retrieve from mysql as not in cache
					log.info(param);
					log.info(sql);
					
					
					this.retrieveOne(param, sql)
					
					.setHandler(this::resSQLHandler)
					
					;
					
				}
			} else {
				log.error("Redis failing getting Cache");
				future.fail(res.cause());
			}
		});
		return future;
	
	}
	
	private Future<Optional<JsonObject>> resSQLHandler(AsyncResult<Optional<JsonObject>> asyncHandler) {
		Future<Optional<JsonObject>> future = Future.future();
		if (asyncHandler.succeeded()) {
			log.info("got from mysql");
			if (asyncHandler.result().isPresent()) {
				JsonObject json = asyncHandler.result().get();
				log.info("got from mysql, adding to cache");
				log.info(typeName + "Id");
				log.info(json.toString());
				String key = json.getInteger(typeName + "Id").toString();
				key = typeName + "-" + key;
				log.info(key);
				json.remove(typeName + "Id");
				json.put(typeName + "Id", key);
				// add retrieved sql to cache
				
				redis.set(key, 
						json.toString(), resRedis -> {
					// TODO: should this fail the procedure
							if (resRedis.succeeded()) {
								log.info("added to cache");
								log.info(json.encodePrettily());
								future.complete(Optional.of(json));
							} else {
								log.error("failed to add to cache");
								future.fail(resRedis.cause());
							}
				});
				
			} else {
				log.info("no data found in mysql");
				future.complete(Optional.empty());
			}
		} else {
			log.error("failed to get from sql");
			future.fail(asyncHandler.cause());
		}
		return future;
	}
	
}
