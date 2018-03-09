package com.konsung.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Base64;

import com.konsung.R;
import com.konsung.bean.TaioHeacheckData;

import java.util.ArrayList;
import java.util.List;

/**
 * 心电图
 */
public class EcgImage {
    private static final int SingleWaveHeight = 160; // 单个心电波形高度，单位为像素
    /**
     * 画12导心电图，布局方式为并列
     * @param canvas 画布
     * @param thd 待画的心电波形数据
     * @throws Exception 异常
     */
    public static void drawImage(Canvas canvas, TaioHeacheckData thd) throws Exception {
        // 画网格
        drawEcgGrid(canvas, canvas.getWidth(), canvas.getHeight());

        int[] value0 = intValue(thd.getECG_I());
        int[] value1 = intValue(thd.getECG_II());
        int[] value2 = intValue(thd.getECG_III());
        int[] value3 = intValue(thd.getECG_aVR());
        int[] value4 = intValue(thd.getECG_aVL());
        int[] value5 = intValue(thd.getECG_aVF());
        int[] value6 = intValue(thd.getECG_V1());
        int[] value7 = intValue(thd.getECG_V2());
        int[] value8 = intValue(thd.getECG_V3());
        int[] value9 = intValue(thd.getECG_V4());
        int[] value10 = intValue(thd.getECG_V5());
        int[] value11 = intValue(thd.getECG_V6());

        float xStart = 0;
        float yStart = 0;
        drawOneWave(UiUitls.getString(R.string.ecg_title_I), canvas, value0,
                xStart, yStart);
        yStart += SingleWaveHeight;
        drawOneWave(UiUitls.getString(R.string.ecg_title_II), canvas, value1,
                xStart, yStart);
        yStart += SingleWaveHeight;
        drawOneWave(UiUitls.getString(R.string.ecg_title_III), canvas, value2,
                xStart, yStart);
        yStart += SingleWaveHeight;
        drawOneWave(UiUitls.getString(R.string.ecg_title_AVR), canvas, value3,
                xStart, yStart);
        yStart += SingleWaveHeight;
        drawOneWave(UiUitls.getString(R.string.ecg_title_AVL), canvas, value4,
                xStart, yStart);
        yStart += SingleWaveHeight;
        drawOneWave(UiUitls.getString(R.string.ecg_title_AVF), canvas, value5,
                xStart, yStart);
        yStart += SingleWaveHeight;
        drawOneWave(UiUitls.getString(R.string.ecg_title_V1), canvas, value6,
                xStart, yStart);
        yStart += SingleWaveHeight;
        drawOneWave(UiUitls.getString(R.string.ecg_title_V2), canvas, value7,
                xStart, yStart);
        yStart += SingleWaveHeight;
        drawOneWave(UiUitls.getString(R.string.ecg_title_V3), canvas, value8,
                xStart, yStart);
        yStart += SingleWaveHeight;
        drawOneWave(UiUitls.getString(R.string.ecg_title_V4), canvas, value9,
                xStart, yStart);
        yStart += SingleWaveHeight;
        drawOneWave(UiUitls.getString(R.string.ecg_title_V5), canvas, value10,
                xStart, yStart);
        yStart += SingleWaveHeight;
        drawOneWave(UiUitls.getString(R.string.ecg_title_V6), canvas, value11,
                xStart, yStart);
    }

    /**
     * 画心电测量界面的网格
     * @param canvas 画布
     */
    public static void drawEcgGrid(Canvas canvas, int width, int height) {
        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        paint.setAntiAlias(true);

        // 在网格右上角写入波形走速和增益幅度，作为网格标题，走速默认为25mm/s，
        // 增益默认为10mm/mv
        float gridTitleX = width - 230;
        float gridTitleY = 20;
        String gridTitle = UiUitls.getString(R.string.grid_title);
        canvas.drawText(gridTitle, gridTitleX, gridTitleY, paint);

        // 设置画笔
//        Paint paint = new Paint();
        paint.setColor(0xFA8072);
        paint.setTextSize(14);
        paint.setAlpha(150);
        paint.setAntiAlias(true);

        // 单位:像素/mm,1280为屏幕横向像素数，216.96为屏幕横向长度（单位mm）
        double pixelPerMm = 1280 / 216.96;
        int widthLen = (int) (width / pixelPerMm);//转换为mm长度
        int heightLen = (int) (height / pixelPerMm);//转换为mm长度

        float startX;
        float startY;
        float stopX;
        float stopY;

        // 画X轴,每隔5mm画一条线
        paint.setStrokeWidth(2.0f);
        startX = 0;
        stopX = width;
        for (int i = 0; i < heightLen; i++) {
            startY = (float)(i * pixelPerMm * 5);
            stopY = (float)(i * pixelPerMm * 5);
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }

        // 画Y轴,每隔5mm画一条线
        paint.setStrokeWidth(2.0f);
        startY = 0;
        stopY = height;
        for (int i = 0; i < widthLen; i++) {
            startX = (float)(i * pixelPerMm * 5);
            stopX = (float)(i * pixelPerMm * 5);
            canvas.drawLine(startX, startY, stopX, stopY, paint);
        }
    }

