package com.konsung.presenter;

import android.content.Context;
import android.widget.TextView;

import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.StatisticalPicBean;
import com.konsung.network.LifeSubscription;

import java.util.List;

/**
 * 血压逻辑
 **/
public class NibpPresenter {
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
         * @param sysValue 收缩压
         * @param diaValue 舒张压
         */
        void measureSuccess(int sysValue, int diaValue);

        /**
         * 刷新引导界面
         * @param tv 调整控件格式
         * @param state 当前状态
         */
        void refreshGuide(TextView tv, int state);

        /**
         * 血压状态
         * @param value 状态值
         */
        void setNibpStatus(int value);

        /**
         * 刷新袖带压
         * @param cuff 进度
         */
        void refreshCuff(int cuff);

        /**
         * 数据回显
         * @param bean 数据集
         */
        void setDateBean(MeasureDataBean bean);
    }

    /**
     * 逻辑处理的接口
     */
    public interface Presenter {
        /**
         * 启动测量
         */
        void startMeasure();

        /**
         * 中断测量
         */
        void stopMeasure();

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
         * 启动校准
         */
        void startCalibrate();

        /**
         * 停止校准
         */
        void stopCalibrate();

        /**
         * 启动漏气测试
         */
        void startLeakTest();

        /**
         * 停止漏气测试
         */
        void stopLeakTest();

        /**
         * 血压复位
         */
        void resetNibp();

        /**
         * 解绑服务
         * @param context 上下文
         */
        void unbindService(Context context);
    }
}
