package com.ecloudiot.framework.utility;

public class ValueMatch {
	public String Type;
	public String Value;
	public String[] Group;
	
	public ValueMatch(String type, String value) {
		this.Type = type;
		this.Value = value;
	}

	public ValueMatch(String type, String value, String[] group) {
		this.Type = type;
		this.Value = value;
		this.Group = group;
	}

	public String getType() {
		return Type;
	}

	public void setType(String type) {
		this.Type = type;
	}

	public String getValue() {
		return Value;
	}

	public void setValue(String value) {
		this.Value = value;
	}

	public String[] getGroup() {
		return Group;
	}

	public void setGroup(String[] group) {
		this.Group = group;
	}

}