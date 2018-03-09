package com.konsung.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Typeface;
import android.util.Base64;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;

/**
 * 心电图波形打印绘制类
 */
public class HeartImage {

    private static final int IMAGE_X_START = 104; //心电图起点X坐标
    private static final int IMAGE_Y_START = 600; //心电图起点Y坐标
    private static final int IMAGE_X_STOP = 3202; //心电图终点X坐标
    private static final int IMAGE_Y_STOP = 2264; //心电图终点Y坐标
    private static final float PIXEL_PER_MM = 11.78f; // 单位:像素/mm
    private static final float WAVE_WIDGET_HEIGHT = PIXEL_PER_MM * 5 * 4;
    public static void drawEcg(MeasureDataBean bean, Canvas canvas, int width, int height) {
        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#323333"));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2.0f);
        paint.setTextSize(32);
        canvas.drawText("I导联", 10, 50, paint);
        canvas.drawText("II导联", 10, height/2 + 50, paint);
        canvas.drawLine(0, 150, 20, 150, paint);
        canvas.drawLine(20, 80, 40, 80, paint);
        canvas.drawLine(40, 150, 60, 150, paint);
        canvas.drawLine(20, 80, 20, 150, paint);
        canvas.drawLine(40, 80, 40, 150, paint);
        int temp = height / 2;
        canvas.drawLine(0, 150 + temp, 20, 150 + temp, paint);
        canvas.drawLine(20, 80 + temp, 40, 80 + temp, paint);
        canvas.drawLine(40, 150 + temp, 60, 150 + temp, paint);
        canvas.drawLine(20, 80 + temp, 20, 150 + temp, paint);
        canvas.drawLine(40, 80 + temp, 40, 150 + temp, paint);
        String waveI = Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(bean.get_ecgWave(KParamType.ECG_I)), Base64.NO_WRAP);
        try {
            int[] ints = UnitConvertUtil.intValue(waveI);
            drawEcgWave(ints, canvas, paint, width, height, 60f, height/4 - 80);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String waveII = Base64.encodeToString(UnitConvertUtil.
                getByteformHexString(bean.get_ecgWave(KParamType.ECG_II)), Base64.NO_WRAP);
        try {
            int[] intII = UnitConvertUtil.intValue(waveII);
            drawEcgWave(intII, canvas, paint, width, height, 60, height * 3 /4 - 80);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 绘制心电波形
     * @param bean 测量Bean
     * @param canvas 画布
     * @param width 宽
     * @param height 高
     */
    public static void drawEcgImage(MeasureDataBean bean, Canvas canvas, int width, int height) {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setAntiAlias(true);

        int widthLen = (int) (width / PIXEL_PER_MM / 5); //Y轴需要画的线条数
        int heightLen = (int) (height / PIXEL_PER_MM / 5); //X轴需要画的线条数

        float startX;
        float startY;
        float stopX;
        float stopY;
        // 画X轴,每隔5mm画一条线
        paint.setStrokeWidth(1.0f);
        startX = IMAGE_X_START;
        stopX = IMAGE_X_STOP;
        for (int i = 0; i < heightLen; i++) {
            startY = (i + 1) * PIXEL_PER_MM * 5 + IMAGE_Y_START;
            stopY = (i + 1) * PIXEL_PER_MM * 5 + IMAGE_Y_START;
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }

        // 画Y轴,每隔5mm画一条线
        paint.setStrokeWidth(1.0f);
        startY = IMAGE_Y_START;
        stopY = IMAGE_Y_STOP;
        for (int i = 0; i < widthLen; i++) {
            startX = (i + 1) * PIXEL_PER_MM * 5 + IMAGE_X_START;
            stopX = (i + 1) * PIXEL_PER_MM * 5 + IMAGE_X_START;
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }

        //画点
        float x;
        float y;
        for (int a = 0; a < heightLen + 1; a++) { // 网格边长为5
            startY = a * PIXEL_PER_MM * 5 + IMAGE_Y_START;
            for (int b = 0; b < widthLen + 1; b++) {
                startX = b * PIXEL_PER_MM * 5 + IMAGE_X_START;
                for (int c = 1; c < 5; c++) { // 平等分5段 四点
                    for (int d = 1; d < 5; d++) {
                        x = c * PIXEL_PER_MM + startX;
                        y = d * PIXEL_PER_MM + startY;
                        if (x < IMAGE_X_STOP && y < IMAGE_Y_STOP) { //超过边界的点不画
                            canvas.drawCircle(x, y, (float) (0.1 * PIXEL_PER_MM), paint);
                        }
                    }
                }
            }
        }

        //绘制分割线
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2.0f);
        paint.setStyle(Paint.Style.STROKE);
        Path path = new Path();
        path.moveTo(IMAGE_X_START + width / 2, IMAGE_Y_START);
        path.lineTo(IMAGE_X_START + width / 2, WAVE_WIDGET_HEIGHT * 6 + IMAGE_Y_START); //Y轴共6条波形
        PathEffect effects = new DashPathEffect(new float[]{6, 4, 6, 4}, 1); //分割线
        paint.setPathEffect(effects);
        canvas.drawPath(path, paint);

        drawRuler(canvas, width);
        drawTitle(canvas, width);
        drawWaveData(canvas);
        try {
            drawWave(bean, canvas, width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 绘制“几”字形标尺
     * @param canvas 画布
     * @param width 宽
     */
    private static void drawRuler(Canvas canvas, int width) {
        float startX;
        float startY;
        float stopX;
        float stopY;
        float waveWidgetWidth = (float) width / 2; //屏幕一半

        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(UiUitls.getColor(R.color.report_text_color));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);

        // i列数，j行数
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 7; j++) {
                startX = i * waveWidgetWidth + IMAGE_X_START;
                startY = WAVE_WIDGET_HEIGHT / 2 + WAVE_WIDGET_HEIGHT * j + IMAGE_Y_START;
                stopX = i * waveWidgetWidth + PIXEL_PER_MM + IMAGE_X_START;
                stopY = WAVE_WIDGET_HEIGHT / 2 + WAVE_WIDGET_HEIGHT * j + IMAGE_Y_START;
                canvas.drawLine(startX, startY + (float) (PIXEL_PER_MM * 2.5), stopX, stopY +
                        (float) (PIXEL_PER_MM * 2.5), paint); //画第一横

                startX = i * waveWidgetWidth + PIXEL_PER_MM + IMAGE_X_START;
                startY = WAVE_WIDGET_HEIGHT / 2 + WAVE_WIDGET_HEIGHT * j - PIXEL_PER_MM * 10 +
                        IMAGE_Y_START;
                stopX = i * waveWidgetWidth + PIXEL_PER_MM * 3 + IMAGE_X_START;
                stopY = WAVE_WIDGET_HEIGHT / 2 + WAVE_WIDGET_HEIGHT * j - PIXEL_PER_MM * 10 +
                        IMAGE_Y_START;
                canvas.drawLine(startX, startY + (float) (PIXEL_PER_MM * 2.5), stopX, stopY +
                        (float) (PIXEL_PER_MM * 2.5), paint); //画第二横

                startX = i * waveWidgetWidth + PIXEL_PER_MM * 3 + IMAGE_X_START;
                startY = WAVE_WIDGET_HEIGHT / 2 + WAVE_WIDGET_HEIGHT * j + IMAGE_Y_START;
                stopX = i * waveWidgetWidth + PIXEL_PER_MM * 4 + IMAGE_X_START;
                stopY = WAVE_WIDGET_HEIGHT / 2 + WAVE_WIDGET_HEIGHT * j + IMAGE_Y_START;
                canvas.drawLine(startX, startY + (float) (PIXEL_PER_MM * 2.5), stopX, stopY +
                        (float) (PIXEL_PER_MM * 2.5), paint); //画第三横

                startX = i * waveWidgetWidth + PIXEL_PER_MM + IMAGE_X_START;
                startY = WAVE_WIDGET_HEIGHT / 2 + WAVE_WIDGET_HEIGHT * j + IMAGE_Y_START;
                stopX = i * waveWidgetWidth + PIXEL_PER_MM + IMAGE_X_START;
                stopY = WAVE_WIDGET_HEIGHT / 2 + WAVE_WIDGET_HEIGHT * j - PIXEL_PER_MM * 10 +
                        IMAGE_Y_START;
                canvas.drawLine(startX, startY + (float) (PIXEL_PER_MM * 2.5), stopX, stopY +
                        (float) (PIXEL_PER_MM * 2.5), paint); //画第一竖

                startX = i * waveWidgetWidth + PIXEL_PER_MM * 3 + IMAGE_X_START;
                startY = WAVE_WIDGET_HEIGHT / 2 + WAVE_WIDGET_HEIGHT * j + IMAGE_Y_START;
                stopX = i * waveWidgetWidth + PIXEL_PER_MM * 3 + IMAGE_X_START;
                stopY = WAVE_WIDGET_HEIGHT / 2 + WAVE_WIDGET_HEIGHT * j - PIXEL_PER_MM * 10 +
                        IMAGE_Y_START;
                canvas.drawLine(startX, startY + (float) (PIXEL_PER_MM * 2.5), stopX, stopY +
                        (float) (PIXEL_PER_MM * 2.5), paint); //画第二竖
            }
        }
    }

    /**
     * 绘制波形标题
     * @param canvas 画布
     * @param width 宽startY
     */
    private static void drawTitle(Canvas canvas, int width) {
        float startX;
        float startY;
        float waveWidgetWidth = (float) width / 2;

        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(UiUitls.getColor(R.color.report_text_color));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        paint.setTextSize(40);
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        paint.setTypeface(font);
        String[] titleArr1 = new String[]{
                UiUitls.getString(R.string.ecg_i),
                UiUitls.getString(R.string.ecg_ii),
                UiUitls.getString(R.string.ecg_iii),
                UiUitls.getString(R.string.ecg_avr),
                UiUitls.getString(R.string.ecg_avl),
                UiUitls.getString(R.string.ecg_avf),
                UiUitls.getString(R.string.ecg_ii)};
        // j行数
        for (int j = 0; j < 7; j++) {
            startX = PIXEL_PER_MM * 5 + IMAGE_X_START;
            startY = WAVE_WIDGET_HEIGHT / 2 + WAVE_WIDGET_HEIGHT * j - PIXEL_PER_MM * 5 +
                    IMAGE_Y_START;
            canvas.drawText(titleArr1[j], startX, startY, paint);
        }

        String[] titleArr2 = new String[]{UiUitls.getString(R.string.V1),
                UiUitls.getString(R.string.V2),
                UiUitls.getString(R.string.V3),
                UiUitls.getString(R.string.V4),
                UiUitls.getString(R.string.V5),
                UiUitls.getString(R.string.V6)};
        // j行数
        for (int j = 0; j < 6; j++) {
            startX = PIXEL_PER_MM * 5 + waveWidgetWidth + IMAGE_X_START;
            startY = WAVE_WIDGET_HEIGHT / 2 + WAVE_WIDGET_HEIGHT * j - PIXEL_PER_MM * 5 +
                    IMAGE_Y_START;
            canvas.drawText(titleArr2[j], startX, startY, paint);
        }
    }

    /**
     * 绘制波形数据
     * @param canvas 画布
     */
    private static void drawWaveData(Canvas canvas) {
        float startX;
        float startY;
        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(UiUitls.getColor(R.color.report_text_color));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        paint.setTextSize(32);
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        paint.setTypeface(font);
        startX = PIXEL_PER_MM * 4 + IMAGE_X_START;
        startY = WAVE_WIDGET_HEIGHT * 7 + IMAGE_Y_START;
        canvas.drawText(UiUitls.getString(R.string.ecg_wave_data), startX, startY, paint);
        startX = 500;
        canvas.drawText(UiUitls.getString(R.string.ecg_wave_info), startX, startY, paint);
    }

    /**
     * 绘制心电波形
     * @param bean 测量Bean
     * @param canvas 画布
     * @param width 宽
     * @param height 高
     * @throws Exception 波形异常
     */
    private static void drawWave(MeasureDataBean bean, Canvas canvas, int width, int height) throws
            Exception {
        float startX;
        float startY;

        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(UiUitls.getColor(R.color.report_text_color));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2.0f);
        startX = PIXEL_PER_MM * 4 + IMAGE_X_START; //几 标尺的宽度
        //先绘制II导联
        //再绘制6*2波形
        for (int i = 1; i < 7; i++) { // 1-6:I,II,III,AVR,AVL,AVF波形
            if (i < 4) {
                startY = (WAVE_WIDGET_HEIGHT * (i - 1)) + PIXEL_PER_MM * 5.5f + IMAGE_Y_START;
                //为了调节波形与几标尺同水平
            } else {
                startY = (WAVE_WIDGET_HEIGHT * (i - 1)) + PIXEL_PER_MM * 5.5f + IMAGE_Y_START;
            }
            String waveStr = Base64.encodeToString(UnitConvertUtil.
                    getByteformHexString(bean.getEcgWave(i)), Base64.NO_WRAP);
            int[] ints = UnitConvertUtil.intValue(waveStr);
            // width / 2， 只绘制宽度的一半
            drawEcgWave(ints, canvas, paint, width / 2 + IMAGE_X_START, height, startX, startY);
        }
        startX = width / 2 + (PIXEL_PER_MM * 2) + IMAGE_X_START; // 屏幕的一半开始绘制
        for (int i = 7; i < 13; i++) { // 7-12 v1-v6波形
            if (i < 10) {
                startY = (WAVE_WIDGET_HEIGHT * (i - 7)) + PIXEL_PER_MM * 5.5f + IMAGE_Y_START;
            } else {
                startY = (WAVE_WIDGET_HEIGHT * (i - 7)) + PIXEL_PER_MM * 5.5f + IMAGE_Y_START;
            }
            String waveStr = Base64.encodeToString(UnitConvertUtil.
                    getByteformHexString(bean.getEcgWave(i)), Base64.NO_WRAP);
            int[] ints = UnitConvertUtil.intValue(waveStr);
            drawEcgWave(ints, canvas, paint, width + IMAGE_X_START, height, startX, startY);
        }
        startX = PIXEL_PER_MM * 4 + IMAGE_X_START;
        startY = WAVE_WIDGET_HEIGHT * 6 + IMAGE_Y_START + PIXEL_PER_MM * 5.5f; //为了调节波形与几标尺同水平
        String waveII = Base64.encodeToString(UnitConvertUtil.
                getByteformHexString(bean.getEcgWave(KParamType.ECG_II)), Base64.NO_WRAP);
        int[] intII = UnitConvertUtil.intValue(waveII);
        drawEcgWave(intII, canvas, paint, width + IMAGE_X_START, height, startX, startY);
    }

