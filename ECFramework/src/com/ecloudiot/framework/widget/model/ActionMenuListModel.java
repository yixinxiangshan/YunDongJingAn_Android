package com.ecloudiot.framework.widget.model;

import java.util.ArrayList;

public class ActionMenuListModel {
	private boolean collapBar;
	private ArrayList<ActionMenuItemModel> menuList;
	
	public ArrayList<ActionMenuItemModel> getMenuList() {
		return menuList;
	}

	public void setMenuList(ArrayList<ActionMenuItemModel> menuList) {
		this.menuList = menuList;
	}

	public boolean isCollapBar() {
		return collapBar;
	}

	public void setCollapBar(boolean collapBar) {
		this.collapBar = collapBar;
	}
}
