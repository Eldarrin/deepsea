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

package io.ensure.deepsea.actuarial.bordereau;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link io.ensure.deepsea.actuarial.bordereau.BordereauLine}.
 *
 * NOTE: This class has been automatically generated from the {@link io.ensure.deepsea.actuarial.bordereau.BordereauLine} original class using Vert.x codegen.
 */
public class BordereauLineConverter {

  public static void fromJson(JsonObject json, BordereauLine obj) {
    if (json.getValue("bordereauLineId") instanceof String) {
      obj.setBordereauLineId((String)json.getValue("bordereauLineId"));
    }
    if (json.getValue("clientId") instanceof String) {
      obj.setClientId((String)json.getValue("clientId"));
    }
    if (json.getValue("customerName") instanceof String) {
      obj.setCustomerName((String)json.getValue("customerName"));
    }
    if (json.getValue("event") instanceof String) {
      obj.setEvent(io.ensure.deepsea.actuarial.bordereau.BordereauEvent.valueOf((String)json.getValue("event")));
    }
    if (json.getValue("ipt") instanceof Number) {
      obj.setIpt(((Number)json.getValue("ipt")).doubleValue());
    }
    if (json.getValue("source") instanceof String) {
      obj.setSource((String)json.getValue("source"));
    }
    if (json.getValue("sourceId") instanceof String) {
      obj.setSourceId((String)json.getValue("sourceId"));
    }
    if (json.getValue("value") instanceof Number) {
      obj.setValue(((Number)json.getValue("value")).doubleValue());
    }
  }

  public static void toJson(BordereauLine obj, JsonObject json) {
    if (obj.getBordereauLineId() != null) {
      json.put("bordereauLineId", obj.getBordereauLineId());
    }
    if (obj.getClientId() != null) {
      json.put("clientId", obj.getClientId());
    }
    if (obj.getCustomerName() != null) {
      json.put("customerName", obj.getCustomerName());
    }
    if (obj.getEvent() != null) {
      json.put("event", obj.getEvent().name());
    }
    json.put("ipt", obj.getIpt());
    if (obj.getSource() != null) {
      json.put("source", obj.getSource());
    }
    if (obj.getSourceId() != null) {
      json.put("sourceId", obj.getSourceId());
    }
    json.put("value", obj.getValue());
  }
}