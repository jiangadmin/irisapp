package com.irisking.scanner.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * Created by Administrator on 2017/11/9.
 */

public class CustomRadioButton extends RadioButton {
    public CustomRadioButton(Context context) {
        super(context);
    }

    public CustomRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        Drawable[] drawables = getCompoundDrawables();
//        Drawable drawable = drawables[0];
//        int gravity = getGravity();
//        int left = 0;
//        if (gravity == Gravity.CENTER) {
////            left = ((int) (getWidth() - drawable.getIntrinsicWidth() - getPaint().measureText(getText().toString()))
////                    / 2);
//            left = ((int) (getWidth() / 2 - getPaint().measureText(getText().toString())));
//        }
//        drawable.setBounds(left, 0, left + drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());

        //获取设置的图片
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {
            //第一个是left
            Drawable drawableLeft = drawables[0];
            if (drawableLeft != null) {
                //获取文字的宽度
                float textWidth = getPaint().measureText(getText().toString());
                int drawablePadding = getCompoundDrawablePadding();
                int drawableWidth = 0;
                drawableWidth = drawableLeft.getIntrinsicWidth();
                float bodyWidth = textWidth + drawableWidth + drawablePadding;
                canvas.translate((getWidth() - bodyWidth) / 2, 0);
            }
        }
        super.onDraw(canvas);
    }
}
