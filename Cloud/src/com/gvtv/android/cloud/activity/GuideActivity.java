package com.gvtv.android.cloud.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.view.PointWidget;
import com.gvtv.android.cloud.view.ScrollLayout;
import com.gvtv.android.cloud.view.ScrollLayout.OnScreenChangedListener;

public class GuideActivity extends BaseActivity {

	private Button btn;
	private ScrollLayout mScrollLayout;
	private PointWidget mPointWidget;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		btn = (Button) findViewById(R.id.btn);
		mScrollLayout = (ScrollLayout) findViewById(R.id.mScrollLayout);
		mPointWidget = (PointWidget) findViewById(R.id.mPointWidget);
		mPointWidget.setPointPadding(7, 0, 7, 0);
		mPointWidget.setPointSize(24, 10);
		mPointWidget.setPointCount(4);//必须在前两句之后
		mPointWidget.setPoint(0);
		
		mScrollLayout.setOnScreenChangedListener(new OnScreenChangedListener() {
			
			@Override
			public void onScreenChanged(int currentScreen) {
				mPointWidget.setPoint(currentScreen);
			}
		});
		
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(GuideActivity.this, MainActivity.class);
				startActivity(intent);
				GuideActivity.this.finish();
			}
		});
	}
	
	@Override
	public void onBackPressed() {
	}

}
