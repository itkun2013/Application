package com.konsung.util;

import android.text.TextUtils;

import com.tencent.bugly.crashreport.CrashReport;

import java.math.BigDecimal;

/**
 * Created by DLX on 2016/9/16 0016.
 */
public class NumUtil {

    /**
     * 按四舍五入将一个小数类型字符串转换为保留两位小数的字符串 （默认两位）
     *
     * @param value
     * @return
     */
    public static String trans2Decimal(String value) {
        return trans2Decimal(value, 2);
    }

    /**
     * 按四舍五入将一个小数类型字符串转换为保留radix位小数的字符串
     *
     * @param value
     * @param radix 保留小数的位数
     * @return
     */
    public static String trans2Decimal(String value, int radix) {
        try {
            if (!TextUtils.isEmpty(value)) {
                if (value.contains(".")) {
                    value = value.replaceAll("0+?$", "");//去掉后面无用的零
                    value = value.replaceAll("[.]$", "");//如小数点后面全是零则去掉小数点
                    double d = Double.valueOf(value);
                    BigDecimal b = new BigDecimal(d);
                    double doubleValue = b.setScale(radix, BigDecimal.ROUND_HALF_UP)
                            .doubleValue();
                    return "" + doubleValue;
                } else {
                    return value;
                }
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
            return "";
        }
    }

    /**
     * 按四舍五入将一个小数类型字符串转换为保留radix位小数的字符串
     *
     * @param value
     * @param radix 保留小数的位数
     * @return
     */
    public static float trans2FloatValue(float value, int radix) {
        try {
            BigDecimal b = new BigDecimal(value);
            float floatValue = b.setScale(radix, BigDecimal.ROUND_HALF_UP).floatValue();
            return floatValue;
        } catch (Exception e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
            return GlobalConstant.INVALID_DECIMAL_VALUE;
        }

    }

    /**
     * 按四舍五入将一个小数类型字符串转换为保留radix位小数的字符串
     *
     * @param value 要转换的值
     * @param radix 保留小数的位数
     * @return
     */
    public static String trans2Decimal(float value, int radix) {
        return trans2Decimal(value + "", radix);
    }

    /**
     * 按四舍五入将一个小数类型字符串转换为保留两位小数的字符串
     *
     * @param value
     * @return
     */
    public static String trans2Decimal(float value) {
        double d = Double.valueOf(value);
        BigDecimal b = new BigDecimal(d);
        double doubleValue = b.setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
        return "" + doubleValue;
    }

    /**
     * 返回一个有效格式的int型字符串 （去掉前面的0）
     *
     * @param value
     * @return
     */
    public static String trans2Int(String value) {
        try {
            if (!TextUtils.isEmpty(value)) {
                int i = Integer.valueOf(value);
                return i + "";
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }



    /**
     * 返回一个放大固定倍数的有效格式的int型字符串 （去掉小数位）
     *
     * @param d 要转换的值
     * @return
     */
    public static int trans2Int(Float d, int rate) {
        BigDecimal bg = new BigDecimal(d * rate);
        double doubleValue = bg.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        return  (int)doubleValue;
    }

    /**
     * 传入URI的code值（0,1,2,3,4,5） 返回应显示的字符串 如果是无效值返回空字符串
     *
     * @param uriCode
     */
    public static String uriCode2Str(String uriCode) {
        if (TextUtils.isEmpty(uriCode)) {
            return "";
        }
        switch (uriCode) {
            case "0":
                return "-";
            case "1":
                return "+-";
            case "2":
                return "+1";
            case "3":
                return "++";
            case "4":
                return "+++";
            case "5":
                return "++++";
            default:
                return "";
        }
    }
}
