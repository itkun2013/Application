package com.konsung.defineview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.konsung.R;
import com.konsung.bean.LineChatBean;
import com.konsung.util.GlobalConstant;
import com.konsung.util.UiUitls;

import java.util.ArrayList;

/**
 * 测量项趋势图
 */
public class LineTable extends View {
    //初始化容器
    private LineChatBean bean;
    private Paint paintX;
    private Paint paintY;
    private Paint paintLine;
    private Paint paintText;
    //转换数据的缓存
    private ArrayList<float[]> dataCache = null;
    //转换线上数据缓存
    private ArrayList<float[]> linedataCache = null;
    //是否为整型
    private Boolean flag;

    /**
     * 构造方法
     * @param context 上下文
     * @param bean 图表参数
     */
    public LineTable(Context context, LineChatBean bean) {
        super(context);
        this.bean = bean;
        setInitBean(bean);

        //判断是否为整型
        flag = UiUitls.whetherResultIsInt(bean.getParam());
    }

    /**
     * 构造方法
     * @param context 上下文
     * @param bean 绘制的数据源
     * @param flag 标识,区分小数点
     */
    public LineTable(Context context, LineChatBean bean, Boolean flag) {
        super(context);
        this.flag = flag;
        setInitBean(bean);
    }

    /**
     * 构造
     * @param context 上下文
     * @param attrs 参数
     */
    public LineTable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 初始化数据源
     * @param bean 数据源
     */
    public void setInitBean(LineChatBean bean) {
        this.bean = bean;
        paintX = new Paint();
        paintX.setStrokeWidth(bean.getXOryDimen());
        paintX.setColor(getResources().getColor(R.color.background_nor));
        paintX.setTextSize(12.0f);
        paintX.setAntiAlias(true);

        paintY = new Paint();
        paintY.setStrokeWidth(bean.getXOryDimen());
        paintY.setColor(getResources().getColor(R.color.chart_color));
        paintY.setTextSize(12.0f);
        paintY.setAntiAlias(true);

        paintLine = new Paint();
        paintLine.setStrokeWidth(4);
        paintLine.setColor(getResources().getColor(R.color.chart_color));
        paintLine.setAntiAlias(true);

        paintText = new Paint();
        paintText.setStrokeWidth(bean.getXOryDimen());
        paintText.setColor(getResources().getColor(R.color.chart_color));
        paintText.setTextSize(14.0f);
        paintText.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawColor(Color.parseColor("#f7f7f7"));
        //绘制xy轴
        drawXY(canvas);
        if (dataCache != null) {
            //绘制记录线条
            drawLine(canvas);
//        //绘制记录数字
            drawTextAbortLine(canvas);
        }
    }

