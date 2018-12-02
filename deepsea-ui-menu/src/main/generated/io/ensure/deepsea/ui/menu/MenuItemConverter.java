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
    if (json.getValue("childrenMenuItems") instanceof JsonArray) {
      java.util.ArrayList<io.ensure.deepsea.ui.menu.MenuItem> list = new java.util.ArrayList<>();
      json.getJsonArray("childrenMenuItems").forEach( item -> {
        if (item instanceof JsonObject)
          list.add(new io.ensure.deepsea.ui.menu.MenuItem((JsonObject)item));
      });
      obj.setChildrenMenuItems(list);
    }
    if (json.getValue("menuItemId") instanceof String) {
      obj.setMenuItemId((String)json.getValue("menuItemId"));
    }
    if (json.getValue("name") instanceof String) {
      obj.setName((String)json.getValue("name"));
    }
    if (json.getValue("parentMenuItem") instanceof JsonObject) {
      obj.setParentMenuItem(new io.ensure.deepsea.ui.menu.MenuItem((JsonObject)json.getValue("parentMenuItem")));
    }
    if (json.getValue("url") instanceof String) {
      obj.setUrl((String)json.getValue("url"));
    }
  }

  public static void toJson(MenuItem obj, JsonObject json) {
    if (obj.getChildrenMenuItems() != null) {
      JsonArray array = new JsonArray();
      obj.getChildrenMenuItems().forEach(item -> array.add(item.toJson()));
      json.put("childrenMenuItems", array);
    }
    if (obj.getMenuItemId() != null) {
      json.put("menuItemId", obj.getMenuItemId());
    }
    if (obj.getName() != null) {
      json.put("name", obj.getName());
    }
    if (obj.getParentMenuItem() != null) {
      json.put("parentMenuItem", obj.getParentMenuItem().toJson());
    }
    if (obj.getUrl() != null) {
      json.put("url", obj.getUrl());
    }
  }
}