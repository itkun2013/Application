package com.konsung.view;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.konsung.network.LifeSubscription;
import com.konsung.network.Stateful;
import com.konsung.presenter.BasePresenter;


/**
 * 所有fragment的基类
 * @param <P> 所有fragment的逻辑处理实例
 */
public abstract class BaseFragment<P extends BasePresenter> extends Fragment implements
        LifeSubscription, Stateful {

    protected P presenter;
    protected View contentView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = initPresenter();
    }

    @Override
    public void setState(int state) {

    }

    /**
     * 初始化presenter
     * @return 对应的presenter
     */
    public abstract P initPresenter();


    @Override
    public void onDetach() {
        super.onDetach();

    }
}
