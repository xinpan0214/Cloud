package com.gvtv.android.cloud.util;

import java.util.Comparator;

import com.gvtv.android.cloud.bean.AppInfo;
import com.gvtv.android.cloud.bean.BackupFile;
import com.gvtv.android.cloud.bean.VideoInfo;


public class SortUtils {
    
	public static class BackupFileSort implements Comparator<BackupFile> {
    	
    	@Override
    	public int compare(BackupFile lhs, BackupFile rhs) {
    		BackupFile s1 = lhs;
    		BackupFile s2 = rhs;
    		return s1.getDate().compareTo(s2.getDate());
    	}
    }
	
	public static class SortVideoInfoByName implements Comparator<VideoInfo> {
		 public int compare(VideoInfo o1, VideoInfo o2) {
			VideoInfo s1 =  o1;
			VideoInfo s2 =  o2;
			return s1.getAppid().compareTo(s2.getAppid());
		 }
	}
	
	public static class SortVideoInfoByStatus implements Comparator<VideoInfo> {
		 public int compare(VideoInfo o1, VideoInfo o2) {
			VideoInfo s1 =  o1;
			VideoInfo s2 =  o2;
			return s1.getStatus()-s2.getStatus();
		 }
	}
	
	public static class SortVideoInfoByFinishtime implements Comparator<VideoInfo> {
		 public int compare(VideoInfo o1, VideoInfo o2) {
			VideoInfo s1 =  o1;
			VideoInfo s2 =  o2;
			return (int) (s1.getFinish_time()-s2.getFinish_time());
		 }
	}
	
	public static class SortAppByAppName implements Comparator<AppInfo> {
		 public int compare(AppInfo o1, AppInfo o2) {
			 AppInfo s1 =  o1;
			 AppInfo s2 =  o2;
			 return s1.getAppname().compareToIgnoreCase(s2.getAppname());
		 }
	}
}