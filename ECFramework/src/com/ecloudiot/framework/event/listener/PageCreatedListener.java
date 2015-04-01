package com.ecloudiot.framework.event.listener;

import com.ecloudiot.framework.event.linterface.OnPageCreatedListener;

public class PageCreatedListener extends BaseEventListener implements OnPageCreatedListener {

	public PageCreatedListener(Object pageContext, Object widget, String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public void onPageCreated() {
		runJs();
	}

}
