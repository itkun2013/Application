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
import com.konsung.presenter.HemoglobinPresenter;
import com.konsung.presenter.impl.HemoglobinPresenterImpl;
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
 * 血红蛋白单测页面
 * Created by DJH on 2017/10/26 0026.
 */
public class MeasureHgbFragment extends com.konsung.view.BaseFragment<HemoglobinPresenterImpl>
        implements HemoglobinPresenter.View, View.OnClickListener {

    //血红蛋白
    @InjectView(R.id.relative_hgb)
    RelativeLayout rlHgb;
    //红细胞压积值
    @InjectView(R.id.relative_hct)
    RelativeLayout rlHct;
    //测量刷新布局
    @InjectView(R.id.layout_contain)
    RelativeLayout containLayout;
    @InjectView(R.id.tv_measure_template1)
    TextView tvLink; //步骤1
    @InjectView(R.id.tv_measure_template2)
    TextView tvSetout; // 步骤2
    @InjectView(R.id.tv_measure_template3)
    TextView tvDetection; // 步骤3
    @InjectView(R.id.btn_measure_template1)
    Button btnMeasure;
    @InjectView(R.id.btn_measure_template3)
    Button btnReport;
    @InjectView(R.id.btn_ecg_setting)
    Button btnSetting;
    @InjectView(R.id.btn_measure_template2)
    Button btnTrend; // 趋势统计

    //血红蛋白
    private TextView tvHgb;
    private TextView tvHgbName;
    TextView tvHgbMax;
    TextView tvHgbMin;
    TextView tvHgbUnit;
    //红细胞压积值
    private TextView tvHct;
    private TextView tvHctName;
    TextView tvHctMax;
    TextView tvHctMin;
    TextView tvHctUnit;
    private View view;
    private MeasureDataBean measureDataBean;
    private long lastClickTimestamp = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_hemoglobin, container, false);
        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);
        initView();
        return view;
    }

    /**
     * 初始化布局
     */
    private void initView() {
        btnTrend.setOnClickListener(this);
    }

    /**
     * 初始化导入的控件
     */
    private void initViewData() {
        initMeasureValueView();
        initGuideView();
        initMeasureData();
    }

    /**
     * 初始化测量数据
     */
    private void initMeasureData() {
        if (measureDataBean != null) {
            UiUitls.setMeasureResult(KParamType.BLOOD_HGB, measureDataBean, tvHgbName, tvHgb);
            UiUitls.setMeasureResult(KParamType.BLOOD_HCT, measureDataBean, tvHctName, tvHct);
        }
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
        initViewData();
        presenter.bindAidlService();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.unbindService();
    }

    @Override
    public void measureSuccess(float hgbValue, float hctValue) {
        if (hgbValue != GlobalConstant.INVALID_DATA) {
            UiUitls.setMeasureResult(KParamType.BLOOD_HGB, hgbValue, tvHgbName, tvHgb);
        }
        if (hctValue != GlobalConstant.INVALID_DATA) {
            UiUitls.setMeasureResult(KParamType.BLOOD_HCT, (int) hctValue, tvHctName, tvHct, false);
        }
        EventBus.getDefault().post(new EventBusUseEvent(AppFragment.HGB_FLAG));
    }

    @Override
    public void setMeasureData(MeasureDataBean measureDataBean) {
        this.measureDataBean = measureDataBean;
        initMeasureData();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public HemoglobinPresenterImpl initPresenter() {

        return new HemoglobinPresenterImpl(this);
    }

    /**
     * 初始化引导界面
     */
    private void initGuideView() {
        containLayout.addView(presenter.getInstallGuideView(getActivity(), containLayout));
        btnMeasure.setVisibility(View.GONE);
        btnReport.setVisibility(View.GONE);
        btnSetting.setVisibility(View.GONE);
        tvLink.setText(UiUitls.getString(R.string.link_device));
        tvSetout.setText(UiUitls.getString(R.string.insert_the_strip));
        tvDetection.setText(UiUitls.getString(R.string.begin_measure));
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
        tvHgbName = (TextView) rlHgb.findViewById(R.id.tv_measure_name);
        tvHgbMax = (TextView) rlHgb.findViewById(R.id.tv_measure_max);
        tvHgbMin = (TextView) rlHgb.findViewById(R.id.tv_measure_min);
        tvHgbUnit = (TextView) rlHgb.findViewById(R.id.tv_measure_unit);
        tvHgb = (TextView) rlHgb.findViewById(R.id.tv_measure_value);
        tvHgbName.setText(R.string.hemoglobin);
        tvHgbUnit.setText(R.string.health_unit_mol);

        tvHctName = (TextView) rlHct.findViewById(R.id.tv_measure_name);
        tvHctMax = (TextView) rlHct.findViewById(R.id.tv_measure_max);
        tvHctMin = (TextView) rlHct.findViewById(R.id.tv_measure_min);
        tvHctUnit = (TextView) rlHct.findViewById(R.id.tv_measure_unit);
        tvHct = (TextView) rlHct.findViewById(R.id.tv_measure_value);
        tvHctName.setText(R.string.red_blood);
        tvHctUnit.setText(R.string.x_measure_unit_percent);

        setCompareValue();
    }

    /**
     * 设置测量项参数范围最大最小值
     */
    private void setCompareValue() {
        ReferenceUtils.initPersonalReference();
        tvHgbMax.setText(String.valueOf(ReferenceUtils.
                getMaxReference(KParamType.BLOOD_HGB)));
        tvHgbMin.setText(String.valueOf(ReferenceUtils.
                getMinReference(KParamType.BLOOD_HGB)));

        tvHctMax.setText(String.valueOf((int) ReferenceUtils.
                getMaxReference(KParamType.BLOOD_HCT)));
        tvHctMin.setText(String.valueOf((int) ReferenceUtils.
                getMinReference(KParamType.BLOOD_HCT)));
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
            EventBus.getDefault().post(new EventBusUseEvent(AppFragment.HGB_FLAG));
            //切换即刷新数据
            setMeasureData(ServiceUtils.getMeausreDataBean());
            setCompareValue();
        }
    }
}
