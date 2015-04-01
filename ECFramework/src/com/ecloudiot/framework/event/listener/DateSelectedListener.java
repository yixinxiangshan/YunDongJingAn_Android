package com.ecloudiot.framework.event.listener;

import java.util.Date;

import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.widget.model.BaseWidgetConfigModel;
import com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener;

public class DateSelectedListener extends BaseEventListener implements OnDateSelectedListener {

	// private static String TAG = "DataSelectedListener";
	private BaseWidgetConfigModel.BaseWidgetConfigSetEventModel eventModel;
	// private CalendarWidget widgetView;
	public DateSelectedListener(Object pageContext, Object widget, String eventConfigString) {
		super(pageContext, widget, eventConfigString);
		// widgetView = (CalendarWidget) widget;
	}

	@Override
	public void onDateSelected(Date date) {
		// TODO Auto-generated method stub
		eventModel = GsonUtil.fromJson(getEventConfigString(), BaseWidgetConfigModel.BaseWidgetConfigSetEventModel.class);
		adapterEvent(date.toLocaleString());
		runJs();
		adapterEvent("date");
	}

	public void adapterEvent(String state) {
		for (int i = 0; i < eventModel.getParams().size(); ++i) {
			if (eventModel.getParams().get(i).getKey().equalsIgnoreCase("date")) {
				eventModel.getParams().get(i).setValue(state);
				break;
			}
		}
		setEventConfigString(GsonUtil.toJson(eventModel));
	}

}
