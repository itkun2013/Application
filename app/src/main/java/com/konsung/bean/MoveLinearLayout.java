package com.konsung.bean;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * Created by YYX on 2017/9/21 0021.
 * 用于移动的自定义控件
 */

public class MoveLinearLayout extends LinearLayout {
    /**
     * 触摸事件的回调
     */
    private OnTouch onTouch;

    /**
     * 构造方法
     * @param context 上下文
     */
    public MoveLinearLayout(Context context) {
        super(context);
    }

    /**
     * 构造方法
     * @param context 上下文
     * @param attrs 属性
     */
    public MoveLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 构造方法
     * @param context 上下文
     * @param attrs 属性
     * @param defStyleAttr style资源
     */
    public MoveLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 设置点击事件
     * @param onTouch 点击事件
     */
    public void setOnTouch(OnTouch onTouch) {
        this.onTouch = onTouch;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (null != onTouch) {
                onTouch.onDown(event.getX(), event.getY());
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (null != onTouch) {
                onTouch.onMove(event);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (null != onTouch) {
                onTouch.onUp(event.getX(), event.getY());
            }
        }
        return true;
    }

    /**
     * 点击事件
     */
    public interface OnTouch {
        /**
         * 按下的方法
         * @param x  坐标 x
         * @param y  坐标 y
         */
        void onDown(float x, float y);

        /**
         * 移动的方法
         * @param event 移动事件
         */
        void onMove(MotionEvent event);

        /**
         * 手指抬起的方法
         * @param x  坐标 x
         * @param y  坐标 y
         */
        void onUp(float x, float y);
    }
}
