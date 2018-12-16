package io.ensure.deepsea.actuarial.bordereau;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link io.ensure.deepsea.actuarial.bordereau.BordereauLine}.
 * NOTE: This class has been automatically generated from the {@link io.ensure.deepsea.actuarial.bordereau.BordereauLine} original class using Vert.x codegen.
 */
public class BordereauLineConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, BordereauLine obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "bordereauLineId":
          if (member.getValue() instanceof String) {
            obj.setBordereauLineId((String)member.getValue());
          }
          break;
        case "clientId":
          if (member.getValue() instanceof String) {
            obj.setClientId((String)member.getValue());
          }
          break;
        case "customerName":
          if (member.getValue() instanceof String) {
            obj.setCustomerName((String)member.getValue());
          }
          break;
        case "dateSourceCreated":
          if (member.getValue() instanceof String) {
            obj.setDateSourceCreated(Instant.from(DateTimeFormatter.ISO_INSTANT.parse((String)member.getValue())));
          }
          break;
        case "event":
          if (member.getValue() instanceof String) {
            obj.setEvent(io.ensure.deepsea.actuarial.bordereau.BordereauEvent.valueOf((String)member.getValue()));
          }
          break;
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
        case "source":
          if (member.getValue() instanceof String) {
            obj.setSource((String)member.getValue());
          }
          break;
        case "sourceId":
          if (member.getValue() instanceof String) {
            obj.setSourceId((String)member.getValue());
          }
          break;
        case "startDate":
          if (member.getValue() instanceof String) {
            obj.setStartDate(Instant.from(DateTimeFormatter.ISO_INSTANT.parse((String)member.getValue())));
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

  public static void toJson(BordereauLine obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(BordereauLine obj, java.util.Map<String, Object> json) {
    if (obj.getBordereauLineId() != null) {
      json.put("bordereauLineId", obj.getBordereauLineId());
    }
    if (obj.getClientId() != null) {
      json.put("clientId", obj.getClientId());
    }
    if (obj.getCustomerName() != null) {
      json.put("customerName", obj.getCustomerName());
    }
    if (obj.getDateSourceCreated() != null) {
      json.put("dateSourceCreated", DateTimeFormatter.ISO_INSTANT.format(obj.getDateSourceCreated()));
    }
    if (obj.getEvent() != null) {
      json.put("event", obj.getEvent().name());
    }
    if (obj.getEventDate() != null) {
      json.put("eventDate", DateTimeFormatter.ISO_INSTANT.format(obj.getEventDate()));
    }
    json.put("ipt", obj.getIpt());
    if (obj.getSource() != null) {
      json.put("source", obj.getSource());
    }
    if (obj.getSourceId() != null) {
      json.put("sourceId", obj.getSourceId());
    }
    if (obj.getStartDate() != null) {
      json.put("startDate", DateTimeFormatter.ISO_INSTANT.format(obj.getStartDate()));
    }
    json.put("value", obj.getValue());
  }
}
