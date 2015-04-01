package com.ecloudiot.framework.widget.model;

public class NumberPickerModel {
	private int defaultNum;
	private int maxNum;
	private int minNum;
	private String numChangeTag;
	private String mWrap;

	public int getDefaultNum() {
		return defaultNum;
	}

	public void setDefaultNum(int defaultNum) {
		this.defaultNum = defaultNum;
	}

	public int getMaxNum() {
		return maxNum;
	}

	public void setMaxNum(int maxNum) {
		this.maxNum = maxNum;
	}

	public int getMinNum() {
		return minNum;
	}

	public void setMinNum(int minNum) {
		this.minNum = minNum;
	}

	public String getNumChangeTag() {
		return numChangeTag;
	}

	public void setNumChangeTag(String numChangeTag) {
		this.numChangeTag = numChangeTag;
	}

	public String getmWrap() {
		return mWrap;
	}

	public void setmWrap(String mWrap) {
		this.mWrap = mWrap;
	}

}
