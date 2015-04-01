package com.ecloudiot.framework.event.linterface;

public interface OnKeyBoardActionListener {
	public void onPress(int primaryCode) ;
	public void onRelease(int primaryCode);
	public void onKey(int primaryCode, int[] keyCodes);
	public void onText(CharSequence text);
	public void swipeLeft();
	public void swipeRight();
	public void swipeDown();
	public void swipeUp();
}
