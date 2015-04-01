package com.ecloudiot.framework.fragment;

import java.util.HashMap;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.BaseActivity;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.activity.SplashActivity;
import com.ecloudiot.framework.appliction.BaseApplication;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.javascript.JsEngine;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.PageUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.widget.model.KeyValueModel;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseFragment extends Fragment {
	private final static String TAG = "BaseFragment";
	private HashMap<String, String> mMap = new HashMap<String, String>();
	private String pageName;
	private JsEngine jscript;
	private Boolean noActionBar = false;
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		LogUtil.i(TAG, "onAttach ..." + activity);
	}

	public JsEngine getJscript() {
		return jscript;
	}

	public void setJscript(JsEngine jscript) {
		this.jscript = jscript;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtil.i(TAG, "onCreate ...");
		try {
			pageName = getArguments().getString("dataString");
			LogUtil.i(TAG, "onCreate : pageName = " + pageName);
		} catch (Exception e) {
			LogUtil.e(TAG, "onCreate error:  start with no bundle String ...");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LogUtil.i(TAG, "onCreateView ...");
		ECApplication.getInstance().setOnFragmentCreateView(this);
		View v = inflater.inflate(R.layout.gen_blank, container, false);
		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		if (getView() == view || (view != null && view.equals(getView()))) {
			// LogUtil.i(TAG, "they are  equal");
			// LogUtil.v(TAG, "getview = " + getView());
			// LogUtil.v(TAG, "view = " + view);
		} else {
			LogUtil.i(TAG, "getview = " + getView() + " , view = " + view);
		}
		LogUtil.i(TAG, "onViewCreated ...");
		if (StringUtil.isNotEmpty(pageName)) {
			PageUtil.initPage(pageName, this);
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		LogUtil.i(TAG, "onActivityCreated ...");
	}

	@Override
	public void onResume() {
		super.onResume();
		LogUtil.i(TAG, "onResume ...");
		ECApplication.getInstance().setOnFragmentResume(this);
		// HashMap<String, String> eventParams = new HashMap<String, String>();
		// String pageId = getParam("page_id");
		// if (StringUtil.isNotEmpty(pageId)) {
		// eventParams.put("pageId", pageId);
		// // JsManager.getInstance().callJsMethodSync(pageId, "onPageResume", eventParams);
		// } else {
		// LogUtil.e(TAG, "onResume error: pageId is null ...");
		// }
	}

	/**
	 * 重新设置布局
	 */
	public void checkContentView() {
		String layout = mMap.get("pageLayout");
		if (StringUtil.isNotEmpty(layout)) {
			View view = LayoutInflater.from(getActivity()).inflate(ResourceUtil.getLayoutIdFromContext(getActivity(), layout), null);
			((ViewGroup) getView()).removeAllViews();
			((ViewGroup) getView()).addView(view);
		}
	}

	public void beforeInitWidgets() {
		// if (this.pageWillInitWidgetsListener != null) {
		// pageWillInitWidgetsListener.willInitWidgets();
		// }
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtil.i(TAG, "onPause ...");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		LogUtil.i(TAG, "onDestroyView ... ");
		// 当前fragment view被销毁后，需要吧js、page从属关系 也都取消掉
		if (jscript != null) {
			jscript.release();
			jscript = null;
		}
		//测试
		if(!(BaseApplication.getInstance().getNowActivity() instanceof SplashActivity)){
			((ItemActivity) BaseApplication.getInstance().getNowActivity()).removeFragment(this.getParam("page_id"));
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		LogUtil.i(TAG, "onDetach : start ..");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (jscript != null) {
			jscript.release();
			jscript = null;
		}
		// 处理、记录fragment与当前page的父子关系，用户actionbar事件，查找遍历子fragment里面的事件
		BaseActivity nowActivity = (BaseActivity) BaseApplication.getInstance().getNowActivity();
		nowActivity.removeFragment(getParam("page_id"));

	}

	/**
	 * 保存 键值对 类的数据
	 * 
	 * @param paramString
	 */
	public void putParam(KeyValueModel keyValueModel) {
		// LogUtil.d(TAG,
		// "putParam : key = "+keyValueModel.getKey()+" , value = "+keyValueModel.getValue());
		mMap.put(keyValueModel.getKey(), keyValueModel.getValue());
	}

	/**
	 * 保存 键值对 类的数据
	 * 
	 * @param key
	 * @param value
	 */
	public void putParam(String key, String value) {
		// LogUtil.d(TAG, "putParam : key = " + key + " , value = " + value);
		if (StringUtil.isNotEmpty(key) && StringUtil.isNotEmpty(value)) {
			mMap.put(key, value);
		} else if (StringUtil.isNotEmpty(key)) {
			mMap.remove(key);
		}
	}

	/**
	 * 获取键值对的数据
	 * 
	 * @param key
	 * @return
	 */
	public String getParam(String key) {
		// LogUtil.d(TAG, "params : "+mMap.toString());
		// LogUtil.d(TAG, "params : "+key);
		// LogUtil.d(TAG, "params : "+mMap.get(key));

		if (StringUtil.isNotEmpty(mMap.get(key))) {
			return mMap.get(key);
		} else {
			return "";
		}
	}


	/**
	 * 清理fragment
	 */
	public void clearSelf() {
		mMap = new HashMap<String, String>();;
		((ViewGroup) (this.getView())).removeAllViews();
	}

	public Boolean getNoActionBar() {
		return noActionBar;
	}

	public void setNoActionBar(Boolean noActionBar) {
		this.noActionBar = noActionBar;
	}

	public HashMap<String, String> getmMap() {
		return mMap;
	}

	public void setmMap(HashMap<String, String> mMap) {
		this.mMap = mMap;
	}
}
