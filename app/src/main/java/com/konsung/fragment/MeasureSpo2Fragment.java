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
import com.konsung.msgevent.EventBusUseEvent;
import com.konsung.presenter.Spo2Presenter;
import com.konsung.presenter.impl.Spo2PresenterImpl;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.ReferenceUtils;
import com.konsung.util.ServiceUtils;
import com.konsung.util.StatisticalDialogController;
import com.konsung.util.UiUitls;
import com.konsung.util.global.GlobalNumber;
import com.konsung.view.holder.Spo2TutorialHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 血氧测量页面
 **/
public class MeasureSpo2Fragment extends com.konsung.view.BaseFragment<Spo2PresenterImpl>
        implements Spo2Presenter.View, View.OnClickListener {

    public static final int GUIDE_LAYOUT = 0; //引导界面
    public static final int FINGER_INSERT_LAYOUT = 1; //插入手指
    public static final int SPO2_LAYOUT = 2; //实时血氧界面
    public static final int MEASURE_LAYOUT = 3; //测量界面
    public static final int OVER_LAYOUT = 4; //测量结束界面
    public static final int MEASURE_FAILED_LAYOUT = 5; //测量超时页面

    public static final int GUIDE_BEING = 4; //提示引导
    public static final int GUIDE_FINISH = 5; //完成引导
    public static final int GUIDE_STEP = 6; //引导步骤

    //血氧连接成功
    public static final int STATUS_LEFF_ON = 0;
    //血氧探头脱落
    public static final int STATUS_LEFF_OFF = 1;
    //手指未插入
    public static final int STATUS_FINGER = 2;

    //血氧值布局
    @InjectView(R.id.relative_layout1)
    RelativeLayout spo2ValueLayout;
    //脉率值布局
    @InjectView(R.id.relative_layout2)
    RelativeLayout prValueLayout;
    //测量刷新布局
    @InjectView(R.id.layout_contain)
    RelativeLayout containLayout;
    //探头安装指示
    @InjectView(R.id.tv_measure_template1)
    TextView tvInstall;
    //手指插入指示
    @InjectView(R.id.tv_measure_template2)
    TextView tvInsert;
    @InjectView(R.id.btn_measure_template1)
    Button btnMeasure;
    @InjectView(R.id.btn_measure_template2)
    Button btnTrend;
    @InjectView(R.id.btn_measure_template3)
    Button btnReport;
    @InjectView(R.id.btn_ecg_setting)
    Button btnSetting;
    //血氧值
    private TextView tvSpo2;
    private TextView tvSpo2Name;

    //脉率值
    private TextView tvPr;
    private TextView tvPrName;

    //引导界面
    private Spo2TutorialHolder guideViewHolder = null;
    //测量界面
    private Spo2PresenterImpl.CheckViewHolder checkViewHolder = null;
    //测量成功界面
    private View checkSuccessView = null;

    //当前页面
    private View currentView = null;

    private View view;
    //测量状态
    private boolean checking = false;
    private StatisticalDialogController statisticalController;

    private MeasureDataBean measureDataBean = null;
    private long lastClickTimestamp = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_spo2_new, container, false);
        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    /**
     * 初始化布局
     */
    private void initView() {
        refresh(GlobalConstant.spoState);
        initSpo2();
        initPr();
        btnReport.setVisibility(View.GONE);
        btnSetting.setVisibility(View.GONE);
        tvInstall.setText(getString(R.string.spo2_pls_install_probe));
        tvInsert.setText(getString(R.string.spo2_pls_insert_finger));
        btnMeasure.setOnClickListener(this);
        btnTrend.setOnClickListener(this);
        setDateBean(measureDataBean);
    }

    /**
     * 初始化血氧
     */
    private void initSpo2() {
        tvSpo2Name = (TextView) spo2ValueLayout.findViewById(R.id.tv_measure_name);
        tvSpo2Name.setTextColor(UiUitls.getColor(R.color.measure_name_text_color));
        tvSpo2Name.setText(R.string.spo2);
        TextView tvSpo2Max = (TextView) spo2ValueLayout.findViewById(R.id.tv_measure_max);
        //参数值未定，还需要重做，暂时写死，
        tvSpo2Max.setText(String.valueOf((int) ReferenceUtils.
                getMaxReference(KParamType.SPO2_TREND)));
        TextView tvSpo2Min = (TextView) spo2ValueLayout.findViewById(R.id.tv_measure_min);
        tvSpo2Min.setText(String.valueOf((int) ReferenceUtils.
                getMinReference(KParamType.SPO2_TREND)));
        tvSpo2 = (TextView) spo2ValueLayout.findViewById(R.id.tv_measure_value);
        tvSpo2.setTextColor(UiUitls.getColor(R.color.measure_value_text_color));

        tvSpo2.setText(R.string.invalid_data);
        TextView tvSpo2Unit = (TextView) spo2ValueLayout.findViewById(R.id.tv_measure_unit);
        tvSpo2Unit.setText(R.string.per_cent);
    }

    /**
     * 初始化脉率
     */
    private void initPr() {
        tvPrName = (TextView) prValueLayout.findViewById(R.id.tv_measure_name);
        tvPrName.setTextColor(UiUitls.getColor(R.color.measure_name_text_color));

        tvPrName.setText(R.string.health_pr);
        TextView tvSpo2Max = (TextView) prValueLayout.findViewById(R.id.tv_measure_max);
        //参数值未定，还需要重做，暂时写死，
        tvSpo2Max.setText(String.valueOf((int) ReferenceUtils.
                getMaxReference(KParamType.SPO2_PR)));
        TextView tvSpo2Min = (TextView) prValueLayout.findViewById(R.id.tv_measure_min);
        tvSpo2Min.setText(String.valueOf((int) ReferenceUtils.
                getMinReference(KParamType.SPO2_PR)));
        tvPr = (TextView) prValueLayout.findViewById(R.id.tv_measure_value);
        tvPr.setTextColor(UiUitls.getColor(R.color.measure_value_text_color));

        tvPr.setText(R.string.invalid_data);
        TextView tvSpo2Unit = (TextView) prValueLayout.findViewById(R.id.tv_measure_unit);
        tvSpo2Unit.setText(R.string.health_unit_bpm);
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
                    btnMeasure.setBackgroundResource(R.drawable.btn_selector_blue);
                    checking = false;
                } else {
                    presenter.startMeasure();
                    btnMeasure.setBackgroundResource(R.drawable.btn_selector_orange);
                    initPr();
                    initSpo2();
                    checking = true;
                }
                break;
            //趋势统计
            case R.id.btn_measure_template2:
                StatisticalDialogController statisticalController = new
                        StatisticalDialogController(getActivity(), presenter
                        .getStatisticalTableItem(), presenter.getStatisticalPic(getActivity()),
                        true);
                statisticalController.showDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
        presenter.bindAidlService();
    }

    @Override
    public void refresh(int state) {
        switch (state) {
            //引导页面
            case GUIDE_LAYOUT:
                if (guideViewHolder == null) {
                    initGuide();
                }
                if (currentView != guideViewHolder.view) {

                    changeView(guideViewHolder.view);
                }
                guideViewHolder.imgTutorial.setImageDrawable(
                        UiUitls.getDrawable(R.drawable.pic_spo2_1));
                guideViewHolder.tvTutorual.setText(R.string.spo2_tutorial_1);

                refreshGuide(tvInstall, GUIDE_BEING);
                btnMeasure.setEnabled(false);
                btnMeasure.setBackgroundResource(R.drawable.btn_selector_blue);
                refreshGuide(tvInsert, GUIDE_STEP);
                break;
            case FINGER_INSERT_LAYOUT:
                if (guideViewHolder == null) {
                    initGuide();
                }
                if (currentView != guideViewHolder.view) {

                    changeView(guideViewHolder.view);
                }
                guideViewHolder.imgTutorial.setImageDrawable(
                        UiUitls.getDrawable(R.drawable.pic_spo2_2));
                guideViewHolder.tvTutorual.setText(R.string.spo2_tutorial_2);
                refreshGuide(tvInstall, GUIDE_FINISH);
                btnMeasure.setEnabled(false);
                btnMeasure.setBackgroundResource(R.drawable.btn_selector_blue);

                refreshGuide(tvInsert, GUIDE_BEING);
                break;
            //实时血氧
            case SPO2_LAYOUT:
                if (checkViewHolder == null) {
                    initCheck();
                }
                if (currentView != checkViewHolder.view) {
                    changeView(checkViewHolder.view);
                }
                checkViewHolder.spo2Wave.reset();
                refreshGuide(tvInstall, GUIDE_FINISH);
                btnMeasure.setEnabled(true);
                btnMeasure.setBackgroundResource(R.drawable.btn_selector_blue);
                checkViewHolder.progressBar.setProgress(0);
                checkViewHolder.tvStatus.setText(R.string.pls_start_measure);
                btnMeasure.setText(R.string.start_measure);
                refreshGuide(tvInsert, GUIDE_FINISH);
                break;
            //测量
            case MEASURE_LAYOUT:
                if (checkViewHolder == null) {
                    initCheck();
                }
                if (currentView != checkViewHolder.view) {
                    changeView(checkViewHolder.view);
                }
                checkViewHolder.spo2Wave.reset();
                refreshGuide(tvInstall, GUIDE_FINISH);
                checkViewHolder.tvStatus.setText(R.string.doing_measure);
                btnMeasure.setText(R.string.stop_measure);
                refreshGuide(tvInsert, GUIDE_FINISH);
                btnMeasure.setEnabled(true);
                btnMeasure.setBackgroundResource(R.drawable.btn_selector_orange);
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
                refreshGuide(tvInstall, GUIDE_FINISH);
                btnMeasure.setText(R.string.start_measure);
                btnMeasure.setBackgroundResource(R.drawable.btn_selector_blue);
                refreshGuide(tvInsert, GUIDE_FINISH);
                break;
            //测量超时
            case MEASURE_FAILED_LAYOUT:
                if (checkViewHolder == null) {
                    initCheck();
                }
                if (currentView != checkViewHolder.view) {
                    changeView(checkViewHolder.view);
                }
                refreshGuide(tvInstall, GUIDE_FINISH);
                refreshGuide(tvInsert, GUIDE_FINISH);
                btnMeasure.setText(R.string.start_measure);
                btnMeasure.setBackgroundResource(R.drawable.btn_selector_blue);

                checkViewHolder.tvStatus.setText(R.string.measure_timeout);
                checking = false;

                break;
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
    public void measureSuccess(int spo2Value, int hrValue) {
        UiUitls.setMeasureResult(KParamType.SPO2_TREND, spo2Value, tvSpo2Name, tvSpo2, false);
        UiUitls.setMeasureResult(KParamType.SPO2_PR, hrValue, tvPrName, tvPr, false);
        //同步左侧栏数据
        EventBus.getDefault().post(new EventBusUseEvent(AppFragment.SPO_FLAG));
    }

    @Override
    public void refreshGuide(TextView tv, int state) {
        switch (state) {
            case GUIDE_BEING: //正在引导
                tv.setTextColor(UiUitls.getColor(R.color.measure_name_text_color));
                Drawable dra = UiUitls.getDrawable(R.drawable.ic_doing);
                dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumHeight());
                tv.setCompoundDrawables(dra, null, null, null);
                break;
            case GUIDE_FINISH: //完成引导
                tv.setTextColor(UiUitls.getColor(R.color.gray));
                Drawable dra2 = UiUitls.getDrawable(R.drawable.ic_finished);
                dra2.setBounds(0, 0, dra2.getMinimumWidth(), dra2.getMinimumHeight());
                tv.setCompoundDrawables(dra2, null, null, null);
                break;
            case GUIDE_STEP: //引导步骤
                tv.setTextColor(UiUitls.getColor(R.color.gray));
                Drawable dra3 = UiUitls.getDrawable(R.drawable.ic_step2);
                dra3.setBounds(0, 0, dra3.getMinimumWidth(), dra3.getMinimumHeight());
                tv.setCompoundDrawables(dra3, null, null, null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.stopMeasure();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (checkViewHolder != null && currentView == checkViewHolder.view) {
            checkViewHolder.progressBar.setProgress(0);
            checkViewHolder.tvStatus.setText(R.string.pls_start_measure);
        }
        if (currentView != null) {
            containLayout.removeView(currentView);
            currentView = null;
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void spo2WaveData(byte[] bytes) {
        if (checkViewHolder != null && currentView == checkViewHolder.view) {
            checkViewHolder.spo2Wave.setData(bytes);
        }
    }

    @Override
    public void setSpo2Status(int value) {
        checking = false;
        switch (value) {
            case STATUS_LEFF_ON:
                refresh(SPO2_LAYOUT);
                GlobalConstant.spoState = SPO2_LAYOUT;
                break;
            case STATUS_LEFF_OFF:
                presenter.stopMeasure();
                refresh(GUIDE_LAYOUT);
                GlobalConstant.spoState = GUIDE_LAYOUT;
                break;
            case STATUS_FINGER:
                presenter.stopMeasure();
                refresh(FINGER_INSERT_LAYOUT);
                GlobalConstant.spoState = FINGER_INSERT_LAYOUT;
                break;
            default:
                break;
        }
    }

    @Override
    public void refreshProgress(int progress) {
        checkViewHolder.progressBar.setProgress(progress);
        checkViewHolder.tvStatus.setText(R.string.doing_measure);
    }

    @Override
    public void setDateBean(MeasureDataBean bean) {
        measureDataBean = bean;
        if (bean != null) {
            int spo2 = bean.getTrendValue(KParamType.SPO2_TREND);
            int spo2Pr = bean.getTrendValue(KParamType.SPO2_PR);
            UiUitls.setMeasureResult(KParamType.SPO2_TREND, spo2, tvSpo2Name, tvSpo2, false);
            UiUitls.setMeasureResult(KParamType.SPO2_PR, spo2Pr, tvPrName, tvPr, false);
        }
    }

    @Override
    public Spo2PresenterImpl initPresenter() {
        return new Spo2PresenterImpl(this);
    }

    /**
     * 初始化引导
     */
    private void initGuide() {
        guideViewHolder = presenter.getInstallGuideView(getActivity(), containLayout);
    }

    /**
     * 初始化测量界面
     */
    private void initCheck() {
        checkViewHolder = presenter.getMeasureLayout(getActivity(), containLayout);
        checkViewHolder.spo2Wave.setSampleRate(GlobalNumber.HUNDRED_AND_TWENTY_FIVE_NUMBER);
    }

    /**
     * 接受用户切换后立刻刷新当前数据信息
     * @param event 事件对象
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshData(EventBusUseEvent event) {
        if (event.getFlag().equals(GlobalConstant.MEASURE_USER_SWITCH)) {
            //刷新页面状态
            refresh(GlobalConstant.spoState);
            //临时存储值
            int spoTemp = ServiceUtils.getMeausreDataBean().getTrendValue((KParamType.SPO2_TREND));
            int prTemp = ServiceUtils.getMeausreDataBean().getTrendValue((KParamType.SPO2_PR));
            //同步左列表数据
            EventBus.getDefault().post(new EventBusUseEvent(AppFragment.SPO_FLAG));
            UiUitls.setMeasureResult(KParamType.SPO2_TREND, spoTemp, tvSpo2Name, tvSpo2, false);
            UiUitls.setMeasureResult(KParamType.SPO2_PR, prTemp, tvPrName, tvPr, false);
        }
    }
}
