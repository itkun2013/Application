package com.konusng.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.Response.EcgQueryResponse;
import com.konsung.util.UiUitls;
import com.konusng.adapter.base.HolderAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xiangshicheng on 2017/7/13 0013.
 */

public class RemoteRecordAdapter extends HolderAdapter<EcgQueryResponse.RowData
        , RemoteRecordAdapter.ViewHolder> {

    /**
     * 构造方法
     * @param context 上下文
     * @param listData 集合
     */
    public RemoteRecordAdapter(Context context, List<EcgQueryResponse.RowData> listData) {
        super(context, listData);
    }

    /**
     * 建立视图
     * @param position 位置
     * @param t 实体对象
     * @param layoutInflater view加载工具类
     */
    @Override
    public View buildConvertView(LayoutInflater layoutInflater, EcgQueryResponse.RowData t
            , int position) {
        return layoutInflater.inflate(R.layout.fragment_remote_item, null);
    }

    /**
     * 绑定视图
     * @param t 实体对象
     * @param position 位置
     * @param convertView item视图
     */
    @Override
    public ViewHolder buildHolder(View convertView, EcgQueryResponse.RowData t, int position) {
        ViewHolder holder = new ViewHolder();
        ButterKnife.inject(holder, convertView);
        return holder;
    }

    /**
     * view赋值数据
     * @param holder holder类
     * @param position 位置
     * @param t 实体对象
     */
    @Override
    public void bindViewDatas(ViewHolder holder, EcgQueryResponse.RowData t, int position) {
        holder.tvName.setText(t.name);
        if(!TextUtils.isEmpty(t.idNumber)){
            holder.tvAge.setText(UiUitls.getAge(t.idNumber) + "岁");
        }else{
            holder.tvAge.setText(t.idNumber);
        }
        holder.tvDate.setText(t.requestDate);
        holder.tvIdCard.setText(t.idNumber);
        holder.tvTel.setText(t.telephone);
        holder.tvState.setText(showState(t.conState));
        if (t.sex.equals("2")) {
            //女
            holder.ivSex.setImageResource(R.drawable.ic_female);
        } else {
            //男
            holder.ivSex.setImageResource(R.drawable.ic_male);
        }
        if (position % 2 != 0) {
            //奇数项
            holder.llBg.setBackgroundResource(
                    R.drawable.remote_item_bg_2);
            holder.line.setBackgroundResource(R.color.color_line2);
        } else {
            //偶数项
            holder.llBg.setBackgroundResource(
                    R.drawable.remote_item_bg_1);
            holder.line.setBackgroundResource(R.color.color_line1);
        }
    }

    private String showState (String state) {
        String str = "";
        switch (Integer.parseInt(state)) {
            //处理中
            case 0:
                str = "待处理";
                break;
            //处理完毕
            case 1:
                str = "已处理";
                break;
            default:
                break;
        }
        return str;
    }
    /**
     * 静态Holder类
     */
    public static class ViewHolder {
        //姓名
        @InjectView(R.id.name)
        TextView tvName;
        //年龄
        @InjectView(R.id.age)
        TextView tvAge;
        //电话
        @InjectView(R.id.tel_number)
        TextView tvTel;
        //身份证
        @InjectView(R.id.id_card)
        TextView tvIdCard;
        //日期
        @InjectView(R.id.apply_date)
        TextView tvDate;
        //状态
        @InjectView(R.id.confirm_state)
        TextView tvState;
        //性别
        @InjectView(R.id.sex)
        ImageView ivSex;
        @InjectView(R.id.view_change_line)
        View line;
        @InjectView(R.id.ll_bg)
        LinearLayout llBg;
    }
}
