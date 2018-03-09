package com.konsung.presenter;

import android.content.Context;
import android.widget.TextView;

import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.StatisticalPicBean;
import com.konsung.network.LifeSubscription;

import java.util.List;

/**
 * 额温逻辑
 **/
public class TempPresenter {
    /**
     * 界面回显的接口
     */
    public interface View extends LifeSubscription {
        /**
         * 刷新界面
         * @param state 界面的状态
         */
        void refresh(int state);

        /**
         * 测量成功
         * @param temperature 温度
         */
        void measureSuccess(int temperature);

        /**
         * 刷新引导界面
         * @param tv 调整控件格式
         * @param state 当前状态
         */
        void refreshGuide(TextView tv, int state);

        /**
         * 数据回显
         * @param bean 数据集合
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
