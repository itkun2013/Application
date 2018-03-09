package com.konsung.presenter.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.konsung.R;
import com.konsung.bean.PatientBean;
import com.konsung.bean.StatisticalPicBean;
import com.konsung.data.ProviderReader;
import com.konsung.presenter.BasePresenter;
import com.konsung.presenter.WbcPresenter;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.ServiceUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * 白细胞逻辑实现
 **/

public class WbcPresenterImpl extends BasePresenter<WbcPresenter.View>
        implements WbcPresenter.Presenter {

    WbcPresenter.View view;

    /**
     * 构造函数
     * @param view 布局操作
     */
    public WbcPresenterImpl(WbcPresenter.View view) {
        this.view = view;
    }

    @Override
    public void bindAidlService() {
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
                switch (param) {
                    case KParamType.BLOOD_WBC:
                        if (value != GlobalConstant.INVALID_DATA) {
                            GlobalConstant.BLOOD_WBC_VALUE
                                    = (float) value / GlobalConstant.WBC_FACTOR;
                            view.measureSuccess(value);
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
            public void sendPersonalDetail(String name, String idcard, int sex, int type
                    , String pic, String address) {

            }

            @Override
            public void send12LeadDiaResult(byte[] bytes) {

            }

            @Override
            public void sendUnConnectMessageSend() {

            }
        });
    }

    @Override
    public List<Integer> getStatisticalTableItem() {
        List<Integer> paramList = new ArrayList<>();
        paramList.add(KParamType.BLOOD_WBC); //趋势表需要显示多少个参数，吧相对应的参数传进来即可
        return paramList;
    }

    @Override
    public StatisticalPicBean[] getStatisticalPic(Context context) {
        //获取当前病人信息
        PatientBean bean = ProviderReader.readPatient();
        StatisticalPicBean picBean = new StatisticalPicBean();
        picBean.setIdCard(bean.getIdCard());
        picBean.setDataSize(GlobalNumber.TEN_NUMBER); //数据长度10个
        picBean.setStartCount(0); //数据库查询起始位置0
        picBean.setMaxValue(GlobalNumber.THIRTY_NUBER); //y刻度值最大300
        picBean.setMinValue(0); //y刻度值最小0
        picBean.setySize(GlobalNumber.TEN_NUMBER); //y轴10个刻度
        picBean.setParameter(KParamType.BLOOD_WBC);
        picBean.setUnit(UiUitls.getString(R.string.unit_index));
        StatisticalPicBean[] beans = {picBean};
        return beans;
    }

    @Override
    public void unbindService() {}

    /**
     * 获取安装引导界面
     * @param context 上下文
     * @param onClickListener 观看操作视频文本点击事件
     * @param root 父布局
     * @return 引导界面的View
     */
    public View getInstallGuideView(Context context, ViewGroup root, View.OnClickListener
            onClickListener) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.layout_wbc_tutorial, root, false);
        TextView tvLookVideo = (TextView) view.findViewById(R.id.tv_look);
        tvLookVideo.setOnClickListener(onClickListener);
        return view;
    }
}
