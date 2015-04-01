package com.ecloudiot.framework.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.utility.FilePath;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.view.CameraView;
import com.ecloudiot.framework.view.CameraView.OnCameraDoneListener;

public class CameraActivity extends BaseActivity {
	private final static String TAG = "CameraActivity";
	private CameraView cameraView;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		setActionBarVisible(false);

		ArrayList<FilePath> filePaths = getIntent().getParcelableArrayListExtra("filePaths");
		cameraView = (CameraView) findViewById(R.id.activity_camera);
		if (filePaths != null) {
			cameraView.setFilePaths(filePaths);
		}
		cameraView.setOnCameraDoneListener(new OnCameraDoneListener() {

			@Override
			public void onCameraDone(ArrayList<FilePath> filePaths) {
				// LogUtil.d(TAG,
				// "onCameraDone : filePaths = "+filePaths+", netFilePaths = "+netFilePaths);

				Intent intent = new Intent();
				intent.putParcelableArrayListExtra("filePaths", filePaths);
				setResult(Activity.RESULT_OK, intent);
				finish();
				// overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
			}

			@Override
			public void onCameraCancel() {
				LogUtil.w(TAG, "onCameraCancel ...");
				finish();
			}
		});
	}
}
