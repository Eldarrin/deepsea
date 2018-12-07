package io.ensure.deepsea.actuarial.bordereau;

import java.util.List;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

@VertxGen
@ProxyGen
public interface BordereauService {

	String SERVICE_NAME = "bordereau-eb-service";

	String SERVICE_ADDRESS = "service.bordereau";

	/**
	 * Initialize the persistence.
	 *
	 * @param resultHandler the result handler will be called as soon as the
	 *                      initialization has been accomplished. The async result
	 *                      indicates whether the operation was successful or not.
	 */
	@Fluent
	BordereauService initializePersistence(Handler<AsyncResult<Void>> resultHandler);

	@Fluent
	BordereauService addBordereauLine(BordereauLine bordereauLine, Handler<AsyncResult<BordereauLine>> resultHandler);

	@Fluent
	BordereauService retrieveBordereauLine(String bordereauLineId, Handler<AsyncResult<BordereauLine>> resultHandler);

	@Fluent
	BordereauService retrieveBordereauByClient(String clientId,
			Handler<AsyncResult<List<BordereauLine>>> resultHandler);

	@Fluent
	BordereauService retrieveBordereauByClientByPage(String clientId, int page,
			Handler<AsyncResult<List<BordereauLine>>> resultHandler);
	
	@Fluent
	BordereauService requestLastRecordBySource(String source, Handler<AsyncResult<BordereauLine>> resultHandler);

	@Fluent
	BordereauService removeBordereauLine(String bordereauLineId, Handler<AsyncResult<Void>> resultHandler);

}
