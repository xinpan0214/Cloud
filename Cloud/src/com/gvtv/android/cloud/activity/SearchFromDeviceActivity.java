package com.gvtv.android.cloud.activity;

import java.util.ArrayList;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.adapter.SearchfromdeviceAdapter;
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

public class SearchFromDeviceActivity extends BaseActivity implements OnClickListener{

	private TitleView mTitleView;
	private EditText et_searchkey;
	private ImageView iv_delete,iv_search;
	private XListView lv;
	private ArrayList<VideoInfo> deviceVideoInfos = new ArrayList<VideoInfo>();
	private SearchfromdeviceAdapter adapter;
	private TextView tv_no_result_tip;
	
	private SearchFromDeviceActivity instance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_from_device);
		findViews();
		setListeners();
		instance = this;
		CloudApplication.getInstance().addActivity(this);
	}
	
	private void findViews(){
		mTitleView = (TitleView) findViewById(R.id.searchTitle);
		et_searchkey = (EditText) findViewById(R.id.et_searchkey);
		iv_delete = (ImageView) findViewById(R.id.iv_delete);
		iv_search = (ImageView) findViewById(R.id.iv_search);
		lv = (XListView) findViewById(R.id.lv_search_result);
		tv_no_result_tip = (TextView) findViewById(R.id.tv_no_result_tip);
		tv_no_result_tip.setVisibility(View.GONE);
		lv.setPullLoadEnable(false);
		lv.setPullRefreshEnable(false);
		adapter = new SearchfromdeviceAdapter(getApplicationContext(), deviceVideoInfos);
		lv.setAdapter(adapter);
	}
	
	private void setListeners(){
		mTitleView.getBackTextView().setOnClickListener(this);
		iv_delete.setOnClickListener(this);
		iv_search.setOnClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		try {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(mTitleView.getBackTextView() == v){
			finish();
		}else if(iv_delete == v){
			et_searchkey.setText("");
			LogUtils.getLog(getClass()).verbose("iv_delete is clicked============");
		}else if(iv_search == v){
			String key = et_searchkey.getText().toString();
			if(key.equals("")){
				ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.input_key_word));
				return;
			}
			deviceVideoInfos.clear();
			adapter.notifyDataSetChanged();
			try {
				AlertDialogUtil.getAlertDialogUtil().showDialog(instance,
						instance.getResources().getString(R.string.loading_please_wait));
				AlertDialogUtil.getAlertDialogUtil().getDialog().setOnCancelListener(new OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						if(deviceVideoInfos.size() == 0){
							tv_no_result_tip.setVisibility(View.VISIBLE);
						}else{
							tv_no_result_tip.setVisibility(View.GONE);
						}
					}
				});
				MsgBean msgSend = new MsgBean();
				msgSend.setAction(MsgActionCode.QUERY_FILE_LIKE);
				msgSend.setJson_content(JsonUtils.buildQUERY_FILE_LIKE(CloudApplication.user_name, key));
				CloudApplication.requestID += 2;
				SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);
			} catch (Exception e) {
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
				LogUtils.getLog(getClass()).error(e.toString());
			}
		}
	}
	
	@Override
	protected void onResume() {
		MessageReceiver.msghList.add(instance);
		super.onResume();
	}

	@Override
	protected void onPause() {
		for (int i = 0; i < MessageReceiver.msghList.size(); i++) {
			if(MessageReceiver.msghList.get(i) instanceof SearchFromDeviceActivity){
				MessageReceiver.msghList.remove(i);
				i--;
			}
		}
		super.onPause();
	}
	
	@Override
	public void onMessage(int type, int resp, MsgBean msg, byte[] restoreByte) {
		super.onMessage(type, resp, msg, restoreByte);
		switch (type) {	
		case MsgTypeCode.COMMAND_FORWARDING_REQ:
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			if(resp == MsgResponseCode.OK){
				String jsonStr = msg.getJson_content();
				if(jsonStr != null){
					TaskReturn taskRet = JsonUtils.parseQUERY_FILE_LIKE(jsonStr);
					if(taskRet.getRet() == MsgResponseCode.OK){
						deviceVideoInfos.clear();
						deviceVideoInfos.addAll(taskRet.getvFiles());
						adapter.notifyDataSetChanged();
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
