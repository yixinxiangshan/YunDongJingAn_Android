package com.ecloudiot.framework.event.linterface;

import com.handmark.pulltorefresh.library.PullToRefreshBase;

import android.view.View;

public interface OnRefreshListener<V extends View> {
	public void onRefresh(PullToRefreshBase<V> refreshView);
}
