package com.ecloudiot.framework.widget.model;


public class DataPickerModel {
	private String selectMode;
	private int minDate;
	private int maxDate;

	public String getSelectMode() {
		return selectMode;
	}

	public void setSelectMode(String selectMode) {
		this.selectMode = selectMode;
	}

	public int getMinDate() {
		return minDate;
	}

	public void setMinDate(int minDate) {
		this.minDate = minDate;
	}

	public int getMaxDate() {
		return maxDate;
	}

	public void setMaxDate(int maxDate) {
		this.maxDate = maxDate;
	}

}
