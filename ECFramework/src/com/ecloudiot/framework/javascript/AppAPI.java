package com.ecloudiot.framework.javascript;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.data.JPushLocalNotification;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.BaseActivity;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.activity.VideoActivity;
import com.ecloudiot.framework.appliction.BaseApplication;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.utility.AppUtil;
import com.ecloudiot.framework.utility.ColorUtil;
import com.ecloudiot.framework.utility.Constants;
import com.ecloudiot.framework.utility.DensityUtil;
import com.ecloudiot.framework.utility.FileUtil;
import com.ecloudiot.framework.utility.ImageUtil;
import com.ecloudiot.framework.utility.IntentUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.PageUtil;
import com.ecloudiot.framework.utility.ReflectionUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.ViewUtil;
import com.ecloudiot.framework.utility.http.HttpAsyncClient;
import com.ecloudiot.framework.utility.http.HttpAsyncHandler;
import com.ecloudiot.framework.utility.http.NetUtil;
import com.ecloudiot.framework.view.CalendarView;
import com.ecloudiot.framework.view.MaterialDialog;
import com.ecloudiot.framework.widget.BaseWidget;
import com.ecloudiot.framework.widget.WebWidget;
import com.google.zxing.client.android.CaptureActivity;
import com.jakewharton.disklrucache.DiskLruCache;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;

public class AppAPI {
	private final static String TAG = "AppAPI";
	private Object context;
	WebWidget widget;
	private static int IO_BUFFER_SIZE = 8 * 1024;

	// 获取实例
	public AppAPI(Object cx) {
		context = cx;
	}

