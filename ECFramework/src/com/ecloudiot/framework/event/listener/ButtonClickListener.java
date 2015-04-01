package com.ecloudiot.framework.event.listener;

import com.ecloudiot.framework.event.linterface.OnButtonClickListener;
import com.ecloudiot.framework.event.listener.BaseEventListener;

import android.view.View;

public class ButtonClickListener extends BaseEventListener implements OnButtonClickListener {

	public ButtonClickListener(Object pageContext, Object widget, String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public void onClick(View view) {
		runJs();
	}

	@Override
	public void onClick(View view, int position) {
		setEventConfigString(matchPosition(getEventConfigString(), "position", position));
		runJs();
		setEventConfigString(matchPosition(getEventConfigString(), position, "position"));
	}

}
