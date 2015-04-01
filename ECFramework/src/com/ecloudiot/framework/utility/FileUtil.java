package com.ecloudiot.framework.utility;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.util.EncodingUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.ec.crypt.aes.android.AESToolsAndroid;
import com.ecloudiot.framework.R;
import com.ecloudiot.framework.appliction.BaseApplication;
import com.ecloudiot.framework.appliction.ECApplication;
import com.ecloudiot.framework.javascript.JsUtility;
import com.nostra13.universalimageloader.core.ImageLoader;

@SuppressLint("SimpleDateFormat")
public class FileUtil {
	private static String TAG = "FileUtil";
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	/**
	 * 判断 文件 是否存在
	 * 
	 * @param dir
	 * @param fileName
	 * @return
	 */
	public static boolean isFileExist(String dir, String fileName) {
		File file = new File(dir + fileName);
		if (file.exists()) {
			return true;
		}
		return false;
	}
	public static String getFileFromConfig(String file_path) {
		if (Constants.DEBUG) {
			return JsUtility.getSysFileString(IntentUtil.getActivity().getFilesDir() + "/" + file_path);
		} else {
			return JsUtility.getSysFileString("assets/" + file_path);
		}
	}
	/**
	 * 判断 缓存中文件是否存在
	 * 
	 * @param fileName
	 * @return
	 */
	public static boolean isCacheFileExist(String fileName) {
		return isFileExist(Constants.CACHEFILE_DIRPATH, fileName);
	}

