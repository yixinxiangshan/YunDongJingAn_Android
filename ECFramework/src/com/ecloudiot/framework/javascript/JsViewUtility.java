package com.ecloudiot.framework.javascript;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.BaseActivity;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.utility.AppUtil;
import com.ecloudiot.framework.utility.FileUtil;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.IntentUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.PageUtil;
import com.ecloudiot.framework.utility.ReflectionUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.ViewUtil;
import com.ecloudiot.framework.utility.WidgetUtil;
import com.ecloudiot.framework.utility.http.HttpAsyncClient;
import com.ecloudiot.framework.utility.http.NetUtil;
import com.ecloudiot.framework.widget.BaseWidget;

/**
 * 提供给js 的 view 相关的接口
 **/
public class JsViewUtility {
	private static String TAG = "JsViewUtility";

	/**
	 * 清理 view 的子试图
	 * 
	 * @param viewId
	 */
	public static void clearView(String viewId) {
		ViewUtil.ClearView(viewId);
	}

	/**
	 * 弹出 确认框
	 * 
	 * @param message
	 * @param okTag
	 * @param cancelTag
	 */
	public static void showConfirm(String message, String okTag, String cancelTag) {
		ViewUtil.ShowConfirm(message, okTag, cancelTag);
	}

	public static void showConfirm(String message) {
		ViewUtil.ShowConfirm(message);
	}

	public static void inflateApp(String projectId) {
		AppUtil.inflateApp(projectId);
	}

	public static void callWidgetMethod(String controlId, String methodName, String param1, String param2) {
		LogUtil.i(TAG, "callWidgetMethod : ...");
		BaseWidget widget = ViewUtil.getWidget(controlId);
		String[] params = null;
		if (param2 != null) {
			params = new String[]{param1, param2};
		} else if (param1 != null) {
			params = new String[]{param1};
		}
		ReflectionUtil.invokeMethod(widget, methodName, params);
	}

	/**
	 * 向 listview 中添加更多获取到的数据
	 * 
	 * @param data
	 * @param widgetName
	 * @param viewId
	 */
	public static void addDataIntoWidget(String data, String viewId, String fatherViewId) {
		String dataString = "{\"itemList\": [{\"image\": \"3002263.jpg\"}]}";
		ViewUtil.addDataIntoWidget(dataString, "gallery_widget", fatherViewId);
	}

	/**
	 * 获得唯一设备id
	 * 
	 * @return Ohmer-Jan 10, 2014 1:28:25 PM
	 */
	public static String getPushToken() {
		return AppUtil.getPushToken();
	}

	/**
	 * 刷新控件数据
	 * 
	 * @param data
	 * @param widgetName
	 * @param viewId
	 */
	public static void refershDataForWidget(String newData, String widgetId, String fatherViewId) {
		ViewUtil.refreshDataForWidget(newData, widgetId, fatherViewId);
	}

	public static void refreshWidget(String controlId) {
		ViewUtil.refreshWidget(controlId);
	}

	public static void refreshBadgeView(String newData, String widgetId, String fatherViewId) {
		// String dataString =
		// "{\"badgeDataList\": [{\"text\": \"9\",\"positon\": \"1\",\"isHide\": \"false\"},{\"text\": \"8\",\"positon\": \"2\",\"color\": \"#A4C639\",\"drawable\": \"\"}]}";
		ViewUtil.refreshBadgeView(newData, widgetId, fatherViewId);
	}

	/**
	 * 设置控件属性
	 * 
	 * @param attrsString
	 * @param widgetId
	 * @param fatherViewId
	 */
	public static void setAttrsForWidget(String attrsString, String widgetId, String fatherViewId) {
		ViewUtil.setAttrsForWidget(attrsString, widgetId, fatherViewId);
	}

	/**
	 * 弹出推送
	 * 
	 * @param context
	 * @param dataString
	 */
	/*
	 * public static void showNotification(String dataString) { LogUtil.d(TAG, dataString); Context mContext = ECApplication.getInstance().getServiceContext();
	 * if (StringUtil.isNotEmpty(dataString)) { NotificationUtil.showNotification(mContext, dataString); } else { LogUtil.e(TAG, "show notification error....");
	 * } }
	 */

