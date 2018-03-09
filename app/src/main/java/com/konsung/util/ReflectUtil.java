package com.konsung.util;

import android.text.TextUtils;
import android.util.Log;

import com.csvreader.CsvWriter;
import com.konsung.R;
import com.konsung.bean.AppPersonDto;
import com.konsung.bean.CSVAnnotation;
import com.konsung.bean.PatientBean;
import com.konsung.util.global.GlobalNumber;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by dlx on 2017/4/14 0014.
 */

public class ReflectUtil {

    public static final String STRING = "String";
    public static final String DATE = "Date";
    public static final String INTEGER = "Integer";
    public static final String LONG = "Long";
    public static final String FLOAT = "Float";
    public static final String DOUBLE = "Double";
    public static final String BOOLEAN = "Boolean";
    public static final String INT = "int";
    public static final String SDF_FULL = "EEE MMM dd HH:mm:ss zzz yyyy";
    public static final String SDF_LONG = "yyyy-MM-dd HH:mm:ss";
    public static final String SDF_SHORT = "yyyy-MM-dd";
    public static final String SET = "set";
    public static final String GET = "get";
    public static final String IS = "is";
    public static final char SEPRATE_CHAR = ',';
    public static final String GBK_CHARSET = "GBK";
    public static final String DB_VERSION = "database_version";

    /**
     * @param csvFilePath csv绝对路径
     * @param beans 数据集合
     * @param <T> 泛型
     * @throws Exception exception
     */
    public static <T> void writeBeanToCsvFile(String csvFilePath, List<T> beans) throws Exception {
        File file = new File(csvFilePath);
        if (!file.exists()) {     //如果文件不存在，创建文件
            file.createNewFile();
        }
        CsvWriter wr = new CsvWriter(csvFilePath, SEPRATE_CHAR, Charset.forName(GBK_CHARSET));

        List<String[]> contents = getStringArrayFromBean(beans);
        for (String[] each : contents) {
            wr.writeRecord(each, true);
        }
        wr.close();
    }

    /**
     * @param beans 数据集合
     * @param <T> 泛型
     * @return csv转换需要的数据集合
     * @throws Exception exption
     */
    public static <T> List<String[]> getStringArrayFromBean(List<T> beans) throws Exception {
        if (beans.size() < 1) {
            throw new IllegalArgumentException(UiUitls.getString(R.string.length_too_short));
        }

        List<String[]> result = new ArrayList<String[]>();
        Class<? extends Object> cls = beans.get(0).getClass(); //获取泛型类型
        Field[] declaredFields = cls.getDeclaredFields();
        List<Field> annoFields = new ArrayList<Field>();

        for (int i = 0; i < declaredFields.length; i++) {
            //筛选出拥有注解的字段
            //获取注解
            CSVAnnotation anno = declaredFields[i].getAnnotation(CSVAnnotation.class);
            if (anno != null) {
                annoFields.add(declaredFields[i]);
            }
        }

        // ## 添加数据库版本信息 ##
        String[] title = new String[annoFields.size() + 1];
        for (int i = 0; i < annoFields.size(); i++) {
            String name = annoFields.get(i).getAnnotation(CSVAnnotation.class).name();
            if (TextUtils.isEmpty(name)) {
                name = annoFields.get(i).getName();
            }
            title[i] = name;
        }
        title[title.length - 1] = DB_VERSION; //保存数据库版本信息
        result.add(title);

        for (T each : beans) {
            String[] item = new String[annoFields.size() + 1];
            for (int i = 0; i < annoFields.size(); i++) {
                String fieldName = annoFields.get(i).getName();
                String type = annoFields.get(i).getType().getSimpleName();

                Method method = getValueGetter(cls, type, fieldName);
                String val = "";
                try {
                    Object invoke = method.invoke(each);
                    if (invoke != null) {
                        val = invoke.toString();
                    }
                    item[i] = val;
                } catch (Exception e) {
                    continue;
                }
            }
            item[item.length - 1] = GlobalConstant.DATABASE_VERSION + "";
            result.add(item);
        }
        return result;
    }

