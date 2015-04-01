package com.ecloudiot.framework.event.listener;


import com.ecloudiot.framework.event.linterface.OnNavItemClickListener;

public class NavItemClickListener extends BaseEventListener implements OnNavItemClickListener {

	public NavItemClickListener(Object pageContext, Object widget, String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public void onClick(int itemPosition, long itemId) {
		setEventConfigString(matchPosition(getEventConfigString(), "position", itemPosition));
		runJs();
		setEventConfigString(matchPosition(getEventConfigString(), itemPosition, "position"));
	}

}
