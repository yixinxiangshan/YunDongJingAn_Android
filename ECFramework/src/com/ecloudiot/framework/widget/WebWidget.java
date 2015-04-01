package com.ecloudiot.framework.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.javascript.JsUtility;
import com.ecloudiot.framework.javascript.JsViewUtility;
import com.ecloudiot.framework.utility.AppUtil;
import com.ecloudiot.framework.utility.Constants;
import com.ecloudiot.framework.utility.DensityUtil;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.IntentUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.ReflectionUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.ViewUtil;
import com.ecloudiot.framework.utility.http.HttpAsyncClient;
import com.ecloudiot.framework.utility.http.HttpAsyncHandler;
import com.ecloudiot.framework.utility.http.NetUtil;
import com.google.gson.JsonObject;

public class WebWidget extends BaseWidget {
	private final static String TAG = "WebWidget";
	private String dataString;
	private String globalJString;
	private String globalCsstring;
	private String indexHtmlString;
	private String htmlDataString;
	private WebView webView;
	public static final int INVOKE_METHOD = 1;
	public static final int RESET_FRAME = 2;
	public static final int LOAD_URL = 3;
	public static final int JSAPI_CALL = 4;
	// 控件属性
	private String webTemplate;
	private String htmlFile;

	public WebWidget(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		this.setId(R.id.web_widget);
		parsingData();
	}

	public String getDataString() {
		return dataString;
	}

