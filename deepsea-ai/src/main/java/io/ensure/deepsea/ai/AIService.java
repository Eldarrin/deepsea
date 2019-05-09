package io.ensure.deepsea.ai;

import io.ensure.deepsea.admin.enrolment.models.Enrolment;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@VertxGen
@ProxyGen
public interface AIService {

    @Fluent
    AIService initializePersistence(Handler<AsyncResult<Void>> resultHandler);

    @Fluent
    AIService addEnrolment(Enrolment enrolment, Handler<AsyncResult<Enrolment>> resultHandler);


}
