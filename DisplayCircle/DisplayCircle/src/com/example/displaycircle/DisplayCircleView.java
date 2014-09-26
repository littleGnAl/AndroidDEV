package com.example.displaycircle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class DisplayCircleView extends View {
	
	/*
	 * Unit of the weight */
	private String unit;
	
	/* 
	 * The counts of the weight */
	private float counts;
	
	/*
	 * The scores of the weight */
	private float scores;
	
	/*
	 * The paint of the base circle */
	private Paint baseCirclePaint = null;
	
	/*
	 * The paint of the inner base circle */
	private Paint baseInCirclePaint = null;
	
	/*
	 * The paint of the spin arc */
	private Paint spinArcPaint = null;
	
	/*
	 * The paint of the text */
	private Paint textPaint = null;
	
	/*
	 * The scale of the base inner circle width */
	private float baseInCircleWidthScale = 0.04f;
	
	/*
	 * The scale of the base circle width */
	private float baseCircleWidthScale = 0.04f;
	
	/*
	 * The scale of the circle width */
	private float circleWidthScale = 0.04f;
	
	/*
	 * The scale of the spin arc */
	private float spinArcWidthScale = 0.008f;
	
	/*
	 * The stroke width of the base inner circle */
	private float baseInCircleWidth;
	
	/*
	 * The stroke width of the base circle */
	private float baseCircleWidth;
	
	/*
	 * The width of the circles*/
	private float circleWidth;
	
	/*
	 * The stroke width of the spin arc */
	private float spinArcWidth;
	
	/*
	 * The progress of the spin arc */
	private float mProgress = 0;
	
	/*
	 * Weather the spin arc spin to the end */
	private boolean isSpinToEnd;
	
	/*
	 * The text size on the middle position */
	private float midTextSize;
	
	/*
	 * The text size on the top or bottom position */
	private float topBottomTextSize; 
	
	/*
	 * The scale of the text size on the middle position */
	private float textSizeScale = 1.7f;
	
	/*
	 * The destination of the slide text */
	private float textDesPos;
	
	/*
	 * True when begin to slide */
	private boolean isBeginSlide;
	
	private int textPosChange;
	
	
	

	public DisplayCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initAttrs(context, attrs);
		initVariable();
	}
	
	@SuppressLint("Recycle")
	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.displayCircleView, 0, 0);
		
		unit = typedArray.getString(R.styleable.displayCircleView_unit);
		counts = typedArray.getFloat(R.styleable.displayCircleView_counts, 119);
		scores = typedArray.getFloat(R.styleable.displayCircleView_scores, 90);
		
		typedArray.recycle();
	}
	
	private void setPaintStrokeWidth(int width) {
		circleWidth = width * circleWidthScale;
		spinArcWidth = width * spinArcWidthScale;
		
		baseCirclePaint.setStrokeWidth(circleWidth);
		baseInCirclePaint.setStrokeWidth(circleWidth);
		spinArcPaint.setStrokeWidth(spinArcWidth);
	}
	
	private void initVariable() {
		baseCirclePaint = new Paint();
		baseCirclePaint.setAntiAlias(true);
		baseCirclePaint.setARGB(150, 255, 255, 255);
		baseCirclePaint.setStyle(Style.STROKE);
		
		baseInCirclePaint = new Paint();
		baseInCirclePaint.setAntiAlias(true);
		baseInCirclePaint.setColor(Color.WHITE);
		baseInCirclePaint.setStyle(Style.STROKE);
		
		spinArcPaint = new Paint();
		spinArcPaint.setAntiAlias(true);
		spinArcPaint.setColor(Color.WHITE);
		spinArcPaint.setStyle(Style.STROKE);
		spinArcPaint.setStrokeCap(Cap.ROUND);
		
		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(Color.WHITE);
		textPaint.setStyle(Style.FILL);
		
		isSpinToEnd = false;
		midTextSize = 300;
		topBottomTextSize = midTextSize / 2;
		isBeginSlide = false;
		textPosChange = 1;
	}
	
	private RectF setRectF(float centerX, float centerY, float radius) {
		RectF oval = new RectF();
		oval.left = centerX - radius - circleWidth / 2 + spinArcWidth / 2;
		oval.top = centerY - radius - circleWidth / 2 + spinArcWidth / 2;
		oval.right = centerX + radius + circleWidth / 2 - spinArcWidth / 2;
		oval.bottom = centerY + radius + circleWidth / 2 - spinArcWidth / 2;
		
//		oval.left = centerX - radius;
//		oval.top = centerY - radius;
//		oval.right = centerX + radius;
//		oval.bottom = centerY + radius;
		
		return oval;
	}
	
	private void setTextSize(float deltaWidth) {
	
		midTextSize = deltaWidth * textSizeScale;
		textPaint.setTextSize(midTextSize);

		topBottomTextSize = midTextSize / 2;
		textPaint.setTextSize(topBottomTextSize);
		//textPaint.setStrokeWidth(10);
	}
	
	private void drawLines(float deltaWidth, int width, int height, Canvas canvas, float radius, float textDesPos) {
		float mLeft = width / 2 - radius + circleWidth;
		float mTop = height / 2 - radius + circleWidth;
		float mRight = width / 2 + radius - circleWidth;
		float mBottom = height / 2 + radius - circleWidth;
		
		Paint linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setColor(Color.WHITE);
		linePaint.setStyle(Style.STROKE);
		linePaint.setStrokeWidth(5);
		
		canvas.drawLine(mLeft, mTop, mRight, mTop, linePaint);
		canvas.drawLine(mLeft, mTop + deltaWidth, mRight, mTop + deltaWidth, linePaint);
		canvas.drawLine(mLeft, mTop + 2 * deltaWidth, mRight, mTop + 2 * deltaWidth, linePaint);
		canvas.drawLine(mLeft, mTop + 3 * deltaWidth, mRight, mTop + 3 * deltaWidth, linePaint);
		
		Log.i("AAA", "textDesPos: " + textDesPos);
		canvas.drawLine(mLeft, textDesPos, mRight, textDesPos, linePaint);
		//canvas.drawLine(mLeft, mBottom, mRight, mBottom, linePaint);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int width = getWidth();
		int height = getHeight();
		
		setPaintStrokeWidth(width);
		
		float centerX = width / 2;
		float centerY = height / 2;
		
		float radius = centerX - circleWidth;
		
		
		
		canvas.drawCircle(centerX, centerY, radius, baseCirclePaint);
		canvas.drawCircle(centerX, centerY, radius - circleWidth, baseInCirclePaint);
		
		RectF oval = setRectF(centerX, centerY, radius);
		
		
		
		if (!isSpinToEnd) {
			canvas.drawArc(oval, -90, (mProgress / 100) * 360, false, spinArcPaint);			
		} else {
			float deltaWidth = (radius - circleWidth) * 2 / 3;
			
			setTextSize(deltaWidth);
			
			String countsString = String.valueOf(counts);
			
			float textLength = textPaint.measureText(countsString, 0,
					countsString.length());
			
			FontMetrics fm = textPaint.getFontMetrics();
			float textHeight = (float) Math.ceil(fm.descent - fm.ascent);
			
			textDesPos = (height / 2 - radius + circleWidth) + deltaWidth / 2 + textHeight / 4;
			
			Log.i("AAA", "deltaWidth: " + deltaWidth);
			Log.i("AAA", "midTextSize: " + midTextSize);
			
			float textPosX = width / 2 - textLength / 2;
			float textPosY = height / 2 + textHeight / 4;
			
			drawLines(deltaWidth, width, height, canvas, radius, textDesPos);
			
			if (!isBeginSlide) {
				canvas.drawText(countsString, textPosX, textPosY, textPaint);
				
			}
			else {
				canvas.drawText(countsString, textPosX, textPosY, textPaint);
			}
		}
		//canvas.drawArc(oval, -90, 70, false, spinArcPaint);
		
		
		
		
	}
	
	/*
	 * Set the progress of the spin arc */
	public void setProgress(int progress) {
		mProgress = progress;
		postInvalidate();
	}
	
	/*
	 * Set true when the spin arc spin to the end */
	public void setSpinToEnd(boolean spinToEnd) {
		isSpinToEnd = spinToEnd;
	}
	
	/*
	 * Set counts in the circle */
	public void setCounts(float counts) {
		this.counts = counts;
		postInvalidate();
	}
	
	/*
	 * Get the counts of the circle */
	public float getCounts() {
		return counts;
	}
	
	/*
	 * Get the destination of the slide text */
	public float getTextDes() {
		return textDesPos;
	}
	
	public void setTextPosChange(int change) {
		textPosChange = change;
	}

}
