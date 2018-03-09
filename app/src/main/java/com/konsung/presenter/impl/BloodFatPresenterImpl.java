package com.konsung.presenter.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.StatisticalPicBean;
import com.konsung.presenter.BasePresenter;
import com.konsung.presenter.BloodFatPresenter;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * 血脂逻辑实现
 **/

public class BloodFatPresenterImpl extends BasePresenter<BloodFatPresenter.View>
        implements BloodFatPresenter.Presenter {

    BloodFatPresenter.View view;
    private float temp;
    private final int minValue = -10;
    private final int maxValue = -100;
    private final float minFloatValue = -10f;
    private final float maxFloatValue = -100f;
    private final float switchNum = 100f;
    /**
     * 构造函数
     * @param view 布局操作
     */
    public BloodFatPresenterImpl(BloodFatPresenter.View view) {
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
                int invalidData = GlobalConstant.INVALID_DATA;
                if (value == minValue) {
                    temp = minFloatValue;
                } else if (value == maxValue) {
                    temp = maxFloatValue;
                } else {
                    temp = value / switchNum;
                }
                switch (param) {
                    case KParamType.BLOOD_FAT_CHO:
                        if (value != GlobalConstant.INVALID_DATA) {
                            GlobalConstant.LIPIDS_CHOL_VALUE = temp;
                            view.measureSuccess(value, invalidData, invalidData,
                                    invalidData);
                            ServiceUtils.saveTrend(param, value);
                        }
                        break;
                    case KParamType.BLOOD_FAT_TRIG:
                        if (value != GlobalConstant.INVALID_DATA) {
                            GlobalConstant.LIPIDS_TRIG_VALUE = temp;
                            view.measureSuccess(invalidData, value, invalidData,
                                    invalidData);
                            ServiceUtils.saveTrend(param, value);
                        }
                        break;
                    case KParamType.BLOOD_FAT_HDL:
                        if (value != GlobalConstant.INVALID_DATA) {
                            GlobalConstant.LIPIDS_HDL_VALUE = temp;
                            view.measureSuccess(invalidData, invalidData, invalidData,
                                    value);
                            ServiceUtils.saveTrend(param, value);
                        }
                        break;
                    case KParamType.BLOOD_FAT_LDL:
                        if (value != GlobalConstant.INVALID_DATA) {
                            GlobalConstant.LIPIDS_LDL_VALUE = temp;
                            view.measureSuccess(invalidData, invalidData, value,
                                    invalidData);
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
        paramList.add(KParamType.BLOOD_FAT_CHO); //趋势表需要显示多少个参数，吧相对应的参数传进来即可
        paramList.add(KParamType.BLOOD_FAT_TRIG);
        paramList.add(KParamType.BLOOD_FAT_LDL);
        paramList.add(KParamType.BLOOD_FAT_HDL);
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
        picBean.setMaxValue(GlobalNumber.TEN_NUMBER); //y刻度值最大300
        picBean.setMinValue(0); //y刻度值最小0
        picBean.setySize(GlobalNumber.TEN_NUMBER); //y轴10个刻度
        picBean.setParameter(KParamType.BLOOD_FAT_CHO);
        picBean.setUnit(UiUitls.getString(R.string.unit_mmol_l));
        StatisticalPicBean picBean2 = new StatisticalPicBean();
        picBean2.setIdCard(currentIdCard);
        picBean2.setDataSize(GlobalNumber.TEN_NUMBER); //数据长度10个
        picBean2.setStartCount(0); //数据库查询起始位置0
        picBean2.setMaxValue(GlobalNumber.TEN_NUMBER); //y刻度值最大300
        picBean2.setMinValue(0); //y刻度值最小0
        picBean2.setySize(GlobalNumber.TEN_NUMBER); //y轴10个刻度
        picBean2.setParameter(KParamType.BLOOD_FAT_TRIG);
        picBean2.setUnit(UiUitls.getString(R.string.unit_mmol_l));
        StatisticalPicBean picBean3 = new StatisticalPicBean();
        picBean3.setIdCard(currentIdCard);
        picBean3.setDataSize(GlobalNumber.TEN_NUMBER); //数据长度10个
        picBean3.setStartCount(0); //数据库查询起始位置0
        picBean3.setMaxValue(GlobalNumber.TEN_NUMBER); //y刻度值最大300
        picBean3.setMinValue(0); //y刻度值最小0
        picBean3.setySize(GlobalNumber.TEN_NUMBER); //y轴10个刻度
        picBean3.setParameter(KParamType.BLOOD_FAT_LDL);
        picBean3.setUnit(UiUitls.getString(R.string.unit_mmol_l));
        StatisticalPicBean picBean4 = new StatisticalPicBean();
        picBean4.setIdCard(currentIdCard);
        picBean4.setDataSize(GlobalNumber.TEN_NUMBER); //数据长度10个
        picBean4.setStartCount(0); //数据库查询起始位置0
        picBean4.setMaxValue(GlobalNumber.TEN_NUMBER); //y刻度值最大300
        picBean4.setMinValue(0); //y刻度值最小0
        picBean4.setySize(GlobalNumber.TEN_NUMBER); //y轴10个刻度
        picBean4.setParameter(KParamType.BLOOD_FAT_HDL);
        picBean4.setUnit(UiUitls.getString(R.string.unit_mmol_l));
        StatisticalPicBean[] beans = {picBean, picBean2, picBean3, picBean4};
        return beans;
    }

    @Override
    public void unbindService() {
    }

    /**
     * 获取安装引导界面
     * @param root 父布局
     * @param context 上下文
     * @param onClickListener 观看操作视频文本点击事件
     * @return 引导界面的View
     */
    public View getInstallGuideView(Context context, ViewGroup root, View.OnClickListener
            onClickListener) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.layout_hemoglobin_tutorial, root, false);
        TextView tvIntroduction = (TextView) view.findViewById(R.id.tv_introduction);
        TextView tvLookVideo = (TextView) view.findViewById(R.id.tv_look);
        tvLookVideo.setOnClickListener(onClickListener);
        return view;
    }
}
