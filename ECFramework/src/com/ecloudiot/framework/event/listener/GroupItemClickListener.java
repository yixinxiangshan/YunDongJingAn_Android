package com.ecloudiot.framework.event.listener;

import java.util.HashMap;

import com.ecloudiot.framework.event.linterface.OnGroupItemClickListener;
import com.ecloudiot.framework.utility.LogUtil;

public class GroupItemClickListener extends BaseEventListener implements OnGroupItemClickListener {
	private final static String TAG = "GroupItemClickListener";

	public GroupItemClickListener(Object pageContext, Object widget, String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public void onGroupItemClick(int groupId, int position) {
		LogUtil.d(TAG, "onGroupItemClick :position =  " + position + ", groupId = " + groupId);
		HashMap<String, String> map = new HashMap<String,String>();
		map.put("groupId", groupId+"");
		map.put("position", position+"");
		setEventConfigString(matchPosition(getEventConfigString(), "groupId", groupId));
		setEventConfigString(matchPosition(getEventConfigString(), "position", position));
		runJs(map);
		setEventConfigString(matchPosition(getEventConfigString(), groupId, "groupId"));
		setEventConfigString(matchPosition(getEventConfigString(), position, "position"));
	}

}
