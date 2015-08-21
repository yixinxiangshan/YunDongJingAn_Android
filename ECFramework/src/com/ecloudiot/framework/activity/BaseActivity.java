package com.ecloudiot.framework.activity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.event.DoActionEvent;
import com.ecloudiot.framework.event.linterface.OnNavItemClickListener;
import com.ecloudiot.framework.fragment.BaseFragment;
import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.utility.ActivityResultUtil;
import com.ecloudiot.framework.utility.ColorUtil;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.ImageUtil;
import com.ecloudiot.framework.utility.IntentUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.widget.ActionBarWidget;
import com.ecloudiot.framework.widget.BaseWidget;
import com.ecloudiot.framework.widget.model.ActionBarModel;
import com.ecloudiot.framework.widget.model.ActionMenuItemModel;
import com.ecloudiot.framework.widget.model.ActionMenuListModel;
import com.ecloudiot.framework.widget.model.ActionNavigationListModel;
import com.ecloudiot.framework.widget.model.ActionNavigationListModel.ActionNavigationItemModel;
import com.ecloudiot.framework.widget.model.KeyWidgetModel;
import com.pgyersdk.feedback.PgyFeedbackShakeManager;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

@SuppressLint({"NewApi", "InflateParams"})
public class BaseActivity extends ActionBarActivity implements OnNavigationListener {
	private String TAG = "BaseActivity";
	private ActionNavigationListModel navList = null;
	private OnNavItemClickListener navItemClickListener = null;
	// private OnOptionItemClickListener optionItemClickListener = null;
	public Map<String, Object> listenerMap;
	private ActionMenuListModel dataMenuList = null;
	private Bundle mBundle = null;
	private ActionBarWidget actionBarWidget;
	protected HashMap<String, BaseWidget> wMap = new HashMap<String, BaseWidget>();
	protected HashMap<String, String> jsEvents = new HashMap<String, String>();
	protected HashMap<String, String> widgetDatas = new HashMap<String, String>();
	protected HashMap<String, BaseFragment> fragments = new HashMap<String, BaseFragment>();
	protected HashMap<String, HashMap<String, String>> widgetJsEvents = new HashMap<String, HashMap<String, String>>();
	private View titleLayout;

