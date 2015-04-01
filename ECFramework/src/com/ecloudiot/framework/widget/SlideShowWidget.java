package com.ecloudiot.framework.widget;

import android.annotation.SuppressLint;
import android.support.v4.view.ViewPager;
import android.widget.LinearLayout;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.widget.adapter.SlideShowAdapter;
import com.ecloudiot.framework.widget.model.SlideShowModel;
import com.google.gson.JsonObject;

@SuppressLint("ViewConstructor")
public class SlideShowWidget extends BaseWidget {

	private String TAG = "SlideShowWidget";
	private SlideShowModel widgetDataModel;
	private SlideShowAdapter adapter;
	private ViewPager viewPager;

	public SlideShowWidget(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		this.setId(R.id.slide_show_widget);
		parsingData();
	}

	protected void initViewLayout(String layoutName) {
		if (StringUtil.isNotEmpty(layoutName)) {
			initBaseView(layoutName);
		} else {
			initBaseView("widget_slideshow");
		}
	}

	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		super.parsingWidgetData(widgetDataJObject);
		try {
			widgetDataModel = GsonUtil.fromJson(widgetDataJObject, SlideShowModel.class);
		} catch (Exception e) {
			LogUtil.e(TAG, "parsingData error: dataString is invalid ...");
		}
	}

	public ViewPager getViewPager() {
		return this.viewPager;
	}

	// 设置数据
	protected void setData() {
		this.viewPager = (ViewPager) getBaseView().findViewById(R.id.widget_slideshow_pagers);
		this.adapter = new SlideShowAdapter(this.ctx, this.widgetDataModel.getItemList());
		this.viewPager.setAdapter(this.adapter);
		this.viewPager.setCurrentItem(this.widgetDataModel.getCurrentPosition());
	}

	// 获取 position 中容器 layout
	public LinearLayout getPagerLayout(int position) {
		return (LinearLayout) getBaseView().findViewById(R.string.slideshow_item_layout_id + position);
	}
	
}
