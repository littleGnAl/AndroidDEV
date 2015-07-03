package com.example.cauchy.demos;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

/**
 * Created by Cauchy on 2015/6/27.
 */
public class AutoAdjustmentScroller {

    /**
     * Scrolling listener interface
     */
    public interface ScrollingListener {
        /**
         * Scrolling callback called when scrolling is performed.
         *
         * @param distance the distance to scroll
         */
        void onScroll(int distance);

        /**
         * Starting callback called when scrolling is started
         */
        void onStarted();

        /**
         * Finishing callback called after justifying
         */
        void onFinished();

        /**
         * Justifying callback called to justify a view when scrolling is ended
         */
        void onJustify();
    }

    private Context mContext;

    /**
     * 自动调整位置scroller
     */
    private OverScroller mAutoAdjustScroller;

    private GestureDetector mGestureDetector;

    private VelocityTracker mVelocityTracker = null;

    private final int SLIDE_VELOCITY = 600;

    private int MAXIMUM_VELOCITY;

    private int MINIMUM_VELOCITY;

    private int TOUCH_SLOP;

    /**
     * The last touched x coordination
     */
    private float mLastDownX;

    /**
     * The last x coordination of the scroll
     */
    private float mLastScrollX;


    private int DEFAULT_SCROLL_DURATION = 200;

    private ScrollingListener mScrollListener;

    private boolean mIsScrollingPerformed;

    // Message what
    private final int MESSAGE_SCROLL = 0;
    private final int MESSAGE_JUSTIFY = 1;


    public AutoAdjustmentScroller(Context context) {
        mContext = context;

        init();
    }

    private void init() {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(mContext);
        TOUCH_SLOP = viewConfiguration.getScaledTouchSlop();
        MAXIMUM_VELOCITY = viewConfiguration.getScaledMaximumFlingVelocity();
        MINIMUM_VELOCITY = 1;

        mAutoAdjustScroller = new OverScroller(mContext);
        mGestureDetector = new GestureDetector(mContext, mGestureListener);
    }

    // gesture listener
    private GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // Do scrolling in onTouchEvent() since onScroll() are not call immediately
            //  when user touch and move the wheel
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mLastScrollX = 0;
            mAutoAdjustScroller.fling(0, (int) mLastScrollX, (int) -velocityX, 0, -0x7FFFFFFF, 0x7FFFFFFF, 0, 0);
            setNextMessage(MESSAGE_SCROLL);
            return true;
        }

    };

    private void fling(int velocityX) {
        mAutoAdjustScroller.fling(mAutoAdjustScroller.getCurrX(), 0, velocityX, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
        setNextMessage(MESSAGE_SCROLL);
    }

    /**
     * Handle touch event
     * @param event
     * @return
     */
    public boolean onTouchEvent(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mLastDownX = event.getX();

                stopScrolling();
                clearMessages();

                break;
            }

            case MotionEvent.ACTION_MOVE: {
                // perform scrolling
                int distanceX = (int) (event.getX() - mLastDownX);
                if (distanceX != 0) {
                    startScrolling();
                    mScrollListener.onScroll(distanceX);
                    mLastDownX = event.getX();
                }
                break;
            }

//            case MotionEvent.ACTION_UP: {
//                int initialVelocity = (int) mVelocityTracker.getXVelocity();
//                fling(-initialVelocity);
//
//                break;
//            }
        }

        if (!mGestureDetector.onTouchEvent(event) && event.getAction() == MotionEvent.ACTION_UP) {
            justify();
        }


        return true;
    }

    public void stopScrolling() {
        mAutoAdjustScroller.abortAnimation();
        mAutoAdjustScroller.forceFinished(true);
    }

    public void scroll(int distance, int duration) {
        mLastScrollX = 0;
        mAutoAdjustScroller.startScroll(0, 0, distance, duration != 0 ? duration : DEFAULT_SCROLL_DURATION);

        setNextMessage(MESSAGE_SCROLL);
        startScrolling();
    }

    private Handler mScrollHandler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message message) {
            mAutoAdjustScroller.computeScrollOffset();

            int currX = mAutoAdjustScroller.getCurrX();
            int delta = (int) (mLastScrollX - currX);
            mLastScrollX = currX;

            if (delta != 0) {
                if (mScrollListener != null) {
                    mScrollListener.onScroll(delta);
                }
            }

            if (!mAutoAdjustScroller.isFinished()) {
                mScrollHandler.sendEmptyMessage(message.what);
            } else if (message.what == MESSAGE_SCROLL) {
                justify();
            } else {
                finishScrolling();
            }

            return true;
        }
    });

    public void setOnScrollListener(ScrollingListener listener) {
        this.mScrollListener = listener;
    }

    /**
     * Justify position
     */
    private void justify() {
        mScrollListener.onJustify();
        setNextMessage(MESSAGE_JUSTIFY);
    }

    /**
     * Set next message to queue. Clears queue before.
     *
     * @param message the message to set
     */
    private void setNextMessage(int message) {
        clearMessages();
        mScrollHandler.sendEmptyMessage(message);
    }

    /**
     * Clears messages from queue
     */
    private void clearMessages() {
        mScrollHandler.removeMessages(MESSAGE_SCROLL);
        mScrollHandler.removeMessages(MESSAGE_JUSTIFY);
    }

    /**
     * Starts scrolling
     */
    private void startScrolling() {
        if (!mIsScrollingPerformed) {
            mIsScrollingPerformed = true;
            mScrollListener.onStarted();
        }
    }

    /**
     * Finishes scrolling
     */
    void finishScrolling() {
        if (mIsScrollingPerformed) {
            mScrollListener.onFinished();
            mIsScrollingPerformed = false;
        }
    }


}
