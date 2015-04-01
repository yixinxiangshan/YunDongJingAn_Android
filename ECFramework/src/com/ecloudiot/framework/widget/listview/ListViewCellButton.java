package com.ecloudiot.framework.widget.listview;

import java.util.HashMap;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.JsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
//import com.ecloudiot.framework.utility.LogUtil;
import com.google.gson.JsonObject;

public class ListViewCellButton extends ListViewCellBase {
	private static String TAG = "ListViewCellButton";
	public ListViewCellButton(String cellType, Context context, ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
		LogUtil.d(TAG, "");
	}
	/**
	 * 返回该类型cell的view
	 */
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent, JsonObject dataObj) {
		// LogUtil.d(TAG, ListViewCellButton.class.getName() + ": getView");
		ViewHolder holder;
		DataModel data;
		// 初始化数据及holder
		data = GsonUtil.fromJson(dataObj, DataModel.class);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_button, null);
			holder = new ViewHolder();
			holder.button_cancel = (Button) convertView.findViewById(R.id.widget_listviewcell_button_btn_cancel);
			holder.button_ok = (Button) convertView.findViewById(R.id.widget_listviewcell_button_btn_ok);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Button button = null;
		if (data.getBtnType().equals("cancel")) {
			button = holder.button_cancel;
			holder.button_cancel.setVisibility(View.VISIBLE);
			holder.button_ok.setVisibility(View.GONE);
		} else {
			button = holder.button_ok;
			holder.button_cancel.setVisibility(View.GONE);
			holder.button_ok.setVisibility(View.VISIBLE);
		}
		button.setEnabled(data.getBtnType().equals("disable") ? false : true);

		button.setText(data.getBtnTitle());
		// 设置监听事件
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("target", "button");
		map.put("position", position);
		button.setTag(map);
		button.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("unchecked")
			public void onClick(View v) {
				HashMap<String, Object> eventParams = new HashMap<String, Object>();
				HashMap<String, Object> map = new HashMap<String, Object>();
				map = (HashMap<String, Object>) v.getTag();
				eventParams.put("target", map.get("target").toString());
				eventParams.put("position", "" + map.get("position"));
				eventParams.put("_form", new JSONObject(getListViewBaseAdapter().getInputMap()));
				JsAPI.runEvent(((ItemActivity) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
				        .getListViewBase().getControlId(), "onItemInnerClick", new JSONObject(eventParams));
			}
		});
		return convertView;
	}
	/**
	 * holder
	 */
	static class ViewHolder {
		Button button_ok;
		Button button_cancel;
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
