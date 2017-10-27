package com.gvtv.android.cloud.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.bean.AppInfo;
public class FileFolderAdapter extends BaseAdapter {

	private Context mContext;
	private List<AppInfo> mAppGroupInfos;
	private LayoutInflater mInflater;

	public FileFolderAdapter(Context mContext,List<AppInfo> mAppGroupInfos) {
		this.mContext = mContext;
		this.mAppGroupInfos = mAppGroupInfos;
		mInflater = LayoutInflater.from(this.mContext);
	}

	@Override
	public int getCount() {
		return mAppGroupInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return mAppGroupInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.folder_list_item, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tv_name.setText(mAppGroupInfos.get(position).getAppname());
		return convertView;
	}

	static class ViewHolder {
		private TextView tv_name;
	}
	
}
