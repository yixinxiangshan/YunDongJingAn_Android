package com.ecloudiot.framework.widget.model;

import java.util.List;

public class TabModel {
	private List<TabItemModel> tabDataList;
	private List<BadgeItemModel> badgeDataList;
	
	public List<TabItemModel> getTabDataList() {
		return tabDataList;
	}

	public void setTabDataList(List<TabItemModel> tabDataList) {
		this.tabDataList = tabDataList;
	}

	public List<BadgeItemModel> getBadgeDataList() {
		return badgeDataList;
	}

	public void setBadgeDataList(List<BadgeItemModel> badgeDataList) {
		this.badgeDataList = badgeDataList;
	}

}
