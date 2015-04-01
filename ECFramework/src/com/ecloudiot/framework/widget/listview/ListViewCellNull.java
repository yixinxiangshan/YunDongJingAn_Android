package com.ecloudiot.framework.widget.listview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecloudiot.framework.R;
//import com.ecloudiot.framework.utility.LogUtil;
import com.google.gson.JsonObject;

public class ListViewCellNull extends ListViewCellBase {
	// private static String TAG = "ListViewCellNull";
	public ListViewCellNull(String cellType, Context context, ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
	}
	/**
	 * 返回该类型cell的view
	 */
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent, JsonObject dataObj) {
		// LogUtil.d(TAG, ListViewCellButton.class.getName() + ": getView");
		ViewHolder holder;
		// DataModel data;
		// 初始化数据及holder
		// data = GsonUtil.fromJson(dataObj, DataModel.class);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_null, null);
			holder = new ViewHolder();
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		return convertView;
	}
	/**
	 * holder
	 */
	static class ViewHolder {

	}
	/**
	 * model
	 */
	static class DataModel {

	}
}
