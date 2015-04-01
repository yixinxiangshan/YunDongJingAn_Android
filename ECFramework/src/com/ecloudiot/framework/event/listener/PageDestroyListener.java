package com.ecloudiot.framework.event.listener;

import com.ecloudiot.framework.event.linterface.OnPageDestroyListener;

public class PageDestroyListener extends BaseEventListener implements OnPageDestroyListener {

	public PageDestroyListener(Object pageContext, Object widget, String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public void onPageDestory() {
		runJs();
	}

}
