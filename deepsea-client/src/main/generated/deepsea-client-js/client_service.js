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

/** @module deepsea-client-js/client_service */
var utils = require('vertx-js/util/utils');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JClientService = Java.type('io.ensure.deepsea.client.ClientService');
var Client = Java.type('io.ensure.deepsea.client.Client');

/**
 @class
*/
var ClientService = function(j_val) {

  var j_clientService = j_val;
  var that = this;

  var __super_initializePersistence = this.initializePersistence;
  var __super_addClient = this.addClient;
  var __super_retrieveClient = this.retrieveClient;
  var __super_retrieveClients = this.retrieveClients;
  var __super_removeClient = this.removeClient;
  /**
   Initialize the persistence.

   @public
   @param resultHandler {function} the result handler will be called as soon as the initialization has been accomplished. The async result indicates whether the operation was successful or not. 
   @return {ClientService}
   */
  this.initializePersistence =  function(resultHandler) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_clientService["initializePersistence(io.vertx.core.Handler)"](function(ar) {
        if (ar.succeeded()) {
          __args[0](null, null);
        } else {
          __args[0](null, ar.cause());
        }
      }) ;
      return that;
    } else if (typeof __super_initializePersistence != 'undefined') {
      return __super_initializePersistence.apply(this, __args);
    }
    else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param client {Object} 
   @param resultHandler {function} 
   @return {ClientService}
   */
  this.addClient =  function(client, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_clientService["addClient(io.ensure.deepsea.client.Client,io.vertx.core.Handler)"](__args[0]  != null ? new Client(new JsonObject(Java.asJSONCompatible(__args[0]))) : null, function(ar) {
        if (ar.succeeded()) {
          __args[1](utils.convReturnDataObject(ar.result()), null);
        } else {
          __args[1](null, ar.cause());
        }
      }) ;
      return that;
    } else if (typeof __super_addClient != 'undefined') {
      return __super_addClient.apply(this, __args);
    }
    else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param id {string} 
   @param resultHandler {function} 
   @return {ClientService}
   */
  this.retrieveClient =  function(id, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_clientService["retrieveClient(java.lang.String,io.vertx.core.Handler)"](__args[0], function(ar) {
        if (ar.succeeded()) {
          __args[1](utils.convReturnDataObject(ar.result()), null);
        } else {
          __args[1](null, ar.cause());
        }
      }) ;
      return that;
    } else if (typeof __super_retrieveClient != 'undefined') {
      return __super_retrieveClient.apply(this, __args);
    }
    else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param resultHandler {function} 
   @return {ClientService}
   */
  this.retrieveClients =  function(resultHandler) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_clientService["retrieveClients(io.vertx.core.Handler)"](function(ar) {
        if (ar.succeeded()) {
          __args[0](utils.convReturnListSetDataObject(ar.result()), null);
        } else {
          __args[0](null, ar.cause());
        }
      }) ;
      return that;
    } else if (typeof __super_retrieveClients != 'undefined') {
      return __super_retrieveClients.apply(this, __args);
    }
    else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param client {Object} 
   @param resultHandler {function} 
   @return {ClientService}
   */
  this.removeClient =  function(client, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_clientService["removeClient(io.ensure.deepsea.client.Client,io.vertx.core.Handler)"](__args[0]  != null ? new Client(new JsonObject(Java.asJSONCompatible(__args[0]))) : null, function(ar) {
        if (ar.succeeded()) {
          __args[1](null, null);
        } else {
          __args[1](null, ar.cause());
        }
      }) ;
      return that;
    } else if (typeof __super_removeClient != 'undefined') {
      return __super_removeClient.apply(this, __args);
    }
    else throw new TypeError('function invoked with invalid arguments');
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_clientService;
};

ClientService._jclass = utils.getJavaClass("io.ensure.deepsea.client.ClientService");
ClientService._jtype = {accept: function(obj) {
    return ClientService._jclass.isInstance(obj._jdel);
  },wrap: function(jdel) {
    var obj = Object.create(ClientService.prototype, {});
    ClientService.apply(obj, arguments);
    return obj;
  },
  unwrap: function(obj) {
    return obj._jdel;
  }
};
ClientService._create = function(jdel) {var obj = Object.create(ClientService.prototype, {});
  ClientService.apply(obj, arguments);
  return obj;
}
module.exports = ClientService;