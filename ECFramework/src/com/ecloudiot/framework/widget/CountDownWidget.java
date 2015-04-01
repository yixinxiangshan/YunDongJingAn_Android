package com.ecloudiot.framework.widget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.event.linterface.OnViewCountDownListener;
import com.ecloudiot.framework.utility.ColorUtil;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.widget.model.CountDownItemModel;
import com.google.gson.JsonObject;

@SuppressLint({"ViewConstructor", "InflateParams"})
public class CountDownWidget extends BaseWidget {
	public static final String TAG = "CountDownWidget";
	private LinearLayout dayLayout = null;
	private TextView dayValue;
	private TextView dayUnit = null;
	private LinearLayout hourLayout = null;
	private TextView hourValue;
	private TextView hourUnit = null;
	private LinearLayout minuteLayout = null;
	private TextView minuteValue;
	private TextView minuteUnit = null;
	private LinearLayout secondLayout = null;
	private TextView secondValue;
	private TextView secondUnit = null;
	private CountDownItemModel model;
	private long days = -1;
	private long hours = -1;
	private long minutes = -1;
	private long seconds = -1;
	private MyCountDown myCountDown = null;
	private OnViewCountDownListener countDownListener;
	// 控件属性
	private String timeTextColor;

	/**
	 * @param context
	 * @param dataString
	 * @param layoutName
	 *            为了保持widget 初始化的一致
	 */
	public CountDownWidget(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		this.setId(R.id.count_down_widget);
		parsingData();
	}

	@Override
	protected void initViewLayout(String layoutName) {
		super.initViewLayout(layoutName);
		baseView = new LinearLayout(ctx);
		baseView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		((LinearLayout) baseView).setOrientation(HORIZONTAL);
		((LinearLayout) baseView).setGravity(Gravity.CENTER);
		addLayouts();
	}

	public void addLayouts() {
		this.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		this.setOrientation(HORIZONTAL);
		this.setGravity(Gravity.CENTER);
		dayLayout = (LinearLayout) LayoutInflater.from((Context) this.pageContext).inflate(R.layout.view_count_down_item, null);
		dayValue = (TextView) dayLayout.findViewById(R.id.view_count_down_item_value);
		LogUtil.d(TAG, dayValue + "");
		dayUnit = (TextView) dayLayout.findViewById(R.id.view_count_down_item_unit);
		dayUnit.setText("天");
		getBaseView().addView(dayLayout);
		hourLayout = (LinearLayout) LayoutInflater.from((Context) this.pageContext).inflate(R.layout.view_count_down_item, null);
		hourValue = (TextView) hourLayout.findViewById(R.id.view_count_down_item_value);
		hourUnit = (TextView) hourLayout.findViewById(R.id.view_count_down_item_unit);
		hourUnit.setText("时");
		getBaseView().addView(hourLayout);
		minuteLayout = (LinearLayout) LayoutInflater.from((Context) this.pageContext).inflate(R.layout.view_count_down_item, null);
		minuteValue = (TextView) minuteLayout.findViewById(R.id.view_count_down_item_value);
		minuteUnit = (TextView) minuteLayout.findViewById(R.id.view_count_down_item_unit);
		minuteUnit.setText("分");
		getBaseView().addView(minuteLayout);
		secondLayout = (LinearLayout) LayoutInflater.from((Context) this.pageContext).inflate(R.layout.view_count_down_item, null);
		secondValue = (TextView) secondLayout.findViewById(R.id.view_count_down_item_value);
		secondUnit = (TextView) secondLayout.findViewById(R.id.view_count_down_item_unit);
		secondUnit.setText("秒");
		getBaseView().addView(secondLayout);
	}