	/**
	 * 打开二维码扫描
	 */
	public String page_openQRCapture(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "page_openQRCapture");
		// IntentUtil.openQRCapture();
		String page = params.get("_page");
		Object cx = getPageByJsMakerId(page, params.get("_jsMakerId"));
		Intent intent = new Intent((ItemActivity) cx, CaptureActivity.class);
		((ItemActivity) cx).startActivityForResult(intent, Constants.QRCAPTURE);
		return "";
	}

	/**
	 * 打开在线播放视频
	 */
	public String page_playVideo(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "page_openQRCapture");
		// IntentUtil.openQRCapture();
		String page = params.get("_page");
		Object cx = getPageByJsMakerId(page, params.get("_jsMakerId"));
		Intent intent = new Intent((ItemActivity) cx, VideoActivity.class);
		String param = params.get("_param");
		intent.putExtra("uriString", param);
		((ItemActivity) cx).startActivity(intent);
		return "";
	}

	/**
	 * 
	 */
	public String app_fullImage(HashMap<String, String> params) {
		String imageUrl = params.get("imageurl");
		// 可传递一些其他的控制参数
		String paramObj = params.get("params");

		IntentUtil.openActivity(
				"com.ecloudiot.framework.activity.ImageActivity", "", imageUrl);

		return "";
	}

	/**
	 * 设置页面等待状态
	 * 
	 * @param params
	 * @return Ohmer-Mar 31, 2014 10:26:45 PM
	 * 
	 *         $A().page("page_test_js").wait();
	 */
	public String page_wait(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "page_wait");
		String page = params.get("_page");
		Object cx = getPageByJsMakerId(page, params.get("_jsMakerId"));
		if (cx instanceof ItemActivity) {
			((ItemActivity) cx).setWaitBeforeCreate(true);
		} else if (cx instanceof ItemFragment) {
			((ItemFragment) cx).setWaitBeforeCreate(true);
		}
		return "_false";
	}

	/**
	 * 设置页面恢复等待状态
	 * 
	 * @param params
	 * @return Ohmer-Mar 31, 2014 10:27:23 PM
	 * 
	 *         $A().page("page_test_js").resumeWait();
	 */
	public String page_resumeWait(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "page_resumeWait");
		String page = params.get("_page");
		Object cx = getPageByJsMakerId(page, params.get("_jsMakerId"));
		if (cx instanceof ItemActivity) {
			((ItemActivity) cx).setWaitBeforeCreate(false);
		} else if (cx instanceof ItemFragment) {
			((ItemFragment) cx).setWaitBeforeCreate(false);
		}
		PageUtil.resumeWaitPage(cx);
		return "_false";
	}

	/**
	 * 设置页面param
	 * 
	 * @param params
	 * @return Ohmer-Mar 31, 2014 10:27:36 PM
	 * 
	 * @example <code>
	 * $A().page("page_test_js").param({key:"testParam",value:"myparam"});
	 * $A().page("page_test_js").param("testParam").then(function(data) {
	 * 	 $A().app().makeToast(data); 
	 * });
	 * </code>
	 */
	public String page_param(HashMap<String, String> params) {
		// LogUtil.d(TAG, "page_param");
		String page = params.get("_page");
		Object cx = getPageByJsMakerId(page, params.get("_jsMakerId"));
		String param = params.get("_param");
		String key = params.get("key");
		String value = params.get("value");

		try {
			// LogUtil.d(TAG, "page_param param:" + param);
			// LogUtil.d(TAG, "page_param key:" + key);
			// LogUtil.d(TAG, "page_param page:" + page);

			if (StringUtil.isEmpty(param)
					|| (!StringUtil.isEmpty(key) && !StringUtil.isEmpty(value))) {
				PageUtil.putParam(key, value, cx);
				return "_false";
			} else {
				if (!StringUtil.isEmpty(key) && StringUtil.isEmpty(value)) {
					param = key;
				}
				// LogUtil.d(TAG, "page_param param:" + param);
				String result = PageUtil.getParam(param, cx);
				// LogUtil.d(TAG, "page_param result:" + result);
				return result;
			}
		} catch (Exception e) {
			LogUtil.e(TAG, "page_param error:" + e.toString());
			e.printStackTrace();
		}
		return "_false";
	}

	/**
	 * 设置页面param
	 * 
	 * @param params
	 * @return Ohmer-Mar 31, 2014 10:27:36 PM
	 * 
	 * @example <code>
	 * $A().page("page_test_js").data("{abc:123}");
	 * $A().page("page_test_js").data().then(function(data) {
	 * 	 $A().app().makeToast(data); 
	 * });
	 * </code>
	 */
	public String page_data(HashMap<String, String> params) {
		String page = params.get("_page");
		Object cx = getPageByJsMakerId(page, params.get("_jsMakerId"));
		String param = params.get("_param");
		try {
			if (StringUtil.isEmpty(param)) {
				// LogUtil.d(TAG, "page_data get");
				return PageUtil.getParam("pageData", cx);
			} else {
				// //LogUtil.d(TAG, "page_data put");
				PageUtil.putParam("pageData", param, cx);
			}
		} catch (Exception e) {
			LogUtil.e(TAG, "page_data error:" + e.toString());
			e.printStackTrace();
		}
		return "_false";

	}

	/**
	 * 设置页面timeout事件
	 * 
	 * @param params
	 * @return Ohmer-Apr 1, 2014 6:57:50 PM
	 * 
	 * @example <code>
	 * $A().page("page_test_js").onResume().then(function(res) {
	 *     $A().app().makeToast("onResume");
	 * });
	 * </code>
	 */
	@SuppressLint("HandlerLeak")
	public String page_setTimeout(final HashMap<String, String> params) {
		// LogUtil.d(TAG, "page_onTimeout:" + params);

		class TimeOutClass extends AsyncTask<Void, Void, String[]> {
			protected void onPostExecute(String[] result) {
				JsAPI.callback(params.get("_jsMakerId"),
						params.get("_callbackId"), "");
				super.onPostExecute(result);
			}

			@Override
			protected String[] doInBackground(Void... _params) {
				try {
					Thread.sleep(Long.parseLong(params.get("_param")));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}
		}
		new TimeOutClass().execute();
		return "_false";
	}

	/**
	 * 设置页面onResult事件
	 * 
	 * @param params
	 * @return Ohmer-Apr 1, 2014 6:57:50 PM
	 * 
	 * @example <code>
	 * $A().page("page_test_js").onResume();
	 * </code>
	 */
	public String page_onResult(HashMap<String, String> params) {
		page_on(params);
		return "_false";
	}

	/**
	 * 设置页面onResume事件
	 * 
	 * @param params
	 * @return Ohmer-Apr 1, 2014 6:57:50 PM
	 * 
	 * @example <code>
	 * $A().page("page_test_js").onResume().then(function(res) {
	 *     $A().app().makeToast("onResume");
	 * });
	 * </code>
	 */
	public String page_onResume(HashMap<String, String> params) {
		page_on(params);
		return "_false";
	}

	/**
	 * 设置页面onResume事件
	 * 
	 * @param params
	 * @return Ohmer-Apr 1, 2014 6:57:50 PM
	 * 
	 * @example <code>
	 * $A().page("page_test_js").onResume().then(function(res) {
	 *     $A().app().makeToast("onResume");
	 * });
	 * </code>
	 */
	public String page_onPageSelected(HashMap<String, String> params) {
		page_on(params);
		return "_false";
	}

	/**
	 * 执行某个page的方法
	 * 
	 * @param params
	 * @return Ohmer-Mar 31, 2014 10:27:23 PM
	 * 
	 *         $A().page("page_test_js").callFun({"pageName":"onSelected",input:
	 *         {}});
	 */
	public String page_callFun(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "page_callFun , params:"+params.toString());
		String page = params.get("_page");
		String pageName = params.get("pageName");
		String input = params.get("input");
		Object cx = getPageByJsMakerId(page, params.get("_jsMakerId"));

		if (cx != null && cx instanceof ItemActivity) {
			JsAPI.runEvent(((ItemActivity) cx).getJsEvents(), pageName, input);
		} else if (cx != null && cx instanceof ItemFragment) {
			JsAPI.runEvent(((ItemFragment) cx).getJsEvents(), pageName, input);
		}

		// //LogUtil.d(TAG, "page_callFun error:" + cx.toString());
		return "_false";
	}

	/**
	 * 设置页面onCreated事件
	 * 
	 * @param params
	 * @return Ohmer-Apr 1, 2014 6:57:50 PM
	 * 
	 * @example <code>
	 * $A().page("page_test_js").onResume().then(function(res) {
	 *     $A().app().makeToast("onResume");
	 * });
	 * </code>
	 */
	public String page_onCreated(HashMap<String, String> params) {
		page_on(params);
		return "_false";
	}

	/**
	 * 设置页面onKeyDown事件
	 * 
	 * @param params
	 * @return Ohmer-Apr 1, 2014 6:57:50 PM
	 * 
	 * @example <code>
	 * $A().page("page_test_js").onKeyDown(function(res) {
	 *     $A().app().makeToast("onResume");
	 * });
	 * </code>
	 */
	public String page_onKeyDown(HashMap<String, String> params) {
		page_on(params);
		return "_false";
	}

	/**
	 * 设置页面on事件统一处理
	 * 
	 * @param params
	 * @return Ohmer-Apr 1, 2014 6:57:50 PM
	 */
	public String page_on(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "page_" + params.get("_method"));
		String page = params.get("_page");
		Object cx = getPageByJsMakerId(page, params.get("_jsMakerId"));
		if (cx instanceof ItemActivity) {
			((ItemActivity) cx).addJsEvent(params.get("_jsMakerId") + "|"
					+ params.get("_event_id"), params.get("_method"));
		} else if (cx instanceof ItemFragment) {
			((ItemFragment) cx).addJsEvent(params.get("_jsMakerId") + "|"
					+ params.get("_event_id"), params.get("_method"));
		}
		return "_false";
	}

	/**
	 * 设置页面refresh
	 * 
	 * @param params
	 * @return
	 */
	public String page_refresh(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "page_" + params.get("_method"));
		return page_resumeWait(params);
	}

	/**
	 * 弹出对话框 android L风格
	 * 
	 * @param params
	 * @return
	 */
	public String app_showConfirm(final HashMap<String, String> params) {
		String okWords = params.get("ok");
		String cancelWords = params.get("cancel");
		String message = params.get("message");
		String title = params.get("title");

		final MaterialDialog mMaterialDialog;

		mMaterialDialog = new MaterialDialog(ECApplication.getInstance()
				.getNowActivity());

		mMaterialDialog.setTitle(title);
		mMaterialDialog.setMessage(message);
		mMaterialDialog.setPositiveButton(okWords, new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				JSONObject arg = new JSONObject();
				try {
					arg.put("state", "ok");
				} catch (JSONException e) {
					// //LogUtil.d(TAG, "showConfirm error:" + e.toString());
					e.printStackTrace();
				}
				JsAPI.callback(params.get("_jsMakerId"),
						params.get("_callbackId"), arg);
				mMaterialDialog.dismiss();
			}
		});

		if (StringUtil.isNotEmpty(cancelWords)) {

			mMaterialDialog.setNegativeButton(cancelWords,
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {

							JSONObject arg = new JSONObject();
							try {
								arg.put("state", "cancel");
							} catch (JSONException e) {
								// //LogUtil.d(TAG, "showConfirm error:" +
								// e.toString());
								e.printStackTrace();
							}
							JsAPI.callback(params.get("_jsMakerId"),
									params.get("_callbackId"), arg);
							mMaterialDialog.dismiss();
						}
					});

		}
		mMaterialDialog.show();
		return "_false";
	}

	/**
	 * 打开可滑动CalendarDialog
	 * 
	 * @author 李立波
	 */
	public String app_showCalendarConfirm(final HashMap<String, String> params) {

		Context context = ECApplication.getInstance().getNowActivity();

		String title = params.get("title");
		String nowDate = params.get("date");

		final MaterialDialog mMaterialDialog = new MaterialDialog(context);
		CalendarView calendarView = new CalendarView(context, nowDate,
				mMaterialDialog, params);
		mMaterialDialog.setView(calendarView.getView());
		mMaterialDialog.show();

		return "_false";
	}

	/**
	 * 时间对话框
	 * 
	 * @param params
	 * @return
	 */
	@SuppressLint("InflateParams")
	public String app_showDatepickerConfirm(final HashMap<String, String> params) {
		// //LogUtil.d(TAG, "app_showRadioConfirm");
		String title = params.get("title");
		String okWords = params.get("ok");
		String defaultDay = params.get("defaultDay");
		String[] now_arr = new String[3];
		String cancelWords = params.get("cancel");
		String dayType = params.get("calendar");
		final JSONObject arg = new JSONObject();
		final MaterialDialog mMaterialDialog;

		mMaterialDialog = new MaterialDialog(ECApplication.getInstance()
				.getNowActivity());

		View view = LayoutInflater.from(
				ECApplication.getInstance().getNowActivity()).inflate(
				R.layout.dialog_date_picker, null);
		DatePicker picker = (DatePicker) view
				.findViewById(R.id.dialog_date_picker_datepicker);
		final CalendarPickerView calendar_view = (CalendarPickerView) view
				.findViewById(R.id.calendar_view);
		RelativeLayout calendar_layout = (RelativeLayout) view
				.findViewById(R.id.calendar_layout);
		TextView pre_month = (TextView) view.findViewById(R.id.pre_month);
		TextView next_month = (TextView) view.findViewById(R.id.next_month);
		if (StringUtil.isNotEmpty(dayType) && dayType.equals("calendar")) {
			final Calendar monthBegin = Calendar.getInstance();
			final Calendar monthEnd = Calendar.getInstance();
			pre_month.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					monthEnd.set(Calendar.DAY_OF_MONTH, 1);
					monthBegin.add(Calendar.MONDAY, -1);
					monthBegin.set(Calendar.DAY_OF_MONTH, 1);
					calendar_view
							.init(monthBegin.getTime(), monthEnd.getTime())
							.inMode(SelectionMode.SINGLE)
							.withSelectedDate(monthBegin.getTime());
				}

			});
			next_month.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					monthEnd.add(Calendar.MONDAY, 2);
					monthEnd.set(Calendar.DAY_OF_MONTH, 1);
					monthBegin.add(Calendar.MONDAY, 1);
					monthBegin.set(Calendar.DAY_OF_MONTH, 1);
					calendar_view
							.init(monthBegin.getTime(), monthEnd.getTime())
							.inMode(SelectionMode.SINGLE)
							.withSelectedDate(monthBegin.getTime());
				}

			});

			monthEnd.add(Calendar.MONDAY, 2);
			monthEnd.set(Calendar.DAY_OF_MONTH, 1);
			monthBegin.add(Calendar.MONDAY, -1);
			monthBegin.set(Calendar.DAY_OF_MONTH, 1);
			calendar_view.invalidate();

			calendar_view.init(monthBegin.getTime(), monthEnd.getTime())
					.inMode(SelectionMode.SINGLE)
					.withSelectedDate(monthBegin.getTime());
			calendar_view
					.setOnDateSelectedListener(new OnDateSelectedListener() {
						@Override
						public void onDateSelected(Date date) {
							LogUtil.d(TAG, date.toLocaleString());
						}
					});
		}
		TextView title_tv = (TextView) view
				.findViewById(R.id.dialog_date_picker_title);

		if (StringUtil.isNotEmpty(dayType) && dayType.equals("calendar")) {
			calendar_layout.setVisibility(View.VISIBLE);
			picker.setVisibility(View.GONE);

		} else {
			calendar_layout.setVisibility(View.GONE);
			picker.setVisibility(View.VISIBLE);
		}
		final Calendar now = Calendar.getInstance();
		if (StringUtil.isNotEmpty(defaultDay)) {
			now_arr = defaultDay.split("-");
			now.set(Integer.parseInt(now_arr[0]),
					Integer.parseInt(now_arr[1]) - 1,
					Integer.parseInt(now_arr[2]));
		}

		title_tv.setText(title);
		picker.init(now.get(Calendar.YEAR), now.get(Calendar.MONTH),
				now.get(Calendar.DAY_OF_MONTH), new OnDateChangedListener() {
					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {

						try {
							// String month = dayOfMonth < 10 ? "0" + dayOfMonth
							// : "" + monthOfYear;
							arg.put("value", year
									+ "-"
									+ (monthOfYear < 9 ? "0"
											+ (monthOfYear + 1) + ""
											: (monthOfYear + 1))
									+ "-"
									+ (dayOfMonth < 10 ? "0" + dayOfMonth + ""
											: dayOfMonth));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}

				});

		// 设置默认值
		try {
			arg.put("value",
					""
							+ now.get(Calendar.YEAR)
							+ "-"
							+ (now.get(Calendar.MONTH) < 9 ? "0"
									+ (now.get(Calendar.MONTH) + 1) + "" : (now
									.get(Calendar.MONTH) + 1))
							+ "-"
							+ (now.get(Calendar.DAY_OF_MONTH) < 10 ? "0"
									+ now.get(Calendar.DAY_OF_MONTH) + "" : now
									.get(Calendar.DAY_OF_MONTH)));
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		mMaterialDialog.setView(view);
		mMaterialDialog.setTitle(title);
		mMaterialDialog.setPositiveButton(okWords, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mMaterialDialog.dismiss();
				try {
					arg.put("state", "ok");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				JsAPI.callback(params.get("_jsMakerId"),
						params.get("_callbackId"), arg);
			}
		});

		if (StringUtil.isNotEmpty(cancelWords)) {
			mMaterialDialog.setNegativeButton(cancelWords,
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							mMaterialDialog.dismiss();

							JSONObject arg = new JSONObject();
							try {
								arg.put("state", "cancel");
							} catch (JSONException e) {
								// //LogUtil.d(TAG, "showConfirm error:" +
								// e.toString());
								e.printStackTrace();
							}
							JsAPI.callback(params.get("_jsMakerId"),
									params.get("_callbackId"), arg);
						}
					});

		}
		mMaterialDialog.show();
		return "_false";
	}

	/**
	 * /** 单选对话框
	 * 
	 * @param params
	 * @return
	 */
	public String app_showRadioConfirm(final HashMap<String, String> params) {
		// //LogUtil.d(TAG, "app_showRadioConfirm");
		String title = params.get("title");
		String okWords = params.get("ok");
		String target = params.get("target");
		final String[] items = params.get("items").split("-");
		final HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < items.length; i++) {
			if (items[i].equals(target)) {
				map.put("target", "" + i);
			}
		}

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				ECApplication.getInstance().getNowActivity());

		builder.setTitle(title);
		// builder.setMessage(message);
		//
		// if (title.endsWith("字体大小")) {
		// items = new String[]{"小", "中", "大", "超大"};
		// }
		// if (title.equals("自动加载图片")) {
		// items = new String[]{"任何时候", "仅WIFI网络", "从不"};
		// }
		// if (title.equals("自动下载新版安装包")) {
		// items = new String[]{"仅WIFI网络", "从不"};
		// }
		builder.setSingleChoiceItems(items,
				Integer.parseInt(map.get("target")), new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, final int which) {
						// Toast.makeText(ECApplication.getInstance().getNowActivity(),
						// "" + which, 3).show();
						map.put("target", "" + which);
					}
				});
		builder.setPositiveButton(okWords, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				JSONObject arg = new JSONObject();
				try {
					arg.put("state", "ok");
					arg.put("target",
							items[Integer.parseInt(map.get("target"))]);
				} catch (JSONException e) {
					// LogUtil.d(TAG, "showConfirm error:" + e.toString());
					e.printStackTrace();
				}
				JsAPI.callback(params.get("_jsMakerId"),
						params.get("_callbackId"), arg);
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();

		// //LogUtil.d(TAG, map.get("target"));
		return "_false";
	}

	/**
	 * 获取当前网络状态
	 * 
	 * @param params
	 * @return offline,wifi,mobile
	 */
	public String app_netState(HashMap<String, String> params) {
		Context nowContext = ECApplication.getInstance().getNowActivity();
		ConnectivityManager cwjManager = (ConnectivityManager) nowContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		String typeName = "offline";
		if (NetUtil.hasNetWork()) {
			if (cwjManager.getActiveNetworkInfo().getTypeName() != null
					&& cwjManager.getActiveNetworkInfo().getTypeName()
							.equalsIgnoreCase("WIFI")) {
				typeName = "wifi";
			} else {
				typeName = "mobile";
			}
		}
		return typeName;
	}

	/**
	 * intent调用
	 * 
	 * @param params
	 * @return
	 */
	public String app_openIntent(HashMap<String, String> params) {

		String param = params.get("_param");
		if (!StringUtil.isEmpty(param)) {
			if (param.equals("restart")) {
				Intent intent = ECApplication
						.getInstance()
						.getNowActivity()
						.getBaseContext()
						.getPackageManager()
						.getLaunchIntentForPackage(
								ECApplication.getInstance().getNowActivity()
										.getBaseContext().getPackageName());
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				ECApplication.getInstance().getNowActivity()
						.startActivity(intent);
			}
		}
		if (!StringUtil.isEmpty(param)) {
			if (param.equals("setting_wifi")) {
				Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
				ECApplication.getInstance().getNowActivity()
						.startActivity(intent);
			}
		}
		if (!StringUtil.isEmpty(param)) {
			if (param.equals("setting_net")) {
				Intent intent = new Intent(
						Settings.ACTION_DATA_ROAMING_SETTINGS);
				ECApplication.getInstance().getNowActivity()
						.startActivity(intent);
			}
		}
		if (!StringUtil.isEmpty(param)) {
			if (param.equals("open_wifi")) {
				WifiManager wm = (WifiManager) ECApplication.getInstance()
						.getNowActivity()
						.getSystemService(Context.WIFI_SERVICE);
				wm.setWifiEnabled(true);
			}
		}
		if (!StringUtil.isEmpty(param)) {
			if (param.equals("open_net")) {
				/**
				 * 移动网络开关
				 */
				ConnectivityManager conMgr = (ConnectivityManager) ECApplication
						.getInstance().getNowActivity()
						.getSystemService(Context.CONNECTIVITY_SERVICE);

				Class<?> conMgrClass = null; // ConnectivityManager类
				Field iConMgrField = null; // ConnectivityManager类中的字段
				Object iConMgr = null; // IConnectivityManager类的引用
				Class<?> iConMgrClass = null; // IConnectivityManager类
				Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法

				try {
					// 取得ConnectivityManager类
					conMgrClass = Class.forName(conMgr.getClass().getName());
					// 取得ConnectivityManager类中的对象mService
					iConMgrField = conMgrClass.getDeclaredField("mService");
					// 设置mService可访问
					iConMgrField.setAccessible(true);
					// 取得mService的实例化类IConnectivityManager
					iConMgr = iConMgrField.get(conMgr);
					// 取得IConnectivityManager类
					iConMgrClass = Class.forName(iConMgr.getClass().getName());
					// 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
					setMobileDataEnabledMethod = iConMgrClass
							.getDeclaredMethod("setMobileDataEnabled",
									Boolean.TYPE);
					// 设置setMobileDataEnabled方法可访问
					setMobileDataEnabledMethod.setAccessible(true);
					// 调用setMobileDataEnabled方法
					setMobileDataEnabledMethod.invoke(iConMgr, true);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}

		return "_false";
	}

	/**
	 * 输出log
	 * 
	 * @param params
	 * @return Ohmer-Apr 1, 2014 6:57:50 PM
	 * 
	 * @example <code>
	 * $A().app().log("test");
	 * </code>
	 */
	public String app_log(HashMap<String, String> params) {
		// LogUtil.d(TAG, "app_log");
		String sub = "-page";
		String pageId = JsAPI.getPageId(params.get("_jsMakerId"),
				params.get("_callbackId"));
		String[] callbackarr = params.get("_callbackId").split("\\_");
		if (!callbackarr[0].equals("device")) {
			sub = "-web";
		}
		String json = params.get("_param");

		int maxLogSize = 1000;
		if (json.length() > 1000) {
			for (int i = 0; i <= json.length() / maxLogSize; i++) {
				int start = i * maxLogSize;
				int end = (i + 1) * maxLogSize;
				end = end > json.length() ? json.length() : end;
				LogUtil.v(pageId + sub, json.substring(start, end));
			}
		} else if (json.contains("{")) {

			try {
				String jsonTitle = json.substring(0, json.indexOf("{"));
				String jsonContent = json.substring(json.indexOf("{"));
				JSONObject jsonObject = new JSONObject(jsonContent);
				LogUtil.d(pageId + sub, jsonTitle);
				// LogUtil.d(pageId + sub, jsonContent.length() + "");
				LogUtil.d(pageId + sub, jsonObject.toString(4));
			} catch (JSONException e) {
				LogUtil.d(pageId + sub, json);
			}
		} else {
			LogUtil.d(pageId + sub, json);

		}
		return "_false";

	}

	/**
	 * open url
	 * 
	 * @param params
	 * @return Ohmer-Apr 1, 2014 6:57:50 PM
	 * 
	 * @example <code>
	 * $A().app().log("test");
	 * </code>
	 */
	public String app_openUrl(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "app_openUrl");
		String param = params.get("_param");
		IntentUtil.OpenWebBrowser(param);
		return "_false";

	}

	public String app_closeLoadingDialog(HashMap<String, String> params) {
		ViewUtil.closeLoadingDianlog();
		return "_false";
	}

	public String app_showLoadingDialog(HashMap<String, String> params) {
		String title = params.get("title");
		String content = params.get("content");
		boolean cancelable = true;
		if (StringUtil.isNotEmpty(params.get("cancelable"))
				&& params.get("cancelable").equals("false")) {
			cancelable = false;
		} else {
			cancelable = true;
		}
		// LogUtil.d(TAG, "app_showLoadingDialog");
		ViewUtil.showLoadingDialog(
				ECApplication.getInstance().getNowActivity(), title, content,
				cancelable);
		return "_false";
	}

	public String app_platform(HashMap<String, String> params) {
		return "android";
	}

	/**
	 * 网络请求
	 * 
	 * @param params
	 * @return
	 */
	public String app_callApi(final HashMap<String, String> params) {
		// LogUtil.d(TAG, "app_callApi params: " + params.toString());
		// HashMap<String, String> param = (HashMap<String, String>)
		// GsonUtil.toHashMap(params.get("_param"));
		// LogUtil.d(TAG, "app_callApi params: " + param.toString());
		HttpAsyncClient.Instance().post("", params, new HttpAsyncHandler() {
			@Override
			public void onFailure(String failResopnse) {
				LogUtil.e(TAG, "onFailure error: " + failResopnse);
				ViewUtil.closeLoadingDianlog();
				JSONObject resObject;
				try {
					resObject = new JSONObject(failResopnse);
					JsAPI.callback(params.get("_jsMakerId"),
							params.get("_callbackId"), resObject);
				} catch (JSONException e) {
					LogUtil.e(TAG,
							"app_callApi failResopnse ERROR: " + e.toString());
					e.printStackTrace();
				}
			}

			@Override
			public void onSuccess(String response) {
				// LogUtil.d(TAG, "onSuccess : " + response.toString());
				// ViewUtil.closeLoadingDianlog();
				JSONObject resObject;
				try {
					resObject = new JSONObject(response);
					JsAPI.callback(params.get("_jsMakerId"),
							params.get("_callbackId"), resObject);
				} catch (JSONException e) {
					LogUtil.e(TAG,
							"app_callApi onResponse ERROR: " + e.toString());
					e.printStackTrace();
				}
			}

			@Override
			public void onProgress(Float progress) {
				// LogUtil.d(TAG, "onProgress : " + progress.toString());
			}

			@Override
			public void onResponse(String resopnseString) {
				// LogUtil.d(TAG, "onResponse : " + params.get("_jsMakerId") +
				// "---" + params.get("_callbackId") + ";response = " +
				// resopnseString);
			}
		});

		return "_false";
	}

	/**
	 * 打开其他page
	 * 
	 * @param params
	 * @return Ohmer-Apr 1, 2014 6:57:50 PM
	 * 
	 * @example <code>
	 * $A().app().log("test");
	 * </code>
	 */
	public String app_openPage(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "app_openPage");
		String pageName = params.get("page_name");
		String param = params.get("params").toString();
		String closeOption = params.get("close_option");
		if (StringUtil.isEmpty(closeOption)) {
			IntentUtil.openActivity("", pageName, param);
		} else if (closeOption.equals("close")) {
			IntentUtil.openActivityWithFinished("", pageName, param);
		} else if (closeOption.equals("close_others")) {
			IntentUtil.openActivityFinishedOthers("", pageName, param);
		}
		return "_false";
	}

	public String app_closePage(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "app_closePage");
		IntentUtil.closeActivity();
		return "_false";
	}

	/**
	 * 获得sharePreference中的数据，保存使用postEvent
	 * 
	 * @param key
	 * @param callBackId
	 *            Ohmer-Dec 31, 2013 9:52:03 AM
	 */
	public String app_preference(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "app_preference");
		String param = params.get("_param");
		String key = params.get("key");
		String value = params.get("value");

		if (StringUtil.isEmpty(param)) {
			if (key.equals("username"))
				if (StringUtil.isEmpty(value))
					ECApplication.getInstance().editConfig("userName", "");
				// AppUtil.clearUserName();
				else
					AppUtil.saveUserName(value);
			else
				ECApplication.getInstance().editConfig(key, value);
		} else {
			if (param.equals("push_token"))
				return JsViewUtility.getPushToken();
			else if (param.equals("username"))
				return AppUtil.getUserName();
			else
				return ECApplication.getInstance().readConfig(param);
		}
		return "_false";
	}

	// 添加 toast
	public String app_makeToast(HashMap<String, String> params) {
		String paramString = params.get("_param");
		if (StringUtil.isEmpty(paramString)) {
			return "_false";
		}
		JsViewUtility.MakeToast(paramString);
		return "_false";
	}

	/**
	 * 
	 * 添加通知
	 * 
	 * @param params
	 * @author pengpeng 2015年01月06日16:12:15
	 * @return
	 */
	public String notification_add(HashMap<String, String> params) {
		String title = params.get("title");
		String content = "";
		if (StringUtil.isNotEmpty(params.get("content"))) {
			content = params.get("content");
		}

		long notificationId = Long.parseLong(params.get("notificationId"));
		long broadcastTime = Long.parseLong(params.get("broadcastTime"));
		;

		JPushLocalNotification ln = new JPushLocalNotification();
		ln.setBuilderId(1);// 设置本地通知样式
		ln.setTitle(title);
		ln.setContent(content);
		ln.setNotificationId(notificationId);
		ln.setBroadcastTime(broadcastTime);
		JPushInterface.addLocalNotification(ECApplication.getInstance()
				.getNowActivity(), ln);
		return "_false";
	}

	/**
	 * 
	 * 清空通知
	 * 
	 * @param params
	 * @author pengpeng 2015年01月06日16:12:15
	 * @return
	 */
	public String notification_clear(HashMap<String, String> params) {
		JPushInterface.clearLocalNotifications(ECApplication.getInstance()
				.getNowActivity());
		return "_false";
	}

	/**
	 * 
	 * 设置别名
	 * 
	 * @param params
	 * @author pengpeng 2015年01月06日16:12:15
	 * @return
	 */
	public String notification_setAlias(HashMap<String, String> params) {
		LogUtil.d(TAG, "notification_setAlias");
		String alias = params.get("alias");
		JPushInterface.setAliasAndTags(ECApplication.getInstance()
				.getNowActivity(), alias, null);
		return "_false";
	}

	/**
	 * 
	 * 清空通知
	 * 
	 * @param params
	 * @author pengpeng 2015年01月06日16:12:15
	 * @return
	 */
	public String notification_clearAlias(HashMap<String, String> params) {
		JPushInterface.setAliasAndTags(ECApplication.getInstance()
				.getNowActivity(), "", null);
		return "_false";
	}

	/**
	 * 
	 * 删除通知
	 * 
	 * @param params
	 * @author pengpeng 2015年01月06日16:12:15
	 * @return
	 */
	public String notification_remove(HashMap<String, String> params) {
		long notificationId = Long.parseLong(params.get("notificationId"));
		JPushInterface.removeLocalNotification(ECApplication.getInstance()
				.getNowActivity(), notificationId);
		return "_false";
	}

	/**
	 * 打开menu菜单
	 * 
	 * @param params
	 */
	public String page_showMenu(HashMap<String, String> params) {
		// LogUtil.d(TAG, "page_showMenu:");
		ECApplication.getInstance().getNowActivity().openOptionsMenu();
		// LogUtil.d(TAG, "page_showMenu:1");
		return "_false";
	}

	/**
	 * page view
	 * 
	 * @param params
	 */
	public String page_view_on(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "page_view_on:");
		String page = params.get("_page");
		String method = params.get("_method");
		String viewId = params.get("_view");
		final String jsMakerId = params.get("_jsMakerId");
		final String eventId = params.get("_event_id");
		Object cx = getPageByJsMakerId(page, params.get("_jsMakerId"));
		View view = (View) ViewUtil.getParentLayout(viewId, cx);
		if (method.equals("onClick")) {
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					JsAPI.callback(jsMakerId, eventId, "{}");
				}
			});
		}

		return "_false";
	}

	/**
	 * view 的 onClick 事件
	 * 
	 * @param params
	 */
	public String page_view_onClick(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "page_view_onClick");
		page_view_on(params);
		return "_false";
	}

	// 打开指定popwindow
	public String page_view_openPopupWindow(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "page_view_openPopupwindow:");
		String viewId = params.get("_view");
		ViewUtil.openPopupwindow(viewId, params.get("_param"));
		return "_false";
	}

	// 关闭popwindow
	public String page_view_closePopupWindow(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "page_view_closePopupwindow:");
		ViewUtil.closePopupwindow();
		return "_false";
	}

	/**
	 * page view
	 * 
	 * @param params
	 */

	public String page_view_append(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "page_view_append:" + params.toString());
		try {
			// 准备参数
			String page = params.get("_page");
			String widget = params.get("_widget");
			String view_id = params.get("id");
			String parent_id = params.get("_view");
			String type = params.get("type");
			if (StringUtil.isEmpty(type))
				type = "LinearLayout";
			String orientation = params.get("orientation");
			String width = params.get("width");
			int widthValue = -1;
			String height = params.get("height");
			int heightValue = -1;
			String margin = params.get("margin");
			String margin_left = params.get("marginLeft");
			String margin_right = params.get("marginRight");
			String margin_top = params.get("marginTop");
			String margin_bottom = params.get("marginBottom");
			String padding = params.get("padding");
			String padding_left = params.get("paddingLeft");
			String padding_right = params.get("paddingRight");
			String padding_top = params.get("paddingTop");
			String padding_bottom = params.get("paddingBottom");
			// String gravity = params.get("gravity");
			String backgroundColor = params.get("backgroundColor");
			// int layoutGravityValue = -1;
			int gravityValue = -1;
			String align = params.get("align");
			String contentAlign = params.get("contentAlign");
			int contentAlignValue = -1;
			String textColor = params.get("textColor");
			String textSize = params.get("textSize");
			String text = params.get("text");
			String imageSrc = params.get("imageSrc");
			Context nowContext = ECApplication.getInstance().getNowActivity();
			// String imageScaleType = params.get("imageScaleType");
			// int imageScaleTypeValue = 0;

			// context
			Object cx = (Object) context;
			if (StringUtil.isEmpty(page) || page.equals("_")) {
				cx = (Object) JsAPI.instance()
						.getJsMaker(params.get("_jsMakerId")).getParent();
			} else {
				cx = (Object) PageUtil.getPageById(page);
			}
			View childView = new View(nowContext);

			// 父View、view初始化
			LinearLayout parentView = null;
			if (!StringUtil.isEmpty(widget)) { // 从widget查找子view
				BaseWidget widgetObj = ViewUtil.getWidget(widget);
				if (parent_id.equals("_")) {
					parentView = (LinearLayout) widgetObj.getBaseView();
				} else {
					Activity cxx = ECApplication.getInstance().getNowActivity();
					parentView = (LinearLayout) widgetObj.getBaseView()
							.findViewById(
									ResourceUtil.getViewIdFromContext(cxx,
											parent_id));
				}

			} else {
				parentView = ViewUtil.getParentLayout(parent_id, cx);
			}
			// 初始化 layout or text or image
			if (StringUtil.isEmpty(orientation))
				orientation = "vertical";
			if (type.equals("LinearLayout")) {
				childView = new LinearLayout(nowContext);
				if (orientation.equals("horizontal"))
					((LinearLayout) childView)
							.setOrientation(LinearLayout.HORIZONTAL);
				else
					((LinearLayout) childView)
							.setOrientation(LinearLayout.VERTICAL);
			} else if (type.equals("ImageView")) {
				childView = new ImageView(nowContext);
			} else if (type.equals("TextView")) {
				childView = new TextView(nowContext);
			}
			childView.setId(JsAPI.instance().addViewId(view_id));
			// 宽高自动适应
			if (!StringUtil.isEmpty(width)) {
				if (width.equals("fill_parent")) {
					widthValue = LayoutParams.FILL_PARENT;
				} else if (width.equals("wrap_content")) {
					widthValue = LayoutParams.WRAP_CONTENT;
				}
			} else {
				widthValue = LayoutParams.WRAP_CONTENT;
			}
			if (!StringUtil.isEmpty(height)) {
				if (height.equals("fill_parent")) {
					heightValue = LayoutParams.FILL_PARENT;
				} else if (height.equals("wrap_content")) {
					heightValue = LayoutParams.WRAP_CONTENT;
				}
			} else {
				heightValue = LayoutParams.WRAP_CONTENT;
			}
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					widthValue, heightValue);
			// 内部对齐方式
			if (!StringUtil.isEmpty(contentAlign)) {
				if (contentAlign.equals("top"))
					contentAlignValue = Gravity.TOP;
				if (contentAlign.equals("bottom"))
					contentAlignValue = Gravity.BOTTOM;
				if (contentAlign.equals("left"))
					contentAlignValue = Gravity.LEFT;
				if (contentAlign.equals("right"))
					contentAlignValue = Gravity.RIGHT;
				if (contentAlign.equals("center"))
					contentAlignValue = Gravity.CENTER;
				if (contentAlignValue > 0)
					if (childView instanceof TextView)
						((TextView) childView).setGravity(contentAlignValue);
					else if (childView instanceof LinearLayout)
						((LinearLayout) childView)
								.setGravity(contentAlignValue);
			}
			// 自己针对于父控件的 重力、对齐
			if (!StringUtil.isEmpty(align)) {
				String[] gravityArray = align.split("|");
				if (gravityArray.length > 0) {
					if (gravityArray[0].equals("top"))
						gravityValue = Gravity.TOP;
					if (gravityArray[0].equals("bottom"))
						gravityValue = Gravity.BOTTOM;
					if (gravityArray[0].equals("left"))
						gravityValue = Gravity.LEFT;
					if (gravityArray[0].equals("right"))
						gravityValue = Gravity.RIGHT;
					if (gravityArray[0].equals("center"))
						if (orientation.equals("horizontal"))
							gravityValue = Gravity.CENTER_HORIZONTAL;
						else
							gravityValue = Gravity.CENTER_VERTICAL;
					if (gravityArray[1].equals("top"))
						gravityValue = gravityValue | Gravity.TOP;
					if (gravityArray[1].equals("bottom"))
						gravityValue = gravityValue | Gravity.BOTTOM;
					if (gravityArray[1].equals("left"))
						gravityValue = gravityValue | Gravity.LEFT;
					if (gravityArray[1].equals("right"))
						gravityValue = gravityValue | Gravity.RIGHT;
					if (gravityArray[1].equals("center"))
						if (orientation.equals("horizontal"))
							gravityValue = gravityValue
									| Gravity.CENTER_HORIZONTAL;
						else
							gravityValue = gravityValue
									| Gravity.CENTER_VERTICAL;
				} else {
					if (gravityArray[1].equals("top"))
						gravityValue = Gravity.TOP;
					if (contentAlign.equals("bottom"))
						gravityValue = Gravity.BOTTOM;
					if (contentAlign.equals("left"))
						gravityValue = Gravity.LEFT;
					if (contentAlign.equals("right"))
						gravityValue = Gravity.RIGHT;
					if (contentAlign.equals("center"))
						if (orientation.equals("horizontal"))
							gravityValue = Gravity.CENTER_HORIZONTAL;
						else
							gravityValue = Gravity.CENTER_VERTICAL;
				}

				if (align.equals("center")) {
					if (orientation.equals("horizontal"))
						gravityValue = Gravity.CENTER_HORIZONTAL;
					else
						gravityValue = Gravity.CENTER_VERTICAL;
				} else if (align.equals("top"))
					gravityValue = Gravity.TOP;
				else if (align.equals("bottom"))
					gravityValue = Gravity.BOTTOM;
				else if (align.equals("left"))
					gravityValue = Gravity.LEFT;
				else if (align.equals("right"))
					gravityValue = Gravity.RIGHT;
				layoutParams.gravity = gravityValue;
			}
			// width
			if (!StringUtil.isEmpty(width) && !width.equals("fill_parent")
					&& !width.equals("wrap_content"))
				if (width.lastIndexOf("%") > 0) {
					layoutParams.weight = Float.parseFloat("."
							+ width.substring(0, width.lastIndexOf("%")));
				} else
					layoutParams.width = DensityUtil.dipTopx(nowContext,
							Float.parseFloat(width));
			// height
			if (!StringUtil.isEmpty(height) && !height.equals("fill_parent")
					&& !height.equals("wrap_content"))
				if (height.lastIndexOf("%") > 0) {
					layoutParams.weight = Float.parseFloat("."
							+ height.substring(0, height.lastIndexOf("%")));
					layoutParams.height = 0;
				} else
					layoutParams.height = DensityUtil.dipTopx(nowContext,
							Float.parseFloat(height));
			// margin
			if (StringUtil.isEmpty(margin_left))
				margin_left = StringUtil.isEmpty(margin) ? "0" : margin;
			if (StringUtil.isEmpty(margin_top))
				margin_top = StringUtil.isEmpty(margin) ? "0" : margin;
			if (StringUtil.isEmpty(margin_right))
				margin_right = StringUtil.isEmpty(margin) ? "0" : margin;
			if (StringUtil.isEmpty(margin_bottom))
				margin_bottom = StringUtil.isEmpty(margin) ? "0" : margin;
			layoutParams.setMargins(
					DensityUtil.dipTopx(nowContext,
							Float.parseFloat(margin_left)),
					DensityUtil.dipTopx(nowContext,
							Float.parseFloat(margin_top)),
					DensityUtil.dipTopx(nowContext,
							Float.parseFloat(margin_right)),
					DensityUtil.dipTopx(nowContext,
							Float.parseFloat(margin_bottom)));
			// padding
			if (StringUtil.isEmpty(padding_left))
				padding_left = StringUtil.isEmpty(padding) ? "0" : padding;
			if (StringUtil.isEmpty(padding_top))
				padding_top = StringUtil.isEmpty(padding) ? "0" : padding;
			if (StringUtil.isEmpty(padding_right))
				padding_right = StringUtil.isEmpty(padding) ? "0" : padding;
			if (StringUtil.isEmpty(padding_bottom))
				padding_bottom = StringUtil.isEmpty(padding) ? "0" : padding;
			childView.setPadding(
					DensityUtil.dipTopx(nowContext,
							Float.parseFloat(padding_left)),
					DensityUtil.dipTopx(nowContext,
							Float.parseFloat(padding_top)),
					DensityUtil.dipTopx(nowContext,
							Float.parseFloat(padding_right)),
					DensityUtil.dipTopx(nowContext,
							Float.parseFloat(padding_bottom)));
			// 设置背景
			if (!StringUtil.isEmpty(backgroundColor))
				childView.setBackgroundColor(ColorUtil
						.getColorValueFromRGB(backgroundColor));

			// 设置textview
			if (type.equals("TextView")) {
				if (!StringUtil.isEmpty(text))
					((TextView) childView).setText(text);
				if (!StringUtil.isEmpty(textColor))
					((TextView) childView).setTextColor(ColorUtil
							.getColorValueFromRGB(textColor));
				if (!StringUtil.isEmpty(textSize)) {
					((TextView) childView).setTextSize(
							TypedValue.COMPLEX_UNIT_DIP,
							Float.parseFloat(textSize) + 1);
				}
			}
			// 设置imageview
			if (type.equals("ImageView")) {
				if (!StringUtil.isEmpty(imageSrc))
					((ImageView) childView).setImageDrawable(ImageUtil
							.getDrawableFromConfig(imageSrc));
				// if (!StringUtil.isEmpty(imageScaleType))
				// ((ImageView)
				// childView).(ColorUtil.getColorValueFromRGB(textColor));
			}
			// 设置显示
			parentView.addView(childView, parentView.getChildCount(),
					layoutParams);
			// //LogUtil.d(TAG, " ccc------- " + childView.getId());
			parentView.setVisibility(View.VISIBLE);
			childView.setVisibility(View.VISIBLE);

		} catch (Exception e) {
			LogUtil.e(TAG, "page_append error:" + e.toString());
			e.printStackTrace();
		}
		return "_false";
	}

	/**
	 * widget 的 option方法
	 * 
	 * @param params
	 */
	public String page_view_text(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "page_view_text");

		// 准备参数
		String page = params.get("_page");
		String view_id = params.get("_view");
		String param = params.get("_param");
		// context
		Object cx = getPageByJsMakerId(page, params.get("_jsMakerId"));
		Object nowView = null;
		if (cx instanceof Fragment) {
			nowView = ((Fragment) cx).getView().findViewById(
					ResourceUtil.getViewIdFromContext((Context) ECApplication
							.getInstance().getNowActivity(), view_id));
		} else {
			nowView = ((Activity) cx).findViewById(ResourceUtil
					.getViewIdFromContext((Context) ECApplication.getInstance()
							.getNowActivity(), view_id));
		}

		// Object nowView = ViewUtil.getParentLayout(view_id, cx);
		((TextView) nowView).setText(param);
		return "_false";
	}

	/**
	 * web widget 的 callFun方法
	 * 
	 * @param params
	 */
	public String widget_page_callFun(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "widget_page_callFun");
		// TODO 这里没有通过page，直接使用widgetid，所以可能会找不到widget
		String widget = params.get("_widget");
		BaseWidget widgetObj = ViewUtil.getWidget(widget);
		Object[] args = new Object[2];
		args[0] = params.get("name");
		args[1] = params.get("params");
		ReflectionUtil.invokeMethod(widgetObj, "callJs", args);
		return "_false";
	}

	/**
	 * widget 的 option方法
	 * 
	 * @param params
	 */
	public String widget_page_option(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "widget_page_option");
		// TODO 这里没有通过page，直接使用widgetid，所以可能会找不到widget
		String widget = params.get("_widget");
		String key = params.get("key");
		String value = params.get("value");
		String param = params.get("_param");
		if (StringUtil.isEmpty(param)) {
			BaseWidget widgetObj = ViewUtil.getWidget(widget);
			ReflectionUtil.invokeMethod(widgetObj, "get"
					+ key.substring(0, 1).toUpperCase() + key.substring(1),
					value);
		} else {
			BaseWidget widgetObj = ViewUtil.getWidget(widget);
			return (String) ReflectionUtil.invokeMethod(widgetObj, "get"
					+ param.substring(0, 1).toUpperCase() + param.substring(1),
					"");
		}
		return "_false";
	}

	/**
	 * page view
	 * 
	 * @param params
	 */

	public String widget_page_view_on(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "widget_page_view_on:");
		// String page = params.get("_page");
		String widget = params.get("_widget");
		String method = params.get("_method");
		String viewId = params.get("_view");
		final String jsMakerId = params.get("_jsMakerId");
		final String eventId = params.get("_event_id");
		// Object cx = (Object) context;
		// if (!StringUtil.isEmpty(page)) {
		// cx = (Object) PageUtil.getPageById(page);
		// }

		BaseWidget widgetObj = ViewUtil.getWidget(widget);
		View view = null;
		if (viewId.equals("_")) {
			view = (LinearLayout) widgetObj.getBaseView();
		} else {
			Activity cxx = ECApplication.getInstance().getNowActivity();
			view = (LinearLayout) widgetObj.getBaseView().findViewById(
					ResourceUtil.getViewIdFromContext(cxx, viewId));
		}

		// view = (View) ViewUtil.getParentLayout(viewId, cx);
		if (method.equals("onClick")) {
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					JsAPI.callback(jsMakerId, eventId, "{}");
				}
			});
		}

		return "_false";
	}

	/**
	 * view 的 onClick 事件
	 * 
	 * @param params
	 */
	public String widget_page_view_onClick(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "widget_page_view_onClick");
		widget_page_view_on(params);
		return "_false";
	}

	/**
	 * widget 的 itemClick 方法
	 * 
	 * @param params
	 */
	public String widget_page_itemClick(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "widget_page_itemClick :" + params.toString());
		String widget = params.get("_widget");
		String param = params.get("_param");
		BaseWidget widgetObj = ViewUtil.getWidget(widget);
		if (StringUtil.isEmpty(param)) {
			ReflectionUtil.invokeMethod(widgetObj, "itemClick", params);
		} else {
			return (String) ReflectionUtil.invokeMethod(widgetObj, "itemClick",
					param);
		}
		return "_false";
	}

	/**
	 * widget 的 onItemClick 事件
	 * 
	 * @param params
	 */
	public String widget_page_onItemClick(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "widget_page_onItemClick:" + params.toString());
		widget_page_on(params);
		return "_false";
	}

	/**
	 * widget 的 on事件
	 * 
	 * @param params
	 */
	public String widget_page_on(HashMap<String, String> params) {
		String page = params.get("_page");
		String widget = params.get("_widget");
		Object cx = getPageByJsMakerId(page, params.get("_jsMakerId"));
		if (cx instanceof ItemActivity) {
			((ItemActivity) cx).addWidgetJsEvents(widget,
					params.get("_jsMakerId") + "|" + params.get("_event_id"),
					params.get("_method"));
		} else if (cx instanceof ItemFragment) {
			((ItemFragment) cx).addWidgetJsEvents(widget,
					params.get("_jsMakerId") + "|" + params.get("_event_id"),
					params.get("_method"));
		}

		return "_false";
	}

	/**
	 * 设置widget onCreated事件
	 * 
	 * @param params
	 * @return Ohmer-Apr 1, 2014 6:57:50 PM
	 * 
	 * @example <code>
	 * 
	 * </code>
	 */
	public String widget_page_onCreated(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "widget_page_onCreated");
		widget_page_on(params);
		return "_false";
	}

	/**
	 * widget 的 onItemClick 事件
	 * 
	 * @param params
	 */
	public String widget_page_onItemInnerClick(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "widget_page_onItemInnerClick");
		widget_page_on(params);
		return "_false";
	}

	/**
	 * widget 的 onItemClick 事件
	 * 
	 * @param params
	 */
	public String widget_page_onFixedItemDisplay(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "widget_page_onItemInnerClick");
		widget_page_on(params);
		return "_false";
	}

	/**
	 * widget 的 onBackClick 事件
	 * 
	 * @param params
	 */
	public String widget_page_onBackClick(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "widget_page_onBackClick");
		widget_page_on(params);
		return "_false";
	}

	/**
	 * widget 的 onMoreData 事件
	 * 
	 * @param params
	 */
	public String widget_page_onMoreData(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "widget_page_onMoreData");
		widget_page_on(params);
		return "_false";
	}

	/**
	 * widget 的 onLoadData 事件
	 * 
	 * @param params
	 */
	public String widget_page_onPullUp(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "widget_page_onPullUp");
		widget_page_on(params);
		return "_false";
	}

	/**
	 * widget 的 onChange 事件
	 * 
	 * @param params
	 */
	public String widget_page_onChange(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "widget_page_onChange");
		widget_page_on(params);
		return "_false";
	}

	/**
	 * widget 的 onLoadData 事件
	 * 
	 * @param params
	 */
	public String widget_page_onPullDown(HashMap<String, String> params) {
		// LogUtil.d(TAG, "widget_page_onPullDown");
		widget_page_on(params);
		return "_false";
	}

	/**
	 * widget 的 onLoadData 事件
	 * 
	 * @param params
	 */
	public String widget_page_onLoadData(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "widget_page_onItemClick");
		widget_page_on(params);
		return "_false";
	}

	/**
	 * widget 的 onSubmit 事件
	 * 
	 * @param params
	 */
	public String widget_page_onSubmit(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "widget_page_onSubmit");
		widget_page_on(params);
		return "_false";
	}

	/**
	 * widget 的 onSubmitSucces 事件
	 * 
	 * @param params
	 */
	public String widget_page_onSubmitSuccess(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "widget_page_onSubmitSuccess");
		widget_page_on(params);
		return "_false";
	}

	/**
	 * 设置widget view append方法
	 * 
	 * @example <code>
	 * 
	 * </code>
	 */
	public String widget_page_view_append(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "widget_page_view_append");
		page_view_append(params);
		return "_false";
	}

	/**
	 * widget 的 title 方法
	 * 
	 * @param params
	 */
	public String widget_page_title(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "widget_page_title");
		String widget = params.get("_widget");
		if (widget.equals("ActionBar"))
			((BaseActivity) getPageByJsMakerId(params.get("_page"),
					params.get("_jsMakerId"))).setActionBarTitle(
					params.get("title"), params.get("icon"));
		return "_false";
	}

	/**
	 * widget 的 refreshData 方法
	 * 
	 * @param params
	 */
	public String widget_page_refreshData(HashMap<String, String> params) {
		// LogUtil.d(TAG, "widget_page_refreshData:" + params.toString());
		String widget = params.get("_widget");
		String page = params.get("_page");
		BaseWidget widgetObj = ViewUtil.getWidget(widget,
				getPageByJsMakerId(page, params.get("_jsMakerId")));
		ReflectionUtil.invokeMethod(widgetObj, "refreshData",
				new String[] { params.get("_param") });
		return "_false";
	}

	/**
	 * widget 的 updateItem 方法
	 * 
	 * @param params
	 */
	public String widget_page_updateItem(HashMap<String, String> params) {
		// LogUtil.d(TAG, "widget_page_updateItem:" + params.toString());
		String widget = params.get("_widget");
		String page = params.get("_page");
		BaseWidget widgetObj = ViewUtil.getWidget(widget,
				getPageByJsMakerId(page, params.get("_jsMakerId")));
		ReflectionUtil.invokeMethod(widgetObj, "updateItem",
				new String[] { params.get("_param") });
		return "_false";
	}

	/**
	 * widget 的 updateItems 方法
	 * 
	 * @param params
	 */
	public String widget_page_updateItems(HashMap<String, String> params) {
		// LogUtil.d(TAG, "widget_page_updateItems:" + params.toString());
		String widget = params.get("_widget");
		String page = params.get("_page");
		BaseWidget widgetObj = ViewUtil.getWidget(widget,
				getPageByJsMakerId(page, params.get("_jsMakerId")));
		ReflectionUtil.invokeMethod(widgetObj, "updateItems", params);
		return "_false";
	}

	/**
	 * 设置、获取页面数据
	 * 
	 * @param params
	 * @return Ohmer-Apr 1, 2014 6:57:50 PM
	 */
	public String widget_page_data(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "widget_page_data");

		String page = params.get("_page");
		Object cx = getPageByJsMakerId(page, params.get("_jsMakerId"));
		String param = params.get("_param");

		if (StringUtil.isEmpty(param)) {
			if (cx instanceof ItemActivity) {
				return ((ItemActivity) cx).getWidgetData(params.get("_widget"));
			} else if (cx instanceof ItemFragment) {
				return ((ItemFragment) cx).getWidgetData(params.get("_widget"));
			}
		} else {
			try {
				if (cx instanceof ItemActivity) {
					((ItemActivity) cx).addWidgetData(params.get("_widget"),
							param);
				} else if (cx instanceof ItemFragment) {
					((ItemFragment) cx).addWidgetData(params.get("_widget"),
							param);
				}
			} catch (Exception e) {
				LogUtil.e(TAG, "widget_page_data error:" + e.toString());
				e.printStackTrace();
			}
		}
		return "_false";
	}

	public String getDecorateConfig(HashMap<String, String> params) {
		return AppUtil.getDecorateConfig();
	}

	public String getDecoratePath(HashMap<String, String> params) {
		return "'file://" + IntentUtil.getActivity().getFilesDir()
				+ "/decorate/'";
	}

	/**
	 * 获取系统资源路径
	 * 
	 * @param callBackId
	 *            Ohmer-Dec 31, 2013 9:44:39 AM
	 */
	public String getAssetsResFilePath(HashMap<String, String> params) {
		return "'file:///android_asset'";
	}

	/**
	 * 获取扩展路径
	 * 
	 * @param callBackId
	 *            Ohmer-Dec 31, 2013 9:44:08 AM
	 */
	public String getExternalFilePath(HashMap<String, String> params) {
		return "'file://" + IntentUtil.getActivity().getFilesDir() + "'";
	}

	/**
	 * 获取配置文件所在路径（config文件夹所在目录）
	 * 
	 * @param callBackId
	 *            Ohmer-Dec 31, 2013 9:47:21 AM
	 * 
	 */
	public String fs_getConfigFilePath(HashMap<String, String> params) {
		if (Constants.DEBUG) {
			return getExternalFilePath(params);
		} else {
			return getAssetsResFilePath(params);
		}
	}

	/**
	 * 分享文字信息
	 * 
	 * @param words
	 * 
	 */
	public String utiliy_shareWords(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "utiliy_shareWords");
		String param = params.get("_param");
		if (!StringUtil.isEmpty(param))
			IntentUtil.shareString(param);
		return "_false";
	}

	/**
	 * 图文分享
	 * 
	 * @param imgPath
	 *            ：图片路径，如：/mnt/sdcard/NiMei/image/imageName.jpg
	 * @param content
	 *            ：文本内容
	 * @param context
	 */
	public static void utiliy_shareImage(HashMap<String, String> params) {
		String imgPath = params.get("imgPath");
		String content = params.get("content");
		File f = new File(imgPath);
		Uri uri = Uri.fromFile(f);
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		if (uri != null && imgPath != null && imgPath != "") {
			shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
			shareIntent.setType("image/jpeg");
			// 当用户选择短信时使用sms_body取得文字
			shareIntent.putExtra("sms_body", content);
		} else {
			shareIntent.setType("text/plain");
		}
		shareIntent.putExtra(Intent.EXTRA_TEXT, content);
		// 自定义选择框的标题
		// context.startActivity(Intent.createChooser(shareIntent, "选择分享方式"));
		// 系统默认标题
		ECApplication.getInstance().getNowActivity()
				.startActivity(Intent.createChooser(shareIntent, "选择分享方式"));
	}

	/**
	 * 拨打电话，跳到拨号界面（不直接拨）
	 * 
	 * @param params
	 * @return
	 */
	public String app_phone(HashMap<String, String> params) {
		String param = params.get("_param");
		if (!StringUtil.isEmpty(param))
			IntentUtil.callPhoneNumber(param);
		return "_false";
	}

	public String app_getPhone(HashMap<String, String> params) {
		TelephonyManager telephonyManager = (TelephonyManager) ECApplication
				.getInstance().getNowActivity()
				.getSystemService(Context.TELEPHONY_SERVICE);
		;
		String nativePhoneNumber = null;
		nativePhoneNumber = telephonyManager.getLine1Number();
		if (nativePhoneNumber != null) {
			if (nativePhoneNumber.startsWith("+")) {
				nativePhoneNumber = nativePhoneNumber.substring(3);
			}
		}
		return nativePhoneNumber;
	}

	/**
	 * 获取缓存大小 单位M
	 * 
	 * @return
	 */
	public String app_cacheSize(HashMap<String, String> params) {
		// 缓存位置
		// 1./data/data/package_name/files（debug模式不清理）
		// 2./data/data/package_name/cache
		// 3.<sdcard>/Android/data/<package_name>/cache/
		// 4.webview缓存数据
		File file = ECApplication.getInstance().getNowActivity().getCacheDir();
		// File file=new File(Constants.CACHEFILE_DIRPATH);
		DecimalFormat df = new DecimalFormat("0.00");
		String size = df.format(FileUtil.getDirSize(file));
		if (null != size)
			return size + "M";
		return "_false";
	}

	/**
	 * 清理缓存(新版)
	 * 
	 * @return
	 */
	public String app_deleteCache(HashMap<String, String> params) {
		// 缓存位置
		// 1./data/data/package_name/files（debug模式不清理）
		// 2./data/data/package_name/cache
		// 3.<sdcard>/Android/data/<package_name>/cache/
		// 4.webview缓存数据
		File file = ECApplication.getInstance().getNowActivity().getCacheDir();
		if (null != file)
			// deleteDir(file);
			FileUtil.delAllFile(file.toString());
		return "_false";
	}

	/**
	 * 获取当前软件的版本信息
	 * 
	 * @param params
	 * @return
	 */
	public String app_getAppVersion(HashMap<String, String> params) {
		if (AppUtil.getAppVersion() > 0) {
			return AppUtil.getAppVersion() + "";
		}
		;
		return "_false";
	}

	/**
	 * 获取远程软件的版本信息 ，其实查询的是本地存的远程版本的值。
	 * 
	 * @param params
	 * @return
	 */
	public String app_getRemoteVersion(HashMap<String, String> params) {
		if (AppUtil.getRemoteVersion() > 0) {
			return AppUtil.getRemoteVersion() + "";
		}
		;
		return "_false";
	}

	/**
	 * 下载最新版本的对话框
	 * 
	 * @param params
	 * @return
	 */
	public String app_confirmDownloadNewVersion(HashMap<String, String> params) {
		String data = params.get("data");
		AppUtil.confirmDownloadNewVersion(data);
		return "_false";
	}

	/**
	 * 把最新版的版本号和下载链接存到本地 net_version_num、net_version_url
	 * 
	 * @param params
	 * @return
	 */
	public String app_checkRemoteVersion(HashMap<String, String> params) {
		AppUtil.checkRemoteVersion();
		return "_false";
	}

	/**
	 * 设置某个key的cache
	 * 
	 * @param params
	 * @return
	 */
	public String lrucache_set(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "lrucache_set");
		String key = params.get("key");
		String value = params.get("value");
		LogUtil.d(TAG, value);
		try {
			DiskLruCache cache = ((BaseApplication) BaseApplication
					.getInstance()).getmDiskLruCache();
			DiskLruCache.Editor editor = cache.edit(key);
			if (editor != null) {
				OutputStream outputStream = editor.newOutputStream(0);
				outputStream.write(value.getBytes("UTF-8"));
				editor.commit();
				cache.flush();
			}
		} catch (IOException e) {
			LogUtil.e(TAG, "lrucache_set error!");
			e.printStackTrace();
		}
		return "_false";
	}

	/**
	 * 获取某组key的cache
	 * 
	 * @param params
	 * @return
	 */
	public JSONObject lrucache_massGet(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "lrucache_massGet");
		String param = params.get("_param");
		// String result = "";
		JSONObject result = new JSONObject();
		JSONArray jsnoArray;
		JSONObject JsonObject = new JSONObject();
		try {
			jsnoArray = new JSONArray(param);
			// 结果
			for (int i = 0; i < jsnoArray.length(); i++) {
				String key = jsnoArray.getString(i);
				HashMap<String, String> kparam = new HashMap<String, String>();
				kparam.put("_param", key);
				String res = lrucache_get(kparam);
				if (res != null && res.length() > 0
						&& res.substring(0, 1).equals("{")) {
					JSONObject jo = new JSONObject(res);
					JsonObject.put(key, jo);
				} else {
					JsonObject.put(key, res);
				}
			}
			// result = JsonObject.toString();
			result = JsonObject;
		} catch (JSONException e) {
			LogUtil.e(TAG, "lrucache_massGet error!");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 删除某组key的cache
	 * 
	 * @param params
	 * @return
	 */
	public String lrucache_massRemove(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "lrucache_massGet");
		String param = params.get("_param");
		JSONArray jsnoArray;
		try {
			jsnoArray = new JSONArray(param);
			// 结果
			for (int i = 0; i < jsnoArray.length(); i++) {
				String key = jsnoArray.getString(i);
				((BaseApplication) BaseApplication.getInstance())
						.getmDiskLruCache().remove(key);
			}
		} catch (JSONException e) {
			LogUtil.e(TAG, "lrucache_massRemove error!");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "_false";
	}

	/**
	 * 获取某个key的cache
	 * 
	 * @param params
	 * @return
	 */
	public String lrucache_get(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "lrucache_get");
		String key = params.get("_param");
		String value = null;
		DiskLruCache.Snapshot snapShot = null;
		try {
			DiskLruCache cache = ((BaseApplication) BaseApplication
					.getInstance()).getmDiskLruCache();
			snapShot = cache.get(key);
			if (snapShot == null) {
				return "";
			}
			final InputStream in = snapShot.getInputStream(0);
			if (in != null) {
				final BufferedInputStream buffIn = new BufferedInputStream(in,
						IO_BUFFER_SIZE);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(buffIn));
				StringBuffer strFileContents = new StringBuffer();
				while (reader.ready()) {
					strFileContents.append((char) reader.read());
				}
				value = strFileContents.toString();
			}
		} catch (IOException e) {
			LogUtil.e(TAG, "lrucache_get error!");
			e.printStackTrace();
		} finally {
			if (snapShot != null) {
				snapShot.close();
			}
		}
		return value;
	}

	/**
	 * 删除某个key的cache
	 * 
	 * @param params
	 * @return
	 */
	public String lrucache_remove(HashMap<String, String> params) {
		// //LogUtil.d(TAG, "lrucache_remove");
		String key = params.get("_param");
		try {
			((BaseApplication) BaseApplication.getInstance())
					.getmDiskLruCache().remove(key);
		} catch (IOException e) {
			LogUtil.e(TAG, "lrucache_remove error!");
			e.printStackTrace();
		}
		return "_false";
	}

	/**
	 * 清除所有的缓存
	 * 
	 * @param params
	 * @return
	 */
	public String lrucache_clear(HashMap<String, String> params) {
		try {

			((BaseApplication) BaseApplication.getInstance())
					.getmDiskLruCache().delete();
		} catch (IOException e) {
			LogUtil.e(TAG, "lrucache_clear error!");
			e.printStackTrace();
		}
		return "_false";
	}

	// 公用函数 -----------------
	public Object getPageByJsMakerId(String pageId, String jsMakerId) {
		Object cx = (Object) context;
		if (StringUtil.isEmpty(pageId) || pageId.equals("_")) {
			cx = (Object) JsAPI.instance().getJsMaker(jsMakerId).getParent();
		} else {
			cx = (Object) PageUtil.getPageById(pageId);
		}
		return cx;
	}
}
