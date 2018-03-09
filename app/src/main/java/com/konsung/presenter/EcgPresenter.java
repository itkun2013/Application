package com.konsung.presenter;

import android.widget.TextView;

import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.MeasureEcgBean;
import com.konsung.bean.StatisticalPicBean;
import com.konsung.network.LifeSubscription;

import java.util.List;

/**
 * 心电逻辑
 */
public class EcgPresenter {
    /**
     * 界面回显的接口
     */
    public interface View extends LifeSubscription {
        /**
         * 刷新界面
         * @param state 界面的状态，以封装常量值（0引导界面，1实时心电界面，2正在测量界面，3测量结束界面）
         */
        void refresh(int state);

        /**
         * 测量成功
         * @param hrValue 心率
         * @param bean 心电诊断结果
         */
        void measureSuccess(int hrValue, MeasureEcgBean bean);

        /**
         * 测量失败
         */
        void measureError();

        /**
         * 引导界面控件的刷新，根据导联链接状态变换实时刷新
         * @param tv 对应刷新的控件
         * @param state 当前状态，常量以封装（4提示引导，5已完成引导，6引导步骤）
         */
        void refreshGuide(TextView tv, int state);

        /**
         * 添加心电波形数据
         * @param param 区分导联（1-12）
         * @param data 转换成波形的数据
         */
        void addEcgWaveData(int param, int data);

        /**
         * 设置心电的连接状态
         * @param value 连接状态
         * （状态值又appDevice发送过来：0连接正常，-1000未启动服务，511导联脱落，其他电极脱落）
         */
        void setEcgConnectStatus(int value);

        /**
         * 保存心电波形
         * @param param 参数
         * @param bytes 波形数据
         */
        void saveEcgWave(int param, byte[] bytes);

        /**
         * 设置测量对象
         * @param measureDataBean 测量对象
         */
        void setMeasureDataBean(MeasureDataBean measureDataBean);
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
         * 电极脱落状态
         * @param leadOff 电极脱落状态值
         * @return 脱落电极的提示语
         */
        String getEcgConnectStatus(int leadOff);

        /**
         * 获取趋势表listView的适配器
         * @return 适配器
         */
        List<Integer> getStatisticalTableItem();

        /**
         * 获取趋势图
         * @return 趋势图
         */
        StatisticalPicBean[] getStatisticalPic();

        /**
         * 保存心电波形数据
         * @param param 参数
         * @param data 波形数据
         */
        void saveWave(int param, byte[] data);
    }
}
