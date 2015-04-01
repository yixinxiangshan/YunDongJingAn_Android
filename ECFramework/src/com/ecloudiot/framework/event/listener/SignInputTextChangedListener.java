package com.ecloudiot.framework.event.listener;

import java.util.HashMap;

import android.text.Editable;
import android.util.Log;

import com.ecloudiot.framework.event.linterface.OnSignInputTextChangedListener;
import com.ecloudiot.framework.utility.StringUtil;

public class SignInputTextChangedListener extends BaseEventListener implements OnSignInputTextChangedListener {
	private final static String TAG = "SignInputTextChangedListener";
	
	public SignInputTextChangedListener(Object pageContext, Object widget, String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (StringUtil.isEmpty(s.toString())) {
			Log.d(TAG, "text is null ");
			return;
		}
		Log.d(TAG, "text : "+s);
		Log.d(TAG, getEventConfigString());
		HashMap<String, String> bundleData = new HashMap<String, String>();
		bundleData.put("inputText", String.valueOf(s));
		runJs(bundleData);
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

}
