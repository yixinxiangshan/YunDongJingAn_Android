package com.ecloudiot.framework.utility;

import java.util.Map;
import java.util.Set;

public class JsonUtil {
	public static String simpleMapToJsonStr(Map<String, String> map) {
		if (map == null || map.isEmpty()) {
			return "{}";
		}
		String jsonStr = "{";
		Set<?> keySet = map.keySet();
		for (Object key : keySet) {
			jsonStr += "\"" + key + "\":\"" + map.get(key) + "\",";
		}
		jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
		jsonStr += "}";
		return jsonStr;
	}
}
