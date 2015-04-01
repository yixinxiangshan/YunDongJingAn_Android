package com.ecloudiot.framework.utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import org.apache.http.Header;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings.Secure;
import android.view.View;
import android.widget.Toast;

import com.ecloudiot.framework.Iinterface.IUserInfo;
import com.ecloudiot.framework.Iinterface.IUserSigned;
import com.ecloudiot.framework.appliction.ECApplication;
//import com.ecloudiot.framework.javascript.JsManager;
import com.ecloudiot.framework.javascript.JsUtility;
import com.ecloudiot.framework.javascript.JsViewUtility;
import com.ecloudiot.framework.utility.http.HttpAsyncClient;
import com.ecloudiot.framework.utility.http.HttpAsyncHandler;
import com.ecloudiot.framework.utility.http.NetUtil;
import com.ecloudiot.framework.view.MaterialDialog;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BinaryHttpResponseHandler;

public class AppUtil {
	private final static String TAG = "AppUtil";

	private static String pushToken = null;
	private static final String INSTALLATION = "PTOKEN";

	public static void inflateApp(String projectId) {
		JsViewUtility.showLoadingDialog("请稍等", "正在加载相关资源...", "false");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("method", "project/projectapps/get_son_appinfo");
		params.put("project_num", projectId);
		LogUtil.i(TAG, "inflateApp : projectId = " + projectId);
		HttpAsyncClient.Instance().post("", params, new HttpAsyncHandler() {

			private void showError() {
				JsViewUtility.closeLoadingDianlog();
				Toast.makeText(IntentUtil.getActivity(), "项目信息获取错误...", Toast.LENGTH_SHORT).show();
			};

			@Override
			public void onSuccess(String response) {
				if (StringUtil.isEmpty(response)) {
					showError();
					return;
				}
				JsonObject dataJsonObject = (JsonObject) (new JsonParser()).parse(response);
				if (dataJsonObject == null || dataJsonObject.isJsonNull()) {
					LogUtil.e(TAG, "response data from net is null ");
					showError();
					return;
				}
				String api_key = (dataJsonObject.get("api_key") == null || dataJsonObject.get("api_key").isJsonNull()) ? "" : dataJsonObject.get("api_key")
				        .getAsString();
				String api_secret = (dataJsonObject.get("api_secret") == null || dataJsonObject.get("api_secret").isJsonNull()) ? "" : dataJsonObject.get(
				        "api_secret").getAsString();
				if (StringUtil.isEmpty(api_key) || StringUtil.isEmpty(api_secret)) {
					showError();
					return;
				}
				JsUtility.setPreference("api_key", api_key);
				JsUtility.setPreference("api_secret", api_secret);
				JsUtility.setPreference("infalateSon", "true");
				decorateApp();
			}

			@Override
			public void onFailure(String response) {
				showError();
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

	public static void decorateApp() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("method", "project/projects/detail");
		HttpAsyncClient.Instance().post("", params, new HttpAsyncHandler() {

			private void showError() {
				JsViewUtility.closeLoadingDianlog();
				Toast.makeText(IntentUtil.getActivity(), "项目信息获取错误...", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSuccess(String response) {
				if (StringUtil.isEmpty(response)) {
					showError();
					return;
				}
				LogUtil.d(TAG, " decorateApp onSuccess : response = " + response);
				String decorateAppConfig = response;
				putParam("decorateAppconfig", decorateAppConfig);
				FileUtil.putStringToFile(decorateAppConfig, IntentUtil.getActivity().getFilesDir() + "/decorate/", "decorate.json.txt");
				NetUtil.getNewToken();
				decorateRes(decorateAppConfig);
			}

			@Override
			public void onFailure(String response) {
				showError();
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

	public static void decorateRes(String decorateAppConfig) {
		JsonObject decorateJObject = (JsonObject) (new JsonParser()).parse(decorateAppConfig);
		String resPath = "";
		try {
			JsonObject configJObject = decorateJObject.get("configs").getAsJsonObject();
			resPath = configJObject.get("package_path").getAsString();
		} catch (Exception e) {
			LogUtil.e(TAG, "decorateRes error: " + e.toString());
			return;
		}
		if (StringUtil.isEmpty(resPath)) {
			return;
		}
		resPath = StringUtil.getResString("base_api_url") + resPath;
		AsyncHttpClient client = new AsyncHttpClient();
		String[] allowedContentTypes = new String[]{"application/zip"};
		client.get(resPath, new BinaryHttpResponseHandler(allowedContentTypes) {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
				LogUtil.d(TAG, "onSuccess : " + IntentUtil.getActivity().getFilesDir() + "/decorate/");
				FileUtil.putByteToFile(binaryData, IntentUtil.getActivity().getFilesDir() + "/decorate/", "decorate.zip");
				UnZipUtil unZipUtil = new UnZipUtil("decorate", IntentUtil.getActivity().getFilesDir() + "/decorate/", IntentUtil.getActivity().getFilesDir()
				        + "/decorate/");
				unZipUtil.addObserver(new Observer() {
					@Override
					public void update(Observable observable, Object data) {
						Toast.makeText(IntentUtil.getActivity(), "资源解压完成...", Toast.LENGTH_SHORT).show();
						// String pageId = (String) ReflectionUtil.invokeMethod(PageUtil.getNowPageContext(), "getParam", new String[]{"page_id"});
						// if (JsManager.getInstance().callJsMethodSync(pageId, "onInflateAppSuccess", null).equalsIgnoreCase("true")) {
						// return;
						// }
					}
				});
				unZipUtil.unzip();

			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {
				if (null != binaryData) {
					String string = new String(binaryData);
					LogUtil.e(TAG, "onFailure error: " + string);
				}
				LogUtil.e(TAG, "onFailure error: ...");
				Toast.makeText(IntentUtil.getActivity(), "下载-资源-压缩包错误...", Toast.LENGTH_SHORT).show();

			}
		});
	}

	/**
	 * 获取app 资源配置信息
	 * 
	 * @return Ohmer-Nov 28, 2013 10:04:20 AM
	 */
	public static String getDecorateConfig() {
		String decorateAppconfig = getParam("decorateAppconfig");
		if (StringUtil.isNotEmpty(decorateAppconfig)) {
			return decorateAppconfig;
		}
		decorateAppconfig = FileUtil.readFileString(IntentUtil.getActivity().getFilesDir() + "/decorate/", "decorate.json.txt");
		putParam("decorateAppconfig", decorateAppconfig);
		LogUtil.i(TAG, "getDecorateConfig : decorateAppconfig length = " + decorateAppconfig.length());
		return decorateAppconfig;
	}

	/**
	 * 验证用户是否通过 某项验证
	 * 
	 * @param itemName
	 *            项目名称
	 * @param callBack
	 *            回调类名，成功返回 callBack+".onSignSuccess"，反之：onSignFailed Ohmer-Dec 3, 2013 2:16:03 PM
	 */
	// public static void signUser(String itemName, final String callBack) {
	// signUser(itemName, new IUserSigned() {
	//
	// @Override
	// public void onSignedSuccess() {
	// JsManager.getInstance().callJsMethodSync(callBack, "onSignSuccess", null);
	// }
	//
	// @Override
	// public void onSignedFailed() {
	// JsManager.getInstance().callJsMethodSync(callBack, "onSignFailed", null);
	//
	// }
	// });
	// }

	public static void signUser(final String itemName, final IUserSigned signedCallBack) {
		String userInfo = getLocalUserInfo();
		if (StringUtil.isEmpty(userInfo)) {
			// post net to sign
			signUserNet(itemName, signedCallBack);
			return;
		}
		// use local info to confirm if signed
		signUser(userInfo, itemName, new IUserSigned() {

			@Override
			public void onSignedSuccess() {
				signedCallBack.onSignedSuccess();
			}

			@Override
			public void onSignedFailed() {
				// if signed failed local ,check net work if signed
				signUserNet(itemName, signedCallBack);
			}
		});
	}

	/**
	 * 验证的实际执行过程
	 * 
	 * @param userInfo
	 * @param itemName
	 *            需要验证的项目
	 * @param signedCallBack
	 *            Ohmer-Dec 3, 2013 3:02:15 PM
	 */
	private static void signUser(String userInfo, String itemName, IUserSigned signedCallBack) {
		if (StringUtil.isEmpty(userInfo)) {
			LogUtil.e(TAG, "onSuccess error: get user info is null");
			return;
		}
		LogUtil.i(TAG, "signUser : userInfo = " + userInfo);
		JsonObject dataJsonObject = null;
		try {
			dataJsonObject = (JsonObject) (new JsonParser()).parse(userInfo);
		} catch (JsonSyntaxException e) {
			LogUtil.e(TAG, "userInfo parsing to json object error : " + e.toString());
			return;
		}
		// start sign
		boolean siged = true;
		if (itemName.contains("nickname")) {
			JsonElement jElement = dataJsonObject.get("nickname");
			if (GsonUtil.isEmpty(jElement)) {
				siged = false;
				LogUtil.d(TAG, "signUser : nickname is null ..");
			}
		}
		if (itemName.contains("avatar")) {
			if (dataJsonObject.get("avatar").isJsonNull() || dataJsonObject.get("nickname") == null) {
				siged = false;
			} else {
				JsonElement jElement = dataJsonObject.get("avatar").getAsJsonObject().get("url");
				if (GsonUtil.isEmpty(jElement)) {
					siged = false;
					LogUtil.d(TAG, "signUser : avatar is null ..");
				}
			}
		}
		// 最后回调
		if (siged) {
			signedCallBack.onSignedSuccess();
			return;
		}
		signedCallBack.onSignedFailed();
	}

	/**
	 * 进行网络端的验证
	 * 
	 * @param itemName
	 * @param signedCallBack
	 *            Ohmer-Dec 3, 2013 3:04:54 PM
	 */
	public static void signUserNet(final String itemName, final IUserSigned signedCallBack) {
		LogUtil.d(TAG, "signUserNet : start ...");
		getNetUserInfo(new IUserInfo() {

			@Override
			public void onfalied() {
				// do nothing !
			}

			@Override
			public void onSuccess(String userInfo) {
				signUser(userInfo, itemName, signedCallBack);
			}
		});
	}

	/**
	 * 获取本地用户信息
	 * 
	 * @return Ohmer-Dec 3, 2013 3:42:25 PM
	 */
	public static String getLocalUserInfo() {
		String userInfo = getParam("userInfo");
		if (StringUtil.isNotEmpty(userInfo)) {
			return userInfo;
		}
		userInfo = FileUtil.readFileString(IntentUtil.getActivity().getFilesDir() + "/user/", "userinfo.json.txt");
		LogUtil.i(TAG, "getLocalUserInfo : userinfo length = " + userInfo.length());
		if (StringUtil.isEmpty(userInfo)) {
			return "";
		}
		putParam("userinfo", userInfo);
		return userInfo;
	}

	public static void getNetUserInfo(final IUserInfo userInfoCallBack) {
		LogUtil.d(TAG, "getNetUserInfo : start ...");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("method", "user/users/detail");
		HttpAsyncClient.Instance().post("", params, new HttpAsyncHandler() {

			@Override
			public void onSuccess(String response) {
				String userInfo = response;
				LogUtil.i(TAG, "getNetUserInfo onSuccess length = " + userInfo.length());
				putLocalUserInfo(userInfo);
				userInfoCallBack.onSuccess(userInfo);
			}

			@Override
			public void onFailure(String response) {
				Toast.makeText(IntentUtil.getActivity(), "网络错误，请售后重试...", Toast.LENGTH_SHORT).show();
				userInfoCallBack.onfalied();
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

	public static void putLocalUserInfo(String userInfo) {
		putParam("userInfo", userInfo);
		FileUtil.putStringToFile(userInfo, IntentUtil.getActivity().getFilesDir() + "/user/", "userinfo.json.txt");
	}

	public static void putParam(String key, String value) {
		ECApplication.getInstance().putParam(key, value);
	}

	public static String getParam(String key) {
		return ECApplication.getInstance().getParam(key);
	}

	/**
	 * 清除填充的字内容 Ohmer-Dec 19, 2013 9:22:24 AM
	 */
	public static void clearInflated() {
		// 重置状态、删除apiKey/toekn
		JsUtility.setPreference("api_key", "");
		JsUtility.setPreference("api_secret", "");
		JsUtility.setPreference("infalateSon", "false");
		// 删除下载资源文件
		FileUtil.delAllFile(IntentUtil.getActivity().getFilesDir() + "/decorate");
		// 删除缓存的userInfo
		FileUtil.delAllFile(IntentUtil.getActivity().getFilesDir() + "/user");
		// 删除缓存文件
		FileUtil.deleteCacheFile();
	}

	public static boolean isLogin() {
		String userName = ECApplication.getInstance().readConfig("userName");
		if (StringUtil.isNotEmpty(userName)) {
			return true;
		}
		return false;
	}

	public static String getUserName() {
		return ECApplication.getInstance().readConfig("userName");
	}

	public static void saveUserName(String userName) {
		if (StringUtil.isEmpty(userName)) {
			return;
		}
		ECApplication.getInstance().editConfig("userName", userName);
	}

	public static void clearUserName() {
		ECApplication.getInstance().editConfig("userName", "");
		ECApplication.getInstance().editConfig("access_token", "");
	}

	/**
	 * 获得唯一设备id
	 * 
	 * @return Ohmer-Jan 10, 2014 1:28:25 PM
	 */
	public synchronized static String getPushToken() {
		if (StringUtil.isEmpty(pushToken)) {
			File installation = new File(ECApplication.getInstance().getFilesDir(), INSTALLATION);
			try {
				if (!installation.exists())
					writeInstallationFile(installation);
				pushToken = readInstallationFile(installation);
			} catch (Exception e) {
				LogUtil.e(TAG, "getDeviceId error: " + e.toString());
			}
		}
		return pushToken;
	}

	private static String readInstallationFile(File installation) throws IOException {
		RandomAccessFile f = new RandomAccessFile(installation, "r");
		byte[] bytes = new byte[(int) f.length()];
		f.readFully(bytes);
		f.close();
		return new String(bytes);
	}

	private static void writeInstallationFile(File installation) throws IOException {
		FileOutputStream out = new FileOutputStream(installation);
		String androidId = Secure.getString(ECApplication.getInstance().getContentResolver(), Secure.ANDROID_ID);
		String time = TimeUtil.toString("MMddHHmm", new Date());
		String id = androidId + time.substring(0, 7);
		out.write(id.getBytes());
		out.close();
	}

	/**
	 * 检测远程版本信息
	 * 
	 * Ohmer-Jan 20, 2014 4:03:01 PM
	 */
	public static void checkRemoteVersion() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("method", "project.getappinfo");
		HttpAsyncClient.Instance().post("", params, new HttpAsyncHandler() {
			@Override
			public void onFailure(String response) {
			}

			@Override
			public void onSuccess(String response) {

				// JsViewUtility.MakeToast("checkRemoteVersion success");
				try {
					JsonObject resData = (JsonObject) (new JsonParser()).parse(response);
					JsonObject newEditionJObject = (JsonObject) resData.get("NewEdition");
					String netVersionNum = newEditionJObject.get("VersionNum").getAsString();
					String netVersionUrl = newEditionJObject.get("DownloadUrl").getAsString();
					if (StringUtil.isNotEmpty(netVersionNum)) {
						ECApplication.getInstance().editConfig(Constants.NET_VERSION_NUM, netVersionNum);
						ECApplication.getInstance().editConfig(Constants.NET_VERSION_URL, netVersionUrl);
					}
					// JsViewUtility.MakeToast("checkRemoteVersion success :"+netVersionNum);
				} catch (Exception e) {

					LogUtil.e(TAG, "checkRemoteVersion error: " + e.toString());
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
	 * 获取远程版本
	 * 
	 * @return Ohmer-Jan 21, 2014 10:29:43 AM
	 */
	public static float getRemoteVersion() {
		String netVersionNum = ECApplication.getInstance().readConfig(Constants.NET_VERSION_NUM);
		if (StringUtil.isNotEmpty(netVersionNum)) {
			return Float.parseFloat(netVersionNum);
		}
		return getAppVersion();
	}

	/**
	 * 获取当前版本信息
	 * 
	 * @return Ohmer-Jan 20, 2014 4:11:11 PM
	 */
	public static float getAppVersion() {
		PackageManager packageManager = ECApplication.getInstance().getNowActivity().getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(ECApplication.getInstance().getPackageName(), 0);
		} catch (NameNotFoundException e) {
			LogUtil.e(TAG, "NameNotFoundException:" + e.toString());
			return -1;
		}
		float versionNum = Float.parseFloat(packInfo.versionName);
		// LogUtil.d(TAG, "getAppVersion : versionNum = " + versionNum);
		return versionNum;
	}

	/**
	 * 检测是否有新版本，这里只管检测
	 * 
	 * @return Ohmer-Jan 20, 2014 4:11:23 PM
	 */
	public static boolean checkNewVersion() {
		float netVersionNum = getRemoteVersion();
		if (netVersionNum > getAppVersion()) {
			return true;
		} else {
			checkRemoteVersion();
			return false;
		}
	}

	public static void confirmDownloadNewVersion(String data) {
		if (StringUtil.isEmpty(data)) {
			data = "";
		}
		if (checkNewVersion()) {
			final MaterialDialog mMaterialDialog;
			mMaterialDialog = new MaterialDialog(ECApplication.getInstance().getNowActivity());
			mMaterialDialog.setTitle("发现新版本");
			mMaterialDialog.setMessage(data);
			mMaterialDialog.setPositiveButton("下载", new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mMaterialDialog.dismiss();
					IntentUtil.OpenWebBrowser(ECApplication.getInstance().readConfig(Constants.NET_VERSION_URL));
				}
			});

			mMaterialDialog.setNegativeButton("取消", new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mMaterialDialog.dismiss();

				}
			});

			mMaterialDialog.show();

		}
	}
	public static int getInitAppCount() {
		String countString = ECApplication.getInstance().readConfig(Constants.INIT_APP_COUNT);
		if (StringUtil.isEmpty(countString)) {
			ECApplication.getInstance().editConfig(Constants.INIT_APP_COUNT, "1");
			return 1;
		}
		int count = Integer.parseInt(countString);
		count++;
		ECApplication.getInstance().editConfig(Constants.INIT_APP_COUNT, String.valueOf(count));
		return count;
	}
}
