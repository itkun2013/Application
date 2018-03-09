package com.konusng.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.PatientBean;
import com.konsung.defineview.CircleImageView;
import com.konusng.adapter.base.HolderAdapter;
import com.synjones.bluetooth.BmpUtil;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xiangshicheng on 2017/10/11 0011.
 */

public class PopupwindowListAdapter extends HolderAdapter<PatientBean
        , PopupwindowListAdapter.ViewHolder> {

    /**
     * 构造方法
     * @param context 上下文
     * @param list 集合
     */
    public PopupwindowListAdapter(Context context, List<PatientBean> list) {
        super(context, list);
    }

    /**
     * 建立视图
     * @param position 位置
     * @param p 实体对象
     * @param layoutInflater view加载工具类
     */
    @Override
    public View buildConvertView(LayoutInflater layoutInflater, PatientBean p, int position) {
        return layoutInflater.inflate(R.layout.popwindow_item_layout, null);
    }

    /**
     * 绑定视图
     * @param p 实体对象
     * @param position 位置
     * @param convertView item视图
     */
    @Override
    public ViewHolder buildHolder(View convertView, PatientBean p, int position) {
        ViewHolder viewHolder = new ViewHolder();
        ButterKnife.inject(viewHolder, convertView);
        return viewHolder;
    }

    /**
     * view赋值数据
     * @param holder holder类
     * @param position 位置
     * @param p 实体对象
     */
    @Override
    public void bindViewDatas(ViewHolder holder, PatientBean p, int position) {
        String headPic = p.getBmpStr();
        String name = p.getName();
        int sex = p.getSex();
        int patientType = p.getPatient_type();
        Bitmap bitmap = BmpUtil.getBitmapByFileName(headPic);
        if (bitmap != null) {
            if (!TextUtils.isEmpty(headPic)) {

                holder.headPic.setImageBitmap(bitmap);
            } else {
                holder.headPic.setImageResource(R.drawable.pic_default_avatar);
            }
        } else {
            holder.headPic.setImageResource(R.drawable.pic_default_avatar);
        }
        holder.tvName.setText(name);
        holder.tvSex.setText(context.getResources().getStringArray(R.array.detail_sex)[sex]);
        holder.tvPatientType.setText(context.getResources()
                .getStringArray(R.array.patient_type_array)[patientType]);
    }

    /**
     * 复用对象持有
     */
    class ViewHolder {
        @InjectView(R.id.measure_head_pic)
        CircleImageView headPic;
        @InjectView(R.id.measure_name)
        TextView tvName;
        @InjectView(R.id.measure_sex)
        TextView tvSex;
        @InjectView(R.id.measure_patient_type)
        TextView tvPatientType;
    }
}
