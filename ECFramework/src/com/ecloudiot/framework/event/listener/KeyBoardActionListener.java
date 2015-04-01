package com.ecloudiot.framework.event.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.inputmethodservice.Keyboard;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ecloudiot.framework.event.linterface.OnKeyBoardActionListener;

public class KeyBoardActionListener extends BaseEventListener implements
		OnKeyBoardActionListener {
	private final static String TAG = "KeyBoardActionListener";
	private LinearLayout layout;
	private int count = 0;
	private int position = 0;
	private List<String> numList = new ArrayList<String>();
	
	public KeyBoardActionListener(Object pageContext, Object widget,
			String eventConfigString) {
		super(pageContext, widget, eventConfigString);
	}

	@Override
	public void onPress(int primaryCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRelease(int primaryCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onKey(int primaryCode, int[] keyCodes) {
		int childCount = layout.getChildCount();
		
		if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
			if (count <= childCount && count > 0) {
				EditText editText = (EditText) layout.getChildAt(count - 1);
				Editable newEditable = editText.getText();
				int start = editText.getSelectionStart();
				if (newEditable != null && newEditable.length() > 0) {
					if (start > 0) {
						position = editText.getId();
						numList.remove(position);
						Log.d(TAG, "index : "+editText.getId());
						newEditable.delete(0, 1);
						count--;
					}
				}
			}
		} else if (primaryCode == 4896) {// 清空
			for (int i = 0; i < childCount; i++) {
				EditText delEdt = (EditText) layout.getChildAt(i);
				delEdt.getText().clear();
				numList.clear();
			}
			count = 0;
		} else {
			if (count >= childCount) {
				return;
			}
			EditText editText = (EditText) layout.getChildAt(count);
			position = editText.getId();
			numList.add(position, Character.toString((char) primaryCode));
			Log.d(TAG, "editText : "+editText);
			Log.d(TAG, "index : "+editText.getId());
			Editable newEditable = editText.getText();
//			int start = editText.getSelectionStart();
			// 将要输入的数字现在编辑框中
			int selEndIndex = Selection.getSelectionEnd(newEditable);
			// 新字符串的长度
			int newLen = newEditable.length();
			// 旧光标位置超过字符串长度
			if (selEndIndex > newLen) {
				selEndIndex = newEditable.length();
			}
			// 设置新光标所在的位置
			Selection.setSelection(newEditable, selEndIndex);
			if (newEditable.length() == 0) {
				newEditable.insert(editText.getSelectionStart(), Character.toString((char) primaryCode));
				count++;
			}
		
		}
		
		HashMap<String, String> bundleData = new HashMap<String, String>();
		bundleData.put("position",String.valueOf(position));
		bundleData.put("text", Character.toString((char) primaryCode));
		String signNum = "";
		for (int i = 0; i < numList.size(); i++) {
			signNum = signNum +numList.get(i);
		}
		bundleData.put("signNum", signNum);
		runJs(bundleData);
	}

	@Override
	public void onText(CharSequence text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void swipeLeft() {
		// TODO Auto-generated method stub

	}

	@Override
	public void swipeRight() {
		// TODO Auto-generated method stub

	}

	@Override
	public void swipeDown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void swipeUp() {
		// TODO Auto-generated method stub

	}

}
