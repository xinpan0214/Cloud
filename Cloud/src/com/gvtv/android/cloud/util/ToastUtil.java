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
import com.gvtv.android.cloud.msg.MsgResponseCode;

import android.content.Context;
import android.widget.Toast;


public class ToastUtil
{
	private static ToastUtil mToastUtil = new ToastUtil();
	private static Toast toast = null;
	
	public static ToastUtil getToastUtils()
	{
		if (mToastUtil == null)
		{
			mToastUtil = new ToastUtil();
		}
		return mToastUtil;
	}
	
	public void showToast(Context context,String text){
		if(toast == null){
			toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		}else{
			toast.setText(text);
		}
		toast.show();
	}
	
	public void showToastByCode(Context context, int code){
		String text =null;
		switch (code) {
		case MsgResponseCode.SYSTEM_ERROR://300
			text = context.getResources().getString(R.string.system_error);
			break;
			
		case MsgResponseCode.PARAMETER_WRONG://301
			text = context.getResources().getString(R.string.parameter_wrong);
			break;
			
		case MsgResponseCode.NO_ACCESS://302
			text = context.getResources().getString(R.string.no_access);
			break;
	
		case MsgResponseCode.CODE_EXIST://303
			text = context.getResources().getString(R.string.system_error);
			break;
	
		case MsgResponseCode.USER_NOT_EXIST://401
			text = context.getResources().getString(R.string.user_not_exist);
			break;
	
		case MsgResponseCode.ACCOUNT_OR_PASSWORD_ERROR://402
			text = context.getResources().getString(R.string.account_or_password_error);
			break;
	
		case MsgResponseCode.ACCOUNT_DISABLE://403
			text = context.getResources().getString(R.string.account_disable);
			break;
	
		case MsgResponseCode.EMAIL_EXIST://407
			text = context.getResources().getString(R.string.email_exist);
			break;
	
		case MsgResponseCode.OLD_PASSWORD_ERROR://408
			text = context.getResources().getString(R.string.old_password_error);
			break;
	
		case MsgResponseCode.INVALID_BUSINESS://409
			text = context.getResources().getString(R.string.invalid_business);
			break;
	
		case MsgResponseCode.INVALID_FRIEND://410
			text = context.getResources().getString(R.string.invalid_friend);
			break;
	
		case MsgResponseCode.MODIFIED_ACCOUNT_NOT_MATCH_CHECK_CODE://412
			text = context.getResources().getString(R.string.modified_account_not_match_check_code);
			break;
	
		case MsgResponseCode.CHANGE_PASSWORD_TIME_EXPIRED://413
			text = context.getResources().getString(R.string.change_password_time_expired);
			break;
	
		case MsgResponseCode.PASSWORD_MODIFIED://414
			text = context.getResources().getString(R.string.password_modified);
			break;
	
		case MsgResponseCode.CHECK_CODE_ERROR://415
			text = context.getResources().getString(R.string.system_error);
			break;
	
		case MsgResponseCode.ACTIVATED_FAILED://416
			text = context.getResources().getString(R.string.activated_failed);
			break;
	
		case MsgResponseCode.ACTIVATED_TIME_EXPIRED://417
			text = context.getResources().getString(R.string.activated_time_expired);
			break;
	
		case MsgResponseCode.ACTIVATED://418
			text = context.getResources().getString(R.string.activated);
			break;
	
		case MsgResponseCode.NO_REPONSE://419
			text = context.getResources().getString(R.string.no_reponse);
			break;
	
		case MsgResponseCode.FRIEND_EXIST://420
			text = context.getResources().getString(R.string.friend_exist);
			break;
	
		case MsgResponseCode.FRIENDSHIP_NOT_EXIST://421
			text = context.getResources().getString(R.string.friendship_not_exist);
			break;
	
		case MsgResponseCode.ACCOUNT_BINDED://422
			text = context.getResources().getString(R.string.account_binded);
			break;
	
		case MsgResponseCode.INVALID_CODE://423
			text = context.getResources().getString(R.string.invalid_code);
			break;
	
		case MsgResponseCode.DEVICE_EXIST://424
			text = context.getResources().getString(R.string.device_exist);
			break;
	
		case MsgResponseCode.USER_EXIST_NOT_ACTIVATED://425
			text = context.getResources().getString(R.string.user_exist_not_activated);
			break;
			
		case MsgResponseCode.RSA_ERROR://501
			text = context.getResources().getString(R.string.rsa_error);
			break;
			
		case MsgResponseCode.AES_ERROR://502
			text = context.getResources().getString(R.string.system_error);
			break;
			
		case MsgResponseCode.AES_SESSIONKEY_TOO_LONG://503
			text = context.getResources().getString(R.string.system_error);
			break;
			
		case MsgResponseCode.NO_SESSIONKEY://504
			text = context.getResources().getString(R.string.no_sessionkey);
			break;
			
		case MsgResponseCode.DEVICE_LOGIN_FAILED://505
			text = context.getResources().getString(R.string.device_login_failed);
			break;
			
		case MsgResponseCode.OTHER_END_OFFLINE://506
			text = context.getResources().getString(R.string.other_end_offline);
			break;
	
		case MsgResponseCode.OTHER_END_SEND_FAILED://507
			text = context.getResources().getString(R.string.other_end_send_failed);
			break;
	
		case MsgResponseCode.NO_ACCESS_TO_DEVICE_UDP_PORT://508
			text = context.getResources().getString(R.string.no_access_to_device_udp_port);
			break;
		case MsgResponseCode.NO_ACCESS_TO_CONTROL_UDP_PORT://509
			text = context.getResources().getString(R.string.no_access_to_control_udp_port);
			break;
			
		case MsgResponseCode.OTHER_CONTROLER_LOGIN://510
			text = context.getResources().getString(R.string.other_controler_login);
			break;
			
		case MsgResponseCode.SESSIONKEY_REPEAT://511
			text = context.getResources().getString(R.string.system_error);
			break;
		case MsgResponseCode.LOGIN_REPEAT://512
			text = context.getResources().getString(R.string.system_error);
			break;
			
		case MsgResponseCode.DEVICE_AUTHENTICATION_FAILURE://513
			text = context.getResources().getString(R.string.device_authentication_failure);
			break;
			
		case MsgResponseCode.DEVICE_AUTHENTICATION_EXPIRING://514
			text = context.getResources().getString(R.string.device_authentication_expiring);
			break;
			
		case MsgResponseCode.DEVICE_RENEWAL_EXPIRE://515
			text = context.getResources().getString(R.string.device_renewal_expire);
			break;
			
		case MsgResponseCode.DEVICE_OUTOFSERVICE://516
			text = context.getResources().getString(R.string.device_outofservice);
			break;
	
		case MsgResponseCode.REMOVE_DOWNLOAD_FAILED://601
			text = context.getResources().getString(R.string.remove_download_failed);
			break;
	
		case MsgResponseCode.ADD_DOWNLOAD_FAILED://602
			text = context.getResources().getString(R.string.add_fail);
			break;
	
		case MsgResponseCode.ADD_DOWNLOAD_FAILED_BUT_EXIST://603
			text = context.getResources().getString(R.string.add_fail);
			break;
	
		case MsgResponseCode.PAUSE_DOWNLOAD_FAILED_NO_TASK://604
			text = context.getResources().getString(R.string.pause_download_failed_no_task);
			break;
			
		case MsgResponseCode.PAUSE_DOWNLOAD_FAILED_UNKNOWN_CAUSE://606
			text = context.getResources().getString(R.string.pause_download_failed_unknown_cause);
			break;
			
		case MsgResponseCode.RESUME_DOWNLOAD_FAILED_NO_TASK://607
			text = context.getResources().getString(R.string.resume_download_failed_no_task);
			break;
			
		case MsgResponseCode.ADD_DOWNLOAD_FAILED_DISC_FULL://609
			text = context.getResources().getString(R.string.disc_full);
			break;
			
		case MsgResponseCode.DEL_DOWNLOAD_FAILED_NO_TASK://610
			text = context.getResources().getString(R.string.delete_fail);
			break;
			
		case MsgResponseCode.DEL_DOWNLOAD_FAILED_UNKNOWN_CAUSE://611
			text = context.getResources().getString(R.string.delete_fail);
			break;
			
		case MsgResponseCode.ADD_DOWNLOAD_FAILED_BUT_FINISHED://613
			text = context.getResources().getString(R.string.delete_fail);
			break;
			
		case MsgResponseCode.COMMAND_EXECUTE_FAILED://616
			text = context.getResources().getString(R.string.msg_send_fail);
			break;
			
		case MsgResponseCode.DEL_RESFILE_FAILED://617
			text = context.getResources().getString(R.string.del_resfile_failed);
			break;
			
		case MsgResponseCode.DEVICE_AUTH_FAILED://618
			text = context.getResources().getString(R.string.device_auth_failed);
			break;
			
		case MsgResponseCode.NO_FILENAME://652
			text = context.getResources().getString(R.string.system_error);
			break;
			
		case MsgResponseCode.NO_FILESIZE://653
			text = context.getResources().getString(R.string.system_error);
			break;
			
		case MsgResponseCode.NO_DISK://666
			text = context.getResources().getString(R.string.no_disc);
			break;
			
		case MsgResponseCode.DISK_NOT_FORMATTED://667
			text = context.getResources().getString(R.string.no_formatting);
			break;
			
			
		default:
			return;
		}
		
		if(toast == null){
			toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		}else{
			toast.setText(text);
		}
		if(text != null){
			toast.show();
		}
	}
}