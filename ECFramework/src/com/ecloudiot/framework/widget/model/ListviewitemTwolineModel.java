package com.ecloudiot.framework.widget.model;

public class ListviewitemTwolineModel extends ItemNewsModel{
	private int numberDefault = 1;
	private int numPickerMax = 0;
	private String numChangeTag;
	private boolean withBt = true;
	private String controlId;
	private String videoUrl;
	private boolean showIcon;
	private String icon;
	private String leftIcon;
	private String sortKey;//排序字母

	public String getLeftIcon() {
		return leftIcon;
	}
	public void setLeftIcon(String leftIcon) {
		this.leftIcon = leftIcon;
	}
	public int getNumberDefault() {
		return numberDefault;
	}
	public void setNumberDefault(int numberDefault) {
		this.numberDefault = numberDefault;
	}
	public boolean isWithBt() {
		return withBt;
	}
	public void setWithBt(boolean withBt) {
		this.withBt = withBt;
	}
	public String getNumChangeTag() {
		return numChangeTag;
	}
	public void setNumChangeTag(String numChangeTag) {
		this.numChangeTag = numChangeTag;
	}
	public String getControlId() {
		return controlId;
	}
	public void setControlId(String controlId) {
		this.controlId = controlId;
	}
	public String getVideoUrl() {
		return videoUrl;
	}
	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	public int getNumPickerMax() {
		return numPickerMax;
	}
	public void setNumPickerMax(int numPickerMax) {
		this.numPickerMax = numPickerMax;
	}
	public boolean isShowIcon() {
		return showIcon;
	}
	public void setShowIcon(boolean showIcon) {
		this.showIcon = showIcon;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getSortKey() {
		return sortKey;
	}
	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}
}
