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
 * Created by Administrator on 2017/10/23.
 */

public class CustomBottomGuideView extends LinearLayout {

    public CustomBottomGuideView(Context context) {
        this(context, null);
    }

    public CustomBottomGuideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        View targetView = LayoutInflater.from(context).inflate(R.layout.item_bottom_user_guide_info, null);
        addView(targetView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
}
