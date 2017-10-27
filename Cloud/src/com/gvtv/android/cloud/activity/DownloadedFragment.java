package com.gvtv.android.cloud.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;

import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.adapter.DownloadedListAdapter;
import com.gvtv.android.cloud.bean.AppInfo;
import com.gvtv.android.cloud.bean.VideoInfo;
import com.gvtv.android.cloud.util.AlertDialogUtil;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.view.utils.ViewAnimation;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

public class DownloadedFragment extends BaseFragment implements OnClickListener, OnCheckedChangeListener{

	private DownloadManagerActivity mActivity;
	private FrameLayout flyt_navi_downloaded;
	private RadioGroup editDownloadingGroup;
	private RadioButton rbtn_redownload, rbtn_delete;
	private View mAnimView;
	
	private int startLeft;
	private int endRight;
	private PullToRefreshScrollView mPullRefreshScrollView;
	
	private DownloadedListAdapter adapter;
	private ExpandableListView expandableListView;
	
	public DownloadedFragment() {	
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = (DownloadManagerActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_downloaded_fragment, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			findViews();
			setListeners();
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onDetach() {
		if(expandableListView != null){
			int groupCount = expandableListView.getCount();
		    for (int i=0; i<groupCount; i++) {
		    	expandableListView.collapseGroup(i);
		    }
		}
		super.onDetach();
	}

	private void findViews() {
		expandableListView = (ExpandableListView) mActivity.findViewById(R.id.lv_downloaded);
		flyt_navi_downloaded = (FrameLayout) mActivity.findViewById(R.id.flyt_navi_fragment_ed);
		editDownloadingGroup = (RadioGroup) mActivity.findViewById(R.id.editDownloadedGroup);
		rbtn_redownload = (RadioButton) mActivity.findViewById(R.id.rbtn_redownload);
		rbtn_delete = (RadioButton) mActivity.findViewById(R.id.rbtn_delete_ed);
		mAnimView = mActivity.findViewById(R.id.checkedLine_downloaded);
		mPullRefreshScrollView = (PullToRefreshScrollView) mActivity.findViewById(R.id.pull_refresh_scrollview_downloaded);
		
	}
	
	private void setListeners(){
		rbtn_redownload.setOnClickListener(this);
		rbtn_delete.setOnClickListener(this);
		editDownloadingGroup.setOnCheckedChangeListener(this);
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				LogUtils.getLog(DownloadingFragment.class).verbose("onRefresh");
				AlertDialogUtil.getAlertDialogUtil().showDialog(mActivity,
						mActivity.getResources().getString(R.string.loading_please_wait));
				try {
					mActivity.queryFinVideo(mActivity.pagenum_downlaoded, mActivity);
				} catch (Exception e) {
					LogUtils.getLog(getClass()).error(e.toString());
				}
				mPullRefreshScrollView.onRefreshComplete();
			}
		});
		
		expandableListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			
			@Override
			public void onGroupCollapse(int groupPosition) {
				setListViewHeight(expandableListView);
			}
		});
		
		expandableListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			
			@Override
			public void onGroupExpand(int groupPosition) {
				setListViewHeight(expandableListView);
			}
		});
		
	}
	
	private void init(){
		rbtn_redownload.setVisibility(View.VISIBLE);
		rbtn_delete.setVisibility(View.VISIBLE);
		rbtn_redownload.setChecked(true);
		LayoutParams mLayoutParams = mAnimView.getLayoutParams();
		mLayoutParams.width = getResources().getDisplayMetrics().widthPixels / 5;
		mAnimView.setLayoutParams(mLayoutParams);
		flyt_navi_downloaded.setVisibility(View.GONE);
		adapter = new DownloadedListAdapter(mActivity, mActivity.groupList_downlaoded);
		expandableListView.setAdapter(adapter);
		setListViewHeight(expandableListView);
		rbtn_redownload.setChecked(true);
		mActivity.editMethod = 3;
	}

	public void onClick(View v) {
		if(v == rbtn_delete){
			rbtn_delete.setChecked(true);
			mActivity.editMethod = 4;
		}else if(v == rbtn_redownload){
			rbtn_redownload.setChecked(true);
			mActivity.editMethod = 3;
		}
	}


	@Override
	public void OnFragmentStatusChanged(boolean isEditting) {
		LogUtils.getLog(getClass()).verbose("OnFragmentStatusChanged ,isEditting: " + isEditting);
		if(isEditting){
			if(flyt_navi_downloaded != null){
				flyt_navi_downloaded.setVisibility(View.VISIBLE);
			}
			for (int i = 0; i < mActivity.groupList_downlaoded.size(); i++) {
				AppInfo group = mActivity.groupList_downlaoded.get(i);
				group.setIsVisible(1);
				group.setIsChecked(0);
				List<VideoInfo> tempVideoInfos = group.getVideoInfoList();
				for (int j = 0; j < tempVideoInfos.size(); j++) {
					VideoInfo vidoInfo = tempVideoInfos.get(j);
					vidoInfo.setIsVisible(1);
					vidoInfo.setChecked(0);
				}
			}
		}else{
			mActivity.flyt_navi.setVisibility(View.VISIBLE);
			mActivity.mTitleView.getRightButton().setText(getResources().getString(R.string.edit));
			if(flyt_navi_downloaded != null){
				flyt_navi_downloaded.setVisibility(View.GONE);
			}
			for (int i = 0; i < mActivity.groupList_downlaoded.size(); i++) {
				AppInfo group = mActivity.groupList_downlaoded.get(i);
				group.setIsVisible(0);
				group.setIsChecked(0);
				List<VideoInfo> tempVideoInfos = group.getVideoInfoList();
				for (int j = 0; j < tempVideoInfos.size(); j++) {
					VideoInfo vidoInfo = tempVideoInfos.get(j);
					vidoInfo.setIsVisible(0);
					vidoInfo.setChecked(0);
				}
			}
		}
		
		if(adapter != null){
			adapter.notifyDataSetChanged();
			setListViewHeight(expandableListView);
		}else{
			adapter = new DownloadedListAdapter(mActivity, mActivity.groupList_downlaoded);
			expandableListView.setAdapter(adapter);
			setListViewHeight(expandableListView);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rbtn_delete_ed:
			startLeft = rbtn_redownload.getLeft();
			endRight = rbtn_delete.getLeft();
			ViewAnimation.setImageSlide(mAnimView, startLeft, endRight, 0, 0);
			break;
			
		case R.id.rbtn_redownload:
			startLeft = rbtn_delete.getLeft();
			endRight = rbtn_redownload.getLeft();
			ViewAnimation.setImageSlide(mAnimView,  startLeft, endRight, 0, 0);
			break;

		default:
			break;
		}
	}
	
	private void setListViewHeight(ExpandableListView listView) {  
        ListAdapter listAdapter = listView.getAdapter();  
        int totalHeight = 0;  
        int count = listAdapter.getCount();  
        for (int i = 0; i < count; i++) {  
            View listItem = listAdapter.getView(i, null, listView);  
            listItem.measure(0, 0);  
            totalHeight += listItem.getMeasuredHeight();  
        }  
  
        ViewGroup.LayoutParams params = listView.getLayoutParams();  
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));  
        listView.setLayoutParams(params);  
        listView.requestLayout();
    }  
}
