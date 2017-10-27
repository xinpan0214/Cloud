package com.gvtv.android.cloud.adapter;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.bean.AppInfo;
import com.gvtv.android.cloud.bean.VideoInfo;
import com.gvtv.android.cloud.util.LogUtils;
import com.gvtv.android.cloud.util.SortUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class DownloadListAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private List<AppInfo> mAppGroupInfoList;
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();

	public DownloadListAdapter(Context context,List<AppInfo> appGroupInfoList) {
		mContext = context;
		mAppGroupInfoList = appGroupInfoList;
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.public_defaulticon01)
		.showImageForEmptyUri(R.drawable.public_defaulticon01)
		.showImageOnFail(R.drawable.public_defaulticon01)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
	}

	public List<AppInfo> getAppGroupInfos() {
		return mAppGroupInfoList;
	}

	@Override
	public int getGroupCount() {
		return mAppGroupInfoList.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		int childrenCount = mAppGroupInfoList.get(groupPosition).getVideoInfoList().size();
		//LogUtils.getLog(getClass()).verbose("childrenCount:" + childrenCount);
		return childrenCount;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mAppGroupInfoList.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mAppGroupInfoList.get(groupPosition).getVideoInfoList()
				.get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupViewHolder groupViewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.download_group_item, null);
			groupViewHolder = new GroupViewHolder();
			groupViewHolder.appIcon = (ImageView) convertView
					.findViewById(R.id.appicon);
			groupViewHolder.appName = (TextView) convertView
					.findViewById(R.id.appname);
			groupViewHolder.expandableButton = (Button) convertView
					.findViewById(R.id.expandablebtn);
			groupViewHolder.groupCheckBox = (CheckBox) convertView
					.findViewById(R.id.groupcheckbox);
			convertView.setTag(groupViewHolder);
		} else {
			groupViewHolder = (GroupViewHolder) convertView.getTag();
		}
		
		final AppInfo appInfo = mAppGroupInfoList.get(groupPosition);
		imageLoader.displayImage(appInfo.getIcon(), groupViewHolder.appIcon, options, null);
		LogUtils.getLog(getClass()).verbose("Icon=====" + appInfo.getIcon());
		
		Collections.sort(appInfo.getVideoInfoList(), new SortUtils.SortVideoInfoByStatus());
		groupViewHolder.appName.setText(appInfo.getAppname());
		if (isExpanded) {
			groupViewHolder.expandableButton
					.setBackgroundResource(R.drawable.public_arrow01_02);
		} else {
			groupViewHolder.expandableButton
					.setBackgroundResource(R.drawable.public_arrow01_01);
		}
		if (appInfo.getIsVisible() == 1) {
			groupViewHolder.groupCheckBox.setVisibility(View.VISIBLE);
		} else {
			groupViewHolder.groupCheckBox.setVisibility(View.GONE);
		}
		if(appInfo.getIsChecked() == 1){
			groupViewHolder.groupCheckBox.setChecked(true);
		}else{
			groupViewHolder.groupCheckBox.setChecked(false);
		}
		groupViewHolder.groupCheckBox
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						List<VideoInfo>  mVideoInfos = mAppGroupInfoList.get(groupPosition).getVideoInfoList();
						if(appInfo.getIsChecked() == 0){
							appInfo.setIsChecked(1);
							for (VideoInfo videoInfo : mVideoInfos) {
								videoInfo.setChecked(1);
							}
						}else{
							appInfo.setIsChecked(0);
							for (VideoInfo videoInfo : mVideoInfos) {
								videoInfo.setChecked(0);
							}
						}
						notifyDataSetChanged();
					}
				});
		return convertView;
	}

	@Override
	public View getChildView(final int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ChildViewHolder childViewHolder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.download_child_item, null);
			childViewHolder = new ChildViewHolder();
			childViewHolder.nameText = (TextView) convertView
					.findViewById(R.id.nameText);
			childViewHolder.percentText = (TextView) convertView
					.findViewById(R.id.percentText);
			childViewHolder.downloadText = (TextView) convertView
					.findViewById(R.id.downloadText);
			childViewHolder.childCheckBox = (CheckBox) convertView
					.findViewById(R.id.childcheckbox);
			convertView.setTag(childViewHolder);
		} else {
			childViewHolder = (ChildViewHolder) convertView.getTag();
		}
		final VideoInfo videoInfo = mAppGroupInfoList.get(groupPosition)
				.getVideoInfoList().get(childPosition);
		childViewHolder.nameText.setText(videoInfo.getFilename());
		//文件状态 0.下载完成, 1.正在下载, 2.等待下载, 3.暂停下载, 4.无资源,失败
		DecimalFormat df = new DecimalFormat("0.00");
		if(videoInfo.getStatus() == 0){
			childViewHolder.percentText.setText(mContext.getResources().getString(R.string.downloaded));
			
			StringBuffer buf = new StringBuffer();
			buf.append(videoInfo.getFilesize() + "MB/");
			buf.append(videoInfo.getFilesize());
			buf.append("MB");
			childViewHolder.downloadText.setText(buf.toString());
			childViewHolder.downloadText.setVisibility(View.VISIBLE);
		}else if(videoInfo.getStatus() == 1){
			childViewHolder.percentText.setText(videoInfo.getRatio() + "%");
			
			StringBuffer buf = new StringBuffer();
			buf.append(df.format(videoInfo.getRatio() / 100.0 * Integer.parseInt(videoInfo.getFilesize())) + "MB/");
			buf.append(videoInfo.getFilesize());
			buf.append("MB");
			childViewHolder.downloadText.setText(buf.toString());
			childViewHolder.downloadText.setVisibility(View.VISIBLE);
		}else if(videoInfo.getStatus() == 2){
			childViewHolder.percentText.setText(mContext.getResources().getString(R.string.waiting));
			childViewHolder.downloadText.setVisibility(View.GONE);
		}else if(videoInfo.getStatus() == 3){
			childViewHolder.percentText.setText(mContext.getResources().getString(R.string.pause));
			
			StringBuffer buf = new StringBuffer();
			buf.append(df.format(videoInfo.getRatio() / 100.0 * Integer.parseInt(videoInfo.getFilesize())) + "MB/");
			buf.append(videoInfo.getFilesize());
			buf.append("MB");
			childViewHolder.downloadText.setText(buf.toString());
			childViewHolder.downloadText.setVisibility(View.VISIBLE);
		}else if(videoInfo.getStatus() == 4){
			childViewHolder.percentText.setText(mContext.getResources().getString(R.string.fail_no_source));
			childViewHolder.downloadText.setVisibility(View.GONE);
		}
		if (videoInfo.getIsVisible() == 1) {
			childViewHolder.childCheckBox.setVisibility(View.VISIBLE);
		} else {
			childViewHolder.childCheckBox.setVisibility(View.GONE);
		}
		if(videoInfo.getChecked() == 1){
			childViewHolder.childCheckBox.setChecked(true);
		}else{
			childViewHolder.childCheckBox.setChecked(false);
		}
		
		childViewHolder.childCheckBox
				.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if(videoInfo.getChecked() == 1){
							mAppGroupInfoList.get(groupPosition).setIsChecked(0);
							videoInfo.setChecked(0);
						}else{
							videoInfo.setChecked(1);
						}
						notifyDataSetChanged();
					}
				});
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {
		super.onGroupCollapsed(groupPosition);
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		super.onGroupExpanded(groupPosition);
	}

	static class GroupViewHolder {
		public CheckBox groupCheckBox;
		public ImageView appIcon;
		public TextView appName;
		public Button expandableButton;
	}

	static class ChildViewHolder {
		public CheckBox childCheckBox;
		public TextView nameText;
		public TextView percentText;
		public TextView downloadText;
	}

}
