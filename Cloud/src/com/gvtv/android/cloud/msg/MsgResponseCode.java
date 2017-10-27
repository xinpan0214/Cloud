package com.gvtv.android.cloud.msg;

public class MsgResponseCode {
	public static final int OK = 200;//成功
	
	public static final int SYSTEM_ERROR = 300;//系统错误
	public static final int PARAMETER_WRONG = 301;//参数有误（格式，长度，取值范围）
	public static final int NO_ACCESS = 302;//无权限访问(IP 跟业务线不匹配)
	public static final int CODE_EXIST = 303;//Code 已存在
	
	public static final int USER_NOT_EXIST = 401;//用户不存在
	public static final int ACCOUNT_OR_PASSWORD_ERROR = 402;//账号或密码错误
	public static final int ACCOUNT_DISABLE = 403;//账号已停用
	public static final int EMAIL_EXIST = 407;//邮箱已被使用（用户已注册）
	public static final int OLD_PASSWORD_ERROR = 408;//旧密码错误
	public static final int INVALID_BUSINESS = 409;//无效的业务线(不存在，不合法)
	public static final int INVALID_FRIEND = 410;//无效的好友 (不存在，不合法)
	
	public static final int MODIFIED_ACCOUNT_NOT_MATCH_CHECK_CODE = 412;//修改的账号和效验码不匹配
	public static final int CHANGE_PASSWORD_TIME_EXPIRED = 413;//修改密码时间已过期
	public static final int PASSWORD_MODIFIED = 414;//密码已经修改过
	public static final int CHECK_CODE_ERROR = 415;//校验码有误
	public static final int ACTIVATED_FAILED = 416;//激活失败
	public static final int ACTIVATED_TIME_EXPIRED = 417;//激活期已过
	public static final int ACTIVATED = 418;//已经激活过
	public static final int NO_REPONSE = 419;//用户中心前台无响应
	public static final int FRIEND_EXIST = 420;//好友已存在
	public static final int FRIENDSHIP_NOT_EXIST = 421;//好友关系不存在
	public static final int ACCOUNT_BINDED = 422;//账户已绑定
	public static final int INVALID_CODE = 423;//无效的设备 code
	public static final int DEVICE_EXIST = 424;//设备已存在
	public static final int USER_EXIST_NOT_ACTIVATED = 425;//用户已存在但未激活
	
	public static final int RSA_ERROR = 501;//RSA 解密会话密钥错误
	public static final int AES_ERROR = 502;//AES 解密失败
	public static final int AES_SESSIONKEY_TOO_LONG = 503;//AES 会话密钥过长
	public static final int NO_SESSIONKEY = 504;//没有协商会话密钥
	public static final int DEVICE_LOGIN_FAILED = 505;//设备登录失败
	public static final int OTHER_END_OFFLINE = 506;//另一端不在线
	public static final int OTHER_END_SEND_FAILED = 507;//另一端发送失败
	public static final int NO_ACCESS_TO_DEVICE_UDP_PORT = 508;//获取不到设备 UDP 端口
	public static final int NO_ACCESS_TO_CONTROL_UDP_PORT = 509;//获取不到控制端 UDP 端口
	public static final int OTHER_CONTROLER_LOGIN = 510;//被踢下线
	public static final int SESSIONKEY_REPEAT = 511;//重复密钥协商
	public static final int LOGIN_REPEAT = 512;//重复登录
	public static final int DEVICE_AUTHENTICATION_FAILURE = 513;//设备鉴权失败
	public static final int DEVICE_AUTHENTICATION_EXPIRING = 514;//设备快到期
	public static final int DEVICE_RENEWAL_EXPIRE = 515;//设备续费到期
	public static final int DEVICE_OUTOFSERVICE = 516;//设备已停用
	
	public static final int REMOVE_DOWNLOAD_FAILED = 601;//删除下载任务失败，channel_delete()函数返回不为 0
	public static final int ADD_DOWNLOAD_FAILED = 602;//添加下载任务失败，原因不明
	public static final int ADD_DOWNLOAD_FAILED_BUT_EXIST = 603;//添加下载任务失败,已经添加了该任务，重复添加，控制端可以理解为添加成功，忽略
	public static final int PAUSE_DOWNLOAD_FAILED_NO_TASK = 604;//暂停下载任务失败,没有查找到相应任务
	
	public static final int PAUSE_DOWNLOAD_FAILED_UNKNOWN_CAUSE = 606;//暂 停 下 载 任 务 失 败 ， 原 因 不 明 ，channel_pause()函返回不为0
	public static final int RESUME_DOWNLOAD_FAILED_NO_TASK = 607;//继续下载任务失败,没有查找到相应任务
	
	public static final int ADD_DOWNLOAD_FAILED_DISC_FULL = 609;//磁盘已满
	public static final int DEL_DOWNLOAD_FAILED_NO_TASK = 610;//删除下载任务失败,没有查找到相应任务
	public static final int DEL_DOWNLOAD_FAILED_UNKNOWN_CAUSE = 611;//删除下载任务失败，channel_remove()函数返回不为 0
	
	public static final int ADD_DOWNLOAD_FAILED_BUT_FINISHED = 613;//添加下载任务失败，已经下载完该任务
	
	
	public static final int COMMAND_EXECUTE_FAILED = 616;//转发的消息，设备执行命令失败
	public static final int DEL_RESFILE_FAILED = 617;//删除资源文件失败
	public static final int DEVICE_AUTH_FAILED = 618;//设备鉴权失败
	
	public static final int NO_FILENAME = 652;//没有文件名
	public static final int NO_FILESIZE = 653;//没有文件大小
	
	
	public static final int NO_DISK = 666;//没有硬盘
	public static final int DISK_NOT_FORMATTED = 667;//硬盘没有格式化
	
}
