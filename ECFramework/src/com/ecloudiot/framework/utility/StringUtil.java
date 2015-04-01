package com.ecloudiot.framework.utility;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;


import android.R.bool;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.telephony.TelephonyManager;

import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.javascript.JsUtility;

public class StringUtil {
	private static String TAG = "StringUtil";

	public static boolean isEmpty(String string) {
		return (string == null || string.equalsIgnoreCase("") || string.equalsIgnoreCase("null") || string.equalsIgnoreCase("undefined"));
	}

	public static boolean isNotEmpty(String string) {
		return !isEmpty(string);
	}

	public static boolean isNotEmpty(String[] strings) {
		if (null == strings || strings.length <= 0)
			return false;
		for (int i = 0; i < strings.length; i++) {
			if (isEmpty(strings[i]))
				return false;
		}
		return true;
	}
	
	public static boolean isImageName(String string) {
		if (isEmpty(string)) {
			return false;
		}
		return (string.endsWith(".png") || string.endsWith(".jpg") || string.endsWith(".jpeg") || string.endsWith(".gif"));
	}

	/**
	 * 返回首字母大写
	 * 
	 * @param string
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static String getFeatureString(String string) {
		if (isNotEmpty(string)) {
			return string.substring(0, 1).toUpperCase() + string.substring(1);
		}
		return string;
	}

	/**
	 * 获取资源文件中string
	 * 
	 * @param name
	 * @return
	 */
	public static String getResString(String name) {
		String string = null;
		try {
			string = ECApplication.getInstance().getNowActivity().getResources()
					.getString(ResourceUtil.getIdFromContext(name, "string"));
		} catch (NotFoundException e) {
			LogUtil.e(TAG, "getResString error: " + e.toString());
		}
		return string;
	}

