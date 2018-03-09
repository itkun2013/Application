package com.konsung.bean;

/**
 * Created by Administrator on 2015/11/18 0018.
 */
public class EcgDiagnoseBean {

    private String qt_qtc;

    private String pr;

    private String pqrst;

    private String qrs;

    private String rv5_sv1_1;

    private String rv5_sv1_2;

    private String srcImg;

    private String ecgDiagnose;

    public void setQt_qtc(String qt_qtc) {
        this.qt_qtc = qt_qtc;
    }

    public void setPr(String pr) {
        this.pr = pr;
    }

    public void setPqrst(String pqrst) {
        this.pqrst = pqrst;
    }

    public void setRv5_sv1_1(String rv5_sv1_1) {
        this.rv5_sv1_1 = rv5_sv1_1;
    }

    public void setRv5_sv1_2(String rv5_sv1_2) {
        this.rv5_sv1_2 = rv5_sv1_2;
    }

    public void setSrcImg(String srcImg) {
        this.srcImg = srcImg;
    }

    public void setEcgDiagnose(String ecgDiagnose) {
        this.ecgDiagnose = ecgDiagnose;
    }

    public void setQrs(String qrs) {
        this.qrs = qrs;
    }

    public String getQrs() {
        return qrs;
    }

    public String getQt_qtc() {
        return qt_qtc;
    }

    public String getPr() {
        return pr;
    }

    public String getPqrst() {
        return pqrst;
    }

    public String getRv5_sv1_1() {
        return rv5_sv1_1;
    }

    public String getRv5_sv1_2() {
        return rv5_sv1_2;
    }

    public String getSrcImg() {
        return srcImg;
    }

    public String getEcgDiagnose() {
        return ecgDiagnose;
    }
}
