package com.irisking.scanner.view.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.irisking.irisapp.R;

/**
 * Created by tony on 2017/10/20.
 */

public class CustomRightThermometerView extends View {

    private Paint mFramePaint;
    private Paint mDegreePaint;
    private Paint mMercuryPaint;

    private int top;
    private int left;
    private int bottom;
    private int right;

    // 所有刻度值（标注于左侧的刻度值）
    private int[] mDegreeArr = new int[]{18, 23, 28, 33, 38};
    // 刻度单位
    private String mDegreeUnit = "cm";
    // 刻度到温度计的偏移量
    private int degreeOffset;
    // 水银模块间距
    private int mercuryMargin;
    // 水银模块弧度
    private int mercuryRadius;
    // 当前值
    private int mCurrentValue;
    // 温度计尺寸
    private int mThermometerWidth;
    private int mThermometerHeight;
    // 每一个刻度的高度
    private int mPerDegreeHeight;
    //刻度最大值与最小值
    private int mMaxValue;
    private int mMinValue;
    // 刻度长度
//    private int mScaleDegreeLength;
    // 水银长度
//    private int mScaleMercuryLength;
    // 水银间隔
    private int mScaleMercuryPadding;
    // 刻度颜色
    private int mScaleDegreeColor;
    // 水银颜色
    private int mScaleMercuryDefaultColor;
    private int mScaleMercuryDarkColor;
    private int mScaleMercuryFillColor;
    // 填充背景色
    private int mFillBackgroundColor;

    //以分辨率1920*1080为基准
    private int defaultWidth = 1080;
    private int defaultHeight = 1920;

    //在分辨率1920*1080下进度条的宽高
    private int thermometerDefaultWidth = 20;
    private int thermometerDefaultHeight = 510;

    float optWidth;
    float optHeight;

    public CustomRightThermometerView(Context context) {
        this(context, null);
    }

    public CustomRightThermometerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context, attrs);
        setupPaints();
    }

    void parseAttributes(Context context, AttributeSet attrs) {
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.ThermometerView);

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels; // 获取屏幕的宽
        int screenHeight = metrics.heightPixels;// 获取屏幕的高

//        Log.e("tony", "parseAttributes screenWidth：" + screenWidth + " screenHeight: " + screenHeight);

        optWidth = (float) screenWidth / defaultWidth;
        int thermometerWidth = (int) (thermometerDefaultWidth * optWidth);//设置适配宽度
        optHeight = (float) screenHeight / defaultHeight;
        int thermometerHeight = (int) (thermometerDefaultHeight * optHeight);//设置适配高度
//        Log.e("tony", "parseAttributes optWidth：" + optWidth + " optHeight: " + optHeight);
//        Log.e("tony", "parseAttributes thermometerWidth：" + thermometerWidth + " thermometerHeight: " + thermometerHeight);

        mThermometerWidth = mTypedArray.getInt(R.styleable.ThermometerView_thermometerWidth, thermometerWidth);
        mThermometerHeight = mTypedArray.getInt(R.styleable.ThermometerView_thermometerHeight, thermometerHeight);
        mMinValue = mTypedArray.getInt(R.styleable.ThermometerView_minValue, 13);
        mMaxValue = mTypedArray.getInt(R.styleable.ThermometerView_maxValue, 43);
