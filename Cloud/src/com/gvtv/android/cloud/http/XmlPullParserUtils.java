package com.gvtv.android.cloud.http;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.gvtv.android.cloud.bean.APPUpdate;
import com.gvtv.android.cloud.bean.AppInfo;
import com.gvtv.android.cloud.bean.Classify;
import com.gvtv.android.cloud.bean.Hotword;
import com.gvtv.android.cloud.bean.RecommendAlbum;
import com.gvtv.android.cloud.bean.Recommendcolumn;
import com.gvtv.android.cloud.bean.SubClassify;
import com.gvtv.android.cloud.bean.Topcolumn;
import com.gvtv.android.cloud.util.StringUtils;

public class XmlPullParserUtils {
	
	public static ArrayList<AppInfo> parseToAppInfo(ByteBuffer responseBytes){
		ArrayList<AppInfo> list = new ArrayList<AppInfo>();//自身的实例集合
		AppInfo app = null;
		int total = 0;
		int currentpage = 0;
		int num = 0;
		ByteArrayInputStream bin = new ByteArrayInputStream(responseBytes.array());
        InputStreamReader in = new InputStreamReader(bin);
        try {
        	XmlPullParserFactory pullFactory = XmlPullParserFactory.newInstance();     
            XmlPullParser parser = pullFactory.newPullParser();
            parser.setInput(in);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if("recommends".equalsIgnoreCase(parser.getName()) || "apps".equalsIgnoreCase(parser.getName())){
						String ttl = parser.getAttributeValue(null, "total");
						if(!StringUtils.isBlank(ttl)){
							total = Integer.parseInt(ttl.trim());
						}
						String currpage = parser.getAttributeValue(null, "currentpage");
						if(!StringUtils.isBlank(currpage)){
							currentpage = Integer.parseInt(currpage.trim());
						}
						
						String number = parser.getAttributeValue(null, "num");
						if(!StringUtils.isBlank(number)){
							num = Integer.parseInt(number.trim());
						}
					}else if("app".equalsIgnoreCase(parser.getName())){
						app = new AppInfo();
					}else if("appcode".equalsIgnoreCase(parser.getName())){
						app.setAppcode(parser.nextText());
					}else if("appname".equalsIgnoreCase(parser.getName())){
						app.setAppname(parser.nextText());
					}else if("pkgname".equalsIgnoreCase(parser.getName())){
						app.setPkgname(parser.nextText());
					}else if("icon".equalsIgnoreCase(parser.getName())){
						app.setIcon(parser.nextText());
					}else if("image".equalsIgnoreCase(parser.getName())){
						String[] strs = parser.nextText().split(";");
						if(strs != null){
							int size = strs.length;
							if(size == 1){
								app.setImage0(strs[0]);
							}else if(size == 2){
								app.setImage0(strs[0]);
								app.setImage1(strs[1]);
							}else if(size == 3){
								app.setImage0(strs[0]);
								app.setImage1(strs[1]);
								app.setImage2(strs[2]);
							}else if(size == 4){
								app.setImage0(strs[0]);
								app.setImage1(strs[1]);
								app.setImage2(strs[2]);
								app.setImage3(strs[3]);
							}else if(size == 5){
								app.setImage0(strs[0]);
								app.setImage1(strs[1]);
								app.setImage2(strs[2]);
								app.setImage3(strs[3]);
								app.setImage4(strs[4]);
							}
						}
					}else if("version".equalsIgnoreCase(parser.getName())){
						app.setVersion(parser.nextText());
					}else if("showversion".equalsIgnoreCase(parser.getName())){
						app.setShowversion(parser.nextText());
					}else if("size".equalsIgnoreCase(parser.getName())){
						app.setSize(parser.nextText());
					}else if("price".equalsIgnoreCase(parser.getName())){
						app.setPrice(Float.parseFloat(parser.nextText()));
					}else if("developer".equalsIgnoreCase(parser.getName())){
						app.setDeveloper(parser.nextText());
					}else if("summary".equalsIgnoreCase(parser.getName())){
						app.setSummary(parser.nextText());
					}else if("classifycode".equalsIgnoreCase(parser.getName())){
						app.setClassifycode(parser.nextText());
					}else if("md5".equalsIgnoreCase(parser.getName())){
						app.setMd5(parser.nextText());
					}else if("downloadurl".equalsIgnoreCase(parser.getName())){
						app.setDownloadurl(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("app".equalsIgnoreCase(parser.getName()) && app != null) {
						app.setTotal(total);
						app.setCurrentpage(currentpage);
						app.setNum(num);
						list.add(app);
						app = null;
					}
					break;
				default:
					break;
				}
                eventType = parser.next();  
            }  
		} catch (Exception e) {
			e.printStackTrace();
		}
        return list;
	}
	
