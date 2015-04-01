package com.ecloudiot.framework.activity;

import com.ecloudiot.framework.event.DoActionEvent;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.NotificationUtil;

import de.greenrobot.event.EventBus;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class NotiActivity extends ItemActivity {
	private static String TAG = "NotiActivity";
	private Bundle mBundle = null;
	public Context mContext = null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "start");
		// 初始化
		mBundle = this.getIntent().getExtras();
		String uri = mBundle.getString(NotificationUtil.NOTI_ACTIVITY_TAG);
		LogUtil.d(TAG, "uri:" + uri);
		EventBus.getDefault().post(new DoActionEvent(uri));
	}

	@Override
	protected void onResume() {
		super.onResume();

	}
}
