package com.ecloudiot.framework.widget.adapter;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.BaseActivity;
import com.ecloudiot.framework.utility.Constants;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.URLUtil;
import com.ecloudiot.framework.widget.model.SlideShowItemModel;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;


public class SlideShowAdapter extends PagerAdapter {
	private final String TAG = "SlideShowAdapter";
	private List<SlideShowItemModel> data;
	private Activity context;
	private int itemLayoutId;

	public SlideShowAdapter(Activity context, List<SlideShowItemModel> data) {
		LogUtil.d(TAG,"start");
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
		final ImageViewTouch touchImageView = (ImageViewTouch) container.findViewById(R.id.widget_slideshow_item_imageview);
		String des = itemContent.getDescription().toString();
		String imageString = itemContent.getImage().toString();

		textDes.setText(des);
		if (imageString.equals("")) {
			imageString = Constants.IMAGE_DEFAULT_SIMAGE;
		}

		String imgnameString = URLUtil.getImageWholeUrl(imageString);
		LogUtil.d(TAG, "imageString:" + imgnameString);
		((BaseActivity)context).setProgressIndeterminateVisible(true);
		ImageLoader.getInstance().loadImage(imgnameString, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String arg0, View arg1) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
				((BaseActivity)context).setProgressIndeterminateVisible(false);
			}
			
			@Override
			public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
				((BaseActivity)context).setProgressIndeterminateVisible(false);
				touchImageView.setDisplayType(DisplayType.FIT_TO_SCREEN);
				LogUtil.i(TAG, "onLoadingComplete : bitmap width + "+bitmap.getWidth()+", height = "+bitmap.getHeight());
				touchImageView.setImageBitmap(bitmap);
			}
			
			@Override
			public void onLoadingCancelled(String arg0, View arg1) {
				((BaseActivity)context).setProgressIndeterminateVisible(false);
				
			}
		});


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
