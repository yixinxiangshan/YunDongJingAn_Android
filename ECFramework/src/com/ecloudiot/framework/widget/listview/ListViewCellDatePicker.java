package com.ecloudiot.framework.widget.listview;

import java.util.Calendar;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.google.gson.JsonObject;

public class ListViewCellDatePicker extends ListViewCellBase {
	private static String TAG = "ListViewCellDatePicker";
	public ListViewCellDatePicker(String cellType, Context context, ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
		LogUtil.d(TAG, "");
	}
	/**
	 * 返回该类型cell的view
	 */
	public View getView(int position, View convertView, ViewGroup parent, JsonObject dataObj) {
		// LogUtil.d(TAG, ListViewCellButton.class.getName() + ": getView");
		ViewHolder holder;
		DataModel data;
		// 初始化数据及holder
		data = GsonUtil.fromJson(dataObj, DataModel.class);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_date_picker, null);
			holder = new ViewHolder();
			holder.picker = (DatePicker) convertView.findViewById(R.id.widget_listviewcell_date_picker_datepicker);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 设置默认值
		Calendar now = Calendar.getInstance();
		if (data.getYear() == 0)
			data.setYear(now.get(Calendar.YEAR));
		if (data.getMonth() == 0)
			data.setMonth(now.get(Calendar.MONTH)+1);
		if (data.getDay() == 0)
			data.setDay(now.get(Calendar.DAY_OF_MONTH));
		
		
		// 设置监听事件
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("target", "datePicker");
		map.put("position", position);
		holder.picker.setTag(map);
		// 系统月份计算比实际数 - 1
		holder.picker.init(data.getYear(), data.getMonth() - 1, data.getDay(), new OnDateChangedListener() {
			@SuppressWarnings("unchecked")
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				HashMap<String, String> eventParams = new HashMap<String, String>();
				HashMap<String, Object> map = new HashMap<String, Object>();
				map = (HashMap<String, Object>) view.getTag();
				eventParams.put("target", map.get("target").toString());
				eventParams.put("position", "" + map.get("position"));
				eventParams.put("value", year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
				JsAPI.runEvent(((ItemActivity) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
				        .getListViewBase().getControlId(), "onChange", new JSONObject(eventParams));
			}
		});
		// 设置当前时间
		HashMap<String, String> eventParams = new HashMap<String, String>();
		map.put("target", "datePicker");
		map.put("position", position);
		eventParams.put("value", data.getYear() + "-" + data.getMonth() + "-" + data.getDay());
		JsAPI.runEvent(((ItemActivity) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
		        .getListViewBase().getControlId(), "onChange", new JSONObject(eventParams));
		return convertView;
	}
	/**
	 * holder
	 */
	static class ViewHolder {
		DatePicker picker;
	}
	/**
	 * model
	 */
	static class DataModel {
		private int year;
		private int month;
		private int day;
		public int getYear() {
			return year;
		}
		public void setYear(int year) {
			this.year = year;
		}
		public int getMonth() {
			return month;
		}
		public void setMonth(int month) {
			this.month = month;
		}
		public int getDay() {
			return day;
		}
		public void setDay(int day) {
			this.day = day;
		}
	}
}
