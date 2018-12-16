package io.ensure.deepsea.admin.enrolment.models;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link io.ensure.deepsea.admin.enrolment.models.Enrolment}.
 * NOTE: This class has been automatically generated from the {@link io.ensure.deepsea.admin.enrolment.models.Enrolment} original class using Vert.x codegen.
 */
public class EnrolmentConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, Enrolment obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "agreeTerms":
          if (member.getValue() instanceof Boolean) {
            obj.setAgreeTerms((Boolean)member.getValue());
          }
          break;
        case "clientId":
          if (member.getValue() instanceof String) {
            obj.setClientId((String)member.getValue());
          }
          break;
        case "dateCreated":
          if (member.getValue() instanceof String) {
            obj.setDateCreated(Instant.from(DateTimeFormatter.ISO_INSTANT.parse((String)member.getValue())));
          }
          break;
        case "dateOfBirth":
          if (member.getValue() instanceof String) {
            obj.setDateOfBirth(Instant.from(DateTimeFormatter.ISO_INSTANT.parse((String)member.getValue())));
          }
          break;
        case "devices":
          if (member.getValue() instanceof JsonArray) {
            java.util.ArrayList<io.ensure.deepsea.admin.enrolment.models.Device> list =  new java.util.ArrayList<>();
            ((Iterable<Object>)member.getValue()).forEach( item -> {
              if (item instanceof JsonObject)
                list.add(new io.ensure.deepsea.admin.enrolment.models.Device((JsonObject)item));
            });
            obj.setDevices(list);
          }
          break;
        case "email":
          if (member.getValue() instanceof String) {
            obj.setEmail((String)member.getValue());
          }
          break;
        case "enrolmentId":
          if (member.getValue() instanceof String) {
            obj.setEnrolmentId((String)member.getValue());
          }
          break;
        case "firstName":
          if (member.getValue() instanceof String) {
            obj.setFirstName((String)member.getValue());
          }
          break;
        case "grossPremium":
          if (member.getValue() instanceof Number) {
            obj.setGrossPremium(((Number)member.getValue()).doubleValue());
          }
          break;
        case "ipt":
          if (member.getValue() instanceof Number) {
            obj.setIpt(((Number)member.getValue()).doubleValue());
          }
          break;
        case "lastName":
          if (member.getValue() instanceof String) {
            obj.setLastName((String)member.getValue());
          }
          break;
        case "middleNames":
          if (member.getValue() instanceof String) {
            obj.setMiddleNames((String)member.getValue());
          }
          break;
        case "productId":
          if (member.getValue() instanceof String) {
            obj.setProductId((String)member.getValue());
          }
          break;
        case "startDate":
          if (member.getValue() instanceof String) {
            obj.setStartDate(Instant.from(DateTimeFormatter.ISO_INSTANT.parse((String)member.getValue())));
          }
          break;
        case "title":
          if (member.getValue() instanceof String) {
            obj.setTitle((String)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(Enrolment obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(Enrolment obj, java.util.Map<String, Object> json) {
    json.put("agreeTerms", obj.isAgreeTerms());
    if (obj.getClientId() != null) {
      json.put("clientId", obj.getClientId());
    }
    if (obj.getDateCreated() != null) {
      json.put("dateCreated", DateTimeFormatter.ISO_INSTANT.format(obj.getDateCreated()));
    }
    if (obj.getDateOfBirth() != null) {
      json.put("dateOfBirth", DateTimeFormatter.ISO_INSTANT.format(obj.getDateOfBirth()));
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
    if (obj.getStartDate() != null) {
      json.put("startDate", DateTimeFormatter.ISO_INSTANT.format(obj.getStartDate()));
    }
    if (obj.getTitle() != null) {
      json.put("title", obj.getTitle());
    }
  }
}
