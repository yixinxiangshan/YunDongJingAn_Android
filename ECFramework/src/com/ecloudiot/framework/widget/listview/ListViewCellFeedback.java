package com.ecloudiot.framework.widget.listview;

import java.util.HashMap;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.utility.LogUtil;
//import com.ecloudiot.framework.utility.LogUtil;
import com.google.gson.JsonObject;

public class ListViewCellFeedback extends ListViewCellBase {
	private static String TAG = "ListViewCellFeedback";
	public ListViewCellFeedback(String cellType, Context context, ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
		LogUtil.d(TAG, "");
	}
	/**
	 * 返回该类型cell的view
	 */
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent, JsonObject dataObj) {
		// LogUtil.d(TAG, ListViewCellButton.class.getName() + ": getView");
		final ViewHolder holder;
		// DataModel data;
		// 初始化数据及holder
		// data = GsonUtil.fromJson(dataObj, DataModel.class);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_feedback, null);
			holder = new ViewHolder();
			holder.content = (EditText) convertView.findViewById(R.id.widget_listviewcell_feedback_content);
			holder.contact = (EditText) convertView.findViewById(R.id.widget_listviewcell_feedback_contact);
			holder.button_submit = (Button) convertView.findViewById(R.id.widget_listviewcell_feedback_button_submit);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// holder.button_submit.setEnabled(data.getBtnType().equals("disable") ? false : true);
		// holder.button_submit.setText(data.getBtnTitle());
		// 设置监听事件

		holder.content.setFocusable(true);
		holder.content.setFocusableInTouchMode(true);
		holder.content.setClickable(true);
		holder.content.requestFocus();

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("target", "button");
		map.put("position", position);
		map.put("content", holder.content.getText().toString());
		map.put("contact", holder.contact.getText().toString());
		holder.button_submit.setTag(map);
		holder.button_submit.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("unchecked")
			public void onClick(View v) {
				HashMap<String, String> eventParams = new HashMap<String, String>();
				HashMap<String, Object> map = new HashMap<String, Object>();
				map = (HashMap<String, Object>) v.getTag();
				eventParams.put("target", map.get("target").toString());
				eventParams.put("position", "" + map.get("position"));
				eventParams.put("content", "" + holder.content.getText().toString());
				eventParams.put("contact", "" + holder.contact.getText().toString());
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
		EditText content;
		EditText contact;
		Button button_submit;
	}
	/**
	 * model
	 */
	static class DataModel {
		private String content;
		private String contact;
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getContact() {
			return contact;
		}
		public void setContact(String contact) {
			this.contact = contact;
		}

	}
}
