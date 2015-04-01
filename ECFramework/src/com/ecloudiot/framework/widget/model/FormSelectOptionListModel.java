package com.ecloudiot.framework.widget.model;

import java.util.List;

public class FormSelectOptionListModel {
	private String _uri;
	private String input_name;
	private String default_value;
	private String default_widget;
	private List<FormSelectOptionModel> options;

	public String get_uri() {
		return _uri;
	}

	public void set_uri(String _uri) {
		this._uri = _uri;
	}

	public List<FormSelectOptionModel> getOptions() {
		return options;
	}

	public void setOptions(List<FormSelectOptionModel> options) {
		this.options = options;
	}

	public String getInput_name() {
		return input_name;
	}

	public void setInput_name(String input_name) {
		this.input_name = input_name;
	}

	public String getDefault_value() {
		return default_value;
	}

	public void setDefault_value(String default_value) {
		this.default_value = default_value;
	}

	public String getDefault_widget() {
		return default_widget;
	}

	public void setDefault_widget(String default_widget) {
		this.default_widget = default_widget;
	}
}
