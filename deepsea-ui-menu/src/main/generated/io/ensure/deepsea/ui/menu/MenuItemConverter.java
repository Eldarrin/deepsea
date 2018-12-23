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

package io.ensure.deepsea.ui.menu;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;

/**
 * Converter for {@link io.ensure.deepsea.ui.menu.MenuItem}.
 *
 * NOTE: This class has been automatically generated from the {@link io.ensure.deepsea.ui.menu.MenuItem} original class using Vert.x codegen.
 */
public class MenuItemConverter {

  public static void fromJson(JsonObject json, MenuItem obj) {
    if (json.getValue("available") instanceof Boolean) {
      obj.setAvailable((Boolean)json.getValue("available"));
    }
    if (json.getValue("children") instanceof JsonArray) {
      java.util.ArrayList<java.lang.String> list = new java.util.ArrayList<>();
      json.getJsonArray("children").forEach( item -> {
        if (item instanceof String)
          list.add((String)item);
      });
      obj.setChildren(list);
    }
    if (json.getValue("childrenMenuItems") instanceof JsonArray) {
      java.util.ArrayList<io.ensure.deepsea.ui.menu.MenuItem> list = new java.util.ArrayList<>();
      json.getJsonArray("childrenMenuItems").forEach( item -> {
        if (item instanceof JsonObject)
          list.add(new io.ensure.deepsea.ui.menu.MenuItem((JsonObject)item));
      });
      obj.setChildrenMenuItems(list);
    }
    if (json.getValue("menuId") instanceof String) {
      obj.setMenuId((String)json.getValue("menuId"));
    }
    if (json.getValue("name") instanceof String) {
      obj.setName((String)json.getValue("name"));
    }
    if (json.getValue("parent") instanceof String) {
      obj.setParent((String)json.getValue("parent"));
    }
    if (json.getValue("parentMenuItem") instanceof JsonObject) {
      obj.setParentMenuItem(new io.ensure.deepsea.ui.menu.MenuItem((JsonObject)json.getValue("parentMenuItem")));
    }
    if (json.getValue("serviceName") instanceof String) {
      obj.setServiceName((String)json.getValue("serviceName"));
    }
    if (json.getValue("url") instanceof String) {
      obj.setUrl((String)json.getValue("url"));
    }
  }

  public static void toJson(MenuItem obj, JsonObject json) {
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