package com.gvtv.android.cloud.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gvtv.android.cloud.R;

public class ViewAddMore extends FrameLayout {

	private TextView tv_loadmore;
	private ProgressBar pb_loadmore;
	
	private View view;

	public ViewAddMore(Context context) {
		super(context);

	}

	public ViewAddMore(Context context, AttributeSet attrs) {
		super(context, attrs);

		view = LayoutInflater.from(context).inflate(R.layout.add_more,
				this);
		tv_loadmore = (TextView) view.findViewById(R.id.tv_loadmore);
		pb_loadmore = (ProgressBar) view.findViewById(R.id.pb_loadmore);
	}

	public void showText() {
		pb_loadmore.setVisibility(View.INVISIBLE);
		tv_loadmore.setVisibility(View.VISIBLE);
	}
	
	public void showProgressBar(){
		pb_loadmore.setVisibility(View.VISIBLE);
		tv_loadmore.setVisibility(View.INVISIBLE);
	}

	public boolean isLoading(){
		return pb_loadmore.getVisibility() == View.VISIBLE;
	}
	
}
