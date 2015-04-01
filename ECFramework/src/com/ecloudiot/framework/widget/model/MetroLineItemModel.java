package com.ecloudiot.framework.widget.model;

import java.util.ArrayList;

public class MetroLineItemModel {
	private boolean isBigImage;
	
	private ArrayList<MetroItemModel> lineItem;
	
	public boolean isBigImage() {
		return isBigImage;
	}
	public void setBigImage(boolean isBigImage) {
		this.isBigImage = isBigImage;
	}
	public ArrayList<MetroItemModel> getLineItem() {
		return lineItem;
	}
	public void setLineItem(ArrayList<MetroItemModel> lineItem) {
		this.lineItem = lineItem;
	}

}
