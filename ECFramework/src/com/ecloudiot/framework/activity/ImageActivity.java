package com.ecloudiot.framework.activity;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchSingleTapListener;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase.DisplayType;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.utility.URLUtil;
import com.ecloudiot.framework.utility.ViewUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

public class ImageActivity extends Activity{
	private final String REQUEST_HD_IMG_SIZE= "1080";
	private final String REQUEST_SMALL_IMG_SIZE= "600";
	private ImageViewTouch imageZoom;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		
		initView();
		
		Bundle bundle=getIntent().getExtras();
		String imageUrl=bundle.getString("paramsString");
		
		String smallImgUrl = URLUtil.getCustomImageWholeUr(imageUrl,REQUEST_SMALL_IMG_SIZE);
		
		String getFullImgUrl=URLUtil.getCustomImageWholeUr(imageUrl, REQUEST_HD_IMG_SIZE);
		
		ImageLoader imgLoader=ImageLoader.getInstance();
		
		if(imgLoader.getDiscCache().get(getFullImgUrl).exists()){
			//大图缓存存在，则加载缓存里的数据
			imageZoom.setImageBitmap(imgLoader.loadImageSync(getFullImgUrl));
			
		}else{
			//大图缓存不存在，先显示小图，然后异步下载大图并显示
			imageZoom.setImageBitmap(imgLoader.loadImageSync(smallImgUrl));
			imgLoader.loadImage(getFullImgUrl, new imgLoadListener());
		}
	}
	
	private void initView(){
		imageZoom=(ImageViewTouch) findViewById(R.id.image_zoom);
		imageZoom.setDoubleTapEnabled(true);
		imageZoom.setScaleEnabled(true);
		imageZoom.setSingleTapListener(new closeView());
		imageZoom.setDisplayType(DisplayType.FIT_TO_SCREEN);
	}
	
	class imgLoadListener implements ImageLoadingListener{

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {
			
		}

		@Override
		public void onLoadingComplete(String arg0, View arg1, Bitmap bitmap) {
			ViewUtil.closeLoadingDianlog();
			imageZoom.setImageBitmap(bitmap);
		}

		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			ViewUtil.closeLoadingDianlog();
		}

		@Override
		public void onLoadingStarted(String arg0, View arg1) {
			ViewUtil.showLoadingDialog(ImageActivity.this, "", "", true);
		}
		
	}
	
	class closeView implements OnImageViewTouchSingleTapListener{

		@Override
		public void onSingleTapConfirmed() {
			finish();
		}		
	}
	/**
	 * 长按选项，还未制作
	 * @author Lee
	 *
	 */
	class optionView implements View.OnLongClickListener{

		@Override
		public boolean onLongClick(View v) {
			return true;
		}
		
	}
	
}
