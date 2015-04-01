package com.ecloudiot.framework.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;

public class DensityUtil {
	private final static String TAG = "DensityUtil";
	public static int DENSITY_DEFAULT = 160;

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dipTopx(Context context, float dpValue) {

		// Resources res = getResources();
		// float value = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
		// valueInDP, res.getDisplayMetrics());

		final float scale = context.getResources().getDisplayMetrics().density;

		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int pxTodip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从px 的单位 转成为 sp
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static float pxToSp(Context context, float pxValue) {
		final float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
		return Float.parseFloat((pxValue / scaledDensity) + "");
	}

	/**
	 * 根据手机的分辨率从sp 的单位 转成为 px
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static float spToPx(Context context, float pxValue) {
		final float scaledDensity = (float) context.getResources().getDisplayMetrics().scaledDensity;
		return (float) (pxValue * scaledDensity);
	}

	@SuppressLint("NewApi")
	public static int screenWidth() {
		LogUtil.d(TAG, "screenWidth");
		Display display = IntentUtil.getActivity().getWindowManager().getDefaultDisplay();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			Point size = new Point();
			display.getSize(size);
			return size.x;
		}
		return display.getWidth();
	}

	public static int screenWidthInDip() {
		return pxTodip(IntentUtil.getActivity(), screenWidth());
	}
}
