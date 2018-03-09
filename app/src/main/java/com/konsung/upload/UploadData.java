package com.konsung.upload;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import com.greendao.dao.PatientBeanDao;
import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.bean.PatientBean;
import com.konsung.bean.Request.EcgDiagnoseApplyRequest;
import com.konsung.bean.Request.QueryEcgDiagnosesRequest;
import com.konsung.bean.Request.VillageHealthPortRequest;
import com.konsung.bean.Response.BaseResponse;
import com.konsung.util.DBDataUtil;
import com.konsung.util.GlobalConstant;
import com.konsung.util.JsonUtils;
import com.konsung.util.KParamType;
import com.konsung.util.NumUtil;
import com.konsung.util.ParamDefine.LogGlobalConstant;
import com.konsung.util.ParamDefine.TempDefine;
import com.konsung.util.RequestUtils;
import com.konsung.util.SpUtils;
import com.konsung.util.URLManage;
import com.konsung.util.UiUitls;
import com.konsung.util.UnitConvertUtil;
import com.konsung.util.global.Constant;
import com.konsung.util.global.EcgRemoteInfoSaveModule;
import com.konsung.util.global.GlobalNumber;
import com.loopj.android.http.TextHttpResponseHandler;
import com.synjones.bluetooth.BmpUtil;
import com.tencent.bugly.crashreport.BuglyLog;
import com.tencent.bugly.crashreport.CrashReport;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by xiangshicheng on 2017/7/7 0007.
 * 数据上传
 */

public class UploadData {
    private Context context;
    private final String textStyle = "utf-8";
    private String ip;
    private String port;
    /**
     * 回调接口
     */
    public interface ResponseCallBack {
        /**
         * 成功回调
         * @param s 字符
         */
        public void onSuccess(String s);

        /**
         * 失败回调
         * @param message 消息字符
         */
        public void onFailure(String message);

        /**
         * 异常回调
         */
        public void onException();
    }

    //接口对象
    private ResponseCallBack responseCallBack;

    /**
     * 构造方法
     * @param context 上下文引用
     */
    public UploadData(Context context) {
        this.context = context;
        ip = SpUtils.getSp(context, GlobalConstant.APP_CONFIG, GlobalConstant.SERVICE_IP
                , GlobalConstant.IP_DEFAULT);
        port = SpUtils.getSp(context, GlobalConstant.APP_CONFIG
                , GlobalConstant.IP_PROT, GlobalConstant.PORT_DEFAULT);
        URLManage.getInstance().setVillagehealthport(ip, port);
        URLManage.getInstance().setRequestEcgDiagnose(ip, port);
        URLManage.getInstance().setQueryEcgDiagnoses(ip, port);
    }

