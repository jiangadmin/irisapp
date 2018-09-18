package com.irisking.scanner.view.custom;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.irisking.irisapp.R;

/**
 * Created by tony on 2017/10/12.
 */

public class EyeScannerView extends LinearLayout {
	
	ImageView mScannerView;
//	ProgressView mProgressView;
//	RoundProgressBar mRoundProgressBar;
	
	ObjectAnimator animator;
	
	public EyeScannerView(@NonNull Context context) {
		super(context);
		addContentView();
	}
	
	public EyeScannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		addContentView();
	}
	
	void addContentView() {
		View childView = LayoutInflater.from(getContext()).inflate(R.layout.view_scanner, null);
		mScannerView = (ImageView) childView.findViewById(R.id.scanner);
//		mProgressView = childView.findViewById(R.id.progress);
//		mRoundProgressBar =childView.findViewById(R.id.roundProgress);
		addView(childView);
	}
	
	public void play() {
		animator = ObjectAnimator.ofFloat(mScannerView, "rotation", 0.0f, 359.0f);
		animator.setRepeatCount(-1);
		animator.setDuration(2400);
		animator.setStartDelay(0);
		animator.setInterpolator(new LinearInterpolator());
		animator.start();
	}
	
	public void pause() {
		if (animator != null) {
			animator.pause();
		}
	}
	
	public void setProgress(int progress) {
//		mProgressView.setProgress(progress);
	}
}
