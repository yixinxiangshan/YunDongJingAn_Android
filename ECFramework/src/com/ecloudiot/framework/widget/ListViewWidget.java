package com.ecloudiot.framework.widget;

import java.util.HashMap;

import org.json.JSONObject;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.event.linterface.OnButtonClickListener;
import com.ecloudiot.framework.event.linterface.OnChangedListener;
import com.ecloudiot.framework.event.linterface.OnLoadMoreListener;
import com.ecloudiot.framework.event.linterface.OnRefreshListener;
import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.WidgetUtil;
import com.ecloudiot.framework.widget.adapter.ListviewTwolineAdapter;
import com.ecloudiot.framework.widget.model.ListviewModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.readystatesoftware.viewbadger.BadgeView;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

@SuppressLint("ViewConstructor")
public class ListViewWidget extends BaseWidget {
	private static String TAG = "ListViewWidget";
	private ListviewModel widgetDataModel;
	private PullToRefreshListView pullToRefreshListView;
	private ListView listView;
	private ListviewTwolineAdapter adapter;

	private OnItemClickListener itemClickListener;
	private OnLoadMoreListener loadMoreListener;
	private OnRefreshListener<ListView> refreshListener;

	private String listLayoutName;
	private String itemLayoutName;
	private OnButtonClickListener onButtonClickListener;
	private OnChangedListener onChangedListener;
	// 控件属性
	private boolean pullable;
	private int dividerHeight;
	private String dividerName;
	private boolean withStrikeThruTextFlag;

	public ListViewWidget(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		this.setId(R.id.list_view_widget);
		parsingData();
	}

	@Override
	protected void initViewLayout(String layoutName) {
		super.initViewLayout(layoutName);
		if (StringUtil.isNotEmpty(layoutName) && layoutName.contains(".")) {
			try {
				String[] ss = layoutName.split("\\.");
				itemLayoutName = ss[0];
				listLayoutName = ss[1];
			} catch (Exception e) {
				LogUtil.e(TAG, "initViewLayout error: layoutName is invalid...");
				listLayoutName = "widget_listview_default";
				itemLayoutName = "widget_listview_item_twoline";
			}
		} else if (StringUtil.isNotEmpty(layoutName)) {
			listLayoutName = "widget_listview_default";
			itemLayoutName = layoutName;
		} else {
			listLayoutName = "widget_listview_default";
			itemLayoutName = "widget_listview_item_twoline";
		}
		initBaseView(listLayoutName);
	}

