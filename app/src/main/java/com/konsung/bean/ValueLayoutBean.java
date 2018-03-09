package com.konsung.bean;

import android.content.Context;
import android.support.annotation.StringRes;

import com.konsung.R;

/**
 * 测量项布局初始化数据集
 */
public class ValueLayoutBean {
    //测量项
    private String name;
    //值
    private String value;
    //单位
    private String unit;
    //上限
    private String max;
    //下限
    private String min;

    /**
     * 无参数构造器
     */
    public ValueLayoutBean() {
        this("", "", "", "", "");
    }

    /**
     * 传入字符串构造器
     * @param name 测量项目名称
     * @param value 值
     * @param unit 单位
     * @param max 最大值
     * @param min 最小值
     */
    public ValueLayoutBean(String name, String value, String unit, String max, String min) {
        this.name = name;
        this.value = value;
        this.unit = unit;
        this.max = max;
        this.min = min;
    }

    /**
     * 有值构造器
     * @param context 上下文
     * @param idName 测量项目名称资源ID
     * @param value 值
     * @param idUnit 单位资源ID
     * @param max 最大值
     * @param min 最小值
     */
    public ValueLayoutBean(Context context, @StringRes int idName, String value, String max,
            String min, @StringRes int idUnit) {
        name = context.getString(idName);
        this.value = value;
        this.max = max;
        this.min = min;
        unit = context.getString(idUnit);
    }

    /**
     * 无值-？-构造器
     * @param context 上下文
     * @param idName 测量项目名称资源ID
     * @param idUnit 单位资源ID
     * @param max 最大值
     * @param min 最小值
     */
    public ValueLayoutBean(Context context, @StringRes int idName, String max,
            String min, @StringRes int idUnit) {
        name = context.getString(idName);
        this.value = context.getString(R.string.invalid_data);
        this.max = max;
        this.min = min;
        unit = context.getString(idUnit);
    }

    /**
     * 无值-？-构造器
     * @param context 上下文
     * @param idName 测量项目名称资源ID
     * @param unit 单位
     * @param max 最大值
     * @param min 最小值
     */
    public ValueLayoutBean(Context context, @StringRes int idName, String max,
            String min, String unit) {
        name = context.getString(idName);
        this.value = context.getString(R.string.invalid_data);
        this.max = max;
        this.min = min;
        this.unit = unit;
    }

    /**
     * 获取项目名称
     * @return 项目名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置项目名称
     * @param name 项目名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取值
     * @return 值文本
     */
    public String getValue() {
        return value;
    }

    /**
     * 设置值
     * @param value 需要显示的值文本
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * 获取单位
     * @return 单位文本
     */
    public String getUnit() {
        return unit;
    }

    /**
     * 设置单位
     * @param unit 单位
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * 获取最大值
     * @return 最大值
     */
    public String getMax() {
        return max;
    }

    /**
     * 设置最大值
     * @param max 最大值
     */
    public void setMax(String max) {
        this.max = max;
    }

    /**
     * 获取最小值
     * @return 最小值
     */
    public String getMin() {
        return min;
    }

    /**
     * 设置最小值
     * @param min 最小值
     */
    public void setMin(String min) {
        this.min = min;
    }
}
