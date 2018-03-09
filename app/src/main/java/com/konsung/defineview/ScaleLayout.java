package com.konsung.defineview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

import com.nineoldandroids.view.ViewHelper;

/**
 * 可缩放的自定义控件
 * Created by DJH on 2017/10/23 0023.
 */
public class ScaleLayout extends FrameLayout {
    private ScaleGestureDetector mScaleGestureDetector = null;
    private float scale; // 缩放比例
    private float preScale = 1; // 默认前一次缩放比例为1
    private long lastClickTime; //记录上次点击时间
    private long currentTime; //记录当前点击时间
    private int count = 0; //点击次数
    private static final long DEFAULT_INTERVAL_TIME = 400; //默认点击间隔时间

    /**
     * 构造方法
     * @param context 上下文
     * @param attrs 属性
     * @param defStyleAttr 默认属性
     */
    public ScaleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 构造方法
     * @param context 上下文
     * @param attrs 属性
     */
    public ScaleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 构造方法
     * @param context 上下文
     */
    public ScaleLayout(Context context) {
        super(context);
        init(context);
    }

    /**
     * 初始化
     * @param context 上下文
     */
    private void init(Context context) {
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureListener());
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int pointerCount = event.getPointerCount(); // 获得多少点
        if (pointerCount > 1) {
            // 多点触控时，拦截父控件的touch事件
            getParent().requestDisallowInterceptTouchEvent(true);
            return mScaleGestureDetector.onTouchEvent(event); //让mScaleGestureDetector处理触摸事件
        } else {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                count++;
                if (count == 1) {
                    lastClickTime = System.currentTimeMillis();
                } else if (count == 2) {
                    currentTime = System.currentTimeMillis();
                    //处理双击事件
                    if (currentTime - lastClickTime < DEFAULT_INTERVAL_TIME) {
                        scale = 1;
                        ViewHelper.setScaleX(ScaleLayout.this, scale);
                        ViewHelper.setScaleY(ScaleLayout.this, scale);
                        count = 0;
                        lastClickTime = 0;
                    } else {
                        lastClickTime = currentTime;
                        count = 1;
                    }
                    currentTime = 0;
                }
            }
            return true;
        }
    }

    /**
     * 手势监听
     */
    public class ScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            float previousSpan = detector.getPreviousSpan(); // 前一次双指间距
            float currentSpan = detector.getCurrentSpan(); // 本次双指间距
            if (currentSpan < previousSpan) {
                // 缩小
                scale = preScale - (previousSpan - currentSpan) / 1000;
            } else {
                // 放大
                scale = preScale + (currentSpan - previousSpan) / 1000;
            }
            // 暂时不支持放大功能
            if (scale > 1) {
                scale = 1;
            }
            // 最大缩小2倍
            if (scale < 0.5) {
                scale = 0.5f;
            }
            // 缩放view
            if (scale >= 0.5) {
                ViewHelper.setScaleX(ScaleLayout.this, scale); // x方向上缩放
                ViewHelper.setScaleY(ScaleLayout.this, scale); // y方向上缩放
            }
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            // 一定要返回true才会进入onScale()这个函数
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            preScale = scale; // 记录本次缩放比例
        }
    }
}
