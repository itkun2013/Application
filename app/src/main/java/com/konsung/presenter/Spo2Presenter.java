package com.konsung.presenter;

import android.content.Context;
import android.widget.TextView;

import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.StatisticalPicBean;
import com.konsung.network.LifeSubscription;

import java.util.List;

/**
 * 血氧逻辑
 **/
public class Spo2Presenter {
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
         * @param spo2Value 血氧
         * @param hrValue 心率
         */
        void measureSuccess(int spo2Value, int hrValue);


        /**
         * 刷新引导界面
         * @param tv 调整控件格式
         * @param state 当前状态
         */
        void refreshGuide(TextView tv, int state);

        /**
         * 添加血氧波形数据
         * @param bytes 字节数据
         */
        void spo2WaveData(byte[] bytes);

        /**
         * 设置血氧的设备状态
         * @param value 连接状态
         */
        void setSpo2Status(int value);

        /**
         * 刷新进度条
         * @param progress 进度
         */
        void refreshProgress(int progress);

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
         * 解绑服务
         * @param context 上下文
         */
        void unbindService(Context context);

    }
}
