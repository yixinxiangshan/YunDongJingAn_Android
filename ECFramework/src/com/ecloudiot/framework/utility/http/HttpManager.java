package com.ecloudiot.framework.utility.http;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.ecloudiot.framework.utility.MessageUtil.MessageData;
import com.ecloudiot.framework.utility.Constants;
import com.ecloudiot.framework.utility.MessageUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpManager implements Observer {
	private ArrayList<HttpAsyncClientFull> clientFulls;
	private static HttpManager instance;

	public static HttpManager getInstance() {
		if (instance == null) {
			instance = new HttpManager();
		}
		return instance;
	}

	public void addClient(AsyncHttpClient client, String url, RequestParams params, AsyncHttpResponseHandler handler) {
		HttpAsyncClientFull clientFull = new HttpAsyncClientFull();
		clientFull.setClient(client);
		clientFull.url = url;
		clientFull.params = params;
		clientFull.handler = handler;
		addClient(clientFull);
	}

	public void post() {
		if (clientFulls == null) {
			return;
		}
		for (HttpAsyncClientFull clientFull : clientFulls) {
			String token = NetUtil.getToken();
			if (StringUtil.isNotEmpty(token)) {
				clientFull.params.put("access_token", token);
				clientFull.getClient().post(clientFull.url, clientFull.params, clientFull.handler);
				clientFulls.remove(clientFull);
			} else {
				break;
			}
		}
	}

	private void addClient(HttpAsyncClientFull clientFull) {
		if (clientFulls == null) {
			clientFulls = new ArrayList<HttpManager.HttpAsyncClientFull>();
		}
		clientFulls.add(clientFull);
		post();
	}

	public class HttpAsyncClientFull {
		private AsyncHttpClient client;
		private String url;
		private RequestParams params;
		private AsyncHttpResponseHandler handler;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public RequestParams getParams() {
			return params;
		}

		public void setParams(RequestParams params) {
			this.params = params;
		}

		public AsyncHttpResponseHandler getHandler() {
			return handler;
		}

		public void setHandler(AsyncHttpResponseHandler handler) {
			this.handler = handler;
		}

		public AsyncHttpClient getClient() {
			return client;
		}

		public void setClient(AsyncHttpClient client) {
			this.client = client;
		}
	}

	@Override
	public void update(Observable observable, Object data) {
		MessageData msgData = (MessageData) data;
		if (msgData.getName().equalsIgnoreCase(Constants.MESSAGE_TAG_TOKEN_RECEIVED)) {
			post();
			MessageUtil.instance().deleteObserver(Constants.MESSAGE_TAG_TOKEN_RECEIVED);
		}
	}
}
