package com.ecloudiot.framework.widget.model;

import java.util.List;

public class FormModel {
	private String method;
	private String title = "";
	private String post_uri = "";
	private String check_enable_uri = "";
	private String button_next = "";
	private String button_pre = "";
	private String button_submit = "";
	private String button_cancel = "";
	private List<FormHelperModel> helper_list;
	private List<FormInputModel> input_list;

	public String getButton_next() {
		return button_next;
	}

	public void setButton_next(String button_next) {
		this.button_next = button_next;
	}

	public String getButton_pre() {
		return button_pre;
	}

	public void setButton_pre(String button_pre) {
		this.button_pre = button_pre;
	}

	public String getButton_submit() {
		return button_submit;
	}

	public void setButton_submit(String button_submit) {
		this.button_submit = button_submit;
	}

	public String getButton_cancel() {
		return button_cancel;
	}

	public void setButton_cancel(String button_cancel) {
		this.button_cancel = button_cancel;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<FormInputModel> getInput_list() {
		return input_list;
	}

	public void setInput_list(List<FormInputModel> input_list) {
		this.input_list = input_list;
	}


	public String getPost_uri() {
		return post_uri;
	}

	public void setPost_uri(String post_uri) {
		this.post_uri = post_uri;
	}

	public String getCheck_enable_uri() {
		return check_enable_uri;
	}

	public void setCheck_enable_uri(String check_enable_uri) {
		this.check_enable_uri = check_enable_uri;
	}

	public List<FormHelperModel> getHelper_list() {
		return helper_list;
	}

	public void setHelper_list(List<FormHelperModel> helper_list) {
		this.helper_list = helper_list;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
}
