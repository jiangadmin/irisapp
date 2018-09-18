package com.irisking.scanner.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.irisking.irisapp.R;

/**
 * 温度计View
 *
 * Created by tony on 2017/10/12.
 */
public class ThermometerView extends View {
	private ThermometerBuilder mBuilder;
	private Paint mFramePaint;
	private Paint mDegreePaint;
	private Paint mMercuryPaint;
	
	private int top;
	private int left;
	private int bottom;
	private int right;
	
	// 刻度最大值与最小值
//	private int mMaxValue;
//	private int mMinValue;
	// 每一个刻度的高度
	private int heightPerDegree;
	// 所有刻度值（标注于左侧的刻度值）
	private int[] mDegreeArr;
	// 刻度单位
	private String mDegreeUnit;
	// 刻度到温度计的偏移量
	private final int degreeOffset = 12;
	// 水银模块间距
	private final int mercuryMargin = 8;
	// 水银模块弧度
	private final int mercuryRadius = 6;
	// 当前值
	private int mCurrentValue;

	public ThermometerView(Context context, ThermometerBuilder builder) {
		super(context);
		mBuilder = builder;
		setupPaints();
	}
	
	public ThermometerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		parseAttributes(context, attrs);
		setupPaints();
	}
	
	void setupPaints() {
		mFramePaint = createNewPaint();
		mDegreePaint = createNewPaint();
		mMercuryPaint = createNewPaint();
		
		if (mBuilder == null) {
			mFramePaint.setColor(Color.BLACK);
		} else {
			mFramePaint.setColor(mBuilder.getmFillBackgroundColor());
		}
		if (mBuilder == null) {
			mDegreePaint.setColor(Color.BLUE);
		} else {
			mDegreePaint.setColor(mBuilder.getmScaleDegreeColor());
		}
		
		if (mBuilder == null) {
			mMercuryPaint.setColor(Color.BLUE);
		} else {
			mMercuryPaint.setColor(mBuilder.getmScaleMercuryDefaultColor());
		}
	}
	
	void parseAttributes(Context context, AttributeSet attrs) {
		if (attrs == null) {
			return;
		}
		if (mBuilder == null) {
			mBuilder = new ThermometerBuilder();
		}
		TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.ThermometerView);
		int width = (int) t.getDimension(R.styleable.ThermometerView_thermometerWidth, -1);
		if (width > 0) {
			mBuilder.setmThermometerWidth(width);
		}
		int height = (int) t.getDimension(R.styleable.ThermometerView_thermometerHeight, -1);
		if (height > 0) {
			mBuilder.setmThermometerHeight(height);
		}
		int maxValue = (int) t.getDimension(R.styleable.ThermometerView_maxValue, -1);
		if (maxValue > 0) {
			mBuilder.setmMinValue(maxValue);
		}
		int minValue = (int) t.getDimension(R.styleable.ThermometerView_minValue, -1);
		if (minValue > 0) {
			mBuilder.setmMinValue(minValue);
		}
		int degreeLength = (int) t.getDimension(R.styleable.ThermometerView_degreeLength, -1);
		if (degreeLength > 0) {
			mBuilder.setmScaleDegreeLength(degreeLength);
		}
		int mercuryLength = (int) t.getDimension(R.styleable.ThermometerView_mercuryLength, -1);
		if (mercuryLength > 0) {
			mBuilder.setmScaleMercuryLength(mercuryLength);
		}
		int mercuryPadding = (int) t.getDimension(R.styleable.ThermometerView_mercuryPadding, -1);
		if (mercuryPadding > 0) {
			mBuilder.setmScaleMercuryPadding(mercuryPadding);
		}
