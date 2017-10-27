package com.gvtv.android.cloud.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.broadcast.MessageReceiver;
import com.gvtv.android.cloud.msg.MsgActionCode;
import com.gvtv.android.cloud.msg.MsgResponseCode;
import com.gvtv.android.cloud.msg.MsgTypeCode;
import com.gvtv.android.cloud.sockets.SocketUtils;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.ToastUtil;
import com.gvtv.android.cloud.view.ProgressWithText;
import com.gvtv.android.cloud.view.TitleView;

@SuppressLint("HandlerLeak")
public class DeviceRebootActivity extends BaseActivity implements Runnable{

	private TitleView restartDeviceTitle;
	private DeviceRebootActivity instance;
	private ProgressWithText progress;
	private int ratio;
	private boolean flag;
	private TextView tv_restart;
	private Handler mhandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			progress.setProgress(ratio);
			if(ratio == 50){
				query_disk();
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reboot_device);
		
		instance = this;
		tv_restart = (TextView) findViewById(R.id.tv_restart);
		restartDeviceTitle = (TitleView) findViewById(R.id.restartDeviceTitle);
		progress = (ProgressWithText) findViewById(R.id.restartProBar);
		restartDeviceTitle.getBackTextView().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		tv_restart.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					MsgBean msgSend = new MsgBean();
					msgSend.setAction(MsgActionCode.REMOTE_REBOOT);
					CloudApplication.sequence += 2;
					msgSend.setSequence(CloudApplication.sequence);
					msgSend.setVersion(9527);
					CloudApplication.requestID += 2;
					SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);
				} catch (Exception e) {
					LogUtils.getLog(getClass()).error(e.toString());
				}
			}
		});
		CloudApplication.getInstance().addActivity(instance);
		MessageReceiver.msghList.add(instance);
		flag = true;
		ratio = 0;
		progress.setProgress(ratio);
		tv_restart.setText(getResources().getString(R.string.restart_device));
		tv_restart.setEnabled(true);
		tv_restart.setBackgroundResource(R.drawable.login_btn_selector);
	}
	@Override
	public void run() {
		while (flag) {
			if(ratio < 100){
				ratio ++;	
			}else{
				ratio = 10;
			}
			mhandler.sendEmptyMessage(0);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		for (int i = 0; i < MessageReceiver.msghList.size(); i++) {
			if(MessageReceiver.msghList.get(i) instanceof DeviceRebootActivity){
				MessageReceiver.msghList.remove(i);
				i--;
			}
		}
		flag = false;
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		
		super.onPause();
	}
	
	@Override
	public void onMessage(int type, int resp, MsgBean msg, byte[] restoreByte) {
		super.onMessage(type, resp, msg, restoreByte);
		switch (type) {
		case MsgTypeCode.COMMAND_FORWARDING_REQ:
			if(resp == MsgResponseCode.OK){
				if(msg.getAction() == MsgActionCode.REMOTE_REBOOT){
					tv_restart.setEnabled(false);
					tv_restart.setBackgroundResource(R.color.transparent);
					ratio = 10;
					tv_restart.setText(getResources().getString(R.string.restartting_device_tip));
					new Thread(this).start();
				}else if(msg.getAction() == MsgActionCode.QUERY_DISK){
					flag = true;
					ratio = 0;
					progress.setProgress(ratio);
					tv_restart.setText(getResources().getString(R.string.restart_device));
					tv_restart.setEnabled(true);
					tv_restart.setBackgroundResource(R.drawable.login_btn_selector);
				}
			}else{
				if(msg.getAction() == MsgActionCode.REMOTE_REBOOT){
					ToastUtil.getToastUtils().showToastByCode(instance, resp);
				}else if(msg.getAction() == MsgActionCode.QUERY_DISK){
					//
				}
			}
			break;
			
		default:
			break;
		}
	}


	private void query_disk(){
		try {
			MsgBean msgSend = new MsgBean();
			msgSend.setAction(MsgActionCode.QUERY_DISK);
			CloudApplication.sequence += 2;
			msgSend.setSequence(CloudApplication.sequence);
			msgSend.setVersion(9527);
			CloudApplication.requestID += 2;
			SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);
		} catch (Exception e) {
			LogUtils.getLog(getClass()).error(e.toString());
		}
	}
}
