package com.gvtv.android.cloud.view.utils;

import android.view.View;
import android.view.animation.TranslateAnimation;

public class ViewAnimation {
    /**
     * 设置图像移动动画效果
     * @param v
     * @param startX
     * @param toX
     * @param startY
     * @param toY
     */
    public static void setImageSlide(View v, int startX, int toX, int startY, int toY) {
        TranslateAnimation anim = new TranslateAnimation(startX, toX, startY, toY);
        anim.setDuration(100);
        anim.setFillAfter(true);
        v.startAnimation(anim);
    }
}
