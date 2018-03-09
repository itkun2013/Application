package com.konsung.activity;

import android.os.Bundle;

import com.konsung.R;
import com.konsung.fragment.RemoteEcgFragment;
import com.konsung.util.UiUitls;

/**
 * Created by xiangshicheng on 2017/10/11 0011.
 */

public class TeleEcgActivity extends BaseActivity {

    //fragment对象
    private RemoteEcgFragment remoteEcgFragment;
    //fragment标识
    private final String remoteEcgFragmentFlag = "remoteEcgFragment";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_measure);
        initTitle();
        initView();
    }

    /**
     * 初始化标题
     */
    private void initTitle() {
        //显示actionBar
        getSupportActionBar().show();
        setLeftButtonText(getRecString(R.string.remote_ecg));
    }

    /**
     * 初始化view
     */
    private void initView() {
        remoteEcgFragment = new RemoteEcgFragment();
        UiUitls.switchToFragment(R.id.measure_content, remoteEcgFragment
                , remoteEcgFragmentFlag, false, getFragmentManager());
    }
}
