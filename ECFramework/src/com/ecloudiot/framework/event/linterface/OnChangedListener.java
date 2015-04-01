package com.ecloudiot.framework.event.linterface;

import com.michaelnovakjr.numberpicker.NumberPicker;


public interface OnChangedListener extends com.michaelnovakjr.numberpicker.NumberPicker.OnChangedListener{
	public void onChanged(NumberPicker picker, int oldVal, int newVal, int position);
}
