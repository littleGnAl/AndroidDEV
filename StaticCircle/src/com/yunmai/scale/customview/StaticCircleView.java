package com.yunmai.scale.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class StaticCircleView extends View{
	private Paint exCirclePaint = null;
	private Paint inCirclePaint = null;
	
	private float circleWidthScale = 0.04f;
	private float exCircleWidth;
	
	private void initVariable() {
		exCirclePaint = setPaint(150, Color.WHITE);
		inCirclePaint = setPaint(255, Color.WHITE);
		
		exCircleWidth = 0;
	}
	
	private Paint setPaint(int alpha, int color) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		paint.setAlpha(alpha);
		paint.setStyle(Style.STROKE);
		
		return paint;
	}
	
	private void setCircleStrokeWidth(int width) {
		exCircleWidth = width * circleWidthScale;
		
		exCirclePaint.setStrokeWidth(width * circleWidthScale);
		inCirclePaint.setStrokeWidth(width * circleWidthScale);
	}
	

	public StaticCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initVariable();
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int width = getWidth();
		int height = getHeight();
		
		//width = height = (width > height) ? height : width;
		Log.i("AAA", "width / height: " + width + " " + height);
		
		setCircleStrokeWidth(width);
		
		float centerX = width / 2;
		float centerY = height / 2;
		
		float radius = centerX - exCircleWidth;
		
		canvas.drawCircle(centerX, centerY, radius, exCirclePaint);
		canvas.drawCircle(centerX, centerY, radius - exCircleWidth, inCirclePaint);
		
	}

}
