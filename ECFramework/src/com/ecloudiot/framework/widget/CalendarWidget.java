package com.ecloudiot.framework.widget;

import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.event.linterface.OnClickDateSelectedButtonListener;
import com.ecloudiot.framework.javascript.JsViewUtility;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.widget.model.DataPickerModel;
import com.google.gson.JsonObject;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;

@SuppressLint("ViewConstructor")
public class CalendarWidget extends BaseWidget {
	private final static String TAG = "CalendarWidget";
	private DataPickerModel dataModel;
	private CalendarPickerView pickView;
	private String selectMode;
	private Button okButton;
	private Button cancleButton;
	private OnClickDateSelectedButtonListener clickDateSelectedButtonListener;

	public CalendarWidget(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		this.setId(R.id.calendar_widget);
		parsingData();
	}

	protected void initViewLayout(String layoutName) {
		super.initViewLayout(layoutName);
		if (StringUtil.isNotEmpty(layoutName))
			initBaseView(layoutName);
		else {
			initBaseView("widget_calendar_default");
		}
		initView();
	}

	private void initView() {
		pickView = (CalendarPickerView) this.getBaseView().findViewById(R.id.widget_calendar_view);
		okButton = (Button) this.getBaseView().findViewById(R.id.widget_calendar_button_ok);
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				clickDateSelectedButtonListener.onClickDateSelectedButton(pickView.getSelectedDates());
			}
		});
		cancleButton = (Button) this.getBaseView().findViewById(R.id.widget_calendar_button_cancle);
		cancleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				JsViewUtility.closeDialog();
			}
		});
	}

	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		super.parsingWidgetData(widgetDataJObject);
		try {
			this.dataModel = GsonUtil.fromJson(widgetDataJObject, DataPickerModel.class);
		} catch (Exception e) {
			LogUtil.e(TAG, "parsingData error: data String is invalid...");
		}
	}

	protected void setData() {
		if (StringUtil.isEmpty(dataModel.getSelectMode()))
			selectMode = "MULTIPLE";
		else {
			selectMode = dataModel.getSelectMode();
		}
		setMode(selectMode);
		super.setData();
	}

	private void setMode(String selectMode) {
		final Calendar nextYear = Calendar.getInstance();
		final Calendar lastYear = Calendar.getInstance();
		Date minDate;
		if (dataModel.getMaxDate() == 0)
			nextYear.add(Calendar.MONTH, 12);
		else {
			nextYear.add(Calendar.MONTH, dataModel.getMaxDate());
		}
		if (dataModel.getMinDate() == 0) {
			minDate = new Date();
		} else {
			lastYear.add(Calendar.MONTH, dataModel.getMinDate());
			minDate = lastYear.getTime();
		}

		initPickView(minDate, nextYear.getTime());
	}

	private void initPickView(Date minDate, Date maxDate) {
		if (selectMode.equalsIgnoreCase("single"))
			pickView.init(minDate, maxDate).inMode(SelectionMode.SINGLE).withSelectedDate(new Date());
		else if (selectMode.equalsIgnoreCase("multiple"))
			pickView.init(minDate, maxDate).inMode(SelectionMode.MULTIPLE).withSelectedDate(new Date());
		else if (selectMode.equalsIgnoreCase("range"))
			pickView.init(minDate, maxDate).inMode(SelectionMode.RANGE).withSelectedDate(new Date());
	}

	public DataPickerModel getDataModel() {
		return dataModel;
	}

	/**
	 * 刷新控件数据
	 * 
	 * @param moreData
	 */
	public void refreshData(String moreData) {
		super.refreshData(moreData);
		parsingWidgetData(moreData);
		setData();
	}

	public String getSelectedDate() {
		if (pickView.getSelectedDates().size() == 1)
			return new String("你选择的日期为:" + pickView.getSelectedDate().toGMTString());
		else {
			return new String("你选择的开始日期为:" + pickView.getSelectedDates().get(0).toGMTString() + "你选择的结束日期为:"
			        + pickView.getSelectedDates().get(pickView.getSelectedDates().size() - 1).toGMTString());
		}
	}

	public void setOnDateSelectedListener(OnDateSelectedListener dateSelectedListener) {
		pickView.setOnDateSelectedListener(dateSelectedListener);
	}

	public void setOnClickDateSelectedButtonListener(OnClickDateSelectedButtonListener clickDateSelectedButtonListener) {
		this.clickDateSelectedButtonListener = clickDateSelectedButtonListener;
	}

}
