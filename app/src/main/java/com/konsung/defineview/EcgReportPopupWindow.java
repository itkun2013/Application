package com.konsung.defineview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.bean.Request.EcgDiagnoseApplyRequest;
import com.konsung.bean.Request.QueryEcgDiagnosesRequest;
import com.konsung.bean.Response.EcgQueryResponse;
import com.konsung.upload.UploadData;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.JsonUtils;
import com.konsung.util.KParamType;
import com.konsung.util.SpUtils;
import com.konsung.util.UiUitls;
import com.konsung.util.global.EcgRemoteInfoSaveModule;
import com.konsung.util.global.GlobalNumber;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 心电报告popupWindow
 * Created by DJH on 2017/7/18 0018.
 */
public class EcgReportPopupWindow extends PopupWindow implements View.OnClickListener {
    @InjectView(R.id.tv_report_name)
    TextView tvReportName;
    @InjectView(R.id.tv_report_sex)
    TextView tvReportSex;
    @InjectView(R.id.tv_report_age)
    TextView tvReportAge;
    @InjectView(R.id.tv_report_idcard)
    TextView tvReportIdcard;
    @InjectView(R.id.tv_report_phone)
    TextView tvReportPhone;
    @InjectView(R.id.tv_report_measure_time)
    TextView tvReportMeasureTime;
    @InjectView(R.id.btn_stop_report)
    Button btnStopReport;
    @InjectView(R.id.tv_report_hr)
    TextView tvReportHr;
    @InjectView(R.id.tv_report_pr)
    TextView tvReportPr;
    @InjectView(R.id.tv_report_qrs)
    TextView tvReportQrs;
    @InjectView(R.id.tv_report_qtqtc)
    TextView tvReportQtqtc;
    @InjectView(R.id.tv_report_pQrsT)
    TextView tvReportPQrsT;
    @InjectView(R.id.tv_report_rv5Sv1)
    TextView tvReportRv5Sv1;
    @InjectView(R.id.tv_report_rv5_plus_sv1)
    TextView tvReportRv5PlusSv1;
    @InjectView(R.id.tv_report_result)
    TextView tvReportResult;
    @InjectView(R.id.iv_ecg_image)
    ImageView ivEcgImage;
    @InjectView(R.id.btn_apply)
    Button btnApply;
    @InjectView(R.id.ll_apply_success)
    LinearLayout llApplySuccess;
    @InjectView(R.id.tv_apply_time)
    TextView tvApplyTime;
    @InjectView(R.id.ll_not_resolve)
    LinearLayout llNotResolve;
    @InjectView(R.id.ll_resolved)
    LinearLayout llResolved;
    @InjectView(R.id.tv_expert_hr)
    TextView tvExpertHr;
    @InjectView(R.id.tv_expert_pr)
    TextView tvExpertPr;
    @InjectView(R.id.tv_expert_qrs)
    TextView tvExpertQrs;
    @InjectView(R.id.tv_expert_qtqtc)
    TextView tvExpertQtqtc;
    @InjectView(R.id.tv_expert_pQrsT)
    TextView tvExpertPQrsT;
    @InjectView(R.id.tv_expert_rv5Sv1)
    TextView tvExpertRv5Sv1;
    @InjectView(R.id.tv_expert_rv5_plus_sv1)
    TextView tvExpertRv5PlusSv1;
    @InjectView(R.id.tv_remote_ecg_result)
    TextView tvRemoteEcgResult;
    @InjectView(R.id.tv_resolve_time)
    TextView tvResolveTime;
    @InjectView(R.id.tv_doctor_sign)
    TextView tvDoctorSign;
    @InjectView(R.id.ll_local_data)
    LinearLayout llLocalData;
    @InjectView(R.id.tv_no_data)
    TextView tvNoData;
    @InjectView(R.id.right_line)
    TextView tvRightLine;
    @InjectView(R.id.right_part)
    RelativeLayout rlRightPart;

