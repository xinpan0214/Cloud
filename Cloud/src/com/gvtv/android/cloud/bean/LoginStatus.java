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

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.Parcel;
import android.os.Parcelable;

public class LoginStatus  implements Parcelable{
	private int mResId;			//资源ID
	private String mResName;	//资源名称
	private int mPosition;   	//显示位置
	private int mStatus;		//资源状态 (0-启用 1-停用)
	private String mImageUrl;	//资源海报
	private int mType;			//资源类型 (0-电影 1-电视剧)
	
	public LoginStatus(){		
	}
	
	public LoginStatus(LoginStatus item){
		mResId = item.mResId;
		mResName = item.mResName;
		mPosition = item.mPosition;
		mStatus = item.mStatus;			
		mImageUrl = item.mImageUrl;		
		mType = item.mType;
	}
	
	public int getResId() {
		return mResId;
	}
	public void setResId(int mResId) {
		this.mResId = mResId;
	}
	public String getResName() {
		return mResName;
	}
	public void setResName(String mResName) {
		this.mResName = mResName;
	}
	public int getPosition() {
		return mPosition;
	}
	public void setPosition(int mPosition) {
		this.mPosition = mPosition;
	}
	public int getStatus() {
		return mStatus;
	}
	public void setStatus(int mStatus) {
		this.mStatus = mStatus;
	}
	public String getImageUrl() {
		return mImageUrl;
	}
	public void setImageUrl(String mImageUrl) {
		this.mImageUrl = mImageUrl;
	}
	public int getType() {
		return mType;
	}
	public void setType(int mType) {
		this.mType = mType;
	}
	
	public int describeContents() {
        return 0;
    }

	@Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(mResId);
        out.writeString(mResName);
        out.writeInt(mPosition);
        out.writeInt(mStatus);
        out.writeString(mImageUrl);
        out.writeInt(mType);
    }

    public static final Parcelable.Creator<LoginStatus> CREATOR
            = new Parcelable.Creator<LoginStatus>() {
        public LoginStatus createFromParcel(Parcel in) {
            return new LoginStatus(in);
        }

        public LoginStatus[] newArray(int size) {
            return new LoginStatus[size];
        }
    };
    
    private LoginStatus(Parcel in) {
    	mResId = in.readInt();
    	mResName = in.readString();
    	mPosition = in.readInt();
    	mStatus = in.readInt();
    	mImageUrl = in.readString();
    	mType = in.readInt();
    }
	public static ArrayList<LoginStatus> parse(ByteBuffer responseBytes){
		ArrayList<LoginStatus> list = new ArrayList<LoginStatus>();//自身的实例集合
		LoginStatus vodDetail = null;
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
					if("Racommend".equalsIgnoreCase(parser.getName())){
						
					}else if("Program".equalsIgnoreCase(parser.getName())){
						vodDetail = new LoginStatus();
					}else if("Name".equalsIgnoreCase(parser.getName())){
						vodDetail.setResName(parser.nextText());
					}else if("Image".equalsIgnoreCase(parser.getName())){
						vodDetail.setImageUrl(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if ("Program".equalsIgnoreCase(parser.getName()) && vodDetail != null) {
						list.add(vodDetail);
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
