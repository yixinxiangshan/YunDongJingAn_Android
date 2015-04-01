package com.ecloudiot.framework.event.linterface;
import android.text.Editable;

public interface OnSignInputTextChangedListener {
	public void beforeTextChanged(CharSequence s, int start, int count, int after);
	public void onTextChanged(CharSequence s, int start, int before, int count);
	public void afterTextChanged(Editable s);
}
