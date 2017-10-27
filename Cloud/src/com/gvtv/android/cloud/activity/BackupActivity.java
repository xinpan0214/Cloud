package com.gvtv.android.cloud.activity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import a_vcard.android.syncml.pim.vcard.VCardException;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.gvtv.android.cloud.AppConst;
import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.bean.BackUpRestoreFileInfo;
import com.gvtv.android.cloud.bean.BackupFile;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.bean.TaskReturn;
import com.gvtv.android.cloud.broadcast.MessageReceiver;
import com.gvtv.android.cloud.msg.JsonUtils;
import com.gvtv.android.cloud.msg.MessageUtils;
import com.gvtv.android.cloud.msg.MsgActionCode;
import com.gvtv.android.cloud.msg.MsgResponseCode;
import com.gvtv.android.cloud.msg.MsgTypeCode;
import com.gvtv.android.cloud.sockets.SocketUtils;
import com.gvtv.android.cloud.sockets.UDPClient;
import com.gvtv.android.cloud.util.AlertDialogUtil;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.TimeUtils;
import com.gvtv.android.cloud.util.ToastUtil;
import com.gvtv.android.cloud.util.sms.ExportSmsXml;
import com.gvtv.android.cloud.util.sms.ImportSms;
import com.gvtv.android.cloud.util.sms.SmsExcetion;
import com.gvtv.android.cloud.util.vcard.ContactHandler;
import com.gvtv.android.cloud.util.vcard.ContactInfo;
import com.gvtv.android.cloud.util.vcard.ContactsExcetion;
import com.gvtv.android.cloud.view.TitleView;

/**
 * {@link #answer_tcp_recover_over()}
 * {@link #answer_tcp_restore_block(Message)}
 * {@link #answer_udp_recover_over()}
 * {@link #answer_udp_recover_block(MsgBean)}
 * {@link #tcp_request_to_nat()}
 * {@link #tryNat()}
 * {@link #tcp_quest_recover()}
 * {@link #udp_request_recover()
 * {@link #udp_backup_block()}
 * {@link #request_recover()}
 * {@link #tcp_request_backup()}
 * {@link #tcp_backup_block()}
 * {@link #backupContacts()}
 * {@link #backupSms()}
 * {@link #restoreContact()}
 * {@link #restoreSms()}
 * {@link #query_backup_info()}
 * {@link #initBackupAndRestore();}
 */
@SuppressLint("HandlerLeak")
public class BackupActivity extends BaseActivity implements OnClickListener {

	private TitleView mTitleView;
	private TextView tv_last_backup, tv_fileName;
	private Button btn_backup, btn_restore;

	private ContentResolver cr;
	private ContactHandler mContactHandler;
	private List<ContactInfo> allConatcts = null;

	private BackupActivity instance;
	private final static int STATUS_BACKUP = 0;
	private final static int STATUS_RESTORE = 1;
	private final static int FILE_CONTACT = 0;
	private final static int FILE_SMS = 1;
	
	private ArrayList<BackupFile> mBackupFileS;
	private BackupFile currBackupFile;

