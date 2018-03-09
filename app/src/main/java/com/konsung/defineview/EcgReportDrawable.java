package com.konsung.defineview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Base64;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.util.KParamType;
import com.konsung.util.UiUitls;
import com.konsung.util.UnitConvertUtil;

/**
 * 心电报告绘制
 */

public class EcgReportDrawable extends Drawable {
    private float zoom = 0.5f;
    private float pixelPerMm = (float) (1280 / 216.576 * zoom);

    private int height; // 高
    private int width; //宽
    private MeasureDataBean measureBean;

    Handler handler = new Handler();
    Canvas canvas;
    Paint paint;

    RectF rect;
    private float waveWidgetHeight = 63; //为了适应多应用心电报告页面，单条波形高度默认设置为63px
    private boolean hasWaveData; //本地是否有波形数据

    /**
     * 构造器
     * @param measureBean 数据
     * @param height 画布高度
     * @param width 画布宽度
     * @param hasWaveData 是否有波形数据
     */
    public EcgReportDrawable(MeasureDataBean measureBean, int height, int width, boolean
            hasWaveData) {
        this.height = height;
        this.width = width;
        this.measureBean = measureBean;
        this.hasWaveData = hasWaveData;

        paint = new Paint();
        rect = new RectF(0, 0, width, height);
    }

    @Override
    public void draw(final Canvas canvas) {
        this.canvas = canvas;
        new Thread(drawLineRunnable).start();
        handler.post(drawTitleRunnable);
        if (hasWaveData) {
            handler.post(drawWaveRunnable);
        }
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

    Runnable drawLineRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                drawLine(canvas, width, height);
                drawPoint(canvas, width, height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Runnable drawTitleRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                drawDottedCurve(canvas, width, height);
                drawRuler(canvas, width, height);
                drawTitle(canvas, width, height);
                drawWaveData(canvas, width, height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Runnable drawWaveRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                drawWave(canvas, width, height);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 绘制网格线
     * @param canvas 画布
     * @param width 宽
     * @param height 高
     */
    private void drawLine(Canvas canvas, int width, int height) {
        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(0xedb2b2);
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
            startY = i * pixelPerMm * 5;
            stopY = i * pixelPerMm * 5;
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }

        // 画Y轴,每隔5mm画一条线
        paint.setStrokeWidth(2.0f);
        startY = 0;
        stopY = height;
        for (int i = 0; i < widthLen; i++) {
            startX = i * pixelPerMm * 5;
            stopX = i * pixelPerMm * 5;
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }
    }

    /**
     * 绘制格子里的点
     * @param canvas 画布
     * @param width 宽
     * @param height 高
     */
    private void drawPoint(Canvas canvas, int width, int height) {
        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(0xedb2b2);
        paint.setTextSize(14);
        paint.setAlpha(150);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);
        float startX;
        float startY;
        // 单位:像素/mm,1280为屏幕横向像素数，216.576为屏幕横向长度（单位mm）
        int widthLen = (int) (width / pixelPerMm); //转换为mm长度
        int heightLen = (int) (height / pixelPerMm); //转换为mm长度
        float x;
        float y;
        float gap = pixelPerMm;

        for (int a = 0; a < heightLen; a++) { // 网格边长为5
            startY = a * pixelPerMm * 5;
            for (int b = 0; b < widthLen; b++) {
                startX = b * pixelPerMm * 5;
                for (int c = 1; c < 5; c++) { // 平等分5段 四点
                    for (int d = 1; d < 5; d++) {
                        x = c * gap + startX;
                        y = d * gap + startY;
                        canvas.drawCircle(x, y, (float) (0.25 * pixelPerMm), paint); // 0.25半径
                    }
                }
            }
        }
    }

    /**
     * 绘制分割线 虚线
     * @param canvas 画布
     * @param width 宽
     * @param height 高
     */
    private void drawDottedCurve(Canvas canvas, int width, int height) {
        float waveWidgetWidth = (float) width / 2;
        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.STROKE);
        Path path = new Path();
        path.moveTo(waveWidgetWidth, 0);
        path.lineTo(waveWidgetWidth, waveWidgetHeight * 6);
        PathEffect effects = new DashPathEffect(new float[]{2, 5, 2, 5}, 1); //分割线
        paint.setPathEffect(effects);
        canvas.drawPath(path, paint);
    }

