package com.ecloudiot.framework.widget.adapter;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.javascript.JsUtility;
import com.ecloudiot.framework.utility.ColorUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.URLUtil;
import com.ecloudiot.framework.widget.model.GridModel;
import com.ecloudiot.framework.widget.model.GriditemModel;
import com.nostra13.universalimageloader.core.ImageLoader;

public class GridviewAdapter extends BaseAdapter {
	private final String TAG = "GridviewAdapter";
	private GridModel dataGridModel;
	private int itemLayoutId;
	private int imgWidthInteger;
	private int imgHeightInteger;
	private int screenWidth;
	private int numColumns;
	private float ratioHToW;
	private boolean notCalImageSize;
	private boolean noImage;
	private boolean circleImage;
	private int cellSpace;
	private int parentPadding;

	public GridviewAdapter(Context context, GridModel dataList, String itemLayoutName) {
		LogUtil.d(TAG, "start");
		this.dataGridModel = dataList;
		if (StringUtil.isNotEmpty(itemLayoutName)) {
			this.setLayout(ResourceUtil.getLayoutIdFromContext(context, itemLayoutName));
		} else {
			this.setLayout(ResourceUtil.getLayoutIdFromContext(context, "widget_grid_item"));
		}
		Display display = JsUtility.GetActivityContext().getWindowManager().getDefaultDisplay();
		this.screenWidth = display.getWidth();
	}

	private void setLayout(int layoutId) {
		this.itemLayoutId = layoutId;
	}

	@Override
	public int getCount() {
		int contentSize = 0;
		if (null != dataGridModel && dataGridModel.getListByType() != null) {
			contentSize = dataGridModel.getListByType().size();
		}
		return contentSize;
	}

	@Override
	public GriditemModel getItem(int position) {
		return GridviewAdapter.this.dataGridModel.getListByType().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		GriditemModel itemContent = GridviewAdapter.this.dataGridModel.getListByType().get(position);

		final ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(ECApplication.getInstance().getNowActivity()).inflate(this.itemLayoutId, null);

			holder = new ViewHolder();
			holder.rLayout = (RelativeLayout) convertView.findViewById(R.id.widget_grid_item_rlayout);
			holder.title = (TextView) convertView.findViewById(R.id.widget_grid_item_title);
			holder.summary = (TextView) convertView.findViewById(R.id.widget_grid_item_abstract);
			holder.imageLl = (LinearLayout) convertView.findViewById(R.id.widget_grid_item_imagell);
			holder.imageview = (ImageView) convertView.findViewById(R.id.widget_grid_item_image);
			if (!isNotCalImageSize()) {
				LayoutParams para = (LayoutParams) holder.imageview.getLayoutParams();
				para.width = isCircleImage() ? (imgWidthInteger * 4 / 5) : imgWidthInteger;
				para.height = isCircleImage() ? (imgWidthInteger * 4 / 5) : imgHeightInteger;
				holder.imageview.setLayoutParams(para);
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String itemBgString = itemContent.getItemBgColor();
		String titleString = itemContent.getTitle().toString();
		String summaryString = itemContent.getAbstracts();
		String coverString = itemContent.getImage_cover().toString();
		if (StringUtil.isNotEmpty(itemBgString)) {
			holder.rLayout.setBackgroundColor(ColorUtil.getColorValueFromRGB(itemBgString));
		}

		if (holder.summary != null && StringUtil.isNotEmpty(summaryString)) {
			holder.summary.setText(summaryString);
			holder.summary.setVisibility(View.VISIBLE);
		} else if (holder.summary != null) {
			holder.summary.setVisibility(View.GONE);
		}
		LogUtil.d(TAG, titleString);
		holder.title.setText(titleString);

		if (!isNoImage()) {
			String imgnameString = "";
			if (numColumns <= 2) {
				imgnameString = URLUtil.getFitImageWholeUrl(coverString);
			} else {
				imgnameString = URLUtil.getSImageWholeUrl(coverString);
			}
			LogUtil.d(TAG, "imgnameString = " + imgnameString);
			ImageLoader.getInstance().displayImage(imgnameString, holder.imageview);
			holder.imageLl.setVisibility(View.VISIBLE);
		} else {
			holder.imageLl.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	static class ViewHolder {
		RelativeLayout rLayout;
		TextView title;
		TextView summary;
		LinearLayout imageLl;
		ImageView imageview;
	}

	/**
	 * 计算item中图片的宽高度，设计参数：numColumns、cellSpace、parentPadding
	 */
	private void calItemImageWidth() {
		this.imgWidthInteger = ((this.screenWidth - getParentPadding() - getCellSpace() * (getNumColumns() - 1)) / getNumColumns()) - JsUtility.dip2px(20);
		LogUtil.i(TAG, "setNumColumns : imgWidthInteger" + imgWidthInteger);
		this.imgHeightInteger = (int) (this.imgWidthInteger * getRatioHToW());
	}

	public void resetData(GridModel gridModel) {
		this.dataGridModel = null;
		this.dataGridModel = gridModel;
		LogUtil.d(TAG, this.dataGridModel.getListByType().size() + "");
	}

	public int getNumColumns() {
		return numColumns;
	}

	public void setNumColumns(int numColumns) {
		this.numColumns = numColumns;
		calItemImageWidth();
	}

	public float getRatioHToW() {
		return ratioHToW > 0 ? ratioHToW : 0.75f;
	}

	public void setRatioHToW(float ratioHToW) {
		this.ratioHToW = ratioHToW;
		calItemImageWidth();
	}

	public boolean isCircleImage() {
		return circleImage;
	}

	public void setCircleImage(boolean circleImage) {
		this.circleImage = circleImage;
	}

	public int getCellSpace() {
		return cellSpace > 0 ? cellSpace : 0;
	}

	public void setCellSpace(int cellSpace) {
		this.cellSpace = cellSpace;
	}

	public boolean isNotCalImageSize() {
		return notCalImageSize;
	}

	public void setNotCalImageSize(boolean notCalImageSize) {
		this.notCalImageSize = notCalImageSize;
	}

	public boolean isNoImage() {
		return noImage;
	}

	public void setNoImage(boolean noImage) {
		this.noImage = noImage;
	}

	public int getParentPadding() {
		return parentPadding > 0 ? parentPadding : 0;
	}

	public void setParentPadding(int parentPadding) {
		this.parentPadding = parentPadding;
		calItemImageWidth();
	}

}
