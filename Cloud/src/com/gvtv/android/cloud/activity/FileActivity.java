package com.gvtv.android.cloud.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gvtv.android.cloud.AppConst;
import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.adapter.FileAdapter;
import com.gvtv.android.cloud.bean.AppInfo;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.bean.TaskReturn;
import com.gvtv.android.cloud.bean.VideoInfo;
import com.gvtv.android.cloud.broadcast.MessageReceiver;
import com.gvtv.android.cloud.msg.JsonUtils;
import com.gvtv.android.cloud.msg.MsgActionCode;
import com.gvtv.android.cloud.msg.MsgResponseCode;
import com.gvtv.android.cloud.msg.MsgTypeCode;
import com.gvtv.android.cloud.sockets.SocketUtils;
import com.gvtv.android.cloud.util.AlertDialogUtil;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.ToastUtil;
import com.gvtv.android.cloud.view.TitleView;
import com.gvtv.android.cloud.view.xlistview.XListView;
import com.gvtv.android.cloud.view.xlistview.XListView.IXListViewListener;

public class FileActivity extends BaseActivity implements OnClickListener, IXListViewListener {

	private TitleView title;
	private XListView lv;
	private RelativeLayout rlyt_edit;
	private TextView tv_nofile;
	//private TextView tv_name, tv_time;
	private TextView tv_select_all, tv_del;
	//private View line_light, line_left;
	private int btn_status;//编辑按钮状态0为空闲，1为编辑状态
	//private int pointX, pointY, endX;  
	//private int position;
	private int pagenum;
	private List<VideoInfo> mVideoInfos = new ArrayList<VideoInfo>();
	private FileAdapter adapter;
	private AppInfo app;
	
	private boolean isAllChecked;
	
	private ArrayList<VideoInfo> vFiles = new ArrayList<VideoInfo>(); //删除集合
	