	/**
	 * 打开网页
	 * 
	 * @param url
	 */
	public static void openWebBrowser(String url) {
		IntentUtil.OpenWebBrowser(url);

	}

	/**
	 * 调用内置拨号服务
	 * 
	 * @param number
	 */
	public static void callPhoneNumber(String number) {
		IntentUtil.callPhoneNumber(number);
	}

	/**
	 * 打开新的activity
	 * 
	 * @param className
	 * @param dataString
	 * @param uri
	 */
	public static void openActivity(String className, String pageName, String params) {
		IntentUtil.openActivity(className, pageName, params);
	}

	/**
	 * 打开新的activity,并关闭之前的activity
	 * 
	 * @param className
	 * @param dataString
	 * @param uri
	 */
	public static void openActivityWithFinished(String className, String dataString, String uri) {
		IntentUtil.openActivityWithFinished(className, dataString, uri);
	}

	/**
	 * 打开新的activity,并关闭之前所有的activity
	 * 
	 * @param className
	 * @param pageName
	 * @param params
	 *            Ohmer-Dec 19, 2013 9:35:32 AM
	 */
	public static void openActivityFinishedOthers(String className, String pageName, String params) {
		IntentUtil.openActivityFinishedOthers(className, pageName, params);
	}

	/**
	 * 关闭 当前 activity
	 */
	public static void closeActivity() {
		IntentUtil.closeActivity();
	}

	public static void closeActivity(String uri) {
		IntentUtil.closeActivity(uri);
	}

	/**
	 * 发送 email
	 * 
	 * @param title
	 * @param mail
	 */
	public static void sendEmail(String title, String mail) {
		IntentUtil.sendEmail(title, mail);
	}

	/**
	 * 初始化 actionBar
	 * 
	 * @param dataString
	 */
	public static void initActionBar(String dataString) {
		try {
			LogUtil.d(TAG, "initActionBar dataString" + dataString);
			((BaseActivity) ECApplication.getInstance().getNowActivity()).initActionBar(dataString);
		} catch (Exception e) {
			LogUtil.e(TAG, "initActionBar error: dataString is invalid or now activity is no extends from BaseActivity...");
			e.printStackTrace();
		}
	}

	/**
	 * 添加 action item
	 * 
	 * @param dataString
	 */
	public static void addActionItem(String dataString) {
		try {
			LogUtil.d(TAG, "addActionItem dataString:" + dataString);
			((BaseActivity) ECApplication.getInstance().getNowActivity()).addActionItem(dataString);
		} catch (Exception e) {
			e.printStackTrace();
			LogUtil.e(TAG, "addActionItem error: dataString is invalid or now activity is no extends from BaseActivity...");
		}
	}

	/**
	 * 根据itemId 删除 ActionItem
	 * 
	 * @param itemId
	 */
	public static void removeActionItem(String itemId) {
		try {
			((BaseActivity) ECApplication.getInstance().getNowActivity()).removeActionItem(itemId);
		} catch (Exception e) {
			LogUtil.e(TAG, "removeActionItem error: dataString is invalid or now activity is no extends from BaseActivity...");
		}
	}

	/**
	 * 删除所有 ActionItem
	 */
	public static void removeAllActionItems() {
		try {
			((BaseActivity) ECApplication.getInstance().getNowActivity()).removeAllActionItems();
		} catch (Exception e) {
			LogUtil.e(TAG, "removeAllActionItems error: dataString is invalid or now activity is no extends from BaseActivity...");
		}
	}

	public static String getFormData(String controlId) {
		BaseWidget widget = ViewUtil.getWidget(controlId);
		if (widget != null) {
			return (String) ReflectionUtil.invokeMethod(widget, "getFormData", null);
		}
		return "";
	}

	/**
	 * 设置 actionBar title
	 * 
	 * @param title
	 */
	public static void setTitle(String title) {
		try {
			((BaseActivity) ECApplication.getInstance().getNowActivity()).setActionTitle(title);
		} catch (Exception e) {
			LogUtil.e(TAG, "setTitle error: now activity is no extends from BaseActivity...");
		}
	}

	/**
	 * 删除navList
	 */
	public static void removeNavList() {
		try {
			((BaseActivity) ECApplication.getInstance().getNowActivity()).removeNavList();
		} catch (Exception e) {
			LogUtil.e(TAG, "removeNavList error: now activity is no extends from BaseActivity...");
		}
	}

