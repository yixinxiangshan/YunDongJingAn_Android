package com.ecloudiot.framework.widget.model;

import java.util.ArrayList;

public class MultiViewpagerModel {
	private int pagerCount;
//	private String loadOverTag;
	private int defaultPager;
	private ArrayList<MultiViewpagerItemModel> itemList;
	
	public int getPagerCount() {
		return pagerCount;
	}
	public void setPagerCount(int pagerCount) {
		this.pagerCount = pagerCount;
	}
//	public String getLoadOverTag() {
//		return loadOverTag;
//	}
//	public void setLoadOverTag(String loadOverTag) {
//		this.loadOverTag = loadOverTag;
//	}
	
	public ArrayList<MultiViewpagerItemModel> getItemList() {
		return itemList;
	}
	public void setItemList(ArrayList<MultiViewpagerItemModel> itemList) {
		this.itemList = itemList;
	}
	
	public int getDefaultPager() {
		return defaultPager;
	}
	public void setDefaultPager(int defaultPager) {
		this.defaultPager = defaultPager;
	}

	public class MultiViewpagerItemModel{
		private String pageName;
		private String tag;

		public String getPageName() {
			return pageName;
		}

		public void setPageName(String pageName) {
			this.pageName = pageName;
		}

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		
	}
	
}


