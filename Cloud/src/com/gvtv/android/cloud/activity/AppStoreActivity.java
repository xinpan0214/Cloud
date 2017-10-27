package com.gvtv.android.cloud.activity;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.common.dataprovider.HttpCallBack;
import com.android.common.dataprovider.packet.outpacket.OutPacket;
import com.gvtv.android.cloud.AppConst;
import com.gvtv.android.cloud.CloudApplication;
import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.adapter.AppStorePagerAdapter;
import com.gvtv.android.cloud.adapter.AppStoreTabPagerAdapter;
import com.gvtv.android.cloud.adapter.HotAppAdapter;
import com.gvtv.android.cloud.bean.AppInfo;
import com.gvtv.android.cloud.bean.Classify;
import com.gvtv.android.cloud.bean.MsgBean;
import com.gvtv.android.cloud.bean.Recommendcolumn;
import com.gvtv.android.cloud.http.HttpConstants;
import com.gvtv.android.cloud.http.HttpRequestUtils;
import com.gvtv.android.cloud.http.XmlPullParserUtils;
import com.gvtv.android.cloud.util.AlertDialogUtil;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.ToastUtil;
import com.gvtv.android.cloud.view.PointWidget;
import com.gvtv.android.cloud.view.PullToRefreshScrollViewVertical;
import com.gvtv.android.cloud.view.SlippingDisabledGridView;
import com.gvtv.android.cloud.view.TitleView;
import com.gvtv.android.cloud.view.ViewAddMore;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

public class AppStoreActivity extends BaseActivity implements OnClickListener, OnItemClickListener, HttpCallBack {

	private ViewPager viewpager_recommended, viewpager_tab;
	private TitleView appstoreTitle;
	private TextView hot_app;
	private Button btn_search;
	private TextView tv_titleleft;
	private SlippingDisabledGridView gv;
	private PointWidget points;
	private RelativeLayout rlyt_recommended;
	private List<GridView> pagers_tab = new ArrayList<GridView>();
	private List<Recommendcolumn> mRecommendcolumns = new ArrayList<Recommendcolumn>();
	private List<AppInfo> mAppInfos_recommand_top = new ArrayList<AppInfo>();
	private List<AppInfo> mAppInfos_recommand_bottom = new ArrayList<AppInfo>();
	private List<Classify> mClassifys = new ArrayList<Classify>();
	private HotAppAdapter mHotAppAdapter;
	private ViewAddMore mViewAddMore;
	
