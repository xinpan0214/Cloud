package com.gvtv.android.cloud.activity;

import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.view.TitleView;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

public class AboutInfoActivity extends BaseActivity implements OnClickListener {

	private TitleView mTitleView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		mTitleView = (TitleView) findViewById(R.id.title);
		mTitleView.getBackTextView().setOnClickListener(this);
		CloudApplication.getInstance().addActivity(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public void onClick(View v) {
		finish();
	};


}
