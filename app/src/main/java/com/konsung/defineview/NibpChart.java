package com.konsung.defineview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.konsung.R;
import com.konsung.bean.InitChartBean;

import java.util.ArrayList;

/**
 * 此类为血压图表界面，基于BrokenLineTable，将原来单趋势值绘图，整合到一张图表
 * <p/>
 * <p/>
 * modified by: kgh
 * last modified date: 2015-11-30
 * ver:
 */
public class NibpChart extends BrokenLineTable {
    private Paint hrPaint;
    private InitChartBean bean;
    private ArrayList<float[]> diaDatas;
    private ArrayList<float[]> avgDatas;
    private ArrayList<float[]> pulseDatas;
    private boolean isDrawRightUnit = true;

    public NibpChart(Context context,
                     InitChartBean bean,
                     ArrayList<float[]> diaDatas,
                     ArrayList<float[]> pulseDatas) {
        super(context, bean);
        this.bean = bean;
        this.diaDatas = diaDatas;
        this.pulseDatas = pulseDatas;

        hrPaint = new Paint();
        hrPaint.setStrokeWidth(bean.getxOryDimen());
        hrPaint.setColor(Color.BLACK);
        hrPaint.setTextSize(12.0f);
        hrPaint.setAntiAlias(true);

        this.setPaintLine(this.hrPaint);
        this.setPaintText(this.hrPaint);
    }

    public NibpChart(Context context,
                     InitChartBean bean,
                     ArrayList<float[]> diaDatas,
                     ArrayList<float[]> avgDatas,
                     ArrayList<float[]> pulseDatas) {
        super(context, bean);
        this.bean = bean;
        this.diaDatas = diaDatas;
        this.avgDatas = avgDatas;
        this.pulseDatas = pulseDatas;

        hrPaint = new Paint();
        hrPaint.setStrokeWidth(bean.getxOryDimen());
        hrPaint.setColor(Color.BLACK);
        hrPaint.setTextSize(12.0f);
        hrPaint.setAntiAlias(true);

        this.setPaintLine(this.hrPaint);
        this.setPaintText(this.hrPaint);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.drawDatas(canvas, diaDatas, Color.BLUE); // 舒张压
        if (avgDatas != null)
        this.drawDatas(canvas, avgDatas, Color.GREEN); // 平均压
        this.drawDatas(canvas, pulseDatas, getResources().getColor(R.color.grass_konsung_2)); // 脉率
        if (isDrawRightUnit) { //血脂四项也用到该类，但不需要右边刻度线。所以屏蔽绘制。不影响血压绘制
            drawPulseRateY(canvas);
            canvas.drawText("bpm", getWidth() - bean.getPADDING_RIGHT() - 30.0f,
                    bean.getPADDING_TOP() + 10.0f, this.hrPaint);
        }
    }

    private void drawDatas(Canvas canvas, ArrayList<float[]> datas, int color) {
        this.reset();
        this.bean.setValues(datas);
        this.hrPaint.setColor(color);
        this.hrPaint.setStrokeWidth(1.0f);

        //绘制记录线条
        super.drawLine(canvas);
        //绘制记录数字
        super.drawTextAbortLine(canvas);

    }

    /**
     * 绘制脉率Y轴
     *
     * @param canvas
     */
    private void drawPulseRateY(Canvas canvas) {
        //绘制横坐标
        float x = getWidth() - bean.getPADDING_LEFT() + 10;
        float value;
        if (bean.getY_SIZE() != 0) {
            //间距
             value = 300 / bean.getY_SIZE();
        }else{
            return;
        }

        //间距像素
        float pix = (getHeight() - bean.getPADDING_BOTTOM() - bean
                .getPADDING_TOP()) / 300;

        // 绘制y轴
        canvas.drawLine(x, bean.getPADDING_TOP(), x, getHeight() - bean
                .getPADDING_BOTTOM(), hrPaint);
        //纵坐标刻度个数
        int y_size = bean.getY_SIZE();
        for (int i = 0; i <= y_size; i++) {
            //绘制文本的纵坐标
            float y = getHeight() - bean.getPADDING_BOTTOM() - pix * (value *
                    i) + 5;
            canvas.drawText(String.valueOf((int) value * i), x + 5, y, hrPaint);

            //刻度长度
            int X_LENGTH = 4;
            //绘制y轴刻度
                canvas.drawLine(x - X_LENGTH,  // x1
                        getHeight() - bean.getPADDING_BOTTOM() - pix * (value * i), // y1
                        x,  // x2
                        getHeight() - bean.getPADDING_BOTTOM() - pix * (value * i), // y2
                        hrPaint);

        }
    }

    public NibpChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InitChartBean getBean() {
        return bean;
    }

    public Paint getHrPaint() {
        return hrPaint;
    }

    public void setDrawRightUnit(boolean is) {
        isDrawRightUnit = is;
    }
}
