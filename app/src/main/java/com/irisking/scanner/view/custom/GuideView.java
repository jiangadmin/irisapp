package com.irisking.scanner.view.custom;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.irisking.irisapp.R;

/**
 * Created by tony on 2017/10/12.
 */

public class GuideView extends LinearLayout {
	
	public GuideView(Context context) {
		this(context, null);
	}
	
	public GuideView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		setOrientation(VERTICAL);
		View targetView = LayoutInflater.from(context).inflate(R.layout.default_user_guide_info, null);
		addView(targetView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
	}
}
