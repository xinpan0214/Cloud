package com.gvtv.android.cloud;

import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.gvtv.android.cloud.bean.DeviceInfo;
import com.gvtv.android.cloud.bean.ServerAddress;
import com.gvtv.android.cloud.service.MessageService;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.PreferenceUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class CloudApplication extends Application {
	private static CloudApplication mApplication;
	public static DeviceInfo bindedDeviceInfo;
	
	public volatile static ArrayList<ServerAddress> bizAddes = new ArrayList<ServerAddress>();//业务
	public volatile static ArrayList<ServerAddress> loadAddes = new ArrayList<ServerAddress>();//负载
	public volatile static ArrayList<DeviceInfo> devs = new ArrayList<DeviceInfo>();
	
	public static String user_name ="";
	public static String user_pwd ="";
	public static String aeskey;
	public static String nat_ip;
	public static int nat_port;
	
	public volatile static int requestID = 2;//偶数包序列
	public volatile static int requestID_relogin = 1;//奇数，自动登录包序列（鉴权）
	public volatile static int sequence = 2;//偶数，大多数转发包序列
	public volatile static int sequence_refresh = 1;//奇数，主要用于刷新下载页
	
	//public static int user_status;//用户状态0，未登录；1，已登录且业务线可用；2已登录且业务线不可用，需重新密钥协商;3已登录且业务线不可用，已协商密钥，需重登录
	private LinkedList<Activity> activityList = new LinkedList<Activity>();
	
	public synchronized static CloudApplication getInstance() {
		return mApplication;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		CrashHandler crashHandler = CrashHandler.getInstance();  
        crashHandler.init();
		mApplication = this;
		initImageLoader(mApplication);
		bindedDeviceInfo = new DeviceInfo();
		ServerAddress loadAddr = new ServerAddress();
		loadAddr.setserverIP(PreferenceUtils.getLoadIP(mApplication));
		loadAddr.setserverPort((short)(PreferenceUtils.getLoadPort(mApplication)));
		
		if(loadAddr.getserverPort() != 0){
			loadAddes.add(loadAddr);
		}
		
		
		ServerAddress bizAddr = new ServerAddress();
		bizAddr.setserverIP(PreferenceUtils.getBizIP(mApplication));
		bizAddr.setserverPort((short)(PreferenceUtils.getBizTCPPort(mApplication)));
		bizAddr.setServerUDPPort((short)(PreferenceUtils.getBizUDPPort(mApplication)));
		
		bizAddr.setserverIP("10.0.1.226");
		bizAddr.setserverPort((short)(8183));
		bizAddr.setServerUDPPort((short)7102);
		
		if(bizAddr.getserverPort() != 0){
			bizAddes.add(bizAddr);
		}
		
	}

	
	@Override
	public void onTerminate() {
		Intent  serviceIntent = new Intent(mApplication, MessageService.class);
		stopService(serviceIntent);
		exit();
		super.onTerminate();
	}
	
	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.discCacheSize(20 * 1024 * 1024)
				.discCacheFileCount(200)
				.memoryCacheSize(1024 * 1024 * 10)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.build();
		
		//.enableLogging() // Not necessary in common
				
		ImageLoader.getInstance().init(config);
	}
	
	//添加Activity到容器中
	public void addActivity(Activity activity){
		activityList.addLast(activity);
		for (Activity aa : activityList) {
			LogUtils.getLog(getClass()).verbose(aa.getClass().getName());
		}
//		int size = activityList.size();
//		for (int i = 0; i < size - 1; i++) {
//			if(activityList.get(i).getClass().getName().equals(activity.getClass().getName())){
//				activityList.get(i).finish();
//				activityList.remove(i);
//				break;
//			}
//		}
	}
	
	public void removeActivity(Activity activity){
		int size = activityList.size();
		for (int i = 0; i < size; i++) {
			if(activityList.get(i).getClass().getName().equals(activity.getClass().getName())){
				activityList.get(i).finish();
				activityList.remove(i);
				break;
			}
		}
	}
	
	
	//遍历所有Activity并finish
	public void exit(){
		for (int i = 0; i < activityList.size(); i++) {
			activityList.getFirst().finish();
			activityList.removeFirst();
			i--;
		}
	}
}
