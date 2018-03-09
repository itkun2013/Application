package com.konsung.network;

/**
 * 状态值的接口
 */
public interface Stateful {
    /**
     * 设置当前状态
     * @param state 状态值
     */
    void setState(int state);
}