	public static ArrayList<APPUpdate> parseToAPPUpdate(ByteBuffer responseBytes){
		ArrayList<APPUpdate> list = new ArrayList<APPUpdate>();//自身的实例集合
		APPUpdate app = null;
		int total = 0;
		ByteArrayInputStream bin = new ByteArrayInputStream(responseBytes.array());
        InputStreamReader in = new InputStreamReader(bin);
        try {
        	XmlPullParserFactory pullFactory = XmlPullParserFactory.newInstance();     
            XmlPullParser parser = pullFactory.newPullParser();
            parser.setInput(in);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if("apps".equalsIgnoreCase(parser.getName())){
						total = Integer.parseInt(parser.getAttributeValue(null, "total"));
					}else if("app".equalsIgnoreCase(parser.getName())){
						app = new APPUpdate();
					}else if("appcode".equalsIgnoreCase(parser.getName())){
						app.setAppcode(parser.nextText());
					}else if("appname".equalsIgnoreCase(parser.getName())){
						app.setAppname(parser.nextText());
					}else if("pkgname".equalsIgnoreCase(parser.getName())){
						app.setPkgname(parser.nextText());
					}else if("icon".equalsIgnoreCase(parser.getName())){
						app.setIcon(parser.nextText());
					}else if("version".equalsIgnoreCase(parser.getName())){
						app.setVersion(parser.nextText());
					}else if("showversion".equalsIgnoreCase(parser.getName())){
						app.setShowversion(parser.nextText());
					}else if("size".equalsIgnoreCase(parser.getName())){
						app.setSize(Integer.parseInt(parser.nextText()));
					}else if("developer".equalsIgnoreCase(parser.getName())){
						app.setDeveloper(parser.nextText());
					}else if("md5".equalsIgnoreCase(parser.getName())){
						app.setMd5(parser.nextText());
					}else if("downloadurl".equalsIgnoreCase(parser.getName())){
						app.setDownloadurl(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("app".equalsIgnoreCase(parser.getName()) && app != null) {
						app.setTotal(total);
						list.add(app);
						app = null;
					}
					break;
				default:
					break;
				}
                eventType = parser.next();  
            }  
		} catch (Exception e) {
			e.printStackTrace();
		}
        return list;
	}
	
	public static ArrayList<Classify> parseToClassify(ByteBuffer responseBytes){
		ArrayList<Classify> list = new ArrayList<Classify>();//自身的实例集合
		ArrayList<SubClassify> subList = null;
		Classify mClassify = null;
		SubClassify mSub = null;
		int total = 0;
		int subTotal = 0;
		ByteArrayInputStream bin = new ByteArrayInputStream(responseBytes.array());
        InputStreamReader in = new InputStreamReader(bin);
        try {
        	XmlPullParserFactory pullFactory = XmlPullParserFactory.newInstance();     
            XmlPullParser parser = pullFactory.newPullParser();
            parser.setInput(in);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if("classifys".equalsIgnoreCase(parser.getName())){
						total = Integer.parseInt(parser.getAttributeValue(null, "total"));
					}else if("classify".equalsIgnoreCase(parser.getName())){
						mClassify = new Classify();
					}else if("classifycode".equalsIgnoreCase(parser.getName())){
						mClassify.setClassifycode(parser.nextText());
					}else if("classifyname".equalsIgnoreCase(parser.getName())){
						mClassify.setClassifyname(parser.nextText());
					}else if("image".equalsIgnoreCase(parser.getName())){
						mClassify.setImage(parser.nextText());
					}else if("subclassify".equalsIgnoreCase(parser.getName())){
						subList = new ArrayList<SubClassify>();
						subTotal = Integer.parseInt(parser.getAttributeValue(null, "total"));
					}else if("sub".equalsIgnoreCase(parser.getName())){
						mSub = new SubClassify();
					}else if("subcode".equalsIgnoreCase(parser.getName())){
						mSub.setSubcode(parser.nextText());
					}else if("subname".equalsIgnoreCase(parser.getName())){
						mSub.setSubname(parser.nextText());
					}else if("subimage".equalsIgnoreCase(parser.getName())){
						mSub.setSubimage(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("sub".equalsIgnoreCase(parser.getName()) && mSub != null) {
						mSub.setSubTotal(subTotal);
						subList.add(mSub);
						mSub = null;
					}else if("classify".equalsIgnoreCase(parser.getName()) && mClassify != null){
						mClassify.setTotal(total);
						list.add(mClassify);
						mClassify = null;
						subTotal = 0;
					}
					break;
				default:
					break;
				}
                eventType = parser.next();  
            }  
		} catch (Exception e) {
			e.printStackTrace();
		}
        return list;
	}
	
