package com.gvtv.android.cloud.broadcast;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.WindowManager;

import com.gvtv.android.cloud.AppConst;
import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.activity.MainActivity;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.msg.MsgTypeCode;
import com.gvtv.android.cloud.service.MessageService;
import com.gvtv.android.cloud.sockets.SocketClient;
import com.gvtv.android.cloud.util.AlertDialogUtil;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.NetWorkUtils;

public class MessageReceiver extends BroadcastReceiver {
	public static ArrayList<MsgHandler> msghList = new ArrayList<MsgHandler>();

	public interface MsgHandler {
		public abstract void onMessage(int type, int resp, MsgBean msg, byte[] restoreByte);

		public abstract void onNetChange(boolean isNetConnected);
	}

	@Override
	public void onReceive(final Context context, Intent intent) {
		if (intent.getAction().equalsIgnoreCase(AppConst.MSG_RECV_ACTION)) {
			int type = intent.getIntExtra(AppConst.RESP_TYPE, 0);
			int resp = intent.getIntExtra(AppConst.RESP_CODE, 0);
			MsgBean msg = intent.getParcelableExtra(AppConst.RESP_MSG);
			if(type == MsgTypeCode.ANOTHER_LOGIN_REQ){
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
				Intent tcpIntent = new Intent(context, MessageService.class);
				context.stopService(tcpIntent);
				SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).disconnect();
				ProgressDialog dialog = new ProgressDialog(context);
				dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
				dialog.setCancelable(false);
				dialog.setMessage(context.getResources().getString(R.string.other_login_tips));
				dialog.setTitle(context.getResources().getString(R.string.force_offline_tips));
				dialog.setButton(DialogInterface.BUTTON_NEUTRAL, context.getResources().getString(R.string.relogin_tips),new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						CloudApplication.getInstance().exit();
						Intent in = new Intent(context, MainActivity.class);
						in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						in.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						context.startActivity(in);
						dialog.cancel();
					}
				});
				
//				dialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.exit),new DialogInterface.OnClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						CloudApplication.getInstance().exit();
//						Intent in = new Intent(context, MainActivity.class);
//						in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//						in.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//						context.startActivity(in);
//						dialog.cancel();
//					}
//				});
				dialog.show();
			}else{
				for (MsgHandler iterable_element : msghList) {
					try {
						LogUtils.getLog(getClass()).verbose("No from: ");
						LogUtils.getLog(getClass()).verbose("onMessage from: " + iterable_element.getClass().getSimpleName());
						iterable_element.onMessage(type,resp, msg ,intent.getByteArrayExtra(AppConst.BACKUP_BYTE));
					} catch (Exception e) {
						LogUtils.getLog(getClass()).error(e.toString());
					}
				}
			}
			
		} else if (intent.getAction().equalsIgnoreCase("android.net.conn.CONNECTIVITY_CHANGE")) {
			for (int i = 0; i < msghList.size(); i++) {
				msghList.get(i).onNetChange(NetWorkUtils.isNetAvailable(context));
			}
		}
	}

}
