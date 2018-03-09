package com.konsung.defineview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;

import com.konsung.R;
import com.konsung.bean.LineChatBean;
import com.konsung.util.GlobalConstant;
import com.konsung.util.OverCheckUtil;
import com.konsung.util.UiUitls;

import java.util.ArrayList;

/**
 * 此类为趋势图，基于BrokenLineTable，将原来单趋势值绘图，整合到一张图表
 */
public class StatisticalPic extends LineTable {

    //X轴方向偏移量
    private static final float TEXT_POSITION_DEVIATION_X = 5f;
    //Y轴方向偏移量
    private static final float TEXT_POSITION_DEVIATION_Y = 5f;
    //ColorRes,画笔颜色
    private int[] colors = {R.color.chart_color, R.color.text_color6, R.color.text_color8,
            R.color.text_color7};
    //显示值的背景
    private int[] valuesBackground = {R.drawable.result_orange, R.drawable.result_green, R
            .drawable.resulit_olive, R.drawable.result_blue};

    private Paint linePaint;
    private Paint textPaint;

    private LineChatBean[] beanArray;
    private Boolean flag;

    //Y轴偏移量
    private static final int Y_DEVIATION = 60;

    /**
     * 构造器
     * @param context 上下文
     * @param beanArray 数据集
     * @param isDrawRight 是否绘制右刻度
     */
    public StatisticalPic(Context context, LineChatBean[] beanArray,
            Boolean isDrawRight) {
        super(context, beanArray[0]);
        this.beanArray = beanArray;
        this.flag = isDrawRight;
        linePaint = new Paint();
        linePaint.setStrokeWidth(4);
        linePaint.setTextSize(12.0f); //设置文字大小
        linePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setStrokeWidth(4);
        textPaint.setTextSize(12.0f); //设置文字大小
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //第一条在父类里面画
        for (int i = 0; i < beanArray.length; i++) {
            if (flag) { //是否绘制右边刻度
                //单位不同才绘制右边刻度
                if (!beanArray[0].getUnit().equals(beanArray[i].getUnit())) {
                    drawPulseRateY(canvas, beanArray[i]);
                }
            }
            linePaint.setColor(UiUitls.getColor(colors[i]));
            //绘制记录线条
            drawLine(canvas, beanArray[i]);
        }
        for (int i = 0; i < beanArray.length; i++) {
            //绘制记录数字
            drawTextAbortLine(i, canvas, beanArray[i]);
        }
    }

    /**
     * 绘制线条
     * @param canvas 画布
     * @param bean2 数据集
     */
    public void drawLine(Canvas canvas, LineChatBean bean2) {
        ArrayList<float[]> dataCache = getData(bean2);
        for (int i = 0; i < dataCache.size(); i++) {
            if (bean2.getColorsId() != null) {
                linePaint.setColor(bean2.getColorsId()[i]);
            }
            if (dataCache.get(i).length > 2) {
//                canvas.drawLines(dataCache.get(i), paintLine);
                //有数据就花出来，没有数据就空着
                for (int j = 0; j < dataCache.get(i).length - 2; j += 2) {
                    float x1 = dataCache.get(i)[j];
                    float y1 = dataCache.get(i)[j + 1];
                    float x2 = dataCache.get(i)[j + 2];
                    float y2 = dataCache.get(i)[j + 3];
                    if (x1 != GlobalConstant.INVALID_DATA && x2 !=
                            GlobalConstant.INVALID_DATA && y1 !=
                            GlobalConstant.INVALID_DATA && y2 !=
                            GlobalConstant.INVALID_DATA) {
                        canvas.drawLine(x1, y1, x2, y2, linePaint);
                    }
                }
            }
        }
    }

