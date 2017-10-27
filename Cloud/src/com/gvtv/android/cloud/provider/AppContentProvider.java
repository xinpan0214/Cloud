package com.gvtv.android.cloud.provider;

import com.gvtv.android.cloud.db.DbConstants;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;


public class AppContentProvider extends ContentProvider {
	private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		MATCHER.addURI(DbConstants.PROVIDER_URL, DbConstants.APPINFO_CACHE_TABLE_NAME, DbConstants.APP_LIST);
		MATCHER.addURI(DbConstants.PROVIDER_URL, DbConstants.APPINFO_CACHE_TABLE_NAME + "/#", DbConstants.APP_DETAIL);
	}
	@Override
	public boolean onCreate() {
		return false;
	}
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		switch (MATCHER.match(uri)) {
		case DbConstants.APP_LIST:
			return null;
		case DbConstants.APP_DETAIL:
			
			return null;
		default:
			throw new IllegalArgumentException("unknow uri"+uri.toString());
		}
	}

	@Override
	public String getType(Uri uri) {
		switch (MATCHER.match(uri)) {
		case DbConstants.APP_LIST:
			return null;
		case DbConstants.APP_DETAIL:
			
			return null;
		default:
			throw new IllegalArgumentException("unknow uri"+uri.toString());
		}	
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri insertUri=null;
		switch (MATCHER.match(uri)) {
		case DbConstants.APP_LIST:
			return insertUri;
		case DbConstants.APP_DETAIL:
			
			return insertUri;
		default:
			throw new IllegalArgumentException("unknow uri"+uri.toString());
		}
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		switch (MATCHER.match(uri)) {
		case DbConstants.APP_LIST:
			return 0;
		case DbConstants.APP_DETAIL:
			
			return 0;
		default:
			throw new IllegalArgumentException("unknow uri"+uri.toString());
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		switch (MATCHER.match(uri)) {
		case DbConstants.APP_LIST:
			return 0;
		case DbConstants.APP_DETAIL:
			
			return 0;
		default:
			throw new IllegalArgumentException("unknow uri"+uri.toString());
		}
	}
}
