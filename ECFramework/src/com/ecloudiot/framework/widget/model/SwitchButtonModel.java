package com.ecloudiot.framework.widget.model;

/*
 * state: ON OFF
 */
public class SwitchButtonModel {

	private String switchOnBackgroud;
	private String switchOffBackgroud;
	private int state = 0;
	public String getSwitchOnBackgroud() {
		return switchOnBackgroud;
	}
	public void setSwitchOnBackgroud(String switchOnBackgroud) {
		this.switchOnBackgroud = switchOnBackgroud;
	}
	public String getSwitchOffBackgroud() {
		return switchOffBackgroud;
	}
	public void setSwitchOffBackgroud(String switchOffBackgroud) {
		this.switchOffBackgroud = switchOffBackgroud;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}

	
}
