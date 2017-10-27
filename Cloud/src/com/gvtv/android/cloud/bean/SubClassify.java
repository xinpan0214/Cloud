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

public class SubClassify  implements Parcelable{
	
	/**
	 * HOST	www.btv.com:8899(域名对应IP:10.0.1.217)
	 * ACTION	classifylist.action
	 * 参数	storeid=00_1&lang=zh_CN
	 * ?xml version="1.0" encoding="utf-8"?>
<classifys total=”2”>
  <classify>
    <classifycode><![CDATA[100]]></classifycode>
    <classifyname><![CDATA[电影]]></classifyname>
　　	<image><![CDATA[图片地址]]></image>
　　<subclassify total=”2”>
　　    <sub>
	　　       <subcode><![CDATA[111]]></subcode>
	　　       <subname><![CDATA[动作]]></subname>
	　　       <subimage><![CDATA[图片地址]]></subimage>
　　　　</sub>
　　　　<sub>
	　　       <subcode><![CDATA[112]]></subcode>
	　　       <subname><![CDATA[生活]]></subname>
	　　       <subimage><![CDATA[图片地址]]></subimage>
　　　　</sub>
　　　　……
　　</subclassify>
  </classify>
　<classify>
	    <classifycode><![CDATA[200]]></classifycode>
	    <classifyname><![CDATA[游戏]]></classifyname>
	　　<image><![CDATA[图片地址]]></image>
　　<subclassify>
　　    <sub>
	　　       <subcode><![CDATA[211]]></subcode>
	　　       <subname><![CDATA[射击]]></subname>
	　　       <subimage><![CDATA[图片地址]]></subimage>
　　　　</sub>
　　　　<sub>
	　　       <subcode><![CDATA[212]]></subcode>
	　　       <subname><![CDATA[体感]]></subname>
	　　       <subimage><![CDATA[图片地址]]></subimage>
　　　　</sub>
　　　　……
　　</subclassify>
  </classify>
　……
</classifys>
	*/
	private String subcode;		
	private String subname;
	private String subimage;
	private int subTotal;
	

	/**
	 * @return the subcode
	 */
	public String getSubcode() {
		return subcode;
	}

	/**
	 * @param subcode the subcode to set
	 */
	public void setSubcode(String subcode) {
		this.subcode = subcode;
	}

	/**
	 * @return the subname
	 */
	public String getSubname() {
		return subname;
	}

	/**
	 * @param subname the subname to set
	 */
	public void setSubname(String subname) {
		this.subname = subname;
	}

	/**
	 * @return the subimage
	 */
	public String getSubimage() {
		return subimage;
	}

	/**
	 * @param subimage the subimage to set
	 */
	public void setSubimage(String subimage) {
		this.subimage = subimage;
	}

	/**
	 * @return the subTotal
	 */
	public int getSubTotal() {
		return subTotal;
	}

	/**
	 * @param subTotal the subTotal to set
	 */
	public void setSubTotal(int subTotal) {
		this.subTotal = subTotal;
	}

	public SubClassify(){		
	}
	
	public SubClassify(SubClassify item){
		subcode = item.subcode;
		subname = item.subname;
		subimage = item.subimage;
		subTotal = item.subTotal;
	}
	
	@Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(subcode);
        out.writeString(subname);
        out.writeString(subimage);
        out.writeInt(subTotal);
    }

    public static final Parcelable.Creator<SubClassify> CREATOR
            = new Parcelable.Creator<SubClassify>() {
        public SubClassify createFromParcel(Parcel in) {
            return new SubClassify(in);
        }

        public SubClassify[] newArray(int size) {
            return new SubClassify[size];
        }
    };
    
    @Override
	public int describeContents() {
		return 0;
	}
    
    private SubClassify(Parcel in) {
    	subcode = in.readString();
    	subname = in.readString();
    	subimage = in.readString();
    	subTotal = in.readInt();
    }
	
}
