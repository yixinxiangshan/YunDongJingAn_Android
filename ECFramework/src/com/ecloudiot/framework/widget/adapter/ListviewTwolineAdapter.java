package com.ecloudiot.framework.widget.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.utility.ImageUtil;
import com.ecloudiot.framework.utility.IntentUtil;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.PageUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.URLUtil;
import com.ecloudiot.framework.widget.ListViewWidget;
import com.ecloudiot.framework.widget.model.ListviewModel;
import com.ecloudiot.framework.widget.model.ListviewitemTwolineModel;
import com.michaelnovakjr.numberpicker.NumberPicker;
import com.michaelnovakjr.numberpicker.NumberPicker.OnChangedListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.viewbadger.BadgeView;

public class ListviewTwolineAdapter extends BaseAdapter {
	private final String TAG = "ListviewTwolineAdapter";
	private ListviewModel data;
	private int itemLayoutId;
	private ListViewWidget widget;
	private int location;
	private Context context;

	public ListviewTwolineAdapter(Context context, ListviewModel dataModel, String layoutName, ListViewWidget widget) {
		LogUtil.d(TAG, "init");
		this.context = context;
		this.widget = widget;
		this.data = dataModel;
		if (StringUtil.isNotEmpty(layoutName)) {
			try {
				itemLayoutId = ResourceUtil.getLayoutIdFromContext(context, layoutName);
			} catch (Exception e) {
				LogUtil.e(TAG, "ListviewTwolineAdapter error : item layoutName is invalid...");
				itemLayoutId = R.layout.widget_listview_item_twoline;
			}
		} else {
			itemLayoutId = R.layout.widget_listview_item_twoline;
		}
	}

	public void setLayout(int layoutId) {
		this.itemLayoutId = layoutId;
	}

	/**
	 * 重设 数据
	 * 
	 * @param dataModel
	 */
	public void resetData(ListviewModel dataModel) {
		this.data = null;
		this.data = dataModel;
	}

	@Override
	public int getCount() {
		int contentSize = 0;
		if (null != data && data.getListByType() != null) {
			contentSize = data.getListByType().size();
		}
		return contentSize;
	}

	@Override
	public ListviewitemTwolineModel getItem(int position) {
		return this.getCount() == 0 ? null : data.getListByType().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ListviewitemTwolineModel itemContent = this.getItem(position);
		this.location = position;
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(ECApplication.getInstance().getNowActivity()).inflate(this.itemLayoutId, null);
		}
		holder = new ViewHolder();
		try {
			holder.twoline_title_tview = (TextView) convertView.findViewById(R.id.widget_listview_item_twoline_title);
			holder.twoline_time_tview = (TextView) convertView.findViewById(R.id.widget_listview_item_twoline_time);
			holder.twoline_subtitle_tview = (TextView) convertView.findViewById(R.id.widget_listview_item_twoline_subtile);
			holder.twoline_expand_tview = (TextView) convertView.findViewById(R.id.widget_listview_item_twoline_expandtext);
			holder.twoline_imageview = (ImageView) convertView.findViewById(R.id.imageview_left);
			holder.imageview_layout = (LinearLayout) convertView.findViewById(R.id.image_layout);
			holder.numberPicker = (NumberPicker) convertView.findViewById(R.id.widget_listview_item_twoline_numberpicker);
			holder.imgBt = (ImageButton) convertView.findViewById(R.id.widget_listview_item_twoline_imgbt);
			holder.arrView = (LinearLayout) convertView.findViewById(R.id.left_image_parrow);
			holder.expandLayout = (LinearLayout) convertView.findViewById(R.id.widget_listview_item_expand_llayout);
			holder.badge = (BadgeView) convertView.findViewById(R.id.badge);
			holder.icon = (ImageView) convertView.findViewById(R.id.widget_listview_item_icon);
			holder.leftIcon = (ImageView) convertView.findViewById(R.id.left_image_icon);
		} catch (Exception e) {
			LogUtil.e(TAG, "item layout is invalid,check it now!!!");
			return convertView;
		}

		String titleString = itemContent.getTitle();
		String timeString = itemContent.getTimeString();
		String abstractString = itemContent.getAbstracts();
		String coverString = itemContent.getImage_cover();
		String expandString = itemContent.getExpandString();
		String controlId = itemContent.getControlId();

		holder.twoline_title_tview.setText(titleString);

		// subtitle
		if (null != holder.twoline_subtitle_tview) {
			if (StringUtil.isNotEmpty(abstractString)) {
				holder.twoline_subtitle_tview.setText(abstractString);
				holder.twoline_subtitle_tview.setVisibility(View.VISIBLE);
			} else {
				holder.twoline_subtitle_tview.setVisibility(View.GONE);
			}
		}

