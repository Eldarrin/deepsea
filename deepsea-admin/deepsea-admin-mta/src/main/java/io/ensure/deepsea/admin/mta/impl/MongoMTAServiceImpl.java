package io.ensure.deepsea.admin.mta.impl;

import java.util.List;

import io.ensure.deepsea.admin.mta.MTAService;
import io.ensure.deepsea.admin.mta.MidTermAdjustment;
import io.ensure.deepsea.common.service.MongoRepositoryWrapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public class MongoMTAServiceImpl extends MongoRepositoryWrapper implements MTAService {
	
	protected final MongoClient client;
	
	public MongoMTAServiceImpl(Vertx vertx, JsonObject config) {
		this.client = MongoClient.createShared(vertx, config);
	}

	@Override
	public MTAService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MTAService addMTA(MidTermAdjustment mta, Handler<AsyncResult<Integer>> resultHandler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MTAService replayMTAs(Integer lastId, Handler<AsyncResult<List<MidTermAdjustment>>> resultHandler) {
		// TODO Auto-generated method stub
		return null;
	}

}
