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
import com.konsung.presenter.TempPresenter;
import com.konsung.presenter.impl.TempPresenterImpl;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.ReferenceUtils;
import com.konsung.util.ServiceUtils;
import com.konsung.util.StatisticalDialogController;
import com.konsung.util.UiUitls;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 体温单测页面
 * Created by DJH on 2017/10/25 0025.
 */
public class MeasureTempFragment extends com.konsung.view.BaseFragment<TempPresenterImpl>
        implements TempPresenter.View, View.OnClickListener {
    public static final int GUIDE_LAYOUT = 0; //引导界面
    public static final int PROBE_INSERT_LAYOUT = 1; //安装探头
    public static final int OVER_LAYOUT = 4; //测量结束界面
    public static final int GUIDE_BEING = 4; //提示引导
    public static final int GUIDE_FINISH = 5; //完成引导
    public static final int GUIDE_STEP1 = 6; //引导步骤
    public static final int GUIDE_STEP2 = 7; //引导步骤

    //体温测量值布局
    @InjectView(R.id.relative_layout1)
    RelativeLayout tempValueLayout;
    //测量值布局2，这里没用到的，隐藏
    @InjectView(R.id.relative_layout2)
    RelativeLayout valueLayout2;
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
    @InjectView(R.id.btn_measure_template3)
    Button btnReport;
    @InjectView(R.id.btn_ecg_setting)
    Button btnSetting;
    @InjectView(R.id.btn_measure_template2)
    Button btnTrend;

    //血氧值
    private TextView tvTemp;
    private TextView tvTempName;

    //引导界面
    private View guideView = null;
    //测量成功界面
    private View checkSuccessView = null;

    private View currentView = null;
    private MeasureDataBean measureDataBean = null;

    private View view;
    private long lastClickTimestamp = 0;
    //text大小
    private final int textSie = 30;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_spo2_new, container, false);
        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);
        initView();
        return view;
    }

    /**
     * 初始化布局
     */
    private void initView() {
        refresh(GUIDE_LAYOUT);
        valueLayout2.setVisibility(View.GONE);
        refreshGuide(tvInstall, GUIDE_STEP1);
        refreshGuide(tvInsert, GUIDE_STEP2);
        tvInstall.setText(getString(R.string.temp_pls_install_probe));
        tvInsert.setText(getString(R.string.temp_pls_move_to_head));
        btnMeasure.setVisibility(View.GONE);
        btnReport.setVisibility(View.GONE);
        btnSetting.setVisibility(View.GONE);
        btnTrend.setOnClickListener(this);
    }

    /**
     * 初始化血氧
     */
    private void initTemp() {
        tvTempName = (TextView) tempValueLayout.findViewById(R.id.tv_measure_name);
        tvTempName.setTextColor(UiUitls.getColor(R.color.measure_name_text_color));
        tvTempName.setText(R.string.health_temp);
        TextView tvTempMax = (TextView) tempValueLayout.findViewById(R.id.tv_measure_max);
        //参数值未定，还需要重做，暂时写死，
        tvTempMax.setText(String.valueOf(ReferenceUtils.getMaxReference(KParamType.IRTEMP_TREND)));
        TextView tvTempMin = (TextView) tempValueLayout.findViewById(R.id.tv_measure_min);
        tvTempMin.setText(String.valueOf(ReferenceUtils.getMinReference(KParamType.IRTEMP_TREND)));
        tvTemp = (TextView) tempValueLayout.findViewById(R.id.tv_measure_value);
        tvTemp.setTextColor(UiUitls.getColor(R.color.measure_value_text_color));

        tvTemp.setText(R.string.invalid_data);
        TextView tvTempUnit = (TextView) tempValueLayout.findViewById(R.id.tv_measure_unit);
        tvTempUnit.setText(R.string.health_unit_degree);
        setDataBean(measureDataBean);
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
            //趋势统计
            case R.id.btn_measure_template2:
                StatisticalDialogController statisticalController = new
                        StatisticalDialogController(getActivity(), presenter
                        .getStatisticalTableItem(), presenter.getStatisticalPic(getActivity()),
                        false);
                statisticalController.showDialog();
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initTemp();
        presenter.bindAidlService();
    }

    @Override
    public void refresh(int state) {
        switch (state) {
            //引导页面
            case GUIDE_LAYOUT:
                if (guideView == null) {
                    guideView = presenter.getInstallGuideView(getActivity(), containLayout);
                }
                if (currentView != guideView) {
                    changeView(guideView);
                }

                break;
            case PROBE_INSERT_LAYOUT:
                if (guideView == null) {
                    guideView = presenter.getInstallGuideView(getActivity(), containLayout);
                }
                if (currentView != guideView) {
                    changeView(guideView);
                }
                break;
            //测量完成
            case OVER_LAYOUT:
                if (checkSuccessView == null) {
                    checkSuccessView = presenter.getSuccessLayout(getActivity(), containLayout);
                }
                if (currentView != checkSuccessView) {

                    changeView(checkSuccessView);
                }
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
    public void measureSuccess(int temperature) {
        if (temperature != GlobalConstant.INVALID_DATA) {
            UiUitls.setMeasureResult(KParamType.IRTEMP_TREND, temperature, tvTempName, tvTemp,
                    true);
        }
        //同步左侧栏数据
        EventBus.getDefault().post(new EventBusUseEvent(AppFragment.TEMP_FLAG));
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
    public void setDataBean(MeasureDataBean bean) {
        measureDataBean = bean;
        if (bean != null) {
            int irTemp = bean.getTrendValue(KParamType.IRTEMP_TREND);
            UiUitls.setMeasureResult(KParamType.IRTEMP_TREND, irTemp, tvTempName, tvTemp, true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.unbindService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        guideView = null;
        currentView = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public TempPresenterImpl initPresenter() {
        return new TempPresenterImpl(this);
    }

    /**
     * 接受用户切换后立刻刷新当前数据信息
     * @param event 事件对象
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshData(EventBusUseEvent event) {
        if (event.getFlag().equals(GlobalConstant.MEASURE_USER_SWITCH)) {
            //临时存储值
            float temp = ServiceUtils.getMeausreDataBean().getTrendValue(KParamType.IRTEMP_TREND);
            //同步左列表数据
            EventBus.getDefault().post(new EventBusUseEvent(AppFragment.TEMP_FLAG));
            UiUitls.setMeasureResult(KParamType.IRTEMP_TREND, temp, tvTempName, tvTemp);
        }
    }
}
