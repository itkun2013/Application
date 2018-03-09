package com.konsung.util;

import android.content.Context;
import android.text.TextUtils;

import com.konsung.R;
import com.konsung.bean.MeasureDataBean;
import com.konsung.util.global.BeneParamValue;
import com.konsung.util.global.BloodMem;
import com.konsung.util.global.BmiParam;
import com.konsung.util.global.Ecg;
import com.konsung.util.global.IrTemp;
import com.konsung.util.global.Nibp;
import com.konsung.util.global.Spo;
import com.konsung.util.global.SugarBloodParam;
import com.konsung.util.global.Urine;
import com.konsung.util.global.WbcParamValue;

/**
 * Created by xiangshicheng on 2017/10/19 0019.
 * 专门用于单测页比较测量值是否正常以及转换后的显示值
 */

public class MeasureValueCompareUtil {

    /**
     * 获取最终结果值
     * @return 结果值
     * @param type 测量类型
     * @param context 上下文
     */
    public static String getFinalValue(int type, Context context) {
        String resultStr = "";
        switch (type) {
            case KParamType.ECG_HR:
                //心率(一项值)
                if (GlobalConstant.ECG_PR_VALUE != GlobalConstant.INVALID_DATA) {
                    resultStr = GlobalConstant.ECG_PR_VALUE + "";
                } else {
                    resultStr = context.getResources().getString(R.string.unmeasure);
                }
                break;
            case KParamType.SPO2_TREND:
                //血氧(两项值)
                if (GlobalConstant.SPO2_VALUE != GlobalConstant.INVALID_DATA) {
                    resultStr = GlobalConstant.SPO2_VALUE + "/" + GlobalConstant.SPO2_PR_VALUE;
                } else {
                    resultStr = context.getResources().getString(R.string.unmeasure);
                }
                break;
            case KParamType.NIBP_SYS:
                //血压(三项值)
                if (GlobalConstant.NIBP_SYS_VALUE != GlobalConstant.INVALID_DATA) {
                    resultStr = GlobalConstant.NIBP_SYS_VALUE + "/" + GlobalConstant.NIBP_DIA_VALUE;
                } else {
                    resultStr = context.getResources().getString(R.string.unmeasure);
                }
                break;
            case KParamType.IRTEMP_TREND:
                //体温(一项值)
                if (GlobalConstant.IR_TEMP_VALUE != GlobalConstant.INVALID_DATA) {
                    resultStr = GlobalConstant.IR_TEMP_VALUE + "";
                } else {
                    resultStr = context.getResources().getString(R.string.unmeasure);
                }
                break;
            case KParamType.BLOODGLU_BEFORE_MEAL:
                //血液三项(三项值)
                if (GlobalConstant.BLOOD_GLU_VALUE == GlobalConstant.INVALID_DATA &&
                        GlobalConstant.URIC_ACID_VALUE == GlobalConstant.INVALID_DATA &&
                        GlobalConstant.CHOLESTEROL_VALUE == GlobalConstant.INVALID_DATA) {
                    resultStr = context.getResources().getString(R.string.unmeasure);
                } else {
                    float min;
                    float max;
                    float min1;
                    float max1;
                    int count = 0;
                    if (GlobalConstant.BLOODGLUSTATE.equals("0")) {
                        //餐前
                        min = BeneParamValue.XT_VALUE_MIN;
                        max = BeneParamValue.XT_VALUE_MAX;
                    } else {
                        //餐后
                        min = BeneParamValue.XT_AFTER_VALUE_MIN;
                        max = BeneParamValue.XT_AFTER_VALUE_MAX;
                    }
                    int sex = SpUtils.getSpInt(context, GlobalConstant.APP_CONFIG
                            , GlobalConstant.CURRENT_SEX, 0);
                    if (sex == 0) {
                        min1 = BeneParamValue.NS_VALUE_MING;
                        max1 = BeneParamValue.NS_VALUE_MAXG;
                    } else {
                        min1 = BeneParamValue.NS_VALUE_MIN;
                        max1 = BeneParamValue.NS_VALUE_MAX;
                    }
                    if (!isValueNormal(max, min, GlobalConstant.BLOOD_GLU_VALUE)) {
                        count ++;
                    }
                    if (!isValueNormal(max1, min1, GlobalConstant.URIC_ACID_VALUE)) {
                        count ++;
                    }
                    if (!isValueNormal(BeneParamValue.CHOL_VALUE_MAX
                            , BeneParamValue.CHOL_VALUE_MIN, GlobalConstant.CHOLESTEROL_VALUE)) {
                        count ++;
                    }
                    if (count > 0) {
                        resultStr = count + context.getResources()
                                .getString(R.string.item_unnormal);
                    } else {
                        resultStr = context.getResources().getString(R.string.item_normal);
                    }
                }
                break;
            case KParamType.URINERT_LEU:
                //尿常规(14项值)
                if (GlobalConstant.URINE_LEU_VALUE == GlobalConstant.INVALID_DATA &&
                        GlobalConstant.URINE_UBG_VALUE == GlobalConstant.INVALID_DATA &&
                        GlobalConstant.URINE_PRO_VALUE == GlobalConstant.INVALID_DATA &&
                        GlobalConstant.URINE_BIL_VALUE == GlobalConstant.INVALID_DATA &&
                        GlobalConstant.URINE_GLU_VALUE == GlobalConstant.INVALID_DATA &&
                        GlobalConstant.URINE_ASC_VALUE == GlobalConstant.INVALID_DATA &&
                        GlobalConstant.URINE_CA_VALUE == GlobalConstant.INVALID_DATA &&
                        GlobalConstant.URINE_ALB_VALUE == GlobalConstant.INVALID_DATA &&
                        GlobalConstant.URINE_CRE_VALUE == GlobalConstant.INVALID_DATA &&
                        GlobalConstant.URINE_SG_VALUE == GlobalConstant.INVALID_DATA &&
                        GlobalConstant.URINE_KET_VALUE == GlobalConstant.INVALID_DATA &&
                        GlobalConstant.URINE_NIT_VALUE == GlobalConstant.INVALID_DATA &&
                        GlobalConstant.URINE_PH_VALUE == GlobalConstant.INVALID_DATA &&
                        GlobalConstant.URINE_BLD_VALUE == GlobalConstant.INVALID_DATA) {
                    resultStr = context.getResources().getString(R.string.unmeasure);
                } else {
                    int count = 0;
                    //14项
                    if (!isUrineNormal(-1, -1, GlobalConstant.URINE_UBG_VALUE, true, context)) {
                        count++;
                    }
                    if (!isUrineNormal(-1, -1, GlobalConstant.URINE_PRO_VALUE, true, context)) {
                        count++;
                    }
                    if (!isUrineNormal(-1, -1, GlobalConstant.URINE_BIL_VALUE, true, context)) {
                        count++;
                    }
                    if (!isUrineNormal(-1, -1, GlobalConstant.URINE_GLU_VALUE, true, context)) {
                        count++;
                    }
                    if (!isUrineNormal(-1, -1, GlobalConstant.URINE_ASC_VALUE, true, context)) {
                        count++;
                    }
                    if (!isUrineNormal(-1, -1, GlobalConstant.URINE_CA_VALUE, true, context)) {
                        count++;
                    }
                    if (!isUrineNormal(-1, -1, GlobalConstant.URINE_ALB_VALUE, true, context)) {
                        count++;
                    }
                    if (!isUrineNormal(-1, -1, GlobalConstant.URINE_CRE_VALUE, true, context)) {
                        count++;
                    }
                    //需传值比较
                    if (!isUrineNormal(Urine.SG_HIGH, Urine.SG_LOW
                            , GlobalConstant.URINE_SG_VALUE, false, context)) {
                        count++;
                    }
                    if (!isUrineNormal(-1, -1, GlobalConstant.URINE_KET_VALUE, true, context)) {
                        count++;
                    }
                    if (!isUrineNormal(-1, -1, GlobalConstant.URINE_NIT_VALUE, true, context)) {
                        count++;
                    }
                    //需传值比较
                    if (!isUrineNormal(Urine.PH_HIGH, -Urine.PH_LOW
                            , GlobalConstant.URINE_PH_VALUE, false, context)) {
                        count++;
                    }
                    if (!isUrineNormal(-1, -1, GlobalConstant.URINE_BLD_VALUE, true, context)) {
                        count++;
                    }
                    if (!isUrineNormal(-1, -1, GlobalConstant.URINE_LEU_VALUE, true, context)) {
                        count++;
                    }
                    if (count > 0) {
                        resultStr = count + context.getResources()
                                .getString(R.string.item_unnormal);
                    } else {
                        resultStr = context.getResources().getString(R.string.item_normal);
                    }
                }
                break;
            case KParamType.BLOOD_HGB:
                //血红蛋白(2项值)
                if (GlobalConstant.BLOOD_HGB_VALUE != GlobalConstant.INVALID_DATA) {
                    resultStr = GlobalConstant.BLOOD_HGB_VALUE + "/"
                            + GlobalConstant.BLOOD_HCT_VALUE;
                } else {
                    resultStr = context.getResources().getString(R.string.unmeasure);
                }
                break;
            case KParamType.LIPIDS_CHOL:
                //血脂四项(四项值)
                if (GlobalConstant.LIPIDS_CHOL_VALUE == GlobalConstant.INVALID_DATA &&
                        GlobalConstant.LIPIDS_HDL_VALUE == GlobalConstant.INVALID_DATA &&
                        GlobalConstant.LIPIDS_LDL_VALUE == GlobalConstant.INVALID_DATA &&
                        GlobalConstant.LIPIDS_TRIG_VALUE == GlobalConstant.INVALID_DATA) {
                    resultStr = context.getResources().getString(R.string.unmeasure);
                } else {
                    int num = 0;
                    if (!judgeLipds(KParamType.LIPIDS_CHOL, GlobalConstant.LIPIDS_CHOL_VALUE)) {
                        num ++;
                    }
                    if (!judgeLipds(KParamType.LIPIDS_TRIG, GlobalConstant.LIPIDS_TRIG_VALUE)) {
                        num ++;
                    }
                    if (!judgeLipds(KParamType.LIPIDS_HDL, GlobalConstant.LIPIDS_HDL_VALUE)) {
                        num ++;
                    }
                    if (!judgeLipds(KParamType.LIPIDS_LDL, GlobalConstant.LIPIDS_LDL_VALUE)) {
                        num ++;
                    }
                    if (num > 0) {
                        resultStr = num + context.getResources()
                                .getString(R.string.item_unnormal);
                    } else {
                        resultStr = context.getResources().getString(R.string.item_normal);
                    }
                }
                break;
            case KParamType.HBA1C_NGSP:
                //糖化血红蛋白(三项值)
                if (GlobalConstant.HBA1C_NGSP == GlobalConstant.INVALID_DATA ||
                        GlobalConstant.HBA1C_IFCC == GlobalConstant.INVALID_DATA ||
                        GlobalConstant.HBA1C_EAG == GlobalConstant.INVALID_DATA) {
                    resultStr = context.getResources().getString(R.string.unmeasure);
                } else {
                    int countNum = 0;
                    if (!judgeLipds(KParamType.HBA1C_NGSP, GlobalConstant.HBA1C_NGSP)) {
                        countNum ++;
                    }
                    if (!judgeLipds(KParamType.HBA1C_IFCC, GlobalConstant.HBA1C_IFCC)) {
                        countNum ++;
                    }
                    if (!judgeLipds(KParamType.HBA1C_EAG, GlobalConstant.HBA1C_EAG)) {
                        countNum ++;
                    }
                    if (countNum > 0) {
                        resultStr = countNum + context.getResources()
                                .getString(R.string.item_unnormal);
                    } else {
                        resultStr = context.getResources().getString(R.string.item_normal);
                    }
                }
                break;
            case KParamType.BLOOD_WBC:
                //白细胞(一项值)
                if (GlobalConstant.BLOOD_WBC_VALUE != GlobalConstant.INVALID_DATA) {
                    resultStr = GlobalConstant.BLOOD_WBC_VALUE + "";
                } else {
                    resultStr = context.getResources().getString(R.string.unmeasure);
                }
                break;
            case GlobalConstant.BMI_FLAG:
                //bmi(一项值)
                if (!TextUtils.isEmpty(GlobalConstant.BMI)) {
                    resultStr = GlobalConstant.BMI + "";
                } else {
                    resultStr = context.getResources().getString(R.string.unmeasure);
                }
                break;
            default:
                break;
        }
        return resultStr;
    }

