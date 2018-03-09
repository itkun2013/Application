package com.konsung.bean;

import java.util.List;

/**
 * APP版本信息
 *
 * @author HEXM
 */

public class AppVersionDto {
    //结果
    private String result;
    //消息
    private String msg;
    //当前服务器最新版本
    private int appVersion;
    //APP名称
    private String appName;
    //APP描述
    private String appDesc;
    //更新时间
    private String uploadTime;
    //更新人员
    private String uploadPerson;
    //APPID
    private String appId;
    //md5
    private String fileMd5;
    //服务器列表
    private List<ServerBaseDto> serverList;

    /**
     * 获取信息
     * @return 信息
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置信息
     * @param msg 信息
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取结果
     * @return 结果
     */
    public String getResult() {
        return result;
    }

    /**
     * 设置结果值
     * @param result 结果
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * 获取app版本号
     * @return 版本号
     */
    public int getAppVersion() {
        return appVersion;
    }

    /**
     * 设置app版本号
     * @param appVersion app版本号
     */
    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    /**
     * 获取app名称
     * @return app名称
     */
    public String getAppName() {
        return appName;
    }

    /**
     * 设置app名称
     * @param appName app名称
     */
    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * 获取app描述信息
     * @return app描述信息
     */
    public String getAppDesc() {
        return appDesc;
    }

    /**
     * 设置app描述信息
     * @param appDesc 描述信息
     */
    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    /**
     * 获取更新时间
     * @return 更新时间
     */
    public String getUploadTime() {
        return uploadTime;
    }

    /**
     * 设置更新时间
     * @param uploadTime 更新时间
     */
    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    /**
     * 获取更新人员
     * @return 更新人员
     */
    public String getUploadPerson() {
        return uploadPerson;
    }

    /**
     * 设置更新人员
     * @param uploadPerson 更新人员
     */
    public void setUploadPerson(String uploadPerson) {
        this.uploadPerson = uploadPerson;
    }

    /**
     * 获取appid
     * @return appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * 设置appId
     * @param appId appId
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * 获取服务器列表
     * @return 服务器列表
     */
    public List<ServerBaseDto> getServerList() {
        return serverList;
    }

    /**
     * 设置服务器列表
     * @param serverList 服务器列表
     */
    public void setServerList(List<ServerBaseDto> serverList) {
        this.serverList = serverList;
    }

    /**
     * 获取文件MD5值
     * @return 文件MD5值
     */
    public String getFileMd5() {
        return fileMd5;
    }

    /**
     * 设置文件MD5值
     * @param fileMd5 文件MD5值
     */
    public void setFileMd5(String fileMd5) {
        this.fileMd5 = fileMd5;
    }
}
