package com.gvtv.android.cloud.activity;

import java.io.UnsupportedEncodingException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.gvtv.android.cloud.AppConst;
import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.broadcast.MessageReceiver;
import com.gvtv.android.cloud.msg.MessageUtils;
import com.gvtv.android.cloud.msg.MsgResponseCode;
import com.gvtv.android.cloud.msg.MsgTypeCode;
import com.gvtv.android.cloud.sockets.SendTcpMsgAsyncTask;
import com.gvtv.android.cloud.sockets.SocketClient;
import com.gvtv.android.cloud.sockets.SocketUtils;
import com.gvtv.android.cloud.util.AlertDialogUtil;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.RegularGrammarUtils;
import com.gvtv.android.cloud.util.StringUtils;
import com.gvtv.android.cloud.util.ToastUtil;
import com.gvtv.android.cloud.view.AgreementDialog;
import com.gvtv.android.cloud.view.TitleView;

public class RegisterActivity extends BaseActivity implements OnClickListener{

	private Class<?> clazz;
	private TitleView mTitleView;
	private View mPreLayout;
	private View mSucLayout;
	private Button mRegisterButton;
	private Button activateBtn;
	private String email;
	private String pwd1;
	private String pwd2;
	private EditText fillMailEdit, inputSecretEdit, confirmSecretEdit;
	private RegisterActivity instance;
	private TextView tv_agreement;
	private ToggleButton toggle_agrrement;
	
