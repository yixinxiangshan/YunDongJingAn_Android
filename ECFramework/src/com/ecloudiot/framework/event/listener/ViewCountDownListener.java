package com.ecloudiot.framework.event.listener;


import com.ecloudiot.framework.event.linterface.OnViewCountDownListener;

public class ViewCountDownListener extends BaseEventListener implements OnViewCountDownListener {

	public ViewCountDownListener(Object pageContext, Object widget, String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}
	
	public void countDownFinised() {

		runJs();
	}
}
