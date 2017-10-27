package com.gvtv.android.cloud.db;


public class DbConstants {
	
	public static final int APP_LIST = 0;
	public static final int APP_DETAIL = 1;
	public static final String PROVIDER_URL = "com.gvtv.android.cloud.provider.AppContentProvider";
	public static final int APPTABLE_CHANGED = 100;
	public static final String DB_NAME = "com.gvtv.android.cloud.db";
	public static final int DB_VERSION = 1;

	private static final String TERMINATOR = ";";

	public static final StringBuffer CREATE_APPINFO_CACHE_TABLE_SQL = new StringBuffer();
	public static final String APPINFO_CACHE_TABLE_NAME = "appinfo_cache";
	public static final String APPINFO_CACHE_ID = android.provider.BaseColumns._ID;
	public static final String APPINFO_CACHE_APPCODE = "appcode";
	public static final String APPINFO_CACHE_APPNAME = "appname";
	public static final String APPINFO_CACHE_PKGNAME = "pkgname";
	public static final String APPINFO_CACHE_ICON = "icon";
	public static final String APPINFO_CACHE_IMAGE0 = "image0";
	public static final String APPINFO_CACHE_IMAGE1 = "image1";
	public static final String APPINFO_CACHE_IMAGE2 = "image2";
	public static final String APPINFO_CACHE_IMAGE3 = "image3";
	public static final String APPINFO_CACHE_IMAGE4 = "image4";
	public static final String APPINFO_CACHE_VERSION = "version";
	public static final String APPINFO_CACHE_SHOWVERSION = "showversion";
	public static final String APPINFO_CACHE_SIZE = "size";
	public static final String APPINFO_CACHE_PRICE = "price";
	public static final String APPINFO_CACHE_DEVELOPER = "developer";
	public static final String APPINFO_CACHE_SUMMARY = "summary";
	public static final String APPINFO_CACHE_CLASSIFYCODE = "classifycode";
	public static final String APPINFO_CACHE_MD5 = "md5";
	public static final String APPINFO_CACHE_DOWNLOADURL = "downloadurl";
	public static final String APPINFO_CACHE_USERNAME = "username";

	public static final StringBuffer CREATE_USERINFO_CACHE_TABLE_SQL = new StringBuffer();
	public static final String USERINFO_CACHE_TABLE_NAME = "userinfo_cache";
	public static final String USERINFO_CACHE_ID = android.provider.BaseColumns._ID;
	public static final String USERINFO_CACHE_USERNAME = "username";
	public static final String USERINFO_CACHE_PASSWORD = "password";
	public static final String USERINFO_CACHE_BIND_EDVCODE = "bind_edvcode";
	
	
	public static final StringBuffer CREATE_DEVINFO_CACHE_TABLE_SQL = new StringBuffer();
	public static final String DEVINFO_CACHE_TABLE_NAME = "devinfo_cache";
	public static final String DEVINFO_CACHE_ID = android.provider.BaseColumns._ID;
	public static final String DEVINFO_CACHE_EDVNAME = "edv_name";
	public static final String DEVINFO_CACHE_ACCESSCODE = "access_code";
	public static final String DEVINFO_CACHE_MAC = "mac";
	public static final String DEVINFO_CACHE_STATUS = "status";
	public static final String DEVINFO_CACHE_TOTALSIZE = "total_size";
	public static final String DEVINFO_CACHE_FREESIZE = "free_size";
	public static final String DEVINFO_CACHE_USEDSIZE = "used_size";
	public static final String DEVINFO_CACHE_SPEEDLIMIT = "speedlimit";
	public static final String DEVINFO_CACHE_UPLOADFLAG = "upload_flag";
	
