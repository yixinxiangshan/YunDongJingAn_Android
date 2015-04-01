package com.ecloudiot.framework.widget.adapter;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.widget.model.TabModel;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LayoutButtonImageAdapter extends BaseAdapter {
	private static String TAG = "LayoutButtonImageAdapter";
	private TabModel gridviewDataList;
	private DisplayMetrics dm;
	private int itemLayoutId = -1;
	private Activity activity = null;
	public LayoutButtonImageAdapter(Activity activity, TabModel gridviewDataList,String itemLayoutName) {
		super();
		this.activity = activity;
		this.gridviewDataList = gridviewDataList;
		if (StringUtil.isNotEmpty(itemLayoutName)) {
			itemLayoutId = ResourceUtil.getLayoutIdFromContext(activity, itemLayoutName);
		}else {
			itemLayoutId = ResourceUtil.getLayoutIdFromContext(activity, "widget_layout_button_item_default");
		}
		LogUtil.d(TAG, "itemLayoutName"+itemLayoutName);
		// 得到屏幕的大小
		dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
	}

	@Override
	public int getCount() {
		return gridviewDataList.getTabDataList().size();
	}
	
	@Override
	public Object getItem(int position) {
		return gridviewDataList.getTabDataList().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		LogUtil.d(TAG, "getView.............................................");
		if (convertView == null) { // if it's not recycled, initialize some
			holder = new ViewHolder();
			convertView = LayoutInflater.from(ECApplication.getInstance().getNowActivity()).inflate(itemLayoutId, null);
			holder.backgroud = (LinearLayout) convertView.findViewById(R.id.widget_gridview_btn_bg);
			holder.imageView = (ImageView) convertView.findViewById(R.id.gridImg);
			holder.left_line1_tile = (TextView) convertView.findViewById(R.id.gridTitle);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
//		holder.backgroud.setLayoutParams((new GridView.LayoutParams(dm.widthPixels / (imageCol) - 6, dm.widthPixels / (imageCol) - 6)));
		holder.backgroud.setLayoutParams((new GridView.LayoutParams((int)(((float)dm.widthPixels/dm.heightPixels)*dm.widthPixels*0.5), (int)(((float)dm.widthPixels/dm.heightPixels)*dm.widthPixels*0.5))));
//		LogUtil.d(TAG, gridviewDataList.getTabDataList().get(position).getIcon() + "");
		holder.imageView.setImageResource(ResourceUtil.getDrawableIdFromContext(activity, gridviewDataList.getTabDataList().get(position).getIcon()));
		holder.left_line1_tile.setText(gridviewDataList.getTabDataList().get(position).getTitle());
		return convertView;
	}

	static class ViewHolder {
		LinearLayout backgroud;
		ImageView imageView;
		TextView left_line1_tile;
	}
}
