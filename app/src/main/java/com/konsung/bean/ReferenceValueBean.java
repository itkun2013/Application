package com.konsung.bean;

import com.konsung.util.KParamType;

/**
 * 测量项参考值，因人而异,通过ReferenceUtils.initValueBean初始化
 * @see com.konsung.util.ReferenceUtils
 */
public class ReferenceValueBean {
    //心电心率
    private float ecgPrMax;
    private float ecgPrMin;
    //血氧饱和度
    private float spo2TrendMax;
    private float spo2TrendMin;
    //血氧脉率
    private float spo2PrMax;
    private float spo2PrMin;
    //血压收缩压
    private float nibpSysMax;
    private float nibpSysMin;
    //血压舒张压
    private float nibpDiaMax;
    private float nibpDiaMin;
    //额温
    private float irtempMax;
    private float irtempMin;
    //血糖-餐前
    private float gluBeforeMealMax;
    private float gluBeforeMealMin;
    private float gluAfterMealMax;
    private float gluAfterMealMin;
    //尿酸
    private float uricacidMax;
    private float uricacidMin;
    //总胆固醇
    private float cholesterolMax;
    private float cholesterolMin;
    //尿PH
    private float uriPHMax;
    private float uriPHMin;
    //尿比重
    private float uriSGMax;
    private float uriSGMin;
    //白细胞
    private float wbcMax;
    private float wbcMin;
    //血红蛋白
    private float hgbMax;
    private float hgbMin;
    //红细胞压积值
    private float hctMax;
    private float hctMin;
    //糖化血红蛋白NGSP标准
    private float hbA1cNGSPMax;
    private float hbA1cNGSPMin;
    //糖化血红蛋白IFCC标准
    private float hbA1cIFCCMax;
    private float hbA1cIFCCMin;
    //平均血糖浓度
    private float eAGMax;
    private float eAGMin;
    //血脂-总胆固醇
    private float bloodCholMax;
    private float bloodCholMin;
    //血脂-甘油三酯
    private float bloodTrigMax;
    private float bloodTrigMin;
    //高密度脂蛋白
    private float bloodHDLMax;
    private float bloodHDLMin;
    //低密度脂蛋白
    private float bloodLDLMax;
    private float bloodLDLMin;

    private float height;
    private float weight;

    public float getEcgPrMax() {
        return ecgPrMax;
    }

    public void setEcgPrMax(float ecgPrMax) {
        this.ecgPrMax = ecgPrMax;
    }

    public float getEcgPrMin() {
        return ecgPrMin;
    }

    public void setEcgPrMin(float ecgPrMin) {
        this.ecgPrMin = ecgPrMin;
    }

    public float getSpo2TrendMax() {
        return spo2TrendMax;
    }

    public void setSpo2TrendMax(float spo2TrendMax) {
        this.spo2TrendMax = spo2TrendMax;
    }

    public float getSpo2TrendMin() {
        return spo2TrendMin;
    }

    public void setSpo2TrendMin(float spo2TrendMin) {
        this.spo2TrendMin = spo2TrendMin;
    }

    public float getSpo2PrMax() {
        return spo2PrMax;
    }

    public void setSpo2PrMax(float spo2PrMax) {
        this.spo2PrMax = spo2PrMax;
    }

    public float getSpo2PrMin() {
        return spo2PrMin;
    }

    public void setSpo2PrMin(float spo2PrMin) {
        this.spo2PrMin = spo2PrMin;
    }

    public float getNibpSysMax() {
        return nibpSysMax;
    }

    public void setNibpSysMax(float nibpSysMax) {
        this.nibpSysMax = nibpSysMax;
    }

    public float getNibpSysMin() {
        return nibpSysMin;
    }

    public void setNibpSysMin(float nibpSysMin) {
        this.nibpSysMin = nibpSysMin;
    }

    public float getNibpDiaMax() {
        return nibpDiaMax;
    }

    public void setNibpDiaMax(float nibpDiaMax) {
        this.nibpDiaMax = nibpDiaMax;
    }

    public float getNibpDiaMin() {
        return nibpDiaMin;
    }

    public void setNibpDiaMin(float nibpDiaMin) {
        this.nibpDiaMin = nibpDiaMin;
    }

    public float getIrtempMax() {
        return irtempMax;
    }

    public void setIrtempMax(float irtempMax) {
        this.irtempMax = irtempMax;
    }

    public float getIrtempMin() {
        return irtempMin;
    }

    public void setIrtempMin(float irtempMin) {
        this.irtempMin = irtempMin;
    }

    public float getGluBeforeMealMax() {
        return gluBeforeMealMax;
    }

    public void setGluBeforeMealMax(float gluBeforeMealMax) {
        this.gluBeforeMealMax = gluBeforeMealMax;
    }

    public float getGluBeforeMealMin() {
        return gluBeforeMealMin;
    }