	private BackUpRestoreFileInfo mFileInfo;//恢复或备份所用到的对象
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case AppConst.REQUEST_BACKUP://请求备份,不管是否需要穿透，都先请求备份
				LogUtils.getLog(getClass()).verbose("=============request_backup");
				if(mFileInfo.filesize > 0){
					tcp_request_backup();
				}else{
					if(mFileInfo.fileflag == 0){
						ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.please_contacts_allow));
					}else if(mFileInfo.fileflag == 1){
						ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.please_sms_allow));
					}
					AlertDialogUtil.getAlertDialogUtil().cancelDialog();
				}
				
				break;

			case AppConst.REQUEST_TO_NAT://发送穿透请求,协助穿透
				LogUtils.getLog(getClass()).verbose("=============request_to_nat");
				tcp_request_to_nat();
				break;

			case AppConst.TCP_BACKUP_BLOCK://tcp备份和备份结束
				LogUtils.getLog(getClass()).verbose("=============tcp_backup_block==msgs.size()：" + mFileInfo.msgs.size());
				tcp_backup_block();
				break;
				
			case AppConst.TCP_REQUEST_RESTORE://TCP恢复请求
				LogUtils.getLog(getClass()).verbose("=============request_restore");
				tcp_quest_recover();
				break;
				
			case AppConst.UDP_REQUEST_RESTORE://UDP恢复请求
				LogUtils.getLog(getClass()).verbose("=============request_restore");
				udp_request_recover();
				break;
				
			case AppConst.ANSWER_RESTORE_BLOCK://收到TCP恢复消息，回应
				LogUtils.getLog(getClass()).verbose("=============answer_restore_block");
				answer_tcp_restore_block(msg);
				break;
		
			case AppConst.ANSWER_TCP_RECOVER_OVER://TCP恢复结束
				LogUtils.getLog(getClass()).verbose("=============ANSWER_TCP_RECOVER_OVER");
				if(mFileInfo.fileflag == FILE_CONTACT){
					mFileInfo.fileflag = FILE_SMS;
					request_to_nat_before_recover();//结束后请求恢复短信
					restoreContact();
				}else if(mFileInfo.fileflag == FILE_SMS){
					mFileInfo.fileflag = -1;
					restoreSms();
				}
				break;
				
			case AppConst.ANSWER_UDP_RECOVER_OVER://UDP恢复结束
				LogUtils.getLog(getClass()).verbose("=============ANSWER_UDP_RECOVER_OVER");
				answer_udp_recover_over();
				if(mFileInfo.fileflag == FILE_CONTACT){
					mFileInfo.fileflag = FILE_SMS;
					request_to_nat_before_recover();//结束后请求恢复短信
					restoreContact();
				}else if(mFileInfo.fileflag == FILE_SMS){
					mFileInfo.fileflag = -1;
					restoreSms();
				}
				break;
			case AppConst.RESTORE_SUCCESS:
				if(msg.arg1 == FILE_CONTACT && msg.arg2 == -1){
					ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.please_contacts_allow));
				}else if(msg.arg1 == FILE_SMS && msg.arg2 == -1){
					ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.please_sms_allow));
				}
				mFileInfo.restoreEndFlag ++;
				if(mFileInfo.restoreEndFlag == 2){
					AlertDialogUtil.getAlertDialogUtil().cancelDialog();
					ToastUtil.getToastUtils().showToast(instance, instance.getResources().getString(R.string.restore_end));
				}
				break;
			case AppConst.UDP_BACKUP_BLOCK://UDP备份
				LogUtils.getLog(getClass()).verbose("=============udp_backup_block");
				udp_backup_block();
				break;
			
			default:
				break;
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_backupcontacts);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
		instance = this;
		findViews();
		setListeners();
		init();
		CloudApplication.getInstance().addActivity(instance);
		MessageReceiver.msghList.add(instance);
	}

	private void findViews() {
		mTitleView = (TitleView) findViewById(R.id.bindDeviceTitle);
		tv_last_backup = (TextView) findViewById(R.id.tv_backup_last);
		tv_last_backup.setVisibility(View.INVISIBLE);
		tv_fileName = (TextView) findViewById(R.id.tv_backup_filename);
		tv_fileName.setVisibility(View.INVISIBLE);
		btn_backup = (Button) findViewById(R.id.btn_backup);
		btn_restore = (Button) findViewById(R.id.btn_restore);
	}

	private void setListeners() {
		mTitleView.getBackTextView().setOnClickListener(this);
		btn_backup.setOnClickListener(this);
		btn_restore.setOnClickListener(this);
	}

	private void init() {
		cr = getContentResolver();
		// 获取联系人处理实例
		mContactHandler = ContactHandler.getInstance();
	}

	@Override
	public void onClick(View v) {
		if (v == mTitleView.getBackTextView()) {
			finish();
		} else if (v == btn_backup) {
			initBackupAndRestore();
			mFileInfo = new BackUpRestoreFileInfo();
			mFileInfo.fileflag = FILE_CONTACT;
			mFileInfo.statusFlag = STATUS_BACKUP;
			AlertDialogUtil.getAlertDialogUtil().showDialogWithoutCancelListener(instance, getResources().getString(R.string.backup_please_wait));
			//AlertDialogUtil.getAlertDialogUtil().getDialog().setCancelable(true);
			AlertDialogUtil.getAlertDialogUtil().getDialog().setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					LogUtils.getLog(getClass()).verbose("onCancel======");
					initBackupAndRestore();
				}
			});
			backupContacts();
		} else if (v == btn_restore) {
			if(mBackupFileS != null && mBackupFileS.size() > 0){
				initBackupAndRestore();
				mFileInfo = new BackUpRestoreFileInfo();
				mFileInfo.fileflag = FILE_CONTACT;
				mFileInfo.statusFlag = STATUS_RESTORE;
				AlertDialogUtil.getAlertDialogUtil().showDialogWithoutCancelListener(instance, getResources().getString(R.string.restore_please_wait));
				//AlertDialogUtil.getAlertDialogUtil().getDialog().setCancelable(true);
				AlertDialogUtil.getAlertDialogUtil().getDialog().setOnCancelListener(new OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						initBackupAndRestore();
					}
				});
				request_to_nat_before_recover();
			}else{
				if(mBackupFileS == null || mBackupFileS.size() == 0){
					query_backup_info();
				}
				ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.restore_null));
			}
		}
	}
	
	private void initBackupAndRestore(){
		if(mFileInfo != null){
			answer_tcp_recover_over();
			answer_udp_recover_over();
			mHandler.removeCallbacksAndMessages(mFileInfo);
			mFileInfo.initStatus();
			mFileInfo = null;
		}
	}

	

	private ContactInfo refreshContactInfo(ContactInfo contact) {
		return mContactHandler.getContactInfo(this, contact, cr);
	}

	@Override
	protected void onResume() {
		if(mBackupFileS == null || mBackupFileS.size() == 0){
			query_backup_info();
		}
		super.onResume();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		AlertDialogUtil.getAlertDialogUtil().cancelDialog();
		LogUtils.getLog(getClass()).verbose("=============onPause");
		for (int i = 0; i < MessageReceiver.msghList.size(); i++) {
			if (MessageReceiver.msghList.get(i) instanceof BackupActivity) {
				MessageReceiver.msghList.remove(i);
				i--;
			}
		}
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		
		super.onPause();
	}
	

	@Override
	public void onBackPressed() {
		instance.finish();
		super.onBackPressed();
	}
	

	@Override
	public void onMessage(int type, int resp, MsgBean msg, byte[] restoreByte) {
		AlertDialogUtil.getAlertDialogUtil().setTimeout_amount(0);
		LogUtils.getLog(getClass()).verbose("=============" + "收到消息：type," + type + ";resp," + resp);
		switch (type) {
		case MsgTypeCode.COMMAND_FORWARDING_REQ:
			String jsonStr;
			TaskReturn taskRet;
			if (resp == MsgResponseCode.OK) {
				switch (msg.getAction()) {
				case MsgActionCode.QUERY_BACKUP_INFO:
					if(msg.getRet() == MsgResponseCode.OK){
						jsonStr = msg.getJson_content();
						taskRet = JsonUtils.parseQUERY_BACKUP_INFO(jsonStr);
						if (taskRet.getRet() == MsgResponseCode.OK) {
							mBackupFileS = taskRet.getmBackupFileS();
							if(mBackupFileS != null && mBackupFileS.size() > 0){
								currBackupFile = mBackupFileS.get(0);
								tv_last_backup.setVisibility(View.VISIBLE);
								tv_last_backup.setText(getResources().getString(R.string.last_backup_time) + TimeUtils.formatTimeToStr(currBackupFile.getDate()));	
							}else{
								tv_last_backup.setVisibility(View.VISIBLE);
								tv_last_backup.setText(getResources().getString(R.string.no_backup_file));	
							}
							//tv_fileName.setText(getResources().getString(R.string.backup_filename) + currBackupFile.getFilename());
							
						} else {
							if(mBackupFileS != null && mBackupFileS.size() > 0){
								currBackupFile = mBackupFileS.get(0);
								tv_last_backup.setVisibility(View.VISIBLE);
								tv_last_backup.setText(getResources().getString(R.string.last_backup_time) + TimeUtils.formatTimeToStr(currBackupFile.getDate()));	
							}else{
								tv_last_backup.setVisibility(View.VISIBLE);
								tv_last_backup.setText(getResources().getString(R.string.no_backup_file));	
							}
							ToastUtil.getToastUtils().showToastByCode(instance, taskRet.getRet());
						}
					}else {
						ToastUtil.getToastUtils().showToastByCode(instance, msg.getRet());
					}
					break;

				case MsgActionCode.QUEST_BACKUP:
					if (msg.getRet() == MsgResponseCode.OK) {
						Message m = Message.obtain(mHandler, AppConst.REQUEST_TO_NAT, mFileInfo);
						mHandler.sendMessage(m);
					} else {
						AlertDialogUtil.getAlertDialogUtil().cancelDialog();
						ToastUtil.getToastUtils().showToastByCode(instance, msg.getRet());
					}
					break;

				case MsgActionCode.BACKUP_BLOCK://TCP备份包发送结果
					if (msg.getRet() == MsgResponseCode.OK) {//发送下一个
						if(mFileInfo == null){
							return;
						}
						mFileInfo.msgs.remove(0);
						mFileInfo.backbytes.remove(0);
						Message m = Message.obtain(mHandler, AppConst.TCP_BACKUP_BLOCK, mFileInfo);
						mHandler.sendMessage(m);
					} else {//发送下一个
						Message m = Message.obtain(mHandler, AppConst.TCP_BACKUP_BLOCK, mFileInfo);
						mHandler.sendMessage(m);
					}
					break;

				case MsgActionCode.BACKUP_OVER://TCP备份结束
					if (msg.getRet() == MsgResponseCode.OK) {
						if(mFileInfo == null){
							return;
						}
						if(mFileInfo.fileflag == FILE_CONTACT){
							ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.start_backup_sms));
							mFileInfo.fileflag = FILE_SMS;
							mFileInfo.statusFlag = STATUS_BACKUP;
							backupSms();
						}else if(mFileInfo.fileflag == FILE_SMS){
							query_backup_info();//刷新
							AlertDialogUtil.getAlertDialogUtil().cancelDialog();
							ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.backup_end));
						}
					} else {
						AlertDialogUtil.getAlertDialogUtil().cancelDialog();
						ToastUtil.getToastUtils().showToastByCode(instance, msg.getRet());
					}
					break;
					
				case MsgActionCode.QUEST_RECOVER://
					if (msg.getRet() == MsgResponseCode.OK) {		
						//无操作
					} else {
						AlertDialogUtil.getAlertDialogUtil().cancelDialog();
						ToastUtil.getToastUtils().showToastByCode(instance, msg.getRet());
					}
					break;
					
				case MsgActionCode.RECOVER_BLOCK://收到恢复包
					if(mFileInfo == null){
						return;
					}
					Message m = Message.obtain(mHandler, AppConst.ANSWER_RESTORE_BLOCK, mFileInfo);
					m.arg1 = msg.getSequence();
					mHandler.sendMessage(m);
					LogUtils.getLog(getClass()).verbose("==RECOVER_BLOCK=offset:" + msg.getOffset() + "，length" + restoreByte.length);
					if(mFileInfo.statusFlag != STATUS_RESTORE){
						answer_tcp_recover_over();
					}else{
						if(msg.getFileName().equalsIgnoreCase(AppConst.CONTACT_NAME)){
							mFileInfo.bytebuffer_contact.position(msg.getOffset());
							mFileInfo.bytebuffer_contact.put(restoreByte);
						}else if(msg.getFileName().equalsIgnoreCase(AppConst.SMS_NAME)){
							mFileInfo.bytebuffer_sms.position(msg.getOffset());
							mFileInfo.bytebuffer_sms.put(restoreByte);
						}
					}
					break;
					
				case MsgActionCode.RECOVER_OVER:
					LogUtils.getLog(getClass()).verbose("====RECOVER_OVER====" + msg.getFileName());
					try {
						answer_udp_recover_over(msg.getFileName());
						if(msg.getFileName().equals(currBackupFile.getFilename())){
							Message m_answer_udp_recover_over = Message.obtain(mHandler, AppConst.ANSWER_TCP_RECOVER_OVER, mFileInfo);
							mHandler.sendMessage(m_answer_udp_recover_over);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;

				default:
					break;
				}
			} else {
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
				ToastUtil.getToastUtils().showToastByCode(instance, resp);
			}
			break;
			
		case MsgTypeCode.FILE_FORWARDING_REQ://文件转发
			switch (msg.getAction()) {
				case MsgActionCode.RECOVER_BLOCK://收到恢复包
					if(mFileInfo == null){
						return;
					}
					Message msg_recover_block = Message.obtain(mHandler, AppConst.ANSWER_RESTORE_BLOCK, mFileInfo);
					msg_recover_block.arg1 = msg.getSequence();
					mHandler.sendMessage(msg_recover_block);
					
					LogUtils.getLog(getClass()).verbose("==RECOVER_BLOCK=offset:" + msg.getOffset() + "，length" + restoreByte.length);
					if(mFileInfo.statusFlag != STATUS_RESTORE){
						answer_tcp_recover_over();
					}else{
						if(msg.getFileName().equalsIgnoreCase(AppConst.CONTACT_NAME)){
							mFileInfo.bytebuffer_contact.position(msg.getOffset());
							mFileInfo.bytebuffer_contact.put(restoreByte);
						}else if(msg.getFileName().equalsIgnoreCase(AppConst.SMS_NAME)){
							mFileInfo.bytebuffer_sms.position(msg.getOffset());
							mFileInfo.bytebuffer_sms.put(restoreByte);
						}
					}
					break;
					
				case MsgActionCode.RECOVER_OVER:
					LogUtils.getLog(getClass()).verbose("====RECOVER_OVER====" + msg.getFileName());
					try {
						answer_udp_recover_over(msg.getFileName());
						if(msg.getFileName().equals(currBackupFile.getFilename())){
							Message m = Message.obtain(mHandler, AppConst.ANSWER_TCP_RECOVER_OVER, mFileInfo);
							mHandler.sendMessage(m);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
			default:
				break;
			}
			break;
			
		case MsgTypeCode.UDP_NATS_REQ://收到穿透的ip和port后进行穿透
			if (resp == MsgResponseCode.OK){
				CloudApplication.requestID += 2;
				tryNat();
			}else{//穿透请求失败，直接使用TCP
				if(mFileInfo == null){
					return;
				}
				if(mFileInfo.statusFlag == STATUS_BACKUP){//备份
					mFileInfo.pkgSize = 8000;
					if(mFileInfo.fileflag == FILE_CONTACT){
						buildContactsMsg();
					}else if(mFileInfo.fileflag == FILE_SMS){
						BuidlpSmsMsg();
					}
				}else if(mFileInfo.statusFlag == STATUS_RESTORE){//恢复请求
					Message m = Message.obtain(mHandler, AppConst.TCP_REQUEST_RESTORE, mFileInfo);
					mHandler.sendMessage(m);
				}
			}
			break;
		case MsgTypeCode.CONTROLLER_DEV_UDP:
			if(mFileInfo == null){
				return;
			}
			if (resp == MsgResponseCode.OK){
				if(msg.getAction() == MsgActionCode.NAT_BLOCK){
					if (msg.getRet() == MsgResponseCode.OK){//收到消息，穿透成功,选择UDP
						LogUtils.getLog(getClass()).error("nat success use udp");
						if(mFileInfo.statusFlag == STATUS_BACKUP){//直接备份
							if(msg.getRet() == MsgResponseCode.OK){
								mFileInfo.pkgSize = 512;
								if(mFileInfo.fileflag == FILE_CONTACT){
									buildContactsMsg();
								}else if(mFileInfo.fileflag == FILE_SMS){
									BuidlpSmsMsg();
								}
							}else{
								AlertDialogUtil.getAlertDialogUtil().cancelDialog();
								ToastUtil.getToastUtils().showToastByCode(instance, msg.getRet());
							}
						}else if(mFileInfo.statusFlag == STATUS_RESTORE){//恢复请求
							LogUtils.getLog(getClass()).verbose("=============request_recover_udp");
							udp_request_recover();//udp恢复请求
						}
					}else{//穿透失败，选择TCP
						LogUtils.getLog(getClass()).verbose("nat error use tcp");
						if(mFileInfo.statusFlag == STATUS_BACKUP){//备份
							mFileInfo.pkgSize = 8000;
							if(mFileInfo.fileflag == FILE_CONTACT){
								buildContactsMsg();
							}else if(mFileInfo.fileflag == FILE_SMS){
								BuidlpSmsMsg();
							}
						}else if(mFileInfo.statusFlag == STATUS_RESTORE){//恢复请求
							Message m = Message.obtain(mHandler, AppConst.TCP_REQUEST_RESTORE, mFileInfo);
							mHandler.sendMessage(m);
						}
						//ToastUtil.getToastUtils().showToastByCode(instance, resp);
					}
				}else if(msg.getAction() == MsgActionCode.QUEST_RECOVER){
					LogUtils.getLog(getClass()).verbose("udp=QUEST_RECOVER===" + msg.getAction());
					if(msg.getRet() == MsgResponseCode.OK){
						//无需操作
					}else{
						AlertDialogUtil.getAlertDialogUtil().cancelDialog();
						ToastUtil.getToastUtils().showToastByCode(instance, msg.getRet());
					}
				}else if(msg.getAction() == MsgActionCode.RECOVER_BLOCK){
					LogUtils.getLog(getClass()).verbose("udp==RECOVER_BLOCK===" + msg.getAction());
					if(mFileInfo.statusFlag != STATUS_RESTORE){
						answer_tcp_recover_over();
					}else{
						if(msg.getRet() == MsgResponseCode.OK){
							if(msg.getFileName().equalsIgnoreCase(AppConst.CONTACT_NAME)){
								mFileInfo.bytebuffer_contact.position(msg.getOffset());
								mFileInfo.bytebuffer_contact.put(restoreByte);
								answer_udp_recover_block(msg);
							}else if(msg.getFileName().equalsIgnoreCase(AppConst.SMS_NAME)){
								mFileInfo.bytebuffer_sms.position(msg.getOffset());
								mFileInfo.bytebuffer_sms.put(restoreByte);
								answer_udp_recover_block(msg);
							}
						}else{
							//
						}
					}
				}else if(msg.getAction() == MsgActionCode.RECOVER_OVER){
					LogUtils.getLog(getClass()).verbose("=============recover_over_udp");
					Message m = Message.obtain(mHandler, AppConst.ANSWER_UDP_RECOVER_OVER, mFileInfo);
					mHandler.sendMessage(m);
				}else if(msg.getAction() == MsgActionCode.QUEST_BACKUP){
					LogUtils.getLog(getClass()).verbose("udp==QUEST_BACKUP===" + msg.getAction());
					if(msg.getRet() == MsgResponseCode.OK){
						Message m = Message.obtain(mHandler, AppConst.UDP_BACKUP_BLOCK, mFileInfo);
						mHandler.sendMessage(m);
					}else{
						AlertDialogUtil.getAlertDialogUtil().cancelDialog();
						ToastUtil.getToastUtils().showToastByCode(instance, msg.getRet());
					}
				}else if(msg.getAction() == MsgActionCode.BACKUP_OVER){
					LogUtils.getLog(getClass()).verbose("=============BACKUP_OVER_udp");
					if(mFileInfo.fileflag == FILE_CONTACT){
						ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.start_backup_sms));
						mFileInfo.fileflag = FILE_SMS;
						mFileInfo.statusFlag = STATUS_BACKUP;
						backupSms();
					}else if(mFileInfo.fileflag == FILE_SMS){
						query_backup_info();//刷新
						AlertDialogUtil.getAlertDialogUtil().cancelDialog();
						ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.backup_end));
					}
				}else if (msg.getAction() == MsgActionCode.BACKUP_BLOCK) {//udp 备份块
					if(msg.getRet() == MsgResponseCode.OK){
						removeUdpmsg(msg.getSequence());
					}else{
						//
					}
				}
			}else{
				ToastUtil.getToastUtils().showToastByCode(instance, resp);
			}
			break;
		default:
			break;
		}
	}
	
	private void query_backup_info(){
		try {
			MsgBean msgSend = new MsgBean();
			msgSend.setAction(MsgActionCode.QUERY_BACKUP_INFO);
			String json_content = JsonUtils.buildQUERY_BACKUP_INFO(CloudApplication.user_name);
			msgSend.setJson_content(json_content);
			CloudApplication.sequence += 2;
			msgSend.setSequence(CloudApplication.sequence);
			msgSend.setVersion(9527);
			CloudApplication.requestID += 2;
			SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);
		} catch (Exception e) {
			LogUtils.getLog(getClass()).error(e.toString());
		}
	}
	
	private void backupContacts() {
		AlertDialogUtil.getAlertDialogUtil().setDialogMessage(getResources().getString(R.string.backup_contact));
		new Thread(new Runnable() {

			@Override
			public void run() {
				allConatcts = mContactHandler.getAllDisplayName(instance, cr);
				if(mFileInfo == null || mFileInfo.statusFlag == -1){
					return;
				}
				mFileInfo.filesize = 0;
				mFileInfo.fileName = AppConst.CONTACT_NAME;
				int allSise = allConatcts.size();
				for (int i = 0; i < allSise; i++) {
					refreshContactInfo(allConatcts.get(i));
					if(mFileInfo == null || mFileInfo.statusFlag == -1){
						return;
					}
				}
				mFileInfo.vcardString = mContactHandler.backupContacts(getApplicationContext(), allConatcts);
				try {
					mFileInfo.byte_contact_backup = mFileInfo.vcardString.getBytes("UTF-8");
					mFileInfo.filesize = mFileInfo.byte_contact_backup.length;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				Message m = Message.obtain(mHandler, AppConst.REQUEST_BACKUP, mFileInfo);
				mHandler.sendMessage(m);
			}
		}).start();

	}
	
	private void buildContactsMsg() {
		allConatcts = mContactHandler.getAllDisplayName(this, cr);
		new Thread(new Runnable() {

			@Override
			public void run() {
				mFileInfo.msgs.clear();
				mFileInfo.backbytes.clear();
				ByteBuffer bytebuffer = ByteBuffer.wrap(mFileInfo.byte_contact_backup);
				
				byte[] dst;
				int num = (mFileInfo.filesize - 1) / mFileInfo.pkgSize + 1;
				MsgBean msg;
				for (int i = 0; i < num; i++) {
					if (mFileInfo.filesize - i * mFileInfo.pkgSize <= mFileInfo.pkgSize) {
						dst = new byte[mFileInfo.filesize - i * mFileInfo.pkgSize];
					} else {
						dst = new byte[mFileInfo.pkgSize];
					}
					bytebuffer.position(i * mFileInfo.pkgSize);
					bytebuffer.get(dst);
					msg = new MsgBean();
					msg.setSequence(i);
					msg.setAction(MsgActionCode.BACKUP_BLOCK);
					msg.setVersion(9527);
					msg.setFileName(mFileInfo.fileName);
					msg.setFilesize(mFileInfo.filesize);
					msg.setOffset(mFileInfo.pkgSize * i);
					mFileInfo.msgs.add(msg);
					mFileInfo.backbytes.add(dst);
				}
				
				mFileInfo.byte_contact_backup = null;
				if(mFileInfo.pkgSize == 512){
					Message m = Message.obtain(mHandler, AppConst.UDP_BACKUP_BLOCK, mFileInfo);
					mHandler.sendMessage(m);
				}else if(mFileInfo.pkgSize == 8000){
					Message m = Message.obtain(mHandler, AppConst.TCP_BACKUP_BLOCK, mFileInfo);
					mHandler.sendMessage(m);
				}
			}
		}).start();

	}
	
	
	private void backupSms() {
		AlertDialogUtil.getAlertDialogUtil().setDialogMessage(getResources().getString(R.string.backup_sms));
		new Thread(new Runnable() {

			@Override
			public void run() {
				mFileInfo.filesize = 0;
				mFileInfo.fileName = AppConst.SMS_NAME;
				ExportSmsXml mExportSmsXml = new ExportSmsXml(instance);
				try {
					mFileInfo.xmlString = mExportSmsXml.createXml();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				try {
					mFileInfo.byte_sms_backup = mFileInfo.xmlString.getBytes("UTF-8");
					mFileInfo.filesize = mFileInfo.byte_sms_backup.length;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				Message m = Message.obtain(mHandler, AppConst.REQUEST_BACKUP, mFileInfo);
				mHandler.sendMessage(m);
			}
		}).start();

	}
	
	private void BuidlpSmsMsg() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				mFileInfo.msgs.clear();
				mFileInfo.backbytes.clear();
				ByteBuffer bytebuffer = ByteBuffer.wrap(mFileInfo.byte_sms_backup);
				
				byte[] dst;
				int num = (mFileInfo.filesize - 1) / mFileInfo.pkgSize + 1;
				MsgBean msg;
				for (int i = 0; i < num; i++) {
					if (mFileInfo.filesize - i * mFileInfo.pkgSize <= mFileInfo.pkgSize) {
						dst = new byte[mFileInfo.filesize - i * mFileInfo.pkgSize];
					} else {
						dst = new byte[mFileInfo.pkgSize];
					}
					bytebuffer.position(i * mFileInfo.pkgSize);
					bytebuffer.get(dst);
					msg = new MsgBean();
					msg.setSequence(i);
					msg.setAction(MsgActionCode.BACKUP_BLOCK);
					msg.setVersion(9527);
					msg.setFileName(mFileInfo.fileName);
					msg.setFilesize(mFileInfo.filesize);
					msg.setOffset(mFileInfo.pkgSize * i);
					mFileInfo.msgs.add(msg);
					mFileInfo.backbytes.add(dst);
				}
				mFileInfo.byte_sms_backup = null;
				if(mFileInfo.pkgSize == 512){
					Message m = Message.obtain(mHandler, AppConst.UDP_BACKUP_BLOCK, mFileInfo);
					mHandler.sendMessage(m);
				}else if(mFileInfo.pkgSize == 8000){
					Message m = Message.obtain(mHandler, AppConst.TCP_BACKUP_BLOCK, mFileInfo);
					mHandler.sendMessage(m);
				}
			}
		}).start();

	}
	
	private void restoreContact() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message m = Message.obtain(mHandler, AppConst.RESTORE_SUCCESS, mFileInfo);
				m.arg1 = FILE_CONTACT;
				m.arg2 = 0;
				try {
					mContactHandler.restoreContacts(mFileInfo.bytebuffer_contact.array(), instance);
					LogUtils.getLog(getClass()).error("restoreContact end=========");
				} catch (FileNotFoundException e) {
					LogUtils.getLog(getClass()).error(e.toString());
				} catch (IOException e) {
					LogUtils.getLog(getClass()).error(e.toString());
				} catch (VCardException e) {
					LogUtils.getLog(getClass()).error(e.toString());
				} catch (ContactsExcetion e) {
					m.arg2 = -1;
					LogUtils.getLog(getClass()).error(e.toString());
				}
				
				mHandler.sendMessage(m);
			}
		}).start();

	}
	
	
	private void restoreSms() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message m = Message.obtain(mHandler, AppConst.RESTORE_SUCCESS, mFileInfo);
				m.arg1 = FILE_SMS;
				m.arg2 = 0;
				try {
					ImportSms mImportSms = new ImportSms(instance);
					mImportSms.insertSMS(mFileInfo.bytebuffer_sms.array());	
				} catch (IOException e) {
					e.printStackTrace();
				}catch (SmsExcetion e) {
					m.arg2 = -1;
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				LogUtils.getLog(getClass()).error("insert smsItems end===========");
				mHandler.sendMessage(m);
			}
		}).start();

	}
	
	//备份请求tcp
	private void tcp_request_backup(){
		try {
			MsgBean msgSend = new MsgBean();
			msgSend.setAction(MsgActionCode.QUEST_BACKUP);
			msgSend.setFileName(mFileInfo.fileName);
			msgSend.setFilesize(mFileInfo.filesize);
			String json_content = JsonUtils.buildQUEST_BACKUP();
			msgSend.setJson_content(json_content);
			CloudApplication.sequence += 2;
			msgSend.setSequence(CloudApplication.sequence);
			msgSend.setVersion(9527);
			CloudApplication.requestID += 2;
			SocketUtils.command_forwarding_req(msgSend, instance,
					CloudApplication.requestID);
		} catch (Exception e) {
			//AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			LogUtils.getLog(getClass()).error(e.toString());
		}
	}
	
	//穿透请求tcp
	private void tcp_request_to_nat(){
		try {
			CloudApplication.requestID += 2;
			SocketUtils.Nats_req(instance, CloudApplication.requestID);
		} catch (Exception e) {
			//AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			LogUtils.getLog(getClass()).error(e.toString());
		}
	}
	
	//TCP备份文件块
	private void tcp_backup_block(){
		LogUtils.getLog(getClass()).verbose(mFileInfo.fileName + "tcp_backup_block=start=" + System.currentTimeMillis());
		if(mFileInfo.msgs.size() > 0){
			CloudApplication.requestID += 2;
			try {
				SocketUtils.command_forwarding_backup(mFileInfo.msgs.get(0),
						mFileInfo.backbytes.get(0), instance,
						CloudApplication.requestID);
			} catch (Exception e) {
				LogUtils.getLog(getClass()).error(e.toString());
			}
			LogUtils.getLog(getClass()).verbose(mFileInfo.fileName + "tcp_backup_block=end=" + System.currentTimeMillis());
		}else{
			AlertDialogUtil.getAlertDialogUtil().setDialogMessage(getResources().getString(R.string.backup_contact_end));
			MsgBean msgSend = new MsgBean();
			msgSend.setAction(MsgActionCode.BACKUP_OVER);
			CloudApplication.sequence += 2;
			msgSend.setFileName(mFileInfo.fileName);
			msgSend.setFilesize(mFileInfo.filesize);
			msgSend.setSequence(CloudApplication.sequence);
			msgSend.setVersion(9527);
			CloudApplication.requestID += 2;
			try {
				SocketUtils.command_forwarding_req(msgSend, instance,
						CloudApplication.requestID);
			} catch (Exception e) {
				LogUtils.getLog(getClass()).error(e.toString());
			}
		}
	}
	
	//恢复请求TCP
	public void tcp_quest_recover(){
		if(currBackupFile.getFilename().equalsIgnoreCase(AppConst.CONTACT_NAME)){
			mFileInfo.bytebuffer_contact = ByteBuffer.allocate(Integer.parseInt(currBackupFile.getFilesize()));
		}else if(currBackupFile.getFilename().equalsIgnoreCase(AppConst.SMS_NAME)){
			mFileInfo.bytebuffer_sms = ByteBuffer.allocate(Integer.parseInt(currBackupFile.getFilesize()));
		}
		LogUtils.getLog(getClass()).verbose("allocate==size" + Integer.parseInt(currBackupFile.getFilesize()));
		try {
			MsgBean msgSend = new MsgBean();
			msgSend.setAction(MsgActionCode.QUEST_RECOVER);
			msgSend.setFileName(currBackupFile.getFilename());
			msgSend.setFilesize(Integer.parseInt(currBackupFile.getFilesize()));
			String json_content = JsonUtils.buildQUEST_RECOVER();
			msgSend.setJson_content(json_content);
			CloudApplication.sequence += 2;
			msgSend.setSequence(CloudApplication.sequence);
			msgSend.setVersion(9527);
			CloudApplication.requestID += 2;
			SocketUtils.command_forwarding_req(msgSend, instance,
					CloudApplication.requestID);
		} catch (Exception e) {
			//AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			LogUtils.getLog(getClass()).error(e.toString());
		}
	}
	
	private void answer_tcp_restore_block(Message msg){
		try {
			MsgBean msgSend = new MsgBean();
			msgSend.setAction(MsgActionCode.RECOVER_BLOCK);
			msgSend.setFileName(currBackupFile.getFilename());
			msgSend.setRet(200);
			msgSend.setVersion(9527);
			msgSend.setSequence(msg.arg1);
			CloudApplication.requestID += 2;
			SocketUtils.command_forwarding_req(msgSend, instance,
					CloudApplication.requestID);
		} catch (Exception e) {
			//AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			LogUtils.getLog(getClass()).error(e.toString());
		}
	}
	
	private void answer_tcp_recover_over(){
		try {
			MsgBean msgSend = new MsgBean();
			msgSend.setAction(MsgActionCode.RECOVER_OVER);
			if(currBackupFile != null){
				msgSend.setFileName(currBackupFile.getFilename());
			}
			msgSend.setRet(200);
			msgSend.setVersion(9527);
			CloudApplication.requestID += 2;
			SocketUtils.command_forwarding_req(msgSend, instance,
					CloudApplication.requestID);
		} catch (Exception e) {
			//AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			LogUtils.getLog(getClass()).error(e.toString());
		}
	}
	
	private void request_to_nat_before_recover(){
		if(mFileInfo.fileflag == FILE_SMS){
			LogUtils.getLog(getClass()).error("request_to_nat_before_recover === FILE_SMS");
			mFileInfo.statusFlag = STATUS_RESTORE;
			currBackupFile = null;
			int size= mBackupFileS.size();
			for (int i = 0; i < size; i++) {
				if(mBackupFileS.get(i).getFilename().startsWith(AppConst.SMS_NAME)){
					currBackupFile = mBackupFileS.get(i);
					break;
				}
			}
			if(currBackupFile != null){
				Message m = Message.obtain(mHandler, AppConst.REQUEST_TO_NAT, mFileInfo);
				mHandler.sendMessage(m);//发送穿透请求
				ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.restore_sms));
			}else{
				mFileInfo.fileflag = FILE_SMS;
				mFileInfo.statusFlag = STATUS_RESTORE;
				request_to_nat_before_recover();
			}
		}else if(mFileInfo.fileflag == FILE_CONTACT){
			LogUtils.getLog(getClass()).error("request_to_nat_before_recover === FILE_CONTACT");
			int size= mBackupFileS.size();
			for (int i = 0; i < size; i++) {
				if(mBackupFileS.get(i).getFilename().startsWith(AppConst.CONTACT_NAME)){
					currBackupFile = mBackupFileS.get(i);
					break;
				}
			}
			if(currBackupFile != null){
				Message m = Message.obtain(mHandler, AppConst.REQUEST_TO_NAT, mFileInfo);
				mHandler.sendMessage(m);//发送穿透请求
				ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.restore_contact));
			}else{
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
				ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.restore_null));
			}
		}
	}
	
	private void udp_backup_block(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean flag = true;
				while (flag) {
					synchronized (mFileInfo) {
						if(mFileInfo.msgs.size() == 0){//发送完毕
							try {
								MsgBean msg = new MsgBean();
								msg.setSequence(new Random().nextInt());
								msg.setFileName(mFileInfo.fileName);
								msg.setFilesize(mFileInfo.filesize);
								msg.setAction(MsgActionCode.BACKUP_OVER);
								UDPClient.getInstance().sendToDevice(MessageUtils.build_UDP_msg(msg));
							} catch (IOException e) {
								e.printStackTrace();
							}
							flag = false;
						}else{
							for (MsgBean msgBean : mFileInfo.msgs) {
								int seq = msgBean.getSequence();
								try {
									UDPClient.getInstance().sendToDevice(MessageUtils.build_nats_backup_block_msg(msgBean, mFileInfo.backbytes.get(seq)));
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	
	private void udp_request_recover(){
		if(currBackupFile.getFilename().equalsIgnoreCase(AppConst.CONTACT_NAME)){
			mFileInfo.bytebuffer_contact = ByteBuffer.allocate(Integer.parseInt(currBackupFile.getFilesize()));
		}else if(currBackupFile.getFilename().equalsIgnoreCase(AppConst.SMS_NAME)){
			mFileInfo.bytebuffer_sms = ByteBuffer.allocate(Integer.parseInt(currBackupFile.getFilesize()));
		}
		MsgBean msgSend = new MsgBean();
		msgSend.setVersion(9527);
		msgSend.setSequence(new Random().nextInt());
		msgSend.setAction(MsgActionCode.QUEST_RECOVER);
		msgSend.setFileName(currBackupFile.getFilename());
		msgSend.setFilesize(Integer.parseInt(currBackupFile.getFilesize()));
		UDPClient.getInstance().sendAsync(MessageUtils.build_UDP_msg(msgSend));
	}
	
	private void tryNat(){
		UDPClient.getInstance().sendAsyncTryNat(MessageUtils.build_nats_try_msg(CloudApplication.requestID));
	}
	
	private void answer_udp_recover_block(MsgBean msg){
		UDPClient.getInstance().sendAsync(MessageUtils.build_UDP_msg(msg));//回复udp恢复消息块
	}
	
	private void answer_udp_recover_over(){
		MsgBean msgSend = new MsgBean();
		msgSend.setAction(MsgActionCode.RECOVER_OVER);
		if(currBackupFile != null){
			msgSend.setFileName(currBackupFile.getFilename());
		}
		msgSend.setRet(200);
		msgSend.setVersion(9527);
		UDPClient.getInstance().sendAsync(MessageUtils.build_UDP_msg(msgSend));//结束UDP恢复
	}
	
	private void answer_udp_recover_over(String filename){
		MsgBean msgSend = new MsgBean();
		msgSend.setAction(MsgActionCode.RECOVER_OVER);
		msgSend.setFileName(filename);
		msgSend.setRet(200);
		msgSend.setVersion(9527);
		UDPClient.getInstance().sendAsync(MessageUtils.build_UDP_msg(msgSend));//结束UDP恢复
	}
	
	private void removeUdpmsg(final int seq){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				synchronized (mFileInfo) {
					for (MsgBean msgBean : mFileInfo.msgs) {
						int temp = msgBean.getSequence();
						if(temp == seq){
							mFileInfo.msgs.remove(msgBean);
							break;
						}
					}
				}
			}
		}).start();
	}
}
