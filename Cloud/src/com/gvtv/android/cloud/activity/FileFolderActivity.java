package com.gvtv.android.cloud.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.gvtv.android.cloud.AppConst;
import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.adapter.FileFolderAdapter;
import com.gvtv.android.cloud.bean.AppInfo;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.bean.TaskReturn;
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

public class FileFolderActivity extends BaseActivity implements OnClickListener 
,IXListViewListener, OnItemClickListener{

	private TitleView title;
	private XListView lv;
	
	private FileFolderActivity instance;
	private TextView tv_nofile;
	
	private List<AppInfo> mAppGroupInfos = new ArrayList<AppInfo>();
	private FileFolderAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_folder);
		instance = this;
		findViews();
		setListeners();
		CloudApplication.getInstance().addActivity(instance);
		AlertDialogUtil.getAlertDialogUtil().showDialog(instance,
				instance.getResources().getString(R.string.loading_please_wait));
	}
	
	private void findViews(){
		title = (TitleView) findViewById(R.id.downloadfiletitle);
		lv = (XListView) findViewById(R.id.xlv);
		lv.setPullLoadEnable(false);
		lv.setPullRefreshEnable(true);
		adapter = new FileFolderAdapter(FileFolderActivity.this, mAppGroupInfos);
		lv.setAdapter(adapter);
		tv_nofile = (TextView) findViewById(R.id.tv_nofile);
	}

	private void setListeners(){
		title.getBackTextView().setOnClickListener(this);
		lv.setXListViewListener(this);
		lv.setOnItemClickListener(this);
	}
	@Override
	public void onClick(View v) {
		if(title.getBackTextView() == v){
			finish();
		}
	}

	@Override
	public void onRefresh() {
		lv.stopRefresh();
		try {
			if(mAppGroupInfos.size() == 0){
				AlertDialogUtil.getAlertDialogUtil().showDialog(instance,
						instance.getResources().getString(R.string.loading_please_wait));
			}
			MsgBean msgSend = new MsgBean();
			String  json_content = JsonUtils.buildQUERY_APPS(CloudApplication.user_name);
			msgSend.setJson_content(json_content);
			msgSend.setAction(MsgActionCode.QUERY_APPS);
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

	@Override
	public void onLoadMore() {
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(FileFolderActivity.this, FileActivity.class);
		//position = 0成为了xlistview的头，因此item从1开始
		if(position == 0){
			return;
		}
		if(mAppGroupInfos.size() >= position){
			intent.putExtra(AppConst.FILEFOLD_TO_FILE, mAppGroupInfos.get(position -1));
			startActivity(intent);
		}
	}

	@Override
	protected void onPause() {
		for (int i = 0; i < MessageReceiver.msghList.size(); i++) {
			if(MessageReceiver.msghList.get(i) instanceof FileFolderActivity){
				MessageReceiver.msghList.remove(i);
				i--;
			}
		}
		super.onPause();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		MessageReceiver.msghList.add(instance);
		try {
			
			MsgBean msgSend = new MsgBean();
			String  json_content = JsonUtils.buildQUERY_APPS(CloudApplication.user_name);
			msgSend.setJson_content(json_content);
			msgSend.setAction(MsgActionCode.QUERY_APPS);
			CloudApplication.sequence += 2;
			msgSend.setSequence(CloudApplication.sequence);
			msgSend.setVersion(9527);
			CloudApplication.requestID += 2;
			SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);
		} catch (Exception e) {
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			LogUtils.getLog(getClass()).error(e.toString());
		}
		super.onResume();
	}

	@Override
	public void onMessage(int type, int resp, MsgBean msg, byte[] restoreByte) {
		super.onMessage(type, resp, msg, restoreByte);
		switch (type) {
		case MsgTypeCode.COMMAND_FORWARDING_REQ:
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			lv.stopRefresh();
			if(resp == MsgResponseCode.OK){
				if(msg.getRet() == MsgResponseCode.OK){
					String jsonStr = msg.getJson_content();
					TaskReturn taskTet = JsonUtils.parseQUERY_APPS(jsonStr);
					mAppGroupInfos.clear();
					mAppGroupInfos.addAll(taskTet.getvGroup());
					adapter.notifyDataSetChanged();
					if(mAppGroupInfos.size() == 0){
						tv_nofile.setVisibility(View.VISIBLE);
					}else{
						tv_nofile.setVisibility(View.INVISIBLE);
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
}



