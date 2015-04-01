package com.ecloudiot.framework.widget;

import java.util.HashMap;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.event.linterface.OnGroupItemClickListener;
import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.view.SecondaryMenuView;
import com.ecloudiot.framework.widget.model.SecondaryMenuModel;
import com.google.gson.JsonObject;

@SuppressLint("ViewConstructor")
public class SecondaryMenuWidget extends BaseWidget {
	private static final String TAG = "SecondaryMenuWidget";
	private SecondaryMenuModel data;
	private String layoutName;
	private SecondaryMenuView secondaryMenuView;
	// 事件监听器
	private OnItemClickListener itemClickListener;
	private OnGroupItemClickListener groupItemClickListener;
	// 控件属性
	private boolean haveSubMenu = false;
	private int defaultGroupSel = -1;
	private int defaultSubSel = -1;
	private String itemBg;
	private String itemSelBg;
	private String itemBgSub;
	private String itemSelBgSub;
	private int dividerHeight = 1;
	private String dividerName;

	public SecondaryMenuWidget(Object pageContext, String dataString, String layoutName) {
		super(pageContext, dataString, layoutName);
		this.setId(R.id.secondary_menu_widget);
		parsingData();
	}

	protected void initViewLayout(String layoutName) {
		super.initViewLayout(layoutName);
		if (StringUtil.isNotEmpty(layoutName)) {
			this.layoutName = layoutName;
		} else {
			this.layoutName = "widget_secondary_menu_default";
		}
		initBaseView(this.layoutName);
	}

	/**
	 * 解析 widget 数据
	 * 
	 * @param widgetDataJObject
	 */
	protected void parsingWidgetData(JsonObject widgetDataJObject) {
		super.parsingWidgetData(widgetDataJObject);
		try {
			data = GsonUtil.fromJson(widgetDataJObject, SecondaryMenuModel.class);
		} catch (Exception e) {
			LogUtil.e(TAG, "parsing SecondaryMenuModel error" + e.toString());
		}
	}