    /**
     * 统一方法判断所有测量项每项的是否异常
     * @param type 测量类型
     * @param context 上下文引用
     * @return 是否正常
     */
    public static boolean isMeasureItemNormal(int type, Context context) {
        boolean isItemNormal = true;
        switch (type) {
            case KParamType.ECG_HR:
                if (!isValueNormal(Ecg.ecgHigh, Ecg.ecgLow, GlobalConstant.ECG_PR_VALUE)) {
                    isItemNormal = false;
                }
                break;
            case KParamType.SPO2_TREND:
                if (!isValueNormal(Spo.SPO2_HIGH, Spo.SPO2_LOW, GlobalConstant.SPO2_VALUE) ||
                        !isValueNormal(Spo.PR_HIGH, Spo.PR_LOW, GlobalConstant.SPO2_PR_VALUE)) {
                    isItemNormal = false;
                }
                break;
            case KParamType.NIBP_SYS:
                if (!isValueNormal(Nibp.SYS_HIGH, Nibp.SYS_LOW, GlobalConstant.NIBP_SYS_VALUE) ||
                        !isValueNormal(Nibp.DIA_HIGH, Nibp.DIA_LOW
                                , GlobalConstant.NIBP_DIA_VALUE)) {
                    isItemNormal = false;
                }
                break;
            case KParamType.IRTEMP_TREND:
                if (!isValueNormal(IrTemp.HIGH, IrTemp.LOW, GlobalConstant.IR_TEMP_VALUE)) {
                    isItemNormal = false;
                }
                break;
            case KParamType.BLOODGLU_BEFORE_MEAL:
                float min;
                float max;
                float min1;
                float max1;
                int count = 0;
                if (GlobalConstant.BLOODGLUSTATE.equals("0")) {
                    //餐前
                    min = BeneParamValue.XT_VALUE_MIN;
                    max = BeneParamValue.XT_VALUE_MAX;
                } else {
                    //餐后
                    min = BeneParamValue.XT_AFTER_VALUE_MIN;
                    max = BeneParamValue.XT_AFTER_VALUE_MAX;
                }
                int sex = SpUtils.getSpInt(context, GlobalConstant.APP_CONFIG
                        , GlobalConstant.CURRENT_SEX, 0);
                if (sex == 0) {
                    min1 = BeneParamValue.NS_VALUE_MING;
                    max1 = BeneParamValue.NS_VALUE_MAXG;
                } else {
                    min1 = BeneParamValue.NS_VALUE_MIN;
                    max1 = BeneParamValue.NS_VALUE_MAX;
                }
                if (!isValueNormal(max, min, GlobalConstant.BLOOD_GLU_VALUE) ||
                        !isValueNormal(max1, min1, GlobalConstant.URIC_ACID_VALUE)
                        || !isValueNormal(BeneParamValue.CHOL_VALUE_MAX
                        , BeneParamValue.CHOL_VALUE_MIN, GlobalConstant.CHOLESTEROL_VALUE)) {
                    isItemNormal = false;
                }
                break;
            case KParamType.URINERT_LEU:
                if (!isUrineNormal(-1, -1, GlobalConstant.URINE_UBG_VALUE, true, context) ||
                        !isUrineNormal(-1, -1, GlobalConstant.URINE_PRO_VALUE, true, context) ||
                        !isUrineNormal(-1, -1, GlobalConstant.URINE_BIL_VALUE, true, context) ||
                        !isUrineNormal(-1, -1, GlobalConstant.URINE_GLU_VALUE, true, context) ||
                        !isUrineNormal(-1, -1, GlobalConstant.URINE_ASC_VALUE, true, context) ||
                        !isUrineNormal(-1, -1, GlobalConstant.URINE_CA_VALUE, true, context) ||
                        !isUrineNormal(-1, -1, GlobalConstant.URINE_ALB_VALUE, true, context) ||
                        !isUrineNormal(-1, -1, GlobalConstant.URINE_CRE_VALUE, true, context) ||
                        !isUrineNormal(Urine.SG_HIGH, Urine.SG_LOW
                                , GlobalConstant.URINE_SG_VALUE, false, context) ||
                        !isUrineNormal(-1, -1, GlobalConstant.URINE_KET_VALUE, true, context) ||
                        !isUrineNormal(-1, -1, GlobalConstant.URINE_NIT_VALUE, true, context) ||
                        !isUrineNormal(Urine.PH_HIGH, -Urine.PH_LOW, GlobalConstant.URINE_PH_VALUE
                                , false, context) || !isUrineNormal(-1, -1
                        , GlobalConstant.URINE_BLD_VALUE, true, context)) {
                    isItemNormal = false;
                }
                break;
            case KParamType.BLOOD_HGB:
                float maxHgb;
                float minHgb;
                float maxHct;
                float minHct;
                int sexType = SpUtils.getSpInt(context, GlobalConstant.APP_CONFIG
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
                if (!isValueNormal(maxHgb, minHgb, GlobalConstant.BLOOD_HGB_VALUE) ||
                        !isValueNormal(maxHct, minHct, GlobalConstant.BLOOD_HCT_VALUE)) {
                    isItemNormal = false;
                }
                break;
            case KParamType.LIPIDS_CHOL:
                if (!judgeLipds(KParamType.LIPIDS_CHOL, GlobalConstant.LIPIDS_CHOL_VALUE) ||
                        !judgeLipds(KParamType.LIPIDS_TRIG, GlobalConstant.LIPIDS_TRIG_VALUE) ||
                        !judgeLipds(KParamType.LIPIDS_HDL, GlobalConstant.LIPIDS_HDL_VALUE) ||
                        !judgeLipds(KParamType.LIPIDS_LDL, GlobalConstant.LIPIDS_LDL_VALUE)) {
                    isItemNormal = false;
                }
                break;
            case KParamType.HBA1C_NGSP:
                if (!judgeLipds(KParamType.HBA1C_NGSP, GlobalConstant.HBA1C_NGSP) ||
                        !judgeLipds(KParamType.HBA1C_IFCC, GlobalConstant.HBA1C_IFCC) ||
                        !judgeLipds(KParamType.HBA1C_EAG, GlobalConstant.HBA1C_EAG)) {
                    isItemNormal = false;
                }
                break;
            case KParamType.BLOOD_WBC:
                if (!isValueNormal(WbcParamValue.MAX_VALUE, WbcParamValue.MIN_VALUE
                        , GlobalConstant.BLOOD_WBC_VALUE)) {
                    isItemNormal = false;
                }
                break;
            case GlobalConstant.BMI_FLAG:
                if (!TextUtils.isEmpty(GlobalConstant.BMI)) {
                    if (!isValueNormal(BmiParam.MAX_VALUE, BmiParam.MIN_VALUE
                            , Float.valueOf(GlobalConstant.BMI))) {
                        isItemNormal = false;
                    }
                }
                break;
            default:
                break;
        }
        return isItemNormal;
    }

    /**
     * 判断结果值是否异常
     * @param min 最小值
     * @param max 最大值
     * @param value 测量值
     * @return 是否异常值
     */
    public static boolean isValueNormal(float max, float min, float value) {
        if (value == GlobalConstant.INVALID_DATA) {
            return true;
        }
        boolean isValueNormal = true;
        if (value > max || value < min) {
            isValueNormal = false;
        }
        return isValueNormal;
    }

    /**
     * 尿常规异常判断
     * @param max 最大值
     * @param min 最小值
     * @param value 测量值
     * @param context 上下文引用
     * @param isNeedSwitch 是否需要转换的值项
     * @return 是否正常
     */
    public static boolean isUrineNormal(double max, double min, double value
            , boolean isNeedSwitch, Context context) {
        if (value == GlobalConstant.INVALID_DATA) {
            return true;
        }
        boolean isNormal = true;
        if (isNeedSwitch) {
            String str = valueToString((int) value);
            if (!str.equals(context.getResources().getString(R.string.compare_flag))) {
                //异常
                isNormal = false;
            }
        } else {
            if (value > max || value < min) {
                isNormal = false;
            }
        }
        return isNormal;
    }

    /**
     * 尿常规值转换
     * @param value 模块传递过来的值
     * @return 显示测量值
     */
    private static String valueToString(int value) {
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

    /**
     * 血脂值/糖化血红蛋白值转换比较
     * @param type 类型
     * @param value 值
     * @return 是否正常值
     */
    private static boolean judgeLipds(int type, float value) {
        if (value == GlobalConstant.INVALID_DATA) {
            return true;
        }
        boolean isNormal = true;
        //超低指标
        int supLow = GlobalConstant.valueMin;
        //超高指标
        int supHigh = GlobalConstant.valueMax;
        //指标数据来源于爱康血脂分析仪
        switch (type) {
            case KParamType.LIPIDS_CHOL:
                if (value == supLow || value == supHigh) {
                    isNormal = false;
                } else if (value > GlobalConstant.LIPIDS_CHOL_ALARM_HIGH ||
                        value < GlobalConstant.LIPIDS_CHOL_ALARM_LOW) {
                    isNormal = false;
                }
                break;
            case KParamType.LIPIDS_TRIG:
                if (value == supLow || value == supHigh) {
                    isNormal = false;
                } else if (value > GlobalConstant.LIPIDS_TRIG_ALARM_HIGH ||
                        value < GlobalConstant.LIPIDS_TRIG_ALARM_LOW) {
                    isNormal = false;
                }
                break;
            case KParamType.LIPIDS_HDL:
                if (value == supLow || value == supHigh) {
                    isNormal = false;
                } else if (value > GlobalConstant.LIPIDS_HDL_ALARM_HIGH ||
                        value < GlobalConstant.LIPIDS_HDL_ALARM_LOW) {
                    isNormal = false;
                }
                break;
            case KParamType.LIPIDS_LDL:
                if (value == supLow || value == supHigh) {
                    isNormal = false;
                } else if (value > GlobalConstant.LIPIDS_LDL_ALARM_HIGH ||
                        value < GlobalConstant.LIPIDS_LDL_ALARM_LOW) {
                    isNormal = false;
                }
                break;
            case KParamType.HBA1C_NGSP:
                if (value == supLow || value == supHigh) {
                    isNormal = false;
                } else if (value > SugarBloodParam.NGSP_MAX || value < SugarBloodParam.NGSP_MIN) {
                    isNormal = false;
                }
                break;
            case KParamType.HBA1C_IFCC:
                if (value == supLow || value == supHigh) {
                    isNormal = false;
                } else if (value > SugarBloodParam.IFCC_MAX ||
                        value < SugarBloodParam.IFCC_MIN) {
                    isNormal = false;
                }
                break;
            case KParamType.HBA1C_EAG:
                if (value == supLow || value == supHigh) {
                    isNormal = false;
                } else if (value > SugarBloodParam.EAG_MAX ||
                        value < SugarBloodParam.EAG_MIN) {
                    isNormal = false;
                }
                break;
            default:
                break;
        }
        return isNormal;
    }

    /**
     * 设置全局变量值为空
     */
    public static void setGlobalValueNull() {
        GlobalConstant.SPO2_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.SPO2_PR_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.ECG_PR_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.NIBP_SYS_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.NIBP_DIA_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.NIBP_MAP_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.NIBP_PR_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.TEMP_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.IR_TEMP_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.BLOOD_GLU_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.BLOODGLUSTATE = "0";
        GlobalConstant.URINE_LEU_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_NIT_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_UBG_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_PRO_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_PH_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_SG_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_BLD_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_KET_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_BIL_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_GLU_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_ASC_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_ALB_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_CRE_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URINE_CA_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.BLOOD_WBC_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.BLOOD_HGB_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.BLOOD_HCT_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.URIC_ACID_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.CHOLESTEROL_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.LIPIDS_CHOL_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.LIPIDS_HDL_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.LIPIDS_LDL_VALUE = GlobalConstant.INVALID_DATA;
        GlobalConstant.LIPIDS_TRIG_VALUE = GlobalConstant.INVALID_DATA;
        //糖化
        GlobalConstant.HBA1C_NGSP = GlobalConstant.INVALID_DATA;
        GlobalConstant.HBA1C_IFCC = GlobalConstant.INVALID_DATA;
        GlobalConstant.HBA1C_EAG = GlobalConstant.INVALID_DATA;
        //BMI
        GlobalConstant.BMI = "";
        GlobalConstant.HEIGHT = "";
        GlobalConstant.WEIGHT = "";
        //白细胞
        GlobalConstant.BLOOD_WBC_VALUE = GlobalConstant.INVALID_DATA;
    }

    /**
     * 初始化全局变量有效值
     * @param measureDataBean 测量记录
     */
    public static void setGlobalValueInit(MeasureDataBean measureDataBean) {
        GlobalConstant.SPO2_VALUE = (int) UiUitls
                .getMesue(measureDataBean.getTrendValue(KParamType.SPO2_TREND));
        GlobalConstant.SPO2_PR_VALUE = (int) UiUitls
                .getMesue(measureDataBean.getTrendValue(KParamType.SPO2_PR));
        GlobalConstant.ECG_PR_VALUE = (int) UiUitls
                .getMesue(measureDataBean.getTrendValue(KParamType.ECG_HR));
        GlobalConstant.NIBP_SYS_VALUE = (int) UiUitls
                .getMesue(measureDataBean.getTrendValue(KParamType.NIBP_SYS));
        GlobalConstant.NIBP_DIA_VALUE = (int) UiUitls
                .getMesue(measureDataBean.getTrendValue(KParamType.NIBP_DIA));
        GlobalConstant.NIBP_MAP_VALUE = (int) UiUitls
                .getMesue(measureDataBean.getTrendValue(KParamType.NIBP_MAP));
        GlobalConstant.NIBP_PR_VALUE = (int) UiUitls
                .getMesue(measureDataBean.getTrendValue(KParamType.NIBP_PR));
        GlobalConstant.TEMP_VALUE = UiUitls.getMesue(
                measureDataBean.getTrendValue(KParamType.TEMP_T1));
        GlobalConstant.IR_TEMP_VALUE = UiUitls.getMesue(
                measureDataBean.getTrendValue(KParamType.IRTEMP_TREND));
        GlobalConstant.BLOOD_GLU_VALUE = UiUitls
                .getMesue(measureDataBean.getTrendValue(KParamType.BLOODGLU_BEFORE_MEAL));
        //血糖测量时状态
        GlobalConstant.BLOODGLUSTATE = measureDataBean.getGluStyle();
        GlobalConstant.URINE_LEU_VALUE = (int)
                UiUitls.getMesue(measureDataBean.getTrendValue(KParamType.URINERT_LEU));
        GlobalConstant.URINE_NIT_VALUE = (int)
                UiUitls.getMesue(measureDataBean.getTrendValue(KParamType.URINERT_NIT));
        GlobalConstant.URINE_UBG_VALUE = (int)
                UiUitls.getMesue(measureDataBean.getTrendValue(KParamType.URINERT_UBG));
        GlobalConstant.URINE_PRO_VALUE = (int)
                UiUitls.getMesue(measureDataBean.getTrendValue(KParamType.URINERT_PRO));
        GlobalConstant.URINE_PH_VALUE = UiUitls
                .getMesue(measureDataBean.getTrendValue(KParamType.URINERT_PH));
        int sgValue = measureDataBean.getTrendValue(KParamType.URINERT_SG);
        GlobalConstant.URINE_SG_VALUE = sgValue == GlobalConstant.INVALID_DATA
                ? GlobalConstant.INVALID_DATA : sgValue / GlobalConstant.THOSOUND_VALUE;
        GlobalConstant.URINE_BLD_VALUE = (int)
                UiUitls.getMesue(measureDataBean.getTrendValue(KParamType.URINERT_BLD));
        GlobalConstant.URINE_KET_VALUE = (int)
                UiUitls.getMesue(measureDataBean.getTrendValue(KParamType.URINERT_KET));
        GlobalConstant.URINE_BIL_VALUE = (int)
                UiUitls.getMesue(measureDataBean.getTrendValue(KParamType.URINERT_BIL));
        GlobalConstant.URINE_GLU_VALUE = (int)
                UiUitls.getMesue(measureDataBean.getTrendValue(KParamType.URINERT_GLU));
        GlobalConstant.URINE_ASC_VALUE = (int)
                UiUitls.getMesue(measureDataBean.getTrendValue(KParamType.URINERT_ASC));
        GlobalConstant.URINE_ALB_VALUE = UiUitls
                .getMesue(measureDataBean.getTrendValue(KParamType.URINERT_ALB));
        GlobalConstant.URINE_CRE_VALUE = UiUitls
                .getMesue(measureDataBean.getTrendValue(KParamType.URINERT_CRE));
        GlobalConstant.URINE_CA_VALUE = UiUitls
                .getMesue(measureDataBean.getTrendValue(KParamType.URINERT_CA));
        GlobalConstant.BLOOD_WBC_VALUE = UiUitls
                .getMesue(measureDataBean.getTrendValue(KParamType.BLOOD_WBC));
        GlobalConstant.BLOOD_HGB_VALUE = UiUitls
                .getMesue(measureDataBean.getTrendValue(KParamType.BLOOD_HGB));
        GlobalConstant.BLOOD_HCT_VALUE = (int) (UiUitls.getMesue(measureDataBean
                .getTrendValue(KParamType.BLOOD_HCT)));
        GlobalConstant.URIC_ACID_VALUE = UiUitls
                .getMesue(measureDataBean.getTrendValue(KParamType.URICACID_TREND));
        GlobalConstant.CHOLESTEROL_VALUE = UiUitls
                .getMesue(measureDataBean.getTrendValue(KParamType.CHOLESTEROL_TREND));
        GlobalConstant.LIPIDS_CHOL_VALUE = UiUitls.getTrendFloat(measureDataBean
                , KParamType.LIPIDS_CHOL);
        GlobalConstant.LIPIDS_HDL_VALUE = UiUitls.getTrendFloat(measureDataBean
                , KParamType.LIPIDS_HDL);
        GlobalConstant.LIPIDS_LDL_VALUE = UiUitls.getTrendFloat(measureDataBean
                , KParamType.LIPIDS_LDL);
        GlobalConstant.LIPIDS_TRIG_VALUE = UiUitls.getTrendFloat(measureDataBean
                , KParamType.LIPIDS_TRIG);
        //糖化
        GlobalConstant.HBA1C_NGSP = UiUitls.getTrendFloat(measureDataBean
                , KParamType.HBA1C_NGSP);
        GlobalConstant.HBA1C_IFCC = UiUitls.getTrendFloat(measureDataBean
                , KParamType.HBA1C_IFCC);
        GlobalConstant.HBA1C_EAG = UiUitls.getTrendFloat(measureDataBean
                , KParamType.HBA1C_EAG);
        //BMI
        GlobalConstant.BMI = measureDataBean.getBmi();
        GlobalConstant.HEIGHT = measureDataBean.getHeight();
        GlobalConstant.WEIGHT = measureDataBean.getWeight();
        //白细胞
        GlobalConstant.BLOOD_WBC_VALUE = UiUitls.getTrendFloat(measureDataBean
                , KParamType.BLOOD_WBC);
        int paramValue = SpUtils.getSpInt(UiUitls.getContent(), GlobalConstant.PARAM_CONFIGS
                , GlobalConstant.PARAM_KEY, GlobalConstant.PARAM_CONFIG);
        measureDataBean.setParamValue(paramValue);
        DBDataUtil.getMeasureDao().update(measureDataBean);
    }
}
