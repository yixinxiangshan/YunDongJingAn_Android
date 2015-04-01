package com.ecloudiot.framework.widget.model;

import java.util.List;

public class BaseWidgetConfigModel {
	private String control_id;
	private String xtype;
	private String layout;
	private List<KeyValueModel> position;
	private List<KeyValueModel> attr;
	private List<KeyValueModel> configs;
	private List<BaseWidgetConfigSetEventModel> setEvent;
	private ConfigDatasourceModel datasource;

	
	public String getLayout() {
		return layout;
	}
	public void setLayout(String layout) {
		this.layout = layout;
	}
	public List<BaseWidgetConfigSetEventModel> getSetEvent() {
		return setEvent;
	}
	public void setSetEvent(List<BaseWidgetConfigSetEventModel> setEvent) {
		this.setEvent = setEvent;
	}
	public String getControl_id() {
		return control_id;
	}
	public void setControl_id(String control_id) {
		this.control_id = control_id;
	}
	public String getXtype() {
		return xtype;
	}
	public void setXtype(String xtype) {
		this.xtype = xtype;
	}
	public List<KeyValueModel> getPosition() {
		return position;
	}
	public void setPosition(List<KeyValueModel> position) {
		this.position = position;
	}
	
	public ConfigDatasourceModel getDatasource() {
		return datasource;
	}
	public void setDatasource(ConfigDatasourceModel datasource) {
		this.datasource = datasource;
	}
	
	public List<KeyValueModel> getConfigs() {
		return configs;
	}
	public void setConfigs(List<KeyValueModel> configs) {
		this.configs = configs;
	}

	public List<KeyValueModel> getAttr() {
		return attr;
	}
	public void setAttr(List<KeyValueModel> attr) {
		this.attr = attr;
	}

	public class BaseWidgetConfigSetEventModel {
		private String name;
		private String id ;
		private List<KeyValueModel> params;
		private String javascript;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getJavascript() {
			return javascript;
		}
		public void setJavascript(String javascript) {
			this.javascript = javascript;
		}
		public List<KeyValueModel> getParams() {
			return params;
		}
		public void setParams(List<KeyValueModel> params) {
			this.params = params;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
	}
	public class ConfigDatasourceModel {
		private String name;
		private String method;
		private List<KeyValueModel> params;
		private List<KeyValueModel> adapter;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getMethod() {
			return method;
		}
		public void setMethod(String method) {
			this.method = method;
		}
		public List<KeyValueModel> getParams() {
			return params;
		}
		public void setParams(List<KeyValueModel> params) {
			this.params = params;
		}
		public List<KeyValueModel> getAdapter() {
			return adapter;
		}
		public void setAdapter(List<KeyValueModel> adapter) {
			this.adapter = adapter;
		}
	}
}
