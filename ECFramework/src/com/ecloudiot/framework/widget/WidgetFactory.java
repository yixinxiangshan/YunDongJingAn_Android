package com.ecloudiot.framework.widget;

import com.ecloudiot.framework.activity.ItemActivity;
//import com.ecloudiot.framework.utility.StringUtil;

public class WidgetFactory {
	private String widgetConfigString;
	public WidgetFactory instance() {
		return new WidgetFactory();
	}

	public WidgetFactory instance(ItemActivity activity, String widgetConfigString) {
		return new WidgetFactory();
	}

	// public BaseWidget create() {
	//
	// }

	// private void parsingWidgetConfig() {
	// if (StringUtil.isEmpty(widgetConfigString)) {
	// return;
	// }
	//
	// }

	public WidgetFactory setActivity(ItemActivity activity) {
		return this;
	}

	public WidgetFactory setLayoutName(String layoutName) {
		return this;
	}

	public WidgetFactory setWidgetConfigString(String widgetConfigString) {
		this.widgetConfigString = widgetConfigString;
		return this;
	}

	public WidgetFactory setControlId(String controlId) {
		return this;
	}

	public String getWidgetConfigString() {
		return widgetConfigString;
	}

}
