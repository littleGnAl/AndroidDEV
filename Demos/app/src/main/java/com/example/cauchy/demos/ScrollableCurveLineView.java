package com.example.cauchy.demos;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Cauchy on 2015/6/29.
 */
public class ScrollableCurveLineView extends View {

    public interface CurveViewScrollListener {

        /**
         * Callback method to be invoked when current item changed
         *
         * @param view    the wheel view whose state has changed
         * @param oldValue the old value of current item
         * @param newValue the new value of current item
         */
        void onChanged(ScrollableCurveLineView view, int oldValue, int newValue);

        /**
         * Callback method to be invoked when scrolling started.
         *
         * @param view the wheel view whose state has changed.
         */
        void onScrollingStarted(ScrollableCurveLineView view);

        /**
         * Callback method to be invoked when scrolling ended.
         *
         * @param view the wheel view whose state has changed.
         */
        void onScrollingFinished(ScrollableCurveLineView view);
    }

    private Context mContext;

    private SparseArray<PointF> mDatas;

    private Paint mLinePaint;

    private Paint mTextPaint;

    private Path mLinePath;

    private Path mRightSidePath;

    /**
     * Scroll offset
     */
    private int scrollingOffset;

    /**
     * Current value
     */
    private int mCurrValue;

    private int POINT_INTERVAL;

    private int SHOW_POINT_COUNT = 7;

    private int MAX_VALUE = 0;

    private int MIN_VALUE = 0;

    private AutoAdjustmentScroller mAdjustmentScroller;

    private CurveViewScrollListener mScrollListener;

    private boolean isScrollingPerformed;


    public ScrollableCurveLineView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        init();

        setUpScrollerScrollEvent();
    }

    private void init() {
        mLinePaint = new Paint();
        mLinePaint.setDither(true);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(5.0f);

        mTextPaint = new Paint();
        mTextPaint.setDither(true);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(Utils.sp2px(mContext, 12));

        mLinePath = new Path();
        mRightSidePath = new Path();

        POINT_INTERVAL = Utils.getScreenSize(mContext).x / SHOW_POINT_COUNT;

        mAdjustmentScroller = new AutoAdjustmentScroller(mContext);
    }

    public void setDatas(SparseArray<PointF> datas) {
        this.mDatas = datas;

        MAX_VALUE = mDatas.size();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight(), mLinePaint);

        drawLines(canvas);
    }

    private void drawLines(Canvas canvas) {
        mRightSidePath.reset();
        mLinePath.reset();

        // 根据间隔计算当前一半宽度的个数+偏移2个
        final int halfCount = (int) (Math.ceil(getWidth() / 2f / POINT_INTERVAL) + Math.ceil(SHOW_POINT_COUNT / 2));
        final int distanceX = scrollingOffset;
        final int currValue = mCurrValue;
        int value;
        float xPosition;
        PointF currentPoint = null;

        for (int i = 0; i < halfCount; i++) {

            //  right path
            xPosition = getWidth() / 2f + i * POINT_INTERVAL + distanceX;
            value = currValue + i;

            if (value >= MIN_VALUE && value < MAX_VALUE) {
                currentPoint = mDatas.valueAt(value);
                if (currentPoint != null) {
                    if (i == 0) {
                        mRightSidePath.moveTo(xPosition, currentPoint.y);
                    } else {
                        mRightSidePath.lineTo(xPosition, currentPoint.y);
                    }

                    canvas.drawText(value + "", xPosition, currentPoint.y, mTextPaint);
                }
            }

            //  left
            xPosition = getWidth() / 2f - i * POINT_INTERVAL + distanceX;
            value = currValue - i;
            if (value >= MIN_VALUE && value < MAX_VALUE) {
                currentPoint = mDatas.valueAt(value);
                if (currentPoint != null) {

                    if (i == 0) {
                        mLinePath.moveTo(xPosition, currentPoint.y);
                    } else {
                        mLinePath.lineTo(xPosition, currentPoint.y);
                    }

                    canvas.drawText(value + "", xPosition, currentPoint.y, mTextPaint);
                }
            }
        }

        canvas.drawPath(mRightSidePath, mLinePaint);
        canvas.drawPath(mLinePath, mLinePaint);

    }

    private void doScroll(int delta) {
        scrollingOffset += delta;
        float offsetCount = scrollingOffset / POINT_INTERVAL;
        if (0 != offsetCount) {
            // 显示在范围内
            int oldValue = Math.min(Math.max(0, mCurrValue), MAX_VALUE);
            mCurrValue -= offsetCount;
            scrollingOffset -= offsetCount * POINT_INTERVAL;
            if (mCurrValue < MIN_VALUE - 3) {
                mAdjustmentScroller.stopScrolling();
            }
            if (mCurrValue > MAX_VALUE + 3) {
                mAdjustmentScroller.stopScrolling();
            }
            if (null != mScrollListener) {
                mScrollListener.onChanged(this, oldValue, Math.min(Math.max(MAX_VALUE, mCurrValue), MAX_VALUE));
            }
        }

        postInvalidate();
    }

    public void setOnScrollListener(CurveViewScrollListener listener) {
        this.mScrollListener = listener;
    }

    private void setUpScrollerScrollEvent() {
        mAdjustmentScroller.setOnScrollListener(new AutoAdjustmentScroller.ScrollingListener() {
            @Override
            public void onScroll(int distance) {
                doScroll(distance);
            }

            @Override
            public void onStarted() {
                isScrollingPerformed = true;
                notifyScrollingListenersAboutStart();
            }

            @Override
            public void onFinished() {
                if (isScrollingPerformed) {
                    notifyScrollingListenersAboutEnd();
                    isScrollingPerformed = false;
                }
                scrollingOffset = 0;

                postInvalidate();
            }

            @Override
            public void onJustify() {
                if (scrollingOffset < -POINT_INTERVAL / 2) {
                    mAdjustmentScroller.scroll(POINT_INTERVAL + scrollingOffset, 0);
                } else if (scrollingOffset > POINT_INTERVAL / 2) {
                    mAdjustmentScroller.scroll(scrollingOffset - POINT_INTERVAL, 0);
                } else {
                    mAdjustmentScroller.scroll(scrollingOffset, 0);
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return true;
        }

        return mAdjustmentScroller.onTouchEvent(event);
    }

    private void notifyScrollingListenersAboutEnd() {
        if (null != mScrollListener) {
            mScrollListener.onScrollingFinished(this);
        }
    }

    private void notifyScrollingListenersAboutStart() {
        if (null != mScrollListener) {
            mScrollListener.onScrollingStarted(this);
        }
    }
}
