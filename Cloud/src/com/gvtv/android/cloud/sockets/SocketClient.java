package com.gvtv.android.cloud.sockets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import android.content.Context;
import android.content.Intent;

import com.gvtv.android.cloud.AppConst;
import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.msg.MessageUtils;
import com.gvtv.android.cloud.msg.MsgResponseCode;
import com.gvtv.android.cloud.msg.MsgTypeCode;
import com.gvtv.android.cloud.service.MessageService;
import com.gvtv.android.cloud.util.CCompatibleUtils;
import com.gvtv.android.cloud.util.IPv4Util;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.PreferenceUtils;
import com.gvtv.android.cloud.util.crypto.AESCrypto;

public class SocketClient {
	public static final int CLIENT_ADDR = 1;
	public static final int CLIENT_LOAD = 2;
	public static final int CLIENT_BUSINESS = 3;

	public static final int BUFFER_LENGTH = 1024 * 4;

	private Socket socket;
	private InputStream is;
	private OutputStream os;

	private boolean isConnected;

	private Context context;
	private String ip;
	private int port;
	public int client_curr;

	private static SocketClient mClient_addr;
	private static SocketClient mClient_load;
	private static SocketClient mClient_business;

	private MsgRecvThread mMsgRecvThread;

	public SocketClient(String ip, int port) {
		this.context = CloudApplication.getInstance();
		this.ip = ip;
		this.port = port;
	}

	public static SocketClient getInstace(int client) {
		switch (client) {
		case CLIENT_ADDR:
			if (mClient_addr == null) {
				mClient_addr = new SocketClient("mClient_addr", AppConst.SERVER_PORT);
			}
			mClient_addr.client_curr = client;
			return mClient_addr;

		case CLIENT_LOAD:
			if (mClient_load == null) {
				if(CloudApplication.loadAddes.size() == 0){
					mClient_load = new SocketClient(
							PreferenceUtils.getLoadIP(CloudApplication.getInstance()),
							(short)(PreferenceUtils.getLoadPort(CloudApplication.getInstance())));

				}else{
					mClient_load = new SocketClient(
							CloudApplication.loadAddes.get(0).getserverIP(),
							CloudApplication.loadAddes.get(0).getserverPort());
				}
			} else {
				if(CloudApplication.loadAddes.size() == 0){
					mClient_load.ip =PreferenceUtils.getLoadIP(CloudApplication.getInstance());
					mClient_load.port = (short)(PreferenceUtils.getLoadPort(CloudApplication.getInstance()));
				}else{
					mClient_load.ip = CloudApplication.loadAddes.get(0).getserverIP();
					mClient_load.port = CloudApplication.loadAddes.get(0).getserverPort();
				}
			}
			mClient_load.client_curr = client;
			return mClient_load;

		case CLIENT_BUSINESS:
			if (mClient_business == null) {
				if(CloudApplication.bizAddes.size() == 0){
					mClient_business = new SocketClient(
							PreferenceUtils.getBizIP(CloudApplication.getInstance()),
							(short)(PreferenceUtils.getBizTCPPort(CloudApplication.getInstance())));
				}else{
					mClient_business = new SocketClient(CloudApplication.bizAddes.get(0)
							.getserverIP(), CloudApplication.bizAddes.get(0).getserverPort());
				}
			} else {
				if(CloudApplication.bizAddes.size() == 0){
					mClient_business.ip = PreferenceUtils.getBizIP(CloudApplication.getInstance());
					mClient_business.port = (short)(PreferenceUtils.getBizTCPPort(CloudApplication.getInstance()));
				}else{
					mClient_business.ip = CloudApplication.bizAddes.get(0).getserverIP();
					mClient_business.port = CloudApplication.bizAddes.get(0).getserverPort();
				}
			}
			mClient_business.client_curr = client;
			return mClient_business;

		default:
			break;
		}

		return null;
	}

	public boolean isSocketConnected() {
		return socket != null && !socket.isClosed() && !socket.isInputShutdown()
				&& !socket.isOutputShutdown() && isConnected;
	}

