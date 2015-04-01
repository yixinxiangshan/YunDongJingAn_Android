package com.ecloudiot.framework.event.listener;

import java.util.Date;
import java.util.List;

import com.ecloudiot.framework.event.linterface.OnClickDateSelectedButtonListener;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.widget.model.BaseWidgetConfigModel;

public class ClickDateSelectedButtonListener extends BaseEventListener implements OnClickDateSelectedButtonListener {

	private static String TAG = "ClickDateSelectedButtonListener";
	private BaseWidgetConfigModel.BaseWidgetConfigSetEventModel eventModel;

	public ClickDateSelectedButtonListener(Object pageContext, Object widget, String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public void onClickDateSelectedButton(List<Date> list) {
		if (null != list) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < list.size(); ++i) {
				sb.append(list.get(i).toLocaleString());
				if (i != list.size() - 1)
					sb.append(" ");
			}
			eventModel = GsonUtil.fromJson(getEventConfigString(), BaseWidgetConfigModel.BaseWidgetConfigSetEventModel.class);
			adapterEvent(sb.toString(), "dateList");
			runJs();
			adapterEvent("dateList", "dateList");
		} else {
			LogUtil.d(TAG, "you do not choose any date");
			return;
		}
	}
	public void adapterEvent(String state, String field) {
		for (int i = 0; i < eventModel.getParams().size(); ++i) {
			if (eventModel.getParams().get(i).getKey().equalsIgnoreCase(field)) {
				eventModel.getParams().get(i).setValue(state);
			}
		}
		setEventConfigString(GsonUtil.toJson(eventModel));
	}

}
