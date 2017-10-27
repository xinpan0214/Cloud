package com.gvtv.android.cloud.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

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
import com.gvtv.android.cloud.util.AlertDialogUtil;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.PreferenceUtils;
import com.gvtv.android.cloud.util.ToastUtil;
import com.gvtv.android.cloud.view.TitleView;

public class DownloadSettingActivity extends BaseActivity implements OnClickListener, OnSeekBarChangeListener{

	private TitleView mTitleView;
	private Button btn_setting;

	private TextView tv_upload_speed;
	private TextView tv_upload_speed_tip;
	private TextView tv_download_speed;
	private TextView tv_download_speed_tip;
	private SeekBar seekbar_upload;
	private SeekBar seekbar_download;
	private int speedlimit;
	private int speedlimit_up;
	private boolean isSpeedlimitEnable;
	private ToggleButton toggle_speed;
	
	private DownloadSettingActivity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download_setting);
		instance = this;
		speedlimit = CloudApplication.bindedDeviceInfo.getSpeedlimit();
		if(speedlimit == -1){
			PreferenceUtils.setSpeedlimitEnable(instance, false);
		}
		if(speedlimit < 1024){
			speedlimit = PreferenceUtils.getSpeedlimit(instance);
		}
		speedlimit_up = PreferenceUtils.getSpeedlimit_up(instance);
		findViews();
		setListeners();
		isSpeedlimitEnable = PreferenceUtils.isSpeedlimitEnable(instance);
		if(isSpeedlimitEnable){
			toggle_speed.setChecked(true);
			checkedUI();
		}else{
			toggle_speed.setChecked(false);
			uncheckedUI();
		}
		CloudApplication.getInstance().addActivity(instance);
	}

	private void findViews(){
		mTitleView = (TitleView) findViewById(R.id.downloadsetting);
		btn_setting = (Button) findViewById(R.id.btn_restore_setting);
		tv_download_speed = (TextView) findViewById(R.id.tv_download_speed);
		tv_upload_speed = (TextView) findViewById(R.id.tv_upload_speed);
		seekbar_download = (SeekBar) findViewById(R.id.seekbar_download);
		seekbar_upload = (SeekBar) findViewById(R.id.seekbar_upload);
		seekbar_upload.setMax(50*1024);
		seekbar_download.setMax(900*1024);
		seekbar_download.setProgress(speedlimit);
		seekbar_upload.setProgress(speedlimit_up);
		tv_download_speed.setText(speedlimit/1024 + "KB/S");
		tv_upload_speed.setText(speedlimit_up/1024 + "KB/S");
		toggle_speed = (ToggleButton) findViewById(R.id.toggle_speed);
		tv_upload_speed_tip = (TextView) findViewById(R.id.tv_upload_speed_tip);
		tv_download_speed_tip = (TextView) findViewById(R.id.tv_download_speed_tip);
	}
	
	private void setListeners(){
		mTitleView.getBackTextView().setOnClickListener(this);
		btn_setting.setOnClickListener(this);
		seekbar_download.setOnSeekBarChangeListener(this);
		seekbar_upload.setOnSeekBarChangeListener(this);
		toggle_speed.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					checkedUI();
					isSpeedlimitEnable = true;
				}else{
					uncheckedUI();
					isSpeedlimitEnable = false;
					AlertDialogUtil.getAlertDialogUtil().showDialog(instance,
							instance.getResources().getString(R.string.download_speed_set_please_wait));
					try {
						MsgBean msgSend = new MsgBean();
						msgSend.setAction(MsgActionCode.SET_SPEED_LIMIT);
						String json_content = JsonUtils.buildSET_SPEED_LIMIT(-1);//设置限速为-1时，不限速
						msgSend.setJson_content(json_content);
						CloudApplication.sequence += 2;
						msgSend.setSequence(CloudApplication.sequence);
						msgSend.setVersion(9527);
						CloudApplication.requestID += 2;
						SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);
					} catch (Exception e) {
						AlertDialogUtil.getAlertDialogUtil().cancelDialog();
						LogUtils.getLog(getClass()).error(e.toString());
					}
				}
			}
		});
	}
	
	private void checkedUI(){
		btn_setting.setBackgroundResource(R.drawable.login_btn_selector);
		btn_setting.setEnabled(true);
		tv_download_speed_tip.setTextColor(getResources().getColor(R.color.email_color));
		tv_upload_speed_tip.setTextColor(getResources().getColor(R.color.email_color));
		seekbar_download.setEnabled(true);
		seekbar_upload.setEnabled(true);
		speedlimit = CloudApplication.bindedDeviceInfo.getSpeedlimit();
		if(speedlimit < 1024){
			speedlimit = PreferenceUtils.getSpeedlimit(instance);
		}
		seekbar_download.setProgress(speedlimit);
		seekbar_upload.setProgress(speedlimit_up);
		
//		
//		seekbar_download.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progressdrawable));
//		Rect bounds = seekbar_download.getProgressDrawable().getBounds();
//		
//		seekbar_download.getProgressDrawable().setBounds(bounds);
//		seekbar_download.setProgress(seekbar_download.getProgress() + 5);
//		//seekbar_download.setProgress(seekbar_download.getProgress() - 5);
	}
	
	private void uncheckedUI(){
		btn_setting.setBackgroundResource(R.drawable.public_btn09);
		btn_setting.setEnabled(false);
		tv_download_speed_tip.setTextColor(getResources().getColor(R.color.unable_color));
		tv_upload_speed_tip.setTextColor(getResources().getColor(R.color.unable_color));
		seekbar_download.setEnabled(false);
		seekbar_upload.setEnabled(false);
		speedlimit = CloudApplication.bindedDeviceInfo.getSpeedlimit();
		if(speedlimit < 1024){
			speedlimit = PreferenceUtils.getSpeedlimit(instance);
		}
		seekbar_download.setProgress(speedlimit);
		seekbar_upload.setProgress(speedlimit_up);
	}


	@Override
	
	public void onClick(View v) {
		if(mTitleView.getBackTextView() == v){
			this.finish();
		}else if(btn_setting == v){
			AlertDialogUtil.getAlertDialogUtil().showDialog(instance,
					instance.getResources().getString(R.string.download_speed_set_please_wait));
			try {
				MsgBean msgSend = new MsgBean();
				msgSend.setAction(MsgActionCode.SET_SPEED_LIMIT);
				String json_content = JsonUtils.buildSET_SPEED_LIMIT(speedlimit);
				msgSend.setJson_content(json_content);
				CloudApplication.sequence += 2;
				msgSend.setSequence(CloudApplication.sequence);
				msgSend.setVersion(9527);
				CloudApplication.requestID += 2;
				SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);
			} catch (Exception e) {
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
				LogUtils.getLog(getClass()).error(e.toString());
			}
		}
	}

	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if(seekBar == seekbar_download){
			speedlimit = progress;
			if(speedlimit < 1024){
				speedlimit = 1024;
			}
			tv_download_speed.setText(speedlimit/1024 + "KB/S");
		}else if(seekBar == seekbar_upload){
			speedlimit_up = progress;
			if(speedlimit_up < 1024){
				speedlimit_up = 1024;
			}
			tv_upload_speed.setText(speedlimit_up/1024 + "KB/S");
		}
	}


	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}
	
	@Override
	protected void onResume() {
		MessageReceiver.msghList.add(instance);
		super.onResume();
	}

	@Override
	protected void onPause() {
		for (int i = 0; i < MessageReceiver.msghList.size(); i++) {
			if(MessageReceiver.msghList.get(i) instanceof DownloadSettingActivity){
				MessageReceiver.msghList.remove(i);
				i--;
			}
		}
		super.onPause();
	}
	
	@Override
	public void onMessage(int type, int resp, MsgBean msg, byte[] restoreByte) {
		super.onMessage(type, resp, msg, restoreByte);
		switch (type) {
		case MsgTypeCode.COMMAND_FORWARDING_REQ:
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			if(resp == MsgResponseCode.OK){
				switch (msg.getAction()) {
				case MsgActionCode.SET_SPEED_LIMIT:
					String jsonStr = msg.getJson_content();
					TaskReturn taskRet = JsonUtils.parseSET_SPEED_LIMIT(jsonStr);
					if(taskRet.getRet() == MsgResponseCode.OK){
						if(isSpeedlimitEnable){
							PreferenceUtils.setSpeedlimit(instance, speedlimit);
							PreferenceUtils.setSpeedlimit_up(instance, speedlimit_up);
							CloudApplication.bindedDeviceInfo.setSpeedlimit(speedlimit);
							PreferenceUtils.setSpeedlimitEnable(instance, isSpeedlimitEnable);
						}else{
							PreferenceUtils.setSpeedlimitEnable(instance, isSpeedlimitEnable);
						}
						ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.speedset_success_tip));
					}else{
						speedlimit = CloudApplication.bindedDeviceInfo.getSpeedlimit();
						if(speedlimit < 1024){
							speedlimit = PreferenceUtils.getSpeedlimit(instance);
						}
						speedlimit_up = PreferenceUtils.getSpeedlimit_up(instance);
						seekbar_download.setProgress(speedlimit);
						seekbar_upload.setProgress(speedlimit_up);
						isSpeedlimitEnable = PreferenceUtils.isSpeedlimitEnable(instance);
						if(isSpeedlimitEnable){
							toggle_speed.setChecked(true);
						}else{
							toggle_speed.setChecked(false);
						}
						ToastUtil.getToastUtils().showToastByCode(instance, taskRet.getRet());
					}
					break;
					
				default:
					break;
				}
			}else{
				speedlimit = CloudApplication.bindedDeviceInfo.getSpeedlimit();
				if(speedlimit < 1024){
					speedlimit = PreferenceUtils.getSpeedlimit(instance);
				}
				speedlimit_up = PreferenceUtils.getSpeedlimit_up(instance);
				seekbar_download.setProgress(speedlimit);
				seekbar_upload.setProgress(speedlimit_up);
				
				isSpeedlimitEnable = PreferenceUtils.isSpeedlimitEnable(instance);
				if(isSpeedlimitEnable){
					toggle_speed.setChecked(true);
				}else{
					toggle_speed.setChecked(false);
				}
				ToastUtil.getToastUtils().showToastByCode(instance, resp);
			}
			break;
		default:
			break;
		}
	}

}