//        mScaleDegreeLength = mTypedArray.getInt(R.styleable.ThermometerView_degreeLength, 5);
//        mScaleMercuryLength = mTypedArray.getInt(R.styleable.ThermometerView_mercuryLength, 60);
        degreeOffset = mTypedArray.getInt(R.styleable.ThermometerView_degreeOffset, 12);
        mercuryMargin = mTypedArray.getInt(R.styleable.ThermometerView_mercuryMargin, 8);
        mercuryRadius = mTypedArray.getInt(R.styleable.ThermometerView_mercuryRadius, 6);
        mScaleMercuryPadding = mTypedArray.getInt(R.styleable.ThermometerView_mercuryPadding, 3);
        mScaleDegreeColor = mTypedArray.getColor(R.styleable.ThermometerView_degreeColor, Color.parseColor("#ff299bd7"));
        mScaleMercuryDefaultColor = mTypedArray.getColor(R.styleable.ThermometerView_mercuryDefaultColor, Color.parseColor("#22299bd7"));
        mScaleMercuryDarkColor = mTypedArray.getColor(R.styleable.ThermometerView_mercuryDarkColor, Color.parseColor("#ff299bd7"));
        mScaleMercuryFillColor = mTypedArray.getColor(R.styleable.ThermometerView_mercuryFillColor, Color.parseColor("#ffff0000"));
        mFillBackgroundColor = mTypedArray.getColor(R.styleable.ThermometerView_fillBackgroundColor, Color.parseColor("#ffffff"));
    }

    void setupPaints() {
        mFramePaint = createNewPaint();
        mDegreePaint = createNewPaint();
        mMercuryPaint = createNewPaint();

        mFramePaint.setColor(mFillBackgroundColor);
        mDegreePaint.setColor(mScaleDegreeColor);
        mMercuryPaint.setColor(mScaleMercuryDefaultColor);
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
        top = (getHeight() - mThermometerHeight) / 2;
        bottom = (getHeight() + mThermometerHeight) / 2;
        left = getWidth() - mThermometerWidth;
        right = getWidth();
        canvas.drawRoundRect(new RectF(left, top, right, bottom), mThermometerWidth / 2, mThermometerWidth / 2, mFramePaint);
    }

    void drawScaleDegree(Canvas canvas) {
        int valueDiff = mMaxValue - mMinValue;
        mPerDegreeHeight = mThermometerHeight / valueDiff;
        if (mDegreeArr == null || mDegreeArr.length == 0) {
            return;
        }
        int unitWidth = 0;
        int x = 0;

        for (int i = 0; i < mDegreeArr.length; i++) {
            int y = bottom - (mDegreeArr[i] - mMinValue) * mPerDegreeHeight;
            mDegreePaint.setTextSize(29 * optWidth);
            if (unitWidth == 0) {
                unitWidth = (int) mDegreePaint.measureText(mDegreeUnit);
                unitWidth = (int) mDegreePaint.measureText("过近");
                x = left - degreeOffset - unitWidth;
            }

            if(i == 0){
                canvas.drawText("过近", x, y, mDegreePaint);
            }else if(i == 2){
                canvas.drawText("合适", x, y, mDegreePaint);
            }else if(i == 4){
                canvas.drawText("过远", x, y, mDegreePaint);
            }

//            canvas.drawText(mDegreeUnit, x, y, mDegreePaint);

//            mDegreePaint.setTextSize(24 * optWidth);
//            int degreeWidth = (int) mDegreePaint.measureText(mDegreeArr[i] + "");
//            canvas.drawText(mDegreeArr[i] + "", x - degreeWidth, y, mDegreePaint);
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
            int currentValue = mDegreeArr[i]; // 23
            int nextValue = mDegreeArr[i + 1]; // 28
            int currentY = bottom - (currentValue - mMinValue) * mPerDegreeHeight;
            int nextY = bottom - (nextValue - mMinValue) * mPerDegreeHeight;

            if (mCurrentValue > currentValue) { //  需要使用填充色
                if (isDistanceValid()) {
                    mMercuryPaint.setColor(mScaleMercuryDarkColor);
                } else {
                    mMercuryPaint.setColor(mScaleMercuryFillColor);
                }
            } else {
                mMercuryPaint.setColor(mScaleMercuryDefaultColor);
            }

            canvas.drawRoundRect(new RectF(
                    left + mScaleMercuryPadding,
                    nextY + mercuryMargin,
                    right - mScaleMercuryPadding,
                    currentY - mercuryMargin), mercuryRadius, mercuryRadius, mMercuryPaint);
        }

        drawTopMercury(canvas);
    }

    void drawBottomMercury(Canvas canvas) {
        int firstElement = mDegreeArr[0];
        int _top = bottom - (firstElement - mMinValue) * mPerDegreeHeight + mercuryMargin - mScaleMercuryPadding;
        int _bottom = bottom - mScaleMercuryPadding;
        int _left = left + mScaleMercuryPadding;
        int _right = right - mScaleMercuryPadding;
        int _arcY = _bottom - (_right - _left);

        if (mCurrentValue > mMinValue) {
            if (isDistanceValid()) {
                mMercuryPaint.setColor(mScaleMercuryDarkColor);
            } else {
                mMercuryPaint.setColor(mScaleMercuryFillColor);
            }
        } else {
            mMercuryPaint.setColor(mScaleMercuryDefaultColor);
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
        int lastElement = mDegreeArr[mDegreeArr.length - 1];
        int _top = top + mScaleMercuryPadding;
        int _bottom = bottom - (lastElement - mMinValue) * mPerDegreeHeight - mercuryMargin - mScaleMercuryPadding;
        int _left = left + mScaleMercuryPadding;
        int _right = right - mScaleMercuryPadding;
        int _arcY = _top + (_right - _left);

        if (mCurrentValue > lastElement) {
            mMercuryPaint.setColor(mScaleMercuryFillColor);
        } else {
            mMercuryPaint.setColor(mScaleMercuryDefaultColor);
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

    public void setProgress(int progress) {
        mCurrentValue = progress;
        invalidate();
    }

//    public int getmThermometerWidth() {
//        return mThermometerWidth;
//    }
//
//    public void setmThermometerWidth(int mThermometerWidth) {
//        this.mThermometerWidth = mThermometerWidth;
//    }
//
//    public int getmThermometerHeight() {
//        return mThermometerHeight;
//    }
//
//    public void setmThermometerHeight(int mThermometerHeight) {
//        this.mThermometerHeight = mThermometerHeight;
//    }
//
//    public int getmPerDegreeHeight() {
//        return mPerDegreeHeight;
//    }
//
//    public void setmPerDegreeHeight(int mPerDegreeHeight) {
//        this.mPerDegreeHeight = mPerDegreeHeight;
//    }
//
//    public int getmMaxValue() {
//        return mMaxValue;
//    }
//
//    public void setmMaxValue(int mMaxValue) {
//        this.mMaxValue = mMaxValue;
//    }
//
//    public int getmMinValue() {
//        return mMinValue;
//    }
//
//    public void setmMinValue(int mMinValue) {
//        this.mMinValue = mMinValue;
//    }
//
//    public int getmScaleDegreeLength() {
//        return mScaleDegreeLength;
//    }
//
//    public void setmScaleDegreeLength(int mScaleDegreeLength) {
//        this.mScaleDegreeLength = mScaleDegreeLength;
//    }
//
//    public int getmScaleMercuryLength() {
//        return mScaleMercuryLength;
//    }
//
//    public void setmScaleMercuryLength(int mScaleMercuryLength) {
//        this.mScaleMercuryLength = mScaleMercuryLength;
//    }
//
//    public int getmScaleMercuryPadding() {
//        return mScaleMercuryPadding;
//    }
//
//    public void setmScaleMercuryPadding(int mScaleMercuryPadding) {
//        this.mScaleMercuryPadding = mScaleMercuryPadding;
//    }
//
//    public int getmScaleDegreeColor() {
//        return mScaleDegreeColor;
//    }
//
//    public void setmScaleDegreeColor(int mScaleDegreeColor) {
//        this.mScaleDegreeColor = mScaleDegreeColor;
//    }
//
//    public int getmScaleMercuryDefaultColor() {
//        return mScaleMercuryDefaultColor;
//    }
//
//    public void setmScaleMercuryDefaultColor(int mScaleMercuryDefaultColor) {
//        this.mScaleMercuryDefaultColor = mScaleMercuryDefaultColor;
//    }
//
//    public int getmScaleMercuryFillColor() {
//        return mScaleMercuryFillColor;
//    }
//
//    public void setmScaleMercuryFillColor(int mScaleMercuryFillColor) {
//        this.mScaleMercuryFillColor = mScaleMercuryFillColor;
//    }
//
//    public int getmScaleMercuryDarkColor() {
//        return mScaleMercuryDarkColor;
//    }
//
//    public void setmScaleMercuryDarkColor(int mScaleMercuryDarkColor) {
//        this.mScaleMercuryDarkColor = mScaleMercuryDarkColor;
//    }
//
//    public int getmFillBackgroundColor() {
//        return mFillBackgroundColor;
//    }
//
//    public void setmFillBackgroundColor(int mFillBackgroundColor) {
//        this.mFillBackgroundColor = mFillBackgroundColor;
//    }
}
