package com.konusng.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.PatientBean;
import com.konsung.defineview.CircleImageView;
import com.konsung.util.GlobalConstant;
import com.konsung.util.StringUtil;
import com.konsung.util.UiUitls;
import com.konsung.util.global.PatientStyle;
import com.synjones.bluetooth.BmpUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 用户信息适配器
 * 适应查询全部用户界面的ListView
 * @author ouyangfan
 * @version 0.0.1
 */
public class PatientItemAdapter extends BaseAdapter {
    private static final int MAN = 1;
    private static final int WOMAN = 0;
    private List<PatientBean> patients = null;
    private LayoutInflater layoutInflater = null;
    private Activity context;
    private Map<Integer, Boolean> deleteMap = new HashMap<>();
    private String idcard;
    private Bitmap bmp = null;

    /**
     * 适配器构造方法
     * @param activity 上下文引用
     * @param patientLists 用户列表
     */
    public PatientItemAdapter(Activity activity, List<PatientBean> patientLists) {
        layoutInflater = layoutInflater.from(activity);
        patients = patientLists;
        context = activity;
    }

    @Override
    public int getCount() {
        return patients.size();
    }

    @Override
    public Object getItem(int position) {
        return patients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final PatientBean patient = patients.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.patient_item, null);
            holder = new ViewHolder(convertView);
            // 使用ButterK nife初始化控件
            ButterKnife.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvName.setText(StringUtil.stringOmit(patient.getName()));
        holder.tvAge.setText(patient.getAge() < 0 ? "" : (patient.getAge() + UiUitls
                .getString(R.string.unit_age)));
        String cardTemp = "";
        if (!TextUtils.isEmpty(patient.getCard())) {
            cardTemp = patient.getCard();
        } else if (!TextUtils.isEmpty(patient.getMemberShipCard())
                && !patient.getMemberShipCard().startsWith(GlobalConstant.preStr)) {
            cardTemp = patient.getMemberShipCard();
        }
        holder.tvIdcard.setText(cardTemp);
        switch (patient.getPatient_type()) {
            case PatientStyle.ADULT:
                holder.tvType.setText(R.string.type_adult);
                break;
            case PatientStyle.YOUTH:
                holder.tvType.setText(R.string.type_youth);
                break;
            case PatientStyle.BABY:
                holder.tvType.setText(R.string.type_baby);
                break;
            default:
                break;
        }
        switch (patient.getSex()) {
            case MAN:
                holder.ivSex.setImageResource(R.drawable.ic_male);
                break;
            case WOMAN:
                holder.ivSex.setImageResource(R.drawable.ic_female);
                break;
            default:
                break;
        }

        try {
            Bitmap bmp = BmpUtil.getHeadBitmap(patient.getCard());
            if (bmp != null) {
                //根据身份证刷新
                holder.ivPic.setImageBitmap(bmp);
            } else {
                //导入1.3.0数据身份证头像
                String tempStr = patient.getHeadBmpStr();
                byte[] temp = Base64.decode(tempStr, Base64.DEFAULT);
                String picStr = new String(temp, "ISO-8859-1");
                if (!TextUtils.isEmpty(picStr)) {
                    //头像名以时间戳唯一命名
                    String bmpStr = patient.getCard();
                    //保存头像至本地
                    UiUitls.savePhoto(picStr, bmpStr);
                    //从本地获取头像
                    Bitmap bitmap = BmpUtil.getBitmapByFileName(bmpStr);
                    if (bitmap != null) {
                        holder.ivPic.setImageBitmap(bitmap);
                    } else {
                        holder.ivPic.setImageResource(R.drawable.pic_default_avatar);
                    }
                } else {
                    holder.ivPic.setImageResource(R.drawable.pic_default_avatar);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //删除模式下选中
        if (deleteMap.containsValue(true) && deleteMap.get(position)) {
            holder.ivCheck.setVisibility(View.VISIBLE);
        } else {
            holder.ivCheck.setVisibility(View.GONE);
        }
        //选中
        if (!TextUtils.isEmpty(idcard) && patient.getCard().equals(idcard)) {
            holder.ivPointer.setVisibility(View.VISIBLE);
        } else {
            holder.ivPointer.setVisibility(View.GONE);
        }
        return convertView;
    }

    /**
     * 设置数据
     * @param list 用户列表
     */
    public void setDatas(List<PatientBean> list) {
        patients = list;
        notifyDataSetChanged();
    }

    /**
     * 添加删除数据刷新
     * @param map 删除
     */
    public void setDeleteMapu(Map<Integer, Boolean> map) {
        deleteMap = map;
        notifyDataSetChanged();
    }
    /**
     * 添加删除数据不刷新
     * @param map 删除
     */
    public void setDeleteMap(Map<Integer, Boolean> map) {
        deleteMap = map;
    }
    /**
     * viewholde类
     */
    static class ViewHolder {
        @InjectView(R.id.iv_pic)
        CircleImageView ivPic;
        @InjectView(R.id.tv_name)
        TextView tvName;
        @InjectView(R.id.iv_sex)
        ImageView ivSex;
        @InjectView(R.id.tv_age)
        TextView tvAge;
        @InjectView(R.id.tv_type)
        TextView tvType;
        @InjectView(R.id.tv_idcard)
        TextView tvIdcard;
        @InjectView(R.id.iv_check)
        CircleImageView ivCheck;
        @InjectView(R.id.iv_pointer)
        ImageView ivPointer;

        /**
         * 构造方法
         * @param view 视图
         */
        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    /**
     * 设置选中的用户的idCard
     * @param idcard 身份证
     */
    public void setSelectedPatient(String idcard) {
        this.idcard = idcard;
        notifyDataSetChanged();
    }
}