package com.gvtv.android.cloud.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.bean.AppInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class AppStorePagerAdapter extends PagerAdapter {

	//private ArrayList<ImageView> pagers;
	private List<AppInfo> mAppInfos_recommand_top;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private LayoutInflater inflater;
	private OnClickListener listener;

	public AppStorePagerAdapter(List<AppInfo> mAppInfos_recommand_top, Context mContext, OnClickListener listener) {
		//this.pagers = pagers;
		this.mAppInfos_recommand_top = mAppInfos_recommand_top;
		this.listener = listener;
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.public_defaulticon02)
		.showImageForEmptyUri(R.drawable.public_defaulticon02)
		.showImageOnFail(R.drawable.public_defaulticon02)
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new SimpleBitmapDisplayer())
		.build();
		inflater = LayoutInflater.from(mContext);
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		if (arg0 == arg1) {
			return true;
		}
		return false;
	}

	@Override
	public int getCount() {
		if(mAppInfos_recommand_top.size() == 1 || mAppInfos_recommand_top.size() == 0){
			return mAppInfos_recommand_top.size();
		}else{
			return Integer.MAX_VALUE;
		}
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView iv = (ImageView) inflater.inflate(R.layout.appstore_recommend_item, null);
		imageLoader.displayImage(mAppInfos_recommand_top.get(immediatePosition(position)).getImage0(), iv, options, null);
		iv.setOnClickListener(listener);
		container.addView(iv);
		return iv;
	}
	
	public int immediatePosition(int position){
		int size = mAppInfos_recommand_top.size();
		if(size == 0){
			size = 1;
		}
		return position % size;
	}
	
}