package com.gvtv.android.cloud.activity;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.android.common.dataprovider.HttpCallBack;
import com.android.common.dataprovider.packet.outpacket.OutPacket;
import com.gvtv.android.cloud.AppConst;
import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.bean.AppInfo;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.bean.TaskReturn;
import com.gvtv.android.cloud.bean.VideoInfo;
import com.gvtv.android.cloud.broadcast.MessageReceiver;
import com.gvtv.android.cloud.db.SqliteUtils;
import com.gvtv.android.cloud.http.HttpConstants;
import com.gvtv.android.cloud.http.HttpRequestUtils;
import com.gvtv.android.cloud.http.HttpUrlUtils;
import com.gvtv.android.cloud.http.XmlPullParserUtils;
import com.gvtv.android.cloud.msg.JsonUtils;
import com.gvtv.android.cloud.msg.MsgActionCode;
import com.gvtv.android.cloud.msg.MsgResponseCode;
import com.gvtv.android.cloud.msg.MsgTypeCode;
import com.gvtv.android.cloud.sockets.SocketUtils;
import com.gvtv.android.cloud.util.AlertDialogUtil;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.ToastUtil;
import com.gvtv.android.cloud.view.TitleView;

@SuppressLint("JavascriptInterface")
public class APPActivity extends BaseActivity implements HttpCallBack {

