package com.cauchy.demo.circleanimation;

import java.lang.ref.WeakReference;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

public class CustomCircle extends View {
	private Paint inRingPaint = null;
	private Paint exRingPaint = null;
	private int sweepAngle = 0;
	
//	private MyHandler mHandler = null;
//
//	private static class MyHandler extends Handler {
//		private WeakReference<WaterWaveProgress> mWeakRef = null;
//
//		private int refreshPeriod = 100;
//
//		public MyHandler(WaterWaveProgress host) {
//			mWeakRef = new WeakReference<WaterWaveProgress>(host);
//		}
//	
//		@Override
//		public void handleMessage(Message msg) {
//			super.handleMessage(msg);
//			if (mWeakRef.get() != null) {
//				mWeakRef.get().invalidate();
//				sendEmptyMessageDelayed(0, refreshPeriod);
//			}
//		}
//	}

	private MyHandler mHandler = null;
	
	private static class MyHandler extends Handler {
		private WeakReference<CustomCircle> mWeakReference = null;
		
		private int refreshPeriod = 100;
		
		public MyHandler(CustomCircle host) {
			mWeakReference = new WeakReference<CustomCircle>(host);
		}
		
		@Override
		public void handleMessage(Message msg){
			super.handleMessage(msg);
			if (mWeakReference.get() != null) {
				mWeakReference.get().invalidate();
				sendEmptyMessageDelayed(0, refreshPeriod);			
			}
		}
	}
	
	private void init(Context context){
		//initial inRingPaint
		inRingPaint = new Paint();
		inRingPaint.setAntiAlias(true);
		inRingPaint.setColor(Color.GRAY);
		inRingPaint.setStyle(Style.STROKE);
		inRingPaint.setStrokeWidth(10);
		
		// initial exRingPaint
		exRingPaint = new Paint();
		exRingPaint.setAntiAlias(true);
		exRingPaint.setColor(Color.MAGENTA);
		exRingPaint.setStyle(Style.STROKE);
		exRingPaint.setStrokeWidth(20);
		
		mHandler = new MyHandler(this);
	}
	
	public CustomCircle(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public CustomCircle(Context context) {
		super(context);
		init(context);
	}
	
//	@Override
//	protected void onAttachedToWindow() {
//		// Set up the paints and bounds before draw
//		super.onAttachedToWindow();
//		// Check the view's size. if set the view in xml as wrap_content. The
//		// should give a default vaule
//		if (this.getLayoutParams().width < 0
//				|| this.getLayoutParams().height < 0) {
//			this.getLayoutParams().width = this.getLayoutParams().height = 100;
//		}
//
//		setUpBarLength(this.getLayoutParams().width,
//				this.getLayoutParams().height);
//		setUpBounds();
//		setUpPaints();
//		invalidate();
//	}
	
//	@Override
//	protected void onAttachedToWindow() {
//		super.onAttachedToWindow();
//	} 
	
	@SuppressLint("DrawAllocation")
	@Override 
	protected void onDraw(Canvas canvas){
		int width = getWidth();
		int height = getHeight();
		
		canvas.drawCircle(width / 2, (height - 20) / 2, (width - 20) / 2, inRingPaint);
		
		RectF ovalF = new RectF();
		float exLeft = width / 2 - (width - 20) / 2;
		float exTop = (height - 20) / 2 - (width - 20) / 2;
		float exRight = (width / 2) + (width - 20) / 2;
		float exBottom = (height - 20) / 2 + (width - 20) / 2;
		ovalF.set(exLeft, exTop, exRight, exBottom);
		
		canvas.drawArc(ovalF, -90, sweepAngle, false, exRingPaint);
//		while (true) {
//			sweepAngle = sweepAngle == 360 ? 0 : sweepAngle;
//			
//			canvas.drawArc(ovalF, -90, sweepAngle, false, exRingPaint);
//			
//			sweepAngle++;
//		}
	}
	
	public void setSweepAngle(int angle){
		sweepAngle = angle;
	}
	
	private Handler mSweepBarHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (sweepAngle > 360) {
				sweepAngle = 0;
			}
			invalidate();
		}
	};
	
	/**
	 * Execute the progress. And this method has to be called.
	 */
	public void execute() {
		mSweepBarHandler.sendEmptyMessage(0);
	}

	/**
	 * Reset the loading
	 */
	public void reset() {
		mSweepBarHandler.removeMessages(0);
	}
	
	
}