	// 让menu显示图标
	@Override
	public boolean onMenuOpened(int featureId, Menu menu) {
		if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
			if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
				try {
					Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
					m.setAccessible(true);
					m.invoke(menu, true);
				} catch (Exception e) {
				}
			}
		}
		return super.onMenuOpened(featureId, menu);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		LogUtil.d(TAG, "onCreateOptionsMenu start");
		LogUtil.d(TAG, "onPrepareOptionsMenu:" + menu.getClass().getSimpleName());
		if (null != dataMenuList) {
			for (int i = 0; i < dataMenuList.getMenuList().size(); i++) {
				ActionMenuItemModel menuItemModel = dataMenuList.getMenuList().get(i);
				menu.add(Menu.NONE, ResourceUtil.getViewIdFromContext(this, menuItemModel.getItemId()), i, menuItemModel.getTitle());
				MenuItem menuItem = menu.getItem(i);
				setActionItem(menuItemModel, menuItem);
			}
		}
		super.onCreateOptionsMenu(menu);
		return true;
	}

	protected void onCreate(Bundle savedInstanceState) {
		// TODO 这里强行设置了style，需要改动
		setTheme(R.style.AppThemeDefault);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		// getWindow().requestFeature((int) Window.FEATURE_ACTION_BAR_OVERLAY);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		super.onCreate(savedInstanceState);
		ECApplication.getInstance().setOnActivityCreate(this);
		
		// setSupportProgressBarIndeterminateVisibility(false);
		// setSupportProgressBarVisibility(false);
		// 初始化
		listenerMap = new HashMap<String, Object>();
		mBundle = this.getIntent().getExtras();
		if (null != mBundle) {
			String uri = mBundle.getString("uri");
			// LogUtil.d(TAG, "BaseActivity uri:" + uri);
			parsingUri(uri);
		}
	}

	// 为activity标记一个widget
	public void putWidget(KeyWidgetModel widget) {
		wMap.put(widget.getKey(), widget.getWidget());
	}

	// 查找widget下的widget
	public BaseWidget getWidget(String key) {
		try {
			return wMap.get(key);
		} catch (Exception e) {
			LogUtil.e(TAG, "getWidget error: " + e.toString());
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageStart("BaseActivity"); // 统计页面
		MobclickAgent.onPause(this);// 友盟统计
	}

	@Override
	protected void onResume() {
		ECApplication.getInstance().setOnActivityResume(this);
		super.onResume();
		MobclickAgent.onPageEnd("BaseActivity");
		MobclickAgent.onResume(this);// 友盟统计

	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		TestinAgent.onStop(this);// 此行必须放在super.onStop后
	}

	@Override
	protected void onDestroy() {
		ECApplication.getInstance().setOnActivityDestroy(this);
		// LogUtil.w(TAG, "onDestroy :  " + this);
		super.onDestroy();
	}

	/**
	 * 设置 action bar 是否可见
	 * 
	 * @param isVisible
	 */
	public void setActionBarVisible(boolean isVisible) {
		if (isVisible) {
			getSupportActionBar().show();
		} else {
			getSupportActionBar().hide();
		}
	}

	/**
	 * 设置 actionBar 背景
	 * 
	 * @param barBgImgName
	 */
	public void setActionBarBg(String barBgImgName) {
		try {
			if (barBgImgName.startsWith("#")) {
				ColorDrawable colorDrawable = new ColorDrawable(ColorUtil.getColorValueFromRGB(barBgImgName));
				getSupportActionBar().setBackgroundDrawable(colorDrawable);
			} else if (StringUtil.isImageName(barBgImgName)) {

			} else {
				BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(ResourceUtil.getDrawableIdFromContext(this, barBgImgName));
				bitmapDrawable.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
				getSupportActionBar().setBackgroundDrawable(bitmapDrawable);
			}

		} catch (Exception e) {
			LogUtil.e(TAG, "setActionBarBg error : action bar backgroud image name may be invalid...");
			e.printStackTrace();
		}
	}

	/**
	 * 设置 action bar home item 是否可见
	 * 
	 * @param isVisible
	 */
	public void setActionHomeItemVisible(boolean isVisible) {
		if (isVisible)
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		else
			getSupportActionBar().setDisplayShowHomeEnabled(false);
	}

	/**
	 * 设置 action bar title 内容，为空则不可见
	 * 
	 * @param title
	 */
	public void setActionTitle(String title) {
		if (StringUtil.isNotEmpty(title)) {
			getSupportActionBar().setDisplayShowTitleEnabled(true);
			getSupportActionBar().setTitle(title);
		} else {
			getSupportActionBar().setDisplayShowTitleEnabled(false);
		}
	}
	/**
	 * 设置 action bar title 内容，为空则不可见
	 * 
	 * @param title
	 */
	public String getActionTitle() {
		return (String) getSupportActionBar().getTitle();
	}

	/**
	 * 设置 action bar subTitle 内容，为空则不可见
	 * 
	 * @param subTitle
	 */
	public void setActionSubTitle(String subTitle) {
		if (StringUtil.isNotEmpty(subTitle)) {
			getSupportActionBar().setSubtitle(subTitle);
		} else {
			getSupportActionBar().setSubtitle(null);
		}
	}

	/**
	 * 设置 action bar home item 是否带向左箭头
	 * 
	 * @param isUp
	 */
	public void setHomeAsUp(boolean isUp) {
		// LogUtil.d(TAG, "setHomeAsUp: " + isUp);
		if (isUp) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		} else {
			getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		}
	}

	public void setHomeClickTag(String homeClickTag) {
		// TODO delete
	}

	/**
	 * 设置进度条进度，有效范围为0-1,精确到小数点后四位
	 * 
	 * @param progress
	 */
	public void setProgress(float progress) {
		if (progress > 0.0f && progress < 1.0f) {
			setSupportProgressBarVisibility(true);
			setSupportProgressBarIndeterminateVisibility(false);
			setSupportProgress((int) (progress * 10000));
		} else {
			setSupportProgressBarVisibility(false);
		}
	}

	/**
	 * 设置进度条,进度不确定
	 * 
	 * @param progress
	 */
	public void setProgressIndeterminateVisible(boolean isVisible) {
		if (isVisible) {
			// LogUtil.d(TAG, "setProgressIndeterminateVisible 可见");
			setSupportProgress(Window.PROGRESS_END);
			setSupportProgressBarIndeterminateVisibility(true);
		} else {
			setSupportProgressBarIndeterminateVisibility(false);
		}
	}

	/**
	 * 添加 menu item，每次修改这个文件
	 * 
	 * @param menuName
	 */
	public void addActionItem(String menuItemString) {
		ActionMenuItemModel actionMenuItemModel = GsonUtil.fromJson(menuItemString, ActionMenuItemModel.class);
		if (null == dataMenuList) {
			dataMenuList = new ActionMenuListModel();
		}
		if (null == dataMenuList.getMenuList()) {
			dataMenuList.setMenuList(new ArrayList<ActionMenuItemModel>());
		}
		dataMenuList.getMenuList().add(actionMenuItemModel);
		invalidateOptionsMenu();
	}

	/**
	 * 根据 itemId 删除actionItem
	 * 
	 * @param menuItemId
	 */
	public void removeActionItem(String menuItemId) {
		if (null != dataMenuList) {
			for (int i = 0; i < dataMenuList.getMenuList().size(); i++) {
				ActionMenuItemModel menuItemModel = dataMenuList.getMenuList().get(i);
				if (menuItemModel.getItemId().equalsIgnoreCase(menuItemId)) {
					dataMenuList.getMenuList().remove(menuItemModel);
					break;
				}
			}
			invalidateOptionsMenu();
		}
	}

	/**
	 * 删除所有 action item
	 */
	public void removeAllActionItems() {
		if (null != dataMenuList) {
			dataMenuList = null;
			invalidateOptionsMenu();
		}
	}

	/**
	 * 设置menu item
	 * 
	 * @param dataMenuString
	 */
	public void initActionMenuItems(String dataMenuString) {
		ActionMenuListModel menuListModel = null;
		try {
			menuListModel = GsonUtil.fromJson(dataMenuString, ActionMenuListModel.class);
		} catch (Exception e) {
			LogUtil.e(TAG, "setActionItem error : dataMenuString is invalid...");
			e.printStackTrace();
			return;
		}
		initActionMenuItems(menuListModel);
	}

	/**
	 * 设置menu item
	 * 
	 * @param menuListModel
	 */
	public void initActionMenuItems(ActionMenuListModel menuListModel) {
		if (null != menuListModel && menuListModel.getMenuList() != null) {
			this.dataMenuList = menuListModel;
		} else {
			removeAllActionItems();
		}
		invalidateOptionsMenu();
	}

	/**
	 * 设置menu item
	 * 
	 * @param dataMenuList
	 */
	@SuppressLint("ResourceAsColor")
	public void setActionItem(final ActionMenuItemModel menuItemModel, final MenuItem menuItem) {
		if (null != menuItem && StringUtil.isNotEmpty(menuItemModel.getShowAsAction())) {
			if (menuItemModel.getShowAsAction().equalsIgnoreCase("always")) {
				menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			} else if (menuItemModel.getShowAsAction().equalsIgnoreCase("never")) {
				menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
			} else if (menuItemModel.getShowAsAction().equalsIgnoreCase("ifroom")) {
				menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			}
		}
        if (!menuItemModel.getClickTag().isEmpty()) {
            menuItem.setNumericShortcut(menuItemModel.getClickTag().charAt(0));
        }
        if (null != menuItem && StringUtil.isNotEmpty(menuItemModel.getActionViewName())) {
			// customView
			try {
				menuItem.setActionView(ResourceUtil.getLayoutIdFromContext(this, menuItemModel.getActionViewName()));
			} catch (Exception e) {
				LogUtil.e(TAG, "action bar item actionview name is invalid : " + menuItemModel.getActionViewName());
				e.printStackTrace();
			}
			// 设置 icon for custom view
			if (StringUtil.isNotEmpty(menuItemModel.getIconName())) {
				ImageView iconImg = (ImageView) menuItem.getActionView().findViewById(
				        ResourceUtil.getViewIdFromContext(this, "actionbar_menu_item_custom_imgview"));
				// 获取图片，如果资源里没有，就从config目录获取
				try {
					Integer res_id = ResourceUtil.getDrawableIdFromContext(this, menuItemModel.getIconName());
					if (res_id == 0)
						iconImg.setImageBitmap(ImageUtil.getBitmapFromConfig(menuItemModel.getIconName()));
					else
						iconImg.setImageResource(res_id);
				} catch (Exception e) {
					LogUtil.e(TAG, "action bar item IconName  is invalid : " + menuItemModel.getIconName());
					e.printStackTrace();
				}
			}
			// 设置 title for custom view
			if (StringUtil.isNotEmpty(menuItemModel.getTitle())) {
				TextView textView = (TextView) menuItem.getActionView().findViewById(
				        ResourceUtil.getViewIdFromContext(this, "actionbar_menu_item_custom_titleview"));
				textView.setText(menuItemModel.getTitle());
				menuItem.setTitle(menuItemModel.getTitle());
				// menuItem.setTextColor(android.R.color.black);
			}
			// 设置是否 折叠
			if (menuItemModel.isNeverCollapses()) {
				menuItem.expandActionView();
			}
			// 添加点击事件
			menuItem.getActionView().setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// LogUtil.d(TAG, "menuItem.getActionView() onClick");
					// if (null != customItemClickListener) {
					// customItemClickListener.onClick(v, menuItem);
					// }
				}
			});
		} else if (null != menuItem) {
			// 没有自定义 view
			// 设置 icon
			if (StringUtil.isNotEmpty(menuItemModel.getIconName())) {
				menuItem.setIcon(ResourceUtil.getDrawableIdFromContext(this, menuItemModel.getIconName()));
				// 获取图片，如果资源里没有，就从config目录获取
				try {
					Integer res_id = ResourceUtil.getDrawableIdFromContext(this, menuItemModel.getIconName());
					if (res_id == 0) {
						menuItem.setIcon(ImageUtil.getDrawableFromConfig(menuItemModel.getIconName()));
						// LogUtil.d(TAG, "icon : " + menuItemModel.getIconName());
					} else
						menuItem.setIcon(res_id);
				} catch (Exception e) {
					LogUtil.e(TAG, "action bar item IconName  is invalid : " + menuItemModel.getIconName());
					e.printStackTrace();
				}

			}
			// // 设置 title
			// if (StringUtil.isNotEmpty(menuItemModel.getTitle())) {
			// menuItem.setTitle(menuItemModel.getTitle());
			//
			// }
		} else {
			LogUtil.e(TAG, "action bar menu item id is invalid...");
		}
	}
	// 屏蔽掉物理Menu键，不然在有物理Menu键的手机上，overflow按钮会显示不出来
	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 添加带下拉列表的 item
	 * 
	 * @param arrayName
	 * @param navListString
	 */
	public void addNavigationList(String navListString) {
		ActionNavigationListModel navListModel = null;
		try {
			navListModel = GsonUtil.fromJson(navListString, ActionNavigationListModel.class);
		} catch (Exception e1) {
			LogUtil.e(TAG, "navListString is invalid...");
			e1.printStackTrace();
			return;
		}
		addNavigationList(navListModel);
	}

	/**
	 * 添加带下拉列表的 item
	 * 
	 * @param arrayName
	 * @param navListModel
	 */
	public void addNavigationList(ActionNavigationListModel navListModel) {
		this.navList = navListModel;
		if (null == navListModel || null == navListModel.getNavTagList() || navListModel.getNavTagList().size() <= 0) {
			// LogUtil.e(TAG, "addNavigationList error: navListModel is null,maybe it is OK !");
			getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			return;
		}
		List<String> listTitle = new ArrayList<String>();
		for (ActionNavigationItemModel itemModel : this.navList.getNavTagList()) {
			listTitle.add(itemModel.getTitle());
		}
		ArrayAdapter<String> listAdapter = null;
		try {
			// listAdapter = new ArrayAdapter<String>(this, R.layout.sherlock_spinner_item, listTitle);
		} catch (Exception e) {
			LogUtil.e(TAG, "get navigation list error...");
			e.printStackTrace();
			return;
		}
		// listAdapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);

		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setListNavigationCallbacks(listAdapter, this);
		if (navListModel.getSelectedItem() >= 0) {
			getSupportActionBar().setSelectedNavigationItem(navListModel.getSelectedItem());
		}
	}

	/**
	 * 删除navList
	 */
	public void removeNavList() {
		this.navList = null;
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	/**
	 * 初始化 action bar
	 * 
	 * @param dataString
	 */
	public void initActionBar(String dataString) {
		// LogUtil.d(TAG, "init action bar string : " + dataString);
		ActionBarModel barModel = null;
		try {
			barModel = GsonUtil.fromJson(dataString, ActionBarModel.class);
		} catch (Exception e) {
			LogUtil.e(TAG, "initActionBar error : data string is not valid...");
			e.printStackTrace();
		}
		if (null != barModel) {
			initActionBar(barModel, this.actionBarWidget);
		} else {
			LogUtil.e(TAG, "initActionBar error: barModel is null...");
		}
	}

	/**
	 * 初始化action bar
	 * 
	 * @param barModel
	 */
	public void initActionBar(ActionBarModel barModel, ActionBarWidget actionBarWidget) {
		// LogUtil.d(TAG, "init barModel string : " + GsonUtil.toJson(barModel));
		this.actionBarWidget = actionBarWidget;

		if (null != barModel) {
			if (!barModel.isWithActionBar()) {
				setActionBarVisible(false);
				return;
			} else if (!getSupportActionBar().isShowing()) {
				setActionBarVisible(true);
			}
			// 设置是否需要返回
			if (barModel.isWithHomeAsUp() && StringUtil.isEmpty(barModel.getHomeIcon())) {
				getSupportActionBar().setIcon(ResourceUtil.getDrawableIdFromContext(this, "widget_actionbar_default_back"));
				setHomeAsUp(false);
			} else {
				setHomeAsUp(barModel.isWithHomeAsUp());
			}
			// 设置 左边 home item
			if (!StringUtil.isEmpty(barModel.getHomeIcon())) {
				setHomeAsUp(false);
				getSupportActionBar().setDisplayShowHomeEnabled(true);
				if (StringUtil.isNotEmpty(barModel.getHomeIcon())) {
					try {
						// 获取图片，如果资源里没有，就从config目录获取
						Integer res_id = ResourceUtil.getDrawableIdFromContext(this, barModel.getHomeIcon());
						if (res_id == 0) {
							getSupportActionBar().setIcon(ImageUtil.getDrawableFromConfig(barModel.getHomeIcon()));
						} else {
							getSupportActionBar().setIcon(res_id);
						}
					} catch (Exception e) {
						LogUtil.e(TAG, "initActionBar error : home icon string is an invalid resouce");
						e.printStackTrace();
					}
				}
			} else {
				LogUtil.e(TAG, "action bar home IconName  not use ");
				// getSupportActionBar().setDisplayShowHomeEnabled(false);
			}

			// 背景
			if (StringUtil.isNotEmpty(barModel.getActionBarBg())) {
				// try {
				// // 获取图片，如果资源里没有，就从config目录获取
				// Integer res_id = ResourceUtil.getDrawableIdFromContext(this, barModel.getActionBarBg());
				// if (res_id == 0) {
				// getSupportActionBar().setBackgroundDrawable(ImageUtil.getDrawableFromConfig(barModel.getActionBarBg()));
				// // getSupportActionBar().setIcon(ImageUtil.getDrawableFromConfig(barModel.getHomeIcon()));
				// } else {
				// getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(res_id));
				// }
				// } catch (Exception e) {
				// LogUtil.e(TAG, "initActionBar error : home getActionBarBg string is an invalid resouce");
				// e.printStackTrace();
				// }
				setActionBarBg(barModel.getActionBarBg());
				// getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.activity_item_actionbar_bg));
			}
			// 显示自定义视图
			titleLayout = LayoutInflater.from(this).inflate(R.layout.widget_actionbar_title_custom, null);
			getSupportActionBar().setDisplayShowCustomEnabled(true);
			getSupportActionBar().setCustomView(titleLayout);
			// 设置title
			setActionBarTitle(barModel.getTitle(), barModel.getTitleIcon());
			// 设置 subTitle
			if (StringUtil.isNotEmpty(barModel.getSubTitle())) {
				setActionSubTitle(barModel.getSubTitle());
			}
			// 设置nav list
			if (null != barModel.getNavTagData()) {
				addNavigationList(barModel.getNavTagData());
			}

			// 设置 menu item
			if (null != barModel.getMenuItemsData()) {
				setOverflowShowingAlways();
				initActionMenuItems(barModel.getMenuItemsData());
			} else {
				removeAllActionItems();
			}
		} else {
			LogUtil.e(TAG, "bar model is null...");
		}
	}
	// 设置title
	public void setActionBarTitle(String title, String titleIcon) {
		// 设置 title
		if (StringUtil.isNotEmpty(title)) {
			// setActionTitle(barModel.getTitle());
			TextView titleTextView = (TextView) titleLayout.findViewById(R.id.widget_actionbar_title_custom_title);
			titleTextView.setText(title);

		}
		// 设置titile图片
		// 如果设置为justClick,只是启动Title的click事件
		if (StringUtil.isNotEmpty(titleIcon)) {
			if(!titleIcon.equals("justClick")){
				ImageView imageView = (ImageView) titleLayout.findViewById(R.id.widget_actionbar_title_custom_rightimage);
				try {
					// 获取图片，如果资源里没有，就从config目录获取
					Integer res_id = ResourceUtil.getDrawableIdFromContext(this, titleIcon);
					if (res_id == 0) {
						imageView.setBackgroundDrawable(ImageUtil.getDrawableFromConfig(titleIcon));
					} else {
						imageView.setBackgroundDrawable(getResources().getDrawable(res_id));
					}
				} catch (Exception e) {
					LogUtil.e(TAG, "initActionBar error : home getActionBarBg string is an invalid resouce");
					e.printStackTrace();
				}
			}
			// 添加title点击事件
			LinearLayout clickView = (LinearLayout) titleLayout.findViewById(R.id.widget_actionbar_title_custom_clickview);
			final BaseActivity activity = this;
			final HashMap<String, String> eventParams = new HashMap<String, String>();
			eventParams.put("title", title);
			clickView.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					JsAPI.runEvent(((BaseActivity) activity).getWidgetJsEvents(), "ActionBar", "onItemClick", new JSONObject(eventParams));
				}
			});
		}
	}
	/**
	 * 解析ActionBarUri和内容Uri
	 * 
	 * @param uri
	 */
	public void parsingUri(String uri) {
		// LogUtil.d(TAG, "parsingUri: " + uri);
		if (StringUtil.isEmpty(uri)) {
			return;
		}
		if (uri.contains(",")) {
			String[] uris = uri.split(",");
			if (uris.length <= 0) {
				return;
			}
			for (int i = 0; i < uris.length; i++) {
				EventBus.getDefault().post(new DoActionEvent(uris[i]));
			}
		} else {
			EventBus.getDefault().post(new DoActionEvent(uri));
		}

	}

	/**
	 * nav List item 点击事件
	 */
	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		// LogUtil.d(TAG, "Navigation Item Selected: " + itemPosition + "itemId = " + itemId + "/");
		HashMap<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("position", "" + itemPosition);
		eventParams.put("controlId", actionBarWidget.getControlId());
		OnNavItemClickListener onNavItemClickListener = null;
		try {
			onNavItemClickListener = (OnNavItemClickListener) listenerMap.get("" + itemPosition);
		} catch (Exception e) {
			LogUtil.e(TAG, "onNavigationItemSelected error: can not find special listener for this item");
			e.printStackTrace();
		}
		if (null != onNavItemClickListener) {
			onNavItemClickListener.onClick(itemPosition, itemId);
			return true;
		}
		if (null != this.navItemClickListener) {
			navItemClickListener.onClick(itemPosition, itemId);
			return true;
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// String itemId = getResources().getResourceEntryName(item.getItemId());
		// if (item.getItemId() == android.R.id.home)
		// itemId = "home";
		// LogUtil.d(TAG, "onOptionsItemSelected : item id = " + itemId);
		// HashMap<String, String> eventParams = new HashMap<String, String>();

		// eventParams.put("itemId", itemId);
		// String controlId = "page_no_actionbar";
		// if (actionBarWidget != null) {
		// controlId = actionBarWidget.getControlId();
		// }
		// eventParams.put("controlId", controlId);
		// if (JsManager.getInstance().callJsMethodSync(controlId, "onOptionItemClick", eventParams).equalsIgnoreCase("true")) {
		// return true;
		// }

		// OnOptionItemClickListener onOptionItemClickListener = null;
		// try {
		// onOptionItemClickListener = (OnOptionItemClickListener) listenerMap.get(itemId);
		// } catch (Exception e) {
		// LogUtil.e(TAG, "onNavigationItemSelected error: can not find special listener for this item");
		// e.printStackTrace();
		// }
		// if (null != onOptionItemClickListener) {
		// onOptionItemClickListener.onClick(item);
		// return true;
		// }
		// LogUtil.d(TAG, "onOptionsItemSelected : onOptionItemClickListener is null ...");
		if (item.getItemId() == android.R.id.home) {
			// LogUtil.d(TAG, "onOptionsItemSelected");
			String result = "";
			// 执行js里面的事件，actionbar比较特殊，需要处理所有子fragment中的事件
			if (!fragments.equals(null)) {
				Iterator<Entry<String, BaseFragment>> iter = fragments.entrySet().iterator();
				// LogUtil.d(TAG, ((ItemActivity) this).getParam("page_id") + " - fragments num :" + fragments.size() + "");
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					result = JsAPI.runEvent(((ItemFragment) entry.getValue()).getWidgetJsEvents(), "ActionBar", "onBackClick", "");
					if (result != null && result.equals("_false"))
						return true;
				}
			}
			result = JsAPI.runEvent(((ItemActivity) this).getWidgetJsEvents(), "ActionBar", "onBackClick", "");
			if (result != null && result.equals("_false"))
				return true;
			IntentUtil.closeActivity();
			return true;
		} else {
			// 执行js里面的事件，actionbar比较特殊，需要处理所有子fragment中的事件
			if (!fragments.equals(null)) {
				Iterator<Entry<String, BaseFragment>> iter = fragments.entrySet().iterator();
				// LogUtil.d(TAG, ((ItemActivity) this).getParam("page_id") + " - fragments num :" + fragments.size() + "");
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
//					JsAPI.runEvent(((ItemFragment) entry.getValue()).getWidgetJsEvents(), "ActionBar", "onItemClick", item.getOrder() + "");
                    JsAPI.runEvent(((ItemFragment) entry.getValue()).getWidgetJsEvents(), "ActionBar", "onItemClick", item.getNumericShortcut() + "");
				}
			}
			JsAPI.runEvent(((ItemActivity) this).getWidgetJsEvents(), "ActionBar", "onItemClick", item.getTitle() + "");
		}
		// if (null != optionItemClickListener) {
		// optionItemClickListener.onClick(item);
		// return true;
		// }
		return false;
	}

	/**
	 * 设置navigation List 事件监听器
	 * 
	 * @param navItemClickListener
	 */
	public void setOnNavItemClickListener(OnNavItemClickListener navItemClickListener) {
		this.navItemClickListener = navItemClickListener;
	}

	/**
	 * 指定position 设置navigation List 事件监听器
	 * 
	 * @param navItemClickListener
	 */
	public void setOnNavItemClickListener(OnNavItemClickListener navItemClickListener, String position) {
		listenerMap.put(position, navItemClickListener);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ActivityResultUtil.onActivityResult(requestCode, resultCode, data, this);
	}

	/*
	 * @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) { LogUtil.d(TAG, "onActivityResult : ni ma de ...");
	 * ActivityResultUtil.onActivityResult(requestCode, resultCode, data); }
	 */

	public HashMap<String, String> addJsEvent(String hash, String type) {
		jsEvents.put(hash, type);
		return jsEvents;
	}

	public void removeJsEvent(String hash) {
		jsEvents.remove(hash);
	}

	public void removeJsEvents() {
		jsEvents = new HashMap<String, String>();
	}

	public HashMap<String, String> getJsEvents() {
		return jsEvents;
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
		// LogUtil.d(TAG, "addWidgetJsEvents :" + widgetName + " - " + hash + " - " + type + " - base:" + widgetJsEvents.toString());
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
			// TODO: handle exception
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

	public void addFragment(String key, BaseFragment fragment) {
		fragments.put(key, fragment);
	}

	public BaseFragment getFragment(String key) {
		return fragments.get(key);
	}

	public void removeFragment(String key) {
		fragments.remove(key);
	}

}
