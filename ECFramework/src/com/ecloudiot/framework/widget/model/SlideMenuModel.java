package com.ecloudiot.framework.widget.model;



public class SlideMenuModel {

	private int mode;
	private int firstShow;
	private String firstFragment;
	private String firstTag;

	public String getFirstFragment() {
		return firstFragment;
	}

	public void setFirstFragment(String firstFragment) {
		this.firstFragment = firstFragment;
	}

	public String getFirstTag() {
		return firstTag;
	}

	public void setFirstTag(String firstTag) {
		this.firstTag = firstTag;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getFirstShow() {
		return firstShow;
	}

	public void setFirstShow(int firstShow) {
		this.firstShow = firstShow;
	}
}
