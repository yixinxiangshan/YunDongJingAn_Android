package com.ecloudiot.framework.widget.listview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.google.gson.JsonObject;

public class ListViewCellImage extends ListViewCellBase {
	private static String TAG = ListViewCellImage.class.getSimpleName();
	public ListViewCellImage(String cellType, Context context, ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
		LogUtil.d(TAG, ListViewCellImage.class.getName() + ": start");
	}
	/**
	 * 返回该类型cell的view
	 */
	@SuppressLint("InflateParams")
	public View getView(int position, View convertView, ViewGroup parent, JsonObject dataObj) {
		LogUtil.d(TAG, ListViewCellImage.class.getName() + ": getView");
		ViewHolder holder;
		final DataModel data;
		// 初始化数据及holder
		data = GsonUtil.fromJson(dataObj, DataModel.class);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_image, null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.widget_listviewcell_image_img);
			holder.bottomTitle = (TextView) convertView.findViewById(R.id.widget_listviewcell_image_bottomTitle);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		// 设置图片
		ListViewBaseUtil.setImageView(holder.image, data.image, getContext());
		ListViewBaseUtil.setText(holder.bottomTitle, data.bottomTitle);
		if (!StringUtil.isEmpty(data.titlePosition)) {
			if (data.titlePosition.equals("center")) {
				holder.bottomTitle.setGravity(Gravity.CENTER_HORIZONTAL);
			} else if (data.titlePosition.equals("left")) {
				holder.bottomTitle.setGravity(Gravity.LEFT);
			} else if (data.titlePosition.equals("right")) {
				holder.bottomTitle.setGravity(Gravity.RIGHT);
			}
		}
		// holder.image.setOnClickListener(new OnClickListener() { // 点击放大
		// public void onClick(View paramView) {
		//
		// // Dialog reNameDialog = new Dialog(getContext(), R.style.FullScreenDialog);
		// // LayoutInflater mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// // View reNameView = mLayoutInflater.inflate(R.layout.audiorecord_rename, null);
		// //
		// // LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// // reNameDialog.addContentView(reNameView, params);
		// // reNameDialog.show();
		//
		// LayoutInflater inflater = LayoutInflater.from(getContext());
		// View imgEntryView = inflater.inflate(R.layout.dialog_image, null); // 加载自定义的布局文件
		// final Dialog dialog = new Dialog(getContext(), R.style.FullScreenDialog);
		// ImageView img = (ImageView) imgEntryView.findViewById(R.id.large_image);
		// // imageDownloader.download("图片地址", img); // 这个是加载网络图片的，可以是自己的图片设置方法
		// // img.getLayoutParams().width = DensityUtil.screenWidth();
		// LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		// ListViewBaseUtil.setImageView(img, data.image, getContext());
		// dialog.addContentView(imgEntryView, params); // 自定义dialog
		// dialog.show();
		// // 点击布局文件（也可以理解为点击大图）后关闭dialog，这里的dialog不需要按钮
		// imgEntryView.setOnClickListener(new OnClickListener() {
		// public void onClick(View paramView) {
		// dialog.cancel();
		// }
		// });
		// }
		// });

		convertView.setEnabled(false);
		return convertView;

	}
	/**
	 * holder
	 */
	static class ViewHolder {
		ImageView image;
		TextView bottomTitle;
	}
	/**
	 * model
	 */
	static class DataModel {
		private String bottomTitle;
		private String titlePosition;
		private ImageModel image;
//		private boolean showFullImage=false;
//		
//		public boolean isShowFullImage() {
//			return showFullImage;
//		}
//		public void setShowFullImage(boolean showFullImage) {
//			this.showFullImage = showFullImage;
//		}
		public String getBottomTitle() {
			return bottomTitle;
		}
		public void setBottomTitle(String bottomTitle) {
			this.bottomTitle = bottomTitle;
		}

		public ImageModel getImage() {
			return image;
		}
		public void setImage(ImageModel image) {
			this.image = image;
		}
		public String getTitlePosition() {
			return titlePosition;
		}
		public void setTitlePosition(String titlePosition) {
			this.titlePosition = titlePosition;
		}
	}
}
