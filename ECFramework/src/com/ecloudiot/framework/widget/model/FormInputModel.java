package com.ecloudiot.framework.widget.model;

public class FormInputModel {

	private Integer input_id;
	private String input_type = "";
	private String default_layout = "";
	private String name = "";
	private String text = "";
	private String default_value = "";
	private String background_wrods = "";
	private String des_wrods = "";
	private String checker_uri = "";
	private boolean simulateConfirm = false;
	private FormSelectOptionListModel selectlist;

	public Integer getInput_id() {
		return input_id;
	}

	public void setInput_id(Integer input_id) {
		this.input_id = input_id;
	}

	public FormSelectOptionListModel getSelectlist() {
		return selectlist;
	}

	public void setSelectlist(FormSelectOptionListModel selectlist) {
		this.selectlist = selectlist;
	}

	public String getChecker_uri() {
		return checker_uri;
	}

	public String getBackground_wrods() {
		return background_wrods;
	}

	public void setBackground_wrods(String background_wrods) {
		this.background_wrods = background_wrods;
	}

	public void setChecker_uri(String checker_uri) {
		this.checker_uri = checker_uri;
	}

	public String getInput_type() {
		return input_type;
	}

	public void setInput_type(String input_type) {
		this.input_type = input_type;
	}

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

	public String getDefault_value() {
		return default_value;
	}

	public void setDefault_value(String default_value) {
		this.default_value = default_value;
	}

	public String getDes_wrods() {
		return des_wrods;
	}

	public void setDes_wrods(String des_wrods) {
		this.des_wrods = des_wrods;
	}

	public String getDefault_layout() {
		return default_layout;
	}

	public void setDefault_layout(String default_layout) {
		this.default_layout = default_layout;
	}

	public boolean isSimulateConfirm() {
		return simulateConfirm;
	}

	public void setSimulateConfirm(boolean simulateConfirm) {
		this.simulateConfirm = simulateConfirm;
	}
}
