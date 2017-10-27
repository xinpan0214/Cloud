package com.gvtv.android.cloud.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gvtv.android.cloud.AppConst;
import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.broadcast.MessageReceiver;
import com.gvtv.android.cloud.msg.MessageUtils;
import com.gvtv.android.cloud.msg.MsgTypeCode;
import com.gvtv.android.cloud.sockets.SendTcpMsgAsyncTask;
import com.gvtv.android.cloud.sockets.SocketClient;
import com.gvtv.android.cloud.sockets.SocketUtils;
import com.gvtv.android.cloud.util.AlertDialogUtil;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.RegularGrammarUtils;
import com.gvtv.android.cloud.util.StringUtils;
import com.gvtv.android.cloud.util.ToastUtil;
import com.gvtv.android.cloud.view.TitleView;

public class FindPWDActivity extends BaseActivity implements OnClickListener {

	private TitleView title;
	private Button btn_commit;
	private EditText et_mail;
	private String url_mail;
	private FindPWDActivity instance;
	private TextView tv_resend,tv_tip1,tv_tip2,tv_tip3;
	private boolean isCommited;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_findpwd);
		instance = this;
		findViews();
		setListeners();
		CloudApplication.getInstance().addActivity(this);
	}

	private void findViews() {
		title = (TitleView) findViewById(R.id.title);
		btn_commit = (Button) findViewById(R.id.commit_btn);
		et_mail = (EditText) findViewById(R.id.mail_et);
		et_mail.setText(getIntent().getStringExtra("account"));
		et_mail.setSelection(et_mail.getText().toString().length());
		tv_resend = (TextView) findViewById(R.id.tv_tips_4);
		tv_tip3 = (TextView) findViewById(R.id.tv_tips_3);
		tv_tip2 = (TextView) findViewById(R.id.tv_tips_2);
		tv_tip1 = (TextView) findViewById(R.id.tv_tips_1);
	}

	private void setListeners() {
		title.getBackTextView().setOnClickListener(this);
		btn_commit.setOnClickListener(this);
		tv_resend.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		try {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if (title.getBackTextView() == v) {
			finish();
		} else if (btn_commit == v) {
			if(isCommited){
				try {
					Uri uri = Uri.parse(RegularGrammarUtils.buildMailUrl(url_mail));
					Intent it = new Intent(Intent.ACTION_VIEW, uri);
					startActivityForResult(it, AppConst.REQUEST_FINDPWD);
				} catch (Exception e) {
					e.printStackTrace();
					Uri uri = Uri.parse("https://www.google.com/");
					Intent it = new Intent(Intent.ACTION_VIEW, uri);
					startActivityForResult(it, AppConst.REQUEST_FINDPWD);
				}
			}else{
				url_mail = et_mail.getText().toString().trim();
				if (StringUtils.isEmpty(url_mail)) {
					ToastUtil.getToastUtils().showToast(instance,
							getResources().getString(R.string.please_input_account));
				} else if (RegularGrammarUtils.isEmail(url_mail)) {
					AlertDialogUtil.getAlertDialogUtil().showDialog(this, getResources().getString(R.string.find_pwd_please_wait));
					try {
						if(CloudApplication.bizAddes.size() > 0){
							CloudApplication.requestID += 2;;
							new SendTcpMsgAsyncTask(MessageUtils.build_PWD_retrieve_msg(url_mail,
									CloudApplication.requestID), SocketClient.CLIENT_BUSINESS, instance).send();
						}else if(CloudApplication.loadAddes.size() > 0){
							SocketUtils.controller_reg_loginreq(instance, url_mail);
						}else{
							CloudApplication.requestID += 2;
							new SendTcpMsgAsyncTask(MessageUtils.build_controller_addr_req_msg(CloudApplication.requestID),
									SocketClient.CLIENT_ADDR, instance).send();
						}
					} catch (Exception e) {
						AlertDialogUtil.getAlertDialogUtil().cancelDialog();
						LogUtils.getLog(getClass()).error(e.toString());
					}
				} else {
					ToastUtil.getToastUtils().showToast(this,
							getResources().getString(R.string.email_format_error));
				}
			}
			
		}else if(tv_resend == v){
			url_mail = et_mail.getText().toString().trim();
			if (RegularGrammarUtils.isEmail(url_mail)) {
				AlertDialogUtil.getAlertDialogUtil().showDialog(this, getResources().getString(R.string.find_pwd_please_wait));
				try {
					if(CloudApplication.bizAddes.size() > 0){
						CloudApplication.requestID += 2;;
						new SendTcpMsgAsyncTask(MessageUtils.build_PWD_retrieve_msg(url_mail,
								CloudApplication.requestID), SocketClient.CLIENT_BUSINESS, instance).send();
					}else if(CloudApplication.loadAddes.size() > 0){
						SocketUtils.controller_reg_loginreq(instance, url_mail);
					}else{
						CloudApplication.requestID += 2;
						new SendTcpMsgAsyncTask(MessageUtils.build_controller_addr_req_msg(CloudApplication.requestID),
								SocketClient.CLIENT_ADDR, instance).send();
					}
				} catch (Exception e) {
					AlertDialogUtil.getAlertDialogUtil().cancelDialog();
					LogUtils.getLog(getClass()).error(e.toString());
				}
			} else {
				ToastUtil.getToastUtils().showToast(this,
						getResources().getString(R.string.email_format_error));
			}
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
			if (MessageReceiver.msghList.get(i) instanceof FindPWDActivity) {
				MessageReceiver.msghList.remove(i);
				i--;
			}
		}
		super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if (AppConst.REQUEST_FINDPWD == requestCode) {
//			this.finish();
//		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onMessage(int type, int resp, MsgBean msg, byte[] restoreByte) {
		super.onMessage(type, resp, msg, restoreByte);
		if (type == MsgTypeCode.PWD_RETRIEVE_REQ) {
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			if (resp == 200) {
				isCommited = true;
				tv_resend.setVisibility(View.VISIBLE);
				tv_tip1.setVisibility(View.VISIBLE);
				tv_tip3.setVisibility(View.VISIBLE);
				tv_tip2.setVisibility(View.VISIBLE);
				et_mail.setVisibility(View.INVISIBLE);
				btn_commit.setText(getResources().getString(R.string.open_email));
			} else {
				ToastUtil.getToastUtils().showToastByCode(FindPWDActivity.this, resp);
			}
		}else if(type == MsgTypeCode.CONTROLLER_REG_RESP){
			if(CloudApplication.bizAddes.size() > 0){
				try {
					CloudApplication.requestID += 2;;
					new SendTcpMsgAsyncTask(MessageUtils.build_PWD_retrieve_msg(url_mail,
							CloudApplication.requestID), SocketClient.CLIENT_BUSINESS, instance).send();
				} catch (Exception e) {
					AlertDialogUtil.getAlertDialogUtil().cancelDialog();
					LogUtils.getLog(getClass()).error(e.toString());
				}
			}else{
				ToastUtil.getToastUtils().showToastByCode(instance, resp);
			}
		}else if(type == MsgTypeCode.CONTROLLER_ADDR_RESP){
			if(CloudApplication.loadAddes.size() > 0){
				try {
					SocketUtils.controller_reg_loginreq(instance, url_mail);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			}
		}
	}

}
