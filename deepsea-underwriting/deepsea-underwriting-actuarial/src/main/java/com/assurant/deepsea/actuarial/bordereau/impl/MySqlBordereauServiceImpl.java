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
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class MySqlBordereauServiceImpl extends MySqlRepositoryWrapper implements BordereauService {
	
	private Logger log = LoggerFactory.getLogger(getClass());

	private static final int PAGE_LIMIT = 10;

	public MySqlBordereauServiceImpl(Vertx vertx, JsonObject config) {
		super(vertx, config);
	}

	@Override
	public BordereauService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
		client.getConnection(connHandler(resultHandler, connection -> {
			connection.execute(CREATE_STATEMENT, r -> {
				resultHandler.handle(r);
				connection.close();
			});
		}));
		return this;
	}

	@Override
	public BordereauService addBordereauLine(BordereauLine bordereauLine, Handler<AsyncResult<Void>> resultHandler) {
		JsonArray params = new JsonArray().add(bordereauLine.getBordereauLineId())
				.add(bordereauLine.getClientId())
				.add(bordereauLine.getCustomerName())
				.add(bordereauLine.getValue())
				.add(bordereauLine.getIpt())
				.add(fromInstant(bordereauLine.getStartDate()))
				.add(fromInstant(bordereauLine.getEventDate()))
				.add(bordereauLine.getEvent().toString());
		executeNoResult(params, INSERT_STATEMENT, resultHandler);
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
	public BordereauService removeBordereauLine(String bordereauLineId, Handler<AsyncResult<Void>> resultHandler) {
		// TODO Auto-generated method stub
		return null;
	}

	// SQL Statements
	private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS `bordereau` (\n"
			+ "  `bordereauLineId` VARCHAR(60) NOT NULL, \n" + "  `clientId` VARCHAR(60) NOT NULL, \n"
			+ "  `customerName` VARCHAR(255) NOT NULL, \n" + "  `value` double NOT NULL,\n"
			+ "  `ipt` double NOT NULL,\n" + "  `startDate` DATETIME NOT NULL, \n" + "  `eventDate` DATETIME NOT NULL, \n"
			+ "  `event` VARCHAR(30) NOT NULL, \n" + "  PRIMARY KEY (`bordereauLineId`),\n"
			+ "  KEY `index_client` (`clientId`) )";

	private static final String INSERT_STATEMENT = "INSERT INTO bordereau (`bordereauLineId`, `clientId`, \n"
			+ "  `customerName`, `value`, `ipt`, `startDate`, `eventDate`, `event`) \n"
			+ "  VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String FETCH_STATEMENT = "SELECT * FROM bordereau WHERE bordereauLineId = ?";

	private static final String FETCH_BY_CLIENT_STATEMENT = "SELECT * FROM bordereau WHERE clientId = ?";

	private static final String FETCH_BY_CLIENT_WITH_PAGE_STATEMENT = "SELECT * FROM bordereau WHERE clientId = ? LIMIT ?, ?";

}
