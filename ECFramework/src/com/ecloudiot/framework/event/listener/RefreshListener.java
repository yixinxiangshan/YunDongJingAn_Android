package com.ecloudiot.framework.event.listener;

import android.annotation.SuppressLint;
import android.text.format.DateUtils;
import android.view.View;

import com.ecloudiot.framework.event.linterface.OnRefreshListener;
import com.ecloudiot.framework.utility.IntentUtil;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

@SuppressLint("HandlerLeak")
public class RefreshListener<V extends View> extends BaseEventListener implements OnRefreshListener<V> {
//	private final static String TAG = "RefreshListener";

	public RefreshListener(Object pageContext, Object widget, String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public void onRefresh(PullToRefreshBase<V> refreshView) {
		String label = DateUtils.formatDateTime(IntentUtil.getActivity(), System.currentTimeMillis(),
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

		// Update the LastUpdatedLabel
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		runJs();
	}
}
