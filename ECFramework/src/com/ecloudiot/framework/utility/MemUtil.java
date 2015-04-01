package com.ecloudiot.framework.utility;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Debug;

public class MemUtil {
	private final static String TAG = "MemUtil";
	private final static double USEABLE_MEM_PROPORTION = 0.8;

	/**
	 * 获得系统剩余内存
	 */
	public static long getSystemMemleft(Context context) {
		long mem = -1;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		mem = mi.availMem;
		LogUtil.d(TAG, "SystemMemleft = " + String.valueOf(mem));
		return mem;
	}

	/**
	 * 获得单个应用的限制内存
	 * 
	 * @param context
	 * @return
	 */
	public static int getAppLimitMem(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		return activityManager.getMemoryClass();
	}

	public enum JvmInfoType {
		MAX_MEMORY, TOTAL_MEMORY, FREE_MEMORY
	}

	/**
	 * 获取虚拟机内存信息
	 * 
	 * @param jvmType
	 * @return
	 */
	public static long getJvmMemInfo(JvmInfoType jvmType) {
		Runtime run = Runtime.getRuntime();
		if (jvmType == JvmInfoType.MAX_MEMORY) {
			return run.maxMemory();
		} else if (jvmType == JvmInfoType.TOTAL_MEMORY) {
			return run.totalMemory();
		} else if (jvmType == JvmInfoType.FREE_MEMORY) {
			return run.freeMemory();
		} else {
			return 0;
		}
	}

	public static long getJvmMaxMem() {
		return getJvmMemInfo(JvmInfoType.MAX_MEMORY);
	}

	public static long getJvmUsedMem() {
		return getJvmMemInfo(JvmInfoType.TOTAL_MEMORY);
	}

	public static long getJvmFreeMem() {
		return getJvmMemInfo(JvmInfoType.FREE_MEMORY);
	}

	/**
	 * 可用内存设为剩余内存的USEABLE_MEM_PROPORTION
	 * 
	 * @return
	 */
	public static long getJvmUseableMem() {
		return (long) (getJvmMemInfo(JvmInfoType.FREE_MEMORY) * USEABLE_MEM_PROPORTION);
	}

	public static void getThreadMemoryInfo(Context context) {
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> procInfo = am.getRunningAppProcesses();
		Debug.MemoryInfo self_mi[] = null;
		for (RunningAppProcessInfo runningAppProcessInfo : procInfo) {
			if (runningAppProcessInfo.processName.indexOf(context.getPackageName()) != -1) {
				int pids[] = { runningAppProcessInfo.pid };
				self_mi = am.getProcessMemoryInfo(pids);
				self_mi[0].getTotalPrivateDirty();
				self_mi[0].getTotalPrivateDirty();
				self_mi[0].getTotalPrivateDirty();
			}
		}
	}
	
}
