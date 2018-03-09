package com.konsung.presenter.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konsung.R;
import com.konsung.bean.PatientBean;
import com.konsung.bean.StatisticalPicBean;
import com.konsung.data.ProviderReader;
import com.konsung.presenter.BasePresenter;
import com.konsung.presenter.TempPresenter;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.ServiceUtils;
import com.konsung.util.UiUitls;

import java.util.ArrayList;
import java.util.List;

/**
 * 体温逻辑实现
 **/

public class TempPresenterImpl extends BasePresenter<TempPresenter.View>
        implements TempPresenter.Presenter {

    TempPresenter.View view;

    /**
     * 构造函数
     * @param view 布局操作
     */
    public TempPresenterImpl(TempPresenter.View view) {
        this.view = view;
    }

    @Override
    public void bindAidlService() {
        initMeasureListener();
    }

    @Override
    public List<Integer> getStatisticalTableItem() {
        List<Integer> paramList = new ArrayList<>();
        paramList.add(KParamType.IRTEMP_TREND); //趋势表需要显示多少个参数，吧相对应的参数传进来即可
        return paramList;
    }

    @Override
    public StatisticalPicBean[] getStatisticalPic(Context context) {
        //获取当前病人信息
        PatientBean bean = ProviderReader.readPatient();
        StatisticalPicBean picBean = new StatisticalPicBean();
        picBean.setIdCard(bean.getIdCard());
        picBean.setDataSize(10); //数据长度10个
        picBean.setStartCount(0); //数据库查询起始位置0
        picBean.setMaxValue(50); //y刻度值最大300
        picBean.setMinValue(30); //y刻度值最小0
        picBean.setySize(10); //y轴10个刻度
        picBean.setParameter(KParamType.IRTEMP_TREND);
        picBean.setUnit(UiUitls.getString(R.string.health_unit_degree));
        StatisticalPicBean[] beans = {picBean};
        return beans;
    }

    @Override
    public void unbindService() {

    }

    /**
     * 获取设备安装引导界面
     * @param context 上下文
     * @param root 父布局
     * @return 显示布局
     */
    public View getInstallGuideView(Context context, ViewGroup root) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.layout_temp_tutorial, root, false);
        return view;
    }

    /**
     * 获取测量成功界面
     * @param context 上下文
     * @param root 父布局
     * @return 测量界面的View
     */
    public View getSuccessLayout(Context context, ViewGroup root) {
        return LayoutInflater.from(context).inflate(R.layout.layout_spo2_mesure_success, root,
                false);
    }

    /**
     * 初始化服务监听
     */
    private void initMeasureListener() {
        view.setDataBean(ServiceUtils.getMeausreDataBean());
        ServiceUtils.setOnMessageSendListener(new ServiceUtils.OnMessageSendListener() {
            @Override
            public void sendParaStatus(String name, String version) {
            }

            @Override
            public void sendWave(int param, byte[] bytes) {
            }

            @Override
            public void sendTrend(int param, int value) {
                switch (param) {
                    //接触式
                    case KParamType.TEMP_T1:
                    case KParamType.TEMP_T2:
                    case KParamType.TEMP_TD:

                        break;
                    //额温
                    case KParamType.IRTEMP_TREND:
                        if (value != GlobalConstant.INVALID_DATA) {
                            GlobalConstant.IR_TEMP_VALUE = value / GlobalConstant.SWITCH_VALUE;
                            view.measureSuccess(value);
                            ServiceUtils.saveTrend(KParamType.IRTEMP_TREND, value);
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
