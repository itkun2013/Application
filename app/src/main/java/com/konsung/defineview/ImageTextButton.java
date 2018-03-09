package com.konsung.defineview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsung.R;

/**
 * Created by 匡国华 on 2015/12/17 0017.
 */
public class ImageTextButton extends LinearLayout {
    final static String ANDROIDXML = "http://schemas.android" +
            ".com/apk/res/android";
    final static String KONSUNG = "http://schemas.android.com/apk/res-auto";

    private ImageView imageViewbutton;
    private TextView textView;
    private int mTextColor;

    public ImageTextButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        setClickable(true);
        setFocusable(true);
        setBackgroundResource(R.drawable.ks_btn_bg);

        // 图标
        int src = attrs.getAttributeResourceValue(ANDROIDXML, "src", -1);
        if (src != -1) {
            imageViewbutton = new ImageView(context);
            imageViewbutton.setPadding(0, 0, 0, 0);
            ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(40, 40);
            imageViewbutton.setLayoutParams(p);
            imageViewbutton.setImageResource(src);
            addView(imageViewbutton);
        }

        // 文字
        String text = "";
        //判断text是硬编码还是引用strings文件的本地资源对应显示text内容
        int textResource = attrs.getAttributeResourceValue(ANDROIDXML, "text",
                -1);
        if (textResource != -1) {
            text = getResources().getString(textResource);
        } else {
            text = attrs.getAttributeValue(ANDROIDXML, "text");
        }

        textView = new TextView(context);
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setPadding(5, 0, 0, 0);
        ViewGroup.LayoutParams p1 = new ViewGroup.LayoutParams(ViewGroup
                .LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams
                .WRAP_CONTENT);
        mTextColor = attrs.getAttributeIntValue(KONSUNG,
                "text_color", Color.WHITE);
        textView.setLayoutParams(p1);
        textView.setTextColor(mTextColor);
        textView.setText(text);

        float textSize = attrs.getAttributeFloatValue(KONSUNG, "text_size", 0f);
        if (textSize != 0) {
            textView.setTextSize(textSize);
        }

        addView(textView);

        int gravity = attrs.getAttributeIntValue(ANDROIDXML, "gravity",
                Gravity.CENTER);
        setGravity(gravity);
    }

    public void setText(String text) {
        this.textView.setText(text);
        invalidate();
    }

    /**
     * 将imagebutton设为设置页面的button样式
     * @param isSettingStyle
     */
    public void setSettingStyle(boolean isSettingStyle) {
        if (isSettingStyle) {
            setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams layout = new
                    LinearLayout.LayoutParams(35, 35);
            layout.setMargins(30, 0, 10, 0);
            this.imageViewbutton.setLayoutParams(layout);
            invalidate();
        }
    }
    public String getText() {
        return this.textView.getText().toString();
    }
}
