package com.ecloudiot.framework.widget;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.ViewGroup;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.event.linterface.OnPageSelectedListener;
import com.ecloudiot.framework.fragment.MultiViewpagerFragment;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.view.ViewMultiViewpagerContainer;
import com.ecloudiot.framework.widget.model.MultiViewpagerModel;
import com.google.gson.JsonObject;

@SuppressLint("ViewConstructor")
public class MultiViewpagerWidget extends BaseWidget {
	private String TAG = "MultiViewpagerWidget";
	private MultiViewpagerModel data;
	private ViewMultiViewpagerContainer viewMultiViewpagerContainer;
	private ViewPager mPager;
	private OnPageSelectedListener onPageSelectedListener;

	public MultiViewpagerWidget(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		this.setId(R.id.multi_viewpager_widget);
		parsingData();
	}

	protected void initViewLayout(String layoutName) {
		super.initViewLayout(layoutName);
		if (StringUtil.isNotEmpty(layoutName)) {
			initBaseView(layoutName);
		} else {
			initBaseView("widget_multi_viewpager");
		}
	}

	/**
	 * 解析数据
	 * 
	 * @param widgetDataJObject
	 */
	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		super.parsingWidgetData(widgetDataJObject);
		try {
			this.data = GsonUtil.fromJson(widgetDataJObject, MultiViewpagerModel.class);
		} catch (Exception e) {
			LogUtil.e(TAG, "parsingWidgetData error: can not parse to json object ...");
		}
	}

	protected void setData() {
		showContent();
		super.setData();
	}

	public OnPageSelectedListener getOnPageSelectedListener() {
		return onPageSelectedListener;
	}

	public void setOnPageSelectedListener(OnPageSelectedListener onPageSelectedListener, Object x) {
		this.onPageSelectedListener = onPageSelectedListener;
	}

	private void showContent() {
		viewMultiViewpagerContainer = (ViewMultiViewpagerContainer) this.getBaseView().findViewById(R.id.pager_container);
		setmPager((ViewPager) this.getBaseView().findViewById(R.id.viewpager));
		FragmentPagerAdapter adapter = new MyPagerAdapter(this.ctx.getSupportFragmentManager());
		getmPager().setAdapter(adapter);
		// Necessary or the pager will only have one extra page to show
		// make this at least however many pages you can see
		getmPager().setOffscreenPageLimit(adapter.getCount());
		// A little space between pages
		getmPager().setPageMargin(15);

		// If hardware acceleration is enabled, you should also remove
		// clipping on the pager for its children.
		DisplayMetrics metrics = new DisplayMetrics();
		this.ctx.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int pagerWidth = (int) (metrics.widthPixels * 0.8f);
		// int pagerHeight = (int)(metrics.heightPixels*0.8f);
		int pagerHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
		ViewGroup.LayoutParams layoutParams = getmPager().getLayoutParams();
		layoutParams.height = pagerHeight;
		layoutParams.width = pagerWidth;
		getmPager().setLayoutParams(layoutParams);
		if (data.getDefaultPager() >= 0) {
			getmPager().setCurrentItem(data.getDefaultPager());
		}
	}

	private class MyPagerAdapter extends FragmentPagerAdapter {
		// private boolean isLoadOver = false;
		// private int pagerNum = -1;
		// private String loadFinishTag;
		private FragmentManager fm;
		private FragmentTransaction mCurTransaction;
		private Fragment mCurrentPrimaryItem;

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
			this.fm = fm;
		}

		@Override
		public Fragment getItem(int position) {
			LogUtil.d(TAG, "pageName : " + data.getItemList().get(position).getPageName());
			return MultiViewpagerFragment.newInstance(data.getItemList().get(position).getPageName());
		}

		@Override
		public int getCount() {
			return data.getPagerCount();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			if (mCurTransaction == null) {
				mCurTransaction = fm.beginTransaction();
			}

			String tag = data.getItemList().get(position).getTag();
			Fragment fragment = fm.findFragmentByTag(tag);
			if (fragment != null) {
				LogUtil.v(TAG, "Attaching tag #" + tag + ": f=" + fragment);
				mCurTransaction.attach(fragment);
			} else {
				fragment = getItem(position);
				LogUtil.v(TAG, "Adding tag #" + tag + ": f=" + fragment);
				mCurTransaction.add(container.getId(), fragment, tag);
			}
			if (fragment != mCurrentPrimaryItem) {
				fragment.setMenuVisibility(false);
				fragment.setUserVisibleHint(false);
			}

			return fragment;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
			Fragment fragment = (Fragment) object;
			if (fragment != mCurrentPrimaryItem) {
				if (mCurrentPrimaryItem != null) {
					mCurrentPrimaryItem.setMenuVisibility(false);
					mCurrentPrimaryItem.setUserVisibleHint(false);
				}
				if (fragment != null) {
					fragment.setMenuVisibility(true);
					fragment.setUserVisibleHint(true);
				}
				mCurrentPrimaryItem = fragment;
			}
		}

		@Override
		public void finishUpdate(ViewGroup container) {
			if (mCurTransaction != null) {
				mCurTransaction.commitAllowingStateLoss();
				mCurTransaction = null;
				fm.executePendingTransactions();
			}
		}
	}

	public MultiViewpagerModel getDataModel() {
		return data;
	}

	public void setOnPageSelectedListener(OnPageSelectedListener onPageSelectedListener) {
		viewMultiViewpagerContainer.setMultiViewpagerWidget(this);
		this.setOnPageSelectedListener(onPageSelectedListener, null);
	}

	public ViewPager getmPager() {
		return mPager;
	}

	public void setmPager(ViewPager mPager) {
		this.mPager = mPager;
	}
}
