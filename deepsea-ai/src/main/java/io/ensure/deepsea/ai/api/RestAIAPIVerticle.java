package io.ensure.deepsea.ai.api;

import io.ensure.deepsea.ai.AIService;
import io.ensure.deepsea.common.RestAPIVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class RestAIAPIVerticle extends RestAPIVerticle {

    public static final String SERVICE_NAME = "ai-rest-api";

    public RestAIAPIVerticle(AIService aiService) {
        super();
    }

    @Override
    public void start(Future<Void> future) throws Exception {
        super.start();
        final Router router = Router.router(vertx);
        // body handler
        router.route().handler(BodyHandler.create());
        // API route handler
        addHealthHandler(router, future);

        startRestService(router, future, SERVICE_NAME, "ai", "deepsea", "deepsea-ai");
    }



}
