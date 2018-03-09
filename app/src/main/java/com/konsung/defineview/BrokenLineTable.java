package com.konsung.defineview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.konsung.R;
import com.konsung.bean.InitChartBean;
import com.konsung.util.GlobalConstant;
import com.konsung.util.global.GlobalNumber;

import java.util.ArrayList;

/**
 * TODO:
 * <p/>
 * Created: JustRush
 * Time:    2015/11/10 10:09
 * ver:
 */
public class BrokenLineTable extends View {
    //初始化容器
    private InitChartBean bean;
    private Paint paintXy;
    private Paint paintLine;
    private Paint paintText;
    //转换数据的缓存
    private ArrayList<float[]> dataCache = null;
    //转换线上数据缓存
    private ArrayList<float[]> linedataCache = null;


    /**
     * 构造器
     * @param context 上下文
     * @param bean 数据bean
     */
    public BrokenLineTable(Context context, InitChartBean bean) {
        super(context);
        setInitBean(bean);
    }

    /**
     * 构造器
     * @param context 上下文
     * @param attrs 属性
     */
    public BrokenLineTable(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 设置初始化bean类
     * @param bean 数据
     */
    public void setInitBean(InitChartBean bean) {
        this.bean = bean;
        paintXy = new Paint();
        paintXy.setStrokeWidth(bean.getxOryDimen());
        paintXy.setColor(Color.BLACK);
        paintXy.setTextSize(GlobalNumber.TWELVE_NUMBER_FLOAT);
        paintXy.setAntiAlias(true);
        paintLine = new Paint();
        paintLine.setStrokeWidth(bean.getxOryDimen());
        paintLine.setColor(getResources().getColor(R.color.grass_konsung_2));
        paintLine.setAntiAlias(true);

        paintText = new Paint();
        paintText.setStrokeWidth(bean.getxOryDimen());
        paintText.setColor(getResources().getColor(R.color.grass_konsung_2));
        paintText.setTextSize(GlobalNumber.FOURTEEN_NUMBER_FLOAT);
        paintText.setAntiAlias(true);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawColor(Color.parseColor("#f7f7f7"));
        //绘制xy轴
        drawXY(canvas);
        //绘制记录线条
        drawLine(canvas);
        //绘制记录数字
        drawTextAbortLine(canvas);
    }

    /**
     * 绘制xy轴
     * @param canvas 画布
     */
    private void drawXY(Canvas canvas) {
        //绘制x轴
//        public void drawLine(float startX, float startY, float stopX, float stopY)
        canvas.drawLine(bean.getPADDING_LEFT(), getHeight() - bean
                .getPADDING_TOP(), getWidth() - bean.getPADDING_RIGHT() + GlobalNumber.TEN_NUMBER,
                getHeight() - bean.getPADDING_BOTTOM(), paintXy);
        //绘制x轴参数—x轴暂定最大数量为100
        int xSize = bean.getX_values().length;

        //初始描点数据位置
        int position = 0;
        if (xSize < GlobalNumber.HUNDRED_NUMBER) {
            position = 0;
        } else {
            position = xSize - GlobalNumber.HUNDRED_NUMBER;
        }
        //绘制X轴数据
        for (int i = 0; position < xSize; position++) {
            i++; //第几点
            String xValue = bean.getX_values()[position];
            //时间格式中存在;就断开
            if (xValue.contains(";")) {
                String[] time = xValue.split(";");
                //直接写的数字是校准数值
                //绘制日期
                canvas.drawText(time[0], bean.getPADDING_LEFT() + bean.getSCALE_x() * i
                        - GlobalNumber.THIRTY_FIVE_NUMBER_FLOAT, getHeight()
                        - bean.getPADDING_BOTTOM() + GlobalNumber.TWENTY_NUMBER_FLOAT, paintXy);
                //绘制时分秒
                canvas.drawText(time[1], bean.getPADDING_LEFT() + bean.getSCALE_x() * i
                        - GlobalNumber.TWENTY_EIGHT_NUMBER_FLOAT, getHeight()
                        - bean.getPADDING_BOTTOM() + GlobalNumber.THIRTY_FIVE_NUMBER_FLOAT
                        , paintXy);
            } else {
                //不存在，就直接绘制时间
                canvas.drawText(xValue, bean.getPADDING_LEFT() + bean
                        .getSCALE_x() * i - GlobalNumber.THIRTY_NUMBER_FLOAT, getHeight()
                        - bean.getPADDING_BOTTOM() + GlobalNumber.FIFTEEN_NUMBER_FLOAT, paintXy);
            }
            //刻度长度
            int yLength = GlobalNumber.FOUR_NUMBER;
            //绘制刻度 x1 y1 x2 y2
            canvas.drawLine(bean.getPADDING_LEFT() + bean.getSCALE_x() * i
                    , getHeight() - bean.getPADDING_BOTTOM()
                    , bean.getPADDING_LEFT() + bean.getSCALE_x() * i
                    , getHeight() - bean.getPADDING_BOTTOM() - yLength, paintXy);
        }

        //绘制y轴
        canvas.drawLine(bean.getPADDING_LEFT(), bean.getPADDING_TOP(), bean
                .getPADDING_LEFT(), getHeight() - bean.getPADDING_BOTTOM(), paintXy);
        //根据布局大小和容量，设置Y轴间距
        float scaleY = (getHeight() - bean.getPADDING_TOP() - bean
                .getPADDING_BOTTOM()) / bean.getSize();
        bean.setSCALE_Y(scaleY);

        //纵坐标刻度个数
        int ySize = bean.getY_SIZE();
        for (int i = 0; i <= ySize; i++) {
            //绘制横坐标
            float x = bean.getPADDING_LEFT() - GlobalNumber.FIFTY_NUMBER_FLOAT;
            //间距
            float value = (bean.getMax_value() - bean.getMin_value()) / bean.getY_SIZE();
            //间距像素
//            float pix = (getHeight() - bean.getPADDING_BOTTOM()
//                    - bean.getPADDING_TOP()) / (bean.getMax_value() - bean.getMin_value());
            float pix = (getHeight() - bean.getPADDING_BOTTOM() - bean
                    .getPADDING_TOP()) / (bean.getMax_value() - bean
                    .getMin_value());

            //绘制文本的纵坐标
            float y = getHeight() - bean.getPADDING_BOTTOM() - pix * (value * i)
                    + GlobalNumber.FIVE_NUMBER;
            float textValue = bean.getMin_value() + value * i;
            String text;
            if (bean.isDecimal()) {
                text = String.valueOf((float) (Math.round(textValue
                        * GlobalNumber.THOUSAND_NUMBER)) / GlobalNumber.THOUSAND_NUMBER);
            } else {
                text = String.valueOf((Math.round(textValue
                        * GlobalNumber.THOUSAND_NUMBER)) / GlobalNumber.THOUSAND_NUMBER);
            }

            canvas.drawText(text, x, y, paintXy);
            //刻度长度
            int xLength = GlobalNumber.FOUR_NUMBER;
            //绘制y轴刻度
            canvas.drawLine(bean.getPADDING_LEFT(), getHeight() - bean
                    .getPADDING_BOTTOM() - pix * (value * i), bean
                    .getPADDING_LEFT() + xLength, getHeight() - bean
                    .getPADDING_BOTTOM() - pix * (value * i), paintXy);
        }
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
                //有数据就画出来，没有数据就空着
                for (int j = 0; j < dataCache.get(i).length - 2; j += 2) {
                    float x1 = dataCache.get(i)[j];
                    float y1 = dataCache.get(i)[j + GlobalNumber.ONE_NUMBER];
                    float x2 = dataCache.get(i)[j + GlobalNumber.TWO_NUMBER];
                    float y2 = dataCache.get(i)[j + GlobalNumber.THREE_NUMBER];
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
            for (int i = 0, j = 0; j < linedataCache.get(k).length; i++, j += 2) {
                if (linedataCache.get(k)[j] != GlobalConstant.INVALID_DATA) {
                    String text;
                    if (bean.isDecimal()) {
                        text = String.valueOf(bean.getValues().get(k)[i]);
                    } else {
                        text = String.valueOf((int) bean.getValues().get(k)[i]);
                    }
                    float x = linedataCache.get(k)[j];
                    float y = linedataCache.get(k)[j + 1];
                    if (bean.getColorsId() != null) {
                        paintText.setColor(bean.getColorsId()[i]);
                    }
                    canvas.drawText(text, x - GlobalNumber.FIVE_NUMBER_FLOAT
                            , y - GlobalNumber.FIVE_NUMBER_FLOAT, paintText);
                    canvas.drawCircle(x, y, GlobalNumber.THREE_NUMBER, paintLine);
                }
            }
            //绘制单位
            canvas.drawText(bean.getUnit(), bean.getPADDING_LEFT()
                    + GlobalNumber.TWENTY_NUMBER_FLOAT
                    , bean.getPADDING_TOP() + GlobalNumber.TEN_NUMBER_FLOAT, paintXy);
        }
    }

    /**
     * 转换为数据转换为x轴需要的float[]
     * @return 集合
     */
    private ArrayList<float[]> getData() {
        ArrayList<float[]> dataList = new ArrayList<>();
        for (int j = 0; j < bean.getValues().size(); j++) {
            ArrayList<Float> list = new ArrayList();
            Float[] data = null;
            for (int i = 0; i < bean.getValues().get(j).length; i++) {
                if (bean.getValues().get(j)[i] != GlobalConstant.INVALID_DATA) {
                    //横坐标
                    float x = bean.getPADDING_LEFT() + bean.getSCALE_x() * (i + 1);
                    list.add(x);
                    float values = bean.getValues().get(j)[i];
                    //单位像素
                    float pix = (getHeight() - bean.getPADDING_BOTTOM() -
                            bean.getPADDING_TOP()) / (bean.getMax_value() - bean.getMin_value());
                    //像素位置
                    float y = getHeight() - ((values - bean.getMin_value()) *
                            pix + bean.getPADDING_TOP());
                    list.add(y);
                    if (i > 0 && i < bean.getValues().get(j).length - 1) {
                        list.add(x);
                        list.add(y);
                    }
                } else {
                    list.add(GlobalNumber.UN_THOUSAND_NUMBER_FLOAT);
                    list.add(GlobalNumber.UN_THOUSAND_NUMBER_FLOAT);
                    list.add(GlobalNumber.UN_THOUSAND_NUMBER_FLOAT);
                    list.add(GlobalNumber.UN_THOUSAND_NUMBER_FLOAT);
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
     * @return 集合
     */
    private ArrayList<float[]> getLineData() {
        ArrayList<float[]> dataList = new ArrayList<>();
        for (int j = 0; j < bean.getValues().size(); j++) {
            ArrayList<Float> list = new ArrayList();
            Float[] data = null;
            for (int i = 0; i < bean.getValues().get(j).length; i++) {
                if (bean.getValues().get(j)[i] != GlobalConstant.INVALID_DATA) {
                    //横坐标
                    float x = bean.getPADDING_LEFT() + bean.getSCALE_x() * (i + 1);
                    list.add(x);
                    float values = bean.getValues().get(j)[i];
                    //单位像素
                    float pix = (getHeight() - bean.getPADDING_BOTTOM() -
                            bean.getPADDING_TOP()) / (bean.getMax_value() - bean.getMin_value());
                    //像素位置
                    float y = getHeight() - ((values - bean.getMin_value())
                            * pix + bean.getPADDING_TOP());
                    list.add(y);
                } else {
                    list.add(GlobalNumber.UN_THOUSAND_NUMBER_FLOAT);
                    list.add(GlobalNumber.UN_THOUSAND_NUMBER_FLOAT);
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
     * 设置文本
     * @param paintText 文本
     */
    public void setPaintText(Paint paintText) {
        this.paintText = paintText;
    }

    /**
     * 设置Paint
     * @param paintLine 画笔
     */
    public void setPaintLine(Paint paintLine) {
        this.paintLine = paintLine;
    }

    /**
     * 设置bean数据
     * @param bean 数据
     */
    public void setBean(InitChartBean bean) {
        this.bean = bean;
    }

    /**
     * 数据重置
     */
    public void reset() {
        this.dataCache = null;
        this.linedataCache = null;
    }
}
