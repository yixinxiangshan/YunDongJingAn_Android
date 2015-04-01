package com.ecloudiot.framework.widget.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.widget.model.PagerItemModel;
import com.ecloudiot.framework.widget.model.PagerModel;

public class PagerAdapter extends FragmentPagerAdapter {
	private String TAG = "PagerAdapter";

	private FragmentManager fm;
	private FragmentTransaction mCurTransaction;
	private Fragment mCurrentPrimaryItem;
	// private JsonObject widgetDataJObject;
	private int totalNum = 200;
	ArrayList<PagerItemModel> pagerList;
	public PagerAdapter(FragmentManager fManager, PagerModel pagerModel) {
		super(fManager);
		LogUtil.d(TAG, "PagerAdapter");
		this.fm = fManager;
		pagerList = new ArrayList<PagerItemModel>();
		for (int i = 0; i < getTotalNum(); i++) {
			PagerItemModel pagerItemModel = new PagerItemModel();
			pagerItemModel.setPageName("page_home_pager?offset=" + (i - getTotalNum() / 2));
			pagerItemModel.setTag("" + (i));
			pagerList.add(pagerItemModel);
		}
		// this.pagerModel.setPagerList(pagerList);
	}
	@Override
	public int getCount() {
		return getTotalNum();
	}

	@Override
	public Fragment getItem(int position) {
		return ItemFragment.newInstance(pagerList.get(position).getpageName());
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (mCurTransaction == null) {
			mCurTransaction = fm.beginTransaction();
		}

		String tag = pagerList.get(position).getTag();
		Fragment fragment = fm.findFragmentByTag(tag);
		if (fragment != null) {
			// LogUtil.d(TAG, "Attaching tag #" + tag + ": f=" + fragment);
			mCurTransaction.attach(fragment);
		} else {
			fragment = getItem(position);
			// LogUtil.d(TAG, "Adding tag #" + tag + ": f=" + fragment);
			mCurTransaction.add(container.getId(), fragment, tag);
		}
		if (fragment != mCurrentPrimaryItem) {
			fragment.setMenuVisibility(false);
			fragment.setUserVisibleHint(false);
		}

		return fragment;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (mCurTransaction == null) {
			mCurTransaction = fm.beginTransaction();
		}
		// LogUtil.v(TAG, "Detaching item #" + getItemId(position) + ": f=" + object + " v=" + ((Fragment) object).getView());
		mCurTransaction.detach((Fragment) object);
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		Fragment fragment = (Fragment) object;
		if (fragment != mCurrentPrimaryItem) {
			if (mCurrentPrimaryItem != null) {
				mCurrentPrimaryItem.setMenuVisibility(false);
				mCurrentPrimaryItem.setUserVisibleHint(false);
				// LogUtil.i(TAG, "setPrimaryItem mCurrentPrimaryItem = " + mCurrentPrimaryItem);
			} else {
				// LogUtil.e(TAG, "setPrimaryItem error: mCurrentPrimaryItem is null ...");
			}
			if (fragment != null) {
				fragment.setMenuVisibility(true);
				fragment.setUserVisibleHint(true);
				// LogUtil.i(TAG, "setPrimaryItem fragment = " + fragment);
			} else {
				// LogUtil.e(TAG, "setPrimaryItem error: fragment is null ...");
			}
			mCurrentPrimaryItem = fragment;
		}
	}
	// @Override
	// public int getItemPosition(Object object) {
	// return POSITION_NONE;
	// }
	@Override
	public void finishUpdate(ViewGroup container) {
		if (mCurTransaction != null) {
			mCurTransaction.commitAllowingStateLoss();
			mCurTransaction = null;
			fm.executePendingTransactions();
		}
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return ((Fragment) object).getView() == view;
	}
	public int getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

}