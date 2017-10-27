package com.gvtv.android.cloud.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class MsgLogin implements Parcelable{
	
	private int rtn;//响应结果代码 
	private String devname;//设备名称 
	private String appInfo;//App信息字节流 
	
	/**
	 * @return the rtn
	 */
	public int getRtn() {
		return rtn;
	}

	/**
	 * @param rtn the rtn to set
	 */
	public void setRtn(int rtn) {
		this.rtn = rtn;
	}

	/**
	 * @return the devname
	 */
	public String getDevname() {
		return devname;
	}

	/**
	 * @param devname the devname to set
	 */
	public void setDevname(String devname) {
		this.devname = devname;
	}

	/**
	 * @return the appInfo
	 */
	public String getAppInfo() {
		return appInfo;
	}

	/**
	 * @param appInfo the appInfo to set
	 */
	public void setAppInfo(String appInfo) {
		this.appInfo = appInfo;
	}

	@Override
	public String toString() {
		return "rtn: " + rtn
				+ ",devname: " + devname
				+ ",appInfo: " + appInfo;
	}

	public MsgLogin(){		
	}
	
	public MsgLogin(MsgLogin item){
		rtn = item.rtn;
		devname = item.devname;
		appInfo = item.appInfo;
	}
	
	@Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(devname);
        out.writeString(appInfo);
        out.writeInt(rtn);
    }

    public static final Parcelable.Creator<MsgLogin> CREATOR
            = new Parcelable.Creator<MsgLogin>() {
        public MsgLogin createFromParcel(Parcel in) {
            return new MsgLogin(in);
        }

        public MsgLogin[] newArray(int size) {
            return new MsgLogin[size];
        }
    };
    
    @Override
	public int describeContents() {
		return 0;
	}
    
    private MsgLogin(Parcel in) {
    	devname = in.readString();
    	appInfo = in.readString();
    	rtn = in.readInt();
    }
}
