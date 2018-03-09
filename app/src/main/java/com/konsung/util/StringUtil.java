package com.konsung.util;

import android.text.TextUtils;

import com.konsung.R;

/**
 * Created by lipengjie on 2016/9/28 0028.
 * 字符串处理的工具类
 */
public class StringUtil {

    public static boolean isEmpty(String str) {
        if (TextUtils.isEmpty(str) || str.equals("null")) {
            return true;
        } else if (str.replaceAll(" ", "").length() == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 通过patientbean中的血型代码获取血型字符串
     * @param position
     * @return
     */
    public static String getBlood(int position) {
        switch (position) {
            case 0:
                return UiUitls.getContent().getString(R.string.detail_blood_type_1);
            case 1:
                return UiUitls.getContent().getString(R.string.detail_blood_type_2);
            case 2:
                return UiUitls.getContent().getString(R.string.detail_blood_type_3);
            case 3:
                return UiUitls.getContent().getString(R.string.detail_blood_type_4);
            case 4:
                return UiUitls.getContent().getString(R.string.detail_blood_type_5);
            default:
                return UiUitls.getContent().getString(R.string.detail_blood_type_5);
        }
    }

    /**
     * 为数据库查询的字符加上%%
     * @param name 查询的条件
     * @return 格式后的字符
     */
    public static String getDBString(String name) {
        if (name != null && name.length() > 0) {
            String string = UiUitls.getString(R.string.per_cent);

            return string + name + string;
        } else {
            return "";
        }
    }

    /**
     * 删除浮点数字符串尾数后的0或者小数点
     * @param numStr 待处理的数字字符串
     * @return 处理后的数字字符串
     */
    public static String deleteEnd0(String numStr) {

        if (numStr != null && numStr.length() > 0) {
            if (numStr.contains(".")) { //包含小数点则为浮点数
                if (numStr.endsWith("0")) {
                    return deleteEnd0(numStr.substring(0, numStr.length() - 1));
                } else if (numStr.endsWith(".")) {
                    return numStr.substring(0, numStr.length() - 1);
                }
            }
        }
        return numStr;
    }

    /**
     * 获取有效字符串（null会返回空字符串）
     * @param string 字符串数据
     * @return 获取有效字符串
     */
    public static String getValidStr(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        return string;
    }

    /**
     * 获取有效int值（null会返回-1）
     * @param string 字符串数据
     * @return 获取有效字符串
     */
    public static Integer getValidInt(String string) {
        if (TextUtils.isEmpty(string)) {
            return -1;
        }
        return Integer.valueOf(string);
    }

    /**
     * 该方法专门用于性别数字转换，后台性别数字和app不一样，后台为了兼容其他版本，只能app做更改
     * @param string 性别数字 1男 0女
     * @return 性别数字
     */
    public static Integer getValidIntInSex(String string) {
        if (TextUtils.isEmpty(string)) {
            return -1;
        }
        //后台女生返回2
        if (string.equals("2")) {
            return 0;
        }
        return Integer.valueOf(string);
    }
    /**
     * 当姓名大于=6位数,只显示前五个字第六个字的时候用“...”表示
     * @param name 群名称
     * @return 省略后的字符串
     */
    public static String stringOmit(String name) {
        StringBuffer buffer = new StringBuffer();
        if (name.length() >= 6) {
            String oldSub = name.substring(0, 5);
            buffer.append(oldSub);
            buffer.append("....");
        } else {
            buffer.append(name);
        }
        return buffer.toString();
    }

}