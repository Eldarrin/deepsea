package io.ensure.deepsea.ui.menu.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.ensure.deepsea.common.service.MongoRedisRepositoryWrapper;
import io.ensure.deepsea.ui.menu.MenuItem;
import io.ensure.deepsea.ui.menu.MenuService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.redis.RedisOptions;
import io.vertx.rxjava.core.Future;

public class MongoMenuServiceImpl extends MongoRedisRepositoryWrapper implements MenuService {

	private static final String MENU = "menu";

	private Logger log = LoggerFactory.getLogger(getClass());

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
		this.upsertWithCache(menuItem.toJson(), MENU).map(option -> option.map(MenuItem::new).orElse(null))
				.setHandler(resultHandler);
		return this;
	}

	@Override
	public MenuService retrieveSubMenu(String id, Handler<AsyncResult<MenuItem>> resultHandler) {
		this.retrieveDocumentWithCache(MENU, id).map(option -> option.map(MenuItem::new).orElse(null))
				.setHandler(resultHandler);
		return this;
	}

	/*@Override
	public MenuService retrieveMenu(Handler<AsyncResult<MenuItem>> resultHandler) {
		Future<MenuItem> future = Future.future();
		this.retrieveDocumentWithCache(MENU, new JsonObject().put("_id", "menu-home"))
				.map(option -> option.map(MenuItem::new).orElse(null)).setHandler(res -> {
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
	}*/
	
	
	
	
	
	private void assignChildren(Optional<JsonObject> j, Handler<AsyncResult<MenuItem>> resultHandler) {
		Future<MenuItem> future = Future.future();
		if (!j.isPresent()) {
			future.setHandler(resultHandler).complete();
		} else {
			MenuItem m = new MenuItem(j.get());
			String parentId = m.getMenuId();
			if (parentId.startsWith("menu-")) {
				parentId = parentId.substring(5);
			}
			this.selectDocuments(MENU, new JsonObject().put("parent", parentId))
				.map(rawList -> rawList.stream().map(MenuItem::new).collect(Collectors.toList()))
				.setHandler(res -> {
					if (res.succeeded()) {
						m.setChildrenMenuItems(res.result());
						future.setHandler(resultHandler).complete(m);
					} else {
						future.setHandler(resultHandler).fail(res.cause());
					}
				});
		}
		//return future;
	}
	
	private MenuItem assignChildren(MenuItem m) {
		String parentId = m.getParent();
		if (parentId.startsWith("menu-")) {
			parentId = parentId.substring(5);
		}
		this.selectDocuments(MENU, new JsonObject().put("parent", parentId))
			.map(rawList -> rawList.stream().map(MenuItem::new).collect(Collectors.toList()))
			.setHandler(res -> {
				if (res.succeeded()) {
					m.setChildrenMenuItems(res.result());
				}
			});
		return m;
	}

	
	public MenuService retrieveMenuChildren2(String parentID, Handler<AsyncResult<List<MenuItem>>> resultHandler) {
		Future<MenuItem> future = Future.future();
		this.retrieveDocument(MENU, parentID)
		.setHandler(res -> {
			if (res.succeeded()) {
				log.info("rMC" + res.result().isPresent());
				if (res.result().isPresent()) {
					log.info(res.result().get());
					//build(new MenuItem(res.result().get()).getMenuId()).setHandler(resultHandler);
				} else {
					log.info("not present");
				}
			}
		});
		;
		return this;
	}
	
	@Override
	public MenuService retrieveMenu(String id, Handler<AsyncResult<MenuItem>> resultHandler) {
		Future<MenuItem> future = Future.future();
		List<Future<MenuItem>> futList = new ArrayList<>();
		this.retrieveDocument(MENU, id)
		.setHandler(res -> {
			if (res.succeeded()) {
				MenuItem m = new MenuItem(res.result().get());
				build(m.getMenuId()).setHandler(ar -> {
					if (ar.succeeded()) {
						
						m.setChildrenMenuItems(ar.result());
						future.setHandler(resultHandler).complete(m);
					}
				});
			}
		});
		return this;
	}
	
	private Future<MenuItem> build(MenuItem m, List<Future<MenuItem>> futList) {
		Future<MenuItem> future = Future.future();
		String parentId = m.getMenuId();
		if (parentId.startsWith("menu-")) {
			parentId = parentId.substring(5);
		}
		log.info("building for " + parentId);
		this.selectDocuments(MENU, new JsonObject().put("parent", parentId))
		.map(rawList -> rawList.stream().map(MenuItem::new).collect(Collectors.toList()))
		.setHandler(res -> {
			if (res.succeeded()) {
				if (res.result().isEmpty()) {
					log.info("build is empty");
					future.complete();
					//resultHandler.handle(res); // first already written
				} else {
					log.info("it has " + res.result().size() + " children");
					// create future array
					for (int i = 0 ; i < res.result().size(); i++) {
						// add a future
						log.info("recommanding build for " + res.result().get(i).getName());
						final int addTo = i;
						// set future to build
						build(res.result().get(addTo), futList).setHandler(ar -> {
							if (ar.succeeded()) {
								//res.result().get(addTo).setChildrenMenuItems(ar.result());
							}
						});
					}
					m.setChildrenMenuItems(res.result());
					future.complete(m);
					//resultHandler.handle(res); // other already written
				}
			} else {
				//new Future<List<MenuItem>>().setHandler(resultHandler).fail(res.cause());
				//Future.resultHandler.handle(Future.failedFuture(res.cause()));
				future.fail(res.cause());
			}
				
		});
		return future;
	}
	
	private Future<List<MenuItem>> build(String parentId) {
		Future<List<MenuItem>> future = Future.future();
		if (parentId.startsWith("menu-")) {
			parentId = parentId.substring(5);
		}
		log.info("building for " + parentId);
		this.selectDocuments(MENU, new JsonObject().put("parent", parentId))
				.map(rawList -> rawList.stream().map(MenuItem::new).collect(Collectors.toList()))
				.setHandler(res -> {
					if (res.succeeded()) {
						if (res.result().isEmpty()) {
							log.info("build is empty");
							future.complete();
							//resultHandler.handle(res); // first already written
						} else {
							log.info("it has " + res.result().size() + " children");
							for (int i = 0; i < res.result().size(); i++) {
								log.info("recommanding build for " + res.result().get(i).getName());
								final int addTo = i;
								build(res.result().get(addTo).getMenuId()).setHandler(ar -> {
									if (ar.succeeded()) {
										res.result().get(addTo).setChildrenMenuItems(ar.result());
									}
								});
							}
							log.info("COMPLETING IT NOW!");
							future.complete(res.result());
							//resultHandler.handle(res); // other already written
						}
					} else {
						//new Future<List<MenuItem>>().setHandler(resultHandler).fail(res.cause());
						//Future.resultHandler.handle(Future.failedFuture(res.cause()));
						future.fail(res.cause());
					}

				});
		return future;
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
