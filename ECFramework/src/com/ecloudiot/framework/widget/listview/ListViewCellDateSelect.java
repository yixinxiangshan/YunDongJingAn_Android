package com.ecloudiot.framework.widget.listview;

import java.util.HashMap;

import org.json.JSONObject;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.view.ListViewCustomGridView;
import com.ecloudiot.framework.widget.listview.ListViewCellBaseGridView.ViewConfig;
import com.google.gson.JsonObject;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListViewCellDateSelect extends ListViewCellBaseGridView implements ListViewCellGridViewItemView{

	private Items []items;
	
	public ListViewCellDateSelect(String cellType, Context context,ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent,JsonObject data) {
		
		Model datas=GsonUtil.fromJson(data, Model.class);
		
		ViewConfig viewConfig=datas.getViewConfig();
		items= datas.getItems();
		
		convertView = getConvertView();
		
		ListViewCustomGridView gridView = getGridView();
		gridView.setNumColumns(viewConfig.getColNum());
		gridView.setHorizontalSpacing(viewConfig.gethSpace());
		gridView.setVerticalSpacing(viewConfig.getvSpace());
		gridView.setBackgroundColor(viewConfig.getBgColor());
		
		gridView.setAdapter(getAdapter(items.length,this));
		
		return convertView;
	}
	
	@Override
	public View getItemView(int position) {
		return itemView(items[position].getDate(),items[position].getContent(),items[position].getOffset());
	}
	
	private View itemView(Date date,String content,String offset)
	{
		LinearLayout linaLayout=getItemBaseView();
		linaLayout.setBackgroundColor(Color.WHITE);
		
		TextView weekTextView = new TextView(getContext());
		weekTextView.setDuplicateParentStateEnabled(true);
		weekTextView.setGravity(Gravity.CENTER_HORIZONTAL);
		setSelector(weekTextView);
		weekTextView.setTextSize(8.0f);
		weekTextView.setText(date.getWeek());
		
		TextView dateTextView = new TextView(getContext());
		dateTextView.setDuplicateParentStateEnabled(true);
		dateTextView.setGravity(Gravity.CENTER_HORIZONTAL);
		setSelector(dateTextView);
		dateTextView.setTextSize(14.0f);
		dateTextView.setText(date.getDateTime());
		
		TextView contentTextView = new TextView(getContext());
		contentTextView.setDuplicateParentStateEnabled(true);
		contentTextView.setGravity(Gravity.CENTER_HORIZONTAL);
		setSelector(contentTextView);
		contentTextView.setTextSize(11.0f);
		contentTextView.setText(content);
		
		if (date.getWeek().length()>0){
			linaLayout.addView(weekTextView);
		}
		if (date.getDateTime().length()>0){
			linaLayout.addView(dateTextView);
		}
		if (content.length()>0){
			linaLayout.addView(contentTextView);
		}
		
		linaLayout.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.listviewcell_date_select_view));
		linaLayout.setOnClickListener(new ItemClick(offset));
		
		return linaLayout;
	}
	
	private void setSelector(TextView textView){
			Resources resources = getContext().getResources();
			ColorStateList stateList = resources.getColorStateList(R.drawable.listviewcell_date_select_text);
			textView.setTextColor(stateList);
	}
	
	class ItemClick implements OnClickListener{

		private final String TYPE="onItemInnerClick";
		private String target="dateSelect";
		private String offset="";
		
		public ItemClick(String offset) {
			this.offset=offset;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void onClick(View v) {
			HashMap<String, String> eventParams = new HashMap<String, String>();
			eventParams.put("target", target);
			eventParams.put("offset", offset);
			if(getListViewBaseAdapter().getListViewBase().getPageContext() instanceof ItemActivity){
				JsAPI.runEvent(((ItemActivity) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
				        .getListViewBase().getControlId(), TYPE, new JSONObject(eventParams));
			}else{
				JsAPI.runEvent(((ItemFragment) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
				        .getListViewBase().getControlId(), TYPE, new JSONObject(eventParams));
			}
		}
	}
	
	class Model{
		private Items[] items;
		private ViewConfig viewConfig;

		public ViewConfig getViewConfig() {
			return viewConfig;
		}

		public void setViewConfig(ViewConfig viewConfig) {
			this.viewConfig = viewConfig;
		}

		public Items[] getItems() {
			return items;
		}

		public void setItems(Items[] items) {
			this.items = items;
		}
	}
	
	class Items{
		private Date date;
		private String content;
		private String offset;
		
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getOffset() {
			return offset;
		}
		public void setOffset(String offset) {
			this.offset = offset;
		}
	}
	
	class Date{
		private String week;
		private String dateTime;
		public String getWeek() {
			return week;
		}
		public void setWeek(String week) {
			this.week = week;
		}
		public String getDateTime() {
			return dateTime;
		}
		public void setDateTime(String dateTime) {
			this.dateTime = dateTime;
		}
	}
}