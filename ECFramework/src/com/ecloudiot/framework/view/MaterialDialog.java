package com.ecloudiot.framework.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.utility.StringUtil;

/**
 * Created by drakeet on 9/28/14.
 */
public class MaterialDialog {

	private Context mContext;
	private AlertDialog mAlertDialog;
	private MaterialDialog.Builder mBuilder;
	private View mView;
	private int mTitleResId;
	private CharSequence mTitle;
	private int mMessageResId;
	private CharSequence mMessage;
	private Button mPositiveButton;
	private Button mNegativeButton;
	private boolean mHasShow = false;
	private Drawable mBackgroundDrawable;
	private int mBackgroundResId;
	private String mNegativeButtonText;
	private String mPositiveButtonText;
	private OnClickListener mNegativeButtonClick = null;
	private OnClickListener mPositiveButtonClick = null;
	public MaterialDialog(Context context) {
		this.mContext = context;
	}

	public void show() {
		if (mHasShow == false)
			mBuilder = new Builder();
		else
			mAlertDialog.show();
		mHasShow = true;
	}

	public void setView(View view) {
		mView = view;
		if (mBuilder != null) {
			mBuilder.setView(view);
		}
	}

	public void setBackground(Drawable drawable) {
		mBackgroundDrawable = drawable;
		if (mBuilder != null) {
			mBuilder.setBackground(mBackgroundDrawable);
		}
	}

	public void setBackgroundResource(int resId) {
		mBackgroundResId = resId;
		if (mBuilder != null) {
			mBuilder.setBackgroundResource(mBackgroundResId);
		}
	}

	public void dismiss() {
		mAlertDialog.dismiss();
	}