	private String temp_email;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		clazz = getClass();
		instance = this;
		findViews();
		setListeners();
		CloudApplication.getInstance().addActivity(instance);
	}
	
	@Override
	protected void onResume() {
		MessageReceiver.msghList.add(instance);
		super.onResume();
	}

	@Override
	protected void onPause() {
		for (int i = 0; i < MessageReceiver.msghList.size(); i++) {
			if(MessageReceiver.msghList.get(i) instanceof RegisterActivity){
				MessageReceiver.msghList.remove(i);
				i--;
			}
		}
		super.onPause();
	}

	private void findViews() {
		mTitleView = (TitleView) findViewById(R.id.registerTitle_register);
		mPreLayout = findViewById(R.id.registerPreLayout);
		mSucLayout = findViewById(R.id.regiterSucLayout);
		mRegisterButton = (Button) findViewById(R.id.registerBtn_register);
		activateBtn = (Button) findViewById(R.id.activateBtn);
		fillMailEdit = (EditText) findViewById(R.id.fillMailEdit);
		inputSecretEdit = (EditText) findViewById(R.id.inputSecretEdit);
		confirmSecretEdit = (EditText) findViewById(R.id.confirmSecretEdit);
		tv_agreement = (TextView) findViewById(R.id.tv_agreement);
		toggle_agrrement = (ToggleButton) findViewById(R.id.toggle_agreement);
		
		TextWatcher mTextWatcher = new TextWatcher() {
	        @Override  
	        public void onTextChanged(CharSequence s, int start, int before, int count) {
	             try {
	            	 if(s != null){
	            		 temp_email = s.toString();
	            		 int len = temp_email.getBytes("UTF-8").length;
	            		 if(len > 32){
	            			 ToastUtil.getToastUtils().showToast(instance, instance.getResources().getString(R.string.please_not_loanger_than_32));
	            			 for (int i = 10; i < 32; i++) {
	            				 String temp = temp_email.substring(0, i);
	 							int l = 0;
	 							try {
	 								l = temp.getBytes("UTF-8").length;
	 							} catch (UnsupportedEncodingException e) {
	 								e.printStackTrace();
	 							}
	 							if( l > 32){
	 								temp = temp_email.substring(0, i-1);
	 								fillMailEdit.setText(temp);
	 								fillMailEdit.setSelection(temp.length());
	 								break;
	 							}
	 						}
	            		 }
	            	 }
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
	        }  
	          
	        @Override  
	        public void beforeTextChanged(CharSequence s, int start, int count,  
	                int after) {  
	        }  
	          
	        @Override  
	        public void afterTextChanged(Editable s) {  
	       
	        }  
	    };
	    fillMailEdit.addTextChangedListener(mTextWatcher);

		//fillMailEdit.setText(CloudApplication.user_name);
		//inputSecretEdit.setText("123456");
		//confirmSecretEdit.setText("123456");
	}

	private void setListeners() {
		mTitleView.getBackTextView().setOnClickListener(instance);
		mRegisterButton.setOnClickListener(instance);
		activateBtn.setOnClickListener(instance);
		tv_agreement.setOnClickListener(instance);
	}

	@Override
	public void onClick(View v) {
		try {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (v == mTitleView.getBackTextView()) {
			finish();
		} else if (mRegisterButton == v) {
			email = fillMailEdit.getText().toString().trim();
			pwd1 = inputSecretEdit.getText().toString().trim();
			pwd2 = confirmSecretEdit.getText().toString().trim();
			int len =  0;
			try {
				len = email.getBytes("UTF-8").length;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if(len > 32){
      			 ToastUtil.getToastUtils().showToast(instance, instance.getResources().getString(R.string.please_not_loanger_than_32));
      		 }else if (StringUtils.isEmpty(email)) {
				ToastUtil.getToastUtils().showToast(instance,
						getResources().getString(R.string.please_input_email));
			} else if (!RegularGrammarUtils.isEmail(email)) {
				ToastUtil.getToastUtils().showToast(instance,
						getResources().getString(R.string.email_format_error));
			} else if (StringUtils.isEmpty(pwd1) || StringUtils.isEmpty(pwd2)) {
				ToastUtil.getToastUtils().showToast(instance,
						getResources().getString(R.string.please_input_pwd));
			} else if (!RegularGrammarUtils.isPasswd(pwd1) || !RegularGrammarUtils.isPasswd(pwd2)) {
				inputSecretEdit.setText(null);
				confirmSecretEdit.setText(null);
				ToastUtil.getToastUtils().showToast(instance,
						getResources().getString(R.string.pwd_format_error));
			} else if (!RegularGrammarUtils.checkPaswdIsSame(pwd1, pwd2)) {
				inputSecretEdit.setText(null);
				confirmSecretEdit.setText(null);
				ToastUtil.getToastUtils().showToast(instance,
						getResources().getString(R.string.pwd_notsame_error));
			} else if(!toggle_agrrement.isChecked()){
				ToastUtil.getToastUtils().showToast(instance,
						getResources().getString(R.string.please_read_and_agree_agreement));
			}else {
				AlertDialogUtil.getAlertDialogUtil().showDialog(instance, getResources().getString(R.string.register_please_wait));
				try {
					if(CloudApplication.bizAddes.size() > 0){
						CloudApplication.requestID += 2;
						SocketUtils.secret_key_req(instance, instance, CloudApplication.requestID);
					}else{
						if(CloudApplication.loadAddes.size() > 0){
							SocketUtils.controller_reg_req(instance);
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
		} else if (activateBtn == v) {
			try {
				Uri uri = Uri.parse(RegularGrammarUtils.buildMailUrl(email));
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				startActivityForResult(it, AppConst.REQUEST_ACTIVATE);
			} catch (Exception e) {
				LogUtils.getLog(getClass()).error(e.toString());
				Uri uri = Uri.parse("https://www.google.com/");
				Intent it = new Intent(Intent.ACTION_VIEW, uri);
				startActivityForResult(it, AppConst.REQUEST_ACTIVATE);
			}
		}else if(tv_agreement == v){
			AgreementDialog.getInstance(instance).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (AppConst.REQUEST_ACTIVATE == requestCode) {
//			PreferenceUtils.saveLoginInfo(instance, email, pwd1);
//			Intent intent = new Intent(instance, MainActivity.class);
//			intent.putExtra("isLogin", false);
//			startActivity(intent);
//			finish();
//		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onMessage(int type, int resp, MsgBean msg, byte[] restoreByte) {
		super.onMessage(type, resp, msg, restoreByte);
		if (type == MsgTypeCode.USER_REGISTER_REQ) {
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).disconnect();
			if (resp == MsgResponseCode.OK) {
				inputSecretEdit.setText(null);
				confirmSecretEdit.setText(null);
				mPreLayout.setVisibility(View.GONE);
				mSucLayout.setVisibility(View.VISIBLE);
			} else if(resp == MsgResponseCode.USER_EXIST_NOT_ACTIVATED){
				ToastUtil.getToastUtils().showToastByCode(instance, resp);;
				inputSecretEdit.setText(null);
				confirmSecretEdit.setText(null);
				mPreLayout.setVisibility(View.GONE);
				mSucLayout.setVisibility(View.VISIBLE);
			}else{
				inputSecretEdit.setText(null);
				confirmSecretEdit.setText(null);
				if(resp == MsgResponseCode.EMAIL_EXIST){
					fillMailEdit.setText(null);
					fillMailEdit.requestFocus();
				}else{
					inputSecretEdit.requestFocus();
					inputSecretEdit.setSelection(inputSecretEdit.getText().toString().length());
				}
				ToastUtil.getToastUtils().showToastByCode(instance, resp);
			}
		}else if(type == MsgTypeCode.SECRET_KEY_REQ){
			if(resp == MsgResponseCode.OK){
				try {
					CloudApplication.requestID += 2;
					SocketUtils.user_register_req(email, pwd1, instance, CloudApplication.requestID);
				} catch (Exception e) {
					AlertDialogUtil.getAlertDialogUtil().cancelDialog();
					LogUtils.getLog(getClass()).error(e.toString());
				}
			}else{
				ToastUtil.getToastUtils().showToastByCode(instance, resp);
			}
		}else if(type == MsgTypeCode.CONTROLLER_REG_RESP){
			if(CloudApplication.bizAddes.size() > 0){
				try {
					SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).disconnect();
					CloudApplication.requestID += 2;
					SocketUtils.secret_key_req(instance, instance, CloudApplication.requestID);
				} catch (Exception e) {
					AlertDialogUtil.getAlertDialogUtil().cancelDialog();
					LogUtils.getLog(clazz).error(e.toString());
				}
			}else{
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			}
		}else if(type == MsgTypeCode.CONTROLLER_ADDR_RESP){
			if(CloudApplication.loadAddes.size() > 0){
				try {
					SocketUtils.controller_reg_req(instance);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			}
		}
	}

}
