package com.gvtv.android.cloud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.broadcast.MessageReceiver;
import com.gvtv.android.cloud.msg.MessageUtils;
import com.gvtv.android.cloud.msg.MsgActionCode;
import com.gvtv.android.cloud.msg.MsgResponseCode;
import com.gvtv.android.cloud.msg.MsgTypeCode;
import com.gvtv.android.cloud.service.MessageService;
import com.gvtv.android.cloud.sockets.SendTcpMsgAsyncTask;
import com.gvtv.android.cloud.sockets.SocketClient;
import com.gvtv.android.cloud.sockets.SocketUtils;
import com.gvtv.android.cloud.util.AlertDialogUtil;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.PreferenceUtils;
import com.gvtv.android.cloud.util.RegularGrammarUtils;
import com.gvtv.android.cloud.util.StringUtils;
import com.gvtv.android.cloud.util.ToastUtil;
import com.gvtv.android.cloud.view.TitleView;

public class LoginActivity extends BaseActivity implements OnClickListener {
	private Class<?> clazz;
	private TitleView title;
	private EditText et_account;
	private EditText et_pwd;
	private Button btn_login;
	private TextView tv_forget;
	private String account;
	private String pwd;
	private LoginActivity instance;
	private int appAmount;//应用总数
	private int currPage;//当前页数
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		instance = this;
		clazz = getClass();
		findViews();
		setListeners();
		CloudApplication.getInstance().addActivity(this);
	}

	@Override
	protected void onDestroy() {
		MessageReceiver.msghList.remove(this);
		super.onDestroy();
	}

	private void findViews() {
		title = (TitleView) findViewById(R.id.loginTitle);
		et_account = (EditText) findViewById(R.id.accountEdit);
		et_pwd = (EditText) findViewById(R.id.secretEdit);
		btn_login = (Button) findViewById(R.id.loginBtn);
		tv_forget = (TextView) findViewById(R.id.forgetText);
		
		et_account.setText(PreferenceUtils.getUsername(instance));
		et_account.setSelection(et_account.getText().toString().length());
		et_pwd.setText(PreferenceUtils.getPassword(instance));
		et_pwd.setSelection(et_pwd.getText().toString().length());
	}

	private void setListeners() {
		title.getBackTextView().setOnClickListener(this);
		btn_login.setOnClickListener(this);
		tv_forget.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		try {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (v == title.getBackTextView()) {
			finish();
		} else if (v == btn_login) {
			SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).disconnect();
			Intent serviceintent = new Intent(getApplicationContext(), MessageService.class);
			stopService(serviceintent);
			
			
			account = et_account.getText().toString().trim();
			pwd = et_pwd.getText().toString().trim();
			if (StringUtils.isEmpty(account)) {
				ToastUtil.getToastUtils().showToast(instance,
						getResources().getString(R.string.please_input_account));
			} else if (StringUtils.isEmpty(pwd)) {
				ToastUtil.getToastUtils().showToast(instance,
						getResources().getString(R.string.please_input_pwd));
			} else if (!RegularGrammarUtils.isEmail(account)) {
				ToastUtil.getToastUtils().showToast(instance,
						getResources().getString(R.string.account_format_error));
			} else if (!RegularGrammarUtils.isPasswd(pwd)) {
				et_pwd.setText(null);
				ToastUtil.getToastUtils().showToast(instance,
						getResources().getString(R.string.pwd_format_error));
			} else {
				AlertDialogUtil.getAlertDialogUtil().showDialog(instance, getResources().getString(R.string.login_please_wait));
				CloudApplication.bindedDeviceInfo.init();
//				ServerAddress bizAddr = new ServerAddress();
//				bizAddr.setserverPort((short)(8082));
//				bizAddr.setServerUDPPort((short)(8080));
//				bizAddr.setserverIP("10.0.10.127");
//				CloudApplication.bizAddes.add(bizAddr);
				
				
				try {
					if(CloudApplication.bizAddes.size() > 0){
						CloudApplication.requestID += 2;
						SocketUtils.secret_key_req(instance, instance, CloudApplication.requestID);
					}else{
						if(CloudApplication.loadAddes.size() > 0){
							SocketUtils.controller_reg_loginreq(instance, account);
						}else{
							CloudApplication.requestID += 2;
							new SendTcpMsgAsyncTask(MessageUtils.build_controller_addr_req_msg(CloudApplication.requestID),
								SocketClient.CLIENT_ADDR, instance).send();
						}
					}
				} catch (Exception e) {
					LogUtils.getLog(clazz).error(e.toString());
				}
			}
		} else if (v == tv_forget) {
			Intent intent = new Intent(LoginActivity.this, FindPWDActivity.class);
			intent.putExtra("account", et_account.getText().toString().trim());
			startActivity(intent);
		}
	}
	
	@Override
	protected void onResume() {
		MessageReceiver.msghList.add(instance);
		super.onResume();
	}

	@Override
	protected void onPause() {
		for (int i = 0; i < MessageReceiver.msghList.size(); i++) {
			if(MessageReceiver.msghList.get(i) instanceof LoginActivity){
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
		case MsgTypeCode.CONTROLLER_LOGIN_REQ:
			if(resp == MsgResponseCode.OK){
				PreferenceUtils.saveLoginInfo(instance, account, pwd);
				PreferenceUtils.saveBizServer(instance, CloudApplication.bizAddes.get(0).getserverIP(), CloudApplication.bizAddes.get(0).getserverPort(), CloudApplication.bizAddes.get(0).getServerUDPPort());
				try {
					PreferenceUtils.saveLoadServer(CloudApplication.getInstance(), CloudApplication.loadAddes.get(0).getserverIP(), CloudApplication.loadAddes.get(0).getserverPort());
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				CloudApplication.user_pwd = pwd;
				CloudApplication.user_name = account;
				//answer_tcp_recover_over();
				appAmount = CloudApplication.bindedDeviceInfo.getApp_amount();
				if(appAmount > 0){
					currPage = 1;
					CloudApplication.bindedDeviceInfo.getAppids().clear();
					CloudApplication.requestID += 2;
					try {
						SocketUtils.appid_req(instance, currPage, CloudApplication.requestID);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					AlertDialogUtil.getAlertDialogUtil().cancelDialog();
					Intent serviceintent = new Intent(getApplicationContext(), MessageService.class);
					startService(serviceintent);
					if(CloudApplication.bindedDeviceInfo.getDev_name() == null || CloudApplication.bindedDeviceInfo.getDev_name().length() == 0){
						Intent intent = new Intent(getApplicationContext(), MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("isLogin", true);
						startActivity(intent);
					}else{
						CloudApplication.bindedDeviceInfo.setStatus(1);
						Intent intent = new Intent(getApplicationContext(), CloudManagerActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
				}
			}else{
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
				et_pwd.setText(null);
				SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).disconnect();
				ToastUtil.getToastUtils().showToastByCode(LoginActivity.this, resp);
			}
			break;
			
		case MsgTypeCode.SECRET_KEY_REQ:
			if(resp == MsgResponseCode.OK){
				try {
					CloudApplication.requestID += 2;
					SocketUtils.user_login_req(account, pwd, instance, CloudApplication.requestID);
				} catch (Exception e) {
					AlertDialogUtil.getAlertDialogUtil().cancelDialog();
					LogUtils.getLog(clazz).error(e.toString());
				}
			}else{
				SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).disconnect();
				ToastUtil.getToastUtils().showToastByCode(LoginActivity.this, resp);
			}
			break;
			
		case MsgTypeCode.CONTROLLER_REG_LOGIN_RESP://业务请求
			if(CloudApplication.bizAddes.size() > 0){
				try {
					SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).disconnect();
					Intent serviceintent = new Intent(getApplicationContext(), MessageService.class);
					stopService(serviceintent);
					CloudApplication.requestID += 2;
					SocketUtils.secret_key_req(instance, instance, CloudApplication.requestID);
				} catch (Exception e) {
					LogUtils.getLog(clazz).error(e.toString());
				}
			}else{
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			}
			break;
			
		case MsgTypeCode.USER_APPID_REQ://查询所有已安装应用的appid
			if(resp == MsgResponseCode.OK){
				if(appAmount > CloudApplication.bindedDeviceInfo.getAppids().size()){
					currPage ++;
					CloudApplication.requestID += 2;
					try {
						SocketUtils.appid_req(instance, currPage, CloudApplication.requestID);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					Intent serviceintent = new Intent(getApplicationContext(), MessageService.class);
					startService(serviceintent);
					if(CloudApplication.bindedDeviceInfo.getDev_name() == null || CloudApplication.bindedDeviceInfo.getDev_name().length() == 0){
						Intent intent = new Intent(getApplicationContext(), MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.putExtra("isLogin", true);
						startActivity(intent);
					}else{
						CloudApplication.bindedDeviceInfo.setStatus(1);
						Intent intent = new Intent(getApplicationContext(), CloudManagerActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
					}
					AlertDialogUtil.getAlertDialogUtil().cancelDialog();
				}
			}else{
				Intent serviceintent = new Intent(getApplicationContext(), MessageService.class);
				startService(serviceintent);
				if(CloudApplication.bindedDeviceInfo.getDev_name() == null || CloudApplication.bindedDeviceInfo.getDev_name().length() == 0){
					Intent intent = new Intent(getApplicationContext(), MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("isLogin", true);
					startActivity(intent);
				}else{
					CloudApplication.bindedDeviceInfo.setStatus(1);
					Intent intent = new Intent(getApplicationContext(), CloudManagerActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			}
			break;
		case MsgTypeCode.CONTROLLER_ADDR_RESP:
			if(CloudApplication.loadAddes.size() > 0){
				try {
					SocketUtils.controller_reg_loginreq(instance, account);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			}
			break;
		default:
			break;
		}
	}

	public void answer_tcp_recover_over(){
		try {
			MsgBean msgSend = new MsgBean();
			msgSend.setAction(MsgActionCode.RECOVER_OVER);
			msgSend.setRet(200);
			msgSend.setVersion(9527);
			CloudApplication.requestID += 2;
			SocketUtils.command_forwarding_req(msgSend, instance,
					CloudApplication.requestID);
		} catch (Exception e) {
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			LogUtils.getLog(getClass()).error(e.toString());
		}
	}
	
}
