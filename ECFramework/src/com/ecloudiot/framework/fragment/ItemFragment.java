package com.ecloudiot.framework.fragment;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecloudiot.framework.event.linterface.OnPageCreatedListener;
import com.ecloudiot.framework.event.linterface.OnPageDestroyListener;
import com.ecloudiot.framework.event.linterface.OnPageResumeListener;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.utility.LogUtil;
import com.umeng.analytics.MobclickAgent;

public class ItemFragment extends BaseFragment {
	private final static String TAG = "ItemFragment";
	private OnPageCreatedListener pageCreatedListener;
	private OnPageResumeListener pageResumeListener;
	private OnPageDestroyListener pageDestoryListener;
	private Boolean waitBeforeCreate = false;;
	private String pageString = "";
	private String datasString = "";
	private HashMap<String, String> jsEvents = new HashMap<String, String>();
	private HashMap<String, String> widgetDatas = new HashMap<String, String>();
	private HashMap<String, HashMap<String, String>> widgetJsEvents = new HashMap<String, HashMap<String, String>>();

	public static ItemFragment newInstance(String pageName) {
		ItemFragment itemFragment = new ItemFragment();
		Bundle args = new Bundle();
		args.putString("dataString", pageName);

		itemFragment.setArguments(args);
		return itemFragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (pageCreatedListener != null) {
			pageCreatedListener.onPageCreated();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getPageString()); // 统计页面
		if (pageResumeListener != null) {
			pageResumeListener.onPageResume();
		}
		// 执行js里面的事件
		JsAPI.runEvent(jsEvents, "onResume", "");
	}
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getPageString());
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	/**
	 * 指定监听器
	 * 
	 * @param pageChangeListener
	 */
	public void setOnPageCreatedListener(OnPageCreatedListener pageCreatedListener) {
		this.pageCreatedListener = pageCreatedListener;
	}

	public void setOnPageResumeListener(OnPageResumeListener pageResumeListener) {
		this.pageResumeListener = pageResumeListener;
	}

	public void setOnPageDestoryListener(OnPageDestroyListener pageDestoryListener) {
		this.pageDestoryListener = pageDestoryListener;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (pageDestoryListener != null) {
			pageDestoryListener.onPageDestory();
		}
	}

	public Boolean getWaitBeforeCreate() {
		return waitBeforeCreate;
	}

	public void setWaitBeforeCreate(Boolean waitBeforeCreate) {
		this.waitBeforeCreate = waitBeforeCreate;
	}

	public String getPageString() {
		return pageString;
	}

	public void setPageString(String pageString) {
		this.pageString = pageString;
	}

	public String getDatasString() {
		return datasString;
	}

	public void setDatasString(String datasString) {
		this.datasString = datasString;
	}

	public HashMap<String, String> addJsEvent(String hash, String type) {
		jsEvents.put(hash, type);
		return jsEvents;
	}

	public HashMap<String, String> getJsEvents() {
		return jsEvents;
	}

	public void removeJsEvent(String hash) {
		jsEvents.remove(hash);
	}

	public void removeJsEvents() {
		jsEvents = new HashMap<String, String>();
	}

	public void removeWidgetData(String key) {
		widgetDatas.remove(key);
	}

	public void addWidgetData(String key, String data) {
		widgetDatas.put(key, data);
	}

	public HashMap<String, String> getWidgetDatas() {
		return widgetDatas;
	}

	public String getWidgetData(String key) {
		return widgetDatas.get(key);
	}

	/**
	 * widgetJsEvents 操作
	 * 
	 * @param widgetName
	 * @param hash
	 * @param type
	 */
	public void addWidgetJsEvents(String widgetName, String hash, String type) {
		// LogUtil.d(TAG, "addWidgetJsEvents :" + widgetName + "("+this.hashCode()+") - " + hash + " - " + type + " - base:" + widgetJsEvents.toString());
		try {
			HashMap<String, String> nowJsEvent = widgetJsEvents.get(widgetName);
			if (nowJsEvent == null) {
				nowJsEvent = new HashMap<String, String>();
				nowJsEvent.put(hash, type);
			} else {
				nowJsEvent.put(hash, type);
			}
			widgetJsEvents.put(widgetName, nowJsEvent);
			// LogUtil.d(TAG, "addWidgetJsEvents result: " +
			// widgetJsEvents.toString());
		} catch (Exception e) {
			LogUtil.e(TAG, "addWidgetJsEvents error: " + e.toString());
			e.printStackTrace();
		}

	}

	public HashMap<String, HashMap<String, String>> getWidgetJsEvents() {
		return widgetJsEvents;
	}

	public HashMap<String, String> getWidgetJsEvent(String widgetName) {
		return widgetJsEvents.get(widgetName);
	}

	public void removeWidgetJsEvent(String widgetName) {
		widgetJsEvents.remove(widgetName);
	}

	public void removeWidgetJsEvents() {
		widgetJsEvents = new HashMap<String, HashMap<String, String>>();
	}
}
