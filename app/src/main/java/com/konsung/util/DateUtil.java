package com.konsung.util;

import com.konsung.R;
import com.tencent.bugly.crashreport.CrashReport;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 时间工具类
 *
 * @author ouyangfan
 * @version 0.0.1
 */
public class DateUtil {

    /**
     * 私有化构造器
     */
    private DateUtil() {
    }

    /**
     * 获取当前时间--如：2015-02-05 12:28:10
     * @return 时间
     */
    public static String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.format(date);
    }

    /**
     * @param time 时间
     * @param format 时间格式
     * @return 将字符串转化为日期对象 返回时间对象
     */
    public static Date getDate(String time, String format) {
        Date date = null;
        try {
            SimpleDateFormat df = new SimpleDateFormat(format);
            df.setLenient(false);
            date = df.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            CrashReport.postCatchedException(e);
            return null;
        }
        return date;
    }

    /**
     * 根据当地格式格式化日期
     *
     * @param date   日期
     * @param locale 当地格式
     * @return 字符串格式时间 返回格式如(2015年2月5日)
     */
    public static String getDateLocalFormat(Date date, Locale locale) {
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG,
                locale);
        return dateFormat.format(date);
    }

    /**
     * 根据当地格式格式化日期
     * 注意与上面getDate的参数及返回值的区别
     *
     * @param date 日期
     * @return 字符串格式时间
     * 返回格式如(yyyy-MM-dd hh:mm:ss) 表示12小时制
     * (yyyy-MM-dd HH:mm:ss) 表示24小时制
     */
    public static String getTime(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(date);
    }

    /**
     * 根据输入的数字，返回性别
     *
     * @param sex 代表性别的字符
     * @return 性别
     */
    public static String getSex(int sex) {
        switch (sex) {
            case 0:
                return UiUitls.getString(R.string.sex_unknown);
            case 1:
                return UiUitls.getString(R.string.sex_man);
            case 2:
                return UiUitls.getString(R.string.sex_woman);
            case 3:
                return UiUitls.getString(R.string.sex_unsay);
            default:
                return UiUitls.getString(R.string.sex_unknown);
        }

    }
    /**
     * 时间戳转换成字符窜
     * @param time 时间戳
     * @return yyyy年MM月dd日
     */
    public static String getDateToString(long time) {
        Date d = new Date(time);
        SimpleDateFormat sf = null;
        sf = new SimpleDateFormat("yyyy年MM月dd日");
        return sf.format(d);
    }
}
