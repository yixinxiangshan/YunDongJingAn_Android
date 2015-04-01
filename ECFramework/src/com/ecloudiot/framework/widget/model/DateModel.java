package com.ecloudiot.framework.widget.model;

public class DateModel implements Cloneable{
	private int year;
	private int month;
	private int day;
	private int week;
	private boolean fillSign=false;
	private boolean selectSign=false;
	
	@Override
	public DateModel clone() throws CloneNotSupportedException {
		return (DateModel)super.clone();
	}

	public DateModel(DateModel temp){
		this.year = temp.getYear();
		this.month = temp.getMonth();
		this.day = temp.getDay();
		this.week = temp.getWeek();
		this.fillSign = temp.isFillSign();
		this.selectSign = temp.isSelectSign();
	}
	
	public DateModel(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	public DateModel(int year, int month, int day,boolean selectSign) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.selectSign = selectSign;
	}
	
	public DateModel(String year, String month, String day) {
		setYear(year);
		setMonth(month);
		setDay(day);
	}
	
	public DateModel(boolean fillSign){
		this.fillSign = fillSign;
	}

	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public void setYear(String year) {
		this.year = Integer.valueOf(year);
	}

	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public void setMonth(String month) {
		this.month = Integer.valueOf(month)-1;
	}

	public int getDay() {
		return day;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public void setDay(String day) {
		this.day = Integer.valueOf(day);
	}

	public int getWeek() {
		return week;
	}
	public void setWeek(int week) {
		this.week = week;
	}
	public void setWeek(String week) {
		this.week = Integer.valueOf(week);
	}
	/**
	 * 返回true代表是填充日期，否则是真实日期
	 */
	public boolean isFillSign() {
		return fillSign;
	}
	public void setFillSign(boolean fillSign) {
		this.fillSign = fillSign;
	}
	/**
	 * 返回true代表是传过来的今天日期应高亮，否则为普通日期
	 */
	public boolean isSelectSign() {
		return selectSign;
	}
	public void setSelectSign(boolean selectSign) {
		this.selectSign = selectSign;
	}
	
	public int getRealMonth(){
		return month+1;
	}
	
	public String getDateStr(){
		return getYear()+"-"+getRealMonth()+"-"+getDay();
	}
}