//		int fillColor = (int) t.getColor(R.styleable.ThermometerView_fillColor, -1);
//		if (fillColor > 0) {
//			mBuilder.setmFillBackgroundColor(fillColor);
//		}
		int degreeColor = (int) t.getColor(R.styleable.ThermometerView_degreeColor, -1);
		if (degreeColor > 0) {
			mBuilder.setmScaleDegreeColor(degreeColor);
		}
		int mercuryDefaultColor = (int) t.getColor(R.styleable.ThermometerView_mercuryDefaultColor, -1);
		if (mercuryDefaultColor > 0) {
			mBuilder.setmScaleMercuryDefaultColor(mercuryDefaultColor);
		}
		int mercuryDarkColor = (int) t.getColor(R.styleable.ThermometerView_mercuryDarkColor, -1);
		if (mercuryDarkColor > 0) {
			mBuilder.setmScaleMercuryDarkColor(mercuryDarkColor);
		}
		int mercuryFillColor = (int) t.getColor(R.styleable.ThermometerView_mercuryFillColor, -1);
		if (mercuryFillColor > 0) {
			mBuilder.setmScaleMercuryFillColor(mercuryFillColor);
		}
	}
	
	Paint createNewPaint() {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStyle(Paint.Style.FILL);
		return paint;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		drawThermometerFrame(canvas);
		drawScaleDegree(canvas);
		drawScaleMercury(canvas);
	}
	
	void drawThermometerFrame(Canvas canvas) {
		top = (getHeight() - mBuilder.getmThermometerHeight()) / 2;
		bottom = (getHeight() + mBuilder.getmThermometerHeight()) / 2;
		left = getWidth() - mBuilder.getmThermometerWidth();
		right = getWidth();
		canvas.drawRoundRect(new RectF(left, top, right, bottom), mBuilder.getmThermometerWidth() / 2, mBuilder.getmThermometerWidth() / 2, mFramePaint);
	}
	
	void drawScaleDegree(Canvas canvas) {
		int valueDiff = mBuilder.getmMaxValue() - mBuilder.getmMinValue();
		heightPerDegree = mBuilder.getmThermometerHeight() / valueDiff;
		if (mDegreeArr == null || mDegreeArr.length == 0) {
			return;
		}
		int unitWidth = 0;
		int x = 0;
		
		for (int i = 0; i < mDegreeArr.length; i++) {
			final int y = bottom - (mDegreeArr[i] - mBuilder.getmMinValue()) * heightPerDegree;
			mDegreePaint.setTextSize(20);
			if (unitWidth == 0) {
				unitWidth = (int) mDegreePaint.measureText(mDegreeUnit);
				x = left - degreeOffset - unitWidth;
			}
			canvas.drawText(mDegreeUnit, x, y, mDegreePaint);
			
			mDegreePaint.setTextSize(24);
			int degreeWidth = (int) mDegreePaint.measureText(mDegreeArr[i] + "");
			canvas.drawText(mDegreeArr[i] + "", x - degreeWidth, y, mDegreePaint);
		}
	}

	//画水银柱
	void drawScaleMercury(Canvas canvas) {
		if (mDegreeArr == null || mDegreeArr.length == 0) {
			return;
		}
		
		drawBottomMercury(canvas);
		
		for (int i = 0; i < mDegreeArr.length; i++) {
			if (i + 1 > mDegreeArr.length - 1) {
				break;
			}
			final int currentValue = mDegreeArr[i]; // 23
			final int nextValue = mDegreeArr[i + 1]; // 28
			final int currentY = bottom - (currentValue - mBuilder.getmMinValue()) * heightPerDegree;
			final int nextY = bottom - (nextValue - mBuilder.getmMinValue()) * heightPerDegree;
			
			if (mCurrentValue > currentValue) { //  需要使用填充色
				if (isDistanceValid()) {
					mMercuryPaint.setColor(mBuilder.getmScaleMercuryDarkColor());
				} else {
					mMercuryPaint.setColor(mBuilder.getmScaleMercuryFillColor());
				}
			} else {
				mMercuryPaint.setColor(mBuilder.getmScaleMercuryDefaultColor());
			}
			
			canvas.drawRoundRect(new RectF(
					left + mBuilder.getmScaleMercuryPadding(),
					nextY + mercuryMargin,
					right - mBuilder.getmScaleMercuryPadding(),
					currentY - mercuryMargin), mercuryRadius, mercuryRadius, mMercuryPaint);
		}
		
		drawTopMercury(canvas);
	}
	
	void drawBottomMercury(Canvas canvas) {
		final int firstElement = mDegreeArr[0];
		final int _top = bottom - (firstElement - mBuilder.getmMinValue()) * heightPerDegree + mercuryMargin - mBuilder.getmScaleMercuryPadding();
		final int _bottom = bottom - mBuilder.getmScaleMercuryPadding();
		final int _left = left + mBuilder.getmScaleMercuryPadding();
		final int _right = right - mBuilder.getmScaleMercuryPadding();
		final int _arcY = _bottom - (_right - _left);
		
		if (mCurrentValue > mBuilder.getmMinValue()) {
			if (isDistanceValid()) {
				mMercuryPaint.setColor(mBuilder.getmScaleMercuryDarkColor());
			} else {
				mMercuryPaint.setColor(mBuilder.getmScaleMercuryFillColor());
			}
		} else {
			mMercuryPaint.setColor(mBuilder.getmScaleMercuryDefaultColor());
		}
		
		Path path = new Path();
		path.moveTo(_left, _top);
		path.lineTo(_left, _arcY);
		path.addArc(new RectF(_left, _arcY, _right, _bottom), 180, -180);
		path.lineTo(_right, _top);
		path.lineTo(_left, _top);
		canvas.drawPath(path, mMercuryPaint);
	}
	
	void drawTopMercury(Canvas canvas) {
		final int lastElement = mDegreeArr[mDegreeArr.length - 1];
		final int _top = top + mBuilder.getmScaleMercuryPadding();
		final int _bottom = bottom - (lastElement - mBuilder.getmMinValue()) * heightPerDegree - mercuryMargin - mBuilder.getmScaleMercuryPadding();
		final int _left = left + mBuilder.getmScaleMercuryPadding();
		final int _right = right - mBuilder.getmScaleMercuryPadding();
		final int _arcY = _top + (_right - _left);
		
		if (mCurrentValue > lastElement) {
			mMercuryPaint.setColor(mBuilder.getmScaleMercuryFillColor());
		} else {
			mMercuryPaint.setColor(mBuilder.getmScaleMercuryDefaultColor());
		}
		
		Path path = new Path();
		path.moveTo(_left, _bottom);
		path.lineTo(_left, _arcY);
		path.addArc(new RectF(_left, _top, _right, _arcY), -180, 180);
		path.lineTo(_right, _bottom);
		path.lineTo(_left, _bottom);
		canvas.drawPath(path, mMercuryPaint);
	}
	
	boolean isDistanceValid() {
		return mCurrentValue >= 23 && mCurrentValue <= 33;
	}
	
	public ThermometerView setBuilder(ThermometerBuilder builder) {
		mBuilder = builder;
		return this;
	}
	
