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
 * Converter for {@link io.ensure.deepsea.admin.enrolment.models.Enrolment}.
 *
 * NOTE: This class has been automatically generated from the {@link io.ensure.deepsea.admin.enrolment.models.Enrolment} original class using Vert.x codegen.
 */
public class EnrolmentConverter {

  public static void fromJson(JsonObject json, Enrolment obj) {
    if (json.getValue("agreeTerms") instanceof Boolean) {
      obj.setAgreeTerms((Boolean)json.getValue("agreeTerms"));
    }
    if (json.getValue("clientId") instanceof String) {
      obj.setClientId((String)json.getValue("clientId"));
    }
    if (json.getValue("devices") instanceof JsonArray) {
      java.util.ArrayList<io.ensure.deepsea.admin.enrolment.models.Device> list = new java.util.ArrayList<>();
      json.getJsonArray("devices").forEach( item -> {
        if (item instanceof JsonObject)
          list.add(new io.ensure.deepsea.admin.enrolment.models.Device((JsonObject)item));
      });
      obj.setDevices(list);
    }
    if (json.getValue("email") instanceof String) {
      obj.setEmail((String)json.getValue("email"));
    }
    if (json.getValue("enrolmentId") instanceof String) {
      obj.setEnrolmentId((String)json.getValue("enrolmentId"));
    }
    if (json.getValue("firstName") instanceof String) {
      obj.setFirstName((String)json.getValue("firstName"));
    }
    if (json.getValue("grossPremium") instanceof Number) {
      obj.setGrossPremium(((Number)json.getValue("grossPremium")).doubleValue());
    }
    if (json.getValue("ipt") instanceof Number) {
      obj.setIpt(((Number)json.getValue("ipt")).doubleValue());
    }
    if (json.getValue("lastName") instanceof String) {
      obj.setLastName((String)json.getValue("lastName"));
    }
    if (json.getValue("middleNames") instanceof String) {
      obj.setMiddleNames((String)json.getValue("middleNames"));
    }
    if (json.getValue("productId") instanceof String) {
      obj.setProductId((String)json.getValue("productId"));
    }
    if (json.getValue("title") instanceof String) {
      obj.setTitle((String)json.getValue("title"));
    }
  }

  public static void toJson(Enrolment obj, JsonObject json) {
    json.put("agreeTerms", obj.isAgreeTerms());
    if (obj.getClientId() != null) {
      json.put("clientId", obj.getClientId());
    }
    if (obj.getDevices() != null) {
      JsonArray array = new JsonArray();
      obj.getDevices().forEach(item -> array.add(item.toJson()));
      json.put("devices", array);
    }
    if (obj.getEmail() != null) {
      json.put("email", obj.getEmail());
    }
    if (obj.getEnrolmentId() != null) {
      json.put("enrolmentId", obj.getEnrolmentId());
    }
    if (obj.getFirstName() != null) {
      json.put("firstName", obj.getFirstName());
    }
    json.put("grossPremium", obj.getGrossPremium());
    json.put("ipt", obj.getIpt());
    if (obj.getLastName() != null) {
      json.put("lastName", obj.getLastName());
    }
    if (obj.getMiddleNames() != null) {
      json.put("middleNames", obj.getMiddleNames());
    }
    if (obj.getProductId() != null) {
      json.put("productId", obj.getProductId());
    }
    if (obj.getTitle() != null) {
      json.put("title", obj.getTitle());
    }
  }
}