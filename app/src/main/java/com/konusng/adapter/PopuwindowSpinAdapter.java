package com.konusng.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konusng.adapter.base.HolderAdapter;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xiangshicheng on 2017/10/11 0011.
 */

public class PopuwindowSpinAdapter extends HolderAdapter<String
        , PopuwindowSpinAdapter.ViewHolder> {
    //当前选中位置，默认为0
    private int mPosition = 0;
    /**
     * 构造方法
     * @param context 上下文
     * @param list 集合
     * @param mPosition 选中位置
     */
    public PopuwindowSpinAdapter(Context context, List<String> list, int mPosition) {
        super(context, list);
        this.mPosition = mPosition;
    }

    /**
     * 建立视图
     * @param position 位置
     * @param s 实体对象
     * @param layoutInflater view加载工具类
     */
    @Override
    public View buildConvertView(LayoutInflater layoutInflater, String s, int position) {
        return layoutInflater.inflate(R.layout.popwindow_spinner_item_layout, null);
    }

    /**
     * 绑定视图
     * @param s 实体对象
     * @param position 位置
     * @param convertView item视图
     */
    @Override
    public ViewHolder buildHolder(View convertView, String s, int position) {
        ViewHolder viewHolder = new ViewHolder();
        ButterKnife.inject(viewHolder, convertView);
        return viewHolder;
    }

    /**
     * view赋值数据
     * @param holder holder类
     * @param position 位置
     * @param s 实体对象
     */
    @Override
    public void bindViewDatas(ViewHolder holder, String s, int position) {
        holder.tvContent.setText(s);
//        if (position == mPosition) {
//            holder.llBg.setBackgroundColor(context.getResources()
//                    .getColor(R.color.first_page_grey));
//        } else {
//            holder.llBg.setBackgroundColor(context.getResources()
//                    .getColor(R.color.actionbar_color));
//        }
    }

    /**
     * Holder类
     */
    class ViewHolder {
        @InjectView(R.id.item_ll)
        LinearLayout llBg;
        @InjectView(R.id.spinner_item_content)
        TextView tvContent;
    }
}
