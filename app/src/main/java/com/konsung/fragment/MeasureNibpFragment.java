package com.konsung.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.defineview.NibpSettingDialog;
import com.konsung.msgevent.EventBusUseEvent;
import com.konsung.presenter.NibpPresenter;
import com.konsung.presenter.impl.NibpPresenterImpl;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.ReferenceUtils;
import com.konsung.util.ServiceUtils;
import com.konsung.util.StatisticalDialogController;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 血压测量页面
 **/
public class MeasureNibpFragment extends com.konsung.view.BaseFragment<NibpPresenterImpl>
        implements NibpPresenter.View, View.OnClickListener {

    public static final int GUIDE_LAYOUT = 0; //引导界面
    public static final int MEASURE_LAYOUT = 1; //测量界面
    public static final int OVER_LAYOUT = 2; //测量结束界面
    public static final int MEASURE_FAILED_LAYOUT = 5; //测量超时页面
//    public static final int TEST_LAYOUT = 7; //漏气检测和校准

    public static final int GUIDE_BEING = 4; //提示引导
    public static final int GUIDE_FINISH = 5; //完成引导
    public static final int GUIDE_STEP2 = 6; //引导步骤
    public static final int GUIDE_STEP1 = 7; //引导步骤

    //舒张压布局
    @InjectView(R.id.relative_layout1)
    RelativeLayout valueLayout1;
    //收缩压布局
    @InjectView(R.id.relative_layout2)
    RelativeLayout valueLayout2;
    //测量刷新布局
    @InjectView(R.id.layout_contain)
    RelativeLayout containLayout;
    //连接一体机
    @InjectView(R.id.tv_measure_template1)
    TextView tvInstall;
    //捆绑手臂
    @InjectView(R.id.tv_measure_template2)
    TextView tvInsert;
    @InjectView(R.id.btn_measure_template1)
    Button btnMeasure;
    @InjectView(R.id.btn_measure_template3)
    Button btnSetting;
    @InjectView(R.id.btn_measure_template2)
    Button btnTrend;
    @InjectView(R.id.btn_ecg_setting)
    Button ecgSetting;

    //舒张压
    private TextView tvSys;
    private TextView tvSysName;

    //收缩压
    private TextView tvDia;
    private TextView tvDiaName;

    //袖带压
    private TextView tvCuff;
    private TextView tvCuffName;

    //引导界面
    private View guideView = null;
    //测量界面
    private View checkView = null;
    //测量成功界面
    private View checkSuccessView = null;

    private View currentView = null;

    private View view;

    private boolean checking = false;

    private MeasureDataBean measureDataBean = null;

    //趋势图
    private NibpSettingDialog settingDialog;
    private StatisticalDialogController statisticalController;
    private long lastClickTimestamp = 0;
    //textsize
    private final int textSize = 30;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        //血压与血氧布局差不不大，共用布局
        view = inflater.inflate(R.layout.fragment_spo2_new, container, false);
        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        presenter.bindAidlService();
    }

    /**
     * 初始化布局
     */
    private void initView() {
        refresh(GUIDE_LAYOUT);
        refreshGuide(tvInstall, GUIDE_STEP1);
        refreshGuide(tvInsert, GUIDE_STEP2);
        initSYS();
        initDia();
        ecgSetting.setVisibility(View.GONE);
        tvInstall.setText(getString(R.string.nibp_connect_healthone));
        tvInsert.setText(getString(R.string.nibp_bind_cuff));
        btnMeasure.setOnClickListener(this);
        btnTrend.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        btnSetting.setVisibility(View.VISIBLE);
        btnSetting.setText(R.string.nibp_setting);
        setDateBean(measureDataBean);
    }

    /**
     * 初始化血压
     */
    private void initSYS() {
        tvSysName = (TextView) valueLayout1.findViewById(R.id.tv_measure_name);
        tvSysName.setTextColor(UiUitls.getColor(R.color.measure_name_text_color));
        tvSysName.setText(R.string.nibp_sys_other);
        TextView tvSysMax = (TextView) valueLayout1.findViewById(R.id.tv_measure_max);
        //参数值未定，还需要重做，暂时写死，
        tvSysMax.setText(String.valueOf((int) ReferenceUtils.getMaxReference(KParamType.NIBP_SYS)));
        TextView tvSysMin = (TextView) valueLayout1.findViewById(R.id.tv_measure_min);
        tvSysMin.setText(String.valueOf((int) ReferenceUtils.getMinReference(KParamType.NIBP_SYS)));
        tvSys = (TextView) valueLayout1.findViewById(R.id.tv_measure_value);
        tvSys.setTextColor(UiUitls.getColor(R.color.measure_value_text_color));

        tvSys.setText(R.string.invalid_data);
        TextView tvSpo2Unit = (TextView) valueLayout1.findViewById(R.id.tv_measure_unit);
        tvSpo2Unit.setText(R.string.unit_mmhg);
    }

    /**
     * 初始化脉率
     */
    private void initDia() {
        tvDiaName = (TextView) valueLayout2.findViewById(R.id.tv_measure_name);
        tvDiaName.setTextColor(UiUitls.getColor(R.color.measure_name_text_color));

        tvDiaName.setText(R.string.nibp_dia_other);
        TextView tvDiaMax = (TextView) valueLayout2.findViewById(R.id.tv_measure_max);
        //参数值未定，还需要重做，暂时写死，
        tvDiaMax.setText(String.valueOf((int) ReferenceUtils.getMaxReference(KParamType.NIBP_DIA)));
        TextView tvDiaMin = (TextView) valueLayout2.findViewById(R.id.tv_measure_min);
        tvDiaMin.setText(String.valueOf((int) ReferenceUtils.getMinReference(KParamType.NIBP_DIA)));
        tvDia = (TextView) valueLayout2.findViewById(R.id.tv_measure_value);
        tvDia.setTextColor(UiUitls.getColor(R.color.measure_value_text_color));

        tvDia.setText(R.string.invalid_data);
        TextView tvSpo2Unit = (TextView) valueLayout2.findViewById(R.id.tv_measure_unit);
        tvSpo2Unit.setText(R.string.unit_mmhg);
    }

    @Override
    public void onClick(View v) {
        //预防点击过快
        long time = System.currentTimeMillis() - lastClickTimestamp;
        if (time < GlobalConstant.CLICK_TIMES && time > 0) {
            return;
        }
        lastClickTimestamp = System.currentTimeMillis();
        switch (v.getId()) {
            //启动测量
            case R.id.btn_measure_template1:
                if (checking) {
                    presenter.stopMeasure();
                    refresh(GUIDE_LAYOUT);
                    checking = false;
                    tvCuffName.setText(R.string.nibp_cuff);
                    tvCuffName.setVisibility(View.VISIBLE);
                    tvCuff.setVisibility(View.VISIBLE);
                    tvCuff.setText("0");
                } else {
                    if (settingDialog != null) {
                        if (settingDialog.getCheckState()) {
                            presenter.stopMeasure();
                            settingDialog.resetUI();
                        }
                    }
                    presenter.startMeasure();
                    tvCuff.setVisibility(View.VISIBLE);
                    initDia();
                    initSYS();
                    checking = true;
                    btnSetting.setEnabled(false);
                }
                break;
            //趋势统计
            case R.id.btn_measure_template2:
                StatisticalDialogController statisticalController = new
                        StatisticalDialogController(getActivity(), presenter
                        .getStatisticalTableItem(), presenter.getStatisticalPic(getActivity()),
                        false);
                statisticalController.showDialog();
                break;
            case R.id.btn_measure_template3:
                if (settingDialog == null) {
                    settingDialog = new NibpSettingDialog(getActivity());
                    settingDialog.addOnButtonClickListener(buttonClickListener);
                }
                settingDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public void refresh(int state) {

        switch (state) {
            //引导页面
            case GUIDE_LAYOUT:
                if (guideView == null) {
                    guideView = presenter.getInstallGuideView(getActivity(), containLayout
                            , null);
                }
                if (currentView != guideView) {
                    changeView(guideView);
                }
                btnMeasure.setText(R.string.start_measure);
                btnSetting.setEnabled(true);
                btnMeasure.setBackgroundResource(R.drawable.btn_selector_blue);
                checking = false;
                break;

            //测量
            case MEASURE_LAYOUT:
                if (checkView == null) {
                    checkView = presenter.getMeasureLayout(getActivity(), containLayout);
                }
                if (currentView != checkView) {
                    changeView(checkView);
                }
                tvCuff = (TextView) checkView.findViewWithTag(
                        NibpPresenterImpl.TAG_CUFF_VALUE_TEXT_VIEW);
                tvCuffName = (TextView) checkView.findViewWithTag(
                        NibpPresenterImpl.TAG_CUFF_NAME_TEXT_VIEW);
                tvCuffName.setText(R.string.nibp_cuff);
                tvCuff.setText("0");
                btnMeasure.setText(R.string.stop_measure);
                btnMeasure.setEnabled(true);
                btnMeasure.setBackgroundResource(R.drawable.btn_selector_orange);

                btnSetting.setEnabled(false);
                checking = true;
                break;
            //测量完成
            case OVER_LAYOUT:
                if (checkSuccessView == null) {
                    checkSuccessView = presenter.getSuccessLayout(getActivity(), containLayout);
                }
                if (currentView != checkSuccessView) {

                    changeView(checkSuccessView);
                }
                checking = false;
                btnMeasure.setText(R.string.start_measure);
                btnSetting.setEnabled(true);
                btnMeasure.setBackgroundResource(R.drawable.btn_selector_blue);
                break;
            //测量超时
            case MEASURE_FAILED_LAYOUT:

                break;
//            //校准和漏气检测
//            case TEST_LAYOUT:
//                if (checkView == null) {
//                    checkView = presenter.getMeasureLayout(getActivity(), containLayout);
//                }
//                if (currentView != checkView) {
//                    changeView(checkView);
//                }
//                tvCuff = (TextView) checkView.findViewWithTag(
//                        NibpPresenterImpl.TAG_CUFF_VALUE_TEXT_VIEW);
//                tvCuffName = (TextView) checkView.findViewWithTag(
//                        NibpPresenterImpl.TAG_CUFF_NAME_TEXT_VIEW);
//                tvCuffName.setText(R.string.nibp_cuff);
//                tvCuff.setVisibility(View.VISIBLE);
//                tvCuff.setText("0");
//                break;
            default:
                break;
        }
    }

    /**
     * 改变显示布局
     * @param view 布局
     */
    private void changeView(View view) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        if (currentView != null) {
            containLayout.removeView(currentView);
        }
        currentView = view;
        containLayout.addView(currentView, params);
    }

    @Override
    public void measureSuccess(int sysValue, int diaValue) {
        UiUitls.setMeasureResult(KParamType.NIBP_SYS, sysValue, tvSysName, tvSys, false);
        UiUitls.setMeasureResult(KParamType.NIBP_DIA, diaValue, tvDiaName, tvDia, false);
        //同步左列表数据
        EventBus.getDefault().post(new EventBusUseEvent(AppFragment.BF_FLAG));
    }

    @Override
    public void refreshGuide(TextView tv, int state) {
        switch (state) {
            case GUIDE_BEING: //正在引导
                tv.setTextColor(getResources().getColor(R.color.measure_name_text_color));
                Drawable dra = getResources().getDrawable(R.drawable.ic_doing);
                dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumHeight());
                tv.setCompoundDrawables(dra, null, null, null);
                break;
            case GUIDE_FINISH: //完成引导
                Drawable dra2 = getResources().getDrawable(R.drawable.ic_finished);
                dra2.setBounds(0, 0, dra2.getMinimumWidth(), dra2.getMinimumHeight());
                tv.setCompoundDrawables(dra2, null, null, null);
                break;
            case GUIDE_STEP2: //引导步骤
                Drawable dra3 = getResources().getDrawable(R.drawable.ic_step2);
                dra3.setBounds(0, 0, dra3.getMinimumWidth(), dra3.getMinimumHeight());
                tv.setCompoundDrawables(dra3, null, null, null);
                break;
            case GUIDE_STEP1: //引导步骤
                Drawable dra4 = getResources().getDrawable(R.drawable.ic_step1);
                dra4.setBounds(0, 0, dra4.getMinimumWidth(), dra4.getMinimumHeight());
                tv.setCompoundDrawables(dra4, null, null, null);
                break;
            default:
                break;
        }
    }

    @Override
    public void setNibpStatus(int value) {
        showMeasureResult(value);
    }

    @Override
    public void refreshCuff(int cuff) {
        if (tvCuff != null && checkView != null && currentView == checkView) {
            tvCuff.setText(String.valueOf(cuff));
        }

        if (settingDialog != null && settingDialog.isShowing()) {
            settingDialog.setCuffValue(cuff);
        }
    }

    @Override
    public void setDateBean(MeasureDataBean bean) {
        measureDataBean = bean;
        if (bean != null) {
            UiUitls.setMeasureResult(KParamType.NIBP_SYS, bean.getTrendValue(KParamType.NIBP_SYS)
                    , tvSysName, tvSys, false);
            UiUitls.setMeasureResult(KParamType.NIBP_DIA, bean.getTrendValue(KParamType.NIBP_DIA)
                    , tvDiaName, tvDia, false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.unbindService(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (currentView == checkView) {
            presenter.stopMeasure();
            if (settingDialog != null) {
                settingDialog.resetUI();
            }
        }
        if (currentView != null) {
            containLayout.removeView(currentView);
            currentView = null;
        }
        EventBus.getDefault().unregister(this);
    }

    /**
     * 测量状态提示
     * @param code 测量状态
     */
    private void showMeasureResult(int code) {
        if (code == 0) {
            refresh(OVER_LAYOUT);
            return;
        }
        String result = new String();
        switch (code) {
            case GlobalNumber.ONE_NUMBER:
                result = getString(R.string.nibbp_result_1);
                break;
            case GlobalNumber.TWO_NUMBER:
                result = getString(R.string.nibbp_result_2);
                break;
            case GlobalNumber.THREE_NUMBER:
                result = getString(R.string.nibbp_result_3);
                break;
            case GlobalNumber.FOUR_NUMBER:
                result = getString(R.string.nibbp_result_4);
                break;
            case GlobalNumber.FIVE_NUMBER:
                result = getString(R.string.nibbp_result_5);
                break;
            case GlobalNumber.SIX_NUMBER:
                result = getString(R.string.nibbp_result_6);
                break;
            case GlobalNumber.SEVEN_NUMBER:
            case GlobalNumber.EIGHT_NUMBER:
                result = getString(R.string.nibbp_result_7);
                break;
            case GlobalNumber.NINE_NUMBER:
                result = getString(R.string.nibbp_result_8);
                break;
            case GlobalNumber.TEN_NUMBER:
                result = getString(R.string.nibbp_result_9);
                break;
            case GlobalNumber.ELEVEN_NUMBER:
                result = getString(R.string.nibbp_result_10);
                break;
            case GlobalNumber.TWELVE_NUMBER:
                result = getString(R.string.nibbp_result_11);
                break;
            case GlobalNumber.THREETEEN_NUMBER:
                result = getString(R.string.nibbp_result_12);
                break;
            //停止测量，如果是漏气检测，则是成功
            case NibpPresenterImpl.STATUS_CHECKING_PASS:
                if (settingDialog != null) {
                    if (settingDialog.isShowing()) {
                        String str = getString(R.string.nibbp_result_13);
                        settingDialog.setCheckStatus(str);
                        settingDialog.resetUI();
                    }
                }
                break;
            default:
                return;
        }
        if (!result.isEmpty() && tvCuffName != null) {
            tvCuffName.setVisibility(View.VISIBLE);
            tvCuffName.setText(result);
            tvCuff.setText("");
            tvCuff.setVisibility(View.GONE);
        }
        btnMeasure.setText(R.string.start_measure);
        btnSetting.setEnabled(true);
        btnMeasure.setBackgroundResource(R.drawable.btn_selector_blue);
        checking = false;

        if (null != settingDialog) {
            if (!result.isEmpty()) {
                settingDialog.setCheckStatus(result);
            }
            settingDialog.resetUI();
        }
    }

    @Override
    public NibpPresenterImpl initPresenter() {
        return new NibpPresenterImpl(this);
    }

    /**
     * 血压设置按钮事件下发
     */
    NibpSettingDialog.OnButtonClickListener buttonClickListener = new NibpSettingDialog
            .OnButtonClickListener() {
        @Override
        public void onCalibrateClick(boolean check) {
            if (check) {
                presenter.startCalibrate();
            } else {
                presenter.stopCalibrate();
            }
        }

        @Override
        public void onLeakTestClick(boolean check) {
            if (check) {
                presenter.startLeakTest();
            } else {
                presenter.stopLeakTest();
            }
        }

        @Override
        public void onResetClick() {
            presenter.resetNibp();
        }
    };

    /**
     * 接受用户切换后立刻刷新当前数据信息
     * @param event 事件对象
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshData(EventBusUseEvent event) {
        if (event.getFlag().equals(GlobalConstant.MEASURE_USER_SWITCH)) {
            //用户切换的同时如果正在测量中即可停止测量并刷新页面
            if (currentView == checkView) {
                //停止测量
                presenter.stopMeasure();
                if (settingDialog != null) {
                    settingDialog.resetUI();
                }
            }
            if (currentView != null) {
                containLayout.removeView(currentView);
                currentView = null;
            }
            //刷新为指导页面
            refresh(GUIDE_LAYOUT);
            //临时存储值
            int sysTemp = ServiceUtils.getMeausreDataBean().getTrendValue(KParamType.NIBP_SYS);
            int diaTemp = ServiceUtils.getMeausreDataBean().getTrendValue(KParamType.NIBP_DIA);
            //同步左列表数据
            EventBus.getDefault().post(new EventBusUseEvent(AppFragment.BF_FLAG));
            UiUitls.setMeasureResult(KParamType.NIBP_SYS, sysTemp, tvSysName, tvSys, false);
            UiUitls.setMeasureResult(KParamType.NIBP_DIA, diaTemp, tvDiaName, tvDia, false);
        }
    }
}
