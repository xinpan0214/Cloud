package com.gvtv.android.cloud.msg;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.bean.AppInfo;
import com.gvtv.android.cloud.bean.DeviceInfo;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.bean.ServerAddress;
import com.gvtv.android.cloud.util.CCompatibleUtils;
import com.gvtv.android.cloud.util.IPv4Util;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.StringUtils;
import com.gvtv.android.cloud.util.crypto.AESCrypto;

/**
 * 消息依次包括： MSG-LEN(int) MSG-ID(int) TYPE(int) CON-LEN(int) CONTENT SEPARATE(int)
 * 其中CONTENT包括：version(int) Json-len(int) sequence(int) action(int) Return（选int）
 * Offset(选int) Filesize(选int) Filename（选char[]） Json-cont(cgar[])
 */

public class MessageUtils {

	
	/*
	 * 请求包数据格式：4字节单位 MSG-LEN MSG-ID TYPE CON-LEN CONTENT SEPARATE 数据包长度 数据包序号
	 * 0X07 内容长度 命令内容 分隔符0x00 响应包数据格式 MSG-LEN MSG-ID TYPE RETURN CON-LEN CONTENT
	 * SEPARATE 数据包长度 数据包序号 0X07 返回结果码 内容长度 命令内容 分隔符0x00
	 * MSG-LEN的长度表示整个数据包的长度，CON-LEN的长度表示CONTENT的长度。
	 * CONTENT内容为设备端与控制端的数据包：按以下格式封装： version Json-len sequence action Return（选）
	 * Offset(选) Filesize(选) Filename（选） Json-cont 4(int) 4(int) 4(int) 4(int)
	 * 4(int) 4(int) 4(int) 32(char) char 消息标识 Json长度 包序号 动作 返回标识 数据偏移 大小 名称
	 * Json内容
	 */

	public static final int SEPARATE = 0x00;

