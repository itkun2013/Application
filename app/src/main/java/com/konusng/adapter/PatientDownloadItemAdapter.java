package com.konusng.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.PatientBean;
import com.konsung.defineview.CircleImageView;
import com.konsung.util.global.GlobalNumber;
import com.konsung.util.global.PatientStyle;
import com.synjones.bluetooth.BmpUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 居民下载页的listview的adapter
 */
public class PatientDownloadItemAdapter extends BaseAdapter {
    private static final int MAN = 1;
    private static final int WOMAN = 0;
    private List<PatientBean> patients = null;
    private LayoutInflater layoutInflater = null;
    private Activity context;
    private String card = "";
    private boolean isCheckAll; //是否全选
    private List<PatientBean> checkedPatients = new ArrayList<>();

    /**
     * 构造器
     * @param activity activity对象
     * @param patientLists 用户列表
     */
    public PatientDownloadItemAdapter(Activity activity, List<PatientBean> patientLists) {
        layoutInflater = layoutInflater.from(activity);
        patients = patientLists;
        context = activity;
    }

    @Override
    public int getCount() {
        if (patients != null) {
            return patients.size();
        }
        return 0;
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
            convertView = layoutInflater.inflate(R.layout.patient_download_item, null);
            holder = new ViewHolder(convertView);

            // 使用ButterK nife初始化控件
            ButterKnife.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String name = patient.getName();
        if (name.length() > GlobalNumber.FOUR_NUMBER) {
            name = name.substring(0, GlobalNumber.THREE_NUMBER) + "…";
        }
        holder.tvName.setText(name);
        String ageTemp = "";
        if (patient.getAge() >= 0) {
            ageTemp = patient.getAge() + "";
        }
        holder.tvAge.setText(ageTemp);
        if (!TextUtils.isEmpty(patient.getCard())) {
            card = patient.getCard();
        } else if (!TextUtils.isEmpty(patient.getMemberShipCard())) {
            if (patient.getMemberShipCard().substring(0, 2).equals("ks")) {
                //ks用于标记身份证和会员卡均未输入时候的假字符串，专门用于数据上传使用
                card = "";
            } else {
                card = patient.getMemberShipCard();
            }
        }
        holder.tvIdcard.setText(card);
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
                holder.ivPic.setImageBitmap(bmp);
            } else {
                holder.ivPic.setImageResource(R.drawable.pic_default_avatar);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.cbSelect.setChecked(isCheckAll);

        holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkedPatients.add(patient);
                } else {
                    //不选择的时候，清除数据
                    checkedPatients.remove(patient);
                }
            }
        });

        return convertView;
    }

    /**
     * 设置数据
     * @param list 数据
     */
    public void setDatas(List<PatientBean> list) {
        patients = list;
        notifyDataSetChanged();
    }

    /**
     * 设置是否全选
     * @param checkAll 是否全选标记
     */
    public void setCheckAll(boolean checkAll) {
        this.isCheckAll = checkAll;
        notifyDataSetChanged();
    }

    /**
     * 获取选中的居民
     * @return 选中的居民
     */
    public List<PatientBean> getCheckedPatients() {
        return checkedPatients;
    }
    /**
     * 获取选中的居民
     * @return 选中的居民
     */
    public boolean isCheckAll() {
        return isCheckAll;
    }

    /**
     * ViewHolder类
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
        @InjectView(R.id.cb_select)
        CheckBox cbSelect;

        /**
         * 内部类
         * @param view 对象
         */
        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}