package io.ensure.deepsea.ai;

import io.ensure.deepsea.ai.api.RestAIAPIVerticle;
import io.ensure.deepsea.ai.impl.GraknAIServiceImpl;
import io.ensure.deepsea.common.BaseMicroserviceVerticle;
import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
import io.ensure.deepsea.common.helper.RedisHelper;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import io.vertx.serviceproxy.ServiceBinder;

public class AIVerticle extends BaseMicroserviceVerticle {

    private static final String SERVICE_NAME = "ai-eb-service";
    private static final String SERVICE_ADDRESS = "service.ai";

    private static final String REDIS_JSON_VALUE = "value";
    private static final String REPLAY = ".replay";
    private static final String ENROLMENT_CHANNEL = "enrolment";
    private static final String REDIS_CHANNEL = "io.vertx.redis.";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private AIService aiService;

    @Override
    public void start(Future<Void> future) {
        super.start();

        ConfigRetriever retriever = ConfigRetriever
                .create(vertx, new ConfigRetrieverHelper()
                        .getOptions("deepsea", "deepsea-ai"));
        retriever.getConfig(res -> {
            if (res.succeeded()) {
                // create the service instance
                JsonObject myGraknConfig = new JsonObject()
                        .put("host", res.result().getString("grakn.host"))
                        .put("keyspace", res.result().getString("grakn.keyspace"));

                RedisHelper.getRedisOptions(vertx, "deepsea-ai").setHandler(resRedis -> {
                    if (resRedis.succeeded()) {
                        aiService = new GraknAIServiceImpl(vertx, myGraknConfig);
                        // Register the handler
                        new ServiceBinder(vertx)
                                .setAddress(SERVICE_ADDRESS)
                                .register(AIService.class, aiService);

                        initAIServer(aiService);

                        // publish the service and REST endpoint in the discovery infrastructure
                        publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, AIService.class)
                                .compose(servicePublished -> deployRestVerticle()).setHandler(future);


                        setupConsumers(resRedis.result());
                    } else {
                        log.error("Cannot find Redis Config");
                    }
                });



            } else {
                log.error("Unable to find config map for deepsea-ai Grakn");
            }
        });

    }

    private Future<Void> initAIServer(AIService service) {
        Future<Void> initFuture = Future.future();
        service.initializePersistence(initFuture);
        return initFuture.map(v -> null);
    }

    private void setupConsumers(RedisOptions redisOptions) {
        vertx.eventBus().<JsonObject>consumer(REDIS_CHANNEL + ENROLMENT_CHANNEL, received -> {
            String message = received.body().getJsonObject(REDIS_JSON_VALUE).getString("message");
            log.trace(message);
            aiService.addEnrolment(new JsonObject(message), res -> {
                // TODO: at failurehandler
            });
        });

        RedisClient redis = RedisClient.create(vertx, redisOptions);

        redis.subscribe(ENROLMENT_CHANNEL, ar -> {
            if (ar.succeeded()) {
                // TODO: addreplayer

            } else {
                log.error(ar.result());
            }
        });

    }

    private Future<Void> deployRestVerticle() {
        Future<String> future = Future.future();
        vertx.deployVerticle(new RestAIAPIVerticle(aiService),
                new DeploymentOptions().setConfig(config()), future);
        return future.map(r -> null);
    }
}