	/**
	 * @Title: getDeviceBindMsg
	 * @Description: res:{dev_name:dev_name,mac:mac,access_code:access_code}
	 * @return
	 */
	public static byte[] build_controller_addr_req_msg(int requestID) {
		ByteBuffer buffer = ByteBuffer.allocate(44);
		buffer.put(CCompatibleUtils.intToBytes(44));
		buffer.put(CCompatibleUtils.intToBytes(requestID));
		buffer.put(CCompatibleUtils.intToBytes(MsgTypeCode.CONTROLLER_ADDR_REQ));
		try {
			buffer.put("Controller\0".getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return buffer.array();
	}
	
	/*请求消息： 
	名称  类型  意义 
	Request len  4 字节，整型  请求包长度 
	Request id  4 字节，整型  请求包 ID 
	Request type  4 字节，整型  请求包类型：0x0b 
	Type  4 字节，整型  0:控制端，  1:设备端 
	usrnamelen  4 字节，整型  用户名长度 
	usrname  字符串  用户名 
	Separate  4 字节，整型  分隔符：0x00 */
	public static byte[] build_nats_req_msg(int requestID) {
		byte[] nameBuf = null;
		try {
			nameBuf = CloudApplication.user_name.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		int msg_len = 24 + nameBuf.length;
		ByteBuffer buffer = ByteBuffer.allocate(msg_len);
		buffer.put(CCompatibleUtils.intToBytes(msg_len));
		buffer.put(CCompatibleUtils.intToBytes(requestID));
		buffer.put(CCompatibleUtils.intToBytes(MsgTypeCode.UDP_NATS_REQ));
		buffer.put(CCompatibleUtils.intToBytes(nameBuf.length));
		buffer.put(nameBuf);
		buffer.put(CCompatibleUtils.intToBytes(0x00));
		return buffer.array();
	}
	//尝试穿透消息包
//	version	Json-len	sequence	action	Return（选）	Offset(选)	Filesize(选)	Filename（选）	Json-cont
//	4(int)	4(int)	4(int)	4(int)	4(int)	4(int)	4(int)	32(char)	char
//	消息标识	Json长度	包序号	动作	返回标识	数据偏移	大小	名称	Json内容
	public static byte[] build_nats_try_msg(int requestID) {
		int msg_len = 60;
		ByteBuffer buffer = ByteBuffer.allocate(msg_len);
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(9527));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(0));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(requestID));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(100));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(0));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(0));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(0));
		return buffer.array();
	}
	

	/*
	 * 请求消息： 名称 类型 意义 Request len 4 字节，整型 请求包长度 Request id 4 字节，整型 请求包 ID
	 * Request type 4 字节，整型 请求包类型：0x02 Usrname len 4 字节，整型 用户名字符串长度 Usrname 字符串
	 * 用户名 Pwdlen 4 字节，整型 Password长度 Passwrod 字节流 密码(对称秘钥加密) Separate 4 字节，整型
	 * 分隔符：0x00
	 */
	public static byte[] buildLoginMsg(String account, byte[] buf_key_login, int requestID) {
		int msg_len = 0;
		try {
			msg_len = 24 + account.getBytes("UTF-8").length + buf_key_login.length;
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		ByteBuffer buffer = ByteBuffer.allocate(msg_len);
		buffer.put(CCompatibleUtils.intToBytes(msg_len));
		buffer.put(CCompatibleUtils.intToBytes(requestID));
		buffer.put(CCompatibleUtils.intToBytes(0x02));
		try {
			buffer.put(CCompatibleUtils.intToBytes(account.getBytes("UTF-8").length));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		try {
			buffer.put(account.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		buffer.put(CCompatibleUtils.intToBytes(buf_key_login.length));
		buffer.put(buf_key_login);
		buffer.put(CCompatibleUtils.intToBytes(0x00));
		return buffer.array();
	}

	/*
	 * 请求消息： 注册 account=%s&password=%s&bizline=0001&terminal=2&status=1 名称 类型 意义
	 * Request len 4 字节，整型 请求包长度 Request id 4 字节，整型 请求包 ID Request type 4 字节，整型
	 * 请求包类型：0x04 Msglen 4 字节，整型 消息体字节流长度 Msg 字节流 消息体(对称秘钥加密) Separate 4 字节，整型
	 * 分隔符：0x00
	 */
	public static byte[] buildRegisterMsg(String account, String pwd, int requestID) throws Exception {
		StringBuffer msgBuffer = new StringBuffer();
		byte[] msg;
		//status:0停用1正常2未激活，默认2未激活
		msgBuffer.append("account=").append(account).append("&").append("password=").append(pwd)
				.append("&bizline=0001&terminal=2&status=2");

		String m = msgBuffer.toString();
		msg = AESCrypto.aesCrypto(m);

		int msg_len = 20 + msg.length;
		ByteBuffer buffer = ByteBuffer.allocate(msg_len);
		buffer.put(CCompatibleUtils.intToBytes(msg_len));
		buffer.put(CCompatibleUtils.intToBytes(requestID));
		buffer.put(CCompatibleUtils.intToBytes(0x04));
		buffer.put(CCompatibleUtils.intToBytes(msg.length));
		buffer.put(msg);
		buffer.put(CCompatibleUtils.intToBytes(0x00));

		return buffer.array();
	}

	/*响应消息： 
	名称  类型  意义 
	Response len  4 字节，整型  响应包长度：0x14 
	Response id  4 字节，整型  响应包 ID 
	Response type  4 字节，整型  响应包类型：0x02 
	Rtn  4 字节，整型  响应结果代码 
	Devnamelen  4 字节，整型  设备名称长度 
	devname  字节流  设备名称 
	APPinfolen  4 字节，整型  App信息字节流长度 
	Appinfo  字节流  App信息字节流 
	Separate  4 字节，整型  分隔符：0x00 */

	public static void parseTomDeviceInfo(ByteBuffer buffer) {
		int position;
		if(CloudApplication.bindedDeviceInfo == null){
			CloudApplication.bindedDeviceInfo = new DeviceInfo();
		}
		byte[] b = new byte[4];

		position = 16;
		buffer.position(position);
		buffer.get(b);
		int devnamelen = CCompatibleUtils.bytesToInt(b);

		position = 20;
		buffer.position(position);
		byte[] bb = new byte[devnamelen];
		buffer.get(bb);
		try {
			CloudApplication.bindedDeviceInfo.setDev_name(new String(bb,"UTF-8"));
		} catch (UnsupportedEncodingException e3) {
			e3.printStackTrace();
		}

		position = 20 + devnamelen;
		//app数量
		buffer.position(position);
		buffer.get(b);
		int num = CCompatibleUtils.bytesToInt(b);
		CloudApplication.bindedDeviceInfo.setApp_amount(num);
		
		
		/*App 信息字节流格式 
		num  Appidlen  Urllen  Namelen  appid  url  Name  …...  appidlen  Urllen  namelen  appid  url  name */
//		ArrayList<AppInfo> apps = CloudApplication.bindedDeviceInfo.getApps();
//		position = 28 + devnamelen;
//		for (int i = 0; i < num; i++) {
//			
//			AppInfo app = new AppInfo();
//			buffer.position(position);//appidlen
//			buffer.get(b);
//			int appidlen = CCompatibleUtils.bytesToInt(b);
//			position += 4;
//			
//			buffer.position(position);//urllen
//			buffer.get(b);
//			int url_len = CCompatibleUtils.bytesToInt(b);
//			position += 4;
//			
//			buffer.position(position);//namelen
//			buffer.get(b);
//			int name_len = CCompatibleUtils.bytesToInt(b);
//			position += 4;
//			
//			buffer.position(position);//appid
//			byte[] bbb = new byte[appidlen];
//			buffer.get(bbb);
//			String appcode = "";
//			try {
//				appcode = new String(bbb,"UTF-8").trim();
//				app.setAppcode(appcode);
//			} catch (UnsupportedEncodingException e2) {
//				e2.printStackTrace();
//			}
//			position += appidlen;
//			
//			buffer.position(position);//url
//			byte[] bbbb = new byte[url_len];
//			buffer.get(bbbb);
//			try {
//				app.setDownloadurl(new String(bbbb,"UTF-8").trim());
//			} catch (UnsupportedEncodingException e1) {
//				e1.printStackTrace();
//			}
//			position += url_len;
//			
//			buffer.position(position);//namelen
//			byte[] bbbbb = new byte[name_len];
//			buffer.get(bbbbb);
//			try {
//				app.setAppname(new String(bbbbb,"UTF-8").trim());
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//			position += name_len;
//			apps.add(app);
//			LogUtils.getLog(MessageUtils.class).verbose(app.getAppcode() + "==" + app.getAppname() + "=" + app.getDownloadurl());
//		}
		LogUtils.getLog(MessageUtils.class).verbose(
				"MsgLogin" + ",Devname===" + CloudApplication.bindedDeviceInfo.getDev_name()
						+ ",AppInfo==" + CloudApplication.bindedDeviceInfo.getApp_amount());
	}
	
	
//	请求消息：
//	名称 类型 意义
//	Request len 4 字节，整型 请求包长度
//	Request id 4 字节，整型 请求包 ID
//	Request type 4 字节，整型 请求包类型：0x0e
//	App id page number 4 字节，整型 APP 信息分页页码（每页 20
//	个）
//	Separate 4 字节，整型 分隔符：0x00
	public static byte[] buildAppidReq(int requestID, int pageNum) {
		int msg_len = 20;
		ByteBuffer buffer = ByteBuffer.allocate(msg_len);
		buffer.put(CCompatibleUtils.intToBytes(msg_len));
		buffer.put(CCompatibleUtils.intToBytes(requestID));
		buffer.put(CCompatibleUtils.intToBytes(MsgTypeCode.USER_APPID_REQ));
		buffer.put(CCompatibleUtils.intToBytes(pageNum));
		buffer.put(CCompatibleUtils.intToBytes(0x00));
		return buffer.array();
	}
	
	
//	响应消息：
//	名称 类型 意义
//	Response len 4 字节，整型 响应包长度
//	Response id 4 字节，整型 响应包 ID
//	Response type 4 字节，整型 响应包类型：0x0e
//	Rtn 4 字节，整型 响应结果代码
//	App info len 4 字节，整型 APPid流长度
//	App info 字节流 Appid 流
//	Separate 4 字节，整型 分隔符：0x00
	
	
//	Appinfo格式如下：
//	Appidnum( 四 字 节
//	整数)
//	Appidlen(四字节整
//	数)
//	Appid（字节流） ...... Appidlen( 四 字=======================还未改完20140709
//	节整数)
	public static void parseAppid(ByteBuffer buffer) {
		if(CloudApplication.bindedDeviceInfo == null){
			CloudApplication.bindedDeviceInfo = new DeviceInfo();
		}
		byte[] bb;
		
		int position = 16;
		byte[] b = new byte[4];
		buffer.position(position);
		buffer.get(b);
		int appInfoLen = CCompatibleUtils.bytesToInt(b);
		if(appInfoLen >= 4){
			position = 20;
			buffer.position(position);
			buffer.get(b);
			int appidnum = CCompatibleUtils.bytesToInt(b);
			
			position = 24;
			int appidlen;
			for (int i = 0; i < appidnum; i++) {
				buffer.position(position);
				buffer.get(b);
				appidlen = CCompatibleUtils.bytesToInt(b);
				position += 4;
				
				buffer.position(position);
				bb = new byte[appidlen];
				buffer.get(bb);
				try {
					String appid = new String(bb, "UTF-8");
					CloudApplication.bindedDeviceInfo.getAppids().add(appid);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				position += appidlen;
			}
		}
	}

	

	/*
	 * 请求消息： 名称 类型 意义 Request len 4 字节，整型 请求包长度 Request id 4 字节，整型 请求包 ID
	 * Request type 4 字节，整型 请求包类型：0x06 Usrname len 4 字节，整型 用户名字符串长度 Usrname 字符串
	 * 用户名 Separate 4 字节，整型 分隔符：0x00
	 */
	public static byte[] build_PWD_retrieve_msg(String account,int requestID) throws Exception {
		
		int msg_len = 20 + account.getBytes("UTF-8").length;
		LogUtils.getLog(MessageUtils.class).verbose("msg_len：" + msg_len);
		ByteBuffer buffer = ByteBuffer.allocate(msg_len);
		buffer.put(CCompatibleUtils.intToBytes(msg_len));
		buffer.put(CCompatibleUtils.intToBytes(requestID));
		buffer.put(CCompatibleUtils.intToBytes(0x06));
		buffer.put(CCompatibleUtils.intToBytes(account.getBytes("UTF-8").length));
		buffer.put(account.getBytes("UTF-8"));
		buffer.put(CCompatibleUtils.intToBytes(0x00));

		return buffer.array();
	}
	
	public static byte[] build_command_forwardingbackup(MsgBean msg, byte[] buf, int requestID) {
		int header_len = 20;
		int Con_len;
		int json_len;
		json_len = buf.length;
		
		Con_len = json_len + 60;
		int msg_len = header_len + Con_len;
		
		ByteBuffer buffer = ByteBuffer.allocate(msg_len);
		buffer.put(CCompatibleUtils.intToBytes(msg_len));
		buffer.put(CCompatibleUtils.intToBytes(requestID));
		buffer.put(CCompatibleUtils.intToBytes(MsgTypeCode.COMMAND_FORWARDING_REQ));
		buffer.put(CCompatibleUtils.intToBytes(Con_len));

		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getVersion()));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(json_len));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getSequence()));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getAction()));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getRet()));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getOffset()));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getFilesize()));
		if(msg.getFileName() != null){
			try {
				buffer.put(msg.getFileName().getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		buffer.position(76);
		buffer.put(buf);
		buffer.put(CCompatibleUtils.intToBytes(SEPARATE));
		return buffer.array();
	}

	public static byte[] build_command_forwarding(MsgBean msg,int requestID) {
		int header_len = 20;
		int Con_len;
		int json_len;
		byte[] jsonBuf = null;
		if (msg.getJson_content() != null) {
			try {
				jsonBuf = msg.getJson_content().getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			json_len = jsonBuf.length;
		}else{
			json_len = 0;
		}
		
		Con_len = json_len + 60;
		int msg_len = header_len + Con_len;
		
		ByteBuffer buffer = ByteBuffer.allocate(msg_len);
		buffer.put(CCompatibleUtils.intToBytes(msg_len));
		buffer.put(CCompatibleUtils.intToBytes(requestID));
		buffer.put(CCompatibleUtils.intToBytes(0x07));
		buffer.put(CCompatibleUtils.intToBytes(Con_len));

		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getVersion()));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(json_len));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getSequence()));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getAction()));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getRet()));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getOffset()));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getFilesize()));
		if(msg.getFileName() != null){
			try {
				buffer.put(msg.getFileName().getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		buffer.position(76);
		if (msg.getJson_content() != null) {
			buffer.put(jsonBuf);
		}
		
		buffer.put(CCompatibleUtils.intToBytes(SEPARATE));
		return buffer.array();
	}

	public static MsgBean parseMsg(ByteBuffer buffer) {
		MsgBean msg = new MsgBean();
		byte[] b = new byte[4];
		
		buffer.position(16);
		buffer.get(b);
		int cont_len = CCompatibleUtils.bytesToInt(b);
		LogUtils.getLog(MessageUtils.class).verbose("cont_len" + cont_len);
		if(cont_len == 0){
			return msg;
		}
		buffer.position(20);
		buffer.get(b);
		msg.setVersion(CCompatibleUtils.bytesToIntLittleEndian(b));

		buffer.position(24);
		buffer.get(b);
		int json_len = CCompatibleUtils.bytesToIntLittleEndian(b);
		LogUtils.getLog(MessageUtils.class).verbose("json_len" + json_len);

		buffer.position(28);
		buffer.get(b);
		msg.setSequence(CCompatibleUtils.bytesToIntLittleEndian(b));

		buffer.position(32);
		buffer.get(b);
		msg.setAction(CCompatibleUtils.bytesToIntLittleEndian(b));

		buffer.position(36);
		buffer.get(b);
		msg.setRet(CCompatibleUtils.bytesToIntLittleEndian(b));

		buffer.position(40);
		buffer.get(b);
		msg.setOffset(CCompatibleUtils.bytesToIntLittleEndian(b));

		buffer.position(44);
		buffer.get(b);
		msg.setFilesize(CCompatibleUtils.bytesToIntLittleEndian(b));

		byte[] bb = new byte[32];
		buffer.position(48);
		buffer.get(bb);
		try {
			msg.setFileName(new String(bb,"UTF-8").trim());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (json_len > 0 && json_len < 1024 * 10) {
			try {
				if(msg.getAction() == MsgActionCode.RECOVER_BLOCK){
					
				}else{
					byte[] bbb = new byte[json_len];
					buffer.position(80);
					buffer.get(bbb);
					msg.setJson_content(new String(bbb,"UTF-8"));
					//LogUtils.getLog(MessageUtils.class).verbose(msg.getJson_content());
				}
			} catch (Exception e) {
				LogUtils.getLog(MessageUtils.class).error(e.toString());
			}
		}
		return msg;
	}
	
	
//	响应消息： 
//	名称  类型  意义 
//	Request len  4 字节，整型  请求包长度 
//	Request id  4 字节，整型  请求包 ID 
//	Request type  4 字节，整型  请求包类型：0x0d 
//	Rtn  4 字节，整型  响应结果代码 
//	Device num  4 字节，整型  待查询的设备个数 
//	Devcode len  4 字节，整型  设备唯一标识长度 
//	devcode  字符串  设备 code 
//	Status  4 字节，整型  1 绑定，-1 解绑，0 未找到 
//	…  …  … 
//	Devcode len  4 字节，整型  设备唯一标识长度 
//	devcode  字符串  设备 code 
//	Status  4 字节，整型  1 绑定，-1 解绑，0 未找到 
//	Separate  4 字节，整型  分隔符：0x00 
	public static void parseBindStatusSearchMsg(ByteBuffer buffer) {
		byte[] b = new byte[4];
		
		buffer.position(16);
		buffer.get(b);
		int num = CCompatibleUtils.bytesToInt(b);
		
		int position = 20;
		for (int i = 0; i < num; i++) {
			buffer.position(position);
			buffer.get(b);
			int devcode_len = CCompatibleUtils.bytesToInt(b);
			position += 4;
			
			if(devcode_len == 0){
				continue;
			}
			
			byte[] bb = new byte[devcode_len];
			buffer.position(position);
			buffer.get(bb);
			String devcode = null;
			try {
				devcode = new String(bb, "UTF-8");
				LogUtils.getLog(MessageUtils.class).verbose(devcode);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			position += devcode_len;
			
			buffer.position(position);
			buffer.get(b);
			int status = CCompatibleUtils.bytesToInt(b);
			position += 4;
			for (int j = 0; j < CloudApplication.devs.size(); j++) {
				if(CloudApplication.devs.get(j).getAccess_code().equalsIgnoreCase(devcode)){
					if(status  == 0){
						CloudApplication.devs.get(j).setStatus(-1);
					}else if(status == -1){
						CloudApplication.devs.get(j).setStatus(0);
					}else if(status == 1){
						CloudApplication.devs.get(j).setStatus(1);
					}
					break;
				}
			}
			
		}
	}
	
	//version	Json-len	sequence	action	Return（选）	Offset(选)	Filesize(选)	Filename（选）	Json-cont
	//消息标识	Json长度	包序号	动作	返回标识	数据偏移	大小	名称	Json内容
	public static MsgBean parseUDPMsg(ByteBuffer buffer) {
		MsgBean msg = new MsgBean();
		byte[] b = new byte[4];
		byte[] bb = new byte[32];
		
		buffer.position(0);
		buffer.get(b);
		int version = CCompatibleUtils.bytesToIntLittleEndian(b);
		msg.setVersion(version);
		
		buffer.position(8);
		buffer.get(b);
		int sequence = CCompatibleUtils.bytesToIntLittleEndian(b);
		msg.setSequence(sequence);
		
		buffer.position(12);
		buffer.get(b);
		int action = CCompatibleUtils.bytesToIntLittleEndian(b);
		msg.setAction(action);
		
		buffer.position(16);
		buffer.get(b);
		int ret = CCompatibleUtils.bytesToIntLittleEndian(b);
		msg.setRet(ret);
		
		buffer.position(20);
		buffer.get(b);
		int offset = CCompatibleUtils.bytesToIntLittleEndian(b);
		msg.setOffset(offset);
		
		buffer.position(24);
		buffer.get(b);
		int filesize = CCompatibleUtils.bytesToIntLittleEndian(b);
		msg.setFilesize(filesize);
		
		buffer.position(28);
		buffer.get(bb);
		try {
			String filename = new String(bb, "UTF-8");
			msg.setFileName(filename);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return msg;
	}
	
	//version	Json-len	sequence	action	Return（选）	Offset(选)	Filesize(选)	Filename（选）	Json-cont
	//消息标识	Json长度	包序号	动作	返回标识	数据偏移	大小	名称	Json内容
	public static byte[] build_nats_backup_block_msg(MsgBean msg, byte[] backbyte) {
		int json_len = 60 + backbyte.length;
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(9527));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(json_len));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getSequence()));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getAction()));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getRet()));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getOffset()));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getFilesize()));
		try {
			buffer.put(msg.getFileName().getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		buffer.position(60);
		buffer.put(backbyte);
		return buffer.array();
	}
	
	public static byte[] build_UDP_msg(MsgBean msg) {
		int json_len = 60;
		ByteBuffer buffer = ByteBuffer.allocate(json_len);
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(9527));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(json_len));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getSequence()));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getAction()));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getRet()));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getOffset()));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(msg.getFilesize()));
		try {
			if(!StringUtils.isBlank(msg.getFileName())){
				buffer.put(msg.getFileName().getBytes("UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return buffer.array();
	}
	
	public static byte[] parseUDPRestore(ByteBuffer bytebuffer) {
		byte[] b = new byte[4];
		bytebuffer.position(4);
		bytebuffer.get(b);
		int json_len = CCompatibleUtils.bytesToIntLittleEndian(b);
		if(json_len > 0 && json_len < 1024 * 10){
			byte[] bbb = new byte[json_len];
			bytebuffer.position(60);
			bytebuffer.get(bbb);
			return bbb;
		}
		return null;
	}
	
	public static byte[] parseRestore(ByteBuffer bytebuffer,int json_len) {
		byte[] bbb = new byte[json_len];
		bytebuffer.position(76);
		bytebuffer.get(bbb);
		return bbb;
	}
	
//	CONTROLLER_REG_RESP
//	Offset Size Name Type Value
//	0 4-Bytes MessageLen int -
//	4 4-Bytes RequestID int
//	8 4-Bytes MessageType int 0x110
//	12 4-Bytes BussinessServerIP int
//	16 2-Bytes BussinessTCPServerPort Short int
//	18 2-Bytes BussinessUDPServerPort Short int
//	20 32-Bytes Agent char “LoadServer\0”
//	52
	
	public static int parseBizServer(ByteBuffer buffer) {
		CloudApplication.bizAddes.clear();
		byte[] b = new byte[4];
		byte[] bb = new byte[2];
		
		ServerAddress bizAddr = new ServerAddress();
		buffer.position(12);
		buffer.get(b);
		bizAddr.setserverIP(IPv4Util.intToIp(CCompatibleUtils.bytesToInt(b)));
		
		buffer.position(16);
		buffer.get(bb);
		bizAddr.setserverPort(CCompatibleUtils.bytesToShort(bb));
		
		buffer.position(18);
		buffer.get(bb);
		bizAddr.setServerUDPPort(CCompatibleUtils.bytesToShort(bb));
		CloudApplication.bizAddes.add(bizAddr);
		
		LogUtils.getLog(MessageUtils.class).verbose(bizAddr.getserverIP());
		LogUtils.getLog(MessageUtils.class).verbose(bizAddr.getserverPort() + "tcp");
		LogUtils.getLog(MessageUtils.class).verbose(bizAddr.getServerUDPPort() + "udp");
		
		LogUtils.getLog(MessageUtils.class).verbose("bizserveramount: " + CloudApplication.bizAddes.size());
		return CloudApplication.bizAddes.size();
	}
	
	
//	CONTROL_LOGIN_RESP
//	（用户没注册或者没绑定设备,随机返回一个业务服务器地址DeviceOnServerIP）
//	Offset Size Name Type Value
//	0 4-Bytes MessageLen int -
//	4 4-Bytes RequestID int
//	8 4-Bytes MessageType int 0x1202
//	12 128-
//	Bytes
//	UserID char Eg: ”abcdef789\0”
//	140 2-Bytes Num of Device Short int If  0 ,has no devices；
//	if -1,user has not register
//	142 128Bytes DeviceID char
//	270 4-Bytes DeviceOnServerIP int Not NULL
//	274 2-Bytes DeviceOnServerTCPPort Shor int
//	276 2-Bytes DeviceOnServerUDPPort Short int
//	142+136*n 32-Bytes Agent char “LoadServer\0”
//	174+136*n
	
	
	public static int parseBizServerLogin(ByteBuffer buffer) {
		CloudApplication.bizAddes.clear();
		byte[] b = new byte[4];
		byte[] bb = new byte[2];
		CloudApplication.bizAddes.clear();
		ServerAddress bizAddr = new ServerAddress();
		buffer.position(270);
		buffer.get(b);
		bizAddr.setserverIP(IPv4Util.intToIp(CCompatibleUtils.bytesToInt(b)));
		
		buffer.position(274);
		buffer.get(bb);
		bizAddr.setserverPort(CCompatibleUtils.bytesToShort(bb));
		
		buffer.position(276);
		buffer.get(bb);
		bizAddr.setServerUDPPort(CCompatibleUtils.bytesToShort(bb));
		
		CloudApplication.bizAddes.add(bizAddr);
		
		LogUtils.getLog(MessageUtils.class).verbose(bizAddr.getserverIP());
		LogUtils.getLog(MessageUtils.class).verbose(bizAddr.getserverPort() + "tcp");
		LogUtils.getLog(MessageUtils.class).verbose(bizAddr.getServerUDPPort() + "udp");
		
		LogUtils.getLog(MessageUtils.class).verbose("bizserveramount: " + CloudApplication.bizAddes.size());
		return CloudApplication.bizAddes.size();
	}

	public static int parseLoadServer(ByteBuffer buffer) {
		byte[] b = new byte[4];
		byte[] bb = new byte[2];

		buffer.position(12);
		buffer.get(b);
		int serveramount = CCompatibleUtils.bytesToInt(b);
		if(serveramount > 0){
			CloudApplication.loadAddes.clear();
		}
		for (int i = 0; i < serveramount; i++) {
			ServerAddress loadAddr = new ServerAddress();
			buffer.position(16 + 6 * i);
			buffer.get(b);
			loadAddr.setserverIP(IPv4Util.intToIp(CCompatibleUtils.bytesToInt(b)));

			buffer.position(20 + 6 * i);
			buffer.get(bb);
			loadAddr.setserverPort(CCompatibleUtils.bytesToShort(bb));
			CloudApplication.loadAddes.add(loadAddr);
			LogUtils.getLog(MessageUtils.class).verbose(loadAddr.getserverIP());
			LogUtils.getLog(MessageUtils.class).verbose(loadAddr.getserverPort() + "");
		}
		LogUtils.getLog(MessageUtils.class).verbose("loadserveramount: " + serveramount);
		return CloudApplication.loadAddes.size();
	}
	
	
//	CONTROLLER_REG_REQ
//	Offset Size Name Type Value
//	0 4-Bytes MessageLen int -
//	4 4-Bytes RequestID int
//	8 4-Bytes MessageType int 0x1101
//	12 32-Bytes Agent char “Controller\0”找业务服务器，未知用户名
//	44
	
	
	public static byte[] controller_reg_req_msg() {
		int msg_len = 44;
		ByteBuffer buffer = ByteBuffer.allocate(msg_len);
		buffer.put(CCompatibleUtils.intToBytes(msg_len));
		buffer.put(CCompatibleUtils.intToBytes(new Random().nextInt()));
		buffer.put(CCompatibleUtils.intToBytes(MsgTypeCode.CONTROLLER_REG_REQ));
		try {
			buffer.put("Controller\0".getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return buffer.array();
	}
	
//	ffset Size Name Type Value
//	0 4-Bytes MessageLen int -
//	4 4-Bytes RequestID int
//	8 4-Bytes MessageType int 0x1201
//	12 128-
//	Bytes
//	UserID char Egg:”abcd789\0”
//	140 32-Bytes Agent “Controller\0”
//	172
	//已知用户名
	public static byte[] controller_reg_req_loginmsg(String user_name) throws UnsupportedEncodingException {
		int msg_len = 172;
		ByteBuffer buffer = ByteBuffer.allocate(msg_len);
		buffer.put(CCompatibleUtils.intToBytes(msg_len));
		buffer.put(CCompatibleUtils.intToBytes(new Random().nextInt()));
		buffer.put(CCompatibleUtils.intToBytes(MsgTypeCode.CONTROLLER_REG_LOGIN_REQ));
		buffer.put((user_name + "\0").getBytes("UTF-8"));
		buffer.position(140);
		buffer.put("Controller\0".getBytes("UTF-8"));
		return buffer.array();
	}

	/*
	 * Request len 4 字节，整型 请求包长度 Request id 4 字节，整型 请求包 ID Request type 4 字节，整型
	 * 请求包类型：0x01 Pwdlen 4 字节，整型 Pwd字符串长度 Pwd 字符串 服务器公钥加密的对称秘钥（随机数），建议采用 256 位秘钥
	 * Separate 4 字节，整型 分隔符：0x00
	 */
	public static byte[] buildSecretkeyMsg(byte[] buf_key_agree, int requestID) {
		int msg_len = 20 + buf_key_agree.length;
		ByteBuffer buffer = ByteBuffer.allocate(msg_len);
		buffer.put(CCompatibleUtils.intToBytes(msg_len));
		buffer.put(CCompatibleUtils.intToBytes(requestID));
		buffer.put(CCompatibleUtils.intToBytes(0x01));
		buffer.put(CCompatibleUtils.intToBytes(buf_key_agree.length));
		buffer.put(buf_key_agree);
		buffer.put(CCompatibleUtils.intToBytes(0x00));
		return buffer.array();
	}

	/*
	 * 获取响应code
	 */
	public static int parseResp(ByteBuffer buffer) {
		byte[] b = new byte[4];

		buffer.position(12);
		buffer.get(b);
		int ret = CCompatibleUtils.bytesToInt(b);
		return ret;
	}
	
	/*
	 * 获取消息type
	 */
	public static int parseType(ByteBuffer buffer) {
		byte[] b = new byte[4];

		buffer.position(8);
		buffer.get(b);
		int type = CCompatibleUtils.bytesToInt(b);
		return type;
	}
	
	/*
	 * 获取消息type
	 */
	public static int parseRequestId(ByteBuffer buffer) {
		byte[] b = new byte[4];

		buffer.position(4);
		buffer.get(b);
		int requestId = CCompatibleUtils.bytesToInt(b);
		return requestId;
	}

	/*
	 * Request 
	 * len  4 字节，整型  请求包长度 
	 *id  4 字节，整型  请求包 ID 
	 * type 4 字节，整型 请求包类型：0x08 
	 * Usrnamelen 4 字节，整型 用户名长度 usrname 字节流 用户名
	 * Devcodelen 4 字节，整型 设备 code长度 
	 * devcode 字节流 设备 code 
	 * terminal 4 字节，整型 操作终端
	 * 		1=pc 2=移动设备 3=盒子
	 *devnamelen 4 字节，整型 设备名字长度 Devname 字符串 设备长度（最长 32 字节）
	 * Separate 4 字节，整型 分隔符：0x00
	 */
	public static byte[] buildBindMsg(DeviceInfo mDeviceInfo, String userName,int requestID) {
		byte[] dev_name = null;
		try {
			dev_name = mDeviceInfo.getDev_name().getBytes("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		byte[] dev_code = null;
		try {
			dev_code = mDeviceInfo.getAccess_code().getBytes("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		byte[] user_name_buf = null;
		try {
			user_name_buf = userName.getBytes("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		int id = requestID;
		
		int msg_len = 32 + dev_name.length + dev_code.length + user_name_buf.length;
		ByteBuffer buffer = ByteBuffer.allocate(msg_len);
		buffer.put(CCompatibleUtils.intToBytes(msg_len));
		buffer.put(CCompatibleUtils.intToBytes(id));
		buffer.put(CCompatibleUtils.intToBytes(0x08));
		buffer.put(CCompatibleUtils.intToBytes(user_name_buf.length));
		buffer.put(user_name_buf);
		buffer.put(CCompatibleUtils.intToBytes(dev_code.length));
		buffer.put(dev_code);
		buffer.put(CCompatibleUtils.intToBytes(2));
		buffer.put(CCompatibleUtils.intToBytes(dev_name.length));
		buffer.put(dev_name);
		buffer.put(CCompatibleUtils.intToBytes(0x00));
		return buffer.array();
	}
	
	/*请求消息： 
	名称  类型  意义 
	Request len  4 字节，整型  请求包长度 
	Request id  4 字节，整型  请求包 ID 
	Request type  4 字节，整型  请求包类型：0x09 
	Usrnamelen  4 字节，整型  用户名长度 
	usrname  字节流  用户名 
	Devcodelen  4 字节，整型  设备 code长度 
	devcode  字节流  设备 code 
	terminal  4 字节，整型  操作终端 
	1=pc 
	2=移动设备 
	3=盒子 
	Separate  4 字节，整型  分隔符：0x00 */
	public static byte[] buildUnbindMsg(DeviceInfo mDeviceInfo, String userName,int requestID) {
		byte[] dev_code = null;
		try {
			dev_code = mDeviceInfo.getAccess_code().getBytes("UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		byte[] user_name_buf = null;
		try {
			user_name_buf = userName.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		int id = requestID;
		
		int msg_len = 28 + dev_code.length + user_name_buf.length;
		ByteBuffer buffer = ByteBuffer.allocate(msg_len);
		buffer.put(CCompatibleUtils.intToBytes(msg_len));
		buffer.put(CCompatibleUtils.intToBytes(id));
		buffer.put(CCompatibleUtils.intToBytes(0x09));
		buffer.put(CCompatibleUtils.intToBytes(user_name_buf.length));
		buffer.put(user_name_buf);
		buffer.put(CCompatibleUtils.intToBytes(dev_code.length));
		buffer.put(dev_code);
		buffer.put(CCompatibleUtils.intToBytes(2));
		buffer.put(CCompatibleUtils.intToBytes(0x00));
		return buffer.array();
	}
	
//	Request len  4 字节，整型  请求包长度 
//	Request id  4 字节，整型  请求包 ID 
//	Request type  4 字节，整型  请求包类型：0x01 
//	Type  4 字节，整型  0:控制端，  1:设备端 
//	Namelen  字符串  Name字段长度 
//	Name  4 字节，整型  设备 code或者控制端用户名 
//	Separate  4 字节，整型  分隔符：0x00 
	public static byte[] buildUDPBHMsg(String username, int requestID) {
		byte[] username_buf = null;
		try {
			username_buf = username.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int id = requestID;
		
		int msg_len = 24 + username_buf.length;
		ByteBuffer buffer = ByteBuffer.allocate(msg_len);
		buffer.put(CCompatibleUtils.intToBytes(msg_len));
		buffer.put(CCompatibleUtils.intToBytes(id));
		buffer.put(CCompatibleUtils.intToBytes(0x01));
		buffer.put(CCompatibleUtils.intToBytes(0));
		buffer.put(CCompatibleUtils.intToBytes(username_buf.length));
		if(username_buf.length > 0){
			buffer.put(username_buf);
		}
		buffer.put(CCompatibleUtils.intToBytes(0x00));
		return buffer.array();
	}
	/*version	
	Json-len	
	sequence	
	action	
	Return（选）	
	Offset(选)	
	Filesize(选)	
	Filename（选）	
	Json-cont*/
	public static byte[] buildLanMsg() {
		
		int msg_len = 60;
		ByteBuffer buffer = ByteBuffer.allocate(msg_len);
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(9527));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(0));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(new Random().nextInt()));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(MsgActionCode.DEVICE_BIND));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(0));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(0));
		buffer.put(CCompatibleUtils.intToBytesLittleEndian(0));
		return buffer.array();
	}
	
	/*请求消息： 
	名称  类型  意义 
	Request len  4 字节，整型  请求包长度 
	Request id  4 字节，整型  请求包 ID 
	Request type  4 字节，整型  请求包类型：0x0c 
	Type  4 字节，整型  0:控制端，  1:设备端 
	Result  4 字节，整型  鉴权结果码 
	Namelen  4 字节，整型  Name字段长度 
	Name  字符串  设备名字（最长 32 字节） 
	Separate  4 字节，整型  分隔符：0x00 */
	
	public static byte[] buildTCPBHMsg(String userName,int requestID) {
		byte[] user_name_buf = null;
		int buf_len = 0;
		if(!StringUtils.isBlank(userName)){
			try {
				user_name_buf = userName.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			buf_len = user_name_buf.length;
		}
		int id = requestID;
		
		int msg_len = 28 + buf_len;
		ByteBuffer buffer = ByteBuffer.allocate(msg_len);
		buffer.put(CCompatibleUtils.intToBytes(msg_len));
		buffer.put(CCompatibleUtils.intToBytes(id));
		buffer.put(CCompatibleUtils.intToBytes(0x0c));
		buffer.put(CCompatibleUtils.intToBytes(0x00));
		buffer.put(CCompatibleUtils.intToBytes(0x00));
		buffer.put(CCompatibleUtils.intToBytes(buf_len));
		if(buf_len != 0){
			buffer.put(user_name_buf);
		}
		buffer.put(CCompatibleUtils.intToBytes(0x00));
		return buffer.array();
	}
	
	
	/*请求消息： 
	名称  类型  意义 
	Request len  4 字节，整型  请求包长度 
	Request id  4 字节，整型  请求包 ID 
	Request type  4 字节，整型  请求包类型：0x05 
	Usrname len  4 字节，整型  用户名字符串长度 
	Usrname  字符串  用户名 
	Oldlen  4 字节，整型  旧密码字节流长度 
	Newlen  4 字节，整型  新密码字节流长度 
	Oldpwd  字节流  旧密码(对称秘钥加密) 
	Newpwd  字节流  新密码(对称秘钥加密) 
	terminal  4 字节，整型  操作终端 
	1=pc 
	2=移动设备 
	3=盒子 
	Separate  4 字节，整型  分隔符：0x00*/
	
	public static byte[] buildChangePwdMsg(String userName ,String oldpwd, String newpwd,int requestID) throws Exception {
		byte[] user_name_buf = userName.getBytes("UTF-8");
		byte[] oldpwd_buf = AESCrypto.aesCrypto(oldpwd);
		byte[] newpwd_buf = AESCrypto.aesCrypto(newpwd);
		int id = requestID;
		
		int msg_len = 32 + user_name_buf.length + oldpwd_buf.length + newpwd_buf.length;
		ByteBuffer buffer = ByteBuffer.allocate(msg_len);
		buffer.put(CCompatibleUtils.intToBytes(msg_len));
		buffer.put(CCompatibleUtils.intToBytes(id));
		buffer.put(CCompatibleUtils.intToBytes(0x05));
		buffer.put(CCompatibleUtils.intToBytes(user_name_buf.length));
		buffer.put(user_name_buf);
		buffer.put(CCompatibleUtils.intToBytes(oldpwd_buf.length));
		buffer.put(CCompatibleUtils.intToBytes(newpwd_buf.length));
		buffer.put(oldpwd_buf);
		buffer.put(newpwd_buf);
		buffer.put(CCompatibleUtils.intToBytes(2));
		buffer.put(CCompatibleUtils.intToBytes(0x00));
		return buffer.array();
	}
	
	/*请求消息： 
	名称  类型  意义 
	Request len  4 字节，整型  请求包长度 
	Request id  4 字节，整型  请求包 ID 
	Request type  4 字节，整型  请求包类型：0x0d 
	Device num  4 字节，整型  待查询的设备个数
	
	Devcode len  4 字节，整型  设备唯一标识长度 
	devcode  字符串  设备 code 
	…  …  … 
	Devcode len  4 字节，整型  设备唯一标识长度 
	devcode  字符串  设备 code 
	
	Separate  4 字节，整型  分隔符：0x00*/

	public static byte[] buildBindStatusSearchMsg(ArrayList<DeviceInfo> mDeviceInfos,int requestID) throws Exception {
		int num = mDeviceInfos.size();
		int msg_len = 20 + num * 4;
		
		for (int i = 0; i < num; i++) {
			msg_len += mDeviceInfos.get(i).getAccess_code().getBytes("UTF-8").length;
		}
		int id = requestID;
		
		ByteBuffer buffer = ByteBuffer.allocate(msg_len);
		buffer.put(CCompatibleUtils.intToBytes(msg_len));
		buffer.put(CCompatibleUtils.intToBytes(id));
		buffer.put(CCompatibleUtils.intToBytes(0x0d));
		buffer.put(CCompatibleUtils.intToBytes(num));
		for (int i = 0; i < num; i++) {
			buffer.put(CCompatibleUtils.intToBytes(mDeviceInfos.get(i).getAccess_code().getBytes("UTF-8").length));
			buffer.put(mDeviceInfos.get(i).getAccess_code().getBytes("UTF-8"));
		}
		buffer.put(CCompatibleUtils.intToBytes(0x00));
		return buffer.array();
	}
	
	/*请求消息： 
	名称  类型  意义 
	Request len  4 字节，整型  请求包长度 
	Request id  4 字节，整型  请求包 ID 
	Request type  4 字节，整型  请求包类型：0x0a 
	Flag  4 字节，整型  0：安装；1：卸载 
	Appidlen  4 字节，整型  应用 ID长度 
	URLlen  4 字节，整型  字符串长度 
	Namelen  4 字节，整型  Name字符串长度 
	Appid  字符串  应用 ID 
	url  字符串  url字符串 
	Name  字符串  Name字符串 
	Separate  4 字节，整型  分隔符：0x00*/
	
	public static byte[] buildAppManageMsg(AppInfo app,int requestID, int flag) throws Exception {
		int id = requestID;
		byte[] appid = app.getAppcode().getBytes("UTF-8");
		byte[] url = app.getDownloadurl().getBytes("UTF-8");
		byte[] name = app.getAppname().getBytes("UTF-8");
		int msg_len = 32 + appid.length + url.length + name.length;
		
		ByteBuffer buffer = ByteBuffer.allocate(msg_len);
		buffer.put(CCompatibleUtils.intToBytes(msg_len));
		buffer.put(CCompatibleUtils.intToBytes(id));
		buffer.put(CCompatibleUtils.intToBytes(0x0a));
		buffer.put(CCompatibleUtils.intToBytes(flag));
		buffer.put(CCompatibleUtils.intToBytes(appid.length));
		buffer.put(CCompatibleUtils.intToBytes(url.length));
		buffer.put(CCompatibleUtils.intToBytes(name.length));
		buffer.put(appid);
		buffer.put(url);
		buffer.put(name);
		buffer.put(CCompatibleUtils.intToBytes(0x00));
		return buffer.array();
	}
	
	
}
