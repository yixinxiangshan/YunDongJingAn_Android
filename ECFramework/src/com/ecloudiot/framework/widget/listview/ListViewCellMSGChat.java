package com.ecloudiot.framework.widget.listview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ecloudiot.framework.R;
import com.ecloudiot.framework.utility.DensityUtil;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.ImageUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.URLUtil;
import com.ecloudiot.framework.view.CircleImageView;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("InflateParams")
public class ListViewCellMSGChat extends ListViewCellBase {
	ViewHolder holder;
	private static String TAG = ListViewCellMSGChat.class.getSimpleName();
	public ListViewCellMSGChat(String cellType, Context context, ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
		LogUtil.d(TAG, ListViewCellMSGChat.class.getName() + ": start");
	}
	/**
	 * 返回该类型cell的view
	 */
	public View getView(int position, View convertView, ViewGroup parent, JsonObject dataObj) {
		LogUtil.d(TAG, ListViewCellMSGChat.class.getName() + ": getView");

		DataModel data;
		// 初始化数据及holder
		data = GsonUtil.fromJson(dataObj, DataModel.class);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_msg_chat, null);
			holder = new ViewHolder();
			holder.layout_from = (LinearLayout) convertView.findViewById(R.id.widget_listviewcell_msg_chat_msg_layout_from);
			holder.layout_to = (LinearLayout) convertView.findViewById(R.id.widget_listviewcell_msg_chat_msg_layout_to);

			holder.time = (TextView) convertView.findViewById(R.id.widget_listviewcell_msg_chat_msg_time);

			holder.from_head = (CircleImageView) convertView.findViewById(R.id.widget_listviewcell_msg_chat_msgfrom_head);
			holder.from_content_text = (TextView) convertView.findViewById(R.id.widget_listviewcell_msg_chat_msgfrom_content_text);
			holder.from_content_img = (ImageView) convertView.findViewById(R.id.widget_listviewcell_msg_chat_msgfrom_content_img);

			holder.to_head = (CircleImageView) convertView.findViewById(R.id.widget_listviewcell_msg_chat_msgto_head);
			holder.to_content_text = (TextView) convertView.findViewById(R.id.widget_listviewcell_msg_chat_msgto_content_text);
			holder.to_content_img = (ImageView) convertView.findViewById(R.id.widget_listviewcell_msg_chat_msgto_content_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();

		}

		// 设置view内容、显示
		setText(holder.time, data.msgTime);
		setText(holder.from_content_text, data.from_content_text);
		setText(holder.to_content_text, data.to_content_text);

		setImageView(holder.from_head, data.from_head);
		setImageView(holder.from_content_img, data.from_content_img);
		setImageView(holder.to_head, data.to_head);
		setImageView(holder.to_content_img, data.to_content_img);

		if (data.from_content_text.equals("")) {
			holder.layout_from.setVisibility(View.GONE);
			holder.layout_to.setVisibility(View.VISIBLE);
		} else {
			holder.layout_from.setVisibility(View.VISIBLE);
			holder.layout_to.setVisibility(View.GONE);
		}
		if (data.showTime) {
			holder.time.setVisibility(View.VISIBLE);
		} else {
			holder.time.setVisibility(View.GONE);
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
				imageWidth = DensityUtil.dipTopx(getContext(), 150);
				imageHeight = DensityUtil.dipTopx(getContext(), 200);
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
		LinearLayout layout_from;
		LinearLayout layout_to;
		TextView time;

		CircleImageView from_head;
		TextView from_content_text;
		ImageView from_content_img;
		CircleImageView to_head;
		TextView to_content_text;
		ImageView to_content_img;
	}
	/**
	 * model
	 */
	static class DataModel {
		private boolean showTime = false;
		private String msgTime;
		private String from_content_text;
		private String to_content_text;
		private ImageModel from_head;
		private ImageModel to_head;
		private ImageModel from_content_img;
		private ImageModel to_content_img;
		public DataModel() {
			this.msgTime = "";
			this.from_content_text = "";
			this.to_content_text = "";

		}

		public DataModel(boolean showTime, String msgTime, String from_content_text, String to_content_text, ImageModel from_head, ImageModel to_head,
		        ImageModel from_content_img, ImageModel to_content_img) {
			this();
			this.showTime = showTime;
			this.msgTime = msgTime;
			this.from_content_text = from_content_text;
			this.to_content_text = to_content_text;
			this.from_head = from_head;
			this.to_head = to_head;
			this.from_content_img = from_content_img;
			this.to_content_img = to_content_img;
		}
		public boolean isShowTime() {
			return showTime;
		}
		public void setShowTime(boolean showTime) {
			this.showTime = showTime;
		}
		public String getMsgTime() {
			return msgTime;
		}
		public void setMsgTime(String msgTime) {
			this.msgTime = msgTime;
		}
		public String getFrom_content_text() {
			return from_content_text;
		}
		public void setFrom_content_text(String from_content_text) {
			this.from_content_text = from_content_text;
		}
		public String getTo_content_text() {
			return to_content_text;
		}
		public void setTo_content_text(String to_content_text) {
			this.to_content_text = to_content_text;
		}
		public ImageModel getFrom_head() {
			return from_head;
		}
		public void setFrom_head(ImageModel from_head) {
			this.from_head = from_head;
		}
		public ImageModel getTo_head() {
			return to_head;
		}
		public void setTo_head(ImageModel to_head) {
			this.to_head = to_head;
		}
		public ImageModel getFrom_content_img() {
			return from_content_img;
		}
		public void setFrom_content_img(ImageModel from_content_img) {
			this.from_content_img = from_content_img;
		}
		public ImageModel getTo_content_img() {
			return to_content_img;
		}
		public void setTo_content_img(ImageModel to_content_img) {
			this.to_content_img = to_content_img;
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
