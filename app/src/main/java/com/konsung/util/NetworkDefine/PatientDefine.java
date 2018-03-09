package com.konsung.util.NetworkDefine;

/**
 * Created by chenshuo on 2016/1/6.
 */
public class PatientDefine {
    // 病人类型
    public static final int ADULT = 0;            // 成人
    public static final int PEDIATRIC = 1;       // 小儿
    public static final int NEONATAL = 2;        // 新生儿

    // 性别
    //注意，此处的定义与Aurora的不一致
    public static final int UNKNOWN = 0;          // 未知
    public static final int MALE = 1;             // 男
    public static final int FEMALE = 2;           // 女
    public static final int NOTEXPLAIN = 9;      // 未说明

    // 血型
    //注意，此处的定义与Aurora的不一致
    public static final int A = 0;               // A型
    public static final int B = 1;               // B型
    public static final int O = 3;               // O型
    public static final int AB = 2;               // AB型
    public static final int NA = 4;               // 不详
}
