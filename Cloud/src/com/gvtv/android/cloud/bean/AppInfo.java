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
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class AppInfo  implements Parcelable{
	
	private String appcode;		
	private String appname;
	private String pkgname;		
	private String icon;
	private String image0, image1, image2, image3, image4;		
	private String version;
	private String showversion;
	private String size;
	private float price;
	private String developer;
	private String summary;
	private String classifycode;
	private String md5;
	private String downloadurl;
	private int total;//推荐总数
	private int currentpage;
	private int num;
	private int tasknum;
	private int isChecked;//是否被选中0否，1是
	private int isVisible;//选框是否可见0否，1是
	private List<VideoInfo> videoInfoList = new ArrayList<VideoInfo>();
	
	
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
	 * @return the image0
	 */
	public String getImage0() {
		return image0;
	}

	/**
	 * @param image0 the image0 to set
	 */
	public void setImage0(String image0) {
		this.image0 = image0;
	}

	/**
	 * @return the image1
	 */
	public String getImage1() {
		return image1;
	}

	/**
	 * @param image1 the image1 to set
	 */
	public void setImage1(String image1) {
		this.image1 = image1;
	}

	/**
	 * @return the image2
	 */
	public String getImage2() {
		return image2;
	}

	/**
	 * @param image2 the image2 to set
	 */
	public void setImage2(String image2) {
		this.image2 = image2;
	}

	/**
	 * @return the image3
	 */
	public String getImage3() {
		return image3;
	}

	/**
	 * @param image3 the image3 to set
	 */
	public void setImage3(String image3) {
		this.image3 = image3;
	}

	/**
	 * @return the image4
	 */
	public String getImage4() {
		return image4;
	}

	/**
	 * @param image4 the image4 to set
	 */
	public void setImage4(String image4) {
		this.image4 = image4;
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
	public String getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}

	/**
	 * @return the price
	 */
	public float getPrice() {
		return price;
	}

	/**
	 * @param price the price to set
	 */
	public void setPrice(float price) {
		this.price = price;
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
	 * @return the summary
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * @param summary the summary to set
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}

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
	 * @return the tasknum
	 */
	public int getTasknum() {
		return tasknum;
	}

	/**
	 * @param tasknum the tasknum to set
	 */
	public void setTasknum(int tasknum) {
		this.tasknum = tasknum;
	}

	/**
	 * @return the isChecked
	 */
	public int getIsChecked() {
		return isChecked;
	}

	/**
	 * @param isChecked the isChecked to set
	 */
	public void setIsChecked(int isChecked) {
		this.isChecked = isChecked;
	}

	/**
	 * @return the isVisible
	 */
	public int getIsVisible() {
		return isVisible;
	}

	/**
	 * @param isVisible the isVisible to set
	 */
	public void setIsVisible(int isVisible) {
		this.isVisible = isVisible;
	}

	/**
	 * @return the videoInfoList
	 */
	public List<VideoInfo> getVideoInfoList() {
		return videoInfoList;
	}

	/**
	 * @param videoInfoList the videoInfoList to set
	 */
	public void setVideoInfoList(List<VideoInfo> videoInfoList) {
		this.videoInfoList = videoInfoList;
	}

	public AppInfo(){		
	}
	
	public AppInfo(AppInfo item){
		appcode = item.appcode;
		appname = item.appname;
		pkgname = item.pkgname;
		icon = item.icon;
		image0 = item.image0;
		image1 = item.image1;
		image2 = item.image2;
		image3 = item.image3;
		image4 = item.image4;
		version = item.version;
		showversion = item.showversion;
		size = item.size;
		price = item.price;
		developer = item.developer;
		summary = item.summary;
		classifycode = item.classifycode;
		md5 = item.md5;
		downloadurl = item.downloadurl;
		total = item.total;
		currentpage = item.currentpage;
		num = item.num;
		tasknum = item.tasknum;
		videoInfoList = item.videoInfoList;
		isChecked = item.isChecked;
		isVisible = item.isVisible;
	}
	
	@Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(appcode);
        out.writeString(appname);
        out.writeString(pkgname);
        out.writeString(icon);
        out.writeString(image0);
        out.writeString(image1);
        out.writeString(image2);
        out.writeString(image3);
        out.writeString(image4);
        out.writeString(version);
        out.writeString(showversion);
        out.writeString(size);
        out.writeFloat(price);
        out.writeString(developer);
        out.writeString(summary);
        out.writeString(classifycode);
        out.writeString(md5);
        out.writeString(downloadurl);
        out.writeInt(total);
        out.writeInt(currentpage);
        out.writeInt(num);
        out.writeInt(tasknum);
        out.writeInt(isChecked);
        out.writeInt(isVisible);
        out.writeList(videoInfoList);
    }

    public static final Parcelable.Creator<AppInfo> CREATOR
            = new Parcelable.Creator<AppInfo>() {
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };
    
    @Override
	public int describeContents() {
		return 0;
	}
    
    @SuppressWarnings("unchecked")
	protected AppInfo(Parcel in) {
    	appcode = in.readString();
    	appname = in.readString();
    	pkgname = in.readString();
    	icon = in.readString();
    	image0 = in.readString();
    	image1 = in.readString();
    	image2 = in.readString();
    	image3 = in.readString();
    	image4 = in.readString();
    	version = in.readString();
    	showversion = in.readString();
    	size = in.readString();
    	price = in.readFloat();
    	developer = in.readString();
    	summary = in.readString();
    	classifycode = in.readString();
    	md5 = in.readString();
    	downloadurl = in.readString();
    	total = in.readInt();
    	currentpage = in.readInt();
    	num = in.readInt();
    	tasknum = in.readInt();
    	isChecked = in.readInt();
    	isVisible = in.readInt();
    	videoInfoList = in.readArrayList(VideoInfo.class.getClassLoader());
    }
	
	
}
