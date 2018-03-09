package com.konsung.bean;

import android.graphics.Color;

import com.konsung.R;

import java.util.ArrayList;

/**
 * TODO:
 * <p/>
 * Created: JustRush
 * Time:    2015/11/16 09:02
 * ver:
 */
public class Time_paramBean
{
    //行数-----参数个数
    private int LINE_SIZE = 0;
    //列数-----测量次数
    private int ROW_SIZE = 0;
    //y轴之间的间距
    private int SCALE_X = 150;
    //x轴之间的间距
    private int SCALE_Y = 50;
    //文字大小
    private int TEXT_SIZE = 20;
    //x轴名称
    private String X_NAME = "时间";
    //y轴名称
    private String Y_NAME = "参数";
    //x轴值-----时间
    private ArrayList<String> X_VALUE;
    //y轴值-----参数
    private ArrayList<String> Y_VALUE;
    //左边距
    private float PADDING_LEFT = 5.0f;
    //上边距
    private float PADDING_TOP = 50.0f;
    //右边距
    private float PADDING_RIGHT = 5.0f;
    //下边距
    private float PADDING_BOTTOM = 50.0f;
    //线条颜色，默认黑色
    private int LINE_COLOR = Color.BLACK;
    //数值颜色，默认康尚绿
//    private int VALUE_COLOR = 0xFF4DB133;
    //蓝色
    private int VALUE_COLOR = 0xFF4ca4a8;
    //横轴，纵轴参数颜色,默认黑色
    private int NAME_COLOR = 0xFF000000;
    //每一纵轴的值
    private ArrayList<ArrayList<String>> data;

    //————————————————位置偏移，现在需要手动按需求修改————————————————
    //x轴名称偏移                 x坐标和y坐标
    private float DEVIAN_NAME_X1 = 100.0F, DEVIAN_NAME_Y1 = -10.0f;
    //y轴名称偏移                 x坐标和y坐标
    private float DEVIAN_NAME_X2 = 15.0F, DEVIAN_NAME_Y2 = 30.0f;

    //x轴标值偏移(在这里是时间)
    private float DEVIAN_XVALUE_X = 20.0f, DEVIAN_XVALUE_Y = 20.0f;
    //y轴标值偏移(参数)
    private float DEVIAN_YVALUE_X = 10.0f, DEVIAN_YVALUE_Y = 30.0f;

    //数据值偏移
    private float DEVIAN_DATA_X = 20.0f, DEVIAN_DATA_Y = 30.0f;


    public int getLINE_SIZE()
    {
        return LINE_SIZE;
    }

    public void setLINE_SIZE(int LINE_SIZE)
    {
        this.LINE_SIZE = LINE_SIZE;
    }

    public int getROW_SIZE()
    {
        return ROW_SIZE;
    }

    public void setROW_SIZE(int ROW_SIZE)
    {
        this.ROW_SIZE = ROW_SIZE;
    }

    public int getSCALE_X()
    {
        return SCALE_X;
    }

    public void setSCALE_X(int SCALE_X)
    {
        this.SCALE_X = SCALE_X;
    }

    public int getSCALE_Y()
    {
        return SCALE_Y;
    }

    public void setSCALE_Y(int SCALE_Y)
    {
        this.SCALE_Y = SCALE_Y;
    }

    public String getX_NAME()
    {
        return X_NAME;
    }

    public void setX_NAME(String x_NAME)
    {
        X_NAME = x_NAME;
    }

    public String getY_NAME()
    {
        return Y_NAME;
    }

    public void setY_NAME(String y_NAME)
    {
        Y_NAME = y_NAME;
    }

    public ArrayList<String> getX_VALUE()
    {
        return X_VALUE;
    }

    public void setX_VALUE(ArrayList<String> x_VALUE)
    {
        X_VALUE = x_VALUE;
    }

    public ArrayList<String> getY_VALUE()
    {
        return Y_VALUE;
    }

    public void setY_VALUE(ArrayList<String> y_VALUE)
    {
        Y_VALUE = y_VALUE;
    }

