package com.example.cauchy.flipviewdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button mButton;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void init() {
        mTextView = (TextView) findViewById(R.id.textView);
        mButton = (Button) findViewById(R.id.button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ObjectAnimator rotationYAnimator = ObjectAnimator.ofFloat(mTextView, "rotationX", 0.0f, 90.0f);
//                rotationYAnimator.setDuration(50);
//                rotationYAnimator.addListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        super.onAnimationEnd(animation);
//
//                        mTextView.setRotationX(-90.0f);
//                        ObjectAnimator rotationYAnimator2 = ObjectAnimator.ofFloat(mTextView, "rotationX", -90.0f, 0.0f);
//                        rotationYAnimator2.setDuration(50);
//                        rotationYAnimator2.start();
//                    }
//                });
//                rotationYAnimator.start();

                mTextView.setRotationX(0.0f);
                mTextView.animate().rotationX(90.0f).setDuration(50).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        mTextView.setRotationX(-90.0f);
                        mTextView.animate().rotationX(0.0f).setListener(null).start();
                    }
                }).start();
            }
        });
    }

//    private void flipit() {
//        Interpolator accelerator = new AccelerateInterpolator();
//        Interpolator decelerator = new DecelerateInterpolator();
//        final LinearLayout visibleList,invisibleList;
//        final ObjectAnimator visToInvis, invisToVis;
//        if (locationLL.getVisibility() == View.GONE) {
//            visibleList = baseLL;
//            invisibleList = locationLL;
//            visToInvis = ObjectAnimator.ofFloat(visibleList, "rotationY", 0f, 90f);
//            invisToVis = ObjectAnimator.ofFloat(invisibleList, "rotationY", -90f, 0f);
//        } else {
//            invisibleList = baseLL;
//            visibleList = locationLL;
//            visToInvis = ObjectAnimator.ofFloat(visibleList, "rotationY", 0f, -90f);
//            invisToVis = ObjectAnimator.ofFloat(invisibleList, "rotationY", 90f, 0f);
//        }
//        visToInvis.setDuration(300);
//        invisToVis.setDuration(300);
//        visToInvis.setInterpolator(accelerator);
//        invisToVis.setInterpolator(decelerator);
//        visToInvis.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator anim) {
//                visibleList.setVisibility(View.GONE);
//                invisToVis.start();
//                invisibleList.setVisibility(View.VISIBLE);
//            }
//        });
//
//        visibleList.animate().ro
//        visToInvis.start();
//    }
}
