package com.ecloudiot.framework.utility;

import android.view.View;

public interface DelegateFilterExpression {

	public  boolean is(View view,String name,String operator,String check,Object[] values);
	
}
