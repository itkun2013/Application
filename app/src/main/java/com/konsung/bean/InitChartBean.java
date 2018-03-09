package com.konsung.bean;

import java.util.ArrayList;

/**
 * TODO:
 * <p>
 * Created: JustRush
 * Time:    2015/11/10 14:23
 * ver:
 */
public class InitChartBean
{
    //x轴下参数
    private String[] x_values;
    //值
    private ArrayList<float[]> values;
    //单位
    private String unit="";
    //线条颜色id: R.color....
    private int[] colorsId;
    //纵坐标刻度数量
    private int Y_SIZE=5;
    //最大值
    private float max_value;
    //最小值
    private float min_value;
    //横坐标最大数量
    private int MAX_SIZE = 100;
    //基准x----------左边距
    private float BASE_X = 20.0f;
    //基准y----------纵轴初始坐标
    private float BASE_Y = 400.0f;
    //左边距
    private float PADDING_LEFT = 50.0f;
    //上边距
    private float PADDING_TOP = 50.0f;
    //右边距
    private float PADDING_RIGHT = 50.0f;
    //下边距
    private float PADDING_BOTTOM = 50.0f;
    //X轴值之间间距
    private float SCALE_Y = 30.0F;
    //Y轴值之间间距
    private float SCALE_x = 70.0F;

    //xy轴线条粗细
    private float xOryDimen = 2.0f;

    //绘制记录的条数
    private int size;

    // Y轴刻度是否有精度要求
    private boolean isDecimal;

    public String[] getX_values()
    {
        return x_values;
    }

    public void setX_values(String[] x_values)
    {
        this.x_values = x_values;
    }

    public float getMax_value()
    {
        return max_value;
    }

    public void setMax_value(float max_value)
    {
        this.max_value = max_value;
    }

    public float getMin_value()
    {
        return min_value;
    }

    public void setMin_value(float min_value)
    {
        this.min_value = min_value;
    }

    public float getBASE_X()
    {
        return BASE_X;
    }

    public void setBASE_X(float BASE_X)
    {
        this.BASE_X = BASE_X;
    }

    public float getBASE_Y()
    {
        return BASE_Y;
    }

    public void setBASE_Y(float BASE_Y)
    {
        this.BASE_Y = BASE_Y;
    }

    public float getPADDING_LEFT()
    {
        return PADDING_LEFT;
    }

    public void setPADDING_LEFT(float PADDING_LEFT)
    {
        this.PADDING_LEFT = PADDING_LEFT;
    }

    public float getPADDING_TOP()
    {
        return PADDING_TOP;
    }

    public void setPADDING_TOP(float PADDING_TOP)
    {
        this.PADDING_TOP = PADDING_TOP;
    }

    public float getPADDING_RIGHT()
    {
        return PADDING_RIGHT;
    }

    public void setPADDING_RIGHT(float PADDING_RIGHT)
    {
        this.PADDING_RIGHT = PADDING_RIGHT;
    }

    public float getPADDING_BOTTOM()
    {
        return PADDING_BOTTOM;
    }

    public void setPADDING_BOTTOM(float PADDING_BOTTOM)
    {
        this.PADDING_BOTTOM = PADDING_BOTTOM;
    }

    public float getSCALE_Y()
    {
        return SCALE_Y;
    }

    public void setSCALE_Y(float SCALE_Y)
    {
        this.SCALE_Y = SCALE_Y;
    }

    public float getSCALE_x()
    {
        return SCALE_x;
    }

    public void setSCALE_x(float SCALE_x)
    {
        this.SCALE_x = SCALE_x;
    }

    public float getxOryDimen()
    {
        return xOryDimen;
    }

    public void setxOryDimen(float xOryDimen)
    {
        this.xOryDimen = xOryDimen;
    }

    public int getMAX_SIZE()
    {
        return MAX_SIZE;
    }

    public void setMAX_SIZE(int MAX_SIZE)
    {
        this.MAX_SIZE = MAX_SIZE;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public ArrayList<float[]> getValues()
    {
        return values;
    }

    public void setValues(ArrayList<float[]> values)
    {
        this.values = values;
    }

    public int getY_SIZE()
    {
        return Y_SIZE;
    }

    public void setY_SIZE(int y_SIZE)
    {
        Y_SIZE = y_SIZE;
    }

    public int[] getColorsId()
    {
        return colorsId;
    }

    public void setColorsId(int[] colorsId)
    {
        this.colorsId = colorsId;
    }

    public String getUnit()
    {
        return unit;
    }

    public void setUnit(String unit)
    {
        this.unit = unit;
    }

    public boolean isDecimal() {
        return isDecimal;
    }

    public void setIsDecimal(boolean isDecimal) {
        this.isDecimal = isDecimal;
    }
}