	public static ArrayList<Hotword> parseToHotword(ByteBuffer responseBytes){
		ArrayList<Hotword> list = new ArrayList<Hotword>();//自身的实例集合
		Hotword hotword = null;
		int total = 0;
		int currentpage = 0;
		int num = 0;
		ByteArrayInputStream bin = new ByteArrayInputStream(responseBytes.array());
        InputStreamReader in = new InputStreamReader(bin);
        try {
        	XmlPullParserFactory pullFactory = XmlPullParserFactory.newInstance();     
            XmlPullParser parser = pullFactory.newPullParser();
            parser.setInput(in);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if("hotwords".equalsIgnoreCase(parser.getName())){
						total = Integer.parseInt(parser.getAttributeValue(null, "total"));
						currentpage = Integer.parseInt(parser.getAttributeValue(null, "currentpage"));
						num = Integer.parseInt(parser.getAttributeValue(null, "num"));
					}else if("word".equalsIgnoreCase(parser.getName())){
						hotword = new Hotword();
					}else if("name".equalsIgnoreCase(parser.getName())){
						hotword.setName(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("word".equalsIgnoreCase(parser.getName()) && hotword != null) {
						hotword.setTotal(total);
						hotword.setCurrentpage(currentpage);
						hotword.setNum(num);
						list.add(hotword);
						hotword = null;
					}
					break;
				default:
					break;
				}
                eventType = parser.next();  
            }  
		} catch (Exception e) {
			e.printStackTrace();
		}
        return list;
	}
	
	public static ArrayList<RecommendAlbum> parseToRecommendAlbum(ByteBuffer responseBytes){
		ArrayList<RecommendAlbum> list = new ArrayList<RecommendAlbum>();//自身的实例集合
		RecommendAlbum album = null;
		int total = 0;
		ByteArrayInputStream bin = new ByteArrayInputStream(responseBytes.array());
        InputStreamReader in = new InputStreamReader(bin);
        try {
        	XmlPullParserFactory pullFactory = XmlPullParserFactory.newInstance();     
            XmlPullParser parser = pullFactory.newPullParser();
            parser.setInput(in);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if("albums".equalsIgnoreCase(parser.getName())){
						total = Integer.parseInt(parser.getAttributeValue(null, "total"));
					}else if("album".equalsIgnoreCase(parser.getName())){
						album = new RecommendAlbum();
					}else if("albumcode".equalsIgnoreCase(parser.getName())){
						album.setAlbumcode(parser.nextText());
					}else if("albumname".equalsIgnoreCase(parser.getName())){
						album.setAlbumname(parser.nextText());
					}else if("image".equalsIgnoreCase(parser.getName())){
						album.setImage(parser.nextText());
					}else if("summary".equalsIgnoreCase(parser.getName())){
						album.setSummary(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("album".equalsIgnoreCase(parser.getName()) && album != null) {
						album.setTotal(total);
						list.add(album);
						album = null;
					}
					break;
				default:
					break;
				}
                eventType = parser.next();  
            }  
		} catch (Exception e) {
			e.printStackTrace();
		}
        return list;
	}
	
	public static ArrayList<Recommendcolumn> parseToRecommendcolumn(ByteBuffer responseBytes){
		ArrayList<Recommendcolumn> list = new ArrayList<Recommendcolumn>();//自身的实例集合
		Recommendcolumn column = null;
		int total = 0;
		ByteArrayInputStream bin = new ByteArrayInputStream(responseBytes.array());
        InputStreamReader in = new InputStreamReader(bin);
        try {
        	XmlPullParserFactory pullFactory = XmlPullParserFactory.newInstance();     
            XmlPullParser parser = pullFactory.newPullParser();
            parser.setInput(in);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if("recommendcolumns".equalsIgnoreCase(parser.getName())){
						total = Integer.parseInt(parser.getAttributeValue(null, "total"));
					}else if("column".equalsIgnoreCase(parser.getName())){
						column = new Recommendcolumn();
					}else if("name".equalsIgnoreCase(parser.getName())){
						column.setName(parser.nextText());
					}else if("code".equalsIgnoreCase(parser.getName())){
						column.setCode(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("column".equalsIgnoreCase(parser.getName()) && column != null) {
						column.setTotal(total);
						list.add(column);
						column = null;
					}
					break;
				default:
					break;
				}
                eventType = parser.next();  
            }  
		} catch (Exception e) {
			e.printStackTrace();
		}
        return list;
	}
	
	public static ArrayList<Topcolumn> parseToTopcolumn(ByteBuffer responseBytes){
		ArrayList<Topcolumn> list = new ArrayList<Topcolumn>();//自身的实例集合
		Topcolumn column = null;
		int total = 0;
		ByteArrayInputStream bin = new ByteArrayInputStream(responseBytes.array());
        InputStreamReader in = new InputStreamReader(bin);
        try {
        	XmlPullParserFactory pullFactory = XmlPullParserFactory.newInstance();     
            XmlPullParser parser = pullFactory.newPullParser();
            parser.setInput(in);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if("topcolumns".equalsIgnoreCase(parser.getName())){
						total = Integer.parseInt(parser.getAttributeValue(null, "total"));
					}else if("column".equalsIgnoreCase(parser.getName())){
						column = new Topcolumn();
					}else if("name".equalsIgnoreCase(parser.getName())){
						column.setName(parser.nextText());
					}else if("code".equalsIgnoreCase(parser.getName())){
						column.setCode(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("column".equalsIgnoreCase(parser.getName()) && column != null) {
						column.setTotal(total);
						list.add(column);
						column = null;
					}
					break;
				default:
					break;
				}
                eventType = parser.next();  
            }  
		} catch (Exception e) {
			e.printStackTrace();
		}
        return list;
	}
}
