package com.konsung.bean;

public class PersonResult {

    private String fileNumber;

    private String orgId;

    private String resultCode;

    public PersonResult() {
        super();
    }

    public PersonResult(String resultCode, String fileNumber, String orgId) {
        this.resultCode = resultCode;
        this.fileNumber = fileNumber;
        this.orgId = orgId;
    }

    public String getFileNumber() {
        return fileNumber;
    }

    public void setFileNumber(String fileNumber) {
        this.fileNumber = fileNumber;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }
}
