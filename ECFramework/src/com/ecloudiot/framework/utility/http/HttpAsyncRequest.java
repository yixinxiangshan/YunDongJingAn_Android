package com.ecloudiot.framework.utility.http;

import java.util.HashMap;

public class HttpAsyncRequest {
	private String url = "";
	private int cacheTime;
	private HashMap<String, String> params;
	private HttpAsyncHandler responseHandler;
	public HttpAsyncRequest(String url, final HashMap<String, String> params, final HttpAsyncHandler responseHandler, final int cacheTime) {
		setUrl(url);
		setParams(params);
		setResponseHandler(responseHandler);
		setCacheTime(cacheTime);
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public HashMap<String, String> getParams() {
		return params;
	}
	public void setParams(HashMap<String, String> params) {
		this.params = params;
	}
	public HttpAsyncHandler getResponseHandler() {
		return responseHandler;
	}
	public void setResponseHandler(HttpAsyncHandler responseHandler) {
		this.responseHandler = responseHandler;
	}
	public int getCacheTime() {
		return cacheTime;
	}
	public void setCacheTime(int cacheTime) {
		this.cacheTime = cacheTime;
	}
}
