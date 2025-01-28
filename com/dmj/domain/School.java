package com.dmj.domain;

import java.io.Serializable;

/* loaded from: School.class */
public class School implements Serializable {
    private Integer id;
    private String schoolNum;
    private String areaNum;
    private String schoolName;
    private String schoolAddress;
    private String schoolType;
    private String insertUser;
    private String insertDate;
    private String updateUser;
    private String updateDate;
    private String description;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String ext3;
    private String shortname;
    private String schoolId;

    public School() {
    }

    public School(String schoolNum, String schoolName, String shortname, String schoolAddress, String schoolType, String insertUser, String insertDate, String updateUser, String updateDate, String schoolId) {
        this.schoolNum = schoolNum;
        this.schoolName = schoolName;
        this.shortname = shortname;
        this.schoolAddress = schoolAddress;
        this.schoolType = schoolType;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
        this.schoolId = schoolId;
    }

    public School(String schoolNum, String areaNum, String schoolName, String shortname, String schoolAddress, String schoolType, String insertUser, String insertDate, String updateUser, String updateDate, String description, String isDelete, String ext1, String ext2, String ext3, String schoolId) {
        this.schoolNum = schoolNum;
        this.areaNum = areaNum;
        this.schoolName = schoolName;
        this.shortname = shortname;
        this.schoolAddress = schoolAddress;
        this.schoolType = schoolType;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
        this.description = description;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.schoolId = schoolId;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(String schoolNum) {
        this.schoolNum = schoolNum;
    }

    public String getAreaNum() {
        return this.areaNum;
    }

    public void setAreaNum(String areaNum) {
        this.areaNum = areaNum;
    }

    public String getSchoolName() {
        return this.schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolAddress() {
        return this.schoolAddress;
    }

    public void setSchoolAddress(String schoolAddress) {
        this.schoolAddress = schoolAddress;
    }

    public String getSchoolType() {
        return this.schoolType;
    }

    public void setSchoolType(String schoolType) {
        this.schoolType = schoolType;
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

    public String getUpdateUser() {
        return this.updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateDate() {
        return this.updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsDelete() {
        return this.isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
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

    public String getShortname() {
        return this.shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getSchoolId() {
        return this.schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }
}