	private int dip2px(float dpValue) {
		final float scale = mContext.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public void setTitle(int resId) {
		mTitleResId = resId;
		if (mBuilder != null)
			mBuilder.setTitle(resId);
	}

	public void setTitle(CharSequence title) {
		mTitle = title;
		if (mBuilder != null) {
			mBuilder.setTitle(title);
		}
	}

	public void setMessage(int resId) {
		mMessageResId = resId;
		if (mBuilder != null)
			mBuilder.setMessage(resId);
	}

	public void setMessage(CharSequence message) {
		mMessage = message;
		if (mBuilder != null)
			mBuilder.setMessage(message);
	}

	public void setPositiveButton(String text, final View.OnClickListener listener) {
		mPositiveButtonText = text;
		mPositiveButtonClick = listener;
	}

	/**
	 * set negative button
	 * 
	 * @param text
	 *            the name of button
	 * @param listener
	 */
	public void setNegativeButton(String text, final View.OnClickListener listener) {
		mNegativeButtonText = text;
		mNegativeButtonClick = listener;
	}

	private class Builder {
		private TextView mTitleView;
		private TextView mMessageView;
		private Window mAlertDialogWindow;
		private LinearLayout mButtonLayout;

		private Builder() {
			mAlertDialog = new AlertDialog.Builder(mContext).create();
			mAlertDialog.show();
			mAlertDialogWindow = mAlertDialog.getWindow();
			mAlertDialogWindow.setContentView(R.layout.materialdialog);
			mTitleView = (TextView) mAlertDialogWindow.findViewById(R.id.title);
			mMessageView = (TextView) mAlertDialogWindow.findViewById(R.id.message);
			mButtonLayout = (LinearLayout) mAlertDialogWindow.findViewById(R.id.buttonLayout);
			mNegativeButton = (Button) mAlertDialog.findViewById(R.id.btn_cancel);
			mPositiveButton = (Button) mAlertDialog.findViewById(R.id.btn_ok);
			if (mView != null) {
				LinearLayout linearLayout = (LinearLayout) mAlertDialogWindow.findViewById(R.id.contentView);
				linearLayout.removeAllViews();
				linearLayout.addView(mView);
			}
			if (mTitleResId != 0) {
				setTitle(mTitleResId);
			}
			if (mTitle != null) {
				setTitle(mTitle);
			}
			if (mMessageResId != 0) {
				setMessage(mMessageResId);
			}
			if (mMessage != null) {
				setMessage(mMessage);
			}

			if (!StringUtil.isEmpty(mPositiveButtonText)) {
				mPositiveButton.setText(mPositiveButtonText);
				mPositiveButton.setOnClickListener(mPositiveButtonClick);
				mPositiveButton.setVisibility(View.VISIBLE);
			} else {
				mPositiveButton.setVisibility(View.GONE);
			}
			if (!StringUtil.isEmpty(mNegativeButtonText)) {
				mNegativeButton.setText(mNegativeButtonText);
				mNegativeButton.setOnClickListener(mNegativeButtonClick);
				mNegativeButton.setVisibility(View.VISIBLE);
			} else {
				mNegativeButton.setVisibility(View.GONE);
			}
			if (mBackgroundResId != 0) {
				LinearLayout linearLayout = (LinearLayout) mAlertDialogWindow.findViewById(R.id.material_background);
				linearLayout.setBackgroundResource(mBackgroundResId);
			}
			if (mBackgroundDrawable != null) {
				LinearLayout linearLayout = (LinearLayout) mAlertDialogWindow.findViewById(R.id.material_background);
				linearLayout.setBackgroundDrawable(mBackgroundDrawable);
			}
		}

		public void setTitle(int resId) {
			mTitleView.setText(resId);
		}

		public void setTitle(CharSequence title) {
			mTitleView.setText(title);
		}

		public void setMessage(int resId) {
			mMessageView.setText(resId);
		}

		public void setMessage(CharSequence message) {
			mMessageView.setText(message);
		}

		/**
		 * set positive button
		 * 
		 * @param text
		 *            the name of button
		 * @param listener
		 */
		@SuppressWarnings("unused")
		public void setPositiveButton(String text, final View.OnClickListener listener) {
			Button button = new Button(mContext);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			button.setLayoutParams(params);
			button.setBackgroundResource(R.drawable.material_card);
			button.setTextColor(Color.argb(255, 35, 159, 242));
			button.setText(text);
			button.setGravity(Gravity.CENTER);
			button.setTextSize(18);
			button.setPadding(dip2px(12), dip2px(12), dip2px(32), dip2px(12));
			button.setOnClickListener(listener);
			mButtonLayout.addView(button);
		}

		/**
		 * set negative button
		 * 
		 * @param text
		 *            the name of button
		 * @param listener
		 */
		@SuppressWarnings("unused")
		public void setNegativeButton(String text, final View.OnClickListener listener) {
			Button button = new Button(mContext);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			button.setLayoutParams(params);
			button.setBackgroundResource(R.drawable.material_card);
			button.setText(text);
			button.setTextColor(Color.argb(222, 0, 0, 0));
			button.setTextSize(18);
			button.setGravity(Gravity.CENTER);
			button.setPadding(0, dip2px(12), 0, dip2px(12));
			button.setOnClickListener(listener);
			if (mButtonLayout.getChildCount() > 0) {
				params.setMargins(20, 0, 10, 0);
				button.setLayoutParams(params);
				mButtonLayout.addView(button, 1);
			} else {
				button.setLayoutParams(params);
				mButtonLayout.addView(button);
			}
		}

		public void setView(View view) {
			LinearLayout l = (LinearLayout) mAlertDialogWindow.findViewById(R.id.contentView);
			l.removeAllViews();
			l.addView(view);
		}

		public void setBackground(Drawable drawable) {
			LinearLayout linearLayout = (LinearLayout) mAlertDialogWindow.findViewById(R.id.material_background);
			linearLayout.setBackgroundDrawable(drawable);
		}

		public void setBackgroundResource(int resId) {
			LinearLayout linearLayout = (LinearLayout) mAlertDialogWindow.findViewById(R.id.material_background);
			linearLayout.setBackgroundResource(resId);
		}
	}
}