package com.ecloudiot.framework.utility;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.event.DoActionEvent;
import com.ecloudiot.framework.fragment.BaseFragment;
import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.widget.BaseWidget;
import com.ecloudiot.framework.widget.GalleryWidget;
import com.ecloudiot.framework.widget.GridWidget;
import com.ecloudiot.framework.widget.ListViewWidget;
import com.ecloudiot.framework.widget.model.HashMapListModel;
import com.ecloudiot.framework.widget.model.HashMapListModel.HashMapItemModel;

import de.greenrobot.event.EventBus;

public class ViewUtil {
	private final static String TAG = "ViewUtil";
	private static ProgressDialog progressDialog = null;
	private static PopupWindow popupWindow = null;
	private static AlertDialog aDialog = null;

	/**
	 * 选择器 空格隔开的每个单元层层递进，
	 * 
	 * @param sel
	 */
	public static void getView(String selectorString) {

	}

	/**
	 * 清理 view 的子试图
	 * 
	 * @param viewId
	 */
	public static void ClearView(String viewId) {
		// LogUtil.d(TAG, "ClearView");
		Activity cx = ECApplication.getInstance().getNowActivity();
		LinearLayout layout = null;
		if (viewId.contains(":")) {
			String[] ss = viewId.split(":");
			int position = Integer.parseInt(ss[1]);
			// LogUtil.d(TAG, "ClearView-ResourceId: " + (ResourceUtil.getViewIdFromContext(cx, ss[0]) + position));
			layout = (LinearLayout) cx.findViewById(ResourceUtil.getViewIdFromContext(cx, ss[0]) + position);
		} else {
			layout = (LinearLayout) cx.findViewById(ResourceUtil.getViewIdFromContext(cx, viewId));
		}
		if (null != layout) {
			layout.removeAllViews();
		} else {
			LogUtil.e(TAG, "ClearView error: layout is null ...");
		}
	}

	/**
	 * 向视图中添加视图
	 * 
	 * @param parentViewId
	 * @param childView
	 * @param position
	 * @param insertType
	 */
	public static void insertViewToView(Object pageContext, String parentViewId, View childView, int position, int insertType) {
		// LogUtil.d(TAG, "insertViewToView : parentViewId = " + parentViewId + " , childView = " + childView.toString());
		// LogUtil.i(TAG, "pageContext:" + pageContext);
		ViewGroup parentView = getParentLayout(parentViewId, pageContext);
		insertViewToView(parentView, childView, position, insertType);
	}

	/**
	 * 获取父控件
	 * 
	 * @param parentViewId
	 * @param pageContext
	 * @return
	 */
	public static LinearLayout getParentLayout(String parentViewId, Object pageContext) {
		Activity cx = ECApplication.getInstance().getNowActivity();
		LinearLayout parentView = null;
		// if (StringUtil.isNotEmpty(parentViewId) &&
		// parentViewId.contains("fragment")) {
		// Fragment fragment = null;
		// fragment = (Fragment) pageContext;
		// if (fragment != null && fragment.getView() instanceof LinearLayout) {
		// parentView = (LinearLayout) fragment.getView();
		// } else if (fragment != null) {
		// try {
		// parentView = (LinearLayout) (((ViewGroup)
		// fragment.getView()).getChildAt(0));
		// } catch (Exception e) {
		// LogUtil.e(TAG,
		// "Hei,guys! base element in fragment layout must be Linearlayout.");
		// }
		// }
		// } else
		if (StringUtil.isNotEmpty(parentViewId)) {
			if (pageContext instanceof Fragment) {
				parentView = (LinearLayout) ((Fragment) pageContext).getView().findViewById(ResourceUtil.getViewIdFromContext(cx, parentViewId));
			} else {
				parentView = (LinearLayout) cx.findViewById(ResourceUtil.getViewIdFromContext(cx, parentViewId));
			}
		}
		return parentView;
	}

	/**
	 * 向视图中添加视图
	 * 
	 * @param parentView
	 * @param childView
	 * @param position
	 * @param insertType
	 */

