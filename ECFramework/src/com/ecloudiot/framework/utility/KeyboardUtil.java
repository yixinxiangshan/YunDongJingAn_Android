package com.ecloudiot.framework.utility;
import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.text.Editable;
import android.text.Selection;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ecloudiot.framework.R;

public class KeyboardUtil {
	private String TAG = "KeyboardUtil";
	private KeyboardView keyboardView;
	private Keyboard k;// 数字键盘
	private LinearLayout layout;
	private int count = 0;
//	private int index = 0;
	
	public KeyboardUtil(Activity act, Context ctx,LinearLayout layout) {
		this.layout = layout;
		k = new Keyboard(ctx, R.xml.symbols);
		keyboardView = (KeyboardView) act.findViewById(R.id.keyboard_view);
		keyboardView.setKeyboard(k);
		keyboardView.setEnabled(true);
		keyboardView.setPreviewEnabled(true);
		keyboardView.setVisibility(View.VISIBLE);
		keyboardView.setOnKeyboardActionListener(listener);
	}

	private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
		@Override
		public void swipeUp() {
		}

		@Override
		public void swipeRight() {
		}

		@Override
		public void swipeLeft() {
		}

		@Override
		public void swipeDown() {
		}

		@Override
		public void onText(CharSequence text) {
		}

		@Override
		public void onRelease(int primaryCode) {
		}

		@Override
		public void onPress(int primaryCode) {
		}

		// 一些特殊操作按键的codes是固定的比如完成、回退等
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
							LogUtil.d(TAG, "index : "+editText.getId());
							newEditable.delete(0, 1);
							count--;
						}
					}
				}
			} else if (primaryCode == 4896) {// 清空
				for (int i = 0; i < childCount; i++) {
					EditText delEdt = (EditText) layout.getChildAt(i);
					delEdt.getText().clear();
				}
				count = 0;
			} else {
				if (count >= childCount) {
					return;
				}
				EditText editText = (EditText) layout.getChildAt(count);
				LogUtil.d(TAG, "editText : "+editText);
				LogUtil.d(TAG, "index : "+editText.getId());
				Editable newEditable = editText.getText();
//				int start = editText.getSelectionStart();
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
		}
	};

	public void showKeyboard() {
		int visibility = keyboardView.getVisibility();
		if (visibility == View.GONE || visibility == View.INVISIBLE) {
			keyboardView.setVisibility(View.VISIBLE);
		}
	}
}
