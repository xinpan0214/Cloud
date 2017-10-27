package com.gvtv.android.cloud.sockets;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

import android.content.Context;
import android.content.Intent;

import com.gvtv.android.cloud.AppConst;
import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.bean.DeviceInfo;
import com.gvtv.android.cloud.msg.JsonUtils;
import com.gvtv.android.cloud.msg.MsgActionCode;
import com.gvtv.android.cloud.util.CCompatibleUtils;
import com.gvtv.android.cloud.util.LogUtils;

public class LanBroadcast {
	
	private DatagramSocket ds;
	private DatagramPacket sp;
	private boolean isConnected;
	private Context context = null;
	
	private static LanBroadcast mUDPClient;
	
	private  LanBroadcast() {
		this.context = CloudApplication.getInstance();
	}
	
	public static LanBroadcast getInstance(){
		if(mUDPClient == null){
			mUDPClient = new LanBroadcast();
		}
		return mUDPClient;
	}

	public boolean connect() {		
		try {
			ds = new DatagramSocket();
			isConnected = true;
			new Thread(new ReceiveThread(context)).start();
		} catch (SocketException e) {
			isConnected = false;
			LogUtils.getLog(getClass()).error(e.toString());
		}
		return isConnected;
	}
	
	public void disconnect() {
		if(ds != null){
			isConnected = false;
			ds.close();
		}
	}
	
	public void sendToServer(byte[] buf) throws IOException{
		sp = new DatagramPacket(buf, buf.length,InetAddress.getByName(AppConst.BROADCAST_IP), AppConst.BROADCAST_INT_PORT);
		ds.send(sp);
	}

	
	class ReceiveThread implements Runnable{
		Context mContext = null;
		public ReceiveThread(Context mContext) {
			this.mContext = mContext;
		}
		@Override
		public void run() {
			DatagramPacket inPacket;
			byte[] buf = new byte[1024];
			while (isConnected) {
				try {
					inPacket = new DatagramPacket(buf, 1024);
					ds.receive(inPacket); // 接收广播信息并将信息封装到inPacket中	
					byte[] b = new byte[4];
					ByteBuffer buffer = ByteBuffer.wrap(buf);
					buffer.position(4);
					buffer.get(b);
					int json_len = CCompatibleUtils.bytesToIntLittleEndian(b);
					if(json_len > 0){
						CloudApplication.devs.clear();
						buffer.position(60);
						byte[] bb = new byte[json_len];
						buffer.get(bb);
						String jsonStr = new String(bb, "UTF-8");
						LogUtils.getLog(getClass()).verbose(jsonStr);
						DeviceInfo dev = JsonUtils.parseDEVICE_BIND(jsonStr);
						CloudApplication.devs.add(dev);
						Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
						intent.putExtra(AppConst.RESP_TYPE, MsgActionCode.DEVICE_BIND);
						CloudApplication.getInstance().sendBroadcast(intent);
					}
				} catch (Exception e) {
					LogUtils.getLog(getClass()).error(e.toString());
				}
			}
		}
	}
	

}
