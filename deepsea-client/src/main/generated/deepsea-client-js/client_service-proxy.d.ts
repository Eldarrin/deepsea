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


/**
 @class
*/
export default class ClientService {

  constructor (eb: any, address: string);

  initializePersistence(resultHandler: (err: any, result: any) => any) : ClientService;

  addClient(client: any, resultHandler: (err: any, result: any) => any) : ClientService;

  retrieveClient(id: string, resultHandler: (err: any, result: any) => any) : ClientService;

  retrieveClients(resultHandler: (err: any, result: Array<any>) => any) : ClientService;

  removeClient(client: any, resultHandler: (err: any, result: any) => any) : ClientService;
}