package com.ecloudiot.framework.event.listener;

import android.view.View;
import android.view.View.OnClickListener;

public class ClickListener extends BaseEventListener implements OnClickListener {
	public ClickListener(Object pageContext, Object widget, String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public void onClick(View view) {
		runJs();
	}

}