	@Override
	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		super.parsingWidgetData(widgetDataJObject);
		LogUtil.d(TAG, "enter parsingWidgetData");
		try {
			model = GsonUtil.fromJson(widgetDataJObject, CountDownItemModel.class);
		} catch (Exception e) {
			LogUtil.e(TAG, "parsingData error: data String is invalid...");
		}
	}

	protected void setData() {
		LogUtil.d(TAG, "enter setData");
		if (StringUtil.isNotEmpty(timeTextColor)) {
			dayValue.setTextColor(ColorUtil.getColorValueFromRGB(timeTextColor));
			hourValue.setTextColor(ColorUtil.getColorValueFromRGB(timeTextColor));
			minuteValue.setTextColor(ColorUtil.getColorValueFromRGB(timeTextColor));
			secondValue.setTextColor(ColorUtil.getColorValueFromRGB(timeTextColor));
		}
		makeData();
		super.setData();
	}

	private void refreshCountTime() {
		if (days > 0) {
			dayValue.setText("" + days);
			dayValue.setVisibility(View.VISIBLE);
		} else {
			dayValue.setVisibility(View.GONE);
		}

		if (hours > 0) {
			hourValue.setText("" + hours);
		} else {
			hourValue.setText("00");
		}

		if (minutes > 0) {
			minuteValue.setText("" + minutes);
		} else {
			minuteValue.setText("00");
		}

		if (seconds > 0) {
			secondValue.setText("" + seconds);
		} else {
			secondValue.setText("00");
		}
	}

	@SuppressLint("SimpleDateFormat")
	private void makeData() {
		if (model != null) {
			// dataItem = GsonParsing.fromJson(dataString,
			// CountDownItemModel.class);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = null;
			Calendar dataCalendar = null;
			Calendar nowCalendar = Calendar.getInstance();
			Calendar disCalendar = new GregorianCalendar();
			try {
				date = dateFormat.parse(model.getTimeString());
				LogUtil.d(TAG, "date:" + date);
				dataCalendar = new GregorianCalendar();
				dataCalendar.setTime(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (dataCalendar.compareTo(nowCalendar) > 0) {
				disCalendar.setTimeInMillis(dataCalendar.getTimeInMillis() - nowCalendar.getTimeInMillis());
				myCountDown = new MyCountDown(disCalendar.getTimeInMillis(), 1000, new MyCountDownListener() {

					@Override
					public void countTicked(long millisUntilFinished) {
						// LogUtil.d(TAG, "倒计时中......");
						long totalSeconds = millisUntilFinished / 1000;
						days = totalSeconds / (24 * 60 * 60);
						long surplusSeconds = totalSeconds % (24 * 60 * 60);
						hours = surplusSeconds / (60 * 60);
						surplusSeconds = totalSeconds % (60 * 60);
						minutes = surplusSeconds / 60;
						seconds = surplusSeconds % 60;
						refreshCountTime();
					}

					@Override
					public void countFinised() {
						LogUtil.d(TAG, "计时结束......");
						seconds = 0;
						refreshCountTime();
						if (null != countDownListener) {
							countDownListener.countDownFinised();
						}
					}
				});
				myCountDown.start();
			} else {
				LogUtil.d(TAG, "时间已过......");
			}
		}
	}

	/**
	 * 设置倒计时监听
	 * 
	 * @param countDownListener
	 */
	public void setOnViewCountDownListener(OnViewCountDownListener onViewCountDownListener) {
		this.countDownListener = onViewCountDownListener;
	}

	class MyCountDown extends CountDownTimer {
		private MyCountDownListener countDownListener = null;

		public MyCountDown(long millisInFuture, long countDownInterval, MyCountDownListener countDownListener) {
			super(millisInFuture, countDownInterval);
			this.countDownListener = countDownListener;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			// EventBus.getDefault().post(new
			// DoActionEvent(dataItem.getCountFTag()));
			if (null != countDownListener) {
				countDownListener.countTicked(millisUntilFinished);
			}
		}

		@Override
		public void onFinish() {
			if (null != countDownListener) {
				countDownListener.countFinised();
			}

		}

	}

	interface MyCountDownListener {
		public void countTicked(long millisUntilFinished);;

		public void countFinised();;
	}

	/**
	 * 刷新控件数据
	 * 
	 * @param moreData
	 */
	public void refreshData(String moreData) {
		super.refreshData(moreData);
		parsingWidgetData(moreData);
		makeData();
	}

	public String getTimeTextColor() {
		return timeTextColor;
	}

	public void setTimeTextColor(String timeTextColor) {
		this.timeTextColor = timeTextColor;
	}

}
