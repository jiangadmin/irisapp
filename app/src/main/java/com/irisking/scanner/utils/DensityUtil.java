package com.irisking.scanner.utils;

import android.content.Context;
import android.util.Log;

/**
 * Created by Administrator on 2017/10/12.
 */

public class DensityUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        Log.e("tony", "dip2px scale " + scale + " pxValue " + dpValue);
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static float px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        Log.e("tony", "px2dip scale " + scale + " pxValue " + pxValue);
        return (pxValue / scale + 0.5f);
    }
}
