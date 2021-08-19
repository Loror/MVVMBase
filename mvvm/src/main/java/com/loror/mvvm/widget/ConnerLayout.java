package com.loror.mvvm.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.loror.mvvm.R;

/**
 * Created by Loror
 * 自己绘制圆角背景的ViewGroup
 */
public class ConnerLayout extends FrameLayout {
    private final Paint mPaint;
    private int arcRadios;
    private int borderColor = Color.TRANSPARENT;
    private int strokeWidth = 2;
    private int backgroundColor = Color.TRANSPARENT;

    public ConnerLayout(Context context) {
        this(context, null);
    }

    public ConnerLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConnerLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);//取消锯齿
        if (attrs != null) {
            initAttributes(context, attrs);
        }
        int leftPadding = getPaddingLeft();
        int topPadding = getPaddingTop();
        int rightPadding = getPaddingRight();
        int bottomPadding = getPaddingBottom();
        setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
        setWillNotDraw(false);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ConnerLayout);
        arcRadios = array.getDimensionPixelSize(R.styleable.ConnerLayout_arcRadios, 0);
        strokeWidth = array.getDimensionPixelSize(R.styleable.ConnerLayout_strokWidth, 2);
        borderColor = array.getColor(R.styleable.ConnerLayout_borderColor, Color.TRANSPARENT);
        backgroundColor = array.getColor(R.styleable.ConnerLayout_fillColor, Color.TRANSPARENT);
        array.recycle();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top + strokeWidth, right, bottom);
    }

    public void setFillColor(int fillColor) {
        this.backgroundColor = fillColor;
        invalidate();
    }

    public void setBorderColor(@ColorInt int color) {
        this.borderColor = color;
        invalidate();
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
        invalidate();
    }

    public void setArcRadios(int arcRadios) {
        this.arcRadios = arcRadios;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //<0或者大于半径时候绘制半圆
        if (getHeight() != 0) {
            if (arcRadios < 0 || arcRadios > getHeight() / 2) {
                arcRadios = getHeight() / 2;
            }
        }

        int height = getHeight();
        int width = getWidth();

        //绘制背景
        mPaint.setColor(backgroundColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(strokeWidth);

        RectF backLeftTop = new RectF(strokeWidth, strokeWidth, 2 * arcRadios - strokeWidth, 2 * arcRadios);
        RectF backRightTop = new RectF(width - 2 * arcRadios + strokeWidth, strokeWidth, width - strokeWidth, 2 * arcRadios);
        RectF backRightBottom = new RectF(width - 2 * arcRadios + strokeWidth, height - 2 * arcRadios + strokeWidth * 2, width - strokeWidth, height - strokeWidth);
        RectF backLeftBottom = new RectF(strokeWidth, height - arcRadios * 2 + strokeWidth * 2, arcRadios * 2 - strokeWidth, height - strokeWidth);

        canvas.drawArc(backLeftTop, 180, 90, true, mPaint);
        canvas.drawArc(backRightTop, 270, 90, true, mPaint);
        canvas.drawArc(backRightBottom, 0, 90, true, mPaint);
        canvas.drawArc(backLeftBottom, 90, 90, true, mPaint);

        Rect rectTop = new Rect(arcRadios, strokeWidth, width - arcRadios, strokeWidth + arcRadios - strokeWidth / 2);
        Rect rectBottom = new Rect(arcRadios, height - arcRadios + strokeWidth / 2, width - arcRadios, height - strokeWidth);
        Rect rectCenter = new Rect(strokeWidth, arcRadios + strokeWidth / 2, width - strokeWidth, height - arcRadios + strokeWidth / 2);

        canvas.drawRect(rectTop, mPaint);
        canvas.drawRect(rectBottom, mPaint);
        canvas.drawRect(rectCenter, mPaint);

        mPaint.setColor(borderColor);
        mPaint.setStyle(Paint.Style.STROKE);//设置画圆弧的画笔

        //绘制边框
        RectF ovalLeftTop = new RectF(strokeWidth / 2, strokeWidth / 2, 2 * arcRadios - strokeWidth / 2, 2 * arcRadios + strokeWidth / 2);
        RectF ovalRightTop = new RectF(width - 2 * arcRadios - strokeWidth / 2, strokeWidth / 2, width - strokeWidth / 2, 2 * arcRadios + strokeWidth / 2);
        RectF ovalRightBottom = new RectF(width - 2 * arcRadios - strokeWidth / 2, height - 2 * arcRadios - strokeWidth / 2, width - strokeWidth / 2, height - strokeWidth / 2);
        RectF ovalLeftBottom = new RectF(strokeWidth / 2, height - arcRadios * 2 - strokeWidth / 2, arcRadios * 2 + strokeWidth / 2, height - strokeWidth / 2);

        canvas.drawArc(ovalLeftTop, 180, 90, false, mPaint);
        canvas.drawArc(ovalRightTop, 270, 90, false, mPaint);
        canvas.drawArc(ovalRightBottom, 0, 90, false, mPaint);
        canvas.drawArc(ovalLeftBottom, 90, 90, false, mPaint);

        canvas.drawLine(strokeWidth / 2, arcRadios, strokeWidth / 2, height - arcRadios, mPaint);
        canvas.drawLine(arcRadios, height - strokeWidth / 2, width - arcRadios, height - strokeWidth / 2, mPaint);
        canvas.drawLine(width - strokeWidth / 2, arcRadios, width - strokeWidth / 2, height - arcRadios, mPaint);
        canvas.drawLine(width - arcRadios, strokeWidth / 2, width - arcRadios, strokeWidth / 2, mPaint);
        canvas.drawLine(width - arcRadios, 0, width - arcRadios, strokeWidth, mPaint);
        canvas.drawLine(width - arcRadios, 0, width - arcRadios, strokeWidth, mPaint);

        canvas.drawLine(arcRadios, strokeWidth / 2, width - arcRadios, strokeWidth / 2, mPaint);
    }

}
