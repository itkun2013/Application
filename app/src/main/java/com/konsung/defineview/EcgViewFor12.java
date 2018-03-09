package com.konsung.defineview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.konsung.R;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.ParamDefine.EcgDefine;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 心电波形绘制控件
 */
public class EcgViewFor12 extends BaseEcgView implements SurfaceHolder.Callback {
    // Y轴比例，像素/AD值。1280为屏幕横向像素数，216.576为屏幕宽度，单位mm，2150和1946为标尺上下对应
    // 的AD值。此变量会被频繁调用，因此声明为static形。
    private final static float ECG_Y_RATIO = (1280f / 216.576f * 10) / (2150 - 1946);
    //单位:像素/mm,1280为屏幕横向像素数，216.576为屏幕横向长度（单位mm）
    private double pixelPerMm = 1280 / 216.576;
    float offset = (float) (pixelPerMm * 2.5); //几字形标尺 向下偏移
    public static final float MS_TIME = 1000f; //秒的转换
    private final static int SLEEP_TIME = 100; // 每次锁屏的时间间距，单位ms
    private final static int BLANK_LINE_WIDTH = 20; // 右侧空白点的宽度，单位像素
    private static final int DEFAULT_ECG_COUNT = 50; //默认画心电数据的个数
    private static final int MULTIPLE_DATA = 12; //存储画心电数据集的倍数
    private static final int MIX_DATA = 4000; //队列存储最大数据量
    private SurfaceHolder surfaceHolder;
    public boolean isRunning; // 波形绘制线程运行标志
    private Canvas canvas;
    private float lockWidth; // 每次锁屏需要画的宽度，单位像素
    private float waveSpeed; // 波速，单位mm/s
    private int ecgPerCount = DEFAULT_ECG_COUNT; // 每次画心电数据的个数，心电每秒有500个数据包
    private float ecgXOffset = 0; // 每次X坐标偏移的像素
    private float waveGain; // 波形增益
    private float zoom = 1; // 界面所有元素的缩放倍数
    private boolean isAutoGain = false; // 是否是自动增益
    private static Queue<Integer> ecg0Data = new LinkedList<>();
    private static Queue<Integer> ecg1Data = new LinkedList<>();
    private static Queue<Integer> ecg2Data = new LinkedList<>();
    private static Queue<Integer> ecg3Data = new LinkedList<>();
    private static Queue<Integer> ecg4Data = new LinkedList<>();
    private static Queue<Integer> ecg5Data = new LinkedList<>();
    private static Queue<Integer> ecg6Data = new LinkedList<>();
    private static Queue<Integer> ecg7Data = new LinkedList<>();
    private static Queue<Integer> ecg8Data = new LinkedList<>();
    private static Queue<Integer> ecg9Data = new LinkedList<>();
    private static Queue<Integer> ecg10Data = new LinkedList<>();
    private static Queue<Integer> ecg11Data = new LinkedList<>();
    private Paint wavePaint; //画波形图的画笔
    private int waveWidgetWidth; //单个波形的控件宽度
    private int waveWidgetHeight; //单个波形的控件高度
    //每次画线的X坐标起点
    private int startX0;
    private int startX1;
    private int startX2;
    private int startX3;
    //每次画线的Y坐标起点
    private int startY0;
    private int startY1;
    private int startY2;
    private int startY3;
    private int startY4;
    private int startY5;
    private int startY6;
    private int startY7;
    private int startY8;
    private int startY9;
    private int startY10;
    private int startY11;
    //Y坐标偏移值
    private int yOffset0;
    private int yOffset1;
    private int yOffset2;
    private int yOffset3;
    private int yOffset4;
    private int yOffset5;
    private int yOffset6;
    private int yOffset7;
    private int yOffset8;
    private int yOffset9;
    private int yOffset10;
    private int yOffset11;
    // 波形绘制矩形区域
    private Rect rect;
    private Thread drawThread; //监控绘制线程是否停止
    /**
     * 构造函数
     * @param context 上下文
     * @param attrs 属性设置
     */
    public EcgViewFor12(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.surfaceHolder = getHolder();
        this.surfaceHolder.addCallback(this);

        //将surfaceView背景变为透明
        setZOrderOnTop(true);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);