    /**
     * 画一条心电波形，包括标题和标尺
     * @param title 心电波形标题
     * @param canvas 画布
     * @param value 待画的心电波形数据
     * @param xStart x方向起点
     * @param yStart y方向起点
     */
    public static void drawOneWave(String title, Canvas canvas, int[] value,
                                   float xStart, float yStart) throws Exception {
        float[] points1 = new float[20000];
        float[] points2 = new float[20000];
        int j = 0;

        // 画该导联波形的标题
        drawEcgTitle(title, canvas, xStart, yStart);

        // 画该导联波形的标尺
        drawEcgRuler(canvas, 1, canvas.getWidth(), SingleWaveHeight, xStart,
                yStart);

        //设置画笔
        Paint paint = new Paint();
        paint.setStrokeWidth(1);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);

        // 采样率500，波形走速25mm/s，屏幕横向分辨率1280，屏幕宽度216.96mm
        // 根据以上数据计算出每个点对应的像素值
        float pointPerPix = (1280 / 216.96f) / (500 / 25);//单位：像素/点
        int pointNum = value.length;
        if (pointNum >= 4) {
            if (pointNum % 2 != 0) {
                pointNum --;
            }
            for (int i = 0; i < pointNum; i++) {
                xStart = i * pointPerPix;
                points1[j * 2] = xStart;

                // 2048为基线的AD值，1280为屏幕横向分辨率，216.96mm为屏幕宽度，
                // 2150为标志高值，1946为标尺低值
                points1[j * 2 + 1] = yStart + 160f / 2f - ((float) value[i] -
                        2048) * (1280f / 216.96f) / ((2150f - 1946f) / 10);

                if (j >= 1) {
                    points2[(j - 1) * 2] = points1[j * 2];
                    points2[(j - 1) * 2 + 1] = points1[j * 2 + 1];
                }

                j ++;
            }
            points2[(j - 1 -1) * 2] = 0;
            points2[(j - 1 - 1) * 2 + 1] = 0;
        }
        canvas.drawLines(points1, paint);
        canvas.drawLines(points2, paint);
    }

    /**
     * 画心电波形标题
     * @param canvas 画布
     */
    private static void drawEcgTitle(String title, Canvas canvas, float xStart,
                                    float yStart) {
        float startX;
        float startY;

        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);  //设置画出的线的 粗细程度

        // 标题放在该条波形的布局的(10, 30)位置
        startX = xStart + 10;
        startY = yStart + 30;

        canvas.drawText(title, startX, startY, paint);
    }

    /**
     * 画心电测量界面的标尺
     * @param canvas 画布
     */
    private static void drawEcgRuler(Canvas canvas, float gain, float width,
                                    float height, float xStart, float yStart) {
        float startX;
        float startY;
        float stopX;
        float stopY;

        // 单位:像素/mm,1280为屏幕横向像素数，216.96为屏幕横向长度（单位mm）
        double pixelPerMm = 1280 / 216.96;

        // 设置画笔
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAlpha(150);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(3.0f);

        // 标尺长度为1cm，横向上放在六分之五处，纵向上位于画布中央
        startX = xStart + width * 5 / 6;
        startY = yStart + height / 2 - (float) pixelPerMm * 5 * gain;
        stopX = xStart + width * 5 / 6;
        stopY = yStart + height / 2 + (float) pixelPerMm * 5 * gain;
        canvas.drawLine(startX, startY, stopX, stopY, paint);
    }

    @SuppressWarnings("restriction")
    private static int[] intValue(String value) throws Exception {
        byte[] out = Base64.decode(value.getBytes(), Base64.DEFAULT);
        byte[] f = new byte[4];
        f[3] = out[0];
        f[2] = out[1];
        f[1] = out[2];
        f[0] = out[3];
        List<Integer> vList = new ArrayList<Integer>();
        for (int i = 4; i < out.length; i++) {
            f = new byte[2];
            f[1] = out[i + 0];
            f[0] = out[i + 1];
            vList.add(toInt(f));
            i++;
        }
        int[] b = new int[vList.size()];
        for (int i = 0; i < vList.size(); i++) {
            b[i] = vList.get(i);
        }
        return b;
    }

    public static int toInt(byte[] bRefArr) {
        int iOutcome = 0;
        byte bLoop;
        for (int i = 0; i < bRefArr.length; i++) {
            bLoop = bRefArr[i];
            iOutcome += (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
    }
}
