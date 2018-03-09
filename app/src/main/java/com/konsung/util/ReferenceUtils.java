package com.konsung.util;

import android.content.res.Resources;

import com.konsung.R;
import com.konsung.bean.ReferenceValueBean;
import com.konsung.data.ProviderReader;
import com.konsung.util.global.BloodMem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 参考值工具类
 * 血糖说明：
 * appdevice数据上传时，只上传餐前的
 * 保存数据时，区分餐前餐后
 */
public class ReferenceUtils {

    /**
     *
     */
    public static ReferenceValueBean bean;

    /**
     * 值低于参考值
     */
    public static final int FLAG_VALUE_BELOW = -1;
    /**
     * 值正常
     */
    public static final int FLAG_VALUE_NORMAL = 0;
    /**
     * 值高于参考值
     */
    public static final int FLAG_VALUE_ABOVE = 1;

    /**
     * 性别-男
     */
    public static final int MALE = 0;
    /**
     * 性别女
     */
    public static final int FEMALE = 1;

    /**
     * 年龄阶段 成年人
     */
    public static final int LEVEL_ADULT = 0;
    /**
     * 年龄阶段 青年(儿童)
     */
    public static final int LEVEL_YOUTH = 1;
    /**
     * 年龄阶段 新生儿
     */
    public static final int LEVEL_BOBY = 2;

    /**
     * 出生后 28天(暂行) 属于新生儿
     */
    public static final int DAY_NEW_BOBY = 28;

    //心率参考值
    private static final float ECG_PR_REFERENCE_MAX = 100f;
    private static final float ECG_PR_REFERENCE_MIN = 60f;

    //血氧参考值
    private static final float SPO2_TREND_REFERENCE_MAX = 100f;
    private static final float SPO2_TREND_REFERENCE_MIN = 94f;
    //血氧脉率
    private static final float SPO2_PR_REFERENCE_MAX = 100f;
    private static final float SPO2_PR_REFERENCE_MIN = 60f;

    //血压-收缩压 -不含140
    private static final float NIBP_SYS_REFERENCE_MAX = 140f;
    private static final float NIBP_SYS_REFERENCE_MIN = 90f;
    //血压舒张压  -不含90
    private static final float NIBP_DIA_REFERENCE_MAX = 90f;
    private static final float NIBP_DIA_REFERENCE_MIN = 60f;

    //额温 -不含37.3
    private static final float IRTEMP_REFERENCE_MAX = 37.2f;
    private static final float IRTEMP_REFERENCE_MIN = 36.2f;

    //血糖 餐前 -不含7.0
    private static final float GLU_BEFORE_MEAL_REFERENCE_MAX = 7.2f;
    private static final float GLU_BEFORE_MEAL_REFERENCE_MIN = 3.9f;
    //血糖 餐后  <11
    private static final float GLU_AFTER_MEAL_REFERENCE_MAX = 10f;
    private static final float GLU_AFTER_MEAL_REFERENCE_MIN = 0.0f;

    //百捷三合一 尿酸
    private static final float URICACID_TREND_REFERENCE_MALE_MAX = 0.42f;
    private static final float URICACID_TREND_REFERENCE_MALE_MIN = 0.2f;
    private static final float URICACID_TREND_REFERENCE_FEMALE_MAX = 0.36f;
    private static final float URICACID_TREND_REFERENCE_FEMALE_MIN = 0.14f;
    //百捷三合一 总胆固醇
    private static final float CHOLESTEROL_TREND_REFERENCE_MAX = 5.2f;
    private static final float CHOLESTEROL_TREND_REFERENCE_MIN = 0f;

    //尿常规的 尿钙，肌酐都是不需要参考值的
    //尿常规-尿比重
    private static final float URINE_TEST_SG_REFERENCE_MAX = 1.025f;
    private static final float URINE_TEST_SG_REFERENCE_MIN = 1.015f;
    //尿常规-ph
    private static final float URINE_TEST_PH_REFERENCE_MAX = 8.0f;
    private static final float URINE_TEST_PH_REFERENCE_MIN = 5.0f;

    //白细胞
    private static final float BLOOD_WBC_REFERENCE_MAX = 10f;
    private static final float BLOOD_WBC_REFERENCE_MIN = 4f;

