package com.ecloudiot.framework.widget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.BaseActivity;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.event.linterface.OnWidgetCreatedListener;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.ReflectionUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.ViewUtil;
import com.ecloudiot.framework.utility.WidgetUtil;
import com.ecloudiot.framework.widget.model.BaseWidgetConfigModel;
import com.ecloudiot.framework.widget.model.BaseWidgetConfigModel.BaseWidgetConfigSetEventModel;
import com.ecloudiot.framework.widget.model.KeyValueModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * 通过widget数据初始化widget，需要做以下几件事情 
 * 1、绑定对应layout 
 * 2、解析配置文件，开始实现自己的配置 
 * 3、保存控件级别的params 
 * 4、指定父控件，将自己放入父控件中 
 * 5、调用数据控件（外部工具），设置数据 
 * 6、设置控件属性（调用选择器），暂未提供 
 * 7、设置控件事件，调用外部工具
 */
@SuppressLint({"ViewConstructor", "ResourceAsColor"})
public class BaseWidget extends LinearLayout {
	private String TAG = "BaseWidget";
	protected ViewGroup baseView;
	protected BaseActivity ctx;
	protected Object pageContext;
	private Map<String, String> mMap = new HashMap<String, String>();
	private Map<String, Object> listenerMap;
	private BaseWidgetConfigModel configDataModel;
	protected Object widgetDataModel;
	protected JsonObject widgetDataJObject;
	private String configDataString;
	private String dataSourceString;
	private OnWidgetCreatedListener onWidgetCreatedListener;
	private String parentViewId;
	private View loadingView;
	protected int insertType;
	private String controlId;
	private LayoutParams layoutParams;

	// 控件属性

	protected enum LOADING_0N_OFF {
		TURN_ON, TURN_OFF
	};

	public BaseWidget(Object pageContext, String dataString, String layoutName) {
		super(ECApplication.getInstance().getNowActivity());
		// LogUtil.d(TAG, "BaseWidget : init");
		this.ctx = (BaseActivity) ECApplication.getInstance().getNowActivity();
		this.pageContext = pageContext;
		this.setGravity(Gravity.CENTER_HORIZONTAL);
		loading(LOADING_0N_OFF.TURN_ON);
		initViewLayout(layoutName);
		configDataString = dataString;
		this.setBackgroundColor(android.R.color.transparent);
	}

	public BaseWidget(Context context) {
		super(context);
	}

	/**
	 * bundle layout,will call initBaseView in sun class
	 * 
	 * @param layoutName
	 */
	protected void initViewLayout(String layoutName) {
		// LogUtil.d(TAG, "initViewLayout : start ... " + layoutName);
	}

	public ViewGroup getBaseView() {
		return baseView;
	}

	/**
	 * 绑定自己的layout,实现
	 * 
	 * @param layoutName
	 */
	protected void initBaseView(String layoutName) {
		// LogUtil.d(TAG, "initBaseView with layoutName: " + layoutName);
		if (StringUtil.isNotEmpty(layoutName)) {
			int layoutId = ResourceUtil.getLayoutIdFromContext(ctx, layoutName);
			if (layoutId > 0) {
				initBaseView(layoutId);
			}
		}
	}

	/**
	 * 绑定自己的layout,实现
	 * 
	 * @param layoutName
	 */
	@SuppressLint("NewApi")
	private void initBaseView(int layoutId) {
		// LogUtil.d(TAG, "initBaseView with layoutId" + layoutId);
		try {
			baseView = (ViewGroup) LayoutInflater.from(ctx).inflate(layoutId, this, false);
			this.setBackgroundColor(android.R.color.transparent);
		} catch (Exception e) {
			LogUtil.e(TAG, "initBaseView with layoutId error : " + e.toString());
			e.printStackTrace();
		}
	}
	protected void parsingData() {
		parsingData(configDataString);
	}

