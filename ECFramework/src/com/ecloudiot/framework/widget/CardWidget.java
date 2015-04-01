package com.ecloudiot.framework.widget;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.event.linterface.OnButtonClickListener;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.widget.model.CardModel;
import com.google.gson.JsonObject;

@SuppressLint("ViewConstructor")
public class CardWidget extends BaseWidget {
	private static String TAG = "CardWidget";
	private CardModel widgetDataModel;
	private LinearLayout headerLLayout;
	private LinearLayout ContentLLayout;
	private LinearLayout footLLayout;
	private View separationHeader;
	private View separationRoot;
	private TextView titleTextView;
	private TextView subtitleTextView;
	private Button bt;
	private TextView footTextView1;
	private TextView footTextView2;
	private TextView footTextView3;
	private TextView footTextView4;

	public CardWidget(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		this.setId(R.id.card_widget);
		parsingData();
	}

	protected void initViewLayout(String layoutName) {
		if (StringUtil.isNotEmpty(layoutName)) {
			initBaseView(layoutName);
			return;
		}
		initBaseView("widget_card_default");
	}

	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		super.parsingWidgetData(widgetDataJObject);
		try {
			widgetDataModel = GsonUtil.fromJson(widgetDataJObject, CardModel.class);
		} catch (Exception e) {
			LogUtil.e(TAG, "parsingData error: dataString is invalid ...");
		}
	}

	private void setContent() {
		LogUtil.d(TAG, "setContent...");
		headerLLayout = (LinearLayout) getBaseView().findViewById(R.id.widget_card_llayout_header_llayout);
		ContentLLayout = (LinearLayout) getBaseView().findViewById(R.id.widget_card_llayout_content_llayout);
		footLLayout = (LinearLayout) getBaseView().findViewById(R.id.widget_card_llayout_foot_llayout);
		separationHeader = (View) getBaseView().findViewById(R.id.widget_card_separate_header);
		separationRoot = (View) getBaseView().findViewById(R.id.widget_card_separate_root);
		titleTextView = (TextView) getBaseView().findViewById(R.id.widget_card_header_title);
		subtitleTextView = (TextView) getBaseView().findViewById(R.id.widget_card_header_subtitle);
		bt = (Button) getBaseView().findViewById(R.id.widget_card_header_bt);
		footTextView1 = (TextView) getBaseView().findViewById(R.id.widget_card_foot_text1);
		footTextView2 = (TextView) getBaseView().findViewById(R.id.widget_card_foot_text2);
		footTextView3 = (TextView) getBaseView().findViewById(R.id.widget_card_foot_text3);
		footTextView4 = (TextView) getBaseView().findViewById(R.id.widget_card_foot_text4);
	}

	/**
	 * 设置 数据到控件
	 */
	protected void setData() {
		LogUtil.d(TAG, "setData...");
		setContent();
		if (null != widgetDataModel && widgetDataModel.isWithHeader()) {
			headerLLayout.setVisibility(View.VISIBLE);
			if (StringUtil.isNotEmpty(widgetDataModel.getTitle())) {
				titleTextView.setText(widgetDataModel.getTitle());
			}
			if (StringUtil.isNotEmpty(widgetDataModel.getSubtitle())) {
				subtitleTextView.setText(widgetDataModel.getSubtitle());
			}
			if (StringUtil.isNotEmpty(widgetDataModel.getButtonBg())) {
				try {
					bt.setBackgroundResource(ResourceUtil.getDrawableIdFromContext(ctx, widgetDataModel.getButtonBg()));
				} catch (Exception e) {
					LogUtil.e(TAG, "setData error: bt bg resource is invald ...");
				}
			}
		} else {
			headerLLayout.setVisibility(View.GONE);
			separationHeader.setVisibility(View.GONE);
		}

		if (null != widgetDataModel && widgetDataModel.isWithFoot()) {
			if (StringUtil.isNotEmpty(widgetDataModel.getFootText1())) {
				footTextView1.setVisibility(View.VISIBLE);
				footTextView1.setText(widgetDataModel.getFootText1());
			} else {
				footTextView1.setVisibility(View.GONE);
			}
			if (StringUtil.isNotEmpty(widgetDataModel.getFootText2())) {
				footTextView2.setVisibility(View.VISIBLE);
				footTextView2.setText(widgetDataModel.getFootText2());
			} else {
				footTextView2.setVisibility(View.GONE);
			}
			if (StringUtil.isNotEmpty(widgetDataModel.getFootText3())) {
				footTextView3.setVisibility(View.VISIBLE);
				footTextView3.setText(widgetDataModel.getFootText3());
			} else {
				footTextView3.setVisibility(View.GONE);
			}
			if (StringUtil.isNotEmpty(widgetDataModel.getFootText4())) {
				footTextView4.setVisibility(View.VISIBLE);
				footTextView4.setText(widgetDataModel.getFootText4());
			} else {
				footTextView4.setVisibility(View.GONE);
			}
		} else {
			footLLayout.setVisibility(View.GONE);
			separationRoot.setVisibility(View.GONE);
		}
		if (null != widgetDataModel && widgetDataModel.isWithFoot()) {
			ContentLLayout.setVisibility(View.VISIBLE);
		} else {
			if (null != widgetDataModel && widgetDataModel.isWithFoot() && widgetDataModel.isWithFoot()) {
				ContentLLayout.setVisibility(View.GONE);
				separationRoot.setVisibility(View.GONE);
			}
		}
		super.setData();
	}

	public CardModel getDataModel() {
		return widgetDataModel;
	}

	class ClickListener implements OnClickListener {
		OnButtonClickListener buttonClickListener;

		public ClickListener(OnButtonClickListener buttonClickListener) {
			this.buttonClickListener = buttonClickListener;
		}

		@Override
		public void onClick(View v) {
			buttonClickListener.onClick(v);

		}

	}

	public void setOnButtonClickListener(final OnButtonClickListener buttonClickListener) {
		bt.setOnClickListener(new ClickListener(buttonClickListener));
	}

//	public void setOnButtonClickListener(final ButtonClickListener buttonClickListener, String viewId) {
//		Button bt = (Button) findViewById(ResourceUtil.getViewIdFromContext(ctx, viewId));
//		bt.setOnClickListener(new ClickListener(buttonClickListener));
//	}

	@Override
	public void refreshData(String moreData) {
		super.refreshData(moreData);
		parsingWidgetData(moreData);
		setData();
	}

}
