package com.ecloudiot.framework.event.listener;

import com.ecloudiot.framework.event.linterface.OnPageWillInitWidgetsListener;

public class PageWillInitWidgetsListener extends BaseEventListener implements OnPageWillInitWidgetsListener {

	public PageWillInitWidgetsListener(Object pageContext, Object widget, String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public void willInitWidgets() {
		runJs();
	}

}
