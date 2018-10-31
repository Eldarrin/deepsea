package io.ensure.deepsea.admin.enrolment;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@VertxGen
@ProxyGen
public interface EnrolmentService {
	
	String SERVICE_NAME = "enrolment-eb-service";

	String SERVICE_ADDRESS = "service.enrolment";
	
	@Fluent
	EnrolmentService initializePersistence(Handler<AsyncResult<Void>> resultHandler);
	
	@Fluent
	EnrolmentService addEnrolment(Enrolment enrolment, Handler<AsyncResult<Void>> resultHandler);

}