package com.cauchy.demo.circleanimation;

import android.os.AsyncTask;
import android.os.Bundle;
import android.R.integer;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {
	CustomCircle cc = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		cc = (CustomCircle)super.findViewById(R.id.customCircle1);
		
		new UpdateBarProgress().execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class UpdateBarProgress extends AsyncTask<Void, Integer, Void> {
		private int angle = 360;

		@Override
		protected Void doInBackground(Void... params) {
			for (int i = 0; i <= angle; i++) {
				try {
					// update every second
					Thread.sleep(10);
				} catch (InterruptedException e) {

				}

				publishProgress(i);
			}
			
			return null;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			cc.setSweepAngle(values[0]);
			cc.execute();
		}

	}
}


