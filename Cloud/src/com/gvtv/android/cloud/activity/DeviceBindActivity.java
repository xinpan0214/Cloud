package com.gvtv.android.cloud.activity;

import java.io.UnsupportedEncodingException;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.broadcast.MessageReceiver;
import com.gvtv.android.cloud.msg.JsonUtils;
import com.gvtv.android.cloud.msg.MsgActionCode;
import com.gvtv.android.cloud.msg.MsgResponseCode;
import com.gvtv.android.cloud.msg.MsgTypeCode;
import com.gvtv.android.cloud.sockets.SocketUtils;
import com.gvtv.android.cloud.util.AlertDialogUtil;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.RegularGrammarUtils;
import com.gvtv.android.cloud.util.ToastUtil;
import com.gvtv.android.cloud.view.InputDialog;
import com.gvtv.android.cloud.view.InputDialog.DialogListener;
import com.gvtv.android.cloud.view.TitleView;

public class DeviceBindActivity extends BaseActivity implements OnClickListener {

	private TitleView mTitleView;
	private Button mModifyNameButton;
	private Button mUnbindButton;
	private TextView accountText;
	private TextView deviceNameText;
	private TextView deviceStateText;
	private InputDialog unbindDialog;
	private InputDialog modName;
	private String newName;
	private String temp_name;