	private PullToRefreshScrollViewVertical mPullToRefreshScrollViewVertical;
	private int pagenumBottom;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appstore);
		pagenumBottom = 1;
		findViews();
		setListners();
		AlertDialogUtil.getAlertDialogUtil().showDialogWithoutCancelListener(this, getResources().getString(R.string.loading_please_wait));
		firstRequest();
		CloudApplication.getInstance().addActivity(this);
	}

	@Override
	public void onMessage(int type, int resp, MsgBean msg, byte[] restoreByte) {
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void findViews() {
		hot_app = (TextView) findViewById(R.id.hot_app);
		mViewAddMore = (ViewAddMore) findViewById(R.id.flyt_loading);
		mViewAddMore.showText();
		viewpager_recommended = (ViewPager) findViewById(R.id.viewpager_recommended);
		viewpager_tab = (ViewPager) findViewById(R.id.viewpager_tab);
		gv = (SlippingDisabledGridView) findViewById(R.id.gv_app);
		appstoreTitle = (TitleView) findViewById(R.id.appstoreTitle);
		appstoreTitle.setViewBackgroundColor(getResources().getColor(R.color.app_store_title_color));
		appstoreTitle.getRightButton().setVisibility(View.GONE);
		btn_search = appstoreTitle.getSearchButton();
		btn_search.setVisibility(View.VISIBLE);
		tv_titleleft = (TextView) appstoreTitle.getBackTextView();
		points = (PointWidget) findViewById(R.id.appstore_point);
		points.setPointPadding(7, 0, 7, 0);
		points.setPointSize(24, 10);
		rlyt_recommended = (RelativeLayout) findViewById(R.id.rlyt_recommended);
		btn_search.setBackgroundResource(R.drawable.icon_04index_search01);
		btn_search.requestFocus();
		mViewAddMore.setVisibility(View.INVISIBLE);
		mPullToRefreshScrollViewVertical = (PullToRefreshScrollViewVertical) findViewById(R.id.sv_appstore);
		mHotAppAdapter = new HotAppAdapter(getApplicationContext(), mAppInfos_recommand_bottom);
		gv.setAdapter(mHotAppAdapter);
	}

	private void setListners() {
		mViewAddMore.setOnClickListener(this);
		btn_search.setOnClickListener(this);
		tv_titleleft.setOnClickListener(this);
		gv.setOnItemClickListener(this);
		mPullToRefreshScrollViewVertical.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				refresh();
				mPullToRefreshScrollViewVertical.onRefreshComplete();
			}
		});
		
		viewpager_recommended.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				refreshPointWidget(arg0 % mAppInfos_recommand_top.size());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}
	
	private void firstRequest(){
		HttpRequestUtils.requestRecommendcolumn(this, this);
		HttpRequestUtils.requestClassifylist(this, this);
	}
	
	private void refresh(){
		int num = 0;
		if(mClassifys.size() == 0){
			HttpRequestUtils.requestClassifylist(this, this);
			num ++;
		}
		if(mRecommendcolumns.size() == 0){
			HttpRequestUtils.requestRecommendcolumn(this, this);
			num ++;
		}else{
			if(mRecommendcolumns.size() > 1){
				Recommendcolumn recommendcolumn_bottom = mRecommendcolumns.get(1);
				if(recommendcolumn_bottom != null){
					hot_app.setText(mRecommendcolumns.get(1).getName());
					HttpRequestUtils.requestRecommend(this, this, mRecommendcolumns.get(1).getCode(), pagenumBottom, 20, HttpConstants.ACTION_RECOMMEND_BOTTOM_TOKEN);
					num ++;
				}
			}
			
			if(mRecommendcolumns.size() > 0){
				Recommendcolumn recommendcolumn_top = mRecommendcolumns.get(0);
				if(recommendcolumn_top != null){
					HttpRequestUtils.requestRecommend(this, this, mRecommendcolumns.get(0).getCode(), 0, 20, HttpConstants.ACTION_RECOMMEND_TOP_TOKEN);
					num ++;
				}
			}
		}
		if(num > 0){
			AlertDialogUtil.getAlertDialogUtil().showDialogWithoutCancelListener(
					AppStoreActivity.this,
					AppStoreActivity.this.getResources().getString(
							R.string.loading_please_wait));
		}
	}

	@Override
	public void onClick(View v) {
		if (v == btn_search) {
			Intent intent = new Intent(AppStoreActivity.this, SearchAppActivity.class);
			startActivity(intent);
		} else if (v == tv_titleleft) {
			if(rlyt_recommended.getVisibility() == View.VISIBLE){
				finish();
			}else{
				rlyt_recommended.setVisibility(View.VISIBLE);
			}
		}else if(v instanceof ImageView){
			int position = viewpager_recommended.getCurrentItem();
			Intent intent = new Intent(AppStoreActivity.this, APPDetailActivity.class);
			intent.putExtra(AppConst.APPSTORE_TO_APPDETAIL, mAppInfos_recommand_top.get(position % mAppInfos_recommand_top.size()));
			startActivity(intent);
		}else if(v == mViewAddMore){
			if(!mViewAddMore.isLoading() && mRecommendcolumns.size() > 1){
				mViewAddMore.showProgressBar();
				HttpRequestUtils.requestRecommend(this, this, mRecommendcolumns.get(1).getCode(), pagenumBottom, 20, HttpConstants.ACTION_RECOMMEND_BOTTOM_TOKEN);
			}
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if(parent == gv){
			Intent intent = new Intent(AppStoreActivity.this, APPDetailActivity.class);
			intent.putExtra(AppConst.APPSTORE_TO_APPDETAIL, mAppInfos_recommand_bottom.get(position));
			startActivity(intent);
		}else{
			for (int i = 0; i < pagers_tab.size(); i++) {
				if(parent == pagers_tab.get(i)){
					int poi = i * 8 + position;
					Intent intent = new Intent(AppStoreActivity.this, AppClassifyActivity.class);
					intent.putExtra(AppConst.CLASSIFY, mClassifys.get(poi));
					startActivity(intent);
					break;
				}
			}
		}
	}

	@Override
	public void onCancel(OutPacket out, int token) {
		ToastUtil.getToastUtils().showToast(getApplicationContext(), getResources().getString(R.string.xlistview_header_hint_normal));
		LogUtils.getLog(getClass()).verbose("task token: " + token + " is canceled");
	}

	@Override
	public void onSuccessful(ByteBuffer buffer, int bufLen, int token) {
		LogUtils.getLog(getClass()).verbose("task token: " + token + " is onSuccessful");
		//LogUtils.getLog(getClass()).verbose(new String(buffer.array()));
		switch (token) {
		case HttpConstants.ACTION_RECOMMENDCOLUMN_TOKEN:
			mRecommendcolumns.addAll(XmlPullParserUtils.parseToRecommendcolumn(buffer));
			if(mRecommendcolumns.size() > 1){
				Recommendcolumn recommendcolumn_bottom = mRecommendcolumns.get(1);
				if(recommendcolumn_bottom != null){
					hot_app.setText(mRecommendcolumns.get(1).getName());
					HttpRequestUtils.requestRecommend(this, this, mRecommendcolumns.get(1).getCode(), pagenumBottom, 20, HttpConstants.ACTION_RECOMMEND_BOTTOM_TOKEN);
				}
			}
			
			if(mRecommendcolumns.size() > 0){
				Recommendcolumn recommendcolumn_top = mRecommendcolumns.get(0);
				if(recommendcolumn_top != null){
					HttpRequestUtils.requestRecommend(this, this, mRecommendcolumns.get(0).getCode(), 0, 20, HttpConstants.ACTION_RECOMMEND_TOP_TOKEN);
				}
			}
			if(mRecommendcolumns.size() == 0){
				AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			}
			
			break;
			
		case HttpConstants.ACTION_RECOMMEND_BOTTOM_TOKEN:
			mViewAddMore.showText();
			ArrayList<AppInfo> temp = XmlPullParserUtils.parseToAppInfo(buffer);
			if(temp .size() > 0){
				if(pagenumBottom == temp.get(0).getCurrentpage()){
					pagenumBottom ++;
					mAppInfos_recommand_bottom.addAll(temp);
				}
			}
			refreshGv();
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			break;
			
		case HttpConstants.ACTION_RECOMMEND_TOP_TOKEN:
			if(mAppInfos_recommand_top.size() == 0){
				mAppInfos_recommand_top.addAll(XmlPullParserUtils.parseToAppInfo(buffer));
				if(mAppInfos_recommand_top.size() <= 5){
				}else if(mAppInfos_recommand_top.size() > 5){
					mAppInfos_recommand_top = mAppInfos_recommand_top.subList(0, 5);
				}
				refresh_pagers_recommended();
			}
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();
			break;
			
		case HttpConstants.ACTION_CLASSIFYLIST_TOKEN:
			mClassifys = XmlPullParserUtils.parseToClassify(buffer);
			refresh_pagers_tab();
			AlertDialogUtil.getAlertDialogUtil().cancelDialog();

		default:
			break;
		}
		
	}

	@Override
	public void onNetError(int responseCode, String errorDesc, OutPacket out, int token) {
		AlertDialogUtil.getAlertDialogUtil().cancelDialog();
		if(mAppInfos_recommand_bottom.size() == 0){
			mViewAddMore.setVisibility(View.INVISIBLE);
		}
		ToastUtil.getToastUtils().showToast(getApplicationContext(), getResources().getString(R.string.xlistview_header_hint_normal));
		LogUtils.getLog(getClass()).verbose("task token: " + token + " is onNetError");

	}

	private void setGridViewHeight(GridView gridView) {
		ListAdapter gridAdapter = gridView.getAdapter();
		if (gridAdapter == null) {
			return;
		}
		View listItem = gridAdapter.getView(0, null, gridView);
		listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		int rows;
		if (mAppInfos_recommand_bottom.size() % 3 == 0) {
			rows = mAppInfos_recommand_bottom.size() / 3;
		} else {
			rows = mAppInfos_recommand_bottom.size() / 3 + 1;
		}
		ViewGroup.LayoutParams paramsG = gridView.getLayoutParams();
		paramsG.height = (int) (listItem.getMeasuredHeight() * rows
				+ getResources().getDisplayMetrics().density * 12 * (rows - 1));
		gridView.setLayoutParams(paramsG);
	}
	
	private void refreshGv(){
		if(mAppInfos_recommand_bottom.size() == 0){
			mViewAddMore.setVisibility(View.INVISIBLE);
		}else{
			if(mAppInfos_recommand_bottom.size() < mAppInfos_recommand_bottom.get(0).getTotal()){
				mViewAddMore.setVisibility(View.VISIBLE);
			}else{
				mViewAddMore.setVisibility(View.INVISIBLE);
			}
		}
		if(mAppInfos_recommand_bottom.size() > 0){
			mHotAppAdapter.notifyDataSetChanged();
			setGridViewHeight(gv);
		}
	}
	
	private void refresh_pagers_recommended(){
		viewpager_recommended.setAdapter(new AppStorePagerAdapter(mAppInfos_recommand_top, AppStoreActivity.this, AppStoreActivity.this));
		viewpager_recommended.setCurrentItem(mAppInfos_recommand_top.size() * 1000);
		initPointWidget(mAppInfos_recommand_top.size());
	}
	
	private void refresh_pagers_tab(){
		LayoutInflater inflater = LayoutInflater.from(this);
		int count = (mClassifys.size() % 8 == 0) ? (mClassifys.size() / 8) : (mClassifys.size() / 8 + 1);
		for (int i = 0; i < count; i++) {
			SlippingDisabledGridView gv = (SlippingDisabledGridView) inflater.inflate(R.layout.appstore_tab_gridview, null);
			pagers_tab.add(gv);
			gv.setOnItemClickListener(this);
		}
		viewpager_tab.setAdapter(new AppStoreTabPagerAdapter(pagers_tab, mClassifys, getApplicationContext()));
	}
	
	private void refreshPointWidget(int curr){
		points.setPoint(curr);
	}
	
	private void initPointWidget(int count){
		points.setPointCount(count);
	}
	

}
