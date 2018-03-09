package com.konsung.view.holder;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.konsung.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 血氧引导界面持有者
 **/

public class Spo2TutorialHolder extends ViewHolder {
    @InjectView(R.id.img_tutorial)
    public ImageView imgTutorial;
    @InjectView(R.id.tv_look)
    public TextView tvLook;
    @InjectView(R.id.tv_tutorial)
    public TextView tvTutorual;

    /**
     * 血氧导航持有者
     * @param context 上下文
     * @param id 布局ID
     * @param root 父布局
     */
    public Spo2TutorialHolder(Context context, @LayoutRes int id, ViewGroup root) {
        super(context, id, root);
        ButterKnife.inject(this, view);
    }
}
