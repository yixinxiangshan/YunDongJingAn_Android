package com.ecloudiot.framework.utility;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.CameraActivity;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.activity.VideoActivity;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.javascript.JsUtility;
import com.ecloudiot.framework.javascript.JsViewUtility;
import com.google.zxing.client.android.CaptureActivity;

public class IntentUtil {
	private final static String TAG = "IntentUtil";

	/**
	 * 打开新的activity
	 * 
	 * @param className
	 * @param pageName
	 * @param params
	 */
	public static void openActivity(String className, String pageName, String params) {
		Intent i = getNewActivity(className, pageName, params);
		if (null != i) {
			getActivity().startActivityForResult(i, Constants.NEWITEMACTIVITY);
			getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
	}

	/**
	 * 打开新的activity,并关闭之前的activity
	 * 
	 * @param classNamet
	 * @param pageName
	 * @param params
	 */
	public static void openActivityWithFinished(String className, String pageName, String params) {
		Intent i = getNewActivity(className, pageName, params);
		if (null != i) {
			Activity preActivity = getActivity();
			// preActivity.startActivityForResult(i, Constants.NEWITEMACTIVITY);
			preActivity.startActivity(i);
			preActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			preActivity.finish();
		}
	}

	/**
	 * 打开新的activity,并关闭之前所有的activity
	 * 
	 * @param className
	 * @param pageName
	 * @param params
	 *            Ohmer-Dec 19, 2013 9:34:20 AM
	 */
	public static void openActivityFinishedOthers(String className, String pageName, String params) {
		Intent i = getNewActivity(className, pageName, params);
		if (null != i) {
			ArrayList<Activity> activities = ECApplication.getInstance().getActivityList();
			Activity preActivity = getActivity();
			preActivity.startActivity(i);
			preActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			for (Activity activity : activities) {
				activity.finish();
			}
		}
	}

	/**
	 * 根据参数准备好即将启动activity
	 * 
	 * @param className
	 * @param pageName
	 * @param params
	 * @return
	 */
	public static Intent getNewActivity(String className, String pageName, String params) {
		Intent i = null;
		if (StringUtil.isEmpty(className)) {
			i = new Intent(getActivity(), ItemActivity.class);
		} else {
			try {
				i = new Intent(JsUtility.GetActivityContext(), Class.forName(className));
			} catch (ClassNotFoundException e) {
				LogUtil.e(TAG, "getNewActivity : className is not activity name or without package name or ... " + e.toString());
				e.printStackTrace();
			}
		}
		Bundle mBundle = new Bundle();
		if (StringUtil.isNotEmpty(pageName)) { // 当页包含数据集合的界面
			mBundle.putString("pageName", pageName);
		} else {
			LogUtil.w(TAG, "openActivity error: pageName is null");
		}
		if (StringUtil.isNotEmpty(params)) {
			mBundle.putString("paramsString", params);
		} else {
			LogUtil.w(TAG, "openActivity error: paramsString is null");
		}

		i.putExtras(mBundle);
		// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
		// Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
		return i;
	}

	/**
	 * 关闭 当前 activity
	 */
	public static void closeActivity() {
		closeActivity("");
	}

	public static void closeActivity(String uri) {
		// LogUtil.d(TAG, "closeActivity : uri = " + uri);
		if (StringUtil.isNotEmpty(uri) && uri != "undefined") {
			Intent intent = new Intent();
			intent.putExtra("uriString", uri);
			getActivity().setResult(Activity.RESULT_OK, intent);
		}
		// LogUtil.d(TAG, "closeActivity count  : " + ECApplication.getInstance().getActivitysCount());
		getActivity().finish();
		// LogUtil.d(TAG, "closeActivity count  : " + ECApplication.getInstance().getActivitysCount());
		getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		return;
	}

	/**
	 * 发送 email
	 * 
	 * @param title
	 * @param mail
	 */
	public static void sendEmail(String title, String mail) {

		// LogUtil.d(TAG, "sendEmail:" + mail);
		String[] reciver = new String[]{mail};
		String[] mySbuject = new String[]{title};
		String mybody = title;
		Intent myIntent = new Intent(android.content.Intent.ACTION_SEND);
		myIntent.setType("plain/text");
		myIntent.putExtra(android.content.Intent.EXTRA_EMAIL, reciver);
		myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mySbuject);
		myIntent.putExtra(android.content.Intent.EXTRA_TEXT, mybody);
		JsUtility.GetActivityContext().startActivity(Intent.createChooser(myIntent, "Select email application."));
	}

