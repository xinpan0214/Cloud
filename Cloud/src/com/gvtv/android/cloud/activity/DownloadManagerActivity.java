package com.gvtv.android.cloud.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.OnCountChangedListener;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.bean.AppInfo;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.bean.TaskReturn;
import com.gvtv.android.cloud.bean.VideoInfo;
import com.gvtv.android.cloud.broadcast.MessageReceiver;
import com.gvtv.android.cloud.broadcast.MessageReceiver.MsgHandler;
import com.gvtv.android.cloud.db.SqliteUtils;
import com.gvtv.android.cloud.msg.JsonUtils;
import com.gvtv.android.cloud.msg.MsgActionCode;
import com.gvtv.android.cloud.msg.MsgResponseCode;
import com.gvtv.android.cloud.msg.MsgTypeCode;
import com.gvtv.android.cloud.sockets.SendTcpMsgAsyncTask.OnSendendListener;
import com.gvtv.android.cloud.sockets.SocketUtils;
import com.gvtv.android.cloud.util.AlertDialogUtil;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.ToastUtil;
import com.gvtv.android.cloud.view.SelectPicPopupWindow;
import com.gvtv.android.cloud.view.TitleView;
import com.gvtv.android.cloud.view.utils.ViewAnimation;

@SuppressLint("HandlerLeak")
public class DownloadManagerActivity extends FragmentActivity implements OnClickListener , OnCountChangedListener, OnCheckedChangeListener, MsgHandler, OnSendendListener{

	public TitleView mTitleView;
	public FrameLayout flyt_navi;
	private RadioGroup main_radiogroup;
	private RadioButton rbtn_downloading, rbtn_downloaded;
	private View mAnimView;
	public List<AppInfo> groupList_downlaoding = new ArrayList<AppInfo>();
	public List<AppInfo> groupList_downlaoded = new ArrayList<AppInfo>();
	
	public List<AppInfo> temp_groupList_downlaoding = new ArrayList<AppInfo>();
	public List<AppInfo> temp_groupList_downlaoded = new ArrayList<AppInfo>();
	
	private ArrayList<VideoInfo> vFiles_temp = new ArrayList<VideoInfo>();
	public int pagenum_downlaoding;
	public int pagenum_downlaoded;
	
	public int pagenum_downlaoding_refresh;
	public int pagenum_downlaoded_refresh;
	
	public int num_downlaoding;
	public int num_downlaoded;
	
	private TextView tv_downloadingNum;//正在下载的数量
	private TextView tv_downloadedNum;//已完成的数量
	
	private boolean isEditting;
	private int startLeft;
	private int endRight;
	private BaseFragment[] mFragments = new BaseFragment[2];
	private int checked;
	private DownloadManagerActivity instance;
	public int editMethod;//0暂停，1继续2，删除3重新下载4删除
	private int del_flag;//取值：0为不删除源文件；1为删除源文件
	