	protected void initViewLayout(String layoutName) {
		super.initViewLayout(layoutName);
		if (StringUtil.isNotEmpty(layoutName)) {
			initBaseView(layoutName);
		} else {
			initBaseView("widget_web_default");
		}
		initWebView();
	}

	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		// don't call super method to close loading here
		LogUtil.d(TAG, "parsingWidgetData : start ...");
		this.dataString = GsonUtil.toJson(widgetDataJObject);
	}

	public void callBackJs(String callBackId, Object params) {
		String callBackJS = "";
		if (params instanceof String) {
			callBackJS = "javascript:_response_callbacks." + callBackId + "(\'" + params + "\')";
		} else {
			callBackJS = "javascript:_response_callbacks." + callBackId + "(" + params.toString() + ")";
		}
		try {
			webView.loadUrl(callBackJS);
		} catch (Exception e) {
			LogUtil.e(TAG, e.toString());
			e.printStackTrace();
		}

	};
	public void callJs(String funName, String params) {
		String callBackJS = "";
		callBackJS = "javascript:" + funName + "(" + params.toString() + ")";
		try {
			webView.loadUrl(callBackJS);
		} catch (Exception e) {
			LogUtil.e(TAG, e.toString());
			e.printStackTrace();
		}

	};
	@SuppressWarnings("unused")
	protected void setData() {
		String dataScriptString = "";
		String html = "";
		Pattern p1 = Pattern.compile("<script[^>]*>[\\s\\S]*(?=<\\/script>)<\\/script>");
		Matcher m1 = p1.matcher(getWebTemplate());
		if (m1.find()) {
			dataScriptString = m1.group(0);
			html = m1.replaceAll(" ");
		} else {
			LogUtil.w(TAG, "setData : 没有匹配到script代码");
			html = getWebTemplate();
		}

		// htmlDataString =
		// JsManager.getInstance().runJs("ecct://app/getWebHtml?template=" +
		// Uri.encode(getWebTemplate()) + "&data=" + Uri.encode(dataString));
		// htmlDataString = JsManager.getInstance().runJs("ecct://app/getWebHtml?template=" + Uri.encode(html) + "&data=" + Uri.encode(dataString));
		htmlDataString += dataScriptString;
		htmlDataString = StringUtil.isNotEmpty(indexHtmlString) ? indexHtmlString.replace("#{html body}", htmlDataString) : htmlDataString;
		// 处理文件script
		Pattern p2 = Pattern.compile("\\{baseurl\\}");
		Matcher m2 = p2.matcher(htmlDataString);
		if (m2.find()) {
			String baseUrlString = "";
			if (Constants.DEBUG) {
				baseUrlString = "file://" + IntentUtil.getActivity().getFilesDir() + "";
			} else {
				baseUrlString = "file:///android_asset";
			}
			htmlDataString = m2.replaceAll(baseUrlString);

		} else {
			LogUtil.w(TAG, "setScript : 没有匹配到script文件");
		}
		// 处理文件rand
		Pattern p3 = Pattern.compile("\\{rand\\}");
		Matcher m3 = p3.matcher(htmlDataString);
		if (m3.find()) {
			if (Constants.DEBUG) {
				htmlDataString = m3.replaceAll(Math.random() + "");
			} else {
				htmlDataString = m3.replaceAll("");
			}
		} else {
			LogUtil.w(TAG, "setScript : 没有匹配到rand");
		}
		// 处理parentid，并且把webview指向到jsapi中
		Pattern p4 = Pattern.compile("\\{parentid\\}");
		Matcher m4 = p4.matcher(htmlDataString);
		if (m4.find()) {
			htmlDataString = m4.replaceAll(this.hashCode() + "");
		} else {
			LogUtil.w(TAG, "setScript : 没有匹配到parentid");
		}
		// 处理文件debug
		Pattern p5 = Pattern.compile("\\{debug\\}");
		Matcher m5 = p5.matcher(htmlDataString);
		if (m5.find()) {
			htmlDataString = m5.replaceAll(Constants.DEBUG + "");
		} else {
			LogUtil.w(TAG, "setScript : 没有匹配到debug");
		}
		JsAPI.instance().addWebJsMaker(this.hashCode() + "", this);
		// LogUtil.d(TAG, htmlDataString);
		// webview being your WebView object reference.
		webView.getSettings().setSupportZoom(false);
		webView.getSettings().setBuiltInZoomControls(false);
		webView.getSettings().setDomStorageEnabled(true);
		// 设置Webview默认为全屏（满屏）显示
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.loadDataWithBaseURL(null, htmlDataString, "text/html", "UTF-8", null);
		LogUtil.w(TAG, "---loadDataWithBaseURL:" + htmlDataString);
		LogUtil.w(TAG, "---string:" + JsUtility.getSysFileString("assets/webview/global.js"));
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				LogUtil.w(TAG, "---onPageFinished");
				super.onPageFinished(webView, url);
				LogUtil.i(TAG, "onPageFinished : ...");
				loading(LOADING_0N_OFF.TURN_OFF);
				// webView.loadUrl("javascript:window.Original.reframeWidget(document.width,document.height);");
			}
		});
		webView.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				LogUtil.i(TAG, "onProgressChanged : newProgress = " + newProgress);
				if (newProgress >= 100 && webView.getSettings().getBlockNetworkImage()) {
					webView.getSettings().setBlockNetworkImage(false);
				}
			}

			@SuppressLint("NewApi")
			@Override
			public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
				super.onConsoleMessage(consoleMessage);
				switch (consoleMessage.messageLevel()) {
					case DEBUG :
						LogUtil.d(TAG, "onConsoleMessage : " + consoleMessage.message());
						break;
					case LOG :
						LogUtil.i(TAG, "onConsoleMessage : " + consoleMessage.message());
						break;
					case WARNING :
						LogUtil.w(TAG, "onConsoleMessage : " + consoleMessage.message());
						break;
					case ERROR :
						LogUtil.e(TAG, "onConsoleMessage : " + consoleMessage.message());
						break;

					default :
						break;
				}
				return true;
			}

		});
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		webView = (WebView) getBaseView().findViewById(R.id.widget_web_webview);
		webView.addJavascriptInterface(new webViewInterface(this), "Original");
		webView.addJavascriptInterface(new WebviewApi(this), "WebviewApi");
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webSettings.setDefaultTextEncodingName("UTF-8");
		webSettings.setRenderPriority(RenderPriority.HIGH);
		// webSettings.setAllowUniversalAccessFromFileURLs(true);
		// webSettings.setBlockNetworkImage(true);
		if (Constants.DEBUG) {
			globalJString = JsUtility.getSysFileString(IntentUtil.getActivity().getFilesDir() + "/webview/global.js");
			globalCsstring = JsUtility.getSysFileString(IntentUtil.getActivity().getFilesDir() + "/webview/global.css");
			indexHtmlString = JsUtility.getSysFileString(IntentUtil.getActivity().getFilesDir() + "/webview/layout_android.html");
		} else {
			globalJString = JsUtility.getSysFileString("assets/webview/global.js");
			globalCsstring = JsUtility.getSysFileString("assets/webview/global.css");
			indexHtmlString = JsUtility.getSysFileString("assets/webview/layout_android.html");
		}
		// indexHtmlString = FileUtil.getSysFileString("raw/index_html");
		indexHtmlString = indexHtmlString.replace("#{global.js}", StringUtil.isEmpty(globalJString) ? "" : globalJString);
		indexHtmlString = indexHtmlString.replace("#{global.css}", StringUtil.isEmpty(globalCsstring) ? "" : globalCsstring);
	}

	public void reframeWidget(Integer height) {
		if (insertType == 2 || insertType == 4) {
			LogUtil.i(TAG, "reframeWidget : height = " + height);
			ViewGroup.LayoutParams layouParams = this.getLayoutParams();
			layouParams.height = DensityUtil.dipTopx(ctx, height + 10);
			this.setLayoutParams(layouParams);
			layouParams = webView.getLayoutParams();
			layouParams.height = DensityUtil.dipTopx(ctx, height + 10);
			webView.setLayoutParams(layouParams);
			LogUtil.d(TAG, "reframeWidget : webView height = " + layouParams.height);
		}
	}

	public void sendMsgtoWebView(String msg) {
		webView.loadUrl("javascript:pushMsg(" + msg + ");");
	}

	public void onPageResume(String msg) {
		// LogUtil.d(TAG, "onPageResume : msg = " + msg);
		webView.loadUrl("javascript:onPageResume(" + msg + ");");
	}

	public String getWebTemplate() {
		String fileName = getHtmlFile();
		if (!StringUtil.isEmpty(fileName)) {
			LogUtil.d(TAG, "fileName:" + fileName);
			if (Constants.DEBUG) {
				return JsUtility.getSysFileString(IntentUtil.getActivity().getFilesDir() + "/webview/views/" + fileName);
			} else {
				return JsUtility.getSysFileString("assets/webview/views/" + fileName);
			}
		}
		return webTemplate;
	}

	public void setWebTemplate(String webTemplate) {
		this.webTemplate = webTemplate;
	}

	public String getHtmlFile() {
		return htmlFile;
	}

	public void setHtmlFile(String htmlFile) {
		this.htmlFile = htmlFile;
	}

	// 与新的js环境接口相同的webviewjs环境
	class WebviewApi {
		WebWidget widget;

		public WebviewApi(WebWidget widget) {
			this.widget = widget;
		}

		public void callCoreApi(String method, String paramString, String callbackId, String action) {
			ArrayList<String> params = new ArrayList<String>();
			params.add(method);
			params.add(paramString);
			params.add(callbackId);
			params.add(action);
			Message msg = mHandler.obtainMessage(JSAPI_CALL, params);
			mHandler.sendMessage(msg);
		}
	}

	class webViewInterface {
		WebWidget widget;

		public webViewInterface(WebWidget widget) {
			this.widget = widget;
		}

		public void reframeWidget(String width, String height) {
			if (StringUtil.isEmpty(height)) {
				return;
			}
			int webviewContentHeight = Integer.parseInt(height);
			LogUtil.i(TAG, "webviewContentWidth = " + width);
			LogUtil.i(TAG, "webviewContentHeight = " + webviewContentHeight);
			Message msg = new Message();
			msg.what = RESET_FRAME;
			ArrayList<Object> parmas = new ArrayList<Object>();
			parmas.add("reframeWidget");
			parmas.add(webviewContentHeight);
			msg.obj = parmas;
			WebWidget.this.mHandler.sendMessage(msg);
		}

		public String getNetParams(String paramString, String callBackId) {
			if (StringUtil.isEmpty(paramString)) {
				return "";
			}
			HashMap<String, String> params = (HashMap<String, String>) GsonUtil.toHashMap(paramString);
			if (params != null) {
				params = NetUtil.getFullParams(params);
				// LogUtil.i(TAG, "getNetParams : "+new JSONObject(params));
				callBackJs(params, callBackId);
				return (new JSONObject(params)).toString();
			}
			return "";
		}

		private void callBackJs(HashMap<String, String> params, String callBackId) {
			callBackJs((new JSONObject(params)).toString(), callBackId);
		};

		private void callBackJs(String params, String callBackId) {
			String callBackJS = "javascript:response_callbacks[\"" + callBackId + "\"](" + params + ")";
			Message msg = new Message();
			msg.what = LOAD_URL;
			msg.obj = callBackJS;
			WebWidget.this.mHandler.sendMessage(msg);
			// webView.loadUrl(callBackJS);
		};

		public void postRequest(String paramString, final String callBackId) {
			// LogUtil.i(TAG, "postRequest : paramString = " + paramString);
			if (StringUtil.isEmpty(paramString)) {
				return;
			}
			HashMap<String, String> params = (HashMap<String, String>) GsonUtil.toHashMap(paramString);
			HttpAsyncClient.Instance().post("", params, new HttpAsyncHandler() {
				@Override
				public void onFailure(String failResopnse) {
					LogUtil.e(TAG, "onFailure error: " + failResopnse);
					ViewUtil.closeLoadingDianlog();
					Toast.makeText(IntentUtil.getActivity(), "网络出错...", Toast.LENGTH_SHORT).show();
				}

				@Override
				public void onSuccess(String response) {
					// LogUtil.d(TAG, "onSuccess : response = " + response);

					// Toast.makeText(IntentUtil.getActivity(), response,
					// Toast.LENGTH_SHORT).show();
					// JsonElement dataElement = (new
					// JsonParser()).parse(response);
					//
					// if (dataElement.isJsonObject()) {
					// JsonElement protocalElement = ((JsonObject)
					// dataElement).get("data");
					// if (protocalElement.isJsonPrimitive()) {
					// String protocalString = protocalElement.getAsString();
					// // EventBus.getDefault().post(new
					// // DoActionEvent(protocalString));
					// // callBackJs(protocalString, callBackId);
					// Toast.makeText(IntentUtil.getActivity(), "网络出错...",
					// Toast.LENGTH_SHORT).show();
					// // EventBus.getDefault().post(new
					// DoActionEvent(protocalString));
					// }
					// }
				}

				@Override
				public void onProgress(Float progress) {

				}

				@Override
				public void onResponse(String resopnseString) {
					LogUtil.d(TAG, "onResponse : response = " + resopnseString);
					callBackJs(resopnseString, callBackId);

				}
			});
			// callBackJs(HttpAsyncClient.Instance().postSync("", params),
			// callBackId);
		}

		public String getDecorateConfig(String callBackId) {
			String getDecorateConfig = AppUtil.getDecorateConfig();
			callBackJs(getDecorateConfig, callBackId);
			return getDecorateConfig;
		}

		public void setScrollable(boolean scrollable) {
			LogUtil.i(TAG, "setScrollable : " + scrollable);
			webView.setVerticalScrollBarEnabled(scrollable);
		}

		public void getDecoratePath(String callBackId) {
			// LogUtil.i(TAG, "getDecoratePath : " + "file://" +
			// IntentUtil.getActivity().getFilesDir() + "/decorate/");
			callBackJs("'file://" + IntentUtil.getActivity().getFilesDir() + "/decorate/'", callBackId);
		}

		public void getConfigFile(String paramString, String callBackId) {
			if (StringUtil.isEmpty(paramString)) {
				return;
			}
			HashMap<String, String> params = (HashMap<String, String>) GsonUtil.toHashMap(paramString);
			String location = params.get("location");
			String resultString = "";
			if (Constants.DEBUG) {
				resultString = JsUtility.getSysFileString(IntentUtil.getActivity().getFilesDir() + "/" + location);
			} else {
				resultString = JsUtility.getSysFileString("assets/" + location);
			}
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("result", resultString);
			} catch (Exception e) {
				LogUtil.e(TAG, "getCache error: " + e.toString());
			}
			callBackJs(jsonObject.toString(), callBackId);
		}

		// 添加 toast
		public void makeToast(String paramString, String callBackId) {
			if (StringUtil.isEmpty(paramString)) {
				return;
			}
			JsViewUtility.MakeToast(paramString);
			callBackJs("{success:true}", callBackId);
		}

		/**
		 * 是否登录
		 * 
		 * @param callBackId
		 *            Ohmer-Jan 15, 2014 4:29:17 PM
		 */
		public void isLogin(String callBackId) {
			callBackJs(String.valueOf(AppUtil.isLogin()), callBackId);
		}

		/**
		 * 获取系统资源路径
		 * 
		 * @param callBackId
		 *            Ohmer-Dec 31, 2013 9:44:39 AM
		 */
		public void getAssetsResFilePath(String callBackId) {
			callBackJs("'file:///android_asset'", callBackId);
		}

		/**
		 * 获取扩展路径
		 * 
		 * @param callBackId
		 *            Ohmer-Dec 31, 2013 9:44:08 AM
		 */
		public void getExternalFilePath(String callBackId) {
			callBackJs("'file://" + IntentUtil.getActivity().getFilesDir() + "'", callBackId);
		}

		/**
		 * 获取配置文件所在路径（config文件夹所在目录）
		 * 
		 * @param callBackId
		 *            Ohmer-Dec 31, 2013 9:47:21 AM
		 */
		public void getConfigFilePath(String callBackId) {
			if (Constants.DEBUG) {
				getExternalFilePath(callBackId);
			} else {
				getAssetsResFilePath(callBackId);
			}
		}

		/**
		 * 获取页面级参数
		 * 
		 * @param key
		 * @param callBackId
		 *            Ohmer-Jan 5, 2014 6:06:41 PM
		 */
		public void getPageParam(String key, String callBackId) {
			String value = JsViewUtility.getPageParam(key);
			callBackJs(value, callBackId);
		}

		/**
		 * 获得sharePreferencez中的数据，保存使用postEvent
		 * 
		 * @param key
		 * @param callBackId
		 *            Ohmer-Dec 31, 2013 9:52:03 AM
		 */
		public void getPreference(String key, String callBackId) {
			callBackJs(JsUtility.getPreference(key), callBackId);
		}

		public void postEvent(String methodName, String param1, String param2, String param3) {
			if (StringUtil.isEmpty(methodName)) {
				return;
			}
			LogUtil.d(TAG, "methodName:" + methodName);
			JsUtility.PostEvent("com.ecloudiot.framework.javascript.JsViewUtility", methodName, param1, param2, param3);
		}
	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		ArrayList<Object> params;

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case RESET_FRAME :
					try {
						params = (ArrayList<Object>) msg.obj;
					} catch (Exception e) {
						LogUtil.e(TAG, "enclosing_method error: " + e.toString());
					}
					if (params != null && params.size() > 0) {
						String methodName = (String) params.get(0);
						params.remove(0);
						Object[] args = new Object[params.size()];
						for (int i = 0; i < params.size(); i++) {
							args[i] = params.get(i);
						}
						ReflectionUtil.invokeMethod(WebWidget.this, methodName, args);
					}
					break;
				case LOAD_URL :
					String callBackJS = (String) msg.obj;
					webView.loadUrl(callBackJS);
					break;
				case JSAPI_CALL :
					try {
						params = (ArrayList<Object>) msg.obj;
					} catch (Exception e) {
						LogUtil.e(TAG, "enclosing_method error: " + e.toString());
					}
					JsAPI.callDeviceApi(params.get(0).toString(), params.get(1).toString(), params.get(2).toString(), params.get(3).toString());
					break;
				default :
					break;
			}
		}
	};
}
