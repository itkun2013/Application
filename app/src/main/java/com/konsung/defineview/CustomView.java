package com.konsung.defineview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
/**
 * 自定义view
 */
public class CustomView extends RelativeLayout {

    final static String ANDROIDXML = "http://schemas.android" + ".com/apk/res/android";
    final static String KONSUNG = "http://schemas.android.com/apk/res-auto";
    final int disabledBackgroundColor = Color.parseColor("#E2E2E2");
    int beforeBackground;
    public boolean isLastTouch = false;

    /**
     * 构造器
     * @param context 上下文
     * @param attrs 属性
     */
    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (enabled) {
            setBackgroundColor(beforeBackground);
        } else {
            setBackgroundColor(disabledBackgroundColor);
        }
        invalidate();
    }

    boolean animation = false;

    @Override
    protected void onAnimationStart() {
        super.onAnimationStart();
        animation = true;
    }

    @Override
    protected void onAnimationEnd() {
        super.onAnimationEnd();
        animation = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (animation) {
            invalidate();
        }
    }
}
