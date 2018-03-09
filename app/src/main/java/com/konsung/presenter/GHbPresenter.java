package com.konsung.presenter;

import android.content.Context;

import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.StatisticalPicBean;
import com.konsung.network.LifeSubscription;

import java.util.List;

/**
 * 糖化血红蛋白逻辑
 **/
public class GHbPresenter {
    /**
     * 界面回显的接口
     */
    public interface View extends LifeSubscription {

        /**
         * 测量成功
         * @param nHbA1cValue NGSP标准的糖化血红蛋白值
         * @param iHbA1cValue IFCC标准的糖化血红蛋白值
         * @param eGA 平均血糖浓度
         */
        void measureSuccess(int nHbA1cValue, int iHbA1cValue, int eGA);

        /**
         * 数据回显
         * @param bean 数据集
         */
        void setDataBean(MeasureDataBean bean);
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
