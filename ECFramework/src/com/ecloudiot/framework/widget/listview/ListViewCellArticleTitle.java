package com.ecloudiot.framework.widget.listview;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.utility.GsonUtil;
import com.google.gson.JsonObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ListViewCellArticleTitle extends ListViewCellBase{

	public ListViewCellArticleTitle(String cellType, Context context,ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent,JsonObject jObject) {
		ViewHolder holder=null;
		Model model=GsonUtil.fromJson(jObject, Model.class);
		if(null==convertView){
			convertView=LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_article_title, null);
			holder = new ViewHolder();
			holder.headTitle = (TextView) convertView.findViewById(R.id.widget_listviewcell_article_title_head_textview);
			holder.subeadTitle = (TextView) convertView.findViewById(R.id.widget_listviewcell_article_title_subhead_textview);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
			
		holder.headTitle.setText(model.getHeadTitle());
		holder.subeadTitle.setText(model.getSubheadTitle());
		
		return convertView;
	}

	class ViewHolder{
		private TextView headTitle;
		private TextView subeadTitle;
		public TextView getHeadTitle() {
			return headTitle;
		}
		public void setHeadTitle(TextView headTitle) {
			this.headTitle = headTitle;
		}
		public TextView getSubeadTitle() {
			return subeadTitle;
		}
		public void setSubeadTitle(TextView subeadTitle) {
			this.subeadTitle = subeadTitle;
		}
	}
	class Model{
		private String headTitle;
		private String subheadTitle;
		public String getHeadTitle() {
			return headTitle;
		}
		public void setHeadTitle(String headTitle) {
			this.headTitle = headTitle;
		}
		public String getSubheadTitle() {
			return subheadTitle;
		}
		public void setSubheadTitle(String subheadTitle) {
			this.subheadTitle = subheadTitle;
		}
		
	}
}
