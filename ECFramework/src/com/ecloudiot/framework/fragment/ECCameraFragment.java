/***
  Copyright (c) 2013 CommonsWare, LLC
  
  Licensed under the Apache License, Version 2.0 (the "License"); you may
  not use this file except in compliance with the License. You may obtain
  a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package com.ecloudiot.framework.fragment;

import java.io.File;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.commonsware.cwac.camera.CameraHost;
import com.commonsware.cwac.camera.SimpleCameraHost;
import com.commonsware.cwac.camera.acl.CameraFragment;
import com.ecloudiot.framework.utility.FileUtil;
import com.ecloudiot.framework.utility.LogUtil;

public class ECCameraFragment extends CameraFragment {
	private final static String TAG = "ECCameraFragment";
	private static final String KEY_USE_FFC = "com.commonsware.cwac.camera.demo.USE_FFC";
	private CameraCatchListener cameraCatchListener;
	private boolean singleShotProcessing = false;
	private static final int TAKEAPHOTO = 1;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case TAKEAPHOTO:
				File imageFile = (File) msg.obj;
				if (getCameraCatchListener() != null) {
					getCameraCatchListener().onCatchImage(imageFile);
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	public static ECCameraFragment newInstance(boolean useFFC) {
		ECCameraFragment f = new ECCameraFragment();
		Bundle args = new Bundle();

		args.putBoolean(KEY_USE_FFC, useFFC);
		f.setArguments(args);

		return (f);
	}

	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);

		// setHasOptionsMenu(true);
		setHost(new DemoCameraHost(getActivity()));
	}

	boolean isSingleShotProcessing() {
		return (singleShotProcessing);
	}

	Contract getContract() {
		return ((Contract) getActivity());
	}

	public CameraCatchListener getCameraCatchListener() {
		return cameraCatchListener;
	}

	public void setCameraCatchListener(CameraCatchListener cameraCatchListener) {
		this.cameraCatchListener = cameraCatchListener;
	}

	interface Contract {
		boolean isSingleShotMode();

		void setSingleShotMode(boolean mode);
	}

	class DemoCameraHost extends SimpleCameraHost {
		public DemoCameraHost(Context _ctxt) {
			super(_ctxt);
		}

		@Override
		public boolean useFrontFacingCamera() {
			return (getArguments().getBoolean(KEY_USE_FFC));
		}

		@Override
		public boolean useSingleShotMode() {
			return false;
		}

		@Override
		public void saveImage(byte[] image) {
			File imageFile = FileUtil.getOutputMediaFile(FileUtil.MEDIA_TYPE_IMAGE);
			if (FileUtil.putByteToFile(image, imageFile)) {
				Message msg = new Message();
				msg.what = TAKEAPHOTO;
				msg.obj = imageFile;
				mHandler.sendMessage(msg);
			}
		}

		@Override
		public RecordingHint getRecordingHint() {
			return RecordingHint.STILL_ONLY;
		}

		@Override
		public Size getPreviewSize(int displayOrientation, int width, int height, Parameters parameters) {
//			Size returnSize = null;
//			for (Size size : parameters.getSupportedPreviewSizes()) {
//				LogUtil.d(TAG, "getPreviewSize : parameters getSupportedPreviewSize = (" + size.width + " , "
//						+ size.height + ")");
//				int screenWidth = DensityUtil.screenWidth();
//				if (returnSize==null || (size.width <= screenWidth && returnSize.width<size.width)) {
//					returnSize = size;
//				}
//			}
//			
//			LogUtil.d(TAG, "getPreviewSize  = (" + returnSize.width + " , " + returnSize.height + ")");
//			return returnSize;
			Size size = super.getPreviewSize(displayOrientation, width, height, parameters);
			return size;
		}

		@Override
		public void onCameraFail(CameraHost.FailureReason reason) {
			super.onCameraFail(reason);
			LogUtil.e(TAG, "onCameraFail : " + reason);
			Toast.makeText(getActivity(), "Sorry, but you cannot use the camera now!", Toast.LENGTH_LONG).show();
		}
	}

	public interface CameraCatchListener {
		public void onCatchImage(File imageFile);
	}
}