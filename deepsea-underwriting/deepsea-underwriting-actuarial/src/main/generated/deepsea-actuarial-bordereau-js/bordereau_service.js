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

/** @module deepsea-actuarial-bordereau-js/bordereau_service */
var utils = require('vertx-js/util/utils');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JBordereauService = Java.type('io.ensure.deepsea.actuarial.bordereau.BordereauService');
var BordereauLine = Java.type('io.ensure.deepsea.actuarial.bordereau.BordereauLine');

/**
 @class
*/
var BordereauService = function(j_val) {

  var j_bordereauService = j_val;
  var that = this;

  var __super_initializePersistence = this.initializePersistence;
  var __super_addBordereauLine = this.addBordereauLine;
  var __super_retrieveBordereauLine = this.retrieveBordereauLine;
  var __super_retrieveBordereauByClient = this.retrieveBordereauByClient;
  var __super_retrieveBordereauByClientByPage = this.retrieveBordereauByClientByPage;
  var __super_requestLastRecordBySource = this.requestLastRecordBySource;
  var __super_removeBordereauLine = this.removeBordereauLine;
  /**
   Initialize the persistence.

   @public
   @param resultHandler {function} the result handler will be called as soon as the initialization has been accomplished. The async result indicates whether the operation was successful or not. 
   @return {BordereauService}
   */
  this.initializePersistence =  function(resultHandler) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_bordereauService["initializePersistence(io.vertx.core.Handler)"](function(ar) {
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
   @param bordereauLine {Object} 
   @param resultHandler {function} 
   @return {BordereauService}
   */
  this.addBordereauLine =  function(bordereauLine, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_bordereauService["addBordereauLine(io.ensure.deepsea.actuarial.bordereau.BordereauLine,io.vertx.core.Handler)"](__args[0]  != null ? new BordereauLine(new JsonObject(Java.asJSONCompatible(__args[0]))) : null, function(ar) {
        if (ar.succeeded()) {
          __args[1](utils.convReturnDataObject(ar.result()), null);
        } else {
          __args[1](null, ar.cause());
        }
      }) ;
      return that;
    } else if (typeof __super_addBordereauLine != 'undefined') {
      return __super_addBordereauLine.apply(this, __args);
    }
    else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param bordereauLineId {string} 
   @param resultHandler {function} 
   @return {BordereauService}
   */
  this.retrieveBordereauLine =  function(bordereauLineId, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_bordereauService["retrieveBordereauLine(java.lang.String,io.vertx.core.Handler)"](__args[0], function(ar) {
        if (ar.succeeded()) {
          __args[1](utils.convReturnDataObject(ar.result()), null);
        } else {
          __args[1](null, ar.cause());
        }
      }) ;
      return that;
    } else if (typeof __super_retrieveBordereauLine != 'undefined') {
      return __super_retrieveBordereauLine.apply(this, __args);
    }
    else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param clientId {string} 
   @param resultHandler {function} 
   @return {BordereauService}
   */
  this.retrieveBordereauByClient =  function(clientId, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_bordereauService["retrieveBordereauByClient(java.lang.String,io.vertx.core.Handler)"](__args[0], function(ar) {
        if (ar.succeeded()) {
          __args[1](utils.convReturnListSetDataObject(ar.result()), null);
        } else {
          __args[1](null, ar.cause());
        }
      }) ;
      return that;
    } else if (typeof __super_retrieveBordereauByClient != 'undefined') {
      return __super_retrieveBordereauByClient.apply(this, __args);
    }
    else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param clientId {string} 
   @param page {number} 
   @param resultHandler {function} 
   @return {BordereauService}
   */
  this.retrieveBordereauByClientByPage =  function(clientId, page, resultHandler) {
    var __args = arguments;
    if (__args.length === 3 && typeof __args[0] === 'string' && typeof __args[1] ==='number' && typeof __args[2] === 'function') {
      j_bordereauService["retrieveBordereauByClientByPage(java.lang.String,int,io.vertx.core.Handler)"](__args[0], __args[1], function(ar) {
        if (ar.succeeded()) {
          __args[2](utils.convReturnListSetDataObject(ar.result()), null);
        } else {
          __args[2](null, ar.cause());
        }
      }) ;
      return that;
    } else if (typeof __super_retrieveBordereauByClientByPage != 'undefined') {
      return __super_retrieveBordereauByClientByPage.apply(this, __args);
    }
    else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param source {string} 
   @param resultHandler {function} 
   @return {BordereauService}
   */
  this.requestLastRecordBySource =  function(source, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_bordereauService["requestLastRecordBySource(java.lang.String,io.vertx.core.Handler)"](__args[0], function(ar) {
        if (ar.succeeded()) {
          __args[1](utils.convReturnDataObject(ar.result()), null);
        } else {
          __args[1](null, ar.cause());
        }
      }) ;
      return that;
    } else if (typeof __super_requestLastRecordBySource != 'undefined') {
      return __super_requestLastRecordBySource.apply(this, __args);
    }
    else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param bordereauLineId {string} 
   @param resultHandler {function} 
   @return {BordereauService}
   */
  this.removeBordereauLine =  function(bordereauLineId, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_bordereauService["removeBordereauLine(java.lang.String,io.vertx.core.Handler)"](__args[0], function(ar) {
        if (ar.succeeded()) {
          __args[1](null, null);
        } else {
          __args[1](null, ar.cause());
        }
      }) ;
      return that;
    } else if (typeof __super_removeBordereauLine != 'undefined') {
      return __super_removeBordereauLine.apply(this, __args);
    }
    else throw new TypeError('function invoked with invalid arguments');
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_bordereauService;
};

BordereauService._jclass = utils.getJavaClass("io.ensure.deepsea.actuarial.bordereau.BordereauService");
BordereauService._jtype = {accept: function(obj) {
    return BordereauService._jclass.isInstance(obj._jdel);
  },wrap: function(jdel) {
    var obj = Object.create(BordereauService.prototype, {});
    BordereauService.apply(obj, arguments);
    return obj;
  },
  unwrap: function(obj) {
    return obj._jdel;
  }
};
BordereauService._create = function(jdel) {var obj = Object.create(BordereauService.prototype, {});
  BordereauService.apply(obj, arguments);
  return obj;
}
module.exports = BordereauService;