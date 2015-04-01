package com.ecloudiot.framework.widget.listview;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.view.ListViewCustomGridView;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
/**
 *网格布局基本类，如需要完成此布局<br>
 *<ul>
 *<li>继承ListViewCellBaseGridView</li>
 *<li>实现ListViewCellGridViewItemView接口</li>
 *<li>需要传递ViewConfig</li>
 *</ul>
 * @author Lee
 */
public class ListViewCellBaseGridView extends ListViewCellBase{
	
	private View convertView;
	private GridAdapter adapter;
	
	public ListViewCellBaseGridView(String cellType, Context context,ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
	}
	
	protected View getConvertView(){
		convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_column, null);
		return convertView;
	}
	
	protected ListViewCustomGridView getGridView(){
		ListViewCustomGridView gridView=(ListViewCustomGridView) convertView.findViewById(R.id.widget_listviewcell_column_gridview);
		return gridView; 
	}

	protected LinearLayout getItemBaseView(){
		View view=LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_column_item, null);
		return (LinearLayout) view.findViewById(R.id.widget_listviewcell_column_layout_item);
	}
	
	protected GridAdapter getAdapter(int length,ListViewCellGridViewItemView itemView){
		return new GridAdapter(length,itemView);
	}
	
	class GridAdapter extends BaseAdapter{
		private int length;
		private ListViewCellGridViewItemView itemView;
		
		public GridAdapter(int length,ListViewCellGridViewItemView itemView){
			this.length = length;
			this.itemView = itemView;
		}

		@Override
		public int getCount() {
			return length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//装载数据
			View view=itemView.getItemView(position);
			//view.setOnClickListener(new ItemClick(position));
			return view;
		}
	}
	
	class ViewConfig{
		private int bgColor = Color.WHITE;
		private int colNum =3;
		private int hSpace = 0;
		private int vSpace = 0;
		/**
		 * default value
		 * @return Color.white
		 */
		public int getBgColor() {
			return bgColor;
		}
		public void setBgColor(int bgColor) {
			this.bgColor = bgColor;
		}
		/**
		 * default value
		 * @return 3
		 */
		public int getColNum() {
			return colNum;
		}
		public void setColNum(int colNum) {
			this.colNum = colNum;
		}
		/**
		 * default value
		 * @return 0
		 */
		public int gethSpace() {
			return px2dp(hSpace);
		}
		public void sethSpace(int hSpace) {
			this.hSpace = hSpace;
		}
		/**
		 * default value
		 * @return 0
		 */
		public int getvSpace() {
			return px2dp(vSpace);
		}
		public void setvSpace(int vSpace) {
			this.vSpace = vSpace;
		}
		
		private int px2dp(int pxValue){
			float scale = ECApplication.getInstance().getNowActivity().getResources().getDisplayMetrics().density;
		    return (int) (pxValue / scale + 0.5f);  
		}
	}
	
}
