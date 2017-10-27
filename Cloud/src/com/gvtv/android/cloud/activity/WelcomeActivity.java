package com.gvtv.android.cloud.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.broadcast.MessageReceiver;
import com.gvtv.android.cloud.msg.MsgTypeCode;
import com.gvtv.android.cloud.util.PreferenceUtils;
import com.gvtv.android.cloud.util.ToastUtil;

public class WelcomeActivity extends BaseActivity {

	private WelcomeActivity instance;
	private Intent intent;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(intent != null){
				startActivity(intent);
				intent = null;
				WelcomeActivity.this.finish();
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		instance = this;
		if(PreferenceUtils.getFlagFirstIn(instance) == 0){
			intent = new Intent(WelcomeActivity.this, GuideActivity.class);
			PreferenceUtils.setFlagFirstIn(instance, 1);
		}else{
			intent = new Intent(WelcomeActivity.this, MainActivity.class);
		}
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
	}

	@Override
	protected void onResume() {
		mHandler.sendEmptyMessageDelayed(0, 3000);
		MessageReceiver.msghList.add(instance);
//		CloudApplication.requestID += 2;
//		new SendTcpMsgAsyncTask(MessageUtils.build_controller_addr_req_msg(CloudApplication.requestID),
//			SocketClient.CLIENT_ADDR, instance).send();
		super.onResume();
	}

	@Override
	protected void onPause() {
		for (int i = 0; i < MessageReceiver.msghList.size(); i++) {
			if(MessageReceiver.msghList.get(i) instanceof WelcomeActivity){
				MessageReceiver.msghList.remove(i);
				i--;
			}
		}
		mHandler.removeMessages(0);
		super.onPause();
	}

	@Override
	public void onMessage(int type, int resp, MsgBean msg, byte[] restoreByte) {
		switch (type) {
		case MsgTypeCode.CONTROLLER_ADDR_RESP:
			//mHandler.sendEmptyMessageDelayed(0, 1000);
			break;
		default:
			break;
		}
	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		if (!isNetConnected) {
			ToastUtil.getToastUtils().showToast(this,
					getResources().getString(R.string.network_not_available));
			mHandler.sendEmptyMessageDelayed(0, 1000);
		}
	}
}
