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

/** @module deepsea-admin-enrolment-js/enrolment_service */
var utils = require('vertx-js/util/utils');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JEnrolmentService = Java.type('io.ensure.deepsea.admin.enrolment.EnrolmentService');
var Enrolment = Java.type('io.ensure.deepsea.admin.enrolment.models.Enrolment');

/**
 @class
*/
var EnrolmentService = function(j_val) {

  var j_enrolmentService = j_val;
  var that = this;

  var __super_initializePersistence = this.initializePersistence;
  var __super_addEnrolment = this.addEnrolment;
  var __super_replayEnrolments = this.replayEnrolments;
  /**

   @public
   @param resultHandler {function} 
   @return {EnrolmentService}
   */
  this.initializePersistence =  function(resultHandler) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_enrolmentService["initializePersistence(io.vertx.core.Handler)"](function(ar) {
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
   @param enrolment {Object} 
   @param resultHandler {function} 
   @return {EnrolmentService}
   */
  this.addEnrolment =  function(enrolment, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_enrolmentService["addEnrolment(io.ensure.deepsea.admin.enrolment.models.Enrolment,io.vertx.core.Handler)"](__args[0]  != null ? new Enrolment(new JsonObject(Java.asJSONCompatible(__args[0]))) : null, function(ar) {
        if (ar.succeeded()) {
          __args[1](utils.convReturnDataObject(ar.result()), null);
        } else {
          __args[1](null, ar.cause());
        }
      }) ;
      return that;
    } else if (typeof __super_addEnrolment != 'undefined') {
      return __super_addEnrolment.apply(this, __args);
    }
    else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param lastDate {string} 
   @param resultHandler {function} 
   @return {EnrolmentService}
   */
  this.replayEnrolments =  function(lastDate, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_enrolmentService["replayEnrolments(java.lang.String,io.vertx.core.Handler)"](__args[0], function(ar) {
        if (ar.succeeded()) {
          __args[1](utils.convReturnListSetDataObject(ar.result()), null);
        } else {
          __args[1](null, ar.cause());
        }
      }) ;
      return that;
    } else if (typeof __super_replayEnrolments != 'undefined') {
      return __super_replayEnrolments.apply(this, __args);
    }
    else throw new TypeError('function invoked with invalid arguments');
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_enrolmentService;
};

EnrolmentService._jclass = utils.getJavaClass("io.ensure.deepsea.admin.enrolment.EnrolmentService");
EnrolmentService._jtype = {accept: function(obj) {
    return EnrolmentService._jclass.isInstance(obj._jdel);
  },wrap: function(jdel) {
    var obj = Object.create(EnrolmentService.prototype, {});
    EnrolmentService.apply(obj, arguments);
    return obj;
  },
  unwrap: function(obj) {
    return obj._jdel;
  }
};
EnrolmentService._create = function(jdel) {var obj = Object.create(EnrolmentService.prototype, {});
  EnrolmentService.apply(obj, arguments);
  return obj;
}
EnrolmentService.SERVICE_NAME = JEnrolmentService.SERVICE_NAME;
EnrolmentService.SERVICE_ADDRESS = JEnrolmentService.SERVICE_ADDRESS;
module.exports = EnrolmentService;