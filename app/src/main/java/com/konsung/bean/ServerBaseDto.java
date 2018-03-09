package com.konsung.bean;
/**
 * 设备基本信息
 * @author HXM
 *
 */

public class ServerBaseDto {

    //不需要给app
    private String serverId;
    //服务器Ip
    private String serverIp;
    //服务器端口
    private String serverPort;
    //升级地址
    private String serverUrl;
    //Client是否需要https访问
    private String isHttps;
    //不需要给app
    private Integer count;
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getIsHttps() {
        return isHttps;
    }

    public void setIsHttps(String isHttps) {
        this.isHttps = isHttps;
    }



}
