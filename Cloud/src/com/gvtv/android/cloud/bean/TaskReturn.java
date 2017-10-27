package com.gvtv.android.cloud.bean;

import java.util.ArrayList;


public class TaskReturn {
	private int ret;//响应值
	private String filename;//文件名称
	private String taskid;//下载任务id
	private String appid;//应用id
	private String appname;//应用名称
	private int load;//正下载的数量
	private int finish;//已经完成的数量
	private int pagenum;//页数
	private int tasknum;//任务数
	private int total;//硬盘总大小
	private int free;//硬盘剩余大小
	private int used;//硬盘使用大小
	
	private String dev_name;//设备名称
	private String devcode;//设备code
	private int speedlimit;//速度限制
	private int upload_flag;//上传标识
	
	private String hardversion;//软件版本
	private String prodversion;//产品版本
	private String prodname;//产品名称
	
	private int state;//升级包下载进度
	
	private ArrayList<VideoInfo> vFiles = null;
	private ArrayList<AppInfo> vGroup = null;
	private ArrayList<BackupFile> mBackupFileS = null;
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
	 * @return the load
	 */
	public int getLoad() {
		return load;
	}
	/**
	 * @param load the load to set
	 */
	public void setLoad(int load) {
		this.load = load;
	}
	/**
	 * @return the finish
	 */
	public int getFinish() {
		return finish;
	}
	/**
	 * @param finish the finish to set
	 */
	public void setFinish(int finish) {
		this.finish = finish;
	}
	/**
	 * @return the pagenum
	 */
	public int getPagenum() {
		return pagenum;
	}
	/**
	 * @param pagenum the pagenum to set
	 */
	public void setPagenum(int pagenum) {
		this.pagenum = pagenum;
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
	 * @return the vFiles
	 */
	public ArrayList<VideoInfo> getvFiles() {
		return vFiles;
	}
	/**
	 * @param vFiles the vFiles to set
	 */
	public void setvFiles(ArrayList<VideoInfo> vFiles) {
		this.vFiles = vFiles;
	}
	/**
	 * @return the vGroup
	 */
	public ArrayList<AppInfo> getvGroup() {
		return vGroup;
	}
	/**
	 * @param vGroup the vGroup to set
	 */
	public void setvGroup(ArrayList<AppInfo> vGroup) {
		this.vGroup = vGroup;
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
	 * @return the free
	 */
	public int getFree() {
		return free;
	}
	/**
	 * @param free the free to set
	 */
	public void setFree(int free) {
		this.free = free;
	}
	/**
	 * @return the used
	 */
	public int getUsed() {
		return used;
	}
	/**
	 * @param used the used to set
	 */
	public void setUsed(int used) {
		this.used = used;
	}
	/**
	 * @return the dev_name
	 */
	public String getDev_name() {
		return dev_name;
	}
	/**
	 * @return the hardversion
	 */
	public String getHardversion() {
		return hardversion;
	}
	/**
	 * @param hardversion the hardversion to set
	 */
	public void setHardversion(String hardversion) {
		this.hardversion = hardversion;
	}
	/**
	 * @return the prodversion
	 */
	public String getProdversion() {
		return prodversion;
	}
	/**
	 * @param prodversion the prodversion to set
	 */
	public void setProdversion(String prodversion) {
		this.prodversion = prodversion;
	}
	/**
	 * @return the prodname
	 */
	public String getProdname() {
		return prodname;
	}
	/**
	 * @param prodname the prodname to set
	 */
	public void setProdname(String prodname) {
		this.prodname = prodname;
	}
	/**
	 * @param dev_name the dev_name to set
	 */
	public void setDev_name(String dev_name) {
		this.dev_name = dev_name;
	}
	/**
	 * @return the devcode
	 */
	public String getDevcode() {
		return devcode;
	}
	/**
	 * @param devcode the devcode to set
	 */
	public void setDevcode(String devcode) {
		this.devcode = devcode;
	}
	/**
	 * @return the speedlimit
	 */
	public int getSpeedlimit() {
		return speedlimit;
	}
	/**
	 * @param speedlimit the speedlimit to set
	 */
	public void setSpeedlimit(int speedlimit) {
		this.speedlimit = speedlimit;
	}
	/**
	 * @return the upload_flag
	 */
	public int getUpload_flag() {
		return upload_flag;
	}
	/**
	 * @param upload_flag the upload_flag to set
	 */
	public void setUpload_flag(int upload_flag) {
		this.upload_flag = upload_flag;
	}

	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state;
	}
	/**
	 * @return the mBackupFileS
	 */
	public ArrayList<BackupFile> getmBackupFileS() {
		return mBackupFileS;
	}
	/**
	 * @param mBackupFileS the mBackupFileS to set
	 */
	public void setmBackupFileS(ArrayList<BackupFile> mBackupFileS) {
		this.mBackupFileS = mBackupFileS;
	}
}
