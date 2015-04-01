package com.ecloudiot.framework.utility;

import android.view.View;

public interface IViewExpression<T> {
	
	public View getView(T t,View context);

}
