package com.konsung.defineview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;

/**
 * 自定义button
 */
public class ButtonFlat extends Button {
    TextView textButton;

    /**
     * 构造器
     * @param context 上下文
     * @param attrs 属性
     */
    public ButtonFlat(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置默认属性值
     */
    protected void setDefaultProperties() {
        minHeight = GlobalNumber.THIRTY_SIX_NUMBER;
        minWidth = GlobalNumber.EIGHTY_EIGHT_NUBER;
        rippleSize = GlobalNumber.THREE_NUMBER;
        setMinimumHeight(UiUitls.dpToPx(minHeight, getResources()));
        setMinimumWidth(UiUitls.dpToPx(minWidth, getResources()));
        setBackgroundResource(R.drawable.background_transparent);
    }

    @Override
    protected void setAttributes(AttributeSet attrs) {
        // Set text button
        String text = null;
        int textResource = attrs.getAttributeResourceValue(ANDROIDXML, "text",
                -1);
        if (textResource != -1) {
            text = getResources().getString(textResource);
        } else {
            text = attrs.getAttributeValue(ANDROIDXML, "text");
        }
        if (text != null) {
            textButton = new TextView(getContext());
            textButton.setText(text.toUpperCase());
            textButton.setTextColor(backgroundColor);

            textButton.setTextSize(GlobalNumber.TEN_NUMBER);
            textButton.setTypeface(null, Typeface.BOLD);
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT,
                    RelativeLayout.TRUE);
            textButton.setLayoutParams(params);
            addView(textButton);
        }
        int bacgroundColor = attrs.getAttributeResourceValue(ANDROIDXML,
                "background", -1);
        if (bacgroundColor != -1) {
            setBackgroundColor(getResources().getColor(bacgroundColor));
        } else {
            // Color by hexadecimal
            // Color by hexadecimal
            background = attrs.getAttributeIntValue(ANDROIDXML, "background",
                    -1);
            if (background != -1) {
                setBackgroundColor(background);
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (x != -1) {

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(makePressColor());
            canvas.drawCircle(x, y, radius, paint);
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
            invalidate();
        }

    }

    /**
     * Make a dark color to ripple effect
     *
     * @return
     */
    @Override
    protected int makePressColor() {
        return Color.parseColor("#88DDDDDD");
    }

    /**
     * 设置Text值
     * @param text 文本值
     */
    public void setText(String text) {
        textButton.setText(text.toUpperCase());
    }

    /**
     * 设置背景颜色
     * @param color 背景颜色
     */
    public void setBackgroundColor(int color) {
        backgroundColor = color;
        if (isEnabled()) {
            beforeBackground = backgroundColor;
        }
        textButton.setTextColor(color);
    }

    @Override
    public TextView getTextView() {
        return textButton;
    }

    /**
     * 获取Text文本
     * @return 文本
     */
    public String getText() {
        return textButton.getText().toString();
    }
}
