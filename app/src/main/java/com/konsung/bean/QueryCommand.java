package com.konsung.bean;

/**
 *
* <p>Title:QueryCommand </p>
* <p>Description: APP端基本信息查询参数DTO</p>
* <p>Company: Konsung</p>
* @author  HWB
* @date 2017年11月1日下午1:59:11
 */
public class QueryCommand {

    /**
     * 机构编码
     */
    private String orgId;

    /**
     * 输入框的值
     */
    private String condition;

    /**
     * 开始的记录数
     */
    private int pageStart;

    /**
     * 每页记录数
     */
    private int pageRecords;

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getPageStart() {
        return pageStart;
    }

    public void setPageStart(int pageStart) {
        this.pageStart = pageStart;
    }

    public int getPageRecords() {
        return pageRecords;
    }

    public void setPageRecords(int pageRecords) {
        this.pageRecords = pageRecords;
    }
}
