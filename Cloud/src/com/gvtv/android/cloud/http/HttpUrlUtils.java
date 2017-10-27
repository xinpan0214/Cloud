package com.gvtv.android.cloud.http;

import java.util.List;

import com.gvtv.android.cloud.bean.AppInfo;
import com.gvtv.android.cloud.util.LogUtils;

public class HttpUrlUtils {
	
	/*storeid=00_1&lang= zh_CN*/
	public static String getRecommendcolumnURL(String storeid, String lang){
		StringBuffer buffer = new StringBuffer();
		buffer.append(HttpConstants.APPSTORE_HOST);
		buffer.append(HttpConstants.SEPARATER);
		buffer.append(HttpConstants.ACTION_RECOMMENDCOLUMN);
		buffer.append("storeid=");
		buffer.append(storeid);
		buffer.append(HttpConstants.AND);
		buffer.append("lang=");
		buffer.append(lang);
		LogUtils.getLog(HttpUrlUtils.class.getClass()).verbose(buffer.toString());
		return buffer.toString();	
	}
	/*storeid=00_1&columncode=1386056388288RECOMMEND317TTT&lang=zh_CN&page=1&size=20*/
	public static String getRecommendURL(String storeid, String columncode, String lang, int page, int size){
		StringBuffer buffer = new StringBuffer();
		buffer.append(HttpConstants.APPSTORE_HOST);
		buffer.append(HttpConstants.SEPARATER);
		buffer.append(HttpConstants.ACTION_RECOMMEND);
		buffer.append("storeid=");
		buffer.append(storeid);
		buffer.append(HttpConstants.AND);
		buffer.append("columncode=");
		buffer.append(columncode);
		buffer.append(HttpConstants.AND);
		buffer.append("lang=");
		buffer.append(lang);
		buffer.append(HttpConstants.AND);
		buffer.append("page=");
		buffer.append(page);
		buffer.append(HttpConstants.AND);
		buffer.append("size=");
		buffer.append(size);
		LogUtils.getLog(HttpUrlUtils.class.getClass()).verbose(buffer.toString());
		return buffer.toString();	
	}
	
	/*storeid=00_1&lang= zh_cn*/
	public static String getAlbumURL(String storeid, String lang){
		StringBuffer buffer = new StringBuffer();
		buffer.append(HttpConstants.APPSTORE_HOST);
		buffer.append(HttpConstants.SEPARATER);
		buffer.append(HttpConstants.ACTION_ALBUM);
		buffer.append("storeid=");
		buffer.append(storeid);
		buffer.append(HttpConstants.AND);
		buffer.append("lang=");
		buffer.append(lang);
		LogUtils.getLog(HttpUrlUtils.class.getClass()).verbose(buffer.toString());
		return buffer.toString();	
	}
	
	/*storeid=00_1&lang=zh_CN*/
	public static String getClassifylistURL(String storeid, String lang){
		StringBuffer buffer = new StringBuffer();
		buffer.append(HttpConstants.APPSTORE_HOST);
		buffer.append(HttpConstants.SEPARATER);
		buffer.append(HttpConstants.ACTION_CLASSIFYLIST);
		buffer.append("storeid=");
		buffer.append(storeid);
		buffer.append(HttpConstants.AND);
		buffer.append("lang=");
		buffer.append(lang);
		LogUtils.getLog(HttpUrlUtils.class.getClass()).verbose(buffer.toString());
		return buffer.toString();	
	}
	
	public static String getclassifysubapplistURL(String storeid, String lang){
		StringBuffer buffer = new StringBuffer();
		buffer.append(HttpConstants.APPSTORE_HOST);
		buffer.append(HttpConstants.SEPARATER);
		buffer.append(HttpConstants.ACTION_CLASSIFYSUBAPPLIST);
		buffer.append("storeid=");
		buffer.append(storeid);
		buffer.append(HttpConstants.AND);
		buffer.append("lang=");
		buffer.append(lang);
		LogUtils.getLog(HttpUrlUtils.class.getClass()).verbose(buffer.toString());
		return buffer.toString();	
	}
	