    public ArrayList<ArrayList<String>> getData()
    {
        return data;
    }

    public void setData(ArrayList<ArrayList<String>> data)
    {
        this.data = data;
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

    public int getLINE_COLOR()
    {
        return LINE_COLOR;
    }

    public void setLINE_COLOR(int LINE_COLOR)
    {
        this.LINE_COLOR = LINE_COLOR;
    }

    public int getVALUE_COLOR()
    {
        return VALUE_COLOR;
    }

    public void setVALUE_COLOR(int VALUE_COLOR)
    {
        this.VALUE_COLOR = VALUE_COLOR;
    }

    public int getNAME_COLOR()
    {
        return NAME_COLOR;
    }

    public void setNAME_COLOR(int NAME_COLOR)
    {
        this.NAME_COLOR = NAME_COLOR;
    }

    public int getTEXT_SIZE()
    {
        return TEXT_SIZE;
    }

    public void setTEXT_SIZE(int TEXT_SIZE)
    {
        this.TEXT_SIZE = TEXT_SIZE;
    }

    public float getDEVIAN_NAME_X1()
    {
        return DEVIAN_NAME_X1;
    }

    public void setDEVIAN_NAME_X1(float DEVIAN_NAME_X1)
    {
        this.DEVIAN_NAME_X1 = DEVIAN_NAME_X1;
    }

    public float getDEVIAN_NAME_Y1()
    {
        return DEVIAN_NAME_Y1;
    }

    public void setDEVIAN_NAME_Y1(float DEVIAN_NAME_Y1)
    {
        this.DEVIAN_NAME_Y1 = DEVIAN_NAME_Y1;
    }

    public float getDEVIAN_NAME_X2()
    {
        return DEVIAN_NAME_X2;
    }

    public void setDEVIAN_NAME_X2(float DEVIAN_NAME_X2)
    {
        this.DEVIAN_NAME_X2 = DEVIAN_NAME_X2;
    }

    public float getDEVIAN_NAME_Y2()
    {
        return DEVIAN_NAME_Y2;
    }

    public void setDEVIAN_NAME_Y2(float DEVIAN_NAME_Y2)
    {
        this.DEVIAN_NAME_Y2 = DEVIAN_NAME_Y2;
    }

    public float getDEVIAN_XVALUE_X()
    {
        return DEVIAN_XVALUE_X;
    }

    public void setDEVIAN_XVALUE_X(float DEVIAN_XVALUE_X)
    {
        this.DEVIAN_XVALUE_X = DEVIAN_XVALUE_X;
    }

    public float getDEVIAN_XVALUE_Y()
    {
        return DEVIAN_XVALUE_Y;
    }

    public void setDEVIAN_XVALUE_Y(float DEVIAN_XVALUE_Y)
    {
        this.DEVIAN_XVALUE_Y = DEVIAN_XVALUE_Y;
    }

    public float getDEVIAN_YVALUE_X()
    {
        return DEVIAN_YVALUE_X;
    }

    public void setDEVIAN_YVALUE_X(float DEVIAN_YVALUE_X)
    {
        this.DEVIAN_YVALUE_X = DEVIAN_YVALUE_X;
    }

    public float getDEVIAN_YVALUE_Y()
    {
        return DEVIAN_YVALUE_Y;
    }

    public void setDEVIAN_YVALUE_Y(float DEVIAN_YVALUE_Y)
    {
        this.DEVIAN_YVALUE_Y = DEVIAN_YVALUE_Y;
    }

    public float getDEVIAN_DATA_X()
    {
        return DEVIAN_DATA_X;
    }

    public void setDEVIAN_DATA_X(float DEVIAN_DATA_X)
    {
        this.DEVIAN_DATA_X = DEVIAN_DATA_X;
    }

    public float getDEVIAN_DATA_Y()
    {
        return DEVIAN_DATA_Y;
    }

    public void setDEVIAN_DATA_Y(float DEVIAN_DATA_Y)
    {
        this.DEVIAN_DATA_Y = DEVIAN_DATA_Y;
    }
}
