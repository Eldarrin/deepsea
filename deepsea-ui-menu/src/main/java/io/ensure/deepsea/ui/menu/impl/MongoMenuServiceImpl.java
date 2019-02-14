package io.ensure.deepsea.ui.menu.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	
	private MenuItem mainMenu;
	
	public MongoMenuServiceImpl(Vertx vertx, JsonObject config, RedisOptions rOptions) {
		super(vertx, config, rOptions, MENU);
	}

	private List<MenuItem> createBaseMenu() {
			List<MenuItem> mnuBase = new ArrayList<>();
			mnuBase.add(simpleMenu("menu-dash", 0, "Dashboard", "dashboard", "#"));
			mnuBase.add(simpleMenu("menu-agent", 1, "Agent", "customer", "#"));
			mnuBase.add(simpleMenu("menu-policy", 2, "Policy", "policy", "#"));
			mnuBase.add(simpleMenu("menu-claim", 3, "Claim", "claim", "#"));
			mnuBase.add(simpleMenu("menu-actuarial", 4, "Actuarial", "blank", "#"));
			mnuBase.add(simpleMenu("menu-other", 5, "Other", "blank", "#"));
			return mnuBase;
	}

	private MenuItem simpleMenu(String id, int pos, String name, String page, String nav) {
		return new MenuItem(new JsonObject()
				.put("menuId", id)
				.put("menuPos", pos)
				.put("menuName", name)
				.put("menuPage", page)
				.put("navLink", nav)
				.put("parent", "menu-home")
		);
	}

	@Override
	public MenuService initializePersistence(Handler<AsyncResult<Void>> resultHandler) {
		Future<Void> future = Future.future();
		future.setHandler(resultHandler);
		mainMenu = new MenuItem();
		mainMenu.setMenuName("Deepsea");
		mainMenu.setNavLink("/#");
		mainMenu.setMenuId("menu-home");
		addMenu(mainMenu, res -> {
			if (res.succeeded()) {
				assignChildren(mainMenu);
				future.succeeded();
			} else {
				future.fail(res.cause());
			}
		});
		return this;
	}

	private void assignChildren(MenuItem m) {
		String parentId = m.getMenuId();
		if (parentId.startsWith("menu-")) parentId = parentId.substring(5);
		this.selectDocuments(MENU, new JsonObject().put("parent", parentId))
				.map(rawList -> rawList.stream().map(MenuItem::new).collect(Collectors.toList()))
				.setHandler(res -> {
					if (res.succeeded()) {
						m.setChildrenMenuItems(res.result());
						for (MenuItem mC : m.getChildrenMenuItems()) {
							assignChildren(mC);
						}
					}
		});
	}


	@Override
	public MenuService addMenu(MenuItem menuItem, Handler<AsyncResult<MenuItem>> resultHandler) {
		this.upsertWithCache(menuItem.toJson(), MENU).map(option -> option.map(MenuItem::new).orElse(null))
		.setHandler(resultHandler);
		assignChildren(mainMenu);
		return this;
	}

	@Override
	public MenuService changeMenuState(MenuItem menuItem, Handler<AsyncResult<MenuItem>> resultHandler) {
		assignChildren(mainMenu);
		return this;
	}

	@Override
	public MenuService retrieveSubMenu(String id, Handler<AsyncResult<MenuItem>> resultHandler) {
		Future<MenuItem> future = Future.future();
		future.setHandler(resultHandler);
		this.retrieveDocument("menu", id)
				.map(option -> option.map(MenuItem::new).orElse(null))
				.setHandler(res -> {
					if (res.succeeded()) {
						this.selectDocuments("menu", new JsonObject().put("parent", "id"))
								.map(rawList -> rawList.stream().map(MenuItem::new).collect(Collectors.toList()))
								.setHandler(rc -> {
									if (rc.succeeded()) {
										if (!rc.result().isEmpty()) {
											Collections.sort(rc.result(), new SortByPos());
											res.result().setChildrenMenuItems(rc.result());
										}
										future.complete(res.result());
									} else {
										future.fail(rc.cause());
									}
								});
					} else {
						future.fail(res.cause());
					}
				});
		return this;
	}

	@Override
	public MenuService retrieveMenuChildren(String parentID, Handler<AsyncResult<List<MenuItem>>> resultHandler) {
		Future<List<MenuItem>> future = Future.future();
		List<MenuItem> mList = new ArrayList<>();
		mList.add(mainMenu);
		future.setHandler(resultHandler).complete(mList);
		return this;
	}

	@Override
	public MenuService retrieveMenu(String id, Handler<AsyncResult<MenuItem>> resultHandler) {
		Future<MenuItem> future = Future.future();
		future.setHandler(resultHandler).complete(mainMenu);
		return this;
	}

	class SortByPos implements Comparator<MenuItem>
	{
		public int compare(MenuItem a, MenuItem b)
		{
			return a.getMenuPos() - b.getMenuPos();
		}
	}

}
