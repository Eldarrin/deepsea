package io.ensure.deepsea.actuarial.bordereau;

import static io.ensure.deepsea.actuarial.bordereau.BordereauService.SERVICE_ADDRESS;
import static io.ensure.deepsea.actuarial.bordereau.BordereauService.SERVICE_NAME;

import java.time.Instant;

import io.ensure.deepsea.actuarial.bordereau.api.RestBordereauAPIVerticle;
import io.ensure.deepsea.actuarial.bordereau.impl.MySqlBordereauServiceImpl;
import io.ensure.deepsea.common.BaseMicroserviceVerticle;
import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
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
	private static final String REDIS_CHANNEL = "redis.deepsea.svc";

	private Logger log = LoggerFactory.getLogger(getClass());

	private BordereauService bordereauService;
	
	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();
		
		ConfigRetriever retriever = ConfigRetriever
				.create(vertx, new ConfigRetrieverHelper()
						.getOptions("deepsea", "deepsea-underwriting-actuarial"));
        retriever.getConfig(res -> {
        	if (res.succeeded()) {
        		// create the service instance
        		JsonObject mySqlConfig = new JsonObject()
        				.put("host", res.result().getString("mysql.host"))
        				.put("port", res.result().getInteger("mysql.port"))
        				.put("username", res.result().getString("mysql.username"))
        				.put("password", res.result().getString("mysql.password"))
        				.put("database", res.result().getString("mysql.database"));

        		bordereauService = new MySqlBordereauServiceImpl(vertx, mySqlConfig);
        		// Register the handler
        		new ServiceBinder(vertx)
        				.setAddress(SERVICE_ADDRESS)
        				.register(BordereauService.class, bordereauService);

        		initBordereauDatabase(bordereauService);
        		
        		// publish the service and REST endpoint in the discovery infrastructure
        		publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, BordereauService.class)
        				.compose(servicePublished -> deployRestVerticle()).setHandler(future.completer());

        	} else {
        		log.error("Unable to find config map for deepsea-underwriting-actuarial MySQL");
        	}
        
        });
        
        ConfigRetriever redisRetriever = ConfigRetriever
				.create(vertx, new ConfigRetrieverHelper()
						.getOptions("deepsea", "deepsea-redis"));
        
        redisRetriever.getConfig(res -> {
        	if (res.succeeded()) {
        		RedisOptions redisConfig = new RedisOptions()
        				.setHost(res.result().getString("redis.host"))
        				.setPort(res.result().getInteger("redis.port"))
        				.setAuth(res.result().getString("redis.auth"));
        		
        		setupConsumers(redisConfig);                            
        		requestMissed();
        	}
        });

	}

	private Future<Void> initBordereauDatabase(BordereauService service) {
		Future<Void> initFuture = Future.future();
		service.initializePersistence(initFuture.completer());
		return initFuture.map(v -> null);
	}
	
	private void setupConsumers(RedisOptions redisOptions) {
		vertx.eventBus().<JsonObject>consumer(REDIS_CHANNEL + "." + MTA_CHANNEL, received -> {
			  // do whatever you need to do with your message
			  JsonObject value = received.body().getJsonObject("value");
			  // the value is a JSON doc with the following properties
			  // channel - The channel to which this message was sent
			  // pattern - Pattern is present if you use psubscribe command and is the pattern that matched this message channel
			  // message - The message payload
			});

			RedisClient redis = RedisClient.create(vertx, redisOptions);

			redis.subscribe(MTA_CHANNEL, res -> {
			  if (res.succeeded()) {
			    log.info(res.result());
			  }
			});
		
		vertx.eventBus().<JsonObject>consumer(ENROLMENT_CHANNEL, res -> 
			// convert enrolment to bordereauline and add
			addBordereauLineFromEnrolment(res.body())
		);
		/*vertx.eventBus().<JsonObject>consumer(MTA_CHANNEL, res -> 
			// convert mta to bordereauline and add
			addBordereauLineFromMTA(res.body())
		);*/
	}
	
	private void requestMissed() {
		bordereauService.requestLastRecordBySource(ENROLMENT_CHANNEL, res -> 
			vertx.eventBus().send(ENROLMENT_CHANNEL + ".replay", 
					new JsonObject().put("lastId", res.result().getSourceId()))
		);
	}
	
	private void addBordereauLineFromMTA(JsonObject mta) {
		// TODO: WILL FAIL ATM
		BordereauLine bl = new BordereauLine();
		bl.setSource(MTA_CHANNEL);
		bl.setSourceId(mta.getInteger("mtaId"));
		bl.setBordereauLineId(MTA_CHANNEL + "-" + bl.getSourceId());
		bordereauService.addBordereauLine(bl, null);
	}
	
	private void addBordereauLineFromEnrolment(JsonObject enrolment) {
		BordereauLine bl = new BordereauLine();
		bl.setSource(ENROLMENT_CHANNEL);
		bl.setSourceId(enrolment.getInteger("enrolmentId"));
		bl.setBordereauLineId("enrolment-" + enrolment.getInteger("enrolmentId"));
		bl.setClientId(enrolment.getString("clientId"));
		bl.setCustomerName(enrolment.getString("firstName").substring(1, 1)
				+ enrolment.getString("lastName"));
		bl.setEvent(BordereauEvent.INCEPTION);
		bl.setEventDate(Instant.now());
		bl.setIpt(enrolment.getDouble("ipt"));
		bl.setValue(enrolment.getDouble("grossPremium"));
		bl.setStartDate(enrolment.getInstant("startDate"));
		bordereauService.addBordereauLine(bl, null);
	}
	
	private Future<Void> deployRestVerticle() {
		Future<String> future = Future.future();
		vertx.deployVerticle(new RestBordereauAPIVerticle(bordereauService),
				new DeploymentOptions().setConfig(config()), future.completer());
		return future.map(r -> null);
	}
}
