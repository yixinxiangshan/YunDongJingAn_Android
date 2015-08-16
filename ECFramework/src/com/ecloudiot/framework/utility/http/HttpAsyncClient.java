package com.ecloudiot.framework.utility.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;

import com.ecloudiot.framework.utility.Constants;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import de.greenrobot.event.EventBus;

@SuppressLint("SimpleDateFormat")
public class HttpAsyncClient {
	private final static String TAG = "HttpAsyncClient";
	private static final String BASE_URL = Constants.BASE_API_SERVER;
	@SuppressWarnings("unused")
	private String md5;
	private AsyncHttpClient client;
	private HttpAsyncRequest cacheRequest = null;

	public HttpAsyncClient() {
		client = new AsyncHttpClient();
	}

	public static HttpAsyncClient Instance() {
		return new HttpAsyncClient();
	}

	public void post(String url, HashMap<String, String> params, HttpAsyncHandler responseHandler) {
		try {
			int cacheTime = 0;
			String ct = null;
			Object pct = params.get("cacheTime");

			if (pct instanceof Double) {
				ct = Integer.toString(((Double) pct).intValue());
			}
			if (StringUtil.isNotEmpty(ct)) {
				cacheTime = Integer.parseInt(ct);
				params.remove("cacheTime");
			}
			post(url, params, responseHandler, cacheTime);
		} catch (Exception e) {
			LogUtil.e(TAG, e.toString());
			e.printStackTrace();
		}
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
	public void post(String url, final HashMap<String, String> params, final HttpAsyncHandler responseHandler, final int cacheTime) {
		// LogUtil.d(TAG, "post : start ...");
		// check method in params
		String methodString = params.get("method");
		if (StringUtil.isEmpty(methodString)) {
			LogUtil.e(TAG, "post error: method in param is null ...");
			// TODO: 是否要将信息反馈至客户端
			return;
		}
		final HashMap<String, String> fullParams = getFullParams(params);
		md5 = MD5HashWithParams(fullParams);
		// TODO 写入缓存
		if (cacheTime > 0) {

		}
		if (!NetUtil.hasNetWork()) {
			// TODO: 修改为标准的错误格式
			responseHandler.onFailure("{\"errors\": \"没有网络\"}");
			return;
		}
		// //LogUtil.d(TAG, "params:" + fullParams.toString());
		AsyncHttpResponseHandler localHandler = new AsyncHttpResponseHandler() {

			@Override
			public void onFinish() {
				super.onFinish();
				// //LogUtil.d(TAG, "onFinish");
			}

			@Override
			public void onRetry(int retryNo) {
				super.onRetry(retryNo);
				// //LogUtil.d(TAG, "onRetry");
			}

			@Override
			public void onStart() {
				super.onStart();
				// //LogUtil.d(TAG, "onStart");
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				// //LogUtil.d(TAG, "onSuccess:start");
				String responseString = new String(responseBody);
				// //LogUtil.d(TAG, "onResponse from net :" + responseString);
				responseHandler.onResponse(responseString);
				if (NetUtil.checkError(responseString)) {
					responseHandler.onFailure(responseString);
					return;
				}
				String dataString = "";
				JsonObject responseJObject = null;
				try {
					responseJObject = (JsonObject) (new JsonParser()).parse(responseString);
				} catch (Exception e) {
					responseHandler.onFailure("{'errors':[{'error_num':'100021','error_msg':'服务器返回错误'}]}");
					return;
				}
				if (responseJObject.isJsonNull()) {
					responseHandler.onFailure("{'errors':[{'error_num':'100021','error_msg':'服务器返回错误'}]}");
					return;
				}
				// //LogUtil.d(TAG, "onSuccess : responseString = " + responseString);
				String method = params.get("method");
				if (method.equalsIgnoreCase("token")) {
					responseHandler.onSuccess(responseString);
				}
				try {
					JsonElement jsonElement = responseJObject.get("data");
					if (jsonElement == null || jsonElement.isJsonNull()) {
						responseHandler.onSuccess("");
						return;
					}
					if (responseJObject.get("data").isJsonArray()) {
						dataString = responseJObject.getAsJsonArray("data").toString();
					} else if (responseJObject.get("data").isJsonObject()) {
						dataString = responseJObject.getAsJsonObject("data").toString();
					} else {
						dataString = responseJObject.get("data").toString();
					}
					responseHandler.onSuccess(dataString);
					// TODO 读取缓存
					if (cacheTime > 0 && StringUtil.isNotEmpty(dataString)) {
						// FileUtil.putStringToCacheFile(dataString, md5);
					}
				} catch (Exception e) {
					LogUtil.e(TAG, "onSuccess error : not json(" + responseString + ") - " + e.toString());
				}

			}

			@Override
			public void onProgress(int bytesWritten, int totalSize) {
				// //LogUtil.d(TAG, "onProgress");
				super.onProgress(bytesWritten, totalSize);
				responseHandler.onProgress((float) bytesWritten / (float) totalSize);
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				// //LogUtil.d(TAG, "onFailure");
				responseHandler.onFailure("{'errors':[{'error_num':'100000','error_msg':'网络连接超时...'}]}");
			}

		};
		if (StringUtil.isEmpty(fullParams.get("access_token")) && !fullParams.get("method").equalsIgnoreCase("token")) {
			// HttpManager.getInstance().addClient(client, getAbsoluteUrl(url, params), NetUtil.getRequestParams(fullParams), localHandler);
			// MessageUtil.instance().addObserver(HttpManager.getInstance());
			setCacheRequest(new HttpAsyncRequest(url, fullParams, responseHandler, cacheTime));
			EventBus.getDefault().register(this);
			NetUtil.getNewToken();
			return;
		}
		client.setConnectTimeout(20000);
		client.setResponseTimeout(20000);
		client.post(getAbsoluteUrl(url, params), NetUtil.getRequestParams(fullParams), localHandler);
	}
	/**
	 * 因token不存在时，不能继续请求，则将请求缓存起来，当token请求回来后，则继续缓存请求
	 * 
	 * @param event
	 */
	public void onEvent(onTokenReceivedEvent event) {
		HttpAsyncRequest request = getCacheRequest();
		if (request != null) {
			post(request.getUrl(), request.getParams(), request.getResponseHandler(), request.getCacheTime());
			setCacheRequest(null);
		}
	}
	public String postSync(String urlString, HashMap<String, String> params) {
		String paramsString = "";
		// 请求的参数转换为byte数组
		try {
			paramsString = StringUtil.toNetString(getFullParams(params));
			String returnObject = "";
			String postData = "";
			HttpPost httpRequest = new HttpPost(getAbsoluteUrl(urlString, params));
			postData = paramsString;
			httpRequest.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8;");
			httpRequest.setEntity(new StringEntity(postData));
			HttpParams param = new BasicHttpParams();
			param.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
			HttpResponse response = new DefaultHttpClient(param).execute(httpRequest);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				returnObject = EntityUtils.toString(response.getEntity());
				// //LogUtil.d(TAG, "postSync : returnObject = " + returnObject);
			}
			return returnObject;
		} catch (Exception e) {
			LogUtil.e(TAG, "postSync error: " + e.toString());
			return "";
		}
	}

