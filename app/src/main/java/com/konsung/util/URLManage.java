package com.konsung.util;

/**
 * Created by xiangshicheng on 2017/6/29 0029.
 * 接口地址
 */

public class URLManage {

    private String web = "/imms-web";
    private String appClient = "/appClient";
    private static URLManage instance = null;
    //测量数据上传接口
    public String villagehealthport;

    //远程心电申请接口
    public String requestEcgDiagnose;

    //远程心电查询接口
    public String queryEcgDiagnoses;
    private String http = "http://";
    private String downloadHeadPic = "/appDownloadPortraits"; //下载头像的url识别字符串
    private String downloadPatient = "/queryPersonForApp"; //下载居民列表

    /**
     * 获取单例对象
     * @return 单例对象
     */
    public static URLManage getInstance() {
        if (instance == null) {
            instance = new URLManage();
        }
        return instance;
    }

    /**
     * 动态设置地址
     * @param ipStr ip地址
     * @param portStr 端口
     */
    public void setVillagehealthport(String ipStr, String portStr) {
        villagehealthport = http + ipStr + ":" + portStr + SpUtils.getSp(UiUitls.getContent()
                , GlobalConstant.APP_CONFIG, GlobalConstant.SERVER_ADDRESS
                , GlobalConstant.SERVER_FIX_ADDRESS);
    }

    /**
     * 动态设置地址
     * @param ipStr ip地址
     * @param portStr 端口
     */
    public void setRequestEcgDiagnose(String ipStr, String portStr) {
        requestEcgDiagnose = http + ipStr + ":" + portStr
                + "/imms-web/appRemoteEcg/requestEcgDiagnose";
    }

    /**
     * 动态设置地址
     * @param ipStr ip地址
     * @param portStr 端口
     */
    public void setQueryEcgDiagnoses(String ipStr, String portStr) {
        queryEcgDiagnoses = http + ipStr + ":" + portStr
                + "/imms-web/appRemoteEcg/queryEcgDiagnoses";
    }

    /**
     * 获取头像下载接口的url
     *
     * @return 头像下载接口的url
     */
    public String getDownloadHeadURL() {
        return getBasicUrl() + appClient +  downloadHeadPic;
    }

    /**
     * 获取头像下载接口的url
     *
     * @return 头像下载接口的url
     */
    public String getDowloadPatientURL() {
        return getBasicUrl() + appClient +  downloadPatient;
    }

    /**
     * 获取接口基本web的url  /imms-web结尾
     *
     * @return 接口基本web的url
     */
    public String getBasicUrl() {
        return http + SpUtils.getIp() + ":" + SpUtils.getPort() + web;
    }
}
