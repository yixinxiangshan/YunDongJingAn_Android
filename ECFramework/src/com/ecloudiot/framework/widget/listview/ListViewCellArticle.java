package com.ecloudiot.framework.widget.listview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.utility.GsonUtil;
import com.google.gson.JsonObject;

public class ListViewCellArticle extends ListViewCellBase {
	// private static String TAG = "ListViewCellArticle";
	ViewHolder holder;
	public ListViewCellArticle(String cellType, Context context, ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
		// LogUtil.d(TAG, ListViewCellSetting.class.getName() + ": start");
	}
	/**
	 * 返回该类型cell的view
	 */
	@SuppressLint({"NewApi", "InflateParams"})
	public View getView(int position, View convertView, ViewGroup parent, JsonObject dataObj) {
		// LogUtil.d(TAG, ListViewCellSetting.class.getName() + ": getView");
		DataModel data;
		// 初始化数据及holder
		data = GsonUtil.fromJson(dataObj, DataModel.class);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_article, null);
			holder.title = (TextView) convertView.findViewById(R.id.widget_listviewcell_article_title);
			holder.subtitle = (TextView) convertView.findViewById(R.id.widget_listviewcell_article_subtitle);
			holder.imgcover = (ImageView) convertView.findViewById(R.id.widget_listviewcell_article_imgcover);
			holder.imgcontent = (ImageView) convertView.findViewById(R.id.widget_listviewcell_article_imgcontent);
			holder.content = (TextView) convertView.findViewById(R.id.widget_listviewcell_article_content);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ListViewBaseUtil.setText(holder.title, data.title);
		ListViewBaseUtil.setText(holder.subtitle, data.subtitle);
		ListViewBaseUtil.setTextDBC(holder.content, data.content);
		ListViewBaseUtil.setImageView(holder.imgcover, data.imgcover, getContext());
		ListViewBaseUtil.setImageView(holder.imgcontent, data.imgcontent, getContext());

		return convertView;
	}

	/**
	 * holder
	 */
	static class ViewHolder {
		TextView title;
		TextView subtitle;
		ImageView imgcover;
		ImageView imgcontent;
		TextView content;
	}

	/**
	 * model
	 */
	static class DataModel {
		private String title;
		private String subtitle;
		private String content;
		private ImageModel imgcover = null;
		private ImageModel imgcontent = null;
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getSubtitle() {
			return subtitle;
		}
		public void setSubtitle(String subtitle) {
			this.subtitle = subtitle;
		}
		public ImageModel getImgcover() {
			return imgcover;
		}
		public void setImgcover(ImageModel imgcover) {
			this.imgcover = imgcover;
		}
		public ImageModel getImgcontent() {
			return imgcontent;
		}
		public void setImgcontent(ImageModel imgcontent) {
			this.imgcontent = imgcontent;
		}

	}

}
