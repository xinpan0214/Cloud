package com.gvtv.android.cloud.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gvtv.android.cloud.bean.AppInfo;
import com.gvtv.android.cloud.bean.DeviceInfo;

public class SqliteUtils {

	private static volatile SqliteUtils instance;

	private DbHelper dbHelper;
	private SQLiteDatabase wDb;
	private Context context;

	private SqliteUtils() {
	}
	
	public static SqliteUtils getInstance(Context context) {
		if (instance == null) {
			synchronized (SqliteUtils.class) {
				if (instance == null) {
					instance = new SqliteUtils();
				}
			}
		}
		instance.context = context;
		return instance;
	}

	public SQLiteDatabase getWDb() {
		return wDb;
	}
	
	public void openDb(){
		dbHelper = new DbHelper(context);
		wDb = dbHelper.getWritableDatabase();
	}
	
	public void closeDb(){
		if(wDb.isOpen()){
			wDb.close();
		}
		if(dbHelper != null){
			dbHelper.close();
		}
	}
	
	public boolean insertApp(AppInfo app, String username){
		openDb();
		ContentValues values = new ContentValues();
		values.put(DbConstants.APPINFO_CACHE_APPCODE, app.getAppcode());
		values.put(DbConstants.APPINFO_CACHE_APPNAME, app.getAppname());
		values.put(DbConstants.APPINFO_CACHE_PKGNAME, app.getPkgname());
		values.put(DbConstants.APPINFO_CACHE_ICON, app.getIcon());
		values.put(DbConstants.APPINFO_CACHE_IMAGE0, app.getImage0());
		values.put(DbConstants.APPINFO_CACHE_IMAGE1, app.getImage1());
		values.put(DbConstants.APPINFO_CACHE_IMAGE2, app.getImage2());
		values.put(DbConstants.APPINFO_CACHE_IMAGE3, app.getImage3());
		values.put(DbConstants.APPINFO_CACHE_IMAGE4, app.getImage4());
		values.put(DbConstants.APPINFO_CACHE_VERSION, app.getVersion());
		values.put(DbConstants.APPINFO_CACHE_SHOWVERSION, app.getShowversion());
		values.put(DbConstants.APPINFO_CACHE_SIZE, app.getSize());
		values.put(DbConstants.APPINFO_CACHE_PRICE, app.getPrice());
		values.put(DbConstants.APPINFO_CACHE_DEVELOPER, app.getDeveloper());
		values.put(DbConstants.APPINFO_CACHE_SUMMARY, app.getSummary());
		values.put(DbConstants.APPINFO_CACHE_CLASSIFYCODE, app.getClassifycode());
		values.put(DbConstants.APPINFO_CACHE_MD5, app.getMd5());
		values.put(DbConstants.APPINFO_CACHE_DOWNLOADURL, app.getDownloadurl());
		values.put(DbConstants.APPINFO_CACHE_USERNAME, username);
		long ret = wDb.insert(DbConstants.APPINFO_CACHE_TABLE_NAME, null, values);
		closeDb();
		if(ret > 0){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean insertMultiApp(List<AppInfo> user_apps, String username){
		long ret = 0;
		openDb();
		for (AppInfo app : user_apps) {
			ContentValues values = new ContentValues();
			values.put(DbConstants.APPINFO_CACHE_APPCODE, app.getAppcode());
			values.put(DbConstants.APPINFO_CACHE_APPNAME, app.getAppname());
			values.put(DbConstants.APPINFO_CACHE_PKGNAME, app.getPkgname());
			values.put(DbConstants.APPINFO_CACHE_ICON, app.getIcon());
			values.put(DbConstants.APPINFO_CACHE_IMAGE0, app.getImage0());
			values.put(DbConstants.APPINFO_CACHE_IMAGE1, app.getImage1());
			values.put(DbConstants.APPINFO_CACHE_IMAGE2, app.getImage2());
			values.put(DbConstants.APPINFO_CACHE_IMAGE3, app.getImage3());
			values.put(DbConstants.APPINFO_CACHE_IMAGE4, app.getImage4());
			values.put(DbConstants.APPINFO_CACHE_VERSION, app.getVersion());
			values.put(DbConstants.APPINFO_CACHE_SHOWVERSION, app.getShowversion());
			values.put(DbConstants.APPINFO_CACHE_SIZE, app.getSize());
			values.put(DbConstants.APPINFO_CACHE_PRICE, app.getPrice());
			values.put(DbConstants.APPINFO_CACHE_DEVELOPER, app.getDeveloper());
			values.put(DbConstants.APPINFO_CACHE_SUMMARY, app.getSummary());
			values.put(DbConstants.APPINFO_CACHE_CLASSIFYCODE, app.getClassifycode());
			values.put(DbConstants.APPINFO_CACHE_MD5, app.getMd5());
			values.put(DbConstants.APPINFO_CACHE_DOWNLOADURL, app.getDownloadurl());
			values.put(DbConstants.APPINFO_CACHE_USERNAME, username);
			ret += wDb.insert(DbConstants.APPINFO_CACHE_TABLE_NAME, null, values);
		}
		closeDb();
		if(ret > 0){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean insertDev(DeviceInfo dev, String username){
		openDb();
		ContentValues values = new ContentValues();
		values.put(DbConstants.DEVINFO_CACHE_ACCESSCODE, dev.getAccess_code());
		
		long ret = wDb.insert(DbConstants.APPINFO_CACHE_TABLE_NAME, null, values);
		closeDb();
		if(ret > 0){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean deleteAppByAppcode(String appCode, String username){
		openDb();
		String[] whereArgs = {appCode,username};
		String whereClause = DbConstants.APPINFO_CACHE_APPCODE + "=?" + " and " + DbConstants.APPINFO_CACHE_USERNAME + "=?";
		long ret = wDb.delete(DbConstants.APPINFO_CACHE_TABLE_NAME, whereClause, whereArgs);
		closeDb();
		if(ret > 0){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean deleteAllAppByUser(String username){
		openDb();
		String[] whereArgs = {username};
		String whereClause = DbConstants.APPINFO_CACHE_USERNAME + "=?";
		long ret = wDb.delete(DbConstants.APPINFO_CACHE_TABLE_NAME, whereClause, whereArgs);
		closeDb();
		if(ret > 0){
			return true;
		}else{
			return false;
		}
	}
	
	
	public boolean refreshAppByLoginInfo(List<String> appids, String username){
		int ret = 0;
		openDb();
		String  where = DbConstants.APPINFO_CACHE_USERNAME + "=\"" + username + "\"";
		Cursor cursor = wDb.query(DbConstants.APPINFO_CACHE_TABLE_NAME, null, where, null, null, null, null);
		ArrayList<AppInfo> list = cursor2AppInfoList(cursor);
		cursor.close();
		for (int i = 0; i < list.size(); i++) {
			int index = -1;
			for (int j = 0; j < appids.size(); j++) {
				if(list.get(i).getAppcode().equals(appids.get(j))){
					index = j;
					appids.remove(j);
					break;
				}
			}
			if(index == -1){//删除登录里不存在的
				String[] whereArgs = {list.get(i).getAppcode(),username};
				String whereClause = DbConstants.APPINFO_CACHE_APPCODE + "=?" + " and " + DbConstants.APPINFO_CACHE_USERNAME + "=?";
				ret += wDb.delete(DbConstants.APPINFO_CACHE_TABLE_NAME, whereClause, whereArgs);
				list.remove(i);
				i--;
			}
		}
		//wDb.beginTransaction();
		//插入数据库没有的
		for (String appid : appids) {
			ContentValues values = new ContentValues();
			values.put(DbConstants.APPINFO_CACHE_APPCODE, appid);
			values.put(DbConstants.APPINFO_CACHE_USERNAME, username);
			ret += wDb.insert(DbConstants.APPINFO_CACHE_TABLE_NAME, null, values);
		}
		//wDb.endTransaction();
		
		closeDb();
		if(ret > 0){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean refreshAppByUpdate(List<AppInfo> mAppList, String username){
		int ret = 0;
		openDb();
		String  where = DbConstants.APPINFO_CACHE_USERNAME + "=\"" + username + "\"";
		Cursor cursor = wDb.query(DbConstants.APPINFO_CACHE_TABLE_NAME, null, where, null, null, null, null);
		ArrayList<AppInfo> list = cursor2AppInfoList(cursor);
		cursor.close();
		for (int i = 0; i < list.size(); i++) {
			AppInfo app = list.get(i);
			for (int j = 0; j < mAppList.size(); j++) {
				AppInfo appupdate = mAppList.get(j);
				if(app.getAppcode().equals(appupdate.getAppcode())){
					if(app.getAppname() == null || !app.getAppname().equals(appupdate.getAppname())){
						ContentValues values = new ContentValues();
						values.put(DbConstants.APPINFO_CACHE_APPCODE, appupdate.getAppcode());
						values.put(DbConstants.APPINFO_CACHE_APPNAME, appupdate.getAppname());
						values.put(DbConstants.APPINFO_CACHE_PKGNAME, appupdate.getPkgname());
						values.put(DbConstants.APPINFO_CACHE_ICON, appupdate.getIcon());
						values.put(DbConstants.APPINFO_CACHE_IMAGE0, appupdate.getImage0());
						values.put(DbConstants.APPINFO_CACHE_IMAGE1, appupdate.getImage1());
						values.put(DbConstants.APPINFO_CACHE_IMAGE2, appupdate.getImage2());
						values.put(DbConstants.APPINFO_CACHE_IMAGE3, appupdate.getImage3());
						values.put(DbConstants.APPINFO_CACHE_IMAGE4, appupdate.getImage4());
						values.put(DbConstants.APPINFO_CACHE_VERSION, appupdate.getVersion());
						values.put(DbConstants.APPINFO_CACHE_SHOWVERSION, appupdate.getShowversion());
						values.put(DbConstants.APPINFO_CACHE_SIZE, appupdate.getSize());
						values.put(DbConstants.APPINFO_CACHE_PRICE, appupdate.getPrice());
						values.put(DbConstants.APPINFO_CACHE_DEVELOPER, appupdate.getDeveloper());
						values.put(DbConstants.APPINFO_CACHE_SUMMARY, appupdate.getSummary());
						values.put(DbConstants.APPINFO_CACHE_CLASSIFYCODE, appupdate.getClassifycode());
						values.put(DbConstants.APPINFO_CACHE_MD5, appupdate.getMd5());
						values.put(DbConstants.APPINFO_CACHE_DOWNLOADURL, appupdate.getDownloadurl());
						values.put(DbConstants.APPINFO_CACHE_USERNAME, username);
						String[] whereArgs = {app.getAppcode(),username};
						String whereClause = DbConstants.APPINFO_CACHE_APPCODE + "=?" + " and " + DbConstants.APPINFO_CACHE_USERNAME + "=?";
						ret += wDb.update(DbConstants.APPINFO_CACHE_TABLE_NAME, values, whereClause, whereArgs);
					}
					break;
				}
			}
		}
		closeDb();
		if(ret > 0){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean upgradeAppIcon(AppInfo app, String username){
		openDb();
		ContentValues values = new ContentValues();
		values.put(DbConstants.APPINFO_CACHE_ICON, app.getIcon());
		String[] whereArgs = {app.getAppcode(),username};
		String whereClause = DbConstants.APPINFO_CACHE_APPCODE + "=?" + " and " + DbConstants.APPINFO_CACHE_USERNAME + "=?";
		long ret = wDb.update(DbConstants.APPINFO_CACHE_TABLE_NAME, values, whereClause, whereArgs);
		closeDb();
		if(ret > 0){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean upgradeApp(AppInfo app, String username){
		openDb();
		ContentValues values = new ContentValues();
		values.put(DbConstants.APPINFO_CACHE_APPCODE, app.getAppcode());
		values.put(DbConstants.APPINFO_CACHE_APPNAME, app.getAppname());
		values.put(DbConstants.APPINFO_CACHE_PKGNAME, app.getPkgname());
		values.put(DbConstants.APPINFO_CACHE_ICON, app.getIcon());
		values.put(DbConstants.APPINFO_CACHE_IMAGE0, app.getImage0());
		values.put(DbConstants.APPINFO_CACHE_IMAGE1, app.getImage1());
		values.put(DbConstants.APPINFO_CACHE_IMAGE2, app.getImage2());
		values.put(DbConstants.APPINFO_CACHE_IMAGE3, app.getImage3());
		values.put(DbConstants.APPINFO_CACHE_IMAGE4, app.getImage4());
		values.put(DbConstants.APPINFO_CACHE_VERSION, app.getVersion());
		values.put(DbConstants.APPINFO_CACHE_SHOWVERSION, app.getShowversion());
		values.put(DbConstants.APPINFO_CACHE_SIZE, app.getSize());
		values.put(DbConstants.APPINFO_CACHE_PRICE, app.getPrice());
		values.put(DbConstants.APPINFO_CACHE_DEVELOPER, app.getDeveloper());
		values.put(DbConstants.APPINFO_CACHE_SUMMARY, app.getSummary());
		values.put(DbConstants.APPINFO_CACHE_CLASSIFYCODE, app.getClassifycode());
		values.put(DbConstants.APPINFO_CACHE_MD5, app.getMd5());
		values.put(DbConstants.APPINFO_CACHE_DOWNLOADURL, app.getDownloadurl());
		values.put(DbConstants.APPINFO_CACHE_USERNAME, username);
		String[] whereArgs = {app.getAppcode(),username};
		String whereClause = DbConstants.APPINFO_CACHE_APPCODE + "=?" + " and " + DbConstants.APPINFO_CACHE_USERNAME + "=?";
		long ret = wDb.update(DbConstants.APPINFO_CACHE_TABLE_NAME, values, whereClause, whereArgs);
		closeDb();
		if(ret > 0){
			return true;
		}else{
			return false;
		}
	}
	
	
	/* 获取特定用户的应用数据*/
	public ArrayList<AppInfo> queryAllApp(String username) {
		openDb();
		String  where = DbConstants.APPINFO_CACHE_USERNAME + "=\"" + username + "\"";
		Cursor cursor = wDb.query(DbConstants.APPINFO_CACHE_TABLE_NAME, null, where, null, null, null, null);
		ArrayList<AppInfo> list = cursor2AppInfoList(cursor);
		cursor.close();
		closeDb();
		return list;

	}
	
	/* 获取特定用户的应用数据*/
	public ArrayList<String> queryAllAppid(String username) {
		openDb();
		String  where = DbConstants.APPINFO_CACHE_USERNAME + "=\"" + username + "\"";
		Cursor cursor = wDb.query(DbConstants.APPINFO_CACHE_TABLE_NAME, null, where, null, null, null, null);
		ArrayList<String> list = cursor2AppidList(cursor);
		cursor.close();
		closeDb();
		return list;

	}
	
	/* 获取特定用户的应用数据,有名字的应用*/
	public ArrayList<AppInfo> queryAllAppWithAppName(String username) {
		openDb();
		String  where = DbConstants.APPINFO_CACHE_USERNAME + "=\"" + username + "\"";
		Cursor cursor = wDb.query(DbConstants.APPINFO_CACHE_TABLE_NAME, null, where, null, null, null, null);
		ArrayList<AppInfo> list = cursor2AppInfoListWithName(cursor);
		cursor.close();
		closeDb();
		return list;

	}
	
	/* 应用游标转换为集合 */
	private ArrayList<String> cursor2AppidList(Cursor cursor) {
		ArrayList<String> list = new ArrayList<String>();
		while (cursor.moveToNext()) {
			list.add(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_APPCODE)));
		}
		return list;
	}
	
	/* 应用游标转换为集合 */
	private ArrayList<AppInfo> cursor2AppInfoList(Cursor cursor) {
		ArrayList<AppInfo> list = new ArrayList<AppInfo>();
		/* 有记录 */
		AppInfo app ;
		while (cursor.moveToNext()) {
			app = new AppInfo();
			app.setAppcode(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_APPCODE)));
			app.setAppname(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_APPNAME)));
			app.setPkgname(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_PKGNAME)));
			app.setIcon(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_ICON)));
			app.setImage0(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE0)));
			app.setImage1(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE1)));
			app.setImage2(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE2)));
			app.setImage3(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE3)));
			app.setImage4(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE4)));
			app.setVersion(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_VERSION)));
			app.setShowversion(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_SHOWVERSION)));
			app.setSize(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_SIZE)));
			app.setPrice(cursor.getFloat(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_PRICE)));
			app.setDeveloper(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_DEVELOPER)));
			app.setSummary(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_SUMMARY)));
			app.setClassifycode(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_CLASSIFYCODE)));
			app.setMd5(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_MD5)));
			app.setDownloadurl(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_DOWNLOADURL)));
			list.add(app);
		}
		return list;
	}
	
	/* 应用游标转换为集合，排除没有应用名的应用 */
	private ArrayList<AppInfo> cursor2AppInfoListWithName(Cursor cursor) {
		ArrayList<AppInfo> list = new ArrayList<AppInfo>();
		/* 有记录 */
		AppInfo app ;
		while (cursor.moveToNext()) {
			String name = cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_APPNAME));
			if(name != null && name.trim().length() > 0){
				app = new AppInfo();
				app.setAppcode(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_APPCODE)));
				app.setAppname(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_APPNAME)));
				app.setPkgname(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_PKGNAME)));
				app.setIcon(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_ICON)));
				app.setImage0(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE0)));
				app.setImage1(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE1)));
				app.setImage2(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE2)));
				app.setImage3(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE3)));
				app.setImage4(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE4)));
				app.setVersion(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_VERSION)));
				app.setShowversion(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_SHOWVERSION)));
				app.setSize(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_SIZE)));
				app.setPrice(cursor.getFloat(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_PRICE)));
				app.setDeveloper(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_DEVELOPER)));
				app.setSummary(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_SUMMARY)));
				app.setClassifycode(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_CLASSIFYCODE)));
				app.setMd5(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_MD5)));
				app.setDownloadurl(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_DOWNLOADURL)));
				list.add(app);
			}
		}
		return list;
	}
	
	
	/* 查询特定应用*/
	public AppInfo queryAppByCodeAndUsername(String appcode, String username) {
		AppInfo app = null;
		openDb();
		String  where = DbConstants.APPINFO_CACHE_APPCODE + "=\"" + appcode + "\""
				+ " and " + DbConstants.APPINFO_CACHE_USERNAME + "=\"" + username + "\"";
		Cursor cursor = wDb.query(DbConstants.APPINFO_CACHE_TABLE_NAME, null, where, null, null, null, null);
		if(cursor != null && cursor.getCount() > 0){
			cursor.moveToFirst();
			app = new AppInfo();
			app.setAppcode(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_APPCODE)));
			app.setAppname(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_APPNAME)));
			app.setPkgname(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_PKGNAME)));
			app.setIcon(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_ICON)));
			app.setImage0(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE0)));
			app.setImage1(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE1)));
			app.setImage2(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE2)));
			app.setImage3(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE3)));
			app.setImage4(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE4)));
			app.setVersion(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_VERSION)));
			app.setShowversion(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_SHOWVERSION)));
			app.setSize(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_SIZE)));
			app.setPrice(cursor.getFloat(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_PRICE)));
			app.setDeveloper(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_DEVELOPER)));
			app.setSummary(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_SUMMARY)));
			app.setClassifycode(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_CLASSIFYCODE)));
			app.setMd5(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_MD5)));
			app.setDownloadurl(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_DOWNLOADURL)));
		}
		cursor.close();
		closeDb();
		return app;
	}
	
	/* 查询特定应用*/
	public AppInfo queryAppByCode(String appcode) {
		AppInfo app = null;
		openDb();
		String  where = DbConstants.APPINFO_CACHE_APPCODE + "=\"" + appcode + "\"";
		Cursor cursor = wDb.query(DbConstants.APPINFO_CACHE_TABLE_NAME, null, where, null, null, null, null);
		if(cursor != null && cursor.getCount() > 0){
			cursor.moveToFirst();
			app = new AppInfo();
			app.setAppcode(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_APPCODE)));
			app.setAppname(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_APPNAME)));
			app.setPkgname(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_PKGNAME)));
			app.setIcon(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_ICON)));
			app.setImage0(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE0)));
			app.setImage1(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE1)));
			app.setImage2(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE2)));
			app.setImage3(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE3)));
			app.setImage4(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_IMAGE4)));
			app.setVersion(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_VERSION)));
			app.setShowversion(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_SHOWVERSION)));
			app.setSize(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_SIZE)));
			app.setPrice(cursor.getFloat(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_PRICE)));
			app.setDeveloper(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_DEVELOPER)));
			app.setSummary(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_SUMMARY)));
			app.setClassifycode(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_CLASSIFYCODE)));
			app.setMd5(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_MD5)));
			app.setDownloadurl(cursor.getString(cursor.getColumnIndex(DbConstants.APPINFO_CACHE_DOWNLOADURL)));
		}
		cursor.close();
		closeDb();
		return app;
	}
}
