package com.ecloudiot.framework.widget.listview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

//import com.ecloudiot.framework.utility.LogUtil;
import com.google.gson.JsonObject;

public class ListViewCellBase {
	// private static String TAG = "ListViewCellBase";
	private String cellName;
	private Context context;
	private ListViewBaseAdapter listViewBaseAdapter;
	public ListViewCellBase(String cellType, Context context, ListViewBaseAdapter listViewBaseAdapter) {
		// LogUtil.d(TAG, ListViewCellBase.class.getName() + ":init");
		setCellName(cellType);
		setContext(context);
		setListViewBaseAdapter(listViewBaseAdapter);

	}
	public View getView(int position, View convertView, ViewGroup parent, JsonObject data) {
		return null;
	}

	// getters and setters
	public String getCellName() {
		return cellName;
	}

	public void setCellName(String cellName) {
		this.cellName = cellName;
	}
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}

	public ListViewBaseAdapter getListViewBaseAdapter() {
		return listViewBaseAdapter;
	}
	public void setListViewBaseAdapter(ListViewBaseAdapter listViewBaseAdapter) {
		this.listViewBaseAdapter = listViewBaseAdapter;
	}
}
