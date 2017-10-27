package com.gvtv.android.cloud.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TimeUtils {
	public static String formatTimeToStr(long time_sec){
		SimpleDateFormat fmtDateAndTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		String str_time = fmtDateAndTime.format(time_sec * 1000);
		return str_time;
	}
}
