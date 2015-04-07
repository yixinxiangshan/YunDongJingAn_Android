package com.ecloudiot.framework.widget.listview;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.widget.listview.ListViewCellTextWithButton.DataModel;
import com.ecloudiot.framework.widget.listview.ListViewCellTextWithButton.ViewHolder;
import com.google.gson.JsonObject;

@SuppressWarnings("hiding")
public class ListViewCellTextWithButton extends ListViewCellBase {
	private static String TAG = "ListViewCellTextWithButton";
	public ListViewCellTextWithButton(String cellType, Context context, ListViewBaseAdapter listViewBaseAdapter) {
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
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_text_with_button, null);
			holder = new ViewHolder();
			holder.button = (TextView) convertView.findViewById(R.id.widget_listviewcell_text_with_button_button);
			holder.button1 = (TextView) convertView.findViewById(R.id.widget_listviewcell_text_with_button_button1);
			holder.title = (TextView) convertView.findViewById(R.id.widget_listviewcell_text_with_button_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (StringUtil.isEmpty(data.getBtnTitle())) {

			holder.button.setVisibility(View.GONE);
		} else {
			holder.button.setText(data.getBtnTitle());
			holder.button.setVisibility(View.VISIBLE);
		}
		if (StringUtil.isEmpty(data.getBtn1Title())) {

			holder.button1.setVisibility(View.GONE);
		} else {
			holder.button1.setText(data.getBtn1Title());
			holder.button1.setVisibility(View.VISIBLE);
		}
		// 设置监听事件
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("target", "title");
		map.put("position", position);
		holder.button.setTag(map);
		holder.button1.setTag(map);
		holder.title.setOnClickListener(null);
		holder.button.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("unchecked")
			public void onClick(View v) {
				HashMap<String, String> eventParams = new HashMap<String, String>();
				HashMap<String, Object> map = new HashMap<String, Object>();
				map = (HashMap<String, Object>) v.getTag();
				eventParams.put("target", "title_button");
				eventParams.put("position", "" + map.get("position"));
				eventParams.put("viewName", "" + "button");

				Map<String, String> mapInput = getListViewBaseAdapter().getInputMap();
				for (Map.Entry<String, String> entry : mapInput.entrySet()) {
					eventParams.put(entry.getKey(), entry.getValue());
				}

				if (getListViewBaseAdapter().getListViewBase().getPageContext() instanceof ItemActivity) {
					JsAPI.runEvent(((ItemActivity) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
					        .getListViewBase().getControlId(), "onItemInnerClick", new JSONObject(eventParams));
				} else if (getListViewBaseAdapter().getListViewBase().getPageContext() instanceof ItemFragment) {
					JsAPI.runEvent(((ItemFragment) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
					        .getListViewBase().getControlId(), "onItemInnerClick", new JSONObject(eventParams));
				}
			}
		});

		holder.button1.setOnClickListener(new View.OnClickListener() {
			@SuppressWarnings("unchecked")
			public void onClick(View v) {
				HashMap<String, String> eventParams = new HashMap<String, String>();
				HashMap<String, Object> map = new HashMap<String, Object>();
				map = (HashMap<String, Object>) v.getTag();
				eventParams.put("target", "title_button1");
				eventParams.put("position", "" + map.get("position"));
				eventParams.put("viewName", "button1");
				Map<String, String> mapInput = getListViewBaseAdapter().getInputMap();
				for (Map.Entry<String, String> entry : mapInput.entrySet()) {
					eventParams.put(entry.getKey(), entry.getValue());
				}

				if (getListViewBaseAdapter().getListViewBase().getPageContext() instanceof ItemActivity) {
					JsAPI.runEvent(((ItemActivity) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
					        .getListViewBase().getControlId(), "onItemInnerClick", new JSONObject(eventParams));
				} else if (getListViewBaseAdapter().getListViewBase().getPageContext() instanceof ItemFragment) {
					JsAPI.runEvent(((ItemFragment) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
					        .getListViewBase().getControlId(), "onItemInnerClick", new JSONObject(eventParams));
				}
			}
		});
		ListViewBaseUtil.setText(holder.title, data.title);

		// if (StringUtil.isNotEmpty(data.btnTitle)) {
		// holder.button.setText(data.btnTitle);
		// holder.button.setVisibility(View.VISIBLE);
		// } else {
		// holder.button.setVisibility(View.INVISIBLE);
		// }
		return convertView;
	}
	/**
	 * holder
	 */
	static class ViewHolder {
		TextView title;
		TextView button;
		TextView button1;

	}
	/**
	 * model
	 */
	static class DataModel {
		private String title;
		private String btnTitle;
		private String btn1Title;
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
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getBtn1Title() {
			return btn1Title;
		}
		public void setBtn1Title(String btn1Title) {
			this.btn1Title = btn1Title;
		}
	}
}