    public void setGluBeforeMealMin(float gluBeforeMealMin) {
        this.gluBeforeMealMin = gluBeforeMealMin;
    }

    public float getUricacidMax() {
        return uricacidMax;
    }

    public void setUricacidMax(float uricacidMax) {
        this.uricacidMax = uricacidMax;
    }

    public float getUricacidMin() {
        return uricacidMin;
    }

    public void setUricacidMin(float uricacidMin) {
        this.uricacidMin = uricacidMin;
    }

    public float getCholesterolMax() {
        return cholesterolMax;
    }

    public void setCholesterolMax(float cholesterolMax) {
        this.cholesterolMax = cholesterolMax;
    }

    public float getCholesterolMin() {
        return cholesterolMin;
    }

    public void setCholesterolMin(float cholesterolMin) {
        this.cholesterolMin = cholesterolMin;
    }

    public float getUriPHMax() {
        return uriPHMax;
    }

    public void setUriPHMax(float uriPHMax) {
        this.uriPHMax = uriPHMax;
    }

    public float getUriPHMin() {
        return uriPHMin;
    }

    public void setUriPHMin(float uriPHMin) {
        this.uriPHMin = uriPHMin;
    }

    public float getUriSGMax() {
        return uriSGMax;
    }

    public void setUriSGMax(float uriSGMax) {
        this.uriSGMax = uriSGMax;
    }

    public float getUriSGMin() {
        return uriSGMin;
    }

    public void setUriSGMin(float uriSGMin) {
        this.uriSGMin = uriSGMin;
    }

    public float getWbcMax() {
        return wbcMax;
    }

    public void setWbcMax(float wbcMax) {
        this.wbcMax = wbcMax;
    }

    public float getWbcMin() {
        return wbcMin;
    }

    public void setWbcMin(float wbcMin) {
        this.wbcMin = wbcMin;
    }

    public float getHgbMax() {
        return hgbMax;
    }

    public void setHgbMax(float hgbMax) {
        this.hgbMax = hgbMax;
    }

    public float getHgbMin() {
        return hgbMin;
    }

    public void setHgbMin(float hgbMin) {
        this.hgbMin = hgbMin;
    }

    public float getHctMax() {
        return hctMax;
    }

    public void setHctMax(float hctMax) {
        this.hctMax = hctMax;
    }

    public float getHctMin() {
        return hctMin;
    }

    public void setHctMin(float hctMin) {
        this.hctMin = hctMin;
    }

    public float getHbA1cNGSPMax() {
        return hbA1cNGSPMax;
    }

    public void setHbA1cNGSPMax(float hbA1cNGSPMax) {
        this.hbA1cNGSPMax = hbA1cNGSPMax;
    }

    public float getHbA1cNGSPMin() {
        return hbA1cNGSPMin;
    }

    public void setHbA1cNGSPMin(float hbA1cNGSPMin) {
        this.hbA1cNGSPMin = hbA1cNGSPMin;
    }

    public float getHbA1cIFCCMax() {
        return hbA1cIFCCMax;
    }

    public void setHbA1cIFCCMax(float hbA1cIFCCMax) {
        this.hbA1cIFCCMax = hbA1cIFCCMax;
    }

    public float getHbA1cIFCCMin() {
        return hbA1cIFCCMin;
    }

    public float getGluAfterMealMax() {
        return gluAfterMealMax;
    }

    public void setGluAfterMealMax(float gluAfterMealMax) {
        this.gluAfterMealMax = gluAfterMealMax;
    }

    public float getGluAfterMealMin() {
        return gluAfterMealMin;
    }

    public void setGluAfterMealMin(float gluAfterMealMin) {
        this.gluAfterMealMin = gluAfterMealMin;
    }

    public void setHbA1cIFCCMin(float hbA1cIFCCMin) {
        this.hbA1cIFCCMin = hbA1cIFCCMin;
    }

    public float geteAGMax() {
        return eAGMax;
    }

    public void seteAGMax(float eAGMax) {
        this.eAGMax = eAGMax;
    }

    public float geteAGMin() {
        return eAGMin;
    }

    public void seteAGMin(float eAGMin) {
        this.eAGMin = eAGMin;
    }

    public float getBloodCholMax() {
        return bloodCholMax;
    }

    public void setBloodCholMax(float bloodCholMax) {
        this.bloodCholMax = bloodCholMax;
    }

    public float getBloodCholMin() {
        return bloodCholMin;
    }

    public void setBloodCholMin(float bloodCholMin) {
        this.bloodCholMin = bloodCholMin;
    }

    public float getBloodTrigMax() {
        return bloodTrigMax;
    }

    public void setBloodTrigMax(float bloodTrigMax) {
        this.bloodTrigMax = bloodTrigMax;
    }

    public float getBloodTrigMin() {
        return bloodTrigMin;
    }

