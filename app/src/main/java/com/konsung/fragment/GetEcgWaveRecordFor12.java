package com.konsung.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.bean.Request.EcgDiagnoseApplyRequest;
import com.konsung.bean.TaioHeacheckData;
import com.konsung.defineview.CustomToast;
import com.konsung.upload.UploadData;
import com.konsung.util.EcgImage;
import com.konsung.util.GlobalConstant;
import com.konsung.util.KParamType;
import com.konsung.util.UiUitls;
import com.konsung.util.UnitConvertUtil;
import com.tencent.bugly.crashreport.CrashReport;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class GetEcgWaveRecordFor12 extends BaseInsertFragment {
    @InjectView(R.id.tv_name)
    TextView tvName;
    @InjectView(R.id.tv_sex)
    TextView tvSex;
    @InjectView(R.id.tv_age)
    TextView tvAge;
    @InjectView(R.id.tv_qrs)
    TextView tvQrs;
    @InjectView(R.id.tv_pr)
    TextView tvPr;
    @InjectView(R.id.tv_qt_qtc)
    TextView tvQtQtc;
    @InjectView(R.id.tv_p_qrs_t)
    TextView tvPQrsT;
    @InjectView(R.id.tv_rv5_sv1)
    TextView tvRv5Sv1;
    @InjectView(R.id.tv_rv5_plus_sv1)
    TextView tvRv5PlusSv1;
    @InjectView(R.id.tv_district)
    TextView tvDistrict;
    @InjectView(R.id.tv_hr)
    TextView tvHr;
    @InjectView(R.id.iv_heart_image)
    ImageView ivHeartImage;
    @InjectView(R.id.tv_ecg_diagnose_result)
    TextView tvEcgDiagnoseResult;

    @InjectView(R.id.apply_remote_ecg)
    TextView applyRemoteEcg;

    private String idcard;
    private String patientName;
    private MeasureDataBean measureDataBean;
    private PatientBean patientBean;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_get_ecg_wave_recordfor12, null);

        ButterKnife.inject(this, view);
        changeTiTle(UiUitls.getString(R.string.x_heart_pic_report));
        initData();
        initView();
        initListener();
        return view;
    }

    private void initListener() {
        applyRemoteEcg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EcgDiagnoseApplyRequest request = new EcgDiagnoseApplyRequest();
                request.dataId = measureDataBean.getUuid();
                request.doctorCode = GlobalConstant.EPMID ;
                request.requestMark = "";
                UiUitls.showProgress(getActivity(), "正在申请中...");
                new UploadData(getActivity()).requestEcgApply(request
                        , new UploadData.ResponseCallBack(){

                    @Override
                    public void onSuccess(String s) {
                        UiUitls.hideProgress();
                        CustomToast.showMessage(getActivity(), s);
                    }

                    @Override
                    public void onFailure(String message) {
                        UiUitls.hideProgress();
                    }

                    @Override
                    public void onException() {
                        UiUitls.hideProgress();
                    }
                });
            }
        });
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    /**
     * 加载心电图
     */
    private void loadHeartImage() {
        // 心电图宽度，单位为像素(1103恰好为17cm,刚好满足布局)
        final int ImageWidth = 1003;
        final int ImageHeight = 1920;// 心电图高度，单位为像素

        if (measureDataBean == null) {
            return;
        }
        TaioHeacheckData thd = new TaioHeacheckData();
        thd.setECG_I(Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measureDataBean.get_ecgWave(
                        KParamType.ECG_I)), Base64.NO_WRAP));
        thd.setECG_II(Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measureDataBean.get_ecgWave(
                        KParamType.ECG_II)), Base64.NO_WRAP));
        thd.setECG_III(Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measureDataBean.get_ecgWave(
                        KParamType.ECG_III)), Base64.NO_WRAP));
        thd.setECG_aVR(Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measureDataBean.get_ecgWave(
                        KParamType.ECG_AVR)), Base64.NO_WRAP));
        thd.setECG_aVF(Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measureDataBean.get_ecgWave(
                        KParamType.ECG_AVF)), Base64.NO_WRAP));
        thd.setECG_aVL(Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measureDataBean.get_ecgWave(
                        KParamType.ECG_AVL)), Base64.NO_WRAP));
        thd.setECG_V1(Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measureDataBean.get_ecgWave(
                        KParamType.ECG_V1)), Base64.NO_WRAP));
        thd.setECG_V2(Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measureDataBean.get_ecgWave(
                        KParamType.ECG_V2)), Base64.NO_WRAP));
        thd.setECG_V3(Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measureDataBean.get_ecgWave(
                        KParamType.ECG_V3)), Base64.NO_WRAP));
        thd.setECG_V4(Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measureDataBean.get_ecgWave(
                        KParamType.ECG_V4)), Base64.NO_WRAP));
        thd.setECG_V5(Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measureDataBean.get_ecgWave(
                        KParamType.ECG_V5)), Base64.NO_WRAP));
        thd.setECG_V6(Base64.encodeToString(UnitConvertUtil
                .getByteformHexString(measureDataBean.get_ecgWave(
                        KParamType.ECG_V6)), Base64.NO_WRAP));

        // 设置创建位图时初始化的颜色
        int[] colors = new int[ImageWidth * ImageHeight];
        for (int y = 0; y < ImageHeight; y++) {
            for (int x = 0; x < ImageWidth; x++) {
                colors[y * ImageWidth + x] = Color.WHITE;
            }
        }

        // 如果不用copy会报Immutable bitmap passed to Canvas constructor错误
        Bitmap bitmapEcg = Bitmap.createBitmap(colors, ImageWidth, ImageHeight,
                Bitmap.Config.RGB_565).copy(Bitmap.Config.RGB_565, true);
        Canvas canvas = new Canvas(bitmapEcg);

        try {
            EcgImage.drawImage(canvas, thd);
        } catch (Exception e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }

        ivHeartImage.setImageBitmap(bitmapEcg);
    }

    /**
     * 初始化病人信息数据和测量数据
     */
    private void initData() {
        GlobalConstant.bar.setDisplayHomeAsUpEnabled(true);
        Bundle bundle = getArguments();
        patientName = bundle.getString("patientName");
        idcard = bundle.getString("idcard");

        measureDataBean = beanMsg.getMeasureDataBean();

        patientBean = beanMsg.getPatientBean();
    }

    /**
     * 初始化病人的体检显示 各类测量数据显示
     */
    private void initView() {
        changeTiTle(getString(R.string.ecg_wave));

        if (measureDataBean == null || patientBean == null) {
            return;
        }

        //  基本资料信息
        // 屏蔽地区显示
//        String orgName = measureDataBean.getOrgName();
//        tvDistrict.setText("" + orgName);
        tvName.setText(patientName);
        tvAge.setText(patientBean.getAge() == -1 ? "" : (patientBean.getAge()
                + UiUitls.getString(R.string.unit_age)));
        int sex = patientBean.getSex();
        String sexStr = "未知";
        switch (sex) {
            case 0:
                sexStr = getString(R.string.sex_woman);
                break;
            case 1:
                sexStr = getString(R.string.sex_man);
                break;
            default:
                sexStr = getString(R.string.sex_woman);
                break;
        }
        tvSex.setText(sexStr);

        String ecgDiagnoseResult = measureDataBean.getEcgDiagnoseResult();
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

                tvQrs.setText(qrsDuration + " " + "ms");
                tvPr.setText(prInterval + " " + "ms");
                tvQtQtc.setText(qt + "/" + qtc + " " + "ms");
                tvPQrsT.setText(pAxis + "/" + qrsAxis + "/" + tAxis + " " + "°");
                tvRv5Sv1.setText(rv5 + "/" + sv1 + " " + "mV");
                tvRv5PlusSv1.setText(rv5_Sv1 + " " + "mV");
                tvHr.setText(hrValue + " " + "bpm");

                if (result.length >= 12) {
                    tvEcgDiagnoseResult.setText(result[11]);
                }
            }
        }

        loadHeartImage();
    }
}
