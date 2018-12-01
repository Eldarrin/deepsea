package io.ensure.deepsea.ui.menu;

import java.util.List;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class MenuItem {

	private String menuItemId;
	private String name;
	private String url;
	private MenuItem parentMenuItem;
	private List<MenuItem> childrenMenuItems;

	public String getMenuItemId() {
		return menuItemId;
	}

	public void setMenuItemId(String menuItemId) {
		this.menuItemId = menuItemId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public MenuItem getParentMenuItem() {
		return parentMenuItem;
	}

	public void setParentMenuItem(MenuItem parentMenuItem) {
		this.parentMenuItem = parentMenuItem;
	}

	public List<MenuItem> getChildrenMenuItems() {
		return childrenMenuItems;
	}

	public void setChildrenMenuItems(List<MenuItem> childrenMenuItems) {
		this.childrenMenuItems = childrenMenuItems;
	}

	public MenuItem(JsonObject json) {
		MenuItemConverter.fromJson(json, this);
		if (json.containsKey("_id") && !json.containsKey("menuItemId")) {
			this.menuItemId = json.getString("_id");
		}
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		MenuItemConverter.toJson(this, json);
		return json;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		MenuItem menuItem = (MenuItem) o;

		return name.equals(menuItem.name) && menuItemId.equals(menuItem.menuItemId);
	}

	@Override
	public int hashCode() {
		int result = menuItemId.hashCode();
		result = 31 * result + menuItemId.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return this.toJson().encodePrettily();
	}
	
}
