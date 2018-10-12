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


export class AccountService {

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

  initializePersistence(resultHandler: (err: any, result: any) => any) : AccountService {
    if (closed) {
      throw new Error('Proxy is closed');
    }
    this.eb.send(this.address, {}, {"action":"initializePersistence"}, function(err, result) { resultHandler(err, result &&result.body); });
      return this;
  }

  addAccount(account: any, resultHandler: (err: any, result: any) => any) : AccountService {
    if (closed) {
      throw new Error('Proxy is closed');
    }
    this.eb.send(this.address, {"account": account}, {"action":"addAccount"}, function(err, result) { resultHandler(err, result &&result.body); });
      return this;
  }

  retrieveAccount(id: string, resultHandler: (err: any, result: any) => any) : AccountService {
    if (closed) {
      throw new Error('Proxy is closed');
    }
    this.eb.send(this.address, {"id": id}, {"action":"retrieveAccount"}, function(err, result) { resultHandler(err, result &&result.body); });
      return this;
  }

  retrieveByUsername(username: string, resultHandler: (err: any, result: any) => any) : AccountService {
    if (closed) {
      throw new Error('Proxy is closed');
    }
    this.eb.send(this.address, {"username": username}, {"action":"retrieveByUsername"}, function(err, result) { resultHandler(err, result &&result.body); });
      return this;
  }

  retrieveAllAccounts(resultHandler: (err: any, result: any) => any) : AccountService {
    if (closed) {
      throw new Error('Proxy is closed');
    }
    this.eb.send(this.address, {}, {"action":"retrieveAllAccounts"}, function(err, result) { resultHandler(err, result &&result.body); });
      return this;
  }

  updateAccount(account: any, resultHandler: (err: any, result: any) => any) : AccountService {
    if (closed) {
      throw new Error('Proxy is closed');
    }
    this.eb.send(this.address, {"account": account}, {"action":"updateAccount"}, function(err, result) { resultHandler(err, result &&result.body); });
      return this;
  }

  deleteAccount(id: string, resultHandler: (err: any, result: any) => any) : AccountService {
    if (closed) {
      throw new Error('Proxy is closed');
    }
    this.eb.send(this.address, {"id": id}, {"action":"deleteAccount"}, function(err, result) { resultHandler(err, result &&result.body); });
      return this;
  }

  deleteAllAccounts(resultHandler: (err: any, result: any) => any) : AccountService {
    if (closed) {
      throw new Error('Proxy is closed');
    }
    this.eb.send(this.address, {}, {"action":"deleteAllAccounts"}, function(err, result) { resultHandler(err, result &&result.body); });
      return this;
  }

}