package com.ecloudiot.framework.event.listener;

import android.annotation.SuppressLint;

import com.ecloudiot.framework.event.linterface.OnLoadMoreListener;

@SuppressLint("HandlerLeak")
public class LoadMoreListener extends BaseEventListener implements OnLoadMoreListener {
	// private final static String TAG = "LoadMoreListener";

	public LoadMoreListener(Object pageContext, Object widget, String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public void OnLoadMore(int position) {
		setEventConfigString(matchPosition(getEventConfigString(), "position", position));
		runJs();
		setEventConfigString(matchPosition(getEventConfigString(), position, "position"));
	}

}
