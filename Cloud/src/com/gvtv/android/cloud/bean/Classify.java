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

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Classify  implements Parcelable{
	
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
	private String classifycode;		
	private String classifyname;
	private String image;
	private int total;//推荐总数
	private ArrayList<SubClassify> subClassifyList = new ArrayList<SubClassify>();
	

	/**
	 * @return the classifycode
	 */
	public String getClassifycode() {
		return classifycode;
	}

	/**
	 * @param classifycode the classifycode to set
	 */
	public void setClassifycode(String classifycode) {
		this.classifycode = classifycode;
	}

	/**
	 * @return the classifyname
	 */
	public String getClassifyname() {
		return classifyname;
	}

	/**
	 * @param classifyname the classifyname to set
	 */
	public void setClassifyname(String classifyname) {
		this.classifyname = classifyname;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(String image) {
		this.image = image;
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

	/**
	 * @return the subClassifyList
	 */
	public ArrayList<SubClassify> getSubClassifyList() {
		return subClassifyList;
	}

	/**
	 * @param subClassifyList the subClassifyList to set
	 */
	public void setSubClassifyList(ArrayList<SubClassify> subClassifyList) {
		this.subClassifyList = subClassifyList;
	}

	public Classify(){		
	}
	
	public Classify(Classify item){
		classifycode = item.classifycode;
		classifyname = item.classifyname;
		image = item.image;
		total = item.total;
		subClassifyList = item.subClassifyList;
	}
	
	@Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(classifycode);
        out.writeString(classifyname);
        out.writeString(image);
        out.writeInt(total);
        out.writeList(subClassifyList);
    }

    public static final Parcelable.Creator<Classify> CREATOR
            = new Parcelable.Creator<Classify>() {
        public Classify createFromParcel(Parcel in) {
            return new Classify(in);
        }

        public Classify[] newArray(int size) {
            return new Classify[size];
        }
    };
    
    @Override
	public int describeContents() {
		return 0;
	}
    
	@SuppressWarnings("unchecked")
	private Classify(Parcel in) {
    	classifycode = in.readString();
    	classifyname = in.readString();
    	image = in.readString();
    	total = in.readInt();
    	subClassifyList = in.readArrayList(SubClassify.class.getClassLoader());
    }
	
}
