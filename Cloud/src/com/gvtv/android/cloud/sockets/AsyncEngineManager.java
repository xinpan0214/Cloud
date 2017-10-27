/**
 * Copyright © 2013GreatVision. All rights reserved.
 *
 * @Title: AsyncEngineManager.java
 * @Prject: BananaTvLauncher
 * @Package: com.gvtv.launcher.dataprovider.http
 * @Description: HTTP引擎管理类
 * @author: 李英英 
 * @date: 2013年9月16日 下午4:32:18
 * @version: V1.0
 */

package com.gvtv.android.cloud.sockets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.AsyncTask;

@SuppressWarnings("rawtypes")
public class AsyncEngineManager {
	
	private final static List<AsyncTask> engineList = new ArrayList<AsyncTask>();

	public static void cancelAll() {
		Iterator<AsyncTask> it = engineList.iterator();
		List<AsyncTask> delList = new ArrayList<AsyncTask>();//用来装需要删除的元素
		while (it.hasNext()) {
			AsyncTask engine = it.next();
			engine.cancel(Boolean.TRUE);
			delList.add(engine);
		}
		engineList.removeAll(delList);
	}
	
	public static void addTask(AsyncTask mSendAsyncTask){
		engineList.add(mSendAsyncTask);
	}

}