    /**
     * 绘制“几”字形标尺
     * @param canvas 画布
     * @param width 宽
     * @param height 高
     */
    private void drawRuler(Canvas canvas, int width, int height) {
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
                startX = i * waveWidgetWidth;
                startY = waveWidgetHeight / 2 + waveWidgetHeight * j;
                stopX = i * waveWidgetWidth + pixelPerMm;
                stopY = waveWidgetHeight / 2 + waveWidgetHeight * j;
                canvas.drawLine(startX, startY + (float) (pixelPerMm * 2.5), stopX, stopY +
                        (float) (pixelPerMm * 2.5), paint); //画第一横

                startX = i * waveWidgetWidth + pixelPerMm;
                startY = waveWidgetHeight / 2 + waveWidgetHeight * j - pixelPerMm * 10;
                stopX = i * waveWidgetWidth + pixelPerMm * 3;
                stopY = waveWidgetHeight / 2 + waveWidgetHeight * j - pixelPerMm * 10;
                canvas.drawLine(startX, startY + (float) (pixelPerMm * 2.5), stopX, stopY +
                        (float) (pixelPerMm * 2.5), paint); //画第二横

                startX = i * waveWidgetWidth + pixelPerMm * 3;
                startY = waveWidgetHeight / 2 + waveWidgetHeight * j;
                stopX = i * waveWidgetWidth + pixelPerMm * 4;
                stopY = waveWidgetHeight / 2 + waveWidgetHeight * j;
                canvas.drawLine(startX, startY + (float) (pixelPerMm * 2.5), stopX, stopY +
                        (float) (pixelPerMm * 2.5), paint); //画第三横

                startX = i * waveWidgetWidth + pixelPerMm;
                startY = waveWidgetHeight / 2 + waveWidgetHeight * j;
                stopX = i * waveWidgetWidth + pixelPerMm;
                stopY = waveWidgetHeight / 2 + waveWidgetHeight * j - pixelPerMm * 10;
                canvas.drawLine(startX, startY + (float) (pixelPerMm * 2.5), stopX, stopY +
                        (float) (pixelPerMm * 2.5), paint); //画第一竖

                startX = i * waveWidgetWidth + pixelPerMm * 3;
                startY = waveWidgetHeight / 2 + waveWidgetHeight * j;
                stopX = i * waveWidgetWidth + pixelPerMm * 3;
                stopY = waveWidgetHeight / 2 + waveWidgetHeight * j - pixelPerMm * 10;
                canvas.drawLine(startX, startY + (float) (pixelPerMm * 2.5), stopX, stopY +
                        (float) (pixelPerMm * 2.5), paint); //画第二竖
            }
        }
    }

    /**
     * 绘制波形标题
     * @param canvas 画布
     * @param width 宽startY
     * @param height 高startX
     */
    private void drawTitle(Canvas canvas, int width, int height) {
        float startX;
        float startY;
        float waveWidgetWidth = (float) width / 2;

        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(UiUitls.getColor(R.color.report_text_color));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        paint.setTextSize(13);
        Typeface font = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD);
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
            startX = pixelPerMm * 5;
            startY = waveWidgetHeight / 2 + waveWidgetHeight * j - pixelPerMm * 5;
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
            startX = pixelPerMm * 5 + waveWidgetWidth;
            startY = waveWidgetHeight / 2 + waveWidgetHeight * j - pixelPerMm * 5;
            canvas.drawText(titleArr2[j], startX, startY, paint);
        }
    }

    /**
     * 绘制波形数据
     * @param canvas 画布
     * @param width 宽
     * @param height 高
     */
    private void drawWaveData(Canvas canvas, int width, int height) {
        float startX;
        float startY;
        float waveWidgetWidth = (float) width / 2; //宽度一半

        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(UiUitls.getColor(R.color.report_text_color));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        paint.setTextSize(18);
        Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        paint.setTypeface(font);
        startX = pixelPerMm * 4;
        startY = waveWidgetHeight * 7 + pixelPerMm * 6;
        canvas.drawText(UiUitls.getString(R.string.ecg_wave_data), startX, startY, paint);
        startX = 200;
        canvas.drawText(UiUitls.getString(R.string.ecg_wave_info), startX, startY, paint);
    }

    /**
     * 绘制心电波形
     * @param canvas 画布
     * @param width 宽
     * @param height 高
     * @throws Exception 波形异常
     */
    private void drawWave(Canvas canvas, int width, int height) throws Exception {
        float startX;
        float startY;

        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(UiUitls.getColor(R.color.report_text_color));
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        startX = pixelPerMm * 4; //几 标尺的宽度
        //先绘制II导联
        //再绘制6*2波形
        for (int i = 1; i < 7; i++) { // 1-6:I,II,III,AVR,AVL,AVF波形
            if (i < 4) {
                startY = (waveWidgetHeight * (i - 1)); //为了调节波形与几标尺同水平
            } else {
                startY = (waveWidgetHeight * (i - 1));
            }
            String waveStr = Base64.encodeToString(UnitConvertUtil.
                    getByteformHexString(measureBean.getEcgWave(i)), Base64.NO_WRAP);
            int[] ints = UnitConvertUtil.intValue(waveStr);
            // width / 2， 只绘制宽度的一半
            drawEcgWave(ints, canvas, paint, width / 2, height, startX, startY);
        }
        startX = width / 2 + (pixelPerMm * 2); // 屏幕的一半开始绘制
        for (int i = 7; i < 13; i++) { // 7-12 v1-v6波形
            if (i < 10) {
                startY = (waveWidgetHeight * (i - 7));
            } else {
                startY = (waveWidgetHeight * (i - 7));
            }
            String waveStr = Base64.encodeToString(UnitConvertUtil.
                    getByteformHexString(measureBean.getEcgWave(i)), Base64.NO_WRAP);
            int[] ints = UnitConvertUtil.intValue(waveStr);
            drawEcgWave(ints, canvas, paint, width, height, startX, startY);
        }
        startX = pixelPerMm * 4;
        startY = waveWidgetHeight * 6; //为了调节波形与几标尺同水平
        String waveII = Base64.encodeToString(UnitConvertUtil.
                getByteformHexString(measureBean.getEcgWave(KParamType.ECG_II)), Base64.NO_WRAP);
        int[] intII = UnitConvertUtil.intValue(waveII);
        drawEcgWave(intII, canvas, paint, width, height, startX, startY);
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
    private void drawEcgWave(int[] value, Canvas canvas, Paint paint, int width, int height,
            float xStart, float yStart) {
        float[] points1 = new float[20000];
        float[] points2 = new float[20000];
        int j = 0;
        float pointPerPix = (1280 / 216.576f) / (500 / 25); //单位：像素/点
        int pointNum = value.length;
        if (pointNum >= 4) { //超过4个点才绘制
            if (pointNum % 2 != 0) {
                pointNum--;
            }
            for (int i = 0; i < pointNum; i++) {
                if ((((i + 1) * pointPerPix * zoom) + xStart) >= width) {
                    break;
                }
                points1[j * 2] = (i * pointPerPix * zoom) + xStart;

                // 2048为基线的AD值，1280为屏幕横向分辨率，216.576mm为屏幕宽度，
                // 2150为标志高值，1946为标尺低值，zoom波幅缩放倍数
                points1[j * 2 + 1] = yStart + (160f / 2f - ((float) value[i] -
                        2048) * (1280f / 216.576f) / ((2150f - 1946f) / 10)) * zoom;

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
