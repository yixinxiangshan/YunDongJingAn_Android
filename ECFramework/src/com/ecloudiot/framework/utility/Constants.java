package com.ecloudiot.framework.utility;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.appliction.ECApplication;
import android.os.Environment;

public class Constants {
	public static final boolean DEBUG = false;
	public static final boolean ENCRYPT = false;//是否加密
	public static final String CHANNEL = "publish20150212";
	// js engine config
	public static final int JS_ENGINE_RIHNO = 0;
	public static final int JS_ENGINE_JV8 = 1;
	public static final int JS_ENGINE_J2V8 = 2;
	// 蒲公英内测
	public static final boolean INNER_TEST = false;
	public static final String PGY_APP_KEY = "47133dd70461da2af194462f84a4fc7b";

	// public static final String CHANNEL = "20150108";
	// public static final String BASE_API_SERVER = "http://211.138.248.114/";
	// public static final String BASE_API_SERVER = "http://openapi.nowapp.cn/";
	public static final String BASE_API_SERVER = ECApplication.getInstance().getResources().getString(R.string.base_api_url);
	public static final String BASE_API_OAUTH = BASE_API_SERVER + "oauth/token";
	public static final String BASE_API = BASE_API_SERVER + "api";
	public static final String BASE_URL_IMAGE = "http://is.hudongka.com/";
	public static final String NOTI_TAG_DEFAULT = "pecct://app/checkPushInfo";
	// the IP address, where your MQTT broker is running.
	public static final String PUSH_MQTT_HOST = "android.push.nowapp.cn";
	// the port at which the broker is running.
	public static int PUSH_MQTT_BROKER_PORT_NUM = 1884;
	public static final String CACHE_ROOT_DIR_STRING = Environment.getExternalStorageDirectory() + "/ecloudCache/"
	        + ECApplication.getInstance().getPackageName();
	public static final String CACHEFILE_DIRPATH = CACHE_ROOT_DIR_STRING + "/cachefile/";
	public static final String IMAGE_NAME_CONTAIN = "image";
	public static final String IMAGE_DEFAULT_SIMAGE = "3002094.png";
	public static final String IMAGE_DEFAULT_IMAGE = "3002095.png";
	public static final String IMAGE_DEFAULT_LOADING = "3002096.png";
	public static final int SPLASHIMGID = R.drawable.proj_activity_splash_background;

	// message tag
	public final static String MESSAGE_TAG_CAMERA = "message_tag_camera";
	public final static String MESSAGE_TAG_ALBUM = "message_tag_album";
	public final static String MESSAGE_TAG_CAMERA_ACTIVITY = "message_tag_camera_activity";
	public final static String MESSAGE_TAG_LOCATION = "message_tag_location";
	public final static String MESSAGE_TAG_TOKEN_RECEIVED = "message_tag_token_received";

	public final static String server_url = "https://msp.alipay.com/x.htm"; // 支付宝支付服务地址
	// 上传文件URL
	public final static String ADDOVERLAYURL = "";
	// 请求码
	public final static int CAMERA = 0;
	public final static int PHOTO = 1;
	public final static int QRCAPTURE = 2;
	public final static int NEWITEMACTIVITY = 3;
	public final static int CAMERAACTIVITY = 4;

	// 其他
	public static String INIT_APP_COUNT = "init_app_count";
	public static String NET_VERSION_NUM = "net_version_num";
	public static String NET_VERSION_URL = "net_version_url";
}