    //血红蛋白
    //成年人 g/L
    private static final float BLOOD_HGB_REFERENCE_MALE_MAX = 170f;
    private static final float BLOOD_HGB_REFERENCE_MALE_MIN = 130f;
    private static final float BLOOD_HGB_REFERENCE_FEMALE_MAX = 150f;
    private static final float BLOOD_HGB_REFERENCE_FEMALE_MIN = 120f;

    //红细胞挤压值
    private static final float BLOOD_HCT_REFERENCE_MALE_MAX = 50f;
    private static final float BLOOD_HCT_REFERENCE_MALE_MIN = 40f;

    private static final float BLOOD_HCT_REFERENCE_FEMALE_MAX = 45f;
    private static final float BLOOD_HCT_REFERENCE_FEMALE_MIN = 37f;
    //血脂四项
    //总胆固醇
    private static final float BLOOD_FAT_CHO_REFERENCE_MAX = 5.69f;
    private static final float BLOOD_FAT_CHO_REFERENCE_MIN = 2.59f;
    //甘油三酯
    private static final float BLOOD_FAT_TRIG_REFERENCE_MAX = 1.7f;
    private static final float BLOOD_FAT_TRIG_REFERENCE_MIN = 0.56f;
    //高密度脂蛋白 >
    private static final float BLOOD_FAT_HDL_REFERENCE_MAX = 2.07f;
    private static final float BLOOD_FAT_HDL_REFERENCE_MIN = 0.91f;
    //低密度脂蛋白
    private static final float BLOOD_FAT_LDL_REFERENCE_MAX = 3.16f;
    private static final float BLOOD_FAT_LDL_REFERENCE_MIN = 1.29f;

    //糖化血红蛋白
    //NGSP标准
    private static final float HBA1C_NGSP_REFERENCE_MAX = 6f;
    private static final float HBA1C_NGSP_REFERENCE_MIN = 4.3f;
    //IFCC标准
    private static final float HBA1C_IFCC_REFERENCE_MAX = 42.1f;
    private static final float HBA1C_IFCC_REFERENCE_MIN = 23.5f;
    //平均血糖浓度
    private static final float EAG_REFERENCE_MAX = 125.5f;
    private static final float EAG_REFERENCE_MIN = 76.7f;

    //艾康U120尿常规尿钙
    private static final int URINE_U120_CA_MAX = 30;
    private static final int URINE_U120_CA_MIN = 10;

    //艾康U120尿常规微量白蛋白
    private static final int URINE_U120_ALB = 10;

    //最大身高输入值
    private static final float HEIGHT = 300f;
    //最大体重输入值，不要歧视胖子
    private static final float WEIGHT = 1000f;

