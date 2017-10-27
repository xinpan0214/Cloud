package com.gvtv.android.cloud.provider;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

import com.gvtv.android.cloud.db.DbConstants;
import com.gvtv.android.cloud.util.LogUtils;

public class AppContentObserver extends ContentObserver {
    private Handler mHandler ;   //更新UI线程  
      
    public AppContentObserver(Context context,Handler handler) {  
        super(handler);  
        mHandler = handler ;  
    }  
    /** 
     * 当所监听的Uri发生改变时，就会回调此方法 
     *  
     * @param selfChange  此值意义不大 一般情况下该回调值false 
     */  
    @Override  
    public void onChange(boolean selfChange){
    	LogUtils.getLog(getClass()).verbose("apptable======onChange=====");
            mHandler.sendEmptyMessage(DbConstants.APPTABLE_CHANGED);          
        }  
    }  