	public static void insertViewToView(ViewGroup parentView, View childView, int position, int insertType) {
		position = position >= 0 ? position : 0;
		LayoutParams layoutParams = null;
		// LogUtil.d(TAG, "insertViewToView : inserType = " + insertType);
		switch (insertType) {
			case 1 :
				layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
				break;
			case 2 :
				layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				break;
			case 3 :
				layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
				break;
			case 4 :
				layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				break;
		}
		if (null != childView && null != parentView && null != layoutParams) {
			parentView.addView(childView, position, layoutParams);
			// parentView.invalidate();
			parentView.setVisibility(View.VISIBLE);
			childView.setVisibility(View.VISIBLE);
		} else if (null != childView && null != parentView && null == layoutParams) {
			parentView.addView(childView, position);
		} else if (null != childView) {
			LogUtil.e(TAG, "instance of content_layout is null...");
		} else {
			LogUtil.e(TAG, "instance of widget is null...");
		}
	}

	/**
	 * 向 listview 中添加更多获取到的数据 ,hasMore 为必填
	 * 
	 * @param data
	 * @param widgetName
	 * @param widgetViewId
	 */
	public static void addDataIntoWidget(String data, String widgetViewId, String fatherViewId) {
		try {
			// LogUtil.d(TAG, "insertDataIntoListView:" + widgetViewId);
			BaseWidget widget = (BaseWidget) getWidget(widgetViewId, fatherViewId);
			if (widget != null && widget instanceof ListViewWidget) {
				((ListViewWidget) widget).addData(data);
			} else if (widget != null && widget instanceof GridWidget) {
				((GridWidget) widget).addData(data);
			} else if (widget != null && widget instanceof GalleryWidget) {

			}
		} catch (Exception e) {
			LogUtil.e(TAG, "GetResouceId error:" + e.toString());
		}
	}

	/**
	 * 刷新控件数据
	 * 
	 * @param data
	 * @param widgetName
	 * @param viewId
	 */
	public static void refreshDataForWidget(String newData, String widgetId, String fatherViewId) {
		// LogUtil.d(TAG, "refershDataForWidget");
		BaseWidget widget = getWidget(widgetId, fatherViewId);
		if (null != widget) {
			widget.refreshData(newData);
		} else {
			LogUtil.e(TAG, "refershDataForWidget error : widget is null,i can not find it...");
		}
	}

	public static void refreshWidget(String widgetId, String fatherViewId) {
		// LogUtil.d(TAG, "refershWidget");
		BaseWidget widget = getWidget(widgetId, fatherViewId);
		if (null != widget) {
			widget.refreshData();
		} else {
			LogUtil.e(TAG, "refershDataForWidget error : widget is null,i can not find it...");
		}
	}

	public static void refreshWidget(String controlId) {
		BaseWidget widget = (BaseWidget) ReflectionUtil.invokeMethod(IntentUtil.getActivity(), "getWidget", controlId);
		// LogUtil.d(TAG, "refreshWidget : " + widget.toString());
		widget.refreshData();
	}

	/**
	 * 上传文件
	 * 
	 * @param controlId
	 */
	public static void upload(String controlId) {
		BaseWidget widget = (BaseWidget) getWidget(controlId);
		if (widget != null && widget instanceof GalleryWidget) {
			((GalleryWidget) widget).upload();
		}
	}

	public static void refreshBadgeView(String newData, String widgetId, String fatherViewId) {
		// LogUtil.d(TAG, "refreshBadgeView");
		BaseWidget widget = getWidget(widgetId, fatherViewId);
		if (null != widget) {
			widget.refreshBadgeView(newData);
		} else {
			LogUtil.e(TAG, "refershDataForWidget error : widget is null,i can not find it...");
		}
	}

	/**
	 * 设置控件属性
	 * 
	 * @param attrsString
	 * @param widgetId
	 * @param fatherViewId
	 */
	public static void setAttrsForWidget(String attrsString, String widgetId, String fatherViewId) {
		BaseWidget widget = getWidget(widgetId, fatherViewId);
		if (null != widget) {
			widget.setAttrs(attrsString);
		} else {
			LogUtil.e(TAG, "setAttrsForWidget error : widget is null,i can not find it...");
		}
	}

