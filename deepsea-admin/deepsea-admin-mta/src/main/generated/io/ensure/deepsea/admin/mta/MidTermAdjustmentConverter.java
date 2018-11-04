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

package io.ensure.deepsea.admin.mta;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link io.ensure.deepsea.admin.mta.MidTermAdjustment}.
 *
 * NOTE: This class has been automatically generated from the {@link io.ensure.deepsea.admin.mta.MidTermAdjustment} original class using Vert.x codegen.
 */
public class MidTermAdjustmentConverter {

  public static void fromJson(JsonObject json, MidTermAdjustment obj) {
    if (json.getValue("mtaEvent") instanceof String) {
      obj.setMtaEvent(io.ensure.deepsea.admin.mta.MTAEvent.valueOf((String)json.getValue("mtaEvent")));
    }
    if (json.getValue("mtaId") instanceof Number) {
      obj.setMtaId(((Number)json.getValue("mtaId")).intValue());
    }
    if (json.getValue("newCoverage") instanceof Number) {
      obj.setNewCoverage(((Number)json.getValue("newCoverage")).doubleValue());
    }
    if (json.getValue("newRiskId") instanceof String) {
      obj.setNewRiskId((String)json.getValue("newRiskId"));
    }
    if (json.getValue("newproductId") instanceof String) {
      obj.setNewproductId((String)json.getValue("newproductId"));
    }
    if (json.getValue("oldCoverage") instanceof Number) {
      obj.setOldCoverage(((Number)json.getValue("oldCoverage")).doubleValue());
    }
    if (json.getValue("oldProductId") instanceof String) {
      obj.setOldProductId((String)json.getValue("oldProductId"));
    }
    if (json.getValue("oldRiskId") instanceof String) {
      obj.setOldRiskId((String)json.getValue("oldRiskId"));
    }
    if (json.getValue("policyId") instanceof String) {
      obj.setPolicyId((String)json.getValue("policyId"));
    }
  }

  public static void toJson(MidTermAdjustment obj, JsonObject json) {
    if (obj.getMtaEvent() != null) {
      json.put("mtaEvent", obj.getMtaEvent().name());
    }
    if (obj.getMtaId() != null) {
      json.put("mtaId", obj.getMtaId());
    }
    json.put("newCoverage", obj.getNewCoverage());
    if (obj.getNewRiskId() != null) {
      json.put("newRiskId", obj.getNewRiskId());
    }
    if (obj.getNewproductId() != null) {
      json.put("newproductId", obj.getNewproductId());
    }
    json.put("oldCoverage", obj.getOldCoverage());
    if (obj.getOldProductId() != null) {
      json.put("oldProductId", obj.getOldProductId());
    }
    if (obj.getOldRiskId() != null) {
      json.put("oldRiskId", obj.getOldRiskId());
    }
    if (obj.getPolicyId() != null) {
      json.put("policyId", obj.getPolicyId());
    }
  }
}