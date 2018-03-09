package com.konsung.defineview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

import com.konsung.R;

public class HorizontalProgressBarWithNumber extends ProgressBar {

    private static final int DEFAULT_TEXT_SIZE = 10;
    private static final int DEFAULT_TEXT_COLOR = 0XFFFC00D1;
    private static final int DEFAULT_COLOR_UNREACHED_COLOR = 0xFFd3d6da;
    private static final int DEFAULT_HEIGHT_REACHED_PROGRESS_BAR = 2;
    private static final int DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR = 2;
    private static final int DEFAULT_SIZE_TEXT_OFFSET = 10;

    /**
     * painter of all drawing things
     */
    protected Paint paint = new Paint();
    /**
     * color of progress number
     */
    protected int textColor = DEFAULT_TEXT_COLOR;
    /**
     * size of text (sp)
     */
    protected int textSize = sp2px(DEFAULT_TEXT_SIZE);

    /**
     * offset of draw progress
     */
    protected int textOffset = dp2px(DEFAULT_SIZE_TEXT_OFFSET);

    /**
     * height of reached progress bar
     */
    protected int reachedProgressBarHeight = dp2px
            (DEFAULT_HEIGHT_REACHED_PROGRESS_BAR);

    /**
     * color of reached bar
     */
    protected int reachedBarColor = DEFAULT_TEXT_COLOR;
    /**
     * color of unreached bar
     */
    protected int unReachedBarColor = DEFAULT_COLOR_UNREACHED_COLOR;
    /**
     * height of unreached progress bar
     */
    protected int unReachedProgressBarHeight = dp2px
            (DEFAULT_HEIGHT_UNREACHED_PROGRESS_BAR);
    /**
     * view width except padding
     */
    protected int realWidth;

    protected boolean ifDrawText = true;

    protected static final int VISIBLE = 0;

    public HorizontalProgressBarWithNumber(Context context, AttributeSet
            attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressBarWithNumber(Context context, AttributeSet attrs,
                                           int defStyle) {
        super(context, attrs, defStyle);
        obtainStyledAttributes(attrs);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setAntiAlias(true);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec,
                                          int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);

        realWidth = getMeasuredWidth() - getPaddingRight() - getPaddingLeft();
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            float textHeight = (paint.descent() - paint.ascent());
            result = (int) (getPaddingTop() + getPaddingBottom() + Math.max(
                    Math.max(reachedProgressBarHeight,
                            unReachedProgressBarHeight), Math.abs
                            (textHeight)));
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * get the styled attributes
     *
     * @param attrs
     */
    private void obtainStyledAttributes(AttributeSet attrs) {
        // init values from custom attributes
        final TypedArray attributes = getContext().obtainStyledAttributes(
                attrs, R.styleable.HorizontalProgressBarWithNumber);

        textColor = attributes
                .getColor(
                        R.styleable
                                .HorizontalProgressBarWithNumber_progress_text_color,
                        DEFAULT_TEXT_COLOR);
        textSize = (int) attributes.getDimension(
                R.styleable.HorizontalProgressBarWithNumber_progress_text_size,
                textSize);

        reachedBarColor = attributes
                .getColor(
                        R.styleable
                                .HorizontalProgressBarWithNumber_progress_reached_color,

                        textColor);
        unReachedBarColor = attributes
                .getColor(
                        R.styleable
                                .HorizontalProgressBarWithNumber_progress_unreached_color,
                        DEFAULT_COLOR_UNREACHED_COLOR);
        reachedProgressBarHeight = (int) attributes
                .getDimension(
                        R.styleable
                                .HorizontalProgressBarWithNumber_progress_reached_bar_height,

                        reachedProgressBarHeight);
        unReachedProgressBarHeight = (int) attributes
                .getDimension(
                        R.styleable
                                .HorizontalProgressBarWithNumber_progress_unreached_bar_height,

                        unReachedProgressBarHeight);
        textOffset = (int) attributes
                .getDimension(
                        R.styleable
                                .HorizontalProgressBarWithNumber_progress_text_offset,

                        textOffset);

        int textVisible = attributes
                .getInt(R.styleable
                                .HorizontalProgressBarWithNumber_progress_text_visibility,
                        VISIBLE);
        if (textVisible != VISIBLE) {
            ifDrawText = false;
        }
        attributes.recycle();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {

        canvas.save();
        canvas.translate(getPaddingLeft(), getHeight() / 2);

        boolean noNeedBg = false;
        float radio = getProgress() * 1.0f / getMax();
        float progressPosX = (int) (realWidth * radio);
        String text = getProgress() + "%";
        // paint.getTextBounds(text, 0, text.length(), mTextBound);

        float textWidth = paint.measureText(text);
        float textHeight = (paint.descent() + paint.ascent()) / 2;

        if (progressPosX + textWidth > realWidth) {
            progressPosX = realWidth - textWidth;
            noNeedBg = true;
        }

        // draw reached bar
        float endX = progressPosX - textOffset / 2;
        if (endX > 0) {
            paint.setColor(reachedBarColor);
            paint.setStrokeWidth(reachedProgressBarHeight);
            canvas.drawLine(0, 0, endX, 0, paint);
        }
        // draw progress bar
        // measure text bound
        if (ifDrawText) {
            paint.setColor(textColor);
            canvas.drawText(text, progressPosX, -textHeight, paint);
        }

        // draw unreached bar
        if (!noNeedBg) {
            float start = progressPosX + textOffset / 2 + textWidth;
            paint.setColor(unReachedBarColor);
            paint.setStrokeWidth(unReachedProgressBarHeight);
            canvas.drawLine(start, 0, realWidth, 0, paint);
        }

        canvas.restore();

    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    protected int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, getResources().getDisplayMetrics());
    }

    /**
     * sp 2 px
     *
     * @param spVal
     * @return
     */
    protected int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, getResources().getDisplayMetrics());

    }

}
