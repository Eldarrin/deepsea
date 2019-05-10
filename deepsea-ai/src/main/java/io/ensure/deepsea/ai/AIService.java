package io.ensure.deepsea.ai;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

@VertxGen
@ProxyGen
public interface AIService {

    @Fluent
    AIService initializePersistence(Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    AIService addEnrolment(JsonObject enrolment, Handler<AsyncResult<JsonObject>> resultHandler);

    @Fluent
    AIService getEnrolmentInfo(Handler<AsyncResult<JsonObject>> resultHandler);


}
