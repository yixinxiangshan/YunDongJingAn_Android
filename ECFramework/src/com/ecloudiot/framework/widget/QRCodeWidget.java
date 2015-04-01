package com.ecloudiot.framework.widget;

import com.ecloudiot.framework.event.listener.CaptureQRCodeListener;
import com.ecloudiot.framework.utility.LogUtil;
import com.google.zxing.client.android.CaptureActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

@SuppressLint("ViewConstructor")
public class QRCodeWidget extends BaseWidget {
	private final static String TAG = "QRCodeWidget";

	public QRCodeWidget(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		LogUtil.d(TAG, "QRCodeWidget : start activity ...");
		parsingData();
		CaptureQRCodeListener captureQRCodeListener = new CaptureQRCodeListener(ctx, this, "");
		Bundle mBundle = new Bundle(); 
		mBundle.putSerializable("captureQRCodeListener", captureQRCodeListener);
		Intent intent = new Intent(ctx, CaptureActivity.class);
//		intent.putExtras(mBundle);
		ctx.startActivity(intent);
	}

}