    /**
     * 初始化参考值,需要根据性别，年龄设置
     * @param u120Status 兼容U120参考值
     * @param level 阶段等级
     * @param gender 性别 {可能值{@link #MALE} {@link #FEMALE}}
     * @return 参考值数据集 {@link ReferenceValueBean}
     */
    public static ReferenceValueBean initReferenceValueBean(boolean u120Status, int level,
            int gender) {
        ReferenceValueBean bean = new ReferenceValueBean();
        bean.setEcgPrMax(ECG_PR_REFERENCE_MAX);
        bean.setEcgPrMin(ECG_PR_REFERENCE_MIN);
        bean.setSpo2TrendMax(SPO2_TREND_REFERENCE_MAX);
        bean.setSpo2TrendMin(SPO2_TREND_REFERENCE_MIN);
        bean.setSpo2PrMax(SPO2_PR_REFERENCE_MAX);
        bean.setSpo2PrMin(SPO2_PR_REFERENCE_MIN);
        bean.setNibpSysMax(NIBP_SYS_REFERENCE_MAX);
        bean.setNibpSysMin(NIBP_SYS_REFERENCE_MIN);
        bean.setNibpDiaMax(NIBP_DIA_REFERENCE_MAX);
        bean.setNibpDiaMin(NIBP_DIA_REFERENCE_MIN);
        bean.setIrtempMax(IRTEMP_REFERENCE_MAX);
        bean.setIrtempMin(IRTEMP_REFERENCE_MIN);
        bean.setGluBeforeMealMax(GLU_BEFORE_MEAL_REFERENCE_MAX);
        bean.setGluBeforeMealMin(GLU_BEFORE_MEAL_REFERENCE_MIN);
        bean.setGluAfterMealMax(GLU_AFTER_MEAL_REFERENCE_MAX);
        bean.setGluAfterMealMin(GLU_AFTER_MEAL_REFERENCE_MIN);

        bean.setCholesterolMax(CHOLESTEROL_TREND_REFERENCE_MAX);
        bean.setCholesterolMin(CHOLESTEROL_TREND_REFERENCE_MIN);
        bean.setUriPHMax(URINE_TEST_PH_REFERENCE_MAX);
        bean.setUriPHMin(URINE_TEST_PH_REFERENCE_MIN);
        bean.setUriSGMax(URINE_TEST_SG_REFERENCE_MAX);
        bean.setUriSGMin(URINE_TEST_SG_REFERENCE_MIN);
        bean.setWbcMax(BLOOD_WBC_REFERENCE_MAX);
        bean.setWbcMin(BLOOD_WBC_REFERENCE_MIN);
        if (GlobalConstant.currentSex == 0) {
            //女性
            bean.setHgbMax(BloodMem.WOMAN_BLOOD_MAX);
            bean.setHgbMin(BloodMem.WOMAN_BLOOD_MIN);
            bean.setHctMax(BloodMem.WOMAN_HOGIN_MAX);
            bean.setHctMin(BloodMem.WOMAN_HOGIN_MIN);

            //尿酸区分男女，细胞压积值区分男女
            bean.setUricacidMax(URICACID_TREND_REFERENCE_FEMALE_MAX);
            bean.setUricacidMin(URICACID_TREND_REFERENCE_FEMALE_MIN);
        } else {
            //男性
            bean.setHgbMax(BloodMem.MAN_BLOOD_MAX);
            bean.setHgbMin(BloodMem.MAN_BLOOD_MIN);
            bean.setHctMax(BloodMem.MAN_HOGIN_MAX);
            bean.setHctMin(BloodMem.MAN_HOGIN_MIN);

            //尿酸区分男女，细胞压积值区分男女
            bean.setUricacidMax(URICACID_TREND_REFERENCE_MALE_MAX);
            bean.setUricacidMin(URICACID_TREND_REFERENCE_MALE_MIN);
        }
        bean.setHbA1cIFCCMax(HBA1C_IFCC_REFERENCE_MAX);
        bean.setHbA1cIFCCMin(HBA1C_IFCC_REFERENCE_MIN);
        bean.setHbA1cNGSPMax(HBA1C_NGSP_REFERENCE_MAX);
        bean.setHbA1cNGSPMin(HBA1C_NGSP_REFERENCE_MIN);
        bean.seteAGMax(EAG_REFERENCE_MAX);
        bean.seteAGMin(EAG_REFERENCE_MIN);
        bean.setBloodCholMax(BLOOD_FAT_CHO_REFERENCE_MAX);
        bean.setBloodCholMin(BLOOD_FAT_CHO_REFERENCE_MIN);
        bean.setBloodTrigMax(BLOOD_FAT_TRIG_REFERENCE_MAX);
        bean.setBloodTrigMin(BLOOD_FAT_TRIG_REFERENCE_MIN);
        bean.setBloodHDLMax(BLOOD_FAT_HDL_REFERENCE_MAX);
        bean.setBloodHDLMin(BLOOD_FAT_HDL_REFERENCE_MIN);
        bean.setBloodLDLMax(BLOOD_FAT_LDL_REFERENCE_MAX);
        bean.setBloodLDLMin(BLOOD_FAT_LDL_REFERENCE_MIN);
        bean.setHeight(HEIGHT);
        bean.setWeight(WEIGHT);
        return bean;
    }

    /**
     * 根据参数获取最大参考值
     * @param param 测量项参数
     * @return 最大值
     */
    public static float getMaxReference(int param) {
        if (bean == null) {
            initPersonalReference();
        }
        return bean.getMaxReference(param);
    }

    /**
     * 初始化个人参考值(暂行只适配身份证的，后面还要考虑身份证不规范和错误)
     * 如果没做兼容，将会展示成人男性的参考值
     */
    public static void initPersonalReference() {
        String currentIdCard = ProviderReader.readPatient().getIdCard();
        //是否使用艾康U120测量尿常规
        boolean usedU120 = UiUitls.getU120UseStatus();

        //18位身份证，暂时只做18位身份证
        if (currentIdCard.length() == 18) {
            char s = currentIdCard.charAt(16);
            String born = currentIdCard.substring(6, 14);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            try {
                Date date = sdf.parse(born);
                int gender = 0;

                int sex = Integer.valueOf(s);
                if (sex % 2 == 0) {
                    gender = FEMALE;
                } else if (sex % 2 == 1) {
                    gender = MALE;
                }
                int level = getPeopleLevel(date);
                bean = initReferenceValueBean(usedU120, level, gender);
            } catch (ParseException e) {
                e.printStackTrace();
                bean = initReferenceValueBean(usedU120, LEVEL_ADULT, MALE);
            }
        } else {
            bean = initReferenceValueBean(usedU120, LEVEL_ADULT, MALE);
        }
    }

