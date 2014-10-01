package com.example.displaycircle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {
	private DisplayCircleView dcv = null;
	
	private ListView listView = null;
	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();
	private SimpleAdapter simpleAdapter = null;
	
	private Button button1 = null;
	private Button button2 = null;
	
	private String[][] data = new String[][] { { "ONE", "It's ONE"}, 
											   { "TWO", "It's TWO" },
											   { "THREE", "It's THREE" },
											   { "FOUR", "It's FOUR" },
											   { "FIVE", "It's FIVE" } };
	
	//Thread displayCircleThread = new Thread(new DisplayCircleRunnable(0, 0, false));
	private boolean enableAnimate = false;
	private float counts = 0.0f;
	private int scores = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dcv = (DisplayCircleView) super.findViewById(R.id.displayCircleView);
		dcv.setUnit("ΩÔ");
		dcv.setScoreUnit("∑÷");
		dcv.setTopText("œ÷‘⁄");
		//dcv.setCounts(221);
		
		listView = (ListView) super.findViewById(R.id.listView1);
		initListView();
		
		button1 = (Button) super.findViewById(R.id.btn1);
		button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//dcv.setCounts(117.9f);
//				for (int i = 0; i < 10; i++) {
//					
//					counts = i * 2.5f;
//					scores = 94;
//					enableAnimate = false;
//					new Thread(new DisplayCircleRunnable()).start();
//					
//					try {
//						Thread.sleep(50);
//					} catch (InterruptedException e) {
//						// TODO: handle exception
//						e.printStackTrace();
//					}
//					
//				}
				
				new Thread(new ChildDisplayCircleRunnable()).start();
				
					
				
				//dcv.setCounts(119f);
				//displayCircleThread.setCounts(118);
			}
		});
		
		button2 = (Button) super.findViewById(R.id.btn2);
		button2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				resetDisplayCircle();
				
			}
		});
		
		//new Thread(new DisplayCircleRunnable()).start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private void initListView() {
		
		
		//listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, data));
		//listView.setAdapter(this, );
		
		for (int i = 0; i < data.length; i++) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("_id", data[i][0]);
			map.put("name", data[i][1]);
			list.add(map);
		}
		
		simpleAdapter = new SimpleAdapter(this, 
										  list,
										  R.layout.data_list, 
										  new String[] { "_id", "name" }, 
										  new int[] { R.id._id, R.id.name });
		
		listView.setAdapter(simpleAdapter);
	}
	
	private void resetDisplayCircle() {
		dcv.setWeight(0);
		dcv.setBeginToSpin(false);
		dcv.setSpinToEnd(false);
		dcv.setBeginToSlide(false);
		dcv.setSlideToEnd(false);
	}
	
	private class ChildDisplayCircleRunnable implements Runnable {
		
		@Override
		public void run() {
			for (int i = 0; i < 10; i++) {
				
				counts = i * 2.5f;
				scores = 94;
				enableAnimate = false;
				new Thread(new DisplayCircleRunnable()).start();
				
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
			}
		}
	}
	
	private class DisplayCircleRunnable implements Runnable {
		
		@Override
		public void run() {
			
			// Counting the counts
			dcv.setWeight(counts);
			
			if (enableAnimate) {
				
				// Begin to spin
				dcv.setBeginToSpin(enableAnimate);
				
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
				dcv.setSpinToEnd(enableAnimate);
				
				int slideDelta = (int) dcv.getTextSlideDelta();
				dcv.setBeginToSlide(enableAnimate);
				
				float midTextSize = dcv.getMidTextSize();
				Log.i("AAA", "midTextSize[get]: " + midTextSize);
				
				dcv.setSlideTextSize(midTextSize);
				
				float topBottomTextSize = dcv.getTopBottomTextSize();
				float sizedistanceScale = topBottomTextSize / slideDelta;
				float sizeStep = sizedistanceScale; 
				
//			float deltaSpinArcWidth = dcv.getDeltaSpinArcWidth();
//			float spinCircleRadius = dcv.getSpinCircleRadius(); 
				
				//Log.i("AAA", "sizedistanceScale: " + sizedistanceScale);
				//Log.i("AAA", "slideDelta: " + slideDelta);
				
				for (int i = 1; i < slideDelta; i++) {
					if (slideDelta - i < 1) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					//Log.i("AAA", "midTextSize[minus]: " + midTextSize);
					midTextSize = midTextSize - sizeStep;
					
					//Log.i("AAA", "result: " + i % (int) (slideDelta / deltaSpinArcWidth));
					//if ((i % (int) (slideDelta / deltaSpinArcWidth)) == 0) {
					
					//spinCircleRadius = spinCircleRadius - deltaSpinArcWidth / slideDelta;			
//				Log.i("AAA", "spinCircleRadius: " + spinCircleRadius);
//				Log.i("AAA", "deltaSpinArcWidth: " + deltaSpinArcWidth);
					
					dcv.setSlideTextSize(midTextSize);
					//dcv.setSpinCircleRadius(spinCircleRadius);
					dcv.setTextPosChange(i);
					
					try {
						Thread.sleep(4);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				// Center text slide to end
				dcv.setSlideToEnd(enableAnimate);
				dcv.setScores(scores);
				
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
				
//			if (Thread.currentThread().isInterrupted()) {
//				Thread.interrupted();
//			}
				
				//Thread.State;
			}
						
		}
		
	}

}
