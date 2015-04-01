package com.ecloudiot.framework.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MultiViewpagerFragment extends BaseFragment {
	public static MultiViewpagerFragment newInstance(String pageName) {
		MultiViewpagerFragment f = new MultiViewpagerFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString("dataString", pageName);
        f.setArguments(args);
        return f;
    }
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
}
