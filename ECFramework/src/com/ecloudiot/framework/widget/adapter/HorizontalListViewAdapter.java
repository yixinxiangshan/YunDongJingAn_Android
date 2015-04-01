package com.ecloudiot.framework.widget.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.ecloudiot.framework.R;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.utility.Constants;
import com.ecloudiot.framework.utility.FileUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.URLUtil;
import com.ecloudiot.framework.widget.model.SlideShowItemModel;
import com.ecloudiot.framework.widget.model.SlideShowModel;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HorizontalListViewAdapter extends BaseAdapter {

	private final static String TAG = "HorizontalListViewAdapter";
	private int itemLayoutId;
	private List<SlideShowItemModel> itemList = new ArrayList<SlideShowItemModel>();

	public HorizontalListViewAdapter(Context context, SlideShowModel dataModel, String itemLayoutName) {
		LogUtil.d(TAG, "enter HorizontalListViewAdapter");
		if (null != dataModel && null != dataModel.getItemList() && 0 != dataModel.getItemList().size()) {
			itemList = dataModel.getItemList();
		}
		LogUtil.d(TAG, "HorizontalListViewAdapter : itemLayoutName:" + itemLayoutName);
		if (StringUtil.isNotEmpty(itemLayoutName)) {
			this.setLayout(ResourceUtil.getLayoutIdFromContext(context, itemLayoutName));
		}
	}

	private void setLayout(int layoutId) {
		this.itemLayoutId = layoutId;
	}

	/**
	 * 增加并改变视图.
	 */
	public void addItem(SlideShowItemModel item) {
		itemList.add(item);
	}

	public void addItem(int position, SlideShowItemModel item) {
		itemList.add(position, item);
	}

	public void remove(int position) {
		itemList.remove(position);
	}
	
	public void clear() {
		itemList.clear();
	}
	
	public List<SlideShowItemModel> getItemList() {
		return itemList;
	}

	@Override
	public int getCount() {
		int contentSize = 0;
		if (null != itemList) {
			contentSize = itemList.size();
		}
		return contentSize;
	}

	@Override
	public Object getItem(int position) {
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (null == convertView) {
			convertView = LayoutInflater.from(ECApplication.getInstance().getNowActivity()).inflate(this.itemLayoutId, null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.widget_gallery_imageview);
			holder.imageView.setPadding(0, 0, 5, 0);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String coverString = itemList.get(position).getImage();
		if (position == itemList.size() - 1 && coverString.contains("addimg")) {
			holder.imageView.setImageResource(Integer.parseInt(coverString.replace("addimg", "")));
		} else if (coverString.equals("")) {
			coverString = Constants.IMAGE_DEFAULT_SIMAGE;
		} else if (coverString.indexOf("/storage") != -1) {
			Bitmap mBitmap = FileUtil.getBitmapFromSD(new File(coverString));
			if (mBitmap != null) {
				holder.imageView.setImageBitmap(mBitmap);
			}
		} else {
			String imgnameString = "";
			imgnameString = URLUtil.getSImageWholeUrl(coverString);
			ImageLoader.getInstance().displayImage(imgnameString, holder.imageView);
		}

		return convertView;
	}

	static class ViewHolder {
		ImageView imageView;
	}
}
