package com.ecloudiot.framework.widget.adapter;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchSingleTapListener;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.javascript.JsUtility;
import com.ecloudiot.framework.utility.Constants;
import com.ecloudiot.framework.utility.FileUtil;
import com.ecloudiot.framework.widget.GalleryWidget;
import com.ecloudiot.framework.widget.model.SlideShowItemModel;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ZoomGalleryAdapter extends PagerAdapter {
	private final String TAG = "ZoomGalleryAdapter";
	private List<SlideShowItemModel> data;
	private Activity context;
	private int itemLayoutId;

	public ZoomGalleryAdapter(Activity context, List<SlideShowItemModel> data) {
		Log.d(TAG, "start");
		this.context = context;
		this.data = data;
		this.itemLayoutId = R.layout.widget_slideshow_item;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public View instantiateItem(ViewGroup viewcontainer, int position) {
		SlideShowItemModel itemContent = this.data.get(position);

		View container = (View) LayoutInflater.from(context).inflate(this.itemLayoutId, null);
		TextView textDes = (TextView) container.findViewById(R.id.widget_slideshow_item_des);
		ImageViewTouch touchImageView = (ImageViewTouch) container.findViewById(R.id.widget_slideshow_item_imageview);
		touchImageView.setSingleTapListener(new OnImageViewTouchSingleTapListener() {

			@Override
			public void onSingleTapConfirmed() {
				Log.d(TAG, "ImageViewTouch onSingleTap");
				GalleryWidget.getDialog().dismiss();
			}
		});
		String des = itemContent.getDescription().toString();
		String imageString = itemContent.getImage().toString();

		textDes.setText(des);
		if (imageString.equals("")) {
			imageString = Constants.IMAGE_DEFAULT_SIMAGE;
		}

		if (imageString.indexOf("/storage") != -1) {
			// holder.imageView.setImageResource(R.drawable.widget_gallery_item_addbtn);
			Bitmap mBitmap = FileUtil.getBitmapFromSD(new File(imageString));
			if (mBitmap != null) {
				touchImageView.setImageBitmap(mBitmap);
			}
		} else {
			// 图片指定宽的尺寸
			String[] ss = new String[2];
			ss = imageString.split("\\.");
			Display display = JsUtility.GetActivityContext().getWindowManager().getDefaultDisplay();
			int screenWidth = display.getWidth();
			String imgnameString = Constants.BASE_URL_IMAGE + ss[0] + "_" + screenWidth + "." + ss[1];
			ImageLoader.getInstance().displayImage(imgnameString, touchImageView);

		}

		viewcontainer.addView(container);
		return container;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object view) {
		container.removeView((View) view);
	}

	@Override
	public void finishUpdate(View container) {
	}

	@Override
	public void restoreState(Parcelable state, ClassLoader loader) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View container) {
	}

}