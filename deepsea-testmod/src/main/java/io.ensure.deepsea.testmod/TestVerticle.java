package io.ensure.deepsea.testmod;

import io.ensure.deepsea.common.BaseMicroserviceVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.net.SocketAddress;
import io.vertx.redis.client.RedisOptions;
import io.vertx.redis.client.Redis;

public class TestVerticle extends BaseMicroserviceVerticle {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void start(Future<Void> future) {
        super.start();

        RedisOptions options = new RedisOptions()
                .addEndpoint(SocketAddress.inetSocketAddress(6379, "172.30.38.221"));

        Redis.createClient(vertx, options)
                .connect(onConnect -> {
                    if (onConnect.succeeded()) {
                        Redis client = onConnect.result();
                        log.info("connect successful");
                    } else {
                        log.error("Failed to Connect", onConnect.cause());
                    }
                });
    }
}
