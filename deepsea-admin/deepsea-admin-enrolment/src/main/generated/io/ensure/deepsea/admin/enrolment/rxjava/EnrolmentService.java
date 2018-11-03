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

package io.ensure.deepsea.admin.enrolment.rxjava;

import java.util.Map;
import rx.Observable;
import rx.Single;
import io.ensure.deepsea.admin.enrolment.models.Enrolment;
import java.util.List;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;


@io.vertx.lang.rxjava.RxGen(io.ensure.deepsea.admin.enrolment.EnrolmentService.class)
public class EnrolmentService {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EnrolmentService that = (EnrolmentService) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final io.vertx.lang.rxjava.TypeArg<EnrolmentService> __TYPE_ARG = new io.vertx.lang.rxjava.TypeArg<>(
    obj -> new EnrolmentService((io.ensure.deepsea.admin.enrolment.EnrolmentService) obj),
    EnrolmentService::getDelegate
  );

  private final io.ensure.deepsea.admin.enrolment.EnrolmentService delegate;
  
  public EnrolmentService(io.ensure.deepsea.admin.enrolment.EnrolmentService delegate) {
    this.delegate = delegate;
  }

  public io.ensure.deepsea.admin.enrolment.EnrolmentService getDelegate() {
    return delegate;
  }

  public EnrolmentService initializePersistence(Handler<AsyncResult<Void>> resultHandler) { 
    delegate.initializePersistence(resultHandler);
    return this;
  }

  public Single<Void> rxInitializePersistence() { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      initializePersistence(fut);
    }));
  }

  public EnrolmentService addEnrolment(Enrolment enrolment, Handler<AsyncResult<Integer>> resultHandler) { 
    delegate.addEnrolment(enrolment, resultHandler);
    return this;
  }

  public Single<Integer> rxAddEnrolment(Enrolment enrolment) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      addEnrolment(enrolment, fut);
    }));
  }

  public EnrolmentService replayEnrolments(Integer lastId, Handler<AsyncResult<List<Enrolment>>> resultHandler) { 
    delegate.replayEnrolments(lastId, resultHandler);
    return this;
  }

  public Single<List<Enrolment>> rxReplayEnrolments(Integer lastId) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      replayEnrolments(lastId, fut);
    }));
  }


  public static  EnrolmentService newInstance(io.ensure.deepsea.admin.enrolment.EnrolmentService arg) {
    return arg != null ? new EnrolmentService(arg) : null;
  }
}
