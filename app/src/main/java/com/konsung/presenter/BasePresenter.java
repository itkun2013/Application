package com.konsung.presenter;


import com.konsung.network.LifeSubscription;

/**
 * 所有逻辑处理类的基类
 * @param <T> 订阅者的实例
 */

public class BasePresenter<T extends LifeSubscription> {
    protected T mLifeSubscription;

    /**
     * 设置订阅者
     * @param mLifeSubscription 订阅者
     */
    public void setLifeSubscription(LifeSubscription mLifeSubscription) {
        this.mLifeSubscription = (T) mLifeSubscription;
    }


}
