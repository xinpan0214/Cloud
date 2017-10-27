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
import com.gvtv.android.cloud.bean.AppInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
public class HotAppAdapter extends BaseAdapter {

	private Context mContext;
	private List<AppInfo> appInfos;
	private LayoutInflater mInflater;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	public HotAppAdapter(Context mContext,List<AppInfo> appInfos) {
		this.mContext = mContext;
		this.appInfos = appInfos;
		mInflater = LayoutInflater.from(this.mContext);
		options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.public_defaulticon01)
			.showImageForEmptyUri(R.drawable.public_defaulticon01)
			.showImageOnFail(R.drawable.public_defaulticon01)
			.cacheInMemory()
			.cacheOnDisc()
			.displayer(new SimpleBitmapDisplayer())
			.build();
	}

	@Override
	public int getCount() {
		return appInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return appInfos.get(position);
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
			convertView = mInflater.inflate(R.layout.gv_item_appstore, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.appIcon);
			holder.tv_name = (TextView) convertView.findViewById(R.id.appName);
			holder.tv_upgrade = (TextView) convertView.findViewById(R.id.appupgrade);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.tv_name.setText(appInfos.get(position).getAppname());
		imageLoader.displayImage(appInfos.get(position).getIcon(), holder.iv, options, null);
		holder.tv_upgrade.setText("");
		return convertView;
	}

	static class ViewHolder {
		private TextView tv_name;
		private TextView tv_upgrade;
		private ImageView iv;
	}
	
}
