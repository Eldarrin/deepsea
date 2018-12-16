package io.ensure.deepsea.admin.enrolment.models;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link io.ensure.deepsea.admin.enrolment.models.Device}.
 * NOTE: This class has been automatically generated from the {@link io.ensure.deepsea.admin.enrolment.models.Device} original class using Vert.x codegen.
 */
public class DeviceConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, Device obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "manufacturer":
          if (member.getValue() instanceof String) {
            obj.setManufacturer((String)member.getValue());
          }
          break;
        case "model":
          if (member.getValue() instanceof String) {
            obj.setModel((String)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(Device obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(Device obj, java.util.Map<String, Object> json) {
    if (obj.getManufacturer() != null) {
      json.put("manufacturer", obj.getManufacturer());
    }
    if (obj.getModel() != null) {
      json.put("model", obj.getModel());
    }
  }
}