	private DeviceBindActivity instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_bind);
		instance = this;
		findViews();
		setListensers();
		CloudApplication.getInstance().addActivity(instance);
	}

	private void findViews() {
		mTitleView = (TitleView) findViewById(R.id.bindDeviceTitle);
		mModifyNameButton = (Button) findViewById(R.id.modifyDeviceBtn);
		mUnbindButton = (Button) findViewById(R.id.unbindBtn);
		accountText = (TextView) findViewById(R.id.accountText);
		deviceNameText = (TextView) findViewById(R.id.deviceNameText);
		deviceStateText = (TextView) findViewById(R.id.deviceStateText);

		if (CloudApplication.bindedDeviceInfo.getStatus() == 1) {
			deviceStateText.setText(getResources().getString(R.string.device_state_binded));
		} else {
			deviceStateText.setText(getResources().getString(R.string.device_state_unbinded));
		}
		accountText.setText(getString(R.string.account_link) + CloudApplication.user_name);
		deviceNameText.setText(getString(R.string.device_name_link)
				+ CloudApplication.bindedDeviceInfo.getDev_name());
		// deviceNameText.setText(getString(R.string.device_name_link) +
		// PreferenceUtils.getDeviceName(instance));
	}

	private void setListensers() {
		mTitleView.getBackTextView().setOnClickListener(this);
		mModifyNameButton.setOnClickListener(this);
		mUnbindButton.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		MessageReceiver.msghList.add(instance);
		super.onResume();
	}

	@Override
	protected void onPause() {
		for (int i = 0; i < MessageReceiver.msghList.size(); i++) {
			if (MessageReceiver.msghList.get(i) instanceof DeviceBindActivity) {
				MessageReceiver.msghList.remove(i);
				i--;
			}
		}
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		if (v == mTitleView.getBackTextView()) {
			finish();
		} else if (v == mUnbindButton) {
			mUnbindButton.setEnabled(false);
			unbindDialog = InputDialog.getInstance(instance, InputDialog.TYPE_UNBIND);
			unbindDialog.show();
			unbindDialog.setCancelable(false);
			unbindDialog.getTv_name().setText(
					getString(R.string.account_link) + CloudApplication.user_name);
			unbindDialog.getEt().setText(null);
			unbindDialog.getTv_unbind().setText(getResources().getString(R.string.unbind));
			unbindDialog.getEt().setHint(getResources().getString(R.string.please_input_cur_pwd));
			unbindDialog.getEt().setInputType(
					InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
			unbindDialog.setListener(new DialogListener() {

				@Override
				public void onDevnameChange(boolean isChanged, String dev_name) {
					if (isChanged) {
						if (dev_name.equalsIgnoreCase(CloudApplication.user_pwd)) {
							try {
								AlertDialogUtil.getAlertDialogUtil().showDialog(instance,
										instance.getResources().getString(R.string.unbind_please_wait));
								CloudApplication.requestID += 2;
								SocketUtils.unbind_device_req(CloudApplication.bindedDeviceInfo,
										CloudApplication.user_name, instance,
										CloudApplication.requestID);
								if (unbindDialog != null) {
									unbindDialog.cancel();
								}
							} catch (Exception e) {
								LogUtils.getLog(getClass()).error(e.toString());
								unbindDialog.cancel();
							}
						} else {
							ToastUtil.getToastUtils().showToast(instance,
									getResources().getString(R.string.error_secret));
						}
					} else {
						unbindDialog.cancel();
					}
				}
			});

			mUnbindButton.setEnabled(true);

		} else if (v == mModifyNameButton) {
			mModifyNameButton.setEnabled(false);
			modName = InputDialog.getInstance(instance, InputDialog.TYPE_MODNAME);
			modName.show();
			modName.setCancelable(false);
			modName.getEt().setText(null);
			modName.getTv_name().setText(
					getString(R.string.account_link) + CloudApplication.user_name);
			modName.getTv_unbind().setText(getResources().getString(R.string.modname));
			modName.getEt().setHint(getResources().getString(R.string.please_input_cur_devname));
			modName.getEt().setInputType(InputType.TYPE_CLASS_TEXT);
			
			TextWatcher mTextWatcher = new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					int bytelen = 0;
					temp_name = s.toString();
					try {
						bytelen = temp_name.getBytes("UTF-8").length;
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if (bytelen > 30) {
						ToastUtil.getToastUtils().showToast(
								instance,
								instance.getResources().getString(
										R.string.please_not_loanger_than_30_char));
						for (int i = 10; i < 30; i++) {
							String temp = temp_name.substring(0, i);
							int l = 0;
							try {
								l = temp.getBytes("UTF-8").length;
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							if( l > 30){
								temp = temp_name.substring(0, i-1);
								modName.getEt().setText(temp);
								modName.getEt().setSelection(temp.length());
								break;
							}
						}
					}
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			};

			modName.getEt().addTextChangedListener(mTextWatcher);
			modName.setListener(new DialogListener() {

				@Override
				public void onDevnameChange(boolean isChanged, String dev_name) {
					int len = 0;
					try {
						len = dev_name.toString().getBytes("UTF-8").length;
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					if (isChanged) {
						if(len > 30){
							 ToastUtil.getToastUtils().showToast(instance, instance.getResources().getString(R.string.please_not_loanger_than_30_char));
						}else if(!RegularGrammarUtils.isDeviceName(dev_name)){
							modName.getEt().setText(null);
							ToastUtil.getToastUtils().showToast(instance, instance.getResources().getString(R.string.contains_illegal_characters));
						}else{
							newName = dev_name;
							try {
								AlertDialogUtil.getAlertDialogUtil().showDialog(instance,
										instance.getResources().getString(R.string.modify_device_name_please_wait));
								MsgBean msgSend = new MsgBean();
								msgSend.setAction(MsgActionCode.CHANGE_DEVNAME);
								String json_content = JsonUtils.buildCHANGE_DEVNAME(dev_name);
								msgSend.setJson_content(json_content);
								CloudApplication.sequence += 2;
								msgSend.setSequence(CloudApplication.sequence);
								msgSend.setVersion(9527);
								CloudApplication.requestID += 2;
								SocketUtils.command_forwarding_req(msgSend, instance,
										CloudApplication.requestID);
								if (modName != null) {
									modName.cancel();
								}
								
							} catch (Exception e) {
								LogUtils.getLog(DeviceBindActivity.class).error(e.toString());
								modName.cancel();
							}
						}
					} else {
						modName.cancel();
					}
				}
			});

			mModifyNameButton.setEnabled(true);
		}
	}

	@Override
	public void onMessage(int type, int resp, MsgBean msg, byte[] restoreByte) {
		super.onMessage(type, resp, msg, restoreByte);
		switch (type) {
		case MsgTypeCode.DEVICE_UNBIND_REQ:// 解绑设备
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			if (resp == MsgResponseCode.OK) {
				Intent intent = new Intent(DeviceBindActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("isLogin", true);
				startActivity(intent);
			} else {
				ToastUtil.getToastUtils().showToastByCode(DeviceBindActivity.this, resp);
			}
			break;

		case MsgTypeCode.COMMAND_FORWARDING_REQ:
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			if (resp == MsgResponseCode.OK) {
				if (msg.getRet() == MsgResponseCode.OK) {
					CloudApplication.bindedDeviceInfo.setDev_name(newName);
					deviceNameText.setText(getString(R.string.device_name_link)
							+ CloudApplication.bindedDeviceInfo.getDev_name());
				} else {
					ToastUtil.getToastUtils().showToastByCode(instance, resp);
				}
			} else {
				ToastUtil.getToastUtils().showToastByCode(instance, resp);
			}
			break;
		default:
			break;
		}
	}

}
