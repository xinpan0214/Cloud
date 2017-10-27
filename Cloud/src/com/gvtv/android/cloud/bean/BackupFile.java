package com.gvtv.android.cloud.bean;



public class BackupFile {
	private String filename;//文件名称
	private String filesize;
	private Long date;
	private String user_term;
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
	 * @return the date
	 */
	public Long getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(Long date) {
		this.date = date;
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

}
