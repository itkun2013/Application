package com.konsung.holder;

import android.view.View;
import android.widget.TextView;

import com.konsung.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 测量项布局持有者
 */
public class ValueLayoutHolder extends ViewHolder {

    @InjectView(R.id.tv_measure_name)
    public TextView tvName;
    @InjectView(R.id.tv_measure_value)
    public TextView tvValue;
    @InjectView(R.id.tv_measure_max)
    public TextView tvMax;
    @InjectView(R.id.tv_measure_min)
    public TextView tvMin;
    @InjectView(R.id.tv_measure_unit)
    public TextView tvUnit;

    /**
     * 可复用的 值显示布局 持有者
     * @param view 布局控件
     */
    public ValueLayoutHolder(View view) {
        super(view);
        ButterKnife.inject(this, view);
    }
}
