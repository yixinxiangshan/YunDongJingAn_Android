package com.ecloudiot.framework.widget.model;

public class TabItemModel {
	private String id;
	private String title;
	private String icon;
	private String tag;
	private String fragmentName;
	private String fragmentString;
	private String badgeText;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getFragmentName() {
		return fragmentName;
	}
	public void setFragmentName(String fragmentName) {
		this.fragmentName = fragmentName;
	}
	public String getFragmentString() {
		return fragmentString;
	}
	public void setFragmentString(String fragmentString) {
		this.fragmentString = fragmentString;
	}
	public String getBadgeText() {
		return badgeText;
	}
	public void setBadgeText(String badgeText) {
		this.badgeText = badgeText;
	}
}
