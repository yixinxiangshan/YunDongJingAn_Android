package com.ecloudiot.framework.event.listener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.Uri;

import com.ecloudiot.framework.event.DoActionEvent;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.WidgetUtil;
import com.ecloudiot.framework.widget.model.BaseWidgetConfigModel.BaseWidgetConfigSetEventModel;

import de.greenrobot.event.EventBus;

public class BaseEventListener {
	private final static String TAG = "BaseEventListener";
	private String eventConfigString = "";
	private String evalJS = "";
	private Object pageContext;
	private Object widget; 

	public BaseEventListener(Object pageContext, Object widget, String eventConfigString) {
		super();
		this.pageContext = pageContext;
		this.widget = widget;
		this.setEventConfigString(eventConfigString);
//		LogUtil.d(TAG, "BaseEventListener : eventConfigString = "+getEventConfigString());
	}

	protected void runJs() {
		setEvalJS(WidgetUtil.getEventJString(getPageContext(), getWidget(), (BaseWidgetConfigSetEventModel) GsonUtil
				.fromJson(getEventConfigString(), BaseWidgetConfigSetEventModel.class)));
//		LogUtil.d(TAG, "runJs : getEvalJS() : "+getEvalJS());
		EventBus.getDefault().post(new DoActionEvent("pecct://app/eval?cmd=" + Uri.encode(getEvalJS())));
	}
	protected void runJs(Object bundle) {
		setEvalJS(WidgetUtil.getEventJString(getPageContext(), getWidget(), (BaseWidgetConfigSetEventModel) GsonUtil
				.fromJson(getEventConfigString(), BaseWidgetConfigSetEventModel.class),bundle));
		EventBus.getDefault().post(new DoActionEvent("pecct://app/eval?cmd=" + Uri.encode(getEvalJS())));
	}
	/**
	 * 匹配相应位置
	 * @param string
	 * @param positionS
	 * @param positionI
	 * @return
	 */
	protected String matchPosition(String string, String positionS,int positionI){
		Pattern pattern = Pattern.compile("\\["+positionS+"\\]");
		Matcher matcher = pattern.matcher(getEventConfigString());
		return matcher.replaceAll("["+positionS+"(" + positionI + ")]");
	}
	/**
	 * 匹配好被使用后，回复配置文件结构，为下次使用做准备
	 * @param string
	 * @param positionI
	 * @param positionS
	 * @return
	 */
	protected String matchPosition(String string,int positionI, String positionS){
		Pattern pattern = Pattern.compile("\\["+positionS+"\\(" + positionI + "\\)\\]");
		Matcher matcher = pattern.matcher(getEventConfigString());
		return matcher.replaceAll("["+positionS+"]");
	}
	public String getEvalJS() {
		return evalJS;
	}

	public void setEvalJS(String evalJS) {
		LogUtil.d(TAG, "setEvalJS : " + evalJS);
		this.evalJS = evalJS;
	}

	public String getEventConfigString() {
		return eventConfigString;
	}

	public void setEventConfigString(String eventConfigString) {
		this.eventConfigString = eventConfigString;
	} 
	public Object getPageContext() {
		return pageContext;
	}

	public void setPageContext(Object pageContext) {
		this.pageContext = pageContext;
	}

	public Object getWidget() {
		return widget;
	}

	public void setWidget(Object widget) {
		this.widget = widget;
	}
	
}
