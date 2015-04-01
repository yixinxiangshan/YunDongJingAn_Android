package com.ecloudiot.framework.utility;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ecloudiot.framework.activity.BaseActivity;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.appliction.BaseApplication;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.fragment.BaseFragment;
import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.javascript.JsEngine;
import com.ecloudiot.framework.utility.http.HttpAsyncClient;
import com.ecloudiot.framework.utility.http.HttpAsyncHandler;
import com.ecloudiot.framework.widget.BaseWidget;
import com.ecloudiot.framework.widget.model.BaseWidgetConfigModel;
import com.ecloudiot.framework.widget.model.BaseWidgetConfigModel.BaseWidgetConfigSetEventModel;
import com.ecloudiot.framework.widget.model.BaseWidgetConfigModel.ConfigDatasourceModel;
import com.ecloudiot.framework.widget.model.KeyValueModel;
import com.ecloudiot.framework.widget.model.KeyWidgetModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class PageUtil {
	private final static String TAG = "PageUtil";
	private static HashMap<String, Object> pages = new HashMap<String, Object>();

	/**
	 * 根据页面配置文件名 获取页面配置数据
	 * 
	 * @param pageName
	 *            页面配置文件名
	 * @return
	 */
	public static String getPageData(String pageName) {
		String fileNameString = "config";
		String[] pageArr = pageName.split("\\?");
		pageName = pageArr[0];
		if ("appconfig".equals(pageName)) {
			fileNameString = "javascript";
		}
		try {
			String pageString = "";
			if (Constants.DEBUG) {
				pageString = FileUtil.getSysFileString(IntentUtil.getActivity().getFilesDir() + "/" + fileNameString + "/" + pageName + ".json");
			} else {
				pageString = FileUtil.getSysFileString("assets/" + fileNameString + "/" + pageName + ".json");
			}

			/*
			 * String pageString = FileUtil.getSysFileString("assets/config/" + pageName + ".json"); // LogUtil.d(TAG, "getPageData : pageString : " +
			 * pageString);
			 */
			return pageString;
		} catch (Exception e) {
			LogUtil.e(TAG, "getPageData error: pageName may be invalid ...");
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 启动一个页面
	 * 
	 * @param pageName
	 *            : 页面配置文件名
	 * @param paramsString
	 *            : 需要在页面保存参数，KeyValueModel List 的Json
	 */
	public static void openPage(String pageName, String paramsString) {
		IntentUtil.openActivity(null, pageName, paramsString);
	}

	public static Object getPageById(String pageId) {
		return pages.get(pageId);
	}

	// 通过widgetid获取page，前提是pageid下只有一个page
	public static Object getPageByWidgetId(String widgetId) {
		int split = 0;
		for (int i = 1; i < widgetId.length(); i++) {
			String s = widgetId.substring(i, i + 1);
			if (s.equals(s.toUpperCase()) && !s.equals("_")) {
				split = i;
				break;
			}
		}
		String pageId = widgetId.substring(0, split - 1);
		// LogUtil.d(TAG, "getPageByWidgetId : " + pageId);
		return pages.get(pageId);
	}

	/**
	 * 初始页面：1、获得页面配置；2、保存页面级参数(configs)；3、初始化页面中的控件
	 * 
	 * @param pageName
	 * @param pageContext
	 * @return
	 */
	public static boolean initPage(String pageName, Object pageContext) {
		// LogUtil.d(TAG, "initPage : pageName = " + pageName + " - " + pageContext.hashCode());
		// 分析获取 page_id && page_class page_class 从page_id分化出来，page_id带参数
		String[] pageArr = pageName.split("\\?");
		String pageClass = pageArr[0];
		String pageString = getPageData(pageClass);
		if (StringUtil.isNotEmpty(pageString)) {
			putParam("pageString", pageString, pageContext);
			// start parse pageString
			JsonParser jsonParser = new JsonParser();
			JsonObject pageJsonObject = (JsonObject) jsonParser.parse(pageString);
			// save page_id && page_class 等url上的参数
			String pageId = pageName;
			putParam("page_id", pageId, pageContext);
			putParam("page_class", pageClass, pageContext);
			pages.put(pageId, pageContext);
			if (pageArr.length > 1) {
//				Map<String, String> urlParams = Splitter.on("&").trimResults().withKeyValueSeparator("=").split(pageArr[1]);
				Map<String, String> urlParams = new HashMap<String, String>();
				String[] paraArr = pageArr[1].split("&");
				for (int i = 0; i < paraArr.length; i++) {
					String[] paraSet = paraArr[i].split("=");
					urlParams.put(paraSet[0], paraSet[1]);
				}
				Iterator<String> iter = urlParams.keySet().iterator();
				String key;
				while (iter.hasNext()) {
					key = iter.next();
					putParam(key, urlParams.get(key), pageContext);
				}
			}
			// 记录fragment与当前page的父子关系，用于actionbar事件，查找遍历子fragment里面的事件，后面可以用于
			// top 方法
			if (pageContext instanceof ItemFragment) {
				ItemActivity nowActivity = (ItemActivity) BaseApplication.getInstance().getNowActivity();
				ItemFragment nowBaseFragment = (ItemFragment) nowActivity.getFragment(pageId);
				if (nowBaseFragment == null) {
					nowActivity.addFragment(pageId, (ItemFragment) pageContext);
				}
				((ItemFragment) pageContext).removeJsEvents();
				((ItemFragment) pageContext).removeWidgetJsEvents();
			}

			// 执行页面启动js,并赋给activity
			String jsString = "";
			if (Constants.DEBUG) {
				jsString = FileUtil.getSysFileString(IntentUtil.getActivity().getFilesDir() + "/config/" + pageClass + ".js");
				
			} else {
				jsString = FileUtil.getSysFileString("assets/config/" + pageClass + ".js");
			}
			
			JsEngine page_jsBuilder = new JsEngine(jsString, pageContext);
			if (pageContext instanceof ItemActivity) {
				((ItemActivity) pageContext).setJscript(page_jsBuilder);
			} else if (pageContext instanceof BaseFragment) {
				((ItemFragment) pageContext).setJscript(page_jsBuilder);
			}

			// get page layout
			String pageLayout = pageJsonObject.get("page_layout") != null ? pageJsonObject.get("page_layout").getAsString() : "";
			if (StringUtil.isNotEmpty(pageLayout)) {
				putParam("pageLayout", pageLayout, pageContext);
			}

			// 触发页面启动事件
			// HashMap<String, String> eventParams = new HashMap<String, String>();
			// eventParams.put("pageId", pageId);
			// if (JsManager.getInstance().callJsMethodSync(pageId, "onPageCreated", eventParams).equalsIgnoreCase("true")) {
			// return false;
			// }

			// save page inside configs
			String configsString = "";
			try {
				configsString = pageJsonObject.getAsJsonArray("configs").toString();
			} catch (Exception e) {
				LogUtil.e(TAG, "parsingPageData error: no params from page configs or ...");
				e.printStackTrace();
			}
			putParams(configsString, pageContext);
			// putParam("page_id", pageJsonObject.get("page_id").getAsString(), pageContext);
			// LogUtil.d(TAG, "initPage : pageContext = " + pageContext);
			// set event
			BaseWidgetConfigModel pageConfigModel = GsonUtil.fromJson(pageJsonObject, BaseWidgetConfigModel.class);
			if (pageConfigModel != null) {
				if (null != pageConfigModel.getSetEvent() && pageConfigModel.getSetEvent().size() > 0) {
					for (int i = 0; i < pageConfigModel.getSetEvent().size(); i++) {
						setEventPage(pageContext, pageConfigModel.getSetEvent().get(i));
					}
				}
			}

			// 替换 页面 layout
			try {
				ReflectionUtil.invokeMethod(pageContext, "checkContentView", null);
			} catch (Exception e) {
				LogUtil.e(TAG, "can not set content view ...");
				e.printStackTrace();
			}

			// 页面打开前可以设置停止，比如等待一些网络请求或者gps等，然后再继续page
			Boolean waitBeforeCreate = false;
			if (pageContext instanceof ItemActivity) {
				waitBeforeCreate = ((ItemActivity) pageContext).getWaitBeforeCreate();
			} else if (pageContext instanceof BaseFragment) {
				waitBeforeCreate = ((ItemFragment) pageContext).getWaitBeforeCreate();
			}
			if (waitBeforeCreate) {
				if (pageContext instanceof ItemActivity) {
					((ItemActivity) pageContext).setPageString(pageString);
				} else if (pageContext instanceof BaseFragment) {
					((ItemFragment) pageContext).setPageString(pageString);
				}
			} else {// 根据页面级数据源，获取页面级数据，
				getAndPutPageData(pageString, pageContext);
			}
		} else {
			LogUtil.e(TAG, "initPage error: pageString from pagename is null,make sure if it exict actually !");
		}
		return true;
	}

	// 页面打开前可以设置停止，比如等待一些网络请求或者gps等，然后再继续page
	public static void resumeWaitPage(Object pageContext) {
		// LogUtil.d(TAG, "resumeWaitPage");
		try {

			String pageString = "";
			if (pageContext instanceof ItemActivity) {
				pageString = ((ItemActivity) pageContext).getPageString();
			} else if (pageContext instanceof BaseFragment) {
				pageString = ((ItemFragment) pageContext).getPageString();
			}
			getAndPutPageData(pageString, pageContext);
		} catch (Exception e) {
			LogUtil.e(TAG, e.toString());
			e.printStackTrace();
		}
	}

	/**
	 * 替换当前页面
	 * 
	 * @param pageName
	 * @param params
	 */
	public static void replacePage(String pageName, String params) {
		Object pageContext = getNowPageContext();
		// ReflectionUtil.invokeMethod(pageContext, "clearSelf", null);
		putParams(params, pageContext);
		initPage(pageName, pageContext);
	}

	/**
	 * 
	 * @param pageString
	 * @param pageContext
	 */

	public static void getAndPutPageData(final String pageString, final Object pageContext) {
		if (StringUtil.isEmpty(pageString)) {
			LogUtil.e(TAG, "getPageData error: pageString is null ...");
		}
		JsonParser jsonParser = new JsonParser();
		JsonObject pageJsonObject = (JsonObject) jsonParser.parse(pageString);
		JsonObject dataSourceJsonObject = null;
		ConfigDatasourceModel datasourceModel = null;
		JsonObject pageDataJObject = null;
		try {
			dataSourceJsonObject = (JsonObject) pageJsonObject.get("datasource");
		} catch (Exception e) {
			LogUtil.e(TAG, "getPageData error: page dataSource is null ...");
			e.printStackTrace();
		}
		if (null == dataSourceJsonObject) {
			// LogUtil.d(TAG, "init controls directly ...");
			initControls(pageString, pageContext);
			return;
		}
		if (dataSourceJsonObject.get("data") != null && dataSourceJsonObject.get("data").isJsonObject()) {
			pageDataJObject = dataSourceJsonObject.get("data").getAsJsonObject();
		}
		try {
			datasourceModel = GsonUtil.fromJson(dataSourceJsonObject, ConfigDatasourceModel.class);
		} catch (Exception e) {
			LogUtil.e(TAG, "getAndPutPageData error: dataString is not valid in the part of datasource... or " + e.toString());
			e.printStackTrace();
			return;
		}

		// 网络获取数据
		final ConfigDatasourceModel finalDatasourceModel = datasourceModel;
		if (pageDataJObject == null || pageDataJObject.isJsonNull() || pageDataJObject.toString().equalsIgnoreCase("{}")) {
			if (StringUtil.isEmpty(datasourceModel.getMethod())) {
				initControls(pageString, pageContext);
				return;
			}
			if (StringUtil.isExpression(datasourceModel.getMethod())) {
				// 指定位置获取数据
				String dataString = WidgetUtil.getValuePurpose(pageContext, null, datasourceModel.getMethod(), null);
				String pageDataString = WidgetUtil.adaptWidgetData(pageContext, null, datasourceModel.getAdapter(), dataString);
				if (StringUtil.isNotEmpty(pageDataString)) {
					// LogUtil.d(TAG, "getAndPutPageData : DataString = " + dataString);
					putParam("pageData", pageDataString, pageContext);
					initControls(pageString, pageContext);
				}
				return;
			}
			// 使用 网络数据 （含缓存数据）
			// LogUtil.d(TAG, "getData : get data from net");
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("method", datasourceModel.getMethod());
			for (int i = 0; i < datasourceModel.getParams().size(); i++) {
				KeyValueModel keyValueItem = datasourceModel.getParams().get(i);
				if (StringUtil.isNotEmpty(new String[]{keyValueItem.getKey(), keyValueItem.getValue()})) {
					params.put(keyValueItem.getKey(), WidgetUtil.getValuePurpose(pageContext, null, keyValueItem.getValue(), null));
				}
			}
			// LogUtil.d(TAG, "getData : params = " + params);
			((BaseActivity) IntentUtil.getActivity()).setProgressIndeterminateVisible(true);
			ViewUtil.showLoadingDialog(IntentUtil.getActivity(), "", "正在请求网络...", false);
			HttpAsyncClient.Instance().post("", params, new HttpAsyncHandler() {

				@Override
				public void onFailure(String failResopnse) {
					ViewUtil.closeLoadingDianlog();
					((BaseActivity) IntentUtil.getActivity()).setProgressBarIndeterminateVisibility(false);
					Toast.makeText(IntentUtil.getActivity(), "网络出错，请稍后重试", Toast.LENGTH_SHORT).show();
					initControls(pageString, pageContext);
				}

				@Override
				public void onSuccess(String response) {
					ViewUtil.closeLoadingDianlog();
					((BaseActivity) IntentUtil.getActivity()).setProgressBarIndeterminateVisibility(false);
					if (StringUtil.isNotEmpty(response)) {
						String pageDataString = WidgetUtil.adaptWidgetData(pageContext, null, finalDatasourceModel.getAdapter(), response);
						putParam("pageData", pageDataString, pageContext);
					} else {
						LogUtil.w(TAG, "response data from net is null ");
					}
					initControls(pageString, pageContext);
				}

				@Override
				public void onProgress(Float progress) {

				}

				@Override
				public void onResponse(String resopnseString) {

				}
			});
		} else {
			// 使用 配置中 数据
			// LogUtil.d(TAG, "getData : start put widget data ...");
			String pageDataString = WidgetUtil.adaptWidgetData(pageContext, null, datasourceModel.getAdapter(), pageDataJObject.toString());
			putParam("pageData", pageDataString, pageContext);
			initControls(pageString, pageContext);
		}
	}

	/**
	 * 初始化页面中的控件
	 * 
	 * @param pageString
	 * @param pageContext
	 */
	public static void initControls(String pageString, Object pageContext) {
		// LogUtil.i(TAG, "Start initControls");
		ReflectionUtil.invokeMethod(pageContext, "beforeInitWidgets", null);
		if (StringUtil.isEmpty(pageString)) {
			// LogUtil.e(TAG, "initControls error: pageString is null ...");
			return;
		}
		JsonParser jsonParser = new JsonParser();
		JsonObject pageJsonObject = (JsonObject) jsonParser.parse(pageString);
		// 初始化控件
		JsonArray controlsList = null;
		JsonArray initControlsList = null;
		try {
			controlsList = pageJsonObject.getAsJsonArray("controls");
			initControlsList = pageJsonObject.getAsJsonArray("auto_start_controls");
		} catch (Exception e) {
			LogUtil.e(TAG, "parsingPageData error: controls in page configs may be null ...");
			e.printStackTrace();
		}
		if (null == controlsList || controlsList.size() <= 0) {
			// LogUtil.e(TAG, "initControls error: controlsList is null ...");
			return;
		}
		if (null == initControlsList || initControlsList.size() <= 0) {
			// LogUtil.e(TAG, "initControls error: initControlsList is null ...");
			return;
		}
		for (JsonElement jElement : initControlsList) {
			if (!jElement.isJsonPrimitive() || StringUtil.isEmpty(jElement.getAsString())) {
				// LogUtil.e(TAG, "initControls error: jElement in initControlsList is not null...");
				continue;
			}
			String initControlId = jElement.getAsString();
			for (int i = 0; i < controlsList.size(); i++) {
				JsonObject ctrlJsonObject = (JsonObject) controlsList.get(i);
				String controlId = "";
				try {
					controlId = ctrlJsonObject.get("control_id").getAsString();
				} catch (Exception e) {
					LogUtil.e(TAG, "initControls error: " + e.toString());
					e.printStackTrace();
				}

				if (StringUtil.isNotEmpty(controlId) && initControlId.equalsIgnoreCase(controlId)) {
					// 处理actionbar在有些时候不初始化的情况
					if (ctrlJsonObject.get("xtype").getAsString().equals("ActionBarWidget") && pageContext instanceof BaseFragment
					        && ((BaseFragment) pageContext).getNoActionBar()) {
						// LogUtil.d(TAG, "noactionbar");
					} else
						initWidget(ctrlJsonObject, pageContext);
					// 执行js里面的事件
					if (pageContext instanceof ItemActivity) {
						JsAPI.runEvent(((ItemActivity) pageContext).getWidgetJsEvents(), ctrlJsonObject.get("control_id").getAsString(), "onCreated", "");
					} else if (pageContext instanceof ItemFragment) {
						JsAPI.runEvent(((ItemFragment) pageContext).getWidgetJsEvents(), ctrlJsonObject.get("control_id").getAsString(), "onCreated", "");
					}
				}
			}
		}
		// 执行js里面的事件
		if (pageContext instanceof ItemActivity) {
			JsAPI.runEvent(((ItemActivity) pageContext).getJsEvents(), "onCreated", "");
		} else if (pageContext instanceof ItemFragment) {
			JsAPI.runEvent(((ItemFragment) pageContext).getJsEvents(), "onCreated", "");
		}
	}
	/**
	 * 指定配置中 controlId 启动widget
	 * 
	 * @param controlId
	 * @param pageContext
	 * @return
	 */
	public static Object initWidgetNamed(String controlId, Object pageContext) {
		JsonObject ctrlJsonObject = getWidgetConfigNamed(controlId, pageContext);
		if (ctrlJsonObject != null) {
			return initWidget(ctrlJsonObject, pageContext);
		}
		return null;
	}

	/**
	 * 获取指定control 的配置内容
	 * 
	 * @param controlId
	 * @param pageContext
	 * @return
	 */
	public static JsonObject getWidgetConfigNamed(String controlId, Object pageContext) {
		String pageString = getParam("pageString", pageContext);
		LogUtil.d(TAG, "initWidgetNamed : controlId = " + controlId);
		if (StringUtil.isEmpty(pageString)) {
			// LogUtil.e(TAG, "getWidgetConfig error: get pageString is null ...");
			return null;
		}
		JsonObject pageJsonObject = (JsonObject) (new JsonParser()).parse(pageString);
		JsonArray controlsList = null;
		try {
			controlsList = pageJsonObject.getAsJsonArray("controls");
		} catch (Exception e) {
			LogUtil.e(TAG, "getWidgetConfig error: " + e.toString());
			e.printStackTrace();
			return null;
		}
		if (null == controlsList || controlsList.size() <= 0) {
			// LogUtil.e(TAG, "getWidgetConfig error: controlsList is null ...");
			return null;
		}
		for (int i = 0; i < controlsList.size(); i++) {
			JsonObject ctrlJsonObject = (JsonObject) controlsList.get(i);
			String tempControlId = "";
			try {
				tempControlId = ctrlJsonObject.get("control_id").getAsString();
			} catch (Exception e) {
				LogUtil.e(TAG, "initControls error: " + e.toString());
				e.printStackTrace();
			}
			// LogUtil.d(TAG, "initWidgetNamed : tempControlId = " + tempControlId);
			if (StringUtil.isNotEmpty(tempControlId) && controlId.equalsIgnoreCase(tempControlId)) {
				return ctrlJsonObject;
			}
		}
		// LogUtil.e(TAG, "getWidgetConfigNamed error: i can not find any control named ...");
		return null;
	}

	public static String getWidgetConfigStringNamed(String controlId, Object pageContext) {
		Object object = getWidgetConfigNamed(controlId, pageContext);
		if (object != null) {
			return object.toString();
		}
		return "";
	}

	/**
	 * 保存 键值对 类的数据
	 * 
	 * @param paramString
	 */
	public static void putParam(KeyValueModel keyValueModel, Object pageContext) {
		if (null == keyValueModel) {
			// LogUtil.e(TAG, "putParam error: keyValueModel is null ...");
			return;
		}
		putParam(keyValueModel.getKey(), keyValueModel.getValue(), pageContext);
	}

	/**
	 * 保存 键值对 类的数据
	 * 
	 * @param paramString
	 */
	public static void putParam(String key, String value, Object pageContext) {
		if (StringUtil.isExpression(value)) {
			value = WidgetUtil.getValuePurpose(getNowPageContext(), null, value, null);
		}
		if (pageContext instanceof ItemActivity) {
			((ItemActivity) pageContext).putParam(key, value);
		} else if (pageContext instanceof BaseFragment) {
			((BaseFragment) pageContext).putParam(key, value);
		}
	}

	/**
	 * 保存 键值对 类的数据
	 * 
	 * @param paramsString
	 */
	public static void putParams(String paramsString, Object pageContext) {
		if (StringUtil.isEmpty(paramsString)) {
			// LogUtil.d(TAG, "putParams : paramsString is null ...");
			return;
		}
		JsonParser jParser = new JsonParser();
		JsonElement jElement = null;
		try {
			jElement = jParser.parse(paramsString);
		} catch (JsonSyntaxException e) {
			LogUtil.e(TAG, "putParams error: " + e.toString());
			e.printStackTrace();
			return;
		}
		if (jElement.isJsonArray()) {
			// 处理key-value model 数组
			JsonArray jArray = jElement.getAsJsonArray();
			if (null == jArray) {
				// LogUtil.d(TAG, "saveParams : configsList is null ...");
				return;
			}
			for (int i = 0; i < jArray.size(); i++) {
				KeyValueModel keyValueModel = GsonUtil.fromJson((JsonObject) jArray.get(i), KeyValueModel.class);
				if (keyValueModel != null)
					putParam(keyValueModel, pageContext);
			}
			return;
		}
		if (jElement.isJsonObject()) {
			// 处理普通的JSON
			JsonObject jObject = jElement.getAsJsonObject();
			Set<Map.Entry<String, JsonElement>> jSet = jObject.entrySet();
			Iterator<Map.Entry<String, JsonElement>> iterator = jSet.iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, JsonElement> entry = (Map.Entry<String, JsonElement>) iterator.next();
				if (entry.getValue().isJsonPrimitive()) {
					putParam(entry.getKey(), entry.getValue().getAsString(), pageContext);
				} else {
					putParam(entry.getKey(), entry.getValue().toString(), pageContext);
				}
			}
			return;
		}

	}

	/**
	 * 获取键值对 中的值
	 * 
	 * @param key
	 * @param pageContext
	 * @return
	 */
	public static String getParam(String key, Object pageContext) {
		if (pageContext instanceof ItemActivity) {
			return ((ItemActivity) pageContext).getParam(key);
		} else if (pageContext instanceof BaseFragment) {
			return ((BaseFragment) pageContext).getParam(key);
		}
		return null;
	}

	/**
	 * 初始化每个 控件
	 * 
	 * @param ctrlJsonObject
	 */
	public static Object initWidget(JsonObject ctrlJsonObject, Object pageContext) {
		String widgetLayoutName = "";
		String widgetName = "";
		try {
			widgetName = ctrlJsonObject.get("xtype").getAsString();
		} catch (Exception e1) {
			LogUtil.e(TAG, "initWidget error: from get widget name ");
			e1.printStackTrace();
		}
		try {
			widgetLayoutName = ctrlJsonObject.get("layout").getAsString();
		} catch (Exception e) {
			LogUtil.i(TAG, " widgetlayoutName in config is null ...");
			e.printStackTrace();
		}

		Class<?> widgetClass = null;
		try {
			// LogUtil.d(TAG, "initWidget:" + widgetName);
			widgetClass = Class.forName("com.ecloudiot.framework.widget." + widgetName);
			Constructor<?> c = widgetClass.getConstructor(new Class[]{Object.class, String.class, String.class});
			Object widget = c.newInstance(new Object[]{pageContext, ctrlJsonObject.toString(), widgetLayoutName});
			registerControlId(pageContext, widget, ctrlJsonObject);
			return widget;
		} catch (Exception e) {
			LogUtil.e(TAG, "initWidget error: " + e.toString());
			if (e instanceof InvocationTargetException) {
				LogUtil.e(TAG, ((InvocationTargetException) e).getTargetException().toString());
			} else {
				// doXXX()
			}
			e.printStackTrace();
			return null;
		}
	}

	public static void registerControlId(Object pageContext, Object widget, JsonObject ctrlJsonObject) {
		// LogUtil.d(TAG, "registerControlId context:"+pageContext.toString());
		// int widgetId = ((View) widget).getId();
		int widgetId = Math.abs(System.identityHashCode(((View) widget)));
		if (widgetId != 0) {
			String control_id = ctrlJsonObject.get("control_id").getAsString();
			KeyValueModel keyValueModel = new KeyValueModel();
			KeyWidgetModel keyWidgetModel = new KeyWidgetModel();
			String parentViewId = ((BaseWidget) widget).getParentViewId();
			// String widgetType = ctrlJsonObject.get("xtype").getAsString();
			// if (widgetType.equalsIgnoreCase("ActionBarWidget")) {
			// parentViewId = "abs__action_bar_container";
			// }
			String valueString = widgetId + "," + parentViewId;
			// LogUtil.d(TAG, "registerControlId control_id:"+control_id+" - valueString:"+valueString);
			keyValueModel.setKey(control_id);
			keyValueModel.setValue(valueString);
			ReflectionUtil.invokeMethod(pageContext, "putParam", keyValueModel);
			// 注册widget到activity
			keyWidgetModel.setKey(control_id);
			keyWidgetModel.setWidget((BaseWidget) widget);
			ReflectionUtil.invokeMethod(IntentUtil.getActivity(), "putWidget", keyWidgetModel);
			((View) widget).setId(widgetId);
		}
	}

	/**
	 * 替换Widget
	 * 
	 * @param controlId
	 */
	public static void replaceWidget(String controlId) {
		Object pageContext = getNowPageContext();
		JsonObject widgetJsonObject = getWidgetConfigNamed(controlId, pageContext);
		BaseWidgetConfigModel baseWidgetConfigModel = GsonUtil.fromJson(widgetJsonObject, BaseWidgetConfigModel.class);
		String parentViewId = null;
		int insertPosition = 0;
		for (int i = 0; i < baseWidgetConfigModel.getPosition().size(); i++) {
			KeyValueModel positionMapItem = baseWidgetConfigModel.getPosition().get(i);
			if (StringUtil.isNotEmpty(new String[]{positionMapItem.getKey(), positionMapItem.getValue()})) {
				if (positionMapItem.getKey().equalsIgnoreCase("parent")) {
					parentViewId = positionMapItem.getValue();
					continue;
				} else if (positionMapItem.getKey().equalsIgnoreCase("location")) {
					insertPosition = Integer.parseInt(positionMapItem.getValue());
				}
			}
		}
		if (StringUtil.isNotEmpty(parentViewId)) {
			LinearLayout layout = ViewUtil.getParentLayout(parentViewId, getNowPageContext());
			if (null != layout.getChildAt(insertPosition)) {
				layout.removeViewAt(insertPosition);
			}
			initWidgetNamed(controlId, pageContext);
		}
	}

	/**
	 * 替换Widget
	 * 
	 * @param controlId
	 */
	public static void replaceWidgets(String controlIds, String parentId) {
		// 清除contentview里面的所有控件
		if (StringUtil.isNotEmpty(parentId)) {
			// LinearLayout layout = ViewUtil.getParentLayout(parentId,
			// getNowPageContext());
			LinearLayout layout = ViewUtil.getParentLayout(parentId, ECApplication.getInstance());
			if (null != layout)
				layout.removeAllViews();
		}
		// 初始化controls
		JsonArray jsonArray = null;
		JsonElement jsonElement = new JsonParser().parse(controlIds);
		if (jsonElement.isJsonArray()) {
			jsonArray = jsonElement.getAsJsonArray();
			for (int i = 0; i < jsonArray.size(); ++i) {
				String controlId = jsonArray.get(i).getAsString();
				LogUtil.d(TAG, "controlId=" + controlId);
				replaceWidget(controlId);
			}
		}
	}

	/**
	 * 获取当前页面级数据源
	 * 
	 * @return
	 */
	public static Object getNowPageContext() {
		return ECApplication.getInstance().getNowPageContext();
	}

	/**
	 * 获取当前pageId
	 * 
	 * @return Ohmer-Dec 19, 2013 9:57:29 AM
	 */
	public static String getNowPageId() {
		Object pageContext = getNowPageContext();
		String nowPageId = "";
		try {
			nowPageId = (String) ReflectionUtil.invokeMethod(pageContext, "getParam", "page_id");
		} catch (Exception e) {
			LogUtil.e(TAG, "getNowControlId error: " + e.toString());
			e.printStackTrace();
		}
		// LogUtil.d(TAG, "getNowPageId : nowPageId = " + nowPageId);
		return nowPageId;
	}

	/**
	 * 设置页面级事件监听器
	 * 
	 * @param pageContext
	 * @param eventModel
	 */
	public static void setEventPage(Object pageContext, BaseWidgetConfigSetEventModel eventModel) {
		EventUtil.setEvent(pageContext, null, eventModel, EventUtil.EVENTPAGE);
	}

	public static HashMap<String, Object> getPages() {
		return pages;
	}

	public static void setPages(HashMap<String, Object> pages) {
		PageUtil.pages = pages;
	}

}