	/*classifycode= 1386056079982STORECLASS212TTT& storeid=00_1&lang=zh_CN&page=1&size=20*/
	public static String getClassifysubapplistURL(String classifycode, String storeid, String lang, int page, int size){
		StringBuffer buffer = new StringBuffer();
		buffer.append(HttpConstants.APPSTORE_HOST);
		buffer.append(HttpConstants.SEPARATER);
		buffer.append(HttpConstants.ACTION_CLASSIFYSUBAPPLIST);
		buffer.append("classifycode=");
		buffer.append(classifycode);
		buffer.append(HttpConstants.AND);
		buffer.append("storeid=");
		buffer.append(storeid);
		buffer.append(HttpConstants.AND);
		buffer.append("lang=");
		buffer.append(lang);
		buffer.append(HttpConstants.AND);
		buffer.append("page=");
		buffer.append(page);
		buffer.append(HttpConstants.AND);
		buffer.append("size=");
		buffer.append(size);
		LogUtils.getLog(HttpUrlUtils.class.getClass()).verbose(buffer.toString());
		return buffer.toString();	
	}
	
	/*albumcode= 1386056818309SUBJECT406TTT&storeid=1101&lang=zh_CN&page=1&size=20*/
	public static String getAlbumsubapplistURL(String albumcode, String storeid, String lang, int page, int size){
		StringBuffer buffer = new StringBuffer();
		buffer.append(HttpConstants.APPSTORE_HOST);
		buffer.append(HttpConstants.SEPARATER);
		buffer.append(HttpConstants.ACTION_ALBUMSUBAPPLIST);
		buffer.append("albumcode=");
		buffer.append(albumcode);
		buffer.append(HttpConstants.AND);
		buffer.append("storeid=");
		buffer.append(storeid);
		buffer.append(HttpConstants.AND);
		buffer.append("lang=");
		buffer.append(lang);
		buffer.append(HttpConstants.AND);
		buffer.append("page=");
		buffer.append(page);
		buffer.append(HttpConstants.AND);
		buffer.append("size=");
		buffer.append(size);
		LogUtils.getLog(HttpUrlUtils.class.getClass()).verbose(buffer.toString());
		return buffer.toString();	
	}
	
	/*storeid=00_1&lang=zh_CN&page=1&size=20*/
	public static String getHotwordURL(String storeid, String lang, int page, int size){
		StringBuffer buffer = new StringBuffer();
		buffer.append(HttpConstants.APPSTORE_HOST);
		buffer.append(HttpConstants.SEPARATER);
		buffer.append(HttpConstants.ACTION_HOTWORD);
		buffer.append("storeid=");
		buffer.append(storeid);
		buffer.append(HttpConstants.AND);
		buffer.append("lang=");
		buffer.append(lang);
		buffer.append(HttpConstants.AND);
		buffer.append("page=");
		buffer.append(page);
		buffer.append(HttpConstants.AND);
		buffer.append("size=");
		buffer.append(size);
		LogUtils.getLog(HttpUrlUtils.class.getClass()).verbose(buffer.toString());
		return buffer.toString();	
	}
	
	/*storeid=00_1&keyword=剑灵&lang=zh_CN&page=1&size=20*/
	public static String getSearchURL(String storeid, String keyword, String lang, int page, int size){
		StringBuffer buffer = new StringBuffer();
		buffer.append(HttpConstants.APPSTORE_HOST);
		buffer.append(HttpConstants.SEPARATER);
		buffer.append(HttpConstants.ACTION_SEARCH);
		buffer.append("keyword=");
		buffer.append(keyword);
		buffer.append(HttpConstants.AND);
		buffer.append("storeid=");
		buffer.append(storeid);
		buffer.append(HttpConstants.AND);
		buffer.append("lang=");
		buffer.append(lang);
		buffer.append(HttpConstants.AND);
		buffer.append("page=");
		buffer.append(page);
		buffer.append(HttpConstants.AND);
		buffer.append("size=");
		buffer.append(size);
		LogUtils.getLog(HttpUrlUtils.class.getClass()).verbose(buffer.toString());
		return buffer.toString();	
	}
	
