package com.ecloudiot.framework.javascript;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.ecloudiot.framework.appliction.BaseApplication;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.event.DoActionEvent;
import com.ecloudiot.framework.utility.AppUtil;
import com.ecloudiot.framework.utility.Constants;
import com.ecloudiot.framework.utility.DensityUtil;
import com.ecloudiot.framework.utility.FileUtil;
import com.ecloudiot.framework.utility.IntentUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.http.NetUtil;

import de.greenrobot.event.EventBus;

/**
 * 提供给js 的 基础接口
 * 
 ***********/
@SuppressLint("DefaultLocale")
public class JsUtility {
	private static String TAG = "JsUtility";

	// 获取上下文
	public static Context GetAppContext() {
		return ECApplication.getInstance().getNowActivity().getApplicationContext();
	}

	public static BaseApplication GetApp() {
		return ECApplication.getInstance();
	}

	public static Activity GetActivityContext() {
		return IntentUtil.getActivity();
	}

	public static Context getServiceContext() {
		return ECApplication.getInstance().getServiceContext();
	}

	public static String getPackageName() {
		return ECApplication.getInstance().getPackageName();
	}

	public static String getDeviceId() {
		return ECApplication.getInstance().getDeviceId();
	}

	public static String getPreference(String key) {
		// LogUtil.d(TAG, "getPreference key :" + key + ", value : "+ ECApplication.getInstance().readConfig(key));
		return ECApplication.getInstance().readConfig(key);
	}

	public static void setPreference(String key, String value) {
		// LogUtil.d(TAG, "setPreference key: "+ key + ", vlaue: "+ value);
		ECApplication.getInstance().editConfig(key, value);
	}

	public static void PostEvent(String className, String methodName, String params1, String params2, String params3) {
		// LogUtil.d(TAG, "PostEvent methodName: "+ methodName + ", vlaue: "+ params3);
		EventBus.getDefault().post(new DoActionEvent(className, methodName, params1, params2, params3));
	}

	public static void PostEvent(String controller, String action, String params) {
		EventBus.getDefault().post(new DoActionEvent(controller, action, params));
	}

	public static void PostEvent(String url) {
		EventBus.getDefault().post(new DoActionEvent(url));
	}

	public static float GetAppVersion() {
		return AppUtil.getAppVersion();
	}

	public static String GetApiKey() {
		return NetUtil.getApiKey();
	}

	public static String GetApiSecret() {
		return NetUtil.getApiSecret();
	}

	public static Boolean GetDebugState() {
		return Constants.DEBUG;
	}

	public static int getResouceId(String viewid, String type) {
		return ResourceUtil.getIdFromContext(viewid, type);
	}

	public static int dip2px(float dipValue) {
		return DensityUtil.dipTopx(GetAppContext(), dipValue);
	}

	public static int px2dip(float pxValue) {
		return DensityUtil.pxTodip(GetAppContext(), pxValue);
	}

	/**
	 * 判断文件是否存在文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static Boolean testFile(String fileName) {
		return FileUtil.isCacheFileExist(fileName);
	}

	/**
	 * 从缓存文件中读取字符串
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readCacheString(String fileName) {
		return FileUtil.readFileFromCache(fileName);
	}

	/**
	 * 将字符串 存到缓存
	 * 
	 * @param data
	 * @param fileName
	 * @return
	 */
	public static boolean writeCacheString(String data, String fileName) {
		return FileUtil.putStringToCacheFile(data, fileName);
	}

	/**
	 * 清除缓存
	 */
	public static void clearCache() {
		FileUtil.clearCache();
	}

	/**
	 * 获取资源文件（asset、raw）
	 * 
	 * @param filename
	 * @return
	 */
	public static String getSysFileString(String filename) {
		return FileUtil.getSysFileString(filename);
	}

	/**
	 * 获取资源文件(src)
	 * 
	 * @param filename
	 * @return
	 */
	public static String getResourceString(String filename) {
		return FileUtil.getResourceFileString(filename);
	}

	/**
	 * 判断是否有网络
	 * 
	 * @return
	 */
	public static boolean hasNetWork() {
		return NetUtil.hasNetWork();
	}

