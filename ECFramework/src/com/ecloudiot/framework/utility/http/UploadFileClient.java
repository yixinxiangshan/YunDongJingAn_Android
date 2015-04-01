package com.ecloudiot.framework.utility.http;

import java.util.HashMap;

import org.apache.http.Header;

import android.annotation.SuppressLint;

import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

@SuppressLint("SimpleDateFormat")
public class UploadFileClient {
	private final static String TAG = "HttpAsyncClient";
	private AsyncHttpClient client;

	public UploadFileClient() {
		client = new AsyncHttpClient();
	}

	public static UploadFileClient Instance() {
		return new UploadFileClient();
	}

	public void post(String url, HashMap<String, String> params, HttpAsyncHandler responseHandler) {
		int cacheTime = 15;
		if (StringUtil.isNotEmpty(params.get("cacheTime"))) {
			cacheTime = Integer.parseInt(params.get("cacheTime"));
			params.remove("cacheTime");
		}
		post(url, params, responseHandler, cacheTime);
	}

	/**
	 * 发起网络请求
	 * 
	 * @param url
	 * @param params
	 * @param responseHandler
	 * @param cacheTime
	 *            缓存时间，以分钟为单位
	 */
	public void post(String url, final HashMap<String, String> params, final HttpAsyncHandler responseHandler,
			final int cacheTime) {
		if (!NetUtil.hasNetWork()) {
			// TODO: 修改为标准的错误格式
			responseHandler.onFailure("没有网络");
			return;
		}
		AsyncHttpResponseHandler localHandler = new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				String responseString = new String(responseBody);
				LogUtil.i(TAG, "onSuccess from net : responsString length = " + responseString.length());
				responseHandler.onSuccess(responseString);
			}

			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				super.onProgress(bytesWritten, totalSize);
				responseHandler.onProgress((float)bytesWritten/(float)totalSize);
			}



			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				responseHandler.onFailure("{'errors':[{'error_num':'100000','error_msg':'网络错误'}]}");
			}

		};

		client.post(HttpAsyncClient.getAbsoluteUrl(url, params), NetUtil.getRequestParams(params), localHandler);
	}


}
