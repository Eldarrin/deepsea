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

	public static Future<RedisOptions> getRedisOptions(Vertx vertx) {
		Future<RedisOptions> future = Future.future();
		ConfigRetriever redisRetriever = ConfigRetriever.create(vertx,
				new ConfigRetrieverHelper().getOptions("deepsea", "deepsea-redis"));

		redisRetriever.getConfig(res -> {
			if (res.succeeded()) {
				RedisOptions redisConfig = new RedisOptions()
						.setHost(res.result().getString("redis.host"))
						.setPort(res.result().getInteger("redis.port"))
						.setAuth(res.result().getString("redis.auth"));
				future.complete(redisConfig);
			} else {
				future.fail(res.cause());
			}
		});
		return future;
	}
	
	public static Future<Optional<JsonObject>> publishRedis(RedisClient redis, String channel, JsonObject jsonObject) {
		Future<Optional<JsonObject>> future = Future.future();
		log.info("Publishing in channel: " + channel + ":" + jsonObject.toString());
		redis.publish(channel, jsonObject.toString(), ar -> {
			if (ar.succeeded()) {
				log.info("publish succeeded");
				log.info(jsonObject.encodePrettily());
				future.complete(Optional.of(jsonObject));
			} else {
				log.error(ar.cause());
				future.fail(ar.cause());
			}
		});
		return future;
	
	}
}
