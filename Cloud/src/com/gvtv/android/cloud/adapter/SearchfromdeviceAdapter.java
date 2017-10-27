package com.gvtv.android.cloud.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.bean.VideoInfo;
public class SearchfromdeviceAdapter extends BaseAdapter {

	private Context mContext;
	private List<VideoInfo> videoInfos;
	private LayoutInflater mInflater;

	public SearchfromdeviceAdapter(Context mContext,List<VideoInfo> videoInfos) {
		this.mContext = mContext;
		this.videoInfos = videoInfos;
		mInflater = LayoutInflater.from(this.mContext);
	}

	@Override
	public int getCount() {
		return videoInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return videoInfos.get(position);
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
			convertView = mInflater.inflate(R.layout.activity_search_from_device_item, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.iv);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_episodename);
			holder.tv_no = (TextView) convertView.findViewById(R.id.tv_episodeid);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_name.setText(videoInfos.get(position).getFilename());
		return convertView;
	}

	static class ViewHolder {
		private TextView tv_name;
		private TextView tv_no;
		private ImageView iv;
	}
	
}
