package com.konsung.bean;

import java.util.ArrayList;

/**
 * 测量项折线图bean
 */
public class LineChatBean {
    //参数
    private int param;
    //x轴下参数
    private String[] xValues;
    //值
    private ArrayList<float[]> values;
    //单位
    private String unit = "";
    //线条颜色id: R.color....
    private int[] colorsId;
    //纵坐标刻度数量
    private int ySize = 5;
    //最大值
    private float maxValue;
    //最小值
    private float minValue;
    //横坐标最大数量
    private int maxSize = 100;
    //基准x----------左边距
    private float baseX = 20.0f;
    //基准y----------纵轴初始坐标
    private float baseY = 400.0f;
    //左边距
    private float paddingLeft = 50.0f;
    //上边距
    private float paddingTop = 10.0f;
    //右边距
    private float paddingRight = 50.0f;
    //下边距
    private float paddingBottom = 70.0f;
    //y轴值之间间距
    private float scaleY = 30.0F;
    //x轴值之间间距
    private float scaleX = 63.0F;

    //xy轴线条粗细
    private float xOryDimen = 1.0f;

    //绘制记录的条数
    private int size;

    // Y轴刻度是否有精度要求
    private boolean isDecimal;

    public String[] getXValues() {
        return xValues;
    }

    public void setXValues(String[] xValues) {
        this.xValues = xValues;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public float getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(float paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public float getPaddingTop() {
        return paddingTop;
    }

    public float getPaddingRight() {
        return paddingRight;
    }

    public float getPaddingBottom() {
        return paddingBottom;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getXOryDimen() {
        return xOryDimen;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public ArrayList<float[]> getValues() {
        return values;
    }

    public void setValues(ArrayList<float[]> values) {
        this.values = values;
    }

    public int getYSize() {
        return ySize;
    }

    public void setYSize(int ySize) {
        this.ySize = ySize;
    }

    public int[] getColorsId() {
        return colorsId;
    }

    public void setColorsId(int[] colorsId) {
        this.colorsId = colorsId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isDecimal() {
        return isDecimal;
    }

    public void setIsDecimal(boolean isDecimal) {
        this.isDecimal = isDecimal;
    }

    public int getParam() {
        return param;
    }

    public void setParam(int param) {
        this.param = param;
    }
}
