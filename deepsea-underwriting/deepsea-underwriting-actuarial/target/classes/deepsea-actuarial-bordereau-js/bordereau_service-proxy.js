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
!function (factory) {
  if (typeof require === 'function' && typeof module !== 'undefined') {
    factory();
  } else if (typeof define === 'function' && define.amd) {
    // AMD loader
    define('deepsea-actuarial-bordereau-js/bordereau_service-proxy', [], factory);
  } else {
    // plain old include
    BordereauService = factory();
  }
}(function () {

  /**
 @class
  */
  var BordereauService = function(eb, address) {

    var j_eb = eb;
    var j_address = address;
    var closed = false;
    var that = this;
    var convCharCollection = function(coll) {
      var ret = [];
      for (var i = 0;i < coll.length;i++) {
        ret.push(String.fromCharCode(coll[i]));
      }
      return ret;
    };

    /**
     Initialize the persistence.

     @public
     @param resultHandler {function} the result handler will be called as soon as the initialization has been accomplished. The async result indicates whether the operation was successful or not. 
     @return {BordereauService}
     */
    this.initializePersistence = function(resultHandler) {
      var __args = arguments;
      if (__args.length === 1 && typeof __args[0] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {}, {"action":"initializePersistence"}, function(err, result) { __args[0](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param bordereauLine {Object} 
     @param resultHandler {function} 
     @return {BordereauService}
     */
    this.addBordereauLine = function(bordereauLine, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"bordereauLine":__args[0]}, {"action":"addBordereauLine"}, function(err, result) { __args[1](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param bordereauLineId {string} 
     @param resultHandler {function} 
     @return {BordereauService}
     */
    this.retrieveBordereauLine = function(bordereauLineId, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"bordereauLineId":__args[0]}, {"action":"retrieveBordereauLine"}, function(err, result) { __args[1](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param clientId {string} 
     @param resultHandler {function} 
     @return {BordereauService}
     */
    this.retrieveBordereauByClient = function(clientId, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"clientId":__args[0]}, {"action":"retrieveBordereauByClient"}, function(err, result) { __args[1](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param clientId {string} 
     @param page {number} 
     @param resultHandler {function} 
     @return {BordereauService}
     */
    this.retrieveBordereauByClientByPage = function(clientId, page, resultHandler) {
      var __args = arguments;
      if (__args.length === 3 && typeof __args[0] === 'string' && typeof __args[1] ==='number' && typeof __args[2] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"clientId":__args[0], "page":__args[1]}, {"action":"retrieveBordereauByClientByPage"}, function(err, result) { __args[2](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param source {string} 
     @param resultHandler {function} 
     @return {BordereauService}
     */
    this.requestLastRecordBySource = function(source, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"source":__args[0]}, {"action":"requestLastRecordBySource"}, function(err, result) { __args[1](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

    /**

     @public
     @param bordereauLineId {string} 
     @param resultHandler {function} 
     @return {BordereauService}
     */
    this.removeBordereauLine = function(bordereauLineId, resultHandler) {
      var __args = arguments;
      if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
        if (closed) {
          throw new Error('Proxy is closed');
        }
        j_eb.send(j_address, {"bordereauLineId":__args[0]}, {"action":"removeBordereauLine"}, function(err, result) { __args[1](err, result &&result.body); });
        return that;
      } else throw new TypeError('function invoked with invalid arguments');
    };

  };

  if (typeof exports !== 'undefined') {
    if (typeof module !== 'undefined' && module.exports) {
      exports = module.exports = BordereauService;
    } else {
      exports.BordereauService = BordereauService;
    }
  } else {
    return BordereauService;
  }
});