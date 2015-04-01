package com.ecloudiot.framework.widget;

import android.annotation.SuppressLint;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.widget.model.ActionBarModel;
import com.google.gson.JsonObject;

@SuppressLint("ViewConstructor")
public class ActionBarWidget extends BaseWidget {
	
	private final static String TAG = "ActionBarWidget";
	private ActionBarModel dataModel;

	public ActionBarWidget(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		this.setId(R.id.action_bar_widget);
		parsingData();
	}
	@Override
	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		dataModel = GsonUtil.fromJson(widgetDataJObject, ActionBarModel.class);
	}
	
	@Override
	protected void setData() {
		if (null != dataModel) {
			ctx.initActionBar(dataModel,this);
		}else {
			LogUtil.e(TAG, "setData error: dataModel is null ...");
		}
		super.setData();
	}
	
	public ActionBarModel getDataModel() {
		return dataModel;
	}
}
