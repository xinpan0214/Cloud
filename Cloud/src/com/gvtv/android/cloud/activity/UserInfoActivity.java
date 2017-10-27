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
import com.gvtv.android.cloud.view.TitleView;

public class UserInfoActivity extends BaseActivity implements OnClickListener {

	private Button mModifySecButton;
	private Button mExitAccoButton;
	private TitleView mTitleView;
	private TextView account_text;
	private TextView bind_device_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		mModifySecButton = (Button) findViewById(R.id.modifyBtn);
		mModifySecButton.setOnClickListener(this);
		mExitAccoButton = (Button) findViewById(R.id.exitBtn);
		mExitAccoButton.setOnClickListener(this);
		mTitleView = (TitleView) findViewById(R.id.registerTitle);
		mTitleView.getBackTextView().setOnClickListener(this);
		account_text = (TextView) findViewById(R.id.account_text);
		bind_device_text = (TextView) findViewById(R.id.bind_device_text);
		account_text.setText(getResources().getString(R.string.account_link) + CloudApplication.user_name);
		bind_device_text.setText(getResources().getString(R.string.binded_device_link) + CloudApplication.bindedDeviceInfo.getDev_name());
		CloudApplication.getInstance().addActivity(this);
	}

	@Override
	public void onClick(View v) {
		if(v == mModifySecButton){
			Intent intent = new Intent(this, ChangePaswdActivity.class);
			startActivity(intent);
		}else if(v == mExitAccoButton){
			Intent tcpIntent = new Intent(this, MessageService.class);
			stopService(tcpIntent);
			SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).disconnect();
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("isLogin", false);
			startActivity(intent);
			finish();
		}else if(v == mTitleView.getBackTextView()){
			finish();
		}
	}

}
