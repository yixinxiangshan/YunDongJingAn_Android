package com.ecloudiot.framework.widget;

import java.util.HashMap;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.utility.ColorUtil;
import com.ecloudiot.framework.utility.DensityUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.widget.listview.ListViewBaseAdapter;
import com.google.gson.JsonObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshPinnedSectionListView;

public class ListViewBase extends BaseWidget implements OnItemClickListener, OnRefreshListener2<ListView> {
	private boolean pullable = true;
	private boolean hasFooterDivider = false;
	private boolean hasHeaderDivider = false;
	private boolean hasFloatButton = false;
	private boolean hasFixedTitle = false;
	private boolean bottomButton = false;//底层按钮标志位
	private int dividerHeight = 1;
	private String dividerColor = "#EBEBEB";
	private String defaultLayoutNameString = "widget_listview";
	private String attrViewPrepair = "";
	private String stateNoMore = "";
	private ListViewBaseAdapter adapter = null;
	private ListView listView = null;
	private ImageView floatButton = null;
	private PullToRefreshPinnedSectionListView pullToRefreshPinnedSectionListView = null;
	private LinearLayout bottomLayout;
	public ListViewBase(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		// LogUtil.d(TAG, ListViewBase.class.getName() + ": start");
		if (getId() == -1)
			this.setId(R.id.list_view_widget_base);
		// 初始化数据
		parsingData();
		// insertViewToView(parentView, childView, position, insertType);
		refreshData();
		// 通知数据源，初始化完成，开始加载数据
		onCreated();
	}
	@Override
	public void refreshData(String data) {
		// LogUtil.d(TAG, "refreshData with data :" + data);
		putWidgetData(data);
		refreshData();
	}
	@SuppressWarnings("deprecation")
	@Override
	public void refreshData() {
		// LogUtil.d(TAG, "refreshData viewid:" + this.getId());
		if (null == getWidgetDataJObject())
			return;
		if (getListView() == null) {
			// 初始化widget
			// LogUtil.d(TAG, "getListView() == null");
			pullToRefreshPinnedSectionListView = (PullToRefreshPinnedSectionListView) getBaseView().findViewById(R.id.widget_listview);
			
			bottomLayout = (LinearLayout) getBaseView().findViewById(R.id.linear_bottom);
			
			floatButton = (ImageView) getBaseView().findViewById(R.id.widget_listview_fabbutton);
			// setPullToRefreshPinnedSectionListView(View);
			setListView(getPullToRefreshPinnedSectionListView().getRefreshableView());
			getListView().setOnItemClickListener(this);
		}
		// 手工处理widgetDataJObject初始化一些属性

		pullable = getWidgetDataJObject().get("pullable").getAsBoolean();
		hasFooterDivider = getWidgetDataJObject().get("hasFooterDivider").getAsBoolean();
		hasHeaderDivider = getWidgetDataJObject().get("hasHeaderDivider").getAsBoolean();
		dividerHeight = getWidgetDataJObject().get("dividerHeight").getAsInt();
		dividerColor = getWidgetDataJObject().get("dividerColor").getAsString();
		
		// 底部布局参数获取
		if(getWidgetDataJObject().get("bottomButton")!=null){
			bottomButton = getWidgetDataJObject().get("bottomButton").getAsBoolean();
		}
		
		if(bottomButton){
			bottomLayout.setVisibility(VISIBLE);
			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);  
			param.setMargins(15, 10, 15, 10);
			Button child = new Button(getContext());
			child.setLayoutParams(param);
			child.setBackgroundResource(R.drawable.widget_listviewcell_button_btn_ok_bg_xml);
			child.setTextColor(Color.WHITE);
			child.setText("确 定");
			child.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					HashMap<String, Object> eventParams = new HashMap<String, Object>();
					eventParams.put("target", "bottomButton");
					JsAPI.runEvent(((ItemActivity) getPageContext()).getWidgetJsEvents(),getControlId(), "onItemInnerClick", new JSONObject(eventParams));
				}
			});
			bottomLayout.removeAllViews();
			bottomLayout.addView(child);
		}
		
		if (getWidgetDataJObject().get("hasFloatButton") != null) {
			hasFloatButton = getWidgetDataJObject().get("hasFloatButton").getAsBoolean();
		}
		if (getWidgetDataJObject().get("hasFixedTitle") != null) {
			hasFixedTitle = getWidgetDataJObject().get("hasFixedTitle").getAsBoolean();
		}

		// 设置悬浮按钮
		if (hasFloatButton) {
			getListView().setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					if (getListView().getFirstVisiblePosition() != 0) {
						floatButton.setVisibility(View.VISIBLE);
					} else {
						floatButton.setVisibility(View.INVISIBLE);
					}
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					floatButton.setVisibility(View.INVISIBLE);

				}
			});
			getListView().setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view, int scrollState) {
					if (getListView().getFirstVisiblePosition() != 0) {
						floatButton.setVisibility(View.VISIBLE);
					} else {
						floatButton.setVisibility(View.INVISIBLE);
					}
				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
					floatButton.setVisibility(View.INVISIBLE);

				}
			});
			floatButton.setVisibility(View.VISIBLE);
			floatButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					getListView().setSelectionFromTop(1, 0);// 回到第一项元素
				}
			});
		} else {
			floatButton.setVisibility(View.INVISIBLE);
		}

		// 设置可下拉刷新及 更多 操作
		if (isPullable()) {
			pullToRefreshPinnedSectionListView.setMode(Mode.PULL_DOWN_TO_REFRESH);
			pullToRefreshPinnedSectionListView.setOnRefreshListener(this);
		} else {
			pullToRefreshPinnedSectionListView.setMode(Mode.DISABLED);
		}

		if (getDividerHeight() > 0) {
			// 设置divider 注意，先设置颜色后设置高度，反过来会出问题！！
			if (getDividerColor().substring(0, 1).equals("#")) {
				getListView().setDivider(new ColorDrawable(ColorUtil.getColorValueFromRGB(getDividerColor())));
			}
			getListView().setDividerHeight(getDividerHeight());
		} else {
			// 设置divider为透明
			getListView().setDividerHeight(1);
			getListView().setDivider(new ColorDrawable(android.R.color.transparent));
		}

		getListView().setHeaderDividersEnabled(isHasHeaderDivider());
		getListView().setFooterDividersEnabled(isHasFooterDivider());

		// 有数据时 初始化adapter
		loading(LOADING_0N_OFF.TURN_ON);
		if (getWidgetDataJObject().get("data").getAsJsonArray().size() > 0) {
			// LogUtil.d(TAG, "refreshData:" + widgetDataJObject.toString());
			if (getAdapter() == null) {
				// LogUtil.d(TAG, "getAdapter() == null");
				setAdapter(new ListViewBaseAdapter(ctx, widgetDataJObject, this));
				getListView().setAdapter(getAdapter());
				// getAdapter().notifyDataSetChanged();
			} else {
				// LogUtil.d(TAG, "refreshData - update:");// + widgetDataJObject.get("data").toString());
				getAdapter().undateAdapter(widgetDataJObject, this);
				// getAdapter().setWidgetDataJArray(widgetDataJObject.get("data").getAsJsonArray());
				// setAdapter(new ListViewBaseAdapter(ctx, widgetDataJObject, this));
				// getListView().setAdapter(getAdapter());

			}
			loading(LOADING_0N_OFF.TURN_OFF);
			new GetDataTask().execute();
		}

	}
	@Override
	protected void initViewLayout(String layoutName) {
		setDefaultLayoutNameString("widget_listview");
		// LogUtil.d(TAG, "initViewLayout:" + getDefaultLayoutNameString());
		super.initViewLayout(getDefaultLayoutNameString());
		initBaseView(getDefaultLayoutNameString());
	}

	/**
	 * 将解析后的数据赋值给控件数据
	 */
	@Override
	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		super.parsingWidgetData(widgetDataJObject);
		this.widgetDataJObject = widgetDataJObject;
	}

	/**
	 * 方法 -- 更新数据
	 */
	public void updateData(String data) {
		// LogUtil.d(TAG, "updateData");
		loading(LOADING_0N_OFF.TURN_OFF);

		getAdapter().notifyDataSetChanged();
	}
	/**
	 * 方法 -- 更新某条数据
	 */
	public void updateItem(String itemData) {
		// LogUtil.d(TAG, "updateItem");
		if (StringUtil.isNotEmpty(itemData)) {
			getAdapter().updateView(itemData);
		}
	}
	/**
	 * 方法 -- 更新多条数据
	 */
	public void updateItems(HashMap<String, String> params) {
		// LogUtil.d(TAG, "updateItems");
		if (params != null) {
			getAdapter().updateViews(params);
		}
	}
	/**
	 * 方法 --添加单条数据
	 */
	public void addItem(String data, int position) {
		// LogUtil.d(TAG, "addItem");
	}
	/**
	 * 方法 -- 在尾部添加批量数据
	 */
	public void attachData(String data) {
		// LogUtil.d(TAG, "attachData");

		getAdapter().notifyDataSetChanged();
	}
	/**
	 * 方法 -- 获取数据
	 */
	public String getData() {
		// LogUtil.d(TAG, "getData");
		return "";
	}
	/**
	 * 通知 -- 外部可以开始获取数据了
	 */
	public void onCreated() {
		// LogUtil.d(TAG, "onCreated");

	}
	/**
	 * 通知 -- listview item被点击
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// LogUtil.d(TAG, "onItemClick");
		HashMap<String, String> eventParams = new HashMap<String, String>();
		eventParams.put("position", "" + (position - 1));
		eventParams.put("controlId", getControlId());
		eventParams.put("allTypes", "" + getAdapter().getViewTypeNames().size());
		eventParams.put("currentType", "" + getAdapter().getItemViewType(position - 1));
		// 执行js里面的事件
		if (pageContext instanceof ItemActivity) {
			JsAPI.runEvent(((ItemActivity) pageContext).getWidgetJsEvents(), getControlId(), "onItemClick", new JSONObject(eventParams));
		} else if (pageContext instanceof ItemFragment) {
			// LogUtil.d(TAG, "onItemClick:" + ((ItemFragment) pageContext).getWidgetJsEvents().toString());
			JsAPI.runEvent(((ItemFragment) pageContext).getWidgetJsEvents(), getControlId(), "onItemClick", new JSONObject(eventParams));
		}

	}
	/**
	 * 通知 -- listview 下拉刷新
	 */
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// LogUtil.d(TAG, "onPullDownToRefresh");
		String label = DateUtils.formatDateTime(getContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
		        | DateUtils.FORMAT_ABBREV_ALL);
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("更新于:" + label);

		HashMap<String, String> eventParams = new HashMap<String, String>();
		// 执行js里面的事件
		if (pageContext instanceof ItemActivity) {
			JsAPI.runEvent(((ItemActivity) pageContext).getWidgetJsEvents(), getControlId(), "onPullDown", new JSONObject(eventParams));
		} else if (pageContext instanceof ItemFragment) {
			JsAPI.runEvent(((ItemFragment) pageContext).getWidgetJsEvents(), getControlId(), "onPullDown", new JSONObject(eventParams));
		}

	}
	/**
	 * 通知 -- listview 上拉获取更多
	 */
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// LogUtil.d(TAG, "onPullUpToRefresh");
		String label = DateUtils.formatDateTime(getContext(), System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
		        | DateUtils.FORMAT_ABBREV_ALL);
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

		HashMap<String, String> eventParams = new HashMap<String, String>();
		// 执行js里面的事件
		if (pageContext instanceof ItemActivity) {
			JsAPI.runEvent(((ItemActivity) pageContext).getWidgetJsEvents(), getControlId(), "onPullUp", new JSONObject(eventParams));
		} else if (pageContext instanceof ItemFragment) {
			JsAPI.runEvent(((ItemFragment) pageContext).getWidgetJsEvents(), getControlId(), "onPullUp", new JSONObject(eventParams));
		}

	}
	private PopupWindow popupwindow;
	@SuppressLint({"InflateParams", "ClickableViewAccessibility"})
	public void initmPopupWindowView() {
		// // 获取自定义布局文件pop.xml的视图
		View customView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listview_popupwindow, null);
		ListView popupwindow_listView = (ListView) customView.findViewById(R.id.widget_listview_popupwindow_listview);
		// popupwindow_listView.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(
		// R.array.countries)));

		// popupwindow_listView.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// Toast.makeText(getContext(), arg2 + "", 2).show();
		//
		// }
		// });
		// 创建PopupWindow实例,200,150分别是宽度和高度
		// DensityUtil.dipTopx(getContext(), 200);
		// DensityUtil.dipTopx(getContext(), 800);
		WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
		@SuppressWarnings("unused")
		int width = wm.getDefaultDisplay().getWidth();
		int height = wm.getDefaultDisplay().getHeight();
		Rect frame = new Rect();
		((Activity) getContext()).getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		@SuppressWarnings("unused")
		int statusBarHeight = frame.top;
		int height1 = height - DensityUtil.dipTopx(getContext(), 72);
		popupwindow = new PopupWindow(customView, DensityUtil.dipTopx(getContext(), 200), height1);
		popupwindow.setAnimationStyle(R.style.AnimationFade);
		// 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
		// popupwindow.setAnimationStyle(R.style.AnimationFade);
		// 自定义view添加触摸事件
		// popupwindow_listView.setOnItemSelectedListener(new OnItemSelectedListener() {
		// });
		popupwindow_listView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (popupwindow != null && popupwindow.isShowing()) {
					int a = (int) (Math.random() * 10);
					Toast.makeText(getContext(), a + "", Toast.LENGTH_LONG).show();
					listView.setSelection(a);
					popupwindow.dismiss();
					popupwindow = null;
				}
				return false;
			}
		});
		/** 在这里可以实现自定义视图的功能 */
	}
	// gatters and setters
	public String getAttrViewPrepair() {
		return attrViewPrepair;
	}

	public void setAttrViewPrepair(String attrViewPrepair) {
		this.attrViewPrepair = attrViewPrepair;
	}

	public String getStateNoMore() {
		return stateNoMore;
	}

	public void setStateNoMore(String stateNoMore) {
		this.stateNoMore = stateNoMore;
	}

	public String getDefaultLayoutNameString() {
		return defaultLayoutNameString;
	}

	public void setDefaultLayoutNameString(String defaultLayoutNameString) {
		this.defaultLayoutNameString = defaultLayoutNameString;
	}

	public ListViewBaseAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(ListViewBaseAdapter adapter) {
		this.adapter = adapter;
	}

	public boolean isPullable() {
		return pullable;
	}

	public void setPullable(boolean pullable) {
		this.pullable = pullable;
	}

	public ListView getListView() {
		return listView;
	}

	public void setListView(ListView listView) {
		this.listView = listView;
	}

	public PullToRefreshPinnedSectionListView getPullToRefreshPinnedSectionListView() {
		return pullToRefreshPinnedSectionListView;
	}

	public void setPullToRefreshPinnedSectionListView(PullToRefreshPinnedSectionListView pullToRefreshPinnedSectionListView) {
		this.pullToRefreshPinnedSectionListView = pullToRefreshPinnedSectionListView;
	}

	public boolean isHasFooterDivider() {
		return hasFooterDivider;
	}
	public void setHasFooterDivider(boolean hasFooterDivider) {
		this.hasFooterDivider = hasFooterDivider;
	}
	public boolean isHasHeaderDivider() {
		return hasHeaderDivider;
	}
	public void setHasHeaderDivider(boolean hasHeaderDivider) {
		this.hasHeaderDivider = hasHeaderDivider;
	}
	public int getDividerHeight() {
		return dividerHeight;
	}
	public void setDividerHeight(int dividerHeight) {
		this.dividerHeight = dividerHeight;
	}
	public String getDividerColor() {
		return dividerColor;
	}
	public void setDividerColor(String dividerColor) {
		this.dividerColor = dividerColor;
	}
	public boolean isHasFixedTitle() {
		return hasFixedTitle;
	}
	public void setHasFixedTitle(boolean hasFixedTitle) {
		this.hasFixedTitle = hasFixedTitle;
	}
	public boolean isHasFloatButton() {
		return hasFloatButton;

	}
	public void setHasFloatButton(boolean hasFloatButton) {
		this.hasFloatButton = hasFloatButton;
	}

	public boolean isBottomButton() {
		return bottomButton;
	}
	public void setBottomButton(boolean bottomButton) {
		this.bottomButton = bottomButton;
	}

	private class GetDataTask extends AsyncTask<Void, Void, String[]> {
		protected void onPostExecute(String[] result) {
			// Call onRefreshComplete when the list has been refreshed.
			pullToRefreshPinnedSectionListView.onRefreshComplete();
			super.onPostExecute(result);
		}

		@Override
		protected String[] doInBackground(Void... params) {
			// try {
			// Thread.sleep(1000);
			// } catch (InterruptedException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			return null;
		}
	}

	class PopupwindowistViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
