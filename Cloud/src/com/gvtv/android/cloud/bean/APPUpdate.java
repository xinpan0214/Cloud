/**
 * Copyright © 2013GreatVision. All rights reserved.
 *
 * @Title: VodRecommendItem.java
 * @Prject: BananaTvLauncher
 * @Package: com.bananatv.custom.launcher.dataprovider.dataitem
 * @Description: 点播推荐信息 包括最新 热门  影视推荐
 * @author: 李英英 
 * @date: 2013年10月11日 下午5:07:27
 * @version: V1.0
 */
package com.gvtv.android.cloud.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class APPUpdate  implements Parcelable{
	
	/**
	 *说明：该接口请求方式为post，字符编码为UTF-8
	 *HOST	www.btv.com:8899(域名对应IP:10.0.1.217)
	 *ACTION	update.action
	 *参数	storeid=00_1& pkgname= com.android.as; com.android.ah&lang=zh_CN
	 * 
	 *<?xml version="1.0" encoding="utf-8"?>
	<apps total=”2”>
	　  <app>
		　　　<appcode><![CDATA[1001]]></appcode>
		　　　<appname><![CDATA[腾讯视频]]></appname>
		　　　<pkgname><![CDATA[包名]]></pkgname>
		　　　<icon><![CDATA[图标地址]]></icon>
		　　　<developer><![CDATA[开发商名称]]></ developer>
		　　　<version><![CDATA[版本信息]]></version>
		　　　<showversion><![CDATA[展示版本]]></showversion>
		　　　<size><![CDATA[应用大小]]></size>
		　　　<md5><![CDATA[md5码]]></md5>
		　　　<downloadurl><![CDATA[下载地址]]></downloadurl>
	　　</app>
	　　<app>
		　　　<appcode><![CDATA[1002]]></appcode>
		　　　<appname><![CDATA[空中打击]]></appname>
		　　　<pkgname><![CDATA[包名]]></pkgname>
		　　　<icon><![CDATA[图标地址]]></icon>
		　　　<developer><![CDATA[开发商名称]]></ developer>
		　　　<version><![CDATA[版本信息]]></version>
		　　　<showversion><![CDATA[展示版本]]></showversion>
		　　　<size><![CDATA[应用大小]]></size>
		　　　<md5><![CDATA[md5码]]></md5>
		　　　<downloadurl><![CDATA[下载地址]]></downloadurl>
	　　</app>
　　	……
	</apps>
	*/
	private String appcode;		
	private String appname;
	private String pkgname;		
	private String icon;
	private String developer;
	private String version;
	private String showversion;
	private int size;
	private String md5;
	private String downloadurl;
	private int total;
	
	
	/**
	 * @return the appcode
	 */
	public String getAppcode() {
		return appcode;
	}

	/**
	 * @param appcode the appcode to set
	 */
	public void setAppcode(String appcode) {
		this.appcode = appcode;
	}

	/**
	 * @return the appname
	 */
	public String getAppname() {
		return appname;
	}

	/**
	 * @param appname the appname to set
	 */
	public void setAppname(String appname) {
		this.appname = appname;
	}

	/**
	 * @return the pkgname
	 */
	public String getPkgname() {
		return pkgname;
	}

	/**
	 * @param pkgname the pkgname to set
	 */
	public void setPkgname(String pkgname) {
		this.pkgname = pkgname;
	}

	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the showversion
	 */
	public String getShowversion() {
		return showversion;
	}

	/**
	 * @param showversion the showversion to set
	 */
	public void setShowversion(String showversion) {
		this.showversion = showversion;
	}

	/**
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * @return the developer
	 */
	public String getDeveloper() {
		return developer;
	}

	/**
	 * @param developer the developer to set
	 */
	public void setDeveloper(String developer) {
		this.developer = developer;
	}

	/**
	 * @return the md5
	 */
	public String getMd5() {
		return md5;
	}

	/**
	 * @param md5 the md5 to set
	 */
	public void setMd5(String md5) {
		this.md5 = md5;
	}

	/**
	 * @return the downloadurl
	 */
	public String getDownloadurl() {
		return downloadurl;
	}

	/**
	 * @param downloadurl the downloadurl to set
	 */
	public void setDownloadurl(String downloadurl) {
		this.downloadurl = downloadurl;
	}

	/**
	 * @return the total
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * @param total the total to set
	 */
	public void setTotal(int total) {
		this.total = total;
	}

	public APPUpdate(){		
	}
	
	public APPUpdate(APPUpdate item){
		appcode = item.appcode;
		appname = item.appname;
		pkgname = item.pkgname;
		icon = item.icon;
		version = item.version;
		showversion = item.showversion;
		size = item.size;
		developer = item.developer;
		md5 = item.md5;
		downloadurl = item.downloadurl;
		total = item.total;
	}
	
	@Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(appcode);
        out.writeString(appname);
        out.writeString(pkgname);
        out.writeString(icon);
        out.writeString(version);
        out.writeString(showversion);
        out.writeInt(size);
        out.writeString(developer);
        out.writeString(md5);
        out.writeString(downloadurl);
        out.writeInt(total);
    }

    public static final Parcelable.Creator<APPUpdate> CREATOR
            = new Parcelable.Creator<APPUpdate>() {
        public APPUpdate createFromParcel(Parcel in) {
            return new APPUpdate(in);
        }

        public APPUpdate[] newArray(int size) {
            return new APPUpdate[size];
        }
    };
    
    @Override
	public int describeContents() {
		return 0;
	}
    
    private APPUpdate(Parcel in) {
    	appcode = in.readString();
    	appname = in.readString();
    	pkgname = in.readString();
    	icon = in.readString();
    	version = in.readString();
    	showversion = in.readString();
    	size = in.readInt();
    	developer = in.readString();;
    	md5 = in.readString();
    	downloadurl = in.readString();
    	total = in.readInt();
    }
	
}
