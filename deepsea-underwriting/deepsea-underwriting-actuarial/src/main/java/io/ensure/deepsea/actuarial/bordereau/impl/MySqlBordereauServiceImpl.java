package io.ensure.deepsea.actuarial.bordereau.impl;

import java.util.List;
import java.util.stream.Collectors;

import io.ensure.deepsea.actuarial.bordereau.BordereauLine;
import io.ensure.deepsea.actuarial.bordereau.BordereauService;
import io.ensure.deepsea.common.service.MySqlRepositoryWrapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.core.Future;

public class MySqlBordereauServiceImpl extends MySqlRepositoryWrapper implements BordereauService {
	
	private static final int PAGE_LIMIT = 10;

	public MySqlBordereauServiceImpl(Vertx vertx, JsonObject config) {
		super(vertx, config);
	}

	@Override
	public BordereauService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
		client.getConnection(connHandler(resultHandler, connection -> 
			connection.execute(CREATE_STATEMENT, r -> {
				resultHandler.handle(r);
				connection.close();
			})
		));
		return this;
	}

	@Override
	public BordereauService addBordereauLine(BordereauLine bordereauLine, Handler<AsyncResult<BordereauLine>> resultHandler) {
		Future<BordereauLine> future = Future.future();
		JsonArray params = new JsonArray().add(bordereauLine.getSource())
				.add(bordereauLine.getSourceId())
				.add(bordereauLine.getBordereauLineId())
				.add(bordereauLine.getClientId())
				.add(bordereauLine.getCustomerName())
				.add(bordereauLine.getValue())
				.add(bordereauLine.getIpt())
				.add(fromInstant(bordereauLine.getStartDate()))
				.add(fromInstant(bordereauLine.getEventDate()))
				.add(bordereauLine.getEvent().toString())
				.add(fromInstant(bordereauLine.getDateSourceCreated()));
		this.executeReturnKey(params, INSERT_STATEMENT).setHandler(res -> {
			if (res.succeeded()) {
				if (res.result().isPresent()) {
					bordereauLine.setBordereauLineId("bordereauline-" + res.result().get());
					future.setHandler(resultHandler).complete(bordereauLine);
				}
			} else {
				future.setHandler(resultHandler).fail(res.cause());
			}
		});
		return this;
	}
	
	@Override
	public BordereauService retrieveBordereauLine(String bordereauLineId,
			Handler<AsyncResult<BordereauLine>> resultHandler) {
		this.retrieveOne(bordereauLineId, FETCH_STATEMENT)
				.map(option -> option.map(BordereauLine::new).orElse(null))
				.setHandler(resultHandler);
		return this;
	}

	@Override
	public BordereauService retrieveBordereauByClient(String clientId,
			Handler<AsyncResult<List<BordereauLine>>> resultHandler) {
		JsonArray params = new JsonArray().add(clientId);
		this.retrieveMany(params, FETCH_BY_CLIENT_STATEMENT)
				.map(rawList -> rawList.stream().map(BordereauLine::new).collect(Collectors.toList()))
				.setHandler(resultHandler);
		return this;
	}

	@Override
	public BordereauService retrieveBordereauByClientByPage(String clientId, int page,
			Handler<AsyncResult<List<BordereauLine>>> resultHandler) {
		JsonArray params = new JsonArray().add(clientId);
		this.retrieveManyByPage(page, PAGE_LIMIT, params, FETCH_BY_CLIENT_WITH_PAGE_STATEMENT)
				.map(rawList -> rawList.stream().map(BordereauLine::new).collect(Collectors.toList()))
				.setHandler(resultHandler);
		return this;
	}
	
	@Override
	public BordereauService requestLastRecordBySource(String source, Handler<AsyncResult<BordereauLine>> resultHandler) {
		this.retrieveOne(source, GET_LAST_ROW)
			.map(option -> option.map(BordereauLine::new).orElse(null))
			.setHandler(resultHandler);
		return this;
	}
	
	@Override
	public BordereauService removeBordereauLine(String bordereauLineId, Handler<AsyncResult<Void>> resultHandler) {
		this.removeOne(bordereauLineId, REMOVE_BORDEREAULINE, resultHandler);
		return this;
	}

	// SQL Statements
	private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS `bordereau` (\n"
			+ "  `source` VARCHAR(60) NOT NULL, \n" + "  `sourceId` VARCHAR(60) NOT NULL, \n"
			+ "  `bordereauLineId` VARCHAR(120) NOT NULL, \n" + "  `clientId` VARCHAR(60) NOT NULL, \n"
			+ "  `customerName` VARCHAR(255) NOT NULL, \n" + "  `value` double NOT NULL,\n"
			+ "  `ipt` double NOT NULL,\n" + "  `startDate` DATETIME NOT NULL, \n" 
			+ "  `eventDate` DATETIME NOT NULL, \n" + "  `event` VARCHAR(30) NOT NULL, \n" 
			+ "  `dateSourceCreated` DATETIME(3) NOT NULL, \n" 
			+ "  PRIMARY KEY (`bordereauLineId`),\n"
			+ "  KEY `index_client` (`clientId`) )";

	private static final String INSERT_STATEMENT = "INSERT INTO bordereau ("
			+ " `source`, `sourceId`, `bordereauLineId`, `clientId`, \n"
			+ "  `customerName`, `value`, `ipt`, `startDate`, `eventDate`, `event`, `dateSourceCreated`) \n"
			+ "  VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String FETCH_STATEMENT = "SELECT * FROM bordereau WHERE bordereauLineId = ?";

	private static final String FETCH_BY_CLIENT_STATEMENT = "SELECT * FROM bordereau WHERE clientId = ?";

	private static final String FETCH_BY_CLIENT_WITH_PAGE_STATEMENT = "SELECT * FROM bordereau WHERE clientId = ? LIMIT ?, ?";
	
	private static final String REMOVE_BORDEREAULINE = "DELETE FROM bordereau WHERE bordereauLineId = ?";
	
	private static final String GET_LAST_ROW = "SELECT * FROM bordereau WHERE source = ? ORDER BY dateSourceCreated DESC LIMIT 1";
	
}
