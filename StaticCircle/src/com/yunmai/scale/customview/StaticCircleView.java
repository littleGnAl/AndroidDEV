package com.yunmai.scale.customview;

import java.io.File;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class StaticCircleView extends View{

	//private static final String FONTS_FOLDER = "fonts";
	//private static final String FONT_HELVETICANEUE = FONTS_FOLDER + File.separator + "HelveticaNeueLTPro-ThEx.ttf";
	
	private Paint exCirclePaint = null;
	private Paint inCirclePaint = null;
	private Paint textAbovePaint = null;
	private Paint textBelowPaint = null;
	
	private final float circleWidthScale = 0.04f;
	private float exCircleWidth;
	private final float textSizeScale = 0.8f;
	
	private String textAbove;
	private String textBelow;
	private String textAboveMantissa;
	
	private float textSizeAbove;
	private float textSizeBelow;
	
	private void initVariable(Context context) {
		//font style 
		//AssetManager assets = context.getAssets();
		//final Typeface font = Typeface.createFromAsset(assets, FONT_HELVETICANEUE);
		
		exCirclePaint = setPaint(150, Color.WHITE, Style.STROKE);
		inCirclePaint = setPaint(255, Color.WHITE, Style.STROKE);
		textAbovePaint = setPaint(255, Color.WHITE, Style.FILL);
		//textAbovePaint.setTypeface(font);
		textBelowPaint = setPaint(255, Color.WHITE, Style.FILL);
		
		exCircleWidth = 0;
		
		textAbove = "20.5";
		textAboveMantissa = "";
		textBelow = "Õý³£";
		
		setLayerType(View.LAYER_TYPE_HARDWARE, null);
	}
	
	private float getTextLength(String text, Paint paint) {
		return paint.measureText(text, 0, text.length());
	}
	
	private float getTextHeight(Paint paint) {
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.ascent);
	}
	
	private Paint setPaint(int alpha, int color, Style style) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(color);
		paint.setAlpha(alpha);
		paint.setStyle(style);
		
		return paint;
	}
	
	private void setTextAboveMantissa() {
		String[] s = textAbove.split("\\.");
		if (s.length > 1) {
			textAbove = s[0];
			textAboveMantissa = "." + s[1];
		} else {
			textAboveMantissa = "";
		}
	}
	
	private void setCircleStrokeWidth(int width) {
		exCircleWidth = width * circleWidthScale;
		
		exCirclePaint.setStrokeWidth(width * circleWidthScale);
		inCirclePaint.setStrokeWidth(width * circleWidthScale);
	}
	

	public StaticCircleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initVariable(context);
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int width = getWidth();
		int height = getHeight();
		
		//width = height = (width > height) ? height : width;
		Log.i("AAA", "width / height: " + width + " " + height);
		
		setCircleStrokeWidth(width);
		setTextAboveMantissa();
		
		float centerX = width / 2;
		float centerY = height / 2;
		
		float radius = centerX - exCircleWidth;
		
		canvas.drawCircle(centerX, centerY, radius, exCirclePaint);
		canvas.drawCircle(centerX, centerY, radius - exCircleWidth, inCirclePaint);
		
		float widthDelta = radius - exCircleWidth;
		textSizeAbove = (radius - exCircleWidth) * textSizeScale;
		textSizeBelow = textSizeAbove / 2;// * textSizeScale;
		
		Log.i("AAA", "widthDelta: " + widthDelta);
		
		textAbovePaint.setTextSize(textSizeAbove);
		textBelowPaint.setTextSize(textSizeBelow);
		
		float textAboveXDes = centerX - getTextLength(textAbove, textAbovePaint) / 2 - getTextLength(textAboveMantissa, textBelowPaint) / 2;
		float textAboveYDes = centerY;
		
		// Text above
		canvas.drawText(textAbove, textAboveXDes, textAboveYDes, textAbovePaint);
		
		// Text mantissa
		canvas.drawText(textAboveMantissa, centerX + getTextLength(textAbove, textAbovePaint) / 2 - getTextLength(textAboveMantissa, textBelowPaint) / 2, textAboveYDes, textBelowPaint);
		
		//textBelowPaint.setTextSize(textSizeBelow);
		float textBelowYDes = centerY + widthDelta / 2 + getTextHeight(textBelowPaint) / 4;
		
		// Text below
		canvas.drawText(textBelow, centerX - getTextLength(textBelow, textBelowPaint) / 2,  textBelowYDes, textBelowPaint);
	}
	
	/*
	 * Set the above text */
	public void setTextAbove(String textAbove) {
		this.textAbove = textAbove;
	}
	
	/*
	 * Set the below text */
	public void setTextBelow(String textBelow) {
		this.textBelow = textBelow;
	}

}
