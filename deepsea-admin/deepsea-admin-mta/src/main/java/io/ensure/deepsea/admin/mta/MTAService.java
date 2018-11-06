package io.ensure.deepsea.admin.mta;

import java.util.List;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@VertxGen
@ProxyGen
public interface MTAService {
	
	String SERVICE_NAME = "mta-eb-service";

	String SERVICE_ADDRESS = "service.mta";

	@Fluent
	MTAService initializePersistence(Handler<AsyncResult<Void>> resultHandler);
	
	@Fluent
	MTAService addMTA(MidTermAdjustment mta, Handler<AsyncResult<String>> resultHandler);
	
	@Fluent
	MTAService replayMTAs(Integer lastId, Handler<AsyncResult<List<MidTermAdjustment>>> resultHandler);

}
