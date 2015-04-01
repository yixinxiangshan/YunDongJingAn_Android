package com.ecloudiot.framework.widget.listview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.utility.ColorUtil;
import com.ecloudiot.framework.utility.DensityUtil;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.google.gson.JsonObject;
//import com.ecloudiot.framework.utility.LogUtil;

public class ListViewCellLine extends ListViewCellBase {
	private static String TAG = ListViewCellLine.class.getSimpleName();
	public ListViewCellLine(String cellType, Context context, ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
		LogUtil.d(TAG, "");

	}

	/**
	 * 返回该类型cell的view
	 */
	@SuppressLint({"NewApi", "ResourceAsColor", "InflateParams"})
	public View getView(int position, View convertView, ViewGroup parent, JsonObject dataObj) {
		// LogUtil.d(TAG, "getView:" + position);

		ViewHolder holder;
		DataModel data;
		// 初始化数据及holder
		data = GsonUtil.fromJson(dataObj, DataModel.class);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_line, null);
			holder = new ViewHolder();
			holder.topDivider = (ImageView) convertView.findViewById(R.id.widget_listviewcell_line_divider_top);
			holder.bottomDivider = (ImageView) convertView.findViewById(R.id.widget_listviewcell_line_divider_bottom);
			holder.leftLayout = (RelativeLayout) convertView.findViewById(R.id.widget_listviewcell_line_layoutleft);
			holder.leftTitle = (TextView) convertView.findViewById(R.id.widget_listviewcell_line_left_title);
			holder.leftImage = (ImageView) convertView.findViewById(R.id.widget_listviewcell_line_left_image);
			holder.leftNotice = (TextView) convertView.findViewById(R.id.widget_listviewcell_line_left_notice);
			holder.centerLayout = (RelativeLayout) convertView.findViewById(R.id.widget_listviewcell_line_layoutcenter);
			holder.centerTitle = (TextView) convertView.findViewById(R.id.widget_listviewcell_line_center_title);
			holder.centerRightdes = (TextView) convertView.findViewById(R.id.widget_listviewcell_line_center_rightdes);
			holder.centerBottomdes = (TextView) convertView.findViewById(R.id.widget_listviewcell_line_center_bottomdes);
			holder.centerBottomdes2 = (TextView) convertView.findViewById(R.id.widget_listviewcell_line_center_bottomdes2);
			holder.centerRighttopdes = (TextView) convertView.findViewById(R.id.widget_listviewcell_line_center_righttopdes);
			holder.centerImage1 = (ImageView) convertView.findViewById(R.id.widget_listviewcell_line_center_image1);
			holder.centerImage2 = (ImageView) convertView.findViewById(R.id.widget_listviewcell_line_center_image2);
			holder.centerImage3 = (ImageView) convertView.findViewById(R.id.widget_listviewcell_line_center_image3);
			holder.rightLayout = (RelativeLayout) convertView.findViewById(R.id.widget_listviewcell_line_layoutright);
			holder.rightDes = (TextView) convertView.findViewById(R.id.widget_listviewcell_line_right_des);
			holder.rightImage = (ImageView) convertView.findViewById(R.id.widget_listviewcell_line_right_image);
			// holder.rightNotice = (TextView) convertView.findViewById(R.id.widget_listviewcell_line_right_notice);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// if (holder.leftImage != null) {
		//
		// BadgeView badge = new BadgeView(getContext(), holder.leftImage);
		// badge.setText("1");
		// badge.show();
		// }
		setLeftAndRightLayoutParams(holder, data);

		// 设置view内容、显示
		ListViewBaseUtil.setBackGround(convertView, data._backgroundColor);
		ListViewBaseUtil.setLayoutSize(holder.leftLayout, data._leftLayoutSize, getContext());
		ListViewBaseUtil.setLayoutSize(holder.centerLayout, data._centerLayoutSize, getContext());
		ListViewBaseUtil.setLayoutSize(holder.rightLayout, data._rightLayoutSize, getContext());
		ListViewBaseUtil.setLayoutSize(holder.centerTitle, data._titleLayoutSize, getContext());

