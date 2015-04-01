package com.ecloudiot.framework.widget.listview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.utility.ColorUtil;
import com.ecloudiot.framework.utility.GsonUtil;
//import com.ecloudiot.framework.utility.LogUtil;
import com.google.gson.JsonObject;

@SuppressLint("InflateParams")
public class ListViewCellGroupTitle extends ListViewCellBase {
	// private static String TAG = "ListViewCellGroupTitle";
	public ListViewCellGroupTitle(String cellType, Context context, ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
		// LogUtil.d(TAG, ListViewCellGroupTitle.class.getName() + ": start");
	}

	/**
	 * 返回该类型cell的view
	 */
	@SuppressLint("NewApi")
	public View getView(int position, View convertView, ViewGroup parent, JsonObject dataObj) {
		// LogUtil.d(TAG, ListViewCellButton.class.getName() + ": getView");
		ViewHolder holder;
		DataModel data;
		// 初始化数据及holder
		data = GsonUtil.fromJson(dataObj, DataModel.class);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_group_title, null);
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.widget_listviewcell_group_title_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String textString = data.getTextTitle();
		if (textString == null || textString.equals("")) {
			holder.text.setTextSize(3);
			holder.text.setText("");
		} else {
			holder.text.setText(textString);
		}

		String color = data.getColor();
		if (null == color || color.equals("")) {

		} else {
			String html = "<font color = '" + color + "'>" + holder.text.getText().toString() + "</font>";
			// 将上面的String解析为html格式，返回 字符串
			CharSequence charSequence = Html.fromHtml(html);
			holder.text.setText(charSequence); // 设置文本
		}

		String textBgColor = data.getTextBgColor();
		if (null == textBgColor || textBgColor.equals("")) {

		} else if (textBgColor.startsWith("#")) {
			ColorDrawable colorDrawable = new ColorDrawable(ColorUtil.getColorValueFromRGB(textBgColor));
			holder.text.setBackgroundDrawable(colorDrawable);
		}
		return convertView;
	}
	/**
	 * holder
	 */
	static class ViewHolder {
		TextView text;
	}
	/**
	 * model
	 */
	static class DataModel {
		private String textTitle;
		private String textBgColor;
		private String color;
		public String getTextBgColor() {
			return textBgColor;
		}
		public void setTextBgColor(String textBgColor) {
			this.textBgColor = textBgColor;
		}
		public String getColor() {
			return color;
		}
		public void setColor(String color) {
			this.color = color;
		}
		public String getTextTitle() {
			return textTitle;
		}
		public void setTextTitle(String textTitle) {
			this.textTitle = textTitle;
		}
		public String getTextType() {
			return textType;
		}
		public void setTextType(String textType) {
			this.textType = textType;
		}
		private String textType;

	}
}
