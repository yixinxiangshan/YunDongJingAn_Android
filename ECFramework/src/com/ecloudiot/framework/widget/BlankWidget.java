package com.ecloudiot.framework.widget;

import com.ecloudiot.framework.R;
import android.annotation.SuppressLint;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.google.gson.JsonObject;

@SuppressLint("ViewConstructor")
public class BlankWidget extends BaseWidget {
	private final static String TAG = "BlankWidget";

	// private Button button = null;

	public BlankWidget(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		LogUtil.d(TAG, "init BlankWIdget");
		this.setId(R.id.ec_blank_widget);
		parsingData();
		loading(LOADING_0N_OFF.TURN_OFF);
	}

	@Override
	protected void initViewLayout(String layoutName) {
		super.initViewLayout(layoutName);
		if (StringUtil.isNotEmpty(layoutName)) {
			initBaseView(layoutName);
		} else {
			initBaseView("widget_blank_default");
		}
	}

	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		super.parsingWidgetData(widgetDataJObject);
	}

	protected void setData() {
		setContent();
		super.setData();
	}

	private void setContent() {
	}

}
