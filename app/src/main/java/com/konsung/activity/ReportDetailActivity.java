package com.konsung.activity;

import android.os.Bundle;

import com.konsung.R;
import com.konsung.fragment.GetMeasureDataByPatientFragment;
import com.konsung.util.GlobalConstant;
import com.konsung.util.UiUitls;

/**
 * 报告详情
 * Created by DJH on 2017/10/13 0013.
 */
public class ReportDetailActivity extends BaseActivity {
    private static final String REPORT_FRAGMENT = "reportFragment";
    private String name;
    private String idCard;
    //会员卡号
    private String memberShipCard;
    private Long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        initTitle();
        initData();
        initView();
    }

    /**
     * 初始化导航栏
     */
    private void initTitle() {
        getSupportActionBar().show();
        setLeftButtonText(getString(R.string.main_medical_report));
    }

    /**
     * 初始化数据
     */
    private void initData() {
        id = getIntent().getLongExtra(GlobalConstant.MEASURE_ID, -1);
        name = getIntent().getStringExtra(GlobalConstant.PATIENT_NAME);
        idCard = getIntent().getStringExtra(GlobalConstant.ID_CARD);
        memberShipCard = getIntent().getStringExtra(GlobalConstant.MEMBER_CARD);
        GlobalConstant.isBackFromReportDetail = true;
    }

    /**
     * 初始化报告详情页面
     */
    private void initView() {
        Bundle bundle = new Bundle();
        bundle.putLong(GlobalConstant.MEASURE_ID, id);
        bundle.putString(GlobalConstant.PATIENT_NAME, name);
        bundle.putString(GlobalConstant.ID_CARD, idCard);
        bundle.putString(GlobalConstant.MEMBER_CARD, memberShipCard);
        GetMeasureDataByPatientFragment reportFragment = new GetMeasureDataByPatientFragment();
        reportFragment.setArguments(bundle);
        UiUitls.switchToFragment(R.id.measure_content, reportFragment, REPORT_FRAGMENT, false,
                getFragmentManager());
    }
}
