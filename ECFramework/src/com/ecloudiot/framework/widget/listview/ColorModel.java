package com.ecloudiot.framework.widget.listview;

class ColorModel {
	private boolean _clickable = false;
	private String pressed = "";
	private String selected = "";
	private String normal = "";
	private int alpha = -1;
	public boolean is_clickable() {
		return _clickable;
	}
	public void set_clickable(boolean _clickable) {
		this._clickable = _clickable;
	}
	public String getPressed() {
		return pressed;
	}
	public void setPressed(String pressed) {
		this.pressed = pressed;
	}
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
	public String getNormal() {
		return normal;
	}
	public void setNormal(String normal) {
		this.normal = normal;
	}
	public int getAlpha() {
		return alpha;
	}
	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}
}