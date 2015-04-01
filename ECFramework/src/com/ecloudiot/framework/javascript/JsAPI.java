package com.ecloudiot.framework.javascript;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.ReflectionUtil;
import com.ecloudiot.framework.widget.WebWidget;

public class JsAPI {
	private static final String TAG = "JsAPI";
	private static JsAPI instance;
	private HashMap<String, JsEngine> jsMakerMap;
	private HashMap<String, WebWidget> WebviewMap;
	private HashMap<String, Integer> viewIdMap = new HashMap<String, Integer>();
	private Integer viewIdNumInteger = 100000000;

	// 获取实例
	public static JsAPI instance() {
		if (instance == null) {
			instance = new JsAPI();
		}
		return instance;
	}

	// 添加Webview到hash
	public void addWebJsMaker(String key, WebWidget Webview) {
		if (WebviewMap == null) {
			WebviewMap = new HashMap<String, WebWidget>();
		}
		WebviewMap.put(key, Webview);
	}

	// 获取Webview的实例
	public WebWidget getWebJsMaker(String key) {
		return WebviewMap.get(key);
	}

	// 添加jsmaker到hash
	public void addJsMaker(String key, JsEngine jsMaker) {
		if (jsMakerMap == null) {
			jsMakerMap = new HashMap<String, JsEngine>();
		}
		jsMakerMap.put(key, jsMaker);
	}

	// 获取jsmaker的实例
	public JsEngine getJsMaker(String key) {
		return jsMakerMap.get(key);
	}

	// 统一反射api接口的函数
	public static void callDeviceApi(String method, String paramString, String callbackId, String action) {
		// LogUtil.d(TAG, "callDeviceApi: method: " + method + "  params:" + paramString + "   callbackid:" + callbackId + "  action:" + action);
		try {
			// 处理、拼接params
			HashMap<String, String> params = (HashMap<String, String>) GsonUtil.toHashMap(paramString);
			// 处理action
			JSONObject actionObject = new JSONObject(action);
			@SuppressWarnings("unchecked")
			Iterator<String> it = actionObject.keys();
			ArrayList<String> actionArrayList = new ArrayList<String>();
			ArrayList<String> actionValueArrayList = new ArrayList<String>();
			while (it.hasNext()) {
				String nowAction = (String) it.next().toString();
				String nowAString = actionObject.getString(nowAction);
				if (!nowAction.equals("pid")) {
					actionArrayList.add(nowAction);
					actionValueArrayList.add(nowAString);
					params.put("_" + nowAction, nowAString);
				}
			}
			params.put("_callbackId", callbackId);
			params.put("_jsMakerId", actionObject.getString("pid"));
			params.put("_method", method);
			// 拼接接口名称
			String methodName = "";
			for (String s : actionArrayList) {
				methodName += s + "_";
			}
			Object result = (Object) ReflectionUtil.invokeMethod(new AppAPI(getPage(actionObject.getString("pid"), callbackId)), methodName + method, params);
			if (result.equals("_false"))
				return;
			JsAPI.callback(actionObject.getString("pid"), callbackId, result);
		} catch (JSONException e) {
			LogUtil.e(TAG, "callDeviceApi error:" + e.toString());
		}
	}

	// 执行回调
	public static String callback(String jsMakerId, String callbackId, Object args) {
		try {
//			LogUtil.d(TAG, "callback, jsMakerId :" + jsMakerId + ";callbackId:" + callbackId + ";args :" + args);

			String[] callbackarr = callbackId.split("\\_");
			// 设备环境的js回调
			if (callbackarr[0].equals("device")) {
				JsEngine jsMaker = JsAPI.instance().getJsMaker(jsMakerId);
				if (null != jsMaker) {
					return jsMaker.callback(callbackId, args);
				} else {
					LogUtil.e(TAG, "callback error, jsMakerId is not exists:" + jsMakerId);
				}
			} else {
				// webview环境的回调
				WebWidget webJsMaker = JsAPI.instance().getWebJsMaker(jsMakerId);
				if (null != webJsMaker) {
					webJsMaker.callBackJs(callbackId, args);
				} else {
					LogUtil.e(TAG, "callback error, webJsMaker is not exists:" + jsMakerId);
				}
			}
		} catch (Exception e) {
			LogUtil.e(TAG, "callback error0:" + e.toString());
			e.printStackTrace();
		}
		return "";
	}

