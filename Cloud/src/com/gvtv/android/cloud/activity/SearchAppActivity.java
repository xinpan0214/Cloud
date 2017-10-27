package com.gvtv.android.cloud.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.android.common.dataprovider.HttpCallBack;
import com.android.common.dataprovider.packet.outpacket.OutPacket;
import com.gvtv.android.cloud.AppConst;
import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.adapter.HotAppAdapter;
import com.gvtv.android.cloud.bean.AppInfo;
import com.gvtv.android.cloud.http.HttpRequestUtils;
import com.gvtv.android.cloud.http.XmlPullParserUtils;
import com.gvtv.android.cloud.util.AlertDialogUtil;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.ToastUtil;
import com.gvtv.android.cloud.view.ViewAddMore;

public class SearchAppActivity extends BaseActivity implements OnClickListener, OnItemClickListener, HttpCallBack{

	private Button btn_back, btn_cancel;
	private ImageView iv_search;
	private EditText et_search;
	private GridView gv;
	private ArrayList<AppInfo> apps = new ArrayList<AppInfo>();
	private HotAppAdapter mHotAppAdapter;
	private TextView tv_noapp;
	private int pagenum;
	private int size;
	private ViewAddMore mViewAddMore;
	private String keyword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchapp);
		findViews();
		setListeners();
		CloudApplication.getInstance().addActivity(this);
		size = 20;
		pagenum = 1;
	}
	
	private void findViews(){
		mViewAddMore = (ViewAddMore) findViewById(R.id.flyt_loading);
		mViewAddMore.showText();
		mViewAddMore.setVisibility(View.INVISIBLE);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		iv_search = (ImageView) findViewById(R.id.iv_search);
		et_search = (EditText) findViewById(R.id.et_searchkey);
		gv = (GridView) findViewById(R.id.gv_app_search);
		tv_noapp = (TextView) findViewById(R.id.tv_noapp);
		tv_noapp.setVisibility(View.INVISIBLE);
	}
	
	private void setListeners(){
		mViewAddMore.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		iv_search.setOnClickListener(this);
		gv.setOnItemClickListener(this);
	}

	
	@Override
	public void onClick(View v) {
		try {
			((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(v == btn_back){
			finish();
		}else if(v == btn_cancel){
			et_search.setText("");
		}else if(v == iv_search){
			keyword = et_search.getText().toString();
			if(keyword.equals("")){
				ToastUtil.getToastUtils().showToast(getApplicationContext(), getResources().getString(R.string.please_input_keyword));
				return;
			}
			AlertDialogUtil.getAlertDialogUtil().showDialogWithoutCancelListener(this, getResources().getString(R.string.loading_please_wait));
			try {
				keyword = URLEncoder.encode(keyword, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			pagenum = 1;
			HttpRequestUtils.requestSearchApp(this, this, keyword, pagenum, size);
			apps.clear();
			refreshGv();
		}else if(v == mViewAddMore){
			if(!mViewAddMore.isLoading()){
				mViewAddMore.showProgressBar();
				HttpRequestUtils.requestSearchApp(this, this, keyword, pagenum, size);
			}
		}
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(SearchAppActivity.this, APPDetailActivity.class);
		intent.putExtra(AppConst.APPSTORE_TO_APPDETAIL, apps.get(position));
		startActivity(intent);
	}

	
	@Override
	public void onCancel(OutPacket out, int token) {
		AlertDialogUtil.getAlertDialogUtil().cancelDialog();
	}

	
	@Override
	public void onSuccessful(ByteBuffer buffer, int bufLen, int token) {
		mViewAddMore.showText();
		LogUtils.getLog(getClass()).verbose("task token: " + token + " is onSuccessful");
		//LogUtils.getLog(getClass()).verbose(new String(buffer.array()));
		ArrayList<AppInfo> apps_temp = XmlPullParserUtils.parseToAppInfo(buffer);
		if(apps_temp.size() > 0){
			if(pagenum == apps_temp.get(0).getCurrentpage()){
				pagenum ++;
				apps.addAll(apps_temp);
			}
			refreshGv();
		}
		AlertDialogUtil.getAlertDialogUtil().cancelDialog();
	}

	@Override
	public void onNetError(int responseCode, String errorDesc, OutPacket out, int token) {
		AlertDialogUtil.getAlertDialogUtil().cancelDialog();
	}
	
	private void refreshGv(){
		if(apps.size() == 0){
			mViewAddMore.setVisibility(View.INVISIBLE);
			tv_noapp.setVisibility(View.VISIBLE);
		}else{
			tv_noapp.setVisibility(View.GONE);
			if(apps.size() < apps.get(0).getTotal()){
				mViewAddMore.setVisibility(View.VISIBLE);
			}else{
				mViewAddMore.setVisibility(View.INVISIBLE);
			}
		}
		if(mHotAppAdapter == null){
			if(apps.size() > 0){
				mHotAppAdapter = new HotAppAdapter(getApplicationContext(), apps);
				gv.setAdapter(mHotAppAdapter);
				setGridViewHeight(gv);
			}
		}else{
			mHotAppAdapter.notifyDataSetChanged();
			setGridViewHeight(gv);
		}
	}
	
	private void setGridViewHeight(GridView gridView) {
		ListAdapter gridAdapter = gridView.getAdapter();
		if (gridAdapter == null) {
			return;
		}
		int count = gridAdapter.getCount();
		if(count > 0){
			View listItem = gridAdapter.getView(0, null, gridView);
			listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
			int rows;
			if (apps.size() % 3 == 0) {
				rows = apps.size() / 3;
			} else {
				rows = apps.size() / 3 + 1;
			}
			ViewGroup.LayoutParams paramsG = gridView.getLayoutParams();
			paramsG.height = (int) (listItem.getMeasuredHeight() * rows
					+ getResources().getDisplayMetrics().density * 12 * (rows - 1));
			gridView.setLayoutParams(paramsG);
		}else{
			ViewGroup.LayoutParams paramsG = gridView.getLayoutParams();
			paramsG.height = 0;
			gridView.setLayoutParams(paramsG);
		}
	}
	
}
