package com.konsung.defineview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.konsung.R;
import com.konsung.bean.InitChartBean;

/**
 * 此类为血氧图标界面，基于BrokenLineTable，将原来单趋势值绘图，整合到一张图表
 * <p/>
 * <p/>
 * modified by: kgh
 * last modified date: 2015-11-30
 * ver:
 */
public class Spo2Chart extends BrokenLineTable {
    private Paint hrPaint;
    private InitChartBean bean;
    private InitChartBean hrBean;

    public Spo2Chart(Context context, InitChartBean bean, InitChartBean
            hrBean) {
        super(context, bean);
        this.bean = bean;
        this.hrBean = hrBean;

        hrPaint = new Paint();
        hrPaint.setStrokeWidth(bean.getxOryDimen());
        hrPaint.setColor(getResources().getColor(R.color.grass_konsung_2));
        hrPaint.setTextSize(12.0f);//设置文字大小
        hrPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        drawPulseRateY(canvas);

        // 构建脉率数据
//        builHrDatas();

        //绘制记录线条
        super.drawLine(canvas);
        //绘制记录数字
        super.drawTextAbortLine(canvas);

//        canvas.drawText("bpm", getWidth() - bean.getPADDING_RIGHT() - 30.0f,
//                bean.getPADDING_TOP() + 10.0f, this.hrPaint);
    }

    private void builHrDatas() {
        this.reset();
        this.setPaintLine(this.hrPaint);
        this.setPaintText(this.hrPaint);
        this.setBean(this.hrBean);
    }

    /**
     * 绘制脉率Y轴
     *
     * @param canvas
     */
    private void drawPulseRateY(Canvas canvas) {
        //绘制横坐标
        float x = getWidth() - bean.getPADDING_LEFT() + 10;

        //间距
        float value = hrBean.getMax_value() / bean.getY_SIZE();

        //间距像素
        float pix = (getHeight() - bean.getPADDING_BOTTOM() - bean
                .getPADDING_TOP()) / hrBean.getMax_value();

        // 绘制y轴
        canvas.drawLine(x, bean.getPADDING_TOP(), x, getHeight() - bean
                .getPADDING_BOTTOM(), hrPaint);

        //纵坐标刻度个数
        int y_size = bean.getY_SIZE();
        for (int i = 0; i <= y_size; i++) {
            //绘制文本的纵坐标
            float y = getHeight() - bean.getPADDING_BOTTOM() - pix * (value *
                    i) + 5;
            canvas.drawText(String.valueOf(value * i), x + 5, y, hrPaint);

            //刻度长度
            int X_LENGTH = 4;
            //绘制y轴刻度
            canvas.drawLine(x - X_LENGTH,  // x1
                    getHeight() - bean.getPADDING_BOTTOM() - pix * (value *
                            i), // y1
                    x,  // x2
                    getHeight() - bean.getPADDING_BOTTOM() - pix * (value *
                            i), // y2
                    hrPaint);

        }
    }

    public Spo2Chart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
