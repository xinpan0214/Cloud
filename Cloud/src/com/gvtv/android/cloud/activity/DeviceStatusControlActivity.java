package com.gvtv.android.cloud.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.bean.TaskReturn;
import com.gvtv.android.cloud.broadcast.MessageReceiver;
import com.gvtv.android.cloud.msg.JsonUtils;
import com.gvtv.android.cloud.msg.MsgActionCode;
import com.gvtv.android.cloud.msg.MsgResponseCode;
import com.gvtv.android.cloud.msg.MsgTypeCode;
import com.gvtv.android.cloud.sockets.SocketUtils;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.PreferenceUtils;
import com.gvtv.android.cloud.util.ToastUtil;
import com.gvtv.android.cloud.view.PercentBar;
import com.gvtv.android.cloud.view.TitleView;

@SuppressLint("HandlerLeak")
public class DeviceStatusControlActivity extends BaseActivity implements OnClickListener{

	private TitleView mTitleView;
	private Button mSolidUpdateButton;
	private Button mRestartButton;
	private Button mRecoveryButton;

	private TextView mAllSpaceTextView;
	private TextView mUseSpaceTextView;
	private TextView mAvailSpaceTextView;
	private PercentBar mPercentView;
	private ImageView per_default;
	
	private int total, avail, used;
	private ProgressDialog dialog;
	
	private int progress;
	private DeviceStatusControlActivity instance;
	////升级标识 升级标识：0无升级;1有升级;2下载中；3可安装
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case 0:
				dialog.setProgress(progress);
				if(progress == 100){
					////升级标识 升级标识：0无升级;1有升级;2下载中；3可安装
					CloudApplication.bindedDeviceInfo.setUpload_flag(3);
					dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
					dialog.getButton(DialogInterface.BUTTON_POSITIVE).requestFocus();
				}
				break;
				
