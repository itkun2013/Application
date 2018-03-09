package com.konsung.fragment;

import android.app.Fragment;
import android.util.Log;

import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;

/**
 * Created by JustRush on 2015/8/1.
 */
public class BaseInsertFragment extends BaseFragment {
    /**
     * 接口
     */
    public interface BeanMsg {
        /**
         * 设置用户
         * @param patient 用户对象
         */
        public void setPatient(PatientBean patient);

        /**
         * 设置测量记录
         * @param measureDataBean 测量记录
         */
        public void setMeasureDataBean(MeasureDataBean measureDataBean);

        /**
         * 获取用户
         * @return 用户
         */
        public PatientBean getPatientBean();

        /**
         * 获取测量记录
         * @return 测量记录
         */
        public MeasureDataBean getMeasureDataBean();

        /**
         * 清除数据信息
         */
        public void clearBean();

        /**
         * 保存用户
         * @param containerViewId containerViewId
         * @param dfragment containerViewId
         * @param isAddedStack isAddedStack
         * @param skip skip
         * @param tag tag
         */
        public void savePatient(boolean skip, int containerViewId, Fragment dfragment, String tag
                , boolean isAddedStack);
        /**
         * 保存测量记录
         */
        public void saveMeasure();
    }

    private boolean islog = true;

    public static BeanMsg beanMsg;

    /**
     * 设置对象
     * @param obj 对象
     */
    public static void setBeanMsgObj(BeanMsg obj) {
        beanMsg = obj;
    }

    /**
     * 打印
     * @param s 字符串
     */
    public void logD(String s) {
        if (islog) {
            Log.d("BaseinsertDetail", s);
        }
    }
}
