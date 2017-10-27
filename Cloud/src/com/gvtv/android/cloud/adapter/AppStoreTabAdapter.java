package com.gvtv.android.cloud.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.bean.Classify;

public class AppStoreTabAdapter extends BaseAdapter {

	private Context mContext;
	private List<Classify> mClassifys;
	private LayoutInflater mInflater;

	public AppStoreTabAdapter(Context mContext,List<Classify> mClassifys) {
		this.mContext = mContext;
		this.mClassifys = mClassifys;
		mInflater = LayoutInflater.from(this.mContext);
	}

	@Override
	public int getCount() {
		return mClassifys.size();
	}

	@Override
	public Object getItem(int position) {
		return mClassifys.get(position);
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
			convertView = mInflater.inflate(R.layout.appstore_tab_item, null);
			holder.tv_name = (TextView) convertView.findViewById(R.id.appstore_tab_textview);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_name.setText(mClassifys.get(position).getClassifyname());
		return convertView;
	}

	static class ViewHolder {
		private TextView tv_name;
	}
	
}

