package com.gvtv.android.cloud.http;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import android.content.Context;

import com.android.common.dataprovider.HttpCallBack;
import com.android.common.dataprovider.http.HttpEngineManager;
import com.android.common.dataprovider.packet.inpacket.InElementListPacket;
import com.android.common.dataprovider.packet.inpacket.InPacket;
import com.android.common.dataprovider.packet.outpacket.OutElementListPacket;
import com.android.common.dataprovider.packet.outpacket.OutPacket;
import com.android.common.dataprovider.packet.outpacket.OutPacket.Method;
import com.gvtv.android.cloud.bean.AppInfo;

public class HttpRequestUtils {
	
	public static int requestRecommendcolumn(HttpCallBack uiCallback, Context mContext){
		OutPacket out = new OutElementListPacket();
		URL url = null;
		try {
			url = new URL(HttpUrlUtils.getRecommendcolumnURL(HttpConstants.STORE_ID, HttpConstants.LANG).trim());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		out.setHttpURL(url);
		out.setRequestMethod(Method.GET);
		InPacket in = new InElementListPacket(uiCallback, HttpConstants.ACTION_RECOMMENDCOLUMN_TOKEN);
		return HttpEngineManager.createHttpEngine(out, in, mContext);
	}
	
	public static int requestRecommend(HttpCallBack uiCallback, Context mContext,String columncode, int page, int size, int token){
		OutPacket out = new OutElementListPacket();
		URL url = null;
		try {
			url = new URL(HttpUrlUtils.getRecommendURL(HttpConstants.STORE_ID, columncode, HttpConstants.LANG, page, size).trim());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		out.setHttpURL(url);
		out.setRequestMethod(Method.GET);
		InPacket in = new InElementListPacket(uiCallback, token);
		return HttpEngineManager.createHttpEngine(out, in, mContext);
	}
	
	public static int requestAlbum(HttpCallBack uiCallback, Context mContext){
		OutPacket out = new OutElementListPacket();
		URL url = null;
		try {
			url = new URL(HttpUrlUtils.getAlbumURL(HttpConstants.STORE_ID, HttpConstants.LANG));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		out.setHttpURL(url);
		out.setRequestMethod(Method.GET);
		InPacket in = new InElementListPacket(uiCallback, HttpConstants.ACTION_ALBUM_TOKEN);
		return HttpEngineManager.createHttpEngine(out, in, mContext);
	}
	
	public static int requestClassifylist(HttpCallBack uiCallback, Context mContext){
		OutPacket out = new OutElementListPacket();
		URL url = null;
		try {
			url = new URL(HttpUrlUtils.getClassifylistURL(HttpConstants.STORE_ID, HttpConstants.LANG));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		out.setHttpURL(url);
		out.setRequestMethod(Method.GET);
		InPacket in = new InElementListPacket(uiCallback, HttpConstants.ACTION_CLASSIFYLIST_TOKEN);
		return HttpEngineManager.createHttpEngine(out, in, mContext);
	}
	
	public static int requestClassifysubapplist(HttpCallBack uiCallback, Context mContext,String classifycode, int page, int size){
		OutPacket out = new OutElementListPacket();
		URL url = null;
		try {
			url = new URL(HttpUrlUtils.getClassifysubapplistURL(classifycode, HttpConstants.STORE_ID, HttpConstants.LANG, page, size));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		out.setHttpURL(url);
		out.setRequestMethod(Method.GET);
		InPacket in = new InElementListPacket(uiCallback, HttpConstants.ACTION_CLASSIFYSUBAPPLIST_TOKEN);
		return HttpEngineManager.createHttpEngine(out, in, mContext);
	}
	
	public static int requestAlbumsubapplist(HttpCallBack uiCallback, Context mContext,String albumcode, int page, int size){
		OutPacket out = new OutElementListPacket();
		URL url = null;
		try {
			url = new URL(HttpUrlUtils.getAlbumsubapplistURL(albumcode, HttpConstants.STORE_ID, HttpConstants.LANG, page, size));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		out.setHttpURL(url);
		out.setRequestMethod(Method.GET);
		InPacket in = new InElementListPacket(uiCallback, HttpConstants.ACTION_ALBUMSUBAPPLIST_TOKEN);
		return HttpEngineManager.createHttpEngine(out, in, mContext);
	}
	
	public static int requestAppHotword(HttpCallBack uiCallback, Context mContext, int page, int size){
		OutPacket out = new OutElementListPacket();
		URL url = null;
		try {
			url = new URL(HttpUrlUtils.getHotwordURL(HttpConstants.STORE_ID, HttpConstants.LANG, page, size));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		out.setHttpURL(url);
		out.setRequestMethod(Method.GET);
		InPacket in = new InElementListPacket(uiCallback, HttpConstants.ACTION_HOTWORD_TOKEN);
		return HttpEngineManager.createHttpEngine(out, in, mContext);
	}
	
	public static int requestSearchApp(HttpCallBack uiCallback, Context mContext,String keyword, int page, int size){
		OutPacket out = new OutElementListPacket();
		URL url = null;
		try {
			url = new URL(HttpUrlUtils.getSearchURL(HttpConstants.STORE_ID, keyword, HttpConstants.LANG, page, size));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		out.setHttpURL(url);
		out.setRequestMethod(Method.POST);
		InPacket in = new InElementListPacket(uiCallback, HttpConstants.ACTION_SEARCH_TOKEN);
		return HttpEngineManager.createHttpEngine(out, in, mContext);
	}
	
	public static int requestUpdateApp(HttpCallBack uiCallback, Context mContext,List<AppInfo> apps){
		OutPacket out = new OutElementListPacket();
		URL url = null;
		try {
			url = new URL(HttpUrlUtils.getUpdateURL(HttpConstants.STORE_ID, apps, HttpConstants.LANG));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		out.setHttpURL(url);
		out.setRequestMethod(Method.POST);
		InPacket in = new InElementListPacket(uiCallback, HttpConstants.ACTION_UPDATE_TOKEN);
		return HttpEngineManager.createHttpEngine(out, in, mContext);
	}
	
	public static int requestSingleUpdateApp(HttpCallBack uiCallback, Context mContext,AppInfo app){
		OutPacket out = new OutElementListPacket();
		URL url = null;
		try {
			url = new URL(HttpUrlUtils.getSingleUpdateURL(HttpConstants.STORE_ID, app, HttpConstants.LANG));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		out.setHttpURL(url);
		out.setRequestMethod(Method.POST);
		InPacket in = new InElementListPacket(uiCallback, HttpConstants.ACTION_UPDATE_TOKEN);
		return HttpEngineManager.createHttpEngine(out, in, mContext);
	}
	
	public static int requestTopcolumn(HttpCallBack uiCallback, Context mContext){
		OutPacket out = new OutElementListPacket();
		URL url = null;
		try {
			url = new URL(HttpUrlUtils.getTopcolumnURL(HttpConstants.STORE_ID, HttpConstants.LANG));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		out.setHttpURL(url);
		out.setRequestMethod(Method.GET);
		InPacket in = new InElementListPacket(uiCallback, HttpConstants.ACTION_TOPCOLUMN_TOKEN);
		return HttpEngineManager.createHttpEngine(out, in, mContext);
	}
	
	public static int requestTopApp(HttpCallBack uiCallback, Context mContext, String columncode, int page, int size){
		OutPacket out = new OutElementListPacket();
		URL url = null;
		try {
			url = new URL(HttpUrlUtils.getTopURL(HttpConstants.STORE_ID, columncode, HttpConstants.LANG, page, size));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		out.setHttpURL(url);
		out.setRequestMethod(Method.GET);
		InPacket in = new InElementListPacket(uiCallback, HttpConstants.ACTION_TOP_TOKEN);
		return HttpEngineManager.createHttpEngine(out, in, mContext);
	}
	
	public static int requestAppUrl(HttpCallBack uiCallback, Context mContext, String downloadurl){
		OutPacket out = new OutElementListPacket();
		URL url = null;
		try {
			url = new URL(downloadurl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		out.setHttpURL(url);
		out.setRequestMethod(Method.GET);
		InPacket in = new InElementListPacket(uiCallback, HttpConstants.ACTION_APPURL);
		return HttpEngineManager.createHttpEngine(out, in, mContext);
	}
	
}
