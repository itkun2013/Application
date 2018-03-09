package com.konsung.activity;

import android.os.Bundle;

import com.konsung.R;
import com.konsung.fragment.SettingFragment;
import com.konsung.util.GlobalConstant;
import com.konsung.util.UiUitls;

/**
 * Created by xiangshicheng on 2017/10/11 0011.
 * 系统设置
 */

public class SystemActivity extends BaseActivity {

    //fragment对象
    private SettingFragment settingFragment;
    //fragment标识
    private final String settingFragmentFlag = "settingFragment";
    //标题字符
    private String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        initTitle();
    }

    /**
     * 初始化标题
     */
    private void initTitle() {
        if (GlobalConstant.isAdministraor) {
            title = getRecString(R.string.factory_protect);
        } else {
            title = getRecString(R.string.main_system_setting);
        }
        getSupportActionBar().show();
        setLeftButtonText(title);
    }

    /**
     * 初始化view
     */
    private void initView() {
        settingFragment = new SettingFragment();
        UiUitls.switchToFragment(R.id.measure_content, settingFragment
                , settingFragmentFlag, false, getFragmentManager());
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }
}