		setText(holder.leftTitle, data.leftTitle, data._leftTitleColor, getContext());
		ListViewBaseUtil.setImageView(holder.leftImage, data.leftImage, getContext());
		ListViewBaseUtil.setNotice(holder.leftNotice, data.leftNotice, getContext());
		setText(holder.centerTitle, data.centerTitle, data._centerTitleColor, getContext());
		setText(holder.centerRighttopdes, data.centerRighttopdes);
		setText(holder.centerRightdes, data.centerRightdes);
		setText(holder.centerBottomdes, data.centerBottomdes, data._centerBottomdesColor, getContext());
		setText(holder.centerBottomdes2, data.centerBottomdes2, data._centerBottomdes2Color, getContext());

		ListViewBaseUtil.setImageView(holder.centerImage1, data.centerImage1, getContext());
		ListViewBaseUtil.setImageView(holder.centerImage2, data.centerImage2, getContext());
		ListViewBaseUtil.setImageView(holder.centerImage3, data.centerImage3, getContext());
		setText(holder.rightDes, data.rightDes);
		ListViewBaseUtil.setImageView(holder.rightImage, data.rightImage, getContext());
		// ListViewBaseUtil.setNotice(holder.rightNotice, data.rightNoticeNum, getContext());
		ListViewBaseUtil.setImageView(holder.topDivider, data._topDivider, getContext());
		ListViewBaseUtil.setImageView(holder.bottomDivider, data._bottomDivider, getContext());

