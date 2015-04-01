package com.ecloudiot.framework.widget.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.widget.model.ChannelTabListModel;

public class ChannelPagersAdapter extends FragmentPagerAdapter {
	private String TAG = "ChannelPagersAdapter";
	private ChannelTabListModel tabDataList;
	private FragmentManager fm;
	private FragmentTransaction mCurTransaction;
	private Fragment mCurrentPrimaryItem;

	public ChannelPagersAdapter(FragmentManager fManager, ChannelTabListModel tabDataList) {
		super(fManager);
		this.tabDataList = tabDataList;
		this.fm = fManager;

	}

	@Override
	public int getCount() {
		return tabDataList.getPagerCount();
	}

	@Override
	public Fragment getItem(int position) {
		return ItemFragment.newInstance(tabDataList.getChannelList().get(position).getpageName());
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		if (mCurTransaction == null) {
			mCurTransaction = fm.beginTransaction();
		}

		String tag = tabDataList.getChannelList().get(position).getTag();
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
	public void destroyItem(ViewGroup container, int position, Object object) {
		if (mCurTransaction == null) {
			mCurTransaction = fm.beginTransaction();
		}
		LogUtil.v(TAG, "Detaching item #" + getItemId(position) + ": f=" + object + " v=" + ((Fragment) object).getView());
		mCurTransaction.detach((Fragment) object);
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		Fragment fragment = (Fragment) object;
		if (fragment != mCurrentPrimaryItem) {
			if (mCurrentPrimaryItem != null) {
				mCurrentPrimaryItem.setMenuVisibility(false);
				mCurrentPrimaryItem.setUserVisibleHint(false);
				LogUtil.i(TAG, "setPrimaryItem mCurrentPrimaryItem = " + mCurrentPrimaryItem);
			} else {
				LogUtil.e(TAG, "setPrimaryItem error: mCurrentPrimaryItem is null ...");
			}
			if (fragment != null) {
				fragment.setMenuVisibility(true);
				fragment.setUserVisibleHint(true);
				LogUtil.i(TAG, "setPrimaryItem fragment = " + fragment);
			} else {
				LogUtil.e(TAG, "setPrimaryItem error: fragment is null ...");
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

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return ((Fragment) object).getView() == view;
	}
}