    /**
     * 绘制线上数据
     * @param position 位置
     * @param canvas 画布
     * @param bean2 数据集
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void drawTextAbortLine(int position, Canvas canvas, LineChatBean bean2) {
        ArrayList<float[]> linedataCache = getLineData(bean2);
        for (int k = 0; k < linedataCache.size(); k++) {
            for (int i = 0, j = 0; j < linedataCache.get(k).length; i++, j +=
                    2) {
                if (linedataCache.get(k)[j] != GlobalConstant.INVALID_DATA) {
                    //需要绘制到线上的文本
                    String text = UiUitls.getValueAfterFactor(bean2.getParam(),
                            (int) bean2.getValues().get(k)[i]);
                    float x = linedataCache.get(k)[j];
                    float y = linedataCache.get(k)[j + 1];

                    linePaint.setColor(UiUitls.getColor(colors[position]));

                    //获取字符串占用空间大小
                    Rect textRect = new Rect();
                    textPaint.getTextBounds(text, 0, text.length(), textRect);

                    float downDeviation = 0f;
                    //由于position=2,3时，点九图有个点在上方，文字需要下移一点
                    if (position >= 2) {
                        downDeviation = 7f;
                    }

                    float textStartX = x + getXDirection(position, textRect);
                    float textStartY = y + getYDirection(position, textRect);

                    //绘制数值背景
                    Bitmap valueBackground = BitmapFactory.decodeResource(getResources(),
                            valuesBackground[position]);
                    NinePatch ninePatch = new NinePatch(valueBackground,
                            valueBackground.getNinePatchChunk());
                    ninePatch.setPaint(new Paint());

                    //+-1是因为文本框需要比文字大点
                    Rect rect = new Rect((int) textStartX - 2,
                            (int) textStartY - 10 - 1,
                            (int) (textStartX + textRect.width()) + 2,
                            (int) (textStartY + textRect.height()) + 1);
                    ninePatch.draw(canvas, rect);

                    //绘制文本
                    canvas.drawText(text, textStartX, textStartY + downDeviation,
                            textPaint);
                    //绘制外边框
                    drawExCircle(canvas, x, y, 8, getResources().getColor(
                            R.color.menuText), bean2);
                    //绘制内圆
                    canvas.drawCircle(x, y, 6, linePaint);
                }
            }
        }
    }

    /**
     * 获取X轴方向
     * @param position 数据位置
     * @param textRect 文本占用空间
     * @return 返回X轴方向偏移量
     */
    private float getXDirection(int position, Rect textRect) {
        switch (position) {
            case 0:
            case 3:
                return TEXT_POSITION_DEVIATION_X;
            case 1:
            case 2:
                return -TEXT_POSITION_DEVIATION_X - textRect.width();
            default:
                return 5;
        }
    }

    /**
     * 获取Y轴方向
     * @param position 数据位置
     * @param textRect 文本空间
     * @return 返回Y轴方向正负
     */
    private float getYDirection(int position, Rect textRect) {
        switch (position) {
            case 0:
            case 1:
                return -TEXT_POSITION_DEVIATION_Y - textRect.height();
            case 2:
            case 3:
                return TEXT_POSITION_DEVIATION_Y + textRect.height();
            default:
                return 10;
        }
    }

    /**
     * 绘制脉率Y轴
     * @param canvas 画布
     * @param bean 数据内容
     */
    private void drawPulseRateY(Canvas canvas, LineChatBean bean) {
        //设置画笔
        Paint paint = new Paint();
        paint.setStrokeWidth(bean.getXOryDimen());
        paint.setColor(getResources().getColor(R.color.text_color6));
        paint.setTextSize(12.0f); //设置文字大小
        paint.setAntiAlias(true);
        //绘制横坐标
        float x = getWidth() - bean.getPaddingLeft();
        //间距
        float value = bean.getMaxValue() / bean.getYSize();
        //间距像素
        float pix = (getHeight() - bean.getPaddingBottom() - bean
                .getPaddingTop()) / ((bean.getMaxValue() - bean
                .getMinValue()) * 1.1f);
        // 绘制y轴
        canvas.drawLine(x, bean.getPaddingTop(), x, getHeight() - bean
                .getPaddingBottom(), paint);

        //纵坐标刻度个数
        int ySize = bean.getYSize();
        for (int i = 0; i <= ySize; i++) {
            //绘制文本的纵坐标
            float y = getHeight() - bean.getPaddingBottom() - pix * (value *
                    i) + 5;
            //绘制刻度上的文本，判断是否带小数点
            if (UiUitls.whetherResultIsInt(bean.getParam())) {
                canvas.drawText(String.valueOf((int) value * i), x + 5, y, paint);
            } else {
                canvas.drawText(String.valueOf(value * i), x + 5, y, paint);
            }
            //刻度长度
            int xLength = 4;
            //绘制y轴刻度
            canvas.drawLine(x - xLength, getHeight() - bean.getPaddingBottom() - pix * (value *
                    i), x, getHeight() - bean.getPaddingBottom() - pix * (value * i), paint);
        }
        //绘制单位
        paint.setTextSize(15.0f);
        canvas.drawText(bean.getUnit(), getWidth()
                - bean.getPaddingRight() - 50.0f, bean.getPaddingTop()
                + 10.0f, paint);
    }

