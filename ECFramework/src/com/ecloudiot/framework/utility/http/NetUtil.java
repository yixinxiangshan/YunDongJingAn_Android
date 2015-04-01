package com.ecloudiot.framework.utility.http;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.javascript.JsUtility;
import com.ecloudiot.framework.utility.FileUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.RequestParams;

import de.greenrobot.event.EventBus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

@SuppressLint("SimpleDateFormat")
public class NetUtil {
	private final static String TAG = "NetUtil";

	/**
	 * 判断是否有网络
	 * 
	 * @return
	 */
	public static boolean hasNetWork() {
		ConnectivityManager cm = (ConnectivityManager) ECApplication.getInstance().getNowActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		LogUtil.e(TAG, "hasNetWork : false");
		return false;
	}

	/**
	 * 获取Token
	 */
	public static String getToken() {
		String access_token = ECApplication.getInstance().readConfig("access_token");
		if (StringUtil.isEmpty(access_token) || 32 != access_token.length()) {
			access_token = ECApplication.getInstance().readConfig("access_token");
		}
		return access_token;
	}

	public static String getApiKey() {
		String api_key = StringUtil.getResString("api_key");
		String infalateSon = JsUtility.getPreference("infalateSon");
		if (infalateSon != null && infalateSon.equalsIgnoreCase("true")) {
			api_key = JsUtility.getPreference("api_key") == null ? api_key : JsUtility.getPreference("api_key");
		}
		return api_key;
	}

	public static String getApiSecret() {
		String api_secret = StringUtil.getResString("api_secret");
		String infalateSon = JsUtility.getPreference("infalateSon");
		if (infalateSon != null && infalateSon.equalsIgnoreCase("true")) {
			api_secret = JsUtility.getPreference("api_secret") == null ? api_secret : JsUtility.getPreference("api_secret");
		}
		return api_secret;
	}

	public static void getNewToken() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("grant_type", "device");
		params.put("client_id", getApiKey());
		params.put("method", "token");
		params = getFullParams(params);
		HttpAsyncClient.Instance().post("oauth/token/", params, new HttpAsyncHandler() {

			@Override
			public void onFailure(String response) {
			}

			@Override
			public void onSuccess(String response) {
				LogUtil.i(TAG, "getNewToken  onSuccess : response = " + response);
				String token = null;
				// String push_android_token = null;
				if (!checkError(response)) {
					JsonObject tokenJObject = (JsonObject) (new JsonParser()).parse(response);
					try {
						token = tokenJObject.get("access_token").getAsString();
						// LogUtil.d(TAG, "getNewToken：token=" + token);
						// push_android_token = tokenJObject.get("push_android_token").getAsString();
						// LogUtil.d(TAG, "getNewToken：push_android_token=" + push_android_token);
					} catch (Exception e) {
						LogUtil.e(TAG, "getToken error: " + e.toString());
					}
					if (StringUtil.isNotEmpty(token) && 32 == token.length()) {
						JsUtility.setPreference("access_token", token);
						EventBus.getDefault().post(new onTokenReceivedEvent());
						// MessageUtil.instance().sendMessage(Constants.MESSAGE_TAG_TOKEN_RECEIVED, token);
					}
				}
			}

			@Override
			public void onProgress(Float progress) {

			}

			@Override
			public void onResponse(String resopnseString) {
				// TODO Auto-generated method stub

			}

		}, 0);
	}

	/**
	 * 检测访问API是否出错,
	 * 
	 * @param responseString
	 * @return 访问错误则返回true
	 */
	public static boolean checkError(String responseString) {
		if (StringUtil.isEmpty(responseString)) {
			return true;
		}
		JsonObject jObject = (JsonObject) (new JsonParser()).parse(responseString);
		String success = (jObject.get("success") == null || jObject.get("success").isJsonNull()) ? "" : jObject.get("success").getAsString();
		if (StringUtil.isNotEmpty(success) && success.equalsIgnoreCase("false")) {
			LogUtil.e(TAG, "checkError error: responseString = " + responseString);
			return true;
		}
		return false;
	}

	/**
	 * 创建callId 用时间生成
	 * */
	public static String createCallID() {
		Date nowDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("ssyyyyMMmmHHdd");
		return dateFormat.format(nowDate) + (0 + (int) (Math.random() * 0xffff));
	}

	public static HashMap<String, String> getFullParams(HashMap<String, String> params) {
		if (params == null) {
			return null;
		}
		// 设置 APIVersion
		if (StringUtil.isEmpty(params.get("apiversion"))) {
			params.put("apiversion", "1.0");
		}
		// 设置 返回格式
		params.put("format", "json");
		// 设置 api_key
		params.put("api_key", getApiKey());
		// 添加 call id
		params.put("call_id", createCallID());
		// 设置 sign
		params.put("sig", getSign(params));
		// 添加 deviceNumber 参数
		params.put("devicenumber", StringUtil.getDeviceId());
		// 设置 token
		params.put("access_token", ECApplication.getInstance().readConfig("access_token"));
		// LogUtil.d(TAG, "getFullParams : params = " + params);
		return params;
	}

	/**
	 * 生成验证码
	 * 
	 * @param map
	 * @return
	 */
	private static String getSign(HashMap<String, String> map) {
		String string = "api_key=" + getApiKey();
		string += "call_id=" + map.get("call_id");
		string += "method=" + map.get("method") + getApiSecret();
		// LogUtil.d(TAG, "getSign : string = " + string);
		return StringUtil.calcMD5String(string);
	}

	public static RequestParams getRequestParams(HashMap<String, String> params) {
		RequestParams requestParams = new RequestParams(params);
		for (Map.Entry<String, String> entry : params.entrySet()) {
			if (entry.getValue().startsWith("file://")) {
				String fileName = entry.getValue().substring(entry.getValue().lastIndexOf("/"), entry.getValue().length());
				requestParams.put(entry.getKey(), FileUtil.readFileFromUri(entry.getValue()), fileName);
			}
		}
		return requestParams;
	}
}
