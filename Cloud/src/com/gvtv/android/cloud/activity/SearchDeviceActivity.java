package com.gvtv.android.cloud.activity;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ListView;
import android.widget.TextView;

import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.adapter.DevicesAdapter;
import com.gvtv.android.cloud.bean.DeviceInfo;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.broadcast.MessageReceiver;
import com.gvtv.android.cloud.db.SqliteUtils;
import com.gvtv.android.cloud.msg.MessageUtils;
import com.gvtv.android.cloud.msg.MsgActionCode;
import com.gvtv.android.cloud.msg.MsgResponseCode;
import com.gvtv.android.cloud.msg.MsgTypeCode;
import com.gvtv.android.cloud.service.MessageService;
import com.gvtv.android.cloud.sockets.LanBroadcast;
import com.gvtv.android.cloud.sockets.SendTcpMsgAsyncTask;
import com.gvtv.android.cloud.sockets.SocketClient;
import com.gvtv.android.cloud.sockets.SocketUtils;
import com.gvtv.android.cloud.util.AlertDialogUtil;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.ToastUtil;
import com.gvtv.android.cloud.view.SearchAnim;
import com.gvtv.android.cloud.view.TitleView;

@SuppressLint("HandlerLeak")
public class SearchDeviceActivity extends BaseActivity implements OnClickListener{

