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
		dcv.setUnit("ΩÔ");
		dcv.setScoreUnit("∑÷");
		dcv.setTopText("œ÷‘⁄");
		
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
			
			// Counting the counts
			dcv.setCounts(118);
			
			float counts = dcv.getCounts();
			
			for (float i = 1; i <= counts; i++) {
				
				dcv.setCounts(i);
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				if (counts - i < 1.0f) {
					try {
						Thread.sleep(800);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			// Begin to spin
			dcv.setBeginToSpin(true);

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
			
			// Spin to end and begin to slide center text
			dcv.setSpinToEnd(true);
			
			int slideDelta = (int) dcv.getTextSlideDelta();
			dcv.setBeginToSlide(true);
			
			float midTextSize = dcv.getMidTextSize();
			Log.i("AAA", "midTextSize[get]: " + midTextSize);
			
			dcv.setSlideTextSize(midTextSize);
			
			float topBottomTextSize = dcv.getTopBottomTextSize();
			float sizedistanceScale = topBottomTextSize / slideDelta;
			float sizeStep = sizedistanceScale; 
			
			float deltaSpinArcWidth = dcv.getDeltaSpinArcWidth();
			float spinCircleRadius = dcv.getSpinCircleRadius(); 
			
			//Log.i("AAA", "sizedistanceScale: " + sizedistanceScale);
			//Log.i("AAA", "slideDelta: " + slideDelta);
			
			for (int i = 1; i < slideDelta; i++) {
				
				//Log.i("AAA", "midTextSize[minus]: " + midTextSize);
				midTextSize = midTextSize - sizeStep;
				if (i % (int) (slideDelta / deltaSpinArcWidth) == 0) {
					spinCircleRadius = spinCircleRadius - deltaSpinArcWidth / slideDelta;
				}
				Log.i("AAA", "spinCircleRadius: " + spinCircleRadius);
				Log.i("AAA", "deltaSpinArcWidth: " + deltaSpinArcWidth);
				
				dcv.setSlideTextSize(midTextSize);
				dcv.setSpinCircleRadius(spinCircleRadius);
				dcv.setTextPosChange(i);
				
				try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// Center text slide to end
			dcv.setSlideToEnd(true);
			dcv.setScores(98);
			
			float scores = dcv.getScores();
			
			for (int i = 1; i <= scores; i++) {
				if (scores - i < 5.0f) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if (scores - i < 2.0f) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					if (scores - i < 1.0f) {
						try {
							Thread.sleep(800);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
				dcv.setScores(i);
				
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			//Thread.State;
						
		}
		
	}

}
