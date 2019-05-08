package io.ensure.deepsea.ai;

import io.ensure.deepsea.common.BaseMicroserviceVerticle;
import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
import io.ensure.deepsea.common.helper.RedisHelper;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;

public class AIVerticle extends BaseMicroserviceVerticle {

    @Override
    public void start(Future<Void> future) throws Exception {
        super.start();

        ConfigRetriever retriever = ConfigRetriever
                .create(vertx, new ConfigRetrieverHelper()
                        .getOptions("deepsea", "deepsea-ai"));
        retriever.getConfig(res -> {
            if (res.succeeded()) {
                // create the service instance
                JsonObject myGraknConfig = new JsonObject()
                        .put("host", res.result().getString("grakn.host"))
                        .put("keyspace", res.result().getInteger("grakn.keyspace"));

                RedisHelper.getRedisOptions(vertx, "deepsea-admin-mta").setHandler(resRedis -> {
                    if (resRedis.succeeded()) {
                        mtaService = new MongoMTAServiceImpl(vertx, myMongoConfig, resRedis.result());
                        // Register the handler
                        new ServiceBinder(vertx)
                                .setAddress(SERVICE_ADDRESS)
                                .register(MTAService.class, mtaService);

                        initMTADatabase(mtaService);

                        // publish the service and REST endpoint in the discovery infrastructure
                        publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, MTAService.class)
                                .compose(servicePublished -> deployRestVerticle()).setHandler(future.completer());
                    } else {
                        log.error("Cannot find Redis Config");
                    }
                });



            } else {
                log.error("Unable to find config map for deepsea-admin-mta Mongo");
            }
        });

    }
}
