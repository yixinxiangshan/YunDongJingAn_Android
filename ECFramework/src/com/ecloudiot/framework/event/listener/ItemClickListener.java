package com.ecloudiot.framework.event.listener;

import java.util.HashMap;

import com.ecloudiot.framework.utility.LogUtil;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ItemClickListener extends BaseEventListener implements OnItemClickListener {
	private final static String TAG = "ItemClickListener";

	public ItemClickListener(Object pageContext, Object widget, String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		LogUtil.d(TAG, "onItemClick : "+position);
		Log.d(TAG, getEventConfigString());
		HashMap<String, String> map = new HashMap<String,String>();
		map.put("position", position+"");
		setEventConfigString(matchPosition(getEventConfigString(), "position", position));
		runJs(map);
		setEventConfigString(matchPosition(getEventConfigString(), position, "position"));
	}

}
