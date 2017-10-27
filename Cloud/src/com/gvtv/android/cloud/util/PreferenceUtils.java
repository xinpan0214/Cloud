package com.gvtv.android.cloud.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferenceUtils {

//	/*设备信息*/
//	public static int getDeviceTotalSize(Context mContext) {
//		SharedPreferences spf = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE);
//		int totalSize = spf.getInt("TOTAL_SIZE", 0);
//		return totalSize;
//	}
	
//	public static void setDeviceTotalSize(Context mContext, int totalSize){
//		SharedPreferences.Editor editor = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE).edit();
//		editor.putInt("TOTAL_SIZE", totalSize);
//		editor.commit();
//	}
//	
//	public static int getDeviceFreeSize(Context mContext) {
//		SharedPreferences spf = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE);
//		int freeSize = spf.getInt("FREE_SIZE", 0);
//		return freeSize;
//	}
//	
//	public static void setDeviceFreeSize(Context mContext, int freeSize){
//		SharedPreferences.Editor editor = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE).edit();
//		editor.putInt("FREE_SIZE", freeSize);
//		editor.commit();
//	}
	
//	public static int getDeviceUsedSize(Context mContext) {
//		SharedPreferences spf = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE);
//		int used = spf.getInt("USED", 0);
//		return used;
//	}
//	
//	public static void setDeviceUsedSize(Context mContext, int used){
//		SharedPreferences.Editor editor = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE).edit();
//		editor.putInt("USED", used);
//		editor.commit();
//	}
	
//	public static void setDeviceName(Context mContext, String name){
//		SharedPreferences.Editor editor = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE).edit();
//		editor.putString("DEV_NAME", name);
//		editor.commit();
//	}
//	
//	public static String getDeviceName(Context mContext) {
//		SharedPreferences spf = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE);
//		String name = spf.getString("DEV_NAME", null);
//		return name;
//	}
//	
//	public static void setDeviceCode(Context mContext, String code){
//		SharedPreferences.Editor editor = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE).edit();
//		editor.putString("DEV_CODE", code);
//		editor.commit();
//	}
//	
//	public static String getDeviceCode(Context mContext) {
//		SharedPreferences spf = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE);
//		String code = spf.getString("DEV_CODE", null);
//		return code;
//	}
	
	public static int getSpeedlimit(Context mContext) {
		SharedPreferences spf = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE);
		int speedlimit = spf.getInt("SPEEDLIMIT", 1024);
		return speedlimit;
	}
	
	public static void setSpeedlimit(Context mContext, int speedlimit){
		if(speedlimit < 1024){
			speedlimit = 1024;
		}
		SharedPreferences.Editor editor = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE).edit();
		editor.putInt("SPEEDLIMIT", speedlimit);
		editor.commit();
	}
	
	public static int getSpeedlimit_up(Context mContext) {
		SharedPreferences spf = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE);
		int speedlimit = spf.getInt("SPEEDLIMIT_UP", 1024);
		return speedlimit;
	}
	
	public static void setSpeedlimit_up(Context mContext, int speedlimit){
		if(speedlimit < 1024){
			speedlimit = 1024;
		}
		SharedPreferences.Editor editor = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE).edit();
		editor.putInt("SPEEDLIMIT_UP", speedlimit);
		editor.commit();
	}

	
	public static boolean isSpeedlimitEnable(Context mContext) {
		SharedPreferences spf = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE);
		boolean speedlimitEnable = spf.getBoolean("SPEEDLIMITENABLE", false);
		return speedlimitEnable;
	}
	
	public static void setSpeedlimitEnable(Context mContext, boolean speedlimitEnable){
		SharedPreferences.Editor editor = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE).edit();
		editor.putBoolean("SPEEDLIMITENABLE", speedlimitEnable);
		editor.commit();
	}
	
	public static boolean isNoticeEnable(Context mContext) {
		SharedPreferences spf = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE);
		boolean noticeEnable = spf.getBoolean("NOTICEENABLE", false);
		return noticeEnable;
	}
	
	public static void setNoticeEnable(Context mContext, boolean noticeEnable){
		SharedPreferences.Editor editor = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE).edit();
		editor.putBoolean("NOTICEENABLE", noticeEnable);
		editor.commit();
	}
	
	public static boolean isContectAccessable(Context mContext) {
		SharedPreferences spf = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE);
		boolean contectAccessable = spf.getBoolean("CONTECTACCESSABLE", true);
		return contectAccessable;
	}
	
	public static void setContectAccessable(Context mContext, boolean contectAccessable){
		SharedPreferences.Editor editor = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE).edit();
		editor.putBoolean("CONTECTACCESSABLE", contectAccessable);
		editor.commit();
	}
	
