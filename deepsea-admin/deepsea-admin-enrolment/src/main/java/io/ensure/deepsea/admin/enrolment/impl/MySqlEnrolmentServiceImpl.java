package io.ensure.deepsea.admin.enrolment.impl;

import io.ensure.deepsea.admin.enrolment.EnrolmentService;
import io.ensure.deepsea.admin.enrolment.models.Enrolment;
import io.ensure.deepsea.common.service.MySqlRepositoryWrapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class MySqlEnrolmentServiceImpl extends MySqlRepositoryWrapper implements EnrolmentService {

	public MySqlEnrolmentServiceImpl(Vertx vertx, JsonObject config) {
		super(vertx, config);
	}

	@Override
	public EnrolmentService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
		client.getConnection(connHandler(resultHandler, connection -> 
		connection.execute(CREATE_STATEMENT, r -> {
				resultHandler.handle(r);
				connection.close();
			})
		));
		return this;
	}

	@Override
	public EnrolmentService addEnrolment(Enrolment enrolment, Handler<AsyncResult<Integer>> resultHandler) {
		JsonArray params = new JsonArray().add(enrolment.getClientId())
				.add(enrolment.getFirstName())
				.add(enrolment.getLastName())
				.add(enrolment.getMiddleNames())
				.add(enrolment.getProductId())
				.add(fromInstant(enrolment.getStartDate()))
				.add(enrolment.getGrossPremium())
				.add(enrolment.getIpt());
		
		this.executeReturnKey(params, INSERT_STATEMENT)
		.map(option -> option.map(Integer::new).orElse(null))
		.setHandler(resultHandler);
		return this;
	}
	
	// SQL Statements
	private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS `enrolment` (\n"
			+ "  `enrolmentId` INT NOT NULL AUTO_INCREMENT, \n" + "  `clientId` VARCHAR(60) NOT NULL, \n"
			+ "  `firstName` VARCHAR(255) NOT NULL, \n" + "  `lastName` VARCHAR(255) NOT NULL, \n" 
			+ "  `middleNames` VARCHAR(255) NOT NULL, \n" + "  `productId` VARCHAR(255) NOT NULL, \n" 
			+ "  `startDate` DATETIME NOT NULL, \n" + "  `grossPremium` double NOT NULL,\n"
			+ "  `ipt` double NOT NULL,\n" + "  PRIMARY KEY (`enrolmentId`),\n"
			+ "  KEY `index_client_product` (`clientId`, `productId`) )";

	private static final String INSERT_STATEMENT = "INSERT INTO enrolment (`clientId`, \n"
			+ "  `firstName`, `lastName`, `middleNames`, `productId`, `startDate`, `grossPremium`, `ipt`) \n"
			+ "  VALUES(?, ?, ?, ?, ?, ?, ?, ?)";


}
