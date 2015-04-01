package com.ecloudiot.framework.view;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.widget.model.CalendarSelect;
import com.ecloudiot.framework.widget.model.DateModel;

public class CalendarView {
	private String nowDate;
	private Context context;
	private CalendarSelect calendarSelect;
	private MaterialDialog dialogHandle;
	private HashMap<String, String> params;
	private pagerCalendar[] viewList;
	private ViewPagerAutoHeight viewPager;
	
	public CalendarView(Context context,String nowDate,MaterialDialog dialogHandle,HashMap<String, String> params) {
		this.context = context;
		this.nowDate = nowDate;
		this.dialogHandle = dialogHandle;
		this.params = params;
		init();
	}
	
	//初始化展示内容
	private void init(){
		
		viewPager = new ViewPagerAutoHeight(context);
		
		viewList = new pagerCalendar[3];
		
		calendarSelect = new CalendarSelect(nowDate);
		calendarSelect.monthDays();
		
		viewList[0]=new pagerCalendar();
		viewList[1]=new pagerCalendar();
		viewList[2]=new pagerCalendar();
		
		viewList[1].setView(linearViewMaker(0));
		viewList[1].setDateModel(calendarSelect.getProcessDate());
		
		viewList[0].setView(linearViewMaker(-1)); 
		viewList[0].setDateModel(calendarSelect.getProcessDate());
		
		viewList[2].setView(linearViewMaker(2));
		viewList[2].setDateModel(calendarSelect.getProcessDate());
		
		
		viewPager.setAdapter(new myPagerAdapter());
		//设置当前页面为100，100%3=1，是中间页
		viewPager.setCurrentItem(100);
		viewPager.setOnPageChangeListener(new onPager());
	}
	
	private class onMyItemClick implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			DateModel itemDate = (DateModel) view.getTag();
			if(!itemDate.isFillSign()){
				JSONObject arg = new JSONObject();
				try {
					arg.put("dateStr", itemDate.getDateStr());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				JsAPI.callback(params.get("_jsMakerId"), params.get("_callbackId"), arg);
				dialogHandle.dismiss();
			}
		}
	}
	
	private class myPagerAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0==arg1;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager)container).removeView((View)object);
//			container.removeView(viewList[position % viewList.length].getView());
		}
		
		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			
			View view = viewList[position % viewList.length].getView();
			
			if (view.getParent() != null) {  
	                ((ViewPager) view.getParent()).removeView(view);  
	        } 
			container.removeView(view);
			container.addView(view);
			return view;
		}
	}
	
	private class onPager implements OnPageChangeListener{

		@Override
		public void onPageScrollStateChanged(int arg0) {
			
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int position) {
			
//			if(position-nowPostion>0){
//				//增加
//				calendarSelect.setProcessDate(viewList[position%viewList.length].getDateModel());
//				
//				viewList[(position+1)%viewList.length].setView(linearViewMaker(1));
//				viewList[(position+1)%viewList.length].setDateModel(calendarSelect.getProcessDate());
//			}else{
//				//减少
//				Log.e("leetest", "减少");
//				calendarSelect.setProcessDate(viewList[position%viewList.length].getDateModel());
//				
//				viewList[(position-1)%viewList.length].setView(linearViewMaker(-1));
//				viewList[(position-1)%viewList.length].setDateModel(calendarSelect.getProcessDate());
//			}
//			nowPostion = position;
			
			try {
				calendarSelect.setProcessDate(viewList[position%viewList.length].getDateModel().clone());
				viewList[(position+1)%viewList.length].setView(linearViewMaker(1));
				viewList[(position+1)%viewList.length].setDateModel(calendarSelect.getProcessDate());
				
				calendarSelect.setProcessDate(viewList[position%viewList.length].getDateModel().clone());
				viewList[(position-1)%viewList.length].setView(linearViewMaker(-1));
				viewList[(position-1)%viewList.length].setDateModel(calendarSelect.getProcessDate());
				
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
private LinearLayout linearViewMaker(int doProess){

		if(doProess!=0){
			calendarSelect.add_Sub_Month(doProess);
		}
		
		View view = LayoutInflater.from(context).inflate(R.layout.view_calendar, null);

		LinearLayout layout = (LinearLayout) view.findViewById(R.id.calendar_view_linearlayout);
		
		//用于显示年份和月份
		TextView yearMonth = (TextView) view.findViewById(R.id.calendar_view_yearmonth_textview);
		String month = calendarSelect.getProcessDate().getRealMonth()<10? "0"+calendarSelect.getProcessDate().getRealMonth():String.valueOf(calendarSelect.getProcessDate().getRealMonth());
		yearMonth.setText(calendarSelect.getProcessDate().getYear() + "-" + month);
		
		//星期显示,以后可以使用Local结合Fromate实现多种语言
		GridView gridViewWeek = (GridView) view.findViewById(R.id.calendar_view_week_gridview);
		gridViewWeek.setAdapter(new BaseAdapter() {
			private String[] strs={"日","一","二","三","四","五","六"};
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView textView = new TextView(context);
				textView.setGravity(Gravity.CENTER);
				textView.setText(strs[position]);
				return textView;
			}
			
			@Override
			public long getItemId(int position) {
				return 0;
			}
			
			@Override
			public Object getItem(int position) {
				return null;
			}
			
			@Override
			public int getCount() {
				return strs.length;
			}
		});
		
		//日历主视图
		GridView gridView = (GridView) view.findViewById(R.id.calendar_view_monthday_gridview);
		gridView.setHorizontalSpacing(2);
		gridView.setVerticalSpacing(2);
		gridView.setPadding(4, 4, 4, 4);
		gridView.setAdapter(calendarSelect.getAdapter());
		gridView.setOnItemClickListener(new onMyItemClick());
		return layout;
	}
	
	public View getView(){
		return viewPager;
	}
	
	private class pagerCalendar{
		private LinearLayout view;
		private DateModel dateModel;
		public View getView() {
			return view;
		}
		public void setView(LinearLayout view) {
			this.view = view;
		}
		
		public DateModel getDateModel() {
			return this.dateModel;
		}
		public void setDateModel(DateModel temp) {
			try {
				this.dateModel = temp.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class ViewPagerAutoHeight extends ViewPager{

		public ViewPagerAutoHeight(Context context) {
			super(context);
		}

		public ViewPagerAutoHeight(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, widthMeasureSpec+50);
		}
	}
}
