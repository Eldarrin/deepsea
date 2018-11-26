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


export class EnrolmentService {

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

  initializePersistence(resultHandler: (err: any, result: any) => any) : EnrolmentService {
    if (closed) {
      throw new Error('Proxy is closed');
    }
    this.eb.send(this.address, {}, {"action":"initializePersistence"}, function(err, result) { resultHandler(err, result &&result.body); });
      return this;
  }

  addEnrolment(enrolment: any, resultHandler: (err: any, result: any) => any) : EnrolmentService {
    if (closed) {
      throw new Error('Proxy is closed');
    }
    this.eb.send(this.address, {"enrolment": enrolment}, {"action":"addEnrolment"}, function(err, result) { resultHandler(err, result &&result.body); });
      return this;
  }

  replayEnrolments(lastDate: string, resultHandler: (err: any, result: any) => any) : EnrolmentService {
    if (closed) {
      throw new Error('Proxy is closed');
    }
    this.eb.send(this.address, {"lastDate": lastDate}, {"action":"replayEnrolments"}, function(err, result) { resultHandler(err, result &&result.body); });
      return this;
  }

}