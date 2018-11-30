package io.ensure.deepsea.common.helper;

import java.util.Optional;

import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

public class RedisHelper {
	
	private static Logger log = LoggerFactory.getLogger("io.ensure.deepsea.common.helper.RedisHelper.class");
	
	private RedisHelper() {
	    throw new IllegalStateException("Redis Utility class");
	  }

	public static Future<RedisOptions> getRedisOptions(Vertx vertx, String configMap) {
		Future<RedisOptions> future = Future.future();
		ConfigRetriever retriever = ConfigRetriever.create(vertx,
				new ConfigRetrieverHelper().getOptions("deepsea", configMap));
		retriever.getConfig(res -> {
			if (res.succeeded()) {
				RedisOptions redisConfig = new RedisOptions()
						.setHost(res.result().getString("redis.host"))
						.setPort(res.result().getInteger("redis.port"))
						.setAuth(System.getenv("REDIS_AUTH"));
				future.complete(redisConfig);
			} else {
				future.fail(res.cause());
			}
		});
		return future;
	}
	
	public static Future<Optional<JsonObject>> publishRedis(RedisClient redis, String channel, JsonObject jsonObject) {
		Future<Optional<JsonObject>> future = Future.future();
		redis.publish(channel, jsonObject.toString(), ar -> {
			if (ar.succeeded()) {
				future.complete(Optional.of(jsonObject));
			} else {
				log.error(ar.cause());
				future.fail(ar.cause());
			}
		});
		return future;
	
	}
	
	public static Future<Optional<JsonObject>> setCache(RedisClient redis, String keyName, JsonObject jsonObject) {
		Future<Optional<JsonObject>> future = Future.future();
		redis.set(jsonObject.getString(keyName), jsonObject.toString(), resRedis -> {
			if (resRedis.succeeded()) {
				future.complete(Optional.of(jsonObject));
			} else {
				future.fail(resRedis.cause());
			}
		});
		return future;
	}
	
}
