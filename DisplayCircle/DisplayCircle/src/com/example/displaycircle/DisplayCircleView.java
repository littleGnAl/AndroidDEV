package com.example.displaycircle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
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
	}

}
