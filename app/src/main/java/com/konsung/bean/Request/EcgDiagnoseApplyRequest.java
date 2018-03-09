package com.konsung.bean.Request;

/**
 * Created by xiangshicheng on 2017/7/17 0017.
 * 远程心电申请参数bean类
 */

public class EcgDiagnoseApplyRequest {
    //申请医生ID,非空字段
    public String doctorCode = "";
    //申请信息字段，非空字段
    public String requestMark = "";
    //数据id业务主键，非空字段
    public String dataId = "";
}
