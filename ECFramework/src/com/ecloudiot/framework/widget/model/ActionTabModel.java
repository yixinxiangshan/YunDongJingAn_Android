package com.ecloudiot.framework.widget.model;

import java.util.List;

public class ActionTabModel {
	private boolean collapseBar;
	private List<ActionTabItemModel> tabList;

	public List<ActionTabItemModel> getTabList() {
		return tabList;
	}

	public void setTabList(List<ActionTabItemModel> tabList) {
		this.tabList = tabList;
	}

	public boolean isCollapseBar() {
		return collapseBar;
	}

	public void setCollapseBar(boolean collapseBar) {
		this.collapseBar = collapseBar;
	}
}
