package com.ecloudiot.framework.widget.model;

import java.util.List;

public class FormSelectOptionModel {
	private String name;
	private String text;
	private String value;
	private List<FormSelectOptionModel> options;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String toString() {
		return text;
	}

	public List<FormSelectOptionModel> getOptions() {
		return options;
	}

	public void setOptions(List<FormSelectOptionModel> options) {
		this.options = options;
	}
}