	/**
	 * 解析配置数据
	 * 
	 * @param dataString
	 */
	protected void parsingData(String dataString) {
		if (StringUtil.isEmpty(dataString)) {
			LogUtil.e(TAG, "parsingData error: dataString is null ...");
			return;
		}
		try {
			setConfigDataModel((BaseWidgetConfigModel) GsonUtil.fromJson(dataString, BaseWidgetConfigModel.class));
		} catch (Exception e) {
			LogUtil.e(TAG, "parsingData error: dataString is not valid ...");
			return;
		}
		if (null != getConfigDataModel()) {
			setControlId(getConfigDataModel().getControl_id());
			// 保存 配置
			for (int i = 0; i < getConfigDataModel().getConfigs().size(); i++) {
				KeyValueModel hashMapItem = getConfigDataModel().getConfigs().get(i);
				putParam(hashMapItem);
			}
			// 放入 父控件
			int insertPosition = 0;
			insertType = 0;
			for (int i = 0; i < getConfigDataModel().getPosition().size(); i++) {
				KeyValueModel positionMapItem = getConfigDataModel().getPosition().get(i);
				if (StringUtil.isNotEmpty(new String[]{positionMapItem.getKey(), positionMapItem.getValue()})) {
					if (positionMapItem.getKey().equalsIgnoreCase("parent")) {
						parentViewId = positionMapItem.getValue();
					} else if (positionMapItem.getKey().equalsIgnoreCase("location")) {
						// 位置 作用:
						insertPosition = Integer.parseInt(positionMapItem.getValue());
					} else if (positionMapItem.getKey().equalsIgnoreCase("insertType")) {
						// 类型 作用：
						insertType = Integer.parseInt(positionMapItem.getValue());
					}
				}
			}
			if (StringUtil.isNotEmpty(parentViewId)) {
				ViewUtil.insertViewToView(pageContext, parentViewId, this, insertPosition, insertType);
			} else {
				LogUtil.e(TAG, "parsingData error: parentViewId in null ...");
			}
			// 设置控件属性
			List<KeyValueModel> attrs = getConfigDataModel().getAttr();
			for (KeyValueModel keyValueModel : attrs) {
				setAttr(keyValueModel.getKey(), keyValueModel.getValue());
			}
			// 绑定数据适配器
			String adapterString = GsonUtil.toJson(getConfigDataModel().getDatasource());
			// LogUtil.d(TAG, "adapterString:" + adapterString);
			this.setDataSourceString(adapterString);
			// 获取数据, 替换为数据加载loading框，再解析完数据移除

			WidgetUtil.getData(pageContext, this, dataString);
		}
	}

	protected void loading(LOADING_0N_OFF loading) {
		switch (loading) {
			case TURN_ON :
				// LogUtil.i(TAG, "loading : loading");
				this.removeAllViews();
				layoutParams = (LayoutParams) this.getLayoutParams();
				LayoutParams tempLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				this.setLayoutParams(tempLayoutParams);
				this.setGravity(Gravity.CENTER);
				loadingView = (View) LayoutInflater.from(ctx).inflate(R.layout.widget_loading_default, null);
				this.setBackgroundColor(android.R.color.transparent);
				this.addView(loadingView);
				break;
			case TURN_OFF :
				// LogUtil.i(TAG, "loading : close  loading");
				if (getBaseView() != null && getBaseView().getParent() != null && getBaseView().getParent().equals(this)) {
					return;
				}
				this.removeAllViews();
				loadingView = null;
				if (getBaseView() != null) {
					if (layoutParams != null) {
						this.setLayoutParams(layoutParams);
					}
					this.addView(getBaseView());
				}
				break;
			default :
				break;
		}
	}

	public String getParentViewId() {
		return parentViewId;
	}

	public void setParentViewId(String parentViewId) {
		this.parentViewId = parentViewId;
	}

