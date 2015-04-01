package com.ecloudiot.framework.widget;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.event.linterface.OnLoadMoreListener;
import com.ecloudiot.framework.event.linterface.OnRefreshListener;
import com.ecloudiot.framework.utility.DensityUtil;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.WidgetUtil;
import com.ecloudiot.framework.widget.adapter.GridviewAdapter;
import com.ecloudiot.framework.widget.model.GridModel;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

@SuppressLint("ViewConstructor")
public class GridWidget extends BaseWidget {
	private static String TAG = "GridWidget";
	private GridView gridView;
	private PullToRefreshGridView pullToRefreshGridView;
	private GridModel widgetDataModel;
	private GridviewAdapter adapter;
	private OnLoadMoreListener loadMoreListener;
	private OnRefreshListener<GridView> refreshListener;
	private OnItemClickListener itemClickListener;
	private String itemLayoutName;
	private String gridLayoutName;
	private int numColumns;
	private float ratioHToW;
	private boolean notCalImageSize;
	private boolean noImage;
	private boolean circleImage;
	private int cellSpace;
	private boolean pullable;

	public GridWidget(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		this.setId(R.id.grid_widget);
		parsingData();
	}

	public void setColumns(Integer columns) {
		this.adapter.setNumColumns(columns);
		this.GetGridView().setNumColumns(columns);
	}

	/**
	 * 初始化 选择布局
	 * 
	 * @param layoutName
	 */
	public void initViewLayout(String layoutName) {
		if (StringUtil.isNotEmpty(layoutName) && layoutName.contains("\\.")) {
			try {
				String[] ss = layoutName.split("\\.");
				itemLayoutName = ss[0];
				gridLayoutName = ss[1];
			} catch (Exception e) {
				LogUtil.e(TAG, "initViewLayout error: layoutName is invalid...");
				gridLayoutName = "widget_grid_default";
				itemLayoutName = "widget_grid_item";
			}
		} else if (StringUtil.isNotEmpty(layoutName)) {
			gridLayoutName = "widget_grid_default";
			itemLayoutName = layoutName;
		} else {
			gridLayoutName = "widget_grid_default";
			this.itemLayoutName = "widget_grid_item";
		}
		LogUtil.d(TAG, "itemLayoutName:" + this.itemLayoutName);
		initBaseView(gridLayoutName);
	}

	public GridView GetGridView() {
		return this.gridView;
	}

