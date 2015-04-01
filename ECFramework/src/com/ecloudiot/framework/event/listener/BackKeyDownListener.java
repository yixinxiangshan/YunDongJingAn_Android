package com.ecloudiot.framework.event.listener;

import com.ecloudiot.framework.event.linterface.OnBackKeyDownListener;

public class BackKeyDownListener extends BaseEventListener implements OnBackKeyDownListener {

	public BackKeyDownListener(Object pageContext, Object widget,
			String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public void OnBackKeyDown() {
		runJs();
	}

}
