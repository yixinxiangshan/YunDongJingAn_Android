package com.ecloudiot.framework.widget;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.event.linterface.OnChangedListener;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.widget.model.NumberPickerModel;
import com.google.gson.JsonObject;
import com.michaelnovakjr.numberpicker.NumberPicker;

import android.annotation.SuppressLint;

@SuppressLint("ViewConstructor")
public class NumberPickerWidget extends BaseWidget {
	private final static String TAG = "NumberPickerWidget";
	private NumberPickerModel widgetDataModel = null;
	private NumberPicker numberPicker = null;

	public NumberPickerWidget(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		this.setId(R.id.number_picker_widget);
		parsingData();
	}

	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		super.parsingWidgetData(widgetDataJObject);
		try {
			widgetDataModel = GsonUtil.fromJson(widgetDataJObject, NumberPickerModel.class);
		} catch (Exception e) {
			LogUtil.e(TAG, "parsingData error: dataString is invalid ...");
		}
	}

	protected void setData() {
		numberPicker = new NumberPicker(this.ctx);
		this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		this.addView(numberPicker, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		if (null != widgetDataModel) {
			if (widgetDataModel.getDefaultNum() >= 0) {
				numberPicker.setCurrent(widgetDataModel.getDefaultNum());
			}
			if (widgetDataModel.getMinNum() >= 0 && widgetDataModel.getMaxNum() > 0
					&& widgetDataModel.getMaxNum() > widgetDataModel.getMinNum()) {
				numberPicker.setRange(widgetDataModel.getMinNum(), widgetDataModel.getMaxNum());
			}
			if (StringUtil.isNotEmpty(widgetDataModel.getmWrap()))
				numberPicker.setWrap(Boolean.parseBoolean(widgetDataModel.getmWrap()));

		}
		super.setData();
	}

	public int getCurrentVal() {
		return numberPicker.getCurrent();
	}

	public NumberPickerModel getDataModel() {
		return widgetDataModel;
	}

	public void setOnChangedListener(OnChangedListener ChangedListener) {
		numberPicker.setOnChangeListener(ChangedListener);
	}
}
