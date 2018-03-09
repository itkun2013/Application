package com.konsung.bean;

import java.util.List;

/**
 * Created by xiangshicheng on 2017/4/28 0028.
 * 病人基本信息
 */

public class PatientBasicInfoBean {
    public String code = "";
    public String message = "";
    public List<Data> data;
    public class Data {
        //姓名
        public String uname = "";
        //身份证号
        public String cardno = "";
        //性别
        public String sex = "";
        //出生时间
        public String birthday = "";
        //病人类型
        public String patientType = "";
    }

//    /**
//     *
//     */
//    public String getUname() {
//        return uname;
//    }
//
//    /**
//     * @param uname 姓名
//     */
//    public void setUname(String uname) {
//        this.uname = uname;
//    }
//
//    /**
//     *
//     */
//    public String getCardno() {
//        return cardno;
//    }
//
//    /**
//     * @param cardno 身份证号
//     */
//    public void setCardno(String cardno) {
//        this.cardno = cardno;
//    }
//
//    /**
//     *
//     */
//    public String getSex() {
//        return sex;
//    }
//
//    /**
//     * @param sex 性别
//     */
//    public void setSex(String sex) {
//        this.sex = sex;
//    }
//
//    /**
//     *
//     */
//    public String getBirthday() {
//        return birthday;
//    }
//
//    /**
//     * @param birthday 出生日期
//     */
//    public void setBirthday(String birthday) {
//        this.birthday = birthday;
//    }
//
//    /**
//     *
//     */
//    public String getPatientType() {
//        return patientType;
//    }
//
//    /**
//     * @param patientType 病人类型
//     */
//    public void setPatientType(String patientType) {
//        this.patientType = patientType;
//    }
}
