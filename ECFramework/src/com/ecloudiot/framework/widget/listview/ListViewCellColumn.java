package com.ecloudiot.framework.widget.listview;

import java.util.HashMap;

import org.json.JSONObject;

import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.view.ListViewCustomGridView;
import com.google.gson.JsonObject;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ListViewCellColumn extends ListViewCellBaseGridView implements ListViewCellGridViewItemView{
	
	private final float TEXT_SIZE=14.0f;
	private int pluginPosition=0;
	private Items []items;
	public ListViewCellColumn(String cellType, Context context,ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
	}
	
	public View getView(int position, View convertView, ViewGroup parent,JsonObject data) {
		
		pluginPosition=position;
		
		Model datas=GsonUtil.fromJson(data, Model.class);
		
		ViewConfig viewConfig=datas.getViewConfig();
		items= datas.getItems();
		
		//不设置setTag，防止refreshData刷新时layout重新addView
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
		return itemView(items[position].getImageModel(),items[position].getText(),position);
	}
	
	private View itemView(ImageModel imageModel,String text,int position)
	{
		//外层布局
		LinearLayout linaLayout=getItemBaseView();
		//内层图片
		ImageView image=new ImageView(getContext());
		image.setScaleType(ScaleType.FIT_CENTER);
		LayoutParams params2=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params2.weight = 3.0f;
		image.setLayoutParams(params2);
		image.setOnClickListener(new ItemClick(text));
		//图片填充器
		ListViewBaseUtil.setImageView(image, imageModel, getContext());
		//内层文字
		TextView textView=new TextView(getContext());
		textView.setGravity(Gravity.CENTER);
		textView.setTextSize(TEXT_SIZE);
		LayoutParams params3=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		params3.weight = 1.0f;
		textView.setLayoutParams(params3);
		textView.setText(text);
		//添加内层布局到外层
		linaLayout.addView(image);
		linaLayout.addView(textView);
		//返回外层布局
		return linaLayout;
	}
	
	class ItemClick implements OnClickListener{

		private final String TYPE="onItemInnerClick";
		private String target="cloumnItem";
		private String columnName="";
		
		public ItemClick(String columnName) {
			this.columnName=columnName;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public void onClick(View v) {
			HashMap<String, String> eventParams = new HashMap<String, String>();
			
			eventParams.put("target", target);
			//设置此控件在ListView里的位置，pluginPosition在ListView的getView里面初始化
			eventParams.put("position", "" + pluginPosition);
			eventParams.put("columnName", columnName);
//			eventParams.put("_form", JsonUtil.simpleMapToJsonStr(getListViewBaseAdapter().getInputMap()));
			JsAPI.runEvent(((ItemFragment) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
			        .getListViewBase().getControlId(), TYPE, new JSONObject(eventParams));
		}
	}
	
	//column视图数据结构
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
	
	//单个Item的数据结构
	class Items{
		private ImageModel imageModel;
		private String text;
		
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public ImageModel getImageModel() {
			return imageModel;
		}
		public void setImageModel(ImageModel imageModel) {
			this.imageModel = imageModel;
		}
	}

}
