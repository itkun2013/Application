package com.konusng.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * 趋势图趋势表弹框的适配器
 */

public class DialogViewPageAdapter extends PagerAdapter {
    List<View> viewList;

    /**
     * 构造器
     * @param views 添加的布局集合
     */
    public DialogViewPageAdapter(List<View> views) {
        viewList = views;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position)); //删除页卡
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //这个方法用来实例化页卡
        container.addView(viewList.get(position), 0); //添加页卡
        return viewList.get(position);
    }

    @Override
    public int getCount() {
        if (viewList != null) {
            return viewList.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object; //官方提示这样写;
    }
}
