package com.gvtv.android.cloud.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.gvtv.android.cloud.R;

public class PercentBar extends View {
	private Paint paint;
	private float roundWidth;
	private int roundColor;
	private float percentTextSize;
	private int useSpaceColor;
	private int usePercentColor;
	private int availableSpaceColor;
	private int availablePercentColor;
	private float max;
	private float useSpaceProgress;
	private float availableSpaceProgress;

	public float getRoundWidth() {
		return roundWidth;
	}

	public void setRoundWidth(float roundWidth) {
		this.roundWidth = roundWidth;
	}

	public int getRoundColor() {
		return roundColor;
	}

	public void setRoundColor(int roundColor) {
		this.roundColor = roundColor;
	}

	public float getPercentTextSize() {
		return percentTextSize;
	}

	public void setPercentTextSize(float percentTextSize) {
		this.percentTextSize = percentTextSize;
	}

	public int getUseSpaceColor() {
		return useSpaceColor;
	}

	public void setUseSpaceColor(int useSpaceColor) {
		this.useSpaceColor = useSpaceColor;
	}

	public int getUsePercentColor() {
		return usePercentColor;
	}

	public void setUsePercentColor(int usePercentColor) {
		this.usePercentColor = usePercentColor;
	}

	public int getAvailableSpaceColor() {
		return availableSpaceColor;
	}

	public void setAvailableSpaceColor(int availableSpaceColor) {
		this.availableSpaceColor = availableSpaceColor;
	}

	public int getAvailablePercentColor() {
		return availablePercentColor;
	}

	public void setAvailablePercentColor(int availablePercentColor) {
		this.availablePercentColor = availablePercentColor;
	}

	public float getMax() {
		return max;
	}

	public void setMax(float max) {
		this.max = max;
	}

	public float getUseSpaceProgress() {
		return useSpaceProgress;
	}

	public void setUseSpaceProgress(float useSpaceProgress) {
		this.useSpaceProgress = useSpaceProgress;
		invalidate();
	}

	public float getAvailableSpaceProgress() {
		return availableSpaceProgress;
	}

	public void setAvailableSpaceProgress(float availableSpaceProgress) {
		this.availableSpaceProgress = availableSpaceProgress;
		invalidate();
	}

	public PercentBar(Context context) {
		this(context, null);
	}

	public PercentBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PercentBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		paint = new Paint();

		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.PercentBar);

		// 获取自定义属性和默认值
		roundColor = mTypedArray.getColor(R.styleable.PercentBar_roundColor,
				0xFF7D7C7C);
		useSpaceColor = mTypedArray.getColor(
				R.styleable.PercentBar_useSpaceColor,
				0xFFFFB71A);
		usePercentColor = mTypedArray.getColor(
				R.styleable.PercentBar_usePercentColor,
				0xFFFFFFFF);
		availableSpaceColor = mTypedArray.getColor(
				R.styleable.PercentBar_availableSpaceColor,
				0xFFDDDADA);
		availablePercentColor = mTypedArray.getColor(
				R.styleable.PercentBar_availablePercentColor,
				0xFF373737);

		percentTextSize = mTypedArray.getDimension(
				R.styleable.PercentBar_percentTextSize, 15);
		roundWidth = mTypedArray.getDimension(
				R.styleable.PercentBar_roundWidth, 5);
		max = mTypedArray.getFloat(R.styleable.PercentBar_max, 100);
		useSpaceProgress = mTypedArray.getInteger(
				R.styleable.PercentBar_useSpaceProgress, 5);
		availableSpaceProgress = mTypedArray.getFloat(
				R.styleable.PercentBar_availableSpaceProgress, 95);

		mTypedArray.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int centre = getWidth() / 2; // 获取0圆心的x坐标
		int radius = (int) (centre - roundWidth / 2); // 圆环的半径
		paint.setColor(roundColor); // 设置圆环的颜色
		paint.setStyle(Paint.Style.STROKE); // 设置空心
		paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
		paint.setAntiAlias(true); // 消除锯齿
		// RectF circleOval = new RectF(roundWidth / 2, roundWidth / 2, 2 *
		// centre
		// - roundWidth / 2, 2 * centre - roundWidth / 2);
		// canvas.drawArc(circleOval, 0, 360, false, paint);
		canvas.drawCircle(centre, centre, radius, paint); // 画出圆环

		paint.setColor(useSpaceColor);
		paint.setStyle(Paint.Style.FILL);

		// fill 表示贴合
		RectF oval = new RectF(roundWidth, roundWidth, 2 * centre - roundWidth,
				2 * centre - roundWidth);

		// stroke用这个表示贴合
		// RectF oval = new RectF(3 * roundWidth / 2, 3 * roundWidth / 2, 2
		// * centre - 3 * roundWidth / 2, 2 * centre - 3 * roundWidth / 2);
		canvas.drawArc(oval, -90, 360 * useSpaceProgress / max, true, paint);

		paint.setColor(availableSpaceColor);
		paint.setStrokeWidth(roundWidth);

		canvas.drawArc(oval, 360 * useSpaceProgress / max - 90, 360
				* availableSpaceProgress / max, true, paint);

		paint.setTextSize(percentTextSize);
		
		
		
		int useSpace = (int) (useSpaceProgress * 100 / max);
		
		if(useSpace != 0){
			paint.setColor(usePercentColor);
			String usePercentText = useSpace + "%";
			float useWidth = paint.measureText(usePercentText);

			canvas.drawText(
					usePercentText,
					(float) (centre * (1 + Math.cos(useSpaceProgress * Math.PI
							/ max - Math.PI / 2) / 2))
							- useWidth / 2,
					(float) (centre * (1 + Math.sin(useSpaceProgress * Math.PI
							/ max - Math.PI / 2) / 2)), paint);
		}
		
		if(useSpace != 100){
			paint.setColor(availablePercentColor);
			String availablePercentText = 100 - useSpace + "%";
			float availWidth = paint.measureText(availablePercentText);
			canvas.drawText(
					availablePercentText,
					(float) (centre * (1 - Math.cos(availableSpaceProgress
							* Math.PI / max - Math.PI / 2) / 2))
							- availWidth / 2,
					(float) (centre * (1 + Math.sin(availableSpaceProgress
							* Math.PI / max - Math.PI / 2) / 2)), paint);
		}
		
	}
}
