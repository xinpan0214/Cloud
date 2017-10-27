package com.gvtv.android.cloud.adapter;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.activity.SearchDeviceActivity;
import com.gvtv.android.cloud.bean.DeviceInfo;
public class DevicesAdapter extends BaseAdapter {

	private SearchDeviceActivity mContext;
	private List<DeviceInfo> deviceInfos;
	private LayoutInflater mInflater;

	public DevicesAdapter(SearchDeviceActivity mContext,List<DeviceInfo> deviceInfos) {
		this.mContext = mContext;
		this.deviceInfos = deviceInfos;
		mInflater = LayoutInflater.from(this.mContext);
	}

	@Override
	public int getCount() {
		return deviceInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return deviceInfos.get(position);
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
			convertView = mInflater.inflate(R.layout.bind_item, null);
			holder.btn = (Button) convertView.findViewById(R.id.btn);
			holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.iv_status = (ImageView) convertView.findViewById(R.id.iv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.btn.setId(position);
		
		holder.tv_name.setText(deviceInfos.get(position).getDev_name());
		if(deviceInfos.get(position).getStatus() == 0){
			holder.tv_status.setText(mContext.getResources().getString(R.string.not_binded));
			holder.iv_status.setImageResource(R.drawable.public_gou);
			holder.btn.setVisibility(View.VISIBLE);
			holder.btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mContext.toBindDevice(v.getId());
				}
			});
		}else if(deviceInfos.get(position).getStatus() == 1){
			holder.tv_status.setText(mContext.getResources().getString(R.string.has_binded));
			holder.iv_status.setImageResource(R.drawable.public_cha);
			holder.btn.setVisibility(View.INVISIBLE);
		}else if(deviceInfos.get(position).getStatus() == -1){
			holder.tv_status.setText("");
			holder.iv_status.setImageResource(R.drawable.public_cha);
			holder.btn.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	static class ViewHolder {
		private TextView tv_name;
		private TextView tv_status;
		private ImageView iv_status;
		private Button btn;
	}
	
}
