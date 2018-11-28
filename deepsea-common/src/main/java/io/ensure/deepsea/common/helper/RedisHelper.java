package io.ensure.deepsea.common.helper;

import java.util.Optional;

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

	public static Future<RedisOptions> getRedisOptions(Vertx vertx) {
		Future<RedisOptions> future = Future.future();
		try {
			RedisOptions redisConfig = new RedisOptions()
					.setHost(System.getenv("REDIS_HOST"))
					.setPort(Integer.parseInt(System.getenv("REDIS_PORT")))
					.setAuth(System.getenv("REDIS_AUTH"));
			future.complete(redisConfig);
		} catch (Exception e) {
			future.fail(e);
		}
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
}
