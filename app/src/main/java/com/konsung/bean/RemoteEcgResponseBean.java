package com.konsung.bean;

/**
 * Created by xiangshicheng on 2017/7/13 0013.
 */

public class RemoteEcgResponseBean {
    /** 1. [可空] 测量记录业务主键 */
    private String equipmentDataId;

    /** 2. [可空] 申请日期 */
    private String requestDate;

    /**3. 诊断医生名字**/
    private String conDoctorName;

    /** 4. [可空] 诊断 结果*/
    private String conResult;

    /** 5. [可空] 诊断日期 */
    private String conResultDate;

    /** 6. [可空] 诊断建议 */
    private String conAdvice;

    /**7.姓名**/
    private String name;

    /**8 性别**/
    private String sex;

    /**9 身份证**/
    private String idNumber;

    /**10联系方式**/
    private String telephone;

    /**11处理状态**/
    private String conState;

    /** 12. [可空] 心率值 */
    protected String hr;

    /** 13. [可空] 脉率值 */
    protected String pr;

    /** 14. [可空] 心电QRS值 */
    protected String qrs;

    /** 15. [可空] 心电QT/QTC值 */
    protected String qtQtc;

    /** 16. [可空] 心电P/QRS/T值 */
    protected String pQrsT;

    /** 17. [可空] 心电RV5/SV1 */
    protected String rv5Sv1;

    public String getEquipmentDataId() {
        return equipmentDataId;
    }

    public void setEquipmentDataId(String equipmentDataId) {
        this.equipmentDataId = equipmentDataId;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getConDoctorName() {
        return conDoctorName;
    }

    public void setConDoctorName(String conDoctorName) {
        this.conDoctorName = conDoctorName;
    }

    public String getConResult() {
        return conResult;
    }

    public void setConResult(String conResult) {
        this.conResult = conResult;
    }

    public String getConResultDate() {
        return conResultDate;
    }

    public void setConResultDate(String conResultDate) {
        this.conResultDate = conResultDate;
    }

    public String getConAdvice() {
        return conAdvice;
    }

    public void setConAdvice(String conAdvice) {
        this.conAdvice = conAdvice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getConState() {
        return conState;
    }

    public void setConState(String conState) {
        this.conState = conState;
    }

    public String getHr() {
        return hr;
    }

    public void setHr(String hr) {
        this.hr = hr;
    }

    public String getPr() {
        return pr;
    }

    public void setPr(String pr) {
        this.pr = pr;
    }

    public String getQrs() {
        return qrs;
    }

    public void setQrs(String qrs) {
        this.qrs = qrs;
    }

    public String getQtQtc() {
        return qtQtc;
    }

    public void setQtQtc(String qtQtc) {
        this.qtQtc = qtQtc;
    }

    public String getpQrsT() {
        return pQrsT;
    }

    public void setpQrsT(String pQrsT) {
        this.pQrsT = pQrsT;
    }

    public String getRv5Sv1() {
        return rv5Sv1;
    }

    public void setRv5Sv1(String rv5Sv1) {
        this.rv5Sv1 = rv5Sv1;
    }
}