//	
//	public static int getUpload_flag(Context mContext) {//　　upload_flag取值：0无升级;1有升级;2下载中；3可安装
//		SharedPreferences spf = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE);
//		int speedlimit = spf.getInt("UPLOAD_FLAG", 0);
//		return speedlimit;
//	}
//	
//	public static void setUpload_flag(Context mContext, int upload_flag){
//		SharedPreferences.Editor editor = mContext.getSharedPreferences("DEVICE_INFO", Context.MODE_PRIVATE).edit();
//		editor.putInt("UPLOAD_FLAG", upload_flag);
//		editor.commit();
//	}
	
	public static void saveLoadServer(Context context,String loadIP,int loadport){
        //获取SharedPreferences对象
        SharedPreferences sharedPre=context.getSharedPreferences("LOADSERVER", Context.MODE_PRIVATE);
        //获取Editor对象
        Editor editor=sharedPre.edit();
        //设置参数
        editor.putString("loadIP", loadIP);
        editor.putInt("loadport", loadport);
        //提交
        editor.commit();
    }
	
	public static String getLoadIP(Context mContext) {
		SharedPreferences spf = mContext.getSharedPreferences("LOADSERVER", Context.MODE_PRIVATE);
		String loadIP = spf.getString("loadIP", "");
		return loadIP;
	}
	
	public static int getLoadPort(Context mContext) {
		SharedPreferences spf = mContext.getSharedPreferences("LOADSERVER", Context.MODE_PRIVATE);
		int loadport = spf.getInt("loadport", 0);
		return loadport;
	}
	
	public static void saveBizServer(Context context,String bizIP,int bizTCPPort, int bizUDPPort){
        //获取SharedPreferences对象
        SharedPreferences sharedPre=context.getSharedPreferences("BIZSERVER", Context.MODE_PRIVATE);
        //获取Editor对象
        Editor editor=sharedPre.edit();
        //设置参数
        editor.putString("bizIP", bizIP);
        editor.putInt("bizTCPPort", bizTCPPort);
        editor.putInt("bizUDPPort", bizUDPPort);
        //提交
        editor.commit();
    }
	
	public static String getBizIP(Context mContext) {
		SharedPreferences spf = mContext.getSharedPreferences("BIZSERVER", Context.MODE_PRIVATE);
		String bizIP = spf.getString("bizIP", "");
		return bizIP;
	}
	
	public static int getBizTCPPort(Context mContext) {
		SharedPreferences spf = mContext.getSharedPreferences("BIZSERVER", Context.MODE_PRIVATE);
		int bizTCPPort = spf.getInt("bizTCPPort", 0);
		return bizTCPPort;
	}
	
	public static int getBizUDPPort(Context mContext) {
		SharedPreferences spf = mContext.getSharedPreferences("BIZSERVER", Context.MODE_PRIVATE);
		int bizUDPPort = spf.getInt("bizUDPPort", 0);
		return bizUDPPort;
	}
	
	
	public static void saveLoginInfo(Context context,String username,String password){
        //获取SharedPreferences对象
        SharedPreferences sharedPre=context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        //获取Editor对象
        Editor editor=sharedPre.edit();
        //设置参数
        editor.putString("username", username);
        editor.putString("password", password);
        //提交
        editor.commit();
    }
	
	public static String getUsername(Context mContext) {
		SharedPreferences spf = mContext.getSharedPreferences("USER", Context.MODE_PRIVATE);
		String username = spf.getString("username", null);
		return username;
	}
	
	public static String getPassword(Context mContext) {
		SharedPreferences spf = mContext.getSharedPreferences("USER", Context.MODE_PRIVATE);
		String password = spf.getString("password", null);
		return password;
	}
	
	public static int getFlagFirstIn(Context mContext) {
		SharedPreferences spf = mContext.getSharedPreferences("FlagFirstIn", Context.MODE_PRIVATE);
		int flagfirstin = spf.getInt("flagfirstin", 0);
		return flagfirstin;
	}
	
	public static void setFlagFirstIn(Context mContext, int flagfirstin) {
        SharedPreferences sharedPre = mContext.getSharedPreferences("FlagFirstIn", Context.MODE_PRIVATE);
        Editor editor=sharedPre.edit();
        editor.putInt("flagfirstin", flagfirstin);
        editor.commit();
	}
	
}
