package com.irisking.scanner.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by tony on 2017/10/12.
 */

public class ProgressView extends View {
	
	int progress;
	Paint paint;
	
	int offset = 5;
	
	public ProgressView(Context context) {
		this(context, null);
	}
	
	public ProgressView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStrokeWidth(8);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.WHITE);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (progress > 0) {
				final int centerX = getWidth() / 2;
				final int centerY = getHeight() / 2;
				int radius = getWidth() > getHeight() ? getHeight() / 2 - offset : getWidth() / 2 - offset;
				canvas.drawArc(
						new RectF(centerX - radius, centerY - radius, centerX + radius, centerY + radius),
					0, progress / 100.0f * 360.0f, false, paint);
		}
	}
	
	public void setProgress(int progress) {
		this.progress = progress;
		invalidate();
	}
}
