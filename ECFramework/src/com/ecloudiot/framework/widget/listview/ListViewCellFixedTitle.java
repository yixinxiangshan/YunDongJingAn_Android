package com.ecloudiot.framework.widget.listview;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.utility.GsonUtil;
import com.google.gson.JsonObject;

public class ListViewCellFixedTitle extends ListViewCellBase {
	@SuppressWarnings("unused")
    private static String TAG = "ListViewCellFixedTitle";

	public ListViewCellFixedTitle(String cellType, Context context, ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
		// LogUtil.d(TAG, ListViewCellSetting.class.getName() + ": start");
	}
	/**
	 * 返回该类型cell的view
	 */
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent, JsonObject dataObj) {
		DataModel data;
		final ViewHolder holder;
		// 初始化数据及holder
		data = GsonUtil.fromJson(dataObj, DataModel.class);

		HashMap<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("target", "");
		eventParams.put("position", "" + position);
		if (getListViewBaseAdapter().getListViewBase().getPageContext() instanceof ItemActivity) {
			JsAPI.runEvent(((ItemActivity) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
			        .getListViewBase().getControlId(), "onFixedItemDisplay", new JSONObject(eventParams));
		} else if (getListViewBaseAdapter().getListViewBase().getPageContext() instanceof ItemFragment) {

			JsAPI.runEvent(((ItemFragment) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
			        .getListViewBase().getControlId(), "onFixedItemDisplay", new JSONObject(eventParams));
		}

		// LogUtil.d(TAG, ListViewCellSetting.class.getSimpleName() + ": getView-" + position + "-" + data.getType());
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_fixed_title, null);
			holder.title = (TextView) convertView.findViewById(R.id.widget_listviewcell_fixed_title_title);
			holder.collectBtn = (CheckBox) convertView.findViewById(R.id.widget_listviewcell_fixed_title_collectBtn);
			holder.leftImage = (ImageView) convertView.findViewById(R.id.widget_listviewcell_fixed_title_left_img);
			holder.rightImage = (ImageView) convertView.findViewById(R.id.widget_listviewcell_fixed_title_right_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ListViewBaseUtil.setText(holder.title, data.title);
		ListViewBaseUtil.setImageView(holder.rightImage, data.rightImage, true, getContext());
		ListViewBaseUtil.setImageView(holder.leftImage, data.leftImage, true, getContext());
		holder.collectBtn.setChecked(data.isCollected);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("target", "fav");
		map.put("position", position);
		holder.collectBtn.setTag(map);
		holder.collectBtn.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("unchecked")
            @Override
			public void onClick(View v) {

				HashMap<String, String> eventParams = new HashMap<String, String>();
				HashMap<String, Object> map1 = new HashMap<String, Object>();
				map1 = (HashMap<String, Object>) v.getTag();
				eventParams.put("target", map1.get("target").toString());
				eventParams.put("position", "" + map1.get("position"));
				eventParams.put("isChecked", holder.collectBtn.isChecked() + "");
				Map<String, String> mapInput = getListViewBaseAdapter().getInputMap();
				for (Map.Entry<String, String> entry : mapInput.entrySet()) {
					eventParams.put(entry.getKey(), entry.getValue());
				}
				if (getListViewBaseAdapter().getListViewBase().getPageContext() instanceof ItemActivity) {
					JsAPI.runEvent(((ItemFragment) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
					        .getListViewBase().getControlId(), "onItemInnerClick", new JSONObject(eventParams));
				} else if (getListViewBaseAdapter().getListViewBase().getPageContext() instanceof ItemFragment) {

					JsAPI.runEvent(((ItemFragment) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
					        .getListViewBase().getControlId(), "onItemInnerClick", new JSONObject(eventParams));
				}
			}
		});
		// holder.collectBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		//
		// HashMap<String, String> eventParams = new HashMap<String, String>();
		// HashMap<String, Object> map1 = new HashMap<String, Object>();
		// map1 = (HashMap<String, Object>) buttonView.getTag();
		// eventParams.put("target", map1.get("target").toString());
		// eventParams.put("position", "" + map1.get("position"));
		// eventParams.put("isChecked", isChecked + "");
		// Map<String, String> mapInput = getListViewBaseAdapter().getInputMap();
		// for (Map.Entry<String, String> entry : mapInput.entrySet()) {
		// eventParams.put(entry.getKey(), entry.getValue());
		// }
		// JsAPI.runEvent(((ItemActivity) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
		// .getListViewBase().getControlId(), "onItemInnerClick", new JSONObject(eventParams));
		//
		// }
		// });
		return convertView;
	}
	/**
	 * holder
	 */
	static class ViewHolder {
		TextView title;
		ImageView leftImage;
		ImageView rightImage;
		CheckBox collectBtn;
	}

	/**
	 * model
	 */
	static class DataModel {
		String type = "";
		Boolean isDone = false;
		ImageModel leftImage = null;
		public ImageModel getLeftImage() {
			return leftImage;
		}
		public void setLeftImage(ImageModel leftImage) {
			this.leftImage = leftImage;
		}
		ImageModel rightImage = null;
		public ImageModel getRightImage() {
			return rightImage;
		}
		public void setRightImage(ImageModel rightImage) {
			this.rightImage = rightImage;
		}
		public Boolean getIsDone() {
			return isDone;
		}
		public void setIsDone(Boolean isDone) {
			this.isDone = isDone;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		String title;
		boolean isCollected = false;
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public boolean isCollected() {
			return isCollected;
		}
		public void setCollected(boolean isCollected) {
			this.isCollected = isCollected;
		}

	}
}
