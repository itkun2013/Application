package com.konsung.bean;

/**
 * Created by xiangshicheng on 2017/5/11 0011.
 */

public class AppMeasureBean {
    //记录是否点击
    private boolean isClick = false;
    //记录图片的地址
    private int  iv;
    //记录点击后图片
    private int ivClick;
    //记录测量项的名字
    private String measureName;
    //记录测量值
    private String measureValue;
    //记录是否异常
    private boolean isNormal = true;
    //记录是否为两项测量值
    private boolean isDoubleMeasureValue = false;

    /**
     * 获取图片
     * @return 图片id
     */
    public int getIv() {
        return iv;
    }

    /**
     * @param iv 图片Id
     */
    public void setIv(int iv) {
        this.iv = iv;
    }

    /**
     * 是否点击
     * @return 是否点击
     */
    public boolean isClick() {
        return isClick;
    }

    /**
     * @param click 点击状态
     */
    public void setClick(boolean click) {
        isClick = click;
    }

    /**
     * 测量项名称获取
     * @return 测量项名字
     */
    public String getMeasureName() {
        return measureName;
    }

    /**
     * @param measureName 测量项名
     */
    public void setMeasureName(String measureName) {
        this.measureName = measureName;
    }

    /**
     * 获取点击后图片
     * @return 图片id
     */
    public int getIvClick() {
        return ivClick;
    }

    /**
     * 设置图片
     * @param ivClick 图片id
     */
    public void setIvClick(int ivClick) {
        this.ivClick = ivClick;
    }

    /**
     * 获取测量值
     * @return 获取值
     */
    public String getMeasureValue() {
        return measureValue;
    }

    /**
     * 设置测量值
     * @param measureValue 测量值
     */
    public void setMeasureValue(String measureValue) {
        this.measureValue = measureValue;
    }

    /**
     * 获取测量值是否正常的标识状态
     * @return 获取值
     */
    public boolean isNormal() {
        return isNormal;
    }

    /**
     * 设置是否正常状态
     * @param normal 是否正常值
     */
    public void setNormal(boolean normal) {
        isNormal = normal;
    }

    /**
     * 获取是否两次点击标识
     * @return 是否被多次点击
     */
    public boolean isDoubleMeasureValue() {
        return isDoubleMeasureValue;
    }

    /**
     * 设置两次点击标识
     * @param doubleMeasureValue 标识
     */
    public void setDoubleMeasureValue(boolean doubleMeasureValue) {
        isDoubleMeasureValue = doubleMeasureValue;
    }
}
