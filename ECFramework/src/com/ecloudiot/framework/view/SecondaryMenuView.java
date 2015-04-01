package com.ecloudiot.framework.view;

import java.util.ArrayList;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.event.linterface.OnGroupItemClickListener;
import com.ecloudiot.framework.utility.ColorUtil;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.widget.adapter.MenuListAdapter;
import com.ecloudiot.framework.widget.model.SecondaryMenuModel;
import com.ecloudiot.framework.widget.model.SecondaryMenuModel.MenuItemModel;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SecondaryMenuView extends LinearLayout {
	private final static String TAG = "SecondaryMenuView";
	private ListView firstListView;
	private ListView secondListView;
	private SecondaryMenuModel data;
	private ArrayList<MenuItemModel> groupList;
	private ArrayList<MenuItemModel> subMenuList;
	private int groupSel = -1;
	private int subSel = -1;
	private int tempGroupSel;
	private int tempSubSel;
	// 事件监听器
	private OnItemClickListener itemClickListener;
	private OnGroupItemClickListener groupItemClickListener;
	// 控件属性
	private boolean withSubMenu;
	private String itemBg;
	private String itemSelBg;
	private String itemBgSub;
	private String itemSelBgSub;
	private int dividerHeight;
	private String dividerName;
	private String mainColor;

	{
		setWithSubMenu(true);
		itemBg = "#EAEAEA";
		itemSelBg = "#FFFFFF";
		itemBgSub = "#FFFFFF";
		itemSelBgSub = "#209D2928";
		dividerHeight = 1;
		dividerName = "general_separation_repeate";
		mainColor = "#FF0000ff";
	}

	public SecondaryMenuView(Context context) {
		super(context);
	}

	public SecondaryMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialise(attrs);
		initView();
	}

	private void initialise(AttributeSet attrs) {
		TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SecondaryMenuView);
		setWithSubMenu(a.getBoolean(R.styleable.SecondaryMenuView_with_submenu, true));
		groupSel = a.getInt(R.styleable.SecondaryMenuView_prime_default_selection, 0);
		subSel = a.getInt(R.styleable.SecondaryMenuView_minor_default_selection, 0);
		if (StringUtil.isNotEmpty(a.getString(R.styleable.SecondaryMenuView_prime_item_bg))) {
			itemBg = a.getString(R.styleable.SecondaryMenuView_prime_item_bg);
		}
		if (StringUtil.isNotEmpty(a.getString(R.styleable.SecondaryMenuView_prime_item_selection_bg))) {
			itemSelBg = a.getString(R.styleable.SecondaryMenuView_prime_item_selection_bg);
		}
		if (StringUtil.isNotEmpty(a.getString(R.styleable.SecondaryMenuView_minor_item_bg))) {
			itemBgSub = a.getString(R.styleable.SecondaryMenuView_minor_item_bg);
		}
		if (StringUtil.isNotEmpty(a.getString(R.styleable.SecondaryMenuView_minor_item_selection_bg))) {
			itemSelBgSub = a.getString(R.styleable.SecondaryMenuView_minor_item_selection_bg);
		}
		dividerHeight = a.getInt(R.styleable.SecondaryMenuView_divider_height, 1);
		if (StringUtil.isNotEmpty(a.getString(R.styleable.SecondaryMenuView_divider_name))) {
			dividerName = a.getString(R.styleable.SecondaryMenuView_divider_name);
		}
		a.recycle();
	}

	private void initView() {
		LinearLayout layout = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_secondary_menu, this, false);
		firstListView = (ListView) layout.findViewById(R.id.view_secondary_menu_listView);
		secondListView = (ListView) layout.findViewById(R.id.view_secondary_menu_listView2);
		layout.removeAllViews();
		addView(firstListView);
		addView(secondListView);
	}

	public void setData(SecondaryMenuModel data) {
		this.data = data;
		groupList = data.getMenulList();
		if (groupList != null && groupList.size() > 0) {
			MenuListAdapter groupAdapter = new MenuListAdapter(getContext(), groupList, "view_secondary_menu_item");
			// 设置选中的项目
			if (groupSel < 0)
				for (int i = 0; i < groupList.size(); i++) {
					if ((groupList.get(i).getSelected() != null) && groupList.get(i).getSelected()) {
						groupSel = i;
					}
				}
			tempGroupSel = (groupSel >= groupList.size() || groupSel < 0) ? 0 : groupSel;

			if (isWithSubMenu()) {
				firstListView.setAdapter(groupAdapter);
				groupAdapter.setSelection(tempGroupSel);
				groupAdapter.setItemBg(getItemBg());
				groupAdapter.setItemSelBg(getItemSelBg());
				subMenuList = getSubMenu(tempGroupSel);
				MenuListAdapter subMenuAdapter = new MenuListAdapter(getContext(), subMenuList, "view_secondary_menu_item");
				secondListView.setAdapter(subMenuAdapter);
				subMenuAdapter.setSelection(subSel);
				subMenuAdapter.setItemBg(getItemBgSub());
				if (mainColor.equals(""))
					subMenuAdapter.setItemSelBg(getItemSelBgSub());
				else
					subMenuAdapter.setItemColor(mainColor);
			} else {
				firstListView.setAdapter(groupAdapter);
				groupAdapter.setSelection(groupSel);
				groupAdapter.setItemBg(getItemBgSub());
				groupAdapter.setItemSelBg(getItemSelBgSub());
				if (mainColor.equals(""))
					groupAdapter.setItemSelBg(getItemSelBg());
				else
					groupAdapter.setItemColor(mainColor);
				// 设置第一层菜单铺满
				LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				firstListView.setLayoutParams(lp);

			}
		}
		// 设置divider，如果有主色，则使用灰色搭配主色的方式
		if (getDividerHeight() > 0 && StringUtil.isNotEmpty(getDividerName())) {
			// LogUtil.d(TAG, "setData : getDividerName = " + getDividerName());
			if (mainColor.equals(""))
				firstListView.setDivider(getContext().getResources().getDrawable(ResourceUtil.getDrawableIdFromContext(getContext(), getDividerName())));
			else
				firstListView.setDivider(new ColorDrawable(ColorUtil.getColorValueFromRGB(itemBg)));
			firstListView.setDividerHeight(getDividerHeight());
			if (mainColor.equals(""))
				secondListView.setDivider(getContext().getResources().getDrawable(ResourceUtil.getDrawableIdFromContext(getContext(), getDividerName())));
			else
				secondListView.setDivider(new ColorDrawable(ColorUtil.getColorValueFromRGB(itemBg)));
			secondListView.setDividerHeight(getDividerHeight());
		}
		firstListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				tempGroupSel = position;
				refreshGroupMenu();
				if (isWithSubMenu()) {
					// 点击处理：不出发控件外设事件、刷新自己、显示子列表
					subMenuList = getSubMenu(tempGroupSel);
					tempSubSel = tempGroupSel == groupSel ? subSel : -1;
					refreshSubMenu();
				} else {
					// 更改groupSel、触发外设事件
					groupSel = position;
					if (null != itemClickListener) {
						itemClickListener.onItemClick(parent, view, position, id);
					}
				}
			}
		});
		secondListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 更改groupSel、subSel、 触发外部事件
				groupSel = tempGroupSel;
				subSel = tempSubSel = position;
				refreshSubMenu();
				LogUtil.d(TAG, "onItemClick : ...");
				if (groupItemClickListener != null) {
					groupItemClickListener.onGroupItemClick(groupSel, subSel);
				}
			}
		});
	}

	public void setData(String dataString) {
		if (StringUtil.isEmpty(dataString)) {
			LogUtil.e(TAG, "setData error: dataString is null ...");
			return;
		}
		LogUtil.d(TAG, "setData : dataString = " + dataString);
		parsingWidgetData(dataString);
		setData(data);
	}

	/**
	 * @param dataString
	 *            Ohmer-Jan 3, 2014 12:24:57 PM
	 */
	private void parsingWidgetData(String dataString) {
		try {
			data = GsonUtil.fromJson(dataString, SecondaryMenuModel.class);
		} catch (Exception e) {
			LogUtil.e(TAG, "parsing SecondaryMenuModel error" + e.toString());
		}
	}

	private ArrayList<MenuItemModel> getSubMenu(int position) {
		ArrayList<MenuItemModel> menuList = groupList.get(position).getMenuList();
		if (menuList == null || menuList.size() <= 0) {
			// if sub menu list is null then set self to sub menuList
			menuList = new ArrayList<SecondaryMenuModel.MenuItemModel>();
			menuList.add(groupList.get(position));
			groupList.get(position).setMenuList(menuList);
		}
		return menuList;
	}

	private void refreshGroupMenu() {
		((MenuListAdapter) (firstListView.getAdapter())).setSelection(tempGroupSel);
		((MenuListAdapter) (firstListView.getAdapter())).setMenuList(groupList);
		((MenuListAdapter) (firstListView.getAdapter())).notifyDataSetChanged();
	}

	private void refreshSubMenu() {
		((MenuListAdapter) (secondListView.getAdapter())).setSelection(tempSubSel);
		((MenuListAdapter) (secondListView.getAdapter())).setMenuList(subMenuList);
		((MenuListAdapter) (secondListView.getAdapter())).notifyDataSetChanged();
	}

	/**
	 * 设置单列menu item点击事件监听器
	 * 
	 * @param itemClickListener
	 *            Ohmer-Jan 3, 2014 12:28:15 PM
	 */
	public void setOnItemClickListener(OnItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	/**
	 * 设置二级menu item监听器
	 * 
	 * @param groupClickListener
	 *            Ohmer-Jan 3, 2014 12:28:37 PM
	 */
	public void setOnGroupItemClickListener(OnGroupItemClickListener groupClickListener) {
		this.groupItemClickListener = groupClickListener;
	}

	public String getItemBg() {
		return itemBg;
	}

	public void setItemBg(String itemBg) {
		this.itemBg = itemBg;
	}

	public String getItemSelBg() {
		return itemSelBg;
	}

	public void setItemSelBg(String itemSelBg) {
		this.itemSelBg = itemSelBg;
	}

	public String getItemBgSub() {
		return itemBgSub;
	}

	public void setItemBgSub(String itemBgSub) {
		this.itemBgSub = itemBgSub;
	}

	public String getItemSelBgSub() {
		return itemSelBgSub;
	}

	public void setItemSelBgSub(String itemSelBgSub) {
		this.itemSelBgSub = itemSelBgSub;
	}

	public int getDividerHeight() {
		return dividerHeight;
	}

	public void setDividerHeight(int dividerHeight) {
		this.dividerHeight = dividerHeight;
	}

	public String getDividerName() {
		return dividerName;
	}

	public void setDividerName(String dividerName) {
		this.dividerName = dividerName;
	}

	public void setPrimeDefaultSelection(int i) {
		groupSel = i;
	}

	public void setMinorDefaultSelection(int i) {
		subSel = i;
	}

	public boolean isWithSubMenu() {
		return withSubMenu;
	}

	public void setWithSubMenu(boolean withSubMenu) {
		LogUtil.d(TAG, "setWithSubMenu :"+withSubMenu);
		this.withSubMenu = withSubMenu;
	}

	public int getGroupSel() {
		return groupSel;
	}

	public void setGroupSel(int groupSel) {
		this.groupSel = groupSel;
	}

	public int getSubSel() {
		return subSel;
	}

	public void setSubSel(int subSel) {
		this.subSel = subSel;
	}
}
