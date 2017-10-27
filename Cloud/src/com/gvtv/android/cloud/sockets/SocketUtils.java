package com.gvtv.android.cloud.sockets;

import java.util.ArrayList;

import android.content.Context;

import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.bean.AppInfo;
import com.gvtv.android.cloud.bean.DeviceInfo;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.msg.MessageUtils;
import com.gvtv.android.cloud.sockets.SendTcpMsgAsyncTask.OnSendendListener;
import com.gvtv.android.cloud.util.StringUtils;
import com.gvtv.android.cloud.util.crypto.AESCrypto;
import com.gvtv.android.cloud.util.crypto.RSACrypto;

public class SocketUtils {
	
	/**
	 * 
	 * @Title: controller_reg_req
	 * @Description: 找业务服务器,未知用户名
	 * @param mOnSendendListener
	 * @throws Exception
	 */
	public static void controller_reg_req(OnSendendListener mOnSendendListener) throws Exception {
		new SendTcpMsgAsyncTask(MessageUtils.controller_reg_req_msg(), SocketClient.CLIENT_LOAD,
				mOnSendendListener).send();
	}
	
	/**
	 * 
	 * @Title: controller_reg_loginreq
	 * @Description: 找业务服务器,已经知用户名
	 * @param mOnSendendListener
	 * @param username
	 * @throws Exception
	 */
	public static void controller_reg_loginreq(OnSendendListener mOnSendendListener,
			String username) throws Exception {
		new SendTcpMsgAsyncTask(MessageUtils.controller_reg_req_loginmsg(username), SocketClient.CLIENT_LOAD,
				mOnSendendListener).send();
	}
	
	/**
	 * 
	 * @Title: secret_key_req
	 * @Description: 密钥协商 
	 * @param mContext
	 * @param mOnSendendListener
	 * @param requestID
	 * @throws Exception
	 */
	public static void secret_key_req(Context mContext, OnSendendListener mOnSendendListener,
			int requestID) throws Exception {

		CloudApplication.aeskey = StringUtils.getRandomCharAndNum(32);
		byte[] buf_key_agree_req = null;
		byte[] buf_key_agree = RSACrypto.encrypt(
				RSACrypto.loadPublicKey(RSACrypto.getPublicKey(mContext)),
				CloudApplication.aeskey.getBytes("UTF-8"));
		buf_key_agree_req = MessageUtils.buildSecretkeyMsg(buf_key_agree, requestID);
		new SendTcpMsgAsyncTask(buf_key_agree_req, SocketClient.CLIENT_BUSINESS,
				mOnSendendListener).send();
	}

	/**
	 * 
	 * @Title: user_login_req
	 * @Description: 登录
	 * @param account
	 * @param pwd
	 * @param mOnSendendListener
	 * @param requestID
	 * @throws Exception
	 */
	public static void user_login_req(String account, String pwd,
			OnSendendListener mOnSendendListener, int requestID) throws Exception {
		byte[] buf_key_login = null;
		byte[] buf_login_req = null;
		buf_key_login = AESCrypto.aesCrypto(pwd);
		buf_login_req = MessageUtils.buildLoginMsg(account, buf_key_login, requestID);
		new SendTcpMsgAsyncTask(buf_login_req, SocketClient.CLIENT_BUSINESS, mOnSendendListener).send();
	}

	/**
	 * 
	 * @Title: user_register_req
	 * @Description: 注册
	 * @param email
	 * @param pwd
	 * @param mOnSendendListener
	 * @param requestID
	 * @throws Exception
	 */
	public static void user_register_req(String email, String pwd,
			OnSendendListener mOnSendendListener, int requestID) throws Exception {
		new SendTcpMsgAsyncTask(MessageUtils.buildRegisterMsg(email, pwd, requestID),
				SocketClient.CLIENT_BUSINESS, mOnSendendListener).send();
	}

	/**
	 * 
	 * @Title: bind_device_req
	 * @Description:  绑定设备
	 * @param mDeviceInfo
	 * @param user_name
	 * @param mOnSendendListener
	 * @param requestID
	 */
	public static void bind_device_req(DeviceInfo mDeviceInfo, String user_name,
			OnSendendListener mOnSendendListener, int requestID) {
		new SendTcpMsgAsyncTask(MessageUtils.buildBindMsg(mDeviceInfo, user_name, requestID),
				SocketClient.CLIENT_BUSINESS, mOnSendendListener).send();
	}

