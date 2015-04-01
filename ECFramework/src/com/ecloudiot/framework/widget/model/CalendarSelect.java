package com.ecloudiot.framework.widget.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.appliction.ECApplication;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class CalendarSelect {
	private DateModel nowDate;
	private DateModel processDate;
	private List<DateModel> list;
	
	public CalendarSelect(String nowDateStr) {
//		list = new ArrayList<CalendarSelect.DateModel>();
		String[] nowDates = nowDateStr.split("-");
		nowDate = new DateModel(nowDates[0], nowDates[1], nowDates[2]);
		processDate = new DateModel(nowDates[0], nowDates[1], nowDates[2]);
	}
	
	public void monthDays(){
		//保持list始终是空的再放入数据
		list = new ArrayList<DateModel>();
		Calendar calendar = Calendar.getInstance();
		calendar.set(processDate.getYear(), processDate.getMonth(), 1);
		makeEmptyView(calendar.get(Calendar.DAY_OF_WEEK));
		makeDateView(calendar.getActualMaximum(calendar.DAY_OF_MONTH));
	}
	
	public void addMonth(){
		add_Sub_Month(1);
	}
	
	public void subMonth(){
		add_Sub_Month(-1);
	}
	
	public void add_Sub_Month(int value){
		Calendar calendar = Calendar.getInstance();
		calendar.set(processDate.getYear(), processDate.getMonth(), 1);
		calendar.add(Calendar.MONTH, value);
		processDate.setYear(calendar.get(Calendar.YEAR));
		processDate.setMonth(calendar.get(Calendar.MONTH));
		//初始化Adapter数据
		monthDays();
	}
	
	private void makeEmptyView(int count){
		count-=1;
		for(int i=0;i<count;i++){
			list.add(new DateModel(true));
		}
	}
	
	private void makeDateView(int count){
		for(int i=1;i<=count;i++){
			if(i==nowDate.getDay() && processDate.getMonth()==nowDate.getMonth() && processDate.getYear()==nowDate.getYear()){
				list.add(new DateModel(processDate.getYear(), processDate.getMonth(), i,true));
			}else{
				list.add(new DateModel(processDate.getYear(), processDate.getMonth(), i));
			}
		}
	}
	
	private View itemView(DateModel tempDateModel){
		MyTextView myTextView = new MyTextView(ECApplication.getInstance().getNowActivity());
		myTextView.setGravity(Gravity.CENTER);
		
		if(tempDateModel.isFillSign()){
			myTextView.setText("");
			myTextView.setBackgroundColor(0xffdddddd);
		}else{
			if(tempDateModel.isSelectSign()){
//				myTextView.setTextColor(Color.RED);
				myTextView.setTextColor(0xff5793ff);
			}
			else{
				myTextView.setTextColor(ECApplication.getInstance().getNowActivity().getResources().getColorStateList(R.drawable.listviewcell_date_select_text));
			}
			myTextView.setBackgroundColor(Color.WHITE);
			myTextView.setText(String.valueOf(tempDateModel.getDay()));
		}
		
		myTextView.setTag(tempDateModel);
		myTextView.setBackgroundResource(R.drawable.gridview_calendar_view);
		return myTextView;
	}
	
	public CalendarSelectAdapter getAdapter(){
		return new CalendarSelectAdapter(list);
	}
	
	public DateModel getProcessDate(){
		return processDate;
	}
	
	public void setProcessDate(DateModel processDate){
		this.processDate = new DateModel(processDate);
	}
	
	class CalendarSelectAdapter extends BaseAdapter{
		private List<DateModel> inList;

		public CalendarSelectAdapter(List<DateModel> inList) {
			this.inList = inList;
		}

		@Override
		public int getCount() {
			return inList.size();
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
			return itemView(inList.get(position));
		}
		
	}
	
	class MyTextView extends TextView{

		public MyTextView(Context context) {
			super(context);
		}

		public MyTextView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}

		public MyTextView(Context context, AttributeSet attrs) {
			super(context, attrs);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
			int childW = getMeasuredWidth();
			int childH = getMeasuredHeight();
			heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childW, MeasureSpec.EXACTLY);
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}
	
	
}
