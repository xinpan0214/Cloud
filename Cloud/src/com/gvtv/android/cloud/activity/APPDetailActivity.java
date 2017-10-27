package com.gvtv.android.cloud.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gvtv.android.cloud.AppConst;
import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.bean.AppInfo;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.broadcast.MessageReceiver;
import com.gvtv.android.cloud.db.SqliteUtils;
import com.gvtv.android.cloud.msg.MsgResponseCode;
import com.gvtv.android.cloud.msg.MsgTypeCode;
import com.gvtv.android.cloud.sockets.SocketUtils;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.ToastUtil;
import com.gvtv.android.cloud.view.ProgressWithText;
import com.gvtv.android.cloud.view.TitleView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class APPDetailActivity extends BaseActivity implements OnClickListener {

	private AppInfo app;
	private AppInfo app_installed;
	private ImageView iv_icon, iv_0, iv_1, iv_2, iv_3, iv_4;
	private Button btn_install, btn_uninstall;
	private TitleView mTitleView;
	private TextView tv_name, tv_version, tv_auther, tv_summary;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private int status;// 应用状态0：未安装，1：已安装最新版本，2：可升级,3正在安装
	private static final int STATUS_UNINSTALLED = 0;
	private static final int STATUS_INSTALLED = 1;
	private static final int STATUS_CANUPDATE = 2;
	private static final int STATUS_INSTALLING = 3;
	private APPDetailActivity instance;
	private ProgressWithText install_progress;
	private int progress;
	private boolean flag;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				CloudApplication.requestID += 2;
				try {
					SocketUtils.install_uninstall_app_req(instance, app, CloudApplication.requestID, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
			case 1:
				install_progress.setProgress(progress);
				break;

			default:
				break;
			}
			
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appdetail);
		instance = this;
		app = getIntent().getParcelableExtra(AppConst.APPSTORE_TO_APPDETAIL);
		app_installed = SqliteUtils.getInstance(instance).queryAppByCodeAndUsername(app.getAppcode(), CloudApplication.user_name);
		if (app_installed != null) {
			status = STATUS_INSTALLED;
			if (!app.getVersion().equalsIgnoreCase(app_installed.getVersion())) {
				status = STATUS_CANUPDATE;
			}
		}
		findViews();
		initView();
		setListeners();
		CloudApplication.getInstance().addActivity(instance);
	}

	private void findViews() {
		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		iv_0 = (ImageView) findViewById(R.id.iv_0_detail);
		iv_1 = (ImageView) findViewById(R.id.iv_1_detail);
		iv_2 = (ImageView) findViewById(R.id.iv_2_detail);
		iv_3 = (ImageView) findViewById(R.id.iv_3_detail);
		iv_4 = (ImageView) findViewById(R.id.iv_4_detail);
		btn_install = (Button) findViewById(R.id.btn_install);
		btn_uninstall = (Button) findViewById(R.id.btn_uninstall);
		mTitleView = (TitleView) findViewById(R.id.title);
		mTitleView.setViewBackgroundColor(getResources().getColor(R.color.app_store_title_color));
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_version = (TextView) findViewById(R.id.tv_version);
		tv_auther = (TextView) findViewById(R.id.tv_auther);
		tv_summary = (TextView) findViewById(R.id.tv_summary);
		install_progress = (ProgressWithText) findViewById(R.id.install_progress);
	}

	private void initView() {
		options = new DisplayImageOptions.Builder().showStubImage(R.drawable.public_defaulticon02)
				.showImageForEmptyUri(R.drawable.public_defaulticon01).showImageOnFail(R.drawable.public_defaulticon02).cacheInMemory()
				.cacheOnDisc().displayer(new SimpleBitmapDisplayer()).build();
		tv_auther.setText(getResources().getString(R.string.app_developer) + app.getDeveloper());
		tv_name.setText(app.getAppname());
		tv_version.setText(getResources().getString(R.string.app_version) + app.getVersion());
		tv_summary.setText(app.getSummary());
		imageLoader.displayImage(app.getIcon(), iv_icon, options, null);
		if(app.getImage0() != null){
			iv_0.setVisibility(View.VISIBLE);
			imageLoader.displayImage(app.getImage0(), iv_0, options, null);
		}else{
			iv_0.setVisibility(View.GONE);
		}
		if(app.getImage1() != null){
			iv_1.setVisibility(View.VISIBLE);
			imageLoader.displayImage(app.getImage1(), iv_1, options, null);
		}else{
			iv_1.setVisibility(View.GONE);
		}
		
		if(app.getImage2() != null){
			iv_2.setVisibility(View.VISIBLE);
			imageLoader.displayImage(app.getImage2(), iv_2, options, null);
		}else{
			iv_2.setVisibility(View.GONE);
		}
		
		if(app.getImage3() != null){
			iv_3.setVisibility(View.VISIBLE);
			imageLoader.displayImage(app.getImage3(), iv_3, options, null);
		}else{
			iv_3.setVisibility(View.GONE);
		}
		
		if(app.getImage4() != null){
			iv_4.setVisibility(View.VISIBLE);
			imageLoader.displayImage(app.getImage4(), iv_4, options, null);
		}else{
			iv_4.setVisibility(View.GONE);
		}
		
		refreshBtns();
	}
	
	private void refreshBtns(){
		if (status == STATUS_UNINSTALLED) {
			btn_install.setSelected(false);
			btn_install.setText(getResources().getString(R.string.app_install));
			btn_uninstall.setVisibility(View.INVISIBLE);
		} else if (status == STATUS_INSTALLED) {
			btn_install.setSelected(false);
			btn_uninstall.setVisibility(View.VISIBLE);
			btn_uninstall.setText(getResources().getString(R.string.app_uninstall));
			btn_install.setText(getResources().getString(R.string.app_open));
		} else if (status == STATUS_CANUPDATE) {
			btn_install.setSelected(false);
			btn_install.setText(getResources().getString(R.string.app_upgrade));
			btn_uninstall.setVisibility(View.VISIBLE);
		}else if(status == STATUS_INSTALLING){
			btn_install.setSelected(true);
			btn_install.setText(getResources().getString(R.string.app_canel_install));
			btn_uninstall.setVisibility(View.INVISIBLE);
		}
	}

	private void setListeners() {
		btn_install.setOnClickListener(this);
		btn_uninstall.setOnClickListener(this);
		mTitleView.getBackTextView().setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mTitleView.getBackTextView()) {
			finish();
		} else if (btn_install == v) {
			appbtn_installManage();
		}else if(v == btn_uninstall){
			appbtn_uninstallManage();
		}
	}

	@Override
	protected void onResume() {
		MessageReceiver.msghList.add(instance);
		flag = true;
		super.onResume();
	}

	@Override
	protected void onPause() {
		mHandler.removeMessages(0);
		mHandler.removeMessages(1);
		flag = false;
		for (int i = 0; i < MessageReceiver.msghList.size(); i++) {
			if (MessageReceiver.msghList.get(i) instanceof APPDetailActivity) {
				MessageReceiver.msghList.remove(i);
				i--;
			}
		}
		super.onPause();
	}

	@Override
	public void onMessage(int type, int resp, MsgBean msg, byte[] restoreByte) {
		super.onMessage(type, resp, msg, restoreByte);
		switch (type) {
		case MsgTypeCode.INSTALL_UNINSTALL_REQ:// 安装卸载应用
			if (resp == MsgResponseCode.OK || resp == MsgResponseCode.SYSTEM_ERROR) {
				installUninstallSuccess();
			} else {
				installUninstallFail();
				ToastUtil.getToastUtils().showToastByCode(instance, resp);
			}
			break;
		default:
			break;
		}
	}


	/**
	 * 点击上方按钮，改变状态
	 */
	private void appbtn_installManage() {
		switch (status) {
		case STATUS_UNINSTALLED:
			status = STATUS_INSTALLING;
			flag = true;
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while (flag && progress < 90) {
						progress += 3;
						mHandler.sendEmptyMessage(1);
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
			refreshBtns();
			mHandler.sendEmptyMessageDelayed(0, 3000);
			break;

		case STATUS_INSTALLED:
			Intent appIntent = new Intent(
					APPDetailActivity.this,
					APPActivity.class);
			appIntent.putExtra(AppConst.CLOUDMANAGER_TO_APP, app);
			startActivity(appIntent);
			refreshBtns();
			break;

		case STATUS_CANUPDATE:
			status = STATUS_INSTALLED;
			SqliteUtils.getInstance(getApplicationContext()).upgradeApp(app,
					CloudApplication.user_name);
			refreshBtns();
			break;
			
		case STATUS_INSTALLING:
			flag = false;
			mHandler.removeMessages(0);
			mHandler.removeMessages(1);
			status = 0;
			progress = 0;
			install_progress.setProgress(progress);
			refreshBtns();
			break;

		default:
			break;
		}
	}
	
	/*
	 * 点击下方按钮，改变状态
	 */
	private void appbtn_uninstallManage() {
		switch (status) {
		case STATUS_INSTALLED:
		case STATUS_CANUPDATE:
			CloudApplication.requestID += 2;
			try {
				SocketUtils.install_uninstall_app_req(instance, app, CloudApplication.requestID, 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		default:
			break;
		}
	}

	private void installUninstallSuccess() {
		switch (status) {
		case STATUS_INSTALLED:
			SqliteUtils.getInstance(getApplicationContext()).deleteAppByAppcode(app.getAppcode(), CloudApplication.user_name);
			status = STATUS_UNINSTALLED;
			progress = 0;
			install_progress.setProgress(progress);
			refreshBtns();
			LogUtils.getLog(getClass()).verbose("delete an App!!!!!!!!!!");
			break;
			
		case STATUS_INSTALLING:
			SqliteUtils.getInstance(getApplicationContext()).insertApp(app,
					CloudApplication.user_name);
			status = STATUS_INSTALLED;
			progress = 100;
			mHandler.sendEmptyMessage(1);	
			refreshBtns();
			LogUtils.getLog(getClass()).verbose("insert an App!!!!!!!!!!");
			break;

		default:
			break;
		}
	}

	private void installUninstallFail() {
		switch (status) {
		case STATUS_INSTALLING:
			status = STATUS_UNINSTALLED;
			progress = 0;
			install_progress.setProgress(progress);
			refreshBtns();
			break;
			
		case STATUS_CANUPDATE:
			
			break;
			
		case STATUS_INSTALLED:
			break;

		default:
			break;
		}
	}
	
}
