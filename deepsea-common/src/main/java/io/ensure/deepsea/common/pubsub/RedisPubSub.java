package io.ensure.deepsea.common.pubsub;

import java.util.List;

import io.ensure.deepsea.common.helper.RedisHelper;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;

public class RedisPubSub {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	private static final String REPLAY_SUFFIX = ".replay";
	
	RedisClient redis;
	
	private Vertx vertx;
	
	public RedisPubSub(Vertx vertx) {
		this.vertx = vertx;
	}
	
	public Future<JsonObject> listenForReplay(String channel) {
		Future<JsonObject> future = Future.future();
		vertx.eventBus().<JsonObject>consumer(channel + REPLAY_SUFFIX, msg -> {
			future.complete(msg.body());
		});
		return future;
	}
	
	public Future<Void> replayMessages(String channel, List<JsonObject> msgs) {
		Future<Void> future = Future.future();
		for (JsonObject msg : msgs) {
			redis.publish(channel, msg.toString(), ar -> {
    			if (!ar.succeeded()) {
    				log.error("Cannot publish to Redis, msg: " + msg.toString());
    			}
    		});
		}
		future.complete();
		return future;
	}

	public Future<Void> startRedisPubSub(String channel) {
		Future<Void> future = Future.future();
		
		RedisHelper.getRedisOptions(vertx).setHandler(res -> {
			if (res.succeeded()) {
				startEBCluster(res.result()).setHandler(redisResult -> {
					if (redisResult.succeeded()) {
						redis.publish(channel, new JsonObject().put("started", "true").encodePrettily(),
								ar -> {
									future.complete();
									log.info("Redis startup successful");
								});
					} else {
						future.fail(redisResult.cause());
						log.error("Failed to connect to Redis");
					}
				});
			} else {
				log.error("Failed to find Redis Config");
			}
		});

		return future;

	}

	private Future<Void> startEBCluster(RedisOptions redisOptions) {
		Future<Void> future = Future.future();

		redis = RedisClient.create(vertx, redisOptions);

		redis.ping(ar -> {
			if (ar.succeeded()) {
				log.info("redis.started.succeed");
				future.complete();
			} else {
				log.error("redis.started.failed");
				future.fail(ar.cause());
			}
		});

		return future;
	}
}