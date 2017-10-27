package com.gvtv.android.cloud.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

public class SlippingDisabledGridView extends GridView {

	public SlippingDisabledGridView(Context context) {
		super(context);
	}

	public SlippingDisabledGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SlippingDisabledGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	// 通过重新dispatchTouchEvent方法来禁止滑动
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_MOVE) {
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}
}
