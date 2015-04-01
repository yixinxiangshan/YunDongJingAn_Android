package com.ecloudiot.framework.widget.listview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ecloudiot.framework.utility.ColorUtil;
import com.ecloudiot.framework.utility.DensityUtil;
import com.ecloudiot.framework.utility.ImageUtil;
import com.ecloudiot.framework.utility.ResourceUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.URLUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.readystatesoftware.viewbadger.BadgeView;

public class ListViewBaseUtil {

	public static void setImageView(ImageView view, ImageModel imageData, Context context) {
		boolean customNotControl = true;
		if (imageData == null) {
			view.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.VISIBLE);
			// 设置尺寸
			int imageWidth = 0;
			int imageHeight = 0;
			if (imageData.getImageSize().equals("custom")) {
				customNotControl=false;
			}else if (imageData.getImageSize().equals("fitSize")) {
				imageWidth = LayoutParams.WRAP_CONTENT;
				imageHeight = LayoutParams.WRAP_CONTENT;
			} else if (imageData.getImageSize().equals("micro")) {
				imageWidth = DensityUtil.dipTopx(context, 20);
				imageHeight = DensityUtil.dipTopx(context, 20);
			} else if (imageData.getImageSize().equals("mini")) {
				imageWidth = DensityUtil.dipTopx(context, 36);
				imageHeight = DensityUtil.dipTopx(context, 36);
			} else if (imageData.getImageSize().equals("small")) {
				imageWidth = DensityUtil.dipTopx(context, 50);
				imageHeight = DensityUtil.dipTopx(context, 50);
			} else if (imageData.getImageSize().equals("middle")) {
				imageWidth = DensityUtil.dipTopx(context, 70);
				imageHeight = DensityUtil.dipTopx(context, 70);
			} else if (imageData.getImageSize().equals("large")) {
				imageWidth = DensityUtil.dipTopx(context, 200);
				imageHeight = DensityUtil.dipTopx(context, 150);
			} else if (imageData.getImageSize().equals("xlarge")) {
				imageWidth = DensityUtil.dipTopx(context, 320);
				imageHeight = DensityUtil.dipTopx(context, 240);
			}
			if(customNotControl){
				view.getLayoutParams().width = imageWidth;
				view.getLayoutParams().height = imageHeight;
			}
			// 设置图片
			if (imageData.getImageType().equals("resource")) {
				view.setImageResource(ResourceUtil.getIdFromContext(imageData.getImageSrc(), "drawable"));
			} else if (imageData.getImageType().equals("assets")) {
				view.setImageDrawable(ImageUtil.getDrawableFromConfig(imageData.getImageSrc()));
			} else if (imageData.getImageType().equals("imageServer") && (imageData.getImageSrc() != null) && (!imageData.getImageSrc().equals(""))) {
				String imgWholeUrl = URLUtil.getSImageWholeUrl(imageData.getImageSrc());
				ImageLoader.getInstance().displayImage(imgWholeUrl, view);
			} else {
				view.setVisibility(View.GONE);
			}
			// 设置点击事件
		}

	}

	/**
	 * 
	 * @param view
	 * @param imageData
	 * @param alwaysShow
	 *            一直显示ImageView 没有图片 显示空白位
	 * @param context
	 */
	public static void setImageView(ImageView view, ImageModel imageData, Boolean alwaysShow, Context context) {
		setImageView(view, imageData, context);
		if (alwaysShow) {
			if (view.getVisibility() == View.GONE)
				view.setVisibility(View.VISIBLE);
		}
	}
	public static void setImageButtton(ImageButton view, ImageModel imageData, Context context) {
		if (imageData == null) {
			view.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.VISIBLE);
			// 设置尺寸
			int imageWidth = 0;
			int imageHeight = 0;
			if (imageData.getImageSize().equals("fitSize")) {
				imageWidth = LayoutParams.WRAP_CONTENT;
				imageHeight = LayoutParams.WRAP_CONTENT;
			} else if (imageData.getImageSize().equals("micro")) {
				imageWidth = DensityUtil.dipTopx(context, 20);
				imageHeight = DensityUtil.dipTopx(context, 20);
			} else if (imageData.getImageSize().equals("mini")) {
				imageWidth = DensityUtil.dipTopx(context, 36);
				imageHeight = DensityUtil.dipTopx(context, 36);
			} else if (imageData.getImageSize().equals("small")) {
				imageWidth = DensityUtil.dipTopx(context, 50);
				imageHeight = DensityUtil.dipTopx(context, 50);
			} else if (imageData.getImageSize().equals("middle")) {
				imageWidth = DensityUtil.dipTopx(context, 70);
				imageHeight = DensityUtil.dipTopx(context, 70);
			} else if (imageData.getImageSize().equals("large")) {
				imageWidth = DensityUtil.dipTopx(context, 200);
				imageHeight = DensityUtil.dipTopx(context, 150);
			} else if (imageData.getImageSize().equals("xlarge")) {
				imageWidth = DensityUtil.dipTopx(context, 320);
				imageHeight = DensityUtil.dipTopx(context, 240);
			}
			view.getLayoutParams().width = imageWidth;
			view.getLayoutParams().height = imageHeight;
			// 设置图片
			if (imageData.getImageType().equals("resource")) {
				view.setImageResource(ResourceUtil.getIdFromContext(imageData.getImageSrc(), "drawable"));
			} else if (imageData.getImageType().equals("assets")) {
				view.setImageDrawable(ImageUtil.getDrawableFromConfig(imageData.getImageSrc()));
			} else if (imageData.getImageType().equals("imageServer")) {
				String imgWholeUrl = URLUtil.getSImageWholeUrl(imageData.getImageSrc());
				ImageLoader.getInstance().displayImage(imgWholeUrl, view);
			} else {
				view.setVisibility(View.GONE);
			}
			// 设置点击事件

		}

	}
	public static void setLayoutSize(View layout, int dpSize, Context context) {
		if (dpSize > 0)
			layout.getLayoutParams().width = DensityUtil.dipTopx(context, dpSize);
		else {
			layout.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
			layout.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
		}
	}
	@SuppressLint("ResourceAsColor")
	public static void setText(TextView view, String text, ColorModel textColor, int rDefaultColor, Context context) {
		setText(view, text);
		if (textColor == null) {
			view.setTextColor(context.getResources().getColor(rDefaultColor));
		} else {
			if (StringUtil.isEmpty(textColor.getPressed()) && StringUtil.isEmpty(textColor.getSelected()))
				view.setTextColor(ColorUtil.getColorValueFromRGB(textColor.getNormal()));
			else
				view.setTextColor(ColorUtil.getColorState(textColor.getNormal(), textColor.getPressed(), textColor.getSelected()));
		}
	}
	@SuppressLint("ResourceAsColor")
	public static void setText(TextView view, String text, ColorModel textColor, Context context) {
		setText(view, text);
		if (textColor != null) {
			if (StringUtil.isEmpty(textColor.getPressed()) && StringUtil.isEmpty(textColor.getSelected()) && (!StringUtil.isEmpty(textColor.getNormal())))
				view.setTextColor(ColorUtil.getColorValueFromRGB(textColor.getNormal()));
			else if (!StringUtil.isEmpty(textColor.getNormal()))
				view.setTextColor(ColorUtil.getColorState(textColor.getNormal(), textColor.getPressed(), textColor.getSelected()));
		}
	}
	public static void setText(TextView view, String text) {
		if (StringUtil.isEmpty(text)) {
			view.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.VISIBLE);
			view.setText(toDBC(text));
		}
	}
	public static void setTextDBC(TextView view, String text) {
		if (StringUtil.isEmpty(text)) {
			view.setVisibility(View.GONE);
		} else {
			view.setVisibility(View.VISIBLE);
			view.setText(toDBC(text));
		}
	}

	public static String toDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}
	/**
	 * 显示notice信息
	 * 
	 * @param view
	 * @param noticeNum
	 *            -1 表示不显示，0 表示显示不带数字的红点，-2表示显示new,其实数字则显示该数字。
	 */
	public static void setNotice(TextView view, int noticeNum, Context context) {
		if (noticeNum == -1) {
			view.setVisibility(View.GONE);
		} else if (noticeNum == 0) {
			view.getLayoutParams().height = DensityUtil.dipTopx(context, 10);
			view.getLayoutParams().width = DensityUtil.dipTopx(context, 10);
			view.setVisibility(View.VISIBLE);
		} else if (noticeNum == -2) {
			view.getLayoutParams().width = DensityUtil.dipTopx(context, 40);
			view.getLayoutParams().height = DensityUtil.dipTopx(context, 15);
			view.setText("new");
			view.setVisibility(View.VISIBLE);
		} else {
			view.setText("" + noticeNum);
			view.getLayoutParams().height = DensityUtil.dipTopx(context, 20);
			view.getLayoutParams().width = DensityUtil.dipTopx(context, 20);
			view.setVisibility(View.VISIBLE);
		}
	}
	public static void setBadge(ImageView view, int leftBadge, Context context) {
		BadgeView badge = new BadgeView(context, view);
		badge.setText("new");
		badge.show();
		// if (leftBadge == -1) {
		// badge.setText("1");
		// badge.show();
		// } else if (leftBadge == 0) {
		// badge.setText("");
		// } else if (leftBadge == -2) {
		// badge.setText("new");
		// badge.show();
		// } else {
		// badge.setText("" + leftBadge);
		// badge.show();
		// }

	}
	@SuppressLint("NewApi")
	public static void setBackGround(View view, ColorModel backgroundColor) {
		if (backgroundColor == null) {
			view.getBackground().setAlpha(0);
		} else {
			// LogUtil.d(TAG, "color:"+backgroundColor.normal+";pressed:"+backgroundColor.pressed+";setAlpha:"+backgroundColor.alpha);
			if (StringUtil.isEmpty(backgroundColor.getPressed()) && StringUtil.isEmpty(backgroundColor.getSelected()))
				view.setBackgroundColor(ColorUtil.getColorValueFromRGB(backgroundColor.getNormal()));
			else
				view.setBackgroundDrawable(ColorUtil.getColorStateDrawable(backgroundColor.getNormal(), backgroundColor.getPressed(),
				        backgroundColor.getSelected()));
			if (backgroundColor.getAlpha() != -1) {
				view.getBackground().setAlpha(backgroundColor.getAlpha());
			}
		}
	}
}
