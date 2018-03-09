package com.konsung.presenter.impl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.konsung.R;
import com.konsung.fragment.MeasureBloodThreeFragment;
import com.konsung.presenter.BasePresenter;
import com.konsung.presenter.BeneTrinityPresenter;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.ServiceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 百捷三合一逻辑实现
 **/
public class BeneTrinityPresenterImpl extends BasePresenter<BeneTrinityPresenter.View>
        implements BeneTrinityPresenter.Presenter {

    BeneTrinityPresenter.View view;

    /**
     * 构造函数
     * @param view 布局操作
     */
    public BeneTrinityPresenterImpl(BeneTrinityPresenter.View view) {
        this.view = view;
    }

    @Override
    public void bindAidlService() {
        initMeasureListener();
    }

    @Override
    public List<Integer> getStatisticalTableItem() {
        List<Integer> paramList = new ArrayList<>();
        paramList.add(KParamType.BLOODGLU_BEFORE_MEAL); //趋势表需要显示多少个参数，吧相对应的参数传进来即可
        paramList.add(KParamType.URICACID_TREND);
        paramList.add(KParamType.CHOLESTEROL_TREND);
        return paramList;
    }

    @Override
    public void saveGlu(int param, int value) {
        ServiceUtils.saveTrend(param, value);
        ServiceUtils.saveToDb2();
    }

    @Override
    public void unbindService() {
    }

    /**
     * 获取安装引导界面
     * @param context 上下文
     * @param root 父布局
     * @return 引导界面的View
     */
    public View getInstallGuideView(Context context, ViewGroup root) {
        View view = LayoutInflater.from(context).
                inflate(R.layout.layout_glu_tutorial, root, false);
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
                    case KParamType.URICACID_TREND:
                        if (value != GlobalConstant.INVALID_DATA) {
                            GlobalConstant.URIC_ACID_VALUE = value / GlobalConstant.SWITCH_VALUE;
                            view.measureSuccess(invalidData, value, invalidData);
                            ServiceUtils.saveTrend(param, value);
                            ServiceUtils.saveToDb2();
                        }
                        break;
                    case KParamType.CHOLESTEROL_TREND:
                        if (value != GlobalConstant.INVALID_DATA) {
                            GlobalConstant.CHOLESTEROL_VALUE = value / GlobalConstant.SWITCH_VALUE;
                            view.measureSuccess(invalidData, invalidData, value);
                            ServiceUtils.saveTrend(param, value);
                            ServiceUtils.saveToDb2();
                        }
                        break;
                    case KParamType.BLOODGLU_AFTER_MEAL:
                        if (value != GlobalConstant.INVALID_DATA) {
                            GlobalConstant.BLOOD_GLU_VALUE = value / GlobalConstant.SWITCH_VALUE;
                            view.measureSuccess(value, invalidData, invalidData);
                            ServiceUtils.saveTrend(param, value);
                            ServiceUtils.saveToDb2();
                        }
                        break;
                    case KParamType.BLOODGLU_BEFORE_MEAL:
                        if (value != GlobalConstant.INVALID_DATA) {
                            GlobalConstant.BLOOD_GLU_VALUE = value / GlobalConstant.SWITCH_VALUE;
                            GlobalConstant.BLOODGLUSTATE
                                    = MeasureBloodThreeFragment.currentGlu + "";
                            view.measureSuccess(value, invalidData, invalidData);
                            ServiceUtils.saveTrend(param, value);
                            ServiceUtils.saveBloodGluState(GlobalConstant.BLOODGLUSTATE);
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
