package com.ecloudiot.framework.activity;

import java.util.Observable;
import java.util.Observer;

import org.apache.http.Header;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.javascript.JsViewUtility;
import com.ecloudiot.framework.utility.AppUtil;
import com.ecloudiot.framework.utility.Constants;
import com.ecloudiot.framework.utility.FileUtil;
import com.ecloudiot.framework.utility.IntentUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.PageUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.UnZipUtil;
import com.ecloudiot.framework.utility.http.NetUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

public class SplashActivity extends Activity implements Observer {
	private static String TAG = "SplashActivity";
	public static final String IS_START_PUSH_SERVICE = "with_push_service";
	private JsonObject mPageJsonObject;
	private String pageName;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 崩溃分析
		TestinAgent.init(this, "9ce52214323a91e69481e1a666c9b7a1", Constants.CHANNEL);
		// 友盟统计
		AnalyticsConfig.setAppkey("54717128fd98c52e0a000507");
		AnalyticsConfig.setChannel(Constants.CHANNEL);
		MobclickAgent.setDebugMode(false);
//		UmengUpdate();
		// 极光推送
		JPushInterface.init(getApplicationContext());
		// for (int i = 0; i < 10; i++) {
		// JPushLocalNotification ln = new JPushLocalNotification();
		// ln.setBuilderId(i);
		// ln.setContent("今天八点记得吃药");
		// ln.setTitle("温馨提醒" + i);
		// ln.setNotificationId(10000000 + i);
		// ln.setBroadcastTime(System.currentTimeMillis() + 1000 * 60 + i * 1000 * 30);
		// JPushInterface.addLocalNotification(getApplicationContext(), ln);
		// }

		// Map<String, Object> map = new HashMap<String, Object>();
		// map.put("name", "jpush");
		// map.put("test", "111");
		// JSONObject json = new JSONObject(map);
		// ln.setExtras(json.toString());

		ECApplication.getInstance().setOnActivityCreate(this);
		
