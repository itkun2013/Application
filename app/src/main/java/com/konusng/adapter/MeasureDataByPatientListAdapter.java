package com.konusng.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.defineview.ImageTextButton;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 根据用户姓名查看ECG数据页面的适配器
 *
 * @author ouyangfan
 * @version 0.0.1
 * @date 2015-02-05 15:10
 */
public class MeasureDataByPatientListAdapter extends BaseAdapter {

    // 布局填充器
    private LayoutInflater mLayoutInflater = null;
    private List<MeasureDataBean> mMeasureDataBeans;
    private Handler mHandler;

    public MeasureDataByPatientListAdapter(Context context, List<MeasureDataBean> dataBeanList,Handler mHandler) {
        mLayoutInflater = LayoutInflater.from(context);
        mMeasureDataBeans = dataBeanList;
        this.mHandler=mHandler;
    }

    @Override
    public int getCount() {
        return mMeasureDataBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return mMeasureDataBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = null;
        if (convertView == null) {
            mViewHolder = new ViewHolder();

//            convertView = mLayoutInflater.inflate(R.layout.measure_data_by_patient_list_item, null);
            //14项尿常规
            convertView = mLayoutInflater.inflate(R.layout.measure_data_by_patient_list_item1, null);
            ButterKnife.inject(mViewHolder, convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        // 设置数据
        MeasureDataBean dataBean = mMeasureDataBeans.get(position);

        String ecghr = (dataBean.getTrendValue(KParamType.ECG_HR) == -1000) ? "-?-" :
                String.valueOf(dataBean.getTrendValue(KParamType.ECG_HR) / GlobalConstant.TREND_FACTOR);
        String ecgbr = (dataBean.getTrendValue(KParamType.RESP_RR) == -1000) ? "-?-" :
                String.valueOf(dataBean.getTrendValue(KParamType.RESP_RR) / GlobalConstant.TREND_FACTOR);
        String ecgDiagnoseResult = (dataBean.getEcgDiagnoseResult().length() == 0) ? "-?-" :
                dataBean.getEcgDiagnoseResult();
        String spo2Trend = (dataBean.getTrendValue(KParamType.SPO2_TREND) == -1000) ? "-?-" :
                String.valueOf(dataBean.getTrendValue(KParamType.SPO2_TREND) / GlobalConstant.TREND_FACTOR);
        String spo2Pr = (dataBean.getTrendValue(KParamType.SPO2_PR) == -1000) ? "-?-" :
                String.valueOf(dataBean.getTrendValue(KParamType.SPO2_PR) / GlobalConstant.TREND_FACTOR);
        String nibpSys = (dataBean.getTrendValue(KParamType.NIBP_SYS) == -1000) ? "-?-" :
                String.valueOf(dataBean.getTrendValue(KParamType.NIBP_SYS) / GlobalConstant.TREND_FACTOR);
        String nibpDia = (dataBean.getTrendValue(KParamType.NIBP_DIA) == -1000) ? "-?-" :
                String.valueOf(dataBean.getTrendValue(KParamType.NIBP_DIA) / GlobalConstant.TREND_FACTOR);
        String nibpMap = (dataBean.getTrendValue(KParamType.NIBP_MAP) == -1000) ? "-?-" :
                String.valueOf(dataBean.getTrendValue(KParamType.NIBP_MAP) / GlobalConstant.TREND_FACTOR);
        String uri_sg = (dataBean.getTrendValue(KParamType.URINERT_SG) == -1000) ? "-?-" :
                String.format("%.3f", (double) dataBean.getTrendValue(KParamType.URINERT_SG) / 1000.0);
        String uri_leu = (dataBean.getTrendValue(KParamType.URINERT_LEU) == -1000) ? "-?-" :
                valueToString(dataBean.getTrendValue(KParamType.URINERT_LEU) / GlobalConstant.URITREND_FACTOR);
        String uri_nit = (dataBean.getTrendValue(KParamType.URINERT_NIT) == -1000) ? "-?-" :
                valueToString(dataBean.getTrendValue(KParamType.URINERT_NIT) / GlobalConstant.URITREND_FACTOR);
        String uri_ubg = (dataBean.getTrendValue(KParamType.URINERT_UBG) == -1000) ? "-?-" :
                valueToString(dataBean.getTrendValue(KParamType.URINERT_UBG) / GlobalConstant.URITREND_FACTOR);
        String uri_ph = (dataBean.getTrendValue(KParamType.URINERT_PH) == -1000) ? "-?-" :
                String.valueOf((float) dataBean.getTrendValue(KParamType.URINERT_PH) / 100.0f);
        String uri_bld = (dataBean.getTrendValue(KParamType.URINERT_BLD) == -1000) ? "-?-" :
                valueToString(dataBean.getTrendValue(KParamType.URINERT_BLD) / GlobalConstant.URITREND_FACTOR);
        String uri_glu = (dataBean.getTrendValue(KParamType.URINERT_GLU) == -1000) ? "-?-" :
                valueToString(dataBean.getTrendValue(KParamType.URINERT_GLU) / GlobalConstant.URITREND_FACTOR);
        String uri_asc = (dataBean.getTrendValue(KParamType.URINERT_ASC) == -1000) ? "-?-" :
                valueToString(dataBean.getTrendValue(KParamType.URINERT_ASC) / GlobalConstant.URITREND_FACTOR);
        String uri_ket = (dataBean.getTrendValue(KParamType.URINERT_KET) == -1000) ? "-?-" :
                valueToString(dataBean.getTrendValue(KParamType.URINERT_KET) / GlobalConstant.URITREND_FACTOR);
        String uri_bil = (dataBean.getTrendValue(KParamType.URINERT_BIL) == -1000) ? "-?-" :
                valueToString(dataBean.getTrendValue(KParamType.URINERT_BIL) / GlobalConstant.URITREND_FACTOR);
        String uri_alb = (dataBean.getTrendValue(KParamType.URINERT_ALB) == -1000) ? "-?-" :
                String.valueOf(dataBean.getTrendValue(KParamType.URINERT_ALB) / GlobalConstant.URITREND_FACTOR);
        String uri_cre = (dataBean.getTrendValue(KParamType.URINERT_CRE) == -1000) ? "-?-" :
                String.format("%.1f", (float) dataBean.getTrendValue(KParamType.URINERT_CRE) / 100.0f);
        String uri_ca = (dataBean.getTrendValue(KParamType.URINERT_CA) == -1000) ? "-?-" :
                String.valueOf((float) dataBean.getTrendValue(KParamType.URINERT_CA) / GlobalConstant.URITREND_FACTOR);
        String uri_pro = (dataBean.getTrendValue(KParamType.URINERT_PRO) == -1000) ? "-?-" :
                valueToString(dataBean.getTrendValue(KParamType.URINERT_PRO) / GlobalConstant.URITREND_FACTOR);
        String blood_glu = (dataBean.getTrendValue(KParamType.BLOODGLU_BEFORE_MEAL) == -1000) ? "-?-" :
                String.valueOf((float) dataBean.getTrendValue(KParamType.BLOODGLU_BEFORE_MEAL) / GlobalConstant.TREND_FACTOR);
        String blood_wbc = (dataBean.getTrendValue(KParamType.BLOOD_WBC) == -1000) ? "-?-" :
                String.valueOf((float) dataBean.getTrendValue(KParamType.BLOOD_WBC) / GlobalConstant.TREND_FACTOR);
        String blood_hgb = (dataBean.getTrendValue(KParamType.BLOOD_HGB) == -1000) ? "-?-" :
                String.valueOf((float) dataBean.getTrendValue(KParamType.BLOOD_HGB) / GlobalConstant.TREND_FACTOR);
        String blood_hct = (dataBean.getTrendValue(KParamType.BLOOD_HCT) == -1000) ? "-?-" :
                String.valueOf(dataBean.getTrendValue(KParamType.BLOOD_HCT) / GlobalConstant.TREND_FACTOR);
        String uric_acid = (dataBean.getTrendValue(KParamType.URICACID_TREND) == -1000) ? "-?-" :
                String.valueOf((float) dataBean.getTrendValue(KParamType.URICACID_TREND) / GlobalConstant.TREND_FACTOR);
        String cholesterol = (dataBean.getTrendValue(KParamType.CHOLESTEROL_TREND) == -1000) ? "-?-" :
                String.valueOf((float) dataBean.getTrendValue(KParamType.CHOLESTEROL_TREND) / GlobalConstant.TREND_FACTOR);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String measureTime = dateFormat.format(dataBean.getMeasureTime());

        //暂时通过此方式判断是
        String temp = "-?-";
        if(dataBean.getTrendValue(KParamType.TEMP_T1) == -1000) {
            temp = (dataBean.getTrendValue(KParamType.IRTEMP_TREND) == -1000) ? "-?-" :
                    String.valueOf(dataBean.getTrendValue(KParamType.IRTEMP_TREND) / 100f);
        } else {
            temp = (dataBean.getTrendValue(KParamType.TEMP_T1) == -1000) ? "-?-" :
                    String.valueOf(dataBean.getTrendValue(KParamType.TEMP_T1) / 100f);
        }

        mViewHolder.ecgHr.setText(ecghr);
        mViewHolder.ecgBr.setText(ecgbr);
        if (!"-?-".equals(ecgDiagnoseResult)) {
            String[] result = ecgDiagnoseResult.split(",");
            if (result.length >= 11) {
                String hrValue = result[0];// HR值
                String prInterval = result[1];// PR间期
                String qrsDuration = result[2];// QRS间期, 单位ms
                String qt = result[3];// QT间期
                String qtc = result[4];// QTC间期
                String pAxis = result[5];// P 波轴
                String qrsAxis = result[6];// QRS波心电轴
                String tAxis = result[7];// T波心电轴
                String rv5 = result[8];// RV5, 单位0.01ms
                String sv1 = result[9];// SV1, 单位0.01ms
                String rv5_Sv1 = result[10];// SV1+RV5, 单位0.01ms
                mViewHolder.qtQtcInterval.setText(qt + "/" + qtc + " " +"ms");
                mViewHolder.prInterval.setText(prInterval + " " + "ms");
                mViewHolder.pQrsT.setText(pAxis + "/" + qrsAxis + "/" + tAxis + " " + "°");
                mViewHolder.qrsInterval.setText(qrsDuration + " " + "ms");
                mViewHolder.rv5Sv1.setText(rv5 + "/" + sv1 + " " + "mV");
                mViewHolder.rv5_Sv1.setText(rv5_Sv1 + " " + "mV");
//                float rv5_sv1 = Float.parseFloat(rv5) + Float.parseFloat(sv1);
//                mViewHolder.rv5_Sv1.setText(String.valueOf(rv5_sv1) + " " + "mV");
//                mViewHolder.rv5_Sv1.setText(String.format("%0.2f", rv5_sv1) + " " + "mV");
                if (result.length >= 12) {
                    mViewHolder.ecgDiagnoseResult.setText(result[11]);
                }
            } else {
                mViewHolder.ecgDiagnoseResult.setText("-?-");
                mViewHolder.qtQtcInterval.setText("-?-");
                mViewHolder.prInterval.setText("-?-");
                mViewHolder.pQrsT.setText("-?-");
                mViewHolder.qrsInterval.setText("-?-");
                mViewHolder.rv5Sv1.setText("-?-");
                mViewHolder.rv5_Sv1.setText("-?-");
            }
        } else {
            mViewHolder.ecgDiagnoseResult.setText("-?-");
            mViewHolder.qtQtcInterval.setText("-?-");
            mViewHolder.prInterval.setText("-?-");
            mViewHolder.pQrsT.setText("-?-");
            mViewHolder.qrsInterval.setText("-?-");
            mViewHolder.rv5Sv1.setText("-?-");
            mViewHolder.rv5_Sv1.setText("-?-");
        }
        mViewHolder.spo2Trend.setText(spo2Trend);
        mViewHolder.spo2Pr.setText(spo2Pr);
        mViewHolder.nibp.setText(nibpSys + "/" + nibpDia + "(" + nibpMap + ")");
        mViewHolder.measureTime.setText(measureTime);
        mViewHolder.temp.setText(temp);
        mViewHolder.uri_sg.setText(uri_sg);
        mViewHolder.uri_pro.setText(uri_pro);
        mViewHolder.blood_glu.setText(blood_glu);
        mViewHolder.uri_leu.setText(uri_leu);
        mViewHolder.uri_bld.setText(uri_bld);
        mViewHolder.uri_nit.setText(uri_nit);
        mViewHolder.uri_ket.setText(uri_ket);
        mViewHolder.uri_ubg.setText(uri_ubg);
        mViewHolder.uri_bil.setText(uri_bil);
        mViewHolder.uri_glu.setText(uri_glu);
        mViewHolder.uri_ph.setText(uri_ph);
        mViewHolder.uri_asc.setText(uri_asc);
        mViewHolder.uri_alb.setText(uri_alb);
        mViewHolder.uri_cre.setText(uri_cre);
        mViewHolder.uri_ca.setText(uri_ca);
        mViewHolder.blood_wbc.setText(blood_wbc);
        mViewHolder.blood_hgb.setText(blood_hgb);
        mViewHolder.blood_hct.setText(blood_hct);
        mViewHolder.uric_acid.setText(uric_acid);
        mViewHolder.cholesterol.setText(cholesterol);
        mViewHolder.check_health_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Message msg = Message.obtain();
                msg.obj = mMeasureDataBeans.get(position);
                msg.arg1 = 1;
                mHandler.sendMessage(msg);
            }
        });
        mViewHolder.check_wave_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg=Message.obtain();
                msg.obj=mMeasureDataBeans.get(position);
                msg.arg1=2;
                mHandler.sendMessage(msg);
            }
        });
        mViewHolder.print_report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg=Message.obtain();
                msg.obj=mMeasureDataBeans.get(position);
                msg.arg1=3;
                mHandler.sendMessage(msg);
            }
        });
        mViewHolder.print_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message msg=Message.obtain();
                msg.obj=mMeasureDataBeans.get(position);
                msg.arg1=4;
                mHandler.sendMessage(msg);
            }
        });

        return convertView;
    }


    /*
     * ViewHolder 装载器
     */
    public final class ViewHolder {
        @InjectView(R.id.ecg_hr)
        TextView ecgHr;
        @InjectView(R.id.ecg_br)
        TextView ecgBr;
        @InjectView(R.id.qt_qtc_interval)
        TextView qtQtcInterval;
        @InjectView(R.id.pr_interval)
        TextView prInterval;
        @InjectView(R.id.p_qrs_t)
        TextView pQrsT;
        @InjectView(R.id.qrs_interval)
        TextView qrsInterval;
        @InjectView(R.id.rv5_sv1)
        TextView rv5Sv1;
        @InjectView(R.id.rv5__sv1)
        TextView rv5_Sv1;
        @InjectView(R.id.ecg_diagnose_result)
        TextView ecgDiagnoseResult;
        @InjectView(R.id.spo2_trend)
        TextView spo2Trend;
        @InjectView(R.id.spo2_pr)
        TextView spo2Pr;
        @InjectView(R.id.nibp_value)
        TextView nibp;
        @InjectView(R.id.measure_time)
        TextView measureTime;
        @InjectView(R.id.temp_value)
        TextView temp;
        @InjectView(R.id.blood_glu_value)
        TextView blood_glu;
        @InjectView(R.id.urinert_sg_value)
        TextView uri_sg;
        @InjectView(R.id.urinert_pro_value)
        TextView uri_pro;
        @InjectView(R.id.urinert_leu_value)
        TextView uri_leu;
        @InjectView(R.id.urinert_bld_value)
        TextView uri_bld;
        @InjectView(R.id.urinert_nit_value)
        TextView uri_nit;
        @InjectView(R.id.urinert_ket_value)
        TextView uri_ket;
        @InjectView(R.id.urinert_ubg_value)
        TextView uri_ubg;
        @InjectView(R.id.urinert_bil_value)
        TextView uri_bil;
        @InjectView(R.id.urinert_glu_value)
        TextView uri_glu;
        @InjectView(R.id.urinert_ph_value)
        TextView uri_ph;
        @InjectView(R.id.urinert_asc_value)
        TextView uri_asc;
        @InjectView(R.id.urinert_alb_value)
        TextView uri_alb;
        @InjectView(R.id.urinert_cre_value)
        TextView uri_cre;
        @InjectView(R.id.urinert_ca_value)
        TextView uri_ca;
        @InjectView(R.id.blood_wbc_value)
        TextView blood_wbc;
        @InjectView(R.id.blood_hgb_value)
        TextView blood_hgb;
        @InjectView(R.id.blood_hct_value)
        TextView blood_hct;
        @InjectView(R.id.uric_acid_value)
        TextView uric_acid;
        @InjectView(R.id.cholesterol_value)
        TextView cholesterol;
        @InjectView(R.id.check_health_btn)
        ImageTextButton check_health_btn;
        @InjectView(R.id.check_wave_btn)
        ImageTextButton check_wave_btn;
        @InjectView(R.id.print_report_btn)
        ImageTextButton print_report_btn;
        @InjectView(R.id.print_mode)
        ImageTextButton print_mode;
    }

    /**
     * 尿常规值转换
     *
     * @param value 模块传递过来的值
     * @return 显示测量值
     */
    private String valueToString(int value) {

        switch (value) {
            case -1:
                return "-";
            case 0:
                return "+-";
            case 1:
                return "+1";
            case 2:
                return "+2";
            case 3:
                return "+3";
            case 4:
                return "+4";
            case 5:
                return "+";
            case 6:
                return "Normal";
            default:
                return String.valueOf(value);
        }
    }
}
