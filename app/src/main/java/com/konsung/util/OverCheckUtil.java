package com.konsung.util;

/**
 * 超出测量范围工具类
 * 会上传 -10，-100
 **/
public class OverCheckUtil {

    //高于测量范围
    public static final int FLAG_OVER_MAX = -100; //大于标识符
    //低于测量范围
    public static final int FLAG_BELOW_MIN = -10; //小于标识符

    /**
     * 糖化血红蛋白NGSP标准
     */
    private static final String GHB_HBA1C_NGSP_ABOVE = ">15.0";
    private static final String GHB_HBA1C_NGSP_BELOW = "<3.0";

    /**
     * 糖化血红蛋白IFCC标准
     */
    private static final String GHB_HBA1C_IFCC_ABOVE = ">140.4";
    private static final String GHB_HBA1C_IFCC_BELOW = "<9.3";
    /**
     * 糖化，平均血糖浓度
     */
    private static final String GHB_EAG_ABOVE = ">383.8";
    private static final String GHB_EAG_BELOW = "<39.4";

    /**
     * 来自：艾康血脂说明书
     */
    private static final String LIPIDS_CHOL_ALARM_ABOVE = ">12.93";
    private static final String LIPIDS_CHOL_ALARM_BELOW = "<2.59";
    private static final String LIPIDS_TRIG_ALARM_ABOVE = ">7.34";
    private static final String LIPIDS_TRIG_ALARM_BELOW = "<0.51";
    private static final String LIPIDS_HDL_ALARM_ABOVE = ">2.59";
    private static final String LIPIDS_HDL_ALARM_BELOW = "<0.39";
    private static final String LIPIDS_LDL_ALARM_ABOVE = ">3.16";
    private static final String LIPIDS_LDL_ALARM_BELOW = "<1.29";

    /**
     * 获取超出上限显示值
     * @param param 参数
     * @param value 上传值
     * @return 显示值
     */
    public static String getOverMaxString(int param, float value) {
        switch (param) {
            case KParamType.GHB_EAG:
                return GHB_EAG_ABOVE;
            case KParamType.GHB_HBA1C_NGSP:
                return GHB_HBA1C_NGSP_ABOVE;
            case KParamType.GHB_HBA1C_IFCC:
                return GHB_HBA1C_IFCC_ABOVE;
            case KParamType.BLOOD_FAT_TRIG:
                return LIPIDS_TRIG_ALARM_ABOVE;
            case KParamType.BLOOD_FAT_CHO:
                return LIPIDS_CHOL_ALARM_ABOVE;
            case KParamType.BLOOD_FAT_HDL:
                return LIPIDS_HDL_ALARM_ABOVE;
            case KParamType.BLOOD_FAT_LDL:
                return LIPIDS_LDL_ALARM_ABOVE;

            default:
                return String.valueOf(value);
        }
    }

    /**
     * 获取低于下限显示值
     * @param param 参数
     * @param value 上传值
     * @return 显示值
     */
    public static String getOverMinString(int param, float value) {
        switch (param) {
            case KParamType.GHB_EAG:
                return GHB_EAG_BELOW;
            case KParamType.GHB_HBA1C_NGSP:
                return GHB_HBA1C_NGSP_BELOW;
            case KParamType.GHB_HBA1C_IFCC:
                return GHB_HBA1C_IFCC_BELOW;
            case KParamType.BLOOD_FAT_TRIG:
                return LIPIDS_TRIG_ALARM_BELOW;
            case KParamType.BLOOD_FAT_CHO:
                return LIPIDS_CHOL_ALARM_BELOW;
            case KParamType.BLOOD_FAT_HDL:
                return LIPIDS_HDL_ALARM_BELOW;
            case KParamType.BLOOD_FAT_LDL:
                return LIPIDS_LDL_ALARM_BELOW;
            default:
                return String.valueOf(value);
        }
    }
}
