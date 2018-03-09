package com.konsung.defineview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.konsung.R;

/**
 * Created by xiangshicheng on 2017/4/21 0021.
 */
public class MyProgress extends ProgressBar {
    private String text;
    private Paint mPaint;
    private static final int DIVIDE = 100;
    private static final float TEXTSIZE = 20f;
    /**
     * 构造方法
     * @param context 上下文引用
     */
    public MyProgress(Context context) {
        super(context);
        initText();
    }

    /**
     * @param context 上下文引用
     * @param attrs 属性
     * @param defStyle 风格
     */
    public MyProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initText();
    }

    /**
     * 构造函数
     * @param context 上下文引用
     * @param attrs 属性
     */
    public MyProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        initText();
    }

    @Override
    public synchronized void setProgress(int progress) {
        setText(progress);
        super.setProgress(progress);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        this.mPaint.getTextBounds(this.text, 0, this.text.length(), rect);
//        int x = (getWidth() / 2) - rect.centerX();
        int x = (getWidth() * getProgress() / 100) - rect.width() - 10;
        int y = (getHeight() / 2) - rect.centerY();
        canvas.drawText(this.text, x, y, this.mPaint);
    }

    /**
     * 初始化，画笔
     */
    private void initText() {
        this.mPaint = new Paint();
//        this.mPaint.setColor(getResources().getColor(R.color.grass_konsung_2));
        this.mPaint.setColor(Color.WHITE);
        this.mPaint.setTextSize(TEXTSIZE);
    }

    /**
     * 设置内容
     */
    public void setText() {
        setText(this.getProgress());
    }

    /**
     * 设置文字内容
     * @param progress 当前进度值
     */
    public void setText(int progress) {
        int i = (progress * DIVIDE) / this.getMax();
        this.text = String.valueOf(i) + "%";
    }

}
