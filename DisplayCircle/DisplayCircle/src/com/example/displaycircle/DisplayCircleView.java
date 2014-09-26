package com.example.displaycircle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
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
	 * The scale of the base inner circle width */
	private float baseInCircleWidthScale = 0.03f;
	
	/*
	 * The scale of the base circle width */
	private float baseCircleWidthScale = 0.02f;
	
	/*
	 * The scale of the spin arc */
	private float spinArcWidthScale = 0.01f;
	

	public DisplayCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@SuppressLint("Recycle")
	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.displayCircleView, 0, 0);
		
		unit = typedArray.getString(R.styleable.displayCircleView_unit);
		counts = typedArray.getFloat(R.styleable.displayCircleView_counts, 119);
		scores = typedArray.getFloat(R.styleable.displayCircleView_scores, 90);
		
		typedArray.recycle();
	}
	
	private void setPaintStrokeWidth(int width, int height) {
		baseCirclePaint.setStrokeWidth(width * baseCircleWidthScale);
		baseInCirclePaint.setStrokeWidth(width * baseInCircleWidthScale);
		spinArcPaint.setStrokeWidth(width * spinArcWidthScale);
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
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		int width = getWidth();
		int height = getHeight();
		
		float centerX = width / 2;
		float centerY = height / 2;
		
		
	}

}