			case 1:
				if(CloudApplication.bindedDeviceInfo.getUpload_flag() == 2){
				////升级标识 升级标识：0无升级;1有升级;2下载中；3可安装
					upload_dev();
				}
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_status_control);
		instance = this;
		findViews();
		setListeners();
		total = CloudApplication.bindedDeviceInfo.getTotal_size();
		avail = CloudApplication.bindedDeviceInfo.getFree_size();
		refreshPercent();
		CloudApplication.getInstance().addActivity(instance);
		MessageReceiver.msghList.add(instance);
	}
	
	@Override
	protected void onResume() {
		LogUtils.getLog(getClass()).error("onResume");	
		query_disk();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if(dialog != null){
			dialog.cancel();
		}
		for (int i = 0; i < MessageReceiver.msghList.size(); i++) {
			if(MessageReceiver.msghList.get(i) instanceof DeviceStatusControlActivity){
				MessageReceiver.msghList.remove(i);
				i--;
			}
		}
		super.onDestroy();
	}
	
	private void findViews(){
		mTitleView = (TitleView) findViewById(R.id.deviceControlTitle);
		mSolidUpdateButton = (Button) findViewById(R.id.solidUpdateBtn);
		mRestartButton = (Button) findViewById(R.id.restartBtn);
		mRecoveryButton = (Button) findViewById(R.id.recoveryBtn);
		mAllSpaceTextView = (TextView) findViewById(R.id.allSizeText);
		mUseSpaceTextView = (TextView) findViewById(R.id.usedSizeText);
		mAvailSpaceTextView = (TextView) findViewById(R.id.availableSizeText);
		mPercentView = (PercentBar) findViewById(R.id.percentView);
		per_default = (ImageView) findViewById(R.id.per_default);
		per_default.setVisibility(View.VISIBLE);
	}
	
	private void setListeners(){
		mTitleView.getBackTextView().setOnClickListener(instance);
		mSolidUpdateButton.setOnClickListener(instance);
		mRestartButton.setOnClickListener(instance);
		mRecoveryButton.setOnClickListener(instance);
	}

	@Override
	public void onClick(View v) {
		if(v == mTitleView.getBackTextView()){
			finish();
		}else if(v == mRestartButton){
			Intent intent = new Intent(instance, DeviceRebootActivity.class);
			startActivity(intent);
		}else if(v == mRecoveryButton){
			Builder dialog = new AlertDialog.Builder(instance);
			dialog.setTitle(getResources().getString(R.string.recovery));
			dialog.setCancelable(false);
			dialog.setPositiveButton(getResources().getString(R.string.recover), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					ctl_reback();
				}
			});
			dialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});
			dialog.show();
		}else if(v == mSolidUpdateButton){
			query_devinfo();
		}
	}

	
	@Override
	public void onMessage(int type, int resp, MsgBean msg, byte[] restoreByte) {
		super.onMessage(type, resp, msg, restoreByte);
		switch (type) {
		case MsgTypeCode.COMMAND_FORWARDING_REQ:
			String jsonStr;
			TaskReturn taskRet;
			if(resp == MsgResponseCode.OK){
				if(msg.getRet()  == MsgResponseCode.OK){
					switch (msg.getAction()) {
					case MsgActionCode.QUERY_DISK:
						jsonStr = msg.getJson_content();
						taskRet = JsonUtils.parseQUERY_DISK(jsonStr);
						if(taskRet.getRet() == MsgResponseCode.OK){
							total = taskRet.getTotal();
							avail = taskRet.getFree();
							used = taskRet.getUsed();
						
							CloudApplication.bindedDeviceInfo.setTotal_size(total);
							CloudApplication.bindedDeviceInfo.setFree_size(avail);
							CloudApplication.bindedDeviceInfo.setUsed_size(used);
							refreshPercent();
						}else{
							//ToastUtil.getToastUtils().showToastByCode(instance, taskRet.getRet());
						}
						break;
						
					case MsgActionCode.UPLOAD_DEV:
						//ToastUtil.getToastUtils().showToast(instance, "===========");
						jsonStr = msg.getJson_content();
						taskRet = JsonUtils.parseUPLOAD_DEV(jsonStr);
						if(taskRet.getRet() == MsgResponseCode.OK){
							CloudApplication.bindedDeviceInfo.setUpload_flag(2);//正在下载
							//ToastUtil.getToastUtils().showToast(instance, "===========1111111");
							showDialog();
							mHandler.sendEmptyMessageDelayed(1, 1000);
						}else{
							ToastUtil.getToastUtils().showToastByCode(instance, taskRet.getRet());
						}
						break;
						
					case MsgActionCode.QUERY_UPLOAD_STATE:
						mHandler.sendEmptyMessageDelayed(1, 1000);
						jsonStr = msg.getJson_content();
						taskRet = JsonUtils.parseQUERY_UPLOAD_STATE(jsonStr);
						if(taskRet.getRet() == MsgResponseCode.OK){
							progress = taskRet.getState();
							mHandler.sendEmptyMessage(0);
						}else{
							ToastUtil.getToastUtils().showToastByCode(instance, taskRet.getRet());
						}
						break;
						
					case MsgActionCode.CTL_REBACK:
						jsonStr = msg.getJson_content();
						taskRet = JsonUtils.parseCTL_REBACK(jsonStr);
						if(taskRet.getRet() == MsgResponseCode.OK){
							CloudApplication.bindedDeviceInfo.setSpeedlimit(-1);
							ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.recovery_tips));
						}else{
							ToastUtil.getToastUtils().showToastByCode(instance, taskRet.getRet());
						}
						break;
						
					case MsgActionCode.INSTALL_UPLOAD:
						jsonStr = msg.getJson_content();
						taskRet = JsonUtils.parseINSTALL_UPLOAD(jsonStr);
						if(taskRet.getRet() == MsgResponseCode.OK){
							CloudApplication.bindedDeviceInfo.setUpload_flag(0);//安装成功
							if(dialog != null){
								dialog.cancel();
							}
							ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.reboot_tips));
						}else{
							ToastUtil.getToastUtils().showToastByCode(instance, taskRet.getRet());
						}
						break;
						
					case MsgActionCode.CANCEL_UPLOAD:
						jsonStr = msg.getJson_content();
						taskRet = JsonUtils.parseCANCEL_UPLOAD(jsonStr);
						if(taskRet.getRet() == MsgResponseCode.OK){
							////升级标识 升级标识：0无升级;1有升级;2下载中；3可安装
							CloudApplication.bindedDeviceInfo.setUpload_flag(1);//取消
							if(dialog != null){
								dialog.cancel();
							}
						}else{
							ToastUtil.getToastUtils().showToastByCode(instance, taskRet.getRet());
						}
						break;
						
					case MsgActionCode.QUERY_DEVINFO:
						jsonStr = msg.getJson_content();
						taskRet = JsonUtils.parseQUERY_DEVINFO(jsonStr);
						if(taskRet.getRet() == MsgResponseCode.OK){
							CloudApplication.bindedDeviceInfo.setDev_name(taskRet.getDev_name());
							CloudApplication.bindedDeviceInfo.setAccess_code(taskRet.getDevcode());
							CloudApplication.bindedDeviceInfo.setFree_size(taskRet.getFree());
							CloudApplication.bindedDeviceInfo.setTotal_size(taskRet.getTotal());
							CloudApplication.bindedDeviceInfo.setUsed_size(taskRet.getUsed());
							CloudApplication.bindedDeviceInfo.setSpeedlimit(taskRet.getSpeedlimit());
							CloudApplication.bindedDeviceInfo.setUpload_flag(taskRet.getUpload_flag());
							PreferenceUtils.setSpeedlimit(instance, taskRet.getSpeedlimit());	
							tryToUpload();
						}else{
							ToastUtil.getToastUtils().showToastByCode(instance, taskRet.getRet());
						}
						break;


					default:
						break;
					}
					
				}else{
					ToastUtil.getToastUtils().showToastByCode(instance, msg.getRet());
				}
			}else{
				ToastUtil.getToastUtils().showToastByCode(instance, resp);
			}
			break;
		default:
			break;
		}
	}

	
	@Override
	public void onNetChange(boolean isNetConnected) {
		if(!isNetConnected){
			ToastUtil.getToastUtils().showToast(instance, instance.getResources().getString(R.string.network_not_available));
		}
	}
	
	private void refreshPercent(){
		if(total == 0){
			per_default.setVisibility(View.VISIBLE);
			mPercentView.setVisibility(View.INVISIBLE);
		}else{
			per_default.setVisibility(View.INVISIBLE);
			mPercentView.setVisibility(View.VISIBLE);
			mAllSpaceTextView.setText(getString(R.string.all_size) + total + "G");
			mUseSpaceTextView.setText(getString(R.string.used_size) + (total - avail) + "G");
			mAvailSpaceTextView.setText(getString(R.string.available_size) + avail + "G");
			mPercentView.setMax(total);
			mPercentView.setAvailableSpaceProgress(avail);
			mPercentView.setUseSpaceProgress(total - avail);
		}
	}
	
	private void showDialog(){
		if(dialog != null && dialog.isShowing()){
			return;
		}
		dialog = new ProgressDialog(instance);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setIndeterminate(false);
		dialog.setMax(100);
		dialog.setProgress(0);
		dialog.setTitle(getResources().getString(R.string.solid_update));
		dialog.setCancelable(false);
		dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel),new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					MsgBean msgSend = new MsgBean();
					msgSend.setAction(MsgActionCode.CANCEL_UPLOAD);
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
		
		dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.install),new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				try {
					MsgBean msgSend = new MsgBean();
					msgSend.setAction(MsgActionCode.INSTALL_UPLOAD);
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
		dialog.show();
		dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(false);
		dialog.getButton(DialogInterface.BUTTON_NEGATIVE).requestFocus();
	}
	
	private void tryToUpload(){
		if(CloudApplication.bindedDeviceInfo.getUpload_flag() == 1 ){
			////升级标识 升级标识：0无升级;1有升级;2下载中；3可安装
			try {
				MsgBean msgSend = new MsgBean();
				msgSend.setAction(MsgActionCode.UPLOAD_DEV);
				CloudApplication.sequence += 2;
				msgSend.setSequence(CloudApplication.sequence);
				msgSend.setVersion(9527);
				CloudApplication.requestID += 2;
				SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);
			} catch (Exception e) {
				LogUtils.getLog(getClass()).error(e.toString());
			}
		}else if(CloudApplication.bindedDeviceInfo.getUpload_flag() == 0){
				ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.already_last_version));
		}else if(CloudApplication.bindedDeviceInfo.getUpload_flag() == 2){
				showDialog();
				mHandler.sendEmptyMessage(1);
				ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.downloading));
		}else if(CloudApplication.bindedDeviceInfo.getUpload_flag() == 3){
				showDialog();
				dialog.setProgress(100);
				dialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(true);
				dialog.getButton(DialogInterface.BUTTON_POSITIVE).requestFocus();
				ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.can_be_installed));
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
	
	private void ctl_reback(){
		try {
			MsgBean msgSend = new MsgBean();
			msgSend.setAction(MsgActionCode.CTL_REBACK);
			CloudApplication.sequence += 2;
			msgSend.setSequence(CloudApplication.sequence);
			msgSend.setVersion(9527);
			CloudApplication.requestID += 2;
			SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);
			//mRecoveryButton.setEnabled(false);
		} catch (Exception e) {
			LogUtils.getLog(getClass()).error(e.toString());
		}
	}
	
	private void query_devinfo(){
		try {
			MsgBean msgSend = new MsgBean();
			msgSend.setAction(MsgActionCode.QUERY_DEVINFO);
			CloudApplication.sequence ++;
			msgSend.setSequence(CloudApplication.sequence);
			msgSend.setVersion(9527);
			CloudApplication.requestID += 2;
			SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);
			//mSolidUpdateButton.setEnabled(false);
		} catch (Exception e) {
			LogUtils.getLog(getClass()).error(e.toString());
		}
	}
	
	private void upload_dev(){
		try {
			MsgBean msgSend = new MsgBean();
			msgSend.setAction(MsgActionCode.QUERY_UPLOAD_STATE);
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