	public void connect() throws UnknownHostException, IOException {
		if (isSocketConnected()) {
			LogUtils.getLog(getClass()).verbose("use a old  connection=======");
		} else {
			if (ip != null && ip.equalsIgnoreCase("mClient_addr")) {
				InetAddress address;
				try {
					address = InetAddress.getByName(AppConst.SERVER_URL);
					ip = address.getHostAddress();
				} catch (UnknownHostException e) {
					LogUtils.getLog(SocketClient.class).error(e.toString() + "===");
				}
				LogUtils.getLog(SocketClient.class).verbose("server_ip: " + ip);
			}
			//socket = new Socket(ip, port);
			InetAddress addr = InetAddress.getByName(ip);  
		    socket = new Socket();  
		    socket.connect(new InetSocketAddress(addr, port ),15000);  
		    //socket.setSendBufferSize(100);
			if (client_curr != CLIENT_BUSINESS) {
				socket.setSoTimeout(12000);
			}else{
				socket.setSoTimeout(12000);
			}
			socket.setTcpNoDelay(true);
			socket.setSoLinger(true, 0);
			os = socket.getOutputStream();
			is = socket.getInputStream();
			isConnected = true;
			mMsgRecvThread = new MsgRecvThread();
			mMsgRecvThread.setPriority(Thread.MAX_PRIORITY);
			mMsgRecvThread.start();
			LogUtils.getLog(getClass()).verbose("create a new  connection=======");
		}
		LogUtils.getLog(getClass()).verbose("ip: " + ip + ",port: " + port);
	}

	public synchronized void sendUrgentData() throws Exception {
		if (isSocketConnected()) {
			socket.sendUrgentData(0xFF);
		} else {
			throw new Exception("socket is null");
		}
	}

	public synchronized boolean send(byte[] b) throws Exception {
		LogUtils.getLog(getClass()).verbose("send=====");
		boolean isSuccess = false;
		os.write(b);
		os.flush();
		isSuccess = true;
		return isSuccess;
	}

