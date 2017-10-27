/**
 * 
 * Copyright © 2013GreatVision. All rights reserved.
 *
 * @Title: ShowToast.java
 * @Prject: BananaTvLauncher
 * @Package: com.bananatv.custom.launcher.view
 * @Description: 显示Toast
 * @author: zhaoqy
 * @date: 2013-11-18 上午11:33:18
 * @version: V1.0
 */

package com.gvtv.android.cloud.util;
import com.gvtv.android.cloud.R;

import android.app.ProgressDialog;
import android.content.Context;


public class AlertDialogUtil{
	private static AlertDialogUtil mToastUtil = new AlertDialogUtil();
	private ProgressDialog dialog;
	private int timeout_amount;
	
	/**
	 * @return the timeout_amount
	 */
	public int getTimeout_amount() {
		return timeout_amount;
	}

	/**
	 * @param timeout_amount the timeout_amount to set
	 */
	public void setTimeout_amount(int timeout_amount) {
		this.timeout_amount = timeout_amount;
	}

	public ProgressDialog getDialog() {
		return dialog;
	}

	public static AlertDialogUtil getAlertDialogUtil()
	{
		if (mToastUtil == null)
		{
			mToastUtil = new AlertDialogUtil();
		}
		return mToastUtil;
	}
	
	public void showDialog(Context context, CharSequence message){
		cancelDialog();
		dialog = ProgressDialog.show(context, null, message);
		dialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
		setDialogCancelable();
		timeout_amount = 0;
	}
	
	public void showDialogWithoutCancelListener(Context context, CharSequence message){
		cancelDialog();
		dialog = ProgressDialog.show(context, null, message);
		dialog.setProgressStyle(android.R.attr.progressBarStyleSmall);
		setDialogCancelable();
		timeout_amount = 0;
	}
	
	public void setDialogCancelable(){
		if(dialog != null){
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(false);
		}
	}
	
	public void setDialogMessage(String message){
		if(dialog != null){
			dialog.setMessage(message);
		}
	}
	
	public void setDialogTitle(String title){
		if(dialog != null){
			dialog.setTitle(title);
		}
	}
	
	public void cancelDialog(){
		if(dialog != null){
			dialog.cancel();
			dialog = null;
		}
		timeout_amount = 0;
	}
	
	public void cancelDialogWhenTimeout(){
		if(dialog != null){
			ToastUtil.getToastUtils().showToast(dialog.getContext(), dialog.getContext().getResources().getString(R.string.time_out));
			dialog.cancel();
			dialog = null;
		}
		timeout_amount = 0;
	}
	
}