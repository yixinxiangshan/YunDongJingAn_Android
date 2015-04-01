package com.ecloudiot.framework.widget.model;

import java.util.ArrayList;

public class HashMapListModel {
	private ArrayList<HashMapItemModel> hashMList;
	
	public ArrayList<HashMapItemModel> getHashMList() {
		return hashMList;
	}


	public void setHashMList(ArrayList<HashMapItemModel> hashMList) {
		this.hashMList = hashMList;
	}
	
	public class HashMapItemModel{
		private String key;
		private String value;
		
		public String getKey() {
			return key;
		}
		public void setKey(String key) {
			this.key = key;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
	}
	
}
