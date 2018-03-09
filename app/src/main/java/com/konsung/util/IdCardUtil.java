package com.konsung.util;

import android.text.TextUtils;

import com.konsung.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 身份证工具类
 * 校验身份证是否正确
 *
 * @author ouyangfan
 * @version 0.0.1
 */
public class IdCardUtil {
    /*
     * 身份证校验方法
     * @link http://zhidao.baidu
     * .com/link?url
     * =sOYq5Il6TlNjPbypEH4jAYPgj9CWwq1jlm61m0L1Yas_p9hzEIt_EWHdbdZjqFO1r310iUn-cI_Ikp0EdYy3R_
     * 1、号码的结构 公民身份号码是特征组合码，由十七位数字本体码和一位校验码组成。排列顺序从左至右依次为：六位数字地址码，
     * 八位数字出生日期码，三位数字顺序码和一位数字校验码。
     * 2、地址码(前六位数）表示编码对象常住户口所在县(市、旗、区)的行政区划代码，按GB/T2260的规定执行。
     * 3、出生日期码（第七位至十四位）表示编码对象出生的年、月、日，按GB/T7408的规定执行，年、月、日代码之间不用分隔符。
     * 4、顺序码（第十五位至十七位）表示在同一地址码所标识的区域范围内，对同年、同月、同日出生的人编定的顺序号，
     * 顺序码的奇数分配给男性，偶数分配给女性。
     * 5、校验码（第十八位数）
     * （1）十七位数字本体码加权求和公式 S = Sum(Ai * Wi), i = 0, ... , 16 ，先对前17位数字的权求和
     * Ai:表示第i位置上的身份证号码数字值 Wi:表示第i位置上的加权因子 Wi: 7 9 10 5 8 4 2 1 6 3 7 9 10 5
     * 8 4 2
     * （2）计算模 Y = mod(S, 11) （3）通过模得到对应的校验码 Y: 0 1 2 3 4 5 6 7 8 9 10 校验码: 1
     * 0 X 9 8 7 6 5 4 3 2
     */

