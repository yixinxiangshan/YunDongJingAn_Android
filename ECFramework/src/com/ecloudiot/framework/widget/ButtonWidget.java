package com.ecloudiot.framework.widget;

import java.util.HashMap;

import com.ecloudiot.framework.R;
import android.annotation.SuppressLint;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecloudiot.framework.event.linterface.OnButtonClickListener;
import com.ecloudiot.framework.event.linterface.OnButtonLongClickListener;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.widget.model.ECButtonModel;
import com.google.gson.JsonObject;

@SuppressLint("ViewConstructor")
public class ButtonWidget extends BaseWidget {
	private final static String TAG = "ECButtonWidget";
	private ECButtonModel buttonModel;
	// private Button button = null;
	private TextView textView;
	private LinearLayout layout;

	public ButtonWidget(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		this.setId(R.id.ec_button_widget);
		parsingData();
	}

	@Override
	protected void initViewLayout(String layoutName) {
		super.initViewLayout(layoutName);
		if (StringUtil.isNotEmpty(layoutName)) {
			initBaseView(layoutName);
		} else {
			initBaseView("widget_button_default");
		}
	}
	
	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		super.parsingWidgetData(widgetDataJObject);
		try {
			buttonModel = GsonUtil.fromJson(widgetDataJObject, ECButtonModel.class);
		} catch (Exception e) {
			LogUtil.e(TAG, "parsingData error: dataString is invalid...");
		}
	}

	protected void setData() {
		setContent();
		super.setData();
	}

	private void setContent() {
		textView = (TextView) findViewById(R.id.widget_button_textview);
		layout = (LinearLayout)findViewById(R.id.widget_button_layout);
		if (null != buttonModel) {
			if (StringUtil.isNotEmpty(buttonModel.getBackground())) {
				try {
					layout.setBackgroundResource(ResourceUtil.getDrawableIdFromContext(ctx, buttonModel.getBackground()));
				} catch (Exception e) {
					LogUtil.e(TAG, "setContent error: backgroud resource isinvalid... ");
				}
			}
			if (StringUtil.isNotEmpty(buttonModel.getClickable())) {
				LogUtil.d(TAG, "getClickable = " + buttonModel.getClickable());
				boolean clickable = true;
				try {
					clickable = Boolean.parseBoolean(buttonModel.getClickable());
				} catch (Exception e) {
					LogUtil.e(TAG, "setContent error: clickable is not valid...");
				}
				layout.setClickable(clickable);
				layout.setEnabled(clickable);
				layout.setOnClickListener(new ClickListener());
			}

			if (StringUtil.isNotEmpty(buttonModel.getText())) {
				textView.setText(buttonModel.getText());
			}
		}
	}
	public ECButtonModel getDataModel() {
		return buttonModel;
	}

	class ClickListener implements OnClickListener {
		OnClickListener clickListener;

		public ClickListener() {
		}

		public ClickListener(OnClickListener clickListener) {
			this.clickListener = clickListener;
		}

		@Override
		public void onClick(View v) {
			HashMap<String, String> eventParams = new HashMap<String, String>();
			eventParams.put("viewId", ctx.getResources().getResourceEntryName(v.getId()));
			eventParams.put("controlId", getControlId());
			if (clickListener != null) {
				clickListener.onClick(v);
			}
		}

	}

	class LongClickListener implements OnLongClickListener {
		OnButtonLongClickListener buttonLongClickListener;

		public LongClickListener(OnButtonLongClickListener buttonLongClickListener) {
			this.buttonLongClickListener = buttonLongClickListener;
		}

		@Override
		public boolean onLongClick(View v) {
			buttonLongClickListener.onLongClick(v);
			return false;
		}

	}

	public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
		layout.setOnClickListener(new ClickListener(onButtonClickListener));
	}

	public void setOnButtonLongClickListener(OnButtonLongClickListener onButtonLongClickListener) {
		layout.setOnLongClickListener(new LongClickListener(onButtonLongClickListener));
	}

}