        initAttributes();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 清除画布
        Canvas canvas = holder.lockCanvas(null);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        holder.unlockCanvasAndPost(canvas);

        initAttributes();
        initWaveDraw(); // 此时相应的控件尺寸确定下来，可以初始化波形绘制相应的参数了
        resetData(); // 复位波形数据
        setWaveGain(); // 设置增益和更新背景
        startThread(); // 启动波形绘制线程
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * 心电控件销毁
     * @param holder 销毁回调
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopThread(); // 销毁时停止波形绘制线程
    }

    @Override
    public void addEcgData(int leadParam, int data) {
        if (ecg0Data.size() > MIX_DATA) {
            resetData();
        }
        switch (leadParam) {
            case KParamType.ECG_I:
                ecg0Data.add(data);
                break;
            case KParamType.ECG_II:
                ecg1Data.add(data);
                break;
            case KParamType.ECG_III:
                ecg2Data.add(data);
                break;
            case KParamType.ECG_AVR:
                ecg3Data.add(data);
                break;
            case KParamType.ECG_AVL:
                ecg4Data.add(data);
                break;
            case KParamType.ECG_AVF:
                ecg5Data.add(data);
                break;
            case KParamType.ECG_V1:
                ecg6Data.add(data);
                break;
            case KParamType.ECG_V2:
                ecg7Data.add(data);
                break;
            case KParamType.ECG_V3:
                ecg8Data.add(data);
                break;
            case KParamType.ECG_V4:
                ecg9Data.add(data);
                break;
            case KParamType.ECG_V5:
                ecg10Data.add(data);
                break;
            case KParamType.ECG_V6:
                ecg11Data.add(data);
                break;
            default:
                break;
        }
    }

    /**
     * 设置波形速率
     * 重新初始化波形数据绘制。
     */
    public void setWaveSpeed() {
        resetData();
        this.waveSpeed = getWaveSpeedConfig();
        stopThread();

        // 清除画布
        canvas = surfaceHolder.lockCanvas(null);
        if (canvas == null) {
            return;
        }
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        surfaceHolder.unlockCanvasAndPost(canvas);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    CrashReport.postCatchedException(e);
                }
                UiUitls.post(new Runnable() {
                    @Override
                    public void run() {
                        initAttributes();
                        setWaveGain();
                        initWaveDraw();
                        resetData();
                        startThread();
                    }
                });
            }
        }).start();
    }

    /**
     * 设置波形增益
     */
    public void setWaveGain() {
        float gain = getWaveGainConfig();

        if (gain == GlobalConstant.INVALID_DATA) {
            isAutoGain = true;
            waveGain = 2f;
        } else {
            isAutoGain = false;
            waveGain = gain;
        }

        drawBackground();
    }

    /**
     * 初始化属性
     */
    private void initAttributes() {
        // 每毫米有多少像素。1280屏幕横向像素数；216.576为屏幕横向宽度，单位mm
        final float pxPerMm = 1280f / 216.576f;
        float pxPerSecond; //每秒画多少像素

        // 初始化矩形波形绘制区域
        rect = new Rect();

        // 初始化画笔
        wavePaint = new Paint();
        wavePaint.setStrokeWidth(2);
        wavePaint.setColor(Color.parseColor("#7CFC00"));
        wavePaint.setAntiAlias(false); // 设置抗锯齿
        // 初始化其他波形绘制相关的参数
        waveSpeed = getWaveSpeedConfig();
        pxPerSecond = waveSpeed * pxPerMm; // 计算每秒画多少像素
        lockWidth = pxPerSecond * (SLEEP_TIME / MS_TIME); // 每次锁屏所需画的宽度
        ecgXOffset = lockWidth / ecgPerCount;
    }

    /**
     * 波形绘制初始化
     */
    private void initWaveDraw() {
        final int columnNum = 2; // 波形列数
        final int rowNum = 6; // 波形行数

        waveWidgetWidth = getWidth() / columnNum;
        waveWidgetHeight = getHeight() / rowNum;

        startX0 = (int) (pixelPerMm * 4) + 1;
        startX1 = (int) (waveWidgetWidth + pixelPerMm * 4) + 1; //第二列与第一列分开
        startX2 = waveWidgetWidth * 2;
        startX3 = waveWidgetWidth * 3;
        //Math.round(offset + 2) 该值为了使波形对准基线而调试
        startY0 = waveWidgetHeight / 2 + Math.round(offset); // 波1初始Y坐标(控件的二分之一处Y坐标)
        startY1 = waveWidgetHeight + waveWidgetHeight / 2 + Math.round(offset + 1.5f);
        startY2 = waveWidgetHeight * 2 + waveWidgetHeight / 2 + Math.round(offset + 2);
        startY3 = waveWidgetHeight * 3 + waveWidgetHeight / 2 + Math.round(offset + 3);
        startY4 = waveWidgetHeight * 4 + waveWidgetHeight / 2 + Math.round(offset + 3.5f);
        startY5 = waveWidgetHeight * 5 + waveWidgetHeight / 2 + Math.round(offset + 4);
        startY6 = waveWidgetHeight / 2 + Math.round(offset);
        startY7 = waveWidgetHeight + waveWidgetHeight / 2 + Math.round(offset + 1.5f);
        startY8 = waveWidgetHeight * 2 + waveWidgetHeight / 2 + Math.round(offset + 2);
        startY9 = waveWidgetHeight * 3 + waveWidgetHeight / 2 + Math.round(offset + 3);
        startY10 = waveWidgetHeight * 4 + waveWidgetHeight / 2 + Math.round(offset + 3.5f);
        startY11 = waveWidgetHeight * 5 + waveWidgetHeight / 2 + Math.round(offset + 4);
        yOffset0 = Math.round(offset) + 1;
        yOffset1 = waveWidgetHeight + Math.round(offset + 2f * zoom);
        yOffset2 = waveWidgetHeight * 2 + Math.round(offset + 2.5f * zoom);
        yOffset3 = waveWidgetHeight * 3 + Math.round(offset + 3f * zoom);
        yOffset4 = waveWidgetHeight * 4 + Math.round(offset + 4f * zoom);
        yOffset5 = waveWidgetHeight * 5 + Math.round(offset + 4.5f * zoom);
        yOffset6 = Math.round(offset);
        yOffset7 = waveWidgetHeight + Math.round(offset + 2f * zoom);
        yOffset8 = waveWidgetHeight * 2 + Math.round(offset + 2.5f * zoom);
        yOffset9 = waveWidgetHeight * 3 + Math.round(offset + 3f * zoom);
        yOffset10 = waveWidgetHeight * 4 + Math.round(offset + 4f * zoom);
        yOffset11 = waveWidgetHeight * 5 + Math.round(offset + 4.5f * zoom);
    }

    /**
     * 获取波形速率配置
     * @return 波形速率
     */
    private float getWaveSpeedConfig() {
        int speed = SpUtils.getSpInt(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                , EcgDefine.ECG_VELOCITY_SYSTEM, EcgDefine.ECG_VELOCITY_DEFAULT);

        if (speed == 0) {
            return 5f;
        } else if (speed == 1) {
            return 6.25f;
        } else if (speed == 2) {
            return 10f;
        } else if (speed == 3) {
            return 12.5f;
        } else if (speed == 4) {
            return 25f;
        } else if (speed == 5) {
            return 50f;
        } else {
            return 25f;
        }
    }

    /**
     * 获取波形增益配置
     * @return 波形增益
     */
    private float getWaveGainConfig() {
        int gain = SpUtils.getSpInt(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                , EcgDefine.ECG_AMPLITUDE_SYSTEM, EcgDefine.ECG_AMPLITUDE_DEFAULT);

        if (gain == 0) {
            return 0.25f;
        } else if (gain == 1) {
            return 0.5f;
        } else if (gain == 2) {
            return 1f;
        } else if (gain == 3) {
            return 2f;
        } else if (gain == 4) {
            return GlobalConstant.INVALID_DATA;
        } else {
            return 1f;
        }
    }

    /**
     * 启动线程
     */
    public void startThread() {
        isRunning = true;
//        心电波形不一致卡顿
        if (drawThread == null) {
            drawThread = new Thread(drawRunnable);
            drawThread.start();
        } else {
            if (!drawThread.isAlive()) {
                drawThread = new Thread(drawRunnable);
                drawThread.start();
            }
        }
    }

    /**
     * 停止线程
     */
    public void stopThread() {
        isRunning = false;
    }

    /**
     * 波形绘制线程
     */
    Runnable drawRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRunning) {
                long startTime = System.currentTimeMillis();
                if (ecg11Data.size() > ecgPerCount) {
                    startDrawWaveGroup0();
                    startDrawWaveGroup1();
                }
                adjustPerCount();

                long endTime = System.currentTimeMillis();
                if (endTime - startTime < SLEEP_TIME) {
                    try {
                        Thread.sleep(SLEEP_TIME - (endTime - startTime));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        CrashReport.postCatchedException(e);
                    }
                }
            }
        }
    };

    /**
     * 绘制第1列波形
     */
    private void startDrawWaveGroup0() {

        //是否需要重置X坐标，是否画到最末端了
        boolean resetStartX = false;
        //需要绘制的点数，默认值
        int drawSize = ecgPerCount;
        //判断边界
        int column1End = (int) (waveWidgetWidth - pixelPerMm * 2.5);
        //重置位置
        int column1Start = (int) (pixelPerMm * 4) + 1;
        //需要绘制点数占用的最大长度
        int xLength = (int) (ecgXOffset * ecgPerCount * zoom);
        //预计终点位置
        int preEndX = startX0 + xLength;
        //超出边界
        if (preEndX > column1End) {
            drawSize = (int) ((column1End - startX0) / (ecgXOffset * zoom));
            resetStartX = true;
        }
        //预防点残留在画布最前端和最后端，需要在最前端和最后端都多清理一部分画布,所以最前段或最后端+1
        if (startX0 == column1Start) {
            startX1 = (int) (waveWidgetWidth + pixelPerMm * 4) + 1;
            rect.set(startX0 - 1, 0, (int) (startX0 + lockWidth + BLANK_LINE_WIDTH),
                    waveWidgetHeight * 6);
        } else {
            if (resetStartX) {
                rect.set(startX0, 0, (int) (startX0 + lockWidth + BLANK_LINE_WIDTH + 1),
                        waveWidgetHeight * 6);
            }
            rect.set(startX0, 0, (int) (startX0 + lockWidth + BLANK_LINE_WIDTH),
                    waveWidgetHeight * 6);
        }

        canvas = surfaceHolder.lockCanvas(rect);
        if (canvas == null) {
            return;
        }

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

        Point point0 = drawWave(startX0, startY0, ecg0Data, yOffset0, 0, drawSize);
        Point point1 = drawWave(startX0, startY1, ecg1Data, yOffset1, 1, drawSize);
        Point point2 = drawWave(startX0, startY2, ecg2Data, yOffset2, 2, drawSize);
        Point point3 = drawWave(startX0, startY3, ecg3Data, yOffset3, 3, drawSize);
        Point point4 = drawWave(startX0, startY4, ecg4Data, yOffset4, 4, drawSize);
        Point point5 = drawWave(startX0, startY5, ecg5Data, yOffset5, 5, drawSize);
        startX0 = point0.x;

        startY0 = point0.y;
        startY1 = point1.y;
        startY2 = point2.y;
        startY3 = point3.y;
        startY4 = point4.y;
        startY5 = point5.y;
        surfaceHolder.unlockCanvasAndPost(canvas);
        if (resetStartX) {
//            修改待机一段时间后2列波形走动不一致问题
            clearData(ecg0Data);
            clearData(ecg1Data);
            clearData(ecg2Data);
            clearData(ecg3Data);
            clearData(ecg4Data);
            clearData(ecg5Data);
        }
        if (resetStartX) {
            startX0 = column1Start;
        }
    }

    /**
     * 绘制第2列波形
     */
    private void startDrawWaveGroup1() {
        //是否需要重置X坐标
        boolean resetStartX = false;
        //需要绘制的点数，默认值
        int drawSize = ecgPerCount;

        //第二列起始位置
        int column2Start = (int) (waveWidgetWidth + pixelPerMm * 4) + 1;
        //第二列终点
        int column2End = (int) (getWidth() - pixelPerMm * 2.5f);

        //需要绘制点数占用的最大长度
        int xLength = (int) (ecgXOffset * ecgPerCount * zoom);
        //预计终点位置
        int preEndX = startX1 + xLength;
        //超出边界
        if (preEndX > column2End) {
            drawSize = (int) ((column2End - startX1) / (ecgXOffset * zoom));
            resetStartX = true;
        }
        //预防点残留在画布最前端和最后端，需要在最前端和最后端都多清理一部分画布
        if (startX1 == column2Start) {
            rect.set(startX1 - 1, 0, (int) (startX1 + lockWidth + BLANK_LINE_WIDTH),
                    waveWidgetHeight * 6);
        } else {
            if (resetStartX) {
                rect.set(startX1, 0, (int) (startX1 + lockWidth + BLANK_LINE_WIDTH + 1),
                        waveWidgetHeight * 6);
            }
            rect.set(startX1, 0, (int) (startX1 + lockWidth + BLANK_LINE_WIDTH),
                    waveWidgetHeight * 6);
        }
        canvas = surfaceHolder.lockCanvas(rect);
        if (canvas == null) {
            return;
        }

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        Point point6 = drawWave(startX1, startY6, ecg6Data, yOffset6, 0, drawSize);
        Point point7 = drawWave(startX1, startY7, ecg7Data, yOffset7, 1, drawSize);
        Point point8 = drawWave(startX1, startY8, ecg8Data, yOffset8, 2, drawSize);
        Point point9 = drawWave(startX1, startY9, ecg9Data, yOffset9, 3, drawSize);
        Point point10 = drawWave(startX1, startY10, ecg10Data, yOffset10, 4, drawSize);
        Point point11 = drawWave(startX1, startY11, ecg11Data, yOffset11, 5, drawSize);
        startX1 = point6.x;
        startY6 = point6.y;
        startY7 = point7.y;
        startY8 = point8.y;
        startY9 = point9.y;
        startY10 = point10.y;
        startY11 = point11.y;
        surfaceHolder.unlockCanvasAndPost(canvas);

        if (resetStartX) {
            startX1 = column2Start;
        }
    }

    /**
     * 绘制心电波形 第一列
     * @param startX 起始x坐标
     * @param startY 起始y坐标
     * @param data 数据集
     * @param yOffset y偏移值
     * @param waveWidgetHeight 高度的倍数
     * @param drawWaveSize 绘制的点数，绘制的点数，不会超过50，并且绘制个数需要提前计算好，预防越界或者漏描
     * @return 绘制波形的最后一个点的坐标
     */
    private synchronized Point drawWave(int startX, int startY, Queue<Integer> data, int yOffset,
            int waveWidgetHeight, int drawWaveSize) {
        int initX;
        int newX;
        int newY;
        Point point;

        initX = startX;
        point = new Point(startX, startY);
        try {
            for (int i = 0; i < drawWaveSize; i++) {
                if (data.size() != 0 && isRunning) {
                    startX = Math.round(initX + ecgXOffset * i * zoom); //*zoom 调节波速
                    newX = Math.round(initX + ecgXOffset * (i + 1) * zoom);
                    newY = ecgConvert(data.poll()) + yOffset;
                    if (newY < this.waveWidgetHeight * waveWidgetHeight) {
                        newY = this.waveWidgetHeight * waveWidgetHeight;
                    } else if (newY > (this.waveWidgetHeight * waveWidgetHeight
                            + this.waveWidgetHeight)) {
                        newY = this.waveWidgetHeight * waveWidgetHeight +
                                this.waveWidgetHeight;
                    }
                    canvas.drawLine(startX, startY, newX, newY, wavePaint);
                    startX = newX;
                    startY = newY;
                }
            }
            point.x = startX;
            point.y = startY;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return point;
    }

    /**
     * 将心电数据转换成用于显示的Y坐标
     * @param data 心电数值
     * @return 转换后的Y坐标值
     */
    private int ecgConvert(int data) {
        int ecgMax = 4096; // 心电的最大值

        // 自动增益
        if (isAutoGain) {
            data = waveWidgetHeight / 2 - (int) ((data - (ecgMax / 2)) * ECG_Y_RATIO
                    * waveGain * zoom);
            if ((data < 0) || (data > waveWidgetHeight)) {
                if (waveGain == 2f) {
                    waveGain = 1f;
                    Message message = Message.obtain();
                    handler.sendMessage(message);
                } else if (waveGain == 1f) {
                    waveGain = 0.5f;
                    Message message = Message.obtain();
                    handler.sendMessage(message);
                }
            }
        } else {
            data = waveWidgetHeight / 2 - (int) ((data - (ecgMax / 2)) * ECG_Y_RATIO
                    * waveGain * zoom);
        }

        return data;
    }

    /**
     * 动态调节每次画心电数据的个数
     */
    private void adjustPerCount() {
        if (isRunning) {
            // 心电每秒传输500个数据，允许缓存1秒的数据
            if (ecg0Data.size() > 600) {
                // ecgPerCount与waveSpeed成正比。waveSpeed误差不能大于正负10%，因此ecgPerCount的误差
                // 也不能超过正负10%，经计算可知，ecgPerCount不能查过范围45~55。
                if (ecgPerCount < 55) {
                    ecgPerCount++;
                }
            } else if ((ecg0Data.size() < 500) && (ecg0Data.size() != 0)) {
                if (ecgPerCount > 50) {
                    ecgPerCount--;
                }
            }
        }
    }

    /**
     * 复位心电波形数据
     */
    public void resetData() {
        ecg0Data.clear();
        ecg1Data.clear();
        ecg2Data.clear();
        ecg3Data.clear();
        ecg4Data.clear();
        ecg5Data.clear();
        ecg6Data.clear();
        ecg7Data.clear();
        ecg8Data.clear();
        ecg9Data.clear();
        ecg10Data.clear();
        ecg11Data.clear();
    }

    /**
     * 画心电背景图
     * 说明：此函数必须在SurfaceCreated执行之后调用
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void drawBackground() {
        final int width = getWidth();
        final int height = getHeight();

        final Drawable drawable = new Drawable() {
            @Override
            public void draw(@NonNull Canvas canvas) {
                canvas.drawColor(Color.BLACK);
                drawEcgGrid(canvas, width, height);
                drawEcgWidgetGrid(canvas, width, height);
                drawEcgRuler(canvas, width, height);
                drawEcgTitle(canvas, width, height);
            }

            @Override
            public void setAlpha(int alpha) {
            }

            @Override
            public void setColorFilter(ColorFilter cf) {
            }

            @Override
            public int getOpacity() {
                return PixelFormat.UNKNOWN;
            }
        };
        this.setBackground(drawable);
    }

    /**
     * 画心电测量界面的网格
     * @param canvas 画布
     * @param width 绘画宽度
     * @param height 绘画高度
     */
    private void drawEcgGrid(Canvas canvas, int width, int height) {
        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(0xFF4c4c4c);
        paint.setTextSize(14);
        paint.setAlpha(150);
        paint.setAntiAlias(true);

        // 单位:像素/mm,1280为屏幕横向像素数，216.576为屏幕横向长度（单位mm）
        int widthLen = (int) (width / pixelPerMm); //转换为mm长度
        int heightLen = (int) (height / pixelPerMm); //转换为mm长度

        float startX;
        float startY;
        float stopX;
        float stopY;

        // 画X轴,每隔5mm画一条线
        paint.setStrokeWidth(2.0f);
        startX = 0;
        stopX = width;
        for (int i = 0; i < heightLen; i++) {
            startY = (float) (i * pixelPerMm * 5);
            stopY = (float) (i * pixelPerMm * 5);
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }

        // 画Y轴,每隔5mm画一条线
        paint.setStrokeWidth(2.0f);
        startY = 0;
        stopY = height;
        for (int i = 0; i < widthLen; i++) {
            startX = (float) (i * pixelPerMm * 5);
            stopX = (float) (i * pixelPerMm * 5);
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }
    }

    /**
     * 画心电分割虚线
     * @param canvas 画布
     * @param width 绘画宽度
     * @param height 绘画高度
     */
    private void drawEcgWidgetGrid(Canvas canvas, int width, int height) {
        float waveWidgetWidth = (float) width / 2;

        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        Path path = new Path();
        path.moveTo(waveWidgetWidth, 0);
        path.lineTo(waveWidgetWidth, height);
        PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        paint.setPathEffect(effects);
        canvas.drawPath(path, paint);
    }

    /**
     * 画心电测量界面的 “几” 字形标尺
     * @param canvas 画布
     * @param width 绘画宽度
     * @param height 绘画高度
     */
    private void drawEcgRuler(Canvas canvas, int width, int height) {
        float startX;
        float startY;
        float stopX;
        float stopY;
        float waveWidgetWidth = (float) width / 2;
        float waveWidgetHeight = (float) height / 6;
        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);

        // i列数，j行数
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 6; j++) {
                startX = i * waveWidgetWidth;
                startY = waveWidgetHeight / 2 + waveWidgetHeight * j + offset;
                stopX = i * waveWidgetWidth + (float) pixelPerMm;
                stopY = startY;
                canvas.drawLine(startX, startY, stopX, stopY, paint); //画第一横

                startX = stopX;
                startY = stopY;
                stopX = startX;
                stopY = waveWidgetHeight / 2 + waveWidgetHeight * j - (float) pixelPerMm * 10 *
                        waveGain
                        + offset;
                canvas.drawLine(startX, startY, stopX, stopY, paint); //画第一竖

                startX = stopX;
                startY = stopY;
                stopX = i * waveWidgetWidth + (float) pixelPerMm * 3;
                stopY = startY;
                canvas.drawLine(startX, startY, stopX, stopY, paint); //画第二横

                startX = stopX;
                startY = stopY;
                stopX = startX;
                stopY = waveWidgetHeight / 2 + waveWidgetHeight * j + offset;
                canvas.drawLine(startX, startY, stopX, stopY, paint); //画第二竖

                startX = stopX;
                startY = stopY;
                stopX = i * waveWidgetWidth + (float) pixelPerMm * 4;
                stopY = startY;
                canvas.drawLine(startX, startY, stopX, stopY, paint); //画第三横
            }
        }
    }

    /**
     * 画心电波形标题
     * @param canvas 画布
     * @param width 绘画宽度
     * @param height 绘画高度
     */
    private void drawEcgTitle(Canvas canvas, int width, int height) {
        float startX;
        float startY;
        float waveWidgetWidth = (float) width / 2;
        float waveWidgetHeight = (float) height / 6;
        String title;
        String gainString;

        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(20);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);  //设置画出的线的 粗细程度

        gainString = getGainString();

        // 标题放在该条波形的布局的(20, 30)位置
        startX = waveWidgetWidth * 0 + 20;
        startY = waveWidgetHeight * 0 + 30;
        title = UiUitls.getString(R.string.ecg_i) + " " + gainString;
        canvas.drawText(title, startX, startY, paint);
        startX = waveWidgetWidth * 0 + 20;
        startY = waveWidgetHeight * 1 + 30;
        title = UiUitls.getString(R.string.ecg_ii) + " " + gainString;
        canvas.drawText(title, startX, startY, paint);
        startX = waveWidgetWidth * 0 + 20;
        startY = waveWidgetHeight * 2 + 30;
        title = UiUitls.getString(R.string.ecg_iii) + " " + gainString;
        canvas.drawText(title, startX, startY, paint);
        startX = waveWidgetWidth * 0 + 20;
        startY = waveWidgetHeight * 3 + 30;
        title = UiUitls.getString(R.string.ecg_avr) + " " + gainString;
        canvas.drawText(title, startX, startY, paint);
        startX = waveWidgetWidth * 0 + 20;
        startY = waveWidgetHeight * 4 + 30;
        title = UiUitls.getString(R.string.ecg_avl) + " " + gainString;
        canvas.drawText(title, startX, startY, paint);
        startX = waveWidgetWidth * 0 + 20;
        startY = waveWidgetHeight * 5 + 30;
        title = UiUitls.getString(R.string.ecg_avf) + " " + gainString;
        canvas.drawText(title, startX, startY, paint);
        startX = waveWidgetWidth * 1 + 10;
        startY = waveWidgetHeight * 0 + 30;
        title = UiUitls.getString(R.string.V1) + " " + gainString;
        canvas.drawText(title, startX, startY, paint);
        startX = waveWidgetWidth * 1 + 10;
        startY = waveWidgetHeight * 1 + 30;
        title = UiUitls.getString(R.string.V2) + " " + gainString;
        canvas.drawText(title, startX, startY, paint);
        startX = waveWidgetWidth * 1 + 10;
        startY = waveWidgetHeight * 2 + 30;
        title = UiUitls.getString(R.string.V3) + " " + gainString;
        canvas.drawText(title, startX, startY, paint);
        startX = waveWidgetWidth * 1 + 10;
        startY = waveWidgetHeight * 3 + 30;
        title = UiUitls.getString(R.string.V4) + " " + gainString;
        canvas.drawText(title, startX, startY, paint);
        startX = waveWidgetWidth * 1 + 10;
        startY = waveWidgetHeight * 4 + 30;
        title = UiUitls.getString(R.string.V5) + " " + gainString;
        canvas.drawText(title, startX, startY, paint);
        startX = waveWidgetWidth * 1 + 10;
        startY = waveWidgetHeight * 5 + 30;
        title = UiUitls.getString(R.string.V6) + " " + gainString;
        canvas.drawText(title, startX, startY, paint);
    }

    /**
     * 获的作为标题的增益字符串
     * @return 当前增益字符串
     */
    private String getGainString() {
        int gain = SpUtils.getSpInt(UiUitls.getContent(), GlobalConstant.APP_CONFIG
                , EcgDefine.ECG_AMPLITUDE_SYSTEM, EcgDefine.ECG_AMPLITUDE_DEFAULT);
        if (gain == 0) {
            return "x0.25";
        } else if (gain == 1) {
            return "x0.5";
        } else if (gain == 2) {
            return "x1";
        } else if (gain == 3) {
            return "x2";
        } else {
            return "x1";
        }
    }

    /**
     * 接收绘图线程的消息，更新背景
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            drawBackground();
        }
    };

    /**
     * 缓存数据过多则清除
     * @param data 缓存数据
     */
    private void clearData(Queue<Integer> data) {
        if (data.size() > (ecgPerCount * MULTIPLE_DATA)) {
            List<Integer> ecg = new ArrayList<>();
            for (int i = 0; i < ecgPerCount * MULTIPLE_DATA; i++) {
                try {
                    ecg.add(data.poll());
                } catch (Exception e) {
                    e.printStackTrace();
                    CrashReport.postCatchedException(e);
                }
            }
            data.clear();
            data.addAll(ecg);
        }
    }

    /**
     * 设置布局整体元素的缩放
     * @param again 倍数
     */
    public void setLayoutZoom(float again) {
        pixelPerMm = pixelPerMm * again;
        zoom = again;
        drawBackground();
        initWaveDraw();
    }
}
