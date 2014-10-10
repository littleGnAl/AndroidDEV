package com.cauchy.demo.filpviewdemo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import fr.castorflex.android.flipimageview.library.FlipImageView;

public class MainActivity extends Activity implements FlipImageView.OnFlipListener {
	
	private FlipImageView flipImageView = null;
	private StaticCircleView staticCircleView = null;
	private ImageView imageView = null; 
	private LinearLayout linearLayout2 = null;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		flipImageView = (FlipImageView) super.findViewById(R.id.imageview); 
		staticCircleView = (StaticCircleView) super.findViewById(R.id.staticCircleView);
		Log.i("AAA", "width: " + staticCircleView.getLayoutParams().width + " " + "height: " + staticCircleView.getWidth());
		imageView = (ImageView) super.findViewById(R.id.imageView1);
		linearLayout2 = (LinearLayout) super.findViewById(R.id.linearLayout2);
		Log.i("AAA", "LinearLayout width: " + linearLayout2.getLayoutParams().width + " " + "height: " + linearLayout2.getWidth());
		
		//StaticCircleView s = new StaticCircleView(context, attrs)
		
		//flipImageView.setImage)
		Bitmap b = saveViewBitmap(staticCircleView);
		imageView.setImageBitmap(b);
		
		flipImageView.setImageBitmap(b);
		flipImageView.setFlippedDrawable(new BitmapDrawable(b));
		flipImageView.setDrawable(new BitmapDrawable(b));
		flipImageView.setOnFlipListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public void onClick(FlipImageView view) {
        flipImageView.setInterpolator(new DecelerateInterpolator());
        flipImageView.setDuration(400);
        flipImageView.setRotationXEnabled(false);
        flipImageView.setRotationYEnabled(true);
        flipImageView.setRotationZEnabled(false);
        flipImageView.setRotationReversed(false);
    }

	@Override
	public void onFlipStart(FlipImageView view) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFlipEnd(FlipImageView view) {
		// TODO Auto-generated method stub
		
	}
	
	private Bitmap saveViewBitmap(View view) {  
		//get current view bitmap  
//		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),    
//				               Bitmap.Config.ARGB_8888);    
//        //利用bitmap生成画布    
//	    Canvas canvas = new Canvas(bitmap);  
//	       
//	    //把view中的内容绘制在画布上    
//	    view.draw(canvas);
		
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        //view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.layout(0, 0, view.getLayoutParams().width, view.getLayoutParams().height);
        
        //Log.i("AAA", "width:" + view.getMeasuredWidth() + "height:" + view.getMear);
        view.buildDrawingCache();
		
//		view.clearFocus();//currentView表示设置的View对象
//
//		view.setPressed(false);
//
//		view.setDrawingCacheBackgroundColor(0);
//
//		view.setDrawingCacheEnabled(true);

		Bitmap viewBitmap = view.getDrawingCache();

		view.setDrawingCacheEnabled(false);


	    return viewBitmap;
	}  
	

}
