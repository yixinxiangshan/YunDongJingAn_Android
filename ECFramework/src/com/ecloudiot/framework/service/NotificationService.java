package com.ecloudiot.framework.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.event.DoActionEvent;
import com.ecloudiot.framework.utility.Constants;
import com.ecloudiot.framework.utility.LogUtil;

import de.greenrobot.event.EventBus;

public class NotificationService extends Service {
	private String TAG = "NotificationService";
	private final static int LOOPTIME = 30 * 1000; // 30秒
	private static boolean isRunThread = false;
	private int intCounter = 0;

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		LogUtil.d(TAG, "service onStart");
		ECApplication.getInstance().setServiceContext(this);
		runThread();

	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 开线程循环下载推送信息
	 */
	public void runThread() {
		Thread thread = new Thread() {
			public void run() {
				while (isRunThread) {
					try {
						Thread.sleep(LOOPTIME);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					intCounter++;
					LogUtil.d(TAG, "ServiceCount: " + intCounter + "");
					EventBus.getDefault().post(new DoActionEvent(Constants.NOTI_TAG_DEFAULT));
				}
			}
		};
		thread.start();
	}
}
