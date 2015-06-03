package com.ecloudiot.framework.widget.listview;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.ecloudiot.framework.widget.ListViewBase;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;

public class ListViewBaseAdapter extends BaseAdapter implements PinnedSectionListAdapter {
	// private static String TAG = "ListViewBaseAdapter";
	private Context context;
	private ListViewBase listViewBase;
	private JsonObject widgetDataJObject;
	private JsonArray widgetDataJArray;
	private int fixedTitleId = -1;

	Map<String, String> inputMap = new HashMap<String, String>();
	public ArrayList<String> getViewTypeNames() {
		return viewTypeNames;
	}
	public void setViewTypeNames(ArrayList<String> viewTypeNames) {
		this.viewTypeNames = viewTypeNames;
	}

	private ArrayList<String> viewTypeNames = new ArrayList<String>();
	private ArrayList<ListViewCellBase> cells = new ArrayList<ListViewCellBase>();
	// 初始化
	public ListViewBaseAdapter(Context context, JsonObject widgetDataJObject, ListViewBase listViewBase) {
		// LogUtil.d(TAG, ListViewBaseAdapter.class.getName() + ":init adapter - " + widgetDataJObject.toString());
		setContext(context);
		setListViewBase(listViewBase);
		setWidgetDataJObject(widgetDataJObject);
		// LogUtil.d(TAG, widgetDataJObject.toString());
		setWidgetDataJArray(widgetDataJObject.get("data").getAsJsonArray());
		for (JsonElement cellDataObject : widgetDataJArray) {
			String cellViewType = cellDataObject.getAsJsonObject().get("viewType").getAsString();
			boolean isNewType = true;
			for (int i = 0; i < viewTypeNames.size(); i++) {
				if (cellViewType.equals(viewTypeNames.get(i))) {
					isNewType = false;
					if (fixedTitleId == -1 && cellViewType.equals("ListViewCellFixedTitle"))
						fixedTitleId = i;
					break;
				}
			}
			if (isNewType)
				addViewType(cellViewType);
		}
	}
	
	public void undateAdapter(JsonObject widgetDataJObject, ListViewBase listViewBase) {
		if (listViewBase != null) {
			setListViewBase(listViewBase);
		}
		setWidgetDataJObject(widgetDataJObject);
		// LogUtil.d(TAG, widgetDataJObject.toString());
		setWidgetDataJArray(widgetDataJObject.get("data").getAsJsonArray());

		for (JsonElement cellDataObject : widgetDataJArray) {
			String cellViewType = cellDataObject.getAsJsonObject().get("viewType").getAsString();
			boolean isNewType = true;
			for (int i = 0; i < viewTypeNames.size(); i++) {
				if (cellViewType.equals(viewTypeNames.get(i))) {
					isNewType = false;
					break;
				}
			}
			if (cellDataObject.getAsJsonObject().get("viewType").getAsString().equals("ListViewCellInputText")){
				addInputMap(cellDataObject.getAsJsonObject().get("name").getAsString(), cellDataObject.getAsJsonObject().get("inputText").getAsString());
			}
			if (isNewType)
				addViewType(cellViewType);
		}
		notifyDataSetChanged();
	}

	public void updateView(String itemData) {
		// 得到第一个可显示控件的位置，
		// LogUtil.d(TAG, "itemData:" + itemData);
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = null;
		try {
			jsonObject = (JsonObject) jsonParser.parse(itemData);
			int positon = jsonObject.get("position").getAsInt();
			JsonElement jsonElement = jsonObject.get("data").getAsJsonObject();
			widgetDataJArray.set(positon, jsonElement);
			notifyDataSetChanged();
		} catch (Exception e) {
			// LogUtil.e(TAG, "updateView error: can not parse to json object ...");
		}

	}

