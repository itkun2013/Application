package com.konsung.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.msgevent.EventBusUseEvent;
import com.konsung.presenter.BeneTrinityPresenter;
import com.konsung.presenter.impl.BeneTrinityPresenterImpl;
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
 * 血液三项测量页面
 * Created by DJH on 2017/10/26 0026.
 */
public class MeasureBloodThreeFragment extends com.konsung.view
        .BaseFragment<BeneTrinityPresenterImpl> implements BeneTrinityPresenter.View
        , View.OnClickListener {
    //餐前
    private static final int BEFORE_GLU = 0;
    //餐后
    private static final int AFTER_GLU = 1;
    //空腹血糖
    @InjectView(R.id.relative_before_glu)
    RelativeLayout rlBeforeGlu;
    //餐后血糖
    @InjectView(R.id.relative_after_glu)
    RelativeLayout rlAfterGlu;
    //尿酸
    @InjectView(R.id.relative_ua)
    RelativeLayout rlUa;
    //总胆固醇
    @InjectView(R.id.relative_total_cho)
    RelativeLayout rlTotalCho;
    //测量刷新布局
    @InjectView(R.id.layout_contain)
    RelativeLayout containLayout;
    @InjectView(R.id.tv_measure_template1)
    TextView tvLink; //连接设备
    @InjectView(R.id.tv_measure_template2)
    TextView tvSetout; // 准备试纸
    @InjectView(R.id.tv_measure_template3)
    TextView tvDetection; // 采血检测
    @InjectView(R.id.btn_measure_template1)
    Button btnMeasure;
    @InjectView(R.id.btn_measure_template3)
    Button btnReport;
    @InjectView(R.id.btn_ecg_setting)
    Button btnSetting;
    @InjectView(R.id.btn_measure_template2)
    Button btnTrend; // 趋势统计

    //空腹血糖
    private TextView tvBeforeGlu;
    private TextView tvBeforeGluName;
    private ImageView ivBeforeDining;
    TextView tvBeforeGluMax;
    TextView tvBeforeGluMin;
    TextView tvBeforeUnit;
    //餐后血糖
    private TextView tvAfterGlu;
    private TextView tvAfterGluName;
    private ImageView ivAfterDining;
    TextView tvAfterGluMax;
    TextView tvAfterGluMin;
    TextView tvAfterUnit;
    //尿酸
    private TextView tvUa;
    private TextView tvUaName;
    //总胆固醇
    private TextView tvTotalCho;
    private TextView tvTotalChoName;

    private View view;
    //保存当前血糖类型 默认餐前
    public static int currentGlu = 0;
    private MeasureDataBean measureData;
    private long lastClickTimestamp = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_bene_trinity, container, false);
        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);
        return view;
    }

    /**
     * 初始化布局
     */
    private void initView() {
        initViewData();
        btnTrend.setOnClickListener(this);
    }

    /**
     * 初始化测量数据
     */
    private void initMeasureData() {
        if (measureData == null) {
            measureData = new MeasureDataBean();
            return;
        }
        tvBeforeGlu.setText(UiUitls.getValueAfterFactor(KParamType.BLOODGLU_BEFORE_MEAL,
                measureData.getTrendValue(KParamType.BLOODGLU_BEFORE_MEAL)));
        tvAfterGlu.setText(UiUitls.getValueAfterFactor(KParamType.BLOODGLU_AFTER_MEAL,
                measureData.getTrendValue(KParamType.BLOODGLU_AFTER_MEAL)));
        UiUitls.setMeasureResult(KParamType.URICACID_TREND, measureData,
                tvUaName, tvUa);
        UiUitls.setMeasureResult(KParamType.CHOLESTEROL_TREND, measureData,
                tvTotalChoName, tvTotalCho);
        //餐前餐后模式值 0 餐前 1 餐后
        int measureModel = Integer.valueOf(measureData.getGluStyle());
        if (measureModel == 0) {
            replaceGluStyle(rlBeforeGlu);
            UiUitls.setMeasureResult(KParamType.BLOODGLU_BEFORE_MEAL, measureData,
                    tvBeforeGluName, tvBeforeGlu);
        } else {
            replaceGluStyle(rlAfterGlu);
            UiUitls.setMeasureResult(KParamType.BLOODGLU_AFTER_MEAL, measureData,
                    tvAfterGluName, tvAfterGlu);
        }
    }

    /**
     * 初始化导入的控件
     */
    private void initViewData() {
        initMeasureValueView();
        initGuideView();
        initMeasureData();
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
            //趋势统计.百捷三合一只显示趋势表
            case R.id.btn_measure_template2:
                StatisticalDialogController statisticalController = new
                        StatisticalDialogController(getActivity(),
                        presenter.getStatisticalTableItem());
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
    public void measureSuccess(float gluValue, float uaValue, float choValue) {
        if (gluValue != GlobalConstant.INVALID_DATA) {
            if (currentGlu == AFTER_GLU) {
                UiUitls.setMeasureResult(KParamType.BLOODGLU_AFTER_MEAL, gluValue
                        , tvAfterGluName, tvAfterGlu);
                UiUitls.setMeasureResult(KParamType.BLOODGLU_BEFORE_MEAL
                        , GlobalConstant.INVALID_DATA, tvBeforeGluName, tvBeforeGlu);
                measureData.setTrendValue(KParamType.BLOODGLU_AFTER_MEAL, (int) gluValue);
                measureData.setTrendValue(KParamType.BLOODGLU_BEFORE_MEAL
                        , GlobalConstant.INVALID_DATA);
            } else {
                UiUitls.setMeasureResult(KParamType.BLOODGLU_BEFORE_MEAL, gluValue
                        , tvBeforeGluName, tvBeforeGlu);
                UiUitls.setMeasureResult(KParamType.BLOODGLU_AFTER_MEAL
                        , GlobalConstant.INVALID_DATA, tvAfterGluName, tvAfterGlu);
                measureData.setTrendValue(KParamType.BLOODGLU_BEFORE_MEAL, (int) gluValue);
                measureData.setTrendValue(KParamType.BLOODGLU_AFTER_MEAL
                        , GlobalConstant.INVALID_DATA);
            }
        }
        if (uaValue != GlobalConstant.INVALID_DATA) {
            UiUitls.setMeasureResult(KParamType.URICACID_TREND, uaValue
                    , tvUaName, tvUa);
        }
        if (choValue != GlobalConstant.INVALID_DATA) {
            UiUitls.setMeasureResult(KParamType.CHOLESTEROL_TREND, choValue
                    , tvTotalChoName, tvTotalCho);
        }
        //同步左侧栏数据
        EventBus.getDefault().post(new EventBusUseEvent(AppFragment.BT_FLAG));
    }

    @Override
        public void setMeasureData(MeasureDataBean measureDataBean) {
        measureData.setGluStyle(measureDataBean.getGluStyle());
        measureData.setTrendValue(KParamType.BLOODGLU_BEFORE_MEAL
                , measureDataBean.getTrendValue(KParamType.BLOODGLU_BEFORE_MEAL));
        int measureModel = Integer.valueOf(measureData.getGluStyle());
        measureData.setTrendValue(KParamType.URICACID_TREND
                , measureDataBean.getTrendValue(KParamType.URICACID_TREND));
        measureData.setTrendValue(KParamType.CHOLESTEROL_TREND
                , measureDataBean.getTrendValue(KParamType.CHOLESTEROL_TREND));
        if (measureModel == 1) {
            measureData.setTrendValue(KParamType.BLOODGLU_AFTER_MEAL
                    , measureData.getTrendValue(KParamType.BLOODGLU_BEFORE_MEAL));
            measureData.setTrendValue(KParamType.BLOODGLU_BEFORE_MEAL
                    , GlobalConstant.INVALID_DATA);
        }
        initMeasureData();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public BeneTrinityPresenterImpl initPresenter() {
        return new BeneTrinityPresenterImpl(this);
    }

    /**
     * 初始化引导界面
     */
    private void initGuideView() {
        containLayout.addView(presenter.getInstallGuideView(getActivity(), containLayout));
        btnMeasure.setVisibility(View.GONE);
        btnReport.setVisibility(View.GONE);
        btnSetting.setVisibility(View.GONE);
        tvLink.setText(UiUitls.getString(R.string.connect_the_detector));
        tvSetout.setText(UiUitls.getString(R.string.blood_test));
        tvDetection.setText(UiUitls.getString(R.string.link_device));
        Drawable drawable1 = UiUitls.getDrawable(R.drawable.ic_step1);
        Drawable drawable2 = UiUitls.getDrawable(R.drawable.ic_step2);
        Drawable drawable3 = UiUitls.getDrawable(R.drawable.ic_step3);
        drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
        drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
        drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
        tvLink.setCompoundDrawables(drawable1, null, null, null);
        tvSetout.setCompoundDrawables(drawable2, null, null, null);
        tvDetection.setCompoundDrawables(drawable3, null, null, null);
//        tvLink.setTextSize(UiUitls.getDimens(R.dimen.px20));
//        tvSetout.setTextSize(UiUitls.getDimens(R.dimen.px20));
//        tvDetection.setTextSize(UiUitls.getDimens(R.dimen.px20));
    }

    /**
     * 初始化测量值的所有控件
     */
    private void initMeasureValueView() {
        tvBeforeGluName = (TextView) rlBeforeGlu.findViewById(R.id.tv_measure_name);
        tvBeforeGluMax = (TextView) rlBeforeGlu.findViewById(R.id.tv_measure_max);
        tvBeforeGluMin = (TextView) rlBeforeGlu.findViewById(R.id.tv_measure_min);
        tvBeforeGluMax.setText(String.valueOf(ReferenceUtils.
                getMaxReference(KParamType.BLOODGLU_BEFORE_MEAL)));
        tvBeforeGluMin.setText(String.valueOf(ReferenceUtils.
                getMinReference(KParamType.BLOODGLU_BEFORE_MEAL)));
        tvBeforeUnit = (TextView) rlBeforeGlu.findViewById(R.id.tv_measure_unit);
        tvBeforeGlu = (TextView) rlBeforeGlu.findViewById(R.id.tv_measure_value);
        ivBeforeDining = (ImageView) rlBeforeGlu.findViewById(R.id.iv_glu_flag);
        tvBeforeGluName.setText(R.string.before_dinner);
        tvBeforeUnit.setText(R.string.p_unit_mmol);
        tvBeforeGlu.setText(R.string.invalid_data);
        tvBeforeGluName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceGluStyle(rlBeforeGlu);
            }
        });
        rlBeforeGlu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceGluStyle(rlBeforeGlu);
            }
        });
        ivBeforeDining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceGluStyle(rlBeforeGlu);
            }
        });

        tvAfterGluName = (TextView) rlAfterGlu.findViewById(R.id.tv_measure_name);
        tvAfterGluMax = (TextView) rlAfterGlu.findViewById(R.id.tv_measure_max);
        tvAfterGluMin = (TextView) rlAfterGlu.findViewById(R.id.tv_measure_min);
        tvAfterGluMax.setText(String.valueOf(ReferenceUtils.
                getMaxReference(KParamType.BLOODGLU_AFTER_MEAL)));
        tvAfterGluMin.setText(String.valueOf(ReferenceUtils.
                getMinReference(KParamType.BLOODGLU_AFTER_MEAL)));
        tvAfterUnit = (TextView) rlAfterGlu.findViewById(R.id.tv_measure_unit);
        tvAfterGlu = (TextView) rlAfterGlu.findViewById(R.id.tv_measure_value);
        ivAfterDining = (ImageView) rlAfterGlu.findViewById(R.id.iv_glu_flag);
        tvAfterGluName.setText(R.string.after_dinner);
        tvAfterUnit.setText(R.string.p_unit_mmol);

        tvAfterGlu.setText(R.string.invalid_data);
        tvAfterGluName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceGluStyle(rlAfterGlu);
            }
        });
        rlAfterGlu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceGluStyle(rlAfterGlu);
            }
        });
        ivAfterDining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceGluStyle(rlAfterGlu);
            }
        });

        tvUaName = (TextView) rlUa.findViewById(R.id.tv_measure_name);
        TextView tvUaMax = (TextView) rlUa.findViewById(R.id.tv_measure_max);
        TextView tvUaMin = (TextView) rlUa.findViewById(R.id.tv_measure_min);
        String uaMax = String.format(getString(R.string.rule_limit_2_after_point),
                ReferenceUtils.getMaxReference(KParamType.URICACID_TREND));
        tvUaMax.setText(uaMax);
        String uaMin = String.format(getString(R.string.rule_limit_2_after_point),
                ReferenceUtils.getMinReference(KParamType.URICACID_TREND));
        tvUaMin.setText(uaMin);
        TextView tvUaUnit = (TextView) rlUa.findViewById(R.id.tv_measure_unit);
        tvUa = (TextView) rlUa.findViewById(R.id.tv_measure_value);
        tvUaName.setTextColor(UiUitls.getColor(R.color.measure_value_text_color));
        tvUaName.setText(R.string.qc_ns);
        tvUaUnit.setText(R.string.p_unit_mmol);
        tvUa.setTextColor(UiUitls.getColor(R.color.measure_value_text_color));
        tvUa.setText(R.string.invalid_data);

        tvTotalChoName = (TextView) rlTotalCho.findViewById(R.id.tv_measure_name);
        TextView tvTotalChoMax = (TextView) rlTotalCho.findViewById(R.id.tv_measure_max);
        TextView tvTotalChoMin = (TextView) rlTotalCho.findViewById(R.id.tv_measure_min);

        String choMax = String.format(getString(R.string.rule_limit_2_after_point),
                ReferenceUtils.getMaxReference(KParamType.CHOLESTEROL_TREND));
        String choMin = String.format(getString(R.string.rule_limit_2_after_point),
                ReferenceUtils.getMinReference(KParamType.CHOLESTEROL_TREND));
        tvTotalChoMax.setText(choMax);
        tvTotalChoMin.setText(choMin);

        TextView tvTotalChoUnit = (TextView) rlTotalCho.findViewById(R.id.tv_measure_unit);
        tvTotalCho = (TextView) rlTotalCho.findViewById(R.id.tv_measure_value);
        tvTotalChoName.setTextColor(UiUitls.getColor(R.color.measure_value_text_color));
        tvTotalChoName.setText(R.string.qc_dgc);
        tvTotalChoUnit.setText(R.string.p_unit_mmol);
        tvTotalCho.setTextColor(UiUitls.getColor(R.color.measure_value_text_color));
        tvTotalCho.setText(R.string.invalid_data);
    }

    /**
     * 切换血糖选中的整体布局颜色
     * @param gluLayout 选中的布局
     */
    private void replaceGluStyle(View gluLayout) {
        if (rlAfterGlu == gluLayout) {
            currentGlu = AFTER_GLU;
            tvAfterGluName.setTextColor(UiUitls.getColor(R.color.measure_name_text_color));
            tvAfterGlu.setTextColor(UiUitls.getColor(R.color.measure_name_text_color));
            tvAfterGluMax.setTextColor(UiUitls.getColor(R.color.measureMax));
            tvAfterGluMin.setTextColor(UiUitls.getColor(R.color.measureMax));
            tvAfterUnit.setTextColor(UiUitls.getColor(R.color.measureMax));
            ivAfterDining.setImageDrawable(UiUitls.getDrawable(R.drawable.radio_sel));

            tvBeforeGluName.setTextColor(UiUitls.getColor(R.color.line));
            tvBeforeGlu.setTextColor(UiUitls.getColor(R.color.line));
            tvBeforeGluMax.setTextColor(UiUitls.getColor(R.color.line));
            tvBeforeGluMin.setTextColor(UiUitls.getColor(R.color.line));
            tvBeforeUnit.setTextColor(UiUitls.getColor(R.color.line));
            ivBeforeDining.setImageDrawable(UiUitls.getDrawable(R.drawable.radio_nor));
            UiUitls.setMeasureResult(KParamType.BLOODGLU_AFTER_MEAL, measureData
                    , tvAfterGluName, tvAfterGlu);
            tvBeforeGlu.setText(R.string.invalid_data);
        } else {
            currentGlu = BEFORE_GLU;
            tvBeforeGluName.setTextColor(UiUitls.getColor(R.color.measure_name_text_color));
            tvBeforeGlu.setTextColor(UiUitls.getColor(R.color.measure_name_text_color));
            tvBeforeGluMax.setTextColor(UiUitls.getColor(R.color.measureMax));
            tvBeforeGluMin.setTextColor(UiUitls.getColor(R.color.measureMax));
            tvBeforeUnit.setTextColor(UiUitls.getColor(R.color.measureMax));
            ivBeforeDining.setImageDrawable(UiUitls.getDrawable(R.drawable.radio_sel));

            tvAfterGluName.setTextColor(UiUitls.getColor(R.color.line));
            tvAfterGlu.setTextColor(UiUitls.getColor(R.color.line));
            tvAfterGluMax.setTextColor(UiUitls.getColor(R.color.line));
            tvAfterGluMin.setTextColor(UiUitls.getColor(R.color.line));
            tvAfterUnit.setTextColor(UiUitls.getColor(R.color.line));
            ivAfterDining.setImageDrawable(UiUitls.getDrawable(R.drawable.radio_nor));
            UiUitls.setMeasureResult(KParamType.BLOODGLU_BEFORE_MEAL, measureData
                    , tvBeforeGluName, tvBeforeGlu);
            tvAfterGlu.setText(R.string.invalid_data);
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
        EventBus.getDefault().unregister(this);
    }

    /**
     * 接受用户切换后立刻刷新当前数据信息
     * @param event 事件对象
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshData(EventBusUseEvent event) {
        if (event.getFlag().equals(GlobalConstant.MEASURE_USER_SWITCH)) {
            //同步左列表数据
            EventBus.getDefault().post(new EventBusUseEvent(AppFragment.BT_FLAG));
            //餐前餐后模式值 0 餐前 1 餐后
            MeasureDataBean measureDataBeanTemp = ServiceUtils.getMeausreDataBean();
            int measureModel = Integer.valueOf(measureDataBeanTemp.getGluStyle());
            measureData.setGluStyle(measureDataBeanTemp.getGluStyle());
            if (measureModel == 0) {
                measureData.setTrendValue(KParamType.BLOODGLU_BEFORE_MEAL
                        , measureDataBeanTemp.getTrendValue(KParamType.BLOODGLU_BEFORE_MEAL));
                measureData.setTrendValue(KParamType.BLOODGLU_AFTER_MEAL
                        , GlobalConstant.INVALID_DATA);
            } else {
                measureData.setTrendValue(KParamType.BLOODGLU_AFTER_MEAL
                        , measureDataBeanTemp.getTrendValue(KParamType.BLOODGLU_BEFORE_MEAL));
                measureData.setTrendValue(KParamType.BLOODGLU_BEFORE_MEAL
                        , GlobalConstant.INVALID_DATA);
            }
            initMeasureData();
        }
    }
}