	/**
	 * 显示进度
	 * 
	 * @param progress
	 */
	public static void setProgress(String progressString) {
		LogUtil.e(TAG, "setProgress error: progressString = " + progressString);
		try {
			float progress = Float.parseFloat(progressString);
			((BaseActivity) ECApplication.getInstance().getNowActivity()).setProgress(progress);
		} catch (Exception e) {
			LogUtil.e(TAG, "setProgress error...");
		}
	}

	/**
	 * 显示 actionBar 加载框
	 * 
	 * @param visibleString
	 */
	public static void showProgressIndeterminateVisible(String visibleString) {
		LogUtil.d(TAG, "showProgressIndeterminateVisible : visibleString" + visibleString);
		try {
			boolean visible = Boolean.parseBoolean(visibleString);
			((BaseActivity) ECApplication.getInstance().getNowActivity()).setProgressBarIndeterminateVisibility(visible);
		} catch (Exception e) {
			LogUtil.e(TAG, "showProgressIndeterminateVisible error ...");
			e.printStackTrace();
		}
	}

	/**
	 * 为控件添加标签
	 * 
	 * @param hashMString
	 * @param widgetViewId
	 * @param fatherViewId
	 */
	public static void addTag(String hashMString, String widgetViewId, String fatherViewId) {
		LogUtil.d(TAG, "addTag start ");
		ViewUtil.addTag(hashMString, widgetViewId, fatherViewId);
	}

	/**
	 * 为控件添加标签
	 * 
	 * @param hashMLString
	 * @param widgetViewId
	 * @param fatherViewId
	 */
	public static void addTags(String hashMLString, String widgetViewId, String fatherViewId) {
		ViewUtil.addTags(hashMLString, widgetViewId, fatherViewId);
	}

	/**
	 * 获得控件标签
	 * 
	 * @param key
	 * @param widgetViewId
	 * @param fatherViewId
	 * @return
	 */
	public static String getTag(String key, String widgetViewId, String fatherViewId) {
		return ViewUtil.getTag(key, widgetViewId, fatherViewId);
	}

	/**
	 * 获取资源配置信息 Ohmer-Nov 28, 2013 9:55:53 AM
	 * 
	 * @return
	 */
	public static String getDecorateConfig() {
		return AppUtil.getDecorateConfig();
	}

	/**
	 * 分享字符串
	 * 
	 * @param context
	 * @param title
	 * @param text
	 */
	public static void shareString(String text) {
		IntentUtil.shareString(text);
	}

	/**
	 * 设置activity 的 contentview
	 * 
	 * @param layoutResID
	 * @return
	 */
	public static void setContentView(int layoutResID) {
		JsUtility.GetActivityContext().setContentView(layoutResID);
	}

