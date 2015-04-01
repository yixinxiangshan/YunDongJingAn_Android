package com.ecloudiot.framework.widget.listview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import com.ecloudiot.framework.R;
import com.ecloudiot.framework.utility.DensityUtil;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.ImageUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.URLUtil;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("InflateParams")
public class ListViewCellNewsOutlook extends ListViewCellBase {
	private static String TAG = ListViewCellNewsOutlook.class.getSimpleName();
	public ListViewCellNewsOutlook(String cellType, Context context, ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
		LogUtil.d(TAG, ListViewCellNewsOutlook.class.getName() + ": start");
	}
	/**
	 * 返回该类型cell的view
	 */
	public View getView(int position, View convertView, ViewGroup parent, JsonObject dataObj) {
		LogUtil.d(TAG, ListViewCellNewsOutlook.class.getName() + ": getView");
		ViewHolder holder;
		DataModel data;
		// 初始化数据及holder
		data = GsonUtil.fromJson(dataObj, DataModel.class);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_news_outlook, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.widget_listviewcell_news_outlook_title);
			holder.time = (TextView) convertView.findViewById(R.id.widget_listviewcell_news_outlook_time);
			holder.img = (ImageView) convertView.findViewById(R.id.widget_listviewcell_news_outlook_img);
			holder.overview = (TextView) convertView.findViewById(R.id.widget_listviewcell_news_outlook_overview);
			holder.todetail = (TextView) convertView.findViewById(R.id.widget_listviewcell_news_outlook_todetail);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 设置view内容、显示
		setText(holder.title, data.title);
		setText(holder.time, data.time);
		setImageView(holder.img, data.img);
		setText(holder.overview, data.overview);
		setText(holder.todetail, data.todetail);
		// setBadge(holder, data);

		if (data.title.equals("")) {
			convertView.setVisibility(View.GONE);
		} else {
			convertView.setVisibility(View.VISIBLE);
		}
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
	public void setImageView(ImageView view, ImageModel imageData) {
		if (imageData == null) {
			view.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.VISIBLE);
			// 设置尺寸
			int imageWidth = 0;
			int imageHeight = 0;
			if (imageData.imageSize.equals("fitSize")) {
				imageWidth = LayoutParams.WRAP_CONTENT;
				imageHeight = LayoutParams.WRAP_CONTENT;
			} else if (imageData.imageSize.equals("micro")) {
				imageWidth = DensityUtil.dipTopx(getContext(), 20);
				imageHeight = DensityUtil.dipTopx(getContext(), 20);
			} else if (imageData.imageSize.equals("mini")) {
				imageWidth = DensityUtil.dipTopx(getContext(), 35);
				imageHeight = DensityUtil.dipTopx(getContext(), 35);
			} else if (imageData.imageSize.equals("small")) {
				imageWidth = DensityUtil.dipTopx(getContext(), 50);
				imageHeight = DensityUtil.dipTopx(getContext(), 50);
			} else if (imageData.imageSize.equals("middle")) {
				imageWidth = DensityUtil.dipTopx(getContext(), 70);
				imageHeight = DensityUtil.dipTopx(getContext(), 70);
			} else if (imageData.imageSize.equals("large")) {
				imageWidth = DensityUtil.dipTopx(getContext(), 300);
				imageHeight = DensityUtil.dipTopx(getContext(), 150);
			}
			view.getLayoutParams().width = imageWidth;
			view.getLayoutParams().height = imageHeight;
			// 设置图片
			if (imageData.imageType.equals("resource")) {
				view.setImageResource(ResourceUtil.getIdFromContext(imageData.imageSrc, "drawable"));
			} else if (imageData.imageType.equals("assets")) {
				view.setImageDrawable(ImageUtil.getDrawableFromConfig(imageData.imageSrc));
			} else if (imageData.imageType.equals("imageServer")) {
				String imgWholeUrl = URLUtil.getSImageWholeUrl(imageData.imageSrc);
				ImageLoader.getInstance().displayImage(imgWholeUrl, view);
			} else {
				view.setVisibility(View.GONE);
			}
			// 设置点击事件

		}
	}
	/**
	 * holder
	 */
	static class ViewHolder {
		TextView title;
		TextView time;
		ImageView img;
		TextView overview;
		TextView todetail;
	}
	/**
	 * model
	 */
	static class DataModel {
		private String title = "";
		private String time = "";
		private String overview = "";
		private String todetail = "";
		private ImageModel img = null;
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public String getOverview() {
			return overview;
		}
		public void setOverview(String overview) {
			this.overview = overview;
		}
		public String getTodetail() {
			return todetail;
		}
		public void setTodetail(String todetail) {
			this.todetail = todetail;
		}
		public ImageModel getImg() {
			return img;
		}
		public void setImg(ImageModel img) {
			this.img = img;
		}
		public boolean isEmpty() {
			if (title.endsWith("")) {
				return true;
			}
			return false;
		}

	}
	static class ImageModel {
		private boolean _clickable = false;
		private String imageType = "";
		private String imageSize = "";
		private String imageSrc = "";

		public boolean is_clickable() {
			return _clickable;
		}
		public void set_clickable(boolean _clickable) {
			this._clickable = _clickable;
		}
		public String getImageType() {
			return imageType;
		}
		public void setImageType(String imageType) {
			this.imageType = imageType;
		}
		public String getImageSize() {
			return imageSize;
		}
		public void setImageSize(String imageSize) {
			this.imageSize = imageSize;
		}
		public String getImageSrc() {
			return imageSrc;
		}
		public void setImageSrc(String imageSrc) {
			this.imageSrc = imageSrc;
		}
	}
}