	public static String getAbsoluteUrl(String relativeUrl, HashMap<String, String> params) {
		if (relativeUrl.startsWith("http://")) {
			return relativeUrl;
		}
		String apiVersion = StringUtil.isEmpty(params.get("apiversion")) ? "1.0" : params.get("apiversion");
		String methodName = params.get("method");
		String absUrl = BASE_URL + (StringUtil.isEmpty(relativeUrl) ? "api/" : relativeUrl) + apiVersion + "/" + methodName;
		// LogUtil.d(TAG, "getAbsoluteUrl : " + absUrl);
		return absUrl;
	}

	private static HashMap<String, String> getFullParams(HashMap<String, String> params) {
		return NetUtil.getFullParams(params);
	}

	/**
	 * 格式化请求参数为照片上传请求参数
	 * 
	 * @param params
	 * @return
	 */
	public static RequestParams getUploadParams(HashMap<String, String> params) {
		RequestParams paramsMap = new RequestParams();
		for (Map.Entry<String, String> entry : params.entrySet()) {
			String entryString = entry.getValue();
			if (entryString.contains("jpg") || entryString.contains("png") || entryString.contains("gif") || entryString.contains("jpeg")) {
				try {
					paramsMap.put(entry.getKey(), new File(entry.getValue()));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				paramsMap.put(entry.getKey(), entry.getValue());
			}
		}
		return paramsMap;
	}

	public static String MD5HashWithParams(HashMap<String, String> params) {
		HashMap<String, String> params0 = new HashMap<String, String>();
		params0.putAll(params);
		params0.remove("call_id");
		params0.remove("sig");
		return StringUtil.calcMD5String(StringUtil.toNetString(params0));
	}

	public HttpAsyncRequest getCacheRequest() {
		return cacheRequest;
	}

	public void setCacheRequest(HttpAsyncRequest cacheRequest) {
		this.cacheRequest = cacheRequest;
	}

}
