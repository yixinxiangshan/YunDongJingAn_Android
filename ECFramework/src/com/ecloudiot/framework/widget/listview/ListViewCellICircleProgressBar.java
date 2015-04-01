package com.ecloudiot.framework.widget.listview;

import java.util.HashMap;

import org.json.JSONObject;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.fragment.ItemFragment;
import com.ecloudiot.framework.javascript.JsAPI;
import com.ecloudiot.framework.utility.ColorUtil;
import com.ecloudiot.framework.utility.DensityUtil;
import com.ecloudiot.framework.utility.GsonUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.google.gson.JsonObject;
import com.pascalwelsch.holocircularprogressbar.HoloCircularProgressBar;
//import com.ecloudiot.framework.utility.LogUtil;

@SuppressLint("InflateParams")
public class ListViewCellICircleProgressBar extends ListViewCellBase implements OnClickListener {
	private ObjectAnimator mProgressBarAnimator;
	// private static String TAG = "ListViewCellICircleProgressBar";
	public ListViewCellICircleProgressBar(String cellType, Context context, ListViewBaseAdapter listViewBaseAdapter) {
		super(cellType, context, listViewBaseAdapter);
		// LogUtil.d(TAG, ListViewCellICircleProgressBar.class.getName() + ": start");
	}
	/**
	 * 返回该类型cell的view
	 */
	public View getView(int position, View convertView, ViewGroup parent, JsonObject dataObj) {
		// LogUtil.d(TAG, ListViewCellICircleProgressBar.class.getName() + ": getView");
		ViewHolder holder;
		DataModel data;
		// 初始化数据及holder
		data = GsonUtil.fromJson(dataObj, DataModel.class);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.widget_listviewcell_circle_progressbar, null);
			holder = new ViewHolder();
			holder.bottomDivider = (ImageView) convertView.findViewById(R.id.widget_listviewcell_circle_progressbar_divider_bottom);
			holder.circularProgressBar = (HoloCircularProgressBar) convertView.findViewById(R.id.circularProgressBar);
			holder.imageLeftBtn = (ImageButton) convertView.findViewById(R.id.widget_listviewcell_circle_progressbar_leftBtn);
			holder.imageRightBtn = (ImageButton) convertView.findViewById(R.id.widget_listviewcell_circle_progressbar_rightBtn);
			holder.progressBarInnerNum = (TextView) convertView.findViewById(R.id.progressBarInnerNum);
			holder.progressBarInnerText = (TextView) convertView.findViewById(R.id.progressBarInnerText);
			holder.pre_tv = (TextView) convertView.findViewById(R.id.widget_listviewcell_circle_progressbar_pre_tv);
			holder.next_tv = (TextView) convertView.findViewById(R.id.widget_listviewcell_circle_progressbar_next_tv);
			holder.rightText = (TextView) convertView.findViewById(R.id.widget_listviewcell_circle_progressbar_rightText);
			holder.rightText_notice = (ImageView) convertView.findViewById(R.id.widget_listviewcell_circle_progressbar_rightText_notice);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ListViewBaseUtil.setBackGround(convertView, data._backgroundColor);
		ListViewBaseUtil.setImageView(holder.bottomDivider, data._bottomDivider, getContext());

		holder.circularProgressBar.setWheelSize(DensityUtil.dipTopx(getContext(), 6));
		holder.circularProgressBar.setProgressBackgroundColor(ColorUtil.getColorValueFromRGB("#A9A9A9"));
		holder.circularProgressBar.setProgressColor(Color.rgb(172, 219, 40));
		setProgress(holder.circularProgressBar, data.progressBarInnerNumPre);

		ListViewBaseUtil.setText(holder.progressBarInnerNum, data.progressBarInnerNum);
		ListViewBaseUtil.setText(holder.progressBarInnerText, data.progressBarInnerText);

		ListViewBaseUtil.setText(holder.rightText, data.rightText);
		HashMap<String, Object> mapRightText = new HashMap<String, Object>();
		mapRightText.put("target", "rightText");
		mapRightText.put("position", position);
		holder.rightText.setTag(mapRightText);

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("target", "leftBtn");
		map.put("position", position);
		holder.imageLeftBtn.setTag(map);
		holder.pre_tv.setTag(map);

		HashMap<String, Object> map1 = new HashMap<String, Object>();
		map1.put("target", "rightBtn");
		map1.put("position", position);
		holder.imageRightBtn.setTag(map1);
		holder.next_tv.setTag(map1);