    /**
     * 转换为数据转换为x轴需要的float[]
     * @param bean2 数据集
     * @return 转换为x轴需要的float[]
     */
    private ArrayList<float[]> getData(LineChatBean bean2) {
        ArrayList<float[]> dataList = new ArrayList<>();
        for (int j = 0; j < bean2.getValues().size(); j++) {
            ArrayList<Float> list = new ArrayList();
            Float[] data = null;
            for (int i = 0; i < bean2.getValues().get(j).length; i++) {
                if (bean2.getValues().get(j)[i] != GlobalConstant.INVALID_DATA) {
                    //横坐标
                    float x = bean2.getPaddingLeft() + bean2.getScaleX() * (i + 1);
                    list.add(x);
                    float values = getYValue(bean2.getParam(), bean2.getValues().get(j)[i]);
                    //单位像素
                    float pix = (getHeight() - bean2.getPaddingBottom() -
                            bean2.getPaddingTop()) / ((bean2.getMaxValue() - bean2
                            .getMinValue()) * 1.1f);
                    //像素位置
                    float y = getHeight() - ((values - bean2.getMinValue()) *
                            pix + bean2.getPaddingTop() + Y_DEVIATION);
                    list.add(y);

                    if (i > 0 && i < bean2.getValues().get(j).length - 1) {
                        list.add(x);
                        list.add(y);
                    }
                } else {
                    list.add(-1000f);
                    list.add(-1000f);
                    list.add(-1000f);
                    list.add(-1000f);
                }
            }
            data = list.toArray(new Float[0]);

            float[] result = new float[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = data[i].floatValue();
            }
            dataList.add(result);
        }

        return dataList;
    }

    /**
     * 转换为数据转换为x轴需要的float[]
     * @param bean2 数据集
     * @return 转换为x轴需要的float[]
     */
    private ArrayList<float[]> getLineData(LineChatBean bean2) {

        ArrayList<float[]> dataList = new ArrayList<>();
        for (int j = 0; j < bean2.getValues().size(); j++) {
            ArrayList<Float> list = new ArrayList();
            Float[] data = null;
            for (int i = 0; i < bean2.getValues().get(j).length; i++) {
                if (bean2.getValues().get(j)[i] != GlobalConstant.INVALID_DATA) {
                    //横坐标
                    float x = bean2.getPaddingLeft() + bean2.getScaleX() * (i + 1);
                    list.add(x);
                    float values = getYValue(bean2.getParam(), bean2.getValues().get(j)[i]);

                    //单位像素
                    float pix = (getHeight() - bean2.getPaddingBottom() -
                            bean2.getPaddingTop()) / ((bean2.getMaxValue() - bean2
                            .getMinValue()) * 1.1f);

                    //像素位置
                    float y = getHeight() - ((values - bean2.getMinValue()) *
                            pix + bean2.getPaddingTop() + Y_DEVIATION);

                    list.add(y);
                } else {
                    list.add(-1000f);
                    list.add(-1000f);
                }
            }
            data = list.toArray(new Float[0]);

            float[] result = new float[data.length];
            for (int i = 0; i < data.length; i++) {
                result[i] = data[i].floatValue();
            }
            dataList.add(result);
        }
        return dataList;
    }

    /**
     * 折线图描点画外圈圆
     * @param canvas 画布
     * @param x x轴
     * @param y y轴
     * @param radius 直径
     * @param color 颜色
     * @param bean 数据内容
     */
    private void drawExCircle(Canvas canvas, float x, float y, int radius, int
            color, LineChatBean bean) {
        Paint paint = new Paint();
        // 去锯齿
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setShadowLayer(1, 0, 0, 0x99999999);
        // 设置paint的外框宽度
        paint.setStrokeWidth(0.5f);
        canvas.drawCircle(x, y, radius, paint);
    }

    /**
     * 将测量的原始值转化为x坐标需要的值
     * @param param 参数
     * @param value 原值
     * @return x坐标需求值
     */
    private float getYValue(int param, float value) {
        if (value == OverCheckUtil.FLAG_OVER_MAX) {
            String valueStr = OverCheckUtil.getOverMaxString(param, value);
            value = Float.valueOf(valueStr.replace(">", ""));
        } else if (value == OverCheckUtil.FLAG_BELOW_MIN) {
            String valueStr = OverCheckUtil.getOverMinString(param, value);
            value = Float.valueOf(valueStr.replace("<", ""));
        } else {
            value = value / UiUitls.getFactor(param);
        }
        return value;
    }
}
