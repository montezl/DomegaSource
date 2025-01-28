package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Userparent.class */
public class Userparent implements Serializable {
    private static final long serialVersionUID = 1;
    private String id;
    private Integer schoolnum;
    private String studentName;
    private String userid;
    private String studentRelation;
    private String username;
    private String password;
    private String usertype;
    private String loginname;
    private String realname;
    private String mobile;
    private String email;
    private String instertUser;
    private String insertDate;
    private String isUser;
    private String isDelete;
    private int autoreg;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSchoolnum() {
        return this.schoolnum;
    }

    public void setSchoolnum(Integer schoolnum) {
        this.schoolnum = schoolnum;
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getStudentRelation() {
        return this.studentRelation;
    }

    public void setStudentRelation(String studentRelation) {
        this.studentRelation = studentRelation;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsertype() {
        return this.usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getLoginname() {
        return this.loginname;
    }

    public void setLoginname(String loginname) {
        this.loginname = loginname;
    }

    public String getRealname() {
        return this.realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getInstertUser() {
        return this.instertUser;
    }

    public void setInstertUser(String instertUser) {
        this.instertUser = instertUser;
    }

    public String getInsertDate() {
        return this.insertDate;
    }

    public void setInsertDate(String insertDate) {
        this.insertDate = insertDate;
    }

    public String getIsUser() {
        return this.isUser;
    }

    public void setIsUser(String isUser) {
        this.isUser = isUser;
    }

    public String getIsDelete() {
        return this.isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public int getAutoreg() {
        return this.autoreg;
    }

    public void setAutoreg(int autoreg) {
        this.autoreg = autoreg;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