    /**
     * 绘制xy轴
     * @param canvas 画布
     */
    private void drawXY(Canvas canvas) {
        canvas.drawLine(bean.getPaddingLeft(), getHeight() - bean.getPaddingBottom(), getWidth()
                - bean.getPaddingRight(), getHeight() - bean.getPaddingBottom(), paintX);
        //绘制x轴参数——————x轴暂定最大数量为100
        int sizeX = bean.getXValues().length > 10 ? 10 : bean.getXValues().length;

        //初始描点数据位置
        int position = 0;
        if (sizeX < 100) {
            position = 0;
        } else {
            position = sizeX - 100;
        }
        //绘制X轴数据
        for (int i = 0; position < sizeX; position++) {
            i++; //第几点
            String valueX = bean.getXValues()[position];
            //时间格式中存在;就断开
            if (valueX.contains(";")) {
                String[] time = valueX.split(";");
                //直接写的数字是校准数值
                //绘制日期
                drawText(canvas, time[0], bean.getPaddingLeft() + bean
                        .getScaleX() * i - 35.0f, getHeight() - bean
                        .getPaddingBottom() + 55.0f, paintX, -35);
                //绘制时分秒
                drawText(canvas, time[1], bean.getPaddingLeft() + bean
                        .getScaleX() * i - 28.0f, getHeight() - bean
                        .getPaddingBottom() + 70.0f, paintX, -35);
            } else { //不存在，就直接绘制时间
                drawText(canvas, valueX, bean.getPaddingLeft() + bean
                        .getScaleX() * i - 30.0f, getHeight() - bean
                        .getPaddingBottom() + 50.0f, paintX, -35);
            }
            //刻度长度
            int lengthY = 4;
            //绘制刻度 x1 y1 x2 y2
            canvas.drawLine(bean.getPaddingLeft() + bean.getScaleX() * i
                    , getHeight() - bean.getPaddingBottom()
                    , bean.getPaddingLeft() + bean.getScaleX() * i
                    , getHeight() - bean.getPaddingBottom() - lengthY, paintX);
        }

        //绘制y轴
        canvas.drawLine(bean.getPaddingLeft(), bean.getPaddingTop(), bean
                .getPaddingLeft(), getHeight() - bean.getPaddingBottom(), paintX);
        //根据布局大小和容量，设置Y轴间距
        float scaleY = (getHeight() - bean.getPaddingTop() -
                bean.getPaddingBottom()) / (bean.getSize() + 1); // Y轴需要多出一节，让文本不被遮挡
        bean.setScaleY(scaleY);

        //纵坐标刻度个数
        int sizeY = bean.getYSize();
        for (int i = 0; i <= sizeY + 1; i++) {
            //绘制横坐标
            float x = bean.getPaddingLeft() - 50.0f;
            //间距
            float value = (bean.getMaxValue() - bean.getMinValue()) / bean
                    .getYSize();
            //间距像素
            float pix = (getHeight() - bean.getPaddingBottom() - bean
                    .getPaddingTop()) / ((bean.getMaxValue() - bean
                    .getMinValue()) * 1.1f);

            //绘制文本的纵坐标
            float y = getHeight() - bean.getPaddingBottom() - pix * (value *
                    i) + 5;

            float textValue = bean.getMinValue() + value * i;
            if (bean.isDecimal()) {
                textValue = (float) (Math.round(textValue * 1000)) / 1000;
            }
            if (i != sizeY + 1) {
                if (flag) {
                    canvas.drawText(String.valueOf((int) textValue), x + 10, y, paintY);
                } else {
                    canvas.drawText(String.valueOf(textValue), x + 10, y, paintY);
                }
            }

            //刻度长度
            int lengthX = 4;
            //绘制y轴刻度
            canvas.drawLine(bean.getPaddingLeft(), getHeight() - bean
                    .getPaddingBottom() - pix * (value * i), bean
                    .getPaddingLeft() + lengthX, getHeight() - bean
                    .getPaddingBottom() - pix * (value * i), paintX);
        }

        //绘制单位
        paintY.setTextSize(15.0f);
        canvas.drawText(bean.getUnit(), bean.getPaddingLeft() + 10.0f,
                bean.getPaddingTop() + 10.0f, paintX);
    }

