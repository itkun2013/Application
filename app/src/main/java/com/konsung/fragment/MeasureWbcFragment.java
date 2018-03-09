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
import com.konsung.presenter.WbcPresenter;
import com.konsung.presenter.impl.WbcPresenterImpl;
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
 * 白细胞测量页面
 **/
public class MeasureWbcFragment extends com.konsung.view.BaseFragment<WbcPresenterImpl>
        implements WbcPresenter.View, View.OnClickListener {

    //白细胞
    @InjectView(R.id.relative_wbc)
    View wbc;
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
    @InjectView(R.id.btn_measure_template2)
    Button btnTrend; // 趋势统计

    //白细胞
    private TextView tvWbc;
    private TextView tvWbcName;
    TextView tvWbcMax;
    TextView tvWbcMin;
    TextView tvWbcUnit;
    private View view;
    private MeasureDataBean measureData;
    private long lastClickTimestamp = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_wbc, container, false);
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
        if (measureData != null) {
            UiUitls.setMeasureResult(KParamType.BLOOD_WBC, measureData, tvWbcName, tvWbc);
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
        initMeasureData();
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.unbindService();
    }

    @Override
    public void measureSuccess(float wbc) {
        UiUitls.setMeasureResult(KParamType.BLOOD_WBC, wbc,
                tvWbcName, tvWbc);
        //同步左列表数据
        EventBus.getDefault().post(new EventBusUseEvent(AppFragment.WC_FLAG));
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
    public WbcPresenterImpl initPresenter() {

        return new WbcPresenterImpl(this);
    }

    /**
     * 初始化引导界面
     */
    private void initGuideView() {
        containLayout.addView(presenter.getInstallGuideView(getActivity(), containLayout,
                VideoUtil.getVideoListener(getActivity(), KParamType.BLOOD_WBC)));
        btnMeasure.setVisibility(View.GONE);
        tvLink.setText(UiUitls.getString(R.string.link_detector));
        tvSetout.setText(UiUitls.getString(R.string.reagent_for_blood_sampling));
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
        tvWbcName = (TextView) wbc.findViewById(R.id.tv_measure_name);
        tvWbcMax = (TextView) wbc.findViewById(R.id.tv_measure_max);
        tvWbcMin = (TextView) wbc.findViewById(R.id.tv_measure_min);
        tvWbcUnit = (TextView) wbc.findViewById(R.id.tv_measure_unit);
        tvWbc = (TextView) wbc.findViewById(R.id.tv_measure_value);
        tvWbcName.setText(R.string.wbc);
        tvWbcUnit.setText(R.string.unit_index);
        tvWbc.setText(R.string.invalid_data);
        tvWbcMax.setText(String.valueOf(ReferenceUtils.getMaxReference(KParamType.BLOOD_WBC)));
        tvWbcMin.setText(String.valueOf(ReferenceUtils.getMinReference(KParamType.BLOOD_WBC)));
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
            EventBus.getDefault().post(new EventBusUseEvent(AppFragment.WC_FLAG));
            //切换即刷新数据
            setMeasureData(ServiceUtils.getMeausreDataBean());
        }
    }
}
