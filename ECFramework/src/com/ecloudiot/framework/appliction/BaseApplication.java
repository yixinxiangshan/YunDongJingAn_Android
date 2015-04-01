package com.ecloudiot.framework.appliction;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap.CompressFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.ecloudiot.framework.event.DoActionEvent;
import com.ecloudiot.framework.event.EAction;
import com.ecloudiot.framework.fragment.BaseFragment;
import com.ecloudiot.framework.utility.FileUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.jakewharton.disklrucache.DiskLruCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import de.greenrobot.event.EventBus;

public class BaseApplication extends Application {
	private static BaseApplication instance;
	private final String TAG = "BaseApplication";
	public final static String CONFIG = "config";
	private ArrayList<Activity> activityList;
	private Activity nowActivity= null;
	private Context serviceContext = null;
	private Object nowPageContext = null;
	private HashMap<String, String> mMap;
	private DiskLruCache mDiskLruCache;
	public void onCreate() {
		super.onCreate();
		instance = this;
		this.InitImageLoader();
		EventBus.getDefault().register(this);
		LogUtil.i(TAG, "application onCreate");

		// 打开缓存，第二个参数为版本呢，版本维持100不变，这样缓存不会因为版本变动被清除；缓存最大为50M
		try {
			// LogUtil.d(TAG, "DiskLruCache.open success!");
			setmDiskLruCache(DiskLruCache.open(FileUtil.getDiskCacheDir("nowapp"), 100, 1, 50 * 1024 * 1024));
		} catch (IOException e) {
			LogUtil.e(TAG, "DiskLruCache.open error!");
			e.printStackTrace();
		}

	}
	/**
	 * activity 创建
	 * 
	 * @param activity
	 */
	public void setOnActivityCreate(Activity activity) {
		// LogUtil.i(TAG, "setOnActivityCreate : activity = " + activity);
		if (null == activityList) {
			activityList = new ArrayList<Activity>();
		}
		activityList.add(0, activity);
		setNowActivity(activity);
		setNowPageContext(activity);
	}

	/**
	 * activity 销毁
	 * 
	 * @param activity
	 */
	public void setOnActivityDestroy(Activity activity) {
		LogUtil.i(TAG, "setOnActivityDestroy : activity = " + activity + " count = " + getActivitysCount());
		if (null != activityList && activityList.size() > 0) {
			activityList.remove(activity);
			setNowActivity();
		}
	}

	/**
	 * activity Resume
	 * 
	 * @param activity
	 */
	public void setOnActivityResume(Activity activity) {
		setNowActivity(activity);
		setNowPageContext(activity);

	}

	/**
	 * fragment CreateView
	 * 
	 * @param fragment
	 */
	public void setOnFragmentCreateView(Fragment fragment) {
		// tabwidget切换fragment的时候，会自动load fragment，这时候，设置当前fragment的时候需要在tabwidget中设置
		if (!((BaseFragment) fragment).getNoActionBar()) {
			LogUtil.i(TAG, "setOnFragmentCreateView : set");
			setNowPageContext(fragment);
		} else {
			LogUtil.i(TAG, "setOnFragmentCreateView : not set");
		}
	}

	/**
	 * fragment Resume
	 * 
	 * @param fragment
	 */
	public void setOnFragmentResume(Fragment fragment) {
		setNowPageContext(fragment);
	}

	/**
	 * 设置当前的activity
	 */
	private void setNowActivity() {
		if (null != activityList && activityList.size() > 0) {
			nowActivity = activityList.get(0);
			// LogUtil.d(TAG, "setNowActivity: " +
			// nowActivity.getComponentName()+" activity count : "+activityList.size());
		}
	}

	/**
	 * 设置当前的activity
	 */
	private void setNowActivity(Activity activity) {
		nowActivity = activity;
	}

	/**
	 * 获取当前activity
	 * 
	 * @return
	 */
	public Activity getNowActivity() {
		return this.nowActivity;
	}

	/**
	 * 获取上一个activity
	 * 
	 * @return
	 */
	public Activity getPreActivity() {
		if (activityList.size() == 1) {
			return null;
		}
		return activityList.get(activityList.size() - 2);
	}

