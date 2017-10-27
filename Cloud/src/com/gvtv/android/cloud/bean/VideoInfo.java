package com.gvtv.android.cloud.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class VideoInfo implements Parcelable {
	
	private String filename;//名称
	private String filesize;//大小
	private String user_term;//用户组
	private String play_url;//下载地址
	private String taskid;//任务id
	private int status;//任务状态0暂停，1等待，2下载中
	private int ratio;//下载比率
	private String appid;//应用id
	private String appname;//应用名称
	private int tasknum;//应用任务数
	private String fileid;//文件id
	private int share_status;//分享状态
	private long create_time;//创建时间
	private long finish_time;//完成时间
	
	private int checked;//是否被选中0否，1是
	private int isVisible;//选框是否可见0否，1是
	
	
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * @return the filesize
	 */
	public String getFilesize() {
		return filesize;
	}

	/**
	 * @param filesize the filesize to set
	 */
	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}

	/**
	 * @return the user_term
	 */
	public String getUser_term() {
		return user_term;
	}

	/**
	 * @param user_term the user_term to set
	 */
	public void setUser_term(String user_term) {
		this.user_term = user_term;
	}

	/**
	 * @return the play_url
	 */
	public String getPlay_url() {
		return play_url;
	}

	/**
	 * @param play_url the play_url to set
	 */
	public void setPlay_url(String play_url) {
		this.play_url = play_url;
	}

	/**
	 * @return the taskid
	 */
	public String getTaskid() {
		return taskid;
	}

	/**
	 * @param taskid the taskid to set
	 */
	public void setTaskid(String taskid) {
		this.taskid = taskid;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the ratio
	 */
	public int getRatio() {
		return ratio;
	}

	/**
	 * @param ratio the ratio to set
	 */
	public void setRatio(int ratio) {
		this.ratio = ratio;
	}

	/**
	 * @return the appid
	 */
	public String getAppid() {
		return appid;
	}

	/**
	 * @param appid the appid to set
	 */
	public void setAppid(String appid) {
		this.appid = appid;
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
	 * @return the fileid
	 */
	public String getFileid() {
		return fileid;
	}

	/**
	 * @param fileid the fileid to set
	 */
	public void setFileid(String fileid) {
		this.fileid = fileid;
	}

	/**
	 * @return the share_status
	 */
	public int getShare_status() {
		return share_status;
	}

	/**
	 * @param share_status the share_status to set
	 */
	public void setShare_status(int share_status) {
		this.share_status = share_status;
	}

	/**
	 * @return the create_time
	 */
	public long getCreate_time() {
		return create_time;
	}

	/**
	 * @param create_time the create_time to set
	 */
	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}

	/**
	 * @return the finish_time
	 */
	public long getFinish_time() {
		return finish_time;
	}

	/**
	 * @param finish_time the finish_time to set
	 */
	public void setFinish_time(long finish_time) {
		this.finish_time = finish_time;
	}

	/**
	 * @return the checked
	 */
	public int getChecked() {
		return checked;
	}

	/**
	 * @param checked the checked to set
	 */
	public void setChecked(int checked) {
		this.checked = checked;
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

	public VideoInfo(){		
	}
	
	public VideoInfo(VideoInfo item){
		filename = item.filename;
		filesize = item.filesize;
		user_term = item.user_term;
		play_url = item.play_url;
		taskid = item.taskid;
		status = item.status;
		ratio = item.ratio;
		appid = item.appid;
		appname = item.appname;
		tasknum = item.tasknum;
		fileid = item.fileid;
		finish_time = item.finish_time;
		create_time = item.create_time;
		share_status = item.share_status;
		checked = item.checked;
		isVisible = item.isVisible;
	}
	
	@Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(filename);
        out.writeString(filesize);
        out.writeString(user_term);
        out.writeString(play_url);
        out.writeString(taskid);
        out.writeInt(status);
        out.writeInt(ratio);
        out.writeString(appid);
        out.writeString(appname);
        out.writeInt(tasknum);
        out.writeString(fileid);
        out.writeString(user_term);
        out.writeInt(isVisible);
        out.writeLong(finish_time);
        out.writeLong(create_time);
        out.writeInt(checked);
        out.writeInt(isVisible);
    }

    public static final Parcelable.Creator<VideoInfo> CREATOR
            = new Parcelable.Creator<VideoInfo>() {
        public VideoInfo createFromParcel(Parcel in) {
            return new VideoInfo(in);
        }

        public VideoInfo[] newArray(int size) {
            return new VideoInfo[size];
        }
    };
    
    @Override
	public int describeContents() {
		return 0;
	}
    
    private VideoInfo(Parcel in) {
		filename = in.readString();
		filesize = in.readString();
		user_term = in.readString();
    	play_url = in.readString();
    	taskid = in.readString();
    	status = in.readInt();
    	ratio = in.readInt();
    	appid = in.readString();
    	appname = in.readString();
    	tasknum = in.readInt();
    	fileid = in.readString();
    	finish_time = in.readLong();
    	create_time = in.readLong();
    	share_status = in.readInt();
    	checked = in.readInt();
    	isVisible = in.readInt();
    }

}
