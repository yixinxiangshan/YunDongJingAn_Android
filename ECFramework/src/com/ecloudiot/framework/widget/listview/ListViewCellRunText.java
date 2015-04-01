package com.ecloudiot.framework.widget.listview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.utility.GsonUtil;
import com.google.gson.JsonObject;

public class ListViewCellRunText extends ListViewCellBase{
	
	public ListViewCellRunText(String cellType, Context context,ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
	}

	
	public View getView(int position, View convertView, ViewGroup parent,JsonObject jObject) {
		ViewHolder holder=null;
		DataModel datas;
		datas=GsonUtil.fromJson(jObject, DataModel.class);
		if(convertView == null){
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_runtext, null);
			holder = new ViewHolder();
			holder.ll = (LinearLayout) convertView.findViewById(R.id.widget_listviewcell_runtext_linearlayout);
			//holder.tv = (TextView) convertView.findViewById(R.id.widget_listviewcell_runtext_textview);
			holder.tv=new scrollTextView(getContext(), "this is test");
			holder.ll.addView(holder.tv);
			
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv.setText(datas.content);
		
		return convertView;
	}
	
	class ViewHolder{
		scrollTextView tv;
		LinearLayout ll;
	}
	
	class scrollTextView extends TextView{
		private String text;
		
		public scrollTextView(Context context,String text) {
			super(context);
			this.text=text;
		}
		
		public scrollTextView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			// TODO Auto-generated constructor stub
		}
		
		public scrollTextView(Context context, AttributeSet attrs) {
			super(context, attrs);
			// TODO Auto-generated constructor stub
		}
		
		public scrollTextView(Context context) {
			super(context);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
		}
		
	}
	
	class DataModel{
		private String content;

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
		
	}

}
