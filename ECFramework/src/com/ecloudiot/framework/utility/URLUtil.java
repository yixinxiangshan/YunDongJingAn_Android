package com.ecloudiot.framework.utility;

import android.view.Display;

import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.javascript.JsUtility;

public class URLUtil {
	private static String TAG = "URLUtil";

	/**
	 * 获取完整 url
	 * 
	 * @param imageName
	 * @return
	 */
	public static String getImageWholeUrl(String imageName) {
		// LogUtil.d(TAG, Constants.BASE_URL_IMAGE+imageName);
		return Constants.BASE_URL_IMAGE + imageName;
	}

	/**
	 * 获取适合屏幕尺寸大小图片 url
	 * 
	 * @param imageName
	 * @return
	 */

	public static String getFitImageWholeUrl(String imageName) {
		if (StringUtil.isEmpty(imageName)) {
			return "";
		}
		String[] ss = new String[2];
		try {
			ss = imageName.split("\\.");
		} catch (Exception e) {
			LogUtil.e(TAG, "image name is invalid...");
		}
		Display display = JsUtility.GetActivityContext().getWindowManager()
				.getDefaultDisplay();
		int screenWidth = display.getWidth();
		String imgnameString = "";
		if (StringUtil.isNotEmpty(ss) && ss.length > 1) {
			imgnameString = ss[0] + "_" + screenWidth + "." + ss[1];
		}
		return getImageWholeUrl(imgnameString);
	}

	/**
	 * 获取用户自定义大小的图片
	 */
	public static String getCustomImageWholeUr(String imageName,String imageSize) {
		if (StringUtil.isEmpty(imageName)) {
			return "";
		}
		String[] ss = new String[2];
		try {
			ss = imageName.split("\\.");
		} catch (Exception e) {
			LogUtil.e(TAG, "image name is invalid...");
		}

		String imgnameString = "";
		if (StringUtil.isNotEmpty(ss) && ss.length > 1) {
			imgnameString = ss[0] + "_" + imageSize + "." + ss[1];
		}
		return getImageWholeUrl(imgnameString);
	}

	/**
	 * 获取小图片
	 * 
	 * @param imageName
	 * @return
	 */
	public static String getSImageWholeUrl(String imageName) {
		String[] ss = new String[2];
		String imgnameString = "";
		try {
			ss = imageName.split("\\.");
		} catch (Exception e) {
			LogUtil.e(TAG, "image name is invalid...");
		}
		if (StringUtil.isNotEmpty(ss) && ss.length > 1) {
			imgnameString = ss[0] + "_" + 600 + "." + ss[1];
		}
		return getImageWholeUrl(imgnameString);
	}
}
