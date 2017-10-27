package com.gvtv.android.cloud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.util.PreferenceUtils;
import com.gvtv.android.cloud.view.TitleView;

public class DeviceControlActivity extends BaseActivity implements OnClickListener{

	private TitleView mTitleView;
	private ToggleButton rbtn_6, rbtn_7;
	private RelativeLayout rlyt_2, rlyt_3, rlyt_4, rlyt_8, rlyt_9;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_control);
		findViews();
		setListeners();
		CloudApplication.getInstance().addActivity(this);
	}

	private void findViews(){
		mTitleView = (TitleView) findViewById(R.id.deviceControlTitle);
		rlyt_2 = (RelativeLayout) findViewById(R.id.rlyt_2);
		rlyt_3 = (RelativeLayout) findViewById(R.id.rlyt_3);
		rlyt_4 = (RelativeLayout) findViewById(R.id.rlyt_4);
		rbtn_6 = (ToggleButton) findViewById(R.id.rbtn_6);
		rbtn_6.setChecked(PreferenceUtils.isNoticeEnable(getApplicationContext()));
		rbtn_7 = (ToggleButton) findViewById(R.id.rbtn_7);
		rbtn_7.setChecked(PreferenceUtils.isContectAccessable(getApplicationContext()));
		rlyt_8 = (RelativeLayout) findViewById(R.id.rlyt_8);
		rlyt_9 = (RelativeLayout) findViewById(R.id.rlyt_9);
	}
	
	private void setListeners(){
		mTitleView.getBackTextView().setOnClickListener(this);
		rlyt_2.setOnClickListener(this);
		rlyt_3.setOnClickListener(this);
		rlyt_4.setOnClickListener(this);
		
		rlyt_8.setOnClickListener(this);
		rlyt_9.setOnClickListener(this);

		rbtn_6.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PreferenceUtils.setNoticeEnable(getApplicationContext(), isChecked);
			}
		});
		rbtn_7.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				PreferenceUtils.setContectAccessable(getApplicationContext(), isChecked);
			}
		});
	}

	@Override
	public void onClick(View v) {
		if(mTitleView.getBackTextView() == v){
			this.finish();
		}else if(rlyt_2 == v){
			Intent intent = new Intent(DeviceControlActivity.this, DeviceStatusControlActivity.class);
			startActivity(intent);
		}else if(rlyt_3 == v){
			Intent intent = new Intent(DeviceControlActivity.this, DownloadSettingActivity.class);
			startActivity(intent);
		}else if(rlyt_4 == v){
			Intent intent = new Intent(DeviceControlActivity.this, DeviceBindActivity.class);
			startActivity(intent);
		}else if(rlyt_8 == v){
			Intent intent = new Intent(DeviceControlActivity.this, UserInfoActivity.class);
			startActivity(intent);
		}else if(rlyt_9 == v){
			Intent intent = new Intent(DeviceControlActivity.this, AboutInfoActivity.class);
			startActivity(intent);
		}
	}



}
