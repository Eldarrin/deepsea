package io.ensure.deepsea.actuarial.bordereau;

import static io.ensure.deepsea.actuarial.bordereau.BordereauService.SERVICE_ADDRESS;
import static io.ensure.deepsea.actuarial.bordereau.BordereauService.SERVICE_NAME;

import java.time.Instant;

import io.ensure.deepsea.actuarial.bordereau.api.RestBordereauAPIVerticle;
import io.ensure.deepsea.actuarial.bordereau.impl.MySqlBordereauServiceImpl;
import io.ensure.deepsea.common.BaseMicroserviceVerticle;
import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
import io.ensure.deepsea.common.helper.RedisHelper;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import io.vertx.serviceproxy.ServiceBinder;

public class BordereauVerticle extends BaseMicroserviceVerticle {

	private static final String ENROLMENT_CHANNEL = "enrolment";
	private static final String MTA_CHANNEL = "mta";
	private static final String REDIS_CHANNEL = "io.vertx.redis.";

	private Logger log = LoggerFactory.getLogger(getClass());

	private BordereauService bordereauService;

	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();

		ConfigRetriever retriever = ConfigRetriever.create(vertx,
				new ConfigRetrieverHelper().getOptions("deepsea", "deepsea-underwriting-actuarial"));
		retriever.getConfig(res -> {
			if (res.succeeded()) {
				// create the service instance
				JsonObject mySqlConfig = new JsonObject().put("host", res.result().getString("mysql.host"))
						.put("port", res.result().getInteger("mysql.port"))
						.put("username", res.result().getString("mysql.username"))
						.put("password", res.result().getString("mysql.password"))
						.put("database", res.result().getString("mysql.database"));

				bordereauService = new MySqlBordereauServiceImpl(vertx, mySqlConfig);
				// Register the handler
				new ServiceBinder(vertx).setAddress(SERVICE_ADDRESS).register(BordereauService.class, bordereauService);

				initBordereauDatabase(bordereauService);

				// publish the service and REST endpoint in the discovery infrastructure
				publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, BordereauService.class)
						.compose(servicePublished -> deployRestVerticle()).setHandler(future.completer());

			} else {
				log.error("Unable to find config map for deepsea-underwriting-actuarial MySQL");
			}

		});
		
		RedisHelper.getRedisOptions(vertx).setHandler(res -> {
			setupConsumers(res.result());
		});

	}

	private Future<Void> initBordereauDatabase(BordereauService service) {
		Future<Void> initFuture = Future.future();
		service.initializePersistence(initFuture.completer());
		return initFuture.map(v -> null);
	}

	private void setupConsumers(RedisOptions redisOptions) {
		vertx.eventBus().<JsonObject>consumer(REDIS_CHANNEL + MTA_CHANNEL, received -> {
			String message = received.body().getJsonObject("value").getString("message");
			log.trace(message);
			addBordereauLineFromMTA(new JsonObject(message));
		});
		vertx.eventBus().<JsonObject>consumer(REDIS_CHANNEL + ENROLMENT_CHANNEL, received -> {
			String message = received.body().getJsonObject("value").getString("message");
			log.trace(message);
			addBordereauLineFromEnrolment(new JsonObject(message));
		});

		RedisClient redis = RedisClient.create(vertx, redisOptions);

		redis.subscribe(MTA_CHANNEL, res -> {
			if (res.succeeded()) {
				redis.subscribe(ENROLMENT_CHANNEL, ar -> {
					if (ar.succeeded()) {
						requestMissed();
					} else {
						log.error(ar.result());
					}
				});
			} else {
				log.error(res.result());
			}
		});
		
	}

	private void requestMissed() {
		bordereauService.requestLastRecordBySource(ENROLMENT_CHANNEL, res -> vertx.eventBus()
				.send(ENROLMENT_CHANNEL + ".replay", 
						new JsonObject().put("dateCreated", res.result().getDateSourceCreated())));
		bordereauService.requestLastRecordBySource(MTA_CHANNEL, res -> vertx.eventBus()
				.send(MTA_CHANNEL + ".replay", 
						new JsonObject().put("dateCreated", res.result().getDateSourceCreated())));
	}

	private void addBordereauLineFromMTA(JsonObject mta) {
		BordereauLine bl = new BordereauLine();
		bl.setSource(MTA_CHANNEL);
		bl.setSourceId(mta.getString("mtaId"));
		bl.setBordereauLineId(bl.getSourceId());
		bl.setClientId("barclays"); //TODO: mta.getString("clientId"));
		bl.setCustomerName("AAA");  //TODO: mta.getString("firstName").substring(1, 1) + mta.getString("lastName"));
		bl.setEvent(BordereauEvent.MTA);
		bl.setEventDate(mta.getInstant("eventDate"));
		bl.setIpt(0);
		bl.setValue(0);
		bl.setStartDate(mta.getInstant("eventDate")); // TODO: get from policy when built
		bl.setDateSourceCreated(mta.getInstant("dateCreated"));
		bordereauService.addBordereauLine(bl, null);
	}

	private void addBordereauLineFromEnrolment(JsonObject enrolment) {
		BordereauLine bl = new BordereauLine();
		bl.setSource(ENROLMENT_CHANNEL);
		bl.setSourceId(enrolment.getString("enrolmentId"));
		bl.setBordereauLineId(bl.getSourceId());
		bl.setClientId(enrolment.getString("clientId"));
		bl.setCustomerName(enrolment.getString("firstName").substring(1, 1) + enrolment.getString("lastName"));
		bl.setEvent(BordereauEvent.INCEPTION);
		bl.setEventDate(Instant.now());
		bl.setIpt(enrolment.getDouble("ipt"));
		bl.setValue(enrolment.getDouble("grossPremium"));
		bl.setStartDate(enrolment.getInstant("startDate"));
		bl.setDateSourceCreated(enrolment.getInstant("dateCreated"));
		bordereauService.addBordereauLine(bl, null);
	}

	private Future<Void> deployRestVerticle() {
		Future<String> future = Future.future();
		vertx.deployVerticle(new RestBordereauAPIVerticle(bordereauService),
				new DeploymentOptions().setConfig(config()), future.completer());
		return future.map(r -> null);
	}
}
