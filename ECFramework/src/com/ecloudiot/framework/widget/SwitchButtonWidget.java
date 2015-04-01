package com.ecloudiot.framework.widget;


import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageButton;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.event.linterface.OnSwitchButtonChangedListener;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.widget.model.SwitchButtonModel;
import com.google.gson.JsonObject;

@SuppressLint("ViewConstructor")
public class SwitchButtonWidget extends BaseWidget {

	private static String TAG = "SwitchButtonWidget";
	private  SwitchButtonModel buttonModel;
	private ImageButton imageButton;
	private OnSwitchButtonChangedListener switchButtonChangedListenerOn;
	private OnSwitchButtonChangedListener switchButtonChangedListenerOff;
	private int state;
	public SwitchButtonWidget(Object pageContext, String dataString,
			String layoutName) {
		super(pageContext, dataString, layoutName);
		this.setId(R.id.widget_switch_button);
		parsingData();
	}
	
	protected void initViewLayout(String layoutName) {
		super.initViewLayout(layoutName);
	}

	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		super.parsingWidgetData(widgetDataJObject);
		try {
			buttonModel = GsonUtil.fromJson(widgetDataJObject, SwitchButtonModel.class);
		} catch (Exception e) {
			LogUtil.e(TAG, "parsingData error: dataString is invalid..."); 
		}
	}
	
	protected void setData() {
		setContent();
		super.setData();
	}
	
	public ImageButton getImageButton() {
		return imageButton;
	}

	private void setContent(){
		if (null == imageButton) {
			imageButton = new ImageButton(ctx);
			this.addView(imageButton);
		}
		if (null != buttonModel) {
			state = buttonModel.getState();
			if (StringUtil.isNotEmpty(buttonModel.getSwitchOnBackgroud()) && StringUtil.isNotEmpty(buttonModel.getSwitchOffBackgroud())) {
				if(state == 0)
					imageButton.setBackgroundResource(ResourceUtil.getDrawableIdFromContext(ctx, buttonModel.getSwitchOffBackgroud()));
				if(state == 1)
					imageButton.setBackgroundResource(ResourceUtil.getDrawableIdFromContext(ctx, buttonModel.getSwitchOnBackgroud()));
			}
			
			imageButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (null != switchButtonChangedListenerOff && null != switchButtonChangedListenerOn) {
						if (state == 0) {
							switchButtonChangedListenerOn.onChanged(state);
						}else {
							switchButtonChangedListenerOff.onChanged(state);
						}
						state = (state == 0 ? 1 : 0);
						setSwitchButtonBackgroud(state);
					}
					else if (null == switchButtonChangedListenerOff && null != switchButtonChangedListenerOn) {
						state = (state == 0 ? 1 : 0);
						switchButtonChangedListenerOn.onChanged(state);
						setSwitchButtonBackgroud(state);
					}
				}
			});
		}
	}
	
	public SwitchButtonModel getDataModel() {
		return buttonModel;
	}
	
	public void setSwitchButtonBackgroud(int state) {
		switch(state) {
		case 0:
			imageButton.setBackgroundResource(ResourceUtil.getDrawableIdFromContext(ctx, buttonModel.getSwitchOffBackgroud()));
			break;
		case 1:
			imageButton.setBackgroundResource(ResourceUtil.getDrawableIdFromContext(ctx, buttonModel.getSwitchOnBackgroud()));
			break;
		}
	}
	

	public void setOnSwitchButtonChangedListener(OnSwitchButtonChangedListener switchButtonOnListener,String state) {
		if (Integer.parseInt(state) == 0) {
			switchButtonChangedListenerOff = switchButtonOnListener;
		}else if (Integer.parseInt(state) == 1) {
			switchButtonChangedListenerOn = switchButtonOnListener;
		}
	}
	public void setOnSwitchButtonChangedListener(OnSwitchButtonChangedListener switchButtonOnListener) {
			switchButtonChangedListenerOn = switchButtonOnListener;
	}
	
}