	/**
	 * 打开网页
	 * 
	 * @param url
	 */
	public static void OpenWebBrowser(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse(url));
		LogUtil.d(TAG, "OpenWebBrowser:"+url);
		ECApplication.getInstance().getNowActivity().startActivity(intent);
	}

	/**
	 * 调用内置拨号服务
	 * 
	 * @param number
	 */
	public static void callPhoneNumber(String number) {
		Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + number));
		JsUtility.GetActivityContext().startActivity(intent);
	}

	/**
	 * 分享字符串
	 * 
	 * @param context
	 * @param title
	 * @param text
	 */
	public static void shareString(String text) {
		Activity activity = getActivity();
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, text);
		activity.startActivity(Intent.createChooser(intent, "share"));
	}

	/**
	 * 获得当前activity
	 * 
	 * @return
	 */
	public static Activity getActivity() {
		return ECApplication.getInstance().getNowActivity();
	}

	/**
	 * 设置当前的activity
	 */
	public static Activity getPreActivity() {
		return ECApplication.getInstance().getPreActivity();
	}

	/**
	 * 打开本地相机
	 */
	@SuppressLint("SimpleDateFormat")
	public static void openCamera() {
		Activity activity = getActivity();
		String status = Environment.getExternalStorageState();
		// 判断是否有SD卡,如果有sd卡存入sd卡在说，没有sd卡直接转换为图片
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			try {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
				File file = FileUtil.getOutputMediaFile(FileUtil.MEDIA_TYPE_IMAGE);
				LogUtil.i(TAG, "openCamera : file.getPath() = " + file.getPath());
				AppUtil.putParam("cameraImageUriString", file.getPath());
				intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
				activity.startActivityForResult(intent, Constants.CAMERA);
			} catch (Exception e) {
				JsViewUtility.MakeToast("未找到系统相机程序");
			}
		} else {
			JsViewUtility.MakeToast("没有可用的存储卡");
		}
	}

	public static void openCameras(ArrayList<FilePath> filePaths) {
		Activity activity = getActivity();
		Intent intent = new Intent(activity, CameraActivity.class);
		if (filePaths != null) {
			intent.putParcelableArrayListExtra("filePaths", filePaths);
		}
		if (null != intent) {
			getActivity().startActivityForResult(intent, Constants.CAMERAACTIVITY);
			getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
		}
	}

	public static void openCameras() {
		openCameras(null);
	}

	/**
	 * 打开本地相册
	 */
	public static void openPhotoAlbum() {
		Activity activity = getActivity();
		// 从相册中去获取
		try {
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
			intent.setType("image/*");
			activity.startActivityForResult(intent, Constants.PHOTO);
			// Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
			// activity.startActivityForResult(i, Constants.PHOTO);
		} catch (ActivityNotFoundException e) {
			JsViewUtility.MakeToast("没有找到照片");
		}
	}

	/**
	 * 打开二维码扫描
	 */
	public static void openQRCapture() {
		LogUtil.d(TAG, "openQRCapture : start ...");
		Activity activity = getActivity();
		Intent intent = new Intent(activity, CaptureActivity.class);
		activity.startActivityForResult(intent, Constants.QRCAPTURE);
	}

	/**
	 * 打开视频
	 * 
	 * @param uri
	 */
	public static void openVideo(String uri) {
		// LogUtil.d(TAG, "openVideo : uri = " + uri);
		Intent intent = new Intent(getActivity(), VideoActivity.class);
		intent.putExtra("uriString", uri);
		getActivity().startActivity(intent);
	}

	/**
	 * 获取app所有缓存的大小
	 * 
	 * @return 返回的数据的单位:k
	 */
	public static int getAppCacheSize() {
		// 1./data/data/package_name/files
		// 2./data/data/package_name/cache
		// 3.<sdcard>/Android/data/<package_name>/cache/
		// 4.webview缓存数据
		File cacheDir = getActivity().getCacheDir();
		if (cacheDir != null) {
			long all_size = cacheDir.length();
			return (int) (all_size / 1024);
		}
		return 0;
	}

}
