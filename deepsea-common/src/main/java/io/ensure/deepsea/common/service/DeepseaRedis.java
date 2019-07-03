package io.ensure.deepsea.common.service;

import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.SocketAddress;
import io.vertx.redis.client.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DeepseaRedis {

    private Redis client;
    private RedisOptions options;
//    private RedisAPI redis;
    private Vertx vertx;
    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final String REPLAY_SUFFIX = ".replay";
    private static final int MAX_RECONNECT_RETRIES = 16;

    public DeepseaRedis(Vertx vertx, RedisOptions options) {
        this(vertx, options, res -> {

        });
    }

    public DeepseaRedis(Vertx vertx, RedisOptions options, Handler<AsyncResult<Void>> resultHandler) {
        this.vertx = vertx;
        this.options = options;
        Future<Void> future = Future.future();
        createRedisClient(vertx, ar -> {
            if (ar.succeeded()) {
                resultHandler.handle(Future.succeededFuture());
            } else {
                resultHandler.handle(Future.failedFuture(ar.cause()));
                log.error("Failed to connect to Redis");
            }
        });
    }

    public Future<JsonObject> listenForReplay(Vertx vertx, String channel) {
        Future<JsonObject> future = Future.future();
        vertx.eventBus().<JsonObject>consumer(channel + REPLAY_SUFFIX, msg ->
                future.complete(msg.body()));
        return future;
    }

    public Future<Void> replayMessages(String channel, List<JsonObject> msgs) {
        Future<Void> future = Future.future();
        for (JsonObject msg : msgs) {
            client.send(Request.cmd(Command.PUBLISH).arg(channel).arg(msg.toString()), res -> {
                if (!res.succeeded()) {
                    log.error("Cannot publish to Redis, msg: " + msg.toString());
                }
            });
        }
        future.complete();
        return future;
    }

    public Future<Optional<JsonObject>> setCache(String keyName, JsonObject jsonObject) {
        log.info(jsonObject);
        Future<Optional<JsonObject>> future = Future.future();
        RedisAPI redis = RedisAPI.api(client);
        redis.set(Arrays.asList(jsonObject.getString(keyName), jsonObject.toString()), set -> {
            if (set.succeeded()) {
                future.complete(Optional.of(jsonObject));
            } else {
                future.fail(set.cause());
            }
        });
        return future;
    }

    public Future<Optional<JsonObject>> getCache(String keyName) {
        Future<Optional<JsonObject>> future = Future.future();
        RedisAPI redis = RedisAPI.api(client);
        redis.get(keyName, get -> {
            if (get.succeeded()) {
                future.complete(Optional.of(new JsonObject(get.result().toString())));
            } else {
                future.fail(get.cause());
            }
        });
        return future;
    }

    public Future<Void> delCache(String keyName) {
        Future<Void> future = Future.future();
        RedisAPI redis = RedisAPI.api(client);
        redis.del(Arrays.asList(keyName), del -> {
            if (del.succeeded()) {
                future.complete();
            } else {
                future.fail(del.cause());
            }
        });
        return future;
    }

    public Future<Void> publish(String channel, JsonObject message) {
        Future<Void> future = Future.future();
        if (client == null) {
            future.fail(new NullPointerException("Redis Not Instantiated, start PUBSUB"));
        } else {
            client.send(Request.cmd(Command.PUBLISH).arg(channel).arg(message.toString()), res -> {
                if (res.succeeded()) {
                    future.complete();
                } else {
                    future.fail(res.cause());
                    log.error("Cannot publish to Redis, msg: " + message.toString());
                }
            });
        }
        return future;
    }

    public void subscribe(String channel) {



        RedisAPI redis = RedisAPI.api(client);
        redis.subscribe(Arrays.asList(channel), subscribe -> {
            if (!subscribe.succeeded()) {
                log.error("Cannot subscribe to channel", subscribe.cause());
            }
        });
    }

    public static Future<RedisOptions> getRedisOptions(Vertx vertx, String configMap) {
        Future<RedisOptions> future = Future.future();
        ConfigRetriever retriever = ConfigRetriever.create(vertx,
                new ConfigRetrieverHelper().getOptions("deepsea", configMap));
        retriever.getConfig(res -> {
            if (res.succeeded()) {
                RedisOptions redisConfig = new RedisOptions()
                        .addEndpoint(SocketAddress.inetSocketAddress(res.result().getInteger("redis.port"),
                                res.result().getString("redis.host")))
                        .setPassword(System.getenv("REDIS_AUTH"))
                        .setType(RedisClientType.STANDALONE);
                future.complete(redisConfig);
            } else {
                future.fail(res.cause());
            }
        });
        return future;
    }

    public Future<Void> startRedisPubSub(Vertx vertx, String channel, String configMap) {
        Future<Void> future = Future.future();

        getRedisOptions(vertx, configMap).setHandler(res -> {
            if (res.succeeded()) {
                options = res.result();
                createRedisClient(vertx, ar -> {
                    if (ar.succeeded()) {
                        client.send(Request.cmd(Command.PUBLISH).arg(channel).
                                arg(new JsonObject().put("started", "true").encodePrettily()), resSend -> {
                            if (resSend.succeeded()) {
                                future.complete();
                                log.info("Redis PubSub startup successful");
                            } else {
                                future.fail(res.cause());
                            }
                        });
                    } else {
                        future.fail(ar.cause());
                        log.error("Failed to connect to Redis", ar.cause());
                    }
                });
            } else {
                log.error("Failed to find Redis Config");
            }
        });

        return future;

    }

    /**
     * Will create a redis client and setup a reconnect handler when there is
     * an exception in the connection.
     */
    private void createRedisClient(Vertx vertx, Handler<AsyncResult<Redis>> handler) {
        Redis.createClient(vertx, options)
                .connect(onConnect -> {
                    if (onConnect.succeeded()) {
                        client = onConnect.result();
                        // make sure the client is reconnected on error
                        client.exceptionHandler(e -> {
                            // attempt to reconnect
                            attemptReconnect(vertx, 0);
                        });
                        log.info("redis.started.succeed");
                    }
                    // allow further processing
                    handler.handle(onConnect);
                });
    }

    /**
     * Attempt to reconnect up to MAX_RECONNECT_RETRIES
     */
    private void attemptReconnect(Vertx vertx, int retry) {
        if (retry > MAX_RECONNECT_RETRIES) {
            // we should stop now, as there's nothing we can do.
        } else {
            // retry with backoff up to 1280ms
            long backoff = (long) (Math.pow(2, MAX_RECONNECT_RETRIES - Math.max(MAX_RECONNECT_RETRIES - retry, 9)) * 10);

            vertx.setTimer(backoff, timer -> createRedisClient(vertx, onReconnect -> {
                if (onReconnect.failed()) {
                    attemptReconnect(vertx, retry + 1);
                }
            }));
        }
    }


}
