package com.ecloudiot.framework.widget.listview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.VideoActivity;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.ImageUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.URLUtil;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ListViewCellVideo extends ListViewCellBase {
	private static String TAG = ListViewCellVideo.class.getSimpleName();
	public ListViewCellVideo(String cellType, Context context, ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
		LogUtil.d(TAG, ListViewCellVideo.class.getName() + ": start");
	}
	/**
	 * 返回该类型cell的view
	 */
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent, JsonObject dataObj) {
		LogUtil.d(TAG, ListViewCellVideo.class.getName() + ": getView");
		ViewHolder holder;
		final DataModel data;
		// 初始化数据及holder
		data = GsonUtil.fromJson(dataObj, DataModel.class);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_video, null);
			holder = new ViewHolder();
			holder.imgcover = (ImageView) convertView.findViewById(R.id.widget_listviewcell_video_imgcover);
			holder.play = (ImageView) convertView.findViewById(R.id.widget_listviewcell_video_play);
			holder.title = (TextView) convertView.findViewById(R.id.widget_listviewcell_video_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ListViewBaseUtil.setText(holder.title, data.title);
		setImageView(holder.imgcover, data.imgcover);
		// 设置视频url;
		holder.play.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!StringUtil.isEmpty(data.videourl)) {
					Intent intent = new Intent(getContext(), VideoActivity.class);
					intent.putExtra("uriString", data.videourl);
					getContext().startActivity(intent);
				}
			}
		});
		return convertView;
	}

	public void setImageView(ImageView view, ImageModel imageData) {
		if (imageData == null) {
			view.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.VISIBLE);
			// 设置尺寸
			// 设置图片
			if (imageData.imageType.equals("resource")) {
				view.setImageResource(ResourceUtil.getIdFromContext(imageData.imageSrc, "drawable"));
			} else if (imageData.imageType.equals("assets")) {
				view.setImageDrawable(ImageUtil.getDrawableFromConfig(imageData.imageSrc));
			} else if (imageData.imageType.equals("imageServer")) {
				if (StringUtil.isEmpty(imageData.imageSrc)) {
					view.setVisibility(View.GONE);
				} else {
					String imgWholeUrl = URLUtil.getSImageWholeUrl(imageData.imageSrc);
					ImageLoader.getInstance().displayImage(imgWholeUrl, view);
				}
			} else {
				view.setVisibility(View.GONE);
			}
			// 设置点击事件

		}
	}

	// public void setVideoView(VideoView videoView, String videourl) {
	// if (StringUtil.isEmpty(videourl)) {
	// videoView.setVisibility(View.GONE);
	// } else {
	// videoView.setVisibility(View.VISIBLE);
	// videoView.setVideoURI(Uri.parse(videourl));
	// MediaController mediaController = new MediaController(getContext());
	// videoView.setMediaController(mediaController);
	// mediaController.show();
	// // videoView.start();
	// }
	// }
	/**
	 * holder
	 */
	static class ViewHolder {
		ImageView imgcover;
		ImageView play;
		TextView title;
	}
	/**
	 * model
	 */
	static class DataModel {
		private ImageModel imgcover;
		private String videourl;
		private String title;
		public String getVideourl() {
			return videourl;
		}
		public void setVideourl(String videourl) {
			this.videourl = videourl;
		}
		public ImageModel getImgcover() {
			return imgcover;
		}
		public void setImgcover(ImageModel imgcover) {
			this.imgcover = imgcover;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
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
