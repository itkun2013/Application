package com.konsung.presenter.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.StatisticalPicBean;
import com.konsung.presenter.BasePresenter;
import com.konsung.presenter.GHbPresenter;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;

import java.util.ArrayList;
import java.util.List;

/**
 * 糖化血红蛋白-逻辑
 **/
public class GHbPresenterImpl extends BasePresenter<GHbPresenter.View>
        implements GHbPresenter.Presenter {

    private GHbPresenter.View view;
    private float temp;
    private final int minValue = -10;
    private final int maxValue = -100;
    private final float minFloatValue = -10f;
    private final float maxFloatValue = -100f;
    private final float switchNum = 100f;
    private final int num = 18;
    /**
     * 糖化血红蛋白Presenter
     * @param view 布局操作
     */
    public GHbPresenterImpl(GHbPresenter.View view) {
        this.view = view;
    }

    /**
     * 绑定服务
     */
    @Override
    public void bindAidlService() {
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
                if (value == minValue) {
                    temp = minFloatValue;
                } else if (value == maxValue) {
                    temp = maxFloatValue;
                } else {
                    temp = value / switchNum;
                }
                switch (param) {
                    case KParamType.GHB_HBA1C_NGSP:
                        if (value != GlobalConstant.INVALID_DATA) {
                            GlobalConstant.HBA1C_NGSP = temp;
                            ServiceUtils.saveTrend(param, value);
                            view.measureSuccess(value,
                                    GlobalConstant.INVALID_DATA, GlobalConstant.INVALID_DATA);
                        }
                        break;
                    case KParamType.GHB_HBA1C_IFCC:
                        if (value != GlobalConstant.INVALID_DATA) {
                            GlobalConstant.HBA1C_IFCC = temp;
                            ServiceUtils.saveTrend(param, value);
                            view.measureSuccess(GlobalConstant.INVALID_DATA,
                                    value, GlobalConstant.INVALID_DATA);
                        }
                        break;
                    case KParamType.GHB_EAG:
                        if (value != GlobalConstant.INVALID_DATA) {
                            ServiceUtils.saveTrend(param, value);
                            ServiceUtils.saveToDb2();
                            GlobalConstant.HBA1C_EAG = temp;
                            view.measureSuccess(GlobalConstant.INVALID_DATA,
                                    GlobalConstant.INVALID_DATA, value);
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

    /**
     * 获取安装引导界面
     * @param context 上下文
     * @param root 父布局
     * @param onClickListener 观看操作视频文本点击事件
     * @return 引导界面的View
     */
    public View getInstallGuideView(Context context, ViewGroup root, View.OnClickListener
            onClickListener) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.layout_ghb_tutorial, root, false);
        TextView tvIntroduction = (TextView) view.findViewById(R.id.tv_introduction);
        TextView tvLookVideo = (TextView) view.findViewById(R.id.tv_look);
        tvLookVideo.setOnClickListener(onClickListener);
        return view;
    }

    @Override
    public List<Integer> getStatisticalTableItem() {
        List<Integer> paramList = new ArrayList<>();
        paramList.add(KParamType.GHB_HBA1C_NGSP); //趋势表需要显示多少个参数，吧相对应的参数传进来即可
        paramList.add(KParamType.GHB_HBA1C_IFCC);
        paramList.add(KParamType.GHB_EAG);
        return paramList;
    }

    @Override
    public StatisticalPicBean[] getStatisticalPic(Context context) {
        String currentIdCard = SpUtils.getSp(context, GlobalConstant.APP_CONFIG
                , GlobalConstant.ID_CARD, "");
        StatisticalPicBean picBean = new StatisticalPicBean();
        picBean.setIdCard(currentIdCard);
        picBean.setDataSize(10); //数据长度10个
        picBean.setStartCount(0); //数据库查询起始位置0
        picBean.setMaxValue(150); //y刻度值最大300
        picBean.setMinValue(0); //y刻度值最小0
        picBean.setySize(10); //y轴10个刻度
        picBean.setParameter(KParamType.GHB_HBA1C_IFCC);
        picBean.setUnit(UiUitls.getString(R.string.unit_mmol_mol));
        StatisticalPicBean picBean2 = new StatisticalPicBean();
        picBean2.setIdCard(currentIdCard);
        picBean2.setDataSize(10); //数据长度10个
        picBean2.setStartCount(0); //数据库查询起始位置0
        picBean2.setMaxValue(400); //y刻度值最大300
        picBean2.setMinValue(0); //y刻度值最小0
        picBean2.setySize(10); //y轴10个刻度
        picBean2.setParameter(KParamType.GHB_EAG);
        picBean2.setUnit(UiUitls.getString(R.string.unit_mg_dl));
        StatisticalPicBean[] beans = {picBean, picBean2};
        return beans;
    }

    @Override
    public void unbindService() {
    }
}
