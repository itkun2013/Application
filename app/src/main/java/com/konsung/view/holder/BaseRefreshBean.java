package com.konsung.view.holder;

import java.util.Observable;

/**
 * $desc
 * @author JustRush
 * @since 1
 */

public class BaseRefreshBean extends Observable {
    private int flickerRate; //刷新频率
    private float[] positions; //位置
    private int[] colors; //颜色
    private int circleSize; //闪缩圆圈的大小
    private int signal;

    /**
     * 获取刷新频率，每秒刷新次数
     * @return 刷新频率
     */
    public int getFlickerRate() {
        return flickerRate;
    }

    /**
     * 设置刷新频率
     * @param flickerRate 刷新频率
     */
    public void setFlickerRate(int flickerRate) {
        this.flickerRate = flickerRate;
        notifyObservers();
    }

    /**
     * 获取需要显示的位置
     * @return 需要显示的位置
     */
    public float[] getPositions() {
        return positions;
    }

    /**
     * 设置需要显示的位置
     * @param positions 需要显示的位置
     */
    public void setPositions(float[] positions) {
        this.positions = positions;
        notifyObservers();
    }

    /**
     * 获取对应位置的颜色
     * @return 位置颜色
     */
    public int[] getColors() {
        return colors;
    }

    /**
     * 设置对应位置颜色
     * @param colors 对应位置颜色
     */
    public void setColors(int[] colors) {
        this.colors = colors;
        notifyObservers();
    }

    /**
     * 获取圆圈的大小
     * @return 圆圈大小
     */
    public int getCircleSize() {
        return circleSize;
    }

    /**
     * 设置圆圈大小
     * @param circleSize 圆圈大小
     */
    public void setCircleSize(int circleSize) {
        this.circleSize = circleSize;
        notifyObservers();
    }
}
