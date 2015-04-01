/***********************************************************************
 * Module:  FileParsing.java
 * Author:  qijian
 * Purpose: Defines the Class FileParsing
 ***********************************************************************/

package com.ecloudiot.framework.utility;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ecloudiot.framework.widget.model.KeyValueModel;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class GsonUtil {
	private final static String TAG = "GsonParsing";
	private static final Gson gson = new Gson();

	private GsonUtil() {
	}

	/**
	 * 把json字符串解析成指定的Type
	 * 
	 * @param json
	 *            需要解析的json字符串
	 * @param typeOfT
	 *            指定需要解析的类型
	 */
	public static <T> T fromJson(String json, Type typeOfT) {
		if (json != null) {
			try {
				return gson.fromJson(json, typeOfT);
			} catch (JsonSyntaxException e) {
				LogUtil.e(TAG, "fromJson error: " + e.toString());
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 把 json对象 解析成指定的java类
	 * 
	 * @param jObject
	 * @param typeOfT
	 * @return
	 */
	public static <T> T fromJson(JsonObject jObject, Type typeOfT) {
		if (jObject != null) {
			try {
				return gson.fromJson(jObject, typeOfT);
			} catch (JsonSyntaxException e) {
				LogUtil.e(TAG, "fromJson error: " + e.toString());
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 把指定的type型转化为json字符串
	 * 
	 * @param itemContent
	 * @return
	 */
	@SuppressWarnings({"unchecked"})
	public static String toJson(Object typeOfT) {
		// LogUtil.d(TAG, "toJson : typeOfT = " + typeOfT.toString());
		if (typeOfT instanceof List) {
			// LogUtil.d(TAG, "toJson : i am list ...");
			return toJson((List<KeyValueModel>) typeOfT);
		}
		if (typeOfT instanceof JsonElement) {
			toJson((JsonElement) typeOfT);
		}
		if (typeOfT != null) {
			return gson.toJson(typeOfT);
		} else {
			return null;
		}
	}

	public static String toJson(JsonElement element) {
		if (element != null) {
			return gson.toJson(element);
		} else {
			return "";
		}
	}

	private static String toJson(List<KeyValueModel> keyValueList) {
		if (keyValueList == null || keyValueList.size() <= 0) {
			return null;
		}
		String json = "{";
		for (int i = 0; i < keyValueList.size(); i++) {
			KeyValueModel itemModel = keyValueList.get(i);
			if (i != 0) {
				json += ",";
			}
			json += "'"
			        + itemModel.getKey()
			        + "':'"
			        + (StringUtil.isEmpty(itemModel.getValue()) && StringUtil.isNotEmpty(itemModel.getDefaultValue()) ? itemModel.getDefaultValue() : itemModel
			                .getValue()) + "'";

		}
		json += "}";
		// LogUtil.d(TAG, "write : json = " + json);
		return URLEncoder.encode(json).replaceAll("\\+", "%20");
	}

	public static Map<String, String> toHashMap(String json) {
		if (StringUtil.isNotEmpty(json)) {
			Type mapType = (new HashMap<String, String>()).getClass();
			try {
				return gson.fromJson(json, mapType);
			} catch (Exception e) {
				LogUtil.e(TAG, "toHashMap error: " + e.toString());
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 深度复制 JsonObject
	 * 
	 * @param jObject
	 * @return
	 */
	public static JsonObject deepCopyJObject(JsonObject jObject) {
		JsonObject result = new JsonObject();
		for (Map.Entry<String, JsonElement> entry : jObject.entrySet()) {
			result.add(entry.getKey(), deepCopyJElement(entry.getValue()));
		}
		return result;
	}

	/**
	 * 深度复制 JsonArray
	 * 
	 * @param jArray
	 * @return
	 */
	public static JsonArray deepCopyJArray(JsonArray jArray) {
		JsonArray result = new JsonArray();
		for (JsonElement element : jArray) {
			result.add(deepCopyJElement(element));
		}
		return result;
	}

	/**
	 * 深度复制 JsonElement
	 * 
	 * @param jElement
	 * @return
	 */
	public static JsonElement deepCopyJElement(JsonElement jElement) {
		if (jElement.isJsonObject()) {
			return deepCopyJObject(jElement.getAsJsonObject());
		} else if (jElement.isJsonArray()) {
			return deepCopyJArray(jElement.getAsJsonArray());
		} else if (jElement.isJsonPrimitive()) {
			return jElement;
		}
		return null;
	}

	/**
	 * 合并jsonObject
	 * 
	 * @param sourceElement
	 * @param terElement
	 * @return
	 */
	public static JsonElement merge(JsonElement element1, JsonElement element2) {
		if (element1.isJsonArray() && element2.isJsonArray()) {
			JsonArray jArray1 = element1.getAsJsonArray();
			JsonArray jArray2 = element2.getAsJsonArray();
			if (!isLikeSuperficial(jArray1, jArray2)) {
				// if (jArray1.size()!=1 && jArray2.size()==1) {
				// jArray1.add(jArray2.get(0));
				// return jArray1;
				// }
				for (int i = 0; i < jArray1.size(); i++) {
					if (i < jArray2.size()) {
						merge(jArray1.get(i), jArray2.get(i));
					}
				}
				if (jArray1.size() < jArray2.size()) {
					for (int i = jArray1.size(); i < jArray2.size(); i++) {
						jArray1.add(jArray2.get(i));
					}
				}
			} else {
				for (int i = 0; i < jArray2.size(); i++) {
					jArray1.add(jArray2.get(i));
				}
			}
		} else if (element1.isJsonObject() && element2.isJsonObject()) {
			JsonObject jObject1 = (JsonObject) element1;
			JsonObject jObject2 = (JsonObject) element2;
			Set<Map.Entry<String, JsonElement>> jSet = jObject2.entrySet();
			Iterator<Map.Entry<String, JsonElement>> iterator = jSet.iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, JsonElement> entry = (Map.Entry<String, JsonElement>) iterator.next();
				if (jObject1.has(entry.getKey())) {
					jObject1.add(entry.getKey(), merge(jObject1.get(entry.getKey()), entry.getValue()));
				} else {
					jObject1.add(entry.getKey(), entry.getValue());
				}
			}
		} else {
			element1 = element2;
		}
		return element1;
	}

	/**
	 * 判断两个jsonObject是否一样,即所有的key都一样,最外一层,数组的话，则所有的成员符合该规则
	 * 
	 * @param jElement1
	 * @param jElement2
	 * @return
	 */
	public static boolean isLikeSuperficial(JsonElement jElement1, JsonElement jElement2) {
		if (jElement1.isJsonObject() && jElement2.isJsonObject()) {
			JsonObject jObject1 = (JsonObject) jElement1;
			JsonObject jObject2 = (JsonObject) jElement2;
			Set<Map.Entry<String, JsonElement>> jSet = jObject2.entrySet();
			Iterator<Map.Entry<String, JsonElement>> iterator = jSet.iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, JsonElement> entry = (Map.Entry<String, JsonElement>) iterator.next();
				if (!jObject1.has(entry.getKey())) {
					return false;
				}
			}
			jSet = jObject1.entrySet();
			Iterator<Map.Entry<String, JsonElement>> iterator2 = jSet.iterator();
			while (iterator2.hasNext()) {
				Map.Entry<String, JsonElement> entry = (Map.Entry<String, JsonElement>) iterator2.next();
				if (!jObject2.has(entry.getKey())) {
					return false;
				}
			}
			return true;
		} else if (jElement1.isJsonArray() && jElement2.isJsonArray()) {
			JsonArray jArray1 = jElement1.getAsJsonArray();
			JsonArray jArray2 = jElement2.getAsJsonArray();
			for (JsonElement e1 : jArray1) {
				for (JsonElement e2 : jArray2) {
					if (!isLikeSuperficial(e1, e2)) {
						return false;
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public static boolean isEmpty(JsonElement jElement) {
		if (jElement.isJsonNull() || jElement == null) {
			return true;
		}
		if (jElement.isJsonPrimitive() && StringUtil.isEmpty(jElement.getAsString())) {
			return true;
		}
		return false;
	}

	public static String getStringElement(String jsonString, String keys) {
		Object object = getObjectElement(jsonString, keys);
		if (object == null) {
			return "";
		}
		if (object instanceof String) {
			return object.toString();
		} else if (object instanceof JsonElement && ((JsonElement) object).isJsonPrimitive()) {
			return ((JsonElement) object).getAsString();
		}
		return "";
	}

	public static int getIntElement(String jsonString, String keys) {
		Object object = getObjectElement(jsonString, keys);
		if (object == null) {
			return -1;
		}
		int i = -1;
		if (object instanceof String) {
			i = Integer.parseInt(object.toString());
			return i;
		} else if (object instanceof JsonElement && ((JsonElement) object).isJsonPrimitive()) {
			return ((JsonElement) object).getAsInt();
		}
		return -1;
	}

	private static Object getObjectElement(String jsonString, String keys) {
		JsonElement jsonElement = (new JsonParser()).parse(jsonString);
		if (jsonElement == null || jsonElement.isJsonNull()) {
			return null;
		}
		List<String> keyList = null;
		if (keys.contains(".")) {
			keyList = Arrays.asList(keys.split("\\."));
		} else {
			keyList = new ArrayList<String>();
			keyList.add(keys);
		}
		Object object = jsonElement;
		for (String key : keyList) {
			object = WidgetUtil.getJElement(null, object, key);
		}
		return object;
	}

}