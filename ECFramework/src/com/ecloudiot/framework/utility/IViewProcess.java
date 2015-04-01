package com.ecloudiot.framework.utility;

import java.util.ArrayList;
import android.view.View;

public interface IViewProcess {

	public Boolean cancel(View view);
	
    public ArrayList<View> items();
	
}
