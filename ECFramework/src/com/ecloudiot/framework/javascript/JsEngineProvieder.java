package com.ecloudiot.framework.javascript;

public interface JsEngineProvieder {
	public Object getParent();
	public void setParent(Object parent);
	public void jsEngineStart(String scriptString);
	public void release();
	public void setViewHasGone(boolean viewHasGone);
	public String callback(String callback_id, Object args);
}
