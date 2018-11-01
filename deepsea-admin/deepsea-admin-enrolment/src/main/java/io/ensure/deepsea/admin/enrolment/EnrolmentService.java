package io.ensure.deepsea.admin.enrolment;

import java.util.List;

import io.ensure.deepsea.admin.enrolment.models.Enrolment;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;

@VertxGen
@ProxyGen
public interface EnrolmentService {
	
	String SERVICE_NAME = "enrolment-eb-service";

	String SERVICE_ADDRESS = "service.enrolment";
	
	@Fluent
	EnrolmentService initializePersistence(Handler<AsyncResult<Void>> resultHandler);
	
	@Fluent
	EnrolmentService addEnrolment(Enrolment enrolment, Handler<AsyncResult<Integer>> resultHandler);
	
	@Fluent
	EnrolmentService replayEnrolments(JsonArray specificIds, Handler<AsyncResult<List<Enrolment>>> resultHandler);

}