	/**
	 * 根据 widget Id 和 father view Id 获得控件
	 * 
	 * @param widgetId
	 * @param fatherViewId
	 * @return
	 */
	public static BaseWidget getWidget(String controlId, Object pageObject) {
		String string = (String) ReflectionUtil.invokeMethod(pageObject, "getParam", controlId);
		String[] ss = string.split(",");
		if (ss.length == 0) {
			return null;
		}
		String widgetId = ss[0];
		BaseWidget widget = null;
		Activity cx = ECApplication.getInstance().getNowActivity();
		int viewid = ResourceUtil.getViewIdFromContext(cx, widgetId);
		// LogUtil.d(TAG, "getWidget viewid:" + viewid);
		if (pageObject instanceof ItemActivity) {
			widget = (BaseWidget) ((ItemActivity) pageObject).findViewById(viewid);
		} else if (pageObject instanceof ItemFragment) {
			widget = (BaseWidget) ((BaseFragment) pageObject).getView().findViewById(viewid);
		}

		// LogUtil.d(TAG, "getWidget widget:" + (widget == null));
		return widget;
	}
	public static BaseWidget getWidget(String widgetId, String parentViewId) {
		// LogUtil.d(TAG, "getWidget  widgetId : " + widgetId + "  parentViewId : " + parentViewId);
		Activity cx = ECApplication.getInstance().getNowActivity();
		LinearLayout parentLayout = getParentLayout(parentViewId, PageUtil.getPageByWidgetId(widgetId));
		BaseWidget widget = null;
		try {
			if (null != parentLayout) {
				// LogUtil.d(TAG, "getWidget  1");
				widget = (BaseWidget) parentLayout.findViewById(ResourceUtil.getViewIdFromContext(cx, widgetId));
			} else {
				// LogUtil.d(TAG, "getWidget  2");
				widget = (BaseWidget) cx.findViewById(ResourceUtil.getViewIdFromContext(cx, widgetId));
			}
		} catch (Exception e) {
			LogUtil.e(TAG, "ohoh,widgetId is not valid or the view is not extends from BaseWidget...");
		}

		return widget;
	}

	public static BaseWidget getWidget(String controlId, String widgetId, String parentViewId) {
		// LogUtil.d(TAG, "getWidget  widgetId : " + widgetId + "  parentViewId : " + parentViewId);
		Activity cx = ECApplication.getInstance().getNowActivity();
		LinearLayout parentLayout = getParentLayout(parentViewId, PageUtil.getPageByWidgetId(controlId));
		BaseWidget widget = null;
		try {
			if (null != parentLayout) {
				// LogUtil.d(TAG, "getWidget  1");
				widget = (BaseWidget) parentLayout.findViewById(ResourceUtil.getViewIdFromContext(cx, widgetId));
			} else {
				// LogUtil.d(TAG, "getWidget  2");
				widget = (BaseWidget) cx.findViewById(ResourceUtil.getViewIdFromContext(cx, widgetId));
			}
		} catch (Exception e) {
			LogUtil.e(TAG, "ohoh,widgetId is not valid or the view is not extends from BaseWidget...");
		}

		return widget;
	}

	/**
	 * 根据controlid获得控件
	 * 
	 * @param pageContext
	 * @param controlId
	 * @return
	 */
	public static BaseWidget getWidget(String controlId) {
		// LogUtil.d(TAG, "getWidget start:" + controlId);
		Object pageObject = PageUtil.getPageByWidgetId(controlId);
		// LogUtil.d(TAG, "getWidget :"+(pageObject == null));
		// if (controlId.equals("page_usercenter_ListViewBase_0") || controlId.equals("page_care_ListViewBase_0")) {
		// LogUtil.d(TAG, "getWidget start:" + ((BaseFragment) pageObject).getmMap().toString());
		// }
		String string = (String) ReflectionUtil.invokeMethod(pageObject, "getParam", controlId);
		if (StringUtil.isEmpty(string)) {
			string = (String) ReflectionUtil.invokeMethod(IntentUtil.getActivity(), "getParam", controlId);
		}
		if (StringUtil.isEmpty(string)) {
			return null;
		}
		String[] ss = string.split(",");
		if (ss.length == 0) {
			return null;
		}
		String widgetId = ss[0];
		String parentViewId = null;
		if (ss.length >= 1) {
			parentViewId = ss[1];
		}
		if (StringUtil.isEmpty(widgetId) || StringUtil.isEmpty(parentViewId)) {
			// LogUtil.d(TAG, "widgetId or parentViewId is null");
			return null;
		}
		// LogUtil.d(TAG, "getWidget widgetId:" + widgetId);
		return getWidget(controlId, widgetId, parentViewId);
	}

	public interface showDialog {
		public void show(showDialog dialog);
	}

