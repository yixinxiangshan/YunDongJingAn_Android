package com.ecloudiot.framework.widget.model;

public class ActionBarModel {
	private boolean withActionBar = true;
	private boolean withHomeItem = true;
	private String actionBarBg;
	private String homeIcon;
	private String title;
	private String titleColor;
	private String titleIcon;
	private String subTitle;
	private boolean withHomeAsUp = true;
	private String homeClickTag;
	private ActionTabModel tabData;
	private ActionMenuListModel menuItemsData;
	private ActionNavigationListModel navTagData;
	

	public String getTitleColor() {
		return titleColor;
	}
	public void setTitleColor(String titleColor) {
		this.titleColor = titleColor;
	}
	public boolean isWithActionBar() {
		return withActionBar;
	}
	public void setWithActionBar(boolean withActionBar) {
		this.withActionBar = withActionBar;
	}
	public boolean isWithHomeItem() {
		return withHomeItem;
	}
	public void setWithHomeItem(boolean withHomeItem) {
		this.withHomeItem = withHomeItem;
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
	public boolean isWithHomeAsUp() {
		return withHomeAsUp;
	}
	public void setWithHomeAsUp(boolean withHomeAsUp) {
		this.withHomeAsUp = withHomeAsUp;
	}
	public String getHomeClickTag() {
		return homeClickTag;
	}
	public void setHomeClickTag(String homeClickTag) {
		this.homeClickTag = homeClickTag;
	}
	public ActionMenuListModel getMenuItemsData() {
		return menuItemsData;
	}
	public void setMenuItemsData(ActionMenuListModel menuItemsData) {
		this.menuItemsData = menuItemsData;
	}
	public ActionNavigationListModel getNavTagData() {
		return navTagData;
	}
	public void setNavTagData(ActionNavigationListModel navTagData) {
		this.navTagData = navTagData;
	}
	public String getHomeIcon() {
		return homeIcon;
	}
	public void setHomeIcon(String homeIcon) {
		this.homeIcon = homeIcon;
	}
	public String getActionBarBg() {
		return actionBarBg;
	}
	public void setActionBarBg(String actionBarBg) {
		this.actionBarBg = actionBarBg;
	}
	public ActionTabModel getTabData() {
		return tabData;
	}
	public void setTabData(ActionTabModel tabData) {
		this.tabData = tabData;
	}

	public String getTitleIcon() {
		return titleIcon;
	}
	public void setTitleIcon(String titleIcon) {
		this.titleIcon = titleIcon;
	}
}