	/**
	 * 
	 * @Title: pwd_modify_req
	 * @Description: 修改密码
	 * @param user_name
	 * @param pwdOld
	 * @param pwdNew
	 * @param mOnSendendListener
	 * @param requestID
	 * @throws Exception
	 */
	public static void pwd_modify_req(String user_name, String pwdOld, String pwdNew,
			OnSendendListener mOnSendendListener, int requestID) throws Exception {
		new SendTcpMsgAsyncTask(MessageUtils.buildChangePwdMsg(CloudApplication.user_name, pwdOld,
				pwdNew, requestID), SocketClient.CLIENT_BUSINESS, mOnSendendListener).send();
	}

	/**
	 * 
	 * @Title: bindstatus_search_req
	 * @Description: 绑定状态查询
	 * @param mDeviceInfos
	 * @param mOnSendendListener
	 * @param requestID
	 * @throws Exception
	 */
	public static void bindstatus_search_req(ArrayList<DeviceInfo> mDeviceInfos,
			OnSendendListener mOnSendendListener, int requestID) throws Exception {
		new SendTcpMsgAsyncTask(MessageUtils.buildBindStatusSearchMsg(mDeviceInfos, requestID),
				SocketClient.CLIENT_BUSINESS, mOnSendendListener).send();
	}

	/* 转发消息 */
	public static void command_forwarding_req(MsgBean msg, OnSendendListener mOnSendendListener,
			int requestID) throws Exception {
		new SendTcpMsgAsyncTask(MessageUtils.build_command_forwarding(msg, requestID),
				SocketClient.CLIENT_BUSINESS, mOnSendendListener).send();
	}
	
	/**
	 * 
	 * @Title: command_forwarding_backup
	 * @Description: 转发备份
	 * @param msg
	 * @param josnBuf
	 * @param mOnSendendListener
	 * @param requestID
	 * @throws Exception
	 */
	public static void command_forwarding_backup(MsgBean msg, byte[] josnBuf, OnSendendListener mOnSendendListener,
			int requestID) throws Exception {
		new SendTcpMsgAsyncTask(MessageUtils.build_command_forwardingbackup(msg, josnBuf, requestID),
				SocketClient.CLIENT_BUSINESS, mOnSendendListener).send();
	}

	/**
	 * 
	 * @Title: unbind_device_req
	 * @Description: 解绑设备
	 * @param mDeviceInfo
	 * @param user_name
	 * @param mOnSendendListener
	 * @param requestID
	 */
	public static void unbind_device_req(DeviceInfo mDeviceInfo, String user_name,
			OnSendendListener mOnSendendListener, int requestID) {
		new SendTcpMsgAsyncTask(MessageUtils.buildUnbindMsg(mDeviceInfo, user_name, requestID),
				SocketClient.CLIENT_BUSINESS, mOnSendendListener).send();
	}

	/**
	 * 
	 * @Title: controller_addr_req
	 * @Description: 负载服务器寻址 
	 * @param mOnSendendListener
	 * @param requestID
	 */
	public static void controller_addr_req(OnSendendListener mOnSendendListener, int requestID) {
		new SendTcpMsgAsyncTask(
				MessageUtils.build_controller_addr_req_msg(requestID),
				SocketClient.CLIENT_ADDR, mOnSendendListener).send();
	}
	
	/**
	 * 
	 * @Title: Nats_req
	 * @Description: 协助穿透
	 * @param mOnSendendListener
	 * @param requestID
	 */
	public static void Nats_req(OnSendendListener mOnSendendListener, int requestID) {
		new SendTcpMsgAsyncTask(
				MessageUtils.build_nats_req_msg(requestID),
				SocketClient.CLIENT_BUSINESS, mOnSendendListener).send();
	}
	
	/**
	 * 
	 * @Title: install_uninstall_app_req
	 * @Description: 安装/卸载应用
	 * @param mOnSendendListener
	 * @param app
	 * @param requestID
	 * @param flag
	 * @throws Exception
	 */
	public static void install_uninstall_app_req(OnSendendListener mOnSendendListener,AppInfo app, int requestID, int flag) throws Exception {
		new SendTcpMsgAsyncTask(
				MessageUtils.buildAppManageMsg(app, requestID, flag),
				SocketClient.CLIENT_BUSINESS, mOnSendendListener).send();
	}
	
	/**
	 * 
	 * @Title: appid_req
	 * @Description:  查询已安装应用appid
	 * @param mOnSendendListener
	 * @param pageNum
	 * @param requestID
	 * @throws Exception
	 */
	public static void appid_req(OnSendendListener mOnSendendListener,int pageNum, int requestID) throws Exception {
		new SendTcpMsgAsyncTask(
				MessageUtils.buildAppidReq(requestID, pageNum),
				SocketClient.CLIENT_BUSINESS, mOnSendendListener).send();
	}

}