    /**
     * 根据心电波形数据绘制波形
     * @param value 波形数据
     * @param canvas 画布
     * @param paint 画笔
     * @param width 宽
     * @param height 高
     * @param xStart 初始X坐标
     * @param yStart 初始Y坐标
     */
    private static void drawEcgWave(int[] value, Canvas canvas, Paint paint, int width, int height,
            float xStart, float yStart) {
        float[] points1 = new float[20000];
        float[] points2 = new float[20000];
        int j = 0;
        float pointPerPix = (PIXEL_PER_MM) / (500 / 25); //单位：像素/点
        int pointNum = value.length;
        if (pointNum >= 4) { //超过4个点才绘制
            if (pointNum % 2 != 0) {
                pointNum--;
            }
            for (int i = 0; i < pointNum; i++) {
                if (((i * pointPerPix) + xStart) >= width) {
                    break;
                }
                points1[j * 2] = (i * pointPerPix) + xStart;

                // 2048为基线的AD值，1280为屏幕横向分辨率，216.576mm为屏幕宽度，
                // 2150为标志高值，1946为标尺低值
                points1[j * 2 + 1] = yStart + 160f / 2f - ((float) value[i] - 2048) *
                        (PIXEL_PER_MM) / ((2150f - 1946f) / 10);

                if (j >= 1) {
                    points2[(j - 1) * 2] = points1[j * 2];
                    points2[(j - 1) * 2 + 1] = points1[j * 2 + 1];
                }

                j++;
            }
            points2[(j - 1 - 1) * 2] = 0;
            points2[(j - 1 - 1) * 2 + 1] = 0;
        }
        canvas.drawLines(points1, paint);
        canvas.drawLines(points2, paint);
    }
}
