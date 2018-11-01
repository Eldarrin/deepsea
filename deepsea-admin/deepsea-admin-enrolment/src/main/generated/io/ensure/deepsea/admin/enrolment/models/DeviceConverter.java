/*
 * Copyright (c) 2014 Red Hat, Inc. and others
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

package io.ensure.deepsea.admin.enrolment.models;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link io.ensure.deepsea.admin.enrolment.models.Device}.
 *
 * NOTE: This class has been automatically generated from the {@link io.ensure.deepsea.admin.enrolment.models.Device} original class using Vert.x codegen.
 */
public class DeviceConverter {

  public static void fromJson(JsonObject json, Device obj) {
    if (json.getValue("manufacturer") instanceof String) {
      obj.setManufacturer((String)json.getValue("manufacturer"));
    }
    if (json.getValue("model") instanceof String) {
      obj.setModel((String)json.getValue("model"));
    }
  }

  public static void toJson(Device obj, JsonObject json) {
    if (obj.getManufacturer() != null) {
      json.put("manufacturer", obj.getManufacturer());
    }
    if (obj.getModel() != null) {
      json.put("model", obj.getModel());
    }
  }
}