	/**
	 * 获取所有activity
	 * 
	 * @return Ohmer-Dec 19, 2013 9:32:21 AM
	 */
	public ArrayList<Activity> getActivityList() {
		return activityList;
	}

	/**
	 * 返回 activity 总数
	 * 
	 * @return
	 */
	public int getActivitysCount() {
		return activityList.size();
	}

	// 建议在您app的退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	/**
	 * 事件相应
	 * 
	 * @param event
	 */
	public void onEventMainThread(DoActionEvent event) {
		LogUtil.i(TAG, "onEventMainThread:" + event.getActionType().toString());
		// 处理发送给js的事件
		if (event.getActionType() == EAction.VIEWSTATICMETHOD) {
			Map<String, String> methodInfo = event.getMethodInfo();
			try {
				Class<?> objclass = Class.forName(methodInfo.get("className"));
				Object obj = objclass.newInstance();
				if (null != methodInfo.get("param3")) {
					LogUtil.d(TAG, "onEventMainThread :3p - className:" + methodInfo.get("className") + "; methodName：" + methodInfo.get("methodName")
					        + "; param1：" + methodInfo.get("param1") + "; param2：" + methodInfo.get("param2") + "; param3：" + methodInfo.get("param3"));
					Method method = obj.getClass().getMethod(methodInfo.get("methodName"), String.class, String.class, String.class);
					method.invoke(obj, methodInfo.get("param1").toString(), methodInfo.get("param2").toString(), methodInfo.get("param3").toString());
				} else if (null != methodInfo.get("param2")) {
					LogUtil.d(TAG, "onEventMainThread :2p");
					Method method = obj.getClass().getMethod(methodInfo.get("methodName"), String.class, String.class);
					method.invoke(obj, methodInfo.get("param1"), methodInfo.get("param2"));
				} else if (null != methodInfo.get("param1")) {
					LogUtil.d(TAG, "onEventMainThread :1p");
					Method method = obj.getClass().getMethod(methodInfo.get("methodName"), String.class);
					method.invoke(obj, methodInfo.get("param1"));
				} else {
					LogUtil.d(TAG, "onEventMainThread :0p");
					Method method = obj.getClass().getMethod(methodInfo.get("methodName"));
					method.invoke(obj);
				}
			} catch (IllegalArgumentException e) {
				LogUtil.e(TAG, "IllegalArgumentException:" + e.toString());
			} catch (SecurityException e) {
				LogUtil.e(TAG, "SecurityException:" + e.toString());
			} catch (InstantiationException e) {
				LogUtil.e(TAG, "InstantiationException:" + e.toString());
			} catch (IllegalAccessException e) {
				LogUtil.e(TAG, "IllegalAccessException:" + e.toString());
			} catch (InvocationTargetException e) {
				LogUtil.e(TAG, "InvocationTargetException:" + e.toString());
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				LogUtil.e(TAG, "ClassNotFoundException:" + e.toString());
			} catch (Exception e) {
				LogUtil.e(TAG, "Exception:" + e.toString());
			}
		}
		LogUtil.d(TAG, "onDoActionEvent：" + event.getActionType());
	}

