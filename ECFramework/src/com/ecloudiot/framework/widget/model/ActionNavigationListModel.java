package com.ecloudiot.framework.widget.model;

import java.util.List;

public class ActionNavigationListModel {
	private List<ActionNavigationItemModel> navTagList;
	private int	selectedItem;

	public List<ActionNavigationItemModel> getNavTagList() {
		return navTagList;
	}

	public void setNavTagList(List<ActionNavigationItemModel> navTagList) {
		this.navTagList = navTagList;
	}
	
	public int getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(int selectedItem) {
		this.selectedItem = selectedItem;
	}

	public class ActionNavigationItemModel{
		private String title = "";
		private String id = "";
		private String subTitle= "";
		
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getSubTitle() {
			return subTitle;
		}
		public void setSubTitle(String subTitle) {
			this.subTitle = subTitle;
		}
		
	}


	
}

