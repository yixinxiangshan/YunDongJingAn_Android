package com.ecloudiot.framework.widget.listview;

import com.ecloudiot.framework.widget.BaseWidget;
import com.google.gson.JsonObject;

public class ListViewCellMSGNews extends BaseWidget {
	// private static String TAG = "ListViewCellMSGNews";

	public ListViewCellMSGNews(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		// this.setId(R.id.list_view_widget);
		parsingData();
	}

	@Override
	protected void initViewLayout(String layoutName) {
		super.initViewLayout(layoutName);

		// initBaseView(listLayoutName);
	}

	/**
	 * 将解析后的数据赋值给控件数据model实例
	 */
	@Override
	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		super.parsingWidgetData(widgetDataJObject);
		// widgetDataModel = parsingWidgetModel(widgetDataJObject);
	}

	@Override
	protected void setData() {

	}

}
