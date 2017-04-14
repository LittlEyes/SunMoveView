package com.litteyes.sunmoveview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.litteyes.sunmoveview.R;


/**
 * Created by bin_li on 2017/4/6.
 */

public class SunProgressBar extends View {

    public static final int DEFAULT_SIZE = 150;

    private static final int LINE_STROKE_WIDTH = 2;
    private static final int BITMAP_RAUDIS = 5;

    private RectF mRectF;
    private RectF mFilterRectF;
    private RectF mLineRectF;

    private Point mCenterPoint;
    private Point mProgressPoint;

    private int mRadius;
    private float mStartAngle;
    private float mTotleAngle;
    //半圆的轨迹线
    private Paint mLinePaint;
    //半圆的实体部分
    private Paint mFilterPaint;
    private TextPaint mTextPaint;

    private Bitmap mProgressBitmap;

    private int mDefaultSize;

    private float mProgress;

    public SunProgressBar(Context context) {
        super(context);
        init(context);
    }

    public SunProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mDefaultSize = dip2px(context, DEFAULT_SIZE);
        mRectF = new RectF();
        mFilterRectF = new RectF();
        mLineRectF = new RectF();
        mBitmapRectF = new RectF();
        mCenterPoint = new Point();
        mProgressPoint = new Point();

        mAnimator = new ValueAnimator();
        mProgressBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.img_sun);
        initPaint();
        initPadding();
    }

    private int bitmapPadding;
    private int strokePadding;
    private int filterPadding;

    private void initPadding() {
        bitmapPadding = dip2px(getContext(), BITMAP_RAUDIS);
        strokePadding = dip2px(getContext(), LINE_STROKE_WIDTH);
        filterPadding = bitmapPadding + strokePadding;
    }

    private void initPaint() {
        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
//        // 设置画笔的样式，为FILL，FILL_OR_STROKE，或STROKE
        mLinePaint.setStyle(Paint.Style.STROKE);
//        // 设置画笔粗细
        mLinePaint.setStrokeWidth(dip2px(getContext(), LINE_STROKE_WIDTH));
//        // 当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的图形样式，如圆形样式
//        // Cap.ROUND,或方形样式 Cap.SQUARE
        PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 2);
        mLinePaint.setPathEffect(effects);
        mLinePaint.setColor(Color.parseColor("#FFAC4C"));

        mFilterPaint = new Paint();
        mFilterPaint.setAntiAlias(true);
        mFilterPaint.setStyle(Paint.Style.FILL);
        mFilterPaint.setStrokeWidth(dip2px(getContext(), LINE_STROKE_WIDTH));
        mFilterPaint.setColor(Color.parseColor("#FFECD7"));


        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.parseColor("#747578"));
        mTextPaint.setTypeface(Typeface.DEFAULT);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLine(canvas);
        drawFilter(canvas);
        drawProgressLogo(canvas);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void drawFilter(Canvas canvas) {
//        mFilterRectF.right = (int) (mProgress * (mRectF.right - mRectF.left));
        mFilterRectF.right = mProgressPoint.x;
        Log.e("wwwww", "wwww" + mFilterRectF.right);
        Path circlePath = new Path();
        circlePath.addCircle(mCenterPoint.x, mCenterPoint.y, mRadius - bitmapPadding, Path.Direction.CW);
        Path squrePath = new Path();
        squrePath.addRect(mFilterRectF, Path.Direction.CW);
        squrePath.op(circlePath, Path.Op.INTERSECT);
        canvas.drawPath(squrePath, mFilterPaint);
    }

    private void drawLine(Canvas canvas) {
        float sweepAngle = mTotleAngle * mProgress;
        canvas.drawArc(mLineRectF, mStartAngle, sweepAngle, false, mLinePaint);
        mProgressPoint.x = (int) (mCenterPoint.x + (mRadius - bitmapPadding) * Math.sin((90 + mStartAngle + sweepAngle) * Math.PI / 180));
        mProgressPoint.y = (int) (mCenterPoint.y - (mRadius - bitmapPadding) * Math.cos((90 + mStartAngle + sweepAngle) * Math.PI / 180));
    }

    private RectF mBitmapRectF;

    private void drawProgressLogo(Canvas canvas) {
        if (mProgressPoint.y + bitmapPadding < mFilterRectF.bottom || mProgressPoint.x < mRectF.right / 2) {
            mBitmapRectF.left = mProgressPoint.x - bitmapPadding;
            mBitmapRectF.top = mProgressPoint.y - bitmapPadding;
            mBitmapRectF.right = mProgressPoint.x + bitmapPadding;
            mBitmapRectF.bottom = mProgressPoint.y + bitmapPadding;
        }
        canvas.drawBitmap(mProgressBitmap, null, mBitmapRectF, new Paint());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int hight = width / 3;

        mRectF.left = filterPadding;
        mRectF.top = filterPadding;
        mRectF.right = width - filterPadding;
        mRectF.bottom = hight - filterPadding;
        mCenterPoint.x = w / 2;
        //圆心的算法
        mCenterPoint.y = mRadius = (hight * hight + width / 2 * width / 2) / (hight * 2);
        mStartAngle = (float) (270 - Math.toDegrees(Math.asin((float) width / 2 / mRadius)));
        mTotleAngle = 2 * (float) (Math.toDegrees(Math.asin((float) width / 2 / mRadius)));

        mFilterRectF.left = 0;
        mFilterRectF.right = (int) (mProgress * width);
        mFilterRectF.top = 0;
        mFilterRectF.bottom = hight;

        mLineRectF = new RectF();
        mLineRectF.left = mCenterPoint.x - mRadius + bitmapPadding;
        mLineRectF.right = mCenterPoint.x + mRadius - bitmapPadding;
        mLineRectF.top = mCenterPoint.y - mRadius + bitmapPadding;
        mLineRectF.bottom = mCenterPoint.y + mRadius - bitmapPadding;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = measures(widthMeasureSpec, mDefaultSize);
        setMeasuredDimension(measures(widthMeasureSpec, mDefaultSize),
                width / 3);
    }

    //属性动画
    private ValueAnimator mAnimator;

    public void startAnimator(float start, float end, long animTime) {
        mAnimator = ValueAnimator.ofFloat(start, end);
        mAnimator.setDuration(animTime);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue();
                Log.e("-----", "进度：" + mProgress);
                invalidate();
            }
        });
        mAnimator.start();
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 测量 View
     *
     * @param measureSpec
     * @param defaultSize View 的默认大小
     * @return
     */
    public static int measures(int measureSpec, int defaultSize) {
        int result = defaultSize;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        if (specMode == View.MeasureSpec.EXACTLY) {
            result = specSize;
        } else if (specMode == View.MeasureSpec.AT_MOST) {
            result = Math.min(result, specSize);
        }
        return result;
    }
}
