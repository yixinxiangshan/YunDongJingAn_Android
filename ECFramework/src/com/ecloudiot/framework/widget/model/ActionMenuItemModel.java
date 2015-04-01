package com.ecloudiot.framework.widget.model;

public class ActionMenuItemModel {
	private String itemId; //value: must from ids of res
	private String clickTag;
	private String actionViewName;
	private boolean neverCollapses;
	private String title;
	private String iconName;
	private String showAsAction; //value: always|never|ifroom
	
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public String getClickTag() {
		return clickTag;
	}
	public void setClickTag(String clickTag) {
		this.clickTag = clickTag;
	}
	public String getActionViewName() {
		return actionViewName;
	}
	public void setActionViewName(String actionViewName) {
		this.actionViewName = actionViewName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIconName() {
		return iconName;
	}
	public void setIconName(String iconName) {
		this.iconName = iconName;
	}
	public boolean isNeverCollapses() {
		return neverCollapses;
	}
	public void setNeverCollapses(boolean neverCollapses) {
		this.neverCollapses = neverCollapses;
	}
	public String getShowAsAction() {
		return showAsAction;
	}
	public void setShowAsAction(String showAsAction) {
		this.showAsAction = showAsAction;
	}
	
}
