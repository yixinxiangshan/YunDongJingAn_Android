package com.ecloudiot.framework.widget.listview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.utility.LogUtil;
//import com.ecloudiot.framework.utility.LogUtil;
import com.google.gson.JsonObject;

@SuppressLint("InflateParams")
public class ListViewCellLoading extends ListViewCellBase {
	private static String TAG =  ListViewCellLoading.class.getSimpleName();
	public ListViewCellLoading(String cellType, Context context, ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
		LogUtil.d(TAG, "");
	}
	/**
	 * 返回该类型cell的view
	 */
	public View getView(int position, View convertView, ViewGroup parent, JsonObject dataObj) {
		// LogUtil.d(TAG, ListViewCellButton.class.getName() + ": getView");
		ViewHolder holder;
		// DataModel data;
		// 初始化数据及holder
		// data = GsonUtil.fromJson(dataObj, DataModel.class);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_loading, null);
			holder = new ViewHolder();
			holder.progressBar = (ProgressBar) convertView.findViewById(R.id.widget_listviewcell_loading_progressBar);
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
		ProgressBar progressBar;
	}
	/**
	 * model
	 */
	static class DataModel {
		private String btnTitle;
		private String btnType;

		public String getBtnTitle() {
			return btnTitle;
		}
		public void setBtnTitle(String btnTitle) {
			this.btnTitle = btnTitle;
		}
		public String getBtnType() {
			return btnType;
		}
		public void setBtnType(String btnType) {
			this.btnType = btnType;
		}
	}
}
