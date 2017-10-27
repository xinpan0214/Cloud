package com.gvtv.android.cloud.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.gvtv.android.cloud.bean.Classify;

public class AppStoreTabPagerAdapter extends PagerAdapter {

	private List<GridView> pagers;
	private Context mContext;
	private List<Classify> mClassifys;
	private List<Classify> mClassifys_page;
	private int totalsize;

	public AppStoreTabPagerAdapter(List<GridView> pagers, List<Classify> mClassifys, Context mContext) {
		this.pagers = pagers;
		this.mContext = mContext;
		this.mClassifys = mClassifys;
		totalsize = mClassifys.size();
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
		return pagers.size();
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(pagers.get(position));
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		GridView gv = pagers.get(position);
		mClassifys_page = new ArrayList<Classify>();
		for (int i = position * 8; i < ((totalsize < (position + 1) * 8) ? (totalsize -1) : ((position + 1) * 8)); i++) {
			mClassifys_page.add(mClassifys.get(i));
		}
		gv.setAdapter(new AppStoreTabAdapter(mContext, mClassifys_page));
		container.addView(gv);
		return gv;
	}
	
}