	public void putWidgetData(String widgetDataString) {
		// LogUtil.d(TAG, "putWidgetData : widgetDataString = " + widgetDataString);
		parsingWidgetData(widgetDataString);
		setData();
	}

	/**
	 * 解析 widget 数据
	 * 
	 * @param widgetDataJObject
	 */
	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		// LogUtil.d(TAG, "parsingWidgetData : start ...");
		loading(LOADING_0N_OFF.TURN_OFF);
	}

	/**
	 * 解析 widget 数据
	 * 
	 * @param widgetDataString
	 */
	protected void parsingWidgetData(String widgetDataString) {
		// 开始适配
		String widgetDataStringAdaptered = widgetDataString;
		if (StringUtil.isNotEmpty(getDataSourceString())) {
			widgetDataStringAdaptered = WidgetUtil.adaptWidgetData(pageContext, this, getDataSourceString(), widgetDataString);
			// LogUtil.d(TAG, "parsingWidgetData : widgetDataStringAdaptered = " + widgetDataStringAdaptered);
		}
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = null;
		if (StringUtil.isNotEmpty(widgetDataStringAdaptered)) {
			try {
				jsonObject = (JsonObject) jsonParser.parse(widgetDataStringAdaptered);
			} catch (Exception e) {
				LogUtil.e(TAG, "parsingWidgetData error: can not parse to json object ...");
			}
			parsingWidgetData(jsonObject);
		} else {
			LogUtil.e(TAG, "parsingWidgetData error: widgetDataStringAdaptered is null ..");
		}

	}

	/**
	 * use to put parsed data to widget here do something after parsing data,set event and attrs method inherit from this must call this at last
	 */
	protected void setData() {
		// LogUtil.d(TAG, "setData ...");
		// set event
		if (null != getConfigDataModel().getSetEvent() && getConfigDataModel().getSetEvent().size() > 0) {
			for (int i = 0; i < getConfigDataModel().getSetEvent().size(); i++) {
				setEvent(getConfigDataModel().getSetEvent().get(i));
			}
		}
		if (onWidgetCreatedListener != null) {
			// onWidgetCreatedListener
			// .onWidgetCreated(ctx.getResources().getResourceEntryName(this.getId()),
			// parentViewId);
			onWidgetCreatedListener.onWidgetCreated("", parentViewId);
		}

	}

	/**
	 * 添加 badgeView
	 */
	protected void setBadge(JsonObject badgeDataJObject) {
		// LogUtil.d(TAG, "setBadge : start ...");
	}

	/**
	 * 调用外部工具设置事件
	 * 
	 * @param eventModel
	 */
	protected void setEvent(BaseWidgetConfigSetEventModel eventModel) {
		// LogUtil.d(TAG, "setEvent : start ...");
		WidgetUtil.setEventWidget(pageContext, this, eventModel);
	}

	/**
	 * 设置控件属性
	 * 
	 * @param key
	 * @param value
	 */
	public void setAttr(String key, String value) {
		if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(value)) {
			String setAttrMothod = "set" + StringUtil.getFeatureString(key);
			if (StringUtil.isExpression(value)) {
				value = WidgetUtil.getValuePurpose(pageContext, this, value, null);
			}
			ReflectionUtil.invokeMethod(this, setAttrMothod, value);
		}
	}

	/**
	 * 设置控件属性
	 * 
	 * @param attrsString
	 */
	public void setAttrs(String attrsString) {
		if (StringUtil.isEmpty(attrsString)) {
			return;
		}
		JsonObject jObject = null;
		try {
			jObject = (JsonObject) (new JsonParser()).parse(attrsString);
		} catch (JsonSyntaxException e) {
			LogUtil.e(TAG, "setAttrS error: " + e.toString());
		}
		if (jObject != null && jObject.get("attr").isJsonArray()) {
			JsonArray jArray = jObject.get("attr").getAsJsonArray();
			for (JsonElement jElement : jArray) {
				if (jElement.isJsonObject()) {
					setAttr(jElement.getAsJsonObject().get("key").getAsString(), jElement.getAsJsonObject().get("value").getAsString());
				}
			}
		}
	}

	/**
	 * 刷新控件数据,支持重新指定数据源 子控件覆盖方法，需自己支持重新指定数据源
	 * 
	 * @param moreData
	 */
	public void refreshData(String widgetDataString) {
		// LogUtil.d(TAG, "refreshData : widgetDataString = " + widgetDataString);
		if (StringUtil.isEmpty(widgetDataString)) {
			refreshData();
		}
		if (StringUtil.isExpression(widgetDataString)) {
			widgetDataString = WidgetUtil.getValuePurpose(pageContext, this, widgetDataString, null);
		}
		parsingWidgetData(widgetDataString);
		setData();
	}

	/**
	 * 刷新控件数据，根据默认数据源
	 */
	public void refreshData() {
		// LogUtil.d(TAG, "refreshData : pageContext = " +
		// pageContext.toString());
		// LogUtil.d(TAG, "refreshData : this = " + this.toString());
		// LogUtil.d(TAG, "refreshData : configDataString = " +
		// configDataString.toString());
		WidgetUtil.refreshData(pageContext, this, configDataString);
	}

	public void refreshBadgeView(String widgetDataString) {
		// LogUtil.d(TAG, "refreshData : widgetDataString = " + widgetDataString);
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = (JsonObject) jsonParser.parse(widgetDataString);
		setBadge(jsonObject);
	}

	/**
	 * @param keyValueModel
	 */
	public void putParam(KeyValueModel keyValueModel) {
		if (StringUtil.isNotEmpty(keyValueModel.getKey()) && StringUtil.isNotEmpty(keyValueModel.getValue())) {
			putParam(keyValueModel.getKey(), keyValueModel.getValue());
		}
	}

	/**
	 * 为控件添加标签
	 * 
	 * @param key
	 * @param value
	 */
	public void putParam(String key, String value) {
		if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(value)) {
			// LogUtil.d(TAG, "put value :" + key + "," + value);
			mMap.put(key, value);
		} else if (StringUtil.isNotEmpty(key)) {
			mMap.remove(key);
		}
	}

	/**
	 * 获取 页面上下文
	 * 
	 * @return
	 */
	public Object getPageContext() {
		return pageContext;
	}

	/**
	 * 获取控件标签
	 * 
	 * @param key
	 * @return
	 */
	public String getParam(String key) {
		if (StringUtil.isNotEmpty(key)) {
			// LogUtil.d(TAG, "get value :" + key + "," + mMap.get(key));
			return mMap.get(key);
		}
		return "";
	}

	public Map<String, Object> getListenerMap() {
		return listenerMap;
	}

	public void setListenerMap(Map<String, Object> listenerMap) {
		this.listenerMap = listenerMap;
	}

	public BaseWidgetConfigModel getConfigDataModel() {
		return configDataModel;
	}

	public void setConfigDataModel(BaseWidgetConfigModel configDataModel) {
		this.configDataModel = configDataModel;
	}

	public String getDataSourceString() {
		return dataSourceString;
	}

	public void setDataSourceString(String dataSourceString) {
		this.dataSourceString = dataSourceString;
	}

	/**
	 * 设置widget 创建完毕监听器
	 * 
	 * @param widgetCreatedListener
	 */
	public void setOnWidgetCreatedListener(OnWidgetCreatedListener widgetCreatedListener) {
		this.onWidgetCreatedListener = widgetCreatedListener;
	}

	public String getControlId() {
		return controlId;
	}

	public void setControlId(String controlId) {
		this.controlId = controlId;
	}

	public JsonObject getWidgetDataJObject() {
		return widgetDataJObject;
	}

	public void setWidgetDataJObject(JsonObject widgetDataJObject) {
		this.widgetDataJObject = widgetDataJObject;
	}

}
