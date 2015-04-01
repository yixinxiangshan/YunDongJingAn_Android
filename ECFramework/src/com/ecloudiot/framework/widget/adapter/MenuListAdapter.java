package com.ecloudiot.framework.widget.adapter;

import java.util.ArrayList;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.utility.ColorUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.ViewUtil;
import com.ecloudiot.framework.widget.SecondaryMenuWidget;
import com.ecloudiot.framework.widget.model.SecondaryMenuModel.MenuItemModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuListAdapter extends BaseAdapter {
	private final static String TAG = "MenuListAdapter";
	private ArrayList<MenuItemModel> menuList;
	// private SecondaryMenuWidget widget;
	private Context context;
	private int itemLayoutId;
	private int selection = -1;
	private String itemBg;
	private String itemSelBg;
	private String itemColor = "";

	public MenuListAdapter(Context context, ArrayList<MenuItemModel> menuList, String layoutName) {
		this(context, menuList, layoutName, null);
	}

	public MenuListAdapter(Context context, ArrayList<MenuItemModel> menuList, String layoutName, SecondaryMenuWidget widget) {
		this.setMenuList(menuList);
		this.context = context;
		if (StringUtil.isNotEmpty(layoutName)) {
			try {
				itemLayoutId = ResourceUtil.getLayoutIdFromContext(context, layoutName);
			} catch (Exception e) {
				LogUtil.w(TAG, "item layoutName is invalid,use default item_layout...");
				itemLayoutId = R.layout.widget_secondary_menu_item;
			}
		} else {
			itemLayoutId = R.layout.widget_secondary_menu_item;
		}
	}

	@Override
	public int getCount() {
		return getMenuList().size();
	}

	@Override
	public MenuItemModel getItem(int position) {
		return getMenuList().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		MenuItemModel itemModel = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(ECApplication.getInstance().getNowActivity()).inflate(this.itemLayoutId, null);
		}
		if (holder == null) {
			holder = new ViewHolder();
			try {
				holder.title = (TextView) convertView.findViewById(R.id.widget_menu_item_title);
				holder.icon = (ImageView) convertView.findViewById(R.id.widget_menu_item_icon);
				holder.layout = (LinearLayout) convertView.findViewById(R.id.widget_menu_item_layout);
			} catch (Exception e) {
				LogUtil.e(TAG, "item layout is invalid,check it now!!!");
				return convertView;
			}
		}
		String titleString = itemModel.getTitle();
		String iconString = itemModel.getIcon();
		holder.title.setText(titleString);
		if (StringUtil.isNotEmpty(iconString)) {
			holder.icon.setImageResource(ResourceUtil.getDrawableIdFromContext(context, iconString));
		}
		if (getSelection() == position) {
			if (itemColor.equals("")) {
				ViewUtil.setBackground(holder.layout, getItemSelBg());
			} else {
				ViewUtil.setBackground(holder.layout, "#f3f3f3");
				holder.title.setTextColor(ColorUtil.getColorValueFromRGB(itemColor));
			}
		} else {
			if (itemColor.equals("")) {
				ViewUtil.setBackground(holder.layout, getItemBg());
			} else {
				ViewUtil.setBackground(holder.layout, getItemBg());
				holder.title.setTextColor(ColorUtil.getColorValueFromRGB("#666666"));
			}
		}
		return convertView;
	}

	public String getItemColor() {
		return itemColor;
	}

	public void setItemColor(String itemColor) {
		this.itemColor = itemColor;
	}

	public int getSelection() {
		return selection;
	}

	public void setSelection(int selection) {
		this.selection = selection;
	}

	public ArrayList<MenuItemModel> getMenuList() {
		return menuList;
	}

	public void setMenuList(ArrayList<MenuItemModel> menuList) {
		this.menuList = menuList;
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

	private class ViewHolder {
		TextView title;
		// TextView subTitle;
		ImageView icon;
		// ImageView arrow;
		LinearLayout layout;
	}

}
