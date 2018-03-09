package com.konsung.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.cengalabs.flatui.views.FlatEditText;
import com.konsung.R;
import com.konsung.activity.BaseActivity;
import com.konsung.defineview.ImageTextButton;
import com.konsung.msgevent.EventBusUseEvent;
import com.konsung.util.GlobalConstant;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author xiangshicheng
 * 厂家维护页面
 */
public class FactoryProtectFragment extends BaseFragment {

    @InjectView(R.id.password)
    FlatEditText passEt;
    @InjectView(R.id.sure_login)
    ImageTextButton sureBtn;
    @InjectView(R.id.pass_wrong)
    TextView passWrongTv;
    private String password;
    private InputMethodManager manager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_factory_protect, container, false);
        ButterKnife.inject(this, view);
        manager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        initListener();
        return view;
    }

    /**
     * 事件初始化
     */
    private void initListener() {
        sureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //键盘消失
                manager.hideSoftInputFromWindow(passEt.getWindowToken(), 0);
                password = passEt.getText().toString().trim();
                //判断密码正确性
                if (password.equals(getString(R.string.test_password))) {
                    passWrongTv.setVisibility(View.GONE);
                    GlobalConstant.TEST_PASSWORD = getString(R.string.test_password);
                    GlobalConstant.isAdministraor = true;
                    //该事件发送至mainActivity处理
                    EventBus.getDefault().post(new EventBusUseEvent(getRecString(R.string
                            .factory_model_flag)));
                    ((BaseActivity) getActivity()).popActivity();
                } else {
                    passWrongTv.setVisibility(View.VISIBLE);
                    passEt.setText("");
                }
            }
        });
    }
}
