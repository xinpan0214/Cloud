package com.gvtv.android.cloud.bean;

import java.util.ArrayList;

public class DeviceInfo {
	private String dev_name = "";//设备名称
	private String mac;
	private String access_code ="";
	private int status = -1;//是否绑定 1绑定0未绑定,-1未找到
	private int app_amount;//应用数量
	private ArrayList<String> appids= new ArrayList<String>();
	private int total_size;
	private int free_size;
	private int used_size;
	private int speedlimit = -1;
	private int upload_flag;//升级标识 升级标识：0无升级;1有升级;2下载中；3可安装
	
	
	public void init(){
		dev_name = "";//设备名称
		mac = "";
		access_code ="";
		status = 0;//是否绑定 1绑定0未绑定
		app_amount = 0;//应用数量
		appids.clear();
		total_size = 0;
		free_size = 0;
		used_size = 0;
		speedlimit = 0;
		upload_flag = 0;//升级标识 升级标识：0无升级;1有升级;2下载中；3可安装
	}
	
	/**
	 * @return the dev_name
	 */
	public String getDev_name() {
		return dev_name;
	}
	/**
	 * @param dev_name the dev_name to set
	 */
	public void setDev_name(String dev_name) {
		//dev_name = "sds";
		this.dev_name = dev_name;
	}
	/**
	 * @return the mac
	 */
	public String getMac() {
		return mac;
	}
	/**
	 * @param mac the mac to set
	 */
	public void setMac(String mac) {
		this.mac = mac;
	}
	/**
	 * @return the access_code
	 */
	public String getAccess_code() {
		return access_code;
	}
	/**
	 * @param access_code the access_code to set
	 */
	public void setAccess_code(String access_code) {
		this.access_code = access_code;
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
	 * @return the app_amount
	 */
	public int getApp_amount() {
		return app_amount;
	}
	/**
	 * @param app_amount the app_amount to set
	 */
	public void setApp_amount(int app_amount) {
		this.app_amount = app_amount;
	}
	/**
	 * @return the apps
	 */
	public ArrayList<String> getAppids() {
		return appids;
	}
	/**
	 * @param apps the apps to set
	 */
	public void setAppids(ArrayList<String> appids) {
		this.appids = appids;
	}
	/**
	 * @return the total_size
	 */
	public int getTotal_size() {
		return total_size;
	}
	/**
	 * @param total_size the total_size to set
	 */
	public void setTotal_size(int total_size) {
		this.total_size = total_size;
	}
	/**
	 * @return the free_size
	 */
	public int getFree_size() {
		return free_size;
	}
	/**
	 * @param free_size the free_size to set
	 */
	public void setFree_size(int free_size) {
		this.free_size = free_size;
	}
	/**
	 * @return the used_size
	 */
	public int getUsed_size() {
		return used_size;
	}
	/**
	 * @param used_size the used_size to set
	 */
	public void setUsed_size(int used_size) {
		this.used_size = used_size;
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

}
