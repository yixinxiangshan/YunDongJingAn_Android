package com.ecloudiot.framework.event.listener;

import android.view.View;

import com.ecloudiot.framework.event.linterface.OnButtonLongClickListener;

public class ButtonLongClickListener extends BaseEventListener implements OnButtonLongClickListener{

	public ButtonLongClickListener(Object pageContext, Object widget,
			String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public boolean onLongClick(View v) {
		runJs();
		return false;
	}

}
