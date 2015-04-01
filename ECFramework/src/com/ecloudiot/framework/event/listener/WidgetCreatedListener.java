package com.ecloudiot.framework.event.listener;

import java.util.HashMap;

import com.ecloudiot.framework.event.linterface.OnWidgetCreatedListener;

public class WidgetCreatedListener extends BaseEventListener implements OnWidgetCreatedListener {

	public WidgetCreatedListener(Object pageContext, Object widget, String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public void onWidgetCreated(String widgetId,String parentId){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("widgetId", widgetId);
		map.put("parentId", parentId);
		runJs(map);
	}

}
