package com.ecloudiot.framework.widget.model;


public class GroupTableListItemModel {
	private String tag;
	private String title;
	private String summary;
	private String icon;
	private String id;
	private String customView;
	private String expand; 
	private String expand2;
	private String noteString;
	private int clickable = 1;
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}


	public String getCustomView() {
		return customView;
	}

	public void setCustomViewId(String customView) {
		this.customView = customView;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExpand() {
		return expand;
	}

	public void setExpand(String expand) {
		this.expand = expand;
	}
	
	public String getExpand2() {
		return expand2;
	}

	public void setExpand2(String expand2) {
		this.expand2 = expand2;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getClickable() {
		return clickable;
	}

	public void setClickable(int clickable) {
		this.clickable = clickable;
	}

	public String getNoteString() {
		return noteString;
	}

	public void setNoteString(String noteString) {
		this.noteString = noteString;
	}

}
