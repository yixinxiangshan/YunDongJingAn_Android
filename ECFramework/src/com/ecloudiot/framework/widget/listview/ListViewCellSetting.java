package com.ecloudiot.framework.widget.listview;

import java.util.HashMap;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.utility.GsonUtil;
//import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.widget.switchbutton.SwitchButton;
import com.google.gson.JsonObject;

public class ListViewCellSetting extends ListViewCellBase implements OnClickListener, OnCheckedChangeListener {
	// private static String TAG = "ListViewCellSetting";
	ViewHolder holder;
	public ListViewCellSetting(String cellType, Context context, ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
		// LogUtil.d(TAG, ListViewCellSetting.class.getName() + ": start");
	}
	/**
	 * 返回该类型cell的view
	 */
	@SuppressLint({"NewApi", "InflateParams"})
	public View getView(int position, View convertView, ViewGroup parent, JsonObject dataObj) {
		// LogUtil.d(TAG, ListViewCellSetting.class.getName() + ": getView");
		DataModel data;
		// 初始化数据及holder
		data = GsonUtil.fromJson(dataObj, DataModel.class);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_setting, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.widget_listviewcell_setting_name);
			holder.value = (TextView) convertView.findViewById(R.id.widget_listviewcell_setting_value);
			holder.description = (TextView) convertView.findViewById(R.id.widget_listviewcell_setting_description);
			holder.switchBtn = (SwitchButton) convertView.findViewById(R.id.widget_listviewcell_setting_btn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();

		}
		// 设置view内容、显示
		setText(holder.name, data.name);
		setText(holder.value, data.value);
		setText(holder.description, data.description);
		// // 设置监听事件
		if (data.isOpen) {
			holder.switchBtn.setChecked(true);
		} else {
			holder.switchBtn.setChecked(false);
		}

		HashMap<String, Object> switchBtnMap = new HashMap<String, Object>();
		switchBtnMap.put("target", "switchBtn");
		switchBtnMap.put("position", position);
		holder.switchBtn.setTag(switchBtnMap);
		holder.switchBtn.setOnCheckedChangeListener(this);

		// 设置监听事件
		HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put("target", "value");
		map.put("position", position);
		holder.value.setTag(map);

		holder.value.setOnClickListener(this);
		// setBadge(holder, data);

		if (data.value.equals("")) {
			holder.value.setVisibility(View.GONE);
		} else {
			holder.value.setVisibility(View.VISIBLE);
		}

		// if (data.isOpen.equals("")) {
		// holder.switchBtn.setVisibility(View.GONE);
		// } else {
		// holder.switchBtn.setVisibility(View.VISIBLE);
		// }
		return convertView;

	}
	public void setText(TextView view, String text) {
		if (StringUtil.isEmpty(text)) {
			view.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.VISIBLE);
			view.setText(text);
		}
	}
	// public void setBadge(ViewHolder holder, DataModel data) {
	// if (null != holder && null != data.badge && data.badge.length() > 0) {
	// BadgeView badge = new BadgeView(getContext(), holder.image);
	// badge.setText(data.badge);
	// badge.show();
	// }
	// }
	/**
	 * holder
	 */
	static class ViewHolder {
		TextView name;
		TextView value;
		TextView description;
		SwitchButton switchBtn;

	}
	/**
	 * model
	 */
	static class DataModel {
		private String name;
		private String value;
		private String description;
		private boolean isOpen = false;

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public DataModel(String name, String value, String description) {
			super();
			this.name = name;
			this.value = value;
			this.description = description;
		}
		public DataModel() {
			this.name = "";
			this.value = "";
			this.description = "";
		}
		public boolean isOpen() {
			return isOpen;
		}
		public void setOpen(boolean isOpen) {
			this.isOpen = isOpen;
		}

	}
	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		// LogUtil.d(TAG, "onItemInnerClick");
		HashMap<String, String> eventParams = new HashMap<String, String>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map = (HashMap<String, Object>) v.getTag();
		eventParams.put("target", map.get("target").toString());
		eventParams.put("position", "" + map.get("position"));
		// eventParams.put("controlId", getControlId());
		// eventParams.put("allTypes", "" + getAdapter().getCount());
		if (getListViewBaseAdapter().getListViewBase().getPageContext() instanceof ItemActivity) {
			JsAPI.runEvent(((ItemActivity) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
			        .getListViewBase().getControlId(), "onItemInnerClick", new JSONObject(eventParams));
		} else if (getListViewBaseAdapter().getListViewBase().getPageContext() instanceof ItemFragment) {
			JsAPI.runEvent(((ItemFragment) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
			        .getListViewBase().getControlId(), "onItemInnerClick", new JSONObject(eventParams));
		}
		// // 弹出对话框
		// AlertDialog.Builder builder = new Builder(getContext());
		// String name = v.getTag().toString();
		// builder.setTitle(name);
		// String[] items = null;
		// if (name.endsWith("字体大小")) {
		// items = new String[]{"小", "中", "大", "超大"};
		// }
		// if (name.equals("自动加载图片")) {
		// items = new String[]{"任何时候", "仅WIFI网络", "从不"};
		// }
		// if (name.equals("自动下载新版安装包")) {
		// items = new String[]{"仅WIFI网络", "从不"};
		// }
		// builder.setSingleChoiceItems(items, 1, null);
		// builder.setPositiveButton("确定", null);
		// builder.create().show();
	}
	@SuppressWarnings("unchecked")
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// LogUtil.d(TAG, "onItemInnerClick");
		// Toast.makeText(getContext(), "" + isChecked, 3).show();
		// LogUtil.d(TAG, "onItemInnerClick");
		HashMap<String, String> eventParams = new HashMap<String, String>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map = (HashMap<String, Object>) buttonView.getTag();
		eventParams.put("target", map.get("target").toString());
		eventParams.put("position", "" + map.get("position"));
		eventParams.put("isChecked", "" + isChecked);
		// eventParams.put("controlId", getControlId());
		// eventParams.put("allTypes", "" + getAdapter().getCount());
		if (getListViewBaseAdapter().getListViewBase().getPageContext() instanceof ItemActivity) {
			JsAPI.runEvent(((ItemActivity) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
			        .getListViewBase().getControlId(), "onItemInnerClick", new JSONObject(eventParams));
		} else if (getListViewBaseAdapter().getListViewBase().getPageContext() instanceof ItemFragment) {
			JsAPI.runEvent(((ItemFragment) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
			        .getListViewBase().getControlId(), "onItemInnerClick", new JSONObject(eventParams));
		}
	}
}
