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
import com.konsung.presenter.BloodFatPresenter;
import com.konsung.presenter.impl.BloodFatPresenterImpl;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.ReferenceUtils;
import com.konsung.util.ServiceUtils;
import com.konsung.util.StatisticalDialogController;
import com.konsung.util.UiUitls;
import com.konsung.util.VideoUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 血脂四项测量页面
 **/
public class MeasureBloodFatFragment extends com.konsung.view.BaseFragment<BloodFatPresenterImpl>
        implements BloodFatPresenter.View, View.OnClickListener {

    //总胆固醇
    @InjectView(R.id.relative_cho)
    RelativeLayout rlCho;
    //甘油三酯
    @InjectView(R.id.relative_trig)
    RelativeLayout rlTrig;
    //低密度脂蛋白
    @InjectView(R.id.relative_ldl)
    RelativeLayout rlLdl;
    //高密度脂蛋白
    @InjectView(R.id.relative_hdl)
    RelativeLayout rlHdl;
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
    @InjectView(R.id.btn_measure_template2)
    Button btnTrend; // 趋势统计
    //总胆固醇
    private TextView tvCho;
    private TextView tvChoName;
    //甘油三酯
    private TextView tvTrig;
    private TextView tvTrigName;
    TextView tvTrigMax;
    TextView tvTrigMin;
    TextView tvTrigUnit;
    //低密度脂蛋白
    private TextView tvLdl;
    private TextView tvLdlName;
    TextView tvLdlMax;
    TextView tvLdlMin;
    TextView tvLdlUnit;
    //高密度脂蛋白
    private TextView tvHdl;
    private TextView tvHdlName;

    private View view;
    private MeasureDataBean measureData;
    private StatisticalDialogController statisticalController;
    private long lastClickTimestamp = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_blood_fat, container, false);
        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);
        initView();
        initGuideView();
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
//        initMeasureData();
    }

    /**
     * 初始化测量数据
     */
    private void initMeasureData() {
        if (measureData == null) {
            return;
        }
        int hdl = measureData.getTrendValue(KParamType.BLOOD_FAT_HDL);
        int ldl = measureData.getTrendValue(KParamType.BLOOD_FAT_LDL);
        int trig = measureData.getTrendValue(KParamType.BLOOD_FAT_TRIG);
        int cho = measureData.getTrendValue(KParamType.BLOOD_FAT_CHO);
        UiUitls.setMeasureResult(KParamType.BLOOD_FAT_HDL, hdl, tvHdlName, tvHdl);
        UiUitls.setMeasureResult(KParamType.BLOOD_FAT_LDL, ldl, tvLdlName, tvLdl);
        UiUitls.setMeasureResult(KParamType.BLOOD_FAT_TRIG, trig, tvTrigName, tvTrig);
        UiUitls.setMeasureResult(KParamType.BLOOD_FAT_CHO, cho, tvChoName, tvCho);
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
                        StatisticalDialogController(getActivity(), presenter.
                        getStatisticalTableItem(), presenter.getStatisticalPic(getActivity()),
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
        initViewData();
        presenter.bindAidlService();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.unbindService();
    }

    @Override
    public void measureSuccess(float choValue, float trigValue, float ldlValue, float hdlValue) {
        if (choValue != GlobalConstant.INVALID_DATA) {
            UiUitls.setMeasureResult(KParamType.BLOOD_FAT_CHO, choValue, tvChoName, tvCho);
        }
        if (trigValue != GlobalConstant.INVALID_DATA) {
            UiUitls.setMeasureResult(KParamType.BLOOD_FAT_TRIG, trigValue, tvTrigName, tvTrig);
        }
        if (ldlValue != GlobalConstant.INVALID_DATA) {
            UiUitls.setMeasureResult(KParamType.BLOOD_FAT_LDL, ldlValue, tvLdlName, tvLdl);
        }
        if (hdlValue != GlobalConstant.INVALID_DATA) {
            UiUitls.setMeasureResult(KParamType.BLOOD_FAT_HDL, hdlValue, tvHdlName, tvHdl);
        }
        EventBus.getDefault().post(new EventBusUseEvent(AppFragment.LIPD_FLAG));
    }

    @Override
    public void setMeasureData(MeasureDataBean measureDataBean) {
        this.measureData = measureDataBean;
        initMeasureData();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public BloodFatPresenterImpl initPresenter() {

        return new BloodFatPresenterImpl(this);
    }

    /**
     * 初始化引导界面
     */
    private void initGuideView() {
        containLayout.addView(presenter.getInstallGuideView(getActivity(), containLayout,
                VideoUtil.getVideoListener(getActivity(), KParamType.BLOOD_FAT_CHO)));
        btnMeasure.setVisibility(View.GONE);

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
    }

    /**
     * 初始化测量值的所有控件
     */
    private void initMeasureValueView() {
        tvTrigName = (TextView) rlTrig.findViewById(R.id.tv_measure_name);
        tvTrigMax = (TextView) rlTrig.findViewById(R.id.tv_measure_max);
        tvTrigMin = (TextView) rlTrig.findViewById(R.id.tv_measure_min);
        tvTrigUnit = (TextView) rlTrig.findViewById(R.id.tv_measure_unit);
        tvTrig = (TextView) rlTrig.findViewById(R.id.tv_measure_value);
        tvTrigName.setText(R.string.TRIG);
        tvTrigUnit.setText(R.string.unit_mmol_l);
        tvTrig.setText(R.string.invalid_data);
        tvTrigMax.setText(String.format("%.2f", ReferenceUtils.getMaxReference(
                KParamType.BLOOD_FAT_TRIG)));
        tvTrigMin.setText(String.valueOf(ReferenceUtils.getMinReference(
                KParamType.BLOOD_FAT_TRIG)));

        tvLdlName = (TextView) rlLdl.findViewById(R.id.tv_measure_name);
        tvLdlMax = (TextView) rlLdl.findViewById(R.id.tv_measure_max);
        tvLdlMin = (TextView) rlLdl.findViewById(R.id.tv_measure_min);
        tvLdlUnit = (TextView) rlLdl.findViewById(R.id.tv_measure_unit);
        tvLdl = (TextView) rlLdl.findViewById(R.id.tv_measure_value);
        tvLdlName.setText(R.string.lip_ldl);
        tvLdlUnit.setText(R.string.unit_mmol_l);
        tvLdl.setText(R.string.invalid_data);
        tvLdlMax.setText(String.valueOf(ReferenceUtils.getMaxReference(
                KParamType.BLOOD_FAT_LDL)));
        tvLdlMin.setText(String.valueOf(ReferenceUtils.getMinReference(
                KParamType.BLOOD_FAT_LDL)));

        tvHdlName = (TextView) rlHdl.findViewById(R.id.tv_measure_name);
        TextView tvHdlMax = (TextView) rlHdl.findViewById(R.id.tv_measure_max);
        TextView tvHdlMin = (TextView) rlHdl.findViewById(R.id.tv_measure_min);
        TextView tvUaUnit = (TextView) rlHdl.findViewById(R.id.tv_measure_unit);
        tvHdl = (TextView) rlHdl.findViewById(R.id.tv_measure_value);
        tvHdlName.setTextColor(UiUitls.getColor(R.color.measure_value_text_color));
        tvHdlName.setText(R.string.HHDL);
        tvUaUnit.setText(R.string.unit_mmol_l);
        tvHdl.setTextColor(UiUitls.getColor(R.color.measure_value_text_color));
        tvHdl.setText(R.string.invalid_data);
        tvHdlMax.setText(String.valueOf(ReferenceUtils.getMaxReference(
                KParamType.BLOOD_FAT_HDL)));
        tvHdlMin.setText(String.valueOf(ReferenceUtils.getMinReference(
                KParamType.BLOOD_FAT_HDL)));

        tvChoName = (TextView) rlCho.findViewById(R.id.tv_measure_name);
        TextView tvChoMax = (TextView) rlCho.findViewById(R.id.tv_measure_max);
        TextView tvChoMin = (TextView) rlCho.findViewById(R.id.tv_measure_min);
        TextView tvTotalChoUnit = (TextView) rlCho.findViewById(R.id.tv_measure_unit);
        tvCho = (TextView) rlCho.findViewById(R.id.tv_measure_value);
        tvChoName.setTextColor(UiUitls.getColor(R.color.measure_value_text_color));
        tvChoName.setText(R.string.total_cho);
        tvTotalChoUnit.setText(R.string.unit_mmol_l);
        tvCho.setTextColor(UiUitls.getColor(R.color.measure_value_text_color));
        tvCho.setText(R.string.invalid_data);
        tvChoMax.setText(String.valueOf(ReferenceUtils.getMaxReference(
                KParamType.BLOOD_FAT_CHO)));
        tvChoMin.setText(String.valueOf(ReferenceUtils.getMinReference(
                KParamType.BLOOD_FAT_CHO)));
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
            EventBus.getDefault().post(new EventBusUseEvent(AppFragment.LIPD_FLAG));
            //切换即刷新数据
            setMeasureData(ServiceUtils.getMeausreDataBean());
        }
    }
}
