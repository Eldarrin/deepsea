/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.ensure.deepsea.ui.menu.rxjava;

import java.util.Map;
import rx.Observable;
import rx.Single;
import io.ensure.deepsea.ui.menu.MenuItem;
import java.util.List;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;


@io.vertx.lang.rxjava.RxGen(io.ensure.deepsea.ui.menu.MenuService.class)
public class MenuService {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MenuService that = (MenuService) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final io.vertx.lang.rxjava.TypeArg<MenuService> __TYPE_ARG = new io.vertx.lang.rxjava.TypeArg<>(
    obj -> new MenuService((io.ensure.deepsea.ui.menu.MenuService) obj),
    MenuService::getDelegate
  );

  private final io.ensure.deepsea.ui.menu.MenuService delegate;
  
  public MenuService(io.ensure.deepsea.ui.menu.MenuService delegate) {
    this.delegate = delegate;
  }

  public io.ensure.deepsea.ui.menu.MenuService getDelegate() {
    return delegate;
  }

  public MenuService initializePersistence(Handler<AsyncResult<Void>> resultHandler) { 
    delegate.initializePersistence(resultHandler);
    return this;
  }

  public Single<Void> rxInitializePersistence() { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      initializePersistence(fut);
    }));
  }

  public MenuService addMenu(MenuItem menuItem, Handler<AsyncResult<MenuItem>> resultHandler) { 
    delegate.addMenu(menuItem, resultHandler);
    return this;
  }

  public Single<MenuItem> rxAddMenu(MenuItem menuItem) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      addMenu(menuItem, fut);
    }));
  }

  public MenuService changeMenuState(MenuItem menuItem, Handler<AsyncResult<MenuItem>> resultHandler) { 
    delegate.changeMenuState(menuItem, resultHandler);
    return this;
  }

  public Single<MenuItem> rxChangeMenuState(MenuItem menuItem) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      changeMenuState(menuItem, fut);
    }));
  }

  public MenuService retrieveSubMenu(String id, Handler<AsyncResult<MenuItem>> resultHandler) { 
    delegate.retrieveSubMenu(id, resultHandler);
    return this;
  }

  public Single<MenuItem> rxRetrieveSubMenu(String id) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      retrieveSubMenu(id, fut);
    }));
  }

  public MenuService retrieveMenuChildren(String parentID, Handler<AsyncResult<List<MenuItem>>> resultHandler) { 
    delegate.retrieveMenuChildren(parentID, resultHandler);
    return this;
  }

  public Single<List<MenuItem>> rxRetrieveMenuChildren(String parentID) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      retrieveMenuChildren(parentID, fut);
    }));
  }

  public MenuService retrieveMenu(String id, Handler<AsyncResult<MenuItem>> resultHandler) { 
    delegate.retrieveMenu(id, resultHandler);
    return this;
  }

  public Single<MenuItem> rxRetrieveMenu(String id) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      retrieveMenu(id, fut);
    }));
  }


  public static  MenuService newInstance(io.ensure.deepsea.ui.menu.MenuService arg) {
    return arg != null ? new MenuService(arg) : null;
  }
}
