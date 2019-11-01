package com.softmed.stockapp.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.stfalcon.chatkit.dialogs.DialogsList;

/**
 * Created by cozej4 on 2019-07-25.
 *
 * @cozej4 https://github.com/cozej4
 */
public class CustomDialogsList extends DialogsList {
    private  CustomScrollView.OnMyScrollChangeListener myScrollChangeListener;
    private float mTouchPosition;

    public CustomDialogsList(Context context) {
        super(context);
    }

    public CustomDialogsList(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomDialogsList(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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

    public CustomScrollView.OnMyScrollChangeListener getMyScrollChangeListener() {
        return myScrollChangeListener;
    }

    public void setMyScrollChangeListener(CustomScrollView.OnMyScrollChangeListener myScrollChangeListener) {
        this.myScrollChangeListener = myScrollChangeListener;
    }

    public interface OnMyScrollChangeListener {
        void onScrollUp();

        void onScrollDown();
    }

}
