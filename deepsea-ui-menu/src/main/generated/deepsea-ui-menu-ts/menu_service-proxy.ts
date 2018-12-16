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


export class MenuService {

  private closed = false;

  private readonly convCharCollection = coll => {
    const ret = [];
    for (let i = 0; i < coll.length; i++) {
      ret.push(String.fromCharCode(coll[i]));
    }
    return ret;
  }

  constructor (private eb: any, private address: string) {
  }

  initializePersistence(resultHandler: (err: any, result: any) => any) : MenuService {
    if (closed) {
      throw new Error('Proxy is closed');
    }
    this.eb.send(this.address, {}, {"action":"initializePersistence"}, function(err, result) { resultHandler(err, result &&result.body); });
      return this;
  }

  addMenu(menuItem: any, resultHandler: (err: any, result: any) => any) : MenuService {
    if (closed) {
      throw new Error('Proxy is closed');
    }
    this.eb.send(this.address, {"menuItem": menuItem}, {"action":"addMenu"}, function(err, result) { resultHandler(err, result &&result.body); });
      return this;
  }

  changeMenuState(menuItem: any, resultHandler: (err: any, result: any) => any) : MenuService {
    if (closed) {
      throw new Error('Proxy is closed');
    }
    this.eb.send(this.address, {"menuItem": menuItem}, {"action":"changeMenuState"}, function(err, result) { resultHandler(err, result &&result.body); });
      return this;
  }

  retrieveSubMenu(id: string, resultHandler: (err: any, result: any) => any) : MenuService {
    if (closed) {
      throw new Error('Proxy is closed');
    }
    this.eb.send(this.address, {"id": id}, {"action":"retrieveSubMenu"}, function(err, result) { resultHandler(err, result &&result.body); });
      return this;
  }

  retrieveMenuChildren(parentID: string, resultHandler: (err: any, result: any) => any) : MenuService {
    if (closed) {
      throw new Error('Proxy is closed');
    }
    this.eb.send(this.address, {"parentID": parentID}, {"action":"retrieveMenuChildren"}, function(err, result) { resultHandler(err, result &&result.body); });
      return this;
  }

  retrieveMenu(id: string, resultHandler: (err: any, result: any) => any) : MenuService {
    if (closed) {
      throw new Error('Proxy is closed');
    }
    this.eb.send(this.address, {"id": id}, {"action":"retrieveMenu"}, function(err, result) { resultHandler(err, result &&result.body); });
      return this;
  }

}