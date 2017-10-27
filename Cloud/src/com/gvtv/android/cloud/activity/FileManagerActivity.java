package com.gvtv.android.cloud.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.util.PreferenceUtils;
import com.gvtv.android.cloud.view.TitleView;

public class FileManagerActivity extends BaseActivity {

	private TitleView mTitleView;

	private Button mDownFileButton;
	private Button mBackupButton;
	private Button mUploadButton;
	private Button mShareButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_manager);

		mTitleView = (TitleView) findViewById(R.id.fileTitle);
		mTitleView.getBackTextView().setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						finish();
					}
				});
		mDownFileButton = (Button) findViewById(R.id.downFileBtn);
		mBackupButton = (Button) findViewById(R.id.backcpBtn);
		mUploadButton = (Button) findViewById(R.id.uploadBtn);
		mUploadButton.setEnabled(false);
		mShareButton = (Button) findViewById(R.id.shareBtn);
		mShareButton.setEnabled(false);
		
		mBackupButton.setEnabled(PreferenceUtils.isContectAccessable(getApplicationContext()));
		mDownFileButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FileManagerActivity.this, FileFolderActivity.class);
				startActivity(intent);
			}
		});
		
		mBackupButton.setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(FileManagerActivity.this, BackupActivity.class);
						startActivity(intent);
					}

				});
		CloudApplication.getInstance().addActivity(this);
	}
	
}
