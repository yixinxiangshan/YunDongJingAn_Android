package com.ecloudiot.framework.event.listener;

import android.util.Log;
import android.view.View;

import com.ecloudiot.framework.event.linterface.OnSecondaryMenuItemClickListener;

public class SecondaryMenuItemClickListener extends BaseEventListener implements OnSecondaryMenuItemClickListener{
	
	private final static String TAG = SecondaryMenuItemClickListener.class.getSimpleName();

	public SecondaryMenuItemClickListener(Object pageContext, Object widget,
			String eventConfigString) {
		super(pageContext, widget, eventConfigString);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onItemClick(View view, int groupId, int position) {
		Log.d(TAG, "groupId:" + groupId + ",position:" + position);
		if(position == -1) {
			if(getEventConfigString().contains(".subMenuModelList[position].")) {
				String eventConfigString = getEventConfigString().replace(".subMenuModelList[position].", ".");
				setEventConfigString(eventConfigString);
			}
		}
		setEventConfigString(matchPosition(getEventConfigString(), "groupId", groupId));
		setEventConfigString(matchPosition(getEventConfigString(), "position", position));
		runJs();
		setEventConfigString(matchPosition(getEventConfigString(), groupId, "groupId"));
		setEventConfigString(matchPosition(getEventConfigString(), position, "position"));
	}

}