    /**
     * 获取属性值对应的取值方法
     * @param cls 类的对象
     * @param type 字段类型
     * @param fieldName 字段名
     * @return 该字段getter方法对象
     */
    public static Method getValueGetter(Class<?> cls, String type, String fieldName) {
        Method method = null;
        if (BOOLEAN.equalsIgnoreCase(type)) {
            String getter = GET + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            try {
                method = cls.getMethod(getter);
            } catch (NoSuchMethodException e) {
                String booleanGetter = IS + fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);
                try {
                    method = cls.getMethod(booleanGetter);
                } catch (NoSuchMethodException e1) {
                    e1.printStackTrace();
                }
            }
            return method;
        } else {
            String methodName = GET + fieldName.substring(0, 1).toUpperCase()
                    + fieldName.substring(1);
            try {
                method = cls.getMethod(methodName);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return method;
        }
    }

    /**
     * set属性的值到Bean
     * @param bean class类型
     * @param fieldName 属性名
     * @param val 设置给属性的值
     * @throws Exception exception
     */
    public static void setFieldValue(Object bean, String fieldName, String val) throws Exception {
        Class<?> cls = bean.getClass();
        // 取出bean里的所有方法
        Method[] methods = cls.getDeclaredMethods();
        Field[] fields = cls.getDeclaredFields();
        if (fieldName.equals(DB_VERSION)) {
            return;
        }

        Field field = null;
        try {
            field = cls.getDeclaredField(fieldName);
            String fieldSetName = parSetName(fieldName);
            if (!checkSetMet(methods, fieldSetName)) {
                return;
            }

            Method fieldSetMet = cls.getMethod(fieldSetName, field.getType());
            if (null != val && !"".equals(val)) {
                String fieldType = field.getType().getSimpleName();
                if (STRING.equals(fieldType)) {
                    fieldSetMet.invoke(bean, val);
                } else if (DATE.equals(fieldType)) {
                    Date temp = parseDate(val);
                    fieldSetMet.invoke(bean, temp);
                } else if (INTEGER.equals(fieldType) || INT.equals(fieldType)) {
                    Integer intval = Integer.parseInt(val);
                    fieldSetMet.invoke(bean, intval);
                } else if (LONG.equalsIgnoreCase(fieldType)) {
                    Long temp = Long.parseLong(val);
                    fieldSetMet.invoke(bean, temp);
                } else if (FLOAT.equalsIgnoreCase(fieldType)) {
                    Float temp = Float.parseFloat(val);
                    fieldSetMet.invoke(bean, temp);
                } else if (DOUBLE.equalsIgnoreCase(fieldType)) {
                    Double temp = Double.parseDouble(val);
                    fieldSetMet.invoke(bean, temp);
                } else if (BOOLEAN.equalsIgnoreCase(fieldType)) {
                    Boolean temp = Boolean.parseBoolean(val);
                    fieldSetMet.invoke(bean, temp);
                } else {
                    Log.e("tdlx", "Exception: unknown type setValue === " + fieldType);
                }
            }
        } catch (Exception e) {
            Log.e("tdlx", "Exception setFieldValue: " + "field:" + fieldName);
        }
    }

