package com.ecloudiot.framework.event.listener;

import java.io.Serializable;

import android.widget.Toast;

import com.ecloudiot.framework.utility.IntentUtil;
import com.google.zxing.client.android.OnCaptureQRCodeListener;

public class CaptureQRCodeListener extends BaseEventListener implements  Serializable,OnCaptureQRCodeListener {

	private static final long serialVersionUID = 304623590841109095L;

	public CaptureQRCodeListener(Object pageContext, Object widget, String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public void OnCapture(String codeString) {
		Toast.makeText(IntentUtil.getActivity(), "codeString = "+codeString, Toast.LENGTH_SHORT).show();
	}

}
