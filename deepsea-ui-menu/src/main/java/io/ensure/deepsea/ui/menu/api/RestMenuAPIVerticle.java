package io.ensure.deepsea.ui.menu.api;

import io.ensure.deepsea.common.RestAPIVerticle;
import io.ensure.deepsea.common.config.ConfigRetrieverHelper;
import io.ensure.deepsea.ui.menu.MenuItem;
import io.ensure.deepsea.ui.menu.MenuService;
import io.vertx.config.ConfigRetriever;
import io.vertx.core.Future;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RestMenuAPIVerticle extends RestAPIVerticle {
	
private Logger log = LoggerFactory.getLogger(getClass());
	
	public static final String SERVICE_NAME = "menu-rest-api";
	
	private static final String API_ADD = "/add";
	private static final String API_RETRIEVE = "/";

	private final MenuService service;
	
	public RestMenuAPIVerticle(MenuService service) {
		this.service = service;
	}
	
	@Override
	public void start(Future<Void> future) throws Exception {
		super.start();
		final Router router = Router.router(vertx);
		// body handler
		router.route().handler(BodyHandler.create());
		// API route handler
		addHealthHandler(router, future);
		router.post(API_ADD).handler(this::apiAdd);
		router.get(API_RETRIEVE).handler(this::apiRetrieve);

		ConfigRetriever retriever = ConfigRetriever
				.create(vertx, new ConfigRetrieverHelper()
						.getOptions("deepsea", "deepsea-ui-menu"));
        retriever.getConfig(res -> {
        	if (res.succeeded()) {
        		String host = res.result().getString("menu.http.address", "0.0.0.0");
        		int port = res.result().getInteger("menu.http.port", 8080);
        		String serviceHost = res.result().getString("menu.service.hostname", "deepsea-ui.deepsea.svc");
        		String apiName = res.result().getString("menu.api.name", "menu");
        		
        		// create HTTP server and publish REST service
        		createHttpServer(router, host, port)
        				.compose(serverCreated -> publishHttpEndpoint(SERVICE_NAME, serviceHost, port, apiName))
        				.setHandler(future.completer());
        		
        	} else {
        		log.error("Unable to find config map for Deepsea Menu");
        	}
        
        });
	}
	
	private void apiAdd(RoutingContext rc) {
		try {
			MenuItem menuItem = new MenuItem(new JsonObject(rc.getBodyAsString()));
			service.addMenu(menuItem, resultHandler(rc, r -> {
				String result = new JsonObject().put("message", "menu added")
						.put("menuItemId", menuItem.getMenuItemId()).encodePrettily();
				rc.response().setStatusCode(201).putHeader("content-type", "application/json").end(result);
			}));
		} catch (DecodeException e) {
			badRequest(rc, e);
		}
	}
	
	private void apiRetrieve(RoutingContext rc) {
		service.retrieveMenu(resultHandlerNonEmpty(rc));
	}
}