	public void updateViews(HashMap<String, String> params) {
		// LogUtil.d(TAG, "updateViews:");
		String jsonData = params.get("data");
		JsonParser jsonParser = new JsonParser();
		@SuppressWarnings("unused")
        JsonObject jsonObject = null;
		try {
			JsonArray jsonArray = (JsonArray) jsonParser.parse(jsonData).getAsJsonArray();
			for (JsonElement jsonElement : jsonArray) {
				JsonObject itemJObject = jsonElement.getAsJsonObject();
				int position = itemJObject.get("position").getAsInt();
				JsonElement itemJsonElement = itemJObject.get("data").getAsJsonObject();
				widgetDataJArray.set(position, itemJsonElement);
			}
			notifyDataSetChanged();
		} catch (Exception e) {
			// e.printStackTrace();
			// LogUtil.e(TAG, "updateViews error: can not parse to json object ...");
		}

	}
	// 初始化时添加一种 viewType
	private void addViewType(String typeName) {
		viewTypeNames.add(typeName);

		Class<?> cellClass = null;
		try {
			if (!typeName.contains("."))
				typeName = this.getClass().getPackage().getName() + "." + typeName;
			// LogUtil.d(TAG, "addViewType : " + typeName);
			cellClass = Class.forName("" + typeName);
			Constructor<?> c = cellClass.getConstructor(new Class[]{String.class, Context.class, ListViewBaseAdapter.class});
			Object cell = c.newInstance(new Object[]{typeName, context, this});
			cells.add((ListViewCellBase) cell);
		} catch (Exception e) {
			// LogUtil.e(TAG, "addViewType error: " + e.toString());
			if (e instanceof InvocationTargetException) {
				// LogUtil.e(TAG, ((InvocationTargetException) e).getTargetException().toString());
			} else {
				// doXXX()
			}
			e.printStackTrace();
		}
	}

	@Override
	public int getItemViewType(int position) {

		final JsonObject itemJson = this.getItem(position);
		for (int i = 0; i < viewTypeNames.size(); i++)
			if (itemJson.get("viewType").getAsString().equals(viewTypeNames.get(i))) {
				// LogUtil.d(TAG, "getItemViewType : " + i);
				return i;
			}
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		// LogUtil.d(TAG, "getViewTypeCount : " + viewTypeNames.size());
		// return viewTypeNames.size();
		return 20;
	}
	@Override
	public int getCount() {
		int count = 0;
		count = widgetDataJArray.size();
		// LogUtil.d(TAG, "getCount:" + count);
		return count;
	}

	@Override
	public JsonObject getItem(int position) {
		// LogUtil.d(TAG, "getItem:" + position);
		return this.getCount() == 0 ? null : getWidgetDataJArray().get(position).getAsJsonObject();
	}

	@Override
	public long getItemId(int position) {
		// LogUtil.d(TAG, "getItemId:" + position);
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// LogUtil.d(TAG, "getView:" + position);
		JsonObject itemJson = this.getItem(position);
		int viewType = getItemViewType(position);
		// LogUtil.d(TAG, position + "-" + getItemViewType(position));
		return cells.get(viewType).getView(position, convertView, parent, itemJson);
	}

	// getters and setters
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}

	public JsonObject getWidgetDataJObject() {
		return widgetDataJObject;
	}
	public void setWidgetDataJObject(JsonObject widgetDataJObject) {
		this.widgetDataJObject = null;
		this.widgetDataJObject = widgetDataJObject;
	}

	public JsonArray getWidgetDataJArray() {
		return widgetDataJArray;
	}
	public void setWidgetDataJArray(JsonArray widgetDataJArray) {
		// this.widgetDataJArray = null;
		this.widgetDataJArray = widgetDataJArray;
	}

	public ListViewBase getListViewBase() {
		return listViewBase;
	}
	public void setListViewBase(ListViewBase listViewBase) {
		this.listViewBase = listViewBase;
	}

	public Map<String, String> getInputMap() {
		return inputMap;
	}
	public void setInputMap(Map<String, String> inputMap) {
		this.inputMap = inputMap;
	}
	public void addInputMap(String key, String value) {
		inputMap.put(key, value);
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {
		// TODO Auto-generated method stub
		// return viewTypeNames.get(viewType).equalsIgnoreCase("ListViewCellFixedTitle");
		// if (widgetDataJObject.get("hasFixedTitle").getAsBoolean())
		// return viewType == 1;
		// else
		// return false;
		// LogUtil.d(TAG, viewTypeNames.get(viewType));
		return false;

	}
}
