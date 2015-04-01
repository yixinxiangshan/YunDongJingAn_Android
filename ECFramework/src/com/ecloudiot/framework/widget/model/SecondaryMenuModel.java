package com.ecloudiot.framework.widget.model;

import java.util.ArrayList;

public class SecondaryMenuModel {
	private ArrayList<MenuItemModel> menuList;

	public class MenuItemModel {
		private String id;
		private String title;
		private String subTitle;
		private String icon;
		private String extend;
		private Boolean selected = false;
		private ArrayList<MenuItemModel> menuList;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public Boolean getSelected() {
			return selected;
		}

		public void setSelected(Boolean selected) {
			this.selected = selected;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getSubTitle() {
			return subTitle;
		}

		public void setSubTitle(String subTitle) {
			this.subTitle = subTitle;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getExtend() {
			return extend;
		}

		public void setExtend(String extend) {
			this.extend = extend;
		}

		public ArrayList<MenuItemModel> getMenuList() {
			return menuList;
		}

		public void setMenuList(ArrayList<MenuItemModel> menuList) {
			this.menuList = menuList;
		}

	}

	public ArrayList<MenuItemModel> getMenulList() {
		return menuList;
	}
	
	public ArrayList<MenuItemModel> getSubMenu(int position) {
		ArrayList<MenuItemModel> menuList = getMenulList().get(position).getMenuList();
		if (menuList == null || menuList.size() <= 0) {
			// if sub menu list is null then set self to sub menuList
			menuList = new ArrayList<SecondaryMenuModel.MenuItemModel>();
			menuList.add(getMenulList().get(position));
			getMenulList().get(position).setMenuList(menuList);
		}
		return menuList;
	}

	public void setMenulList(ArrayList<MenuItemModel> menuList) {
		this.menuList = menuList;
	}

}
