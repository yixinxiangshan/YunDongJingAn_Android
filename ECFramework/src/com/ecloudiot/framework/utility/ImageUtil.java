package com.ecloudiot.framework.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.ecloudiot.framework.appliction.ECApplication;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageUtil {
	private final static String TAG = "ImageUtil";

	/**
	 * 描述：通过文件的本地地址读取图片.
	 * 
	 * @param file
	 *            the file
	 * @return Bitmap 图片
	 */
	public static Bitmap getBitmapFromConfig(String file_path) {
		// LogUtil.d(TAG, "getBitmapFromConfig:" + file_path);
		file_path = file_path.substring(0, file_path.length() - 4) + "@2x" + file_path.substring(file_path.length() - 4);
		Bitmap bitmap = null;
		// 根据密度计算当前的图片如何缩放
		Float scaleFloat = (float) (0.76 * ECApplication.getInstance().getNowActivity().getResources().getDisplayMetrics().density / (1.5));
		try {
			String baseUrlString = "";
			if (Constants.DEBUG) {
				baseUrlString = IntentUtil.getActivity().getFilesDir() + "/";
				File file = new File(baseUrlString + file_path);
				if (file.exists())
					bitmap = getScaledBitmap(ImageUtil.originalImg(file), (float) scaleFloat);
			} else {
				AssetManager am = IntentUtil.getActivity().getResources().getAssets();
				bitmap = getScaledBitmap(BitmapFactory.decodeStream(am.open(file_path)), (float) scaleFloat);
			}
		} catch (Exception e) {
			LogUtil.e(TAG, "getBitmapFromConfig error ,path:" + file_path);
			// e.printStackTrace();
			return bitmap;
		}
		return bitmap;
		// return bitmap;
	}
	public static Drawable getDrawableFromFile(String file_path) {
		// LogUtil.d(TAG, "getBitmapFromConfig:" + file_path);
		Bitmap bitmap = null;
		// 根据密度计算当前的图片如何缩放
		Float scaleFloat = (float) (0.76 * ECApplication.getInstance().getNowActivity().getResources().getDisplayMetrics().density / (1.5));
		try {
			String baseUrlString = "";
			if (Constants.DEBUG) {
				baseUrlString = IntentUtil.getActivity().getFilesDir() + "/";
				File file = new File(baseUrlString + file_path);
				if (file.exists())
					bitmap = getScaledBitmap(ImageUtil.originalImg(file), (float) scaleFloat);
			} else {
				AssetManager am = IntentUtil.getActivity().getResources().getAssets();
				bitmap = getScaledBitmap(BitmapFactory.decodeStream(am.open(file_path)), (float) scaleFloat);
			}
		} catch (Exception e) {
			// LogUtil.e(TAG, "getDrawableFromFile error ,path:" + file_path);
			// e.printStackTrace();
			return null;
		}
		return new BitmapDrawable(IntentUtil.getActivity().getResources(), bitmap);
	}
	public static Drawable getDrawableFromConfig(String file_path) {
		// LogUtil.i(TAG, "getDrawableFromConfig - " + file_path);
		StateListDrawable bgzoomin = new StateListDrawable();
		try {
			Bitmap bitNormal = getBitmapFromConfig(file_path);
			if (bitNormal == null) {
				return getDrawableFromFile(file_path);
			}
			Drawable normal_icon = new BitmapDrawable(IntentUtil.getActivity().getResources(), bitNormal);

			Drawable pressed_icon = new BitmapDrawable(IntentUtil.getActivity().getResources(), getBitmapFromConfig(file_path.substring(0,
			        file_path.length() - 4) + "_pressed" + file_path.substring(file_path.length() - 4)));
			Drawable selected_icon = new BitmapDrawable(IntentUtil.getActivity().getResources(), getBitmapFromConfig(file_path.substring(0,
			        file_path.length() - 4) + "_selected" + file_path.substring(file_path.length() - 4)));
			Drawable disabled_icon = new BitmapDrawable(IntentUtil.getActivity().getResources(), getBitmapFromConfig(file_path.substring(0,
			        file_path.length() - 4) + "_disabled" + file_path.substring(file_path.length() - 4)));
			// View.PRESSED_ENABLED_STATE_SET
			bgzoomin.addState(new int[]{android.R.attr.state_pressed}, pressed_icon);
			// View.FOCUSED_STATE_SET
			bgzoomin.addState(new int[]{android.R.attr.state_focused, android.R.attr.state_selected}, selected_icon);
			// View.FOCUSED_STATE_SET
			bgzoomin.addState(new int[]{android.R.attr.state_selected}, selected_icon);
			// View.EMPTY_STATE_SET
			bgzoomin.addState(new int[]{android.R.attr.state_enabled}, normal_icon);
			bgzoomin.addState(new int[]{android.R.attr.state_focused}, selected_icon);
			// View.WINDOW_FOCUSED_STATE_SET
			bgzoomin.addState(new int[]{android.R.attr.state_window_focused}, disabled_icon);
			bgzoomin.addState(new int[]{}, normal_icon);
		} catch (Exception e) {
			// LogUtil.e(TAG, "getDrawableFromConfig error ,path:" + file_path);
			// LogUtil.e(TAG, e.toString());
			// e.printStackTrace();
		}
		return bgzoomin;
	}

	/**
	 * 将视图转换成Bitmap
	 * 
	 * @param view
	 * @return
	 */
	public static Bitmap convertViewToBitmap(View view) {
		view.setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
		try {
			view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		view.setDrawingCacheEnabled(true);
		view.buildDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
		view.setDrawingCacheEnabled(false);
		return bitmap;
	}

	/**
	 * 将视图转换成 Drawable
	 * 
	 * @param view
	 * @return
	 */
	public static Drawable convertViewToDrawable(View view) {
		Drawable drawable = new BitmapDrawable(IntentUtil.getActivity().getResources(), convertViewToBitmap(view));
		return drawable;
	}

	/**
	 * 根据图片自身大小缩放
	 * 
	 * @param activity
	 * @param imageView
	 * @param rateToScreen
	 * @param drawable
	 * @return
	 */
	public static ImageView setSize(Activity activity, ImageView imageView, Double rateToScreen, Drawable drawable) {
		Display display = activity.getWindowManager().getDefaultDisplay();

		int imageWidth = (int) (display.getWidth() * rateToScreen);
		int drawableWidth = drawable.getIntrinsicWidth();
		int drawableHeight = drawable.getIntrinsicHeight();
		double heightToWidth = (float) drawableHeight / (float) drawableWidth;
		if (imageView != null && drawable != null) {
			imageView = setSize(activity, imageView, DensityUtil.pxTodip(activity, imageWidth), heightToWidth);
			imageView.setImageDrawable(drawable);
			return imageView;
		} else {
			return null;
		}
	}

	/**
	 * @param activity
	 * @param imageView
	 * @param imageWidth
	 *            为dp值
	 * @param heightToWidth
	 * @return
	 */
	public static ImageView setSize(Activity activity, ImageView imageView, int imageWidth, Double heightToWidth) {
		if (imageView != null && heightToWidth != 0) {
			android.view.ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
			layoutParams.width = DensityUtil.dipTopx(activity, imageWidth);
			LogUtil.i(TAG, "ImageView setSize : layoutParams width = " + layoutParams.width);
			layoutParams.height = (int) (DensityUtil.dipTopx(activity, imageWidth) * heightToWidth);
			imageView.setLayoutParams(layoutParams);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			return imageView;
		} else {
			return null;
		}
	}

	/**
	 * 设置图片显示大小
	 * 
	 * @param activity
	 * @param imageView
	 * @param rateToScreen
	 * @param heightToWidth
	 * @return
	 */
	public static ImageView setSize(Activity activity, ImageView imageView, Double rateToScreen, Double heightToWidth) {
		Display display = activity.getWindowManager().getDefaultDisplay();

		int imageWidth = (int) (display.getWidth() * rateToScreen);
		if (imageView != null && heightToWidth != 0) {
			return setSize(activity, imageView, DensityUtil.pxTodip(activity, imageWidth), heightToWidth);
		}
		return null;
	}

	/**
	 * 获取圆形图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getCircleBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		int ovalLen = Math.min(width, height);
		Rect src = new Rect((width - ovalLen) / 2, (height - ovalLen) / 2, (width - ovalLen) / 2 + ovalLen, (height - ovalLen) / 2 + ovalLen);
		Rect dst = new Rect(0, 0, ovalLen, ovalLen);
		Bitmap output = Bitmap.createBitmap(ovalLen, ovalLen, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(Color.BLUE);
		canvas.drawOval(new RectF(0, 0, ovalLen, ovalLen), paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		if (!output.equals(bitmap)) {
			bitmap.recycle();
		}
		return output;
	}

	/**
	 * 缩放图片
	 * 
	 * @param bitmap
	 * @param rate
	 * @return
	 */
	public static Bitmap getScaledBitmap(Bitmap bitmap, float rate) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		// LogUtil.d(TAG, "getScaledBitmap : width = " + (int) (width * rate) + " , height = " + (int) (height * rate));
		return Bitmap.createScaledBitmap(bitmap, (int) (width * rate), (int) (height * rate), true);
	}

	/**
	 * 获取固定大小图片，宽度为640px
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getConstantBitmap(Bitmap bitmap) {
		float rate = (640.0f / bitmap.getWidth() >= 1.0) ? 1.0f : 640.0f / bitmap.getWidth();
		return getScaledBitmap(bitmap, rate);
	}

	/**
	 * 描述：获取原图
	 * 
	 * @param file
	 *            File对象
	 * @return Bitmap 图片
	 */
	public static Bitmap originalImg(File file) {
		Bitmap resizeBmp = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			resizeBmp = BitmapFactory.decodeFile(file.getPath(), options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resizeBmp;
	}

	/**
	 * 从文件中读取成小图片
	 * 
	 * @param filePath
	 * @return Ohmer-Dec 9, 2013 1:00:11 PM
	 */
	public static Bitmap smallBitmap(String filePath) {
		Bitmap resizeBmp = null;
		try {
			// -------- handle OOM start -----
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, bitmapOptions);
			bitmapOptions.inSampleSize = Math.round((float) bitmapOptions.outWidth / 200.f);
			bitmapOptions.inJustDecodeBounds = false;
			// -------- handle OOM end -----
			resizeBmp = BitmapFactory.decodeFile(filePath, bitmapOptions);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resizeBmp;
	}

	/**
	 * 从文件中读取宽度为640的图片
	 * 
	 * @param filePath
	 * @return Ohmer-Dec 9, 2013 1:01:35 PM
	 */
	public static Bitmap screenBitmap(String filePath) {
		Bitmap resizeBmp = null;
		try {
			// -------- handle OOM start -----
			BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
			bitmapOptions.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, bitmapOptions);
			bitmapOptions.inSampleSize = Math.round((float) bitmapOptions.outWidth / 640.f);
			bitmapOptions.inJustDecodeBounds = false;
			// -------- handle OOM end -----
			resizeBmp = BitmapFactory.decodeFile(filePath, bitmapOptions);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resizeBmp;
	}

	public static Bitmap writeBitmapToFile(Bitmap bm, File fImage) {
		if (bm == null) {
			return null;
		}
		FileOutputStream iStream = null;
		try {
			iStream = new FileOutputStream(fImage);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		LogUtil.d(TAG, "writeBitmapToFile : width = " + bm.getWidth());
		bm.compress(Bitmap.CompressFormat.JPEG, 100, iStream);
		LogUtil.d(TAG, "writeBitmapToFile : width = " + bm.getWidth());
		return bm;

	}

	/**
	 * Bitmap转换成byte[]
	 * 
	 * @param bm
	 * @return
	 */
	public static byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 根据图片名设置图片
	 * 
	 * @param imageView
	 * @param imageName
	 *            Ohmer-Dec 26, 2013 3:22:38 PM
	 */
	public static void setImage(ImageView imageView, String imageName) {
		if (StringUtil.isEmpty(imageName)) {
			return;
		}
		if (StringUtil.isImageName(imageName)) {
			ImageLoader.getInstance().displayImage(URLUtil.getImageWholeUrl(imageName), imageView);
		} else {
			imageView.setImageResource(ResourceUtil.getDrawableIdFromContext(IntentUtil.getActivity(), imageName));
		}
	}

}
