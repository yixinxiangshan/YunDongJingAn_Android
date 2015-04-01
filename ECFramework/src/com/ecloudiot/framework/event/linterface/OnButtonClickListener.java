package com.ecloudiot.framework.event.linterface;

import android.view.View;
import android.view.View.OnClickListener;

public interface OnButtonClickListener extends OnClickListener{
	public void onClick(View view, int position);
}
