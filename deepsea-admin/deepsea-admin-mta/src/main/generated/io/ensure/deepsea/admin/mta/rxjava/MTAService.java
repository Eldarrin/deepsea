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

package io.ensure.deepsea.admin.mta.rxjava;

import java.util.Map;
import rx.Observable;
import rx.Single;
import java.util.List;
import io.ensure.deepsea.admin.mta.MidTermAdjustment;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;


@io.vertx.lang.rx.RxGen(io.ensure.deepsea.admin.mta.MTAService.class)
public class MTAService {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MTAService that = (MTAService) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final io.vertx.lang.rx.TypeArg<MTAService> __TYPE_ARG = new io.vertx.lang.rx.TypeArg<>(    obj -> new MTAService((io.ensure.deepsea.admin.mta.MTAService) obj),
    MTAService::getDelegate
  );

  private final io.ensure.deepsea.admin.mta.MTAService delegate;
  
  public MTAService(io.ensure.deepsea.admin.mta.MTAService delegate) {
    this.delegate = delegate;
  }

  public io.ensure.deepsea.admin.mta.MTAService getDelegate() {
    return delegate;
  }

  public io.ensure.deepsea.admin.mta.rxjava.MTAService initializePersistence(Handler<AsyncResult<Void>> resultHandler) { 
    delegate.initializePersistence(resultHandler);
    return this;
  }

    public Single<Void> rxInitializePersistence() { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      initializePersistence(fut);
    }));
  }

  public io.ensure.deepsea.admin.mta.rxjava.MTAService addMTA(MidTermAdjustment mta, Handler<AsyncResult<MidTermAdjustment>> resultHandler) { 
    delegate.addMTA(mta, resultHandler);
    return this;
  }

    public Single<MidTermAdjustment> rxAddMTA(MidTermAdjustment mta) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      addMTA(mta, fut);
    }));
  }

  public io.ensure.deepsea.admin.mta.rxjava.MTAService replayMTAs(Integer lastId, Handler<AsyncResult<List<MidTermAdjustment>>> resultHandler) { 
    delegate.replayMTAs(lastId, resultHandler);
    return this;
  }

    public Single<List<MidTermAdjustment>> rxReplayMTAs(Integer lastId) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      replayMTAs(lastId, fut);
    }));
  }


  public static  MTAService newInstance(io.ensure.deepsea.admin.mta.MTAService arg) {
    return arg != null ? new MTAService(arg) : null;
  }
}
