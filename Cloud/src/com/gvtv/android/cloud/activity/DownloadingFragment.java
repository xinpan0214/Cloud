package com.gvtv.android.cloud.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;

import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.adapter.DownloadListAdapter;
import com.gvtv.android.cloud.bean.AppInfo;
import com.gvtv.android.cloud.bean.VideoInfo;
import com.gvtv.android.cloud.util.AlertDialogUtil;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.view.utils.ViewAnimation;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

public class DownloadingFragment extends BaseFragment implements OnClickListener, OnChildClickListener, OnCheckedChangeListener{

	private DownloadManagerActivity mActivity;
	private FrameLayout flyt_navi_downloading;
	private RadioGroup editDownloadingGroup;
	private RadioButton rbtn_pause, rbtn_continue, rbtn_delete;
	
	private DownloadListAdapter adapter;
	private ExpandableListView expandableListView;
	private View mAnimView;
	private int startLeft;
	private int endRight;
	private PullToRefreshScrollView mPullRefreshScrollView;
	
	public DownloadingFragment() {	
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = (DownloadManagerActivity) activity;
		LogUtils.getLog(getClass()).verbose("=======onAttach========");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LogUtils.getLog(getClass()).verbose("=======onCreate========");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.activity_downloading_fragment, container,
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
		expandableListView = (ExpandableListView) mActivity.findViewById(R.id.lv_downloading);
		flyt_navi_downloading = (FrameLayout) mActivity.findViewById(R.id.flyt_navi_fragment);
		editDownloadingGroup = (RadioGroup) mActivity.findViewById(R.id.editDownloadingGroup);
		rbtn_pause = (RadioButton) mActivity.findViewById(R.id.rbtn_pause_ing);
		rbtn_continue = (RadioButton) mActivity.findViewById(R.id.rbtn_continue_ing);
		rbtn_delete = (RadioButton) mActivity.findViewById(R.id.rbtn_delete);
		mAnimView = mActivity.findViewById(R.id.checkedLine_download);
		mPullRefreshScrollView = (PullToRefreshScrollView) mActivity.findViewById(R.id.pull_refresh_scrollview);
	}
	
	private void setListeners(){
		rbtn_pause.setOnClickListener(this);
		rbtn_continue.setOnClickListener(this);
		rbtn_delete.setOnClickListener(this);
		expandableListView.setOnChildClickListener(this);
		editDownloadingGroup.setOnCheckedChangeListener(this);
		
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
		mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				LogUtils.getLog(DownloadingFragment.class).verbose("onRefresh");
				AlertDialogUtil.getAlertDialogUtil().showDialog(mActivity,
						mActivity.getResources().getString(R.string.loading_please_wait));
				try {
					mActivity.queryVideo(mActivity.pagenum_downlaoding, mActivity);
				} catch (Exception e) {
					LogUtils.getLog(getClass()).error(e.toString());
				}
				mPullRefreshScrollView.onRefreshComplete();
			}
		});
		
		
	}
	
	private void init(){
		rbtn_pause.setVisibility(View.VISIBLE);
		rbtn_continue.setVisibility(View.VISIBLE);
		rbtn_delete.setVisibility(View.VISIBLE);
		rbtn_pause.setChecked(true);
		//mActivity.onDownloadingCountChanged(0);
		flyt_navi_downloading.setVisibility(View.GONE);
		rbtn_pause.setChecked(true);
		mActivity.editMethod = 0;
		adapter = new DownloadListAdapter(mActivity, mActivity.groupList_downlaoding);
		expandableListView.setAdapter(adapter);
		setListViewHeight(expandableListView);
	}

	public void onClick(View v) {
		if(v == rbtn_continue){
			rbtn_continue.setChecked(true);
			mActivity.editMethod = 1;
		}else if(v == rbtn_delete){
			rbtn_delete.setChecked(true);
			mActivity.editMethod = 2;
		}else if(v == rbtn_pause){
			rbtn_pause.setChecked(true);
			mActivity.editMethod = 0;
		}
	}

	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
			int childPosition, long id) {
		
		return false;
	}

	@Override
	public void OnFragmentStatusChanged(boolean isEditting) {
		LogUtils.getLog(getClass()).verbose("OnFragmentStatusChanged");
		if(isEditting){
			if(flyt_navi_downloading !=  null){
				flyt_navi_downloading.setVisibility(View.VISIBLE);
				mAnimView.setLeft(getResources().getDisplayMetrics().widthPixels / 5);
			}
			for (int i = 0; i < mActivity.groupList_downlaoding.size(); i++) {
				AppInfo group = mActivity.groupList_downlaoding.get(i);
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
			if(flyt_navi_downloading != null){
				flyt_navi_downloading.setVisibility(View.GONE);
			}
			for (int i = 0; i < mActivity.groupList_downlaoding.size(); i++) {
				AppInfo group = mActivity.groupList_downlaoding.get(i);
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
			adapter = new DownloadListAdapter(mActivity, mActivity.groupList_downlaoding);
			expandableListView.setAdapter(adapter);
			setListViewHeight(expandableListView);
		}
		
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rbtn_pause_ing:
			endRight = rbtn_pause.getLeft() - rbtn_pause.getWidth();
			ViewAnimation.setImageSlide(mAnimView,  startLeft, endRight, 0, 0);
			startLeft = endRight;
			break;
			
		case R.id.rbtn_continue_ing:
			endRight = rbtn_continue.getLeft() - rbtn_continue.getWidth();
			ViewAnimation.setImageSlide(mAnimView,  startLeft, endRight, 0, 0);
			startLeft = endRight;
			break;
			
		case R.id.rbtn_delete:
			endRight = rbtn_delete.getLeft() - rbtn_delete.getWidth();
			ViewAnimation.setImageSlide(mAnimView,  startLeft, endRight, 0, 0);
			startLeft = endRight;
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
