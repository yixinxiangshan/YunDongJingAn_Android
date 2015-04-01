package com.ecloudiot.framework.utility.http;

public interface HttpAsyncHandler {
	public void onSuccess(String resopnseString);
	public void onResponse(String resopnseString);
	public void onFailure(String resopnseString);
	public void onProgress(Float progress);
}
