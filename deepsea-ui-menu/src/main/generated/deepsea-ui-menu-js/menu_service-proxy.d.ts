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
export default class MenuService {

  constructor (eb: any, address: string);

  initializePersistence(resultHandler: (err: any, result: any) => any) : MenuService;

  addMenu(menuItem: any, resultHandler: (err: any, result: any) => any) : MenuService;

  changeMenuState(menuItem: any, resultHandler: (err: any, result: any) => any) : MenuService;

  retrieveSubMenu(id: string, resultHandler: (err: any, result: any) => any) : MenuService;

  retrieveMenuChildren(parentID: string, resultHandler: (err: any, result: Array<any>) => any) : MenuService;

  retrieveMenu(id: string, resultHandler: (err: any, result: any) => any) : MenuService;
}