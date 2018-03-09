package com.konsung.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.konsung.R;
import com.konsung.msgevent.RemoteEcgSearchEvent;
import com.konsung.util.UiUitls;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xiangshicheng on 2017/7/12 0012.
 */

public class RemoteEcgFragment extends BaseFragment implements View.OnClickListener
        , TextWatcher {

    @InjectView(R.id.ll_all)
    LinearLayout llAll;
    @InjectView(R.id.ll_done)
    LinearLayout llDone;
    @InjectView(R.id.ll_wait)
    LinearLayout llWait;

    @InjectView(R.id.all_ecg_num)
    TextView tvAllEcg;
    @InjectView(R.id.done_num)
    TextView tvDoneEcg;
    @InjectView(R.id.wait_num)
    TextView tvWaitRcg;

    @InjectView(R.id.search_input)
    EditText etSearch;
    @InjectView(R.id.search_tv)
    Button btnSearch;

    @InjectView(R.id.blue_line_1)
    View line1;
    @InjectView(R.id.blue_line_2)
    View line2;
    @InjectView(R.id.blue_line_3)
    View line3;

    @InjectView(R.id.all)
    TextView tvAll;
    @InjectView(R.id.done)
    TextView tvDone;
    @InjectView(R.id.wait)
    TextView tvWait;

    @InjectView(R.id.content_layout)
    RelativeLayout rlContentLayout;

    //记录当前心电状态所在页
    private String currentFlag = "";

    private final String fragmentAllTag = "fragmentAllTag";
    private final String fragmentDoneTag = "fragmentDoneTag";
    private final String fragmentWaitTag = "fragmentWaitTag";

    //全部为空
    public static final String ECG_ALL_STATE = "";
    //处理完毕为1
    public static final String ECG_DONE_STATE = "1";
    //待处理为0
    public static final String ECG_WAIT_STATE = "0";

    //区分手动清除和自动清除，自动清除不需要重新加载数据，手动清除需重新加载数据
    public boolean isAutoClear = false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_remote_ecg, null);
        ButterKnife.inject(this, view);
        initView();
        initListener();
        return view;
    }
    /**
     * 初始化视图
     */
    public void initView() {
        //首次进来默认为全部列表展示,已处理/待处理tab置灰
        tvDone.setTextColor(getResources().getColor(R.color.color_grey));
        tvDoneEcg.setTextColor(getResources().getColor(R.color.color_grey));
        tvWait.setTextColor(getResources().getColor(R.color.color_grey));
        tvWaitRcg.setTextColor(getResources().getColor(R.color.color_grey));
        //默认选中第一条
        switchTab(R.id.ll_all);
    }

    /**
     * 初始化数据
     */
    public void initData() {}

    /**
     * 初始化监听
     */
    public void initListener() {
        llAll.setOnClickListener(this);
        llDone.setOnClickListener(this);
        llWait.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        etSearch.addTextChangedListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //点击全部
            case R.id.ll_all:
                currentFlag = "";
                //全部状态
                changeState(View.VISIBLE, View.INVISIBLE, View.INVISIBLE
                        , R.color.remote_color_1, R.color.color_grey, R.color.color_grey);
                switchTab(R.id.ll_all);
                break;
            //点击已处理
            case R.id.ll_done:
                currentFlag = "1";
                //已处理状态
                changeState(View.INVISIBLE, View.VISIBLE, View.INVISIBLE
                        , R.color.color_grey, R.color.remote_color_1, R.color.color_grey);
                switchTab(R.id.ll_done);
                break;
            //点击待处理
            case R.id.ll_wait:
                currentFlag = "0";
                //待处理状态
                changeState(View.INVISIBLE, View.INVISIBLE, View.VISIBLE
                        , R.color.color_grey, R.color.color_grey, R.color.remote_color_1);
                switchTab(R.id.ll_wait);
                break;
            //查询数据
            case R.id.search_tv:
                EventBus.getDefault().post(new RemoteEcgSearchEvent(currentFlag));
                break;
            default:
                break;
        }
    }

    /**
     * 点击改变状态
     * @param isAll 隐藏值
     * @param isDone 隐藏值
     * @param isWait 隐藏值
     * @param colorAll 颜色值
     * @param colorDone 颜色值
     * @param colorWait 颜色值
     */
    private void changeState(int isAll, int isDone, int isWait
            , int colorAll, int colorDone, int colorWait) {
        Resources colorResource = getResources();
        line1.setVisibility(isAll);
        line2.setVisibility(isDone);
        line3.setVisibility(isWait);
        tvAll.setTextColor(colorResource.getColor(colorAll));
        tvAllEcg.setTextColor(colorResource.getColor(colorAll));
        tvDone.setTextColor(colorResource.getColor(colorDone));
        tvDoneEcg.setTextColor(colorResource.getColor(colorDone));
        tvWait.setTextColor(colorResource.getColor(colorWait));
        tvWaitRcg.setTextColor(colorResource.getColor(colorWait));
    }

    /**
     * 切换fragment
     * @param viewId 控件ID
     */
    private void switchTab(int viewId) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment allFragment = fm.findFragmentByTag(fragmentAllTag);
        Fragment doneFragment = fm.findFragmentByTag(fragmentDoneTag);
        Fragment waitFragment = fm.findFragmentByTag(fragmentWaitTag);

        if (allFragment != null) {
            ft.hide(allFragment);
        }
        if (doneFragment != null) {
            ft.hide(doneFragment);
        }
        if (waitFragment != null) {
            ft.hide(waitFragment);
        }
        switch (viewId) {
            case R.id.ll_all:
                if (allFragment == null) {
                    allFragment = new RemoteEcgAllFragment();
                    ft.add(R.id.content_layout, allFragment, fragmentAllTag);
                } else {
                    ft.show(allFragment);
                }
                break;
            case R.id.ll_done:
                if (doneFragment == null) {
                    doneFragment = new RemoteEcgDoneFragment();
                    ft.add(R.id.content_layout, doneFragment, fragmentDoneTag);
                } else {
                    ft.show(doneFragment);
                }
                break;
            case R.id.ll_wait:
                if (waitFragment == null) {
                    waitFragment = new RemoteEcgWaitFragment();
                    ft.add(R.id.content_layout, waitFragment, fragmentWaitTag);
                } else {
                    ft.show(waitFragment);
                }
                break;
            default:
                break;
        }
        ft.commit();
    }

    /**
     * 获取输入框的内容
     * @return 查询框的内容
     */
    public String getInputContent() {
        return etSearch.getText().toString().trim();
    }

    /**
     * 清空输入框内容
     */
    public void clearEtContent() {
        etSearch.setText("");
    }

    /**
     * 设置总记录条数
     * @param ecgFlag 心电类型
     * @param num 数量
     */
    public void setEcgRecordNum(String ecgFlag, int num) {
        switch (ecgFlag) {
            case ECG_ALL_STATE :
                if (num > 0) {
                    tvAllEcg.setVisibility(View.VISIBLE);
                    tvAllEcg.setText("(" + num + ")");
                } else {
                    tvAllEcg.setVisibility(View.GONE);
                }
                break;
            case ECG_DONE_STATE :
                if (num > 0) {
                    tvDoneEcg.setVisibility(View.VISIBLE);
                    tvDoneEcg.setText("(" + num + ")");
                } else {
                    tvDoneEcg.setVisibility(View.GONE);
                }
                break;
            case ECG_WAIT_STATE :
                if (num > 0) {
                    tvWaitRcg.setVisibility(View.VISIBLE);
                    tvWaitRcg.setText("(" + num + ")");
                } else {
                    tvWaitRcg.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //导航栏返回键是否点击
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //监听 actionbar 的返回键
        if (item.getItemId() == android.R.id.home) {
            UiUitls.hideSoftInput(getActivity(), getView());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        //当搜索框内容被清空的时候查询当前状态下心电首页
        if (s.length() == 0) {
            if (!isAutoClear) {
                EventBus.getDefault().post(new RemoteEcgSearchEvent(currentFlag));
            } else {
                isAutoClear = false;
            }
        }
    }
}
