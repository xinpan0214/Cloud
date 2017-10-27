package com.gvtv.android.cloud.activity;

import android.app.Activity;
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

public class BindDeviceActivity extends Activity implements OnClickListener{

	private TitleView mTitleView;
	private TextView tv_deviceAccount;
	private Button btn_bind, btn_register;
	private long time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bind_device);
		findViews();
		setListeners();
		CloudApplication.getInstance().addActivity(this);
	}
	
	private void findViews(){
		mTitleView = (TitleView) findViewById(R.id.bindDeviceTitle);
		tv_deviceAccount = (TextView) findViewById(R.id.deviceAccountText);
		tv_deviceAccount.setText(CloudApplication.user_name);
		btn_bind = (Button) findViewById(R.id.bindBtn);
		btn_register = (Button) findViewById(R.id.registerBtn);
	}
	
	private void setListeners(){
		btn_bind.setOnClickListener(this);
		btn_register.setOnClickListener(this);
		mTitleView.getBackTextView().setVisibility(View.INVISIBLE);
		mTitleView.getBackTextView().setOnClickListener(this);
		mTitleView.getRightButton().setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v == btn_bind){
			Intent intent = new Intent(getApplicationContext(), SearchDeviceActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}else if(v == btn_register){
			Intent tcpIntent = new Intent(this, MessageService.class);
			stopService(tcpIntent);
			SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).disconnect();
			Intent registerIntent = new Intent(this, MainActivity.class);
			registerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(registerIntent);
		}else if(v == mTitleView.getBackTextView()){
			Intent intent = new Intent(getApplicationContext(), SearchDeviceActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}else if(v == mTitleView.getRightButton()){
			Intent mainintent = new Intent(getApplicationContext(), MainActivity.class);
			mainintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mainintent);
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		long temp = time;
		time = System.currentTimeMillis();
		if(time - temp > 3000){
			ToastUtil.getToastUtils().showToast(BindDeviceActivity.this, getResources().getString(R.string.press_backkey_once_more_logout));
		}else{
			Intent tcpIntent = new Intent(this, MessageService.class);
			stopService(tcpIntent);
			SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).disconnect();
			Intent mainintent = new Intent(getApplicationContext(), MainActivity.class);
			mainintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mainintent);
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
}
