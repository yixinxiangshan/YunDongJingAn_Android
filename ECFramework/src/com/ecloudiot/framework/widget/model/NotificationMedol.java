package com.ecloudiot.framework.widget.model;


public class NotificationMedol {
	private int notificationId;
	private int iconId;
	private String iconName;
	private String title;
	private String content;
	private String notiActivityTag;
	private String notiActivityName;
	
	public int getNotificationId() {
		return notificationId;
	}
	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}
	public int getIconId() {
		return iconId;
	}
	public void setIconId(int iconId) {
		this.iconId = iconId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getNotiActivityTag() {
		return notiActivityTag;
	}
	public void setNotiActivityTag(String notiActivityTag) {
		this.notiActivityTag = notiActivityTag;
	}
	
	public String getNotiActivityName() {
		return notiActivityName;
	}
	public void setNotiActivityName(String notiActivityName) {
		this.notiActivityName = notiActivityName;
	}
	public String getIconName() {
		return iconName;
	}
	public void setIconName(String iconName) {
		this.iconName = iconName;
	}
}
