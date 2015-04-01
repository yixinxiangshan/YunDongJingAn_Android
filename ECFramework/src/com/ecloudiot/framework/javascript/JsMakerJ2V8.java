package com.ecloudiot.framework.javascript;

import com.eclipsesource.v8.V8;
import com.ecloudiot.framework.utility.Constants;
import com.ecloudiot.framework.utility.IntentUtil;

public class JsMakerJ2V8 implements JsEngineProvieder {
	private static final String TAG = "JsMakerV8";
	private Object parent;
	private V8 v28;
	private int hashCode;
	public boolean viewHasGone = false;

	public JsMakerJ2V8(Object parentObject, int hashCode) {
		this.hashCode = hashCode;
		setParent(parentObject);
	}

	// 初始化
	public void jsEngineStart(String scriptString) {
		v28 = V8.createV8Runtime();
		String js_String = "_lang='zh';_debug=" + Constants.DEBUG + ";_pid='" + hashCode + "';" + getJsStr("promise.js") + getJsStr("env_android.js")
		        + getJsStr("api.js") + scriptString;

		v28.registerJavaMethod(JsAPI.instance(), "callDeviceApi", "callDeviceApi", new Class<?>[]{String.class, String.class, String.class, String.class});

		if (!this.viewHasGone) {
			v28.executeVoidScript(js_String);
		}
	}

	public String callback(String callback_id, Object args) {
		Object resultString = null;
		// LogUtil.d(TAG, "callback: " + callback_id + " - " + hashCode + " - " + args.toString());
		String input = "";
		if (args instanceof String)
			input = args + "";
		else
			input = args.toString();
		if (!this.viewHasGone) {
			if (args instanceof String) {
				resultString = v28.executeScript("_response_callbacks." + callback_id + "('" + args + "')");
			} else {
				resultString = v28.executeScript("_response_callbacks." + callback_id + "(" + args.toString() + ")");
			}
		}
		if (resultString != null) {
			return resultString.toString();
		}
		return "";
	}

	// 资源释放
	public void release() {
		if (null != v28) {
			v28.release(false);
			// LogUtil.e(TAG, "" + V8.count);
		}
	}

	// 从获取文件js内容
	private String getJsStr(String strName) {
		if (Constants.DEBUG) {
			return JsUtility.getSysFileString(IntentUtil.getActivity().getFilesDir() + "/webview/" + strName);
		} else {
			return JsUtility.getSysFileString("assets/webview/" + strName);
		}
	}

	public Object getParent() {
		return parent;
	}

	public void setParent(Object parent) {
		this.parent = parent;
	}

	public boolean isViewHasGone() {
		return viewHasGone;
	}
	/**
	 * 如果设置为true则代表页面已经销毁，不再执行Js
	 */
	public void setViewHasGone(boolean viewHasGone) {
		this.viewHasGone = viewHasGone;
	}
}
