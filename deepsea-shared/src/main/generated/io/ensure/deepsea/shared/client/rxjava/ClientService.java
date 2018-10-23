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

package io.ensure.deepsea.shared.client.rxjava;

import java.util.Map;
import rx.Observable;
import rx.Single;
import java.util.List;
import io.vertx.core.AsyncResult;
import io.ensure.deepsea.shared.client.Client;
import io.vertx.core.Handler;


@io.vertx.lang.rxjava.RxGen(io.ensure.deepsea.shared.client.ClientService.class)
public class ClientService {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ClientService that = (ClientService) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final io.vertx.lang.rxjava.TypeArg<ClientService> __TYPE_ARG = new io.vertx.lang.rxjava.TypeArg<>(
    obj -> new ClientService((io.ensure.deepsea.shared.client.ClientService) obj),
    ClientService::getDelegate
  );

  private final io.ensure.deepsea.shared.client.ClientService delegate;
  
  public ClientService(io.ensure.deepsea.shared.client.ClientService delegate) {
    this.delegate = delegate;
  }

  public io.ensure.deepsea.shared.client.ClientService getDelegate() {
    return delegate;
  }

  /**
   * Initialize the persistence.
   * @param resultHandler the result handler will be called as soon as the initialization has been accomplished. The async result indicates whether the operation was successful or not.
   * @return 
   */
  public ClientService initializePersistence(Handler<AsyncResult<Void>> resultHandler) { 
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

  public ClientService addClient(Client client, Handler<AsyncResult<Void>> resultHandler) { 
    delegate.addClient(client, resultHandler);
    return this;
  }

  public Single<Void> rxAddClient(Client client) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      addClient(client, fut);
    }));
  }

  public ClientService retrieveClients(Handler<AsyncResult<List<Client>>> resultHandler) { 
    delegate.retrieveClients(resultHandler);
    return this;
  }

  public Single<List<Client>> rxRetrieveClients() { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      retrieveClients(fut);
    }));
  }

  public ClientService removeClient(Client client, Handler<AsyncResult<Client>> resultHandler) { 
    delegate.removeClient(client, resultHandler);
    return this;
  }

  public Single<Client> rxRemoveClient(Client client) { 
    return Single.create(new io.vertx.rx.java.SingleOnSubscribeAdapter<>(fut -> {
      removeClient(client, fut);
    }));
  }


  public static  ClientService newInstance(io.ensure.deepsea.shared.client.ClientService arg) {
    return arg != null ? new ClientService(arg) : null;
  }
}
