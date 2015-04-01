package com.ecloudiot.framework.utility;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

public class ColorUtil {
	private static String TAG = "ColorUtil";

	public static int getColorValueFromRGB(String RGBString) {
		int colorValue = 0;
		try {
			colorValue = Color.parseColor(RGBString);
		} catch (Exception e) {
			LogUtil.e(TAG, "color String is invalid...");
		}
		return colorValue;
	}

	public static int[] getComponentFromRGB(int color) {
		int[] rgb = new int[3];
		try {
			rgb[0] = Color.red(color);
			rgb[1] = Color.green(color);
			rgb[2] = Color.blue(color);
		} catch (Exception e) {
			LogUtil.e(TAG, "color String is invalid...");
			e.printStackTrace();
		}
		return rgb;
	}



	public static int[] getComponentFromARGB(int color) {
		int[] rgb = new int[4];
		try {
			rgb[0] = Color.alpha(color);
			rgb[1] = Color.red(color);
			rgb[2] = Color.green(color);
			rgb[3] = Color.blue(color);
		} catch (Exception e) {
			LogUtil.e(TAG, "color String is invalid...");
			e.printStackTrace();
		}
		return rgb;
	}

	// 从配置中获取颜色
	public static ColorStateList getColorState(String normal, String pressed, String selected) {
		ColorStateList cs = null;
		int pressed_Color = 0;
		int selected_Color = 0;
		int normal_Color = 0;
		if (!StringUtil.isEmpty(normal)) {
			normal_Color = getColorValueFromRGB(normal);
		}
		if (!StringUtil.isEmpty(pressed)) {
			pressed_Color = getColorValueFromRGB(pressed);
		}
		if (!StringUtil.isEmpty(selected)) {
			selected_Color = getColorValueFromRGB(selected);
		}
		int[] colors = new int[]{pressed_Color, selected_Color, normal_Color};
		int[][] states = new int[3][];
		states[0] = new int[]{android.R.attr.state_pressed};
		states[1] = new int[]{android.R.attr.state_selected};
		states[2] = new int[]{};
		cs = new ColorStateList(states, colors);
		return cs;
	}
	// 从配置中获取颜色
	public static ColorStateList getColorStateFromConfig(String file_path) {
		String jsonString = FileUtil.getFileFromConfig(file_path);
		JSONObject jsonObject;
		ColorStateList cs = null;
		int pressed_Color = 0;
		int selected_Color = 0;
		int normal_Color = 0;

		try {
			jsonObject = new JSONObject(jsonString);
			if (jsonObject.has("normal")) {
				normal_Color = getColorValueFromRGB(jsonObject.getString("normal"));
			}
			if (jsonObject.has("pressed")) {
				pressed_Color = getColorValueFromRGB(jsonObject.getString("pressed"));
			}
			if (jsonObject.has("selected")) {
				selected_Color = getColorValueFromRGB(jsonObject.getString("selected"));
			}
			int[] colors = new int[]{pressed_Color, selected_Color, normal_Color};
			int[][] states = new int[3][];
			states[0] = new int[]{android.R.attr.state_pressed};
			states[1] = new int[]{android.R.attr.state_selected};
			states[2] = new int[]{};
			cs = new ColorStateList(states, colors);

		} catch (JSONException e) {
			LogUtil.e(TAG, "getDrawbleFromConfig error: " + e.toString());
			e.printStackTrace();
		}

		return cs;
	}
	// 从配置中获取颜色 drawable
	public static Drawable getColorStateDrawable(String normal, String pressed, String selected) {
		StateListDrawable cs = null;
		Drawable pressed_Color;
		Drawable selected_Color;
		Drawable normal_Color;
		cs = new StateListDrawable();
		if (!StringUtil.isEmpty(normal)) {
			normal_Color = new ColorDrawable(Color.parseColor(normal));
			cs.addState(new int[]{}, normal_Color);
		}
		if (!StringUtil.isEmpty(pressed)) {
			pressed_Color = new ColorDrawable(Color.parseColor(pressed));
			cs.addState(new int[]{android.R.attr.state_pressed}, pressed_Color);
		}
		if (!StringUtil.isEmpty(selected)) {
			selected_Color = new ColorDrawable(Color.parseColor(selected));
			cs.addState(new int[]{android.R.attr.state_selected}, selected_Color);
		}
		return cs;
	}
	// 从配置中获取颜色 drawable
	public static Drawable getColorStateDrawableFromConfig(String file_path) {
		String jsonString = FileUtil.getFileFromConfig(file_path);
		JSONObject jsonObject;
		StateListDrawable cs = null;
		Drawable pressed_Color;
		Drawable selected_Color;
		Drawable normal_Color;
		cs = new StateListDrawable();
		try {

			jsonObject = new JSONObject(jsonString);
			if (jsonObject.has("pressed")) {
				pressed_Color = new ColorDrawable(Color.parseColor(jsonObject.getString("pressed")));
				cs.addState(new int[]{android.R.attr.state_pressed}, pressed_Color);
			}
			if (jsonObject.has("selected")) {
				selected_Color = new ColorDrawable(Color.parseColor(jsonObject.getString("selected")));
				cs.addState(new int[]{android.R.attr.state_selected}, selected_Color);
			}
			if (jsonObject.has("normal")) {
				normal_Color = new ColorDrawable(Color.parseColor(jsonObject.getString("normal")));
				cs.addState(new int[]{}, normal_Color);
			}

		} catch (JSONException e) {
			LogUtil.e(TAG, "getDrawbleFromConfig error: " + e.toString());
			e.printStackTrace();
		}

		return cs;
	}

}
