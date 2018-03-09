package com.konsung.view.holder;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 用于继承的ViewHolder，专门实现页面加载
 * @author yuchunhui
 **/
public abstract class ViewHolder {

    public final View view;

    /**
     * 构造器
     * @param context 上下文
     * @param id 布局资源文件id
     * @param root 父布局
     */
    public ViewHolder(Context context, @LayoutRes int id, ViewGroup root) {
        view = LayoutInflater.from(context).inflate(id, root, false);
    }

    /**
     * 构造器
     * @param view 布局控件
     */
    public ViewHolder(View view) {
        this.view = view;
    }
}