	private TitleView mTitleView;
	private SearchAnim mSearchAnim;
	private TextView tv_deviceAccount;
	private ListView lv_device;
	private TextView unFindText;
	private Class<?> clazz;
	private LanBroadcast lanBc;
	private SearchDeviceActivity instance;
	private DevicesAdapter mDevicesAdapter;
	private TextView searchText;
	private int bindIndex;
	private boolean isBinded;
	private DeviceInfo bindDevice;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {		
			if(msg.what == 0){
				CloudApplication.requestID += 2;
				try {
					SocketUtils.bindstatus_search_req(CloudApplication.devs, instance, CloudApplication.requestID);
				} catch (Exception e) {
					LogUtils.getLog(getClass()).error(e.toString());
				}
			}else if(msg.what == 1){
				mDevicesAdapter.notifyDataSetChanged();
				mSearchAnim.setEnabled(true);
			}
			
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_device);
		instance = this;
		clazz = getClass();
		findViews();
		setListeners();
		tv_deviceAccount.setText(getResources().getString(R.string.account_id) + CloudApplication.user_name);
		CloudApplication.getInstance().addActivity(instance);
	}
	
	private void findViews(){
		mTitleView = (TitleView) findViewById(R.id.searchDeviceTitle);
		mSearchAnim = (SearchAnim) findViewById(R.id.searchAnim);
		tv_deviceAccount = (TextView) findViewById(R.id.deviceAccountText);
		lv_device = (ListView) findViewById(R.id.deviceList);
		unFindText = (TextView) findViewById(R.id.unFindText);
		searchText = (TextView) findViewById(R.id.searchText);
		CloudApplication.devs.clear();
		mDevicesAdapter = new DevicesAdapter(instance, CloudApplication.devs);
		lv_device.setAdapter(mDevicesAdapter);
	}
	
	private void setListeners(){
		mTitleView.getBackTextView().setOnClickListener(this);
		mTitleView.getRightButton().setOnClickListener(this);
		mSearchAnim.setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		if(mSearchAnim == v){
			searchText.setText(getResources().getString(R.string.searching_device));
			mSearchAnim.startAnimation();
			mSearchAnim.getAnimation().setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
					
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					searchText.setText(getResources().getString(R.string.search_again));
				}
			});
			mSearchAnim.setEnabled(false);
			
			if(CloudApplication.devs.size() == 0){
				unFindText.setText(R.string.unfind_device);
				unFindText.setVisibility(View.VISIBLE);
			}else{
				unFindText.setVisibility(View.GONE);
			}
			requestToBindDevice();
			
		}else if(mTitleView.getBackTextView() == v){
			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("isLogin", true);
			startActivity(intent);
		}else if(v == mTitleView.getRightButton()){
			Intent tcpIntent = new Intent(instance, MessageService.class);
			stopService(tcpIntent);
			SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).disconnect();
			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("isLogin", false);
			startActivity(intent);
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
			if(MessageReceiver.msghList.get(i) instanceof SearchDeviceActivity){
				MessageReceiver.msghList.remove(i);
				i--;
			}
		}
		super.onPause();
	}
	
	private void requestToBindDevice(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				lanBc = LanBroadcast.getInstance();
				lanBc.connect();
				byte[] buf = MessageUtils.buildLanMsg();
				try {
					lanBc.sendToServer(buf);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				lanBc.disconnect();
				mHandler.sendEmptyMessage(1);
			}
		}).start();		
	}


	@Override
	public void onMessage(int type, int resp, MsgBean msg, byte[] restoreByte) {
		super.onMessage(type, resp, msg, restoreByte);
		LogUtils.getLog(getClass()).verbose("type: " + type + ",resp: " + resp);
		switch (type) {	
		case MsgActionCode.DEVICE_BIND://设备查询
			if(CloudApplication.devs.size() == 0){
				unFindText.setText(R.string.unfind_device);
				unFindText.setVisibility(View.VISIBLE);
			}else{
				mHandler.sendEmptyMessage(0);
				unFindText.setVisibility(View.GONE);
			}
			break;
			
		case MsgTypeCode.BIND_QUERY_REQ://设备绑定信息查询
			if(resp == MsgResponseCode.OK){
				mDevicesAdapter.notifyDataSetChanged();
			}else{
				ToastUtil.getToastUtils().showToastByCode(instance, resp);
			}
			break;
			
		case MsgTypeCode.DEVICE_BIND_REQ://设备绑定成功，找负载
			if(resp == MsgResponseCode.OK){
				isBinded = true;
				ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.bind_success));
				Intent serviceintent = new Intent(getApplicationContext(), MessageService.class);
				stopService(serviceintent);
				SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).disconnect();
				
				AlertDialogUtil.getAlertDialogUtil().setDialogMessage(getResources().getString(R.string.login_please_wait));
				AlertDialogUtil.getAlertDialogUtil().setDialogTitle(getResources().getString(R.string.login));
				CloudApplication.requestID += 2;
				new SendTcpMsgAsyncTask(MessageUtils.build_controller_addr_req_msg(CloudApplication.requestID),
					SocketClient.CLIENT_ADDR, instance).send();
			}else{
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
				ToastUtil.getToastUtils().showToastByCode(instance, resp);
			}
			break;
		case MsgTypeCode.CONTROLLER_ADDR_RESP://负载找到，找业务
			if(CloudApplication.loadAddes.size() > 0){
				try {
					SocketUtils.controller_reg_loginreq(instance, CloudApplication.user_name);
				} catch (Exception e) {
					LogUtils.getLog(clazz).error(e.toString());
				}
			}else{
				ToastUtil.getToastUtils().showToastByCode(instance, resp);
				Intent intent = new Intent(instance, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("isLogin", false);
				startActivity(intent);
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			}
			break;
			
		case MsgTypeCode.CONTROLLER_REG_LOGIN_RESP://获得业务服务器，密钥协商
			if(CloudApplication.bizAddes.size() > 0){
				try {
					SocketUtils.secret_key_req(instance, instance, CloudApplication.requestID);
				} catch (Exception e) {
					LogUtils.getLog(clazz).error(e.toString());
				}
			}else{
				ToastUtil.getToastUtils().showToastByCode(instance, resp);
				Intent intent = new Intent(instance, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("isLogin", false);
				startActivity(intent);
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			}
			break;
		case MsgTypeCode.SECRET_KEY_REQ://密钥协商后，登录
				if(resp == MsgResponseCode.OK){
					try {
						CloudApplication.requestID += 2;
						SocketUtils.user_login_req(CloudApplication.user_name, CloudApplication.user_pwd, instance, CloudApplication.requestID);
					} catch (Exception e) {
						LogUtils.getLog(clazz).error(e.toString());
					}
				}else{
					ToastUtil.getToastUtils().showToastByCode(instance, resp);
					SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).disconnect();
					Intent intent = new Intent(instance, MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("isLogin", false);
					startActivity(intent);
					AlertDialogUtil.getAlertDialogUtil().cancelDialog();
				}
				break;
			
		case MsgTypeCode.CONTROLLER_LOGIN_REQ://登录回应
			isBinded = false;//跳转前，改回未绑定，避免调回主页
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
				if(resp == MsgResponseCode.OK){
					Intent serviceintent = new Intent(getApplicationContext(), MessageService.class);
					startService(serviceintent);
					
					CloudApplication.bindedDeviceInfo.setStatus(1);
					CloudApplication.bindedDeviceInfo.setAppids(SqliteUtils.getInstance(instance).queryAllAppid(CloudApplication.user_name));
					Intent intent = new Intent(instance, CloudManagerActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}else{
					ToastUtil.getToastUtils().showToastByCode(instance, resp);
					SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).disconnect();
					Intent intent = new Intent(instance, MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("isLogin", false);
					startActivity(intent);
				}
				
				break;
		default:
			break;
		}
	}

	
	public void toBindDevice(int position){
		isBinded = false;
		bindIndex = position;
		bindDevice = CloudApplication.devs.get(bindIndex);
		AlertDialogUtil.getAlertDialogUtil().showDialogWithoutCancelListener(this, getResources().getString(R.string.binding_please_wait));
		AlertDialogUtil.getAlertDialogUtil().getDialog().setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				if(isBinded){
					SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).disconnect();
					Intent intent = new Intent(instance, MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent.putExtra("isLogin", false);
					startActivity(intent);
				}
			}
		});
		try {
			CloudApplication.requestID += 2;
			SocketUtils.bind_device_req(bindDevice, CloudApplication.user_name, instance,CloudApplication.requestID);
		} catch (Exception e) {
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			LogUtils.getLog(clazz).error(e.toString());
		}
	}
}