	/**
	 * 网络post请求
	 * 
	 * @param urlStr
	 * @param params
	 * @param scopeid
	 * @return
	 */
	public static String HttpRequest(String urlStr, String params, String scopeid) {
		String returnObject = null;
		String postData = "";
		Uri uri = Uri.parse(urlStr + "?" + params);
		String apiVersion = StringUtil.isEmpty(uri.getQueryParameter("apiversion")) ? "1.0" : uri.getQueryParameter("apiversion");
		String methodName = uri.getQueryParameter("method");
		urlStr += apiVersion + "/" + methodName;
		// LogUtil.d(TAG, "Post url: " + urlStr + " ; data: " + params);
		HttpPost httpRequest = new HttpPost(urlStr);
		try {
			postData = params;
			httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8;");
			httpRequest.setEntity(new StringEntity(postData));
			HttpParams param = new BasicHttpParams();
			param.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			HttpResponse response = new DefaultHttpClient(param).execute(httpRequest);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				returnObject = EntityUtils.toString(response.getEntity());
			}
		} catch (ClientProtocolException e) {
			LogUtil.e(TAG, e.toString() + " send request failed");
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			LogUtil.e(TAG, "IOExcepiton: " + e.toString());
			e.printStackTrace();
			return null;
		}
		return returnObject;
	}

	/**
	 * 获取applaction静态Map对象的属性字的符串
	 */
	@SuppressLint("DefaultLocale")
	public static String getAppMapPropertyString(String propertyName) {
		try {
			BaseApplication app = BaseApplication.getInstance();
			String getAttribute = "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
			Method method = app.getClass().getMethod(getAttribute);
			@SuppressWarnings("unchecked")
			Map<String, String> map = (Map<String, String>) method.invoke(app);
			return map.toString();

		} catch (IllegalArgumentException e) {
			LogUtil.e(TAG, "IllegalArgumentException error:" + e.toString());
		} catch (SecurityException e) {
			LogUtil.e(TAG, "SecurityException error:" + e.toString());
		} catch (IllegalAccessException e) {
			LogUtil.e(TAG, "SecurityException error:" + e.toString());
		} catch (InvocationTargetException e) {
			LogUtil.e(TAG, "InvocationTargetException error:" + e.toString());
		} catch (Exception e) {
			LogUtil.e(TAG, "GetResouceId error:" + e.toString());
		}
		return "";
	}

	// 获取表单参数内容
	public static String getFormParams() {
		Map<String, String> map = null;
		// Map<String, String> map = BaseApplication.getInstance().getNowFormWidget().getInputValues();
		return mapToQueryString(map);
	}

	public static String mapToQueryString(Map<String, String> map) {
		StringBuilder string = new StringBuilder();
		if (map.size() > 0) {
			string.append("?");
		}
		for (Entry<String, String> entry : map.entrySet()) {
			string.append(entry.getKey());
			string.append("=");
			string.append(entry.getValue());
			string.append("&");
		}
		return string.toString();
	}

	// 卸载app
	public static void uninstallApp(String packageName) {
		Activity context = BaseApplication.getInstance().getNowActivity();
		Uri packageURI = Uri.parse("package:" + packageName);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(uninstallIntent);
	}
	// public static void uninstallApp(final Context context, final String uninstallPackage, final String appName) {
	// DialogFactory.getInstance().showConfirmDialog(context, null, appName + "已全新升级，系统检测到您装有" + appName + "旧版本的软件，请点击“确认”卸载旧版软件。", new DialogListener() {
	//
	// @Override
	// public void confirmGo() {
	// AppUtil.uninstallApp(context, uninstallPackage);
	// mHandler.sendEmptyMessage(UNINSTALL_APP_OK);
	// }
	//
	// @Override
	// public void cancelGo() {
	// mHandler.sendEmptyMessage(UNINSTALL_APP_CANCEL);
	// }
	// });
	// }
	// 查询是否有旧版本软件需要卸载
	public static boolean checkInstalledPackage(String packagename) {
		Context context = BaseApplication.getInstance().getApplicationContext();
		ArrayList<PackageInforms> pkgList = getInstalledAppInform(context);
		if (pkgList != null) {
			for (int i = 0; i < pkgList.size(); i++) {
				if (packagename.equals(pkgList.get(i).pname)) {
					// uninstallApp(packagename);
					return true;
				}
			}
		}
		return false;
	}
	private static class PackageInforms {
		public String appname = null;
		public String pname = null;
		public String versionName = null;
		public int versionCode = 0;
		@SuppressWarnings("unused")
		public Drawable icon = null;

		@SuppressWarnings("unused")
		public void PrintInform() {
			LogUtil.i("taskmanger", appname + "\t" + pname + "\t" + versionName + "\t" + versionCode + "\t");
		}
	}
	public static ArrayList<PackageInforms> getInstalledAppInform(Context context) {
		PackageInforms newInfo = null;
		PackageInfo pi = null;
		ArrayList<PackageInforms> res = new ArrayList<PackageInforms>();
		List<android.content.pm.PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++) {
			pi = packs.get(i);
			if (pi.versionName == null) {
				continue;
			}
			newInfo = new PackageInforms();
			newInfo.appname = pi.applicationInfo.loadLabel(context.getPackageManager()).toString();
			newInfo.pname = pi.packageName;
			newInfo.versionName = pi.versionName;
			newInfo.versionCode = pi.versionCode;
			res.add(newInfo);
		}
		return res;
	}
}