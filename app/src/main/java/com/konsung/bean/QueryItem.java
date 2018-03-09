package com.konsung.bean;

import java.util.Date;

/**
 *
 */
public class QueryItem {
    private int sex = -1; //性别
    private String name; //姓名
    private Date measureStart; //测量数据的开始时间
    private Date measureEnd; //测量数据的结束时间

    /**
     * 获取sex的值
     *
     * @return sex sex值
     */
    public int getSex() {
        return sex;
    }

    /**
     * 设置sex的值
     *
     * @param sex sex值
     */
    public void setSex(int sex) {
        this.sex = sex;
    }

    /**
     * 获取name的值
     *
     * @return name name值
     */
    public String getName() {
        return name;
    }

    /**
     * 设置name的值
     *
     * @param name name值
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取measureStart的值
     *
     * @return measureStart measureStart值
     */
    public Date getMeasureStart() {
        return measureStart;
    }

    /**
     * 设置measureStart的值
     *
     * @param measureStart measureStart值
     */
    public void setMeasureStart(Date measureStart) {
        this.measureStart = measureStart;
    }

    /**
     * 获取measureEnd的值
     *
     * @return measureEnd measureEnd值
     */
    public Date getMeasureEnd() {
        return measureEnd;
    }

    /**
     * 设置measureEnd的值
     *
     * @param measureEnd measureEnd值
     */
    public void setMeasureEnd(Date measureEnd) {
        this.measureEnd = measureEnd;
    }
}
