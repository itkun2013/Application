package com.konsung.view.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.konsung.R;
import com.konsung.defineview.EcgProbeGuideView;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 心电测量引导页面持有者
 */
public class EcgTutorialHolder extends ViewHolder {
    /**
     * 接入导联
     */
    public static final int STATUS_CONNECT = 0;
    /**
     * 安装导联
     */
    public static final int STATUS_INSTALL = 1;
    /**
     * 隐藏引导界面，测量状态
     */
    public static final int STATUS_MEASURE = 2;

    @InjectView(R.id.layout_insert)
    public LinearLayout layoutInsert;
    @InjectView(R.id.layout_install)
    public RelativeLayout layoutInstall;

    @InjectView(R.id.img_connect)
    public EcgProbeGuideView guideView;

    /**
     * Ecg导航持有者
     * @param view 布局控件
     */
    public EcgTutorialHolder(View view) {
        super(view);
        ButterKnife.inject(this, view);
    }

    /**
     * 设置导联脱落信号
     * @param signal 信号
     */
    public void setLeadOffSignal(int signal) {
        //信号解析
        //设置信号
        guideView.sentLeadOffSignal(signal);
    }

    /**
     * 设置导航页的状态
     * @param status 导航状态{{@link #STATUS_CONNECT}{@link #STATUS_INSTALL}{@link #STATUS_MEASURE}}
     */
    public void setViewStatus(int status) {
        switch (status) {
            case STATUS_CONNECT:
                view.setVisibility(View.VISIBLE);
                layoutInsert.setVisibility(View.VISIBLE);
                layoutInstall.setVisibility(View.GONE);
                break;
            case STATUS_INSTALL:
                view.setVisibility(View.VISIBLE);
                layoutInsert.setVisibility(View.GONE);
                layoutInstall.setVisibility(View.VISIBLE);
                break;
            case STATUS_MEASURE:
                view.setVisibility(View.GONE);
                break;
            default:
                view.setVisibility(View.GONE);
                break;
        }
    }
}
