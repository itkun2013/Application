package com.konsung.bean;

import com.konsung.util.global.BloodReference;
import com.konsung.util.global.PatientStyle;
import com.konsung.util.global.Sex;

/**
 * 缓存血红蛋白参考值
 * @author yuchunhui
 **/

public class BloodReferenceBean {

    //血红蛋白最高值
    public float hgbHigh = 0;
    public float hgbLow = 0;
    //红细胞积压值
    public float htcHigh = 0;
    public float htcLow = 0;

    /**
     * 构造器
     * @param hgbHigh 血红蛋白上限值
     * @param hgbLow 血红蛋白下限值
     * @param htcHigh 积压值上限值
     * @param htcLow 积压值下限值
     */
    public BloodReferenceBean(float hgbHigh, float hgbLow, float htcHigh, float htcLow) {
        this.hgbHigh = hgbHigh;
        this.hgbLow = hgbLow;
        this.htcHigh = htcHigh;
        this.htcLow = htcLow;
    }

    /**
     * 默认构造器
     */
    public BloodReferenceBean() {}

    /**
     * 重载构造器
     * @param sex 性别
     * @param style 类别
     */
    public BloodReferenceBean(int style, int sex) {
        switch (style) {
            case PatientStyle.ADULT:
                if (sex == Sex.FEMALE) {
                    hgbHigh = BloodReference.HGB_FEMALE_HIGH;
                    hgbLow = BloodReference.HGB_FEMALE_LOW;
                    htcHigh = BloodReference.HTC_FEMALE_HIGH;
                    htcLow = BloodReference.HTC_FEMALE_LOW;
                } else {
                    hgbHigh = BloodReference.HGB_MALE_HIGH;
                    hgbLow = BloodReference.HGB_MALE_LOW;
                    htcHigh = BloodReference.HTC_MALE_HIGH;
                    htcLow = BloodReference.HTC_MALE_LOW;
                }
                break;
            case PatientStyle.YOUTH:
                hgbHigh = BloodReference.HGB_YOUTH_HIGH;
                hgbLow = BloodReference.HGB_YOUTH_LOW;
                htcHigh = BloodReference.HTC_NORMAL_HIGH;
                htcLow = BloodReference.HTC_NORMAL_LOW;
                break;
            case PatientStyle.BABY:
                hgbHigh = BloodReference.HGB_BABY_HIGH;
                hgbLow = BloodReference.HGB_BABY_LOW;
                htcHigh = BloodReference.HTC_NORMAL_HIGH;
                htcLow = BloodReference.HTC_NORMAL_LOW;
                break;
            default:
                hgbHigh = BloodReference.HGB_MALE_HIGH;
                hgbLow = BloodReference.HGB_MALE_LOW;
                htcHigh = BloodReference.HTC_MALE_HIGH;
                htcLow = BloodReference.HTC_MALE_LOW;
                break;
        }
    }

    /**
     * 获取血红蛋白上限值
     * @return 上限值
     */
    public float getHgbHigh() {
        return hgbHigh;
    }

    /**
     * 设置血红蛋白上限值
     * @param hgbHigh 血红蛋白上限值
     */
    public void setHgbHigh(float hgbHigh) {
        this.hgbHigh = hgbHigh;
    }

    /**
     * 获取血红蛋白下限值
     * @return 血红蛋白下限值
     */
    public float getHgbLow() {
        return hgbLow;
    }

    /**
     * 设置血红蛋白下限值
     * @param hgbLow 下限值
     */
    public void setHgbLow(float hgbLow) {
        this.hgbLow = hgbLow;
    }

    /**
     * 获取积压值上限值
     * @return 上限值
     */
    public float getHtcHigh() {
        return htcHigh;
    }

    /**
     * 设置积压值上限值
     * @param htcHigh 积压值上限值
     */
    public void setHtcHigh(float htcHigh) {
        this.htcHigh = htcHigh;
    }

    /**
     * 获取积压值下限值
     * @return 下限值
     */
    public float getHtcLow() {
        return htcLow;
    }

    /**
     * 设置积压值下限值
     * @param htcLow 下限值
     */
    public void setHtcLow(float htcLow) {
        this.htcLow = htcLow;
    }
}