//	public ThermometerView setMaxValue(int max) {
//		mMaxValue = max;
//		return this;
//	}
//
//	public ThermometerView setMinValue(int min) {
//		mMinValue = min;
//		return this;
//	}
	
	public ThermometerView setUnit(String unit) {
		mDegreeUnit = unit;
		return this;
	}
	
	public ThermometerView setDegrees(int[] degrees) {
		mDegreeArr = degrees;
		return this;
	}
	
	public ThermometerView setProgress(int progress) {
		mCurrentValue = progress;
		invalidate();
		return this;
	}

	public void reDraw() {
		invalidate();
	}
	
	public static class ThermometerBuilder {
		// 温度计尺寸
		private int mThermometerWidth;
		private int mThermometerHeight;
		//刻度最值
		private int mMaxValue;
		private int mMinValue;
		// 刻度长度
		private int mScaleDegreeLength;
		// 水银长度
		private int mScaleMercuryLength;
		// 水银间隔
		private int mScaleMercuryPadding;
		// 刻度颜色
		private int mScaleDegreeColor;
		// 水银颜色
		private int mScaleMercuryDefaultColor;
		private int mScaleMercuryFillColor;
		private int mScaleMercuryDarkColor;
		// 填充背景色
		private int mFillBackgroundColor;
		
		public int getmThermometerWidth() {
			return mThermometerWidth;
		}
		
		public ThermometerBuilder setmThermometerWidth(int mThermometerWidth) {
			this.mThermometerWidth = mThermometerWidth;
			return this;
		}
		
		public int getmThermometerHeight() {
			return mThermometerHeight;
		}
		
		public ThermometerBuilder setmThermometerHeight(int mThermometerHeight) {
			this.mThermometerHeight = mThermometerHeight;
			return this;
		}

		public int getmMaxValue() {
			return mMaxValue;
		}

		public ThermometerBuilder setmMaxValue(int mMaxValue) {
			this.mMaxValue = mMaxValue;
			return this;
		}

		public int getmMinValue() {
			return mMinValue;
		}

		public ThermometerBuilder setmMinValue(int mMinValue) {
			this.mMinValue = mMinValue;
			return this;
		}
		
		public int getmScaleDegreeLength() {
			return mScaleDegreeLength;
		}
		
		public ThermometerBuilder setmScaleDegreeLength(int mScaleDegreeLength) {
			this.mScaleDegreeLength = mScaleDegreeLength;
			return this;
		}
		
		public int getmScaleMercuryLength() {
			return mScaleMercuryLength;
		}
		
		public ThermometerBuilder setmScaleMercuryLength(int mScaleMercuryLength) {
			this.mScaleMercuryLength = mScaleMercuryLength;
			return this;
		}
		
		public int getmScaleMercuryPadding() {
			return mScaleMercuryPadding;
		}
		
		public ThermometerBuilder setmScaleMercuryPadding(int mScaleMercuryPadding) {
			this.mScaleMercuryPadding = mScaleMercuryPadding;
			return this;
		}
		
		public int getmScaleDegreeColor() {
			return mScaleDegreeColor;
		}
		
		public ThermometerBuilder setmScaleDegreeColor(int mScaleDegreeColor) {
			this.mScaleDegreeColor = mScaleDegreeColor;
			return this;
		}
		
		public int getmScaleMercuryDefaultColor() {
			return mScaleMercuryDefaultColor;
		}
		
		public ThermometerBuilder setmScaleMercuryDefaultColor(int mScaleMercuryDefaultColor) {
			this.mScaleMercuryDefaultColor = mScaleMercuryDefaultColor;
			return this;
		}
		
		public int getmScaleMercuryFillColor() {
			return mScaleMercuryFillColor;
		}
		
		public ThermometerBuilder setmScaleMercuryFillColor(int mScaleMercuryFillColor) {
			this.mScaleMercuryFillColor = mScaleMercuryFillColor;
			return this;
		}
		
		public int getmScaleMercuryDarkColor() {
			return mScaleMercuryDarkColor;
		}
		
		public ThermometerBuilder setmScaleMercuryDarkColor(int mScaleMercuryDarkColor) {
			this.mScaleMercuryDarkColor = mScaleMercuryDarkColor;
			return this;
		}
		
		public int getmFillBackgroundColor() {
			return mFillBackgroundColor;
		}
		
		public ThermometerBuilder setmFillBackgroundColor(int mFillBackgroundColor) {
			this.mFillBackgroundColor = mFillBackgroundColor;
			return this;
		}
		
		public ThermometerView createView(Context context) {
			return new ThermometerView(context, this);
		}
	}
}
