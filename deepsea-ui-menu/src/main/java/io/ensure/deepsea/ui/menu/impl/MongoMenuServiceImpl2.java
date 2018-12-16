package io.ensure.deepsea.ui.menu.impl;

import java.util.List;

import io.ensure.deepsea.common.service.MongoRedisRepositoryWrapper;
import io.ensure.deepsea.ui.menu.MenuItem;
import io.ensure.deepsea.ui.menu.MenuService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisOptions;
import io.vertx.rxjava.core.Future;

public class MongoMenuServiceImpl2 extends MongoRedisRepositoryWrapper implements MenuService {

	private static final String MENU = "menu";
	
	private MenuItem mainMenu;
	
	public MongoMenuServiceImpl2(Vertx vertx, JsonObject config, RedisOptions rOptions) {
		super(vertx, config, rOptions, MENU);
	}

	@Override
	public MenuService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
		Future<Void> future = Future.future();
		future.setHandler(resultHandler);
		mainMenu = new MenuItem();
		mainMenu.setName("Home");
		mainMenu.setUrl("#");
		mainMenu.setMenuId("menu-home");
		addMenu(mainMenu, res -> {
			if (res.succeeded()) {
				future.succeeded();
			} else {
				future.fail(res.cause());
			}
		});
		return this;
	}

	@Override
	public MenuService addMenu(MenuItem menuItem, Handler<AsyncResult<MenuItem>> resultHandler) {
		this.upsertWithCache(menuItem.toJson(), MENU).map(option -> option.map(MenuItem::new).orElse(null))
		.setHandler(resultHandler);
		return this;
	}

	@Override
	public MenuService changeMenuState(MenuItem menuItem, Handler<AsyncResult<MenuItem>> resultHandler) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public MenuService retrieveSubMenu(String id, Handler<AsyncResult<MenuItem>> resultHandler) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public MenuService retrieveMenuChildren(String parentID, Handler<AsyncResult<List<MenuItem>>> resultHandler) {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public MenuService retrieveMenu(String id, Handler<AsyncResult<MenuItem>> resultHandler) {
		// TODO Auto-generated method stub
		return this;
	}

}
