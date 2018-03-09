package com.konsung.presenter.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konsung.R;
import com.konsung.bean.StatisticalPicBean;
import com.konsung.presenter.BasePresenter;
import com.konsung.presenter.HemoglobinPresenter;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.NumUtil;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * 血红蛋白逻辑实现
 **/

public class HemoglobinPresenterImpl extends BasePresenter<HemoglobinPresenter.View>
        implements HemoglobinPresenter.Presenter {

    HemoglobinPresenter.View view;

    /**
     * 构造函数
     * @param view 布局操作
     */
    public HemoglobinPresenterImpl(HemoglobinPresenter.View view) {
        this.view = view;
    }

    @Override
    public void bindAidlService() {
        initMeasureListener();
    }

    @Override
    public List<Integer> getStatisticalTableItem() {
        List<Integer> paramList = new ArrayList<>();
        paramList.add(KParamType.BLOOD_HGB); //趋势表需要显示多少个参数，吧相对应的参数传进来即可
        paramList.add(KParamType.BLOOD_HCT);
        return paramList;
    }

    @Override
    public StatisticalPicBean[] getStatisticalPic(Context context) {
        String currentIdCard = SpUtils.getSp(context, GlobalConstant.APP_CONFIG
                , GlobalConstant.ID_CARD, "");
        StatisticalPicBean picBean = new StatisticalPicBean();
        picBean.setIdCard(currentIdCard);
        picBean.setDataSize(GlobalNumber.TEN_NUMBER); //数据长度10个
        picBean.setStartCount(0); //数据库查询起始位置0
        picBean.setMaxValue(GlobalNumber.THREE_HUNDRED_NUMBER); //y刻度值最大300
        picBean.setMinValue(0); //y刻度值最小0
        picBean.setySize(GlobalNumber.TEN_NUMBER); //y轴10个刻度
        picBean.setParameter(KParamType.BLOOD_HGB);
        picBean.setUnit(UiUitls.getString(R.string.health_unit_mol));
        StatisticalPicBean picBean2 = new StatisticalPicBean();
        picBean2.setIdCard(currentIdCard);
        picBean2.setDataSize(GlobalNumber.TEN_NUMBER); //数据长度10个
        picBean2.setStartCount(0); //数据库查询起始位置0
        picBean2.setMaxValue(GlobalNumber.THREE_HUNDRED_NUMBER); //y刻度值最大300
        picBean2.setMinValue(0); //y刻度值最小0
        picBean2.setySize(GlobalNumber.TEN_NUMBER); //y轴10个刻度
        picBean2.setParameter(KParamType.BLOOD_HCT);
        picBean2.setUnit(UiUitls.getString(R.string.x_measure_unit_percent));
        StatisticalPicBean[] beans = {picBean, picBean2};
        return beans;
    }

    @Override
    public void unbindService() {

    }

    /**
     * 获取安装引导界面
     * @param context 上下文
     * @param root 上下文
     * @return 引导界面的View
     */
    public View getInstallGuideView(Context context, ViewGroup root) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.layout_hemoglobin_tutorial, root, false);
        return view;
    }

    /**
     * 初始化服务监听
     */
    private void initMeasureListener() {
        view.setMeasureData(ServiceUtils.getMeausreDataBean());
        ServiceUtils.setOnMessageSendListener(new ServiceUtils.OnMessageSendListener() {

            @Override
            public void sendParaStatus(String name, String version) {

            }

            @Override
            public void sendWave(int param, byte[] bytes) {

            }

            @Override
            public void sendTrend(int param, int value) {
                int invalidData = GlobalConstant.INVALID_DATA;
                switch (param) {
                    case KParamType.BLOOD_HGB:
                        if (value != GlobalConstant.INVALID_DATA) {
                            float v1 = (float) value / GlobalConstant.TREND_FACTOR
                                    / GlobalNumber.TEN_NUMBER_FLOAT * GlobalConstant.TREND_HGB;
                            // 获取转换后的值
                            float v2 = NumUtil.trans2FloatValue(v1, 1);
                            GlobalConstant.BLOOD_HGB_VALUE = v2;
                            int hgbValue = (int) (v2 * GlobalConstant.TREND_FACTOR);
                            view.measureSuccess(hgbValue, invalidData);
                            ServiceUtils.saveTrend(param, hgbValue);
                        }
                        break;
                    case KParamType.BLOOD_HCT:
                        if (value != GlobalConstant.INVALID_DATA) {
                            GlobalConstant.BLOOD_HCT_VALUE = value / GlobalConstant.TREND_FACTOR;
                            view.measureSuccess(invalidData, value);
                            ServiceUtils.saveTrend(param, value);
                            ServiceUtils.saveToDb2();
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void sendConfig(int param, int value) {

            }

            @Override
            public void sendPersonalDetail(String name, String idcard, int sex, int type, String
                    pic, String address) {

            }

            @Override
            public void send12LeadDiaResult(byte[] bytes) {

            }

            @Override
            public void sendUnConnectMessageSend() {

            }
        });
    }
}
