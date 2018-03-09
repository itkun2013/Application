package com.konsung.presenter;

import android.content.Context;

import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.StatisticalPicBean;
import com.konsung.network.LifeSubscription;

import java.util.List;

/**
 * 血红蛋白逻辑
 **/
public class HemoglobinPresenter {
    /**
     * 界面回显的接口
     */
    public interface View extends LifeSubscription {

        /**
         * 测量成功
         * @param hgbValue 血红蛋白
         * @param hctValue 红细胞压积值
         */
        void measureSuccess(float hgbValue, float hctValue);

        /**
         * 初始化测量数据
         * @param measureDataBean 测量数据
         */
        void setMeasureData(MeasureDataBean measureDataBean);
    }

    /**
     * 逻辑处理的接口
     */
    public interface Presenter {

        /**
         * 绑定服务
         */
        void bindAidlService();

        /**
         * 获取趋势表listView的适配器
         * @return 适配器
         */
        List<Integer> getStatisticalTableItem();

        /**
         * 获取趋势图
         * @param context 上下文
         * @return 趋势图
         */
        StatisticalPicBean[] getStatisticalPic(Context context);

        /**
         * 解绑服务
         */
        void unbindService();
    }
}