	protected void setData() {
		secondaryMenuView = (SecondaryMenuView) getBaseView().findViewById(R.id.widget_secondary_view);
		// 设置属性
		secondaryMenuView.setWithSubMenu(isHaveSubMenu());
		secondaryMenuView.setGroupSel(getDefaultGroupSel());
		secondaryMenuView.setSubSel(getDefaultSubSel());
		if (StringUtil.isNotEmpty(getItemBg())) {
			secondaryMenuView.setItemBg(getItemBg());
		}
		if (StringUtil.isNotEmpty(getItemSelBg())) {
			secondaryMenuView.setItemSelBg(getItemSelBg());
		}
		if (StringUtil.isNotEmpty(getItemBgSub())) {
			secondaryMenuView.setItemBgSub(getItemBgSub());
		}
		if (StringUtil.isNotEmpty(getItemSelBgSub())) {
			secondaryMenuView.setItemSelBgSub(getItemSelBgSub());
		}
		secondaryMenuView.setDividerHeight(getDividerHeight());
		if (StringUtil.isNotEmpty(getDividerName())) {
			secondaryMenuView.setDividerName(getDividerName());
		}
		secondaryMenuView.setData(data);
		secondaryMenuView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				HashMap<String, String> eventParams = new HashMap<String, String>();
				eventParams.put("position", "" + position);
				eventParams.put("controlId", getControlId());
				OnItemClickListener tempItemClickListener = null;
				try {
					tempItemClickListener = (OnItemClickListener) getListenerMap().get("itemClickListener" + position);
				} catch (Exception e) {
					LogUtil.i(TAG, "onItemClick info : no special event listener ...");
				}
				if (null != tempItemClickListener) {
					tempItemClickListener.onItemClick(parent, view, position, id);
					return;
				}
				if (null != itemClickListener) {
					itemClickListener.onItemClick(parent, view, position, id);
				}
				// 执行js里面的事件
				String result = "";
				if (pageContext instanceof ItemActivity) {
					result = JsAPI.runEvent(((ItemActivity) pageContext).getWidgetJsEvents(), getControlId(), "onItemClick", new JSONObject(eventParams));
				} else if (pageContext instanceof ItemFragment) {
					result = JsAPI.runEvent(((ItemFragment) pageContext).getWidgetJsEvents(), getControlId(), "onItemClick", new JSONObject(eventParams));
				}
				if (result != null && !StringUtil.isEmpty(result)) {
					if (result.equals("_false"))
						return;
				}
//				if (JsManager.getInstance().callJsMethodSync(getControlId(), "onItemClick", eventParams).equalsIgnoreCase("true")) {
//					return;
//				}
			}
		});
		secondaryMenuView.setOnGroupItemClickListener(new OnGroupItemClickListener() {

			@Override
			public void onGroupItemClick(int groupId, int position) {
				HashMap<String, String> eventParams = new HashMap<String, String>();
				eventParams.put("groupId", "" + groupId);
				eventParams.put("position", "" + position);
				eventParams.put("controlId", getControlId());
				
				OnGroupItemClickListener onGroupItemClickListener = null;
				try {
					onGroupItemClickListener = (OnGroupItemClickListener) getListenerMap().get("groupClickListener" + groupId + ":" + position);
				} catch (Exception e) {
					LogUtil.i(TAG, "onClick no special event listener");
				}
				if (null != onGroupItemClickListener) {
					onGroupItemClickListener.onGroupItemClick(groupId, position);
					return;
				}
				if (groupItemClickListener != null) {
					groupItemClickListener.onGroupItemClick(groupId, position);
				}
				// 执行js里面的事件
				String result = "";
				if (pageContext instanceof ItemActivity) {
					result = JsAPI.runEvent(((ItemActivity) pageContext).getWidgetJsEvents(), getControlId(), "onItemClick", new JSONObject(eventParams));
				} else if (pageContext instanceof ItemFragment) {
					result = JsAPI.runEvent(((ItemFragment) pageContext).getWidgetJsEvents(), getControlId(), "onItemClick", new JSONObject(eventParams));
				}
				if (result != null && !StringUtil.isEmpty(result)) {
					if (result.equals("_false"))
						return;
				}
				// if (JsManager.getInstance().callJsMethodSync(getControlId(), "onGroupItemClick", eventParams).equalsIgnoreCase("true")) {
				// return;
				// }
			}
		});
		super.setData();
	}

	/**
	 * 设置单列menu item点击事件监听器
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
	 * 设置二级menu item监听器
	 * 
	 * @param groupClickListener
	 */
	public void setOnGroupItemClickListener(OnGroupItemClickListener groupClickListener) {
		this.groupItemClickListener = groupClickListener;
	}

	public void setOnGroupItemClickListener(OnGroupItemClickListener groupClickListener, String groupPosition) {
		if (null != groupClickListener && StringUtil.isNotEmpty(groupPosition)) {
			if (null == getListenerMap()) {
				setListenerMap(new HashMap<String, Object>());
			}
			getListenerMap().put("groupClickListener" + groupPosition, groupClickListener);
		}
	}

	public SecondaryMenuModel getDataModel() {
		return data;
	}

	public boolean isHaveSubMenu() {
		return haveSubMenu;
	}

	public void setHaveSubMenu(String haveSubMenu) {
		this.haveSubMenu = Boolean.parseBoolean(haveSubMenu);
	}

	public String getItemBg() {
		return StringUtil.isEmpty(itemBg) ? "#F1F1F1" : itemBg;
	}

	public void setItemBg(String itemBg) {
		this.itemBg = itemBg;
	}

	public String getItemSelBg() {
		return StringUtil.isEmpty(itemSelBg) ? "#FFFFFF" : itemSelBg;
	}

	public void setItemSelBg(String itemSelBg) {
		this.itemSelBg = itemSelBg;
	}

	public String getItemBgSub() {
		return StringUtil.isEmpty(itemBgSub) ? "#FFFFFF" : itemBgSub;
	}

	public void setItemBgSub(String itemBgSub) {
		this.itemBgSub = itemBgSub;
	}

	public String getItemSelBgSub() {
		return StringUtil.isEmpty(itemSelBgSub) ? "#209D2928" : itemSelBgSub;
	}

	public void setItemSelBgSub(String itemSelBgSub) {
		this.itemSelBgSub = itemSelBgSub;
	}

	public int getDividerHeight() {
		return dividerHeight >= 0 ? dividerHeight : 0;
	}

	public void setDividerHeight(String dividerHeight) {
		if (StringUtil.toInt(dividerHeight) >= 0)
			this.dividerHeight = Integer.parseInt(dividerHeight);
	}

	public String getDividerName() {
		return dividerName;
	}

	public void setDividerName(String dividerName) {
		this.dividerName = dividerName;
	}

	public int getDefaultGroupSel() {
		return defaultGroupSel;
	}

	public void setDefaultGroupSel(String defaultGroupSel) {
		if (StringUtil.toInt(defaultGroupSel) >= 0)
			this.defaultGroupSel = Integer.parseInt(defaultGroupSel);
	}

	public int getDefaultSubSel() {
		return defaultSubSel;
	}

	public void setDefaultSubSel(String defaultSubSel) {
		if (StringUtil.toInt(defaultSubSel) >= 0)
			this.defaultSubSel = Integer.parseInt(defaultSubSel);
	}

}