    /**
     * 格式化string为Date
     * @param datestr 日期字符串
     * @return date
     */
    private static Date parseDate(String datestr) {
        if (null == datestr || "".equals(datestr)) {
            return null;
        }
//        "Sun Apr 23 14:44:28 GMT+08:00 20"
        try {
            String fmtstr = null;
            if (datestr.length() <= GlobalNumber.TEN_NUMBER) {
                fmtstr = SDF_SHORT;
            } else if (datestr.length() <= GlobalNumber.TWENTY_NUMBER) {
                fmtstr = SDF_LONG;
            } else if (datestr.length() <= GlobalNumber.THIRTY_FIVE_NUMBER) {
                fmtstr = SDF_FULL;
            }
            SimpleDateFormat sdf = new SimpleDateFormat(fmtstr, Locale.UK);
            return sdf.parse(datestr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 日期转化为String
     * @param date 日期对象
     * @return date string
     */
    private static String fmtDate(Date date) {
        if (null == date) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(SDF_LONG, Locale.US);
            return sdf.format(date);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否存在某属性的 set方法
     * @param methods 所有的方法集合
     * @param fieldSetMet 字段set方法名
     * @return boolean
     */
    private static boolean checkSetMet(Method[] methods, String fieldSetMet) {
        for (Method met : methods) {
            if (fieldSetMet.equals(met.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否存在某属性的 get方法
     * @param methods 所有的方法集合
     * @param fieldGetMet 字段get方法名
     * @return boolean
     */
    private static boolean checkGetMet(Method[] methods, String fieldGetMet) {
        for (Method met : methods) {
            if (fieldGetMet.equals(met.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 拼接某属性的 get方法
     * @param fieldName 字段名
     * @return String
     */
    private static String parGetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        return GET + fieldName.substring(0, 1).toUpperCase()
                + fieldName.substring(1);
    }

    /**
     * 拼接在某属性的 set方法
     * @param fieldName 字段名
     * @return String
     */
    private static String parSetName(String fieldName) {
        if (null == fieldName || "".equals(fieldName)) {
            return null;
        }
        return SET + fieldName.substring(0, 1).toUpperCase()
                + fieldName.substring(1);
    }

    /**
     * 获取bean对象中的某个字段的值（使用反射）
     * @param obj 该对象
     * @param fieldName 该字段
     * @return 字段结果值
     */
    public static String getValFromObj(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj).toString();
        } catch (Exception e) {
            Log.e("tdlx", "getValFromObj: " + "exception:" + e.getMessage());
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 获取类中拥有导出注解的字段
     * @param cls 目标类
     * @return 该类中拥有导出注解的字段
     */
    public static Map<String, CSVAnnotation> getExportAnnotation(Class cls) {

        Field[] declaredFields = cls.getDeclaredFields();
        Map<String, CSVAnnotation> annoFields = new HashMap<>();

        for (int i = 0; i < declaredFields.length; i++) {
            //筛选出拥有注解的字段
            //获取注解
            CSVAnnotation anno = declaredFields[i].getAnnotation(CSVAnnotation.class);
            if (anno != null) {
                annoFields.put(declaredFields[i].getName(), anno);
            }
        }

        // ## 排序 ##
        Map<String, CSVAnnotation> map = sortByPriority(annoFields);
        return map;
    }

    /**
     * 使用 Map按value进行排序
     * @param oriMap map值
     * @return map值
     */
    public static Map<String, CSVAnnotation> sortByPriority(Map<String, CSVAnnotation> oriMap) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }
        Map<String, CSVAnnotation> sortedMap = new LinkedHashMap<String, CSVAnnotation>();
        List<Map.Entry<String, CSVAnnotation>> entryList
                = new ArrayList<Map.Entry<String, CSVAnnotation>>(
                oriMap.entrySet());
        Collections.sort(entryList, new PriorityComparator());

        Iterator<Map.Entry<String, CSVAnnotation>> iter = entryList.iterator();
        Map.Entry<String, CSVAnnotation> tmpEntry = null;
        while (iter.hasNext()) {
            tmpEntry = iter.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
    }

    /**
     *
     * 居民信息下载转换
     *
     * @param personDtos 转换前的数据
     * @return 转换后的居民信息列表
     */
    public static List<PatientBean> toPatients(List<AppPersonDto> personDtos) {
        ArrayList list = new ArrayList();
        if (personDtos == null || personDtos.size() <= 0) {
            return list;
        }

        for (AppPersonDto personDto : personDtos) {
            PatientBean patientBean = new PatientBean();
            patientBean.setName(StringUtil.getValidStr(personDto.getName()));
            patientBean.setCard(StringUtil.getValidStr(personDto.getCardNo()));
            patientBean.setAge(StringUtil.getValidInt(personDto.getAge()));
            patientBean.setMemberShipCard(StringUtil.getValidStr(personDto.getMembershipCard()));
            patientBean.setSex(StringUtil.getValidIntInSex(personDto.getSex()));
            patientBean.setBlood(StringUtil.getValidInt(personDto.getBloodType()));
            patientBean.setPhone(StringUtil.getValidStr(personDto.getTelePhone()));
            patientBean.setPatient_type(StringUtil.getValidInt(personDto.getPatientType()));
            patientBean.setAddress(StringUtil.getValidStr(personDto.getAddress()));
            patientBean.setRemark(StringUtil.getValidStr(personDto.getDesc()));
            patientBean.setBmpStr(StringUtil.getValidStr(personDto.getHeadPortrait()));
            list.add(patientBean);
        }
        return list;
    }

    /**
     * 比较类
     */
    public static class PriorityComparator implements Comparator<Map.Entry<String, CSVAnnotation>> {

        @Override
        public int compare(Map.Entry<String, CSVAnnotation> lhs, Map.Entry<String, CSVAnnotation>
                rhs) {
            return lhs.getValue().priority() - rhs.getValue().priority();
        }
    }
}