	/**
	 * 从缓存中读取
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readFileFromCache(String fileName) {
		return readFileString(Constants.CACHEFILE_DIRPATH, fileName);
	}

	public static InputStream readFileFromUri(String fileUri) {
		if (StringUtil.isEmpty(fileUri)) {
			return null;
		}
		String filePath = fileUri.substring(6, fileUri.length());
		return readFile(filePath);
	}

	public static InputStream readFile(String filePath) {
		if (StringUtil.isEmpty(filePath)) {
			return null;
		}
		File file = new File(filePath);
		if (!file.exists()) {
			LogUtil.e(TAG, "readFile error: file is not exits . filePath = " + filePath);
			return null;
		}
		if (StringUtil.isImageName(filePath)) {
			Bitmap bitmap = ImageUtil.screenBitmap(filePath);
			bitmap = ImageUtil.getConstantBitmap(bitmap);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.PNG, 100, bos);
			byte[] bitmapdata = bos.toByteArray();
			ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
			return bs;
		}
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
		} catch (Exception e) {
			LogUtil.e(TAG, "readFile error: " + e.toString());
			e.printStackTrace();
		}
		return fileInputStream;
	}

	/**
	 * 读取本地文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static String readFileString(String dir, String fileName) {
		String res = "";
		try {
			File file = new File(dir + fileName);
			if (!file.exists()) {
				LogUtil.w(TAG, "readFileString : file is not exixt.");
				return "";
			}
			FileInputStream fin = new FileInputStream(file);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		} catch (Exception e) {
			LogUtil.e(TAG, "readFileString :" + e.toString());
		}
		// LogUtil.d(TAG, "获取本地数据 ,长度为 ： " + res.length());
		return res;
	}

	/**
	 * 描述：SD卡是否能用.
	 * 
	 * @return true 可用,false不可用
	 */
	public static boolean isCanUseSD() {
		try {
			return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		} catch (Exception e) {
			LogUtil.e(TAG, "isCanUseSD error: " + e.toString());
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 描述：通过文件的本地地址从SD卡读取图片.
	 * 
	 * @param file
	 *            the file
	 * @return Bitmap 图片
	 */
	public static Bitmap getBitmapFromSD(File file) {
		Bitmap bitmap = null;
		try {
			// SD卡是否存在
			if (!isCanUseSD()) {
				return null;
			}
			// 文件是否存在
			if (!file.exists()) {
				return null;
			}
			// 文件存在
			bitmap = ImageUtil.originalImg(file);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 存入到 缓存文件
	 * 
	 * @param data
	 * @param filename
	 * @return
	 */
	public static boolean putStringToCacheFile(String data, String filename) {
		return putStringToFile(data, Constants.CACHEFILE_DIRPATH, filename);
	}

	/**
	 * 将String数据存到文件
	 * 
	 * @param data
	 *            数据
	 * @param dirpath
	 *            路径
	 * @param filename
	 *            文件名
	 * @return
	 */
	public static boolean putStringToFile(String data, String dirpath, String filename) {
		byte[] b = data.getBytes();
		return putByteToFile(b, dirpath, filename);
	}

	public static boolean putByteToFile(byte[] data, File file) {
		BufferedOutputStream stream = null;
		try {
			FileOutputStream fstream = new FileOutputStream(file);
			stream = new BufferedOutputStream(fstream);
			stream.write(data);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e1) {
					LogUtil.e(TAG, "IOException:" + e1.toString());
					e1.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}

	public static boolean putByteToFile(byte[] data, String dirpath, String filename) {
		File destDir = new File(dirpath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		// LogUtil.d(TAG, "dirpath :" + dirpath + filename);
		File file = null;
		file = new File(dirpath, filename);
		return putByteToFile(data, file);
	}
	/**
	 * 返回文件夹内所有文件的大小以 "M"为单位
	 * 
	 * @param file
	 * @return
	 */
	public static double getDirSize(File file) {
		// 判断文件是否存在
		if (file.exists()) {
			// 如果是目录则递归计算其内容的总大小
			if (file.isDirectory()) {
				File[] children = file.listFiles();
				double size = 0;
				for (File f : children)
					size += getDirSize(f);
				return size;
			} else {// 如果是文件则直接返回其大小,以“兆”为单位
				double size = (double) file.length() / 1024 / 1024;
				return size;
			}
		} else {
			LogUtil.e(TAG, "文件或者文件夹不存在，请检查路径是否正确！" + file.toString());
			return 0.0;
		}
	}
	/**
	 * 清除缓存
	 */
	public static void clearCache() {
		// 清除图片
		ImageLoader.getInstance().clearMemoryCache();
		ImageLoader.getInstance().clearDiscCache();
		// 清除api 缓存
		deleteCacheFile();
		Toast.makeText(IntentUtil.getActivity(), "缓存清理成功...", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 删除 缓存中的文件
	 */
	public static void deleteCacheFile() {
		delAllFile(Constants.CACHEFILE_DIRPATH);
	}

	/**
	 * 删除文件夹里面的所有文件
	 * 
	 * @param path
	 *            String 文件夹路径 如 c:/fqf
	 */
	public static boolean delAllFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return false;
		}
		if (!file.isDirectory()) {
			return false;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
			}
		}
		return true;
	}

	/**
	 * 删除文件夹
	 * 
	 * @param filePathAndName
	 *            String 文件夹路径及名称 如c:/fqf
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 删除空文件夹

		} catch (Exception e) {
			System.out.println("删除文件夹操作出错");
			e.printStackTrace();

		}
	}

	/**
	 * 从文件里获得字符串,assets/raw 或 其他目录下
	 * 
	 * @param filename
	 * @return
	 */
	public static String getSysFileString(String filename) {
		String jsStr = null;
		InputStream inStream = null;
		InputStreamReader inputReader = null;
		android.content.Context context = ECApplication.getInstance().getApplicationContext();
		if (filename == null) {
			return null;
		} else if (filename.startsWith("assets/")) {
			filename = filename.substring("assets/".length());
			try {
				//decrypt start
				if(Constants.ENCRYPT&&filename.indexOf("config/")!=-1&&filename.indexOf(".json")==-1){
					AESToolsAndroid aesTools = new AESToolsAndroid();
					File deFile = aesTools.decryptFile(ECApplication.getInstance().getApplicationContext().getResources().getAssets().open(filename), "temp", null, ECApplication.getInstance().getApplicationContext().getResources().getString(R.string.ori_key));
					try {
						inStream = new FileInputStream(deFile);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}else{
					inStream = ECApplication.getInstance().getApplicationContext().getResources().getAssets().open(filename);
				}
				//decrypt end
				inputReader = new InputStreamReader(inStream);
			} catch (IOException e) {
				LogUtil.e(TAG, "IOException: " + e.toString());
				e.printStackTrace();
			}
		} else if (filename.startsWith("raw/")) {
			int endIndex = 0;
			if (filename.contains(".")) {
				endIndex = filename.lastIndexOf(".");
				filename = filename.substring("raw/".length(), endIndex);
			} else {
				filename = filename.substring("raw/".length());
			}
			//decrypt start
			if(Constants.ENCRYPT&&filename.indexOf("config/")!=-1&&filename.indexOf(".json")==-1){
				AESToolsAndroid aesTools = new AESToolsAndroid();
				File deFile = aesTools.decryptFile(context.getResources().openRawResource(ResourceUtil.getRawFromContext(context, filename)), "temp", null, ECApplication.getInstance().getApplicationContext().getResources().getString(R.string.ori_key));
				try {
					inStream = new FileInputStream(deFile);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}else{
				inStream = context.getResources().openRawResource(ResourceUtil.getRawFromContext(context, filename));
			}
			//decrypt end
			inputReader = new InputStreamReader(inStream);
		} else {
			try {
				//decrypt start
				if(Constants.ENCRYPT&&filename.indexOf("/config/")!=-1&&filename.indexOf(".json")==-1){
					AESToolsAndroid aesTools = new AESToolsAndroid();
					File nowfile = new File(filename);
					File defile = aesTools.decryptFile(nowfile, null, ECApplication.getInstance().getApplicationContext().getResources().getString(R.string.ori_key));
					inStream = new FileInputStream(defile);
				}else{
					inStream = new FileInputStream(filename);
				}
				//decrypt end
				inputReader = new InputStreamReader(inStream);
			} catch (Exception e) {
				LogUtil.e(TAG, "Exception:" + e.toString());
				e.printStackTrace();
			}
		}
		if (inputReader != null) {
			LineNumberReader reader = null;
			try {
				reader = new LineNumberReader(inputReader);
				String s = null;
				StringBuffer sb = new StringBuffer();
				while ((s = reader.readLine()) != null) {
					sb.append(s).append("\n");
				}
				jsStr = sb.toString();
			} catch (IOException e) {
				LogUtil.e(TAG, "IOException:" + e.toString());
				e.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					LogUtil.e(TAG, "IOException:" + e.toString());
					e.printStackTrace();
				}
			}
		}
		return jsStr;
	}

	/**
	 * 获取资源中文件
	 * 
	 * @param filePath
	 *            (含文件名)
	 * @return
	 */
	public static String getResourceFileString(String filePath) {
		InputStream inputStream = ECApplication.getInstance().getApplicationContext().getClass().getResourceAsStream(filePath);
		String readString = null;
		try {
			readString = convertStreamToString(inputStream);
		} catch (Exception e) {
			LogUtil.e(TAG, "Exception:" + e.toString());
			e.printStackTrace();
		}
		return readString;
	}

	/**
	 * convert inputStream to string
	 * 
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public static String convertStreamToString(InputStream is) throws Exception {
		String jsStr = null;
		InputStreamReader inputReader = new InputStreamReader(is);
		if (inputReader != null) {
			LineNumberReader reader = null;
			try {
				reader = new LineNumberReader(inputReader);
				String s = null;
				StringBuffer sb = new StringBuffer();
				while ((s = reader.readLine()) != null) {
					sb.append(s).append("\n");
				}
				jsStr = sb.toString();
			} catch (IOException e) {
				LogUtil.e(TAG, "IOException:" + e.toString());
				e.printStackTrace();
			} finally {
				try {
					reader.close();
				} catch (IOException e) {
					LogUtil.e(TAG, "IOException:" + e.toString());
					e.printStackTrace();
				}
			}
		}

		is.close();
		return jsStr;
	}

	/**
	 * 读取输入流
	 * 
	 * @param is
	 * @return
	 */
	public static String readStream(InputStream is) {
		try {
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			int i = is.read();
			while (i != -1) {
				bo.write(i);
				i = is.read();
			}
			return bo.toString("UTF-8");
		} catch (Exception e) {
			LogUtil.e(TAG, "Exception:" + e.toString());
			e.printStackTrace();
			return "";
		}
	}

	/** Create a file Uri for saving an image or video */
	public static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	public static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				LogUtil.e("MyCameraApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}

	@SuppressLint("NewApi")
	public static File getDiskCacheDir(String uniqueName) {
		String cachePath;
		Context context = BaseApplication.getInstance();
		// LogUtil.d(TAG, "diskCacheDir:"+Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()));
		// LogUtil.d(TAG, "diskCacheDir:" + !Environment.isExternalStorageRemovable());
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			cachePath = context.getExternalCacheDir().getPath();
		} else {
			cachePath = context.getCacheDir().getPath();
		}
		File resultFile = new File(cachePath + File.separator + uniqueName);
		if (!resultFile.exists()) {
			resultFile.mkdirs();
		}
		return resultFile;
	}

}
