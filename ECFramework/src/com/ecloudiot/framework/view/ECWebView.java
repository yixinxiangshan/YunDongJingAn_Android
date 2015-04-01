package com.ecloudiot.framework.view;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

public class ECWebView extends WebView {
	
	public ECWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		
	}

	
}
