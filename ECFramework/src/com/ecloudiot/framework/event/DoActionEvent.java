package com.ecloudiot.framework.event;

import java.util.HashMap;
import java.util.Map;

import com.ecloudiot.framework.utility.LogUtil;

public class DoActionEvent {
	private static String TAG = "DoActionEvent";
	private String url;
	private String controller;
	private String action;
	private String params;
	private EAction actionType;
	private Map<String, String> methodInfo = new HashMap<String, String>();
	
	public DoActionEvent(String url) {
		LogUtil.d(TAG,"DoActionEvent url:"+url);
		this.actionType = EAction.URL;
		this.url = url;
	}

	public DoActionEvent(String controller, String action, String params) {
		LogUtil.d(TAG,"DoActionEvent - controller:"+controller+";action:"+action+";params:"+params);
		this.actionType = EAction.PARAM;
		this.controller = controller;
		this.action = action;
		this.params = params;
	}
//	调用JsViewUtility静态方法
	public DoActionEvent(String className ,String methodName ,String param1 , String param2 ,String param3){
		LogUtil.d(TAG,"DoActionEvent className"+className+";methodName:"+methodName+";param1:"+param1+";param2:"+param2+";param3:"+param3);
		this.actionType = EAction.VIEWSTATICMETHOD;
		this.methodInfo.put("className", className);
		this.methodInfo.put("methodName", methodName);
		this.methodInfo.put("param1", param1);
		this.methodInfo.put("param2", param2);
		this.methodInfo.put("param3", param3);
	}
	public EAction getActionType() {
		return this.actionType;
	}
	public Map<String, String> getMethodInfo() {
		return this.methodInfo;
	}
	public String getUrl() {
		return this.url;
	}
	public String getController() {
		return this.controller;
	}
	public String getAction() {
		return this.action;
	}
	public String getParams() {
		return this.params;
	}
}