    /**
     * 获取人类年龄阶段
     * @param bornDate 生日
     * @return 阶段等级
     */
    private static int getPeopleLevel(Date bornDate) {
        Calendar bornCalendar = Calendar.getInstance();
        bornCalendar.setTime(bornDate);
        //如果出生日期比当日时间迟，则默认为成人
        if (bornCalendar.after(Calendar.getInstance())) {
            return LEVEL_ADULT;
        }
        int age = IdCardUtil.getAge(bornDate);
        if (age < 0 || age >= 18) {
            return LEVEL_ADULT;
        } else if (age > 0) {
            return LEVEL_YOUTH;
        } else {
            //出生日期+28天，比当前日期晚，则是新生儿
            bornCalendar.add(Calendar.DATE, DAY_NEW_BOBY);
            if (bornCalendar.after(Calendar.getInstance())) {
                return LEVEL_BOBY;
            } else {
                return LEVEL_YOUTH;
            }
        }
    }

    /**
     * 根据参数获取参考值最小值
     * @param param 测量项参数{@link KParamType}
     * @return 最小值
     */
    public static float getMinReference(int param) {
        if (bean == null) {
            initPersonalReference();
        }
        return bean.getMinReference(param);
    }

    /**
     * 获取参考值范围
     * @param param 测量项参数{@link KParamType}
     * @return 参考值范围字符串
     */
    public static String getReferenceRange(int param) {
        String strRange = "";
        switch (param) {
            case KParamType.URINERT_SG:
            case KParamType.ECG_HR:
            case KParamType.SPO2_TREND:
            case KParamType.SPO2_PR:
            case KParamType.NIBP_SYS:
            case KParamType.NIBP_DIA:
            case KParamType.IRTEMP_TREND:
            case KParamType.BLOODGLU_AFTER_MEAL:
            case KParamType.BLOODGLU_BEFORE_MEAL:
            case KParamType.CHOLESTEROL_TREND:
            case KParamType.URICACID_TREND:
            case KParamType.BLOOD_WBC:
            case KParamType.BLOOD_HGB:
            case KParamType.BLOOD_HCT:
            case KParamType.GHB_HBA1C_NGSP:
            case KParamType.GHB_HBA1C_IFCC:
            case KParamType.GHB_EAG:
            case KParamType.BLOOD_FAT_CHO:
            case KParamType.BLOOD_FAT_TRIG:
            case KParamType.BLOOD_FAT_HDL:
            case KParamType.BLOOD_FAT_LDL:
                String min = StringUtil.deleteEnd0(getMinReference(param) + "");
                String max = StringUtil.deleteEnd0(getMaxReference(param) + "");
                strRange = min + "-" + max;
                break;
            case KParamType.URINERT_PH:
                strRange = getMinReference(param) + "-" + getMaxReference(param);
                break;
            case KParamType.URINERT_LEU:
            case KParamType.URINERT_NIT:
            case KParamType.URINERT_UBG:
            case KParamType.URINERT_PRO:
            case KParamType.URINERT_BLD:
            case KParamType.URINERT_KET:
            case KParamType.URINERT_BIL:
            case KParamType.URINERT_GLU:
            case KParamType.URINERT_VC:
            case KParamType.URINERT_ASC:
                strRange = UiUitls.getString(R.string.uri_reference_value);
                break;
            case KParamType.URINERT_ALB:
                if (UiUitls.getU120UseStatus()) {
                    strRange = URINE_U120_ALB + " " + UiUitls.getString(R.string.x_measure_unit_mg);
                } else {
                    strRange = UiUitls.getString(R.string.uri_reference_value);
                }
                break;
            case KParamType.URINERT_CA:
                if (UiUitls.getU120UseStatus()) {
                    strRange = URINE_U120_CA_MIN + "-" + URINE_U120_CA_MAX +
                            UiUitls.getString(R.string.health_unit_dl);
                } else {
                    strRange = UiUitls.getString(R.string.uri_reference_value);
                }
                break;

            case KParamType.URINERT_CRE:
                if (UiUitls.getU120UseStatus()) {
                    strRange = "/";
                } else {
                    strRange = UiUitls.getString(R.string.uri_reference_value);
                }
                break;
            default:
                strRange = "-";
                break;
        }
        return strRange;
    }

