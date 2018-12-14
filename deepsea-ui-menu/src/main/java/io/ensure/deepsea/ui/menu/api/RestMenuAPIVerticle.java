package io.ensure.deepsea.ui.menu.api;

import io.ensure.deepsea.common.RestAPIVerticle;
import io.ensure.deepsea.ui.menu.MenuItem;
import io.ensure.deepsea.ui.menu.MenuService;
import io.vertx.core.Future;
import io.vertx.core.json.DecodeException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RestMenuAPIVerticle extends RestAPIVerticle {
	
	public static final String SERVICE_NAME = "menu-rest-api";
	
	private static final String API_ADD = "/add";
	private static final String API_RETRIEVE = "/";
	private static final String API_RETRIEVE_ITEM = "/:menuItemId";

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
		router.get(API_RETRIEVE_ITEM).handler(this::apiRetrieveItem);
		startRestService(router, future, SERVICE_NAME, "menu", "deepsea", "deepsea-ui-menu");
		
	}
	
	private void apiAdd(RoutingContext rc) {
		try {
			MenuItem menuItem = new MenuItem(new JsonObject(rc.getBodyAsString()));
			service.addMenu(menuItem, resultHandler(rc, r -> {
				String result = new JsonObject().put("message", "menu added")
						.put("menuItem", r.toJson()).encodePrettily();
				rc.response().setStatusCode(201).putHeader("content-type", "application/json").end(result);
			}));
		} catch (DecodeException e) {
			badRequest(rc, e);
		}
	}
	
	private void apiRetrieveItem(RoutingContext context) {
		String menuItemId = context.request().getParam("menuItemId");
		service.retrieveMenuChildren(menuItemId, resultHandlerNonEmpty(context));
	}
	
	private void apiRetrieve(RoutingContext rc) {
		service.retrieveMenu("home", resultHandlerNonEmpty(rc));
	}
}
