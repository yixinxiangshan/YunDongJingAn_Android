package com.ecloudiot.framework.activity;

import java.util.HashMap;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.event.linterface.OnPageCreatedListener;
import com.ecloudiot.framework.event.linterface.OnPageDestroyListener;
//import com.ecloudiot.framework.event.linterface.OnPageResumeListener;
import com.ecloudiot.framework.event.linterface.OnPageWillInitWidgetsListener;
import com.ecloudiot.framework.fragment.BaseFragment;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.javascript.JsEngine;
import com.ecloudiot.framework.utility.Constants;
import com.ecloudiot.framework.utility.IntentUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.PageUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.ViewUtil;
import com.ecloudiot.framework.widget.model.KeyValueModel;
import com.jakewharton.disklrucache.DiskLruCache;
import com.pgyersdk.crash.PgyCrashManager;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;

/**
 * 空activity，获取页面配置文件name交给页面管理工具（PageUtil），保存传递过来的page级别的参数
 */
@SuppressLint("NewApi")
public class ItemActivity extends BaseActivity {
	private static String TAG = "ItemActivity";
	private Bundle mBundle = null;
	private HashMap<String, String> mMap = new HashMap<String, String>();
	private OnPageCreatedListener pageCreatedListener;
	// private OnPageResumeListener pageResumeListener;
	private OnPageDestroyListener pageDestroyListener;
	// private OnBackKeyDownListener onBackKeyDownListener;
	private OnPageWillInitWidgetsListener pageWillInitWidgetsListener;
	private JsEngine jscript;
	private Boolean waitBeforeCreate = false;
	private String pageString = "";
	private String datasString = "";
	private HashMap<String, String> params = new HashMap<String, String>();
	private DiskLruCache mDiskLruCache;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTheme(R.style.AppThemeDefault);
		setContentView(R.layout.page_layout_default);
		mBundle = getIntent().getExtras();
		String pageName = mBundle.getString("pageName");
		String paramsString = mBundle.getString("paramsString");
		LogUtil.i(TAG, "onCreate : paramsString = " + paramsString);
		if(Constants.INNER_TEST){
			PgyCrashManager.register(this, Constants.PGY_APP_KEY);
		}
		// 打开缓存，第二个参数为版本呢，版本维持100不变，这样缓存不会因为版本变动被清除；缓存最大为50M
		// try {
		// // LogUtil.d(TAG, "DiskLruCache.open success!");
		// setmDiskLruCache(DiskLruCache.open(FileUtil.getDiskCacheDir("nowapp"), 100, 1, 50 * 1024 * 1024));
		// } catch (IOException e) {
		// LogUtil.e(TAG, "DiskLruCache.open error!");
		// e.printStackTrace();
		// }