	/**
	 * MD5加密
	 * 
	 * @param String
	 */
	public static String calcMD5String(String inString) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			LogUtil.d(TAG, "calcMD5String : " + e.toString());
			return "";
		}
		char[] charArray = inString.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++)
			byteArray[i] = (byte) charArray[i];
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}

	/**
	 * 获取设备id
	 * 
	 * @return
	 */
	public static String getDeviceId() {
		TelephonyManager telephonyManager = (TelephonyManager) ECApplication.getInstance().getNowActivity()
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (StringUtil.isNotEmpty(telephonyManager.getDeviceId())) {
			return telephonyManager.getDeviceId();
		}
		return "201310080001";
	}

	/**
	 * 去掉字符串头尾
	 * 
	 * @param string
	 * @return
	 */
	public static String slim(String string) {
		if (string.length() <= 2) {
			return string;
		}
		return string.substring(1, string.length() - 1);
	}

	/**
	 * 去掉首字母
	 * 
	 * @param string
	 * @return
	 */
	public static String slimH(String string) {
		if (string.length() <= 1) {
			return string;
		}
		return string.substring(1, string.length());
	}

	/**
	 * 去掉尾字母
	 * 
	 * @param string
	 * @return
	 */
	public static String slimE(String string) {
		if (string.length() <= 1) {
			return string;
		}
		return string.substring(0, string.length() - 1);
	}

	/**
	 * 解析 viewUtility 中 widgetName 参数
	 * 
	 * @param widgetName
	 * @return
	 */
	public static String getWidgetNameSingle(String widgetName) {
		String[] arrStrings = null;
		if (!isEmpty(widgetName)) {
			arrStrings = widgetName.split(":");
		}
		if (arrStrings != null)
			return arrStrings[0];
		return null;
	}

	public static String getLayoutNmaeSingle(String widgetName) {
		String[] arrStrings = null;
		if (!isEmpty(widgetName)) {
			arrStrings = widgetName.split(":");
		}
		if (arrStrings != null && arrStrings.length >= 2) {
			return arrStrings[1];
		}
		return null;

	}

	@SuppressWarnings("rawtypes")
	public static Class classString() throws ClassNotFoundException {
		return int.class;
	}

	public static String stringToString(String str) {
		return str;
	}

	@SuppressWarnings("rawtypes")
	public static Class classRGBInt() throws ClassNotFoundException {
		return int.class;
	}

	public static Object stringToRGBInt(String str) {
		return ColorUtil.getColorValueFromRGB(str);
	}

	@SuppressWarnings("rawtypes")
	public static Class classResId() throws ClassNotFoundException {
		return int.class;
	}

	public static Object stringToResId(String str) {
		return ResourceUtil.getViewIdFromContext(JsUtility.GetAppContext(), str);
	}

	@SuppressWarnings("rawtypes")
	public static Class classResLayout() throws ClassNotFoundException {
		return int.class;
	}

	public static Object stringToResLayout(String str) {
		return ResourceUtil.getLayoutIdFromContext(JsUtility.GetAppContext(), str);
	}

	@SuppressWarnings("rawtypes")
	public static Class classResArray() throws ClassNotFoundException {
		return int.class;
	}

	public static Object stringToResArray(String str) {
		return ResourceUtil.getArrayIdFromContext(JsUtility.GetAppContext(), str);
	}

	@SuppressWarnings("rawtypes")
	public static Class classResMenu() throws ClassNotFoundException {
		return int.class;
	}

	public static Object stringToResMenu(String str) {
		return ResourceUtil.getMenuIdFromContext(JsUtility.GetAppContext(), str);
	}

	@SuppressWarnings("rawtypes")
	public static Class classResRaw() throws ClassNotFoundException {
		return int.class;
	}

	public static Object stringToResRaw(String str) {
		return ResourceUtil.getRawFromContext(JsUtility.GetAppContext(), str);
	}

	@SuppressWarnings("rawtypes")
	public static Class classResDrawable() throws ClassNotFoundException {
		return int.class;
	}

	public static Object stringToResDrawable(String str) {
		return ResourceUtil.getDrawableIdFromContext(JsUtility.GetAppContext(), str);
	}

	@SuppressWarnings("rawtypes")
	public static Class classSpPx() throws ClassNotFoundException {
		return float.class;
	}

	public static Object stringToSpPx(String str) {
		Float sp = Float.parseFloat(str);
		return DensityUtil.spToPx(JsUtility.GetActivityContext(), sp);
	}

	@SuppressWarnings("rawtypes")
	public static Class classDipPx() throws ClassNotFoundException {
		return int.class;
	}

	public static Object stringToDipPx(String str) {
		int dip = Integer.parseInt(str);
		return DensityUtil.dipTopx(JsUtility.GetActivityContext(), dip);
	}

	@SuppressWarnings("rawtypes")
	public static Class classBoolean() throws ClassNotFoundException {
		return bool.class;
	}

	public static Object stringToBoolean(String str) {
		return Boolean.getBoolean(str);
	}

	@SuppressWarnings("rawtypes")
	public static Class classFloat() throws ClassNotFoundException {
		return float.class;
	}

	public static Object stringToFloat(String str) {
		return Float.parseFloat(str);
	}

	@SuppressWarnings("rawtypes")
	public static Class classInt() throws ClassNotFoundException {
		return int.class;
	}

	public static Object stringToInt(String str) {
		return Integer.parseInt(str);
	}

	@SuppressWarnings("rawtypes")
	public static Class classCharSequence() throws ClassNotFoundException {
		return Class.forName("java.lang.CharSequence");
	}

	public static Object stringToCharSequence(String str) {
		return str;
	}

	public static String join(String[] source, String value) {
		if (source == null)
			return null;
		int len = source.length;
		if (len == 0)
			return "";
		if (value == null)
			value = "";

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i != len; i++) {
			sb.append(source[i]).append(value);
		}
		len = sb.length();
		int end = len, start = end - value.length();
		sb.delete(start, end);
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	public static boolean inArray(Iterable iterable, Object value) {
		Iterator it = iterable.iterator();
		Object item;
		while (it.hasNext()) {
			item = it.next();
			if (item == value)
				return true;
		}
		return false;
	}

	public static boolean inArray(String[] source, String t) {
		return inArray(source, t, false);
	}

	public static boolean inArray(String[] source, String t, boolean ignoreCase) {
		for (String item : source) {
			if (ignoreCase) {
				if (item.equalsIgnoreCase(t)) {
					LogUtil.d(TAG, "inArray_ignoreCase_true:True 1");
					return true;
				}
			} else {
				if (item.equals(t)) {
					LogUtil.d(TAG, "inArray_ignoreCase_false:True 2");
					return true;
				}

			}

		}
		return false;
	}

	/**
	 * 判断字符串是否为取值表达式
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isExpression(String string) {
		if (isEmpty(string)) {
			return false;
		}
		return ((string.startsWith("{_") || string.startsWith("{#") || string.startsWith("{$")) && string.endsWith("}"));
	}

	/**
	 * 字符串转化为int
	 * 
	 * @param string
	 * @return
	 */
	public static int toInt(String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			LogUtil.e(TAG, "toInt error: " + e.toString());
			return -1;
		}
	}

	/**
	 * 字符串转化为浮点型
	 * 
	 * @param string
	 * @return
	 */
	public static float toFloat(String string) {
		try {
			return Float.parseFloat(string);
		} catch (NumberFormatException e) {
			LogUtil.e(TAG, "toInt error: " + e.toString());
			return -1.0f;
		}
	}
	/**
	 * 构造网络请求参数
	 * @param params
	 * @return
	 */
	public static String toNetString(HashMap<String, String> params) {
		if (params == null) {
			return "";
		}
		String paramsString = "";
		try {
			Iterator<String> keySetIterator = params.keySet().iterator();
			while (keySetIterator.hasNext()) {
				String key = keySetIterator.next();
				paramsString += key + "=" + URLEncoder.encode(params.get(key), "UTF-8").replaceAll("\\+", "%20") + "&";
			}
			paramsString = StringUtil.slimE(paramsString);
			return paramsString;
		} catch (Exception e) {
			LogUtil.d(TAG, "toNetString : "+e.toString());
			return "";
		}
	}


}