	public synchronized void disconnect() {
		isConnected = false;
		try {
			if (socket != null) {
				socket.shutdownOutput();
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		try {
			if (socket != null) {
				socket.shutdownInput();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if (is != null) {
			try {
				is.close();
				is = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (os != null) {
			try {
				os.close();
				os = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (socket != null) {
			try {
				socket.close();
				socket = null;
				LogUtils.getLog(getClass()).verbose("socket is disconnected=======");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 不断接收消息的线程
	class MsgRecvThread extends Thread {
		byte[] buffer = new byte[BUFFER_LENGTH];
		byte[] b = new byte[4];
		ByteBuffer bytebuffer = null;
		int total_len = 0;
		int recv_len = 0;
		int single_len = 0;// 单个包长度
		
		public void init(){
			bytebuffer = null;
			total_len = 0;
			recv_len = 0;
			single_len = 0;// 单个包长度
		}
		
		// 接收消息线程的构造方法
		public MsgRecvThread() {
		}
		
		public void recursionRead(){
			if(recv_len == 0){
				for (int i = 0; i < b.length; i++) {
					b[i] = buffer[i];
				}
				total_len = CCompatibleUtils.bytesToInt(b);
				if(total_len > 1024 * 10 || total_len <= 0){
					return;
				}
				bytebuffer = ByteBuffer.allocate(total_len);
			}
			if(total_len - recv_len > single_len){//需继续收
				bytebuffer.put(buffer, 0, single_len);
				recv_len += single_len;
			}else if(total_len - recv_len < single_len){//收了下一个消息
				bytebuffer.put(buffer, 0, total_len - recv_len);
				new MsgParseThread(bytebuffer).start();
				
				ByteBuffer temp = ByteBuffer.allocate(BUFFER_LENGTH);
				single_len -= total_len - recv_len;
				temp.put(buffer, total_len - recv_len, single_len);
				buffer = temp.array();
				recv_len = 0;
				total_len = 0;
				recursionRead();//递归
			}else if(total_len - recv_len == single_len){
				bytebuffer.put(buffer, 0, single_len);
				new MsgParseThread(bytebuffer).start();
				recv_len = 0;
				total_len = 0;
			}
		}

		public void run() {
			
			while (isConnected) {
				try {
					if(is == null){
						throw new Exception("InputStream  is null");
					}
					while((single_len = is.read(buffer, 0, BUFFER_LENGTH)) != -1){  
						recursionRead();
                    }
				}catch (SocketTimeoutException e) {
					LogUtils.getLog(getClass()).error(e.toString());
					try {
						is = socket.getInputStream();
						init();
					} catch (IOException e1) {
						disconnect();
						LogUtils.getLog(getClass()).error(e1.toString());
					}
					if(client_curr == CLIENT_BUSINESS){
						Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
						intent.putExtra(AppConst.RESP_TYPE, MsgTypeCode.TIME_OUT);
						intent.putExtra(AppConst.RESP_CODE, MsgResponseCode.OK);
						context.sendBroadcast(intent);
					}else{
						Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
						intent.putExtra(AppConst.RESP_TYPE, MsgTypeCode.TIME_OUT);
						intent.putExtra(AppConst.RESP_CODE, MsgResponseCode.OK);
						context.sendBroadcast(intent);
						context.sendBroadcast(intent);
					}
				} catch (IOException e) {
					disconnect();
					LogUtils.getLog(getClass()).error(e.toString());
				} catch (Exception e) {
					LogUtils.getLog(getClass()).error(e.toString());
				}
			}
			LogUtils.getLog(getClass()).error("socket recv end!!!!!!!!!!!!!");
		}

		class MsgParseThread extends Thread {
			ByteBuffer bytebuffer = null;

			public MsgParseThread(ByteBuffer bytebuffer) {
				this.bytebuffer = bytebuffer;
			}

			@Override
			public void run() {
				msgHandle(bytebuffer);
			}
		}

		public void msgHandle(ByteBuffer bytebuffer) {
			int type = 0;
			int ret = 0;
			int requestId = 0;
			requestId = MessageUtils.parseRequestId(bytebuffer);
			type = MessageUtils.parseType(bytebuffer);
			ret = MessageUtils.parseResp(bytebuffer);
			LogUtils.getLog(getClass()).verbose("requestId: " + requestId + ",type: " + type + ",ret: " + ret);
			if (ret == MsgResponseCode.NO_ACCESS) {
				LogUtils.getLog(getClass()).error("NO_ACCESS==业务线不可用");
				Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
				intent.putExtra(AppConst.RESP_CODE, ret);
				intent.putExtra(AppConst.RESP_TYPE, type);
				context.sendBroadcast(intent);
				disconnect();
			} else {
				if (type == MsgTypeCode.CONTROLLER_ADDR_RESP) {// 控制端寻址响应0x102
					MessageUtils.parseLoadServer(bytebuffer);
					Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
					intent.putExtra(AppConst.RESP_CODE, ret);
					intent.putExtra(AppConst.RESP_TYPE, type);
					context.sendBroadcast(intent);
					disconnect();
				} else if (type == MsgTypeCode.SECRET_KEY_REQ) {// 密钥协商0x01
					if (ret == 511) {// 密钥重复协商
						// 不做处理
					} else {
						if (requestId % 2 == 1 && ret == 200) {
							try {
								send(getlogin_req_buf());
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
							intent.putExtra(AppConst.RESP_CODE, ret);
							intent.putExtra(AppConst.RESP_TYPE, type);
							context.sendBroadcast(intent);
						}
					}
				} else if (type == MsgTypeCode.CONTROLLER_LOGIN_REQ) {// 控制端登录0x02
					if (requestId % 2 == 1) {
						Intent serviceintent = new Intent(context, MessageService.class);
						context.startService(serviceintent);
					} else {
						MessageUtils.parseTomDeviceInfo(bytebuffer);
						Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
						intent.putExtra(AppConst.RESP_CODE, ret);
						intent.putExtra(AppConst.RESP_TYPE, type);
						context.sendBroadcast(intent);
					}
				} else if (type == MsgTypeCode.USER_REGISTER_REQ) {// 注册0x04
					byte[] bb;
					byte[] b = new byte[4];
					bytebuffer.position(16);
					bytebuffer.get(b);
					int msg_len = CCompatibleUtils.bytesToInt(b);
					bb = new byte[msg_len];
					bytebuffer.position(20);
					bytebuffer.get(bb);
					String msg_ret = null;
					if (ret == 200) {
						msg_ret = AESCrypto.aesDecryption(new String(bb));
					} else {
						msg_ret = new String(bb);
					}

					Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
					intent.putExtra(AppConst.RESP_CODE, ret);
					intent.putExtra(AppConst.RESP_TYPE, type);
					intent.putExtra(AppConst.RESP_MSG, msg_ret);
					context.sendBroadcast(intent);
					disconnect();
				} else if (type == MsgTypeCode.PWD_MODIFY_REQ) {// 修改密码密码0x05
					Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
					intent.putExtra(AppConst.RESP_CODE, ret);
					intent.putExtra(AppConst.RESP_TYPE, type);
					context.sendBroadcast(intent);
					disconnect();
				} else if (type == MsgTypeCode.PWD_RETRIEVE_REQ) {// 找回密码0x06
					Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
					intent.putExtra(AppConst.RESP_CODE, ret);
					intent.putExtra(AppConst.RESP_TYPE, type);
					context.sendBroadcast(intent);
					disconnect();
				} else if (type == MsgTypeCode.COMMAND_FORWARDING_REQ) {// 转发消息0x07
					MsgBean msg;
					msg = MessageUtils.parseMsg(bytebuffer);
					Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
					
					intent.putExtra(AppConst.RESP_MSG, msg);
					intent.putExtra(AppConst.RESP_CODE, ret);
					intent.putExtra(AppConst.RESP_TYPE, type);
					context.sendBroadcast(intent);
				} else if (type == MsgTypeCode.DEVICE_BIND_REQ) {// 设备绑定0x08
					Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
					intent.putExtra(AppConst.RESP_CODE, ret);
					intent.putExtra(AppConst.RESP_TYPE, type);
					context.sendBroadcast(intent);
				} else if (type == MsgTypeCode.DEVICE_UNBIND_REQ) {// 解绑设备0x09
					Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
					intent.putExtra(AppConst.RESP_CODE, ret);
					intent.putExtra(AppConst.RESP_TYPE, type);
					context.sendBroadcast(intent);
				} else if (type == MsgTypeCode.INSTALL_UNINSTALL_REQ) {// 安装卸载应用0x0a
					Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
					intent.putExtra(AppConst.RESP_CODE, ret);
					intent.putExtra(AppConst.RESP_TYPE, type);
					context.sendBroadcast(intent);
				} else if (type == MsgTypeCode.UDP_NATS_REQ) {// 协助 P2P 穿透接口0x0b
					Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
					intent.putExtra(AppConst.RESP_CODE, ret);
					intent.putExtra(AppConst.RESP_TYPE, type);

					if (ret == 200) {
						byte[] b = new byte[4];
						bytebuffer.position(16);
						bytebuffer.get(b);
						int ip = CCompatibleUtils.bytesToInt(b);
						CloudApplication.nat_ip = IPv4Util.intToIp(ip);

						bytebuffer.position(20);
						bytebuffer.get(b);
						CloudApplication.nat_port = CCompatibleUtils.bytesToInt(b);
						if(CloudApplication.nat_port < 0){
							CloudApplication.nat_port = (short)CloudApplication.nat_port  & 0x0FFFF;
						}
						
						LogUtils.getLog(getClass()).verbose(
								"CloudApplication.nat_ip: " + CloudApplication.nat_ip);
						LogUtils.getLog(getClass()).verbose(
								"CloudApplication.nat_port: " + CloudApplication.nat_port);
					}
					context.sendBroadcast(intent);
				} else if (type == MsgTypeCode.BIND_QUERY_REQ) {// 绑定状态查询0x0d
					MessageUtils.parseBindStatusSearchMsg(bytebuffer);
					Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
					intent.putExtra(AppConst.RESP_CODE, ret);
					intent.putExtra(AppConst.RESP_TYPE, type);
					context.sendBroadcast(intent);
				} else if (type == MsgTypeCode.USER_APPID_REQ) {///获取appid信息0x0e
					MessageUtils.parseAppid(bytebuffer);
					Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
					intent.putExtra(AppConst.RESP_CODE, ret);
					intent.putExtra(AppConst.RESP_TYPE, type);
					context.sendBroadcast(intent);
				} else if (type == MsgTypeCode.ANOTHER_LOGIN_REQ) {// 被T下线0x0f
					Intent tcpIntent = new Intent(context, MessageService.class);
					context.stopService(tcpIntent);
					Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
					intent.putExtra(AppConst.RESP_CODE, ret);
					intent.putExtra(AppConst.RESP_TYPE, type);
					context.sendBroadcast(intent);
				} else if (type == MsgTypeCode.CONTROLLER_REG_RESP) {// 注册负载响应
					MessageUtils.parseBizServer(bytebuffer);
					Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
					intent.putExtra(AppConst.RESP_TYPE, type);
					context.sendBroadcast(intent);
					disconnect();
				} else if (type == MsgTypeCode.CONTROLLER_REG_LOGIN_RESP) {// 登录负载响应
					MessageUtils.parseBizServerLogin(bytebuffer);
					Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
					intent.putExtra(AppConst.RESP_TYPE, type);
					context.sendBroadcast(intent);
					disconnect();
				}else if (type == MsgTypeCode.FILE_FORWARDING_REQ) {// 文件转发
					MsgBean msg = new MsgBean();
					byte[] b = new byte[4];
					
					bytebuffer.position(12);
					bytebuffer.get(b);
					int cont_len = CCompatibleUtils.bytesToInt(b);
					LogUtils.getLog(MessageUtils.class).verbose("restore cont_len" + cont_len);
					
					bytebuffer.position(16);
					bytebuffer.get(b);
					msg.setVersion(CCompatibleUtils.bytesToIntLittleEndian(b));

					bytebuffer.position(20);
					bytebuffer.get(b);
					int json_len = CCompatibleUtils.bytesToIntLittleEndian(b);
					LogUtils.getLog(MessageUtils.class).verbose("json_len" + json_len);

					bytebuffer.position(24);
					bytebuffer.get(b);
					msg.setSequence(CCompatibleUtils.bytesToIntLittleEndian(b));

					bytebuffer.position(28);
					bytebuffer.get(b);
					msg.setAction(CCompatibleUtils.bytesToIntLittleEndian(b));

					bytebuffer.position(32);
					bytebuffer.get(b);
					msg.setRet(CCompatibleUtils.bytesToIntLittleEndian(b));

					bytebuffer.position(36);
					bytebuffer.get(b);
					msg.setOffset(CCompatibleUtils.bytesToIntLittleEndian(b));

					bytebuffer.position(40);
					bytebuffer.get(b);
					msg.setFilesize(CCompatibleUtils.bytesToIntLittleEndian(b));

					byte[] bb = new byte[32];
					bytebuffer.position(44);
					bytebuffer.get(bb);
					try {
						msg.setFileName(new String(bb,"UTF-8").trim());
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}

					Intent intent = new Intent(AppConst.MSG_RECV_ACTION);
					if (json_len > 0 && json_len < 1024 * 10) {
						intent.putExtra(AppConst.BACKUP_BYTE, MessageUtils.parseRestore(bytebuffer, json_len));
					}
					intent.putExtra(AppConst.RESP_MSG, msg);
					intent.putExtra(AppConst.RESP_CODE, ret);
					intent.putExtra(AppConst.RESP_TYPE, type);
					context.sendBroadcast(intent);
				}
			}
		}

		public byte[] getlogin_req_buf() throws UnsupportedEncodingException, Exception {
			byte[] buf_key_login = null;
			byte[] buf_login_req = null;
			CloudApplication.requestID_relogin += 2;
			buf_key_login = AESCrypto.aesCrypto(CloudApplication.user_pwd);
			buf_login_req = MessageUtils.buildLoginMsg(CloudApplication.user_name, buf_key_login,
					CloudApplication.requestID_relogin);
			return buf_login_req;
		}
	}

}
