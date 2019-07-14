package com.softmed.stockapp.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * Created by cozej4 on 2019-07-10.
 *
 * @cozej4 https://github.com/cozej4
 */

public class CustomScrollView extends ScrollView {

    OnMyScrollChangeListener myScrollChangeListener;
    private float mTouchPosition;

    public CustomScrollView(Context context) {
        super(context);
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mTouchPosition = event.getY();
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float mReleasePosition = event.getY();

            if (mTouchPosition - mReleasePosition > 0) {
                // user scroll down
                myScrollChangeListener.onScrollDown();
            } else {
                myScrollChangeListener.onScrollUp();
            }
        }
        return super.onTouchEvent(event);
    }

    public OnMyScrollChangeListener getMyScrollChangeListener() {
        return myScrollChangeListener;
    }

    public void setMyScrollChangeListener(OnMyScrollChangeListener myScrollChangeListener) {
        this.myScrollChangeListener = myScrollChangeListener;
    }

    public interface OnMyScrollChangeListener {
        public void onScrollUp();

        public void onScrollDown();
    }
}