		return convertView;
	}

	public void setLeftAndRightLayoutParams(ViewHolder holder, DataModel data) {
		if (!StringUtil.isEmpty(data.centerBottomdes2)) {
			// 左右图片 不居中 + margin
			RelativeLayout.LayoutParams leftLayoutParams = new RelativeLayout.LayoutParams(holder.leftLayout.getLayoutParams());
			leftLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			leftLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			leftLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, 0);
			holder.leftLayout.setLayoutParams(leftLayoutParams);

			LayoutParams rightLayoutParams = new LayoutParams(holder.rightLayout.getLayoutParams());
			rightLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			rightLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			rightLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, 0);
			holder.rightLayout.setLayoutParams(rightLayoutParams);

			LayoutParams leftImageParams = new LayoutParams(holder.leftImage.getLayoutParams());
			leftImageParams.addRule(RelativeLayout.CENTER_VERTICAL);
			leftImageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
			holder.leftImage.setLayoutParams(leftImageParams);
			holder.leftImage.setPadding(0, DensityUtil.dipTopx(getContext(), 26), 0, 0);

			LayoutParams rightImageParams = new LayoutParams(holder.rightImage.getLayoutParams());
			rightImageParams.addRule(RelativeLayout.CENTER_VERTICAL);
			rightImageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			rightImageParams.addRule(RelativeLayout.RIGHT_OF, R.id.widget_listviewcell_line_right_des);
			holder.rightImage.setLayoutParams(rightImageParams);
			holder.rightImage.setPadding(0, DensityUtil.dipTopx(getContext(), 26), 0, 0);
		} else {
			// 左右图片居中
			RelativeLayout.LayoutParams leftLayoutParams = new RelativeLayout.LayoutParams(holder.leftLayout.getLayoutParams());
			leftLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
			leftLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			leftLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
			holder.leftLayout.setLayoutParams(leftLayoutParams);

			LayoutParams rightLayoutParams = new LayoutParams(holder.rightLayout.getLayoutParams());
			rightLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
			rightLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			rightLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
			holder.rightLayout.setLayoutParams(rightLayoutParams);

			LayoutParams leftImageParams = new LayoutParams(holder.leftImage.getLayoutParams());
			leftImageParams.addRule(RelativeLayout.CENTER_VERTICAL);
			leftImageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
			holder.leftImage.setLayoutParams(leftImageParams);
			holder.leftImage.setPadding(0, 0, 0, 0);

			LayoutParams rightImageParams = new LayoutParams(holder.rightImage.getLayoutParams());
			rightImageParams.addRule(RelativeLayout.CENTER_VERTICAL);
			rightImageParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
			rightImageParams.addRule(RelativeLayout.RIGHT_OF, R.id.widget_listviewcell_line_right_des);
			holder.rightImage.setLayoutParams(rightImageParams);
			holder.rightImage.setPadding(0, DensityUtil.dipTopx(getContext(), 2), 0, 0);
		}
	}

	public void setText(TextView view, String text) {
		if (StringUtil.isEmpty(text)) {
			view.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.VISIBLE);
			view.setText(text);
		}
	}
	@SuppressLint("ResourceAsColor")
	public void setText(TextView view, String text, ColorModel textColor, int rDefaultColor, Context context) {
		setText(view, text);
		if (textColor == null) {
			view.setTextColor(context.getResources().getColor(rDefaultColor));
		} else {
			if (StringUtil.isEmpty(textColor.getPressed()) && StringUtil.isEmpty(textColor.getSelected()))
				view.setTextColor(ColorUtil.getColorValueFromRGB(textColor.getNormal()));
			else
				view.setTextColor(ColorUtil.getColorState(textColor.getNormal(), textColor.getPressed(), textColor.getSelected()));
		}
	}
	@SuppressLint("ResourceAsColor")
	public void setText(TextView view, String text, ColorModel textColor, Context context) {
		setText(view, text);
		if (textColor != null) {
			if (StringUtil.isEmpty(textColor.getPressed()) && StringUtil.isEmpty(textColor.getSelected()) && (!StringUtil.isEmpty(textColor.getNormal())))
				view.setTextColor(ColorUtil.getColorValueFromRGB(textColor.getNormal()));
			else if (!StringUtil.isEmpty(textColor.getNormal()))
				view.setTextColor(ColorUtil.getColorState(textColor.getNormal(), textColor.getPressed(), textColor.getSelected()));
		}
	}
	/**
	 * holder
	 */
	static class ViewHolder {
		ImageView topDivider;
		ImageView bottomDivider;
		RelativeLayout leftLayout;
		TextView leftTitle;
		ImageView leftImage;
		TextView leftNotice;
		RelativeLayout centerLayout;
		TextView centerTitle;
		TextView centerRightdes;
		TextView centerBottomdes;
		TextView centerBottomdes2;
		TextView centerRighttopdes;
		ImageView centerImage1;
		ImageView centerImage2;
		ImageView centerImage3;
		RelativeLayout rightLayout;
		TextView rightDes;
		ImageView rightImage;
		TextView rightNotice;
	}

	/**
	 * model
	 */
	class DataModel {
		private ImageModel _topDivider = null;
		private ImageModel _bottomDivider = null;
		private ColorModel _backgroundColor = null;
		private boolean _clickable = true;
		private int _leftLayoutSize = 0;
		private int _centerLayoutSize = 0;
		private int _rightLayoutSize = 0;
		private int _titleLayoutSize = 0;
		private ColorModel _leftTitleColor = null;
		private ColorModel _centerTitleColor = null;
		private ColorModel _centerBottomdesColor = null;
		private ColorModel _centerBottomdes2Color = null;
		private ColorModel _centerRighttopdesColor = null;
		private String leftTitle = "";
		private ImageModel leftImage = null;
		private int leftNotice = -1;
		private String centerTitle = "";
		private String centerRightdes = "";
		private String centerBottomdes = "";
		private String centerBottomdes2 = "";
		private String centerRighttopdes = "";
		private ImageModel centerImage1 = null;
		private ImageModel centerImage2 = null;
		private ImageModel centerImage3 = null;
		private String rightDes = "";
		private ImageModel rightImage = null;
		private int rightNoticeNum = -1;
		public ColorModel get_centerBottomdes2Color() {
			return _centerBottomdes2Color;
		}
		public void set_centerBottomdes2Color(ColorModel _centerBottomdes2Color) {
			this._centerBottomdes2Color = _centerBottomdes2Color;
		}

		public ColorModel get_centerBottomdesColor() {
			return _centerBottomdesColor;
		}
		public void set_centerBottomdesColor(ColorModel _centerBottomdesColor) {
			this._centerBottomdesColor = _centerBottomdesColor;
		}

		public ColorModel get_leftTitleColor() {
			return _leftTitleColor;
		}
		public void set_leftTitleColor(ColorModel _leftTitleColor) {
			this._leftTitleColor = _leftTitleColor;
		}
		public ColorModel get_centerTitleColor() {
			return _centerTitleColor;
		}
		public void set_centerTitleColor(ColorModel _centerTitleColor) {
			this._centerTitleColor = _centerTitleColor;
		}
		public ColorModel get_centerRighttopdesColor() {
			return _centerRighttopdesColor;
		}
		public void set_centerRighttopdesColor(ColorModel _centerRighttopdesColor) {
			this._centerRighttopdesColor = _centerRighttopdesColor;
		}
		public String getLeftTitle() {
			return leftTitle;
		}
		public void setLeftTitle(String leftTitle) {
			this.leftTitle = leftTitle;
		}
		public int get_titleLayoutSize() {
			return _titleLayoutSize;
		}
		public void set_titleLayoutSize(int _titleLayoutSize) {
			this._titleLayoutSize = _titleLayoutSize;
		}
		public int get_leftLayoutSize() {
			return _leftLayoutSize;
		}
		public void set_leftLayoutSize(int _leftLayoutSize) {
			this._leftLayoutSize = _leftLayoutSize;
		}
		public int get_centerLayoutSize() {
			return _centerLayoutSize;
		}
		public void set_centerLayoutSize(int _centerLayoutSize) {
			this._centerLayoutSize = _centerLayoutSize;
		}
		public int get_rightLayoutSize() {
			return _rightLayoutSize;
		}
		public void set_rightLayoutSize(int _rightLayoutSize) {
			this._rightLayoutSize = _rightLayoutSize;
		}
		public ImageModel getLeftImage() {
			return leftImage;
		}
		public void setLeftImage(ImageModel leftImage) {
			this.leftImage = leftImage;
		}

		public ColorModel get_backgroundColor() {
			return _backgroundColor;
		}
		public void set_backgroundColor(ColorModel _backgroundColor) {
			this._backgroundColor = _backgroundColor;
		}
		public int getLeftNotice() {
			return leftNotice;
		}
		public void setLeftNotice(int leftNotice) {
			this.leftNotice = leftNotice;
		}
		public String getCenterTitle() {
			return centerTitle;
		}
		public void setCenterTitle(String centerTitle) {
			this.centerTitle = centerTitle;
		}
		public String getCenterRightdes() {
			return centerRightdes;
		}
		public void setCenterRightdes(String centerRightdes) {
			this.centerRightdes = centerRightdes;
		}
		public String getCenterBottomdes() {
			return centerBottomdes;
		}
		public void setCenterBottomdes(String centerBottomdes) {
			this.centerBottomdes = centerBottomdes;
		}
		public String getCenterBottomdes2() {
			return centerBottomdes2;
		}
		public void setCenterBottomdes2(String centerBottomdes2) {
			this.centerBottomdes2 = centerBottomdes2;
		}
		public String getCenterRighttopdes() {
			return centerRighttopdes;
		}
		public void setCenterRighttopdes(String centerRighttopdes) {
			this.centerRighttopdes = centerRighttopdes;
		}
		public ImageModel getCenterImage1() {
			return centerImage1;
		}
		public void setCenterImage1(ImageModel centerImage1) {
			this.centerImage1 = centerImage1;
		}
		public ImageModel getCenterImage2() {
			return centerImage2;
		}
		public void setCenterImage2(ImageModel centerImage2) {
			this.centerImage2 = centerImage2;
		}
		public ImageModel getCenterImage3() {
			return centerImage3;
		}
		public void setCenterImage3(ImageModel centerImage3) {
			this.centerImage3 = centerImage3;
		}
		public String getRightDes() {
			return rightDes;
		}
		public void setRightDes(String rightDes) {
			this.rightDes = rightDes;
		}
		public ImageModel getRightImage() {
			return rightImage;
		}
		public void setRightImage(ImageModel rightImage) {
			this.rightImage = rightImage;
		}

		public int getRightNoticeNum() {
			return rightNoticeNum;
		}
		public void setRightNoticeNum(int rightNoticeNum) {
			this.rightNoticeNum = rightNoticeNum;
		}
		public boolean is_clickable() {
			return _clickable;
		}
		public void set_clickable(boolean _clickable) {
			this._clickable = _clickable;
		}
		public ImageModel get_topDivider() {
			return _topDivider;
		}
		public void set_topDivider(ImageModel _topDivider) {
			this._topDivider = _topDivider;
		}
		public ImageModel get_bottomDivider() {
			return _bottomDivider;
		}
		public void set_bottomDivider(ImageModel _bottomDivider) {
			this._bottomDivider = _bottomDivider;
		}
	}

}