    /**
     * 绘制线条
     * @param canvas 画布
     */
    public void drawLine(Canvas canvas) {
        if (dataCache == null) {
            dataCache = getData();
        }
        for (int i = 0; i < dataCache.size(); i++) {
            if (bean.getColorsId() != null) {
                paintLine.setColor(bean.getColorsId()[i]);
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
                        canvas.drawLine(x1, y1, x2, y2, paintLine);
                    }
                }
            }
        }
    }

    /**
     * 绘制线上数据
     * @param canvas 画布
     */
    public void drawTextAbortLine(Canvas canvas) {
        if (linedataCache == null) {
            linedataCache = getLineData();
        }
        for (int k = 0; k < linedataCache.size(); k++) {
            for (int i = 0, j = 0; j < linedataCache.get(k).length; i++, j +=
                    2) {
                if (linedataCache.get(k)[j] != GlobalConstant.INVALID_DATA) {
                    String text = "";
                    if (flag) {
                        text = String.valueOf((int) bean.getValues().get(k)[i]);
                    } else {
                        text = String.valueOf(bean.getValues().get(k)[i]);
                    }
                    float x = linedataCache.get(k)[j];
                    float y = linedataCache.get(k)[j + 1];

                    if (bean.getColorsId() != null) {
                        paintText.setColor(bean.getColorsId()[i]);
                    }

                    canvas.drawText(text, x - 5.f, y - 10, paintText);
                    drawExCircle(canvas, x, y, 10, getResources().getColor(R.color.background));
                    canvas.drawCircle(x, y, 6, paintLine);
                }
            }
        }
    }

    /**
     * 转换为数据转换为x轴需要的float[]
     * @return x轴需要的float[]
     */
    private ArrayList<float[]> getData() {
        ArrayList<float[]> dataList = new ArrayList<>();
        for (int j = 0; j < bean.getValues().size(); j++) {
            ArrayList<Float> list = new ArrayList();
            Float[] data = null;
            for (int i = 0; i < bean.getValues().get(j).length; i++) {
                if (bean.getValues().get(j)[i] != GlobalConstant.INVALID_DATA) {
                    //横坐标
                    float x = bean.getPaddingLeft() + bean.getScaleX() * (i
                            + 1);
                    list.add(x);

                    float values = bean.getValues().get(j)[i] / UiUitls.getFactor(bean.getParam());
                    //单位像素
                    float pix = (getHeight() - bean.getPaddingBottom() -
                            bean.getPaddingTop()) / ((bean.getMaxValue() -
                            bean.getMinValue()) * 1.1f);
                    //像素位置
                    float y = getHeight() - ((values - bean.getMinValue()) *
                            pix + bean.getPaddingTop() + 40);
                    list.add(y);

                    if (i > 0 && i < bean.getValues().get(j).length - 1) {
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
     * @return x轴需要的float[]
     */
    private ArrayList<float[]> getLineData() {

        ArrayList<float[]> dataList = new ArrayList<>();
        for (int j = 0; j < bean.getValues().size(); j++) {
            ArrayList<Float> list = new ArrayList();
            Float[] data = null;
            for (int i = 0; i < bean.getValues().get(j).length; i++) {
                if (bean.getValues().get(j)[i] != GlobalConstant.INVALID_DATA) {
                    //横坐标
                    float x = bean.getPaddingLeft() + bean.getScaleX() * (i
                            + 1);
                    list.add(x);
                    float values = bean.getValues().get(j)[i];
                    //单位像素
                    float pix = (getHeight() - bean.getPaddingBottom() -
                            bean.getPaddingTop()) / ((bean.getMaxValue() -
                            bean.getMinValue()) * 1.1f);
                    //像素位置
                    float y = getHeight() - ((values - bean.getMinValue()) *
                            pix + bean.getPaddingTop() + 40);
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
     * 设置文字画笔
     * @param paintText 文字画笔
     */
    public void setPaintText(Paint paintText) {
        this.paintText = paintText;
    }

    /**
     * 设置线条画笔
     * @param paintLine 线条画笔
     */
    public void setPaintLine(Paint paintLine) {
        this.paintLine = paintLine;
    }

    /**
     * 设置绘制数据源
     * @param bean 数据源
     */
    public void setBean(LineChatBean bean) {
        this.bean = bean;
    }

    /**
     * 重置数据
     */
    public void reset() {
        this.dataCache = null;
        this.linedataCache = null;
    }

    /**
     * 折线图描点画外圈圆
     * @param canvas 画布
     * @param x 圆形 x轴
     * @param y y轴
     * @param radius 半径
     * @param color 颜色
     */
    private void drawExCircle(Canvas canvas, float x, float y, int radius, int
            color) {
        Paint paint = new Paint();
        // 去锯齿
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
        // 设置paint的外框宽度
        paint.setStrokeWidth(bean.getXOryDimen());
        canvas.drawCircle(x, y, radius, paint);
    }

    /**
     * 绘制倾斜字体
     * @param canvas 画布
     * @param text 文本内容
     * @param x x轴
     * @param y y轴
     * @param paint 画笔
     * @param angle 倾斜角度
     */
    public void drawText(Canvas canvas, String text, float x, float y, Paint paint, float angle) {
        if (angle != 0) {
            canvas.rotate(angle, x, y);
        }
        canvas.drawText(text, x, y, paint);
        if (angle != 0) {
            canvas.rotate(-angle, x, y);
        }
    }
}
