package com.ecloudiot.framework.widget.model;

public class PagerItemModel {
	private String pageName;
	private String tag;
	private String title;
	private int textColor;
	private int bgColor;
	private int bgImage;
	private int textSize;

	public String getpageName() {
		return pageName;
	}
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public int getBgColor() {
		return bgColor;
	}

	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
	}

	public int getBgImage() {
		return bgImage;
	}

	public void setBgImage(int bgImage) {
		this.bgImage = bgImage;
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
}
