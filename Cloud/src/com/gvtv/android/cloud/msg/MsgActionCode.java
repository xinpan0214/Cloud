package com.gvtv.android.cloud.msg;

public class MsgActionCode {
	public static final int ADD_TASK = 3;//添加任务
	public static final int DEL_FIN_TASK = 4;//删除已完成任务
	public static final int DEL_TASK = 5;//删除下载任务并删除未完成的文件
	public static final int RESUME_TASK = 6;//继续下载任务
	public static final int PAUSE_TASK = 7;//暂停下载任务
	public static final int QUERY_TASK = 8;//查询下载中任务列表
	public static final int QUERY_FILE = 9;//查询指定应用下文件列表
	public static final int QUERY_APPS = 10;//查询 user 的所有应用的列表
	public static final int QUERY_RSC_NUM = 11;//查询 user 所有资源的数目
	public static final int REMOVE_FILE = 12;//删除除指定的文件
	public static final int QUERY_FIN_TASK = 13;//查询 user 下载完成的列表
	public static final int QUERY_FILE_LIKE = 15;//关键字模糊查询下载完成的文件
	public static final int REDOWNLOAD_TASK = 16;//重新下载

	public static final int DEVICE_BIND = 40;//绑定设备搜索
	public static final int QUERY_DISK = 41;//查询硬盘空间
	public static final int QUERY_DEVINFO = 42;//查询设备信息
	public static final int CTL_REBACK = 43;//恢复出厂设置
	public static final int REMOTE_SHUTDOW = 44;//远程控制设备端关机
	public static final int REMOTE_REBOOT = 45;//远程控制设备端重启
	public static final int TV_PLAY = 46;//播放端搜索
	public static final int QUERY_PROD_INFO = 47;//查询设备的产品信息
	public static final int UPLOAD_DEV = 48;//控制设备升级固件
	public static final int QUERY_UPLOAD_STATE = 49;//查看下载固件升级包进度
	public static final int CANCEL_UPLOAD = 50;//取消下载固件升级包
	public static final int INSTALL_UPLOAD = 51;//安装固件升级包
	public static final int SET_SPEED_LIMIT = 52;//设置下载速度上限
	public static final int CHANGE_DEVNAME = 53;//修改设备名称
	
	public static final int NAT_BLOCK = 100;//穿透尝试
	public static final int QUEST_BACKUP = 101;//备份文件请求
	public static final int BACKUP_BLOCK = 102;//发送备份文件块
	public static final int BACKUP_OVER = 103;//发送备份文件完毕，告知设备端
	public static final int QUERY_BACKUP_INFO = 104;//查询备份文件信息，返回的信息有文件的 ID、名字、大小等
	public static final int QUEST_RECOVER = 105;//请求下载通过上一步查询好的文件，包含文件名字、文件 ID
	public static final int RECOVER_BLOCK = 106;//恢复文件块
	public static final int RECOVER_OVER = 107;//设备端发送完毕，告知客户端


}