		ListViewBaseUtil.setImageButtton(holder.imageLeftBtn, data.imageLeftBtn, getContext());
		ListViewBaseUtil.setImageButtton(holder.imageRightBtn, data.imageRightBtn, getContext());
		holder.imageLeftBtn.setOnClickListener(this);
		if (data.isRightText_notice_show()) {
			holder.rightText_notice.setVisibility(View.VISIBLE);
		} else {
			holder.rightText_notice.setVisibility(View.GONE);
		}
		holder.imageRightBtn.setOnClickListener(this);
		holder.rightText.setOnClickListener(this);
		holder.next_tv.setOnClickListener(this);
		holder.pre_tv.setOnClickListener(this);
		int duration = (Integer.parseInt(data.progressBarInnerNum) - Integer.parseInt(data.progressBarInnerNumPre)) * 15;
		if (duration < 0) {
			duration = 0;
		}
		animate(holder.circularProgressBar, null, Integer.parseInt(data.progressBarInnerNum) / 100.0f, duration);
		return convertView;
	}
	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		// LogUtil.d(TAG, "onItemInnerClick");
		HashMap<String, String> eventParams = new HashMap<String, String>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map = (HashMap<String, Object>) v.getTag();
		eventParams.put("target", map.get("target").toString());
		eventParams.put("position", "" + map.get("position"));

		if (getListViewBaseAdapter().getListViewBase().getPageContext() instanceof ItemActivity) {
			JsAPI.runEvent(((ItemActivity) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
			        .getListViewBase().getControlId(), "onItemInnerClick", new JSONObject(eventParams));
		} else if (getListViewBaseAdapter().getListViewBase().getPageContext() instanceof ItemFragment) {
			JsAPI.runEvent(((ItemFragment) getListViewBaseAdapter().getListViewBase().getPageContext()).getWidgetJsEvents(), getListViewBaseAdapter()
			        .getListViewBase().getControlId(), "onItemInnerClick", new JSONObject(eventParams));
		}
	}

	public void setProgress(HoloCircularProgressBar circularProgressBar, String progressBarInnerNum) {
		if (StringUtil.isEmpty(progressBarInnerNum)) {
			circularProgressBar.setProgress(0.0f);
		} else {
			int currentProgress = Integer.parseInt(progressBarInnerNum);
			circularProgressBar.setProgress(currentProgress / 100.0f);
		}
	}

	@SuppressLint("NewApi")
	private void animate(final HoloCircularProgressBar progressBar, final AnimatorListener listener, final float progress, final int duration) {

		mProgressBarAnimator = ObjectAnimator.ofFloat(progressBar, "progress", progress);
		mProgressBarAnimator.setDuration(duration);

		mProgressBarAnimator.addListener(new AnimatorListener() {

			@Override
			public void onAnimationCancel(final Animator animation) {
			}

			@Override
			public void onAnimationEnd(final Animator animation) {
				progressBar.setProgress(progress);
			}

			@Override
			public void onAnimationRepeat(final Animator animation) {
			}

			@Override
			public void onAnimationStart(final Animator animation) {
			}
		});
		if (listener != null) {
			mProgressBarAnimator.addListener(listener);
		}
		mProgressBarAnimator.reverse();
		mProgressBarAnimator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(final ValueAnimator animation) {
				progressBar.setProgress((Float) animation.getAnimatedValue());
			}
		});
		progressBar.setMarkerProgress(progress);
		mProgressBarAnimator.start();
	}

	/**
	 * holder
	 */
	static class ViewHolder {
		ImageButton imageLeftBtn;
		ImageView bottomDivider;
		ImageButton imageRightBtn;
		HoloCircularProgressBar circularProgressBar;
		TextView progressBarInnerNum;
		TextView progressBarInnerText;
		TextView rightText;
		ImageView rightText_notice;
		TextView next_tv;
		TextView pre_tv;
	}
	/**
	 * model
	 */
	static class DataModel {
		private ImageModel _bottomDivider = null;
		public ImageModel get_bottomDivider() {
			return _bottomDivider;
		}
		public void set_bottomDivider(ImageModel _bottomDivider) {
			this._bottomDivider = _bottomDivider;
		}
		private boolean rightText_notice_show;
		private ImageModel imageLeftBtn;
		private ImageModel imageRightBtn;
		private ImageModel rightText_notice;
		private ColorModel _backgroundColor = null;
		private String progressBarInnerNum;
		private String progressBarInnerNumPre;
		private String progressBarInnerText;
		public ImageModel getRightText_notice() {
			return rightText_notice;
		}
		public void setRightText_notice(ImageModel rightText_notice) {
			this.rightText_notice = rightText_notice;
		}

		public String getProgressBarInnerNum() {
			return progressBarInnerNum;
		}
		public void setProgressBarInnerNum(String progressBarInnerNum) {
			this.progressBarInnerNum = progressBarInnerNum;
		}
		public String getProgressBarInnerText() {
			return progressBarInnerText;
		}
		public void setProgressBarInnerText(String progressBarInnerText) {
			this.progressBarInnerText = progressBarInnerText;
		}
		public String getRightText() {
			return rightText;
		}
		public void setRightText(String rightText) {
			this.rightText = rightText;
		}
		private String rightText;
		public ImageModel getImageLeftBtn() {
			return imageLeftBtn;
		}
		public void setImageLeftBtn(ImageModel imageLeftBtn) {
			this.imageLeftBtn = imageLeftBtn;
		}
		public ImageModel getImageRightBtn() {
			return imageRightBtn;
		}
		public void setImageRightBtn(ImageModel imageRightBtn) {
			this.imageRightBtn = imageRightBtn;
		}
		public ColorModel get_backgroundColor() {
			return _backgroundColor;
		}
		public void set_backgroundColor(ColorModel _backgroundColor) {
			this._backgroundColor = _backgroundColor;
		}
		public boolean isRightText_notice_show() {
			return rightText_notice_show;
		}
		public void setRightText_notice_show(boolean rightText_notice_show) {
			this.rightText_notice_show = rightText_notice_show;
		}
		public String getProgressBarInnerNumPre() {
			return progressBarInnerNumPre;
		}
		public void setProgressBarInnerNumPre(String progressBarInnerNumPre) {
			this.progressBarInnerNumPre = progressBarInnerNumPre;
		}
	}

}
