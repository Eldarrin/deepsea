package io.ensure.deepsea.common.service;

import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.SocketAddress;
import io.vertx.redis.client.RedisClientType;
import io.vertx.redis.client.RedisOptions;
import io.vertx.redis.client.Redis;

public class RedisWrapper {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Vertx vertx;

    public RedisWrapper(Vertx vertx) {
        this.vertx = vertx;
    }

    private RedisOptions getRedisOptions(String host, Integer port) {
        RedisOptions options = new RedisOptions()
                .addEndpoint(SocketAddress.inetSocketAddress(port, host))
                .setType(RedisClientType.STANDALONE);
        return options;
    }

    public void connect(String host, Integer port, Handler<AsyncResult<Redis>> resultHandler) {
        Redis.createClient(vertx, getRedisOptions(host, port))
                .connect(onConnect -> {
                    if (onConnect.succeeded()) {
                        resultHandler.handle(Future.succeededFuture(onConnect.result()));
                    } else {
                        resultHandler.handle(Future.failedFuture(onConnect.cause()));
                        log.error("Failed to connect to Redis");
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