		this.setContentView(R.layout.activity_splash);
		LogUtil.i(TAG, "onCreate : splashactivity ....");
		if (Constants.DEBUG) {
			Toast.makeText(this, "当前状态为调试模式...", Toast.LENGTH_SHORT).show();
		}
		if (Constants.DEBUG) {
			if (!NetUtil.hasNetWork()) {
				Toast.makeText(this, "没有可用网络，请确保网络正常...", Toast.LENGTH_SHORT).show();
				handleConfigsFailure();
				return;
			}
			String resoursePackage = getConfigJsUrl().substring(getConfigJsUrl().lastIndexOf("=") + 1);
			JsViewUtility.showLoadingDialog("请稍等", "下载配置最新包...\n[" + resoursePackage + "][" + AppUtil.getAppVersion() + "]\n", "false");
			AsyncHttpClient client = new AsyncHttpClient();
			String[] allowedContentTypes = new String[]{"application/zip"};
			client.get(getConfigJsUrl(), new BinaryHttpResponseHandler(allowedContentTypes) {

				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
					JsViewUtility.closeLoadingDianlog();
					LogUtil.d(TAG, "onSuccess : " + getFilesDir() + "/");
					FileUtil.putByteToFile(binaryData, getFilesDir() + "/", "js.zip");
					UnZipUtil unZipUtil = new UnZipUtil("js", getFilesDir() + "/", getFilesDir() + "/");
					unZipUtil.addObserver(SplashActivity.this);
					unZipUtil.unzip();
				}
				
				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
					JsViewUtility.closeLoadingDianlog();
					if (null != binaryData) {
						String string = new String(binaryData);
						LogUtil.e(TAG, "onFailure error: " + string);
					}
					handleConfigsFailure();
				}
			});
		} else {
			Handler handler = new Handler();
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					startIndexPage();
					UmengUpdate();
//					innerTest();
				}
			};
			handler.postDelayed(runnable, 1000);
		}
	}

	//友盟更新
	private void UmengUpdate(){
//		UmengUpdateAgent.setAppkey("");
//		UmengUpdateAgent.setChannel(Constants.CHANNEL);
//		UmengUpdateAgent.update(SplashActivity.this);
//		UmengUpdateAgent.setUpdateOnlyWifi(false);
	}
	
	//蒲公英内测
	private void innerTest(){
		if(Constants.INNER_TEST){
			PgyCrashManager.register(SplashActivity.this,Constants.PGY_APP_KEY);
			PgyFeedbackShakeManager.register(SplashActivity.this, Constants.PGY_APP_KEY);
		}
	}
	
	private String getConfigJsUrl() {
		String configJsUrl = ECApplication.getInstance().readConfig("configJsUrl");
		if (StringUtil.isNotEmpty(configJsUrl)) {
			LogUtil.d(TAG, "getConfigJsUrl : " + configJsUrl);
			return configJsUrl;
		}
		return getResources().getString(R.string.config_js_url);
	}

	@Override
	protected void onDestroy() {
		ECApplication.getInstance().setOnActivityDestroy(this);
		super.onDestroy();
	}

	@Override
	public void update(Observable observable, Object data) {
		startIndexPage();
	}

	private void startIndexPage() {
		// 解析 app 配置文件
		String pageString = PageUtil.getPageData("appconfig");
		if (StringUtil.isEmpty(pageString)) {
			LogUtil.e(TAG, "startIndexPage : can not find app config");
			return;
		}
		JsonParser jsonParser = new JsonParser();
		try {
			mPageJsonObject = (JsonObject) jsonParser.parse(pageString);
		} catch (JsonSyntaxException e) {
			LogUtil.e(TAG, "startIndexPage : app config is not valid json");
			return;
		}
		pageName = mPageJsonObject.get("start_controller").getAsString();
		JsonElement guideElement = mPageJsonObject.get("guide_page");
		String guidePage = "";
		if (guideElement != null && guideElement.isJsonPrimitive()) {
			guidePage = guideElement.getAsString();
		}
		
		// 检查远程版本信息
		// AppUtil.checkRemoteVersion();

		// 是否启动服务
		// JsonElement sElement = mPageJsonObject.get("startPushService");
		// String isStartService = "false";
		// if (null == sElement) {
		// LogUtil.w(TAG, "获取启动推送服务配置失败....");
		// }
		// isStartService = sElement.getAsString();
		// JsUtility.setPreference(IS_START_PUSH_SERVICE, isStartService);
		// if ("true".equals(isStartService)) {
		// LogUtil.i(TAG, "start service ...");
		// ECApplication.getInstance().editConfig(ECApplication.CONFIG, AppUtil.getPushToken());
		// }

		// 是否启动 引导界面或首界面
		if (AppUtil.getInitAppCount() == 1 && StringUtil.isNotEmpty(guidePage)) {
			IntentUtil.openActivityWithFinished("", guidePage, "");
			return;
		}
		IntentUtil.openActivityWithFinished("", pageName, "");
	}

	private void handleConfigsFailure() {
		final EditText editText = (EditText) SplashActivity.this.getLayoutInflater().inflate(R.layout.widget_form_input_text, null);
		editText.setText(getConfigJsUrl());
		AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
		builder.setTitle("下载失败!").setMessage("当前下载地址：" + getConfigJsUrl() + "\n请输入新地址:\n确定:关闭重启\n取消:尝试使用历史包。");
		builder.setView(editText);
		builder.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (StringUtil.isNotEmpty(editText.getText().toString())) {
					ECApplication.getInstance().editConfig("configJsUrl", editText.getText().toString());
					JsViewUtility.closeActivity();
				}
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					startIndexPage();
				} catch (Exception e) {
					LogUtil.e(TAG, "onClick error: " + e.toString());
					e.printStackTrace();
				}
			}
		});
		builder.create().show();
	}
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("SplashScreen"); // 统计页面
		MobclickAgent.onResume(this); // 统计时长
	}
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("SplashScreen"); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);// 友盟统计
	}
}
