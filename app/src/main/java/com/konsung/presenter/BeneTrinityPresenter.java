package com.konsung.presenter;

import com.konsung.bean.MeasureDataBean;
import com.konsung.network.LifeSubscription;

import java.util.List;

/**
 * 百捷三合一逻辑
 **/
public class BeneTrinityPresenter {
    /**
     * 界面回显的接口
     */
    public interface View extends LifeSubscription {

        /**
         * 测量成功
         * @param gluValue 血糖值
         * @param uaValue 尿酸
         * @param choValue 总胆固醇
         */
        void measureSuccess(float gluValue, float uaValue, float choValue);

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
         * 保存血糖
         * @param param 餐前餐后参数
         * @param value 血糖值
         */
        void saveGlu(int param, int value);

        /**
         * 绑定服务
         */
        void unbindService();
    }
}