	public void setGridView(GridView gridView) {
		this.gridView = gridView;
	}

	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		super.parsingWidgetData(widgetDataJObject);
		this.widgetDataModel = GsonUtil.fromJson(widgetDataJObject, GridModel.class);
	}

	// 设置数据
	public void setData() {
		this.pullToRefreshGridView = (PullToRefreshGridView) this.getBaseView().findViewById(R.id.widget_grid_gridview);
		setGridView(pullToRefreshGridView.getRefreshableView());
		this.adapter = new GridviewAdapter(this.ctx, this.widgetDataModel, this.itemLayoutName);
		// TODO: 设置属性,独立出来
		this.GetGridView().setHorizontalSpacing(DensityUtil.dipTopx(ctx, getCellSpace()));
		this.GetGridView().setVerticalSpacing(DensityUtil.dipTopx(ctx, getCellSpace()));
		adapter.setNotCalImageSize(isNotCalImageSize());
		this.setColumns(getNumColumns());
		adapter.setCircleImage(isCircleImage());
		adapter.setRatioHToW(getRatioHToW());
		adapter.setNoImage(isNoImage());
		View parent = (View)this.getParent();
		int parentPadding = parent.getPaddingLeft()+parent.getPaddingRight();
		adapter.setParentPadding(parentPadding);
		this.GetGridView().setAdapter(this.adapter);
		if (!isPullable()) {
			pullToRefreshGridView.setMode(Mode.DISABLED);
		} else {
			pullToRefreshGridView.setMode(Mode.BOTH);
		}
		if (insertType == 2 || insertType == 4) {
			try {
				resetGridviewSize();
			} catch (Exception e) {
				LogUtil.e(TAG, "setData error: resetListviewSize only work when item's root-layout is Linerlayout");
			}
		}
		pullToRefreshGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				HashMap<String, String> eventParams = new HashMap<String, String>();
				eventParams.put("controlId", getControlId());
//				if (JsManager.getInstance().callJsMethodSync(getControlId(), "onRefresh", eventParams)
//						.equalsIgnoreCase("true")) {
//					return;
//				}
				if (null != refreshListener) {
					refreshListener.onRefresh(refreshView);
				} else {
					refreshData();
				}
				return;

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
				int lastPosition = widgetDataModel.getListByType().size() - 1;
				HashMap<String, String> eventParams = new HashMap<String, String>();
				eventParams.put("lastPosition", "" + lastPosition);
				eventParams.put("controlId", getControlId());
//				if (JsManager.getInstance().callJsMethodSync(getControlId(), "onLoadMore", eventParams)
//						.equalsIgnoreCase("true")) {
//					return;
//				}
				if (null != loadMoreListener) {
					loadMoreListener.OnLoadMore(lastPosition);
				} else {
					WidgetUtil.addMoreData(pageContext, GridWidget.this, getConfigDataModel().getDatasource(),
							widgetDataModel.getListByType().get(lastPosition).getId());
				}
				return;
			}

		});
		GetGridView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, String> eventParams = new HashMap<String, String>();
				eventParams.put("position", "" + position);
				eventParams.put("controlId", getControlId());
				// if (JsManager.getInstance().callJsMethodSync(getControlId(), "onItemClick", eventParams)
				// .equalsIgnoreCase("true")) {
				// return;
				// }
				LogUtil.i(TAG, "onItemClick : continue implement event from config ...");
				OnItemClickListener tempItemClickListener = null;
				try {
					tempItemClickListener = (OnItemClickListener) getListenerMap().get(
							"itemClickListener" + position );
				} catch (Exception e) {
					LogUtil.i(TAG, "onItemClick info : no special event listener ...");
				}
				if (null != tempItemClickListener) {
					tempItemClickListener.onItemClick(parent, view, position , id);
					return;
				}
				if (null != itemClickListener) {
					itemClickListener.onItemClick(parent, view, position , id);
				}
			}
		});
		super.setData();
	}

	public GridModel getDataModel() {
		return widgetDataModel;
	}

	/**
	 * 解析数据为gridview 的Model
	 * 
	 * @param dataString
	 * @return
	 */
	private GridModel parsingWidgetModel(String dataString) {
		if (StringUtil.isEmpty(dataString))
			return null;
		JsonParser jParser = new JsonParser();
		JsonObject jObject = null;
		try {
			jObject = (JsonObject) jParser.parse(dataString);
		} catch (JsonSyntaxException e) {
			LogUtil.e(TAG, "parsingWidgetModel error: data string may be invalid or  " + e.toString());
		}
		return parsingWidgetModel(jObject);
	}

	/**
	 * 解析数据为listView 的Model
	 * @param widgetDataJObject
	 * @return
	 */
	private GridModel parsingWidgetModel(JsonObject widgetDataJObject) {
		GridModel gridModel = null;
		try {
			gridModel = GsonUtil.fromJson(widgetDataJObject, GridModel.class);
		} catch (Exception e) {
			LogUtil.e(TAG, "parsingData error: widgetDataJObject  is invalid...");
		}
		return gridModel;

	}

	/**
	 * 添加更多数据
	 * 
	 * @param moreData
	 */
	public void addData(String moreData) {
		try {
			pullToRefreshGridView.onRefreshComplete();
		} catch (Exception e) {
			LogUtil.e(TAG, "refreshData error: is not refresh from pull...");
		}
		LogUtil.d(TAG, "addData...");
		if (StringUtil.isEmpty(moreData)) {
			showNoMore();
			return;
		}
		String moreDataAfterAdapted = moreData;
		if (StringUtil.isNotEmpty(getDataSourceString())) {
			moreDataAfterAdapted = WidgetUtil.adaptWidgetData(pageContext, this, getDataSourceString(), moreData);
		}
		GridModel moreDataListviewModel = parsingWidgetModel(moreDataAfterAdapted);
		if (null == moreDataListviewModel || moreDataListviewModel.getListByType().size() <= 0) {
			showNoMore();
			return;
		}
		for (int i = 0; i < moreDataListviewModel.getListByType().size(); i++) {
			this.widgetDataModel.getListByType().add(moreDataListviewModel.getListByType().get(i));
		}
		adapter.resetData(this.widgetDataModel);
		adapter.notifyDataSetChanged();
	}


	/**
	 * 弹出提示没有更多数据，并设置不会再触发loadMore事件
	 */
	private void showNoMore() {
		pullToRefreshGridView.setMode(Mode.PULL_FROM_START);
		Toast.makeText(ctx, "没有更多...", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 刷新数据
	 */
	@Override
	public void refreshData(String newData) {
		try {
			pullToRefreshGridView.onRefreshComplete();
		} catch (Exception e) {
			LogUtil.e(TAG, "refreshData error: is not refresh from pull...");
		}
		widgetDataModel = null;
		if (StringUtil.isEmpty(newData)) {
			LogUtil.e(TAG, "newData is null ....");
		}
		String newDataAfterAdapted = newData;
		if (StringUtil.isNotEmpty(getDataSourceString())) {
			newDataAfterAdapted = WidgetUtil.adaptWidgetData(pageContext, this, getDataSourceString(), newData);
		}
		widgetDataModel = parsingWidgetModel(newDataAfterAdapted);
		if (null == widgetDataModel || widgetDataModel.getListByType() == null
				|| widgetDataModel.getListByType().size() <= 0) {
			LogUtil.e(TAG, "dataModel is null ....");
		} else {
			pullToRefreshGridView.setMode(Mode.BOTH);
		}
		adapter.resetData(this.widgetDataModel);
		adapter.notifyDataSetChanged();
		LogUtil.i(TAG, "refreshData : adapter.resetData");
	}
	/**
	 * 根据item数量重新计算高度
	 */
	private void resetGridviewSize() {
		GridviewAdapter gridviewAdapter = (GridviewAdapter) GetGridView().getAdapter();
		if (gridviewAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < gridviewAdapter.getCount(); i++) {
			View listItem = gridviewAdapter.getView(i, null, GetGridView());
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = GetGridView().getLayoutParams();
		params.height = totalHeight+(getCellSpace() * gridviewAdapter.getCount())/getNumColumns();
		LogUtil.i(TAG, "resetGridviewSize : height = "+params.height);
//		((MarginLayoutParams) params).setMargins(10, 10, 10, 10);
		GetGridView().setLayoutParams(params);
	}

	/**
	 * 设置下拉刷新事件监听器
	 * 
	 * @param refreshListener
	 */
	public void setOnRefreshListener(OnRefreshListener<GridView> refreshListener) {
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

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.itemClickListener = onItemClickListener;
	}

	public int getNumColumns() {
		return numColumns > 0 ? numColumns : 3;
	}

	public void setNumColumns(String numColumns) {
		if (StringUtil.toInt(numColumns) >= 0)
			this.numColumns = Integer.parseInt(numColumns);
	}

	public float getRatioHToW() {
		return ratioHToW > 0 ? ratioHToW : 0.75f;
	}

	public void setRatioHToW(String ratioHToW) {
		if (StringUtil.toFloat(ratioHToW) >= 0)
			this.ratioHToW = Float.parseFloat(ratioHToW);
	}

	public int getCellSpace() {
		return cellSpace >= 0 ? cellSpace : 0;
	}

	public void setCellSpace(String cellSpace) {
		if (StringUtil.toInt(cellSpace) >= 0)
			this.cellSpace = Integer.parseInt(cellSpace);
	}

	public boolean isPullable() {
		return pullable;
	}

	public void setPullable(String pullable) {
		this.pullable = Boolean.parseBoolean(pullable);
	}

	public boolean isCircleImage() {
		return circleImage;
	}

	public void setCircleImage(String circleImage) {
		this.circleImage = Boolean.parseBoolean(circleImage);
	}

	public boolean isNotCalImageSize() {
		return notCalImageSize;
	}

	public void setNotCalImageSize(String notCalImageSize) {
		this.notCalImageSize = Boolean.parseBoolean(notCalImageSize);
	}

	public boolean isNoImage() {
		return noImage;
	}

	public void setNoImage(String noImage) {
		this.noImage = Boolean.parseBoolean(noImage);
	}

}
