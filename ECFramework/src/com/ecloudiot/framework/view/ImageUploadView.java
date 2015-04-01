package com.ecloudiot.framework.view;

import java.io.File;
import java.util.HashMap;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.utility.DensityUtil;
import com.ecloudiot.framework.utility.FilePath;
import com.ecloudiot.framework.utility.ImageUtil;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.utility.URLUtil;
import com.ecloudiot.framework.utility.http.HttpAsyncHandler;
import com.ecloudiot.framework.utility.http.UploadFileClient;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ImageView.ScaleType;

public class ImageUploadView extends RelativeLayout {
	private ImageView imageView;
	private ImageButton imageButton;
	private ProgressBar progressBar;
	private String imageServerUrl = "http://is.hudongka.com/saveimg.php";
	private String imageUploadKey = "image";
	private OnUploadImageListener uploadImageListener;
	private OnRemoveListener removeListener;
	private FilePath filePath;

	public ImageUploadView(Context context) {
		super(context);
		initView();
	}

	public ImageUploadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public ImageUploadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {
		filePath = new FilePath();
		imageView = new ImageView(getContext());
		imageView.setScaleType(ScaleType.CENTER_CROP);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		int margin = DensityUtil.dipTopx(getContext(), 5);
		layoutParams.setMargins(margin, margin, margin, margin);
		this.addView(imageView, layoutParams);
		imageButton = new ImageButton(getContext());
		imageButton.setBackgroundResource(R.drawable.gen_red_delete_fork_xml);
		layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		this.addView(imageButton, layoutParams);
		progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
		layoutParams = new LayoutParams(DensityUtil.dipTopx(getContext(), 10), DensityUtil.dipTopx(getContext(), 10));
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		this.addView(progressBar, layoutParams);
		progressBar.setVisibility(GONE);

		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((ViewGroup) ImageUploadView.this.getParent()).removeView(ImageUploadView.this);
				if (removeListener != null) {
					removeListener.onRemove(ImageUploadView.this);
				}
			}
		});
	}

	public void setImageFile(FilePath filePath) {
		this.filePath = filePath;
		if (StringUtil.isNotEmpty(filePath.getLocalFilePaths())) {
			setImageFile(new File(filePath.getLocalFilePaths()), false);
		} else if (StringUtil.isNotEmpty(filePath.getNetFilePaths())) {
			ImageLoader.getInstance().displayImage(URLUtil.getSImageWholeUrl(filePath.getNetFilePaths()), imageView);
		}
	}

	public void setImageFile(File imageFile) {
		setImageFile(imageFile, true);
	}

	public void setImageFile(File imageFile, boolean upload) {
		Bitmap bitmap = ImageUtil.smallBitmap(imageFile.getPath());
		filePath.setLocalFilePaths(imageFile.getPath());
		imageView.setImageBitmap(bitmap);
		if (upload) {
			uploadImage(imageFile);
		}
	}

	private void uploadImage(final File imageFile) {
		// 触发上传时间
		HashMap<String, String> params = new HashMap<String, String>();
		params.put(imageUploadKey, "file://" + imageFile.getPath());
		params.put("method", "upload");
		progressBar.setVisibility(VISIBLE);
		if (uploadImageListener != null) {
			uploadImageListener.onUplaodStart();
		}
		UploadFileClient.Instance().post(imageServerUrl, params, new HttpAsyncHandler() {

			@Override
			public void onSuccess(String resopnseString) {
				progressBar.setVisibility(GONE);
				if (StringUtil.isImageName(resopnseString)) {
					filePath.setNetFilePaths(resopnseString);
				}
				if (uploadImageListener != null) {
					uploadImageListener.onUploadSuccess(resopnseString);
				}
			}

			@Override
			public void onFailure(String resopnseString) {
				progressBar.setVisibility(GONE);
				imageView.setImageResource(R.drawable.gen_load_failure);
				if (uploadImageListener != null) {
					uploadImageListener.onUploadFailure();
				}
			}

			@Override
			public void onProgress(Float progress) {
				progressBar.setProgress((int) (progress * 100));
			}

			@Override
			public void onResponse(String resopnseString) {
				// TODO Auto-generated method stub
				
			}
		}, 0);
	}

	public interface OnUploadImageListener {
		public void onUplaodStart();
		public void onUploadSuccess(String resopnseString);
		public void onUploadFailure();
	}

	public interface OnRemoveListener {
		public void onRemove(ImageUploadView view);
	}

	public String getImageServerUrl() {
		return imageServerUrl;
	}

	public void setImageServerUrl(String imageServerUrl) {
		this.imageServerUrl = imageServerUrl;
	}

	public String getImageUploadKey() {
		return imageUploadKey;
	}

	public void setImageUploadKey(String imageUploadKey) {
		this.imageUploadKey = imageUploadKey;
	}

	public OnUploadImageListener getUploadImageListener() {
		return uploadImageListener;
	}

	public void setOnUploadImageListener(OnUploadImageListener uploadImageListener) {
		this.uploadImageListener = uploadImageListener;
	}

	public OnRemoveListener getRemoveListener() {
		return removeListener;
	}

	public void setOnRemoveListener(OnRemoveListener removeListener) {
		this.removeListener = removeListener;
	}

	public FilePath getFilePath() {
		return filePath;
	}

}
