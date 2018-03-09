package com.konusng.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.konsung.R;
import com.konsung.bean.AppMeasureBean;
import com.konsung.fragment.AppFragment;
import com.konsung.util.GlobalConstant;
import com.konsung.util.MeasureValueCompareUtil;
import com.konsung.util.SpUtils;
import com.konsung.util.global.BloodMem;
import com.konsung.util.global.Nibp;
import com.konsung.util.global.Spo;
import com.konsung.util.global.UrineType;

import java.util.List;
import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 健康测量选项卡适配器
 * 设置测量项目的三种状态
 */
public class HealthCheckSelectedAdapter extends BaseAdapter {
    private List<AppMeasureBean> list;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private final String spoName = "血氧";
    private final String nibpName = "血压";
    private final String hgbName = "血红蛋白";
    /**
     *构造函数
     * @param mContext 上下文引用
     * @param list 集合
     */
    public HealthCheckSelectedAdapter(Context mContext, List<AppMeasureBean> list) {
        super();
        this.mContext = mContext;
        this.list = list;
        mLayoutInflater = LayoutInflater.from(mContext);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = null;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = mLayoutInflater.inflate(R.layout.mapp_list_item, null);
            ButterKnife.inject(mViewHolder, convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }
        AppMeasureBean bean = list.get(position);
        String text = bean.getMeasureName();
        //默认图片
        Integer bg = bean.getIv();
        //点击后的图片
        Integer bgClick = bean.getIvClick();
        mViewHolder.tvName.setText(text);
        if (bean.isClick()) {
            mViewHolder.img.setImageResource(bgClick);
            mViewHolder.tvName.setTextColor(mContext.getResources()
                    .getColor(R.color.title_text_normal));
            mViewHolder.rightArrow.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.img.setImageResource(bg);
            mViewHolder.tvName.setTextColor(mContext.getResources()
                    .getColor(R.color.user_add_title_color));
            mViewHolder.rightArrow.setVisibility(View.INVISIBLE);
        }
        if (bean.isDoubleMeasureValue()) {
            if (bean.getMeasureName().equals(spoName)) {
                setDoubleMeasureValue(Spo.SPO2_HIGH, Spo.SPO2_LOW, Spo.PR_HIGH, Spo.PR_LOW
                        , GlobalConstant.SPO2_VALUE, GlobalConstant.SPO2_PR_VALUE
                        , mViewHolder.tvValue, mViewHolder.tvValueOther, MeasureType.Spo);
            } else if (bean.getMeasureName().equals(nibpName)) {
                //血压
                setDoubleMeasureValue(Nibp.SYS_HIGH, Nibp.SYS_LOW, Nibp.DIA_HIGH, Nibp.DIA_LOW
                        , GlobalConstant.NIBP_SYS_VALUE, GlobalConstant.NIBP_DIA_VALUE
                        , mViewHolder.tvValue, mViewHolder.tvValueOther, MeasureType.Nibp);
            } else if (bean.getMeasureName().equals(hgbName)) {
                float maxHgb;
                float minHgb;
                float maxHct;
                float minHct;
                int sexType = SpUtils.getSpInt(mContext, GlobalConstant.APP_CONFIG
                        , GlobalConstant.CURRENT_SEX, 0);
                if (sexType == 0) {
                    //女
                    maxHgb = BloodMem.WOMAN_BLOOD_MAX;
                    minHgb = BloodMem.WOMAN_BLOOD_MIN;
                    maxHct = BloodMem.WOMAN_HOGIN_MAX;
                    minHct = BloodMem.WOMAN_HOGIN_MIN;
                } else {
                    //男
                    maxHgb = BloodMem.MAN_BLOOD_MAX;
                    minHgb = BloodMem.MAN_BLOOD_MIN;
                    maxHct = BloodMem.MAN_HOGIN_MAX;
                    minHct = BloodMem.MAN_HOGIN_MIN;
                }
                //血红蛋白
                setDoubleMeasureValue(maxHgb, minHgb, maxHct, minHct
                        , GlobalConstant.BLOOD_HGB_VALUE, GlobalConstant.BLOOD_HCT_VALUE
                        , mViewHolder.tvValue, mViewHolder.tvValueOther, MeasureType.Hgb);
            }
        } else {
            mViewHolder.tvValueOther.setVisibility(View.GONE);
            mViewHolder.tvValue.setText(bean.getMeasureValue());
            if (bean.isNormal()) {
                mViewHolder.tvValue.setTextColor(mContext.getResources()
                        .getColor(R.color.user_add_title_color));
            } else {
                mViewHolder.tvValue.setTextColor(mContext.getResources()
                        .getColor(R.color.measure_unnormal_color));
            }
        }
        return convertView;
    }