    /**
     * 测量数据上传
     * @param measureDataBean 测量bean类
     * @param responseCallBack 结果回调接口
     */
    public void uploadMeasureData(final MeasureDataBean measureDataBean
            , final ResponseCallBack responseCallBack) {
        this.responseCallBack = responseCallBack;
        UiUitls.postShortThread(new Runnable() {
            @Override
            public void run() {
                List<PatientBean> patientBeen = DBDataUtil.getPatientDao().queryBuilder()
                        .where(PatientBeanDao.Properties.IdCard.eq(measureDataBean.getIdcard()))
                        .list();
                if (patientBeen != null && patientBeen.size() > 0) {
                    PatientBean patientBean = patientBeen.get(0);
                    //上传数据到云平台
                    UploadCloudMeasureData.newInstance().uploadPhysicalMeasure(patientBean
                            , measureDataBean);
                    VillageHealthPortRequest vhpr = new VillageHealthPortRequest();
                    fillData(vhpr, measureDataBean, patientBean);
                    try {
                        final StringEntity stringEntity = new StringEntity(JsonUtils.
                                toJsonString(vhpr), textStyle);
//                        Log.e("request--->>>", "" + JsonUtils.toJsonString(vhpr));
                        RequestUtils.clientPost(context, URLManage.getInstance().villagehealthport
                                , stringEntity, new TextHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int i, Header[] headers
                                            , final String s) {
//                                        Log.e("response_success-->>", "" + s);
                                        UiUitls.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    BaseResponse response = JsonUtils.toEntity(s
                                                            , BaseResponse.class);
                                                    responseCallBack.onSuccess(response.msgCode);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    UiUitls.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            responseCallBack.onException();
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(int i, Header[] headers, final String s
                                            , Throwable throwable) {
//                                        Log.e("response_fail-->>", "" + s);
                                        BuglyLog.v(UploadData.class.getName(),
                                                String.valueOf(stringEntity));
                                        BuglyLog.v(UploadData.class.getName(), LogGlobalConstant
                                                .URL + URLManage.getInstance().villagehealthport);
                                        CrashReport.postCatchedException(throwable);
                                        UiUitls.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                responseCallBack.onFailure(s);
                                            }
                                        });
                                    }
                                });
                    } catch (UnsupportedEncodingException e) {
                        BuglyLog.v(UploadData.class.getName(), LogGlobalConstant
                                .URL + URLManage.getInstance().villagehealthport);
                        CrashReport.postCatchedException(e);
                        e.printStackTrace();
                        UiUitls.post(new Runnable() {
                            @Override
                            public void run() {
                                responseCallBack.onException();
                            }
                        });
                    }
                }
            }
        });
    }

    /**
     * 请求参数赋值
     * @param vhpr 参数请求类
     * @param measureDataBean 测量类
     * @param patientBean 病人信息类
     */
    private void fillData(VillageHealthPortRequest vhpr, MeasureDataBean measureDataBean
            , PatientBean patientBean) {
        String deviceID = SpUtils.getSp(UiUitls.getContent(),
                GlobalConstant.APP_CONFIG,
                GlobalConstant.APP_CODING, Constant.DEVICE_CODE);
        //检查时间yyyy-MM-dd hh:mm:ss
        String checkDate = UiUitls.getDateFormat(UiUitls.DateState.LONG)
                .format(measureDataBean.getMeasureTime());
        String uploadData = UiUitls.getDateFormat(UiUitls.DateState.LONG).format(new Date());
        //事先固定好的一个私钥
        String single = "konsung";
        //加密字段，key=md5(uploadDate+area+single),其中single是在事先固定好的一个私钥。
        String key = UiUitls.stringMD5(uploadData + UiUitls.getAreaName() + single);
        String sex = "";
        if (patientBean.getSex() == 0) {
            //女
            sex = "2";
        } else {
            //1 男
            sex = String.valueOf(patientBean.getSex());
        }
        //体温 摄氏度
        String temp = "";
        int type = SpUtils.getSpInt(UiUitls.getContent(), "sys_config",
                "temp_type",
                TempDefine.TEMP_INFRARED);
        switch (type) {
            case TempDefine.TEMP_CONTACT:
                temp = (measureDataBean.getTrendValue(KParamType.TEMP_T1) ==
                        GlobalConstant.INVALID_TREND_DATA) ? "" : String.valueOf(measureDataBean
                        .getTrendValue(KParamType.TEMP_T1) / GlobalConstant.SWITCH_VALUE);
                break;
            case TempDefine.TEMP_INFRARED:
                temp = (measureDataBean.getTrendValue(KParamType
                        .IRTEMP_TREND) == GlobalConstant.INVALID_TREND_DATA) ? "" : String
                        .valueOf(measureDataBean.getTrendValue(KParamType.IRTEMP_TREND)
                                / GlobalConstant.SWITCH_VALUE);
                break;
            default:
                temp = "";
                break;
        }
        //基本信息
        vhpr.id = measureDataBean.getUuid();
        vhpr.empId = GlobalConstant.EPMID;
        vhpr.doctorCode = GlobalConstant.USERNAME;
        vhpr.orgCode = GlobalConstant.ORG_ID;
        vhpr.deviceCode = deviceID;
        vhpr.uploadDate = uploadData;
        vhpr.checkDate = checkDate;
        vhpr.area = UiUitls.getAreaName();
        vhpr.key = key;
        vhpr.patientType = patientBean.getPatient_type() + "";
        vhpr.version = GlobalConstant.INTEFACE_VERSION;
        vhpr.deviceVersion = GlobalConstant.INTEFACE_VERSION;
        vhpr.equipmentModel = UiUitls.getAreaName() + "-1";
        vhpr.height = measureDataBean.getHeight();
        vhpr.weight = measureDataBean.getWeight();
        vhpr.bmi = UiUitls.countBmi(vhpr.height, vhpr.weight);
        vhpr.waist = patientBean.getWaist() == null ? "" : patientBean.getWaist();
        vhpr.hipline = patientBean.getHipline() == null ? "" : patientBean.getHipline();
        vhpr.patientType = patientBean.getPatient_type() + "";
        //病人信息
        //身份证
        String idCard = patientBean.getCard();
        //生日 默认为1900-01-01 后台接口已经写死为必传字段
        String birthDay = "1900-01-01";
        if (!TextUtils.isEmpty(idCard)) {
            String s = idCard.substring(GlobalNumber.SIX_NUMBER, GlobalNumber.FOURTEEN_NUMBER);
            s += "0000";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmm");
            try {
                birthDay = UiUitls.getDateFormat(UiUitls.DateState.SHORT).format(sdf.parse(s));
            } catch (ParseException e) {
                e.printStackTrace();
                CrashReport.postCatchedException(e);
            }
        }
        vhpr.personinfo.cardNo = idCard;
        vhpr.personinfo.birthDay = birthDay;
        vhpr.personinfo.name = UiUitls.stringFilter(patientBean.getName());
        vhpr.personinfo.sex = sex;
        vhpr.personinfo.telePhone = patientBean.getSelfmobile();
        vhpr.personinfo.age = patientBean.getAge() + "";
        vhpr.personinfo.address = patientBean.getAddress();
        vhpr.personinfo.desc = patientBean.getRemark();
        //1.3.0新增字段
        vhpr.personinfo.membershipCard = patientBean.getMemberShipCard();
        vhpr.personinfo.bloodType = patientBean.getBlood() + "";
        if (!TextUtils.isEmpty(patientBean.getBmpStr())) {
            vhpr.personinfo.headPortrait
                    = BmpUtil.bmp2bytes(BmpUtil.getBitmapByFileName(patientBean.getBmpStr()));
        }
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
        //心电数据
        vhpr.uwd.hr = (measureDataBean.getTrendValue(KParamType.ECG_HR)
                == GlobalConstant.INVALID_TREND_DATA) ? ""
                : String.valueOf(measureDataBean.getTrendValue(KParamType
                .ECG_HR) / GlobalConstant.TREND_FACTOR);
        vhpr.uwd.resp_rr = (measureDataBean.getTrendValue(KParamType.RESP_RR) ==
                GlobalConstant.INVALID_TREND_DATA) ? "" : String.valueOf((float) measureDataBean
                .getTrendValue(KParamType.RESP_RR) / GlobalConstant.MEASURE_TIME);
        vhpr.uwd.sample = "500";
        vhpr.uwd.p05 = "2150";
        vhpr.uwd.n05 = "1946";
        vhpr.uwd.duration = "5";
        // PR间期
        vhpr.uwd.PR = String.valueOf(measureDataBean.getPr());
        // QRS间期, 单位ms
        vhpr.uwd.QRS = String.valueOf(measureDataBean.getQrs());
        // QT间期
        vhpr.uwd.QT = String.valueOf(measureDataBean.getQt());
        // QTC间期
        vhpr.uwd.QTC = String.valueOf(measureDataBean.getQtc());
        // P 波轴
        vhpr.uwd.P = String.valueOf(measureDataBean.getpAxis());
        // QRS波心电轴
        vhpr.uwd.QRSZ = String.valueOf(measureDataBean.getQrsAxis());
        // T波心电轴
        vhpr.uwd.T = String.valueOf(measureDataBean.gettAxis());
        // RV5, 单位0.01ms
        vhpr.uwd.RV5 = measureDataBean.getRv5();
        // SV1, 单位0.01ms
        vhpr.uwd.SV1 = measureDataBean.getSv1();
        // RV5+SV1
        String rv5PlusSv1 = measureDataBean.getRv5PlusSv1();
        String mEcgDiagnosis = measureDataBean.getEcgDiagnoseResult().equals("0.00") ?
                "" : measureDataBean.getEcgDiagnoseResult();
        //心电图自动诊断结果
        String anal = vhpr.uwd.hr + "," + vhpr.uwd.PR + "," + vhpr.uwd.QRS + "," + vhpr.uwd.QT +
                "," + vhpr.uwd.QTC + "," + vhpr.uwd.P + "," + vhpr.uwd.QRSZ + "," + vhpr.uwd.T +
                "," + vhpr.uwd.RV5 + "," + vhpr.uwd.SV1 + "," + "" + rv5PlusSv1 + ",,,,,," +
                mEcgDiagnosis;
        vhpr.uwd.anal = anal;

        vhpr.uwd.ecg_i = Base64.encodeToString(UnitConvertUtil
                        .getByteformHexString(measureDataBean.get_ecgWave(KParamType.ECG_I))
                , Base64.NO_WRAP);
        vhpr.uwd.ecg_ii = Base64.encodeToString(UnitConvertUtil
                        .getByteformHexString(measureDataBean.get_ecgWave(KParamType.ECG_II))
                , Base64.NO_WRAP);
        vhpr.uwd.ecg_iii = Base64.encodeToString(UnitConvertUtil
                        .getByteformHexString(measureDataBean.get_ecgWave(KParamType.ECG_III))
                , Base64.NO_WRAP);
        vhpr.uwd.ecg_avr = Base64.encodeToString(UnitConvertUtil
                        .getByteformHexString(measureDataBean.get_ecgWave(KParamType.ECG_AVR))
                , Base64.NO_WRAP);
        vhpr.uwd.ecg_avf = Base64.encodeToString(UnitConvertUtil
                        .getByteformHexString(measureDataBean.get_ecgWave(KParamType.ECG_AVF))
                , Base64.NO_WRAP);
        vhpr.uwd.ecg_avl = Base64.encodeToString(UnitConvertUtil
                        .getByteformHexString(measureDataBean.get_ecgWave(KParamType.ECG_AVL))
                , Base64.NO_WRAP);
        vhpr.uwd.ecg_v1 = Base64.encodeToString(UnitConvertUtil
                        .getByteformHexString(measureDataBean.get_ecgWave(KParamType.ECG_V1))
                , Base64.NO_WRAP);
        vhpr.uwd.ecg_v2 = Base64.encodeToString(UnitConvertUtil
                        .getByteformHexString(measureDataBean.get_ecgWave(KParamType.ECG_V2))
                , Base64.NO_WRAP);
        vhpr.uwd.ecg_v3 = Base64.encodeToString(UnitConvertUtil
                        .getByteformHexString(measureDataBean.get_ecgWave(KParamType.ECG_V3))
                , Base64.NO_WRAP);
        vhpr.uwd.ecg_v4 = Base64.encodeToString(UnitConvertUtil
                        .getByteformHexString(measureDataBean.get_ecgWave(KParamType.ECG_V4))
                , Base64.NO_WRAP);
        vhpr.uwd.ecg_v5 = Base64.encodeToString(UnitConvertUtil
                        .getByteformHexString(measureDataBean.get_ecgWave(KParamType.ECG_V5))
                , Base64.NO_WRAP);
        vhpr.uwd.ecg_v6 = Base64.encodeToString(UnitConvertUtil
                        .getByteformHexString(measureDataBean.get_ecgWave(KParamType.ECG_V6))
                , Base64.NO_WRAP);
        //
        vhpr.heartFile = getHeartFile(vhpr, measureDataBean);
        vhpr.heartType = "F";
        //血糖数据
        vhpr.ubsud.glu = (measureDataBean.getTrendValue(KParamType.BLOODGLU_BEFORE_MEAL)
                == GlobalConstant.INVALID_TREND_DATA) ? ""
                : String.valueOf((float) measureDataBean
                .getTrendValue(KParamType.BLOODGLU_BEFORE_MEAL) / GlobalConstant.TREND_FACTOR);
        vhpr.ubsud.gluStyle = measureDataBean.getGluStyle();
        vhpr.ubsud.bsPh = (measureDataBean.getTrendValue(KParamType.URICACID_TREND)
                == GlobalConstant.INVALID_TREND_DATA) ? ""
                : String.valueOf((float) measureDataBean
                .getTrendValue(KParamType.URICACID_TREND) / GlobalConstant.TREND_FACTOR);
//        vhpr.ubsud.totalCholesterol = (measureDataBean.getTrendValue(KParamType
//                .CHOLESTEROL_TREND) == GlobalConstant.INVALID_TREND_DATA) ? ""
//                : String.valueOf((float) measureDataBean
//                .getTrendValue(KParamType.CHOLESTEROL_TREND) / GlobalConstant.TREND_FACTOR);
        //血氧
        vhpr.uod.spo2 = (measureDataBean.getTrendValue(KParamType.SPO2_TREND) ==
                GlobalConstant.INVALID_TREND_DATA) ? ""
                : String.valueOf(measureDataBean.getTrendValue(KParamType
                .SPO2_TREND) / GlobalConstant.TREND_FACTOR);
        vhpr.uod.oxPr = (measureDataBean.getTrendValue(KParamType.SPO2_PR)
                == GlobalConstant.INVALID_TREND_DATA) ? "" :
                String.valueOf(measureDataBean.getTrendValue(KParamType.SPO2_PR) /
                        GlobalConstant.TREND_FACTOR);
        //血压
        vhpr.ubpd.sbp = (measureDataBean.getTrendValue(KParamType.NIBP_SYS)
                == GlobalConstant.INVALID_TREND_DATA) ?
                "" : String.valueOf(measureDataBean.getTrendValue(KParamType.NIBP_SYS) /
                GlobalConstant.TREND_FACTOR);
        vhpr.ubpd.dbp = (measureDataBean.getTrendValue(KParamType.NIBP_DIA)
                == GlobalConstant.INVALID_TREND_DATA) ?
                "" : String.valueOf(measureDataBean.getTrendValue(KParamType.NIBP_DIA) /
                GlobalConstant.TREND_FACTOR);
        //体温
        vhpr.temp.temp = temp;
        //尿常规
        vhpr.uud.urinePh = (measureDataBean.getTrendValue(KParamType.URINERT_PH) ==
                GlobalConstant.INVALID_TREND_DATA) ? "" : String.valueOf((float) measureDataBean
                .getTrendValue(KParamType.URINERT_PH) / GlobalConstant.SWITCH_VALUE);
        vhpr.uud.urineUbg = (measureDataBean.getTrendValue(KParamType.URINERT_UBG) ==
                GlobalConstant.INVALID_TREND_DATA) ? ""
                : DBDataUtil.valueToString(measureDataBean.getTrendValue(KParamType
                .URINERT_UBG) / GlobalConstant.TREND_FACTOR);
        vhpr.uud.urineBld = (measureDataBean.getTrendValue(KParamType.URINERT_BLD) ==
                GlobalConstant.INVALID_TREND_DATA) ? ""
                : DBDataUtil.valueToString(measureDataBean.getTrendValue(KParamType
                .URINERT_BLD) / GlobalConstant.TREND_FACTOR);
        vhpr.uud.urinePro = (measureDataBean.getTrendValue(KParamType.URINERT_PRO) ==
                GlobalConstant.INVALID_TREND_DATA) ? ""
                : DBDataUtil.valueToString(measureDataBean.getTrendValue(KParamType
                .URINERT_PRO) / GlobalConstant.TREND_FACTOR);
        vhpr.uud.urineKet = (measureDataBean.getTrendValue(KParamType.URINERT_KET) ==
                GlobalConstant.INVALID_TREND_DATA) ? ""
                : DBDataUtil.valueToString(measureDataBean.getTrendValue(KParamType
                .URINERT_KET) / GlobalConstant.TREND_FACTOR);
        vhpr.uud.urineNit = (measureDataBean.getTrendValue(KParamType.URINERT_NIT) ==
                GlobalConstant.INVALID_TREND_DATA) ? ""
                : DBDataUtil.valueToString(measureDataBean.getTrendValue(KParamType
                .URINERT_NIT) / GlobalConstant.TREND_FACTOR);
        vhpr.uud.urineGlu = (measureDataBean.getTrendValue(KParamType.URINERT_GLU) ==
                GlobalConstant.INVALID_TREND_DATA) ? ""
                : DBDataUtil.valueToString(measureDataBean.getTrendValue(KParamType
                .URINERT_GLU) / GlobalConstant.TREND_FACTOR);
        vhpr.uud.urineBil = (measureDataBean.getTrendValue(KParamType.URINERT_BIL) ==
                GlobalConstant.INVALID_TREND_DATA) ? ""
                : DBDataUtil.valueToString(measureDataBean.getTrendValue(KParamType
                .URINERT_BIL) / GlobalConstant.TREND_FACTOR);
        vhpr.uud.urineLeu = (measureDataBean.getTrendValue(KParamType.URINERT_LEU) ==
                GlobalConstant.INVALID_TREND_DATA) ? ""
                : DBDataUtil.valueToString(measureDataBean.getTrendValue(KParamType
                .URINERT_LEU) / GlobalConstant.TREND_FACTOR);
        vhpr.uud.urineSg = (measureDataBean.getTrendValue(KParamType.URINERT_SG) ==
                GlobalConstant.INVALID_TREND_DATA) ? ""
                : String.format("%.3f", (double) measureDataBean
                .getTrendValue(KParamType.URINERT_SG) / GlobalConstant.THOSOUND_VALUE);
        vhpr.uud.urineVc = (measureDataBean.getTrendValue(KParamType.URINERT_ASC) ==
                GlobalConstant.INVALID_TREND_DATA) ? ""
                : DBDataUtil.valueToString(measureDataBean.getTrendValue(KParamType
                .URINERT_ASC) / GlobalConstant.TREND_FACTOR);
        vhpr.uud.urineCre = (measureDataBean.getTrendValue(KParamType.URINERT_CRE) ==
                GlobalConstant.INVALID_TREND_DATA) ? ""
                : DBDataUtil.valueToString(measureDataBean.getTrendValue(KParamType.URINERT_CRE)
                / GlobalConstant.TREND_FACTOR);
        vhpr.uud.urineCa = (measureDataBean.getTrendValue(KParamType.URINERT_CA) ==
                GlobalConstant.INVALID_TREND_DATA) ? "" : DBDataUtil.valueToString(measureDataBean
                .getTrendValue(KParamType.URINERT_CA) / GlobalConstant.TREND_FACTOR);
        vhpr.uud.urineMa = (measureDataBean.getTrendValue(KParamType.URINERT_ALB) ==
                GlobalConstant.INVALID_TREND_DATA) ? ""
                : DBDataUtil.valueToString(measureDataBean.getTrendValue(KParamType
                .URINERT_ALB) / GlobalConstant.TREND_FACTOR);
        //血红蛋白
        vhpr.uhd.hgb = (measureDataBean.getTrendValue(KParamType.BLOOD_HGB)
                == GlobalConstant.INVALID_TREND_DATA) ? "" :
                String.valueOf(NumUtil.trans2FloatValue(measureDataBean
                        .getTrendValue(KParamType.BLOOD_HGB) / GlobalConstant.SWITCH_VALUE, 1));
        vhpr.uhd.htc = (measureDataBean.getTrendValue(KParamType.BLOOD_HGB)
                == GlobalConstant.INVALID_TREND_DATA) ? "" :
                String.valueOf(measureDataBean.getTrendValue(KParamType.BLOOD_HCT)
                        / GlobalConstant.SWITCH_VALUE);
        //血脂四项
        vhpr.ubsd.cholesterol = (measureDataBean.getTrendValue(KParamType.LIPIDS_CHOL)
                == GlobalConstant.INVALID_TREND_DATA) ? ""
                : String.valueOf((float) measureDataBean
                .getTrendValue(KParamType.LIPIDS_CHOL) / GlobalConstant.TREND_FACTOR);
        vhpr.ubsd.flipidsTrig = (measureDataBean.getTrendValue(KParamType.LIPIDS_TRIG)
                == GlobalConstant.INVALID_TREND_DATA) ? ""
                : String.valueOf((float) measureDataBean
                .getTrendValue(KParamType.LIPIDS_TRIG) / GlobalConstant.TREND_FACTOR);
        vhpr.ubsd.flipidsHdl = (measureDataBean.getTrendValue(KParamType.LIPIDS_HDL)
                == GlobalConstant.INVALID_TREND_DATA) ? ""
                : String.valueOf((float) measureDataBean
                .getTrendValue(KParamType.LIPIDS_HDL) / GlobalConstant.TREND_FACTOR);
        vhpr.ubsd.flipidsLDL = (measureDataBean.getTrendValue(KParamType.LIPIDS_LDL)
                == GlobalConstant.INVALID_TREND_DATA) ? ""
                : String.valueOf((float) measureDataBean
                .getTrendValue(KParamType.LIPIDS_LDL) / GlobalConstant.TREND_FACTOR);
        //糖化血红蛋白
        vhpr.ughd.hba1cNgsp = (measureDataBean.getTrendValue(KParamType.HBA1C_NGSP)
                == GlobalConstant.INVALID_TREND_DATA) ||
                measureDataBean.getTrendValue(KParamType.HBA1C_NGSP) == 0 ? ""
                : String.valueOf((float) measureDataBean
                .getTrendValue(KParamType.HBA1C_NGSP) / GlobalConstant.TREND_FACTOR);
        vhpr.ughd.hba1cIfcc = (measureDataBean.getTrendValue(KParamType.HBA1C_IFCC)
                == GlobalConstant.INVALID_TREND_DATA)
                || measureDataBean.getTrendValue(KParamType.HBA1C_IFCC) == 0 ? ""
                : String.valueOf((float) measureDataBean
                .getTrendValue(KParamType.HBA1C_IFCC) / GlobalConstant.TREND_FACTOR);
        vhpr.ughd.hba1cEag = (measureDataBean.getTrendValue(KParamType.HBA1C_EAG)
                == GlobalConstant.INVALID_TREND_DATA)
                || measureDataBean.getTrendValue(KParamType.HBA1C_EAG) == 0 ? ""
                : String.valueOf((float) measureDataBean
                .getTrendValue(KParamType.HBA1C_EAG) / GlobalConstant.TREND_FACTOR);
        //白细胞
        vhpr.hemameba.hemameba = (measureDataBean.getTrendValue(KParamType.BLOOD_WBC)
                == GlobalConstant.INVALID_TREND_DATA) ? "" : String.valueOf((float)
                measureDataBean.getTrendValue(KParamType.BLOOD_WBC) / GlobalConstant.WBC_FACTOR);
        //总胆固醇(单测项)
        vhpr.chol = (measureDataBean.getTrendValue(KParamType
                .CHOLESTEROL_TREND) == GlobalConstant.INVALID_TREND_DATA) ? ""
                : String.valueOf((float) measureDataBean
                .getTrendValue(KParamType.CHOLESTEROL_TREND) / GlobalConstant.TREND_FACTOR);
    }

    /**
     * 远程心电申请
     * @param request 请求参数
     * @param responseCallBack 结果回调
     */
    public void requestEcgApply(final EcgDiagnoseApplyRequest request
            , final ResponseCallBack responseCallBack) {
        this.responseCallBack = responseCallBack;
        UiUitls.postShortThread(new Runnable() {
            @Override
            public void run() {
                try {
                    StringEntity stringEntity = new StringEntity(JsonUtils.toJsonString(request));
                    RequestUtils.clientPost(context, URLManage.getInstance().requestEcgDiagnose
                            , stringEntity, new TextHttpResponseHandler() {
                                @Override
                                public void onSuccess(int i, Header[] headers, final String s) {
                                    UiUitls.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (s.length() > 0) {
                                                try {
                                                    BaseResponse response = JsonUtils.toEntity(s
                                                            , BaseResponse.class);
                                                    String message = response.msgText;
                                                    if (response.msgCode.equals("10000")) {
                                                        //申请成功
                                                        responseCallBack.onSuccess(message);
                                                    } else {
                                                        //申请失败
                                                        responseCallBack.onFailure(message);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    responseCallBack.onException();
                                                }
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(int i, Header[] headers, final String s
                                        , Throwable throwable) {
                                    UiUitls.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            responseCallBack.onFailure(s);
                                        }
                                    });
                                }
                            });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    UiUitls.post(new Runnable() {
                        @Override
                        public void run() {
                            responseCallBack.onException();
                        }
                    });
                }
            }
        });
    }

    /**
     * 远程心电查询
     * @param request 请求参数
     * @param responseCallBack 结果回调
     */
    public void queryEcgRemote(final QueryEcgDiagnosesRequest request
            , final ResponseCallBack responseCallBack) {
        this.responseCallBack = responseCallBack;
        UiUitls.postShortThread(new Runnable() {
            @Override
            public void run() {
                try {
                    StringEntity stringEntity = new StringEntity(JsonUtils
                            .toJsonString(request), textStyle);
                    RequestUtils.clientPost(context, URLManage.getInstance().queryEcgDiagnoses
                            , stringEntity, new TextHttpResponseHandler() {
                                @Override
                                public void onSuccess(int i, Header[] headers, final String s) {
                                    UiUitls.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!TextUtils.isEmpty(s)) {
                                                responseCallBack.onSuccess(s);
                                            } else {
                                                responseCallBack.onFailure(s);
                                            }
                                        }
                                    });
                                }

                                @Override
                                public void onFailure(int i, Header[] headers, final String s
                                        , Throwable throwable) {
                                    UiUitls.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            responseCallBack.onFailure(s);
                                        }
                                    });
                                }
                            });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    UiUitls.post(new Runnable() {
                        @Override
                        public void run() {
                            responseCallBack.onException();
                        }
                    });
                }
            }
        });
    }

    /**
     * 多条测量数据上传
     * @param measureDataBean 测量bean类
     * @param patientBean 病人实体类
     */
    public void uploadMeasureDataMore(final MeasureDataBean measureDataBean
            , final PatientBean patientBean) {
        EcgRemoteInfoSaveModule.getInstance().isUploadSuccess = false;
        //上传数据到云平台
        UploadCloudMeasureData.newInstance().uploadPhysicalMeasure(patientBean, measureDataBean);
        VillageHealthPortRequest vhpr = new VillageHealthPortRequest();
        fillData(vhpr, measureDataBean, patientBean);
        try {
            StringEntity stringEntity = new StringEntity(JsonUtils.toJsonString(vhpr), textStyle);
            RequestUtils.clientPost(context, URLManage.getInstance().villagehealthport
                    , stringEntity, new TextHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, final String s) {
                            try {
                                BaseResponse response = JsonUtils.toEntity(s, BaseResponse.class);
                                if (response.msgCode.equals("10000")) {
                                    EcgRemoteInfoSaveModule.getInstance().isUploadSuccess = true;
                                    UiUitls.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            UiUitls.toast(context
                                                    , context.getString(R.string.upload_success));
                                        }
                                    });
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int i, Header[] headers, final String s
                                , Throwable throwable) {
                            UiUitls.post(new Runnable() {
                                @Override
                                public void run() {
                                    UiUitls.toast(context
                                            , context.getString(R.string.data_upload_fail));
                                }
                            });
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成心电文件
     * @param request 数据上传参数类
     * @param measureDataBean 测量记录
     * @return 心电xml文件
     */
    private String getHeartFile(VillageHealthPortRequest request, MeasureDataBean measureDataBean) {
        String heartFile = "";
        //心电的xml字符串
        String heart = "<WaveForm>" +
                "<Sample>%s</Sample>" +
                "<P05>%s</P05>" +
                "<N05>%s</N05>" +
                "<Duration>%s</Duration>" +
                "<ECG_I>%s</ECG_I>" +
                "<ECG_II>%s</ECG_II>" +
                "<ECG_III>%s</ECG_III>" +
                "<ECG_aVR>%s</ECG_aVR>" +
                "<ECG_aVF>%s</ECG_aVF>" +
                "<ECG_aVL>%s</ECG_aVL>" +
                "<ECG_V1>%s</ECG_V1>" +
                "<ECG_V2>%s</ECG_V2>" +
                "<ECG_V3>%s</ECG_V3>" +
                "<ECG_V4>%s</ECG_V4>" +
                "<ECG_V5>%s</ECG_V5>" +
                "<ECG_V6>%s</ECG_V6>" +
                "</WaveForm>";
        if (request.uwd.anal.equals("")) {
            heart = "";
        } else {
            heart = String.format(heart, request.uwd.sample, request.uwd.p05, request.uwd.n05
                    , request.uwd.duration, request.uwd.ecg_i, request.uwd.ecg_ii
                    , request.uwd.ecg_iii, request.uwd.ecg_avr, request.uwd.ecg_avf
                    , request.uwd.ecg_avl, request.uwd.ecg_v1, request.uwd.ecg_v2
                    , request.uwd.ecg_v3, request.uwd.ecg_v4, request.uwd.ecg_v5
                    , request.uwd.ecg_v6);
            //创建文件
            File file = new File(UiUitls.getContent().getCacheDir()
                    , measureDataBean.getUuid() + "" + ".xml");
            File zipFile = new File(UiUitls.getContent().getCacheDir()
                    , measureDataBean.getUuid() + ".zip");
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter(file));
                writer.write(heart);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            File doZip = null;
            try {
                doZip = UiUitls.doZip(file, zipFile);
                FileInputStream in = new FileInputStream(doZip);
                byte[] bytes = UiUitls.toByteArray(in);
                in.close();
                heartFile = Base64.encodeToString(bytes, Base64.NO_WRAP);
                //删除临时文件
                if (file != null) {
                    file.delete();
                }
                if (zipFile != null) {
                    zipFile.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return heartFile;
    }
}
