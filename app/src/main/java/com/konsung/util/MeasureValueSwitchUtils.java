package com.konsung.util;

import com.konsung.util.global.GlobalNumber;

/**
 * Created by xiangshicheng on 2017/12/27 0027.
 * 该类用于测量值结果转换公用类
 */

public class MeasureValueSwitchUtils {

    /**
     * 尿常规值转换
     * @param value 模块传递过来的值
     * @return 显示测量值
     */
    public static String uriToString(int value) {
        switch (value) {
            case GlobalNumber.UN_ONE:
                return "-";
            case GlobalNumber.ZERO_NUMBER:
                return "+-";
            case GlobalNumber.ONE_NUMBER:
                return "+1";
            case GlobalNumber.TWO_NUMBER:
                return "+2";
            case GlobalNumber.THREE_NUMBER:
                return "+3";
            case GlobalNumber.FOUR_NUMBER:
                return "+4";
            case GlobalNumber.FIVE_NUMBER:
                return "+";
            case GlobalNumber.SIX_NUMBER:
                return "Normal";
            default:
                return String.valueOf(value);
        }
    }
}
