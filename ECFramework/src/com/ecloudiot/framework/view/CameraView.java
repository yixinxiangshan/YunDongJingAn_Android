package com.ecloudiot.framework.view;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.activity.BaseActivity;
import com.ecloudiot.framework.fragment.ECCameraFragment;
import com.ecloudiot.framework.fragment.ECCameraFragment.CameraCatchListener;
import com.ecloudiot.framework.utility.Constants;
import com.ecloudiot.framework.utility.DensityUtil;
import com.ecloudiot.framework.utility.FilePath;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.MessageUtil;
import com.ecloudiot.framework.utility.MessageUtil.MessageData;
import com.ecloudiot.framework.utility.StringUtil;
import com.ecloudiot.framework.view.ImageUploadView.OnRemoveListener;
import com.ecloudiot.framework.view.ImageUploadView.OnUploadImageListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

@SuppressLint("NewApi")
public class CameraView extends FrameLayout implements Observer {
	public static final String TAG = "CameraView";
	private FrameLayout cameraContainer;
	private LinearLayout imagesLayout;
	private ImageButton albumBt;
	private ImageButton photoBt;
	private ImageButton doneBt;
	private ImageButton cancelBt;
	private ImageButton switchBt;
	private ImageButton autoFocusBt;
	private ECCameraFragment camera = null;
	private ECCameraFragment front = null;
	private ECCameraFragment back = null;
	private boolean backCamera = true;
	private boolean hasTwoCameras = (Camera.getNumberOfCameras() > 1);
	private ArrayList<FilePath> filePaths;
	private OnCameraDoneListener cameraDoneListener;

	public CameraView(Context context) {
		super(context);
		setContent();
	}

	public CameraView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setContent();
	}

	public CameraView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setContent();
	}

	private void setContent() {
		LayoutInflater.from(getContext()).inflate(R.layout.view_camera, this);
		cameraContainer = (FrameLayout) findViewById(R.id.view_camera_container);
		imagesLayout = (LinearLayout) findViewById(R.id.view_camera_images_container_ll);
		albumBt = (ImageButton) findViewById(R.id.view_camera_images_bt);
		photoBt = (ImageButton) findViewById(R.id.view_camera_photo_bt);
		doneBt = (ImageButton) findViewById(R.id.view_camera_done_bt);
		cancelBt = (ImageButton) findViewById(R.id.view_camera_cancel_bt);
		switchBt = (ImageButton) findViewById(R.id.view_camera_switch_bt);
		autoFocusBt = (ImageButton) findViewById(R.id.view_camera_autofocus_bt);
		if (filePaths == null) {
			filePaths = new ArrayList<FilePath>();
		}
		// camera
		camera = ECCameraFragment.newInstance(true);
		replaceCameraFragment();

		cameraContainer.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				camera.autoFocus();
				return false;
			}
		});

		photoBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				camera.takePicture();
			}
		});
		albumBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
				intent.setType("image/*");
				((Activity) getContext()).startActivityForResult(intent,
						Constants.PHOTO);
				MessageUtil.instance().addObserver(Constants.MESSAGE_TAG_ALBUM,
						CameraView.this);
			}
		});
		doneBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cameraDoneListener != null) {
					cameraDoneListener.onCameraDone(filePaths);
				}
			}
		});
		cancelBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cameraDoneListener != null) {
					cameraDoneListener.onCameraCancel();
				}
			}
		});
		autoFocusBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				camera.autoFocus();
			}
		});

		if (hasTwoCameras) {
			switchBt.setVisibility(VISIBLE);
			switchBt.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					exchangeCameraFragment();
				}
			});
		}
	}

	private void exchangeCameraFragment() {
		if (backCamera) {
			if (front == null) {
				front = ECCameraFragment.newInstance(false);
			}
			camera = front;
		} else {
			if (back == null) {
				back = ECCameraFragment.newInstance(true);
			}
			camera = back;
		}
		replaceCameraFragment();
		backCamera = !backCamera;
	}

	private void replaceCameraFragment() {
		((BaseActivity) getContext()).getSupportFragmentManager()
				.beginTransaction().replace(R.id.view_camera_container, camera)
				.commit();
		camera.setCameraCatchListener(new CameraCatchListener() {

			@Override
			public void onCatchImage(File imageFile) {
				addImage(imageFile);
			}
		});
	}

	private void addImage(String imageFilePath) {
		File file = new File(imageFilePath);
		addImage(file);
	}

	private void addImage(FilePath filePath) {
		ImageUploadView imageUploadView = new ImageUploadView(getContext());
		imageUploadView.setImageFile(filePath);
		addImage(imageUploadView);
	}

	private void addImage(File imageFile) {
		final ImageUploadView imageUploadView = new ImageUploadView(getContext());
		imageUploadView.setOnUploadImageListener(new OnUploadImageListener() {

			@Override
			public void onUploadSuccess(String resopnseString) {
				enableDone(true);
			}

			@Override
			public void onUploadFailure() {
				enableDone(true);
				filePaths.remove(imageUploadView.getFilePath());
			}

			@Override
			public void onUplaodStart() {
				enableDone(false);
			}
		});
		imageUploadView.setImageFile(imageFile, true);
		addImage(imageUploadView);

	}

	private void addImage(ImageUploadView imageUploadView) {
		int margin = DensityUtil.dipTopx(getContext(), 5);
		LayoutParams layoutParams = new LayoutParams(DensityUtil.dipTopx(
				getContext(), 90), DensityUtil.dipTopx(getContext(), 90));
		layoutParams.setMargins(margin, margin, 0, margin);
		imagesLayout.addView(imageUploadView, layoutParams);
		filePaths.add(imageUploadView.getFilePath());
		imageUploadView.setOnRemoveListener(new OnRemoveListener() {

			@Override
			public void onRemove(ImageUploadView view) {
				deleteImage(view);
			}
		});

	}

	private void deleteImage(ImageUploadView imageUploadView) {
		filePaths.remove(imageUploadView.getFilePath());
	}

	private void enableDone(boolean enabled) {
		LogUtil.d(TAG, "enableDone : enabled = " + enabled);
		doneBt.setEnabled(enabled);
		doneBt.setClickable(enabled);
	};

	@Override
	public void update(Observable observable, Object data) {
		MessageData msgData = (MessageData) data;
		String imageName = "";
		if (msgData.getName().equalsIgnoreCase(Constants.MESSAGE_TAG_ALBUM)) {
			imageName = (String) msgData.getData();
			MessageUtil.instance().deleteObserver(Constants.MESSAGE_TAG_ALBUM);
		}
		if (StringUtil.isNotEmpty(imageName)) {
			addImage(imageName);
		}
	}

	public OnCameraDoneListener getCameraDoneListener() {
		return cameraDoneListener;
	}

	public void setOnCameraDoneListener(OnCameraDoneListener cameraDoneListener) {
		this.cameraDoneListener = cameraDoneListener;
	}

	public void setFilePaths(ArrayList<FilePath> filePaths) {
		if (this.filePaths != null) {
			this.filePaths = new ArrayList<FilePath>();
		}
		imagesLayout.removeAllViews();
		for (FilePath filePath : filePaths) {
			addImage(filePath);
		}
	}

	public interface OnCameraDoneListener {
		public void onCameraDone(ArrayList<FilePath> filePaths);

		public void onCameraCancel();
	}

}
