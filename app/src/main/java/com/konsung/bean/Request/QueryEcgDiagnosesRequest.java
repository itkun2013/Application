package com.konsung.bean.Request;

/**
 * Created by xiangshicheng on 2017/7/17 0017.
 * 远程心电查询参数类
 */

public class QueryEcgDiagnosesRequest {
    //数据id
    public String dataId = "";
    //申请医生id
    public String doctorCode = "";
    //查询输入信息
    public String condition = "";
    //申请单处理状态，非空字段
    public String state = "";
    //分页开始页值，非空字段
    public int beginPage;
    //每页数量，非空字段
    public int pageRecord;
}
