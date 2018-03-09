package com.konsung.bean;

/**
 * Created by lipengjie on 2017/1/17 0017.
 * 登录接口返回的bean
 */

public class CheckData {

    /**
     * code : SUCESS
     * checkData : {"empId":"ks001","lastLogin":null,"orgName":"康尚生物科技","available":"Y",
     * "ordernum":0,"userName":"ks001","userId":"ks001","orgId":"ks001","lastLoginIp":null,
     * "password":"054e7930c303ecfbc7884ff931e33211","parentOrgId":null,"empName":"康尚管理员",
     * "orgArea":null}
     * message :
     */
    private String code;
    private CheckDataEntity checkData;
    private String message;

    /**
     * 设置code值
     * @param code code值
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 设置checkData值
     * @param checkData CheckDataEntity值
     */
    public void setCheckData(CheckDataEntity checkData) {
        this.checkData = checkData;
    }

    /**
     * 设置消息字符
     * @param message 字符消息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取code值
     * @return code值
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取CheckDataEntity实例对象
     * @return CheckDataEntity对象
     */
    public CheckDataEntity getCheckData() {
        return checkData;
    }

    /**
     * 获取message值
     * @return message值
     */
    public String getMessage() {
        return message;
    }

    /**
     * CheckDataEntity实体类
     */
    public class CheckDataEntity {
        /**
         * empId : ks001
         * lastLogin : null
         * orgName : 康尚生物科技
         * available : Y
         * ordernum : 0
         * userName : ks001
         * userId : ks001
         * orgId : ks001
         * lastLoginIp : null
         * password : 054e7930c303ecfbc7884ff931e33211
         * parentOrgId : null
         * empName : 康尚管理员
         * orgArea : null
         */
        private String empId;
        private String lastLogin;
        private String orgName;
        private String available;
        private int ordernum;
        private String userName;
        private String userId;
        private String orgId;
        private String lastLoginIp;
        private String password;
        private String parentOrgId;
        private String empName;
        private String orgArea;

        /**
         * 设置empid
         * @param empId empid值
         */
        public void setEmpId(String empId) {
            this.empId = empId;
        }

        /**
         * 设置lastLogin值
         * @param lastLogin lastLogin值
         */
        public void setLastLogin(String lastLogin) {
            this.lastLogin = lastLogin;
        }

        /**
         * o设置rgName值
         * @param orgName 机构名称
         */
        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        /**
         * 设置available值
         * @param available available值
         */
        public void setAvailable(String available) {
            this.available = available;
        }

        /**
         * 设置ordernum
         * @param ordernum ordernum值
         */
        public void setOrdernum(int ordernum) {
            this.ordernum = ordernum;
        }

        /**
         * 设置用户名称
         * @param userName 用户名称
         */
        public void setUserName(String userName) {
            this.userName = userName;
        }

        /**
         * 设置用户id
         * @param userId 用户id值
         */
        public void setUserId(String userId) {
            this.userId = userId;
        }

        /**
         * 设置机构id值
         * @param orgId 机构id值
         */
        public void setOrgId(String orgId) {
            this.orgId = orgId;
        }

        /**
         * 设置lastLoginIp
         * @param lastLoginIp lastLoginIp值
         */
        public void setLastLoginIp(String lastLoginIp) {
            this.lastLoginIp = lastLoginIp;
        }

        /**
         * 设置密码
         * @param password 密码
         */
        public void setPassword(String password) {
            this.password = password;
        }

        /**
         * 设置parentOrgId
         * @param parentOrgId parentOrgId值
         */
        public void setParentOrgId(String parentOrgId) {
            this.parentOrgId = parentOrgId;
        }

        /**
         * 设置empName值
         * @param empName 值
         */
        public void setEmpName(String empName) {
            this.empName = empName;
        }

        /**
         * 设置orgArea值
         * @param orgArea 值
         */
        public void setOrgArea(String orgArea) {
            this.orgArea = orgArea;
        }

        /**
         * 获取empId值
         * @return empId值
         */
        public String getEmpId() {
            return empId;
        }

        /**
         * 获取lastLogin值
         * @return lastLogin值
         */
        public String getLastLogin() {
            return lastLogin;
        }

        /**
         * 获取orgName值
         * @return orgName值
         */
        public String getOrgName() {
            return orgName;
        }

        /**
         * 获取available值
         * @return available值
         */
        public String getAvailable() {
            return available;
        }

        /**
         * 获取ordernum值
         * @return ordernum值
         */
        public int getOrdernum() {
            return ordernum;
        }

        /**
         * 获取userName值
         * @return userName值
         */
        public String getUserName() {
            return userName;
        }

        /**
         * 获取userId值
         * @return 获取userId值
         */
        public String getUserId() {
            return userId;
        }

        /**
         * 获取orgId值
         * @return  orgId值
         */
        public String getOrgId() {
            return orgId;
        }

        /**
         * 获取lastLoginIp值
         * @return lastLoginIp值
         */
        public String getLastLoginIp() {
            return lastLoginIp;
        }

        /**
         * 获取password值
         * @return password值
         */
        public String getPassword() {
            return password;
        }

        /**
         * 获取parentOrgId值
         * @return parentOrgId值
         */
        public String getParentOrgId() {
            return parentOrgId;
        }

        /**
         * 获取empName值
         * @return empName
         */
        public String getEmpName() {
            return empName;
        }

        /**
         * 获取orgArea值
         * @return orgArea值
         */
        public String getOrgArea() {
            return orgArea;
        }
    }
}
