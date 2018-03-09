package com.konsung.fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * fragment基类
 *
 * @author ouyangfan
 * @version 0.0.1
 */
public class BaseFragment extends Fragment {

    public boolean isBind;
    public Intent intent;
    // 是否已经绑定服务
    public boolean isAttach;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container
            , Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 切换fragment
     * @param containerViewId Fragment被添加到的容器id
     * @param dfragment 被添加的Fragment
     * @param tag fragment的Tag
     * @param isAddedStack 是否被加入到BackStack 的标识符，在这里一定要被添加的。
     */
    public void switchToFragment(int containerViewId, Fragment dfragment
            , String tag, boolean isAddedStack) {
        // 如果当前的Fragment正在显示就不需要去切换
        if (dfragment.isVisible()) {
            return;
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(containerViewId, dfragment, tag);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        if (isAddedStack) {
            ft.addToBackStack(tag);
        }
//        ft.commit();
        //防止activity异常销毁前保存状态(onSavedInstanceState())导致添加fragment异常
        ft.commitAllowingStateLoss();
    }

    /**
     * 获取本地String字符
     * @param id 字符串id
     * @return 字符串
     */
    public String getRecString(int id) {
        return getActivity().getResources().getString(id);
    }

    /**
     * 改变标题（该方法已被弃用）
     * @param title 标题
     */
    public void changeTiTle(String title) {
//        Intent it = new Intent();
//        it.setAction("com.konsung.fragmentChange.receiver");
//        Bundle bundle = new Bundle();
//        bundle.putString("title", title);
//        it.putExtra("fragmentChange", bundle);
//        getActivity().sendBroadcast(it);
        //改为直接获取LaunchaActivity对象去改变导航栏，广播会存在延时，会导致出现切换的瞬间闪现
//        LauncherActivity activity = (LauncherActivity) getActivity();
//        activity.setAppTitle(title);
    }

    /**
     * 初始化
     */
    public void initDatas() {

    }
}
