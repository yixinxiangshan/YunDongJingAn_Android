package com.ecloudiot.framework.activity;

import com.ecloudiot.framework.R;
import com.ecloudiot.framework.utility.LogUtil;
import com.ecloudiot.framework.utility.ViewUtil;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

@SuppressLint("SetJavaScriptEnabled")
@TargetApi(Build.VERSION_CODES.FROYO)
public class VideoActivity extends Activity {
	private final static String TAG = "VideoActivity";
	private Bundle mBundle = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		LinearLayout layout = (LinearLayout) findViewById(R.id.activity_item_container_llayout);
		layout.removeAllViews();
		mBundle = getIntent().getExtras();
		String uriString;
		try {
			uriString = mBundle.getString("uriString");
			if (!uriString.startsWith("http://")) {
				uriString = "http://qymhvideo.oss.aliyuncs.com/" + uriString;
			}
		} catch (Exception e) {
			LogUtil.d(TAG, "onCreate : "+e.toString());
			Toast.makeText(this, "视频地址错误", Toast.LENGTH_SHORT).show();
			return;
		}
		VideoView videoView = new VideoView(this);
		layout.addView(videoView);
//		String uriString = "http://qymhvideo.oss.aliyuncs.com/uploads/coupon_content/video_url/14619/01jingan-class-power.mp4";
		LogUtil.d(TAG, "onCreate : uri= "+uriString);
		Uri uri = Uri.parse(uriString);
		LogUtil.d(TAG, "onCreate : uri= "+uri.toString());
		videoView.setMediaController(new MediaController(this));    
		videoView.setVideoURI(uri);    
		videoView.start();    
		videoView.requestFocus();  
		ViewUtil.showLoadingDialog(this, "", "loading...", true);
		videoView.setOnPreparedListener(new OnPreparedListener() {
			
			@Override
			public void onPrepared(MediaPlayer mp) {
				LogUtil.d(TAG, "onPrepared : ok...........");
				ViewUtil.closeLoadingDianlog();
			}
		});

	}
}