		// time
		if (null != holder.twoline_time_tview) {
			if (StringUtil.isNotEmpty(timeString)) {
				holder.twoline_time_tview.setText(timeString);
				holder.twoline_time_tview.setVisibility(View.VISIBLE);
			} else {
				holder.twoline_time_tview.setVisibility(View.GONE);
			}
		}
		// image
		if (null != holder.twoline_imageview && holder.imageview_layout != null) {
			if (StringUtil.isNotEmpty(coverString) && ((coverString.split("\\.")).length == 2)) {
				String imgWholeUrl = URLUtil.getSImageWholeUrl(coverString);
				ImageLoader.getInstance().displayImage(imgWholeUrl, holder.twoline_imageview);
				holder.imageview_layout.setVisibility(View.VISIBLE);
			} else if (StringUtil.isNotEmpty(coverString) && ((coverString.split("\\.")).length == 1)) {
				holder.twoline_imageview.setImageResource(ResourceUtil.getIdFromContext(coverString, "drawable"));
				holder.imageview_layout.setVisibility(View.VISIBLE);
			} else {
				holder.imageview_layout.setVisibility(View.GONE);
				// LogUtil.e(TAG, "getView error: there is no image view ");
			}
		}
		// expand
		if (null != holder.twoline_expand_tview) {
			if (StringUtil.isNotEmpty(expandString)) {
				holder.twoline_expand_tview.setText(Html.fromHtml(expandString));
				if (((ListViewWidget) widget).isWithStrikeThruTextFlag()) {
					holder.twoline_expand_tview.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG); // 中划线
				}
				holder.twoline_expand_tview.setVisibility(View.VISIBLE);
			} else {
				holder.twoline_expand_tview.setVisibility(View.GONE);
			}
		}
		// 如果没有点击事件，则隐藏取消右边的箭头
		if (null != holder.arrView) {
			if (itemContent.isClickable()) {
				holder.arrView.setVisibility(View.VISIBLE);
			} else {
				holder.arrView.setVisibility(View.GONE);
			}
		}
		if (null != holder.numberPicker) {
			if (itemContent.getNumPickerMax() <= 0) {
				holder.numberPicker.setEnabled(false);
				holder.numberPicker.setVisibility(View.GONE);
			} else {
				holder.numberPicker.setCurrent(itemContent.getNumberDefault());
				holder.numberPicker.setEnabled(true);
				holder.numberPicker.setVisibility(View.VISIBLE);
				holder.numberPicker.setRange(0, itemContent.getNumPickerMax());
				holder.numberPicker.setOnChangeListener(new OnChangedListener() {
					int position = location;

					@Override
					public void onChanged(NumberPicker picker, int oldVal, int newVal) {
						((ListViewWidget) widget).getOnChangedListener().onChanged(picker, oldVal, newVal, position);
					}
				});
			}
		}
		if (null != holder.imgBt) {
			if (!itemContent.isWithBt()) {
				holder.imgBt.setVisibility(View.GONE);
			} else {
				holder.imgBt.setVisibility(View.VISIBLE);
				holder.imgBt.setFocusable(false);
				holder.imgBt.setOnClickListener(new OnClickListener() {
					int position = location;

					@Override
					public void onClick(View v) {
						LogUtil.d(TAG, "position:" + position);
						((ListViewWidget) widget).getOnButtonClickListener().onClick(v, position);
					}
				});
			}
		}

		if (null != holder.expandLayout) {
			if (StringUtil.isNotEmpty(controlId)) {
				View widget = (View) PageUtil.initWidgetNamed(controlId, IntentUtil.getActivity());
				if (null != widget.getParent())
					((ViewGroup) (widget.getParent())).removeView(widget);
				holder.expandLayout.setVisibility(View.VISIBLE);
				holder.expandLayout.addView(widget, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			} else {
				holder.expandLayout.setVisibility(View.GONE);
			}
		}

		if (null != holder.badge) {
			holder.badge.setVisibility(View.GONE);
		}

		if (null != holder.icon) {
			if (itemContent.isShowIcon()) {
				holder.icon.setVisibility(View.VISIBLE);
			} else {
				holder.icon.setVisibility(View.GONE);
			}
			if (StringUtil.isNotEmpty(itemContent.getIcon())) {
				holder.icon.setVisibility(View.VISIBLE);
				// 获取图片，如果资源里没有，就从config目录获取
				try {
					Integer res_id = ResourceUtil.getDrawableIdFromContext(context, itemContent.getIcon());
					if (res_id == 0) {
						holder.icon.setImageDrawable(ImageUtil.getDrawableFromConfig(itemContent.getIcon()));
					} else
						holder.icon.setImageResource(res_id);
				} catch (Exception e) {
					LogUtil.e(TAG, "tab item IconName  is getIcon : " + itemContent.getIcon());
				}
				// holder.icon.setImageResource(ResourceUtil.getDrawableIdFromContext(context,
				// itemContent.getIcon()));
			}
		}

		// 如果存在lefticon，则布局一个icon到左边
		if (null != holder.leftIcon && StringUtil.isNotEmpty(itemContent.getLeftIcon())) {

			RelativeLayout llLayout = (RelativeLayout) convertView.findViewById(R.id.widget_listview_item_title_layout);
			RelativeLayout.LayoutParams lp = (LayoutParams) llLayout.getLayoutParams();
			lp.addRule(RelativeLayout.RIGHT_OF, holder.leftIcon.getId());
			llLayout.setLayoutParams(lp);
			holder.leftIcon.setVisibility(View.VISIBLE);
			// 获取图片，如果资源里没有，就从config目录获取
			try {
				Integer res_id = ResourceUtil.getDrawableIdFromContext(context, itemContent.getLeftIcon());
				if (res_id == 0) {
					holder.leftIcon.setImageDrawable(ImageUtil.getDrawableFromConfig(itemContent.getLeftIcon()));
				} else
					holder.leftIcon.setImageResource(res_id);
			} catch (Exception e) {
				LogUtil.e(TAG, "tab item IconName  is getIcon : " + itemContent.getIcon());
			}
			// holder.icon.setImageResource(ResourceUtil.getDrawableIdFromContext(context,
			// itemContent.getIcon()));
		}

		return convertView;

	}

	static class ViewHolder {
		TextView twoline_title_tview;
		TextView twoline_subtitle_tview;
		TextView twoline_time_tview;
		TextView twoline_expand_tview;
		LinearLayout imageview_layout;
		ImageView twoline_imageview;
		NumberPicker numberPicker;
		ImageButton imgBt;
		LinearLayout arrView;
		LinearLayout expandLayout;
		BadgeView badge;
		ImageView icon;
		ImageView leftIcon;
	}
}
