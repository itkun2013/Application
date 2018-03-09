package com.konsung.defineview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.konsung.R;
import com.konsung.bean.InitChartBean;
import com.konsung.util.global.GlobalNumber;

/**
 * 此类为血氧图标界面，基于BrokenLineTable，将原来单趋势值绘图，整合到一张图表
 * <p/>
 * <p/>
 * modified by: kgh
 * last modified date: 2015-11-30
 * ver:
 */
public class BloodHbgChart extends BrokenLineTable {
    private Paint hrPaint;
    private InitChartBean bean;
    private InitChartBean hrBean;

    /**
     * 构造器
     * @param context 上下文引用
     * @param bean bean数据
     * @param hrBean bean数据
     */
    public BloodHbgChart(Context context, InitChartBean bean, InitChartBean hrBean) {
        super(context, bean);
        this.bean = bean;
        this.hrBean = hrBean;

        hrPaint = new Paint();
        hrPaint.setStrokeWidth(bean.getxOryDimen());
        hrPaint.setColor(getResources().getColor(R.color.report_text_color));
        //设置文字大小
        hrPaint.setTextSize(GlobalNumber.TWELVE_NUMBER_FLOAT);
        hrPaint.setAntiAlias(true);

        this.setPaintLine(this.hrPaint);
        this.setPaintText(this.hrPaint);
        this.setBean(this.bean);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        // 构建脉率数据
//        builHrDatas();

        //绘制记录线条
        super.drawLine(canvas);
        //绘制记录数字
        super.drawTextAbortLine(canvas);

        // 构建脉率数据
        builHrDatas(canvas);
        drawPulseRateY(canvas);
        canvas.drawText("%", getWidth() - bean.getPADDING_RIGHT()
                - GlobalNumber.THIRTY_NUMBER_FLOAT
                , bean.getPADDING_TOP() + GlobalNumber.TEN_NUMBER_FLOAT, this.hrPaint);
    }

    /**
     * 绘制线条
     * @param canvas 画布
     */
    private void builHrDatas(Canvas canvas) {
        this.reset();
        hrPaint.setColor(getResources().getColor(R.color.grass_konsung_2));
        this.setPaintLine(this.hrPaint);
        this.setPaintText(this.hrPaint);
        this.setBean(this.hrBean);

        //绘制记录线条
        super.drawLine(canvas);
        //绘制记录数字
        super.drawTextAbortLine(canvas);
    }

    /**
     * 绘制脉率Y轴
     * @param canvas 画布
     */
    private void drawPulseRateY(Canvas canvas) {
        //绘制横坐标
        float x = getWidth() - bean.getPADDING_LEFT() + GlobalNumber.TEN_NUMBER;

        //间距
        float value = GlobalNumber.SIXTY_NUMBER / bean.getY_SIZE();

        //间距像素
        float pix = (getHeight() - bean.getPADDING_BOTTOM() - bean
                .getPADDING_TOP()) / GlobalNumber.SIXTY_NUMBER;

        // 绘制y轴
        canvas.drawLine(x, bean.getPADDING_TOP(), x, getHeight() - bean
                .getPADDING_BOTTOM(), hrPaint);

        //纵坐标刻度个数
        int ySize = bean.getY_SIZE();
        for (int i = 0; i <= ySize; i++) {
            //绘制文本的纵坐标
            float y = getHeight() - bean.getPADDING_BOTTOM()
                    - pix * (value * i) + GlobalNumber.FIVE_NUMBER;
            canvas.drawText(String.valueOf(value * i), x + GlobalNumber.FIVE_NUMBER, y, hrPaint);

            //刻度长度
            int xLength = GlobalNumber.FOUR_NUMBER;
            //绘制y轴刻度 x1 y1 x2 y2
            canvas.drawLine(x - xLength, getHeight() - bean.getPADDING_BOTTOM() - pix * (value * i)
                    , x, getHeight() - bean.getPADDING_BOTTOM() - pix * (value * i),
                    hrPaint);
        }
    }

    /**
     * 构造器
     * @param context 上下文
     * @param attrs 属性
     */
    public BloodHbgChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