    /**
     * 单独方法显示两项的血氧、血压、血红蛋白值
     * @param max1 测量项1的最大值
     * @param max2 测量项2的最大值
     * @param min1 测量项1的最小值
     * @param min2 测量项2的最小值
     * @param value1 测量项1的测量值
     * @param value2 测量项2的测量值
     * @param tv1 测量项1的显示控件
     * @param tv2 测量项2的显示控件
     * @param type 测量项标识
     */
    private void setDoubleMeasureValue(float max1, float min1, float max2, float min2
            , float value1, float value2, TextView tv1, TextView tv2, MeasureType type) {
        if (value1 != GlobalConstant.INVALID_DATA) {
            if (type == MeasureType.Spo || type == MeasureType.Nibp) {
                tv1.setText((int) value1 + "");
                tv2.setText("/" + (int) value2);
            } else if (type == MeasureType.Hgb) {
                tv1.setText(value1 + "");
                tv2.setText("/" + (int) value2);
            }
            tv2.setVisibility(View.VISIBLE);
            boolean isOneNormal = MeasureValueCompareUtil.isValueNormal(max1, min1, value1);
            boolean isTwoNormal = MeasureValueCompareUtil.isValueNormal(max2, min2, value2);
            if (isOneNormal) {
                tv1.setTextColor(mContext.getResources()
                        .getColor(R.color.user_add_title_color));
            } else {
                tv1.setTextColor(mContext.getResources()
                        .getColor(R.color.measure_unnormal_color));
            }
            if (isTwoNormal) {
                tv2.setTextColor(mContext.getResources()
                        .getColor(R.color.user_add_title_color));
            } else {
                tv2.setTextColor(mContext.getResources()
                        .getColor(R.color.measure_unnormal_color));
            }
        } else {
            tv2.setVisibility(View.GONE);
            tv1.setText(mContext.getString(R.string.unmeasure));
            tv1.setTextColor(mContext.getResources()
                    .getColor(R.color.user_add_title_color));
        }
    }

    /**
     * 赋值数据
     * @param listData list数组
     */
    public void setData(List<AppMeasureBean> listData) {
        list = listData;
        notifyDataSetChanged();
    }

    /**
     * view对象持有
     */
    class ViewHolder {
        @InjectView(R.id.text)
        TextView tvName;
        @InjectView(R.id.text_value)
        TextView tvValue;
        @InjectView(R.id.text_value_other)
        TextView tvValueOther;
        @InjectView(R.id.img_check_item)
        ImageView img;
        @InjectView(R.id.right_iv)
        ImageView rightArrow;
    }

    /**
     * 记录当前测量项位置
     */
    public int recordCurrentMeasurePosition(int itemPosition) {
        String measureName = list.get(itemPosition).getMeasureName();
        int position = 0;
        switch (measureName) {
            //血氧
            case AppFragment.SPO_FLAG:
                position = 0;
                break;
            //血压
            case AppFragment.BF_FLAG:
                position = 1;
                break;
            //体温
            case AppFragment.TEMP_FLAG:
                position = 2;
                break;
            //血液三项
            case AppFragment.BT_FLAG:
                position = 3;
                break;
            //尿常规
            case AppFragment.URINT_FLAG:
                int type = SpUtils.getSpInt(mContext, GlobalConstant.APP_CONFIG
                        , GlobalConstant.URINETYPE, UrineType.ELEVEN);
                if (type == UrineType.ELEVEN) {
                    position = 4;
                } else {
                    position = 5;
                }
                break;
            //血红蛋白
            case AppFragment.HGB_FLAG:
                position = 6;
                break;
            //血脂
            case AppFragment.LIPD_FLAG:
                position = 7;
                break;
            //糖化血红蛋白
            case AppFragment.BHD_FLAG:
                position = 8;
                break;
            //BMI
            case AppFragment.BMI_FLAG:
                position = 9;
                break;
            //白细胞
            case AppFragment.WC_FLAG:
                position = 10;
                break;
            //心电
            case AppFragment.ECG_FLAG:
                position = 11;
                break;
            default:
                break;
        }
        return position;
    }

    /**
     * 枚举
     */
    private enum MeasureType {
        Spo, Nibp, Hgb
    }
}