	/**
	 * Toast 接口
	 * 
	 * @param msg
	 */
	public static void MakeToast(String msg) {
		Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 获取上下文
	 * 
	 * @return
	 */
	public static Activity getContext() {
		return ECApplication.getInstance().getNowActivity();
	}

	/**
	 * 通过widget，保存页面级 参数
	 * 
	 * @param fatherId
	 * @param widgetId
	 * @param key
	 * @param value
	 */
	public static void putPageParams(String fatherId, String widgetId, String keyValueString) {
		LogUtil.e(TAG, "错误：这个方法已经被弃用，参见putPageParams（key,value）或者 putWidgetParams(controlId,keyValueString)");
		// KeyValueModel keyValueModel = GsonUtil.fromJson(keyValueString,
		// KeyValueModel.class);
		// if (keyValueModel != null) {
		// WidgetUtil.putPageParams(fatherId, widgetId, keyValueModel.getKey(),
		// keyValueModel.getValue());
		// } else {
		// LogUtil.e(TAG, "putPageParams error: keyValueModel is null ...");
		// }
	}

	public static void putWidgetParams(String controlId, String keyValueString) {
		WidgetUtil.putWidgetParams(controlId, keyValueString);
	}

	public static void putWidgetParams(String controlId, String key, String value) {
		WidgetUtil.putWidgetParams(controlId, key, value);
	}

	public static String getWidgetParam(String controlId, String key) {
		return WidgetUtil.getWidgetParam(controlId, key);
	}

	public static void getSysImageRes() {
		View root = LayoutInflater.from(IntentUtil.getActivity()).inflate(R.layout.widget_gallery_popup_window_layout,
		        new LinearLayout(IntentUtil.getActivity()), false);
		final PopupWindow popup = new PopupWindow(root, android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT);
		popup.setAnimationStyle(R.style.PopupAnimation);
		popup.setFocusable(true);
		popup.setBackgroundDrawable(new BitmapDrawable());
		root.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popup.isShowing())
					popup.dismiss();
			}
		});
		Button cameraButton = (Button) root.findViewById(R.id.button1);
		cameraButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IntentUtil.openCamera();
				if (popup.isShowing())
					popup.dismiss();
			}
		});
		Button photoButton = (Button) root.findViewById(R.id.button2);
		photoButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				IntentUtil.openPhotoAlbum();
				if (popup.isShowing())
					popup.dismiss();
			}
		});
		Button cancleButton = (Button) root.findViewById(R.id.button3);
		cancleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (popup.isShowing())
					popup.dismiss();

			}
		});
		popup.showAtLocation(IntentUtil.getActivity().findViewById(R.id.activity_item_container_llayout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
	}

	/**
	 * 打开视频
	 * 
	 * @param uri
	 */
	public static void openVideo(String uri) {
		IntentUtil.openVideo(uri);
	}

	/**
	 * 打开二维码扫描
	 */
	public static void openQRCapture() {
		IntentUtil.openQRCapture();
	}

	/**
	 * 打开照相 页面
	 * 
	 * Ohmer-Jan 15, 2014 9:58:17 AM
	 */
	public static void openCameras() {
		IntentUtil.openCameras();
	}

	/**
	 * 关闭软键盘
	 */
	public static void closeSoftInput() {
		InputMethodManager inputMethodManager = (InputMethodManager) ECApplication.getInstance().getNowActivity()
		        .getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(null, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 打开二级菜单
	 */
	public static void openPopupwindow(String positionId, String controlId) {
		ViewUtil.openPopupwindow(positionId, controlId);
	}

	/**
	 * 关闭二级菜单
	 */
	public static void closePopupwindow() {
		LogUtil.d(TAG, "closePopupwindow");
		ViewUtil.closePopupwindow();
	}

	/**
	 * 打开对话框
	 */
	public static void openDialog(String title, String controlId) {
		ViewUtil.openDialog(title, controlId);
	}

	/**
	 * 关闭对话框
	 */
	public static void closeDialog() {
		ViewUtil.closeDialog();
	}

	/**
	 * 从文件里获得字符串,assets/raw 或 其他目录下
	 * 
	 * @param filename
	 * @return
	 */
	public static String getSysFileString(String filename) {
		return FileUtil.getSysFileString(filename);
	}

	public static String getPreference(String key) {
		LogUtil.d(TAG, "getPreference key :" + key + ", value : " + ECApplication.getInstance().readConfig(key));
		return JsUtility.getPreference(key);
	}

	public static void setPreference(String key, String value) {
		LogUtil.d(TAG, "setPreference key: " + key + ", vlaue: " + value);
		JsUtility.setPreference(key, value);
	}

	/**
	 * 保存页面级别数据
	 * 
	 * @param string
	 */
	public static void putPageParams(String string) {
		PageUtil.putParams(string, PageUtil.getNowPageContext());
	}

	/**
	 * 保存页面级别数据
	 * 
	 * @param string
	 */
	public static void putPageParam(String key, String value) {
		PageUtil.putParam(key, value, PageUtil.getNowPageContext());
	}

	public static void putPageParam(String key, String value, String controlId) {
		BaseWidget widget = (BaseWidget) ReflectionUtil.invokeMethod(IntentUtil.getActivity(), "getWidget", controlId);
		PageUtil.putParam(key, value, widget.getPageContext());
	}

	/**
	 * 获取页面级数据
	 */
	public static String getPageParam(String string) {
		return PageUtil.getParam(string, PageUtil.getNowPageContext());
	}

	/**
	 * 将js页面级参数转存在Preference里，便于JS调用
	 */
	public static void transPreference(String key) {
		JsUtility.setPreference(key, getPageParam(key));
	}

	/**
	 * 根据字符串表达式获取相应的目标值
	 * 
	 * @param controlId
	 * @param desc
	 * @param bundleString
	 * @return
	 */
	public static String getValuePurpose(String controlId, String desc, String bundleString) {
		return WidgetUtil.getValuePurpose(controlId, desc, bundleString);
	}

	/**
	 * 显示加载等待对话框
	 * 
	 * @param context
	 * @param loadingTitle
	 * @param loadingMessage
	 * @param cancelable
	 */
	public static void showLoadingDialog(String loadingTitle, String loadingMessage, String cancelable) {
		ViewUtil.showLoadingDialog(IntentUtil.getActivity(), loadingTitle, loadingMessage, Boolean.parseBoolean(cancelable));
	}

	public static void closeLoadingDianlog() {
		ViewUtil.closeLoadingDianlog();
	}

	/**
	 * 检测访问API是否出错
	 * 
	 * @param responseString
	 * @return 访问错误则返回true
	 */
	public static boolean checkError(String responseString) {
		return NetUtil.checkError(responseString);
	}

	/**
	 * 获取当前页面id
	 * 
	 * @return Ohmer-Dec 20, 2013 6:25:44 PM
	 */
	public static String getPageId() {
		return PageUtil.getNowPageId();

	}

	/**
	 * 替换 页面
	 * 
	 * @param pageName
	 * @param params
	 */
	public static void replacePage(String pageName, String params) {
		PageUtil.replacePage(pageName, params);
	}

	/**
	 * 替换Widget
	 * 
	 * @param controlId
	 */
	public static void replaceWidget(String controlId) {
		PageUtil.replaceWidget(controlId);
	}

	public static void replaceWidgets(String controlIds, String parentId) {
		PageUtil.replaceWidgets(controlIds, parentId);
	}

	public static void setTabHostCurrentTab(String controlId, String index) {
		callWidgetMethod(controlId, "setCurrentTab", index, null);
	}

	/**
	 * 获取本地用户信息
	 * 
	 * @return Ohmer-Dec 3, 2013 3:42:25 PM
	 */
	public static String getLocalUserInfo() {
		return AppUtil.getLocalUserInfo();
	}

	/**
	 * 保存到本地用户信息
	 * 
	 * @param userInfo
	 *            Ohmer-Dec 3, 2013 4:51:00 PM
	 */
	public static void putLocalUserInfo(String userInfo) {
		AppUtil.putLocalUserInfo(userInfo);
	}

	/**
	 * 获取新的用户token,没人知道参数用来干嘛的，所以先删掉
	 * 
	 * @param grant_type
	 */
	public static void getNewToken(String grantTypeString) {
		// JsonParser jsonParser = new JsonParser();
		// JsonObject jsonObject = (JsonObject)
		// jsonParser.parse(grantTypeString);
		// String grant_type = jsonObject.get("grant_type").getAsString();
		NetUtil.getNewToken();
	}

	// ---------------- user ---------------------------
	public static boolean isLogin() {
		return AppUtil.isLogin();
	}

	public static String getUserName() {
		return AppUtil.getUserName();
	}

	public static void saveUserName(String userName) {
		AppUtil.saveUserName(userName);
	}

	public static void clearUserName() {
		AppUtil.clearUserName();
	}

	/**
	 * 清除缓存
	 */
	public void clearCache() {
		FileUtil.clearCache();
	}

	/**
	 * 清除填充的子内容 Ohmer-Dec 19, 2013 9:23:44 AM
	 */
	public static void clearInflated() {
		AppUtil.clearInflated();
	}

	/**
	 * 检测是否有新版本
	 * 
	 * @return Ohmer-Jan 21, 2014 10:16:46 AM
	 */
	public static boolean checkNewVersion() {
		return AppUtil.checkNewVersion();
	}

	/**
	 * 获取当前版本信息
	 * 
	 * @return Ohmer-Jan 20, 2014 4:11:11 PM
	 */
	public static float getAppVersion() {
		return AppUtil.getAppVersion();
	}

	// 网络请求
	public static String app_callApi(String paramString) {
		if (StringUtil.isEmpty(paramString)) {
			return "";
		}
		HashMap<String, String> params = (HashMap<String, String>) GsonUtil.toHashMap(paramString);
		String result = HttpAsyncClient.Instance().postSync("", params);
		LogUtil.e(TAG, result);
		return result;

	}

}