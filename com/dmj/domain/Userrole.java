package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Userrole.class */
public class Userrole implements Serializable {
    private Integer id;
    private String userNum;
    private String roleNum;
    private String insertUser;
    private String insertDate;
    private Integer schoolNum;
    private String isDelete;
    private String resource;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserNum() {
        return this.userNum;
    }

    public void setUserNum(String userNum) {
        this.userNum = userNum;
    }

    public String getRoleNum() {
        return this.roleNum;
    }

    public void setRoleNum(String roleNum) {
        this.roleNum = roleNum;
    }

    public String getInsertUser() {
        return this.insertUser;
    }

    public void setInsertUser(String insertUser) {
        this.insertUser = insertUser;
    }

    public String getInsertDate() {
        return this.insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public Integer getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(Integer schoolNum) {
        this.schoolNum = schoolNum;
    }

    public String getIsDelete() {
        return this.isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getResource() {
        return this.resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
