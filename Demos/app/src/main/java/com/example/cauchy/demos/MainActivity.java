package com.example.cauchy.demos;

import android.graphics.Point;
import android.graphics.PointF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private SparseArray<PointF> mDatas;

    private ScrollableCurveLineView mScrollableCurveLineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        mScrollableCurveLineView = (ScrollableCurveLineView) findViewById(R.id.scrollable_curve_view);

        mDatas = new SparseArray<PointF>();

        for (int i = 0; i < 1000; i++) {
            PointF point = new PointF(i, (float) (Math.random() * 300));
            mDatas.put(i, point);
        }

        mScrollableCurveLineView.setDatas(mDatas);
        mScrollableCurveLineView.postInvalidate();
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
}
