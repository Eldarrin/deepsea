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

package io.ensure.deepsea.actuarial.bordereau.rxjava;

import java.util.Map;
import rx.Observable;
import rx.Single;
import java.util.List;
import io.ensure.deepsea.actuarial.bordereau.BordereauLine;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;


@io.vertx.lang.rx.RxGen(io.ensure.deepsea.actuarial.bordereau.BordereauService.class)
public class BordereauService {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BordereauService that = (BordereauService) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final io.vertx.lang.rx.TypeArg<BordereauService> __TYPE_ARG = new io.vertx.lang.rx.TypeArg<>(    obj -> new BordereauService((io.ensure.deepsea.actuarial.bordereau.BordereauService) obj),
    BordereauService::getDelegate
  );

  private final io.ensure.deepsea.actuarial.bordereau.BordereauService delegate;
  
  public BordereauService(io.ensure.deepsea.actuarial.bordereau.BordereauService delegate) {
    this.delegate = delegate;
  }

  public io.ensure.deepsea.actuarial.bordereau.BordereauService getDelegate() {
    return delegate;
  }

  /**
   * Initialize the persistence.
   * @param resultHandler the result handler will be called as soon as the initialization has been accomplished. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public io.ensure.deepsea.actuarial.bordereau.rxjava.BordereauService initializePersistence(Handler<AsyncResult<Void>> resultHandler) { 
    delegate.initializePersistence(resultHandler);
    return this;
  }

    /**
   * Initialize the persistence.
   * @return 
   */
  public Single<Void> rxInitializePersistence() { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      initializePersistence(fut);
    }));
  }

  public io.ensure.deepsea.actuarial.bordereau.rxjava.BordereauService addBordereauLine(BordereauLine bordereauLine, Handler<AsyncResult<BordereauLine>> resultHandler) { 
    delegate.addBordereauLine(bordereauLine, resultHandler);
    return this;
  }

    public Single<BordereauLine> rxAddBordereauLine(BordereauLine bordereauLine) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      addBordereauLine(bordereauLine, fut);
    }));
  }

  public io.ensure.deepsea.actuarial.bordereau.rxjava.BordereauService retrieveBordereauLine(String bordereauLineId, Handler<AsyncResult<BordereauLine>> resultHandler) { 
    delegate.retrieveBordereauLine(bordereauLineId, resultHandler);
    return this;
  }

    public Single<BordereauLine> rxRetrieveBordereauLine(String bordereauLineId) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      retrieveBordereauLine(bordereauLineId, fut);
    }));
  }

  public io.ensure.deepsea.actuarial.bordereau.rxjava.BordereauService retrieveBordereauByClient(String clientId, Handler<AsyncResult<List<BordereauLine>>> resultHandler) { 
    delegate.retrieveBordereauByClient(clientId, resultHandler);
    return this;
  }

    public Single<List<BordereauLine>> rxRetrieveBordereauByClient(String clientId) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      retrieveBordereauByClient(clientId, fut);
    }));
  }

  public io.ensure.deepsea.actuarial.bordereau.rxjava.BordereauService retrieveBordereauByClientByPage(String clientId, int page, Handler<AsyncResult<List<BordereauLine>>> resultHandler) { 
    delegate.retrieveBordereauByClientByPage(clientId, page, resultHandler);
    return this;
  }

    public Single<List<BordereauLine>> rxRetrieveBordereauByClientByPage(String clientId, int page) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      retrieveBordereauByClientByPage(clientId, page, fut);
    }));
  }

  public io.ensure.deepsea.actuarial.bordereau.rxjava.BordereauService requestLastRecordBySource(String source, Handler<AsyncResult<BordereauLine>> resultHandler) { 
    delegate.requestLastRecordBySource(source, resultHandler);
    return this;
  }

    public Single<BordereauLine> rxRequestLastRecordBySource(String source) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      requestLastRecordBySource(source, fut);
    }));
  }

  public io.ensure.deepsea.actuarial.bordereau.rxjava.BordereauService removeBordereauLine(String bordereauLineId, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.removeBordereauLine(bordereauLineId, resultHandler);
    return this;
  }

    public Single<Void> rxRemoveBordereauLine(String bordereauLineId) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      removeBordereauLine(bordereauLineId, fut);
    }));
  }


  public static  BordereauService newInstance(io.ensure.deepsea.actuarial.bordereau.BordereauService arg) {
    return arg != null ? new BordereauService(arg) : null;
  }
}
