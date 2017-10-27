package com.gvtv.android.cloud.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class MsgBean implements Parcelable{
	private int version = 9527;
	private int sequence;
	private int action;
	private int ret;
	private int offset;
	private int filesize;
	private String fileName;
	private String json_content;
	
	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}
	/**
	 * @return the sequence
	 */
	public int getSequence() {
		return sequence;
	}
	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	/**
	 * @return the action
	 */
	public int getAction() {
		return action;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(int action) {
		this.action = action;
	}
	/**
	 * @return the ret
	 */
	public int getRet() {
		return ret;
	}
	/**
	 * @param ret the ret to set
	 */
	public void setRet(int ret) {
		this.ret = ret;
	}
	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}
	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}
	/**
	 * @return the filesize
	 */
	public int getFilesize() {
		return filesize;
	}
	/**
	 * @param filesize the filesize to set
	 */
	public void setFilesize(int filesize) {
		this.filesize = filesize;
	}
	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}
	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * @return the json_content
	 */
	public String getJson_content() {
		return json_content;
	}
	/**
	 * @param json_content the json_content to set
	 */
	public void setJson_content(String json_content) {
		this.json_content = json_content;
	}

	
	@Override
	public String toString() {
		return "sequence:" + sequence
				+ ",action: " + action
				+ ",ret: " + ret
				+ ",offset: " + offset
				+ ",filesize: " + filesize
				+ ",fileName: " + fileName
				+ ",json_content: " + json_content;
	}

	public MsgBean(){		
	}
	
	public MsgBean(MsgBean item){
		version = item.version;
		sequence = item.sequence;
		action = item.action;
		ret = item.ret;
		offset = item.offset;
		filesize = item.filesize;
		fileName = item.fileName;
		json_content = item.json_content;
	}
	
	@Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(version);
        out.writeInt(sequence);
        out.writeInt(action);
        out.writeInt(ret);
        out.writeInt(offset);
        out.writeInt(filesize);
        out.writeString(fileName);
        out.writeString(json_content);
    }

    public static final Parcelable.Creator<MsgBean> CREATOR
            = new Parcelable.Creator<MsgBean>() {
        public MsgBean createFromParcel(Parcel in) {
            return new MsgBean(in);
        }

        public MsgBean[] newArray(int size) {
            return new MsgBean[size];
        }
    };
    
    @Override
	public int describeContents() {
		return 0;
	}
    
    private MsgBean(Parcel in) {
    	version = in.readInt();
    	sequence = in.readInt();
    	action = in.readInt();
    	ret = in.readInt();
    	offset = in.readInt();
    	filesize = in.readInt();
    	fileName = in.readString();
    	json_content = in.readString();
    }
}
