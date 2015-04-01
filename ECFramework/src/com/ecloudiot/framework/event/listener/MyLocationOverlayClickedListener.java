package com.ecloudiot.framework.event.listener;

import android.util.Log;

import com.ecloudiot.framework.event.linterface.OnMyLocationOverlayClickedListener;

public class MyLocationOverlayClickedListener extends BaseEventListener implements OnMyLocationOverlayClickedListener{

	private static String TAG = "MyLocationOverlayClickedListener";
	public MyLocationOverlayClickedListener(Object pageContext, Object widget,
			String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public boolean dispatchTap() {
		Log.d(TAG, "dispatchTap start...");
		runJs();
		return false;
	}

}