	public static void ShowConfirm(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(ECApplication.getInstance().getNowActivity());
		builder.setTitle("");
		builder.setMessage(message);
		builder.setPositiveButton("确认", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// LogUtil.d(TAG, "onClick : ShowConfirm  Confirm ...");
				// JsManager.getInstance().callJsMethodSync(PageUtil.getNowPageId(), "confirm", null);
			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// JsManager.getInstance().callJsMethodSync(PageUtil.getNowPageId(), "cancel", null);
			}
		});
		builder.create().show();
	}

	/**
	 * 弹出 确认框
	 * 
	 * @param message
	 * @param okTag
	 * @param cancelTag
	 */
	public static void ShowConfirm(String message, final String okTag, final String cancelTag) {
		if (StringUtil.isEmpty(okTag) && StringUtil.isEmpty(cancelTag)) {
			ShowConfirm(message);
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(ECApplication.getInstance().getNowActivity());
		builder.setTitle("");
		builder.setMessage(message);
		builder.setPositiveButton("确认", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				EventBus.getDefault().post(new DoActionEvent(okTag));
			}
		});
		if (StringUtil.isNotEmpty(cancelTag)) {
			builder.setNegativeButton("取消", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					EventBus.getDefault().post(new DoActionEvent(cancelTag));
				}
			});
		}
		builder.create().show();
	}

	/**
	 * 为控件添加标签
	 * 
	 * @param hashMString
	 * @param widgetViewId
	 * @param fatherViewId
	 */
	public static void addTag(String hashMString, String widgetViewId, String fatherViewId) {
		BaseWidget widget = getWidget(widgetViewId, fatherViewId);
		if (null != widget && StringUtil.isNotEmpty(hashMString)) {
			HashMapItemModel hashMModel = null;
			try {
				hashMModel = GsonUtil.fromJson(hashMString, HashMapItemModel.class);
			} catch (Exception e) {
				LogUtil.e(TAG, "addTag error: HashMapItemModel string is invalid...");
			}
			if (null != hashMModel) {

				widget.putParam(hashMModel.getKey(), hashMModel.getValue());
			}
		} else if (StringUtil.isNotEmpty(hashMString)) {
			LogUtil.e(TAG, "addTag error: widget is null...");
		} else {
			LogUtil.e(TAG, "addTag error: hashMString is null...");
		}
	}

	/**
	 * 为控件添加标签
	 * 
	 * @param hashMLString
	 * @param widgetViewId
	 * @param fatherViewId
	 */
	public static void addTags(String hashMLString, String widgetViewId, String fatherViewId) {
		BaseWidget widget = getWidget(widgetViewId, fatherViewId);
		if (null != widget && StringUtil.isNotEmpty(hashMLString)) {
			HashMapListModel hashMListModel = null;
			try {
				hashMListModel = GsonUtil.fromJson(hashMLString, HashMapListModel.class);
			} catch (Exception e) {
				LogUtil.e(TAG, "addTags error: HashMapListModel string is invalid...");
			}
			if (null != hashMListModel) {
				for (HashMapItemModel hashMModel : hashMListModel.getHashMList()) {
					widget.putParam(hashMModel.getKey(), hashMModel.getValue());
				}
			}
		}
	}

	/**
	 * 获得 控件标签
	 * 
	 * @param key
	 * @param widgetViewId
	 * @param fatherViewId
	 * @return
	 */
	public static String getTag(String key, String widgetViewId, String fatherViewId) {
		BaseWidget widget = getWidget(widgetViewId, fatherViewId);
		if (null != widget && StringUtil.isNotEmpty(key)) {
			return widget.getParam(key);
		}
		return null;
	}

	/**
	 * 显示加载等待对话框
	 * 
	 * @param context
	 * @param loadingTitle
	 * @param loadingMessage
	 * @param cancelable
	 */
	public static void showLoadingDialog(Context context, String loadingTitle, String loadingMessage, boolean cancelable) {
		if (null != progressDialog) {
			closeLoadingDianlog();
		}
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle(loadingTitle);
		progressDialog.setMessage(loadingMessage);
		progressDialog.setCancelable(cancelable);
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
	}

	public static void closeLoadingDianlog() {

		if (progressDialog != null) {
			try {
				progressDialog.dismiss();
			} catch (Exception e) {
				LogUtil.e(TAG, "closeLoadingDianlog error: " + e.toString());
			}
			progressDialog = null;
		}
	}

	/**
	 * 打开弹出式窗口
	 * 
	 * @param positionId
	 * @param controlId
	 */

	public static void openPopupwindow(String positionId, String controlId) {
		// LogUtil.d(TAG, "openPopupwindow");
		if (null != popupWindow && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
			return;
		} else if (null != popupWindow) {
			popupWindow = null;
		}
		View widgetView = (View) PageUtil.initWidgetNamed(controlId, PageUtil.getNowPageContext());
		if (null != widgetView && null != widgetView.getParent())
			((ViewGroup) (widgetView.getParent())).removeView(widgetView);
		LinearLayout linearLayout = new LinearLayout(IntentUtil.getActivity());
		linearLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		linearLayout.addView(widgetView);
		linearLayout.setBackgroundColor(ColorUtil.getColorValueFromRGB("#30000000"));
		if (popupWindow == null) {
			popupWindow = new PopupWindow(linearLayout, android.view.ViewGroup.LayoutParams.FILL_PARENT, android.view.ViewGroup.LayoutParams.FILL_PARENT);
			// popupWindow = new PopupWindow(widgetView,
			// android.view.ViewGroup.LayoutParams.FILL_PARENT,
			// DensityUtil.screenWidthInDip() * 3 / 2);
			popupWindow.setAnimationStyle(R.style.SecondaryMenuAnimation);
			popupWindow.setFocusable(true);
			// popupWindow.setOutsideTouchable(true);
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
		}
		popupWindow.showAsDropDown(ResourceUtil.getViewByid(positionId));
	}

	/**
	 * 关闭弹出式窗口
	 * 
	 * @param positionId
	 * @param widgetDataString
	 */
	public static void closePopupwindow() {
		if (null != popupWindow && popupWindow.isShowing()) {
			popupWindow.dismiss();
			popupWindow = null;
			return;
		}
	}

	public static PopupWindow initPopupWindow(String layoutName) {
		View root = LayoutInflater.from(IntentUtil.getActivity()).inflate(ResourceUtil.getLayoutIdFromContext(IntentUtil.getActivity(), layoutName), null);
		final PopupWindow popup = new PopupWindow(root, android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		popup.setFocusable(true);
		popup.setAnimationStyle(R.style.PopupAnimation);
		popup.setOutsideTouchable(true);
		popup.showAtLocation(IntentUtil.getActivity().findViewById(R.id.activity_item_container_llayout), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

		return popup;
	}

	/**
	 * 打开对话框
	 * 
	 * @param widgetDataString
	 */
	public static void openDialog(String title, String controlId) {
		if (null != aDialog && aDialog.isShowing()) {
			aDialog.dismiss();
			aDialog = null;
			return;
		}
		Activity nowActivity = IntentUtil.getActivity();
		AlertDialog.Builder builder = new AlertDialog.Builder(nowActivity);
		if (StringUtil.isNotEmpty(title))
			builder.setTitle(title);
		if (StringUtil.isEmpty(controlId)) {
			// LogUtil.d(TAG, "openDialog controlId is null");
			return;
		}
		View widgetView = (View) PageUtil.initWidgetNamed(controlId, nowActivity);
		if (null != widgetView.getParent())
			((ViewGroup) (widgetView.getParent())).removeView(widgetView);
		widgetView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		builder.setView(widgetView);
		aDialog = builder.create();
		aDialog.show();
	}

	public static void openCustomDialog(View view, boolean withConfirm, boolean withCancle) {
		if (null != aDialog && aDialog.isShowing()) {
			aDialog.dismiss();
			aDialog = null;
			return;
		} else if (null != aDialog) {
			aDialog = null;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(IntentUtil.getActivity());
		if (view.getParent() != null) {
			((ViewGroup) view.getParent()).removeView(view);
		}
		builder.setView(view);
		if (withConfirm) {
			builder.setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
		}
		if (withCancle) {
			builder.setNegativeButton("取消", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
		}
		aDialog = builder.create();
		aDialog.show();

	}

	/*
	 * 关闭对话框
	 */
	public static void closeDialog() {
		if (null != aDialog && aDialog.isShowing()) {
			aDialog.dismiss();
			aDialog = null;
		}
	}

	public static void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) IntentUtil.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		try {
			imm.hideSoftInputFromWindow(IntentUtil.getActivity().getCurrentFocus().getWindowToken(), 0);
		} catch (Exception e) {
			LogUtil.e(TAG, "hideKeyboard error: " + e.toString());
		}
	}

	/**
	 * 设置试图背景
	 * 
	 * @param view
	 * @param bgString
	 */

	public static void setBackground(View view, String bgString) {
		if (bgString.startsWith("#")) {
			view.setBackgroundColor(ColorUtil.getColorValueFromRGB(bgString));
			return;
		}
		Drawable drawable = IntentUtil.getActivity().getResources().getDrawable(ResourceUtil.getDrawableIdFromContext(IntentUtil.getActivity(), bgString));
		if (drawable != null)
			view.setBackgroundDrawable(drawable);
	}

}
