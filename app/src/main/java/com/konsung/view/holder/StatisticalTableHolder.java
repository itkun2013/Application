package com.konsung.view.holder;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.konsung.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 趋势表布局持有者
 **/

public class StatisticalTableHolder extends ViewHolder {

    @InjectView(R.id.lv_statistical)
    public ListView lvStatistical;

    public List<TextView> titles;

    /**
     * 趋势表持有者
     * @param view 布局控件
     */
    public StatisticalTableHolder(View view) {
        super(view);
        ButterKnife.inject(this, view);
        titles = new ArrayList<>();
        TextView title1 = (TextView) view.findViewById(R.id.tv_value1);
        TextView title2 = (TextView) view.findViewById(R.id.tv_value2);
        TextView title3 = (TextView) view.findViewById(R.id.tv_value3);
        TextView title4 = (TextView) view.findViewById(R.id.tv_value4);
        titles.add(title1);
        titles.add(title2);
        titles.add(title3);
        titles.add(title4);
    }
}