		PageUtil.putParams(paramsString, this);
		if ("".equals(pageName))
			return;
		PageUtil.initPage(pageName, this);
		setSupportProgressBarIndeterminateVisibility(false);
		if (pageCreatedListener != null) {
			pageCreatedListener.onPageCreated();
		}
		
		
	}

	public JsEngine getJscript() {
		return jscript;
	}

	public void setJscript(JsEngine jscript) {
		this.jscript = jscript;
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// No call for super(). Bug on API Level > 11.
	}

	/**
	 * 保存 键值对 类的数据
	 * 
	 * @param paramString
	 */
	public void putParam(KeyValueModel keyValueModel) {
		// LogUtil.d(TAG, "putParam : key = " + keyValueModel.getKey() +
		// " , value = " + keyValueModel.getValue());
		putParam(keyValueModel.getKey(), keyValueModel.getValue());
	}

	/**
	 * 重新设置布局
	 */
	public void checkContentView() {
		String layout = mMap.get("pageLayout");
		if (StringUtil.isNotEmpty(layout)) {
			setContentView(ResourceUtil.getLayoutIdFromContext(this, layout));
		}
	}

	/**
	 * 保存 键值对 类的数据
	 * 
	 * @param key
	 * @param value
	 */
	public void putParam(String key, String value) {
		// LogUtil.d(TAG, "putParam : key = " + key + " , value = " + value +
		// " ,activity = " + this);
		// if (PageUtil.getNowPageContext() != this &&
		// PageUtil.getNowPageContext() instanceof BaseFragment) {
		// ((BaseFragment) PageUtil.getNowPageContext()).putParam(key, value);
		// return;
		// }
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
		String value = "";
		// LogUtil.d(TAG, "params : " + mMap.toString());
		if (PageUtil.getNowPageContext() != this && PageUtil.getNowPageContext() instanceof BaseFragment) {
			value = ((BaseFragment) PageUtil.getNowPageContext()).getParam(key);
		}
		if (StringUtil.isEmpty(value) && StringUtil.isNotEmpty(mMap.get(key))) {
			value = mMap.get(key);
		} else {
			value = "";
		}
		// LogUtil.d(TAG, "getParam : key = " + key + " , value = " +
		// value.substring(0, value.length() > 20 ? 20 : value.length()) +
		// "...");
		return value;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			// js来处理事件
			HashMap<String, String> eventParams = new HashMap<String, String>();
			eventParams.put("keyName", "back");
			String result = JsAPI.runEvent(this.getJsEvents(), "onKeyDown", new JSONObject(eventParams));
			if (result != null && result.equals("_false"))
				return true;

			// 判断 该页面是否为 引导页
			String isGuide = getParam("isGuide");
			if (StringUtil.isNotEmpty(isGuide) && isGuide.equalsIgnoreCase("true")) {
				ViewUtil.ShowConfirm("退出程序，确认?", "pecct://app/closeApp", "pecct://app/null");
				return true;
			}
			IntentUtil.closeActivity();
			return true;
		}
		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		//蒲公英摇一摇反馈
		if(Constants.INNER_TEST){
			PgyFeedbackShakeManager.register(this, Constants.PGY_APP_KEY);
		}

		if (StringUtil.isNotEmpty(getParam("uriString"))) {
			LogUtil.i(TAG, "onResume : uriString = " + getParam("uriString"));
		} else {
			LogUtil.i(TAG, "onResume error: uriString is null ");
		}
		// 执行js里面的事件
		JsAPI.runEvent(jsEvents, "onResume", "");
	}

	public void beforeInitWidgets() {
		if (this.pageWillInitWidgetsListener != null) {
			pageWillInitWidgetsListener.willInitWidgets();
		}
	}

	@Override
    protected void onPause() {
	    super.onPause();
		//蒲公英摇一摇反馈
		if(Constants.INNER_TEST){
			PgyFeedbackShakeManager.unregister();
		}

    }

	/**
	 * 指定监听器
	 * 
	 * @param pageChangeListener
	 */
	public void setOnPageCreatedListener(OnPageCreatedListener pageCreatedListener) {
		this.pageCreatedListener = pageCreatedListener;
	}

	// public void setOnPageResumeListener(OnPageResumeListener pageResumeListener) {
	// this.pageResumeListener = pageResumeListener;
	// }

	public void setOnPageDestroyListener(OnPageDestroyListener pageDestroyListener) {
		this.pageDestroyListener = pageDestroyListener;
	}

	public void setOnPageWillInitWidgetsListener(OnPageWillInitWidgetsListener pageWillInitWidgetsListener) {
		this.pageWillInitWidgetsListener = pageWillInitWidgetsListener;
	}

	/**
	 * 设置返回键事件
	 */
	// public void setOnBackKeyDownListener(OnBackKeyDownListener
	// backKeyDownListener) {
	// this.onBackKeyDownListener = backKeyDownListener;
	// }

	@Override
	protected void onStop() {
		super.onStop();
		LogUtil.i(TAG, "onStop : ...");
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		LogUtil.i(TAG, "onRestart : ...");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (pageDestroyListener != null) {
			pageDestroyListener.onPageDestory();
		}
		if (jscript != null) {
			jscript.release();
			jscript = null;
		}
		// 关闭缓存
		// try {
		// getmDiskLruCache().close();
		// } catch (IOException e) {
		// LogUtil.e(TAG, "diskLruCache close error!");
		// e.printStackTrace();
		// }
	}

	/**
	 * 清理activity
	 */
	public void clearSelf() {
		mBundle = null;
		mMap = new HashMap<String, String>();
		((ViewGroup) (this.findViewById(android.R.id.content))).removeAllViews();
		setContentView(R.layout.activity_item);
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

	public HashMap<String, String> getParams() {
		return params;
	}

	public void setParams(HashMap<String, String> params) {
		this.params = params;
	}

	public String getDatasString() {
		return datasString;
	}

	public void setDatasString(String datasString) {
		this.datasString = datasString;
	}

	public HashMap<String, String> getmMap() {
		return mMap;
	}

	public void setmMap(HashMap<String, String> mMap) {
		this.mMap = mMap;
	}

	public DiskLruCache getmDiskLruCache() {
		return mDiskLruCache;
	}

	public void setmDiskLruCache(DiskLruCache mDiskLruCache) {
		this.mDiskLruCache = mDiskLruCache;
	}
}
