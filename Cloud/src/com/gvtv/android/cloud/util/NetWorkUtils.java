package com.gvtv.android.cloud.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetWorkUtils {

	public static boolean isNetAvailable(Context context) {
		ConnectivityManager conManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			return networkInfo.isAvailable();
		}
		return false;
	}
	/**
	 * 
	 * @Title: getNetType
	 * @Description: 1是移动网络，2是wifi
	 * @param context
	 * @return
	 */
	public static int getNetType(Context context) {
		int flag = -1;
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (mobile == State.CONNECTED) {
			return 1;
		}
		if (wifi == State.CONNECTED) {
			return 2;
		}
		return flag;
	}

	public static int getWifiLevel(Context context) {
		int strength = 0;
		// Wifi的连接速度及信号强度：
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiManager.getConnectionInfo();
		if (info.getBSSID() != null) {
			// 链接信号强度
			strength = WifiManager.calculateSignalLevel(info.getRssi(), 5);
		}
		return strength;
	}

}
