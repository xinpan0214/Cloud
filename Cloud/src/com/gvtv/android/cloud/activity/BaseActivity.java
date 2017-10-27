package com.gvtv.android.cloud.activity;

import android.app.Activity;
import android.os.Bundle;

import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.broadcast.MessageReceiver.MsgHandler;
import com.gvtv.android.cloud.msg.MsgTypeCode;
import com.gvtv.android.cloud.sockets.SendTcpMsgAsyncTask.OnSendendListener;
import com.gvtv.android.cloud.util.AlertDialogUtil;
import com.gvtv.android.cloud.util.NetWorkUtils;
import com.gvtv.android.cloud.util.ToastUtil;

public class BaseActivity extends Activity implements MsgHandler , OnSendendListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public void onMessage(int type, int resp, MsgBean msg, byte[] restoreByte) {
		if(type == MsgTypeCode.TIME_OUT){
			AlertDialogUtil.getAlertDialogUtil().setTimeout_amount(AlertDialogUtil.getAlertDialogUtil().getTimeout_amount() + 1);
			if(AlertDialogUtil.getAlertDialogUtil().getTimeout_amount() == 2){
				AlertDialogUtil.getAlertDialogUtil().cancelDialogWhenTimeout();
			}
		}else{
			AlertDialogUtil.getAlertDialogUtil().setTimeout_amount(0);
		}
	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		if(!isNetConnected){
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			ToastUtil.getToastUtils().showToast(this, this.getResources().getString(R.string.network_not_available));
		}
	}

	@Override
	public void isSendend() {
		AlertDialogUtil.getAlertDialogUtil().cancelDialog();
		if(!NetWorkUtils.isNetAvailable(this)){
			ToastUtil.getToastUtils().showToast(this, this.getResources().getString(R.string.network_not_available));
		}else{
			ToastUtil.getToastUtils().showToast(this, this.getResources().getString(R.string.server_not_available));
		}
	};
	
	@Override
	protected void onDestroy() {
		CloudApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}
}
