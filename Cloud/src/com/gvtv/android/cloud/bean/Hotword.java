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

public class Hotword  implements Parcelable{
	
	/**
	 * HOST	www.btv.com:8899(域名对应IP:10.0.1.217)
	 * ACTION	hotword.action
	 * 参数	storeid=00_1&lang=zh_CN&page=1&size=20
	 *<?xml version="1.0" encoding="utf-8"?>
	<hotwords currentpage=”1” num=”20” total=”2”>
　  	<word>
　　　		<name><![CDATA[影视古]]></appname>
　　	</word>
　　	<word>
　　　		<name><![CDATA[好播音乐]]></appname>
　　</word>
　　……
</hotwords>
	*/
	
	private String name;	//资源名称
	private int currentpage;
	private int num;
	private int total;//推荐总数

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the currentpage
	 */
	public int getCurrentpage() {
		return currentpage;
	}

	/**
	 * @param currentpage the currentpage to set
	 */
	public void setCurrentpage(int currentpage) {
		this.currentpage = currentpage;
	}

	/**
	 * @return the num
	 */
	public int getNum() {
		return num;
	}

	/**
	 * @param num the num to set
	 */
	public void setNum(int num) {
		this.num = num;
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

	public Hotword(){		
	}
	
	public Hotword(Hotword item){
		name = item.name;
		currentpage = item.currentpage;
		num = item.num;
		total = item.total;
	}
	
	@Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeInt(currentpage);
        out.writeInt(num);
        out.writeInt(total);
    }

    public static final Parcelable.Creator<Hotword> CREATOR
            = new Parcelable.Creator<Hotword>() {
        public Hotword createFromParcel(Parcel in) {
            return new Hotword(in);
        }

        public Hotword[] newArray(int size) {
            return new Hotword[size];
        }
    };
    
    @Override
	public int describeContents() {
		return 0;
	}
    
    private Hotword(Parcel in) {
    	name = in.readString();
    	total = in.readInt();
    }
	
}