	private FileActivity instance;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file);
		instance = this;
		app = getIntent().getParcelableExtra(AppConst.FILEFOLD_TO_FILE);
		adapter = new FileAdapter(instance, mVideoInfos);
		findViews();
		setListeners();
		pagenum = 0;
		CloudApplication.getInstance().addActivity(instance);
		MessageReceiver.msghList.add(instance);
		AlertDialogUtil.getAlertDialogUtil().showDialog(instance,
				instance.getResources().getString(R.string.loading_please_wait));
		queryFile();
	}
	
	private void queryFile(){
		try {
			MsgBean msgSend = new MsgBean();
			String  json_content = JsonUtils.buildQUERY_FILE(CloudApplication.user_name, pagenum, app.getAppcode());
			msgSend.setJson_content(json_content);
			msgSend.setAction(MsgActionCode.QUERY_FILE);
			CloudApplication.sequence += 2;
			msgSend.setSequence(CloudApplication.sequence);
			msgSend.setVersion(9527);
			CloudApplication.requestID += 2;
			SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);
		} catch (Exception e) {
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			LogUtils.getLog(getClass()).error(e.toString());
		}
	}
	
	private void findViews(){
		title = (TitleView) findViewById(R.id.downloadfiletitle);
		title.getTitleTextView().setText(app.getAppname());
		title.getRightButton().setText(getResources().getString(R.string.edit));
		lv = (XListView) findViewById(R.id.xlv);
		lv.setPullLoadEnable(false);
		lv.setPullRefreshEnable(true);
		rlyt_edit = (RelativeLayout) findViewById(R.id.rlyt_edit);
		rlyt_edit.setVisibility(View.GONE);
		//tv_name = (TextView) findViewById(R.id.tv_name);
		//tv_time = (TextView) findViewById(R.id.tv_time);
		tv_select_all = (TextView) findViewById(R.id.tv_select_all);
		tv_del = (TextView) findViewById(R.id.tv_del);
		//line_left = findViewById(R.id.line_left);
		//line_light = findViewById(R.id.line_right);
		lv.setAdapter(adapter);
		tv_nofile = (TextView) findViewById(R.id.tv_nofile);
	}

	private void setListeners(){
		title.getBackTextView().setOnClickListener(this);
		title.getRightButton().setOnClickListener(this);
		tv_select_all.setOnClickListener(this);
		tv_del.setOnClickListener(this);
		lv.setXListViewListener(this);
	}
	@Override
	public void onClick(View v) {
		if(title.getBackTextView() == v){
			finish();
		}else if(v == title.getRightButton() && mVideoInfos.size() > 0){
			if(btn_status == 0){
				btn_status = 1;
				rlyt_edit.setVisibility(View.VISIBLE);
				for (VideoInfo mVideoInfo : mVideoInfos) {
					mVideoInfo.setIsVisible(1);
				}
				adapter.notifyDataSetChanged();
				title.getRightButton().setText(getResources().getString(R.string.complete));
			}else{
				btn_status = 0;
				rlyt_edit.setVisibility(View.GONE);
				for (VideoInfo mVideoInfo : mVideoInfos) {
					mVideoInfo.setIsVisible(0);
					if(mVideoInfo.getChecked() == 1){
						mVideoInfo.setChecked(0);
						vFiles.add(mVideoInfo);
					}
				}
				if(vFiles.size() > 0){
					delTask();
				}else{
					ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.no_file_selected));
				}
				adapter.notifyDataSetChanged();
				title.getRightButton().setText(getResources().getString(R.string.edit));
			}
		}else if(v == tv_select_all){
			isAllChecked = !isAllChecked;
			if(isAllChecked){
				for (VideoInfo mVideoInfo : mVideoInfos) {
					mVideoInfo.setChecked(1);
				}
			}else{
				for (VideoInfo mVideoInfo : mVideoInfos) {
					mVideoInfo.setChecked(0);
				}
			}
			adapter.notifyDataSetChanged();
		}else if(v == tv_del){
			
		}
	}
	
	@Override
	protected void onDestroy() {
		for (int i = 0; i < MessageReceiver.msghList.size(); i++) {
			if(MessageReceiver.msghList.get(i) instanceof FileActivity){
				MessageReceiver.msghList.remove(i);
				i--;
			}
		}
		super.onDestroy();
	}

	@Override
	public void onMessage(int type, int resp, MsgBean msg, byte[] restoreByte) {
		super.onMessage(type, resp, msg, restoreByte);
		switch (type) {
		case MsgTypeCode.COMMAND_FORWARDING_REQ:
			if(resp == MsgResponseCode.OK){
				if(msg.getAction() == MsgActionCode.REMOVE_FILE){
					AlertDialogUtil.getAlertDialogUtil().cancelDialog();
					String jsonStr = msg.getJson_content();
					LogUtils.getLog(getClass()).verbose(jsonStr);
					Map<String, Integer> rets = JsonUtils.parseREMOVE_FILE(jsonStr);
					Set<Map.Entry<String, Integer>> set = rets.entrySet();
			        for (Iterator<Map.Entry<String, Integer>> it = set.iterator(); it.hasNext();) {
			            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
			            if(entry.getValue() == MsgResponseCode.OK){
			            	for (int i = 0; i < mVideoInfos.size(); i++) {
								if(mVideoInfos.get(i).getFileid().equalsIgnoreCase(entry.getKey())){
									mVideoInfos.remove(i);
									break;
								}
							}
			            }
			        }
				}else if(msg.getAction() == MsgActionCode.QUERY_FILE){
					String jsonStr = msg.getJson_content();
					TaskReturn taskRet = JsonUtils.parseQUERY_FILE(jsonStr);
					if(taskRet.getRet() == MsgResponseCode.OK){
						mVideoInfos.addAll(taskRet.getvFiles());
						adapter.notifyDataSetChanged();
						if(taskRet.getvFiles().size() > 0){
							pagenum ++;
							queryFile();
						}else{
							AlertDialogUtil.getAlertDialogUtil().cancelDialog();
						}
					}else{
						AlertDialogUtil.getAlertDialogUtil().cancelDialog();
					}
				}
			}else{
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
				ToastUtil.getToastUtils().showToastByCode(instance, resp);
			}
			if(mVideoInfos.size() == 0){
				tv_nofile.setVisibility(View.VISIBLE);
			}else{
				tv_nofile.setVisibility(View.INVISIBLE);
			}
			adapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
	}

	
	public void delTask(){
		try {
			MsgBean msgSend = new MsgBean();
			int size = vFiles.size();
			for (int i = 0; i < size;) {
				int start = i;
				int end = start + 100;
				if(end >= size){
					end = size;
				}
				i = end;
				String  json_content = JsonUtils.buildREMOVE_FILE(vFiles.subList(start, end));
				msgSend.setJson_content(json_content);
				msgSend.setAction(MsgActionCode.REMOVE_FILE);
				CloudApplication.sequence += 2;
				msgSend.setSequence(CloudApplication.sequence);
				msgSend.setVersion(9527);
				CloudApplication.requestID += 2;
				SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);		
			}
		} catch (Exception e) {
			LogUtils.getLog(getClass()).error(e.toString());
		}
	}
	
	public void delATask(int position){
		try {
			MsgBean msgSend = new MsgBean();
			vFiles.clear();
			vFiles.add(mVideoInfos.get(position));
			String  json_content = JsonUtils.buildREMOVE_FILE(vFiles);
			msgSend.setJson_content(json_content);
			msgSend.setAction(MsgActionCode.REMOVE_FILE);
			CloudApplication.sequence += 2;
			msgSend.setSequence(CloudApplication.sequence);
			msgSend.setVersion(9527);
			CloudApplication.requestID += 2;
			SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);
		} catch (Exception e) {
			LogUtils.getLog(getClass()).error(e.toString());
		}
	}

	@Override
	public void onRefresh() {
		pagenum = 0;
		mVideoInfos.clear();
		adapter.notifyDataSetChanged();
		AlertDialogUtil.getAlertDialogUtil().showDialog(instance,
				instance.getResources().getString(R.string.loading_please_wait));
		queryFile();
		lv.stopRefresh();
	}

	@Override
	public void onLoadMore() {
		AlertDialogUtil.getAlertDialogUtil().showDialog(instance,
				instance.getResources().getString(R.string.loading_please_wait));
		queryFile();
		lv.stopLoadMore();
	}

}



