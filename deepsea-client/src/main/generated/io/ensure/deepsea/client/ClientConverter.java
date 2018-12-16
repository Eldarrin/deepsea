package io.ensure.deepsea.client;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link io.ensure.deepsea.client.Client}.
 * NOTE: This class has been automatically generated from the {@link io.ensure.deepsea.client.Client} original class using Vert.x codegen.
 */
public class ClientConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, Client obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "clientId":
          if (member.getValue() instanceof String) {
            obj.setClientId((String)member.getValue());
          }
          break;
        case "clientName":
          if (member.getValue() instanceof String) {
            obj.setClientName((String)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(Client obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(Client obj, java.util.Map<String, Object> json) {
    if (obj.getClientId() != null) {
      json.put("clientId", obj.getClientId());
    }
    if (obj.getClientName() != null) {
      json.put("clientName", obj.getClientName());
    }
  }
}
