package com.ecloudiot.framework.widget;

import java.util.HashMap;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.utility.ColorUtil;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.ImageUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.PageUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.widget.model.TabItemModel;
import com.ecloudiot.framework.widget.model.TabModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jfeinstein.jazzyviewpager.JazzyViewPager;
import com.jfeinstein.jazzyviewpager.JazzyViewPager.TransitionEffect;
import com.viewpagerindicator.IconPagerAdapter;
import com.viewpagerindicator.TabPageIndicator;

@SuppressLint({"ViewConstructor", "UseSparseArrays"})
public class TabWidget extends BaseWidget {
	private final static String TAG = "TabWidget";
	private TabModel widgetDataModel;
	private String itemTextColor;
	private JazzyViewPager pager;
	private HashMap<Integer, ItemFragment> tabFragments = new HashMap<Integer, ItemFragment>();
	private int currentPosition;
	public TabWidget(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		this.setId(R.id.tab_widget);
		parsingData();
	}

	@Override
	protected void initViewLayout(String layoutName) {
		super.initViewLayout(layoutName);
		initBaseView("widget_tabpager_default");
	}

	@Override
	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		// do not call super method because of postponing closing-loading is
		// necessary
		try {
			widgetDataModel = GsonUtil.fromJson(widgetDataJObject, TabModel.class);
            currentPosition = Integer.valueOf(ECApplication.getInstance().getNowActivity().getIntent().getExtras().getString("paramsString")) - 1;
        } catch (Exception e) {
			LogUtil.e(TAG, "parsingWidgetData error: tab data is not valid ...");
		}
	}

	@Override
	protected void setData() {
		if (null == widgetDataModel) {
			LogUtil.e(TAG, "addTabBar error: dataModel is null ...");
			return;
		}

		FragmentPagerAdapter adapter = new TabPagerAdapter(this.ctx.getSupportFragmentManager());
		// 设置带动画的pager
		pager = (JazzyViewPager) baseView.findViewById(R.id.pager);
		pager.setTransitionEffect(TransitionEffect.Standard);
		pager.setFadeEnabled(!pager.getFadeEnabled());
		// pager.setPageTransformer(true, new ZoomOutPageTransformer());
		pager.setAdapter(adapter);
		// pager.setPageMargin(5);
		TabPageIndicator indicator = (TabPageIndicator) baseView.findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			// page 被选择后，actionbar相应变化
			public void onPageSelected(int position) {
				// LogUtil.e(TAG, "onPageSelected" + position);
				TabItemModel dataItem = widgetDataModel.getTabDataList().get(position);
				String pageString = PageUtil.getPageData(dataItem.getFragmentString());
				if (StringUtil.isNotEmpty(pageString)) {
					JsonParser jsonParser = new JsonParser();
					JsonObject pageJsonObject = (JsonObject) jsonParser.parse(pageString);
					// 执行通知tab里面的widget，当前处于被选中状态
                    try {
                        JsAPI.runEvent(tabFragments.get(position).getJsEvents(), "onPageSelected", "");
                    }catch (Exception e){}
					// fragment第一次启动时，不设置actionbar，只有切换到framgment的时候，切换action
					JsonArray controlsList = pageJsonObject.getAsJsonArray("controls");
					for (int i = 0; i < controlsList.size(); i++) {
						JsonObject ctrlJsonObject = (JsonObject) controlsList.get(i);
						if (ctrlJsonObject.get("xtype").getAsString().equals("ActionBarWidget")) {
							PageUtil.initWidget(ctrlJsonObject, pageContext);
						}
					}
					// fragment第一次启动时，不设置为当前fragment，只有切换到fragment时，设置为当前fragment
					ECApplication.getInstance().setNowPageContext(tabFragments.get(position));
				}
			}
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
        indicator.setCurrentItem(currentPosition);

		loading(LOADING_0N_OFF.TURN_OFF);
		super.setData();
	}
	class TabPagerAdapter extends FragmentPagerAdapter implements IconPagerAdapter {
		public TabPagerAdapter(FragmentManager fm) {
			super(fm);
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			LogUtil.d(TAG, "instantiateItem:" + position);
			Object obj = super.instantiateItem(container, position);
			pager.setObjectForPosition(obj, position);
			return obj;
		}
		@Override
		public boolean isViewFromObject(View view, Object object) {
			if (object != null) {
				return ((Fragment) object).getView() == view;
			} else {
				return false;
			}
		}
		/**
		 * 不删除item
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			// super.destroyItem(container, position, object);
		}

		@Override
		public Fragment getItem(int position) {
			// LogUtil.e(TAG, "getItem" + position);
			TabItemModel dataItem = widgetDataModel.getTabDataList().get(position);
			ItemFragment itemFragment = ItemFragment.newInstance(dataItem.getFragmentString());
			// actionbar设置通过onPageSelected设置，除了被选择为默认的
			if (position != currentPosition)
				itemFragment.setNoActionBar(true);
			tabFragments.put(position, itemFragment);
			return itemFragment;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			TabItemModel dataItem = widgetDataModel.getTabDataList().get(position);
			return dataItem.getTitle();
		}

		@Override
		public int getIconResId(int index) {
			return 0;
		}
		@Override
		public int getCount() {
			return widgetDataModel.getTabDataList().size();
		}

		@Override
		public Drawable getIconDrawable(int index) {
			TabItemModel dataItem = widgetDataModel.getTabDataList().get(index);
//			if (StringUtil.isNotEmpty(getItemTextColor())) {
				return ImageUtil.getDrawableFromConfig(dataItem.getIcon());
//			}
//			return null;
		}

		@Override
		public ColorStateList getTitleColorList(int index) {
//			if (StringUtil.isNotEmpty(getItemTextColor())) {
				return ColorUtil.getColorState("White", "Yellow", "Yellow");
//				return ColorUtil.getColorStateFromConfig(getItemTextColor());
//			}
//			return null;
		}

	}
	public String getItemTextColor() {
		return itemTextColor;
	}

	public void setItemTextColor(String itemTextColor) {
		this.itemTextColor = itemTextColor;
	}
}