	static {
		CREATE_APPINFO_CACHE_TABLE_SQL.append("CREATE TABLE ").append(APPINFO_CACHE_TABLE_NAME);
		CREATE_APPINFO_CACHE_TABLE_SQL.append(" (").append(APPINFO_CACHE_ID)
				.append(" integer primary key autoincrement,");
		CREATE_APPINFO_CACHE_TABLE_SQL.append(APPINFO_CACHE_APPCODE).append(" text,");
		CREATE_APPINFO_CACHE_TABLE_SQL.append(APPINFO_CACHE_APPNAME).append(" text,");
		CREATE_APPINFO_CACHE_TABLE_SQL.append(APPINFO_CACHE_PKGNAME).append(" text,");
		CREATE_APPINFO_CACHE_TABLE_SQL.append(APPINFO_CACHE_ICON).append(" text,");
		CREATE_APPINFO_CACHE_TABLE_SQL.append(APPINFO_CACHE_IMAGE0).append(" text,");
		CREATE_APPINFO_CACHE_TABLE_SQL.append(APPINFO_CACHE_IMAGE1).append(" text,");
		CREATE_APPINFO_CACHE_TABLE_SQL.append(APPINFO_CACHE_IMAGE2).append(" text,");
		CREATE_APPINFO_CACHE_TABLE_SQL.append(APPINFO_CACHE_IMAGE3).append(" text,");
		CREATE_APPINFO_CACHE_TABLE_SQL.append(APPINFO_CACHE_IMAGE4).append(" text,");
		CREATE_APPINFO_CACHE_TABLE_SQL.append(APPINFO_CACHE_VERSION).append(" text,");
		CREATE_APPINFO_CACHE_TABLE_SQL.append(APPINFO_CACHE_SHOWVERSION).append(" text,");
		CREATE_APPINFO_CACHE_TABLE_SQL.append(APPINFO_CACHE_SIZE).append(" integer,");
		CREATE_APPINFO_CACHE_TABLE_SQL.append(APPINFO_CACHE_PRICE).append(" float,");
		CREATE_APPINFO_CACHE_TABLE_SQL.append(APPINFO_CACHE_DEVELOPER).append(" text,");
		CREATE_APPINFO_CACHE_TABLE_SQL.append(APPINFO_CACHE_SUMMARY).append(" text,");
		CREATE_APPINFO_CACHE_TABLE_SQL.append(APPINFO_CACHE_CLASSIFYCODE).append(" text,");
		CREATE_APPINFO_CACHE_TABLE_SQL.append(APPINFO_CACHE_MD5).append(" text,");
		CREATE_APPINFO_CACHE_TABLE_SQL.append(APPINFO_CACHE_DOWNLOADURL).append(" text,");
		CREATE_APPINFO_CACHE_TABLE_SQL.append(APPINFO_CACHE_USERNAME).append(" text)")
				.append(TERMINATOR);

		
		CREATE_USERINFO_CACHE_TABLE_SQL.append("CREATE TABLE ").append(USERINFO_CACHE_TABLE_NAME);
		CREATE_USERINFO_CACHE_TABLE_SQL.append(" (").append(USERINFO_CACHE_ID)
				.append(" integer primary key autoincrement,");
		CREATE_USERINFO_CACHE_TABLE_SQL.append(USERINFO_CACHE_USERNAME).append(" text,");
		CREATE_USERINFO_CACHE_TABLE_SQL.append(USERINFO_CACHE_PASSWORD).append(" text,");
		CREATE_USERINFO_CACHE_TABLE_SQL.append(USERINFO_CACHE_BIND_EDVCODE).append(" text)").append(TERMINATOR);
		
		
		CREATE_DEVINFO_CACHE_TABLE_SQL.append("CREATE TABLE ").append(DEVINFO_CACHE_TABLE_NAME);
		CREATE_DEVINFO_CACHE_TABLE_SQL.append(" (").append(DEVINFO_CACHE_ID)
				.append(" integer primary key autoincrement,");
		CREATE_DEVINFO_CACHE_TABLE_SQL.append(DEVINFO_CACHE_EDVNAME).append(" text,");
		CREATE_DEVINFO_CACHE_TABLE_SQL.append(DEVINFO_CACHE_ACCESSCODE).append(" text,");
		CREATE_DEVINFO_CACHE_TABLE_SQL.append(DEVINFO_CACHE_MAC).append(" text,");
		CREATE_DEVINFO_CACHE_TABLE_SQL.append(DEVINFO_CACHE_STATUS).append(" integer,");
		CREATE_DEVINFO_CACHE_TABLE_SQL.append(DEVINFO_CACHE_TOTALSIZE).append(" integer,");
		CREATE_DEVINFO_CACHE_TABLE_SQL.append(DEVINFO_CACHE_FREESIZE).append(" integer,");
		CREATE_DEVINFO_CACHE_TABLE_SQL.append(DEVINFO_CACHE_USEDSIZE).append(" integer,");
		CREATE_DEVINFO_CACHE_TABLE_SQL.append(DEVINFO_CACHE_SPEEDLIMIT).append(" integer,");
		CREATE_DEVINFO_CACHE_TABLE_SQL.append(DEVINFO_CACHE_UPLOADFLAG).append(" integer)")
				.append(TERMINATOR);
				
	}
}
