package com.ecloudiot.framework.widget.model;

import java.util.ArrayList;

public class GroupTableListModel {
	private ArrayList<GroupTableListItemModel> tableList;
	private String sectionTitle;

	public ArrayList<GroupTableListItemModel> getTableList() {
		return tableList;
	}

	public void setTableList(ArrayList<GroupTableListItemModel> tableList) {
		this.tableList = tableList;
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

	public void setSectionTitle(String sectionTitle) {
		this.sectionTitle = sectionTitle;
	}
}
