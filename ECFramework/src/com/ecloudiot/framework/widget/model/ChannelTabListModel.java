package com.ecloudiot.framework.widget.model;

import java.util.ArrayList;


public class ChannelTabListModel {
	private int pagerCount;
	private ArrayList<ChannelTabItemModel> channelList;

	public int getPagerCount() {
		return pagerCount;
	}

	public void setPagerCount(int pagerCount) {
		this.pagerCount = pagerCount;
	}

	public ArrayList<ChannelTabItemModel> getChannelList() {
		return channelList;
	}

	public void setChannelList(ArrayList<ChannelTabItemModel> channelList) {
		this.channelList = channelList;
	}
	
	
}