    public void setBloodTrigMin(float bloodTrigMin) {
        this.bloodTrigMin = bloodTrigMin;
    }

    public float getBloodHDLMax() {
        return bloodHDLMax;
    }

    public void setBloodHDLMax(float bloodHDLMax) {
        this.bloodHDLMax = bloodHDLMax;
    }

    public float getBloodHDLMin() {
        return bloodHDLMin;
    }

    public void setBloodHDLMin(float bloodHDLMin) {
        this.bloodHDLMin = bloodHDLMin;
    }

    public float getBloodLDLMax() {
        return bloodLDLMax;
    }

    public void setBloodLDLMax(float bloodLDLMax) {
        this.bloodLDLMax = bloodLDLMax;
    }

    public float getBloodLDLMin() {
        return bloodLDLMin;
    }

    public void setBloodLDLMin(float bloodLDLMin) {
        this.bloodLDLMin = bloodLDLMin;
    }

    /**
     * 通过键获取参考值上限
     * @param param 键 见KParamType
     * @return 参考值(float)
     */
    public float getMaxReference(int param) {
        switch (param) {
            case KParamType.ECG_HR:
                return ecgPrMax;
            case KParamType.SPO2_TREND:
                return spo2TrendMax;
            case KParamType.SPO2_PR:
                return spo2PrMax;
            case KParamType.NIBP_SYS:
                return nibpSysMax;
            case KParamType.NIBP_DIA:
                return nibpDiaMax;
            case KParamType.IRTEMP_TREND:
                return irtempMax;
            case KParamType.BLOODGLU_BEFORE_MEAL:
                return gluBeforeMealMax;
            case KParamType.BLOODGLU_AFTER_MEAL:
                return gluAfterMealMax;
            case KParamType.URICACID_TREND:
                return uricacidMax;
            case KParamType.CHOLESTEROL_TREND:
                return cholesterolMax;
            case KParamType.URINERT_SG:
                return uriSGMax;
            case KParamType.URINERT_PH:
                return uriPHMax;
            case KParamType.BLOOD_WBC:
                return wbcMax;
            case KParamType.BLOOD_HGB:
                return hgbMax;
            case KParamType.BLOOD_HCT:
                return hctMax;
            case KParamType.GHB_HBA1C_NGSP:
                return hbA1cNGSPMax;
            case KParamType.GHB_HBA1C_IFCC:
                return hbA1cIFCCMax;
            case KParamType.BLOOD_FAT_CHO:
                return bloodCholMax;
            case KParamType.BLOOD_FAT_TRIG:
                return bloodTrigMax;
            case KParamType.BLOOD_FAT_HDL:
                return bloodHDLMax;
            case KParamType.BLOOD_FAT_LDL:
                return bloodLDLMax;
            case KParamType.GHB_EAG:
                return eAGMax;
            case KParamType.HEIGHT:
                return height;
            case KParamType.WEIGHT:
                return weight;
            default:
                return 0;
        }
    }

    /**
     * 通过键获取参考值下限
     * @param param 键 见KParamType
     * @return 参考值(float)
     */
    public float getMinReference(int param) {
        switch (param) {
            case KParamType.ECG_HR:
                return ecgPrMin;
            case KParamType.SPO2_TREND:
                return spo2TrendMin;
            case KParamType.SPO2_PR:
                return spo2PrMin;
            case KParamType.NIBP_SYS:
                return nibpSysMin;
            case KParamType.NIBP_DIA:
                return nibpDiaMin;
            case KParamType.IRTEMP_TREND:
                return irtempMin;
            case KParamType.BLOODGLU_BEFORE_MEAL:
                return gluBeforeMealMin;
            case KParamType.BLOODGLU_AFTER_MEAL:
                return gluAfterMealMin;
            case KParamType.URICACID_TREND:
                return uricacidMin;
            case KParamType.CHOLESTEROL_TREND:
                return cholesterolMin;
            case KParamType.URINERT_SG:
                return uriSGMin;
            case KParamType.URINERT_PH:
                return uriPHMin;
            case KParamType.BLOOD_WBC:
                return wbcMin;
            case KParamType.BLOOD_HGB:
                return hgbMin;
            case KParamType.BLOOD_HCT:
                return hctMin;
            case KParamType.GHB_HBA1C_NGSP:
                return hbA1cNGSPMin;
            case KParamType.GHB_HBA1C_IFCC:
                return hbA1cIFCCMin;
            case KParamType.BLOOD_FAT_CHO:
                return bloodCholMin;
            case KParamType.BLOOD_FAT_TRIG:
                return bloodTrigMin;
            case KParamType.BLOOD_FAT_HDL:
                return bloodHDLMin;
            case KParamType.BLOOD_FAT_LDL:
                return bloodLDLMin;
            case KParamType.GHB_EAG:
                return eAGMin;

            default:
                return 0;
        }
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
