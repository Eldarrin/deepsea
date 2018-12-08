package io.ensure.deepsea.ui.menu;

import java.util.List;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class MenuItem {

	private String menuId;
	private String name;
	private String url;
	private String parent;
	private List<String> children;
	private MenuItem parentMenuItem;
	private List<MenuItem> childrenMenuItems;
	private boolean isAvailable;
	private String serviceName;
	
	public MenuItem() {
		
	}
	
	public MenuItem(MenuItem menuItem) {
		this.menuId = menuItem.menuId;
		this.name = menuItem.name;
		this.url = menuItem.url;
		this.parentMenuItem = menuItem.parentMenuItem;
		this.childrenMenuItems = menuItem.childrenMenuItems;
		this.parent = menuItem.parent;
		this.children = menuItem.children;
	}

	public String getMenuId() {
		return menuId;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
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
	
	public boolean hasChildren() {
		try {
			return (!childrenMenuItems.isEmpty());
		} catch (NullPointerException n) {
			return false;
		}
	}

	public MenuItem(JsonObject json) {
		MenuItemConverter.fromJson(json, this);
		if (json.containsKey("_id") && !json.containsKey("menuId")) {
			this.menuId = "menu-" + json.getString("_id");
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

		return name.equals(menuItem.name) && menuId.equals(menuItem.menuId);
	}

	@Override
	public int hashCode() {
		int result = menuId.hashCode();
		result = 31 * result + menuId.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return this.toJson().encodePrettily();
	}

	public List<String> getChildren() {
		return children;
	}

	public void setChildren(List<String> children) {
		this.children = children;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	
}
