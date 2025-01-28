package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Schoolgroup.class */
public class Schoolgroup implements Serializable {
    private int id;
    private String schoolGroupName;
    private String schoolNum;
    private String schoolGroupNum;
    private String insertUser;
    private String updateUser;
    private String ext1;
    private String ext2;
    private String ext3;
    private String ext4;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSchoolGroupName() {
        return this.schoolGroupName;
    }

    public void setSchoolGroupName(String schoolGroupName) {
        this.schoolGroupName = schoolGroupName;
    }

    public String getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(String schoolNum) {
        this.schoolNum = schoolNum;
    }

    public String getSchoolGroupNum() {
        return this.schoolGroupNum;
    }

    public void setSchoolGroupNum(String schoolGroupNum) {
        this.schoolGroupNum = schoolGroupNum;
    }

    public String getInsertUser() {
        return this.insertUser;
    }

    public void setInsertUser(String insertUser) {
        this.insertUser = insertUser;
    }

    public String getUpdateUser() {
        return this.updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getExt1() {
        return this.ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1;
    }

    public String getExt2() {
        return this.ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2;
    }

    public String getExt3() {
        return this.ext3;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3;
    }

    public String getExt4() {
        return this.ext4;
    }

    public void setExt4(String ext4) {
        this.ext4 = ext4;
    }
}
