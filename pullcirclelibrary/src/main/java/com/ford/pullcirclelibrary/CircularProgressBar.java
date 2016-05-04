package com.ford.pullcirclelibrary;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

/**
 * @author Ford
 */
public class CircularProgressBar extends ProgressBar {

    private static final String TAG = CircularProgressBar.class.getSimpleName();

    private static final int STROKE_WIDTH = 20;

    private int mStrokeWidth = STROKE_WIDTH;

    // 各种paint
    private final RectF mCircleBounds = new RectF();
    private final Paint mRightSemicirclePaint = new Paint();
    private final Paint mLeftSemicirclePaint = new Paint();
    private final Paint mBackgroundColorPaint = new Paint();

    private boolean mHasShadow = true;

    private int mShadowColor = Color.WHITE;

    private int progress = 0;

    private int spinSpeed = 2;

    private boolean isSpinning = false;


    public CircularProgressBar(Context context) {
        super(context);
        init(null, 0);
    }

    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CircularProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @SuppressLint("NewApi")
    public void init(AttributeSet attrs, int style) {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircularProgressBar, style, 0);
        String color;
        Resources res = getResources();
        this.mHasShadow = a.getBoolean(R.styleable.CircularProgressBar_hasShadow, true);
        color = a.getString(R.styleable.CircularProgressBar_progressColor);
        if (color == null) {
            mRightSemicirclePaint.setColor(res.getColor(R.color.circular_progress_default_progress));
            mLeftSemicirclePaint.setColor(res.getColor(R.color.circular_progress_default_progress));
        } else {
            mRightSemicirclePaint.setColor(Color.parseColor(color));
            mLeftSemicirclePaint.setColor(Color.parseColor(color));
        }
        color = a.getString(R.styleable.CircularProgressBar_backgroundColor);
        if (color == null)
            mBackgroundColorPaint.setColor(res.getColor(R.color.circular_progress_default_background));
        else
            mBackgroundColorPaint.setColor(Color.parseColor(color));

        // mStrokeWidth = a.getInt(R.styleable.CircularProgressBar_strokeWidth,
        // STROKE_WIDTH);

        mStrokeWidth = DensityUtility.dip2px(getContext(), 2.5f);
        a.recycle();

        mRightSemicirclePaint.setAntiAlias(true);
        mRightSemicirclePaint.setStyle(Paint.Style.STROKE);
        mRightSemicirclePaint.setStrokeWidth(DensityUtility.dip2px(getContext(), 4));

        mLeftSemicirclePaint.setAntiAlias(true);
        mLeftSemicirclePaint.setStyle(Paint.Style.STROKE);
        mLeftSemicirclePaint.setStrokeWidth(DensityUtility.dip2px(getContext(), 4));

        mBackgroundColorPaint.setAntiAlias(true);
        mBackgroundColorPaint.setStyle(Paint.Style.STROKE);
        mBackgroundColorPaint.setStrokeWidth(mStrokeWidth);
        mBackgroundColorPaint.setAlpha(145);

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {

        canvas.drawArc(mCircleBounds, 0, 360, false, mBackgroundColorPaint);
        if (isSpinning) {
            canvas.drawArc(mCircleBounds, progress - 90, 30, false, mRightSemicirclePaint);
        } else {
            canvas.drawArc(mCircleBounds, 270, progress, false, mRightSemicirclePaint);
            canvas.drawArc(mCircleBounds, 270, -progress, false, mLeftSemicirclePaint);
        }
        if (mHasShadow) {
            mRightSemicirclePaint.setShadowLayer(DensityUtility.dip2px(getContext(), 5), 0, 0, mShadowColor);
            mLeftSemicirclePaint.setShadowLayer(DensityUtility.dip2px(getContext(), 5), 0, 0, mShadowColor);
        }
        if (isSpinning) {
            scheduleRedraw();
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min + 2 * STROKE_WIDTH, min + 2 * STROKE_WIDTH);

        mCircleBounds.set(STROKE_WIDTH, STROKE_WIDTH, min + STROKE_WIDTH, min + STROKE_WIDTH);
    }

    @Override
    public synchronized void setProgress(int progress) {
        super.setProgress(progress);
        invalidate();
    }

    public synchronized void setHasShadow(boolean flag) {
        this.mHasShadow = flag;
        invalidate();
    }

    public synchronized void setShadow(int color) {
        this.mShadowColor = color;
        invalidate();
    }

    public boolean getHasShadow() {
        return mHasShadow;
    }

    public synchronized void incrementProgress() {
        progress++;
        if (progress >= 180) {
            progress = 0;
        }
        invalidate();
    }

    public void decreasingProgress() {
        progress--;
        if (progress <= 0) {
            progress = 0;
            invalidate();
            return;
        }
        invalidate();
    }

    public void setPro(int progress) {
        this.progress = progress;
        if (progress >= 180) {
            this.progress = 180;
            invalidate();
            return;
        }
        invalidate();
    }

    public float getCurrentPro() {
        return this.progress;
    }

    public void setCurrentPro(int progress) {
        this.progress = progress;
    }

    private void scheduleRedraw() {
        progress += spinSpeed;
        if (progress > 360) {
            progress = 0;
        }
        postInvalidateDelayed(0);
    }

    public void stopSpinning() {
        isSpinning = false;
        progress = 0;
        postInvalidate();
    }

    public void spin() {
        isSpinning = true;
        postInvalidate();
    }

}
