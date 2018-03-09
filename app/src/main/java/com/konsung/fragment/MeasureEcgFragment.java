package com.konsung.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.MeasureEcgBean;
import com.konsung.bean.PatientBean;
import com.konsung.data.ProviderReader;
import com.konsung.defineview.EcgReportPopupWindow;
import com.konsung.defineview.EcgSettingDialog;
import com.konsung.defineview.EcgViewFor12;
import com.konsung.msgevent.EventBusUseEvent;
import com.konsung.presenter.EcgPresenter;
import com.konsung.presenter.impl.EcgPresenterImpl;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.ParamDefine.EcgDefine;
import com.konsung.util.ReferenceUtils;
import com.konsung.util.ServiceUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.StatisticalDialogController;
import com.konsung.util.UiUitls;
import com.konsung.util.global.EcgRemoteInfoSaveModule;
import com.konsung.util.global.GlobalNumber;
import com.konsung.view.holder.EcgMeasureHolder;
import com.konsung.view.holder.EcgTutorialHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 心电测量界面
 */
public class MeasureEcgFragment extends EcgBaseFragment<EcgPresenterImpl>
        implements EcgPresenter.View, View.OnClickListener {
    private static final int LEAD_OFF_LAYOUT = 0; //引导界面
    //    private static final int LEFF_OFF_LAYOUT = 0; //导联脱落
    private static final int ELECTRODE_OFF_LAYOUT = 7; //电极脱落

    private static final int ECG_LAYOUT = 1; //实时心电界面
    private static final int MEASURE_LAYOUT = 2; //测量界面
    private static final int OVER_LAYOUT = 3; //测量结束界面
    private static final int GUIDE_BEING = 4; //提示引导
    private static final int GUIDE_FINISH = 5; //完成引导
    private static final int GUIDE_STEP = 6; //引导步骤
    private static final int SAVE_ECG_WAVE = 101; //保存心电标记

    @InjectView(R.id.tv_measure_name)
    TextView tvMeasureName;
    @InjectView(R.id.tv_measure_max)
    TextView tvMeasureMax;
    @InjectView(R.id.tv_measure_min)
    TextView tvMeasureMin;
    @InjectView(R.id.tv_measure_unit)
    TextView tvMeasureUnit;
    @InjectView(R.id.tv_measure_value)
    TextView tvMeasureValue;
    @InjectView(R.id.tv_measure_template1)
    TextView tvMeasureTemplate1;
    @InjectView(R.id.tv_measure_template2)
    TextView tvMeasureTemplate2;
    @InjectView(R.id.tv_measure_template3)
    TextView tvMeasureTemplate3;
    @InjectView(R.id.btn_measure_template1)
    Button btnStartMeasure; //启动测量
    @InjectView(R.id.btn_measure_template2)
    Button btnHistoryData; //趋势统计
    @InjectView(R.id.btn_measure_template3)
    Button btnEcgViewReport; //查看报告
    @InjectView(R.id.btn_ecg_setting)
    Button btnEcgSetting; //心电设置
    @InjectView(R.id.ecg_wave)
    EcgViewFor12 ecgWave; //波形
    //    @InjectView(R.id.all_ecg_wave)
//    EcgViewFor12 allEcgWave; //全屏波形
    @InjectView(R.id.fl_ecg_content)
    FrameLayout layoutContain;
    @InjectView(R.id.ll_ecg_value)
    LinearLayout llEcgValue; //心电记录显示
    @InjectView(R.id.rl_ecg_measure_result)
    RelativeLayout rlEcgMeasureResult; //心电测量结果回显
    @InjectView(R.id.tv_ecg_result_hr)
    TextView tvHr;
    @InjectView(R.id.tv_ecg_result_pr)
    TextView tvPr;
    @InjectView(R.id.tv_ecg_result_p_qrs_t)
    TextView tvPQrsT; //PQrsT值
    @InjectView(R.id.tv_ecg_result_qrs)
    TextView tvQrs;
    @InjectView(R.id.tv_ecg_result_qt_qtc)
    TextView tvQtQtc;
    @InjectView(R.id.tv_ecg_result_rv5_sv1)
    TextView tvRv5SV1;
    @InjectView(R.id.tv_ecg_rv5_plus_sv1)
    TextView tvEcgRv5PlusSv1;
    @InjectView(R.id.tv_ecg_result)
    TextView tvEcgResult; //心电测量结果
    @InjectView(R.id.root)
    RelativeLayout rootLayout;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (handler.hasMessages(0)) {
                    handler.removeMessages(0);
                }
            }
            if (msg.what == SAVE_ECG_WAVE) {
                byte[] data = (byte[]) msg.obj;
                presenter.saveWave(msg.arg1, data);
            }
        }
    };
    private boolean isChecking; //是否测量
    private int currentLayout = LEAD_OFF_LAYOUT; //当前界面
    private int currentEcgLead = EcgDefine.ECG_12_LEAD; //当前导联默认12
    private boolean isEcgConnect; //心电是否连接
    private boolean isShowToast; // 是否要显示toast

    private MeasureDataBean measureBean;
    private PatientBean patient;
    //当前显示的界面
    private View currentView;

    /**
     * 心电引导界面持有者
     */
    private EcgTutorialHolder guideHolder;
    private EcgMeasureHolder ecgHolder;

    private WindowManager windowManager;
    //心电报告
    private EcgReportPopupWindow ecgReport;

    //记录参数版的版本号
    private String paraBoardVersion;

    //标识是否切换到设置等其他外部页面
    private boolean isInOutPage = false;
    //标识是否有点击查看报告按钮
    private boolean isClickCheckReport = false;
    //标记是否有设置ecgView隐藏
    private boolean isEcgGone = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container
            , Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ecg_guide, container, false);
        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);
        bindEvent();
        ecgWave.setLayoutZoom(GlobalNumber.ZERO_FOUR_FIVE_FLOAT); //波形整体缩小0.5
        windowManager = (WindowManager) UiUitls.getContent()
                .getSystemService(Context.WINDOW_SERVICE);
        presenter.bindAidlService();
        GlobalConstant.ecgViewFor12 = ecgWave;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        isEcgConnect = false;
        isChecking = false;
        initViewData();

        presenter.stopMeasure();
        patient = ProviderReader.readPatient();

        if (AppFragment.probeEcgStatus != GlobalConstant.INVALID_DATA) {
            setEcgConnectStatus(AppFragment.probeEcgStatus);
        } else {
            refresh(LEAD_OFF_LAYOUT);
            refreshGuide(tvMeasureTemplate1, GUIDE_BEING);
            refreshGuide(tvMeasureTemplate2, GUIDE_STEP);
        }
        //组合判断是否是从心电点击查看报告然后切换到设置页面然后返回
        //用于解决从心电报告界面跳出去后返回导致ecgView出现在popwindow上的bug
        if (isInOutPage && isClickCheckReport) {
            isInOutPage = false;
            ecgWave.setVisibility(View.GONE);
            isEcgGone = true;
        }
    }

    /**
     * 初始化控件数据
     */
    private void initViewData() {

        if (ecgHolder == null) {
            ecgHolder = new EcgMeasureHolder(getActivity());
        }

        btnStartMeasure.setEnabled(false);
        tvMeasureTemplate3.setVisibility(View.GONE);
        tvMeasureTemplate1.setText(getString(R.string.please_lead_access));
        tvMeasureTemplate2.setText(getString(R.string.install_electrodes));
        tvMeasureName.setText(getString(R.string.ecg_hr_other));
        tvMeasureUnit.setText(getString(R.string.health_unit_bpm));
        tvMeasureMax.setText(String.valueOf((int) ReferenceUtils.
                getMaxReference(KParamType.ECG_HR)));
        tvMeasureMin.setText(String.valueOf((int) ReferenceUtils.
                getMinReference(KParamType.ECG_HR)));
    }

    /**
     * 初始化测量数据
     */
    private void initMeasureData() {
        int hr = measureBean.getTrendValue(KParamType.ECG_HR);
        UiUitls.setMeasureResult(KParamType.ECG_HR, hr, tvMeasureName, tvMeasureValue, false);
    }

    /**
     * 绑定点击事件
     */
    private void bindEvent() {
        btnEcgViewReport.setVisibility(View.VISIBLE);
        btnEcgViewReport.setText(R.string.view_report);
        btnEcgViewReport.setEnabled(false);
        btnEcgViewReport.setOnClickListener(this);
        btnStartMeasure.setOnClickListener(this);
        btnHistoryData.setOnClickListener(this);
        btnEcgSetting.setOnClickListener(this);
    }

    @Override
    public EcgPresenterImpl initPresenter() {
        paraBoardVersion = SpUtils.getSp(getActivity().getApplicationContext()
                , "app_config", "paraBoardVersion", "");
        return new EcgPresenterImpl(this, paraBoardVersion);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_measure_template1: //启动测量
                if (!isChecking && isEcgConnect && currentEcgLead == EcgDefine.ECG_12_LEAD) {
                    //启动12导诊断
                    presenter.resetMeasureDataBeanData();
                    presenter.startMeasure();
                    refresh(MEASURE_LAYOUT);
                    isChecking = true;
                    isShowToast = true;
                    AppFragment.isCheckingEcg = true;
                } else if (!isChecking && isEcgConnect) { //不在12导联的状态下就实时获取心率
                    isChecking = true;
                } else {
                    isChecking = false;
                    AppFragment.isCheckingEcg = false;
                }
                break;
            case R.id.btn_measure_template2: //趋势统计
                StatisticalDialogController statisticalController = new
                        StatisticalDialogController(getActivity(), presenter
                        .getStatisticalTableItem(), presenter.getStatisticalPic(), false);
                statisticalController.showDialog();
                break;
            case R.id.btn_measure_template3:
                View view = getActivity().getWindow().getDecorView();
                int width = view.getWidth();
                Rect outRect = new Rect();
                view.getWindowVisibleDisplayFrame(outRect);
                int height = outRect.height();
                EcgRemoteInfoSaveModule.getInstance().isFromEcgMeasure = true;
                isClickCheckReport = true;
                ecgReport = new EcgReportPopupWindow(getActivity(), patient, measureBean, width,
                        height, true, false);
                ecgReport.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                ecgReport.setOnPopWindowCloseListener(new EcgReportPopupWindow
                        .OnPopWindowCloseListener() {
                    @Override
                    public void popWindowClose() {
                        isClickCheckReport = false;
                        isInOutPage = false;
                        if (isEcgGone) {
                            isEcgGone = false;
                            ecgWave.setVisibility(View.VISIBLE);
                        }
                    }
                });
                break;
            case R.id.btn_ecg_setting:
                EcgSettingDialog dialog = new EcgSettingDialog(getActivity(),
                        UiUitls.getString(R.string.ecg_setting), new
                        EcgSettingDialog.UpdataButtonState() {
                            @Override
                            public void getButton(Boolean pressed) {
                                if (pressed) {
                                    ecgWave.setWaveSpeed();
                                }
                            }
                        });
                dialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public void refresh(int state) {
        currentLayout = state;
        switch (state) {
            case ECG_LAYOUT: //实时心电界面
                btnStartMeasure.setEnabled(true);
                ecgWave.setWaveSpeed();
                ecgHolder.remove(windowManager);
                rlEcgMeasureResult.setVisibility(View.GONE);
                ecgWave.setVisibility(View.VISIBLE);
                llEcgValue.setVisibility(View.VISIBLE);
                layoutContain.setVisibility(View.VISIBLE);
                if (currentView != null) {
                    layoutContain.removeView(currentView);
                    currentView = null;
                }
                break;
            case LEAD_OFF_LAYOUT: //导联脱落
                if (guideHolder == null) {
                    guideHolder = presenter.getInstallGuideView(getActivity(), layoutContain);
                }
                if (currentView != guideHolder.view) {
                    layoutContain.removeView(currentView);
                    currentView = guideHolder.view;
                    layoutContain.addView(currentView);
                }
                guideHolder.setViewStatus(EcgTutorialHolder.STATUS_CONNECT);
                ecgHolder.remove(windowManager);
                ecgWave.setVisibility(View.GONE);
                rlEcgMeasureResult.setVisibility(View.GONE);
                layoutContain.setVisibility(View.VISIBLE);
                llEcgValue.setVisibility(View.VISIBLE);
                break;
            case ELECTRODE_OFF_LAYOUT:
                isEcgConnect = false;
                if (guideHolder == null) {
                    guideHolder = presenter.getInstallGuideView(getActivity(), layoutContain);
                }
                if (currentView != guideHolder.view) {
                    layoutContain.removeView(currentView);
                    currentView = guideHolder.view;
                    layoutContain.addView(currentView);
                }
                guideHolder.setViewStatus(EcgTutorialHolder.STATUS_INSTALL);
                ecgHolder.remove(windowManager);
                ecgWave.setVisibility(View.GONE);
                rlEcgMeasureResult.setVisibility(View.GONE);
                layoutContain.setVisibility(View.VISIBLE);
                llEcgValue.setVisibility(View.VISIBLE);
                break;
            case MEASURE_LAYOUT: //测量界面
                ecgHolder.ecgView.setWaveSpeed();
                if (currentView != null) {
                    layoutContain.removeView(currentView);
                    currentView = null;
                }
                ecgWave.setVisibility(View.GONE);
                rlEcgMeasureResult.setVisibility(View.GONE);
                llEcgValue.setVisibility(View.GONE);
                layoutContain.setVisibility(View.GONE);
                ecgHolder.addToWindow(windowManager);
                break;
            case OVER_LAYOUT: //测量结束界面
                ecgHolder.remove(windowManager);
                ecgWave.setVisibility(View.GONE);
                if (currentView != null) {
                    layoutContain.removeView(currentView);
                    currentView = null;
                }
                btnEcgViewReport.setEnabled(true);
                rlEcgMeasureResult.setVisibility(View.VISIBLE);
                layoutContain.setVisibility(View.VISIBLE);
                llEcgValue.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    @Override
    public void measureSuccess(int ecgHr, MeasureEcgBean bean) {
        refresh(OVER_LAYOUT);
        isChecking = false;
        AppFragment.isCheckingEcg = false;
        String limit = UiUitls.getString(R.string.unit_limit);
        String mv = UiUitls.getString(R.string.unit_mv);
        String ms = UiUitls.getString(R.string.unit_ms);
        String bpm = UiUitls.getString(R.string.health_unit_bpm);
        tvPr.setText(bean.getPr() + UiUitls.getString(R.string.blank) + ms);
        tvHr.setText(ecgHr + UiUitls.getString(R.string.blank) + bpm);
        tvQrs.setText(bean.getQrs() + UiUitls.getString(R.string.blank) + ms);
        tvQtQtc.setText(bean.getQtQtc() + UiUitls.getString(R.string.blank) + ms);
        tvPQrsT.setText(bean.getpQrsT() + UiUitls.getString(R.string.blank) + limit);
        tvRv5SV1.setText(bean.getRv5Sv1() + UiUitls.getString(R.string.blank) + mv);
        tvEcgRv5PlusSv1.setText(bean.getRv5PlusSv1() + UiUitls.getString(R.string.blank) + mv);
        tvEcgResult.setText(bean.getResult());

        UiUitls.setMeasureResult(KParamType.ECG_HR, ecgHr * GlobalConstant.TREND_FACTOR
                , tvMeasureName, tvMeasureValue, false);
    }

    @Override
    public void measureError() {
        isChecking = false;
        AppFragment.isCheckingEcg = false;
        refresh(ECG_LAYOUT);
    }

    @Override
    public void refreshGuide(TextView tv, int state) {
        switch (state) {
            case GUIDE_BEING: //提示引导
                tv.setTextColor(getResources().getColor(R.color.normal_color));
                Drawable dra = getResources().getDrawable(R.drawable.ic_doing);
                dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumHeight());
                tv.setCompoundDrawables(dra, null, null, null);

                break;
            case GUIDE_FINISH: //已完成引导
                tv.setTextColor(getResources().getColor(R.color.gray));
                Drawable dra2 = getResources().getDrawable(R.drawable.ic_finished);
                dra2.setBounds(0, 0, dra2.getMinimumWidth(), dra2.getMinimumHeight());
                tv.setCompoundDrawables(dra2, null, null, null);
                break;
            case GUIDE_STEP: //引导步骤
                tv.setTextColor(getResources().getColor(R.color.gray));
                Drawable dra3 = getResources().getDrawable(R.drawable.ic_step2);
                dra3.setBounds(0, 0, dra3.getMinimumWidth(), dra3.getMinimumHeight());
                tv.setCompoundDrawables(dra3, null, null, null);
                break;
            default:
                break;
        }
    }

    @Override
    public void addEcgWaveData(int param, int data) {
        //绘制心电波形
        if (currentLayout == ECG_LAYOUT) {
            ecgWave.addEcgData(param, data);
        } else if (currentLayout == MEASURE_LAYOUT) {
            ecgHolder.addEcgData(param, data);
        }
    }

    @Override
    public void setEcgConnectStatus(int leadOff) {
        //规避三导联时，提示导联线脱落问题
//        boolean ecg3Lead = currentEcgLead == EcgDefine.ECG_3_LEAD
//                && leadOff == KParamType.ECG_FALL_OFF;
        switch (leadOff) {
            case 0: //连接正常
                if (!isEcgConnect) {
                    if (currentView != null) {
                        layoutContain.removeView(currentView);
                        currentView = null;
                    }
                    refresh(ECG_LAYOUT);
                    btnStartMeasure.setEnabled(true);
                    isEcgConnect = true;
                    tvMeasureTemplate1.setText(UiUitls.getString(R.string.please_lead_access));
                    tvMeasureTemplate2.setText(UiUitls.getString(R.string.install_electrodes));
                    refreshGuide(tvMeasureTemplate1, GUIDE_FINISH);
                    refreshGuide(tvMeasureTemplate2, GUIDE_FINISH);
                }
                break;
            case GlobalConstant.INVALID_DATA:
                //服务没启动
                presenter.stopMeasure();
                btnStartMeasure.setEnabled(false);
                isEcgConnect = false;
                isChecking = false;
                AppFragment.isCheckingEcg = false;
                refresh(LEAD_OFF_LAYOUT);
                if (isShowToast) {
                    UiUitls.toast(UiUitls.getContent(), UiUitls.getString(R.string
                            .ecg_pls_checkfordevice));
                    isShowToast = false;
                }
                break;
            case GlobalNumber.FIVE_ONE_ONE_NUMBER: //导联脱落
                presenter.stopMeasure();
                btnStartMeasure.setEnabled(false);
                isEcgConnect = false;
                isChecking = false;
                AppFragment.isCheckingEcg = false;
                refresh(LEAD_OFF_LAYOUT);
                refreshGuide(tvMeasureTemplate1, GUIDE_BEING);
                refreshGuide(tvMeasureTemplate2, GUIDE_STEP);
                tvMeasureTemplate2.setText(UiUitls.getString(R.string.install_electrodes));
                break;
            default: //电极脱落
                if (guideHolder != null) {
                    guideHolder.setLeadOffSignal(leadOff);
                }
                presenter.stopMeasure();
                btnStartMeasure.setEnabled(false);
                isEcgConnect = false;
                isChecking = false;
                AppFragment.isCheckingEcg = false;
                refresh(ELECTRODE_OFF_LAYOUT);
                refreshGuide(tvMeasureTemplate1, GUIDE_FINISH);
                refreshGuide(tvMeasureTemplate2, GUIDE_BEING);
                tvMeasureTemplate2.setText(UiUitls.getString(R.string.install_electrodes));
                if (currentEcgLead == EcgDefine.ECG_12_LEAD) {
                    btnStartMeasure.setText(UiUitls.getString(R.string.start_measure));
                } else {
                    btnStartMeasure.setText(UiUitls.getString(R.string.get_hr));
                }
                break;
        }
    }

    @Override
    public void saveEcgWave(int param, byte[] bytes) {
        //测量状态下，保存心电波形
        if (isChecking && isEcgConnect) {
            Message msg = Message.obtain();
            msg.what = SAVE_ECG_WAVE;
            msg.arg1 = param;
            msg.obj = bytes;
            //发送数据到Handler保存
            handler.sendMessage(msg);
        }
    }

    @Override
    public void setMeasureDataBean(MeasureDataBean measureDataBean) {
        this.measureBean = measureDataBean;
        initMeasureData();
        if (measureDataBean.getTrendValue(KParamType.ECG_HR) != GlobalConstant.INVALID_DATA) {
            btnEcgViewReport.setEnabled(true);
        } else {
            btnEcgViewReport.setEnabled(false);
        }
        EventBus.getDefault().post(new EventBusUseEvent(AppFragment.ECG_FLAG));
    }

    @Override
    public void onPause() {
        super.onPause();
        layoutContain.removeView(currentView);
        if (guideHolder != null) {
            guideHolder.guideView.stop();
        }
        currentView = null;
        guideHolder = null;
        isEcgConnect = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isChecking = false;
        AppFragment.isCheckingEcg = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        isChecking = false;
        presenter.stopMeasure();
        isInOutPage = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * 接受用户切换后立刻刷新当前数据信息
     * @param event 事件对象
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshData(EventBusUseEvent event) {
        if (event.getFlag().equals(GlobalConstant.MEASURE_USER_SWITCH)) {
            //同步左列表数据
            setMeasureDataBean(ServiceUtils.getMeausreDataBean());
            isEcgConnect = false;
            if (AppFragment.probeEcgStatus != GlobalConstant.INVALID_DATA) {
                setEcgConnectStatus(AppFragment.probeEcgStatus);
            } else {
                refresh(LEAD_OFF_LAYOUT);
                refreshGuide(tvMeasureTemplate1, GUIDE_BEING);
                refreshGuide(tvMeasureTemplate2, GUIDE_STEP);
            }
        }
    }
}