    /**
     * 获取参考值范围
     * @param param 测量项参数{@link KParamType}
     * @return 参考值范围字符串
     */
    public static String getReferenceRangeInQuickCheck(int param) {
        String strRange = "";
        switch (param) {
            case KParamType.URINERT_SG:
            case KParamType.ECG_HR:
            case KParamType.SPO2_TREND:
            case KParamType.SPO2_PR:
            case KParamType.NIBP_SYS:
            case KParamType.NIBP_DIA:
            case KParamType.IRTEMP_TREND:
            case KParamType.BLOODGLU_AFTER_MEAL:
            case KParamType.BLOODGLU_BEFORE_MEAL:
            case KParamType.CHOLESTEROL_TREND:
            case KParamType.URICACID_TREND:
            case KParamType.BLOOD_WBC:
            case KParamType.BLOOD_HGB:
            case KParamType.BLOOD_HCT:
            case KParamType.GHB_HBA1C_NGSP:
            case KParamType.GHB_HBA1C_IFCC:
            case KParamType.GHB_EAG:
            case KParamType.BLOOD_FAT_CHO:
            case KParamType.BLOOD_FAT_TRIG:
            case KParamType.BLOOD_FAT_HDL:
            case KParamType.BLOOD_FAT_LDL:
                String min = StringUtil.deleteEnd0(getMinReference(param) + "");
                String max = StringUtil.deleteEnd0(getMaxReference(param) + "");
                strRange = min + "-" + max;
                break;
            case KParamType.URINERT_PH:
                strRange = getMinReference(param) + "-" + getMaxReference(param);
                break;
            case KParamType.URINERT_LEU:
            case KParamType.URINERT_NIT:
            case KParamType.URINERT_UBG:
            case KParamType.URINERT_PRO:
            case KParamType.URINERT_BLD:
            case KParamType.URINERT_KET:
            case KParamType.URINERT_BIL:
            case KParamType.URINERT_GLU:
            case KParamType.URINERT_VC:
            case KParamType.URINERT_ASC:
                strRange = UiUitls.getString(R.string.uri_default_value);
                break;
            case KParamType.URINERT_ALB:
                if (UiUitls.getU120UseStatus()) {
                    strRange = URINE_U120_ALB + " " + UiUitls.getString(R.string.x_measure_unit_mg);
                } else {
                    strRange = UiUitls.getString(R.string.uri_default_value);
                }
                break;
            case KParamType.URINERT_CA:
                if (UiUitls.getU120UseStatus()) {
                    strRange = URINE_U120_CA_MIN + "-" + URINE_U120_CA_MAX +
                            UiUitls.getString(R.string.health_unit_dl);
                } else {
                    strRange = UiUitls.getString(R.string.uri_default_value);
                }
                break;

            case KParamType.URINERT_CRE:
                if (UiUitls.getU120UseStatus()) {
                    strRange = "-";
                } else {
                    strRange = UiUitls.getString(R.string.uri_default_value);
                }
                break;
            default:
                strRange = "-";
                break;
        }
        return strRange;
    }

