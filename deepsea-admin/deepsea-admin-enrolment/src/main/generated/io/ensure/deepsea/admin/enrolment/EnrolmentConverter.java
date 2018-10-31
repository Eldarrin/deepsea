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

package io.ensure.deepsea.admin.enrolment;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link io.ensure.deepsea.admin.enrolment.Enrolment}.
 *
 * NOTE: This class has been automatically generated from the {@link io.ensure.deepsea.admin.enrolment.Enrolment} original class using Vert.x codegen.
 */
public class EnrolmentConverter {

  public static void fromJson(JsonObject json, Enrolment obj) {
    if (json.getValue("clientId") instanceof String) {
      obj.setClientId((String)json.getValue("clientId"));
    }
    if (json.getValue("enrolmentId") instanceof Number) {
      obj.setEnrolmentId(((Number)json.getValue("enrolmentId")).intValue());
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
  }

  public static void toJson(Enrolment obj, JsonObject json) {
    if (obj.getClientId() != null) {
      json.put("clientId", obj.getClientId());
    }
    json.put("enrolmentId", obj.getEnrolmentId());
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
  }
}