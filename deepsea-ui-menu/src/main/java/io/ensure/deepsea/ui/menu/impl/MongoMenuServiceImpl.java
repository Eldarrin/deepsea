package io.ensure.deepsea.ui.menu.impl;

import java.util.List;
import java.util.stream.Collectors;

import io.ensure.deepsea.common.service.MongoRedisRepositoryWrapper;
import io.ensure.deepsea.ui.menu.MenuItem;
import io.ensure.deepsea.ui.menu.MenuService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisOptions;
import io.vertx.rxjava.core.Future;

public class MongoMenuServiceImpl extends MongoRedisRepositoryWrapper implements MenuService {

	private static final String MENU = "menu";

	public MongoMenuServiceImpl(Vertx vertx, JsonObject config, RedisOptions rOptions) {
		super(vertx, config, rOptions, MENU);
	}

	@Override
	public MenuService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
		Future<Void> future = Future.future();
		future.setHandler(resultHandler);
		MenuItem menuItem = new MenuItem();
		menuItem.setName("Home");
		menuItem.setUrl("#");
		menuItem.setMenuId("menu-home");
		addMenu(menuItem, res -> {
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
		this.upsertWithCache(menuItem.toJson(), MENU)
		.map(option -> option.map(MenuItem::new).orElse(null))
		.setHandler(resultHandler);
		return this;
	}

	@Override
	public MenuService retrieveSubMenu(String id, Handler<AsyncResult<MenuItem>> resultHandler) {
		this.retrieveDocumentWithCache(MENU, id)
		.map(option -> option.map(MenuItem::new).orElse(null))
		.setHandler(resultHandler);
		return this;
	}

	@Override
	public MenuService retrieveMenu(Handler<AsyncResult<MenuItem>> resultHandler) {
		Future<MenuItem> future = Future.future();
		this.retrieveDocumentWithCache(MENU, new JsonObject().put("_id", "menu-home"))
		.map(option -> option.map(MenuItem::new).orElse(null))
		.setHandler(res -> {
			if (res.succeeded()) {
				this.selectDocuments(MENU, new JsonObject().put("parent", "home"))
				.map(rawList -> rawList.stream().map(MenuItem::new).collect(Collectors.toList()))
				.setHandler(ar -> {
					if (ar.succeeded()) {
						MenuItem mItem = res.result();
						mItem.setChildrenMenuItems(ar.result());
						future.setHandler(resultHandler).complete(mItem);
					} else {
						future.setHandler(resultHandler).fail(ar.cause());
					}
				});
			} else {
				future.setHandler(resultHandler).fail(res.cause());
			}
		});
		return this;
	}

	public MenuService retrieveMenuChildren2(String parentID, Handler<AsyncResult<List<MenuItem>>> resultHandler) {
		if (parentID.startsWith("menu-")) {
			parentID = parentID.substring(5);
		}
		this.selectDocuments(MENU, new JsonObject().put("parent", parentID))
		.map(rawList -> rawList.stream().map(MenuItem::new).collect(Collectors.toList()))
		.setHandler(res -> {
			if (res.succeeded()) {
				for (MenuItem mItem : res.result()) {
					retrieveMenuChildren2(mItem.getMenuId(), ar -> {
						if (ar.succeeded() && !ar.result().isEmpty()) {
							mItem.setChildrenMenuItems(ar.result());
						}
					});
				}

			}
		});
		return this;
	}

	@Override
	public MenuService retrieveMenuChildren(String parentID, Handler<AsyncResult<List<MenuItem>>> resultHandler) {
		if (parentID.startsWith("menu-")) {
			parentID = parentID.substring(5);
		}
		this.selectDocuments(MENU, new JsonObject().put("parent", parentID))
		.map(rawList -> rawList.stream().map(MenuItem::new).collect(Collectors.toList()))
		.setHandler(resultHandler);
		return this;
	}

	@Override
	public MenuService changeMenuState(MenuItem menuItem, Handler<AsyncResult<MenuItem>> resultHandler) {
		// TODO Auto-generated method stub
		return null;
	}

}
