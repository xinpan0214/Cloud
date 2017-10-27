package com.gvtv.android.cloud.view;

import com.gvtv.android.cloud.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TitleView extends RelativeLayout {

	private TextView mBackTextView;
	private TextView mTitleTextView;
	private Button mRightButton;
	private Button searchButton;
	private View view;

	public TitleView(Context context) {
		super(context);

	}

	public TitleView(Context context, AttributeSet attrs) {
		super(context, attrs);

		view = LayoutInflater.from(context).inflate(R.layout.title_view,
				this);
		view.setBackgroundColor(getResources().getColor(R.color.title_bgcolor));
		mBackTextView = (TextView) view.findViewById(R.id.backText);
		mTitleTextView = (TextView) view.findViewById(R.id.titleText);
		mRightButton = (Button) view.findViewById(R.id.rightBtn);
		searchButton = (Button) view.findViewById(R.id.searchBtn);

		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.TitleView);
		String title = array.getString(R.styleable.TitleView_title);
		boolean isVisible = array.getBoolean(
				R.styleable.TitleView_right_visibility, false);
		array.recycle();

		mTitleTextView.setText(title);
		if (isVisible) {
			mRightButton.setVisibility(View.VISIBLE);
		} else {
			mRightButton.setVisibility(View.INVISIBLE);
		}

	}

	public TextView getBackTextView() {
		return mBackTextView;
	}
	
	public void setViewBackgroundColor(int resid){
		view.setBackgroundColor(resid);
	}

	public TextView getTitleTextView() {
		return mTitleTextView;
	}

	public Button getRightButton() {
		return mRightButton;
	}
	
	public Button getSearchButton() {
		return searchButton;
	}

}
