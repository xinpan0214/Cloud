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

public class Recommendcolumn  implements Parcelable{
	
	/**
	 * 推荐栏目列表接口
	 * HOST	www.btv.com:8899(域名对应IP:10.0.1.217)
	 * ACTION	recommendcolumn.action
	 * 参数	storeid=00_1&lang= zh_CN
	 * <recommendcolumns total=”2”>
	  <column>
	　　<code><![CDATA[1001]]></code>
	　　<name><![CDATA[热门推荐]]></name>
	　</column>
	　<column>
	　　<code><![CDATA[1002]]></code>
	　　<name><![CDATA[最新应用推荐]]></name>
	　</column>
	　……
	</recommendcolumns>
	*/
	private String code;			//资源ID
	private String name;	//资源名称
	private int total;//推荐总数
	
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}

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

	public Recommendcolumn(){		
	}
	
	public Recommendcolumn(Recommendcolumn item){
		code = item.code;
		name = item.name;
		total = item.total;
	}
	
	@Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(code);
        out.writeString(name);
        out.writeInt(total);
    }

    public static final Parcelable.Creator<Recommendcolumn> CREATOR
            = new Parcelable.Creator<Recommendcolumn>() {
        public Recommendcolumn createFromParcel(Parcel in) {
            return new Recommendcolumn(in);
        }

        public Recommendcolumn[] newArray(int size) {
            return new Recommendcolumn[size];
        }
    };
    
    @Override
	public int describeContents() {
		return 0;
	}
    
    private Recommendcolumn(Parcel in) {
    	code = in.readString();
    	name = in.readString();
    	total = in.readInt();
    }
}
