package com.konsung.defineview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;
import com.tencent.bugly.crashreport.CrashReport;
/**
 * 自定义view
 */
public abstract class Button extends CustomView {

    final float defaultNumber = 30f;
    final static String ANDROIDXML = "http://schemas.android.com/apk/res" +
            "/android";
    int minWidth;
    int minHeight;
    int background;
    float rippleSpeed = defaultNumber;
    int rippleSize = GlobalNumber.THREE_NUMBER;
    Integer rippleColor;
    View.OnClickListener onClickListener;
    boolean clickAfterRipple = true;
    int backgroundColor = Color.parseColor("#1E88E5");
    boolean enableRipple = true;
    float textSize = GlobalNumber.FOURTEEN_NUMBER;
    float x = -1;
    float y = -1;
    float radius = -1;
    final int compareValue = 0xFF;
    /**
     *构造器
     * @param context 上下文引用
     * @param attrs 属性
     */
    public Button(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefaultProperties();
        clickAfterRipple = attrs.getAttributeBooleanValue(KONSUNG, "animate", true);
        enableRipple = attrs.getAttributeBooleanValue(KONSUNG, "rippleEnable", true);
        textSize = attrs.getAttributeFloatValue(KONSUNG, "textSize", -1);
        setAttributes(attrs);
        beforeBackground = backgroundColor;
        if (rippleColor == null) {
            rippleColor = makePressColor();
        }
    }

    /**
     * 设置默认属性值
     */
    protected void setDefaultProperties() {
        setMinimumHeight(UiUitls.dpToPx(minHeight, getResources()));
        setMinimumWidth(UiUitls.dpToPx(minWidth, getResources()));
        setBackgroundResource(background);
        setBackgroundColor(backgroundColor);
    }


    /**
     * 设置视图属性
     * @param attrs 属性
     */
    abstract protected void setAttributes(AttributeSet attrs);

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        invalidate();
        if (isEnabled()) {
            isLastTouch = true;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                radius = getHeight() / rippleSize;
                x = event.getX();
                y = event.getY();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                radius = getHeight() / rippleSize;
                x = event.getX();
                y = event.getY();
                if (!((event.getX() <= getWidth() && event.getX() >= 0) && (
                        event.getY() <= getHeight() && event.getY() >= 0))) {
                    isLastTouch = false;
                    x = -1;
                    y = -1;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if ((event.getX() <= getWidth() && event.getX() >= 0)
                        && (event.getY() <= getHeight() && event.getY() >= 0)) {
                    radius++;
                    if (!clickAfterRipple && onClickListener != null) {
                        onClickListener.onClick(this);
                    }
                } else {
                    isLastTouch = false;
                    x = -1;
                    y = -1;
                }
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                isLastTouch = false;
                x = -1;
                y = -1;
            }
        }
        return true;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
            Rect previouslyFocusedRect) {
        if (!gainFocus) {
            x = -1;
            y = -1;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // super.onInterceptTouchEvent(ev);
        return true;
    }

    /**
     * 获取bitmap
     * @return bitmap
     */
    public Bitmap makeCircle() {
        Bitmap output = Bitmap.createBitmap(
                getWidth() - UiUitls.dpToPx(GlobalNumber.SIX_NUMBER, getResources()), getHeight()
                - UiUitls.dpToPx(GlobalNumber.SEVEN_NUMBER, getResources()), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(rippleColor);
        canvas.drawCircle(x, y, radius, paint);

        if (!enableRipple) {
            rippleSpeed = GlobalNumber.TEN_THOUSAND_NUMBER;
        }

        if (radius > getHeight() / rippleSize) {
            radius += rippleSpeed;
        }
        if (radius >= getWidth()) {
            x = -1;
            y = -1;
            radius = getHeight() / rippleSize;
            if (onClickListener != null && clickAfterRipple) {
                onClickListener.onClick(this);
            }
        }
        return output;
    }

    /**
     * Make a dark color to ripple effect
     * @return 按压颜色
     */
    protected int makePressColor() {
        int r = (this.backgroundColor >> GlobalNumber.SIXTEEN_NUMBER) & compareValue;
        int g = (this.backgroundColor >> GlobalNumber.EIGHT_NUMBER) & compareValue;
        int b = (this.backgroundColor >> 0) & compareValue;
        r = (r - GlobalNumber.THIRTY_NUBER < 0) ? 0 : r - GlobalNumber.THIRTY_NUBER;
        g = (g - GlobalNumber.THIRTY_NUBER < 0) ? 0 : g - GlobalNumber.THIRTY_NUBER;
        b = (b - GlobalNumber.THIRTY_NUBER < 0) ? 0 : b - GlobalNumber.THIRTY_NUBER;
        return Color.rgb(r, g, b);
    }

    @Override
    public void setOnClickListener(View.OnClickListener l) {
        onClickListener = l;
    }

    /**
     * 设置背景颜色
     * @param color 背景颜色
     */
    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
        if (isEnabled()) {
            beforeBackground = backgroundColor;
        }
        try {
            LayerDrawable layer = (LayerDrawable) getBackground();
            GradientDrawable shape = (GradientDrawable) layer
                    .findDrawableByLayerId(R.id.shape_bacground);
            shape.setColor(backgroundColor);
            rippleColor = makePressColor();
        } catch (Exception ex) {
            CrashReport.postCatchedException(ex);
        }
    }

    /**
     * 获取TextView视图
     * @return TextView
     */
    abstract public TextView getTextView();

    /**
     * 设置rippleSpeed
     * @param rippleSpeed rippleSpeed
     */
    public void setRippleSpeed(float rippleSpeed) {
        this.rippleSpeed = rippleSpeed;
    }

    /**
     * 获取rippleSpeed
     * @return rippleSpeed
     */
    public float getRippleSpeed() {
        return this.rippleSpeed;
    }
}