	private void InitImageLoader() {

		@SuppressWarnings("deprecation")
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		// .showStubImage(R.drawable.ic_stub)
		// .showImageForEmptyUri(R.drawable.ic_empty)
		// .showImageOnFail(R.drawable.ic_error)
		// .resetViewBeforeLoading()
		        .delayBeforeLoading(1)
		        // .cacheInMemory()
		        .cacheOnDisc()
		        // .preProcessor(...)
		        // .postProcessor(...)
		        // .extraForDownloader(...)
		        // .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) //
		        // default
		        // .bitmapConfig(Bitmap.Config.ARGB_8888) // default
		        // .displayer(new SimpleBitmapDisplayer()) // default
		        .build();

		// File cacheDir =
		// StorageUtils.getCacheDirectory(this.getApplicationContext());
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this.getApplicationContext()) //
		        .memoryCacheExtraOptions(1080, 1920) // default = device screen
		                                             // dimensions
		        .discCacheExtraOptions(1080, 1920, CompressFormat.JPEG, 85, null)
		        // .taskExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
		        // .taskExecutorForCachedImages(AsyncTask.THREAD_POOL_EXECUTOR)
		        .threadPoolSize(3) // default
		        // .threadPriority(Thread.NORM_PRIORITY - 1) // default
		        // .tasksProcessingOrder(QueueProcessingType.FIFO) // default
		        .denyCacheImageMultipleSizesInMemory()
		        // .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 *1024))
		        // // default
		        .memoryCacheSize(2 * 1024 * 1024)
		        // .discCache(new UnlimitedDiscCache(cacheDir)) // default
		        .discCacheSize(50 * 1024 * 1024).discCacheFileCount(200)
		        // .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
		        // // default
		        // .imageDownloader(new
		        // BaseImageDownloader(this.getApplicationContext())) // default
		        .defaultDisplayImageOptions(options)
		        // // default
		        // .enableLogging()
		        .build();
		ImageLoader.getInstance().init(config);

	}

	public static BaseApplication getInstance() {
		return instance;
	}

	/**
	 * 编辑配置参数
	 * 
	 * @param key
	 *            配置参数的key
	 * @param value
	 *            配置参数的值
	 */
	public void editConfig(String key, String value) {
		if (StringUtil.isEmpty(key)) {
			return;
		}
		SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(CONFIG, MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if (StringUtil.isEmpty(value)) {
			editor.remove(key);
		} else {
			editor.putString(key, value);
		}
		editor.commit();
	}

	/**
	 * 读取配置参数值
	 * 
	 * @param key
	 *            配置参数的key
	 * @return
	 */
	public String readConfig(String key) {
		SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(CONFIG, MODE_PRIVATE);
		String orderId = sharedPreferences.getString(key, "");
		// LogUtil.d(TAG, "readConfig : key = " + key + " , value = " +
		// sharedPreferences.getString(key, "default"));
		return orderId;
	}

	public String getDeviceId() {
		return StringUtil.getDeviceId();
	}

	public void exit() {
		try {
			for (Activity activity : activityList) {
				try {
					if (activity != null)
						activity.finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.exit(0);
		}
	}

	/**
	 * 一组key-value进行保存
	 * 
	 * @param params
	 */
	public void saveAccessParams(Map<String, String> params) {
		if (params != null && params.size() > 0) {
			String key = "";
			Iterator<String> iterator = params.keySet().iterator();
			while (iterator.hasNext()) {
				key = iterator.next().toString();
				BaseApplication.getInstance().editConfig(key, params.get(key));
			}
		}
	}

	/**
	 * 单个值保存
	 * 
	 * @param key
	 * @param values
	 */
	public void saveAccessParams(String key, String values) {
		BaseApplication.getInstance().editConfig(key, values);
	}

	public NetworkInfo getUsableNetworkInfo(ConnectivityManager ConManager) {
		NetworkInfo[] infos = ConManager.getAllNetworkInfo();
		NetworkInfo info = null;
		if (infos != null) {
			for (int i = 0; i < infos.length; i++) {
				if (infos[i].isConnected()) {
					return info;
				}
			}
		}
		return null;
	}

	public void putParam(String key, String value) {
		if (StringUtil.isEmpty(key) || StringUtil.isEmpty(value)) {
			return;
		}
		if (mMap == null) {
			mMap = new HashMap<String, String>();
		}
		mMap.put(key, value);
	}

	public String getParam(String key) {
		if (mMap == null) {
			return "";
		}
		return StringUtil.isEmpty(key) ? "" : mMap.get(key);
	}

	public Context getServiceContext() {
		return serviceContext;
	}

	public void setServiceContext(Context serviceContext) {
		this.serviceContext = serviceContext;
	}

	public Object getNowPageContext() {
		return nowPageContext;
	}

	public void setNowPageContext(Object nowPageContext) {
		this.nowPageContext = nowPageContext;
	}
	public DiskLruCache getmDiskLruCache() {
		return mDiskLruCache;
	}
	public void setmDiskLruCache(DiskLruCache mDiskLruCache) {
		this.mDiskLruCache = mDiskLruCache;
	}

}