	/**
	 * 将解析后的数据赋值给控件数据model实例
	 */
	@Override
	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		super.parsingWidgetData(widgetDataJObject);
		widgetDataModel = parsingWidgetModel(widgetDataJObject);
	}

	/**
	 * 解析数据为listView 的Model
	 * 
	 * @param dataString
	 * @return
	 */
	private ListviewModel parsingWidgetModel(String dataString) {
		if (StringUtil.isEmpty(dataString))
			return null;
		JsonParser jParser = new JsonParser();
		JsonObject jObject = null;
		try {
			jObject = (JsonObject) jParser.parse(dataString);
		} catch (Exception e) {
			LogUtil.e(TAG, "parsingWidgetModel error: data string may be invalid or  " + e.toString());
		}
		return parsingWidgetModel(jObject);
	}

	/**
	 * 解析数据为listView 的Model
	 * 
	 * @param widgetDataJObject
	 * @return
	 */
	private ListviewModel parsingWidgetModel(JsonObject widgetDataJObject) {
		ListviewModel listviewModel = null;
		try {
			listviewModel = GsonUtil.fromJson(widgetDataJObject, ListviewModel.class);
		} catch (Exception e) {
			LogUtil.e(TAG, "parsingData error: widgetDataJObject  is invalid...");
		}
		return listviewModel;
	}

	@Override
	protected void setData() {
		// LogUtil.i(TAG, "setData : is thread  = "+(Looper.myLooper() ==
		// Looper.getMainLooper()));
		this.adapter = new ListviewTwolineAdapter(this.ctx, this.widgetDataModel, itemLayoutName, this);
		this.pullToRefreshListView = (PullToRefreshListView) getBaseView().findViewById(R.id.widget_listview_pulltorefresh_listview);

		this.setListView(pullToRefreshListView.getRefreshableView());
		getListView().setAdapter(this.adapter);
		adapter.notifyDataSetChanged();
		if (!isPullable()) {
			pullToRefreshListView.setMode(Mode.DISABLED);
		} else {
			pullToRefreshListView.setMode(Mode.BOTH);
		}
		if (insertType == 2 || insertType == 4) {
			try {
				resetListviewSize();
			} catch (Exception e) {
				LogUtil.e(TAG, "setData error: resetListviewSize only work when item's root-layout is Linerlayout = " + e.toString());
				e.printStackTrace();
			}
		}
		if (getDividerHeight() > 0) {
			if (StringUtil.isEmpty(getDividerName()))
				setDividerName("general_separation_repeate");
			getListView().setDividerHeight(getDividerHeight());
			getListView().setDivider(ctx.getResources().getDrawable(ResourceUtil.getDrawableIdFromContext(ctx, getDividerName())));
		}
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				try {
					HashMap<String, String> eventParams = new HashMap<String, String>();
					eventParams.put("controlId", getControlId());
					// 执行v8js里面的事件，如果有返回，则返回
					String result = "";
					if (pageContext instanceof ItemActivity) {
						result = JsAPI.runEvent(((ItemActivity) pageContext).getWidgetJsEvents(), getControlId(), "onLoadData", new JSONObject(eventParams));
					} else if (pageContext instanceof ItemFragment) {
						result = JsAPI.runEvent(((ItemFragment) pageContext).getWidgetJsEvents(), getControlId(), "onLoadData", new JSONObject(eventParams));
					}

					if (!StringUtil.isEmpty(result)) {
						refreshData(result);
						// setData(result);
						// resetListviewSize();
					}
					// old js
//					if (JsManager.getInstance().callJsMethodSync(getControlId(), "onRefresh", eventParams).equalsIgnoreCase("true")) {
//						return;
//					}
					if (null != refreshListener) {
						refreshListener.onRefresh(refreshView);
					} else {
						refreshData();
					}
				} catch (Exception e) {
					LogUtil.e(TAG, "pullDown Error:" + e.toString());
					e.printStackTrace();
					// TODO: handle exception
				}

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				try {
					int lastPosition = widgetDataModel.getListByType().size() - 1;
					HashMap<String, String> eventParams = new HashMap<String, String>();
					eventParams.put("lastPosition", "" + lastPosition);
					eventParams.put("controlId", getControlId());
					// 执行v8js里面的事件，如果有返回，则返回
					String result = "";
					if (pageContext instanceof ItemActivity) {
						result = JsAPI.runEvent(((ItemActivity) pageContext).getWidgetJsEvents(), getControlId(), "onMoreData", new JSONObject(eventParams));
					} else if (pageContext instanceof ItemFragment) {
						result = JsAPI.runEvent(((ItemFragment) pageContext).getWidgetJsEvents(), getControlId(), "onMoreData", new JSONObject(eventParams));
					}
					if (!StringUtil.isEmpty(result)) {
						addData(result);
						// resetListviewSize();
					}
					// old js
//					if (JsManager.getInstance().callJsMethodSync(getControlId(), "onLoadMore", eventParams).equalsIgnoreCase("true")) {
//						return;
//					}
					if (null != loadMoreListener) {
						LogUtil.d(TAG, "onLastItemVisible : start load more ...");
						loadMoreListener.OnLoadMore(lastPosition);
					} else {
						// ((BaseActivity)
						// IntentUtil.getActivity()).setProgressIndeterminateVisible(true);
						// pullToRefreshListView.onRefreshComplete();
						// return;
						WidgetUtil.addMoreData(pageContext, ListViewWidget.this, getConfigDataModel().getDatasource(),
						        widgetDataModel.getListByType().get(lastPosition).getId());
					}
				} catch (Exception e) {
					LogUtil.e(TAG, "pullUp Error:" + e.toString());
					e.printStackTrace();
					// TODO: handle exception
				}

			}
		});
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, String> eventParams = new HashMap<String, String>();
				eventParams.put("position", "" + (position - 1));
				eventParams.put("controlId", getControlId());
				// 执行js里面的事件
				if (pageContext instanceof ItemActivity) {
					JsAPI.runEvent(((ItemActivity) pageContext).getWidgetJsEvents(), getControlId(), "onItemClick", new JSONObject(eventParams));
				} else if (pageContext instanceof ItemFragment) {
					JsAPI.runEvent(((ItemFragment) pageContext).getWidgetJsEvents(), getControlId(), "onItemClick", new JSONObject(eventParams));
				}

				// if (JsManager.getInstance().callJsMethodSync(getControlId(), "onItemClick", eventParams).equalsIgnoreCase("true")) {
				// return;
				// }
				OnItemClickListener tempItemClickListener = null;
				LogUtil.i(TAG, "onItemClick : continue implement event from config ...");
				try {
					tempItemClickListener = (OnItemClickListener) getListenerMap().get("itemClickListener" + (position - 1));
				} catch (Exception e) {
					LogUtil.i(TAG, "onItemClick info : no special event listener ...");
				}
				if (null != tempItemClickListener) {
					tempItemClickListener.onItemClick(parent, view, (position - 1), id);
					return;
				}
				if (null != itemClickListener) {
					itemClickListener.onItemClick(parent, view, (position - 1), id);
				}
			}
		});
		super.setData();
	}

	/**
	 * 设置微章图标
	 */
	@Override
	protected void setBadge(JsonObject badgeDataJObject) {
		LogUtil.d(TAG, "setBadge start ");
		JsonArray badgeDataList = badgeDataJObject.getAsJsonArray("badgeDataList");
		int count = badgeDataList.size();
		LogUtil.d(TAG, "badgeData count : " + count);
		for (int i = 0; i < count; i++) {
			JsonObject object = (JsonObject) badgeDataList.get(i);
			JsonElement text = object.get("text");
			String position = object.get("position").getAsString();
			JsonElement isHide = object.get("isHide");
			JsonElement color = object.get("color");
			JsonElement drawable = object.get("drawable");
			if (StringUtil.isEmpty(position)) {
				return;
			}
			BadgeView badge = (BadgeView) getListView().getChildAt(Integer.parseInt(position)).findViewById(R.id.badge);
			if (null != color) {
				if (StringUtil.isNotEmpty(color.getAsString())) {
					badge.setBadgeBackgroundColor(Color.parseColor(color.getAsString()));
				}
			}
			if (null != drawable) {
				if (StringUtil.isNotEmpty(drawable.getAsString())) {
					badge.setBackgroundResource(ResourceUtil.getIdFromContext(drawable.getAsString(), "drawable"));
					badge.setGravity(Gravity.TOP);
				}
			}

			if (null != text) {
				if (StringUtil.isNotEmpty(text.getAsString())) {
					badge.setText(text.getAsString());
					badge.setVisibility(View.VISIBLE);
				}
			}
			if (null != isHide) {
				if (isHide.getAsString().equals("true")) {
					badge.hide();
				}
			}
		}
	}

	/**
	 * 添加更多数据
	 * 
	 * @param moreData
	 */
	public void addData(String moreData) {
		LogUtil.d(TAG, "addData...");
		try {
			pullToRefreshListView.onRefreshComplete();
		} catch (Exception e) {
			LogUtil.e(TAG, "refreshData error: is not refresh from pull...");
		}
		if (StringUtil.isEmpty(moreData)) {
			showNoMore();
			return;
		}
		String moreDataAfterAdapted = moreData;
		if (StringUtil.isNotEmpty(getDataSourceString())) {
			moreDataAfterAdapted = WidgetUtil.adaptWidgetData(pageContext, this, getDataSourceString(), moreData);
		}
		ListviewModel moreDataListviewModel = parsingWidgetModel(moreDataAfterAdapted);
		if (null == moreDataListviewModel || moreDataListviewModel.getListByType().size() <= 0) {
			showNoMore();
			return;
		}
		for (int i = 0; i < moreDataListviewModel.getListByType().size(); i++) {
			widgetDataModel.getListByType().add(moreDataListviewModel.getListByType().get(i));
		}
		adapter.resetData(widgetDataModel);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 弹出提示没有更多数据，并设置不会再触发loadMore事件
	 */
	private void showNoMore() {
		pullToRefreshListView.setMode(Mode.PULL_FROM_START);
		Toast.makeText(ctx, "没有更多...", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 刷新数据
	 */
	@Override
	public void refreshData(String newData) {
		if (null == adapter) {
			WidgetUtil.putWidgetData(pageContext, this, newData, WidgetUtil.INIT_WIDGET);
			return;
			// this.setData();
		}
		LogUtil.d(TAG, "refreshData new data :" + newData);
		try {
			pullToRefreshListView.onRefreshComplete();
		} catch (Exception e) {
			LogUtil.i(TAG, "refreshData error: is not refresh from pull...");
		}
		widgetDataModel = null;
		if (StringUtil.isEmpty(newData)) {
			LogUtil.e(TAG, "refreshData error: newData is null ...");
		}
		String newDataAfterAdapted = newData;
		if (StringUtil.isNotEmpty(getDataSourceString())) {
			newDataAfterAdapted = WidgetUtil.adaptWidgetData(pageContext, this, getDataSourceString(), newData);
		}
		widgetDataModel = parsingWidgetModel(newDataAfterAdapted);
		if (null == widgetDataModel || widgetDataModel.getListByType().size() <= 0) {
			LogUtil.e(TAG, "refreshData error: newData parsing is null ...");
		}

		adapter.resetData(widgetDataModel);
		adapter.notifyDataSetChanged();
		if (!isPullable()) {
			pullToRefreshListView.setMode(Mode.DISABLED);
		} else {
			pullToRefreshListView.setMode(Mode.BOTH);
		}

	}

	/**
	 * 根据item数量重新计算高度
	 */
	private void resetListviewSize() {
		ListviewTwolineAdapter listviewAdapter = (ListviewTwolineAdapter) this.adapter;
		if (listviewAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listviewAdapter.getCount(); i++) {
			View listItem = listviewAdapter.getView(i, null, getListView());
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = getListView().getLayoutParams();
		params.height = totalHeight + (getDividerHeight() * (listviewAdapter.getCount() - 1));
		getListView().setLayoutParams(params);
		params = this.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		}
		// ((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
		params.height = totalHeight + (getDividerHeight() * (listviewAdapter.getCount() - 1)) + 20;
		LogUtil.i(TAG, "resetListviewSize : height = " + params.height);
		this.setLayoutParams(params);
	}

	/**
	 * 设置item点击事件监听器
	 * 
	 * @param itemClickListener
	 */
	public void setOnItemClickListener(OnItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener itemClickListener, String position) {
		if (getListenerMap() == null) {
			setListenerMap(new HashMap<String, Object>());
		}
		getListenerMap().put("itemClickListener" + position, itemClickListener);
	}

	/**
	 * 设置下拉刷新事件监听器
	 * 
	 * @param refreshListener
	 */
	public void setOnRefreshListener(OnRefreshListener<ListView> refreshListener) {
		this.refreshListener = refreshListener;
	}

	/**
	 * 设置加载更多事件监听器
	 * 
	 * @param loadMoreListener
	 */
	public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
		this.loadMoreListener = loadMoreListener;
	}

	/**
	 * 设置button监听器
	 */
	public void setOnButtonClickListener(OnButtonClickListener buttonClickListener) {
		this.onButtonClickListener = buttonClickListener;
	}

	/**
	 * 设置NumberPicker事件
	 * 
	 * @return
	 */
	public void setOnChangedListener(OnChangedListener changedListener) {
		this.onChangedListener = changedListener;
	}

	public OnButtonClickListener getOnButtonClickListener() {
		return onButtonClickListener;
	}

	public OnChangedListener getOnChangedListener() {
		return onChangedListener;
	}

	public ListView getListView() {
		return listView;
	}

	public void setListView(ListView listView) {
		this.listView = listView;
	}

	public ListviewModel getDataModel() {
		return widgetDataModel;
	}

	public int getDividerHeight() {
		return dividerHeight >= 0 ? dividerHeight : 0;
	}

	public void setDividerHeight(String dividerHeight) {
		if (StringUtil.toInt(dividerHeight) >= 0)
			this.dividerHeight = Integer.parseInt(dividerHeight);
	}

	public boolean isPullable() {
		return pullable;
	}

	public void setPullable(String pullable) {
		this.pullable = Boolean.parseBoolean(pullable);
	}

	public String getDividerName() {
		return StringUtil.isEmpty(dividerName) ? "general_separation_repeate" : dividerName;
	}

	public void setDividerName(String dividerName) {
		this.dividerName = dividerName;
	}

	public boolean isWithStrikeThruTextFlag() {
		return withStrikeThruTextFlag;
	}

	public void setWithStrikeThruTextFlag(String withStrikeThruTextFlag) {
		this.withStrikeThruTextFlag = Boolean.parseBoolean(withStrikeThruTextFlag);
	}
}
