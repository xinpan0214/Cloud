package com.gvtv.android.cloud.bean;

public class UserInfo {
	private String userName;
	private String user_term;//User的终端类型：1-ios；2-android；3-PC
	private String pwd;
	private String bindDevCode;

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
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
	 * @return the pwd
	 */
	public String getPwd() {
		return pwd;
	}

	/**
	 * @param pwd the pwd to set
	 */
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	/**
	 * @return the bindDevCode
	 */
	public String getBindDevCode() {
		return bindDevCode;
	}

	/**
	 * @param bindDevCode the bindDevCode to set
	 */
	public void setBindDevCode(String bindDevCode) {
		this.bindDevCode = bindDevCode;
	}

}
