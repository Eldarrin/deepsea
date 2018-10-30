package io.ensure.deepsea.admin.enrolment;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@VertxGen
@ProxyGen
public interface EnrolmentService {
	
	@Fluent
	EnrolmentService initializePersistence(Handler<AsyncResult<Void>> resultHandler);

}
