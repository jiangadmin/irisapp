package com.irisking.scanner.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2017/10/17.
 */

public class PlayingRelativeLayout extends RelativeLayout {
	
	private int centerX;
	private int width;
	private int height;
	
	private int minSpreadWidth = 50; // 最小扩散宽度，初次扩散的最小波纹宽度
	private int speedPerFrame = 70; // 每一帧扩散 的速度，60ms{SPREAD_INTERVAL}为一帧
	private int radius = 20; // 波纹边角弧度
	
	static final int SPREAD_INTERVAL = 60; // 动画帧
	
	private boolean isPlaying;
	private boolean resetFlag;
	private boolean playComplete;
	
	static final int STATE_PLAYING = 1;
	static final int STATE_PLAYING_FINISH = 2;
	static final int STATE_RESET = 3;
	
	private int mState;
	
	private Paint paint;
	private int currentFrame; // 当前帧
	private int currentSpreadWidth; // 当前波纹宽度
	private OnAnimationUpdateListener mUpdateListener;

	Handler mFrameHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			currentSpreadWidth = minSpreadWidth + speedPerFrame * currentFrame;
			if (currentSpreadWidth >= width) {
				mState = STATE_PLAYING_FINISH;
				if (mUpdateListener != null) {
					mUpdateListener.onAnimationFinish();
				}
				invalidate();
				return;
			}
			if (mUpdateListener != null) {
				float progressRatio = ((float) currentFrame - minSpreadWidth) / (width - minSpreadWidth);
				mUpdateListener.onAnimationProgress((int) (progressRatio * 100));
			}
			invalidate();
		}
	};
	
	public PlayingRelativeLayout(Context context) {
		this(context, null);
	}
	
	public PlayingRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setColor(Color.parseColor("#33dddddd"));
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStyle(Paint.Style.FILL);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if (centerX == 0 || height == 0 || width == 0) {
			centerX = getWidth() / 2;
			height = getHeight();
			width = getWidth();
			radius = height / 2;
		}
		
		canvas.save();
		
		// 背景重置
		if (mState == STATE_RESET) {
			canvas.drawColor(Color.WHITE);
			super.onDraw(canvas);
		} else if (mState == STATE_PLAYING_FINISH) {
			// 波形扩散完毕
			super.onDraw(canvas);
			canvas.drawColor(Color.parseColor("#dddddd"));
		} else if (mState == STATE_PLAYING) {
			// 波形扩散
			super.onDraw(canvas);
			drawWaving(canvas);
		}
		
		canvas.restore();
		
	}
	
	void drawWaving(Canvas canvas) {
		canvas.drawRoundRect(
				new RectF(
						centerX - currentSpreadWidth / 2,
						0,
						centerX + currentSpreadWidth / 2,
						height),
				radius, radius, paint);
		
		currentFrame++;
		mFrameHandler.sendEmptyMessageDelayed(0, SPREAD_INTERVAL);
	}
	
	public void play() {
		mState = STATE_PLAYING;
		mFrameHandler.sendEmptyMessage(0);
	}
	
	public void reset() {
		mState = STATE_RESET;
		invalidate();
	}
	
	public void setAnimationUpdateListener(OnAnimationUpdateListener listener) {
		mUpdateListener = listener;
	}
	
	public interface OnAnimationUpdateListener {
		void onAnimationProgress(int progress);
		
		void onAnimationFinish();
	}
}
