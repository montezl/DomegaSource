package com.dmj.domain;

import java.io.Serializable;

/* loaded from: Grade.class */
public class Grade implements Serializable {
    private Integer id;
    private Integer gradeNum;
    private String gradeName;
    private Integer schoolNum;
    private String insertUser;
    private String insertDate;
    private String updateUser;
    private String updateDate;
    private String description;
    private String isDelete;
    private String ext1;
    private String ext2;
    private String ext3;
    private String ext5;
    private Integer jie;
    private Integer islevel;
    private String schoolName;

    public Integer getJie() {
        return this.jie;
    }

    public void setJie(Integer jie) {
        this.jie = jie;
    }

    public Grade() {
    }

    public Grade(Integer gradeNum, String gradeName, Integer schoolNum, String insertUser, String insertDate, String updateUser, String updateDate) {
        this.gradeNum = gradeNum;
        this.gradeName = gradeName;
        this.schoolNum = schoolNum;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
    }

    public Grade(Integer gradeNum, String gradeName, Integer schoolNum, String insertUser, String insertDate, String updateUser, String updateDate, String description, String isDelete, String ext1, String ext2, String ext3, String schoolName, Integer islevel) {
        this.gradeNum = gradeNum;
        this.gradeName = gradeName;
        this.schoolNum = schoolNum;
        this.insertUser = insertUser;
        this.insertDate = insertDate;
        this.updateUser = updateUser;
        this.updateDate = updateDate;
        this.description = description;
        this.isDelete = isDelete;
        this.ext1 = ext1;
        this.ext2 = ext2;
        this.ext3 = ext3;
        this.schoolName = schoolName;
        this.islevel = islevel;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGradeNum() {
        return this.gradeNum;
    }

    public void setGradeNum(Integer gradeNum) {
        this.gradeNum = gradeNum;
    }

    public String getGradeName() {
        return this.gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public Integer getSchoolNum() {
        return this.schoolNum;
    }

    public void setSchoolNum(Integer schoolNum) {
        this.schoolNum = schoolNum;
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

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolName() {
        return this.schoolName;
    }

    public Integer getIslevel() {
        return this.islevel;
    }

    public void setIslevel(Integer islevel) {
        this.islevel = islevel;
    }

    public String getExt5() {
        return this.ext5;
    }

    public void setExt5(String ext5) {
        this.ext5 = ext5;
    }
}
