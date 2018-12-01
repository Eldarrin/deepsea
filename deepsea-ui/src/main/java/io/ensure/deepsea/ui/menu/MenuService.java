package io.ensure.deepsea.ui.menu;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;

public interface MenuService {

	@Fluent
	MenuService initializePersistence(Handler<AsyncResult<Void>> resultHandler);

	@Fluent
	MenuService addMenuItem(MenuItem menuItem, Handler<AsyncResult<Menu>> resultHandler);

	@Fluent
	MenuService retrieveSubMenu(Handler<AsyncResult<Menu>> resultHandler);
	
	@Fluent
	MenuService retrieveMenu(Handler<AsyncResult<Menu>> resultHandler);
	
}
