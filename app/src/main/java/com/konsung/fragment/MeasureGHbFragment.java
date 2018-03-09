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
import com.konsung.bean.ValueLayoutBean;
import com.konsung.holder.ValueLayoutHolder;
import com.konsung.msgevent.EventBusUseEvent;
import com.konsung.presenter.GHbPresenter;
import com.konsung.presenter.impl.GHbPresenterImpl;
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
 * 糖化血红蛋白(glycosylated hemoglobin 即GHb)测量页面
 **/
public class MeasureGHbFragment extends com.konsung.view.BaseFragment<GHbPresenterImpl>
        implements GHbPresenter.View, View.OnClickListener {

    /**
     * 参数布局高度(px)
     */
    public static final int VALUE_LAYOUT_HEIGHT = 174;

    @InjectView(R.id.relative_ifcc)
    RelativeLayout relativeIFCC;
    @InjectView(R.id.relative_ngsp)
    RelativeLayout relativeNGSP;
    @InjectView(R.id.relative_ega)
    RelativeLayout relativeEGA;
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
    Button btnTrend;

    private ValueLayoutHolder holderNGSP;
    private ValueLayoutHolder holderIFCC;
    private ValueLayoutHolder holderEGA;

    private MeasureDataBean measureDataBean = null;

    private View view;
    private StatisticalDialogController statisticalController;
    private long lastClickTimestamp = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ghb, container, false);
        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);
        initGuideView();
        return view;
    }

    /**
     * 初始化布局
     */
    private void initView() {
        btnMeasure.setVisibility(View.GONE);
        btnTrend.setOnClickListener(this);
        holderNGSP = new ValueLayoutHolder(relativeNGSP);
        holderIFCC = new ValueLayoutHolder(relativeIFCC);
        holderEGA = new ValueLayoutHolder(relativeEGA);
        ValueLayoutBean ngsp = new ValueLayoutBean(getActivity(),
                R.string.ghb_ngsp, String.valueOf(ReferenceUtils.
                getMaxReference(KParamType.GHB_HBA1C_NGSP)), String.valueOf(ReferenceUtils.
                getMinReference(KParamType.GHB_HBA1C_NGSP)), UiUitls.getValueUnit(KParamType
                .GHB_HBA1C_NGSP));
        initLayout(holderNGSP, ngsp);

        ValueLayoutBean ifcc = new ValueLayoutBean(getActivity(),
                R.string.ghb_ifcc, String.valueOf(ReferenceUtils.
                getMaxReference(KParamType.GHB_HBA1C_IFCC)), String.valueOf(ReferenceUtils.
                getMinReference(KParamType.GHB_HBA1C_IFCC)), UiUitls.getValueUnit(KParamType
                .GHB_HBA1C_IFCC));
        initLayout(holderIFCC, ifcc);

        ValueLayoutBean eGA = new ValueLayoutBean(getActivity(),
                R.string.ghb_eag, String.valueOf(ReferenceUtils.
                getMaxReference(KParamType.GHB_EAG)), String.valueOf(ReferenceUtils.
                getMinReference(KParamType.GHB_EAG)), UiUitls.getValueUnit(KParamType.GHB_EAG));
        initLayout(holderEGA, eGA);
//        setDataBean(measureDataBean);
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
                        .getStatisticalTableItem(), presenter.getStatisticalPic(getActivity())
                        , true);
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
    public void onStop() {
        super.onStop();
        presenter.unbindService();
    }

    @Override
    public void measureSuccess(int nHbA1cValue, int iHbA1cValue, int eGA) {
        if (nHbA1cValue != GlobalConstant.INVALID_DATA) {
            UiUitls.setMeasureResult(KParamType.GHB_HBA1C_NGSP, nHbA1cValue,
                    holderNGSP.tvName, holderNGSP.tvValue);
        }
        if (iHbA1cValue != GlobalConstant.INVALID_DATA) {
            UiUitls.setMeasureResult(KParamType.GHB_HBA1C_IFCC, iHbA1cValue,
                    holderIFCC.tvName, holderIFCC.tvValue);
        }
        if (eGA != GlobalConstant.INVALID_DATA) {
            UiUitls.setMeasureResult(KParamType.GHB_EAG, eGA,
                    holderEGA.tvName, holderEGA.tvValue);
        }
        EventBus.getDefault().post(new EventBusUseEvent(AppFragment.BHD_FLAG));
    }

    @Override
    public void setDataBean(MeasureDataBean bean) {
        measureDataBean = bean;
        if (bean != null) {
            UiUitls.setMeasureResult(KParamType.HBA1C_NGSP,
                    bean.getTrendValue(KParamType.HBA1C_NGSP),
                    holderNGSP.tvName, holderNGSP.tvValue);
            UiUitls.setMeasureResult(KParamType.HBA1C_IFCC,
                    bean.getTrendValue(KParamType.HBA1C_IFCC),
                    holderIFCC.tvName, holderIFCC.tvValue);
            UiUitls.setMeasureResult(KParamType.HBA1C_EAG,
                    bean.getTrendValue(KParamType.HBA1C_EAG),
                    holderEGA.tvName, holderEGA.tvValue);
        }
    }

    @Override
    public GHbPresenterImpl initPresenter() {
        return new GHbPresenterImpl(this);
    }

    /**
     * 初始化导航
     */
    private void initGuideView() {
        containLayout.addView(presenter.getInstallGuideView(getActivity(), containLayout,
                VideoUtil.getVideoListener(getActivity(), KParamType.GHB_HBA1C_NGSP)));
        btnMeasure.setVisibility(View.GONE);
        tvLink.setText(UiUitls.getString(R.string.link_device));
        tvSetout.setText(UiUitls.getString(R.string.ghb_solution_water));
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
     * 初始化测量项布局
     * @param holder 布局持有者
     * @param bean 布局数据集
     */
    private void initLayout(ValueLayoutHolder holder, ValueLayoutBean bean) {
        holder.view.getLayoutParams().height = VALUE_LAYOUT_HEIGHT;
        holder.tvName.setText(bean.getName());
        holder.tvValue.setText(bean.getValue());

        holder.tvMax.setText(bean.getMax());
        holder.tvMin.setText(bean.getMin());
        holder.tvUnit.setText(bean.getUnit());
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
            EventBus.getDefault().post(new EventBusUseEvent(AppFragment.BHD_FLAG));
            //切换即刷新数据
            MeasureDataBean measureDataBeanTemp = ServiceUtils.getMeausreDataBean();
            setDataBean(measureDataBeanTemp);
        }
    }
}
