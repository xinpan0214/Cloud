/**
 * @Package tv.hitv.android.smarttv.util 
 * @Description
 *
 * Copyright (c) Inspur Group Co., Ltd. Unpublished
 *
 * Inspur Group Co., Ltd.
 * Proprietary & Confidential
 *
 * This source code and the algorithms implemented therein constitute
 * confidential information and may comprise trade secrets of Inspur
 * or its associates, and any use thereof is subject to the terms and
 * conditions of the Non-Disclosure Agreement pursuant to which this
 * source code was originally received.
 */
package com.gvtv.android.cloud;


public class AppConst {
	
	public static final int REQUEST_BACKUP = 0;
	public static final int REQUEST_TO_NAT = 1;
	public static final int TCP_BACKUP_BLOCK = 2;
	public static final int TCP_REQUEST_RESTORE = 3;
	public static final int ANSWER_RESTORE_BLOCK = 4;
	public static final int ANSWER_TCP_RECOVER_OVER = 5;
	public static final int RESTORE_SUCCESS = 6;
	public static final int UDP_BACKUP_BLOCK = 7;
	public static final int ANSWER_RESTORE_UDPBLOCK = 8;
	public static final int UDP_REQUEST_RESTORE = 9;
	public static final int ANSWER_UDP_RECOVER_OVER = 10;
	

	//public static final String SERVER_URL = "addr.bananacloud.com";
	public static final String SERVER_URL = "addr.mysolive.com";
	public static final int SERVER_PORT = 5900;
	public static final int HTTP_LOGIN = 0x1001;
	
	public static final String BROADCAST_IP = "255.255.255.255";// 广播IP
	public static final int BROADCAST_INT_PORT = 7102; // 不同的port对应不同的socket发送端和接收端
	
	public static final int REQUEST_SEARCH_DEVICE = 1;
	public static final int RESULT_NOT_BIND = 1;
	public static final int RESULT_BINDED = 2;
	public static final int REQUEST_ACTIVATE = 1;
	public static final int REQUEST_FINDPWD = 1;
	public static final int REQUEST_LOGIN = 1;
	public static final int REQUEST_NOT_LOGIN = 2;
	
	
	public static final String CLOUD_NAME = "BC_";
	public static final String ACCESS_CODE = "00050057b4a83118ac867e03441a";//有效的code
	
	public static final String MSG_RECV_ACTION = "com.gvtv.android.cloud.MSGRECV_ACTION";
	
	public static final String FILEFOLD_TO_FILE = "filefold_to_file";
	public static final String APPSTORE_TO_APPDETAIL = "appstore_to_appdetail";
	public static final String CLOUDMANAGER_TO_APP = "cloudmanager_to_app";
	public static final String CLASSIFY = "classify";
	
	
	public static final int USERINFO_REQUESTCODE = 0x01;
	public static final int USERINFO_CLOSE = 0x01;
	
	public static final String RESP_TYPE = "resp_type";
	public static final String RESP_ACTION = "resp_action";
	public static final String RESP_MSG = "resp_msg";
	public static final String RESP_CODE = "resp_code";
	public static final String RESP_MSG_REGISTER = "resp_msg_register";
	public static final String BACKUP_BYTE = "backup_byte";
	
	public static final String BACKUPCONTACT_NAME = "address.vcf";
	public static final String BACKUPSMS_NAME = "message.xml";
	
	public static final String CONTACT_NAME = "address";
	public static final String SMS_NAME = "message";
}
