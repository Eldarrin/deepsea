package io.ensure.deepsea.common.helper;

import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.redis.RedisOptions;

public class RedisHelper {
	
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
}
