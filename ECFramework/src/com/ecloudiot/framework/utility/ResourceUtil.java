package com.ecloudiot.framework.utility;

import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.javascript.JsUtility;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResourceUtil {
	// private static String TAG = "ResourceUtil";
	private static ResourceUtil resourceUtil = null;

	// private static String TAG = "ResourceUtil";

	public static ResourceUtil getInstance() {
		if (resourceUtil == null) {
			resourceUtil = new ResourceUtil();
		}
		return resourceUtil;
	}

	/**
	 * 通过控件或是资源名字获得相应的id
	 * 
	 * @param name
	 * @return id
	 */
	public static int getRawFromContext(Context context, String name) {
		return getIdFromContext(context, name, "raw");
	}

	/**
	 * 通过控件或是资源名字获得相应的id
	 * 
	 * @param name
	 * @return id
	 */
	public static int getLayoutIdFromContext(Context context, String name) {
		return getIdFromContext(context, name, "layout");
	}

	/**
	 * 通过控件或是资源名字获得相应的id
	 * 
	 * @param name
	 * @return id
	 */
	public static int getViewIdFromContext(Context context, String name) {
		return getIdFromContext(context, name, "id");
	}

	/**
	 * 获取 Drawable
	 * 
	 * @param context
	 * @param name
	 * @return
	 */
	public static int getDrawableIdFromContext(Context context, String name) {
		return getIdFromContext(context, name, "drawable");
	}
	/**
	 * 获取 Color
	 * 
	 * @param context
	 * @param name
	 * @return
	 */
	public static int getColorIdFromContext(Context context, String name) {
		return getIdFromContext(context, name, "color");
	}

	/**
	 * 获取 Menu id
	 * 
	 * @param context
	 * @param name
	 * @return
	 */
	public static int getMenuIdFromContext(Context context, String name) {
		return getIdFromContext(context, name, "menu");
	}

	/**
	 * 获取 array id
	 * 
	 * @param context
	 * @param name
	 * @return
	 */
	public static int getArrayIdFromContext(Context context, String name) {
		return getIdFromContext(context, name, "array");
	}

	/**
	 * 获取button
	 * 
	 * @param id
	 * @return
	 */
	public static Button getButtonByid(String id) {
		return (Button) getViewByid(id);
	}

	/**
	 * 获取text view
	 * 
	 * @param id
	 * @return
	 */
	public static TextView getTextViewById(String id) {
		return (TextView) getViewByid(id);
	}

	/**
	 * 获取 view
	 * 
	 * @param id
	 * @return
	 */
	public static View getViewByid(String id) {
		Activity activity = JsUtility.GetActivityContext();
		return (View) activity.findViewById(getIdFromContext(id, "id"));
	}

	/**
	 * 获取 对应type（drawable、layout、menu等） 的 id
	 * 
	 * @param context
	 * @param name
	 * @param type
	 * @return
	 */
	public static int getIdFromContext(Context context, String name, String type) {
		// 先查询jsviewid，有则先返回
		Integer jsViewid = JsAPI.instance().getdViewId(name);
		if (jsViewid != null) {
			return jsViewid;
		}

		int id;
		// 反射获得id
		try {
			// LogUtil.d(TAG, "packageName:" + context.getPackageName());
			id = context.getResources().getIdentifier(name, type, context.getPackageName());
		} catch (Exception e) {
			// LogUtil.d(TAG, "get res id error");
			e.printStackTrace();
			id = 0;
		}
		return id;
	}

	/**
	 * 获取 对应type（drawable、layout、menu等） 的 id
	 * 
	 * @param name
	 * @param type
	 * @return
	 */
	public static int getIdFromContext(String name, String type) {
		Activity activity = JsUtility.GetActivityContext();
		return getIdFromContext(activity, name, type);
	}

}
