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
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.msg.MessageUtils;
import com.gvtv.android.cloud.msg.MsgActionCode;
import com.gvtv.android.cloud.msg.MsgTypeCode;
import com.gvtv.android.cloud.util.LogUtils;

public class UDPClient {
	
	private DatagramSocket ds;
	private DatagramPacket sp;
	private boolean isConnected;
	private Context context = null;
	
	private static UDPClient mUDPClient;
	private boolean isNatSuccess;
	
	private  UDPClient() {
		this.context = CloudApplication.getInstance();
	}
	
	public static UDPClient getInstance(){
		if(mUDPClient == null){
			mUDPClient = new UDPClient();
			mUDPClient.connect();
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
			ds = null;
			LogUtils.getLog(getClass()).error(e.toString());
		}
		return isConnected;
	}
	
	public void disconnect() {
		if(ds != null){
			isConnected = false;
			ds.close();
			ds = null;
		}
	}
	
	public void sendToServer(byte[] buf) throws IOException{
		sp = new DatagramPacket(buf, buf.length,InetAddress.getByName(CloudApplication.bizAddes.get(0).getserverIP()), CloudApplication.bizAddes.get(0).getServerUDPPort());
		ds.send(sp);
	}
	
	public void sendToDevice(byte[] buf) throws IOException{
		sp = new DatagramPacket(buf, buf.length,InetAddress.getByName(CloudApplication.nat_ip), CloudApplication.nat_port);
		ds.send(sp);
	}
	
	public void sendAsync(byte[] buf){
		new Thread(new SendThread(buf)).start();
	}
	
	public void sendAsyncTryNat(byte[] buf){
		new Thread(new SendToNatsThread(buf)).start();
	}
	
	class SendToNatsThread implements Runnable{
		byte[] buf = null;
		public SendToNatsThread(byte[] buf) {
			this.buf = buf;
		}
		@Override
		public void run() {
			int time = 0;
			isNatSuccess = false;
			while (time < 6000) {
				try {
					sendToDevice(buf);
					Thread.sleep(300);
					time += 300;
					LogUtils.getLog(getClass()).info("udp try ----");
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(!isNatSuccess){
				//失败
				LogUtils.getLog(getClass()).error("udp try fail---");
				MsgBean msg = new MsgBean();
				msg.setAction(MsgActionCode.NAT_BLOCK);
				msg.setRet(404);
				Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
				intent.putExtra(AppConst.RESP_TYPE, MsgTypeCode.CONTROLLER_DEV_UDP);
				intent.putExtra(AppConst.RESP_MSG, msg);
				intent.putExtra(AppConst.RESP_CODE, 200);
				context.sendBroadcast(intent);
			}
		}
	}
	
	class SendThread implements Runnable{
		byte[] buf = null;
		public SendThread(byte[] buf) {
			this.buf = buf;
		}
		@Override
		public void run() {
			try {
				sendToServer(buf);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*响应消息： 
	名称  类型  意义 
	Response len  4 字节，整型  响应包长度 
	Response id  4 字节，整型  响应包 ID 
	Response type  4 字节，整型  响应包类型：0x0b 
	Rtn  4 字节，整型  响应结果代码 
	Ip  4 字节，整型  对端 IP 地址（网络字节
	Udpport  4 字节，整型  对端 Udp端口号 
	Separate  4 字节，整型  分隔符：0x00 */
	
	class ReceiveThread implements Runnable{
		Context mContext = null;
		public ReceiveThread(Context mContext) {
			this.mContext = mContext;
		}
		@Override
		public void run() {
			ByteBuffer bytebuffer;
			while(isConnected){
				byte[] buf = null;
				buf = new byte[1024];
				DatagramPacket rp = new DatagramPacket(buf, 1024);
				try {
					ds.receive(rp);
					bytebuffer = ByteBuffer.wrap(buf);
					msgHandle(bytebuffer);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//version	Json-len	sequence	action	Return（选）	Offset(选)	Filesize(选)	Filename（选）	Json-cont
	//消息标识	Json长度	包序号	动作	返回标识	数据偏移	大小	名称	Json内容
	public void msgHandle(ByteBuffer bytebuffer){
		MsgBean msg = MessageUtils.parseUDPMsg(bytebuffer);
		if (msg.getAction() == MsgActionCode.NAT_BLOCK) {//穿透尝试100
			isNatSuccess = true;
			Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
			intent.putExtra(AppConst.RESP_CODE, 200);
			intent.putExtra(AppConst.RESP_TYPE, MsgTypeCode.CONTROLLER_DEV_UDP);
			intent.putExtra(AppConst.RESP_MSG, msg);
			context.sendBroadcast(intent);
		}else if (msg.getAction() == MsgActionCode.QUEST_RECOVER) {//105请求下载通过上一步查询好的文件，包含文件名字、文件 ID
				Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
				intent.putExtra(AppConst.RESP_TYPE, MsgTypeCode.CONTROLLER_DEV_UDP);
				intent.putExtra(AppConst.RESP_MSG, msg);
				intent.putExtra(AppConst.RESP_CODE, 200);
				context.sendBroadcast(intent);
			} else if (msg.getAction() == MsgActionCode.RECOVER_BLOCK) {//恢复文件块106
				Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
				intent.putExtra(AppConst.RESP_MSG, msg);
				intent.putExtra(AppConst.RESP_TYPE, MsgTypeCode.CONTROLLER_DEV_UDP);
				intent.putExtra(AppConst.RESP_CODE, 200);
				intent.putExtra(AppConst.BACKUP_BYTE, MessageUtils.parseUDPRestore(bytebuffer));
				context.sendBroadcast(intent);
			}else if (msg.getAction() == MsgActionCode.RECOVER_OVER) {//设备端发送完毕，告知客户端107
				Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
				intent.putExtra(AppConst.RESP_MSG, msg);
				intent.putExtra(AppConst.RESP_CODE, 200);
				intent.putExtra(AppConst.RESP_TYPE, MsgTypeCode.CONTROLLER_DEV_UDP);
				context.sendBroadcast(intent);
			}else if(msg.getAction() == MsgActionCode.BACKUP_BLOCK){//102备份文件块
				Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
				intent.putExtra(AppConst.RESP_CODE, 200);
				intent.putExtra(AppConst.RESP_MSG, msg);
				intent.putExtra(AppConst.RESP_TYPE, MsgTypeCode.CONTROLLER_DEV_UDP);
				context.sendBroadcast(intent);
			}else if(msg.getAction() == MsgActionCode.BACKUP_OVER){//103备份结束
				Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
				intent.putExtra(AppConst.RESP_MSG, msg);
				intent.putExtra(AppConst.RESP_CODE, 200);
				intent.putExtra(AppConst.RESP_TYPE, MsgTypeCode.CONTROLLER_DEV_UDP);
				context.sendBroadcast(intent);
			}
		}

}
