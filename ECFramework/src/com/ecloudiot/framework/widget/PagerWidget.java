package com.ecloudiot.framework.widget;

import java.util.HashMap;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.widget.adapter.PagerAdapter;
import com.ecloudiot.framework.widget.model.PagerModel;
import com.google.gson.JsonObject;

@SuppressLint("ViewConstructor")
public class PagerWidget extends BaseWidget {
	// private static String TAG = "PagerWidget";
	private PagerModel widgetDataModel;
	private ViewPager mPager;
	private int totalNum = 200;
	// private int offset = 0;
	public PagerWidget(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		this.setId(R.id.pager_widget);
		parsingData();
	}

	public void initViewLayout(String layoutName) {
		if (StringUtil.isNotEmpty(layoutName)) {
			initBaseView(layoutName);
		} else {
			initBaseView("widget_pager");
		}
	}

	/**
	 * 解析数据
	 * 
	 * @param dataString
	 */
	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		super.parsingWidgetData(widgetDataJObject);
		try {
			widgetDataModel = GsonUtil.fromJson(widgetDataJObject, PagerModel.class);
		} catch (Exception e) {
			// LogUtil.e(TAG, "parsingData error: dataString is invalid ...");
		}
	}

	protected void setData() {
		// set pages
		final ViewPager pager = initViewPager();

		pager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// LogUtil.d(TAG, "onPageSelected");
				HashMap<String, String> eventParams = new HashMap<String, String>();
				eventParams.put("position", "" + (arg0 - getTotalNum() / 2));
				eventParams.put("controlId", getControlId());
				// 执行js里面的事件
				JsAPI.runEvent(((ItemActivity) pageContext).getJsEvents(), "onPageSelected", new JSONObject(eventParams));

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		super.setData();
	}

	private ViewPager initViewPager() {

		mPager = (ViewPager) getBaseView().findViewById(R.id.pagers);
		// LogUtil.d(TAG, "initViewPager ...");
		FragmentManager fManager = null;
		if (pageContext instanceof ItemFragment) {
			fManager = ((ItemFragment) pageContext).getChildFragmentManager();
		} else {
			fManager = ctx.getSupportFragmentManager();
		}
		FragmentPagerAdapter fPagerAdapter = new PagerAdapter(fManager, widgetDataModel);
		((PagerAdapter) fPagerAdapter).setTotalNum(getTotalNum());
		mPager.setAdapter(fPagerAdapter);
		mPager.setOffscreenPageLimit(1);
		mPager.setCurrentItem(getTotalNum() / 2);
		return mPager;
		// mPager.setCurrentItem(0);
	}

	public PagerModel getDataModel() {
		return widgetDataModel;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	@Override
	public void refreshData(String data) {
		// LogUtil.d(TAG, "refreshData：" + data);

		parsingWidgetData(data);

		// LogUtil.d(TAG, "refreshData：1");
		// offset = getWidgetDataJObject().get("offset").getAsInt();
		// LogUtil.d(TAG, "refreshData：" + offset);
		widgetDataModel.getOffset();
		mPager.setCurrentItem(getTotalNum() / 2 + widgetDataModel.getOffset());
	}
}
