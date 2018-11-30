package io.ensure.deepsea.admin.mta.impl;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import io.ensure.deepsea.admin.mta.MTAService;
import io.ensure.deepsea.admin.mta.MidTermAdjustment;
import io.ensure.deepsea.common.service.MongoRedisRepositoryWrapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisOptions;

public class MongoMTAServiceImpl extends MongoRedisRepositoryWrapper implements MTAService {
	
	private static final String MTA = "mta";

	public MongoMTAServiceImpl(Vertx vertx, JsonObject config, RedisOptions rOptions) {
		super(vertx, config, rOptions);
		this.typeName = MTA;
	}

	@Override
	public MTAService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
		// Not required
		return null;
	}

	@Override
	public MTAService addMTA(MidTermAdjustment mta, Handler<AsyncResult<MidTermAdjustment>> resultHandler) {
		this.upsertWithPublish(mta.toJson(), MTA)
			.map(option -> option.map(MidTermAdjustment::new).orElse(null))
			.setHandler(resultHandler);
		return this;
	}

	@Override
	public MTAService replayMTAs(Integer lastId, Handler<AsyncResult<List<MidTermAdjustment>>> resultHandler) {
		this.selectDocuments(MTA, new JsonObject().put("eventDate", Instant.now()))
			.map(rawList -> rawList.stream().map(MidTermAdjustment::new).collect(Collectors.toList()))
			.setHandler(resultHandler);
		return this;
	}

}
