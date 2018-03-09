package com.konusng.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.activity.ReportListActivity;
import com.konsung.bean.MeasureDataBean;
import com.konsung.defineview.ChangeableLinearLayout;
import com.konsung.upload.UploadData;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.ReferenceUtils;
import com.konsung.util.ReferenceValueException;
import com.konsung.util.UiUitls;
import com.konsung.util.global.BmiParam;
import com.konsung.util.global.GlobalNumber;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 体检报告列表适配器
 * Created by DJH on 2017/10/9 0009.
 */
public class ReportListAdapter extends BaseAdapter {
    private static final int ITEM_NO_MEASURE = 1; //未测
    private static final int ITEM_NORMAL = 2; //正常
    private static final int ITEM_ABNORMAL = 3; //异常
    private Context context;
    private List<MeasureDataBean> measureList;
    private List<MeasureDataBean> selectedList = new ArrayList<>();
    private static final int TIME = 300; //延时时间
    private final int delayTime = 2 * 60 * 1000;

    /**
     * 构造方法
     * @param context 上下文
     * @param list 测量bean集合
     */
    public ReportListAdapter(Context context, List<MeasureDataBean> list) {
        this.context = context;
        this.measureList = list;
    }

    @Override
    public int getCount() {
        if (null != measureList) {
            return measureList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != measureList) {
            return measureList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            convertView = View.inflate(context, R.layout.list_item_for_report, null);
            holder = new ViewHolder();
            ButterKnife.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final MeasureDataBean bean = measureList.get(position);
        if (position < GlobalNumber.TEN_NUMBER) {
            String serial = "0" + String.valueOf(position + 1);
            holder.tvSerial.setText(serial);
        } else {
            holder.tvSerial.setText(String.valueOf(position + 1));
        }
        if (bean.getUploadFlag()) {
            holder.ivUpload.setImageResource(R.drawable.ic_report_upload_dis);
            holder.tvUpload.setText(UiUitls.getString(R.string.uploaded));
            holder.ivUpload.setClickable(false);
        } else {
            holder.tvUpload.setText(UiUitls.getString(R.string.unUpload));
            holder.ivUpload.setClickable(true);
            holder.ivUpload.setImageResource(R.drawable.report_upload_selector);
        }
        String measureTime = UiUitls.getString(R.string.measure_time) + UiUitls
                .getDateFormat(UiUitls.DateState.NOSECOND).format(bean.getMeasureTime());
        holder.tvMeasureTime.setText(measureTime);
        //点击上传按钮
        holder.ivUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果该页面处于删除状态不能点击上传
                if (GlobalConstant.isDeleteState) {
                    return;
                }
                if (bean.getUploadFlag()) {
                    UiUitls.toast(context, UiUitls.getString(R.string.data_has_uploaded));
                    return;
                }
                UiUitls.showProgress(context, UiUitls.getString(R.string.isuploading));
                UiUitls.postDelayed(runnable, delayTime);
                new UploadData(context).uploadMeasureData(bean, new UploadData.ResponseCallBack() {
                    @Override
                    public void onSuccess(String s) {
                        UiUitls.hideProgress();
                        UiUitls.removeCallbacks(runnable);
                        if (s.equals("10000")) {
                            UiUitls.toast(context, UiUitls.getString(R.string.updata_sussess));
                            bean.setUploadFlag(true);
                            DBDataUtil.getMeasureDao().update(bean);
                            UiUitls.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    notifyDataSetChanged();
                                }
                            }, TIME);
                        } else {
                            UiUitls.toast(context, UiUitls.getString(R.string.data_upload_fail));
                        }
                    }

                    @Override
                    public void onFailure(String message) {
                        UiUitls.hideProgress();
                        UiUitls.removeCallbacks(runnable);
                        UiUitls.toast(context, UiUitls.getString(R.string.data_upload_fail));
                    }

                    @Override
                    public void onException() {
                        UiUitls.hideProgress();
                        UiUitls.removeCallbacks(runnable);
                        UiUitls.toast(context, UiUitls.getString(R.string.data_upload_fail));
                    }
                });
            }
        });
        Map<String, Integer> map = new LinkedHashMap<>();
        int paramValue = bean.getParamValue();
        if ((paramValue & (0x01)) != 0) {
            map.put(UiUitls.getString(R.string.ecg), getStatus(bean, KParamType.ECG_HR, KParamType
                    .ECG_HR));
        }
        if ((paramValue & (0x01 << 1)) != 0) {
            map.put(UiUitls.getString(R.string.spo2), getStatus(bean, KParamType.SPO2_TREND,
                    KParamType.SPO2_TREND, KParamType.SPO2_PR));
        }
        if ((paramValue & (0x01 << 2)) != 0) {
            map.put(UiUitls.getString(R.string.p_xueya), getStatus(bean, KParamType.NIBP_SYS,
                    KParamType.NIBP_SYS, KParamType.NIBP_DIA, KParamType.NIBP_PR));
        }
        if ((paramValue & (0x01 << 3)) != 0) {
            map.put(UiUitls.getString(R.string.p_temperature), Math.max(getStatus(bean, KParamType
                    .IRTEMP_TREND, KParamType.IRTEMP_TREND), getStatus(bean, KParamType.TEMP_T1,
                    KParamType.TEMP_T1)));
        }
        if ((paramValue & (0x01 << 4)) != 0) {
            map.put(UiUitls.getString(R.string.blood_three), getMax(getStatus(bean, KParamType
                    .BLOODGLU_BEFORE_MEAL, KParamType.BLOODGLU_BEFORE_MEAL), getStatus(bean,
                    KParamType.BLOODGLU_AFTER_MEAL, KParamType.BLOODGLU_AFTER_MEAL), getStatus(bean
                    , KParamType.URICACID_TREND, KParamType.URICACID_TREND), getStatus(bean,
                    KParamType.CHOLESTEROL_TREND, KParamType.CHOLESTEROL_TREND)));
        }
        if ((paramValue & (0x01 << 5)) != 0) {
            map.put(UiUitls.getString(R.string.p_ur), getStatus(bean, KParamType.URINERT_LEU,
                    KParamType.URINERT_LEU, KParamType.URINERT_NIT, KParamType.URINERT_UBG,
                    KParamType.URINERT_PRO, KParamType.URINERT_PH, KParamType.URINERT_SG,
                    KParamType.URINERT_BLD, KParamType.URINERT_KET, KParamType.URINERT_BIL,
                    KParamType.URINERT_GLU, KParamType.URINERT_VC));
        }
        if ((paramValue & (0x01 << 6)) != 0) {
            map.put(UiUitls.getString(R.string.p_ur), getStatus(bean, KParamType.URINERT_LEU,
                    KParamType.URINERT_LEU, KParamType.URINERT_NIT, KParamType.URINERT_UBG,
                    KParamType.URINERT_PRO, KParamType.URINERT_PH, KParamType.URINERT_SG,
                    KParamType.URINERT_BLD, KParamType.URINERT_KET, KParamType.URINERT_BIL,
                    KParamType.URINERT_GLU, KParamType.URINERT_VC, KParamType.URINERT_ALB,
                    KParamType.URINERT_CRE, KParamType.URINERT_CA));
        }
        if ((paramValue & (0x01 << 7)) != 0) {
            map.put(UiUitls.getString(R.string.hemoglobin), getStatus(bean, KParamType.BLOOD_HGB,
                    KParamType.BLOOD_HGB, KParamType.BLOOD_HCT));
        }
        if ((paramValue & (0x01 << 8)) != 0) {
            map.put(UiUitls.getString(R.string.param_blood_4), getStatus(bean, KParamType
                    .LIPIDS_CHOL, KParamType.LIPIDS_CHOL, KParamType.LIPIDS_TRIG, KParamType
                    .LIPIDS_HDL, KParamType.LIPIDS_LDL));
        }
        if ((paramValue & (0x01 << 9)) != 0) {
            map.put(UiUitls.getString(R.string.suagr_bhd), getStatus(bean, KParamType.HBA1C_NGSP,
                    KParamType.HBA1C_NGSP, KParamType.HBA1C_IFCC, KParamType.HBA1C_EAG));
        }
        if ((paramValue & (0x01 << 10)) != 0) {
            map.put(UiUitls.getString(R.string.param_bmi), getBmiStatus(bean));
        }
        if ((paramValue & (0x01 << 11)) != 0) {
            map.put(UiUitls.getString(R.string.param_white), getStatus(bean, KParamType.BLOOD_WBC,
                    KParamType.BLOOD_WBC));
        }
        holder.llContainer.setContent(map);
        if (selectedList.contains(bean)) {
            holder.rlSelect.setVisibility(View.VISIBLE);
        } else {
            holder.rlSelect.setVisibility(View.GONE);
        }
        return convertView;
    }

    /**
     * 获取Bmi结果状态
     * @param bean 测量bean
     * @return 测量结果状态
     */
    private int getBmiStatus(MeasureDataBean bean) {
        if (TextUtils.isEmpty(bean.getBmi())) {
            return ITEM_NO_MEASURE;
        } else {
            float bmi = Float.parseFloat(bean.getBmi());
            if (bmi >= BmiParam.MIN_VALUE && bmi <= BmiParam.MAX_VALUE) {
                return ITEM_NORMAL;
            } else {
                return ITEM_ABNORMAL;
            }
        }
    }

    /**
     * 获取最大值
     * @param status 比较参数
     * @return 最大值
     */
    private int getMax(int... status) {
        int max = 0;
        for (int item : status) {
            max = Math.max(max, item);
        }
        return max;
    }

    /**
     * 获取测量项状态
     * @param bean 测量Bean
     * @param mainParam 判断是否测量的key值
     * @param params 测量项包含的key值
     * @return 结果状态（1：未测、2：正常、3：异常）
     */
    private int getStatus(MeasureDataBean bean, int mainParam, int... params) {
        if (bean.getTrendValue(mainParam) == GlobalConstant.INVALID_DATA) {
            return ITEM_NO_MEASURE;
        } else {
            if (checkAbnormal(bean, params)) {
                return ITEM_ABNORMAL;
            } else {
                return ITEM_NORMAL;
            }
        }
    }

    /**
     * 检查测量结果是否异常
     * @param bean 测量bean
     * @param params 需要判断的key值
     * @return 是否异常，true代表异常
     */
    private boolean checkAbnormal(MeasureDataBean bean, int... params) {
        boolean abnormal = false;
        for (int param : params) {
            try {
                int result = ReferenceUtils.compareWithReference(param, bean.getTrendValue(param));
                if (result == ReferenceUtils.FLAG_VALUE_ABOVE || result == ReferenceUtils
                        .FLAG_VALUE_BELOW) {
                    abnormal = true;
                    break;
                }
            } catch (ReferenceValueException e) {
                e.printStackTrace();
            }
        }
        return abnormal;
    }

    /**
     * 传递选中的item
     * @param bean 条目
     */
    public void sendPosition(MeasureDataBean bean) {
        if (selectedList.contains(bean)) {
            selectedList.remove(bean);
            //如果没有被标记的删除选项了则还原页面
            if (isNullSelect()) {
                ((ReportListActivity) context).restoreSelectAllState();
            }
        } else {
            selectedList.add(bean);
        }
        notifyDataSetChanged();
    }

    /**
     * 判断选择删除的标记是否为空
     * @return 是否选择为空
     */
    public boolean isNullSelect() {
        boolean isNullSelect = false;
        if (selectedList.size() <= 0) {
            isNullSelect = true;
        }
        return isNullSelect;
    }

    /**
     * 清除所有标记状态
     */
    public void clearState() {
        selectedList.clear();
        notifyDataSetChanged();
    }

    /**
     * 全选
     */
    public void selectAll() {
        if (measureList != null && measureList.size() > 0) {
            selectedList.clear();
            selectedList.addAll(measureList);
            notifyDataSetChanged();
        }
    }

    /**
     * 获取选中的item集合
     * @return 选中的条目position集合
     */
    public List<MeasureDataBean> getSelectList() {
        return selectedList;
    }

    /**
     * 更新数据源
     * @param list 测量bean集合
     */
    public void refreshData(List<MeasureDataBean> list) {
        measureList = list;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            UiUitls.hideProgress();
            UiUitls.toast(context, context.getString(R.string.upload_again));
        }
    };

    /**
     * viewHolder类
     */
    class ViewHolder {
        @InjectView(R.id.iv_upload)
        ImageView ivUpload;
        @InjectView(R.id.tv_upload)
        TextView tvUpload;
        @InjectView(R.id.tv_serial)
        TextView tvSerial;
        @InjectView(R.id.tv_measure_time)
        TextView tvMeasureTime;
        @InjectView(R.id.ll_container)
        ChangeableLinearLayout llContainer;
        @InjectView(R.id.rl_select)
        RelativeLayout rlSelect;
    }
}
