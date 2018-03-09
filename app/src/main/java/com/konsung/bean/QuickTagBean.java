package com.konsung.bean;

/**
 * Created by YYX on 2017/11/2 0002.
 * 快检页面设置tagbean
 */

public class QuickTagBean {
    /**
     * 控件高度标记
     */
    private int weight = 0;
    /**
     * 控件对于的资源文件的标记
     */
    private int posi = 0;
    /**
     * 获取weight的值
     *
     * @return weight weight值
     */
    public int getWeight() {
        return weight;
    }

    /**
     * 设置weight的值
     *
     * @param weight weight值
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * 获取posi的值
     *
     * @return posi posi值
     */
    public int getPosi() {
        return posi;
    }

    /**
     * 设置posi的值
     *
     * @param posi posi值
     */
    public void setPosi(int posi) {
        this.posi = posi;
    }
}
