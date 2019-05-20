package io.ensure.deepsea.admin.enrolment.impl;

import java.text.ParseException;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import io.ensure.deepsea.admin.enrolment.EnrolmentService;
import io.ensure.deepsea.admin.enrolment.models.Enrolment;
import io.ensure.deepsea.common.helper.ISO8601DateParser;
import io.ensure.deepsea.common.service.MySqlRedisRepositoryWrapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.client.RedisOptions;

public class MySqlEnrolmentServiceImpl extends MySqlRedisRepositoryWrapper implements EnrolmentService {
	
	private final Logger log = LoggerFactory.getLogger(getClass());

	public MySqlEnrolmentServiceImpl(Vertx vertx, JsonObject config, RedisOptions rOptions) {
		super(vertx, config, rOptions);
		this.typeName = "enrolment";
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
	public EnrolmentService addEnrolment(Enrolment enrolment, Handler<AsyncResult<Enrolment>> resultHandler) {
		enrolment.setDateCreated(Instant.now());
		JsonArray params = new JsonArray().add(enrolment.getClientId())
				.add(enrolment.getFirstName())
				.add(enrolment.getLastName())
				.add(enrolment.getMiddleNames())
				.add(enrolment.getProductId())
				.add(fromInstant(enrolment.getStartDate()))
				.add(enrolment.getGrossPremium())
				.add(enrolment.getIpt())
				.add(fromInstant(enrolment.getDateCreated()));
		
		this.executeWithPublish(params, INSERT_STATEMENT, enrolment.toJson())
			.map(option -> option.map(Enrolment::new).orElse(null))
			.setHandler(resultHandler);
		return this;
	}
	
	@Override
	public EnrolmentService replayEnrolments(String lastDate,
			Handler<AsyncResult<List<Enrolment>>> resultHandler) {
		try {
			JsonArray params = new JsonArray().add(ISO8601DateParser.parse(lastDate).toInstant());
			this.retrieveMany(params, REPLAY_STATEMENT)
					.map(rawList -> rawList.stream().map(Enrolment::new).collect(Collectors.toList()))
					.setHandler(resultHandler);
		} catch (ParseException pe) {
			log.error(pe);
		}
		return this;
	}
	
	
	// SQL Statements
	private static final String CREATE_STATEMENT = "CREATE TABLE IF NOT EXISTS `enrolment` (\n"
			+ "  `enrolmentId` INT NOT NULL AUTO_INCREMENT, \n" + "  `clientId` VARCHAR(60) NOT NULL, \n"
			+ "  `firstName` VARCHAR(255) NOT NULL, \n" + "  `lastName` VARCHAR(255) NOT NULL, \n" 
			+ "  `middleNames` VARCHAR(255) NOT NULL, \n" + "  `productId` VARCHAR(255) NOT NULL, \n" 
			+ "  `startDate` DATETIME NOT NULL, \n" + "  `grossPremium` double NOT NULL,\n"
			+ "  `ipt` double NOT NULL,\n" + "  `dateCreated` DATETIME NOT NULL,\n"
			+ "  PRIMARY KEY (`enrolmentId`),\n"
			+ "  KEY `index_client_product` (`clientId`, `productId`) )";

	private static final String INSERT_STATEMENT = "INSERT INTO enrolment (`clientId`, \n"
			+ "  `firstName`, `lastName`, `middleNames`, `productId`, `startDate`, `grossPremium`, `ipt`, `dateCreated`) \n"
			+ "  VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

	private static final String REPLAY_STATEMENT = "SELECT * FROM enrolment WHERE dateCreated > ?";



}
