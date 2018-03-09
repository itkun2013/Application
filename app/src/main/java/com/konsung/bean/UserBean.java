package com.konsung.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by JustRush on 2015/8/25.
 */
@Entity
public class UserBean {
    @Property(nameInDb = "id")
    @Id(autoincrement = true)
    private Long id;
    private String username = "";
    
    private String password = "";
    
    private String orgName = "";
    
    private String orgId = "";
    
    private String empId = "";
    
    private String empName = "";

    @Generated(hash = 1268574799)
    public UserBean(Long id, String username, String password, String orgName,
            String orgId, String empId, String empName) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.orgName = orgName;
        this.orgId = orgId;
        this.empId = empId;
        this.empName = empName;
    }

    @Generated(hash = 1203313951)
    public UserBean() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {

        this.orgName = orgName;
    }
    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {

        this.orgId = orgId;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }
}
