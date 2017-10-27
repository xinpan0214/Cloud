package com.gvtv.android.cloud.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gvtv.android.cloud.util.LogUtils;

public class DbHelper extends SQLiteOpenHelper {

    public DbHelper(Context context){
        super(context, DbConstants.DB_NAME, null, DbConstants.DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            db.execSQL(DbConstants.CREATE_APPINFO_CACHE_TABLE_SQL.toString());
            LogUtils.getLog(getClass()).verbose(DbConstants.CREATE_APPINFO_CACHE_TABLE_SQL.toString());
            db.execSQL(DbConstants.CREATE_USERINFO_CACHE_TABLE_SQL.toString());
            LogUtils.getLog(getClass()).verbose(DbConstants.CREATE_USERINFO_CACHE_TABLE_SQL.toString());
            db.execSQL(DbConstants.CREATE_DEVINFO_CACHE_TABLE_SQL.toString());
            LogUtils.getLog(getClass()).verbose(DbConstants.CREATE_DEVINFO_CACHE_TABLE_SQL.toString());
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
