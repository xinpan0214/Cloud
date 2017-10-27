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

public class RecommendAlbum  implements Parcelable{
	
	/**
	 * 专题推荐列表接口
	 * HOST	www.btv.com:8899(域名对应IP:10.0.1.217)
	 * ACTION	album.action
	 * 参数	storeid=00_1&lang= zh_CN
	 * <?xml version="1.0" encoding="utf-8"?>
	<albums total=”2”>
	  	<album>
		　　	<albumcode><![CDATA[1001]]></albumcode>
		　　	<albumname><![CDATA[幼儿识字]]></albumname>
		　　	<image><![CDATA[图标地址]]></image>
		　　	<summary><![CDATA[摘要]]></summary>
	　	</album>
	　	<album>
		　　	<albumcode><![CDATA[1002]]></albumcode>
		　　	<albumname><![CDATA[英语通]]></albumname>
		　　	<image><![CDATA[图标地址]]></image>
		　　	<summary><![CDATA[摘要]]></summary>
	　	</album>
　	……
	</albums>
	*/
	private String albumcode;
	private String albumname;
	private String image;
	private String summary;
	private int total;
	
	/**
	 * @return the albumcode
	 */
	public String getAlbumcode() {
		return albumcode;
	}

	/**
	 * @param albumcode the albumcode to set
	 */
	public void setAlbumcode(String albumcode) {
		this.albumcode = albumcode;
	}

	/**
	 * @return the albumname
	 */
	public String getAlbumname() {
		return albumname;
	}

	/**
	 * @param albumname the albumname to set
	 */
	public void setAlbumname(String albumname) {
		this.albumname = albumname;
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

	public RecommendAlbum(){		
	}
	
	public RecommendAlbum(RecommendAlbum item){
		albumcode = item.albumcode;
		albumname = item.albumname;
		image = item.image;
		summary = item.summary;
		total = item.total;
	}
	
	@Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(albumcode);
        out.writeString(albumname);
        out.writeString(image);
        out.writeString(summary);
        out.writeInt(total);
    }

    public static final Parcelable.Creator<RecommendAlbum> CREATOR
            = new Parcelable.Creator<RecommendAlbum>() {
        public RecommendAlbum createFromParcel(Parcel in) {
            return new RecommendAlbum(in);
        }

        public RecommendAlbum[] newArray(int size) {
            return new RecommendAlbum[size];
        }
    };
    
    @Override
	public int describeContents() {
		return 0;
	}
    
    private RecommendAlbum(Parcel in) {
    	albumcode = in.readString();
    	albumname = in.readString();
    	image = in.readString();
    	summary = in.readString();
    	total = in.readInt();
    }
	
}
