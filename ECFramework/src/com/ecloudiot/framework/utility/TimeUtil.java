package com.ecloudiot.framework.utility;

import android.annotation.SuppressLint;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint("SimpleDateFormat")
public class TimeUtil {

	public static String sectionTime(int duration) {
		Date date = new Date();
		int hour = date.getHours();
		int minute = date.getMinutes();
		if (duration < 60) {
			int minuteV = Math.min(60, (int) (Math.ceil((float) minute / duration) * duration));
			date.setMinutes(minuteV);
		} else if (duration >= 60 && duration <= 60 * 24) {
			int dayMinutes = Math.min(60 * 24, (int) (Math.ceil((float) (hour * 60 + minute) / duration)) * duration);
			int hourV = dayMinutes / 60;
			int minuteV = dayMinutes % 60;
			date.setHours(hourV);
			date.setMinutes(minuteV);
		}
		return toString("", date);
	}

	public static String append(int duration) {
		Date date = new Date();
		int minute = date.getMinutes();
		minute = minute + duration;
		date.setMinutes(minute);
		String formatString = "yyyy-MM-ddHH:mm";
		return toString(formatString, date);
	}

	public static String toString(String format, Date date) {
		if (StringUtil.isEmpty(format)) {
			format = "yyyy-MM-ddHH:mm";
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		String timeString = dateFormat.format(date.getTime());
		return timeString;
	}

}
