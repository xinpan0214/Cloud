package com.gvtv.android.cloud.activity;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.common.dataprovider.HttpCallBack;
import com.android.common.dataprovider.packet.outpacket.OutPacket;
import com.gvtv.android.cloud.AppConst;
import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.adapter.HotAppAdapter;
import com.gvtv.android.cloud.bean.AppInfo;
import com.gvtv.android.cloud.bean.Classify;
import com.gvtv.android.cloud.http.HttpRequestUtils;
import com.gvtv.android.cloud.http.XmlPullParserUtils;
import com.gvtv.android.cloud.util.AlertDialogUtil;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.view.PullToRefreshScrollViewVertical;
import com.gvtv.android.cloud.view.SlippingDisabledGridView;
import com.gvtv.android.cloud.view.TitleView;
import com.gvtv.android.cloud.view.ViewAddMore;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

public class AppClassifyActivity extends BaseActivity implements OnClickListener, OnItemClickListener,
		HttpCallBack {

	private SlippingDisabledGridView gv;
	private ArrayList<AppInfo> apps = new ArrayList<AppInfo>();
	private HotAppAdapter mHotAppAdapter;
	private TextView tv_noapp, tv_search_result;
	private int pagenum;
	private int size;
	private Classify mClassify;
	private ViewAddMore mViewAddMore;
	private TitleView title;
	private PullToRefreshScrollViewVertical sv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appclassify);
		CloudApplication.getInstance().addActivity(this);
		mClassify = getIntent().getParcelableExtra(AppConst.CLASSIFY);
		findViews();
		setListeners();
		size = 20;
		pagenum = 1;
		AlertDialogUtil.getAlertDialogUtil().showDialogWithoutCancelListener(this,
				getResources().getString(R.string.loading_please_wait));
		AlertDialogUtil.getAlertDialogUtil().getDialog().setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				if (apps.size() == 0) {
					mViewAddMore.setVisibility(View.INVISIBLE);
					tv_noapp.setVisibility(View.VISIBLE);
				} else {
					tv_noapp.setVisibility(View.GONE);
					if (apps.size() < apps.get(0).getTotal()) {
						mViewAddMore.setVisibility(View.VISIBLE);
					} else {
						mViewAddMore.setVisibility(View.INVISIBLE);
					}
				}
			}
		});
		HttpRequestUtils.requestClassifysubapplist(this, this, mClassify.getClassifycode(),
				pagenum, size);
	}

	private void findViews() {
		mViewAddMore = (ViewAddMore) findViewById(R.id.flyt_loading);
		mViewAddMore.showText();
		mViewAddMore.setVisibility(View.INVISIBLE);
		gv = (SlippingDisabledGridView) findViewById(R.id.gv_appclassify);
		tv_noapp = (TextView) findViewById(R.id.tv_noapp);
		tv_noapp.setVisibility(View.GONE);
		title = (TitleView) findViewById(R.id.header);
		title.getTitleTextView().setText(mClassify.getClassifyname());
		tv_search_result = (TextView) findViewById(R.id.tv_search_result);
		tv_search_result.setText(mClassify.getClassifyname());
		title.setViewBackgroundColor(getResources().getColor(R.color.app_store_title_color));
		sv = (PullToRefreshScrollViewVertical) findViewById(R.id.sv_classify);
	}

	private void setListeners() {
		mViewAddMore.setOnClickListener(this);
		title.getBackTextView().setOnClickListener(this);
		gv.setOnItemClickListener(this);
		sv.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				AlertDialogUtil.getAlertDialogUtil().showDialogWithoutCancelListener(
						AppClassifyActivity.this,
						AppClassifyActivity.this.getResources().getString(
								R.string.loading_please_wait));
				HttpRequestUtils.requestClassifysubapplist(AppClassifyActivity.this,
						AppClassifyActivity.this, mClassify.getClassifycode(), pagenum, size);
				sv.onRefreshComplete();
			}
		});
	}

	@Override
	public void onClick(View v) {
		if (v == title.getBackTextView()) {
			finish();
		} else if (v == mViewAddMore) {
			if (!mViewAddMore.isLoading()) {
				mViewAddMore.showProgressBar();
				HttpRequestUtils.requestClassifysubapplist(this, this, mClassify.getClassifycode(),
						pagenum, size);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Intent intent = new Intent(AppClassifyActivity.this, APPDetailActivity.class);
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
		if (apps_temp.size() > 0) {
			if (pagenum == apps_temp.get(0).getCurrentpage()) {
				pagenum++;
				apps.addAll(apps_temp);
			}
		}
		refreshGv();
		AlertDialogUtil.getAlertDialogUtil().cancelDialog();
	}

	@Override
	public void onNetError(int responseCode, String errorDesc, OutPacket out, int token) {
		AlertDialogUtil.getAlertDialogUtil().cancelDialog();
	}

	private void refreshGv() {
		if (apps.size() == 0) {
			mViewAddMore.setVisibility(View.INVISIBLE);
			tv_noapp.setVisibility(View.VISIBLE);
		} else {
			tv_noapp.setVisibility(View.GONE);
			if (apps.size() < apps.get(0).getTotal()) {
				mViewAddMore.setVisibility(View.VISIBLE);
			} else {
				mViewAddMore.setVisibility(View.INVISIBLE);
			}
		}
		if (mHotAppAdapter == null) {
			if (apps.size() > 0) {
				mHotAppAdapter = new HotAppAdapter(getApplicationContext(), apps);
				gv.setAdapter(mHotAppAdapter);
				setGridViewHeight(gv);
			}
		} else {
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
