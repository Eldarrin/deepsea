package io.ensure.deepsea.common.service;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.SocketAddress;
import io.vertx.redis.client.*;
import java.util.List;

public class RedisWrapper {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Vertx vertx;
    private RedisOptions options;

    public RedisWrapper(Vertx vertx) {
        this.vertx = vertx;
    }

    public RedisWrapper(Vertx vertx, RedisOptions options) {
        this.vertx = vertx;
        this.options = options;
    }

    private RedisOptions getRedisOptions(String host, Integer port) {
        RedisOptions options = new RedisOptions()
                .addEndpoint(SocketAddress.inetSocketAddress(port, host))
                .setType(RedisClientType.STANDALONE);
        return options;
    }

    public void connect(RedisOptions options, Handler<AsyncResult<Redis>> resultHandler) {
        Redis.createClient(vertx, options)
                .connect(onConnect -> {
                    if (onConnect.succeeded()) {
                        resultHandler.handle(Future.succeededFuture(onConnect.result()));
                    } else {
                        log.error("Failed to connect to Redis");
                        resultHandler.handle(Future.failedFuture(onConnect.cause()));
                    }
                });
    }

    public void connect(String host, Integer port, Handler<AsyncResult<Redis>> resultHandler) {
        connect(getRedisOptions(host, port), resultHandler);
    }

    public void connect(Handler<AsyncResult<Redis>> resultHandler) {
        connect(options, resultHandler);
    }

    public void subscribe(Redis redis, List<String> channels) {
        RedisAPI api = RedisAPI.api(redis);
        api.subscribe(channels, subscribe -> {
            if (subscribe.succeeded()) {
                log.info(subscribe.result().get(0));
            } else {
                log.error("Cannot subscribe to channel", subscribe.cause());
            }
        });
    }

    void subscribe() {
        connect("localhost", 6379, res -> {
            if (res.succeeded()) {
                res.result().handler(message -> {
                    // do whatever you need to do with your message
                });
            }
        });
    }

}