    /**
     * 与参考值比较
     * @param param 测量项参数{@link KParamType}
     * @param value 测量结果-原值
     * @return 比较结果 可能值:
     * {@link #FLAG_VALUE_ABOVE}
     * {@link #FLAG_VALUE_BELOW}
     * {@link #FLAG_VALUE_NORMAL}
     * @throws ReferenceValueException 转化结果不正确
     */
    public static int compareWithReference(int param, int value) throws ReferenceValueException {
        if (value == GlobalConstant.INVALID_DATA) {
            return FLAG_VALUE_NORMAL;
        }
        int resultCode = FLAG_VALUE_NORMAL;
        switch (param) {
            case KParamType.URINERT_LEU:
            case KParamType.URINERT_NIT:
            case KParamType.URINERT_UBG:
            case KParamType.URINERT_PRO:
            case KParamType.URINERT_BLD:
            case KParamType.URINERT_KET:
            case KParamType.URINERT_BIL:
            case KParamType.URINERT_GLU:
            case KParamType.URINERT_VC:
            case KParamType.URINERT_ASC:
                resultCode = getUrtCompare(param, value);
                break;
            case KParamType.URINERT_ALB:
                value = value / (int) UiUitls.getFactor(param);

                //-1 ->10mg/L ,1->30mg/L ,2->80mg/L ,3->150mg/L
                //在U120中，10mg/L是正常值
                if (value != -1) {
                    resultCode = FLAG_VALUE_ABOVE;
                } else {
                    resultCode = FLAG_VALUE_NORMAL;
                }
                break;
            case KParamType.URINERT_CRE:
                //CRE 在U120中都是正常值
                if (UiUitls.getU120UseStatus()) {
                    resultCode = FLAG_VALUE_NORMAL;
                } else {
                    resultCode = getUrtCompare(param, value);
                }
                break;
            case KParamType.URINERT_CA:
                if (UiUitls.getU120UseStatus()) {
                    value = value / (int) UiUitls.getFactor(param);
                    //-1 ->4mg/dL ,1->10mg/dL ,2->20mg/dL ,3->30mg/dL ,4->40mg/dL
                    //在U120中，10-30mg/L是正常值
                    if (value == -1) {
                        resultCode = FLAG_VALUE_BELOW;
                    } else if (value > 3) {
                        resultCode = FLAG_VALUE_ABOVE;
                    } else {
                        resultCode = FLAG_VALUE_NORMAL;
                    }
                } else {
                    resultCode = getUrtCompare(param, value);
                }
                break;
            default:
                return compare(param, value);
        }
        return resultCode;
    }

    /**
     * 比较尿常规
     * @param param 参数
     * @param value 结果
     * @return 比较结果
     * @throws ReferenceValueException 转化异常
     */
    private static int getUrtCompare(int param, int value) throws ReferenceValueException {
        value = value / (int) UiUitls.getFactor(param);
        String vStr = valueToString(value);
        if ("-".equals(vStr) || "Normal".equals(vStr)) {
            return FLAG_VALUE_NORMAL;
        } else if (vStr.contains(">") || vStr.contains("+")) {
            return FLAG_VALUE_ABOVE;
        } else if (vStr.contains("<")) {
            return FLAG_VALUE_BELOW;
        } else {
            throw new ReferenceValueException(UiUitls.getString(
                    R.string.urine_value_convert_error));
        }
    }

    /**
     * 比较
     * @param param 测量项参数
     * @param value 比较值，传入原值
     * @return 大小标志 {@link #FLAG_VALUE_BELOW}{@link #FLAG_VALUE_NORMAL}{@link #FLAG_VALUE_ABOVE}
     * @throws ReferenceValueException 转化结果不正确
     */
    private static int compare(int param, int value) throws ReferenceValueException {
        if (value == OverCheckUtil.FLAG_OVER_MAX) {
            return FLAG_VALUE_ABOVE;
        } else if (value == OverCheckUtil.FLAG_BELOW_MIN) {
            return FLAG_VALUE_BELOW;
        } else {
            float factor = GlobalConstant.FACTOR;
            float convertedValue = GlobalConstant.INVALID_DATA;
            //尿比重的倍数不一样
            if (KParamType.URINERT_SG == param) {
                factor = GlobalConstant.SG_FACTOR;
            }
            convertedValue = value / factor;
            if (convertedValue > getMaxReference(param)) {
                return FLAG_VALUE_ABOVE;
            } else if (convertedValue < getMinReference(param)) {
                return FLAG_VALUE_BELOW;
            } else {
                return FLAG_VALUE_NORMAL;
            }
        }
    }

    /**
     * 尿常规值转换
     * @param value 模块传递过来的值
     * @return 显示测量值
     */
    public static String valueToString(int value) {
        Resources resources = UiUitls.getContent().getResources();
        final String[] cas = resources.getStringArray(R.array.uri_value);
        switch (value) {
            case -1:
                return cas[0];
            case 0:
                return cas[1];
            case 1:
                return cas[2];
            case 2:
                return cas[3];
            case 3:
                return cas[4];
            case 4:
                return cas[5];
            case 5:
                return cas[6];
            case 6:
                return cas[7];
            case 7:
                return cas[8];
            case 8:
                return cas[9];
            case 9:
                return cas[10];
            case 10:
                return cas[11];
            case 11:
                return cas[12];
            case 12:
                return cas[13];
            default:
                return String.valueOf(value);
        }
    }
}
