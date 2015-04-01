package com.ecloudiot.framework.widget.adapter;

import java.io.IOException;
import java.util.List;

import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.widget.model.KeyValueModel;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class MapAdapter extends TypeAdapter<List<KeyValueModel>> {
	private final static String TAG = "MapAdapter";

	public MapAdapter() {
		super();
	}

	@Override
	public List<KeyValueModel> read(JsonReader reader) throws IOException {
		return null;
	}

	@Override
	public void write(JsonWriter writer, List<KeyValueModel> keyValueList) throws IOException {
		LogUtil.d(TAG, "MapAdapter write ...");
		if (keyValueList == null || keyValueList.size() <= 0) {
			writer.nullValue();
			return;
		}
		String json = "{";
		for (int i = 0; i < keyValueList.size(); i++) {
			KeyValueModel itemModel = keyValueList.get(i);
			if (i != 0) {
				json += ",";
			}
			json += "\"" + itemModel.getKey() + "\":\""
					+ (StringUtil.isEmpty(itemModel.getValue()) ? itemModel.getDefaultValue() : itemModel.getValue())
					+ "\"";

		}
		json += "}";
		LogUtil.d(TAG, "write : json = " + json);
		writer.value(json);
	}
}
