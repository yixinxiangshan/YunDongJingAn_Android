package com.ecloudiot.framework.widget.model;

import com.ecloudiot.framework.widget.BaseWidget;

public class KeyWidgetModel {

	private String key = "";
	private BaseWidget widget = null;
	private String type = "";
	private String defaultValue = "";
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public BaseWidget getWidget() {
		return widget;
	}
	public void setWidget(BaseWidget widget) {
		this.widget = widget;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
}
