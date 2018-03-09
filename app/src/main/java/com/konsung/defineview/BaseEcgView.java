package com.konsung.defineview;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 心电波形绘制控件
 */
public abstract class BaseEcgView extends SurfaceView implements SurfaceHolder.Callback {
    public final SurfaceHolder surfaceHolder = getHolder();
    /**
     * 构造函数
     * @param context 上下文
     * @param attrs 属性设置
     */
    public BaseEcgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder.addCallback(this);
        //将surfaceView背景变为透明
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public abstract void surfaceDestroyed(SurfaceHolder holder);

    /**
     * 添加波形数据到循环队列
     * 由子类重写实现
     * @param leadParam 导联参数
     * @param data 待绘制的心电波形数据
     */
    public abstract void addEcgData(int leadParam, int data);


    /**
     * 设置波形增益
     */
    public abstract void setWaveGain();

    /**
     * 设置波形速率
     * 重新初始化波形数据绘制。
     */
    public abstract void setWaveSpeed();


    /**
     * 启动线程
     */
    public abstract void startThread();

    /**
     * 停止线程
     */
    public abstract void stopThread();


    /**
     * 复位心电波形数据
     */
    public abstract void resetData();



}
