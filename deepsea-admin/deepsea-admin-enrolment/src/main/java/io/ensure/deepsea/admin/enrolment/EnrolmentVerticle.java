package io.ensure.deepsea.admin.enrolment;

import static io.ensure.deepsea.admin.enrolment.EnrolmentService.SERVICE_ADDRESS;
import static io.ensure.deepsea.admin.enrolment.EnrolmentService.SERVICE_NAME;

import io.ensure.deepsea.admin.enrolment.api.RestEnrolmentAPIVerticle;
import io.ensure.deepsea.admin.enrolment.impl.MySqlEnrolmentServiceImpl;
import io.ensure.deepsea.common.BaseMicroserviceVerticle;
import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.serviceproxy.ServiceBinder;

public class EnrolmentVerticle extends BaseMicroserviceVerticle {

	private Logger log = LoggerFactory.getLogger(getClass());

	private EnrolmentService enrolmentService;

	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();
		startEBCluster();
		
		ConfigRetriever retriever = ConfigRetriever
				.create(vertx, new ConfigRetrieverHelper()
						.getOptions("deepsea", "deepsea-admin-enrolment"));
        retriever.getConfig(res -> {
        	if (res.succeeded()) {
        		// create the service instance
        		JsonObject mySqlConfig = new JsonObject()
        				.put("host", res.result().getString("mysql.host"))
        				.put("port", res.result().getInteger("mysql.port"))
        				.put("username", res.result().getString("mysql.username"))
        				.put("password", res.result().getString("mysql.password"))
        				.put("database", res.result().getString("mysql.database"));

        		enrolmentService = new MySqlEnrolmentServiceImpl(vertx, mySqlConfig);
        		// Register the handler
        		new ServiceBinder(vertx)
        				.setAddress(SERVICE_ADDRESS)
        				.register(EnrolmentService.class, enrolmentService);

        		initEnrolmentDatabase(enrolmentService);

        		// publish the service and REST endpoint in the discovery infrastructure
        		publishEventBusService(SERVICE_NAME, SERVICE_ADDRESS, EnrolmentService.class)
        				.compose(servicePublished -> deployRestVerticle()).setHandler(future.completer());
        		vertx.eventBus().publish("enrolment", new JsonObject().put("started", "true"));
        		setupReplayConsumer();
        	} else {
        		log.error("Unable to find config map for deepsea-admin-enrolment MySQL");
        	}
        
        });
		
	}
	
	private void setupReplayConsumer() {
		vertx.eventBus().<JsonObject>consumer("enrolment.replay", msg -> 
			enrolmentService.replayEnrolments(msg.body().getInteger("lastId"), res -> {
				if (res.succeeded()) {
					msg.reply(res.result());
				}
			})
		);
	}
	
	private Future<Void> initEnrolmentDatabase(EnrolmentService service) {
		Future<Void> initFuture = Future.future();
		service.initializePersistence(initFuture.completer());
		return initFuture.map(v -> null);
	}

	private Future<Void> deployRestVerticle() {
		Future<String> future = Future.future();
		vertx.deployVerticle(new RestEnrolmentAPIVerticle(enrolmentService),
				new DeploymentOptions().setConfig(config()), future.completer());
		return future.map(r -> null);
	}

	private Future<Void> startEBCluster() {
		Future<Void> future = Future.future();
		VertxOptions options = new VertxOptions();
		Vertx.clusteredVertx(options, res -> {
			if (res.succeeded()) {
				Vertx vertx = res.result();
				EventBus eventBus = vertx.eventBus();
				log.info("Clustered event bus started: " + eventBus);
				future.complete();
			} else {
				log.error("Clustered event bus failed: " + res.cause());
				future.fail(res.cause());
			}
		});
		return future;
	}

}
