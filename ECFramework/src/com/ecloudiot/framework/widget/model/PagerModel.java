package com.ecloudiot.framework.widget.model;


public class PagerModel {
	private int pagerCount;
	private String itemPageName;
	private int offset;
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public int getPagerCount() {
		return pagerCount;
	}
	public void setPagerCount(int pagerCount) {
		this.pagerCount = pagerCount;
	}
	public String getItemPageName() {
		return itemPageName;
	}
	public void setItemPageName(String itemPageName) {
		this.itemPageName = itemPageName;
	}
	// ArrayList<PagerItemModel> pagerList;
	// public ArrayList<PagerItemModel> getPagerList() {
	// return pagerList;
	// }
	// public void setPagerList(ArrayList<PagerItemModel> pagerList) {
	// this.pagerList = pagerList;
	// }
}
