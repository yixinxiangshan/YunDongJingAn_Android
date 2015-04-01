package com.ecloudiot.framework.javascript;

import com.ecloudiot.framework.utility.Constants;
import com.ecloudiot.framework.utility.IntentUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.jovianware.jv8.V8Exception;
import com.jovianware.jv8.V8MappableMethod;
import com.jovianware.jv8.V8Runner;
import com.jovianware.jv8.V8Value;

public class JsMakerV8 implements JsEngineProvieder {
	private static final String TAG = "JsMakerV8";
	private Object parent;
	private V8Runner v8;
	private int hashCode;
	public boolean viewHasGone = false;
	
	public JsMakerV8(Object parentObject, int hashCode) {
		this.hashCode = hashCode;

		setParent(parentObject);
	}
	
	// 初始化
	@Override
	public void jsEngineStart(String scriptString) {
		v8 = new V8Runner();
		String js_String = "_lang='zh';_debug=" + Constants.DEBUG + ";_pid='" + hashCode + "';" + getJsStr("promise.js") + getJsStr("env_android.js")
		        + getJsStr("api.js") + scriptString;
		v8.map("callDeviceApi", new V8MappableMethod() {
			@Override
			public V8Value methodToRun(V8Value[] args) {
				try {
					JsAPI.callDeviceApi(args[0].toString(), args[1].toString(), args[2].toString(), args[3].toString());
					return v8.val("");
				} catch (Exception e) {
					LogUtil.e(TAG, "error:" + e.toString());
					e.printStackTrace();
				}
				return v8.val("");
			}
		});
		try {
			v8.runJS("(program)", js_String);
		} catch (V8Exception e) {
			LogUtil.e(TAG, e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public String callback(String callback_id, Object args) {
		try {
			Object resultString = null;
			// LogUtil.d(TAG, "callback: " + callback_id + " - " + hashCode + " - " + args.toString());
			if (args instanceof String) {
				resultString = (Object) v8.runJS("(program)", "_response_callbacks." + callback_id + "('" + args + "')");
			} else {
				resultString = (Object) v8.runJS("(program)", "_response_callbacks." + callback_id + "(" + args.toString() + ")");
			}
			if (resultString != null)
				return resultString.toString();
			return "";
		} catch (V8Exception e) {
			LogUtil.e(TAG, e.toString());
			e.printStackTrace();
		}
		return "";
	}

	// 从获取文件js内容
	private String getJsStr(String strName) {
		if (Constants.DEBUG) {
			return JsUtility.getSysFileString(IntentUtil.getActivity().getFilesDir() + "/webview/" + strName);
		} else {
			return JsUtility.getSysFileString("assets/webview/" + strName);
		}
	}

	@Override
	public Object getParent() {
		return this.parent;
	}

	@Override
	public void setParent(Object parent) {
		this.parent = parent;
	}
	@Override
	public void release() {
		v8.finalize();
	}
	public void setViewHasGone(boolean viewHasGone) {
		this.viewHasGone = viewHasGone;
	}
}
