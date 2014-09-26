package com.example.displaycircle;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	private DisplayCircleView dcv = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dcv = (DisplayCircleView) super.findViewById(R.id.displayCircleView);
		
		new Thread(new DisplayCircleRunnable()).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private class DisplayCircleRunnable implements Runnable {

		@Override
		public void run() {
			for (int i = 1; i <= 100; i++) {
				if (i == 1) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				dcv.setProgress(i);
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			dcv.setSpinToEnd(true);
			dcv.setCounts(98);
			
			float counts = dcv.getCounts();
			
			for (float i = 1; i <= counts; i++) {
				if (counts - i < 5.0f) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if (counts - i < 2.0f) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					if (counts - i < 1.0f) {
						try {
							Thread.sleep(800);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
				dcv.setCounts(i);
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			int slideDelta = (int) dcv.getTextSlideDelta();
			dcv.setBeginToSlide(true);
			
			float midTextSize = dcv.getMidTextSize();
			Log.i("AAA", "midTextSize[get]: " + midTextSize);
			
			float topBottomTextSize = dcv.getTopBottomTextSize();
			float sizedistanceScale = topBottomTextSize / slideDelta;
			float sizeStep = sizedistanceScale; 
			
			Log.i("AAA", "sizedistanceScale: " + sizedistanceScale);
			
			
			Log.i("AAA", "slideDelta: " + slideDelta);
			
			for (int i = 1; i < slideDelta; i++) {
				midTextSize = midTextSize - sizeStep;
				Log.i("AAA", "midTextSize[minus]: " + midTextSize);
				
				dcv.setSlideTextSize(midTextSize);
				dcv.setTextPosChange(i);
				
				
				
				
				try {
					Thread.sleep(20);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}

}
