package com.gvtv.android.cloud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.broadcast.MessageReceiver;
import com.gvtv.android.cloud.msg.MsgResponseCode;
import com.gvtv.android.cloud.msg.MsgTypeCode;
import com.gvtv.android.cloud.sockets.SocketUtils;
import com.gvtv.android.cloud.util.AlertDialogUtil;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.PreferenceUtils;
import com.gvtv.android.cloud.util.RegularGrammarUtils;
import com.gvtv.android.cloud.util.StringUtils;
import com.gvtv.android.cloud.util.ToastUtil;
import com.gvtv.android.cloud.view.TitleView;

public class ChangePaswdActivity extends BaseActivity implements OnClickListener{
	
	private TitleView mTitleView;
	private RelativeLayout rlyt_input_pwd, rlyt_mod_pwd_end;
	private EditText et_1, et_2;
	private Button btn_modded, btn_commit;
	private int status;//介面状态，0为输入旧密码，1为输入新密码，2为修改成功
	private String pwdOld, pwdNew1, pwdNew2;
	private ChangePaswdActivity instance;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modpwd);
		findViews();
		setListeners();
		instance = this;
		CloudApplication.getInstance().addActivity(this);
	}
	
	@Override
	protected void onResume() {
		MessageReceiver.msghList.add(instance);
		super.onResume();
	}

	@Override
	protected void onPause() {
		for (int i = 0; i < MessageReceiver.msghList.size(); i++) {
			if(MessageReceiver.msghList.get(i) instanceof ChangePaswdActivity){
				MessageReceiver.msghList.remove(i);
				i--;
			}
		}
		super.onPause();
	}
	
	private void findViews(){
		mTitleView = (TitleView) findViewById(R.id.title);
		mTitleView.getBackTextView().setVisibility(View.INVISIBLE);
		rlyt_input_pwd = (RelativeLayout) findViewById(R.id.rlyt_input_pwd);
		rlyt_mod_pwd_end = (RelativeLayout) findViewById(R.id.rlyt_mod_pwd_end);
		et_1 = (EditText) findViewById(R.id.et_1);
		et_2 = (EditText) findViewById(R.id.et_2);
		btn_commit = (Button) findViewById(R.id.btn_commit);
		btn_modded = (Button) findViewById(R.id.btn_modded);
		rlyt_input_pwd.setVisibility(View.VISIBLE);
		rlyt_mod_pwd_end.setVisibility(View.GONE);
		et_1.setVisibility(View.VISIBLE);
		et_2.setVisibility(View.GONE);
		et_1.setHint(R.string.input_old_pwd);
		btn_commit.setText(R.string.next_step);
		mTitleView.getRightButton().setVisibility(View.VISIBLE);
		mTitleView.getRightButton().setText(R.string.cancel);
		status = 0;
	}
	
	private void setListeners(){
		btn_commit.setOnClickListener(this);
		btn_modded.setOnClickListener(this);
		mTitleView.getBackTextView().setOnClickListener(this);
		mTitleView.getRightButton().setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		try {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(v == btn_commit){
			if(status == 0){
				pwdOld = et_1.getText().toString().trim();
				if (StringUtils.isEmpty(pwdOld)) {
					ToastUtil.getToastUtils().showToast(this,
							getResources().getString(R.string.please_input_oldpwd));
				}else if (!RegularGrammarUtils.isPasswd(pwdOld)) {
					ToastUtil.getToastUtils().showToast(this,
							getResources().getString(R.string.pwd_format_error));
					et_1.setText(null);
				}else if (!pwdOld.equals(CloudApplication.user_pwd)) {
					ToastUtil.getToastUtils().showToast(this,
							getResources().getString(R.string.wrong_secret));
					et_1.setText(null);
				}else{
					status = 1;
					et_1.setText(null);
					rlyt_input_pwd.setVisibility(View.VISIBLE);
					rlyt_mod_pwd_end.setVisibility(View.GONE);
					et_1.setVisibility(View.VISIBLE);
					et_1.setHint(R.string.input_new1_pwd);
					et_2.setVisibility(View.VISIBLE);
					btn_commit.setText(R.string.commit_email);
				}
			}else if(status == 1){
				pwdNew1 = et_1.getText().toString().trim();
				pwdNew2 = et_2.getText().toString().trim();
				if (StringUtils.isEmpty(pwdNew1)) {
					ToastUtil.getToastUtils().showToast(this,
							getResources().getString(R.string.please_input_newpwd));
				}else if (!RegularGrammarUtils.isPasswd(pwdNew1)) {
					et_1.setText(null);
					et_2.setText(null);
					et_1.requestFocus();
					ToastUtil.getToastUtils().showToast(this,
							getResources().getString(R.string.pwd_format_error));
				}else if (StringUtils.isEmpty(pwdNew2)) {
					ToastUtil.getToastUtils().showToast(this,
							getResources().getString(R.string.please_input_newpwd));
				}else if (!RegularGrammarUtils.isPasswd(pwdNew2)) {
					et_1.setText(null);
					et_2.setText(null);
					et_1.requestFocus();
					ToastUtil.getToastUtils().showToast(this,
							getResources().getString(R.string.pwd_format_error));
				}else if(!RegularGrammarUtils.checkPaswdIsSame(pwdNew1, pwdNew2)){
					et_1.setText(null);
					et_2.setText(null);
					et_1.requestFocus();
					ToastUtil.getToastUtils().showToast(this,
							getResources().getString(R.string.pwd_notsame_error));
				}else{
					AlertDialogUtil.getAlertDialogUtil().showDialogWithoutCancelListener(this, getResources().getString(R.string.connecting_please_wait));
					try {
						CloudApplication.requestID += 2;
						SocketUtils.pwd_modify_req(CloudApplication.user_name, pwdOld, pwdNew1, instance, CloudApplication.requestID);
					} catch (Exception e) {
						AlertDialogUtil.getAlertDialogUtil().cancelDialog();
						LogUtils.getLog(getClass()).error(e.toString());
					}
				}
			}
		}else if(v == btn_modded){
			finish();
		}else if(v == mTitleView.getRightButton()){
			finish();
		}
	}

	
	@Override
	public void onMessage(int type, int resp, MsgBean msg, byte[] restoreByte) {
		switch (type) {
		
		case MsgTypeCode.PWD_MODIFY_REQ:
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			et_1.setText(null);
			et_2.setText(null);
			if(resp == MsgResponseCode.OK){
				CloudApplication.user_pwd = pwdNew1;
				PreferenceUtils.saveLoginInfo(instance, CloudApplication.user_name, CloudApplication.user_pwd);
				rlyt_input_pwd.setVisibility(View.GONE);
				rlyt_mod_pwd_end.setVisibility(View.VISIBLE);
				mTitleView.getRightButton().setVisibility(View.GONE);
				
				Intent in = new Intent(instance, MainActivity.class);
				in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(in);
				
			}else{
				et_1.requestFocus();
				ToastUtil.getToastUtils().showToastByCode(ChangePaswdActivity.this, resp);
			}
			break;
		default:
			break;
		}
	}
}
