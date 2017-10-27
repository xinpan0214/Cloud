package com.gvtv.android.cloud.view;
import java.util.ArrayList;

import com.gvtv.android.cloud.R;
import com.gvtv.android.cloud.util.UiUtil;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;



public class PointWidget extends LinearLayout{

	ArrayList<View> pointList;
	ImageView point;
	LayoutParams lp;
	Context context;
	int left,top,right,bottom, width = -2,heigth = -2;// -2为warp
	
	public PointWidget(Context context) {
		super(context);
		this.context=context;
		init();
		setOrientation(0);
	}  
	public PointWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		init();
		setOrientation(0);
	}
	private void init(){
		pointList = new ArrayList<View>();
	}
	public void setPointCount(int PointCount){
		lp = (null == lp ? new LayoutParams(width, heigth) : lp);
		for(int i=0;i<PointCount;i++){
			point = new ImageView(context);
			point.setImageResource(R.drawable.page_indicator_selector);
			point.setPadding(left, top, right, bottom);
			point.setEnabled(false);
			point.setLayoutParams(lp);
			if(pointList.size()==0){
				point.setEnabled(true);
			}else{
				pointList.get(0).setEnabled(true);
			}
			pointList.add(point);
			addView(point);
		}
	}
	public void setPoint(int i){
		for(int a=0;a<pointList.size();a++){
			if(a==i){
				pointList.get(i).setEnabled(true);
				continue;
			}
			pointList.get(a).setEnabled(false);
		}
		
	}
	
	public int getPointLenth(){
		return (null == pointList ? 0 : pointList.size());
	}
	
	/**
	 * 设置point边距 单位dp
	 * @param left
	 * @param top
	 * @param right
	 * @param bottom
	 */
	public void setPointPadding(int left, int top, int right, int bottom){
		this.left = UiUtil.dip2px(context, left);
		this.top = UiUtil.dip2px(context, top);
		this.right = UiUtil.dip2px(context, right);
		this.bottom = UiUtil.dip2px(context, bottom);
	}
	
	/**
	 * 设置point大小 单位dp
	 * @param width
	 * @param height
	 */
	public void setPointSize(int width, int height) {
		this.width = UiUtil.dip2px(context, width);
		this.heigth = UiUtil.dip2px(context, height);
	}
}