	private SelectPicPopupWindow menuWindow;
	private Timer mTimer;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {	
			switch (msg.what) {
			case 0:
				try {
					refreshVideo(pagenum_downlaoding_refresh, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 1:
				try {
					refreshFinVideo(pagenum_downlaoded_refresh, null);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
				
			case 3:
				try {
					queryVideo(pagenum_downlaoding, instance);
				} catch (Exception e) {
					LogUtils.getLog(getClass()).error(e.toString());
				}
				break;
			case 4:
				try {
					queryFinVideo(pagenum_downlaoded, instance);
				} catch (Exception e) {
					LogUtils.getLog(getClass()).error(e.toString());
				}
				break;
				
			case 5://全部刷新正在下载页面
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
				num_downlaoding = msg.arg1;
				tv_downloadingNum.setText(num_downlaoding + "");
				groupList_downlaoding.clear();
				groupList_downlaoding.addAll(temp_groupList_downlaoding);
				temp_groupList_downlaoding.clear();
				if(mFragments[0].isAdded()){
					mFragments[0].OnFragmentStatusChanged(isEditting);
				}
				break;
				
			case 6://全部刷新已经下载页面
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
				num_downlaoded = msg.arg1;
				tv_downloadedNum.setText(num_downlaoded + "");
				groupList_downlaoded.clear();
				groupList_downlaoded.addAll(temp_groupList_downlaoded);
				temp_groupList_downlaoded.clear();
				if(mFragments[1].isAdded()){
					mFragments[1].OnFragmentStatusChanged(isEditting);
				}
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download_manager);
		instance = this;
		findViews();
		setListeners();
		init();
		checked = 0;
		replaceFragment(checked);
		CloudApplication.getInstance().addActivity(instance);
		AlertDialogUtil.getAlertDialogUtil().showDialog(instance,
				instance.getResources().getString(R.string.loading_please_wait));
		MessageReceiver.msghList.add(instance);
	}
	
	
	private void findViews() {
		mTitleView = (TitleView) findViewById(R.id.downloadmanagertitle);
		flyt_navi = (FrameLayout) findViewById(R.id.flyt_navi);
		main_radiogroup = (RadioGroup) findViewById(R.id.main_radiogroup);
		rbtn_downloading = (RadioButton) findViewById(R.id.downloadingBtn);
		rbtn_downloaded = (RadioButton) findViewById(R.id.downloadedBtn);
		tv_downloadingNum = (TextView) findViewById(R.id.downloadingNum);
		tv_downloadedNum = (TextView) findViewById(R.id.downloadedNum);
		mTitleView.getRightButton().setText(getResources().getString(R.string.edit));
		mAnimView = findViewById(R.id.checkedLine);
	}
	
	private void setListeners(){
		mTitleView.getBackTextView().setOnClickListener(this);
		mTitleView.getRightButton().setOnClickListener(this);
		rbtn_downloading.setOnClickListener(this);
		rbtn_downloaded.setOnClickListener(this);
		main_radiogroup.setOnCheckedChangeListener(this);
	}
	
	private void init(){
		mFragments[0] = new DownloadingFragment();
		mFragments[1] = new DownloadedFragment();
		tv_downloadedNum.setText("0");
		tv_downloadingNum.setText("0");
		flyt_navi.setVisibility(View.VISIBLE);
		LayoutParams mLayoutParams = mAnimView.getLayoutParams();
		mLayoutParams.width = getResources().getDisplayMetrics().widthPixels /2;
		mAnimView.setLayoutParams(mLayoutParams);
	}
	
	
	private  void replaceFragment(int index) {
		FragmentManager fm;
		FragmentTransaction ft;
		fm = getSupportFragmentManager();
		ft = fm.beginTransaction();
		ft.replace(R.id.layout_body, mFragments[index]);
		ft.commit();
	}


	@Override
	public void onClick(View v) {
		if(v == mTitleView.getBackTextView()){
			finish();
		}else if(v == mTitleView.getRightButton()){
			if((checked == 0 && groupList_downlaoding.size() <= 0) || (checked == 1 && groupList_downlaoded.size() <= 0)){
				return;
			}
			if(isEditting){
				isEditting = false;
				try {
					editDownload(editMethod);
				} catch (Exception e) {
					if(mFragments[checked].isAdded()){
						mFragments[checked].OnFragmentStatusChanged(isEditting);
					}
					e.printStackTrace();
				}
			}else{
				mTitleView.getRightButton().setText(getResources().getString(R.string.complete));
				isEditting = true;
				flyt_navi.setVisibility(View.GONE);
				if(mFragments[checked].isAdded()){
					mFragments[checked].OnFragmentStatusChanged(isEditting);
				}
			}
		}else if(v == rbtn_downloading){
			rbtn_downloading.setChecked(true);
		}else if(v == rbtn_downloaded){
			rbtn_downloaded.setChecked(true);
		}
	}

	@Override
	public void onDownloadingCountChanged(int downloadingCount) {
		if(tv_downloadingNum != null){
			tv_downloadingNum.setText(downloadingCount + "");
		}
	}

	@Override
	public void onDownloadedCountChanged(int downloadedCount) {
		if(tv_downloadedNum != null){
			tv_downloadedNum.setText(downloadedCount + "");
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.downloadingBtn:
			rbtn_downloading.setEnabled(false);
			checked = 0;
			replaceFragment(checked);
			startLeft = getResources().getDisplayMetrics().widthPixels / 2;
			endRight = 0;
			ViewAnimation.setImageSlide(mAnimView, startLeft , endRight, 0, 0);
			rbtn_downloading.setEnabled(true);
			break;
			
		case R.id.downloadedBtn:
			rbtn_downloaded.setEnabled(false);
			checked = 1;
			replaceFragment(checked);
			startLeft = 0;
			endRight = getResources().getDisplayMetrics().widthPixels / 2;
			ViewAnimation.setImageSlide(mAnimView, startLeft , endRight, 0, 0);
			rbtn_downloaded.setEnabled(true);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		query();
		startTimer();
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		mHandler.removeMessages(0);
		mHandler.removeMessages(1);
		mHandler.removeMessages(2);
		mHandler.removeMessages(3);
		mHandler.removeMessages(4);
		mHandler.removeMessages(5);
		mHandler.removeMessages(6);
		for (int i = 0; i < MessageReceiver.msghList.size(); i++) {
			if(MessageReceiver.msghList.get(i) instanceof DownloadManagerActivity){
				MessageReceiver.msghList.remove(i);
				i--;
			}
		}
		CloudApplication.getInstance().removeActivity(instance);
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		stopTimer();
		super.onPause();
	}

	@Override
	public void isSendend() {
		if(mFragments[0].isAdded()){
			mFragments[0].OnFragmentStatusChanged(isEditting);
		}
		if(mFragments[1].isAdded()){
			mFragments[1].OnFragmentStatusChanged(isEditting);
		}
	}
	@Override
	public void onMessage(int type, int resp, MsgBean msg, byte[] restoreByte) {
		switch (type) {
		case MsgTypeCode.COMMAND_FORWARDING_REQ:
			AlertDialogUtil.getAlertDialogUtil().setTimeout_amount(0);
			if(resp == MsgResponseCode.OK){
				switch (msg.getAction()) {
				case MsgActionCode.DEL_TASK:
					String json_del_task = msg.getJson_content();
					//LogUtils.getLog(getClass()).verbose("json_del_task: " + json_del_task);
					ArrayList<TaskReturn> rets_del_task = JsonUtils.parseDEL_TASK(json_del_task);
					parsedelVideo(rets_del_task);
					break;
					
				case MsgActionCode.PAUSE_TASK:
					String json_pause_task = msg.getJson_content();
					LogUtils.getLog(getClass()).verbose("json_pause_task: " + json_pause_task);
					List<TaskReturn> rets_pause_task = JsonUtils.parsePASUE_TASK(json_pause_task);
					parsepauseVideo(rets_pause_task);
					break;
					
				case MsgActionCode.RESUME_TASK:
					String json_resume_task = msg.getJson_content();
					//LogUtils.getLog(getClass()).verbose("json_resume_task: " + json_resume_task);
					List<TaskReturn> rets_resume_task = JsonUtils.parseRESUME_TASK(json_resume_task);
					parseresumeVideo(rets_resume_task);
					break;
	
				case MsgActionCode.QUERY_TASK:
					String json_query_task = msg.getJson_content();
					//LogUtils.getLog(getClass()).verbose("json_query_task:msg.getSequence() " + msg.getSequence() + "---------" + json_query_task);
					TaskReturn ret_query_task = JsonUtils.parseQUERY_TASK(json_query_task);
					if(msg.getSequence() % 2 == 0){
						parsequeryVideo(ret_query_task);
						num_downlaoding = ret_query_task.getTasknum();
						tv_downloadingNum.setText(num_downlaoding + "");
						if(mFragments[0].isAdded()){
							mFragments[0].OnFragmentStatusChanged(isEditting);
						}
					}else{
						new Thread(new ParseRefreshVideoRunnable(ret_query_task)).start();
						//parseRefreshVideo(ret_query_task);
					}
					break;
					
				case MsgActionCode.DEL_FIN_TASK:
					String json_del_fin_task = msg.getJson_content();
					//LogUtils.getLog(getClass()).verbose("json_del_fin_task: " + json_del_fin_task);
					List<TaskReturn> taskRets = JsonUtils.parseDEL_FIN_TASK(json_del_fin_task);
					parsedelfinVideo(taskRets);
					break;
					
				case MsgActionCode.QUERY_FIN_TASK:
					String json_query_fin_task = msg.getJson_content();
					//LogUtils.getLog(getClass()).verbose("json_query_fin_task: " + json_query_fin_task);
					TaskReturn ret_query_fin_task = JsonUtils.parseQUERY_FIN_TASK(json_query_fin_task);
					if(msg.getSequence() % 2 == 0){
						parsequeryFinVideo(ret_query_fin_task);
						num_downlaoded = ret_query_fin_task.getTasknum();
						tv_downloadedNum.setText(num_downlaoded + "");
						if(mFragments[1].isAdded()){
							mFragments[1].OnFragmentStatusChanged(isEditting);
						}
					}else{
						//parseRefreshFinVideo(ret_query_fin_task);
						new Thread(new ParseRefreshFinVideoRunnable(ret_query_fin_task));
					}
					break;
					
				case MsgActionCode.REDOWNLOAD_TASK:
					String json_add_task = msg.getJson_content();
					//LogUtils.getLog(getClass()).verbose("json_add_task: " + json_add_task);
					ArrayList<String> taskIds = JsonUtils.parseReDownload_TASK(json_add_task);
					parseredownloadVideo(taskIds);
					break;
					
				default:
					break;
				}
			}else{
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
				if(mFragments[checked].isAdded()){
					mFragments[checked].OnFragmentStatusChanged(isEditting);
				}
				ToastUtil.getToastUtils().showToastByCode(instance, resp);
			}
			break;
		case MsgTypeCode.TIME_OUT:
			AlertDialogUtil.getAlertDialogUtil().setTimeout_amount(AlertDialogUtil.getAlertDialogUtil().getTimeout_amount() + 1);
			if(AlertDialogUtil.getAlertDialogUtil().getTimeout_amount() == 2){
				AlertDialogUtil.getAlertDialogUtil().cancelDialogWhenTimeout();
			}
		default:
			break;
		}
	}
	@Override
	public void onNetChange(boolean isNetConnected) {
		if(mFragments[0].isAdded()){
			mFragments[0].OnFragmentStatusChanged(isEditting);
		}
		if(mFragments[1].isAdded()){
			mFragments[1].OnFragmentStatusChanged(isEditting);
		}
	}

	
	public void editDownload(int method) throws Exception{
		switch (method) {
		case 0:
			pauseVideo();
			if(mFragments[checked].isAdded()){
				mFragments[checked].OnFragmentStatusChanged(isEditting);
			}
			break;
			
		case 1:
			resumeVideo();
			if(mFragments[checked].isAdded()){
				mFragments[checked].OnFragmentStatusChanged(isEditting);
			}
			break;
			
		case 2:
			delVideo();
			if(mFragments[checked].isAdded()){
				mFragments[checked].OnFragmentStatusChanged(isEditting);
			}
			break;
	
		case 3:
			redownloadVideo();
			if(mFragments[checked].isAdded()){
				mFragments[checked].OnFragmentStatusChanged(isEditting);
			}
			break;
	
		case 4:
			menuWindow = new SelectPicPopupWindow(instance, new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					menuWindow.dismiss();
					switch (v.getId()) {
					case R.id.btn_delete_file:
						del_flag = 1;
						try {
							delfinVideo(del_flag);
							if(mFragments[checked].isAdded()){
								mFragments[checked].OnFragmentStatusChanged(isEditting);
							}
						} catch (Exception e) {
							if(mFragments[checked].isAdded()){
								mFragments[checked].OnFragmentStatusChanged(isEditting);
							}
							//LogUtils.getLog(getClass()).error(e.toString());
						}
						break;
					case R.id.btn_reserve_file:	
						del_flag = 0;
						try {
							delfinVideo(del_flag);
							if(mFragments[checked].isAdded()){
								mFragments[checked].OnFragmentStatusChanged(isEditting);
							}
						} catch (Exception e) {
							if(mFragments[checked].isAdded()){
								mFragments[checked].OnFragmentStatusChanged(isEditting);
							}
							//LogUtils.getLog(getClass()).error(e.toString());
						}
						break;
					default:
						break;
					}
				}
			});
			menuWindow.showAtLocation(instance.findViewById(R.id.layout_body), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			break;

		default:
			break;
		}
	}
	
	
	public void query(){
		try {
			queryVideo(pagenum_downlaoding, instance);
		} catch (Exception e) {
			LogUtils.getLog(getClass()).error(e.toString());
		}
		try {
			queryFinVideo(pagenum_downlaoded, instance);
		} catch (Exception e) {
			LogUtils.getLog(getClass()).error(e.toString());
		}
	}
	
	
	public void queryVideo(int pagenum, OnSendendListener mOnSendendListener) throws Exception{
		MsgBean msgSend = new MsgBean();
		msgSend.setAction(MsgActionCode.QUERY_TASK);
		String json_content = JsonUtils.buildQUERY_TASK(CloudApplication.user_name, pagenum);
		msgSend.setJson_content(json_content);
		CloudApplication.sequence += 2;
		msgSend.setSequence(CloudApplication.sequence);
		msgSend.setVersion(9527);
		CloudApplication.requestID += 2;
		SocketUtils.command_forwarding_req(msgSend, mOnSendendListener, CloudApplication.requestID);
	}
	
	public void refreshVideo(int pagenum, OnSendendListener mOnSendendListener) throws Exception{
		MsgBean msgSend = new MsgBean();
		msgSend.setAction(MsgActionCode.QUERY_TASK);
		String json_content = JsonUtils.buildQUERY_TASK(CloudApplication.user_name, pagenum);
		msgSend.setJson_content(json_content);
		CloudApplication.sequence_refresh += 2;
		msgSend.setSequence(CloudApplication.sequence_refresh);
		msgSend.setVersion(9527);
		CloudApplication.requestID += 2;
		SocketUtils.command_forwarding_req(msgSend, mOnSendendListener, CloudApplication.requestID);
	}
	
	class ParseRefreshVideoRunnable implements Runnable{
		TaskReturn taskRet;
		public ParseRefreshVideoRunnable(TaskReturn taskRet) {
			this.taskRet = taskRet;
		}
		
		@Override
		public void run() {
			parseRefreshVideo(taskRet);
		}
	}
	
	public synchronized void parseRefreshVideo(TaskReturn ret_query_task){
		if(pagenum_downlaoding_refresh == 0){
			temp_groupList_downlaoding.clear();
		}
		ArrayList<VideoInfo> videos_query_task = ret_query_task.getvFiles();
		if(videos_query_task == null || videos_query_task.size() == 0){
			pagenum_downlaoding_refresh = 0;
		}else{
			pagenum_downlaoding_refresh ++;
		}
		
		/*替换*/
		for (int i = 0; i < videos_query_task.size(); i++) {
			for (AppInfo appinfo : temp_groupList_downlaoding) {
				if(appinfo.getAppcode().equalsIgnoreCase(videos_query_task.get(i).getAppid())){
					List<VideoInfo> infos = appinfo.getVideoInfoList();
					for (VideoInfo videoInfo : infos) {
						if(videoInfo.getTaskid().equalsIgnoreCase(videos_query_task.get(i).getTaskid())){
							videoInfo.setRatio(videos_query_task.get(i).getRatio());
							videoInfo.setStatus(videos_query_task.get(i).getStatus());
							videos_query_task.remove(i);//替换后立刻移除
							i--;
							break;
						}
					}
					break;
				}
			}
		}
		
		/*插入*/
		AppInfo app_downloading = null;
		boolean isAdded;
		for (int i = 0; i < videos_query_task.size(); i++) {
			if(temp_groupList_downlaoding.size() == 0){
				app_downloading = new AppInfo();
				app_downloading.setAppname(videos_query_task.get(i).getAppname());
				app_downloading.setAppcode(videos_query_task.get(i).getAppid());
				AppInfo app_temp = SqliteUtils.getInstance(instance).queryAppByCode(app_downloading.getAppcode());
				if(app_temp != null){
					app_downloading.setIcon(app_temp.getIcon());
				}
				
				app_downloading.getVideoInfoList().add(videos_query_task.get(i));
				temp_groupList_downlaoding.add(app_downloading);
				videos_query_task.remove(i);//插入后立刻移除
				i--;
			}else{
				isAdded = false;
				for (AppInfo appinfo : temp_groupList_downlaoding) {
					if(appinfo.getAppcode().equalsIgnoreCase(videos_query_task.get(i).getAppid())){
						appinfo.getVideoInfoList().add(videos_query_task.get(i));
						videos_query_task.remove(i);//插入后立刻移除
						i--;
						isAdded = true;
						break;
					}else{
						continue;
					}
				}
				if(!isAdded){
					app_downloading = new AppInfo();
					app_downloading.setAppname(videos_query_task.get(i).getAppname());
					app_downloading.setAppcode(videos_query_task.get(i).getAppid());
					AppInfo app_temp = SqliteUtils.getInstance(instance).queryAppByCode(app_downloading.getAppcode());
					if(app_temp != null){
						app_downloading.setIcon(app_temp.getIcon());
					}
					app_downloading.getVideoInfoList().add(videos_query_task.get(i));
					temp_groupList_downlaoding.add(app_downloading);
					videos_query_task.remove(i);//插入后立刻移除
					i--;
				}
			}
		}
		if(pagenum_downlaoding_refresh == 0){
			if(!isEditting){
				Message msg = Message.obtain();
				msg.what = 5;
				msg.arg1 = ret_query_task.getTasknum();
				mHandler.sendMessage(msg);
			}
		}else{
			mHandler.sendEmptyMessage(0);
		}
	}
	
	public void parsequeryVideo(TaskReturn ret_query_task){
		//LogUtils.getLog(getClass()).info("parsequeryVideo=======start: " + System.currentTimeMillis());
		ArrayList<VideoInfo> videos_query_task = ret_query_task.getvFiles();
		
		if(videos_query_task.size() >0){
			pagenum_downlaoding = ret_query_task.getPagenum() + 1;
			mHandler.sendEmptyMessage(3);
		}else{
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			pagenum_downlaoding = 0;
		}
		
		/*替换*/
		for (int i = 0; i < videos_query_task.size(); i++) {
			for (AppInfo appinfo : groupList_downlaoding) {
				if(appinfo.getAppcode().equalsIgnoreCase(videos_query_task.get(i).getAppid())){
					List<VideoInfo> infos = appinfo.getVideoInfoList();
					for (VideoInfo videoInfo : infos) {
						if(videoInfo.getTaskid().equalsIgnoreCase(videos_query_task.get(i).getTaskid())){
							videoInfo.setRatio(videos_query_task.get(i).getRatio());
							videoInfo.setStatus(videos_query_task.get(i).getStatus());
							videos_query_task.remove(i);//替换后立刻移除
							i--;
							break;
						}
					}
					break;
				}
			}
		}
		
		/*插入*/
		AppInfo app_downloading = null;
		boolean isAdded;
		for (int i = 0; i < videos_query_task.size(); i++) {
			if(groupList_downlaoding.size() == 0){
				app_downloading = new AppInfo();
				app_downloading.setAppname(videos_query_task.get(i).getAppname());
				app_downloading.setAppcode(videos_query_task.get(i).getAppid());
				AppInfo app_temp = SqliteUtils.getInstance(instance).queryAppByCode(app_downloading.getAppcode());
				if(app_temp != null){
					app_downloading.setIcon(app_temp.getIcon());
					//LogUtils.getLog(getClass()).verbose(app_temp.getIcon());
				}
				
				app_downloading.getVideoInfoList().add(videos_query_task.get(i));
				groupList_downlaoding.add(app_downloading);
				videos_query_task.remove(i);//插入后立刻移除
				i--;
			}else{
				isAdded = false;
				for (AppInfo appinfo : groupList_downlaoding) {
					if(appinfo.getAppcode().equalsIgnoreCase(videos_query_task.get(i).getAppid())){
						appinfo.getVideoInfoList().add(videos_query_task.get(i));
						videos_query_task.remove(i);//插入后立刻移除
						i--;
						isAdded = true;
						break;
					}else{
						continue;
					}
				}
				if(!isAdded){
					app_downloading = new AppInfo();
					app_downloading.setAppname(videos_query_task.get(i).getAppname());
					app_downloading.setAppcode(videos_query_task.get(i).getAppid());
					AppInfo app_temp = SqliteUtils.getInstance(instance).queryAppByCode(app_downloading.getAppcode());
					if(app_temp != null){
						app_downloading.setIcon(app_temp.getIcon());
						//LogUtils.getLog(getClass()).verbose(app_temp.getIcon());
					}
					app_downloading.getVideoInfoList().add(videos_query_task.get(i));
					groupList_downlaoding.add(app_downloading);
					videos_query_task.remove(i);//插入后立刻移除
					i--;
				}
			}
		}
		//LogUtils.getLog(getClass()).info("parsequeryVideo=======end: " + System.currentTimeMillis());
	}
	
	
	public void queryFinVideo(int pagenum, OnSendendListener mOnSendendListener) throws Exception{
		MsgBean msgSend = new MsgBean();
		msgSend.setAction(MsgActionCode.QUERY_FIN_TASK);
		String json_content = JsonUtils.buildQUERY_FIN_TASK(CloudApplication.user_name, pagenum);
		msgSend.setJson_content(json_content);
		CloudApplication.sequence += 2;
		msgSend.setSequence(CloudApplication.sequence);
		msgSend.setVersion(9527);
		CloudApplication.requestID += 2;
		SocketUtils.command_forwarding_req(msgSend, mOnSendendListener, CloudApplication.requestID);
	}
	
	public void refreshFinVideo(int pagenum, OnSendendListener mOnSendendListener) throws Exception{
		MsgBean msgSend = new MsgBean();
		msgSend.setAction(MsgActionCode.QUERY_FIN_TASK);
		String json_content = JsonUtils.buildQUERY_FIN_TASK(CloudApplication.user_name, pagenum);
		msgSend.setJson_content(json_content);
		CloudApplication.sequence_refresh += 2;
		msgSend.setSequence(CloudApplication.sequence_refresh);
		msgSend.setVersion(9527);
		CloudApplication.requestID += 2;
		SocketUtils.command_forwarding_req(msgSend, mOnSendendListener, CloudApplication.requestID);
	}
	
	class ParseRefreshFinVideoRunnable implements Runnable{
		TaskReturn taskRet;
		public ParseRefreshFinVideoRunnable(TaskReturn taskRet) {
			this.taskRet = taskRet;
		}
		
		@Override
		public void run() {
			parseRefreshFinVideo(taskRet);
		}
	}
	
	public synchronized void parseRefreshFinVideo(TaskReturn ret_query_fin_task){
		if(pagenum_downlaoded_refresh == 0){
			temp_groupList_downlaoded.clear();
		}
		ArrayList<VideoInfo> videos_query_fin_task = ret_query_fin_task.getvFiles();
		if(videos_query_fin_task == null || videos_query_fin_task.size() == 0){
			pagenum_downlaoded_refresh = 0;
		}else{
			pagenum_downlaoded_refresh ++;
		}
		/*替换*/
		for (int i = 0; i < videos_query_fin_task.size(); i++) {
			for (AppInfo appinfo : temp_groupList_downlaoded) {
				if(appinfo.getAppcode().equalsIgnoreCase(videos_query_fin_task.get(i).getAppid())){
					List<VideoInfo> infos = appinfo.getVideoInfoList();
					for (VideoInfo videoInfo : infos) {
						if(videoInfo.getTaskid().equalsIgnoreCase(videos_query_fin_task.get(i).getTaskid())){
							videoInfo.setShare_status(videos_query_fin_task.get(i).getShare_status());
							videos_query_fin_task.remove(i);//替换后立刻移除
							i--;
							break;
						}
					}
					break;
				}
			}
		}
		
		/*插入*/
		AppInfo app_downloaded = null;
		boolean isAdded;
		for (int i = 0; i < videos_query_fin_task.size(); i++) {
			if(temp_groupList_downlaoded.size() == 0){
				app_downloaded = new AppInfo();
				app_downloaded.setAppname(videos_query_fin_task.get(i).getAppname());
				app_downloaded.setAppcode(videos_query_fin_task.get(i).getAppid());
				AppInfo app_temp = SqliteUtils.getInstance(instance).queryAppByCode(app_downloaded.getAppcode());
				if(app_temp != null){
					app_downloaded.setIcon(app_temp.getIcon());
				}
				
				app_downloaded.getVideoInfoList().add(videos_query_fin_task.get(i));
				temp_groupList_downlaoded.add(app_downloaded);
				videos_query_fin_task.remove(i);//插入后立刻移除
				i--;
			}else{
				isAdded = false;
				for (AppInfo appinfo : temp_groupList_downlaoded) {
					if(appinfo.getAppcode().equalsIgnoreCase(videos_query_fin_task.get(i).getAppid())){
						appinfo.getVideoInfoList().add(videos_query_fin_task.get(i));
						videos_query_fin_task.remove(i);//插入后立刻移除
						i--;
						isAdded = true;
						break;
					}else{
						continue;
					}
				}
				if(!isAdded){
					app_downloaded = new AppInfo();
					app_downloaded.setAppname(videos_query_fin_task.get(i).getAppname());
					app_downloaded.setAppcode(videos_query_fin_task.get(i).getAppid());
					AppInfo app_temp = SqliteUtils.getInstance(instance).queryAppByCode(app_downloaded.getAppcode());
					if(app_temp != null){
						app_downloaded.setIcon(app_temp.getIcon());
					}
					app_downloaded.getVideoInfoList().add(videos_query_fin_task.get(i));
					temp_groupList_downlaoded.add(app_downloaded);
					videos_query_fin_task.remove(i);//插入后立刻移除
					i--;
				}
			}
		}
		
		if(pagenum_downlaoded_refresh == 0){
			if(!isEditting){
				Message msg = Message.obtain();
				msg.what = 6;
				msg.arg1 = ret_query_fin_task.getTasknum();
				mHandler.sendMessage(msg);
			}
		}else{
			mHandler.sendEmptyMessage(1);
		}
	}
	
	public void parsequeryFinVideo(TaskReturn ret_query_fin_task){
		ArrayList<VideoInfo> videos_query_fin_task = ret_query_fin_task.getvFiles();
		if(videos_query_fin_task.size() > 0){
			pagenum_downlaoded = ret_query_fin_task.getPagenum() + 1;
			mHandler.sendEmptyMessage(4);
		}else{
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			pagenum_downlaoded = 0;
		}
		/*替换*/
		for (int i = 0; i < videos_query_fin_task.size(); i++) {
			for (AppInfo appinfo : groupList_downlaoded) {
				if(appinfo.getAppcode().equalsIgnoreCase(videos_query_fin_task.get(i).getAppid())){
					List<VideoInfo> infos = appinfo.getVideoInfoList();
					for (VideoInfo videoInfo : infos) {
						if(videoInfo.getTaskid().equalsIgnoreCase(videos_query_fin_task.get(i).getTaskid())){
							videoInfo.setShare_status(videos_query_fin_task.get(i).getShare_status());
							videos_query_fin_task.remove(i);//替换后立刻移除
							i--;
							break;
						}
					}
					break;
				}
			}
		}
		
		/*插入*/
		AppInfo app_downloaded = null;
		boolean isAdded;
		for (int i = 0; i < videos_query_fin_task.size(); i++) {
			if(groupList_downlaoded.size() == 0){
				app_downloaded = new AppInfo();
				app_downloaded.setAppname(videos_query_fin_task.get(i).getAppname());
				app_downloaded.setAppcode(videos_query_fin_task.get(i).getAppid());
				AppInfo app_temp = SqliteUtils.getInstance(instance).queryAppByCode(app_downloaded.getAppcode());
				if(app_temp != null){
					app_downloaded.setIcon(app_temp.getIcon());
					//LogUtils.getLog(getClass()).verbose(app_temp.getIcon());
				}
				
				app_downloaded.getVideoInfoList().add(videos_query_fin_task.get(i));
				groupList_downlaoded.add(app_downloaded);
				videos_query_fin_task.remove(i);//插入后立刻移除
				i--;
			}else{
				isAdded = false;
				for (AppInfo appinfo : groupList_downlaoded) {
					if(appinfo.getAppcode().equalsIgnoreCase(videos_query_fin_task.get(i).getAppid())){
						appinfo.getVideoInfoList().add(videos_query_fin_task.get(i));
						videos_query_fin_task.remove(i);//插入后立刻移除
						i--;
						isAdded = true;
						break;
					}else{
						continue;
					}
				}
				if(!isAdded){
					app_downloaded = new AppInfo();
					app_downloaded.setAppname(videos_query_fin_task.get(i).getAppname());
					app_downloaded.setAppcode(videos_query_fin_task.get(i).getAppid());
					AppInfo app_temp = SqliteUtils.getInstance(instance).queryAppByCode(app_downloaded.getAppcode());
					if(app_temp != null){
						app_downloaded.setIcon(app_temp.getIcon());
						//LogUtils.getLog(getClass()).verbose(app_temp.getIcon());
					}
					app_downloaded.getVideoInfoList().add(videos_query_fin_task.get(i));
					groupList_downlaoded.add(app_downloaded);
					videos_query_fin_task.remove(i);//插入后立刻移除
					i--;
				}
			}
		}
	}
	
	
	
	public void delVideo() throws Exception{
		vFiles_temp.clear();
		for (int i = 0; i < groupList_downlaoding.size(); i++) {
			AppInfo group = groupList_downlaoding.get(i);
			if(group.getIsChecked() == 1){
				vFiles_temp.addAll(group.getVideoInfoList());
			}else{
				List<VideoInfo> tempVideoInfos = group.getVideoInfoList();
				for (int j = 0; j < tempVideoInfos.size(); j++) {
					VideoInfo vidoInfo = tempVideoInfos.get(j);
					if(vidoInfo.getChecked() == 1){
						vFiles_temp.add(vidoInfo);
					}
				}
			}
		}
		int size = vFiles_temp.size();
		if(size == 0){
			ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.no_file_selected));
			throw new Exception("no video");
		}
		//LogUtils.getLog(getClass()).error("size: " + size);
		for (int i = 0; i < size;) {
			int start = i;
			int end = start + 100;
			if(end >= size){
				end = size;
			}
			i = end;
			CloudApplication.requestID += 2;
			MsgBean msgSend = new MsgBean();
			msgSend.setAction(MsgActionCode.DEL_TASK);
			String json_content = JsonUtils.buildDEL_TASK(vFiles_temp.subList(start, end));
			msgSend.setJson_content(json_content);
			CloudApplication.sequence += 2;
			msgSend.setSequence(CloudApplication.sequence);
			msgSend.setVersion(9527);
			SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);
		}
		
	}
	
