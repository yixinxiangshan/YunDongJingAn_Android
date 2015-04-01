package com.ecloudiot.framework.event.listener;

import com.ecloudiot.framework.event.linterface.OnPageSelectedListener;

public class PageSelectedListener extends BaseEventListener implements OnPageSelectedListener{

	public PageSelectedListener(Object pageContext, Object widget,
			String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public void onPageSelected(int position) {
		setEventConfigString(matchPosition(getEventConfigString(), "position", position));
		runJs();
		setEventConfigString(matchPosition(getEventConfigString(), position, "position"));
	}

}
