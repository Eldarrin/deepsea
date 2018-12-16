package io.ensure.deepsea.ui.menu;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter for {@link io.ensure.deepsea.ui.menu.MenuItem}.
 * NOTE: This class has been automatically generated from the {@link io.ensure.deepsea.ui.menu.MenuItem} original class using Vert.x codegen.
 */
public class MenuItemConverter {

  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, MenuItem obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "available":
          if (member.getValue() instanceof Boolean) {
            obj.setAvailable((Boolean)member.getValue());
          }
          break;
        case "children":
          if (member.getValue() instanceof JsonArray) {
            java.util.ArrayList<java.lang.String> list =  new java.util.ArrayList<>();
            ((Iterable<Object>)member.getValue()).forEach( item -> {
              if (item instanceof String)
                list.add((String)item);
            });
            obj.setChildren(list);
          }
          break;
        case "childrenMenuItems":
          if (member.getValue() instanceof JsonArray) {
            java.util.ArrayList<io.ensure.deepsea.ui.menu.MenuItem> list =  new java.util.ArrayList<>();
            ((Iterable<Object>)member.getValue()).forEach( item -> {
              if (item instanceof JsonObject)
                list.add(new io.ensure.deepsea.ui.menu.MenuItem((JsonObject)item));
            });
            obj.setChildrenMenuItems(list);
          }
          break;
        case "menuId":
          if (member.getValue() instanceof String) {
            obj.setMenuId((String)member.getValue());
          }
          break;
        case "name":
          if (member.getValue() instanceof String) {
            obj.setName((String)member.getValue());
          }
          break;
        case "parent":
          if (member.getValue() instanceof String) {
            obj.setParent((String)member.getValue());
          }
          break;
        case "parentMenuItem":
          if (member.getValue() instanceof JsonObject) {
            obj.setParentMenuItem(new io.ensure.deepsea.ui.menu.MenuItem((JsonObject)member.getValue()));
          }
          break;
        case "serviceName":
          if (member.getValue() instanceof String) {
            obj.setServiceName((String)member.getValue());
          }
          break;
        case "url":
          if (member.getValue() instanceof String) {
            obj.setUrl((String)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(MenuItem obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(MenuItem obj, java.util.Map<String, Object> json) {
    json.put("available", obj.isAvailable());
    if (obj.getChildren() != null) {
      JsonArray array = new JsonArray();
      obj.getChildren().forEach(item -> array.add(item));
      json.put("children", array);
    }
    if (obj.getChildrenMenuItems() != null) {
      JsonArray array = new JsonArray();
      obj.getChildrenMenuItems().forEach(item -> array.add(item.toJson()));
      json.put("childrenMenuItems", array);
    }
    if (obj.getMenuId() != null) {
      json.put("menuId", obj.getMenuId());
    }
    if (obj.getName() != null) {
      json.put("name", obj.getName());
    }
    if (obj.getParent() != null) {
      json.put("parent", obj.getParent());
    }
    if (obj.getParentMenuItem() != null) {
      json.put("parentMenuItem", obj.getParentMenuItem().toJson());
    }
    if (obj.getServiceName() != null) {
      json.put("serviceName", obj.getServiceName());
    }
    if (obj.getUrl() != null) {
      json.put("url", obj.getUrl());
    }
  }
}
