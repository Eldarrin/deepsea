package io.ensure.deepsea.admin.mta.impl;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import io.ensure.deepsea.admin.mta.MTAService;
import io.ensure.deepsea.admin.mta.MidTermAdjustment;
import io.ensure.deepsea.common.service.MongoRepositoryWrapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

public class MongoMTAServiceImpl extends MongoRepositoryWrapper implements MTAService {
	
	public MongoMTAServiceImpl(Vertx vertx, JsonObject config) {
		super(vertx, config);
	}

	@Override
	public MTAService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MTAService addMTA(MidTermAdjustment mta, Handler<AsyncResult<String>> resultHandler) {
		this.upsertSingle(mta.toJson(), "mta", resultHandler);
		return this;
	}

	@Override
	public MTAService replayMTAs(Integer lastId, Handler<AsyncResult<List<MidTermAdjustment>>> resultHandler) {
		this.selectDocuments("mta", new JsonObject().put("eventDate", Instant.now()))
			.map(rawList -> rawList.stream().map(MidTermAdjustment::new).collect(Collectors.toList()))
			.setHandler(resultHandler);
		return this;
	}

}
