package com.ecloudiot.framework.utility;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.ecloudiot.framework.activity.BaseActivity;
import com.ecloudiot.framework.activity.ItemActivity;
import com.ecloudiot.framework.javascript.JsAPI;

public class ActivityResultUtil {

	private final static String TAG = "ActivityResultUtil";

	public static String getRealPathFromURI(Uri contentUri) {
		Cursor cursor = null;
		try {
			String[] proj = {MediaStore.Images.Media.DATA};
			cursor = IntentUtil.getActivity().getContentResolver().query(contentUri, proj, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	public static void onActivityResult(int requestCode, int resultCode, Intent data, Activity activity) {
		// LogUtil.d(TAG, "start handle activity result ...");
		switch (requestCode) {
			case Constants.CAMERA :
				try {
					String filePath = AppUtil.getParam("cameraImageUriString");
					MessageUtil.instance().sendMessage(Constants.MESSAGE_TAG_CAMERA, filePath);
					// 执行js
					HashMap<String, String> eventParams = new HashMap<String, String>();
					eventParams.put("type", "camera");
					eventParams.put("filePath", filePath.toString());
					JsAPI.runEvent(((BaseActivity) activity).getJsEvents(), "onResult", new JSONObject(eventParams));
				} catch (Exception e) {
					e.printStackTrace();
					LogUtil.e(TAG, "onActivityResult : " + "no picture is selected");
				}
				break;
			case Constants.PHOTO :
				try {
					if (null != data) {
						Uri bitmapUri = data.getData();
						LogUtil.d(TAG, "bitmapUri:" + bitmapUri);
						String[] filePathColumn = {MediaStore.Images.Media.DATA};
						Cursor cursor = IntentUtil.getActivity().getContentResolver().query(bitmapUri, filePathColumn, null, null, null);
						cursor.moveToFirst();
						int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
						String filePath = cursor.getString(columnIndex);
						String bitmapDir = filePath;
						LogUtil.d(TAG, "bitmapDir:" + bitmapDir);
						cursor.close();
						MessageUtil.instance().sendMessage(Constants.MESSAGE_TAG_ALBUM, bitmapDir);
						// 执行js
						HashMap<String, String> eventParams = new HashMap<String, String>();
						eventParams.put("type", "photo");
						eventParams.put("bitmapdir", bitmapDir.toString());
						JsAPI.runEvent(((BaseActivity) activity).getJsEvents(), "onResult", new JSONObject(eventParams));
					}
				} catch (Exception e) {
					e.printStackTrace();
					LogUtil.d(TAG, "onActivityResult : " + "no picture is selected");
				}
				break;
			case Constants.QRCAPTURE :
				if (resultCode == Activity.RESULT_OK) {
					String codeString = data.getStringExtra("codeString");
					HashMap<String, String> eventParams = new HashMap<String, String>();
					// 执行js
					eventParams.put("type", "qrcaputre");
					eventParams.put("codeString", codeString);
					String result = JsAPI.runEvent(((BaseActivity) activity).getJsEvents(), "onResult", new JSONObject(eventParams));
					// LogUtil.d(TAG, "onresult:"+((BaseActivity) activity).getJsEvents().toString());
					if (result != null && result.equals("_false"))
						return;

					// Toast.makeText(IntentUtil.getActivity(), "codeString = " + codeString, Toast.LENGTH_SHORT).show();
					URI uri = null;
					try {
						uri = new URI(codeString);
					} catch (URISyntaxException e1) {
						LogUtil.e(TAG, "onActivityResult error: " + e1.toString());
						return;
					}
					// LogUtil.d(TAG, "onActivityResult : uri path " + uri.getPath());
					// NetUtil.getToken();
					// IntentUtil.openActivity("", "page_signin_demo", "");
					// LogUtil.d(TAG, "onActivityResult : uri = " + codeString);
					// HashMap<String, String> params = new HashMap<String, String>();
					// params.put("method", "QRCode/get");
					// params.put("code", uri.getPath().substring(1, uri.getPath().length()));
					// // params.put("code", "eE6IyD5ZKM");
					// // params.put("code", "5qE5pa95b");
					//
					// HttpAsyncClient.Instance().post("", params, new HttpAsyncHandler() {
					// @Override
					// public void onFailure(String failResopnse) {
					// LogUtil.e(TAG, "onFailure error: " + failResopnse);
					// ViewUtil.closeLoadingDianlog();
					// // Toast.makeText(IntentUtil.getActivity(), "网络出错...", Toast.LENGTH_SHORT).show();
					// }
					//
					// @Override
					// public void onSuccess(String response) {
					// LogUtil.d(TAG, "onSuccess : response = " + response);
					// JsonElement dataElement = (new JsonParser()).parse(response);
					// if (dataElement.isJsonObject()) {
					// JsonElement protocalElement = ((JsonObject) dataElement).get("data");
					// if (protocalElement.isJsonPrimitive()) {
					// String protocalString = protocalElement.getAsString();
					// EventBus.getDefault().post(new DoActionEvent(protocalString));
					// }
					// }
					// }
					//
					// @Override
					// public void onProgress(Float progress) {
					//
					// }
					//
					// @Override
					// public void onResponse(String resopnseString) {
					// // TODO Auto-generated method stub
					//
					// }
					// });
				}
				break;
			case Constants.NEWITEMACTIVITY :
				LogUtil.d(TAG, "onActivityResult : resultCode = " + resultCode);
				if (resultCode == Activity.RESULT_OK) {
					String uri = null;
					try {
						uri = data.getStringExtra("uriString");
					} catch (Exception e) {
						LogUtil.e(TAG, "uriString is null,you forget it?");
					}
					if (StringUtil.isNotEmpty(uri)) {
						((ItemActivity) activity).putParam("uriString", uri);
					}
				}
				break;
			case Constants.CAMERAACTIVITY :
				LogUtil.d(TAG, "onActivityResult : from CAMERAACTIVITY");
				if (resultCode == Activity.RESULT_OK && data != null) {
					ArrayList<FilePath> filePaths = data.getParcelableArrayListExtra("filePaths");
					MessageUtil.instance().sendMessage(Constants.MESSAGE_TAG_CAMERA_ACTIVITY, filePaths);

					// 执行js
					HashMap<String, String> eventParams = new HashMap<String, String>();
					eventParams.put("type", "cameraactivity");
					eventParams.put("filePaths", filePaths.toString());
					JsAPI.runEvent(((BaseActivity) activity).getJsEvents(), "onResult", new JSONObject(eventParams));
				}
				break;
		}
	}
}