	/*storeid=00_1& pkgname= com.android.as; com.android.ah&lang=zh_CN*/
	public static String getUpdateURL(String storeid, List<AppInfo> apps, String lang){
		StringBuffer buffer = new StringBuffer();
		buffer.append(HttpConstants.APPSTORE_HOST);
		buffer.append(HttpConstants.SEPARATER);
		buffer.append(HttpConstants.ACTION_UPDATE);
		buffer.append("storeid=");
		buffer.append(storeid);
		buffer.append(HttpConstants.AND);
		buffer.append("appid=");
		for (int i = 0; i < apps.size(); i++) {
			buffer.append(apps.get(i).getAppcode());
			if(i < apps.size() -1){
				buffer.append(";");
			}
		}
		buffer.append(HttpConstants.AND);
		buffer.append("lang=");
		buffer.append(lang);
		LogUtils.getLog(HttpUrlUtils.class.getClass()).verbose(buffer.toString());
		return buffer.toString();	
	}
	
	public static String getSingleUpdateURL(String storeid, AppInfo app, String lang){
		StringBuffer buffer = new StringBuffer();
		buffer.append(HttpConstants.APPSTORE_HOST);
		buffer.append(HttpConstants.SEPARATER);
		buffer.append(HttpConstants.ACTION_UPDATE);
		buffer.append("storeid=");
		buffer.append(storeid);
		buffer.append(HttpConstants.AND);
		buffer.append("appid=");
		buffer.append(app.getAppcode());
		buffer.append(";");
		buffer.append(HttpConstants.AND);
		buffer.append("lang=");
		buffer.append(lang);
		LogUtils.getLog(HttpUrlUtils.class.getClass()).verbose(buffer.toString());
		return buffer.toString();	
	}
	
	/*storeid=00_1&lang=zh_CN*/
	public static String getTopcolumnURL(String storeid, String lang){
		StringBuffer buffer = new StringBuffer();
		buffer.append(HttpConstants.APPSTORE_HOST);
		buffer.append(HttpConstants.SEPARATER);
		buffer.append(HttpConstants.ACTION_TOPCOLUMN);
		buffer.append("storeid=");
		buffer.append(storeid);
		buffer.append(HttpConstants.AND);
		buffer.append("lang=");
		buffer.append(lang);
		LogUtils.getLog(HttpUrlUtils.class.getClass()).verbose(buffer.toString());
		return buffer.toString();	
	}
	
	/*storeid=00_1& columncode= 1386057304681RANKLIST499TTT &lang=zh_CN&page=1&size=20*/
	public static String getTopURL(String storeid, String columncode, String lang, int page, int size){
		StringBuffer buffer = new StringBuffer();
		buffer.append(HttpConstants.APPSTORE_HOST);
		buffer.append(HttpConstants.SEPARATER);
		buffer.append(HttpConstants.ACTION_TOP);
		buffer.append("columncode=");
		buffer.append(columncode);
		buffer.append(HttpConstants.AND);
		buffer.append("storeid=");
		buffer.append(storeid);
		buffer.append(HttpConstants.AND);
		buffer.append("lang=");
		buffer.append(lang);
		buffer.append(HttpConstants.AND);
		buffer.append("page=");
		buffer.append(page);
		buffer.append(HttpConstants.AND);
		buffer.append("size=");
		buffer.append(size);
		LogUtils.getLog(HttpUrlUtils.class.getClass()).verbose(buffer.toString());
		return buffer.toString();	
	}
	
	
	/*APPURL = "http://test.epg.com:7370/A1ZVNK1LTDIB/index.action?appcode=A1ZVNK1LTDIB&lang=zh_CN&terminal=5&ostype=2";*/
	public static String getAPPURL(String app_host, String appcode,String lang, String terminal, String ostype){
		StringBuffer buffer = new StringBuffer();
		buffer.append(app_host);
		//buffer.append(HttpConstants.SEPARATER);
		buffer.append(appcode);
		buffer.append(HttpConstants.SEPARATER);
		buffer.append(HttpConstants.ACTION_APP);
		buffer.append("appcode=");
		buffer.append(appcode);
		buffer.append(HttpConstants.AND);
		buffer.append("lang=");
		buffer.append(lang);
		buffer.append(HttpConstants.AND);
		buffer.append("terminal=");
		buffer.append(terminal);
		buffer.append(HttpConstants.AND);
		buffer.append("ostype=");
		buffer.append(ostype);
		LogUtils.getLog(HttpUrlUtils.class.getClass()).verbose(buffer.toString());
		return buffer.toString();	
	}
}