	public void parsedelVideo(ArrayList<TaskReturn> rets_del_task){
		int temp = 0;
		for (TaskReturn taskReturn : rets_del_task) {
			if(taskReturn.getRet() == MsgResponseCode.OK){
				for (int i = 0; i < groupList_downlaoding.size(); i++) {
					List<VideoInfo> vFiles = groupList_downlaoding.get(i).getVideoInfoList();
					for (int j = 0; j < vFiles.size(); j++) {
						if(taskReturn.getTaskid().equalsIgnoreCase(vFiles.get(j).getTaskid())){
							vFiles.remove(j);
							temp ++;
							if(vFiles == null || vFiles.size() == 0){
								groupList_downlaoding.remove(i);
								i--;
							}
							break;
						}
					}
				}
			}
		}
		num_downlaoding = num_downlaoding - temp;
		if(num_downlaoding < 0){
			num_downlaoding = 0;
		}
		tv_downloadingNum.setText(num_downlaoding + "");
		if(mFragments[0].isAdded()){
			mFragments[0].OnFragmentStatusChanged(isEditting);
		}
	}
	
	public void pauseVideo() throws Exception{
		vFiles_temp.clear();
		//LogUtils.getLog(getClass()).verbose("pauseVideo");
		for (int i = 0; i < groupList_downlaoding.size(); i++) {
			AppInfo group = groupList_downlaoding.get(i);
			if(group.getIsChecked() == 1){
				vFiles_temp.addAll(group.getVideoInfoList());
			}else{
				List<VideoInfo> tempVideoInfos = group.getVideoInfoList();
				for (int j = 0; j < tempVideoInfos.size(); j++) {
					VideoInfo vidoInfo = tempVideoInfos.get(j);
					if(vidoInfo.getChecked() == 1){
						vFiles_temp.add(vidoInfo);
					}
				}
			}
		}
		int size = vFiles_temp.size();
		if(size == 0){
			ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.no_file_selected));
			throw new Exception("no video");
		}
		
		//LogUtils.getLog(getClass()).error("size: " + size);
		for (int i = 0; i < size;) {
			int start = i;
			int end = start + 100;
			if(end >= size){
				end = size;
			}
			i = end;
			CloudApplication.requestID += 2;
			MsgBean msgSend = new MsgBean();
			msgSend.setAction(MsgActionCode.PAUSE_TASK);
			String json_content = JsonUtils.buildDEL_TASK(vFiles_temp.subList(start, end));
			msgSend.setJson_content(json_content);
			CloudApplication.sequence += 2;
			msgSend.setSequence(CloudApplication.sequence);
			msgSend.setVersion(9527);
			SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);
		}
		
	}
	
	public void parsepauseVideo(List<TaskReturn> rets_pause_task){
		for (TaskReturn taskReturn : rets_pause_task) {
			if(taskReturn.getRet() == MsgResponseCode.OK){
				for (AppInfo vGroup : groupList_downlaoding) {
					List<VideoInfo> vFiles = vGroup.getVideoInfoList();
					for (VideoInfo videoInfo : vFiles) {
						if(taskReturn.getTaskid().equalsIgnoreCase(videoInfo.getTaskid())){
							videoInfo.setStatus(3);//文件状态 0.下载完成, 1.正在下载, 2.等待下载, 3.暂停下载, 4.无资源,失败
							break;
						}
					}
				}
			}
		}
		if(mFragments[0].isAdded()){
			mFragments[0].OnFragmentStatusChanged(isEditting);
		}
	}
	
	public void resumeVideo() throws Exception{
		vFiles_temp.clear();
		//LogUtils.getLog(getClass()).verbose("resumeVideo");
		for (int i = 0; i < groupList_downlaoding.size(); i++) {
			AppInfo group = groupList_downlaoding.get(i);
			if(group.getIsChecked() == 1){
				vFiles_temp.addAll(group.getVideoInfoList());
			}else{
				List<VideoInfo> tempVideoInfos = group.getVideoInfoList();
				for (int j = 0; j < tempVideoInfos.size(); j++) {
					VideoInfo vidoInfo = tempVideoInfos.get(j);
					if(vidoInfo.getChecked() == 1){
						vFiles_temp.add(vidoInfo);
					}
				}
			}
		}
		int size = vFiles_temp.size();
		if(size == 0){
			ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.no_file_selected));
			throw new Exception("no video");
		}
		
		//LogUtils.getLog(getClass()).error("size: " + size);
		for (int i = 0; i < size;) {
			int start = i;
			int end = start + 100;
			if(end >= size){
				end = size;
			}
			i = end;
			CloudApplication.requestID += 2;
			MsgBean msgSend = new MsgBean();
			msgSend.setAction(MsgActionCode.RESUME_TASK);
			String json_content = JsonUtils.buildDEL_TASK(vFiles_temp.subList(start, end));
			msgSend.setJson_content(json_content);
			CloudApplication.sequence += 2;
			msgSend.setSequence(CloudApplication.sequence);
			msgSend.setVersion(9527);
			SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);
		}
		
	}
	
	public void parseresumeVideo(List<TaskReturn> rets_resume_task){
		for (TaskReturn taskReturn : rets_resume_task) {
			if(taskReturn.getRet() == MsgResponseCode.OK){
				for (AppInfo vGroup : groupList_downlaoding) {
					List<VideoInfo> vFiles = vGroup.getVideoInfoList();
					for (VideoInfo videoInfo : vFiles) {
						if(taskReturn.getTaskid().equalsIgnoreCase(videoInfo.getTaskid())){
							videoInfo.setStatus(1);//文件状态 0.下载完成, 1.正在下载, 2.等待下载, 3.暂停下载, 4.无资源,失败
							break;
						}
					}
				}
			}
		}
		if(mFragments[0].isAdded()){
			mFragments[0].OnFragmentStatusChanged(isEditting);
		}
	}
	
	///del_rsc取值：0为不删除源文件；1为删除源文件
	public void delfinVideo(int del_rsc) throws Exception{
		vFiles_temp.clear();
		//LogUtils.getLog(getClass()).verbose("delfinVideo");
		for (int i = 0; i < groupList_downlaoded.size(); i++) {
			AppInfo group = groupList_downlaoded.get(i);
			if(group.getIsChecked() == 1){
				vFiles_temp.addAll(group.getVideoInfoList());
			}else{
				List<VideoInfo> tempVideoInfos = group.getVideoInfoList();
				for (int j = 0; j < tempVideoInfos.size(); j++) {
					VideoInfo vidoInfo = tempVideoInfos.get(j);
					if(vidoInfo.getChecked() == 1){
						vFiles_temp.add(vidoInfo);
					}
				}
			}
		}
		int size = vFiles_temp.size();
		if(size == 0){
			ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.no_file_selected));
			throw new Exception("no video");
		}
		
		//LogUtils.getLog(getClass()).error("size: " + size);
		for (int i = 0; i < size;) {
			int start = i;
			int end = start + 100;
			if(end >= size){
				end = size;
			}
			i = end;
			CloudApplication.requestID += 2;
			MsgBean msgSend = new MsgBean();
			msgSend.setAction(MsgActionCode.DEL_FIN_TASK);
			String json_content = JsonUtils.buildDEL_FIN_TASK(del_rsc,vFiles_temp.subList(start, end));
			msgSend.setJson_content(json_content);
			CloudApplication.sequence += 2;
			msgSend.setSequence(CloudApplication.sequence);
			msgSend.setVersion(9527);
			SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);
		}
	}
	
	public void parsedelfinVideo(List<TaskReturn> taskRets){
		int temp = 0;
		for (TaskReturn taskReturn : taskRets) {
			if(taskReturn.getRet() == MsgResponseCode.OK){
				for (int i = 0; i < groupList_downlaoded.size(); i++) {
					List<VideoInfo> vFiles = groupList_downlaoded.get(i).getVideoInfoList();
					for (int j = 0; j < vFiles.size(); j++) {
						if(taskReturn.getTaskid().equalsIgnoreCase(vFiles.get(j).getTaskid())){
							vFiles.remove(j);
							temp ++;
							if(vFiles == null || vFiles.size() == 0){
								groupList_downlaoded.remove(i);
								i--;
							}
							break;
						}
					}
				}
			}
		}
		num_downlaoded = num_downlaoded - temp;
		if(num_downlaoded < 0){
			num_downlaoded = 0;
		}
		tv_downloadedNum.setText(num_downlaoded + "");
		if(mFragments[1].isAdded()){
			mFragments[1].OnFragmentStatusChanged(isEditting);
		}
	}
	
	/*重新下载*/
	public void redownloadVideo() throws Exception{
		//LogUtils.getLog(getClass()).verbose("redownloadVideo");
		vFiles_temp.clear();
		for (int i = 0; i < groupList_downlaoded.size(); i++) {
			AppInfo group = groupList_downlaoded.get(i);
			if(group.getIsChecked() == 1){
				vFiles_temp.addAll(group.getVideoInfoList());
			}else{
				List<VideoInfo> tempVideoInfos = group.getVideoInfoList();
				for (int j = 0; j < tempVideoInfos.size(); j++) {
					VideoInfo vidoInfo = tempVideoInfos.get(j);
					if(vidoInfo.getChecked() == 1){
						vFiles_temp.add(vidoInfo);
					}
				}
			}
		}
		int size = vFiles_temp.size();
		if(size == 0){
			ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.no_file_selected));
			throw new Exception("no video");
		}
		
		//LogUtils.getLog(getClass()).error("size: " + size);
		for (int i = 0; i < size;) {
			int start = i;
			int end = start + 100;
			if(end >= size){
				end = size;
			}
			i = end;
			CloudApplication.requestID += 2;
			MsgBean msgSend = new MsgBean();
			msgSend.setAction(MsgActionCode.REDOWNLOAD_TASK);
			String json_content = JsonUtils.buildReDownload_TASK(vFiles_temp.subList(start, end));
			msgSend.setJson_content(json_content);
			CloudApplication.sequence += 2;
			msgSend.setSequence(CloudApplication.sequence);
			msgSend.setVersion(9527);
			SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);
		}
	}
	
	public void parseredownloadVideo(ArrayList<String> taskIds){
		int temp = 0;
		for (String taskId : taskIds) {
			for (int i = 0; i < groupList_downlaoded.size(); i++) {
				List<VideoInfo> vFiles = groupList_downlaoded.get(i).getVideoInfoList();
				for (int j = 0; j < vFiles.size(); j++) {
					if(taskId.equalsIgnoreCase(vFiles.get(j).getTaskid())){
						vFiles.remove(j);
						temp ++;
						if(vFiles == null || vFiles.size() == 0){
							groupList_downlaoded.remove(i);
							i--;
						}
						break;
					}
				}
			}
		}
		num_downlaoded = num_downlaoded - temp;
		if(num_downlaoded < 0){
			num_downlaoded = 0;
		}
		tv_downloadedNum.setText(num_downlaoded + "");
		if(mFragments[1].isAdded()){
			mFragments[1].OnFragmentStatusChanged(isEditting);
		}
	}
	
	
	public class RefreshTask extends TimerTask {
		public RefreshTask() {
		}

		@Override
		public void run() {
			mHandler.sendEmptyMessage(0);
			mHandler.sendEmptyMessage(1);
		}
	}
	
	private void startTimer() {
		if (mTimer == null) {
			mTimer = new Timer();
			RefreshTask refreshTask = new RefreshTask();
			mTimer.schedule(refreshTask, 30000L, 30000L);
		}
	}
	
	private void stopTimer() {
		if(mTimer != null){
			mTimer.purge();
			mTimer.cancel();
			mTimer = null;
		}
	}
}
