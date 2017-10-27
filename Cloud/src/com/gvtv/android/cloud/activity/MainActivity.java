package com.gvtv.android.cloud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.service.MessageService;
import com.gvtv.android.cloud.sockets.SocketClient;
import com.gvtv.android.cloud.util.ToastUtil;
import com.gvtv.android.cloud.view.TitleView;

public class MainActivity extends BaseActivity implements OnClickListener {

	private Button bindBtn;
	private Button btn_register;
	private MainActivity instance;
	private TextView tv_tips_1, tv_tips_2;
	private TextView tv_account;
	private long time;
	private boolean isLogin;
	private TitleView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bind_device);
		instance = this;
		title = (TitleView) findViewById(R.id.mainTitle);
		title.getRightButton().setOnClickListener(instance);
		btn_register = (Button) findViewById(R.id.registerBtn);
		btn_register.setOnClickListener(instance);
		bindBtn = (Button) findViewById(R.id.bindBtn);
		bindBtn.setOnClickListener(instance);
		
		tv_tips_1 = (TextView) findViewById(R.id.tv_tips_1);
		tv_account = (TextView) findViewById(R.id.deviceAccountText);
		tv_tips_2 = (TextView) findViewById(R.id.tv_tips_2);
		CloudApplication.getInstance().addActivity(instance);
		isLogin = getIntent().getBooleanExtra("isLogin", false);
	}

	@Override
	public void onClick(View v) {
		if(v == bindBtn){
			if(isLogin){
				Intent loginIntent = new Intent(this, SearchDeviceActivity.class);
				startActivity(loginIntent);
			}else{
				Intent loginIntent = new Intent(this, LoginActivity.class);
				startActivity(loginIntent);
			}
		}else if(v == btn_register){
			isLogin = false;
			Intent tcpIntent = new Intent(this, MessageService.class);
			stopService(tcpIntent);
			SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).disconnect();
			Intent registerIntent = new Intent(this, RegisterActivity.class);
			startActivity(registerIntent);
		}else if(v == title.getRightButton()){
			isLogin = false;
			Intent intent = new Intent(instance, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("isLogin", false);
			startActivity(intent);
			//instance.finish();
//			tv_account.setVisibility(View.INVISIBLE);
//			tv_tips_1.setText(getResources().getString(R.string.login_tip));
//			bindBtn.setText(getResources().getString(R.string.login));
//			title.setVisibility(View.INVISIBLE);
			//btn_register.setVisibility(View.VISIBLE);
			//tv_tips_2.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onResume() {
		if(isLogin){
			tv_account.setVisibility(View.VISIBLE);
			tv_account.setText(getResources().getString(R.string.account_display) + CloudApplication.user_name);
			tv_tips_1.setText(getResources().getString(R.string.cannot_use_device));
			bindBtn.setText(getResources().getString(R.string.bind_device));
			title.setVisibility(View.VISIBLE);
			title.getBackTextView().setVisibility(View.INVISIBLE);
			//btn_register.setVisibility(View.INVISIBLE);
			//tv_tips_2.setVisibility(View.INVISIBLE);
		}else{
			tv_account.setVisibility(View.INVISIBLE);
			tv_tips_1.setText(getResources().getString(R.string.login_tip));
			bindBtn.setText(getResources().getString(R.string.login));
			title.setVisibility(View.INVISIBLE);
			//btn_register.setVisibility(View.VISIBLE);
			//tv_tips_2.setVisibility(View.VISIBLE);
		}
		super.onResume();
	}
	
	@Override
	public void onBackPressed() {
		long temp = time;
		time = System.currentTimeMillis();
		if(time - temp > 3000){
			ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.press_backkey_once_more));
		}else{
			Intent tcpIntent = new Intent(instance, MessageService.class);
			stopService(tcpIntent);
			SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).disconnect();
			CloudApplication.getInstance().exit();
			super.onBackPressed();
		}
	}
}
