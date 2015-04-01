package com.ecloudiot.framework.utility;

import java.util.ArrayList;

import android.view.View;

public abstract class TDelegateViewProcess<TArgs,TValue>
{
	
	public abstract Boolean cancel(View view,Integer index,Integer depth,Integer size,TArgs  args);
	
	public ArrayList<TValue> values(){
		return valuelist;
	}
	
	
	public void AddValue(TValue value)
	{
		if(valuelist==null)
			valuelist = new ArrayList<TValue>();
		valuelist.add(value);
	}

	private ArrayList<TValue> valuelist = null;
	
	
	public ArrayList<View> items() {
	
		return list==null? new ArrayList<View>(): list ;
	}

    private ArrayList<View> list = null;

    protected void add(View view)
    {
    	if(list==null)
    		list = new ArrayList<View>();
    	 list.add(view);
    }
	

}