    private PatientBean patientBean;
    private MeasureDataBean measureDataBean;
    private static final String REMOTE_ECG_NOT_RESOLVE = "0"; //远程心电未处理
    private static final String REMOTE_ECG_RESOLVED = "1"; //远程心电已处理状态
    private Activity context;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GlobalNumber.ONE_NUMBER: //申请成功
                    llNotResolve.setVisibility(View.VISIBLE);
                    llResolved.setVisibility(View.GONE);
                    btnApply.setVisibility(View.GONE);
                    llApplySuccess.setVisibility(View.VISIBLE);
                    tvApplyTime.setVisibility(View.VISIBLE);
                    tvApplyTime.setText(msg.obj.toString());
                    break;
                case GlobalNumber.TWO_NUMBER: //未申请
                    llNotResolve.setVisibility(View.VISIBLE);
                    llResolved.setVisibility(View.GONE);
                    btnApply.setVisibility(View.VISIBLE);
                    llApplySuccess.setVisibility(View.GONE);
                    tvApplyTime.setVisibility(View.GONE);
                    break;
                case GlobalNumber.THREE_NUMBER: //已申请
                    llNotResolve.setVisibility(View.VISIBLE);
                    llResolved.setVisibility(View.GONE);
                    btnApply.setVisibility(View.GONE);
                    llApplySuccess.setVisibility(View.VISIBLE);
                    tvApplyTime.setVisibility(View.VISIBLE);
                    tvApplyTime.setText(EcgRemoteInfoSaveModule.getInstance().rowData.requestDate);
                    break;
                case GlobalNumber.FOUR_NUMBER: //已处理
                    llNotResolve.setVisibility(View.GONE);
                    llResolved.setVisibility(View.VISIBLE);
                    getRemoteEcgResult();
                    break;
                default:
                    llNotResolve.setVisibility(View.GONE);
                    llResolved.setVisibility(View.GONE);
                    break;
            }
        }
    };

    /**
     * 接口
     */
    public interface OnPopWindowCloseListener {
        /**
         * popwindow关闭回调接口
         */
        public void popWindowClose();
    }
    private OnPopWindowCloseListener onPopWindowCloseListener;

    /**
     * 设置接口监听
     * @param onPopWindowCloseListener 接口
     */
    public void setOnPopWindowCloseListener(OnPopWindowCloseListener onPopWindowCloseListener) {
        this.onPopWindowCloseListener = onPopWindowCloseListener;
    }
    /**
     * 构造方法
     * @param activity activity
     * @param patientBean 居民信息
     * @param measureDataBean 测量信息
     * @param width 控件宽度
     * @param height 控件高度
     * @param hasData 取的本地数据还是网络返回数据
     * @param isDeleted 本地数据是否已删除
     */
    public EcgReportPopupWindow(Activity activity, PatientBean patientBean, MeasureDataBean
            measureDataBean, int width, int height, boolean hasData, boolean isDeleted) {
        super(width, height);
        this.context = activity;
        this.patientBean = patientBean;
        this.measureDataBean = measureDataBean;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pop_ecg_report, null);
        ButterKnife.inject(this, view);
        initView();
        initEvent();
        if (hasData) {
            if (EcgRemoteInfoSaveModule.getInstance().isFromEcgMeasure) {
                EcgRemoteInfoSaveModule.getInstance().isFromEcgMeasure = false;
                handler.sendEmptyMessage(2);
            } else {
                requestRemoteEcgInfo();
            }
            initReportPatientData();
        } else {
            refreshRemoteEcg();
        }
        if (isDeleted) {
            llLocalData.setVisibility(View.INVISIBLE);
            tvNoData.setVisibility(View.VISIBLE);
            tvReportMeasureTime.setVisibility(View.GONE);
            initReportEcgWave(false);
        } else {
            llLocalData.setVisibility(View.VISIBLE);
            tvNoData.setVisibility(View.INVISIBLE);
            tvReportMeasureTime.setVisibility(View.VISIBLE);
            initReportMeasureData();
            initReportEcgWave(true);
        }
        //设置PopupWindow的View
        this.setContentView(view);
        //设置PopupWindow焦点
        this.setFocusable(true);
    }

    /**
     * 页面初始化
     */
    private void initView() {
        if (EcgRemoteInfoSaveModule.getInstance().isFromEcgMeasure) {
//            EcgRemoteInfoSaveModule.getInstance().isFromEcgMeasure = false;
            tvRightLine.setVisibility(View.GONE);
            rlRightPart.setVisibility(View.GONE);
        } else {
            tvRightLine.setVisibility(View.VISIBLE);
            rlRightPart.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 初始化页面事件
     */
    private void initEvent() {
        btnStopReport.setOnClickListener(this);
        btnApply.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_stop_report:
                if (isShowing()) {
                    if (onPopWindowCloseListener != null) {
                        onPopWindowCloseListener.popWindowClose();
                    }
                    dismiss();
                }
                break;
            case R.id.btn_apply:
                applyRemoteEcg();
                break;
            default:
                break;
        }
    }

    /**
     * 初始化病人信息
     */
    private void initReportPatientData() {
        if (patientBean != null) {
            if (SpUtils.getSp(UiUitls.getContent(), GlobalConstant.APP_CONFIG,
                    GlobalConstant.CARD_INPUT, true)) {
                String age = patientBean.getAge() >= 0 ? patientBean.getAge() + UiUitls.getString(
                        R.string.age) : "";
                tvReportAge.setText(age);
            } else {
                tvReportAge.setText("");
            }
            tvReportName.setText(patientBean.getName());
            tvReportIdcard.setText(patientBean.getCard());
            tvReportPhone.setText(patientBean.getSelfmobile());
            tvReportSex.setText(UiUitls.getSexString(patientBean.getSex()));
        }
    }

    /**
     * 初始化测量数据
     */
    private void initReportMeasureData() {
        if (measureDataBean != null) {
            //对于低版本升级高版本的时候字段不统一的问题
            //该判断条件为判断为心电为老版本测量的心电
            if (measureDataBean.getEcgDiagnoseResult().contains(",")
                    && measureDataBean.getEcgDiagnoseResult().split(",").length
                    >= GlobalNumber.TWELVE_NUMBER) {
                String[] splitArray = measureDataBean.getEcgDiagnoseResult().split(",");
                measureDataBean.setPr(Integer.parseInt(splitArray[1]));
                measureDataBean.setQrs(Integer.parseInt(splitArray[2]));
                measureDataBean.setQt(Integer.parseInt(splitArray[3]));
                measureDataBean.setQtc(Integer.parseInt(splitArray[4]));
                measureDataBean.setpAxis(Integer.parseInt(splitArray[5]));
                measureDataBean.setQrsAxis(Integer.parseInt(splitArray[6]));
                measureDataBean.settAxis(Integer.parseInt(splitArray[7]));
                measureDataBean.setRv5(splitArray[8]);
                measureDataBean.setSv1(splitArray[9]);
                measureDataBean.setRv5PlusSv1(splitArray[10]);
                measureDataBean.setEcgDiagnoseResult(splitArray[11]);
                DBDataUtil.getMeasureDao().update(measureDataBean);
            }
            SimpleDateFormat dateFormat = UiUitls.getDateFormat(UiUitls.DateState.LONG);
            String measureTime = dateFormat.format(measureDataBean.getMeasureTime());
            tvReportMeasureTime.setText(UiUitls.getString(R.string.measure_time_colon)
                    + measureTime);
            String divide = UiUitls.getString(R.string.unit_divide);
            String ms = UiUitls.getString(R.string.unit_ms);
            String limit = UiUitls.getString(R.string.unit_limit);
            String mv = UiUitls.getString(R.string.unit_mv);
            String pr = measureDataBean.getPr() == GlobalConstant.INVALID_DATA ? UiUitls
                    .getString(R.string.default_value) : String.valueOf(measureDataBean.getPr());
            String qrs = measureDataBean.getQrs() == GlobalConstant.INVALID_DATA ? UiUitls
                    .getString(R.string.default_value) : String.valueOf(measureDataBean.getQrs());
            String qt = measureDataBean.getQt() == GlobalConstant.INVALID_DATA ? UiUitls
                    .getString(R.string.default_value) : String.valueOf(measureDataBean.getQt());
            String qtc = measureDataBean.getQtc() == GlobalConstant.INVALID_DATA ? UiUitls
                    .getString(R.string.default_value) : String.valueOf(measureDataBean.getQtc());
            String pAxis = measureDataBean.getpAxis() == GlobalConstant.INVALID_DATA ? UiUitls
                    .getString(R.string.default_value) : String.valueOf(measureDataBean.getpAxis());
            String qrsAxis = measureDataBean.getQrsAxis() == GlobalConstant.INVALID_DATA ? UiUitls
                    .getString(R.string.default_value) : String.valueOf(measureDataBean
                    .getQrsAxis());
            String tAxis = measureDataBean.gettAxis() == GlobalConstant.INVALID_DATA ? UiUitls
                    .getString(R.string.default_value) : String.valueOf(measureDataBean.gettAxis());
            String rv5 = measureDataBean.getRv5().equals("") ? UiUitls.getString(R.string
                    .default_value) : measureDataBean.getRv5();
            String sv1 = measureDataBean.getSv1().equals("") ? UiUitls.getString(R.string
                    .default_value) : measureDataBean.getSv1();
            String rv5PlusSv1 = measureDataBean.getRv5PlusSv1().equals("") ? UiUitls.getString(R
                    .string.default_value) : measureDataBean.getRv5PlusSv1();
            String qtQtc = qt + divide + qtc + UiUitls
                    .getString(R.string.blank) + ms;
            String pQrsT = pAxis + divide + qrsAxis + divide + tAxis
                    + UiUitls.getString(R.string.blank) + limit;
            tvReportHr.setText(measureDataBean.getTrendValue(KParamType.ECG_HR)
                    / GlobalConstant.TREND_FACTOR + UiUitls.getString(R.string.blank) + UiUitls
                    .getString(R.string.health_unit_bpm));
            tvReportPr.setText(pr + UiUitls
                    .getString(R.string.blank) + ms);
            tvReportQrs.setText(qrs + UiUitls
                    .getString(R.string.blank) + UiUitls.getString(R.string.unit_ms));
            String rv5Sv1 = rv5 + divide + sv1 + UiUitls
                    .getString(R.string.blank) + mv;
            tvReportQtqtc.setText(qtQtc);
            tvReportPQrsT.setText(pQrsT);
            tvReportRv5Sv1.setText(rv5Sv1);
            tvReportRv5PlusSv1.setText(rv5PlusSv1 + UiUitls.getString(R.string.blank) + mv);
            tvReportResult.setText(measureDataBean.getEcgDiagnoseResult().equals("0.00") ?
                    "" : measureDataBean.getEcgDiagnoseResult());
        }
    }

    /**
     * 初始化心电波形
     * @param hasWaveData 是否有波形数据
     */
    private void initReportEcgWave(boolean hasWaveData) {
        int widgetWidth = GlobalNumber.ONE_THOUSAND_TWO_HUNDRED_FOURTY_NUMBER; // 波形控件宽度
        int widgetHeight = GlobalNumber.FOUR_HUNDRED_SIXTY_SEVEN_NUMBER; // 波形控件高度
        EcgReportDrawable drawable = new EcgReportDrawable(measureDataBean, widgetHeight,
                widgetWidth, hasWaveData);
        Bitmap bitmap = Bitmap.createBitmap(widgetWidth, widgetHeight, drawable.getOpacity() !=
                PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, widgetWidth, widgetHeight);
        drawable.draw(canvas);
        ivEcgImage.setImageBitmap(bitmap);
    }

    /**
     * 获取远程心电处理结果
     */
    private void getRemoteEcgResult() {
        tvExpertHr.setText(EcgRemoteInfoSaveModule.getInstance().rowData.hr + UiUitls
                .getString(R.string.blank) + UiUitls.getString(R.string.health_unit_bpm));
        tvExpertPr.setText(EcgRemoteInfoSaveModule.getInstance().rowData.pr + UiUitls
                .getString(R.string.blank) + UiUitls.getString(R.string.unit_ms));
        tvExpertQrs.setText(EcgRemoteInfoSaveModule.getInstance().rowData.qrs + UiUitls
                .getString(R.string.blank) + UiUitls.getString(R.string.unit_ms));
        tvExpertQtqtc.setText(EcgRemoteInfoSaveModule.getInstance().rowData.qtQtc + UiUitls
                .getString(R.string.blank) + UiUitls.getString(R.string.unit_ms));
        tvExpertPQrsT.setText(EcgRemoteInfoSaveModule.getInstance().rowData.pQrsT + UiUitls
                .getString(R.string.blank) + UiUitls.getString(R.string.unit_limit));
        tvExpertRv5Sv1.setText(EcgRemoteInfoSaveModule.getInstance().rowData.rv5Sv1 + UiUitls
                .getString(R.string.blank) + UiUitls.getString(R.string.unit_mv));
        if (!TextUtils.isEmpty(EcgRemoteInfoSaveModule.getInstance().rowData.rv5Sv1)) {
            String[] value = EcgRemoteInfoSaveModule.getInstance().rowData.rv5Sv1.split(UiUitls
                    .getString(R.string.unit_divide));
            if (value.length > 1) {
                String rv5 = value[0].replaceAll(" ", "");
                String sv1 = value[1].replaceAll(" ", "");
                if (!TextUtils.isEmpty(rv5) && !TextUtils.isEmpty(sv1)) {
                    try {
                        BigDecimal bigDecimal1 = new BigDecimal(rv5);
                        BigDecimal bigDecimal2 = new BigDecimal(sv1);
                        String rv5PlusSv1 = bigDecimal1.add(bigDecimal2).toString();
                        tvExpertRv5PlusSv1.setText(rv5PlusSv1 + UiUitls.getString(R.string.blank)
                                + UiUitls.getString(R.string.unit_mv));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        tvRemoteEcgResult.setText(EcgRemoteInfoSaveModule.getInstance().rowData.conResult);
        tvResolveTime.setText(UiUitls.getString(R.string.resolve_time) + EcgRemoteInfoSaveModule
                .getInstance().rowData.conResultDate);
        tvDoctorSign.setText(UiUitls.getString(R.string.resolve_doctor)
                + EcgRemoteInfoSaveModule.getInstance().rowData.conDoctorName);
    }

    /**
     * 请求获取远程心电信息
     */
    private void requestRemoteEcgInfo() {
        UiUitls.showProgress(context, UiUitls.getString(R.string.loading_remote_ecg_info));
        QueryEcgDiagnosesRequest request = new QueryEcgDiagnosesRequest();
        request.dataId = measureDataBean.getUuid();
        request.beginPage = 0;
        request.pageRecord = 1;
        new UploadData(context).queryEcgRemote(request, new UploadData.ResponseCallBack() {

            @Override
            public void onSuccess(String s) {
                UiUitls.hideProgress();
                try {
                    EcgQueryResponse response = JsonUtils.toEntity(s, EcgQueryResponse.class);
                    int total = response.total;
                    if (total > 0) {
                        EcgRemoteInfoSaveModule.getInstance().rowData = response.rows.get(0);
                        refreshRemoteEcg();
                    } else {
                        handler.sendEmptyMessage(2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String message) {
                UiUitls.hideProgress();
                handler.sendEmptyMessage(2);
            }

            @Override
            public void onException() {
                UiUitls.hideProgress();
                handler.sendEmptyMessage(2);
            }
        });
    }

    /**
     * 根据远程心电申请状态更新界面
     */
    private void refreshRemoteEcg() {
        tvReportName.setText(EcgRemoteInfoSaveModule.getInstance().rowData.name);
        tvReportIdcard.setText(EcgRemoteInfoSaveModule.getInstance().rowData.idNumber);
        tvReportPhone.setText(EcgRemoteInfoSaveModule.getInstance().rowData.telephone);
        tvReportSex.setText(UiUitls.getSexString(Integer.parseInt(EcgRemoteInfoSaveModule
                .getInstance().rowData.sex)));
        int age = UiUitls.getAge(EcgRemoteInfoSaveModule.getInstance().rowData.idNumber);
        tvReportAge.setText(String.valueOf(age) + UiUitls.getString(R.string.age));
        String statue = EcgRemoteInfoSaveModule.getInstance().rowData.conState;
        switch (statue) {
            case REMOTE_ECG_NOT_RESOLVE:
                handler.sendEmptyMessage(GlobalNumber.THREE_NUMBER);
                break;
            case REMOTE_ECG_RESOLVED:
                handler.sendEmptyMessage(GlobalNumber.FOUR_NUMBER);
                break;
            default:
                break;
        }
    }

    /**
     * 申请远程心电
     */
    private void applyRemoteEcg() {
        EcgDiagnoseApplyRequest request = new EcgDiagnoseApplyRequest();
        request.dataId = measureDataBean.getUuid();
        request.doctorCode = GlobalConstant.EPMID;
        request.requestMark = "";
        UiUitls.showProgress(context, UiUitls.getString(R.string.applying));
        new UploadData(context).requestEcgApply(request, new UploadData.ResponseCallBack() {

            @Override
            public void onSuccess(String s) {
                UiUitls.hideProgress();
                Message msg = handler.obtainMessage();
                msg.obj = s;
                msg.what = 1;
                handler.sendMessage(msg);
                UiUitls.toast(context, UiUitls.getString(R.string.apply_success));
            }

            @Override
            public void onFailure(String message) {
                UiUitls.hideProgress();
                handler.sendEmptyMessage(2);
                UiUitls.toast(context, UiUitls.getString(R.string.apply_fail));
            }

            @Override
            public void onException() {
                UiUitls.hideProgress();
                handler.sendEmptyMessage(2);
                UiUitls.toast(context, UiUitls.getString(R.string.apply_fail));
            }
        });
    }
}
