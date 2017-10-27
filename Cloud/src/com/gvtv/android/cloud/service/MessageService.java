package com.gvtv.android.cloud.service;

import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.bean.ServerAddress;
import com.gvtv.android.cloud.msg.MessageUtils;
import com.gvtv.android.cloud.sockets.SocketClient;
import com.gvtv.android.cloud.sockets.UDPClient;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.PreferenceUtils;
import com.gvtv.android.cloud.util.crypto.RSACrypto;

public class MessageService extends Service {
	
	public static final int FOREGROUND_ID = 0;
	private Timer tcpTimer;
	private SocketClient mClient;
	
	private UDPClient mUDPClient;
	private Timer udpTimer;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		startTimer();
		startForeground(FOREGROUND_ID, new Notification());
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		LogUtils.getLog(getClass()).error("MessageService is finished...");
		stopTimer();
		super.onDestroy();
	}
	
	private void stopTimer() {
		if(tcpTimer != null){
			tcpTimer.purge();
			tcpTimer.cancel();
			tcpTimer = null;
		}
		
		if(udpTimer != null){
			udpTimer.purge();
			udpTimer.cancel();
			udpTimer = null;
		}
	}
	
	
	private void startTimer() {
		if (tcpTimer == null) {
			tcpTimer = new Timer();
			TCPBHTask tcpTask = new TCPBHTask();
			tcpTimer.schedule(tcpTask, 1000L, 3000L);
		}
		
		if (udpTimer == null) {
			udpTimer = new Timer();
			UDPBHTask udpTask = new UDPBHTask();
			udpTimer.schedule(udpTask, 1000L, 60000L);
		}
	}
	
	public class TCPBHTask extends TimerTask {
		public TCPBHTask() {
		}

		@Override
		public void run() {
			try {
				SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).sendUrgentData();
			} catch (Exception e) {
				LogUtils.getLog(getClass()).error(e.toString() + "...reconnect...");
				mClient = SocketClient.getInstace(SocketClient.CLIENT_BUSINESS);
				mClient.disconnect();
				byte[] buf_key_agree_req = null;
				byte[] buf_key_agree;
				try {
					if(CloudApplication.bizAddes.size() == 0){
						ServerAddress bizAddr = new ServerAddress();
						bizAddr.setserverIP(PreferenceUtils.getBizIP(CloudApplication.getInstance()));
						bizAddr.setserverPort((short)(PreferenceUtils.getBizTCPPort(CloudApplication.getInstance())));
						bizAddr.setServerUDPPort((short)(PreferenceUtils.getBizUDPPort(CloudApplication.getInstance())));
						if(bizAddr.getserverPort() != 0){
							CloudApplication.bizAddes.add(bizAddr);
						}
					}
					mClient.connect();
					buf_key_agree = RSACrypto.encrypt(
							RSACrypto.loadPublicKey(RSACrypto.getPublicKey(CloudApplication.getInstance())),
							CloudApplication.aeskey.getBytes("UTF-8"));
					CloudApplication.requestID_relogin += 2;
					buf_key_agree_req = MessageUtils.buildSecretkeyMsg(buf_key_agree, CloudApplication.requestID_relogin);
					mClient.send(buf_key_agree_req);
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public class UDPBHTask extends TimerTask {
		public UDPBHTask() {
		}

		@Override
		public void run() {
			try {
				SocketClient.getInstace(SocketClient.CLIENT_BUSINESS).send(MessageUtils.buildTCPBHMsg(CloudApplication.user_name, CloudApplication.requestID));
				mUDPClient = UDPClient.getInstance();
				mUDPClient.sendToServer(MessageUtils.buildUDPBHMsg(CloudApplication.user_name, CloudApplication.requestID));
			} catch (Exception e) {
				e.printStackTrace();
			}
			LogUtils.getLog(getClass()).verbose("BH======================start");
		}
	}

}
