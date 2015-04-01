package com.ecloudiot.framework.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.widget.Toast;

import com.ecloudiot.framework.activity.BaseActivity;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.utility.http.HttpAsyncClient;
import com.ecloudiot.framework.utility.http.HttpAsyncHandler;
import com.ecloudiot.framework.widget.BaseWidget;
import com.ecloudiot.framework.widget.model.BaseWidgetConfigModel.ConfigDatasourceModel;
import com.ecloudiot.framework.widget.model.BaseWidgetConfigModel.BaseWidgetConfigSetEventModel;
import com.ecloudiot.framework.widget.model.KeyValueModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class WidgetUtil {
	private final static String TAG = "WidgetUtil";

	public final static int INIT_WIDGET = 11;
	public final static int REFRESH_WIDGET = 12;
	public final static int ADDDATA_WIDGET = 13;

	public static void setEventWidget(Object pageContext, Object widget, BaseWidgetConfigSetEventModel eventModel) {
		EventUtil.setEvent(pageContext, widget, eventModel, EventUtil.EVENTWIDGET);
	}

	/**
	 * 根据数据配置 获得最后可执行的JS，含获取相应参数
	 * 
	 * @param pageContext
	 * @param widget
	 * @param eventModel
	 * @return
	 */
	public static String getEventJString(Object pageContext, Object widget, BaseWidgetConfigSetEventModel eventModel) {
		return getEventJString(pageContext, widget, eventModel, null);
	}

	/**
	 * 根据数据配置 获得最后可执行的JS，含获取相应参数
	 * 
	 * @param pageContext
	 * @param widget
	 * @param eventModel
	 * @return
	 */
	public static String getEventJString(Object pageContext, Object widget, BaseWidgetConfigSetEventModel eventModel, Object bundle) {
		String jString = "var params = decodeURIComponent('";
		for (int i = 0; i < eventModel.getParams().size(); i++) {
			KeyValueModel keyValueModel = eventModel.getParams().get(i);
			String string = getValuePurpose(pageContext, widget, keyValueModel.getValue(), bundle);
			keyValueModel.setValue(string);
			if (StringUtil.isEmpty(keyValueModel.getValue()) && StringUtil.isNotEmpty(keyValueModel.getDefaultValue())) {
				keyValueModel.setValue(keyValueModel.getDefaultValue());
			}
		}
		// LogUtil.d(TAG, "getEventJString : jString = " +
		// GsonUtil.toJson(eventModel.getParams()));
		jString += GsonUtil.toJson(eventModel.getParams()) + "').evalJSON();";
		// LogUtil.d(TAG, "jString:" + jString);
		return jString += eventModel.getJavascript();
	}

	public static String getValuePurpose(String controlId, String desc, String bundleString) {
		BaseWidget widget = null;
		// LogUtil.d(TAG, "widget: " + controlId);
		if (StringUtil.isNotEmpty(controlId)) {
			widget = (BaseWidget) ReflectionUtil.invokeMethod(IntentUtil.getActivity(), "getWidget", controlId);
			// LogUtil.d(TAG, "widget: " + widget.toString());
			// widget = ViewUtil.getWidget(controlId);
		}
		HashMap<String, String> bundleData = new HashMap<String, String>();
		if (StringUtil.isNotEmpty(bundleString)) {
			bundleData = (HashMap<String, String>) GsonUtil.toHashMap(bundleString);
		}
		return getValuePurpose(PageUtil.getNowPageContext(), widget, desc, bundleData);
	}

	/**
	 * 根据字符串获得对应值
	 * 
	 * @param pageContext
	 *            页面上下文
	 * @param widget
	 *            当前控件
	 * @param desc
	 *            值位置描述 "{***}",大括号括住，否则原值返回
	 * @param bundleData
	 *            辅助值
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getValuePurpose(Object pageContext, Object widget, String desc, Object bundleData) {
		if (StringUtil.isEmpty(desc)) {
			// LogUtil.d(TAG, "getEventParamValue : desc is null");
			return "";
		}
		// LogUtil.d(TAG, "getEventParamValue : desc = " + desc);
		String descString = "";
		if (!StringUtil.isExpression(desc)) {
			// LogUtil.d(TAG, "desc is not Expression ...");
			return desc;
		} else {
			descString = StringUtil.slim(desc);
		}
		// LogUtil.d(TAG, "getEventParamValue : descString = " + descString);
		String value = "";
		if (descString.contains("}+{")) {
			descString = "{" + descString + "}";
			// LogUtil.i(TAG, "getValuePurpose : descString = " + descString);
			String[] descs = descString.split("\\+");
			for (String string : descs) {
				value = value + getValuePurpose(pageContext, widget, string, bundleData);
			}
		} else {
			if (descString.startsWith("#")) {
				// 执行js表达式
				descString = StringUtil.slimH(descString);
				if (StringUtil.isNotEmpty(descString)) {
					// LogUtil.d(TAG, "getValuePurpose : descString = " + descString);
					// return JsUtility.runJs(descString);
				}
				return desc;
			}
			if (descString.startsWith("$")) {
				// 返回字符串本身
				descString = StringUtil.slimH(descString);
				if (StringUtil.isNotEmpty(descString)) {
					return descString;
				}
				return desc;
			}

			String[] ss = null;
			String valueRec = "";
			String valueKey = "";
			if (descString.contains(".")) {
				ss = descString.split("\\.");
			}
			if (null == ss || ss.length < 1) {
				valueRec = descString;
			} else {
				valueRec = ss[0];
				valueKey = ss[1];
			}
			if (valueRec.equalsIgnoreCase("_app")) {
				value = ECApplication.getInstance().readConfig(valueKey);
			} else if (valueRec.equalsIgnoreCase("_page")) {
				try {
					value = (String) ReflectionUtil.invokeMethod(pageContext, "getParam", valueKey);
				} catch (Exception e) {
					LogUtil.e(TAG, "getEventParamValue error: " + e.toString());
				}

			} else if (valueRec.equalsIgnoreCase("_pageData")) {
				String pageDataString = PageUtil.getParam("pageData", pageContext);
				if (StringUtil.isEmpty(pageDataString)) {
					LogUtil.e(TAG, "getValuePurpose error: pageDataString is null ...");
					return null;
				}
				if (StringUtil.isEmpty(valueKey)) {
					return pageDataString;
				}
				JsonObject jObject = (JsonObject) (new JsonParser()).parse(pageDataString);
				try {
					Object obj = jObject;
					for (int i = 1; i < ss.length; i++) {
						obj = getJElement(widget, obj, ss[i]);
					}
					if (obj instanceof String && null != obj) {
						value = (String) obj;
					} else if (null != obj) {
						value = obj.toString();
					}
				} catch (Exception e) {
					LogUtil.e(TAG, "getEventParamValue error: " + e.toString());
					e.printStackTrace();
				}
			} else if (valueRec.equalsIgnoreCase("_widgetConfig")) {
				try {
					value = (String) ReflectionUtil.invokeMethod(widget, "getParam", valueKey);
				} catch (Exception e) {
					LogUtil.e(TAG, "getEventParamValue error: " + e.toString());
				}

			} else if (valueRec.equalsIgnoreCase("_widgetData")) {

				Object dataModel = ReflectionUtil.invokeMethod(widget, "getDataModel", null);
				if (null == dataModel) {
					LogUtil.e(TAG, widget.toString() + " get Data Model is null ...");
					for (int i = 1; i < ss.length; i++) {
						dataModel = ReflectionUtil.getModelData(dataModel, ss[i]);
					}
					return "";
				}
				for (int i = 1; i < ss.length; i++) {
					dataModel = ReflectionUtil.getModelData(dataModel, ss[i]);
				}
				if (dataModel instanceof String && null != dataModel) {
					value = (String) dataModel;
				} else if (null != dataModel) {
					value = dataModel.toString();
				} else {
					return "";
				}
				// LogUtil.d(TAG, "getValuePurpose : value = " + value);
			} else if (valueRec.equalsIgnoreCase("_self")) {
				if (bundleData instanceof HashMap) {
					try {
						value = ((HashMap<String, String>) bundleData).get(valueKey);
					} catch (Exception e) {
						LogUtil.e(TAG, "getEventParamValue error: unknown");
					}
				} else if (bundleData instanceof JsonObject) {
					JsonElement jElement = ((JsonObject) bundleData).get(valueKey);
					if (jElement != null && !jElement.isJsonNull() && jElement.isJsonPrimitive()) {
						value = jElement.getAsString();
					}
				} else {
					Object dataModel = bundleData;
					for (int i = 1; i < ss.length; i++) {
						String getString = ss[i];
						dataModel = ReflectionUtil.getModelData(dataModel, getString);
					}
					if (null != dataModel && dataModel instanceof String) {
						value = (String) dataModel;
					}
				}
			}
			// LogUtil.d(TAG, "getValuePurpose : value = " + value);
		}

		return StringUtil.isNotEmpty(value) ? value : "";
	}

	/**
	 * 根据指定数据源获取数据,并显示在控件上
	 * 
	 * @param pageContext
	 * @param widget
	 * @param configString
	 */
	public static void getData(Object pageContext, final Object widget, String configString) {
		getWidgetData(pageContext, widget, configString, INIT_WIDGET);
	}

	/**
	 * 根据指定数据源获取数据，刷新控件数据
	 * 
	 * @param pageContext
	 * @param widget
	 * @param configString
	 */
	public static void refreshData(Object pageContext, final Object widget, String configString) {
		getWidgetData(pageContext, widget, configString, REFRESH_WIDGET);
	}

	/**
	 * ListView 及 GridView 默认加载更多事件
	 * 
	 * @param pageContext
	 * @param widget
	 * @param datasourceModel
	 * @param lastId
	 */
	public static void addMoreData(Object pageContext, final Object widget, ConfigDatasourceModel datasourceModel, String lastId) {
		HashMap<String, String> params = getConfigNetParams(pageContext, widget, datasourceModel);
		params.put("lastid", lastId);
		postWidgetNet(pageContext, widget, params, ADDDATA_WIDGET);
	}

	/**
	 * 获取控件数据
	 * 
	 * @param pageContext
	 * @param widget
	 * @param configString
	 * @param status
	 *            刷新/初始化
	 */
	public static void getWidgetData(Object pageContext, Object widget, String configString, final int status) {
		JsonObject widgetDataJObject = null;
		ConfigDatasourceModel datasourceModel = null;
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = (JsonObject) jsonParser.parse(configString);
		try {
			widgetDataJObject = jsonObject.getAsJsonObject("datasource").getAsJsonObject("data");
		} catch (Exception e) {
			LogUtil.e(TAG, "parsingData error: dataString is not valid in the part of datasource... or " + e.toString());
			return;
		}
		if (jsonObject.get("datasource") != null && jsonObject.get("datasource").isJsonObject()) {
			datasourceModel = GsonUtil.fromJson(jsonObject.getAsJsonObject("datasource"), ConfigDatasourceModel.class);
		}

		if (widgetDataJObject == null || widgetDataJObject.isJsonNull() || widgetDataJObject.toString().equalsIgnoreCase("{}")) {
			// widget data 数据被手工设置过
			// 执行js里面的事件
			String widgetDataString = "";
			if (pageContext instanceof ItemActivity) {
				widgetDataString = ((ItemActivity) pageContext).getWidgetData(jsonObject.get("control_id").getAsString());
			} else if (pageContext instanceof ItemFragment) {
				widgetDataString = ((ItemFragment) pageContext).getWidgetData(jsonObject.get("control_id").getAsString());
			}

			if (!StringUtil.isEmpty(widgetDataString)) {
				putWidgetData(pageContext, widget, widgetDataString, status);
				return;
			}
			if (StringUtil.isEmpty(datasourceModel.getMethod())) {
				putWidgetData(pageContext, widget, "", status);
				return;
			}
			if (StringUtil.isExpression(datasourceModel.getMethod())) {
				// 指定位置获取数据
				String dataString = getValuePurpose(pageContext, widget, datasourceModel.getMethod(), null);
				putWidgetData(pageContext, widget, dataString, status);
				// LogUtil.d(TAG, "getData : widgetDataString = " + dataString);
				return;
			}
			// 使用 网络数据 （含缓存数据）
			if (StringUtil.isEmpty(datasourceModel.getMethod())) {
				LogUtil.e(TAG, "getWidgetData error: try get net data but datasourceModel.getMethod() is null ...");
				return;
			}
			// LogUtil.d(TAG, "getData : get data from net");
			HashMap<String, String> params = getConfigNetParams(pageContext, widgetDataJObject, datasourceModel);
			postWidgetNet(pageContext, widget, params, status);
		} else {
			// 使用 配置中 数据
			// LogUtil.d(TAG, "getData : start put widget data ...");
			String widgetDataString = widgetDataJObject.toString();
			putWidgetData(pageContext, widget, widgetDataString, status);
		}
	}

	/**
	 * 根据data source 适配获取网络请求参数
	 * 
	 * @param pageContext
	 * @param widget
	 * @param datasourceModel
	 * @return
	 */
	public static HashMap<String, String> getConfigNetParams(Object pageContext, Object widget, ConfigDatasourceModel datasourceModel) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("method", datasourceModel.getMethod());
		for (int i = 0; i < datasourceModel.getParams().size(); i++) {
			KeyValueModel keyValueItem = datasourceModel.getParams().get(i);
			if (StringUtil.isNotEmpty(new String[]{keyValueItem.getKey(), keyValueItem.getValue()})) {
				params.put(keyValueItem.getKey(), getValuePurpose(pageContext, widget, keyValueItem.getValue(), null));
			}
		}
		return params;
	}

	/**
	 * 请求widget 网络数据
	 * 
	 * @param pageContext
	 * @param widget
	 * @param params
	 * @param status
	 */
	public static void postWidgetNet(final Object pageContext, final Object widget, HashMap<String, String> params, final int status) {
		((BaseActivity) IntentUtil.getActivity()).setProgressIndeterminateVisible(true);
		// ViewUtil.showLoadingDialog(IntentUtil.getActivity(), "", "正在请求网络...",
		// false);
		HttpAsyncHandler httpResponseHandler = new HttpAsyncHandler() {

			@Override
			public void onFailure(String failResopnse) {
				((BaseActivity) IntentUtil.getActivity()).setProgressIndeterminateVisible(false);
				if (widget != null && widget instanceof BaseWidget) {
					// BaseWidget baseWidget = (BaseWidget) widget;
					// if (JsManager.getInstance().callJsMethodSyncS(baseWidget.getControlId(), "onNetDataFailure", failResopnse).equalsIgnoreCase("true")) {
					// return;
					// }
				}
				String errorDes = GsonUtil.getStringElement(failResopnse, "errors[0].error_msg");
				int errorNum = GsonUtil.getIntElement(failResopnse, "errors[0].error_num");
				// LogUtil.e(TAG,
				// "onFailure : errorNum = "+errorNum+" , errorDes = "+errorDes);
				if (errorNum >= 200000 && errorNum < 300000 && StringUtil.isNotEmpty(errorDes)) {
					Toast.makeText(IntentUtil.getActivity(), errorDes, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(IntentUtil.getActivity(), "网络访问出错，请稍后重试！", Toast.LENGTH_SHORT).show();
				}
				// ((BaseActivity)
				// IntentUtil.getActivity()).setProgressIndeterminateVisible(false);
				// String widgetDataString = "";
				// LogUtil.d(TAG, "onFailure : widgetDataString = " +
				// widgetDataString);
				// putWidgetData(pageContext, widget, widgetDataString, status);
			}

			@Override
			public void onSuccess(String response) {
				((BaseActivity) IntentUtil.getActivity()).setProgressIndeterminateVisible(false);
				String widgetDataString = response;
				// LogUtil.d(TAG, "onSuccess : widgetDataString = " + widgetDataString);
				putWidgetData(pageContext, widget, widgetDataString, status);
			}

			@Override
			public void onProgress(Float progress) {
				((BaseActivity) IntentUtil.getActivity()).setProgress(progress);
			}

			@Override
			public void onResponse(String resopnseString) {
				// TODO Auto-generated method stub

			}
		};
		if (status == REFRESH_WIDGET || status == ADDDATA_WIDGET) {
			HttpAsyncClient.Instance().post("", params, httpResponseHandler, 0);
		} else {
			HttpAsyncClient.Instance().post("", params, httpResponseHandler);
		}
	}

	/**
	 * 添加数据到控件
	 * 
	 * @param pageContext
	 * @param widget
	 * @param widgetDataString
	 * @param status
	 */
	public static void putWidgetData(Object pageContext, Object widget, String widgetDataString, int status) {
		switch (status) {
			case INIT_WIDGET :
				// ((BaseWidget)widget).putWidgetData(widgetDataString);
				ReflectionUtil.invokeMethod(widget, "putWidgetData", widgetDataString);
				break;
			case REFRESH_WIDGET :
				ReflectionUtil.invokeMethod(widget, "refreshData", widgetDataString);
				break;
			case ADDDATA_WIDGET :
				ReflectionUtil.invokeMethod(widget, "addData", widgetDataString);
				break;
			default :
				break;
		}
	}

	public static String adaptWidgetData(Object pageContext, Object widget, String dataSourceString, String resString) {
		ConfigDatasourceModel datasourceModel = GsonUtil.fromJson(dataSourceString, ConfigDatasourceModel.class);
		if (datasourceModel != null) {
			return adaptWidgetData(pageContext, widget, datasourceModel.getAdapter(), resString);
		}
		return "";
	}

	/**
	 * 适配数据,适配模式分为：light|modest|deep|free light:简单字符串替换 modest:数据源可指定深度,暂不支持数组 deep:数据源和返回数据都可指定深度,暂不支持数组 free:支持数组，动态获取数据等强大功能
	 * 
	 * @param adapters
	 * @param resString
	 * @return
	 */
	public static String adaptWidgetData(Object pageContext, Object widget, List<KeyValueModel> adapters, String resString) {
		String adapterModel = "light";
		if (null == adapters || adapters.size() <= 0) {
			return resString;
		}
		if (StringUtil.isEmpty(resString))
			return "";
		for (int i = 0; i < adapters.size(); i++) {
			KeyValueModel keyValueModel = adapters.get(i);
			if (keyValueModel.getKey().equalsIgnoreCase("model") && StringUtil.isNotEmpty(keyValueModel.getValue())) {
				adapterModel = keyValueModel.getValue();
				adapters.remove(i);
				break;
			}
		}
		if (adapterModel.equalsIgnoreCase("light")) {
			return adapterDatalight(adapters, resString);
		} else if (adapterModel.equalsIgnoreCase("modest")) {
			return adapterDataModest(adapters, resString);
		} else if (adapterModel.equalsIgnoreCase("deep")) {
			return adapterDataDeep(pageContext, widget, adapters, resString);
		} else if (adapterModel.equalsIgnoreCase("free")) {
			return adapterDataFree(pageContext, widget, adapters, resString);
		}
		return resString;
	}

	/**
	 * light:简单字符串替换
	 * 
	 * @param adapters
	 * @param resString
	 * @return
	 */
	public static String adapterDatalight(List<KeyValueModel> adapters, String resString) {
		// LogUtil.d(TAG, "adapterDatalight : adapters" + GsonUtil.toJson(adapters));
		for (int i = 0; i < adapters.size(); i++) {
			KeyValueModel keyValueModel = adapters.get(i);
			// LogUtil.d(TAG, "adapterDatalight : keyValueModel.getKey() = " + keyValueModel.getKey());
			Pattern pattern = Pattern.compile("\\\"" + keyValueModel.getKey() + "\\\"");
			Matcher matcher = pattern.matcher(resString);
			resString = matcher.replaceAll("\"" + keyValueModel.getValue() + "\"");
		}
		return resString;
	}

	/**
	 * modest:数据源可指定深度,暂不支持数组
	 * 
	 * @param adapters
	 * @param resString
	 * @return
	 */
	public static String adapterDataModest(List<KeyValueModel> adapters, String resString) {
		// LogUtil.d(TAG, "adapterDataModest : ...");
		JsonParser jParser = new JsonParser();
		JsonElement sourcdElement = jParser.parse(resString);
		for (int i = 0; i < adapters.size(); i++) {
			String from = adapters.get(i).getKey();
			String to = adapters.get(i).getValue();
			String[] froms = null;
			if (from.contains(".")) {
				froms = from.split("\\.");
			} else {
				froms = new String[]{from};
			}
			if (!sourcdElement.isJsonObject()) {
				LogUtil.e(TAG, "adapterDataModest error: resString is invalid or is not supported now ..");
				return resString;
			}
			JsonObject tempElement = (JsonObject) sourcdElement;
			try {
				for (int j = 0; j < froms.length - 1; j++) {
					tempElement = tempElement.get(froms[j]).getAsJsonObject();
				}
			} catch (Exception e) {
				LogUtil.e(TAG, "adapterDataModest error: resString is invalid or is not supported now ...");
				return resString;
			}
			JsonElement element = ((JsonObject) tempElement).get(froms[froms.length - 1]);
			tempElement.add(to, element);
		}
		return sourcdElement.toString();
	}

	/**
	 * deep:数据源和返回数据都可指定深度,暂不支持数组
	 * 
	 * @param adapters
	 * @param resString
	 * @return
	 */
	public static String adapterDataDeep(Object pageContext, Object widget, List<KeyValueModel> adapters, String resString) {
		// LogUtil.d(TAG, "adapterDataDeep : ...");
		JsonParser jParser = new JsonParser();
		JsonElement sourcdElement = jParser.parse(resString);
		JsonObject resultElement = new JsonObject();
		for (int i = 0; i < adapters.size(); i++) {
			String from = adapters.get(i).getKey();
			String to = adapters.get(i).getValue();
			String[] froms = null;
			String[] tos = null;
			if (from.contains(".")) {
				froms = from.split("\\.");
			} else {
				froms = new String[]{from};
			}
			if (to.contains(".")) {
				tos = to.split("\\.");
			} else {
				tos = new String[]{to};
			}
			JsonElement element = null;
			// 构造 目标值
			if (StringUtil.isExpression(from)) {
				String string = WidgetUtil.getValuePurpose(pageContext, widget, from, resString);
				if (StringUtil.isNotEmpty(string)) {
					element = (new JsonParser()).parse(string);
				}
			} else {
				if (!sourcdElement.isJsonObject()) {
					LogUtil.e(TAG, "adapterDataModest error: resString is invalid or is not supported now ..");
					return resString;
				}
				JsonObject tempElement = (JsonObject) sourcdElement;
				try {
					for (int j = 0; j < froms.length - 1; j++) {
						tempElement = tempElement.get(froms[j]).getAsJsonObject();
					}
				} catch (Exception e) {
					LogUtil.e(TAG, "adapterDataModest error: resString is invalid or is not supported now ...");
					return resString;
				}
				element = ((JsonObject) tempElement).get(froms[froms.length - 1]);
			}
			// 构造目标key
			JsonObject tempObject = resultElement;
			for (int j = 0; j < tos.length - 1; j++) {
				if (null == tempObject.get(tos[j]) || tempObject.get(tos[j]).isJsonNull()) {
					tempObject.add(tos[j], new JsonObject());
				}
				tempObject = tempObject.get(tos[j]).getAsJsonObject();
			}
			tempObject.add(tos[tos.length - 1], element);

		}
		return resultElement.toString();
	}

	/**
	 * 更加强大的数据适配，支持数组list[]、表达式"{#***}"、指定值"{$***}"
	 * 
	 * @param pageContext
	 * @param widget
	 * @param adapters
	 * @param resString
	 * @return
	 */
	public static String adapterDataFree(Object pageContext, Object widget, List<KeyValueModel> adapters, String resString) {
		// LogUtil.d(TAG, "adapterDataFree : start ...");
		JsonParser jParser = new JsonParser();
		JsonElement sourcdElement = jParser.parse(resString);
		JsonObject resultElement = new JsonObject();
		for (int i = 0; i < adapters.size(); i++) {
			String from = adapters.get(i).getKey();
			String to = adapters.get(i).getValue();
			String[] froms = null;
			String[] tos = null;
			if (from.contains(".")) {
				froms = from.split("\\.(?![^\\{\\}]*\\})");
			} else {
				froms = new String[]{from};
			}
			if (to.contains(".")) {
				tos = to.split("\\.");
			} else {
				tos = new String[]{to};
			}
			Object object = null;
			// 构造 目标值
			if (StringUtil.isExpression(from)) {
				String string = WidgetUtil.getValuePurpose(pageContext, widget, from, resString);
				if (StringUtil.isNotEmpty(string)) {
					// LogUtil.d(TAG, "adapterDataFree : string  from Expression = " + string);
					try {
						object = (new JsonParser()).parse(string);
						object = (object == null || ((JsonElement) object).isJsonNull()) ? null : (new JsonParser()).parse(string);
					} catch (Exception e) {
						LogUtil.e(TAG, "adapterDataFree JsonParser error: " + e.toString());
					}
					if (object == null) {
						object = new JsonPrimitive(string);
					}
				}
			} else {
				object = GsonUtil.deepCopyJElement(sourcdElement);
				for (int j = 0; j < froms.length; j++) {
					// LogUtil.d(TAG, "getJElement : key = " + froms[j] +
					// "  object = " + object.toString());
					object = getJElement(widget, object, froms[j]);
				}
			}

			// 构造目标key
			if (null == object) {
				LogUtil.e(TAG, "adapterDataFree error: object is null ...(after get)");
			} else {
				// LogUtil.i(TAG, "adapterDataFree : object = " + object.toString());
				for (int j = 0; j < tos.length; j++) {
					// LogUtil.v(TAG, "before putJElement : key = " + tos[tos.length - 1 - j] + "  object = " + object.toString());
					object = putJElement(object, tos[tos.length - 1 - j]);
					// LogUtil.d(TAG, "after putJElement : key = " + tos[tos.length - 1 - j] + "  object = " + object.toString());
				}
				if (object == null || !(object instanceof JsonElement)) {
					LogUtil.e(TAG, "adapterDataFree error: object is null ...(after put)");
				} else {
					resultElement = (JsonObject) GsonUtil.merge(resultElement, (JsonElement) object);
					// LogUtil.d("resultElement", "adapterDataFree : resultElement = " + resultElement);
				}
			}

		}
		// LogUtil.d(TAG, "adapterDataFree : resultElement = " + resultElement.toString());
		return resultElement.toString();
	}

	/**
	 * 根据key 获得JSONELEMENT 子对象
	 * 
	 * @param jElement
	 * @param key
	 */
	@SuppressWarnings("unchecked")
	public static Object getJElement(Object widget, Object object, String key) {
		if (StringUtil.isEmpty(key)) {
			return object;
		}
		if (!(object instanceof JsonElement)) {
			if (object instanceof ArrayList) {
				ArrayList<Object> eList = (ArrayList<Object>) object;
				ArrayList<Object> eListReturn = new ArrayList<Object>();
				for (Object object2 : eList) {
					eListReturn.add(getJElement(widget, object2, key));
				}
				return eListReturn;
			} else {
				return object;
			}
		}
		JsonElement jElement = (JsonElement) object;
		if (jElement.isJsonArray()) {
			JsonArray jArray = jElement.getAsJsonArray();
			ArrayList<Object> eList = new ArrayList<Object>();
			for (JsonElement jsonElement : jArray) {
				eList.add(getJElement(widget, jsonElement, key));
			}
			return eList;
		} else if (jElement.isJsonObject()) {
			if (key.contains("]") && key.contains("]")) {
				int leftbracketPosition = key.indexOf("[");
				int rightbracketPosition = key.indexOf("]");
				// get array
				String keyS = key.substring(0, leftbracketPosition);
				JsonObject jObject = jElement.getAsJsonObject();
				if ((leftbracketPosition + 1) == rightbracketPosition) {
					return (jObject.get(keyS) != null && jObject.get(keyS).isJsonArray()) ? jObject.get(keyS) : null;
				} else {
					// 指定数组位置
					// LogUtil.d(TAG, "getJElement : key = " + key +
					// " , leftbracketPosition = " + leftbracketPosition
					// + " , rightbracketPosition = " + rightbracketPosition);
					int position = Integer.parseInt(key.substring(leftbracketPosition + 1, rightbracketPosition));
					return (jObject.get(keyS) != null && jObject.get(keyS).isJsonArray() && position < jObject.get(keyS).getAsJsonArray().size()) ? jObject
					        .get(keyS).getAsJsonArray().get(position) : null;
				}
			} else if (StringUtil.isExpression(key)) {
				return getValuePurpose(PageUtil.getNowPageContext(), widget, key, object);
			} else {
				// get element
				JsonObject jObject = jElement.getAsJsonObject();
				JsonElement jElement2 = jObject.get(key);
				// LogUtil.d(TAG, "getJElement : jElement2 = " + jElement2 +
				// ", jObject = " + jObject);
				if (jElement2.isJsonPrimitive()) {
					return jElement2.getAsString();
				}
				// return
				// jObject.get(key).isJsonPrimitive()?jObject.get(key).getAsString():jObject.get(key);
				return jObject.get(key);
			}
		} else if (jElement.isJsonPrimitive()) {
			return jElement.getAsString();
		}
		return null;
	}

	/**
	 * 根据key 和 对象 构造 可添加对象（JSONELEMENT || LIST<JSONELEMENT>）
	 * 
	 * @param object
	 * @param key
	 * @return
	 */
	public static Object putJElement(Object object, String key) {
		JsonObject jObject = new JsonObject();
		if (object instanceof ArrayList) {
			@SuppressWarnings("unchecked")
			ArrayList<Object> array = (ArrayList<Object>) object;
			if (key.contains("[]")) {
				// 该层级子内容为数组
				key = key.substring(0, key.length() - 2);
				JsonArray jArray = new JsonArray();
				boolean matchDeep = false;
				for (Object object2 : array) {
					if (object2 instanceof JsonElement) {
						jArray.add((JsonElement) object2);
						jObject.add(key, jArray);
					} else if (object2 instanceof ArrayList) {
						matchDeep = true;
						jArray.add((JsonElement) putJElement(object2, key + "[]"));
					}
				}
				return matchDeep ? jArray : jObject;
			} else {
				// 该层级仍为数组
				ArrayList<Object> array2 = new ArrayList<Object>();
				for (Object object2 : array) {
					array2.add(putJElement(object2, key));
				}
				return array2;
			}
		} else {
			if (object instanceof JsonElement) {
				if (key.contains("[]")) {
					key = key.substring(0, key.length() - 2);
					if (!(object instanceof JsonArray)) {
						JsonArray jArray = new JsonArray();
						jArray.add((JsonElement) object);
						object = jArray;
					}
				} else {
					Pattern pattern = Pattern.compile("\\[(\\d*)\\]");
					Matcher matcher = pattern.matcher(key);
					if (matcher.find()) {
						int count = Integer.parseInt(StringUtil.slim(matcher.group(0)));
						int leftBracketPosition = matcher.start();
						key = key.substring(0, leftBracketPosition);
						JsonArray jArray = new JsonArray();
						for (int i = 0; i < count + 1; i++) {
							jArray.add((i == count) ? ((JsonElement) object) : (new JsonObject()));
						}
						object = jArray;
					}
				}
				jObject.add(key, (JsonElement) object);
			} else if (object instanceof String) {
				jObject.add(key, (new JsonPrimitive((String) object)));
			}
			return jObject;
		}
	}

	/**
	 * 保存控件级传参
	 * 
	 * @param controlId
	 * @param key
	 * @param value
	 */
	public static void putWidgetParams(String controlId, String key, String value) {
		BaseWidget widget = ViewUtil.getWidget(controlId);
		if (widget == null) {
			LogUtil.e(TAG, "putWidgetParams error: get widget id null! controlId = " + controlId);
			return;
		}
		widget.putParam(key, value);
	}

	public static void putWidgetParams(String controlId, String keyValueString) {
		BaseWidget widget = ViewUtil.getWidget(controlId);
		if (widget == null) {
			LogUtil.e(TAG, "putWidgetParams error: get widget id null! controlId = " + controlId);
			return;
		}
		widget.putParam((KeyValueModel) GsonUtil.fromJson(keyValueString, KeyValueModel.class));
	}

	/**
	 * 获取控件传参
	 * 
	 * @param controlId
	 * @param key
	 * @return
	 */
	public static String getWidgetParam(String controlId, String key) {
		BaseWidget widget = ViewUtil.getWidget(controlId);
		if (widget == null) {
			LogUtil.e(TAG, "putWidgetParams error: get widget id null! controlId = " + controlId);
			return "";
		}
		return widget.getParam(key);
	}

	public static String getWidgetModelData(String controlId, String keyDesc) {
		BaseWidget widget = ViewUtil.getWidget(controlId);
		if (widget == null) {
			LogUtil.e(TAG, "putWidgetParams error: get widget id null! controlId = " + controlId);
			return "";
		}
		// Object dataModel = ReflectionUtil.invokeMethod(widget,
		// "getDataModel", null);
		// TODO:完成获取widget data
		return "";
	}
}
