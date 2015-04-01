package com.ecloudiot.framework.event.listener;

import com.ecloudiot.framework.event.linterface.OnPageResumeListener;

public class PageResumeListener extends BaseEventListener implements OnPageResumeListener {

	public PageResumeListener(Object pageContext, Object widget, String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public void onPageResume() {
		runJs();
	}

}
