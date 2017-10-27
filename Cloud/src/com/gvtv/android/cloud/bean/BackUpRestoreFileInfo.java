package com.gvtv.android.cloud.bean;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class BackUpRestoreFileInfo {
	public int statusFlag = -1;//0为备份，1为恢复。状态行为
	public int fileflag = -1;//0备份通信录，1备份短信。文件类型
	public ByteBuffer bytebuffer_contact;//恢复接收到的contact数据
	public ByteBuffer bytebuffer_sms;//恢复接收的sms数据
	public List<MsgBean> msgs = new ArrayList<MsgBean>();//消息体
	public List<byte[]> backbytes = new ArrayList<byte[]>();
	public int filesize;//文件大小
	public int pkgSize;//单个包大小
	public String fileName;//文件名

	public String vcardString = "";//contact字符串
	public String xmlString = "";//sms字符串
	public byte[] byte_contact_backup;//通信录备份文件
	public byte[] byte_sms_backup;//短信备份文件
	public int restoreEndFlag;//恢复标识，必须同时恢复两个文件才认为完全恢复，即其值为2
	
	public void initStatus(){
		statusFlag = -1;
		fileflag = -1;
		msgs.clear();
		backbytes.clear();
	}
}
