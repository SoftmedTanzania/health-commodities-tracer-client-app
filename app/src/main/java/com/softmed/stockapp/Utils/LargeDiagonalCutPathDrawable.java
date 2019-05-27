package com.softmed.stockapp.Utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;

public class LargeDiagonalCutPathDrawable extends Drawable {

    Path mPath = new Path();
    Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int diagonalSize;

    public LargeDiagonalCutPathDrawable(int slantSize){
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(5);
        diagonalSize = slantSize;
    }

    @Override
    public void draw(Canvas canvas) {
        mPath.moveTo(10,10);
        mPath.lineTo(canvas.getWidth()-diagonalSize, 10);
        mPath.lineTo(canvas.getWidth()-10, diagonalSize);
        mPath.lineTo(canvas.getWidth()-10, canvas.getHeight()-10);
        mPath.lineTo(10, canvas.getHeight()-10);

        mPath.close();
        canvas.drawPath(mPath, mPaint);

    }

    @Override
    public void setAlpha(int i) {}

    @Override
    public void setColorFilter(ColorFilter colorFilter) {}

    @Override
    public int getOpacity() {
        return 0;
    }


}