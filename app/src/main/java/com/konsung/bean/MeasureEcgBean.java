package com.konsung.bean;

/**
 * 心电测量结果数据集
 */

public class MeasureEcgBean {
    private static final String ST_DEFAULT_VALUE = "-?-"; //防止超过测量范围，设置默认ST值为"-?-"
    private String hr = ST_DEFAULT_VALUE;
    private String p = ST_DEFAULT_VALUE;
    private String pr = ST_DEFAULT_VALUE;
    private String qrs = ST_DEFAULT_VALUE;
    private String qtQtc = ST_DEFAULT_VALUE;
    private String pQrsT = ST_DEFAULT_VALUE;
    private String rv5Sv1 = ST_DEFAULT_VALUE;
    private String result = ST_DEFAULT_VALUE;
    private String rv5PlusSv1 = ST_DEFAULT_VALUE;

    public String getHr() {
        return hr;
    }

    public void setHr(String hr) {
        this.hr = hr;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
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

    public String getRv5PlusSv1() {
        return rv5PlusSv1;
    }

    public void setRv5PlusSv1(String rv5PlusSv1) {
        this.rv5PlusSv1 = rv5PlusSv1;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