    /*
     * @param IdStr 身份证号
     * @return 有效返回"" ,无效返回String错误信息
     */
    public static String IdCardValidate(String IdStr) {
        String errorInfo = "";      // 记录错误信息
        String Ai = "";             // Ai:表示第i位置上的身份证号码数字值
        // Wi:表示第i位置上的加权因子 Wi: 7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2
        String[] Wi = {"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7",
                "9", "10", "5", "8", "4", "2"};
        String[] ValCodeArr = {"1", "0", "x", "9", "8", "7", "6", "5", "4",
                "3", "2"};

        // ============ 身份证号码的长度15位或18位 ================
        if (IdStr.length() != 15 && IdStr.length() != 18) {
            errorInfo = UiUitls.getContent().getResources().getString(R
                    .string.id_error_info1);
            return errorInfo;
        }

        // ================ 数字 除最后一位都为数字 ===============
        if (IdStr.length() == 18) {
            Ai = IdStr.substring(0, 17);
        } else if (IdStr.length() == 15) {
            Ai = IdStr.substring(0, 6) + "19" + IdStr.substring(6, 15);
        }
        if (!isNumeric(Ai)) {
            errorInfo = UiUitls.getContent().getResources().getString(R
                    .string.id_error_info2);
            return errorInfo;
        }

        // ================ 出生年月是否有效 ================
        String strYear = Ai.substring(6, 10);       // 年
        String strMonth = Ai.substring(10, 12);     // 月
        String strDay = Ai.substring(12, 14);       // 日
        if (!isDate(strYear + "-" + strMonth + "-" + strDay)) {
            errorInfo = UiUitls.getContent().getResources().getString(R
                    .string.id_error_info3);
        }
        GregorianCalendar gc = new GregorianCalendar();
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            if ((gc.get(Calendar.YEAR) - Integer.parseInt(strYear)) > 150
//                    || (gc.getTime().getTime() - s.parse(
//                    strYear + "-" + strMonth + "-" + strDay).getTime()) < 0) {
//                errorInfo = UiUitls.getContent().getResources().getString(R
//                        .string.id_error_info4);
//                return errorInfo;
//            }
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        } catch (java.text.ParseException e) {
//            e.printStackTrace();
//        }
        if (Integer.parseInt(strMonth) > 12 || Integer.parseInt(strMonth) ==
                0) {
            errorInfo = UiUitls.getContent().getResources().getString(R
                    .string.id_error_info5);
            return errorInfo;
        }
        if (Integer.parseInt(strDay) > 31 || Integer.parseInt(strDay) == 0) {
            errorInfo = UiUitls.getContent().getResources().getString(R
                    .string.id_error_info6);
            return errorInfo;
        }

        // ================ 地区码时候有效 ================
        Hashtable h = getAreaCode();
        if (h.get(Ai.substring(0, 2)) == null) {
            errorInfo = UiUitls.getContent().getResources().getString(R
                    .string.id_error_info7);
            return errorInfo;
        }
        // ==============================================

        // ================ 判断最后一位的值 ================
        int TotalmulAiWi = 0;
        for (int i = 0; i < 17; i++) {
            TotalmulAiWi = TotalmulAiWi
                    + Integer.parseInt(String.valueOf(Ai.charAt(i)))
                    * Integer.parseInt(Wi[i]);
        }
        int modValue = TotalmulAiWi % 11;
        String strVerifyCode = ValCodeArr[modValue];
        Ai = Ai + strVerifyCode;

        if (IdStr.length() == 18) {
            if (Ai.equals(IdStr) == false) {
                errorInfo = UiUitls.getContent().getResources().getString(R
                        .string.id_error_info8);
                return errorInfo;
            }
        } else {
            return "";
        }
        return errorInfo;
    }

    /*
     * 功能：判断字符串是否为数字
     * @param str
     * @return
     */
    private static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (isNum.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * 功能：判断字符串是否为日期格式
     * @param strDate 日期字符串
     * @return
     */
    private static boolean isDate(String strDate) {
        Pattern pattern = Pattern.compile("^((\\d{2}(([02468][048])|" +
                "([13579][26]))[\\-\\/\\s]?((((0?[13578])|(1[02]))" +
                "[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|" +
                "(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|" +
                "([1-2][0-9])|(30)))|" +
                "(0?2[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])))))|(\\d{2}(" +
                "([02468][1235679])|" +
                "([13579][01345789]))[\\-\\/\\s]?((((0?[13578])|(1[02]))" +
                "[\\-\\/\\s]?((0?[1-9])|" +
                "([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?(" +
                "(0?[1-9])|([1-2][0-9])|(30)))|" +
                "(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))(\\s((" +
                "(0?[0-9])|" +
                "([1-2][0-3]))\\:([0-5]?[0-9])((\\s)|(\\:([0-5]?[0-9])))))?$");
        Matcher m = pattern.matcher(strDate);
        if (m.matches()) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * 功能：设置地区编码
     * @return
     */
    private static Hashtable getAreaCode() {
        Hashtable hashtable = new Hashtable();
        hashtable.put("11", "北京");
        hashtable.put("12", "天津");
        hashtable.put("13", "河北");
        hashtable.put("14", "山西");
        hashtable.put("15", "内蒙古");
        hashtable.put("21", "辽宁");
        hashtable.put("22", "吉林");
        hashtable.put("23", "黑龙江");
        hashtable.put("31", "上海");
        hashtable.put("32", "江苏");
        hashtable.put("33", "浙江");
        hashtable.put("34", "安徽");
        hashtable.put("35", "福建");
        hashtable.put("36", "江西");
        hashtable.put("37", "山东");
        hashtable.put("41", "河南");
        hashtable.put("42", "湖北");
        hashtable.put("43", "湖南");
        hashtable.put("44", "广东");
        hashtable.put("45", "广西");
        hashtable.put("46", "海南");
        hashtable.put("50", "重庆");
        hashtable.put("51", "四川");
        hashtable.put("52", "贵州");
        hashtable.put("53", "云南");
        hashtable.put("54", "西藏");
        hashtable.put("61", "陕西");
        hashtable.put("62", "甘肃");
        hashtable.put("63", "青海");
        hashtable.put("64", "宁夏");
        hashtable.put("65", "新疆");
        hashtable.put("71", "台湾");
        hashtable.put("81", "香港");
        hashtable.put("82", "澳门");
        hashtable.put("91", "国外");
        return hashtable;
    }

    public static String getRandomID() {
        String id = "";
        // 随机生成省、自治区、直辖市代码 1-2
        String provinces[] = {"11", "12", "13", "14", "15", "21", "22", "23",
                "31", "32", "33", "34", "35", "36", "37", "41", "42", "43",
                "44", "45", "46", "50", "51", "52", "53", "54", "61", "62",
                "63", "64", "65", "71", "81", "82"};
        String province = provinces[new Random().nextInt(provinces.length - 1)];
        // 随机生成地级市、盟、自治州代码 3-4
        String citys[] = {"01", "02", "03", "04", "05", "06", "07", "08",
                "09", "10", "21", "22", "23", "24", "25", "26", "27", "28"};
        String city = citys[new Random().nextInt(citys.length - 1)];
        // 随机生成县、县级市、区代码 5-6
        String countys[] = {"01", "02", "03", "04", "05", "06", "07", "08",
                "09", "10", "21", "22", "23", "24", "25", "26", "27", "28",
                "29", "30", "31", "32", "33", "34", "35", "36", "37", "38"};
        String county = countys[new Random().nextInt(countys.length - 1)];
        // 随机生成出生年月 7-14
        SimpleDateFormat dft = new SimpleDateFormat("yyyyMMdd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE,
                date.get(Calendar.DATE) - new Random().nextInt(365 * 100));
        String birth = dft.format(date.getTime());
        // 随机生成顺序号 15-17
        String no = new Random().nextInt(999) + "";
        // 随机生成校验码 18
        String checks[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
                "X"};
        String check = checks[new Random().nextInt(checks.length - 1)];
        // 拼接身份证号码
        id = province + city + county + birth + no + check;

        return id;
    }
    /**
     * 根据身份中计算年龄
     * @param idCard 身份证号码
     * @return 年龄
     */
    public static int getAgeFormIdCard(String idCard) {
        int currentYear;
        int bornYear;
        Date date = new Date();
        if (idCard.length() == 18) {
            bornYear = Integer.parseInt(idCard.substring(6, 10));
            String str = date.toString();
            currentYear = Integer.valueOf(str.substring(str.length() - 4, str.length()));
            if (currentYear - bornYear <= 0 || currentYear - bornYear > 130) {
                return 0;
            } else {
                return currentYear - bornYear;
            }
        }
        return 0;
    }

    /**
     * 根据身份证获取年龄
     * @param birthDay 出生日期
     * @return 年龄
     */
    public static int getAge(Date birthDay) {
        try {
            //获取当前系统时间
            Calendar cal = Calendar.getInstance();
            //如果出生日期大于当前时间，则抛出异常
            if (cal.before(birthDay)) {
                return -1;
            }
            //取出系统当前时间的年、月、日部分
            int yearNow = cal.get(Calendar.YEAR);
            int monthNow = cal.get(Calendar.MONTH);
            int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

            //将日期设置为出生日期
            cal.setTime(birthDay);
            //取出出生日期的年、月、日部分
            int yearBirth = cal.get(Calendar.YEAR);
            int monthBirth = cal.get(Calendar.MONTH);
            int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
            //当前年份与出生年份相减，初步计算年龄
            int age = yearNow - yearBirth;
            //当前月份与出生日期的月份相比，如果月份小于出生月份，则年龄上减1，表示不满多少周岁
            if (monthNow <= monthBirth) {
                //如果月份相等，在比较日期，如果当前日，小于出生日，也减1，表示不满多少周岁
                if (monthNow == monthBirth) {
                    if (dayOfMonthNow < dayOfMonthBirth) {
                        age--;
                    }
                } else {
                    age--;
                }
            }
            return age;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 根据身份证获取性别
     * @param idCard 身份证号码
     * @return 性别code
     */
    public static Date getBirthday(String idCard) {
        if (TextUtils.isEmpty(idCard)) {
            return null;
        }
        if (idCard.length() == 18) {
            String birthday = idCard.substring(6, 10) + "-" + idCard.substring(10, 12) + "-"
                    + idCard.substring(12, 14);
            SimpleDateFormat sdf = UiUitls.getDateFormat(UiUitls.DateState.SHORT);

            try {
                return sdf.parse(birthday);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
