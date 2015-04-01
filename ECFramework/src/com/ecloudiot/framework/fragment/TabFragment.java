package com.ecloudiot.framework.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class TabFragment extends BaseFragment {
	private FragmentTabHost mTabHost;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mTabHost = new FragmentTabHost(getActivity());
//		mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.fragment1);
//
//		mTabHost.addTab(mTabHost.newTabSpec("simple").setIndicator("Simple"),
//				ItemFragment.class, null);
//		mTabHost.addTab(mTabHost.newTabSpec("contacts").setIndicator("Contacts"),
//				ItemFragment2.class, null);
//		mTabHost.addTab(mTabHost.newTabSpec("custom").setIndicator("Custom"),
//				ItemFragment.class, null);
//		mTabHost.addTab(mTabHost.newTabSpec("throttle").setIndicator("Throttle"),
//				ItemFragment2.class, null);

		return mTabHost;
	}

}
