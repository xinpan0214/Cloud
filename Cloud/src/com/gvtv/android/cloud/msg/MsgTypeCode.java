package com.gvtv.android.cloud.msg;

public class MsgTypeCode {
	
	public static final int CONTROLLER_DEV_UDP = 0x201;//和设备直接通信的UDP消息
	
	/*消息类型*/
	public static final int CONTROLLER_ADDR_REQ = 0x101;//控制端寻址请求
	public static final int CONTROLLER_ADDR_RESP = 0x102;//控制端寻址响应
	
	public static final int CONTROLLER_REG_REQ = 0x1101;//负载找业务请求
	public static final int CONTROLLER_REG_RESP = 0x1102;//负载找业务响应
	
	public static final int CONTROLLER_REG_LOGIN_REQ = 0x1201;//负载找业务请求，要用户名
	public static final int CONTROLLER_REG_LOGIN_RESP = 0x1202;//负载找业务响应，要用户名
	
	public static final int DEV_ADDR_REQ = 0x201;//设备端寻址请求
	public static final int DEV_ADDR_RESP = 0x202;//设备端寻址响应
	
	public static final int UDP_BH_REQ = 0x01;//UDP心跳
	
	public static final int SECRET_KEY_REQ = 0x01;//密钥协商
	public static final int CONTROLLER_LOGIN_REQ = 0x02;//登录
	
	public static final int USER_REGISTER_REQ = 0x04;//注册
	public static final int PWD_MODIFY_REQ = 0x05;//修改密码
	public static final int PWD_RETRIEVE_REQ = 0x06;//找回密码
	public static final int COMMAND_FORWARDING_REQ = 0x07;//转发控制端命令接口 
	public static final int DEVICE_BIND_REQ = 0x08;//设备绑定
	
	public static final int DEVICE_UNBIND_REQ = 0x09;//解绑设备
	public static final int INSTALL_UNINSTALL_REQ = 0x0a;//应用安装/卸载通知
	public static final int UDP_NATS_REQ = 0x0b;//协助 P2P 穿透接口
	public static final int DEVICE_BH_REQ = 0x0c;//设备端心跳
	public static final int BIND_QUERY_REQ = 0x0d;//绑定状态查询
	public static final int USER_APPID_REQ = 0x0e;//获取appid信息
	public static final int ANOTHER_LOGIN_REQ = 0x0f;//被踢下线
	public static final int FILE_FORWARDING_REQ = 0x10;//文件转发
	
	
	public static final int TIME_OUT = 0x11;//超时
}
