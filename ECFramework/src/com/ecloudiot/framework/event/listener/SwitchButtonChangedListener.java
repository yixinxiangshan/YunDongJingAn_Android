package com.ecloudiot.framework.event.listener;

import com.ecloudiot.framework.event.linterface.OnSwitchButtonChangedListener;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.widget.model.BaseWidgetConfigModel;

public class SwitchButtonChangedListener extends BaseEventListener implements
		OnSwitchButtonChangedListener {

	private BaseWidgetConfigModel.BaseWidgetConfigSetEventModel eventModel;
	public SwitchButtonChangedListener(Object pageContext, Object widget,
			String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public void onChanged(int state) {
		eventModel = GsonUtil.fromJson(getEventConfigString(), BaseWidgetConfigModel.BaseWidgetConfigSetEventModel.class);
		adapterEvent(Integer.toString(state));
		runJs();
		adapterEvent("state");
	}
	
	public void adapterEvent(String state) {
		for(int i = 0; i < eventModel.getParams().size(); ++i) {
			if(eventModel.getParams().get(i).getKey().equalsIgnoreCase("state")) {
				eventModel.getParams().get(i).setValue(state);
				break;
			}
		}
		setEventConfigString(GsonUtil.toJson(eventModel));
	}

}