	private ArrayList<VideoInfo> mVideoInfos = new ArrayList<VideoInfo>();
	private AppInfo app;
	private WebView wv;
	private APPActivity instance;
	private TitleView mTitleView;
	private String app_host;
	private String url;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app);
		instance = this;
		findViews();
		app = getIntent().getParcelableExtra(AppConst.CLOUDMANAGER_TO_APP);
		CloudApplication.getInstance().addActivity(instance);
		mTitleView.getTitleTextView().setText(getResources().getString(R.string.myapp));
		mTitleView.setViewBackgroundColor(getResources().getColor(R.color.app_store_title_color));
		mTitleView.getBackTextView().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				instance.finish();
			}
		});
		AlertDialogUtil.getAlertDialogUtil().showDialogWithoutCancelListener(instance,getResources().getString(R.string.loading_please_wait));
		HttpRequestUtils.requestSingleUpdateApp(instance, instance, app);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && wv.canGoBack()){
			wv.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void findViews(){
		wv=(WebView) findViewById(R.id.wv);
		wv.getSettings().setDefaultTextEncodingName("UTF-8");
		wv.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		wv.getSettings().setJavaScriptEnabled(true);
		mTitleView = (TitleView) findViewById(R.id.appheader);
	}
	private void setListeners(){
		wv.setWebViewClient(new AppWebViewClient());
		wv.addJavascriptInterface(new AppJavaScriptInterface(), "demo");
	}
	
	private void removeListeners(){
		wv.setWebViewClient(null);
		wv.removeJavascriptInterface("demo");
	}
	
	final class AppJavaScriptInterface {  
		AppJavaScriptInterface() {  
        }  
  
        public void downloadAndroidCallback(String json) {
           // LogUtils.getLog(getClass()).verbose(json);
            //{"videoName":"上错花轿嫁错郎","colls":[{"count":1,"size":30,"url":"gvppp://PR/Ffxb1ZtpPQtA4SzPgqQ=="}]}
		if(json == null || json.equals("")){
			ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.no_task));
			return;
		}
        try {
			AlertDialogUtil.getAlertDialogUtil().showDialog(instance,
					instance.getResources().getString(R.string.add_task_please_wait));
			mVideoInfos.clear();
			mVideoInfos.addAll(JsonUtils.parseAppJson(json, APPActivity.this));
			MsgBean msgSend = new MsgBean();
			msgSend.setAction(MsgActionCode.ADD_TASK);
			msgSend.setJson_content(JsonUtils.buildADD_TASK(CloudApplication.user_name, app.getAppcode(), app.getAppname(), mVideoInfos));
			CloudApplication.requestID += 2;
			SocketUtils.command_forwarding_req(msgSend, instance, CloudApplication.requestID);
		} catch (Exception e) {
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			LogUtils.getLog(getClass()).error(e.toString());
		}
        }  
    }  
	
	/**
	 * 
	 * Copyright © 2014GreatVision. All rights reserved.
	 *
	 * @Title: APPActivity.java
	 * @Prject: MyCloud
	 * @Package: com.gvtv.android.cloud.activity
	 * @Description: TODO
	 * @author: yh
	 * @date: 2014-7-11 下午4:33:40
	 * @version: V1.0
	 */
	final class AppWebViewClient extends WebViewClient{


		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
		}
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
		}
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			LogUtils.getLog(getClass()).verbose("redirect url: " + url);
			//拦截url.如果url末尾是{},你就认为是点击下载
			if(url.endsWith("{}")){
				wv.loadUrl("javascript:downloadAndroid()");
				return true;
			}else{
				instance.url = url;
				AlertDialogUtil.getAlertDialogUtil().showDialogWithoutCancelListener(instance,getResources().getString(R.string.loading_please_wait));
			}
			return super.shouldOverrideUrlLoading(view, url);
		}
		
	}
	
	@Override
	protected void onResume() {
		MessageReceiver.msghList.add(instance);
		setListeners();
		super.onResume();
	}

	@Override
	protected void onPause() {
		removeListeners();
		for (int i = 0; i < MessageReceiver.msghList.size(); i++) {
			if(MessageReceiver.msghList.get(i) instanceof APPActivity){
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
				if(jsonStr != null && msg.getAction() == MsgActionCode.ADD_TASK){
					LogUtils.getLog(getClass()).verbose("ADD_TASK——----rep: " + jsonStr);
					ArrayList<TaskReturn> taskRets = JsonUtils.parseADD_TASK(jsonStr);
					int temp = 0;
					for (TaskReturn taskReturn : taskRets) {
//						200：成功，返回的appid为当前任务所在appid
//				         602：添加下载任务失败，原因不明
//				         603：添加下载任务失败,已经添加了该任务，重复添加，控制端可以理解为添加成功，忽略
//				         613: 添加下载任务失败，已经下载完该任务
//				         609：磁盘已满
						
						if(taskReturn.getRet() == MsgResponseCode.OK
								|| taskReturn.getRet() == MsgResponseCode.ADD_DOWNLOAD_FAILED_BUT_EXIST){
							temp ++;
						}
					}
					if(temp > 0){
						ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.add_success));
					}else{
						ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.add_fail));
					}
				}
			}else{
				ToastUtil.getToastUtils().showToast(instance, getResources().getString(R.string.add_fail));
			}
			break;
		default:
			break;
		}
	}


	@Override
	public void onCancel(OutPacket out, int token) {
		if(token == HttpConstants.ACTION_UPDATE_TOKEN){
			HttpRequestUtils.requestAppUrl(instance, instance, app.getDownloadurl());
		}else if(token ==  HttpConstants.ACTION_APPURL){
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
		}
	}

	
	@Override
	public void onNetError(int responseCode, String errorDesc, OutPacket out, int token) {
		if(token == HttpConstants.ACTION_UPDATE_TOKEN){
			HttpRequestUtils.requestAppUrl(instance, instance, app.getDownloadurl());
		}else if(token ==  HttpConstants.ACTION_APPURL){
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
		}
	}

	@Override
	public void onSuccessful(ByteBuffer buffer, int bufLen, int token) {		
		if(token == HttpConstants.ACTION_UPDATE_TOKEN){
			ArrayList<AppInfo> apps = XmlPullParserUtils.parseToAppInfo(buffer);
			if(apps != null && apps.size() > 0){
				app = apps.get(0);
				SqliteUtils.getInstance(instance).upgradeApp(app, CloudApplication.user_name);
				HttpRequestUtils.requestAppUrl(instance, instance, app.getDownloadurl());
			}else{
				HttpRequestUtils.requestAppUrl(instance, instance, app.getDownloadurl());
			}
		}else if(token ==  HttpConstants.ACTION_APPURL){
			try {
				app_host = new String(buffer.array(), "UTF-8").trim();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if(app_host != null){
				LogUtils.getLog(getClass()).verbose("app_host: " + app_host);
				instance.url = HttpUrlUtils.getAPPURL(app_host,app.getAppcode(), HttpConstants.LANG, HttpConstants.TERMINAL, HttpConstants.OSTYPE);
				wv.loadUrl(instance.url);
			}else{
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			}
		}
		
	}
	
	
	@Override
	public void onNetChange(boolean isNetConnected) {
		if(isNetConnected){
			if(url == null){
				HttpRequestUtils.requestAppUrl(instance, instance, app.getDownloadurl());
			}else{
				wv.loadUrl(url);
			}
		}
		super.onNetChange(isNetConnected);
	}
	
}