	public static Object getPage(String jsMakerId, String callbackId) {
//		LogUtil.d(TAG, "getPage : jsMakerId" + jsMakerId + " - callbackId:" + callbackId);
//		LogUtil.d(TAG, "getPage jsmaker:" + JsAPI.instance().jsMakerMap.toString());
		String[] callbackarr = callbackId.split("\\_");
		if (callbackarr[0].equals("device")) {
//			LogUtil.e(TAG, "getPage:" + JsAPI.instance().getJsMaker(jsMakerId) == null ? "true" : "false");
			return (Object) JsAPI.instance().getJsMaker(jsMakerId).getParent();
		} else {
			return (Object) JsAPI.instance().getWebJsMaker(jsMakerId).getContext();
		}

	}

	public static String getPageId(String jsMakerId, String callbackId) {
		// 执行js里面的事件
		Object pageContext = getPage(jsMakerId, callbackId);
		if (pageContext instanceof ItemActivity) {
			return ((ItemActivity) pageContext).getParam("page_id");
		} else if (pageContext instanceof ItemFragment) {
			return ((ItemFragment) pageContext).getParam("page_id");
		}
		return "_false";

	}

	// 执行事件
	public static String runEvent(HashMap<String, String> jsEvents, String type, Object args) {
		// LogUtil.d(TAG, "runEvent :" + jsEvents.toString() + " - " + type + " - " + args);
		// 执行js里的事件
		try {
			Iterator<Entry<String, String>> iter = jsEvents.entrySet().iterator();
			while (iter.hasNext()) {
				@SuppressWarnings("rawtypes")
				Map.Entry entry = (Map.Entry) iter.next();
				String keyString = (String) entry.getKey();
				if (((String) entry.getValue()).equals(type)) {
					String[] keyarr = keyString.split("\\|");
					return JsAPI.callback(keyarr[0], keyarr[1], args);
				}
			}
		} catch (Exception e) {
			LogUtil.e(TAG, "runEvent error:" + e.toString());
			e.printStackTrace();
		}
		return "";
	}

	// 切换fragment的时候，nowactivity 没有更换，但是需要把原来的这个page的事件覆盖掉才能顺利执行
	// 执行事件
	@SuppressWarnings("rawtypes")
	public static String runEvent(HashMap<String, HashMap<String, String>> widgetJsEvents, String widgetName, String type, Object args) {
		// LogUtil.d(TAG, "runWidgetEvent :" + widgetJsEvents.toString() + " - " + type + " - " + args);
		// 执行js里的事件
		try {
			HashMap<String, String> jsEvents = widgetJsEvents.get(widgetName);
			if (jsEvents != null) {
				Iterator<Entry<String, String>> iter = jsEvents.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String keyString = (String) entry.getKey();
					if (((String) entry.getValue()).equals(type)) {
						String[] keyarr = keyString.split("\\|");
						return JsAPI.callback(keyarr[0], keyarr[1], args);
					}
				}
			}

		} catch (Exception e) {
			LogUtil.e(TAG, "runEvent error:" + e.toString());
			e.printStackTrace();
		}
		return "";
	}

	// public static void makeTorast(String words) {
	// // LogUtil.d(TAG, "makeTorast : " + words);
	// // JsViewUtility.MakeToast(words);
	// }

	public HashMap<String, Integer> getViewIdMap() {
		return viewIdMap;
	}

	public Integer addViewId(String viewIdKey) {
		viewIdNumInteger = viewIdNumInteger + 1;
		viewIdMap.put(viewIdKey, viewIdNumInteger);
		return viewIdNumInteger;
	}
	public Integer getdViewId(String viewIdKey) {
		return viewIdMap.get(viewIdKey);
	}

	public void setViewId(HashMap<String, Integer> viewIdMap) {
		this.viewIdMap = viewIdMap;
	}
}