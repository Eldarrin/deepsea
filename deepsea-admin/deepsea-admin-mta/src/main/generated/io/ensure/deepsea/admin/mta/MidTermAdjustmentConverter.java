package io.ensure.deepsea.admin.mta;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link io.ensure.deepsea.admin.mta.MidTermAdjustment}.
 * NOTE: This class has been automatically generated from the {@link io.ensure.deepsea.admin.mta.MidTermAdjustment} original class using Vert.x codegen.
 */
public class MidTermAdjustmentConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, MidTermAdjustment obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "eventDate":
          if (member.getValue() instanceof String) {
            obj.setEventDate(Instant.from(DateTimeFormatter.ISO_INSTANT.parse((String)member.getValue())));
          }
          break;
        case "ipt":
          if (member.getValue() instanceof Number) {
            obj.setIpt(((Number)member.getValue()).doubleValue());
          }
          break;
        case "mtaEvent":
          if (member.getValue() instanceof String) {
            obj.setMtaEvent(io.ensure.deepsea.admin.mta.MTAEvent.valueOf((String)member.getValue()));
          }
          break;
        case "mtaId":
          if (member.getValue() instanceof String) {
            obj.setMtaId((String)member.getValue());
          }
          break;
        case "newCoverage":
          if (member.getValue() instanceof Number) {
            obj.setNewCoverage(((Number)member.getValue()).doubleValue());
          }
          break;
        case "newRiskId":
          if (member.getValue() instanceof String) {
            obj.setNewRiskId((String)member.getValue());
          }
          break;
        case "newproductId":
          if (member.getValue() instanceof String) {
            obj.setNewproductId((String)member.getValue());
          }
          break;
        case "oldCoverage":
          if (member.getValue() instanceof Number) {
            obj.setOldCoverage(((Number)member.getValue()).doubleValue());
          }
          break;
        case "oldProductId":
          if (member.getValue() instanceof String) {
            obj.setOldProductId((String)member.getValue());
          }
          break;
        case "oldRiskId":
          if (member.getValue() instanceof String) {
            obj.setOldRiskId((String)member.getValue());
          }
          break;
        case "policyId":
          if (member.getValue() instanceof String) {
            obj.setPolicyId((String)member.getValue());
          }
          break;
        case "value":
          if (member.getValue() instanceof Number) {
            obj.setValue(((Number)member.getValue()).doubleValue());
          }
          break;
      }
    }
  }

  public static void toJson(MidTermAdjustment obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(MidTermAdjustment obj, java.util.Map<String, Object> json) {
    if (obj.getEventDate() != null) {
      json.put("eventDate", DateTimeFormatter.ISO_INSTANT.format(obj.getEventDate()));
    }
    json.put("ipt", obj.getIpt());
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
    json.put("value", obj.getValue());
  }
}
