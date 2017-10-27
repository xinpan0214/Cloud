package com.gvtv.android.cloud.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.gvtv.android.cloud.R;

public class SearchAnim extends View {

	private Animation animation;
	/**
	 * @return the animation
	 */
	public Animation getAnimation() {
		return animation;
	}

	public SearchAnim(Context context) {
		super(context);
	}

	public SearchAnim(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	
	private void iniAnim(){
		animation = AnimationUtils.loadAnimation(getContext(),R.anim.progress_anim);
	}

	public void startAnimation() {
		iniAnim();
		startAnimation(animation);
	}
	
	public void cancelAnimation() {
		if(animation != null){
			animation.cancel();
		}
	}

}
