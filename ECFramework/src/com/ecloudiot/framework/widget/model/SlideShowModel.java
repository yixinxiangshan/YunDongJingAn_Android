package com.ecloudiot.framework.widget.model;

import java.util.List;

public class SlideShowModel {
	private static final long serialVersionUID = 4140010476873687658L;
	private String actionType;
	private int currentPosition = 0;
	public List<SlideShowItemModel> itemList;
	
	public List<SlideShowItemModel> getItemList() {
		return itemList;
	}

	public void setItemList(List<SlideShowItemModel> list) {
		this.itemList = list;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}
}
