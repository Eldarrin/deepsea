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

/** @module deepsea-ui-menu-js/menu_service */
var utils = require('vertx-js/util/utils');

var io = Packages.io;
var JsonObject = io.vertx.core.json.JsonObject;
var JMenuService = Java.type('io.ensure.deepsea.ui.menu.MenuService');
var MenuItem = Java.type('io.ensure.deepsea.ui.menu.MenuItem');

/**
 @class
*/
var MenuService = function(j_val) {

  var j_menuService = j_val;
  var that = this;

  /**

   @public
   @param resultHandler {function} 
   @return {MenuService}
   */
  this.initializePersistence = function(resultHandler) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_menuService["initializePersistence(io.vertx.core.Handler)"](function(ar) {
      if (ar.succeeded()) {
        resultHandler(null, null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param menuItem {Object} 
   @param resultHandler {function} 
   @return {MenuService}
   */
  this.addMenu = function(menuItem, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_menuService["addMenu(io.ensure.deepsea.ui.menu.MenuItem,io.vertx.core.Handler)"](menuItem != null ? new MenuItem(new JsonObject(Java.asJSONCompatible(menuItem))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnDataObject(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param menuItem {Object} 
   @param resultHandler {function} 
   @return {MenuService}
   */
  this.changeMenuState = function(menuItem, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && (typeof __args[0] === 'object' && __args[0] != null) && typeof __args[1] === 'function') {
      j_menuService["changeMenuState(io.ensure.deepsea.ui.menu.MenuItem,io.vertx.core.Handler)"](menuItem != null ? new MenuItem(new JsonObject(Java.asJSONCompatible(menuItem))) : null, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnDataObject(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param id {string} 
   @param resultHandler {function} 
   @return {MenuService}
   */
  this.retrieveSubMenu = function(id, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_menuService["retrieveSubMenu(java.lang.String,io.vertx.core.Handler)"](id, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnDataObject(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param parentID {string} 
   @param resultHandler {function} 
   @return {MenuService}
   */
  this.retrieveMenuChildren = function(parentID, resultHandler) {
    var __args = arguments;
    if (__args.length === 2 && typeof __args[0] === 'string' && typeof __args[1] === 'function') {
      j_menuService["retrieveMenuChildren(java.lang.String,io.vertx.core.Handler)"](parentID, function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnListSetDataObject(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  /**

   @public
   @param resultHandler {function} 
   @return {MenuService}
   */
  this.retrieveMenu = function(resultHandler) {
    var __args = arguments;
    if (__args.length === 1 && typeof __args[0] === 'function') {
      j_menuService["retrieveMenu(io.vertx.core.Handler)"](function(ar) {
      if (ar.succeeded()) {
        resultHandler(utils.convReturnDataObject(ar.result()), null);
      } else {
        resultHandler(null, ar.cause());
      }
    });
      return that;
    } else throw new TypeError('function invoked with invalid arguments');
  };

  // A reference to the underlying Java delegate
  // NOTE! This is an internal API and must not be used in user code.
  // If you rely on this property your code is likely to break if we change it / remove it without warning.
  this._jdel = j_menuService;
};

MenuService._jclass = utils.getJavaClass("io.ensure.deepsea.ui.menu.MenuService");
MenuService._jtype = {
  accept: function(obj) {
    return MenuService._jclass.isInstance(obj._jdel);
  },
  wrap: function(jdel) {
    var obj = Object.create(MenuService.prototype, {});
    MenuService.apply(obj, arguments);
    return obj;
  },
  unwrap: function(obj) {
    return obj._jdel;
  }
};
MenuService._create = function(jdel) {
  var obj = Object.create(MenuService.prototype, {});
  MenuService.apply(obj, arguments);
  return obj;
}
module.exports = MenuService;