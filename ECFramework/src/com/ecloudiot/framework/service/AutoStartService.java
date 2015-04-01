package com.ecloudiot.framework.service;
import com.ecloudiot.framework.utility.LogUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStartService extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		/*	Intent mIntent = new Intent(context,NotificationService.class);
		context.startService(mIntent);*/
		 LogUtil.i("Autostart", "started");
	}
}
