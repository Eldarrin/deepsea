package io.ensure.deepsea.actuarial.bordereau;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import io.ensure.deepsea.actuarial.bordereau.api.RestBordereauAPIVerticle;
import io.ensure.deepsea.actuarial.bordereau.impl.MySqlBordereauServiceImpl;
import io.ensure.deepsea.common.BaseMicroserviceVerticle;
import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
import io.ensure.deepsea.common.helper.ISO8601DateParser;
import io.ensure.deepsea.common.service.DeepseaRedis;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.AsyncResult;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.client.Redis;
import io.vertx.redis.client.RedisOptions;
import io.vertx.serviceproxy.ServiceBinder;
import io.ensure.deepsea.common.service.RedisWrapper;

public class BordereauVerticle extends BaseMicroserviceVerticle {
	
	private static final String SERVICE_NAME = "bordereau-eb-service";
	private static final String SERVICE_ADDRESS = "service.bordereau";
	private static final String REDIS_JSON_VALUE = "value";
	private static final String REPLAY = ".replay";
	private static final String DATE_CREATED = "dateCreated";
	private static final String ENROLMENT_CHANNEL = "enrolment";
	private static final String MTA_CHANNEL = "mta";
	private static final String REDIS_CHANNEL = "io.vertx.redis.";

	private final Logger log = LoggerFactory.getLogger(getClass());

	private BordereauService bordereauService;

	private DeepseaRedis dRedis;



	@Override
	public void start(Future<Void> future) {
		super.start();

		ConfigRetriever retriever = ConfigRetriever.create(vertx,
				new ConfigRetrieverHelper().getOptions("deepsea", "deepsea-underwriting-actuarial"));
		retriever.getConfig(res -> {
			if (res.succeeded()) {
				// create the service instance
        		JsonObject mySqlConfig = new JsonObject()
        				.put("host", res.result().getString("database.host"))
						.put("port", res.result().getInteger("database.port"))
						.put("username", System.getenv("DB_USERNAME"))
						.put("password", System.getenv("DB_PASSWORD"))
						.put("database", System.getenv("DB_NAME"));

				bordereauService = new MySqlBordereauServiceImpl(vertx, mySqlConfig);
				// Register the handler
				new ServiceBinder(vertx).setAddress(SERVICE_ADDRESS).register(BordereauService.class, bordereauService);

				initBordereauDatabase(bordereauService);

				// publish the service and REST endpoint in the discovery infrastructure
				publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, BordereauService.class)
						.compose(servicePublished -> deployRestVerticle()).setHandler(future);

				DeepseaRedis.getRedisOptions(vertx, "deepsea-underwriting-actuarial").setHandler(ar ->
					setupConsumers(ar.result()));
			} else {
				log.error("Unable to find config map for deepsea-underwriting-actuarial MySQL");
			}

		});
		
		

	}

	private Future<Void> initBordereauDatabase(BordereauService service) {
		Future<Void> initFuture = Future.future();
		service.initializePersistence(initFuture);
		return initFuture.map(v -> null);
	}

	private void setupConsumers(RedisOptions redisOptions) {
		List<String> channels = new ArrayList();
		channels.add(ENROLMENT_CHANNEL);
		channels.add(MTA_CHANNEL);

		RedisWrapper r = new RedisWrapper(vertx, redisOptions);

		r.connect(res -> {
			if (res.succeeded()) {
				log.info("redis connected");
				r.subscribe(res.result(), channels);
				bordereauService.requestLastRecordBySource(ENROLMENT_CHANNEL, req -> {
					if (req.succeeded()) {
						String dateRequired = "1970-01-01T00:00:00.000Z";
						if (req.result() != null) {
							dateRequired = ISO8601DateParser.toJsonString(req.result().getDateSourceCreated());
						}
						vertx.eventBus().send(ENROLMENT_CHANNEL + REPLAY,
								new JsonObject().put(DATE_CREATED, dateRequired));
					}

				});
				bordereauService.requestLastRecordBySource(MTA_CHANNEL, req -> vertx.eventBus()
						.send(MTA_CHANNEL + REPLAY,
								new JsonObject().put(DATE_CREATED,
										ISO8601DateParser.toJsonString(req.result().getDateSourceCreated()))));
				res.result().handler(msg -> {
					switch(msg.get(1).toString()) {
						case ENROLMENT_CHANNEL:
							addBordereauLineFromEnrolment(new JsonObject(msg.get(2).toString()));
							log.info(msg.get(2).toString());
							break;
						case MTA_CHANNEL:
							//addBordereauLineFromMTA(new JsonObject(msg.get(2).toString()));
							log.info(msg.get(2).toString());
							break;
						default:
							log.info(msg.get(1).toString());
							break;
					}
					log.trace("message:" + msg.get(1) + ":" + msg.get(2));
				});
			}
		});

	}

	private void addBordereauLineFromMTA(JsonObject mta) {
		BordereauLine bl = new BordereauLine();
		Policy policy = new Policy();
		bl.setSource(MTA_CHANNEL);
		bl.setSourceId(mta.getString("mtaId"));
		bl.setBordereauLineId(bl.getSourceId());
		bl.setClientId(policy.getClientId());
		bl.setCustomerName(policy.getCustomerName());  
		bl.setEvent(BordereauEvent.MTA);
		bl.setEventDate(mta.getInstant("eventDate"));
		bl.setIpt(mta.getDouble("ipt"));
		bl.setValue(mta.getDouble("value"));
		bl.setStartDate(policy.getStartDate());
		bl.setDateSourceCreated(mta.getInstant(DATE_CREATED));
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
		bl.setDateSourceCreated(enrolment.getInstant(DATE_CREATED));
		bordereauService.addBordereauLine(bl, null);
	}

	private Future<Void> deployRestVerticle() {
		Future<String> future = Future.future();
		vertx.deployVerticle(new RestBordereauAPIVerticle(bordereauService),
				new DeploymentOptions().setConfig(config()), future);
		return future.map(r -> null);
	}
	
	class Policy {
		
		private final String clientId;
		private final Instant startDate;
		private final String customerName;
		
		Policy() {
			this.clientId = "barclays";
			this.startDate = Instant.now();
			this.customerName = "PolTest";
		}

		public String getClientId() {
			return clientId;
		}

		public Instant getStartDate() {
			return startDate;
		}

		public String getCustomerName() {
			return customerName;
		}
	}
}
