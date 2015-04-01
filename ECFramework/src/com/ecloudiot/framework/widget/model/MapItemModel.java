package com.ecloudiot.framework.widget.model;


public class MapItemModel {
	private String id;
	private String number;
	private String title;
	private String latitude ;
	private String longitude;
	private String address;
	private String popupItemString;
	private String image;
	private String cmsSortId;
	private String markIcon;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPopupItemString() {
		return popupItemString;
	}
	public void setPopupItemString(String popupItemString) {
		this.popupItemString = popupItemString;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getCmsSortId() {
		return cmsSortId;
	}
	public void setCmsSortId(String cmsSortId) {
		this.cmsSortId = cmsSortId;
	}
	public String getMarkIcon() {
		return markIcon;
	}
	public void setMarkIcon(String markIcon) {
		this.markIcon = markIcon;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
}
