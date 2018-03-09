package com.konsung.bean;

/**
 * 绘制趋势图所需要的数据
 */

public class StatisticalPicBean {
    private int startCount; // 数据的起始位置
    private int dataSize; // 数据集合的长度
    private int parameter; // 数据对应数据库的参数值
    private int maxValue; // 趋势图最大刻度值
    private int minValue; // 趋势图最小刻度值
    private int ySize = 10; // 趋势图Y轴刻度数量,默认10个
    private String idCard; // 当前居民身份证
    private String unit; // 当前趋势值对应的单位

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }

    public int getParameter() {
        return parameter;
    }

    public void setParameter(int parameter) {
        this.parameter = parameter;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getySize() {
        return ySize;
    }

    public void setySize(int ySize) {
        this.ySize = ySize;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public int getStartCount() {
        return startCount;
    }

    public void setStartCount(int startCount) {
        this.startCount = startCount;
    }
}
