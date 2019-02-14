package io.ensure.deepsea.ui.menu;

import java.util.List;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class MenuItem {

	private String menuId;
	private int menuPos;
	private String menuName;
	private String navLink;
	private String menuPage;
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
		this.menuName = menuItem.menuName;
		this.navLink = menuItem.navLink;
		this.menuPage = menuItem.menuPage;
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

	public String getMenuName() {
		return menuName;
	}

	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public String getNavLink() {
		return navLink;
	}

	public void setNavLink(String navLink) {
		this.navLink = navLink;
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

		return menuName.equals(menuItem.menuName) && menuId.equals(menuItem.menuId);
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

	public String getMenuPage() {
		return menuPage;
	}

	public void setMenuPage(String menuPage) {
		this.menuPage = menuPage;
	}

	public int getMenuPos() {
		return menuPos;
	}

	public void setMenuPos(int menuPos) {
		this.menuPos = menuPos;
	}
}
